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
import org.n52.sos.ds.hibernate.dao.FeatureOfInterestDAO;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.procedure.generator.AbstractHibernateProcedureDescriptionGeneratorSml;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * TODO JavaDoc
 *
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 * @since 4.0.0
 */
public class ObservationConstellationOmObservationCreator extends AbstractOmObservationCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObservationConstellationOmObservationCreator.class);

    protected final ObservationConstellation oc;

    protected final List<String> featureIds;

    public ObservationConstellationOmObservationCreator(ObservationConstellation observationConstellation,
                                                        List<String> featureOfInterestIdentifiers,
                                                        AbstractObservationRequest request,
                                                        LocalizedProducer<OwsServiceProvider> serviceProvider,
                                                        Locale language,
                                                        String pdf,
                                                        DaoFactory daoFactory,
                                                        Session session) {
        super(request, language, serviceProvider, pdf, daoFactory, session);
        this.oc = observationConstellation;
        this.featureIds = featureOfInterestIdentifiers;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public ObservationStream create() throws OwsExceptionReport, ConverterException {
        final List<OmObservation> observations = Lists.newLinkedList();
        if (getObservationConstellation() == null || getFeatureIds() == null) {
            return ObservationStream.empty();
        }
        SosProcedureDescription procedure = createProcedure(getObservationConstellation().getProcedure().getIdentifier());
        OmObservableProperty obsProp = createObservableProperty(getObservationConstellation().getObservableProperty());
        obsProp.setUnit(queryUnit());
        FeatureOfInterestDAO featureOfInterestDAO = new FeatureOfInterestDAO(getDaoFactory());
        for (final String featureId : getFeatureIds()) {
            final AbstractFeature feature = createFeatureOfInterest(featureOfInterestDAO.get(featureId, getSession()));
            final OmObservationConstellation obsConst = getObservationConstellation(procedure, obsProp, feature);

            final OmObservation sosObservation = new OmObservation();
            sosObservation.setNoDataValue(getNoDataValue());
            sosObservation.setTokenSeparator(getTokenSeparator());
            sosObservation.setTupleSeparator(getTupleSeparator());
            sosObservation.setDecimalSeparator(getDecimalSeparator());
            sosObservation.setObservationConstellation(obsConst);
            final NilTemplateValue value = new NilTemplateValue();
            value.setUnit(obsProp.getUnit());
            sosObservation.setValue(new SingleObservationValue(new TimeInstant(), value));
            observations.add(sosObservation);
        }
        return ObservationStream.of(observations);
    }

    private OmObservationConstellation getObservationConstellation(SosProcedureDescription<?> procedure,
            OmObservableProperty obsProp, AbstractFeature feature) {
        OmObservationConstellation obsConst = new OmObservationConstellation(procedure, obsProp, null, feature, null);
        /* get the offerings to find the templates */
        if (obsConst.getOfferings() == null) {
            obsConst.setOfferings(Sets.newHashSet(getCache().getOfferingsForProcedure(
                    obsConst.getProcedure().getIdentifier())));
        }
        return obsConst;
    }

    private String queryUnit() {
        if (HibernateHelper.isNamedQuerySupported(
                AbstractHibernateProcedureDescriptionGeneratorSml.SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_PROCEDURE_OFFERING,
                getSession())) {
            Query namedQuery =
                    getSession()
                            .getNamedQuery(
                                    AbstractHibernateProcedureDescriptionGeneratorSml.SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_PROCEDURE_OFFERING);
            namedQuery.setParameter(ObservationConstellation.OBSERVABLE_PROPERTY, oc.getObservableProperty()
                    .getIdentifier());
            namedQuery.setParameter(ObservationConstellation.PROCEDURE, oc.getProcedure().getIdentifier());
            namedQuery.setParameter(ObservationConstellation.OFFERING, oc.getOffering().getIdentifier());
            LOGGER.debug(
                    "QUERY queryUnit(observationConstellation) with NamedQuery: {}",
                    AbstractHibernateProcedureDescriptionGeneratorSml.SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_PROCEDURE_OFFERING);
            return (String) namedQuery.uniqueResult();
        } else if (HibernateHelper.isNamedQuerySupported(
                AbstractHibernateProcedureDescriptionGeneratorSml.SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_PROCEDURE,
                getSession())) {
            Query namedQuery =
                    getSession()
                            .getNamedQuery(
                                    AbstractHibernateProcedureDescriptionGeneratorSml.SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_PROCEDURE);
            namedQuery.setParameter(ObservationConstellation.OBSERVABLE_PROPERTY, oc.getObservableProperty()
                    .getIdentifier());
            namedQuery.setParameter(ObservationConstellation.PROCEDURE, oc.getProcedure().getIdentifier());
            LOGGER.debug("QUERY queryUnit(observationConstellation) with NamedQuery: {}",
                    AbstractHibernateProcedureDescriptionGeneratorSml.SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY_PROCEDURE);
            return (String) namedQuery.uniqueResult();
        } else if (HibernateHelper.isNamedQuerySupported(
                AbstractHibernateProcedureDescriptionGeneratorSml.SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY, getSession())) {
            Query namedQuery =
                    getSession().getNamedQuery(
                            AbstractHibernateProcedureDescriptionGeneratorSml.SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY);
            namedQuery.setParameter(ObservationConstellation.OBSERVABLE_PROPERTY, oc.getObservableProperty()
                    .getIdentifier());
            LOGGER.debug("QUERY queryUnit(observationConstellation) with NamedQuery: {}",
                    AbstractHibernateProcedureDescriptionGeneratorSml.SQL_QUERY_GET_UNIT_FOR_OBSERVABLE_PROPERTY);
            return (String) namedQuery.uniqueResult();
        }
        return null;
    }

    /**
     * @return the observation constellation
     */
    protected ObservationConstellation getObservationConstellation() {
        return oc;
    }

    /**
     * @return the featureIds
     */
    protected List<String> getFeatureIds() {
        return featureIds;
    }
}
