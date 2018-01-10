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
package org.n52.sos.ds.hibernate.dao;

import static org.n52.janmayen.http.HTTPStatus.BAD_REQUEST;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.janmayen.function.Suppliers;
import org.n52.janmayen.http.HTTPStatus;
import org.n52.shetland.ogc.OGCConstants;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.ds.FeatureQueryHandler;
import org.n52.sos.ds.hibernate.dao.observation.AbstractObservationDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.SeriesObservationDAO;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterestType;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.observation.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.observation.legacy.ContextualReferencedLegacyObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.ContextualReferencedSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.Series;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.NoopTransformerAdapter;
import org.n52.sos.ds.hibernate.util.QueryHelper;
import org.n52.sos.service.Configurator;

/**
 * Hibernate data access class for featureOfInterest
 *
 * @author CarstenHollmann
 * @since 4.0.0
 */
public class FeatureOfInterestDAO extends AbstractIdentifierNameDescriptionDAO implements HibernateSqlQueryConstants {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeatureOfInterestDAO.class);

    private static final String SQL_QUERY_GET_FEATURE_OF_INTEREST_IDENTIFIER_FOR_OFFERING =
            "getFeatureOfInterestIdentifiersForOffering";

    private static final String SQL_QUERY_GET_FEATURE_OF_INTEREST_IDENTIFIER_FOR_OBSERVATION_CONSTELLATION =
            "getFeatureOfInterestIdentifiersForObservationConstellation";

    public FeatureOfInterestDAO(DaoFactory daoFactory) {
        super(daoFactory);
    }

    /**
     * Get featureOfInterest object for identifier
     *
     * @param identifier
     *            FeatureOfInterest identifier
     * @param session
     *            Hibernate session Hibernate session
     * @return FeatureOfInterest entity
     */
    public FeatureOfInterest get(String identifier, Session session) {
        Criteria criteria = session.createCriteria(FeatureOfInterest.class)
                .add(Restrictions.eq(FeatureOfInterest.IDENTIFIER, identifier));
        LOGGER.debug("QUERY getFeatureOfInterest(identifier): {}", HibernateHelper.getSqlString(criteria));
        return (FeatureOfInterest) criteria.uniqueResult();
    }

    /**
     * Get featureOfInterest identifiers for observation constellation
     *
     * @param oc
     *            Observation constellation
     * @param session
     *            Hibernate session Hibernate session
     * @return FeatureOfInterest identifiers for observation constellation
     * @throws CodedException
     */
    @SuppressWarnings("unchecked")
    public List<String> getIdentifiers(ObservationConstellation oc, Session session) throws OwsExceptionReport {
        if (HibernateHelper.isNamedQuerySupported(
                SQL_QUERY_GET_FEATURE_OF_INTEREST_IDENTIFIER_FOR_OBSERVATION_CONSTELLATION, session)) {
            Query namedQuery =
                    session.getNamedQuery(SQL_QUERY_GET_FEATURE_OF_INTEREST_IDENTIFIER_FOR_OBSERVATION_CONSTELLATION);
            namedQuery.setParameter(PROCEDURE, oc.getProcedure().getIdentifier());
            namedQuery.setParameter(OBSERVABLE_PROPERTY, oc.getObservableProperty().getIdentifier());
            namedQuery.setParameter(OFFERING, oc.getOffering().getIdentifier());
            LOGGER.debug(
                    "QUERY getFeatureOfInterestIdentifiersForObservationConstellation(observationConstellation) with NamedQuery: {}",
                    SQL_QUERY_GET_FEATURE_OF_INTEREST_IDENTIFIER_FOR_OBSERVATION_CONSTELLATION);
            return namedQuery.list();
        } else {
            AbstractObservationDAO observationDAO = getDaoFactory().getObservationDAO();
            Criteria criteria = observationDAO.getDefaultObservationInfoCriteria(session);
            if (observationDAO instanceof SeriesObservationDAO) {
                Criteria seriesCriteria = criteria.createCriteria(ContextualReferencedSeriesObservation.SERIES);
                seriesCriteria.add(Restrictions.eq(Series.PROCEDURE, oc.getProcedure())).add(Restrictions.eq(Series.OBSERVABLE_PROPERTY, oc.getObservableProperty()));
                seriesCriteria.createCriteria(Series.FEATURE_OF_INTEREST)
                        .setProjection(Projections.distinct(Projections.property(FeatureOfInterest.IDENTIFIER)));
            } else {
                criteria.add(Restrictions.eq(ObservationConstellation.PROCEDURE, oc.getProcedure()))
                        .add(Restrictions.eq(ObservationConstellation.OBSERVABLE_PROPERTY, oc.getObservableProperty()));
                criteria.createCriteria(ContextualReferencedLegacyObservation.FEATURE_OF_INTEREST)
                        .setProjection(Projections.distinct(Projections.property(FeatureOfInterest.IDENTIFIER)));
            }
            criteria.createCriteria(AbstractObservation.OFFERINGS).add(Restrictions.eq(Offering.ID, oc.getOffering().getOfferingId()));
            LOGGER.debug(
                    "QUERY getFeatureOfInterestIdentifiersForObservationConstellation(observationConstellation): {}",
                    HibernateHelper.getSqlString(criteria));
            return criteria.list();
        }
    }

    /**
     * Get featureOfInterest identifiers for an offering identifier
     *
     * @param offering
     *            Offering identifier
     * @param session
     *            Hibernate session Hibernate session
     * @return FeatureOfInterest identifiers for offering
     * @throws CodedException
     */
    @SuppressWarnings({ "unchecked" })
    public List<String> getIdentifiersForOffering(String offering, Session session)
            throws OwsExceptionReport {
        if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_FEATURE_OF_INTEREST_IDENTIFIER_FOR_OFFERING, session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_FEATURE_OF_INTEREST_IDENTIFIER_FOR_OFFERING);
            namedQuery.setParameter(OFFERING, offering);
            LOGGER.debug("QUERY getFeatureOfInterestIdentifiersForOffering(offeringIdentifiers) with NamedQuery: {}",
                    SQL_QUERY_GET_FEATURE_OF_INTEREST_IDENTIFIER_FOR_OFFERING);
            return namedQuery.list();
        } else {
            AbstractObservationDAO observationDAO = getDaoFactory().getObservationDAO();
            Criteria c = observationDAO.getDefaultObservationInfoCriteria(session);
            if (observationDAO instanceof SeriesObservationDAO) {
                c.createCriteria(ContextualReferencedSeriesObservation.SERIES)
                    .createCriteria(Series.FEATURE_OF_INTEREST)
                        .setProjection(Projections.distinct(Projections.property(FeatureOfInterest.IDENTIFIER)));

            } else {
                c.createCriteria(AbstractObservation.FEATURE_OF_INTEREST)
                    .setProjection(Projections.distinct(Projections.property(FeatureOfInterest.IDENTIFIER)));
            }

            getDaoFactory().getOfferingDAO().addOfferingRestricionForObservation(c, offering);
            LOGGER.debug("QUERY getFeatureOfInterestIdentifiersForOffering(offeringIdentifiers): {}",
                    HibernateHelper.getSqlString(c));
            return c.list();
        }
    }

    /**
     * Get featureOfInterest objects for featureOfInterest identifiers
     *
     * @param identifiers
     *            FeatureOfInterest identifiers
     * @param session
     *            Hibernate session
     * @return FeatureOfInterest objects
     */
    @SuppressWarnings("unchecked")
    public List<FeatureOfInterest> getFeatureOfInterestObject(Collection<String> identifiers, Session session) {
        if (identifiers == null || identifiers.isEmpty()) {
            return Collections.emptyList();
        }
        Criteria criteria = session.createCriteria(FeatureOfInterest.class)
                .add(QueryHelper.getCriterionForFoiIds(FeatureOfInterest.IDENTIFIER, identifiers));
        LOGGER.debug("QUERY getFeatureOfInterestObject(identifiers): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    /**
     * Get all featureOfInterest objects
     *
     * @param session
     *            Hibernate session
     * @return FeatureOfInterest objects
     */
    @SuppressWarnings("unchecked")
    public List<FeatureOfInterest> getFeatureOfInterestObjects(Session session) {
        Criteria criteria = session.createCriteria(FeatureOfInterest.class);
        LOGGER.debug("QUERY getFeatureOfInterestObjects(identifier): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    /**
     * Load FOI identifiers and parent ids for use in the cache. Just loading the ids allows us to not load
     * the geometry columns, XML, etc.
     *
     * @param session
     * @return Map keyed by FOI identifiers, with value collections of parent FOI identifiers if supported
     */
    public Map<String,Collection<String>> getIdentifiersWithParents(Session session) {
        Criteria criteria = session.createCriteria(FeatureOfInterest.class)
                .createAlias(FeatureOfInterest.PARENTS, "pfoi", JoinType.LEFT_OUTER_JOIN)
                .setProjection(Projections.projectionList()
                        .add(Projections.property(FeatureOfInterest.IDENTIFIER))
                        .add(Projections.property("pfoi." + FeatureOfInterest.IDENTIFIER)));
        //return as List<Object[]> even if there's only one column for consistency
        criteria.setResultTransformer(NoopTransformerAdapter.INSTANCE);

        LOGGER.debug("QUERY getFeatureOfInterestIdentifiersWithParents(): {}", HibernateHelper.getSqlString(criteria));
        @SuppressWarnings("unchecked")
        List<Object[]> results = criteria.list();
        Map<String,Collection<String>> foiMap = new HashMap<>();
        results.forEach(result -> {
            String featureIdentifier = (String) result[0];
            String parentFeatureIdentifier = (String) result[1];
            if (parentFeatureIdentifier != null) {
                foiMap.computeIfAbsent(featureIdentifier, Suppliers.asFunction(ArrayList::new))
                        .add(parentFeatureIdentifier);
            } else {
                foiMap.put(featureIdentifier, null);
            }
        });
        return foiMap;
    }

    /**
     * Get all featureOfInterest identifiers
     *
     * @param session
     *            Hibernate session
     * @return FeatureOfInterest identifiers
     */
    @SuppressWarnings("unchecked")
    public List<String> getIdentifiers(Session session) {
        Criteria criteria = session.createCriteria(FeatureOfInterest.class)
                .setProjection(Projections.distinct(Projections.property(FeatureOfInterest.IDENTIFIER)));
        LOGGER.debug("QUERY getFeatureOfInterestIdentifiers(): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    /**
     * Insert and/or get featureOfInterest object for identifier
     *
     * @param identifier
     *            FeatureOfInterest identifier
     * @param url
     *            FeatureOfInterest URL, if defined as link
     * @param session
     *            Hibernate session
     * @return FeatureOfInterest object
     */
    public FeatureOfInterest getOrInsert(String identifier, String url, Session session) {
        FeatureOfInterest feature = get(identifier, session);
        if (feature == null) {
            feature = new FeatureOfInterest();
            feature.setIdentifier(identifier);
            if (url != null && !url.isEmpty()) {
                feature.setUrl(url);
            }
            FeatureOfInterestType type =
                    new FeatureOfInterestTypeDAO().getOrInsertFeatureOfInterestType(OGCConstants.UNKNOWN, session);
            feature.setFeatureOfInterestType(type);
            session.save(feature);
        } else if (feature.getUrl() != null && !feature.getUrl().isEmpty() && url != null && !url.isEmpty()) {
            feature.setUrl(url);
            session.saveOrUpdate(feature);
        }
        //don't flush here because we may be batching
        return feature;
    }

    /**
     * Insert featureOfInterest relationship
     *
     * @param parentFeature
     *            Parent featureOfInterest
     * @param childFeature
     *            Child featureOfInterest
     * @param session
     *            Hibernate session
     */
    public void insertRelationship(FeatureOfInterest parentFeature, FeatureOfInterest childFeature, Session session) {
        parentFeature.getChilds().add(childFeature);
        session.saveOrUpdate(parentFeature);
        //don't flush here because we may be batching
    }

    /**
     * Insert featureOfInterest/related feature relations if relatedFeatures
     * exists for offering.
     *
     * @param featureOfInterest
     *            FeatureOfInerest
     * @param offering
     *            Offering
     * @param session
     *            Hibernate session
     */
    public void checkOrInsertRelatedFeatureRelation(FeatureOfInterest featureOfInterest, Offering offering, Session session) {
        getDaoFactory().getRelatedFeatureDAO().getRelatedFeatureForOffering(offering.getIdentifier(), session).stream()
                .filter(relatedFeature -> !featureOfInterest.getIdentifier().equals(relatedFeature.getFeatureOfInterest().getIdentifier()))
                .forEachOrdered(relatedFeature -> insertRelationship(relatedFeature.getFeatureOfInterest(), featureOfInterest, session));

    }

    /**
     * Insert featureOfInterest if it is supported
     *
     * @param featureOfInterest
     *            SOS featureOfInterest to insert
     * @param session
     *            Hibernate session
     * @return FeatureOfInterest object
     * @throws NoApplicableCodeException
     *             If SOS feature type is not supported (with status
     *             {@link HTTPStatus}.BAD_REQUEST
     */
    public FeatureOfInterest checkOrInsert(AbstractFeature featureOfInterest, Session session) throws OwsExceptionReport {
        if (featureOfInterest instanceof SamplingFeature) {
            SamplingFeature sf = (SamplingFeature) featureOfInterest;
            String featureIdentifier = getFeatureQueryHandler().insertFeature(sf, session);
            return getOrInsert(featureIdentifier, sf.getUrl(), session);
        } else {
            Object type = featureOfInterest != null ? featureOfInterest.getClass().getName() : featureOfInterest;
            throw new NoApplicableCodeException()
                    .withMessage("The used feature type '%s' is not supported.", type)
                    .setStatus(BAD_REQUEST);
        }
    }

    private FeatureQueryHandler getFeatureQueryHandler() {
        return Configurator.getInstance().getFeatureQueryHandler();
    }
}
