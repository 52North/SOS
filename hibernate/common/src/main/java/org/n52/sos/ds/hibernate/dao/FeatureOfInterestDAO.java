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

import static org.n52.sos.util.http.HTTPStatus.BAD_REQUEST;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.sos.ds.hibernate.dao.series.SeriesObservationDAO;
import org.n52.sos.ds.hibernate.entities.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterestType;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.ObservationInfo;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.RelatedFeature;
import org.n52.sos.ds.hibernate.entities.TFeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.series.Series;
import org.n52.sos.ds.hibernate.entities.series.SeriesObservationInfo;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.NoopTransformerAdapter;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.http.HTTPStatus;

import com.google.common.collect.Maps;

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

    /**
     * Get featureOfInterest object for identifier
     *
     * @param identifier
     *            FeatureOfInterest identifier
     * @param session
     *            Hibernate session Hibernate session
     * @return FeatureOfInterest entity
     */
    public FeatureOfInterest getFeatureOfInterest(final String identifier, final Session session) {
        Criteria criteria =
                session.createCriteria(FeatureOfInterest.class).add(
                        Restrictions.eq(FeatureOfInterest.IDENTIFIER, identifier));
        LOGGER.debug("QUERY getFeatureOfInterest(identifier): {}", HibernateHelper.getSqlString(criteria));
        return (FeatureOfInterest) criteria.uniqueResult();
    }

    /**
     * Get featureOfInterest identifiers for observation constellation
     *
     * @param observationConstellation
     *            Observation constellation
     * @param session
     *            Hibernate session Hibernate session
     * @return FeatureOfInterest identifiers for observation constellation
     * @throws CodedException
     */
    @SuppressWarnings("unchecked")
    public List<String> getFeatureOfInterestIdentifiersForObservationConstellation(
            final ObservationConstellation observationConstellation, final Session session) throws OwsExceptionReport {
        if (HibernateHelper.isNamedQuerySupported(
                SQL_QUERY_GET_FEATURE_OF_INTEREST_IDENTIFIER_FOR_OBSERVATION_CONSTELLATION, session)) {
            Query namedQuery =
                    session.getNamedQuery(SQL_QUERY_GET_FEATURE_OF_INTEREST_IDENTIFIER_FOR_OBSERVATION_CONSTELLATION);
            namedQuery.setParameter(PROCEDURE, observationConstellation.getProcedure().getIdentifier());
            namedQuery.setParameter(OBSERVABLE_PROPERTY, observationConstellation.getObservableProperty()
                    .getIdentifier());
            namedQuery.setParameter(OFFERING, observationConstellation.getOffering().getIdentifier());
            LOGGER.debug(
                    "QUERY getFeatureOfInterestIdentifiersForObservationConstellation(observationConstellation) with NamedQuery: {}",
                    SQL_QUERY_GET_FEATURE_OF_INTEREST_IDENTIFIER_FOR_OBSERVATION_CONSTELLATION);
            return namedQuery.list();
        } else {
            AbstractObservationDAO observationDAO = DaoFactory.getInstance().getObservationDAO();
            Criteria criteria = observationDAO.getDefaultObservationInfoCriteria(session);
            if (observationDAO instanceof SeriesObservationDAO) {
                Criteria seriesCriteria = criteria.createCriteria(SeriesObservationInfo.SERIES);
                seriesCriteria.add(Restrictions.eq(Series.PROCEDURE, observationConstellation.getProcedure())).add(
                        Restrictions.eq(Series.OBSERVABLE_PROPERTY, observationConstellation.getObservableProperty()));
                seriesCriteria.createCriteria(Series.FEATURE_OF_INTEREST).setProjection(
                        Projections.distinct(Projections.property(FeatureOfInterest.IDENTIFIER)));
            } else {
                criteria.add(Restrictions.eq(ObservationConstellation.PROCEDURE, observationConstellation.getProcedure()))
                        .add(Restrictions.eq(ObservationConstellation.OBSERVABLE_PROPERTY,
                                observationConstellation.getObservableProperty()));
                criteria.createCriteria(ObservationInfo.FEATURE_OF_INTEREST).setProjection(
                        Projections.distinct(Projections.property(FeatureOfInterest.IDENTIFIER)));
            }
            criteria.createCriteria(AbstractObservation.OFFERINGS).add(
                    Restrictions.eq(Offering.ID, observationConstellation.getOffering().getOfferingId()));
            LOGGER.debug(
                    "QUERY getFeatureOfInterestIdentifiersForObservationConstellation(observationConstellation): {}",
                    HibernateHelper.getSqlString(criteria));
            return criteria.list();
        }
    }

    /**
     * Get featureOfInterest identifiers for an offering identifier
     *
     * @param offeringIdentifiers
     *            Offering identifier
     * @param session
     *            Hibernate session Hibernate session
     * @return FeatureOfInterest identifiers for offering
     * @throws CodedException
     */
    @SuppressWarnings({ "unchecked" })
    public List<String> getFeatureOfInterestIdentifiersForOffering(final String offeringIdentifiers,
            final Session session) throws OwsExceptionReport {
        if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_FEATURE_OF_INTEREST_IDENTIFIER_FOR_OFFERING, session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_FEATURE_OF_INTEREST_IDENTIFIER_FOR_OFFERING);
            namedQuery.setParameter(OFFERING, offeringIdentifiers);
            LOGGER.debug("QUERY getFeatureOfInterestIdentifiersForOffering(offeringIdentifiers) with NamedQuery: {}",
                    SQL_QUERY_GET_FEATURE_OF_INTEREST_IDENTIFIER_FOR_OFFERING);
            return namedQuery.list();
        } else {
            AbstractObservationDAO observationDAO = DaoFactory.getInstance().getObservationDAO();
            Criteria c = observationDAO.getDefaultObservationInfoCriteria(session);
            if (observationDAO instanceof SeriesObservationDAO) {
                Criteria seriesCriteria = c.createCriteria(SeriesObservationInfo.SERIES);
                seriesCriteria.createCriteria(Series.FEATURE_OF_INTEREST).setProjection(
                        Projections.distinct(Projections.property(FeatureOfInterest.IDENTIFIER)));

            } else {
                c.createCriteria(AbstractObservation.FEATURE_OF_INTEREST).setProjection(
                        Projections.distinct(Projections.property(FeatureOfInterest.IDENTIFIER)));
            }
            new OfferingDAO().addOfferingRestricionForObservation(c, offeringIdentifiers);
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
    public List<FeatureOfInterest> getFeatureOfInterestObject(final Collection<String> identifiers,
            final Session session) {
        if (identifiers != null && !identifiers.isEmpty()) {
            Criteria criteria =
                    session.createCriteria(FeatureOfInterest.class).add(
                            Restrictions.in(FeatureOfInterest.IDENTIFIER, identifiers));
            LOGGER.debug("QUERY getFeatureOfInterestObject(identifiers): {}", HibernateHelper.getSqlString(criteria));
            return criteria.list();
        }
        return Collections.emptyList();
    }

    /**
     * Get all featureOfInterest objects
     *
     * @param session
     *            Hibernate session
     * @return FeatureOfInterest objects
     */
    @SuppressWarnings("unchecked")
    public List<FeatureOfInterest> getFeatureOfInterestObjects(final Session session) {
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
    public Map<String,Collection<String>> getFeatureOfInterestIdentifiersWithParents(final Session session) {
        Criteria criteria = session.createCriteria(FeatureOfInterest.class);
        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.property(FeatureOfInterest.IDENTIFIER));

        //get parents if transactional profile is active
        boolean tFoiSupported = HibernateHelper.isEntitySupported(TFeatureOfInterest.class);
        if (tFoiSupported) {
            criteria.createAlias(TFeatureOfInterest.PARENTS, "pfoi", JoinType.LEFT_OUTER_JOIN);
            projectionList.add(Projections.property("pfoi." + FeatureOfInterest.IDENTIFIER));
        }
        criteria.setProjection(projectionList);
        //return as List<Object[]> even if there's only one column for consistency
        criteria.setResultTransformer(NoopTransformerAdapter.INSTANCE);

        LOGGER.debug("QUERY getFeatureOfInterestIdentifiersWithParents(): {}", HibernateHelper.getSqlString(criteria));
        @SuppressWarnings("unchecked")
        List<Object[]> results = criteria.list();
        Map<String,Collection<String>> foiMap = Maps.newHashMap();
        for(Object[] result : results) {
            String featureIdentifier = (String) result[0];
            String parentFeatureIdentifier = null;
            if (tFoiSupported) {
                parentFeatureIdentifier = (String) result[1];
            }
            if (parentFeatureIdentifier != null) {
                CollectionHelper.addToCollectionMap(featureIdentifier, parentFeatureIdentifier, foiMap);
            } else {
                foiMap.put(featureIdentifier, null);
            }
        }
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
    public List<String> getFeatureOfInterestIdentifiers(Session session) {
        Criteria criteria =
                session.createCriteria(FeatureOfInterest.class).setProjection(
                        Projections.distinct(Projections.property(FeatureOfInterest.IDENTIFIER)));
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
    public FeatureOfInterest getOrInsertFeatureOfInterest(final String identifier, final String url,
            final Session session) {
        FeatureOfInterest feature = getFeatureOfInterest(identifier, session);
        if (feature == null) {
            feature = new TFeatureOfInterest();
            feature.setIdentifier(identifier);
            if (url != null && !url.isEmpty()) {
                feature.setUrl(url);
            }
            final FeatureOfInterestType featureOfInterestType =
                    new FeatureOfInterestTypeDAO().getOrInsertFeatureOfInterestType(OGCConstants.UNKNOWN, session);
            feature.setFeatureOfInterestType(featureOfInterestType);
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
    public void insertFeatureOfInterestRelationShip(final TFeatureOfInterest parentFeature,
            final FeatureOfInterest childFeature, final Session session) {
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
    public void checkOrInsertFeatureOfInterestRelatedFeatureRelation(final FeatureOfInterest featureOfInterest,
            final Offering offering, final Session session) {
        final List<RelatedFeature> relatedFeatures =
                new RelatedFeatureDAO().getRelatedFeatureForOffering(offering.getIdentifier(), session);
        if (CollectionHelper.isNotEmpty(relatedFeatures)) {
            for (final RelatedFeature relatedFeature : relatedFeatures) {
                insertFeatureOfInterestRelationShip((TFeatureOfInterest) relatedFeature.getFeatureOfInterest(),
                        featureOfInterest, session);
            }
        }
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
    public FeatureOfInterest checkOrInsertFeatureOfInterest(final AbstractFeature featureOfInterest,
            final Session session) throws OwsExceptionReport {
        if (featureOfInterest instanceof SamplingFeature) {
            final String featureIdentifier =
                    Configurator.getInstance().getFeatureQueryHandler()
                            .insertFeature((SamplingFeature) featureOfInterest, session);
            return getOrInsertFeatureOfInterest(featureIdentifier, ((SamplingFeature) featureOfInterest).getUrl(),
                    session);
        } else {
            throw new NoApplicableCodeException().withMessage("The used feature type '%s' is not supported.",
                    featureOfInterest != null ? featureOfInterest.getClass().getName() : featureOfInterest).setStatus(
                    BAD_REQUEST);
        }
    }
}
