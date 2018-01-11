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
package org.n52.sos.ds.hibernate.dao.observation;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.spatial.criterion.SpatialProjections;
import org.hibernate.transform.ResultTransformer;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.shetland.ogc.filter.FilterConstants.TimeOperator;
import org.n52.shetland.ogc.filter.TemporalFilter;
import org.n52.shetland.ogc.gml.time.IndeterminateValue;
import org.n52.shetland.ogc.gml.time.Time;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.om.NamedValue;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.SingleObservationValue;
import org.n52.shetland.ogc.om.values.BooleanValue;
import org.n52.shetland.ogc.om.values.CategoryValue;
import org.n52.shetland.ogc.om.values.ComplexValue;
import org.n52.shetland.ogc.om.values.CountValue;
import org.n52.shetland.ogc.om.values.CvDiscretePointCoverage;
import org.n52.shetland.ogc.om.values.GeometryValue;
import org.n52.shetland.ogc.om.values.HrefAttributeValue;
import org.n52.shetland.ogc.om.values.MultiPointCoverage;
import org.n52.shetland.ogc.om.values.NilTemplateValue;
import org.n52.shetland.ogc.om.values.ProfileValue;
import org.n52.shetland.ogc.om.values.QuantityRangeValue;
import org.n52.shetland.ogc.om.values.QuantityValue;
import org.n52.shetland.ogc.om.values.RectifiedGridCoverage;
import org.n52.shetland.ogc.om.values.ReferenceValue;
import org.n52.shetland.ogc.om.values.SweDataArrayValue;
import org.n52.shetland.ogc.om.values.TLVTValue;
import org.n52.shetland.ogc.om.values.TVPValue;
import org.n52.shetland.ogc.om.values.TextValue;
import org.n52.shetland.ogc.om.values.TimeRangeValue;
import org.n52.shetland.ogc.om.values.UnknownValue;
import org.n52.shetland.ogc.om.values.Value;
import org.n52.shetland.ogc.om.values.XmlValue;
import org.n52.shetland.ogc.om.values.visitor.ValueVisitor;
import org.n52.shetland.ogc.ows.exception.InvalidParameterValueException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OptionNotSupportedException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.ExtendedIndeterminateTime;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.ogc.swe.SweAbstractDataRecord;
import org.n52.shetland.ogc.swe.SweField;
import org.n52.shetland.util.CollectionHelper;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.shetland.util.ReferencedEnvelope;
import org.n52.sos.ds.hibernate.dao.AbstractIdentifierNameDescriptionDAO;
import org.n52.sos.ds.hibernate.dao.CodespaceDAO;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.ObservablePropertyDAO;
import org.n52.sos.ds.hibernate.dao.ObservationConstellationDAO;
import org.n52.sos.ds.hibernate.dao.ObservationTypeDAO;
import org.n52.sos.ds.hibernate.dao.ParameterDAO;
import org.n52.sos.ds.hibernate.dao.UnitDAO;
import org.n52.sos.ds.hibernate.entities.Codespace;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasSamplingGeometry;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Unit;
import org.n52.sos.ds.hibernate.entities.observation.AbstractBaseObservation;
import org.n52.sos.ds.hibernate.entities.observation.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.observation.ContextualReferencedObservation;
import org.n52.sos.ds.hibernate.entities.observation.Observation;
import org.n52.sos.ds.hibernate.entities.observation.TemporalReferencedObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.ComplexObservation;
import org.n52.sos.ds.hibernate.entities.parameter.Parameter;
import org.n52.sos.ds.hibernate.entities.parameter.ParameterFactory;
import org.n52.sos.ds.hibernate.util.HibernateConstants;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.ObservationSettingProvider;
import org.n52.sos.ds.hibernate.util.ScrollableIterable;
import org.n52.sos.ds.hibernate.util.SosTemporalRestrictions;
import org.n52.sos.ds.hibernate.util.SpatialRestrictions;
import org.n52.sos.ds.hibernate.util.TimeExtrema;
import org.n52.sos.ds.hibernate.util.observation.HibernateObservationUtilities;
import org.n52.sos.util.GeometryHandler;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;

/**
 * Abstract Hibernate data access class for observations.
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.0.0
 *
 */
public abstract class AbstractObservationDAO extends AbstractIdentifierNameDescriptionDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractObservationDAO.class);

    private static final String SQL_QUERY_OBSERVATION_TIME_EXTREMA = "getObservationTimeExtrema";

    public AbstractObservationDAO(DaoFactory daoFactory) {
        super(daoFactory);
    }

    /**
     * Add observation identifier (procedure, observableProperty,
     * featureOfInterest) to observation
     *
     * @param observationIdentifiers
     *                               Observation identifiers
     * @param observation
     *                               Observation to add identifiers
     * @param session
     *                               Hibernate session
     *
     * @throws OwsExceptionReport
     */
    protected abstract void addObservationContextToObservation(ObservationContext observationIdentifiers,
                                                               Observation<?> observation, Session session) throws
            OwsExceptionReport;

    /**
     * Get Hibernate Criteria for querying observations with parameters
     * featureOfInterst and procedure
     *
     * @param feature
     *                  FeatureOfInterest to query for
     * @param procedure
     *                  Procedure to query for
     * @param session
     *                  Hiberante Session
     *
     * @return Criteria to query observations
     */
    public abstract Criteria getObservationInfoCriteriaForFeatureOfInterestAndProcedure(String feature,
                                                                                        String procedure,
                                                                                        Session session);

    /**
     * Get Hibernate Criteria for querying observations with parameters
     * featureOfInterst and offering
     *
     * @param feature
     *                 FeatureOfInterest to query for
     * @param offering
     *                 Offering to query for
     * @param session
     *                 Hiberante Session
     *
     * @return Criteria to query observations
     */
    public abstract Criteria getObservationInfoCriteriaForFeatureOfInterestAndOffering(String feature, String offering,
                                                                                       Session session);

    /**
     * Get Hibernate Criteria for observation with restriction procedure
     *
     * @param procedure
     *                  Procedure parameter
     * @param session
     *                  Hibernate session
     *
     * @return Hibernate Criteria to query observations
     *
     * @throws OwsExceptionReport
     */
    public abstract Criteria getObservationCriteriaForProcedure(String procedure, Session session)
            throws OwsExceptionReport;

    /**
     * Get Hibernate Criteria for observation with restriction
     * observableProperty
     *
     * @param observableProperty
     * @param session
     *                           Hibernate session
     *
     * @return Hibernate Criteria to query observations
     *
     * @throws OwsExceptionReport
     */
    public abstract Criteria getObservationCriteriaForObservableProperty(String observableProperty, Session session)
            throws OwsExceptionReport;

    /**
     * Get Hibernate Criteria for observation with restriction featureOfInterest
     *
     * @param featureOfInterest
     * @param session
     *                          Hibernate session
     *
     * @return Hibernate Criteria to query observations
     *
     * @throws OwsExceptionReport
     */
    public abstract Criteria getObservationCriteriaForFeatureOfInterest(String featureOfInterest, Session session)
            throws OwsExceptionReport;

    /**
     * Get Hibernate Criteria for observation with restrictions procedure and
     * observableProperty
     *
     * @param procedure
     * @param observableProperty
     * @param session
     *                           Hibernate session
     *
     * @return Hibernate Criteria to query observations
     *
     * @throws OwsExceptionReport
     */
    public abstract Criteria getObservationCriteriaFor(String procedure, String observableProperty, Session session)
            throws OwsExceptionReport;

    /**
     * Get Hibernate Criteria for observation with restrictions procedure,
     * observableProperty and featureOfInterest
     *
     * @param procedure
     * @param observableProperty
     * @param featureOfInterest
     * @param session
     *                           Hibernate session
     *
     * @return Hibernate Criteria to query observations
     *
     * @throws OwsExceptionReport
     */
    public abstract Criteria getObservationCriteriaFor(String procedure, String observableProperty,
                                                       String featureOfInterest, Session session) throws OwsExceptionReport;

    /**
     * Get all observation identifiers for a procedure.
     *
     * @param procedureIdentifier
     * @param session
     *
     * @return Collection of observation identifiers
     */
    public abstract Collection<String> getObservationIdentifiers(String procedureIdentifier, Session session);

    /**
     * Get Hibernate Criteria for {@link TemporalReferencedObservation} with restrictions observation identifiers
     *
     * @param observation The observation with restriction values
     * @param session
     *                    Hibernate session
     *
     * @return Hibernate Criteria to query observations
     *
     * @throws OwsExceptionReport
     */
    public abstract Criteria getTemoralReferencedObservationCriteriaFor(OmObservation observation, Session session)
            throws OwsExceptionReport;

    /**
     * Query observation by identifier
     *
     * @param identifier
     *                   Observation identifier (gml:identifier)
     * @param session
     *                   Hiberante session
     *
     * @return Observation
     */
    public Observation<?> getObservationByIdentifier(String identifier, Session session) {
        Criteria criteria = getDefaultObservationCriteria(session);
        addObservationIdentifierToCriteria(criteria, identifier, session);
        return (Observation<?>) criteria.uniqueResult();
    }

    /**
     * Check if there are numeric observations for the offering
     *
     * @param offeringIdentifier
     *                           Offering identifier
     * @param session
     *                           Hibernate session
     *
     * @return If there are observations or not
     */
    public boolean checkNumericObservationsFor(String offeringIdentifier, Session session) {
        return checkObservationFor(getObservationFactory().numericClass(), offeringIdentifier, session);
    }

    /**
     * Check if there are boolean observations for the offering
     *
     * @param offeringIdentifier
     *                           Offering identifier
     * @param session
     *                           Hibernate session
     *
     * @return If there are observations or not
     */
    public boolean checkBooleanObservationsFor(String offeringIdentifier, Session session) {
        return checkObservationFor(getObservationFactory().truthClass(), offeringIdentifier, session);
    }

    /**
     * Check if there are count observations for the offering
     *
     * @param offeringIdentifier
     *                           Offering identifier
     * @param session
     *                           Hibernate session
     *
     * @return If there are observations or not
     */
    public boolean checkCountObservationsFor(String offeringIdentifier, Session session) {
        return checkObservationFor(getObservationFactory().countClass(), offeringIdentifier, session);
    }

    /**
     * Check if there are category observations for the offering
     *
     * @param offeringIdentifier
     *                           Offering identifier
     * @param session
     *                           Hibernate session
     *
     * @return If there are observations or not
     */
    public boolean checkCategoryObservationsFor(String offeringIdentifier, Session session) {
        return checkObservationFor(getObservationFactory().categoryClass(), offeringIdentifier, session);
    }

    /**
     * Check if there are text observations for the offering
     *
     * @param offeringIdentifier
     *                           Offering identifier
     * @param session
     *                           Hibernate session
     *
     * @return If there are observations or not
     */
    public boolean checkTextObservationsFor(String offeringIdentifier, Session session) {
        return checkObservationFor(getObservationFactory().textClass(), offeringIdentifier, session);
    }

    /**
     * Check if there are blob observations for the offering
     *
     * @param offeringIdentifier
     *                           Offering identifier
     * @param session
     *                           Hibernate session
     *
     * @return If there are observations or not
     */
    public boolean checkBlobObservationsFor(String offeringIdentifier, Session session) {
        return checkObservationFor(getObservationFactory().blobClass(), offeringIdentifier, session);
    }

    /**
     * Check if there are geometry observations for the offering
     *
     * @param offeringIdentifier
     *                           Offering identifier
     * @param session
     *                           Hibernate session
     *
     * @return If there are observations or not
     */
    public boolean checkGeometryObservationsFor(String offeringIdentifier, Session session) {
        return checkObservationFor(getObservationFactory().geometryClass(), offeringIdentifier, session);
    }

    /**
     * Check if there are complex observations for the offering
     *
     * @param offeringIdentifier
     *                           Offering identifier
     * @param session
     *                           Hibernate session
     *
     * @return If there are observations or not
     */
    public boolean checkComplexObservationsFor(String offeringIdentifier, Session session) {
        return checkObservationFor(getObservationFactory().complexClass(), offeringIdentifier, session);
    }

    /**
     * Check if there are SweDataArray observations for the offering
     *
     * @param offeringIdentifier
     *                           Offering identifier
     * @param session
     *                           Hibernate session
     *
     * @return If there are observations or not
     */
    public boolean checkSweDataArrayObservationsFor(String offeringIdentifier, Session session) {
        return checkObservationFor(getObservationFactory().sweDataArrayClass(), offeringIdentifier, session);
    }

    /**
     * Get Hibernate Criteria for result model
     *
     * @param resultModel
     *                    Result model
     * @param session
     *                    Hibernate session
     *
     * @return Hibernate Criteria
     */
    public Criteria getObservationClassCriteriaForResultModel(String resultModel, Session session) {
        return createCriteriaForObservationClass(getObservationFactory().classForObservationType(resultModel), session);
    }

    /**
     * Get default Hibernate Criteria to query observations, default flag ==
     * <code>false</code>
     *
     * @param session
     *                Hiberante session
     *
     * @return Default Criteria
     */
    public Criteria getDefaultObservationCriteria(Session session) {
        return getDefaultCriteria(getObservationFactory().observationClass(), session);
    }

    /**
     * Get default Hibernate Criteria to query observation info, default flag ==
     * <code>false</code>
     *
     * @param session
     *                Hiberante session
     *
     * @return Default Criteria
     */
    public Criteria getDefaultObservationInfoCriteria(Session session) {
        return getDefaultCriteria(getObservationFactory().contextualReferencedClass(), session);
    }

    /**
     * Get default Hibernate Criteria to query observation time, default flag ==
     * <code>false</code>
     *
     * @param session
     *                Hibernate session
     *
     * @return Default Criteria
     */
    public Criteria getDefaultObservationTimeCriteria(Session session) {
        return getDefaultCriteria(getObservationFactory().temporalReferencedClass(), session);
    }

    @SuppressWarnings("rawtypes")
    private Criteria getDefaultCriteria(Class clazz, Session session) {
        Criteria criteria = session.createCriteria(clazz)
                .add(Restrictions.eq(Observation.DELETED, false));

        if (!isIncludeChildObservableProperties()) {
            criteria.add(Restrictions.eq(Observation.CHILD, false));
        } else {
            criteria.add(Restrictions.eq(Observation.PARENT, false));
        }

        return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    }

    /**
     * Get Hibernate Criteria for observation with restriction procedure Insert
     * a multi value observation for observation constellations and
     * featureOfInterest
     *
     * @param observationConstellations
     *                                  Observation constellation objects
     * @param feature
     *                                  FeatureOfInterest object
     * @param containerObservation
     *                                  SOS observation
     * @param codespaceCache
     *                                  Map based codespace object cache to prevent redundant queries
     * @param unitCache
     *                                  Map based unit object cache to prevent redundant queries
     * @param session
     *                                  Hibernate session
     *
     * @throws OwsExceptionReport
     *                            If an error occurs
     */
    public void insertObservationMultiValue(Set<ObservationConstellation> observationConstellations,
                                            FeatureOfInterest feature, OmObservation containerObservation,
                                            Map<String, Codespace> codespaceCache,
                                            Map<String, Unit> unitCache, Session session) throws OwsExceptionReport {
        List<OmObservation> unfoldObservations = HibernateObservationUtilities.unfoldObservation(containerObservation);
        for (OmObservation sosObservation : unfoldObservations) {
            insertObservationSingleValue(observationConstellations, feature, sosObservation, codespaceCache, unitCache,
                                         session);
        }
    }

    /**
     * Insert a single observation for observation constellations and
     * featureOfInterest without local caching for codespaces and units
     *
     * @param hObservationConstellations
     *                                   Observation constellation objects
     * @param hFeature
     *                                   FeatureOfInterest object
     * @param sosObservation
     *                                   SOS observation to insert
     * @param session
     *                                   Hibernate session
     *
     * @throws OwsExceptionReport
     */
    public void insertObservationSingleValue(Set<ObservationConstellation> hObservationConstellations,
                                             FeatureOfInterest hFeature, OmObservation sosObservation, Session session)
            throws OwsExceptionReport {
        insertObservationSingleValue(hObservationConstellations, hFeature, sosObservation, null, null, session);
    }

    /**
     * Insert a single observation for observation constellations and
     * featureOfInterest with local caching for codespaces and units
     *
     * @param hObservationConstellations
     *                                   Observation constellation objects
     * @param hFeature
     *                                   FeatureOfInterest object
     * @param sosObservation
     *                                   SOS observation to insert
     * @param codespaceCache
     *                                   Map cache for codespace objects (to prevent redundant
     *                                   querying)
     * @param unitCache
     *                                   Map cache for unit objects (to prevent redundant querying)
     * @param session
     *                                   Hibernate session
     *
     * @throws OwsExceptionReport
     */
    @SuppressWarnings("rawtypes")
    public void insertObservationSingleValue(Set<ObservationConstellation> hObservationConstellations,
                                             FeatureOfInterest hFeature, OmObservation sosObservation,
                                             Map<String, Codespace> codespaceCache,
                                             Map<String, Unit> unitCache, Session session)
            throws OwsExceptionReport {
        SingleObservationValue<?> value
                = (SingleObservationValue) sosObservation.getValue();
        ObservationPersister persister = new ObservationPersister(
                getGeometryHandler(),
                this,
                getDaoFactory(),
                sosObservation,
                hObservationConstellations,
                hFeature,
                codespaceCache,
                unitCache,
                session
        );
        value.getValue().accept(persister);
    }

    protected ObservationContext createObservationContext() {
        return new ObservationContext();
    }

    protected ObservationContext fillObservationContext(ObservationContext ctx, OmObservation sosObservation,
                                                        Session session) {
        return ctx;
    }

    /**
     * If the local codespace cache isn't null, use it when retrieving
     * codespaces.
     *
     * @param codespace
     *                   Codespace
     * @param localCache
     *                   Cache (possibly null)
     * @param session
     *
     * @return Codespace
     */
    protected Codespace getCodespace(String codespace, Map<String, Codespace> localCache, Session session) {
        if (localCache != null && localCache.containsKey(codespace)) {
            return localCache.get(codespace);
        } else {
            // query codespace and set cache
            Codespace hCodespace = new CodespaceDAO().getOrInsertCodespace(codespace, session);
            if (localCache != null) {
                localCache.put(codespace, hCodespace);
            }
            return hCodespace;
        }
    }

    /**
     * If the local unit cache isn't null, use it when retrieving unit.
     *
     * @param unit
     *                   Unit
     * @param localCache
     *                   Cache (possibly null)
     * @param session
     *
     * @return Unit
     */
    protected Unit getUnit(String unit, Map<String, Unit> localCache, Session session) {
        if (localCache != null && localCache.containsKey(unit)) {
            return localCache.get(unit);
        } else {
            // query unit and set cache
            Unit hUnit = new UnitDAO().getOrInsertUnit(unit, session);
            if (localCache != null) {
                localCache.put(unit, hUnit);
            }
            return hUnit;
        }
    }

    /**
     * Add observation identifier (gml:identifier) to Hibernate Criteria
     *
     * @param criteria
     *                   Hibernate Criteria
     * @param identifier
     *                   Observation identifier (gml:identifier)
     * @param session
     *                   Hibernate session
     */
    protected void addObservationIdentifierToCriteria(Criteria criteria, String identifier, Session session) {
        criteria.add(Restrictions.eq(Observation.IDENTIFIER, identifier));
    }

    // /**
    // * Add offerings to observation and return the observation identifiers
    // * procedure and observableProperty
    // *
    // * @param hObservation
    // * Observation to add offerings
    // * @param hObservationConstellations
    // * Observation constellation with offerings, procedure and
    // * observableProperty
    // * @return ObservaitonIdentifiers object with procedure and
    // * observableProperty
    // */
    // protected ObservationIdentifiers
    // addOfferingsToObaservationAndGetProcedureObservableProperty(
    // AbstractObservation hObservation, Set<ObservationConstellation>
    // hObservationConstellations) {
    // Iterator<ObservationConstellation> iterator =
    // hObservationConstellations.iterator();
    // boolean firstObsConst = true;
    // ObservationIdentifiers observationIdentifiers = new
    // ObservationIdentifiers();
    // while (iterator.hasNext()) {
    // ObservationConstellation observationConstellation = iterator.next();
    // if (firstObsConst) {
    // observationIdentifiers.setObservableProperty(observationConstellation.getObservableProperty());
    // observationIdentifiers.setProcedure(observationConstellation.getProcedure());
    // firstObsConst = false;
    // }
    // hObservation.getOfferings().add(observationConstellation.getOffering());
    // }
    // return observationIdentifiers;
    // }
    protected void finalizeObservationInsertion(OmObservation sosObservation, Observation<?> hObservation,
                                                Session session) throws OwsExceptionReport {
        // TODO if this observation is a deleted=true, how to set deleted=false
        // instead of insert

    }

    /**
     * Insert om:parameter into database. Differs between Spatial Filtering
     * Profile parameter and others.
     *
     * @param parameter
     *                    om:Parameter to insert
     * @param observation
     *                    related observation
     * @param session
     *                    Hibernate session
     *
     * @throws OwsExceptionReport
     */
    @Deprecated
    protected void insertParameter(Collection<NamedValue<?>> parameter, Observation<?> observation, Session session)
            throws OwsExceptionReport {
        for (NamedValue<?> namedValue : parameter) {
            if (!Sos2Constants.HREF_PARAMETER_SPATIAL_FILTERING_PROFILE.equals(namedValue.getName().getHref())) {
                throw new OptionNotSupportedException().at("om:parameter")
                        .withMessage("The om:parameter support is not yet implemented!");
            }
        }
    }

    /**
     * Check if there are observations for the offering
     *
     * @param clazz
     *                           Observation sub class
     * @param offeringIdentifier
     *                           Offering identifier
     * @param session
     *                           Hibernate session
     *
     * @return If there are observations or not
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected boolean checkObservationFor(Class clazz, String offeringIdentifier, Session session) {
        Criteria c = session.createCriteria(clazz).add(Restrictions.eq(Observation.DELETED, false));
        c.createCriteria(Observation.OFFERINGS).add(Restrictions.eq(Offering.IDENTIFIER, offeringIdentifier));
        c.setMaxResults(1);
        LOGGER.debug("QUERY checkObservationFor(clazz, offeringIdentifier): {}", HibernateHelper.getSqlString(c));
        return CollectionHelper.isNotEmpty(c.list());
    }

    /**
     * Get min phenomenon time from observations
     *
     * @param session
     *                Hibernate session Hibernate session
     *
     * @return min time
     */
    public DateTime getMinPhenomenonTime(Session session) {
        Criteria criteria = session.createCriteria(getObservationFactory().temporalReferencedClass())
                .setProjection(Projections.min(Observation.PHENOMENON_TIME_START))
                .add(Restrictions.eq(Observation.DELETED, false));
        LOGGER.debug("QUERY getMinPhenomenonTime(): {}", HibernateHelper.getSqlString(criteria));
        Object min = criteria.uniqueResult();
        if (min != null) {
            return new DateTime(min, DateTimeZone.UTC);
        }
        return null;
    }

    /**
     * Get max phenomenon time from observations
     *
     * @param session
     *                Hibernate session Hibernate session
     *
     * @return max time
     */
    public DateTime getMaxPhenomenonTime(Session session) {

        Criteria criteriaStart = session.createCriteria(getObservationFactory().temporalReferencedClass())
                .setProjection(Projections.max(Observation.PHENOMENON_TIME_START))
                .add(Restrictions.eq(Observation.DELETED, false));
        LOGGER.debug("QUERY getMaxPhenomenonTime() start: {}", HibernateHelper.getSqlString(criteriaStart));
        Object maxStart = criteriaStart.uniqueResult();

        Criteria criteriaEnd = session.createCriteria(getObservationFactory().temporalReferencedClass())
                .setProjection(Projections.max(Observation.PHENOMENON_TIME_END))
                .add(Restrictions.eq(Observation.DELETED, false));
        LOGGER.debug("QUERY getMaxPhenomenonTime() end: {}", HibernateHelper.getSqlString(criteriaEnd));
        Object maxEnd = criteriaEnd.uniqueResult();
        if (maxStart == null && maxEnd == null) {
            return null;
        } else {
            DateTime start = new DateTime(maxStart, DateTimeZone.UTC);
            if (maxEnd != null) {
                DateTime end = new DateTime(maxEnd, DateTimeZone.UTC);
                if (end.isAfter(start)) {
                    return end;
                }
            }
            return start;
        }
    }

    /**
     * Get min result time from observations
     *
     * @param session
     *                Hibernate session Hibernate session
     *
     * @return min time
     */
    public DateTime getMinResultTime(Session session) {

        Criteria criteria = session.createCriteria(getObservationFactory().temporalReferencedClass())
                .setProjection(Projections.min(Observation.RESULT_TIME))
                .add(Restrictions.eq(Observation.DELETED, false));
        LOGGER.debug("QUERY getMinResultTime(): {}", HibernateHelper.getSqlString(criteria));
        Object min = criteria.uniqueResult();
        return (min == null) ? null : new DateTime(min, DateTimeZone.UTC);
    }

    /**
     * Get max phenomenon time from observations
     *
     * @param session
     *                Hibernate session Hibernate session
     *
     * @return max time
     */
    public DateTime getMaxResultTime(Session session) {

        Criteria criteria = session.createCriteria(getObservationFactory().temporalReferencedClass())
                .setProjection(Projections.max(Observation.RESULT_TIME))
                .add(Restrictions.eq(Observation.DELETED, false));
        LOGGER.debug("QUERY getMaxResultTime(): {}", HibernateHelper.getSqlString(criteria));
        Object max = criteria.uniqueResult();
        return (max == null) ? null : new DateTime(max, DateTimeZone.UTC);
    }

    /**
     * Get global temporal bounding box
     *
     * @param session
     *                Hibernate session the session
     *
     * @return the global getEqualRestiction bounding box over all observations,
     *         or <tt>null</tt>
     */
    public TimePeriod getGlobalTemporalBoundingBox(Session session) {
        if (session != null) {
            Criteria criteria = session.createCriteria(getObservationFactory().temporalReferencedClass());
            criteria.add(Restrictions.eq(Observation.DELETED, false));
            criteria.setProjection(Projections.projectionList()
                    .add(Projections.min(Observation.PHENOMENON_TIME_START))
                    .add(Projections.max(Observation.PHENOMENON_TIME_START))
                    .add(Projections.max(Observation.PHENOMENON_TIME_END)));
            LOGGER.debug("QUERY getGlobalTemporalBoundingBox(): {}", HibernateHelper.getSqlString(criteria));
            Object temporalBoundingBox = criteria.uniqueResult();
            if (temporalBoundingBox instanceof Object[]) {
                Object[] record = (Object[]) temporalBoundingBox;
                TimePeriod bBox = createTimePeriod((Timestamp) record[0], (Timestamp) record[1], (Timestamp) record[2]);
                return bBox;
            }
        }
        return null;
    }

    /**
     * Get order for {@link ExtendedIndeterminateTime} value
     *
     * @param indetTime
     *                  Value to get order for
     *
     * @return Order
     */
    protected Order getOrder(IndeterminateValue indetTime) {
        if (indetTime.equals(ExtendedIndeterminateTime.FIRST)) {
            return Order.asc(Observation.PHENOMENON_TIME_START);
        } else if (indetTime.equals(ExtendedIndeterminateTime.LATEST)) {
            return Order.desc(Observation.PHENOMENON_TIME_END);
        }
        return null;
    }

    /**
     * Get projection for {@link ExtendedIndeterminateTime} value
     *
     * @param indetTime
     *                  Value to get projection for
     *
     * @return Projection to use to determine indeterminate time extrema
     */
    protected Projection getIndeterminateTimeExtremaProjection(IndeterminateValue indetTime) {
        if (indetTime.equals(ExtendedIndeterminateTime.FIRST)) {
            return Projections.min(Observation.PHENOMENON_TIME_START);
        } else if (indetTime.equals(ExtendedIndeterminateTime.LATEST)) {
            return Projections.max(Observation.PHENOMENON_TIME_END);
        }
        return null;
    }

    /**
     * Get the Observation property to filter on for an
     * {@link ExtendedIndeterminateTime}
     *
     * @param indetTime
     *                  Value to get property for
     *
     * @return String property to filter on
     */
    protected String getIndeterminateTimeFilterProperty(IndeterminateValue indetTime) {
        if (indetTime.equals(ExtendedIndeterminateTime.FIRST)) {
            return Observation.PHENOMENON_TIME_START;
        } else if (indetTime.equals(ExtendedIndeterminateTime.LATEST)) {
            return Observation.PHENOMENON_TIME_END;
        }
        return null;
    }

    /**
     * Add an indeterminate time restriction to a criteria. This allows for
     * multiple results if more than one observation has the extrema time (max
     * for latest, min for first). Note: use this method *after* adding all
     * other applicable restrictions so that they will apply to the min/max
     * observation time determination.
     *
     * @param c
     *                             Criteria to add the restriction to
     * @param sosIndeterminateTime
     *                             Indeterminate time restriction to add
     *
     * @return Modified criteria
     */
    protected Criteria addIndeterminateTimeRestriction(Criteria c, IndeterminateValue sosIndeterminateTime) {
        // get extrema indeterminate time
        c.setProjection(getIndeterminateTimeExtremaProjection(sosIndeterminateTime));
        Timestamp indeterminateExtremaTime = (Timestamp) c.uniqueResult();
        return addIndeterminateTimeRestriction(c, sosIndeterminateTime, indeterminateExtremaTime);
    }

    /**
     * Add an indeterminate time restriction to a criteria. This allows for
     * multiple results if more than one observation has the extrema time (max
     * for latest, min for first). Note: use this method *after* adding all
     * other applicable restrictions so that they will apply to the min/max
     * observation time determination.
     *
     * @param c
     *                                 Criteria to add the restriction to
     * @param sosIndeterminateTime
     *                                 Indeterminate time restriction to add
     * @param indeterminateExtremaTime
     *                                 Indeterminate time extrema
     *
     * @return Modified criteria
     */
    protected Criteria addIndeterminateTimeRestriction(Criteria c, IndeterminateValue sosIndeterminateTime,
                                                       Date indeterminateExtremaTime) {
        // reset criteria
        // see http://stackoverflow.com/a/1472958/193435
        c.setProjection(null);
        c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        // get observations with exactly the extrema time
        c.add(Restrictions.eq(getIndeterminateTimeFilterProperty(sosIndeterminateTime), indeterminateExtremaTime));

        // not really necessary to return the Criteria object, but useful if we
        // want to chain
        return c;
    }

    /**
     * Create Hibernate Criteria for Class
     *
     * @param clazz
     *                Class
     * @param session
     *                Hibernate session
     *
     * @return Hibernate Criteria for Class
     */
    @SuppressWarnings("rawtypes")
    protected Criteria createCriteriaForObservationClass(Class clazz, Session session) {
        return session.createCriteria(clazz).add(Restrictions.eq(Observation.DELETED, false))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    }

    /**
     * Add phenomenon and result time to observation object
     *
     * @param observation
     *                       Observation object
     * @param phenomenonTime
     *                       SOS phenomenon time
     * @param resultTime
     *                       SOS result Time
     *
     * @throws OwsExceptionReport
     *                        If an error occurs
     */
    protected void addPhenomeonTimeAndResultTimeToObservation(Observation<?> observation, Time phenomenonTime,
                                                              TimeInstant resultTime) throws OwsExceptionReport {
        addPhenomenonTimeToObservation(observation, phenomenonTime);
        addResultTimeToObservation(observation, resultTime, phenomenonTime);
    }

    /**
     * Add phenomenon and result time to observation object
     *
     * @param sosObservation the SOS observation
     * @param observation
     *                       Observation object
     *
     * @throws OwsExceptionReport
     *                        If an error occurs
     */
    protected void addTime(OmObservation sosObservation, Observation<?> observation) throws OwsExceptionReport {
        addPhenomeonTimeAndResultTimeToObservation(observation, sosObservation.getPhenomenonTime(), sosObservation
                                                   .getResultTime());
        addValidTimeToObservation(observation, sosObservation.getValidTime());
    }

    /**
     * Add phenomenon time to observation object
     *
     * @param observation
     *                       Observation object
     * @param phenomenonTime
     *                       SOS phenomenon time
     */
    protected void addPhenomenonTimeToObservation(Observation<?> observation, Time phenomenonTime) {
        if (phenomenonTime instanceof TimeInstant) {
            TimeInstant time = (TimeInstant) phenomenonTime;
            observation.setPhenomenonTimeStart(time.getValue().toDate());
            observation.setPhenomenonTimeEnd(time.getValue().toDate());
        } else if (phenomenonTime instanceof TimePeriod) {
            TimePeriod time = (TimePeriod) phenomenonTime;
            observation.setPhenomenonTimeStart(time.getStart().toDate());
            observation.setPhenomenonTimeEnd(time.getEnd().toDate());
        }
    }

    /**
     * Add result time to observation object
     *
     * @param observation
     *                       Observation object
     * @param resultTime
     *                       SOS result time
     * @param phenomenonTime
     *                       SOS phenomenon time
     *
     * @throws OwsExceptionReport
     *                            If an error occurs
     */
    protected void addResultTimeToObservation(Observation<?> observation, TimeInstant resultTime, Time phenomenonTime)
            throws OwsExceptionReport {
        if (resultTime != null) {
            if (resultTime.getValue() != null) {
                observation.setResultTime(resultTime.getValue().toDate());
            } else if (phenomenonTime instanceof TimeInstant) {
                observation.setResultTime(((TimeInstant) phenomenonTime).getValue().toDate());
            } else {
                throw new NoApplicableCodeException()
                        .withMessage("Error while adding result time to Hibernate Observation entitiy!");
            }
        } else if (phenomenonTime instanceof TimeInstant) {
            observation.setResultTime(((TimeInstant) phenomenonTime).getValue().toDate());
        } else {
            throw new NoApplicableCodeException()
                    .withMessage("Error while adding result time to Hibernate Observation entitiy!");
        }
    }

    /**
     * Add valid time to observation object
     *
     * @param observation
     *                    Observation object
     * @param validTime
     *                    SOS valid time
     */
    protected void addValidTimeToObservation(Observation<?> observation, TimePeriod validTime) {
        if (validTime != null) {
            observation.setValidTimeStart(validTime.getStart().toDate());
            observation.setValidTimeEnd(validTime.getEnd().toDate());
        }
    }

    /**
     * Update observations, set deleted flag
     *
     * @param scroll
     *                   Observations to update
     * @param deleteFlag
     *                   New deleted flag value
     * @param session
     *                   Hibernate session
     */
    protected void updateObservation(ScrollableIterable<? extends Observation<?>> scroll, boolean deleteFlag,
                                     Session session) {
        if (scroll != null) {
            try {
                for (Observation<?> o : scroll) {
                    o.setDeleted(deleteFlag);
                    session.update(o);
                    session.flush();
                }
            } finally {
                scroll.close();
            }
        }
    }

    /**
     * Check if a Spatial Filtering Profile filter is requested and add to
     * criteria
     *
     * @param c
     *                Criteria to add crtierion
     * @param request
     *                GetObservation request
     * @param session
     *                Hiberante Session
     *
     * @throws OwsExceptionReport
     *                            If Spatial Filteirng Profile is not supported or an error
     *                            occurs.
     */
    protected void checkAndAddSpatialFilteringProfileCriterion(Criteria c, GetObservationRequest request,
                                                               Session session) throws OwsExceptionReport {
        if (request.hasSpatialFilteringProfileSpatialFilter()) {
            c.add(SpatialRestrictions.filter(Observation.SAMPLING_GEOMETRY,
                    request.getSpatialFilter().getOperator(),
                    getGeometryHandler().switchCoordinateAxisFromToDatasourceIfNeeded(
                            request.getSpatialFilter().getGeometry())));
        }

    }

    /**
     * Get all observation identifiers
     *
     * @param session
     *                Hibernate session
     *
     * @return Observation identifiers
     */
    @SuppressWarnings("unchecked")
    public List<String> getObservationIdentifier(Session session) {
        Criteria criteria = session.createCriteria(getObservationFactory().contextualReferencedClass())
                .add(Restrictions.eq(ContextualReferencedObservation.DELETED, false))
                .add(Restrictions.isNotNull(ContextualReferencedObservation.IDENTIFIER))
                .setProjection(Projections.distinct(Projections.property(ContextualReferencedObservation.IDENTIFIER)));
        LOGGER.debug("QUERY getObservationIdentifiers(): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    public ReferencedEnvelope getSpatialFilteringProfileEnvelopeForOfferingId(String offeringID, Session session)
            throws OwsExceptionReport {
        try {
            // XXX workaround for Hibernate Spatial's lack of support for
            // GeoDB's extent aggregate see
            // http://www.hibernatespatial.org/pipermail/hibernatespatial-users/2013-August/000876.html
            Dialect dialect = ((SessionFactoryImplementor) session.getSessionFactory()).getDialect();
            if (getGeometryHandler().isSpatialDatasource() &&
                     HibernateHelper.supportsFunction(dialect, HibernateConstants.FUNC_EXTENT)) {
                Criteria criteria = getDefaultObservationInfoCriteria(session);
                criteria.setProjection(SpatialProjections.extent(TemporalReferencedObservation.SAMPLING_GEOMETRY));
                criteria.createCriteria(Observation.OFFERINGS).add(
                        Restrictions.eq(Offering.IDENTIFIER, offeringID));
                LOGGER.debug("QUERY getSpatialFilteringProfileEnvelopeForOfferingId(offeringID): {}", HibernateHelper
                             .getSqlString(criteria));
                Geometry geom = (Geometry) criteria.uniqueResult();
                geom = getGeometryHandler().switchCoordinateAxisFromToDatasourceIfNeeded(geom);
                if (geom != null) {
                    return new ReferencedEnvelope(geom.getEnvelopeInternal(), getGeometryHandler()
                                                  .getStorageEPSG());
                }
            } else {
                final Envelope envelope = new Envelope();
                Criteria criteria = getDefaultObservationInfoCriteria(session);
                criteria.createCriteria(AbstractObservation.OFFERINGS)
                        .add(Restrictions.eq(Offering.IDENTIFIER, offeringID));
                LOGGER.debug("QUERY getSpatialFilteringProfileEnvelopeForOfferingId(offeringID): {}",
                             HibernateHelper.getSqlString(criteria));
                @SuppressWarnings("unchecked")
                final List<TemporalReferencedObservation> observationTimes = criteria.list();
                if (CollectionHelper.isNotEmpty(observationTimes)) {
                    observationTimes.stream()
                            .filter(HasSamplingGeometry::hasSamplingGeometry)
                            .map(HasSamplingGeometry::getSamplingGeometry)
                            .filter(Objects::nonNull)
                            .filter(geom -> (geom != null && geom.getEnvelopeInternal() != null))
                            .forEachOrdered((geom) -> {
                                envelope.expandToInclude(geom.getEnvelopeInternal());
                            });
                    if (!envelope.isNull()) {
                        return new ReferencedEnvelope(envelope, getGeometryHandler().getStorageEPSG());
                    }
                }
            }
        } catch (final HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he)
                    .withMessage("Exception thrown while requesting feature envelope for observation ids");
        }
        return null;
    }

    public abstract String addProcedureAlias(Criteria criteria);

    public abstract List<Geometry> getSamplingGeometries(String feature, Session session);

    public abstract ObservationFactory getObservationFactory();

    protected abstract Criteria addAdditionalObservationIdentification(Criteria c, OmObservation sosObservation);

    /**
     * @param sosObservation {@link OmObservation} to check
     * @param session        Hibernate {@link Session}
     *
     * @throws OwsExceptionReport
     */
    public void checkForDuplicatedObservations(OmObservation sosObservation, Session session) throws OwsExceptionReport {
        Criteria c = getTemoralReferencedObservationCriteriaFor(sosObservation, session);
        addAdditionalObservationIdentification(c, sosObservation);
        // add times check (start/end phen, result)
        List<TemporalFilter> filters = Lists.newArrayListWithCapacity(2);
        filters.add(getPhenomeonTimeFilter(c, sosObservation.getPhenomenonTime()));
        filters.add(getResultTimeFilter(c, sosObservation.getResultTime(), sosObservation.getPhenomenonTime()));
        c.add(SosTemporalRestrictions.filter(filters));
        if (sosObservation.isSetHeightDepthParameter()) {
            NamedValue<Double> hdp = sosObservation.getHeightDepthParameter();
            addParameterRestriction(c, hdp);
        }
        c.setMaxResults(1);
        LOGGER.debug("QUERY checkForDuplicatedObservations(): {}", HibernateHelper.getSqlString(c));
        if (!c.list().isEmpty()) {
            StringBuilder builder = new StringBuilder();
            builder.append("procedure=").append(sosObservation.getObservationConstellation().getProcedureIdentifier());
            builder.append("observedProperty=").append(sosObservation.getObservationConstellation()
                    .getObservablePropertyIdentifier());
            builder.append("featureOfInter=").append(sosObservation.getObservationConstellation()
                    .getFeatureOfInterestIdentifier());
            builder.append("phenomenonTime=").append(sosObservation.getPhenomenonTime().toString());
            builder.append("resultTime=").append(sosObservation.getResultTime().toString());
            // TODO for e-Reporting SampligPoint should be added.
            if (sosObservation.isSetHeightDepthParameter()) {
                NamedValue<Double> hdp = sosObservation.getHeightDepthParameter();
                builder.append("height/depth=").append(hdp.getName().getHref()).append("/").append(hdp.getValue()
                        .getValue());
            }
            throw new NoApplicableCodeException()
                    .withMessage("The observation for %s already exists in the database!", builder.toString());
        }
    }

    private void addParameterRestriction(Criteria c, NamedValue<?> hdp) throws OwsExceptionReport {
        c.add(Subqueries.propertyIn(AbstractBaseObservation.ID,
                                    getParameterRestriction(c, hdp.getName().getHref(),
                                                            hdp.getValue().getValue(),
                                                            hdp.getValue().accept(getParameterFactory()).getClass())));
    }

    protected DetachedCriteria getParameterRestriction(Criteria c, String name, Object value, Class<?> clazz) {
        DetachedCriteria detachedCriteria = DetachedCriteria.forClass(clazz);
        addParameterNameRestriction(detachedCriteria, name);
        addParameterValueRestriction(detachedCriteria, value);
        detachedCriteria.setProjection(Projections.distinct(Projections.property(Parameter.ID)));
        return detachedCriteria;
    }

    protected DetachedCriteria addParameterNameRestriction(DetachedCriteria detachedCriteria, String name) {
        detachedCriteria.add(Restrictions.eq(Parameter.NAME, name));
        return detachedCriteria;
    }

    protected DetachedCriteria addParameterValueRestriction(DetachedCriteria detachedCriteria, Object value) {
        detachedCriteria.add(Restrictions.eq(Parameter.VALUE, value));
        return detachedCriteria;
    }

    private TemporalFilter getPhenomeonTimeFilter(Criteria c, Time phenomenonTime) {
        return new TemporalFilter(TimeOperator.TM_Equals, phenomenonTime, Sos2Constants.EN_PHENOMENON_TIME);
    }

    private TemporalFilter getResultTimeFilter(Criteria c, TimeInstant resultTime, Time phenomenonTime) throws
            OwsExceptionReport {
        String valueReferencep = Sos2Constants.EN_RESULT_TIME;
        if (resultTime != null) {
            if (resultTime.getValue() != null) {
                return new TemporalFilter(TimeOperator.TM_Equals, resultTime, valueReferencep);
            } else if (phenomenonTime instanceof TimeInstant) {
                return new TemporalFilter(TimeOperator.TM_Equals, phenomenonTime, valueReferencep);
            } else {
                throw new NoApplicableCodeException()
                        .withMessage("Error while creating result time filter for querying observations!");
            }
        } else {
            if (phenomenonTime instanceof TimeInstant) {
                return new TemporalFilter(TimeOperator.TM_Equals, phenomenonTime, valueReferencep);
            } else {
                throw new NoApplicableCodeException()
                        .withMessage("Error while creating result time filter for querying observations!");
            }
        }
    }

    public ParameterFactory getParameterFactory() {
        return ParameterFactory.getInstance();
    }


    /**
     * Check if the observation table contains samplingGeometries with values
     *
     * @param session
     *                Hibernate session
     *
     * @return <code>true</code>, if the observation table contains
     *         samplingGeometries with values
     */
    public boolean containsSamplingGeometries(Session session) {
        Criteria criteria = getDefaultObservationInfoCriteria(session);
        criteria.add(Restrictions.isNotNull(AbstractObservation.SAMPLING_GEOMETRY));
        criteria.setProjection(Projections.rowCount());
        LOGGER.debug("QUERY containsSamplingGeometries(): {}", HibernateHelper.getSqlString(criteria));
        return (Long) criteria.uniqueResult() > 0;
    }

    public TimeExtrema getObservationTimeExtrema(Session session) throws OwsExceptionReport {
        if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_OBSERVATION_TIME_EXTREMA, session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_OBSERVATION_TIME_EXTREMA);
            LOGGER.debug("QUERY getObservationTimeExtrema() with NamedQuery: {}", SQL_QUERY_OBSERVATION_TIME_EXTREMA);
            namedQuery.setResultTransformer(new ObservationTimeTransformer());
            return (TimeExtrema) namedQuery.uniqueResult();
        } else {
            Criteria c = getDefaultObservationTimeCriteria(session).setProjection(
                    Projections.projectionList().add(Projections.min(AbstractObservation.PHENOMENON_TIME_START))
                            .add(Projections.max(AbstractObservation.PHENOMENON_TIME_END))
                            .add(Projections.min(AbstractObservation.RESULT_TIME))
                            .add(Projections.max(AbstractObservation.RESULT_TIME)));
            c.setResultTransformer(new ObservationTimeTransformer());
            return (TimeExtrema) c.uniqueResult();
        }
    }

    protected boolean isIncludeChildObservableProperties() {
        return ObservationSettingProvider.getInstance().isIncludeChildObservableProperties();
    }

    private GeometryHandler getGeometryHandler() {
        return GeometryHandler.getInstance();
    }

    private static class ObservationPersister
            implements ValueVisitor<Observation<?>, OwsExceptionReport> {

        private final Set<ObservationConstellation> observationConstellations;
        private final FeatureOfInterest featureOfInterest;
        private final Caches caches;
        private final Session session;
        private final Geometry samplingGeometry;
        private final DAOs daos;
        private final ObservationFactory observationFactory;
        private final OmObservation sosObservation;
        private final boolean childObservation;
        ObservationPersister(GeometryHandler geometryHandler,
                             AbstractObservationDAO observationDao,
                             DaoFactory daoFactory,
                             OmObservation sosObservation,
                             Set<ObservationConstellation> observationConstellations,
                             FeatureOfInterest featureOfInterest,
                             Map<String, Codespace> codespaceCache,
                             Map<String, Unit> unitCache,
                             Session session) throws OwsExceptionReport {
            this(new DAOs(observationDao, daoFactory), new Caches(codespaceCache, unitCache), sosObservation,
                 observationConstellations, featureOfInterest, getSamplingGeometry(geometryHandler, sosObservation), session, false);
        }

        private ObservationPersister(DAOs daos,
                                     Caches caches,
                                     OmObservation observation,
                                     Set<ObservationConstellation> observationConstellations,
                                     FeatureOfInterest featureOfInterest,
                                     Geometry samplingGeometry,
                                     Session session,
                                     boolean childObservation)
                throws OwsExceptionReport {
            this.observationConstellations = observationConstellations;
            this.featureOfInterest = featureOfInterest;
            this.caches = caches;
            this.sosObservation = observation;
            this.samplingGeometry = samplingGeometry;
            this.session = session;
            this.daos = daos;
            this.observationFactory = daos.observation().getObservationFactory();
            this.childObservation = childObservation;
            checkForDuplicity();
        }

        private void checkForDuplicity() throws OwsExceptionReport {
            /*
            *  TODO check if observation exists in database for
            *  - series, phenTimeStart, phenTimeEnd, resultTime
            *  - series, phenTimeStart, phenTimeEnd, resultTime, depth/height parameter (same observation different depth/height)
            */
            daos.observation().checkForDuplicatedObservations(sosObservation, session);

        }

         @Override
        public Observation<?> visit(TLVTValue value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        @Override
        public Observation<?> visit(CvDiscretePointCoverage value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        @Override
        public Observation<?> visit(MultiPointCoverage value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        @Override
        public Observation<?> visit(RectifiedGridCoverage value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        @Override
        public Observation<?> visit(ProfileValue value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        @Override
        public Observation<?> visit(QuantityRangeValue value) throws OwsExceptionReport {
              throw notSupported(value);
        }

        @Override
        public Observation<?> visit(BooleanValue value) throws OwsExceptionReport {
            return setUnitAndPersist(observationFactory.truth(), value);
        }

        @Override
        public Observation<?> visit(CategoryValue value) throws OwsExceptionReport {
            return setUnitAndPersist(observationFactory.category(), value);
        }

        @Override
        public Observation<?> visit(CountValue value) throws OwsExceptionReport {
            return setUnitAndPersist(observationFactory.count(), value);
        }

        @Override
        public Observation<?> visit(GeometryValue value) throws OwsExceptionReport {
            return setUnitAndPersist(observationFactory.geometry(), value);
        }

        @Override
        public Observation<?> visit(QuantityValue value) throws OwsExceptionReport {
            return setUnitAndPersist(observationFactory.numeric(), value);
        }

        @Override
        public Observation<?> visit(TextValue value) throws OwsExceptionReport {
            return setUnitAndPersist(observationFactory.text(), value);
        }

        @Override
        public Observation<?> visit(UnknownValue value) throws OwsExceptionReport {
            return setUnitAndPersist(observationFactory.blob(), value);
        }

        @Override
        public Observation<?> visit(SweDataArrayValue value) throws OwsExceptionReport {
            return persist(observationFactory.sweDataArray(), value.getValue().getXml());
        }

        @Override
        public Observation<?> visit(ComplexValue value) throws OwsExceptionReport {
            ComplexObservation complex = observationFactory.complex();
            complex.setParent(true);
            return persist(complex, persistChildren(value.getValue()));
        }

        @Override
        public Observation<?> visit(HrefAttributeValue value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        @Override
        public Observation<?> visit(NilTemplateValue value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        @Override
        public Observation<?> visit(ReferenceValue value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        @Override
        public Observation<?> visit(TVPValue value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        @Override
        public Observation<?> visit(TimeRangeValue value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        @Override
        public Observation<?> visit(XmlValue<?> value) throws OwsExceptionReport {
            throw notSupported(value);
        }

        private Set<Observation<?>> persistChildren(SweAbstractDataRecord dataRecord)
                throws HibernateException, OwsExceptionReport {
            Set<Observation<?>> children = new HashSet<>(dataRecord.getFields().size());
            for (SweField field : dataRecord.getFields()) {
                ObservableProperty observableProperty = getObservablePropertyForField(field);
                ObservationPersister childPersister = createChildPersister(observableProperty);
                children.add(field.accept(ValueCreatingSweDataComponentVisitor.getInstance()).accept(childPersister));
            }
            session.flush();
            return children;
        }

        private ObservationPersister createChildPersister(ObservableProperty observableProperty)
                throws OwsExceptionReport {
            return new ObservationPersister(daos, caches, sosObservation, getObservationConstellations(observableProperty),
                    featureOfInterest, samplingGeometry, session, true);
        }

        private Set<ObservationConstellation> getObservationConstellations(ObservableProperty observableProperty) {
            Set<ObservationConstellation> newObservationConstellations = new HashSet<>(observationConstellations.size());
            for (ObservationConstellation constellation : observationConstellations) {
                newObservationConstellations.add(daos.observationConstellation().checkOrInsertObservationConstellation(
                        constellation.getProcedure(), observableProperty, constellation.getOffering(), true, session));
            }
            return newObservationConstellations;
        }

        private OwsExceptionReport notSupported(Value<?> value) throws OwsExceptionReport {
            throw new NoApplicableCodeException().withMessage("Unsupported observation value %s",
                                                              value.getClass().getCanonicalName());
        }

        private ObservableProperty getObservablePropertyForField(SweField field) {
            String definition = field.getElement().getDefinition();
            return daos.observableProperty().getObservablePropertyForIdentifier(definition, session);
        }

        private <V, T extends Observation<V>>  T setUnitAndPersist(T observation, Value<V> value)
                throws OwsExceptionReport {
            observation.setUnit(getUnit(value));
            return persist(observation, value.getValue());
        }

        private Unit getUnit(Value<?> value) {
            return value.isSetUnit() ? daos.observation().getUnit(value.getUnit(), caches.units(), session) : null;
        }

        private <V, T extends Observation<V>>  T persist(T observation, V value) throws OwsExceptionReport {
            if (!observation.isSetUnit()) {
                observation.setUnit(getUnit(sosObservation.getValue().getValue()));
            }
            observation.setDeleted(false);

            if (!childObservation) {
                daos.observation().addIdentifier(sosObservation, observation, session);
            } else {
                observation.setChild(true);
            }

            daos.observation().addName(sosObservation, observation, session);
            daos.observation().addDescription(sosObservation, observation);
            daos.observation().addTime(sosObservation, observation);

            observation.setSamplingGeometry(samplingGeometry);

            ObservationContext observationContext = daos.observation().createObservationContext();
            Set<Offering> offerings = observation.getOfferings();

            String observationType = observation.accept(ObservationTypeObservationVisitor.getInstance());

            for (ObservationConstellation oc : observationConstellations) {
                offerings.add(oc.getOffering());
                if (!daos.observationConstellation().checkObservationType(oc, observationType, session)) {
                    throw new InvalidParameterValueException().withMessage(
                            "The requested observationType (%s) is invalid for procedure = %s, observedProperty = %s and offering = %s! The valid observationType is '%s'!",
                            observationType, observation.getProcedure().getIdentifier(),
                            oc.getObservableProperty().getIdentifier(), oc.getOffering().getIdentifier(),
                            oc.getObservationType().getObservationType());
                }
            }

            ObservationConstellation first = Iterables.getFirst(observationConstellations, null);
            if (first != null) {
                observationContext.setObservableProperty(first.getObservableProperty());
                observationContext.setProcedure(first.getProcedure());
            }
            // set value before ObservationContext is added otherwise the first/last value is not updated in series table.
            observation.setValue(value);

            observationContext.setFeatureOfInterest(featureOfInterest);
            daos.observation().fillObservationContext(observationContext, sosObservation, session);
            daos.observation().addObservationContextToObservation(observationContext, observation, session);

            session.saveOrUpdate(observation);

            if (sosObservation.isSetParameter()) {
                daos.parameter().insertParameter(sosObservation.getParameter(), observation.getObservationId(), caches.units(), session);
            }
            return observation;
        }

        private  static Geometry getSamplingGeometry(GeometryHandler geometryHandler, OmObservation sosObservation) throws OwsExceptionReport {
            if (!sosObservation.isSetSpatialFilteringProfileParameter()) {
                return null;
            }
            NamedValue<Geometry> spatialFilteringProfileParameter = sosObservation.getSpatialFilteringProfileParameter();
            Geometry geometry = spatialFilteringProfileParameter.getValue().getValue();
            return geometryHandler.switchCoordinateAxisFromToDatasourceIfNeeded(geometry);
        }

        private static class Caches {
            private final Map<String, Codespace> codespaces;

            private final Map<String, Unit> units;

            Caches(Map<String, Codespace> codespaces, Map<String, Unit> units) {
                this.codespaces = codespaces;
                this.units = units;
            }

            public Map<String, Codespace> codespaces() {
                return codespaces;
            }

            public Map<String, Unit> units() {
                return units;
            }
        }

        private static class DAOs {
            private final ObservablePropertyDAO observableProperty;
            private final ObservationConstellationDAO observationConstellation;
            private final AbstractObservationDAO observation;
            private final ObservationTypeDAO observationType;
            private final ParameterDAO parameter;

            DAOs(AbstractObservationDAO observationDao, DaoFactory daoFactory) {
                this.observation = observationDao;
                this.observableProperty = daoFactory.getObservablePropertyDAO();
                this.observationConstellation = daoFactory.getObservationConstellationDAO();
                this.observationType = daoFactory.getObservationTypeDAO();
                this.parameter = daoFactory.getParameterDAO();
            }

            public ObservablePropertyDAO observableProperty() {
                return this.observableProperty;
            }

            public ObservationConstellationDAO observationConstellation() {
                return this.observationConstellation;
            }

            public AbstractObservationDAO observation() {
                return this.observation;
            }

            public ObservationTypeDAO observationType() {
                return this.observationType;
            }

            public ParameterDAO parameter() {
                return this.parameter;
            }
        }
    }

    /**
     * Observation time extrema {@link ResultTransformer}
     *
     * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
     * @since 4.4.0
     *
     */
    protected static class ObservationTimeTransformer implements ResultTransformer {

        private static final long serialVersionUID = -3401483077212678275L;

        @Override
        public TimeExtrema transformTuple(Object[] tuple, String[] aliases) {
            TimeExtrema timeExtrema = new TimeExtrema();
            if (tuple != null) {
                timeExtrema.setMinPhenomenonTime(DateTimeHelper.makeDateTime(tuple[0]));
                timeExtrema.setMaxPhenomenonTime(DateTimeHelper.makeDateTime(tuple[1]));
                timeExtrema.setMinResultTime(DateTimeHelper.makeDateTime(tuple[2]));
                timeExtrema.setMaxResultTime(DateTimeHelper.makeDateTime(tuple[3]));
            }
            return timeExtrema;
        }

        @Override
        @SuppressWarnings("rawtypes")
        public List transformList(List collection) {
            return collection;
        }
    }

}
