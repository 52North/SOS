/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.util.observation;

import java.util.List;
import java.util.Locale;
import org.hibernate.query.Query;
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
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesObservationDAO;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Create {@link OmObservation}s from series
 *
 * @since 4.0.0
 *
 */
public class SeriesOmObservationCreator extends AbstractOmObservationCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeriesOmObservationCreator.class);

    protected final DatasetEntity series;

    public SeriesOmObservationCreator(
            DatasetEntity series,
            AbstractObservationRequest request,
            Locale i18n,
            String pdf,
            OmObservationCreatorContext creatorContext,
            Session session) {
        super(request, i18n, pdf, creatorContext, session);
        this.series = series;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public ObservationStream create() throws OwsExceptionReport, ConverterException {
        final List<OmObservation> observations = Lists.newLinkedList();
        if(series == null) {
            return ObservationStream.empty();
        }
        SosProcedureDescription procedure = createProcedure(series.getProcedure().getIdentifier());
        OmObservableProperty obsProp = createObservableProperty(series.getObservableProperty());
        obsProp.setUnit(queryUnit());
        AbstractFeature feature = createFeatureOfInterest(series.getFeature());

        final OmObservationConstellation obsConst = getObservationConstellation(procedure, obsProp, feature);
        final OmObservation sosObservation = new OmObservation();
        sosObservation.setNoDataValue(getNoDataValue());
        sosObservation.setTokenSeparator(getTokenSeparator());
        sosObservation.setTupleSeparator(getTupleSeparator());
        sosObservation.setDecimalSeparator(getDecimalSeparator());
        sosObservation.setObservationConstellation(obsConst);
        checkForAdditionalObservationCreator(series, sosObservation);
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
     * @throws OwsExceptionReport
     */
    protected OmObservationConstellation getObservationConstellation(SosProcedureDescription<?> procedure,
            OmObservableProperty obsProp, AbstractFeature feature) throws OwsExceptionReport {
        OmObservationConstellation obsConst = new OmObservationConstellation(procedure, obsProp, null, feature, null);
        /* get the offerings to find the templates */
        if (obsConst.getOfferings() == null) {
            if (getSeries().isSetOffering()) {
                obsConst.setOfferings(Sets.newHashSet(getSeries().getOffering().getIdentifier()));
            } else {
                AbstractSeriesObservationDAO observationDAO = (AbstractSeriesObservationDAO)getDaoFactory().getObservationDAO();
                obsConst.setOfferings(observationDAO.getOfferingsForSeries(series, getSession()));
//            } else {
//                obsConst.setOfferings(Sets.newHashSet(getCache().getOfferingsForProcedure(
//                        obsConst.getProcedure().getIdentifier())));
            }
        }
        if (getSeries().isSetIdentifier()) {
            addIdentifier(obsConst, getSeries());
        }
        if (getSeries().isSetName()) {
            addName(obsConst, getSeries());
        }
        if (getSeries().isSetDescription()) {
            obsConst.setDescription(getSeries().getDescription());
        }
        return obsConst;
    }

    /**
     * @return
     */
    protected DatasetEntity getSeries() {
        return series;
    }

    @SuppressWarnings("unchecked")
    protected void checkForAdditionalObservationCreator(DatasetEntity series, OmObservation sosObservation) throws CodedException {
        AdditionalObservationCreatorKey key = new AdditionalObservationCreatorKey(getResponseFormat(), series.getClass());
        AdditionalObservationCreatorRepository repo = AdditionalObservationCreatorRepository.getInstance();
        if (repo.hasAdditionalObservationCreatorFor(key)) {
            repo.get(key).create(sosObservation, series);
        } else if (checkAcceptType()) {
            for (MediaType acceptType : getAcceptType()) {
                AdditionalObservationCreatorKey acceptKey = new AdditionalObservationCreatorKey(acceptType.withoutParameters().toString(), series.getClass());
                if (AdditionalObservationCreatorRepository.getInstance().hasAdditionalObservationCreatorFor(acceptKey)) {
                    AdditionalObservationCreator creator = AdditionalObservationCreatorRepository.getInstance().get(acceptKey);
                    creator.create(sosObservation, series, getSession());
                }
            }
        }
    }

    private String queryUnit() {
        String property = series.getObservableProperty().getIdentifier();
        String procedure = series.getProcedure().getIdentifier();

        if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_PROCEDURE_SERIES, getSession())) {
            Query namedQuery = getSession().getNamedQuery(SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_PROCEDURE_SERIES);
            namedQuery.setParameter(DatasetEntity.PROPERTY_PHENOMENON, property);
            namedQuery.setParameter(DatasetEntity.PROPERTY_PROCEDURE, procedure);
            LOGGER.debug("QUERY queryUnit({}, {}) with NamedQuery '{}': {}", property, procedure, SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_PROCEDURE_SERIES, namedQuery.getQueryString());
            return (String) namedQuery.uniqueResult();
        } else if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_SERIES, getSession())) {
            Query namedQuery = getSession().getNamedQuery(SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_SERIES);
            namedQuery.setParameter(DatasetEntity.PROPERTY_PHENOMENON, property);
            LOGGER.debug("QUERY queryUnit({}) with NamedQuery '{}': {}", property, SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_SERIES, namedQuery.getQueryString());
            return (String) namedQuery.uniqueResult();
        }
        return null;
    }

    private void addParameter(OmObservation observation, DatasetEntity series) throws OwsExceptionReport {
        new DatasetParameterAdder(observation, series.getParameters()).add();
    }

}
