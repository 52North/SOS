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
package org.n52.sos.ds.hibernate.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.ResultTemplate;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosResultEncoding;
import org.n52.sos.ogc.sos.SosResultStructure;
import org.n52.sos.request.InsertResultTemplateRequest;
import org.n52.sos.util.CollectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * Hibernate data access class for featureofInterest types
 * 
 * @author CarstenHollmann
 * @since 4.0.0
 */
public class ResultTemplateDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResultTemplateDAO.class);

    /**
     * Get result template object for result template identifier
     * 
     * @param identifier
     *            Result template identifier
     * @param session
     *            Hibernate session
     * @return Result template object
     */
    public ResultTemplate getResultTemplateObject(final String identifier, final Session session) {
        Criteria criteria =
                session.createCriteria(ResultTemplate.class)
                        .add(Restrictions.eq(ResultTemplate.IDENTIFIER, identifier))
                        .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        LOGGER.debug("QUERY getResultTemplateObject(identifier): {}", HibernateHelper.getSqlString(criteria));
        return (ResultTemplate) criteria.uniqueResult();
    }

    /**
     * Get all result template objects
     * 
     * @param session
     *            Hibernate session
     * @return Result template objects
     */
    @SuppressWarnings("unchecked")
    public List<ResultTemplate> getResultTemplateObjects(final Session session) {
        return session.createCriteria(ResultTemplate.class)
                .setFetchMode(ResultTemplate.OFFERING, FetchMode.JOIN)
                .setFetchMode(ResultTemplate.OBSERVABLE_PROPERTY, FetchMode.JOIN)
                .setFetchMode(ResultTemplate.FEATURE_OF_INTEREST, FetchMode.JOIN)
                .list();
    }

    /**
     * Get result template object for observation constellation
     * 
     * @param observationConstellation
     *            Observation constellation object
     * @param session
     *            Hibernate session
     * @return Result template object
     */
    public ResultTemplate getResultTemplateObjectsForObservationConstellation(
            final ObservationConstellation observationConstellation, final Session session) {
        return getResultTemplateObject(observationConstellation.getOffering().getIdentifier(),
                observationConstellation.getObservableProperty().getIdentifier(), session);
    }

    /**
     * Get result template objects for observation constellation and
     * featureOfInterest
     * 
     * @param observationConstellation
     *            Observation constellation object
     * @param sosAbstractFeature
     *            FeatureOfInterest
     * @param session
     *            Hibernate session
     * @return Result template objects
     */
    public List<ResultTemplate> getResultTemplateObjectsForObservationConstellationAndFeature(
            final ObservationConstellation observationConstellation, final AbstractFeature sosAbstractFeature,
            final Session session) {
        return getResultTemplateObject(observationConstellation.getOffering().getIdentifier(),
                observationConstellation.getObservableProperty().getIdentifier(),
                Lists.newArrayList(sosAbstractFeature.getIdentifierCodeWithAuthority().getValue()), session);
    }

    /**
     * Get result template object for offering identifier and observable
     * property identifier
     * 
     * @param offering
     *            Offering identifier
     * @param observedProperty
     *            Observable property identifier
     * @param session
     *            Hibernate session
     * @return Result template object
     */
    @SuppressWarnings("unchecked")
    public ResultTemplate getResultTemplateObject(final String offering, final String observedProperty,
            final Session session) {
        final Criteria rtc = session.createCriteria(ResultTemplate.class).setMaxResults(1);
        rtc.createCriteria(ObservationConstellation.OFFERING).add(Restrictions.eq(Offering.IDENTIFIER, offering));
        rtc.createCriteria(ObservationConstellation.OBSERVABLE_PROPERTY).add(
                Restrictions.eq(ObservableProperty.IDENTIFIER, observedProperty));
        /* there can be multiple but equal result templates... */
        LOGGER.debug("QUERY getResultTemplateObject(offering, observedProperty): {}",
                HibernateHelper.getSqlString(rtc));
        final List<ResultTemplate> templates = rtc.list();
        return templates.isEmpty() ? null : templates.iterator().next();
    }

    /**
     * Get result template objects for offering identifier, observable property
     * identifier and featureOfInterest identifier
     * 
     * @param offering
     *            Offering identifier
     * @param observedProperty
     *            Observable property identifier
     * @param featureOfInterest
     *            FeatureOfInterest identifier
     * @param session
     *            Hibernate session
     * @return Result template objects
     */
    @SuppressWarnings("unchecked")
    public List<ResultTemplate> getResultTemplateObject(final String offering, final String observedProperty,
            final Collection<String> featureOfInterest, final Session session) {
        final Criteria rtc =
                session.createCriteria(ResultTemplate.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        rtc.createCriteria(ObservationConstellation.OFFERING).add(Restrictions.eq(Offering.IDENTIFIER, offering));
        rtc.createCriteria(ObservationConstellation.OBSERVABLE_PROPERTY).add(
                Restrictions.eq(ObservableProperty.IDENTIFIER, observedProperty));
        if (featureOfInterest != null && !featureOfInterest.isEmpty()) {
            rtc.createCriteria(ResultTemplate.FEATURE_OF_INTEREST).add(
                    Restrictions.in(FeatureOfInterest.IDENTIFIER, featureOfInterest));
        }
        LOGGER.debug("QUERY getResultTemplateObject(offering, observedProperty, featureOfInterest): {}",
                HibernateHelper.getSqlString(rtc));
        return rtc.list();
    }

    /**
     * Check or insert result template
     * 
     * @param request
     *            Insert result template request
     * @param observationConstellation
     *            Observation constellation object
     * @param featureOfInterest
     *            FeatureOfInterest object
     * @param session
     *            Hibernate session
     * @throws OwsExceptionReport
     *             If the requested structure/encoding is invalid
     */
    public void checkOrInsertResultTemplate(final InsertResultTemplateRequest request,
            final ObservationConstellation observationConstellation, final FeatureOfInterest featureOfInterest,
            final Session session) throws OwsExceptionReport {
        final List<ResultTemplate> resultTemplates =
                getResultTemplateObject(observationConstellation.getOffering().getIdentifier(),
                        observationConstellation.getObservableProperty().getIdentifier(), null, session);
        if (CollectionHelper.isEmpty(resultTemplates)) {
            createAndSaveResultTemplate(request, observationConstellation, featureOfInterest, session);
        } else {
            final List<String> storedIdentifiers = new ArrayList<String>(0);
            for (final ResultTemplate storedResultTemplate : resultTemplates) {
                storedIdentifiers.add(storedResultTemplate.getIdentifier());
                final SosResultStructure storedStructure =
                        new SosResultStructure(storedResultTemplate.getResultStructure());
                final SosResultStructure newStructure = new SosResultStructure(request.getResultStructure().getXml());

                if (!storedStructure.equals(newStructure)) {
                    throw new InvalidParameterValueException().at(
                            Sos2Constants.InsertResultTemplateParams.proposedTemplate).withMessage(
                            "The requested resultStructure is different from already inserted result template "
                                    + "for procedure (%s) observedProperty (%s) and offering (%s)!",
                            observationConstellation.getProcedure().getIdentifier(),
                            observationConstellation.getObservableProperty().getIdentifier(),
                            observationConstellation.getOffering().getIdentifier());
                }
                final SosResultEncoding storedEncoding =
                        new SosResultEncoding(storedResultTemplate.getResultEncoding());
                final SosResultEncoding newEndoding = new SosResultEncoding(request.getResultEncoding().getXml());
                if (!storedEncoding.equals(newEndoding)) {
                    throw new InvalidParameterValueException().at(
                            Sos2Constants.InsertResultTemplateParams.proposedTemplate).withMessage(
                            "The requested resultEncoding is different from already inserted result template "
                                    + "for procedure (%s) observedProperty (%s) and offering (%s)!",
                            observationConstellation.getProcedure().getIdentifier(),
                            observationConstellation.getObservableProperty().getIdentifier(),
                            observationConstellation.getOffering().getIdentifier());
                }
            }
            if (request.getIdentifier() != null && !storedIdentifiers.contains(request.getIdentifier())) {
                /* save it only if the identifier is different */
                createAndSaveResultTemplate(request, observationConstellation, featureOfInterest, session);
            }
        }
    }

    /**
     * Insert result template
     * 
     * @param request
     *            Insert result template request
     * @param observationConstellation
     *            Observation constellation object
     * @param featureOfInterest
     *            FeatureOfInterest object
     * @param session
     *            Hibernate session
     * @throws OwsExceptionReport
     */
    private void createAndSaveResultTemplate(final InsertResultTemplateRequest request,
            final ObservationConstellation observationConstellation, final FeatureOfInterest featureOfInterest,
            final Session session) throws OwsExceptionReport {
        final ResultTemplate resultTemplate = new ResultTemplate();
        resultTemplate.setIdentifier(request.getIdentifier());
        resultTemplate.setProcedure(observationConstellation.getProcedure());
        resultTemplate.setObservableProperty(observationConstellation.getObservableProperty());
        resultTemplate.setOffering(observationConstellation.getOffering());
        resultTemplate.setFeatureOfInterest(featureOfInterest);
        resultTemplate.setResultStructure(request.getResultStructure().getXml());
        resultTemplate.setResultEncoding(request.getResultEncoding().getXml());
        session.save(resultTemplate);
        session.flush();
    }
}
