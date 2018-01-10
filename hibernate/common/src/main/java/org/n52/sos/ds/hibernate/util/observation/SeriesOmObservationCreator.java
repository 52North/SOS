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

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.iceland.convert.ConverterException;
import org.n52.iceland.util.LocalizedProducer;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.om.ObservationStream;
import org.n52.shetland.ogc.om.OmObservableProperty;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.OmObservationConstellation;
import org.n52.shetland.ogc.om.SingleObservationValue;
import org.n52.shetland.ogc.om.values.NilTemplateValue;
import org.n52.shetland.ogc.ows.OwsServiceProvider;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.shetland.ogc.sos.request.AbstractObservationRequest;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.entities.observation.series.Series;
import org.n52.sos.ds.hibernate.util.HibernateHelper;

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

    private static final String SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_PROCEDURE_SERIES = "getUnitForObservablePropertyProcedureSeries";

    private static final String SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_SERIES = "getUnitForObservablePropertySeries";

    protected final Series series;

    public SeriesOmObservationCreator(Series series, AbstractObservationRequest request, LocalizedProducer<OwsServiceProvider> serviceProvider, Locale language, String pdf, DaoFactory daoFactory, Session session) {
        super(request, language, serviceProvider, pdf, daoFactory, session);
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
        AbstractFeature feature = createFeatureOfInterest(series.getFeatureOfInterest());

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
     */
    protected OmObservationConstellation getObservationConstellation(SosProcedureDescription<?> procedure,
            OmObservableProperty obsProp, AbstractFeature feature) {
        OmObservationConstellation obsConst = new OmObservationConstellation(procedure, obsProp, null, feature, null);
        /* get the offerings to find the templates */
        if (obsConst.getOfferings() == null) {
            obsConst.setOfferings(Sets.newHashSet(getCache().getOfferingsForProcedure(
                    obsConst.getProcedure().getIdentifier())));
        }
        return obsConst;
    }

    /**
     * @return
     */
    protected Series getSeries() {
        return series;
    }

    @SuppressWarnings("unchecked")
    protected void checkForAdditionalObservationCreator(Series series, OmObservation sosObservation) {
        AdditionalObservationCreatorKey key = new AdditionalObservationCreatorKey(getResponseFormat(), series.getClass());
        AdditionalObservationCreatorRepository repo = AdditionalObservationCreatorRepository.getInstance();
        if (repo.hasAdditionalObservationCreatorFor(key)) {
            repo.get(key).create(sosObservation, series);
        }
    }

    private String queryUnit() {
        String property = series.getObservableProperty().getIdentifier();
        String procedure = series.getProcedure().getIdentifier();

        if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_PROCEDURE_SERIES, getSession())) {
            Query namedQuery = getSession().getNamedQuery(SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_PROCEDURE_SERIES);
            namedQuery.setParameter(Series.OBSERVABLE_PROPERTY, property);
            namedQuery.setParameter(Series.PROCEDURE, procedure);
            LOGGER.debug("QUERY queryUnit({}, {}) with NamedQuery '{}': {}", property, procedure, SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_PROCEDURE_SERIES, namedQuery.getQueryString());
            return (String) namedQuery.uniqueResult();
        } else if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_SERIES, getSession())) {
            Query namedQuery = getSession().getNamedQuery(SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_SERIES);
            namedQuery.setParameter(Series.OBSERVABLE_PROPERTY, property);
            LOGGER.debug("QUERY queryUnit({}) with NamedQuery '{}': {}", property, SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_SERIES, namedQuery.getQueryString());
            return (String) namedQuery.uniqueResult();
        }
        return null;
    }

}
