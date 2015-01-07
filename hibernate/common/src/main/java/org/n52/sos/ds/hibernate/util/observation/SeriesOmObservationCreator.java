/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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
import org.n52.sos.convert.ConverterException;
import org.n52.sos.ds.hibernate.entities.series.Series;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.values.NilTemplateValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.request.AbstractObservationRequest;
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

    private static final String SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_PROCEDURE_SERIES = "getUnitForObservablePropertyProcedureSeries";

    private static final String SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_SERIES = "getUnitForObservablePropertySeries";

    protected final Series series;

    /**
     * Constructor
     *
     * @param series
     *            Series to generate observation from
     * @param version
     *            SOS version
     * @param session
     *            Hibernate sesssion
     */
    public SeriesOmObservationCreator(Series series, AbstractObservationRequest request, Session session) {
        super(request, session);
        this.series = series;
    }

    public SeriesOmObservationCreator(Series series, AbstractObservationRequest request, Locale language, Session session) {
        super(request, language, session);
        this.series = series;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<OmObservation> create() throws OwsExceptionReport, ConverterException {
        final List<OmObservation> observations = Lists.newLinkedList();
        if (series != null) {
            SosProcedureDescription procedure = createProcedure(series.getProcedure().getIdentifier());
            OmObservableProperty obsProp = createObservableProperty(series.getObservableProperty());
            obsProp.setUnit(queryUnit());
            AbstractFeature feature = createFeatureOfInterest(series.getFeatureOfInterest().getIdentifier());

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
            observations.add(sosObservation);
        }
        return observations;
    }

//    /**
//     * Get featureOfInterest object from series
//     *
//     * @return FeatureOfInerest object
//     * @throws OwsExceptionReport
//     *             If an error occurs
//     */
//    protected AbstractFeature getFeatureOfInterest() throws OwsExceptionReport {
//        FeatureQueryHandlerQueryObject queryObject = new FeatureQueryHandlerQueryObject();
//        queryObject.addFeatureIdentifier(getSeries().getFeatureOfInterest().getIdentifier()).setConnection(getSession()).setVersion(getVersion());
//        final AbstractFeature feature =
//                getFeatureQueryHandler().getFeatureByID(queryObject);
////                        getSeries().getFeatureOfInterest().getIdentifier(),
////                        getSession(), getVersion(), -1);
//        return feature;
//    }
//
//    /**
//     * Get procedure object from series
//     *
//     * @return Procedure object
//     * @throws ConverterException
//     *             If an error occurs sensor description creation
//     * @throws OwsExceptionReport
//     *             If an error occurs
//     */
//    protected SosProcedureDescription getProcedure() throws ConverterException, OwsExceptionReport {
//        String id = getSeries().getProcedure().getIdentifier();
//        Procedure hProcedure = new ProcedureDAO().getProcedureForIdentifier(id, getSession());
//        String pdf = hProcedure.getProcedureDescriptionFormat().getProcedureDescriptionFormat();
//        if (getActiveProfile().isEncodeProcedureInObservation()) {
//            return new HibernateProcedureConverter().createSosProcedureDescription(hProcedure, pdf, getVersion(),
//                    getSession());
//        } else {
//            return new SosProcedureDescriptionUnknowType(id, pdf, null);
//        }
//    }

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
    protected OmObservationConstellation getObservationConstellation(SosProcedureDescription procedure,
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
        if (AdditionalObservationCreatorRepository.getInstance().hasAdditionalObservationCreatorFor(key)) {
            AdditionalObservationCreator<Series> creator = AdditionalObservationCreatorRepository.getInstance().get(key);
            creator.create(sosObservation, series);
        }
    }

    private String queryUnit() {
        if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_PROCEDURE_SERIES, getSession())) {
            Query namedQuery = getSession().getNamedQuery(SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_PROCEDURE_SERIES);
            namedQuery.setParameter(Series.OBSERVABLE_PROPERTY,
                    series.getObservableProperty().getIdentifier());
            namedQuery.setParameter(Series.PROCEDURE,
                    series.getProcedure().getIdentifier());
            LOGGER.debug("QUERY queryUnit(observationConstellation) with NamedQuery: {}",
                    SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_PROCEDURE_SERIES);
            return (String) namedQuery.uniqueResult();
        } else if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_SERIES, getSession())) {
            Query namedQuery = getSession().getNamedQuery(SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_SERIES);
            namedQuery.setParameter(Series.OBSERVABLE_PROPERTY,
                    series.getObservableProperty().getIdentifier());
            LOGGER.debug("QUERY queryUnit(observationConstellation) with NamedQuery: {}",
                    SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_SERIES);
            return (String) namedQuery.uniqueResult();
        }
        return null;
    }

}
