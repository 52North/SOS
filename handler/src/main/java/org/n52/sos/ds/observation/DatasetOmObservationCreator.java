/*
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.sos.ds.observation;

import java.util.Locale;

import org.hibernate.Session;
import org.n52.iceland.convert.ConverterException;
import org.n52.janmayen.http.MediaType;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.om.ObservationStream;
import org.n52.shetland.ogc.om.OmObservableProperty;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.OmObservationConstellation;
import org.n52.shetland.ogc.om.SingleObservationValue;
import org.n52.shetland.ogc.om.values.NilTemplateValue;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.shetland.ogc.sos.request.AbstractObservationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

public class DatasetOmObservationCreator extends AbstractOmObservationCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatasetOmObservationCreator.class);

    protected final DatasetEntity dataset;

    public DatasetOmObservationCreator(DatasetEntity dataset, AbstractObservationRequest request, Locale i18n,
            String pdf, OmObservationCreatorContext creatorContext, Session session) {
        super(request, i18n, pdf, creatorContext, session);
        this.dataset = dataset;
    }

    @Override
    public ObservationStream create() throws OwsExceptionReport, ConverterException {
        if (getDataset() == null) {
            return ObservationStream.empty();
        }
        SosProcedureDescription procedure = createProcedure(getDataset().getProcedure());
        OmObservableProperty obsProp = createObservableProperty(getDataset().getObservableProperty());
        if (getDataset().isSetUnit()) {
            obsProp.setUnit(getDataset().getUnit().getSymbol());
        }
        AbstractFeature feature = createFeatureOfInterest(getDataset().getFeature());

        final OmObservationConstellation obsConst = getObservationConstellation(procedure, obsProp, feature);
        final OmObservation sosObservation = new OmObservation();
        sosObservation.setNoDataValue(getNoDataValue());
        sosObservation.setTokenSeparator(getTokenSeparator());
        sosObservation.setTupleSeparator(getTupleSeparator());
        sosObservation.setDecimalSeparator(getDecimalSeparator());
        sosObservation.setObservationConstellation(obsConst);
        checkForAdditionalObservationCreator(getDataset(), sosObservation);
        final NilTemplateValue value = new NilTemplateValue();
        value.setUnit(obsProp.getUnit());
        sosObservation.setValue(new SingleObservationValue(new TimeInstant(), value));
        return ObservationStream.of(sosObservation);
    }

    /**
     * Get {@link OmObservationConstellation} from series information
     *
     * @param procedure
     *            Procedure object
     * @param obsProp
     *            ObservableProperty object
     * @param feature
     *            FeatureOfInterest object
     * @return Observation constellation
     * @throws OwsExceptionReport If an error occurs
     */
    protected OmObservationConstellation getObservationConstellation(SosProcedureDescription<?> procedure,
            OmObservableProperty obsProp, AbstractFeature feature) throws OwsExceptionReport {
        OmObservationConstellation obsConst = new OmObservationConstellation(procedure, obsProp, null, feature, null);
        /* get the offerings to find the templates */
        if (obsConst.getOfferings() == null) {
            obsConst.setOfferings(Sets.newHashSet(getDataset().getOffering()
                    .getIdentifier()));
        }
        if (getDataset().isSetIdentifier()) {
            addIdentifier(obsConst, getDataset());
        }
        if (getRequest().isSetRequestedLanguage()) {
            addNameAndDescription(getDataset(), obsConst,
                    getRequestedLanguage(), getI18N(), false);
            if (obsConst.isSetName()) {
                obsConst.setHumanReadableIdentifier(obsConst.getFirstName().getValue());
            }
        } else {
            if (getDataset().isSetName()) {
                addName(obsConst, getDataset());
            }
            if (getDataset().isSetDescription()) {
                obsConst.setDescription(getDataset().getDescription());
            }
        }
        return obsConst;
    }

    /**
     * @return The {@link DatasetEntity}
     */
    protected DatasetEntity getDataset() {
        return dataset;
    }

    protected void checkForAdditionalObservationCreator(DatasetEntity series, OmObservation sosObservation)
            throws CodedException {
        AdditionalObservationCreatorKey key =
                new AdditionalObservationCreatorKey(getResponseFormat(), series.getClass());
        AdditionalObservationCreatorRepository repo = getCreatorContext().getAdditionalObservationCreatorRepository();
        if (repo.hasAdditionalObservationCreatorFor(key)) {
            repo.get(key).create(sosObservation, series);
        } else if (checkAcceptType()) {
            for (MediaType acceptType : getAcceptType()) {
                AdditionalObservationCreatorKey acceptKey = new AdditionalObservationCreatorKey(
                        acceptType.withoutParameters().toString(), series.getClass());
                if (repo.hasAdditionalObservationCreatorFor(acceptKey)) {
                    AdditionalObservationCreator creator = repo.get(acceptKey);
                    creator.create(sosObservation, series, getSession());
                }
            }
        }
    }

}
