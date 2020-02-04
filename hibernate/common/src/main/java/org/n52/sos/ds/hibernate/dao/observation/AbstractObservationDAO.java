/**
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
package org.n52.sos.ds.hibernate.dao.observation;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
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
import org.n52.sos.ds.hibernate.dao.AbstractIdentifierNameDescriptionDAO;
import org.n52.sos.ds.hibernate.dao.CodespaceDAO;
import org.n52.sos.ds.hibernate.dao.UnitDAO;
import org.n52.sos.ds.hibernate.entities.Codespace;
import org.n52.sos.ds.hibernate.entities.EntitiyHelper;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Unit;
import org.n52.sos.ds.hibernate.entities.feature.AbstractFeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.feature.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.observation.AbstractBaseObservation;
import org.n52.sos.ds.hibernate.entities.observation.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.observation.BaseObservation;
import org.n52.sos.ds.hibernate.entities.observation.ContextualReferencedObservation;
import org.n52.sos.ds.hibernate.entities.observation.Observation;
import org.n52.sos.ds.hibernate.entities.observation.TemporalReferencedObservation;
import org.n52.sos.ds.hibernate.entities.observation.legacy.LegacyObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.Series;
import org.n52.sos.ds.hibernate.entities.observation.series.SeriesObservation;
import org.n52.sos.ds.hibernate.entities.parameter.observation.Parameter;
import org.n52.sos.ds.hibernate.entities.parameter.observation.ParameterFactory;
import org.n52.sos.ds.hibernate.util.HibernateConstants;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.ResultFilterClasses;
import org.n52.sos.ds.hibernate.util.ResultFilterRestrictions;
import org.n52.sos.ds.hibernate.util.ResultFilterRestrictions.SubQueryIdentifier;
import org.n52.sos.ds.hibernate.util.ScrollableIterable;
import org.n52.sos.ds.hibernate.util.SpatialRestrictions;
import org.n52.sos.ds.hibernate.util.TemporalRestrictions;
import org.n52.sos.ds.hibernate.util.TimeExtrema;
import org.n52.sos.ds.hibernate.util.observation.HibernateObservationUtilities;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.MissingParameterValueException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.OptionNotSupportedException;
import org.n52.sos.ogc.UoM;
import org.n52.sos.ogc.filter.ComparisonFilter;
import org.n52.sos.ogc.filter.Filter;
import org.n52.sos.ogc.filter.FilterConstants.TimeOperator;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.Time.TimeIndeterminateValue;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants.SosIndeterminateTime;
import org.n52.sos.ogc.sos.SosEnvelope;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.JavaHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;


public abstract class AbstractObservationDAO extends AbstractIdentifierNameDescriptionDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractObservationDAO.class);
    
    private static final String SQL_QUERY_CHECK_SAMPLING_GEOMETRIES = "checkSamplingGeometries";

    private static final String SQL_QUERY_OBSERVATION_TIME_EXTREMA = "getObservationTimeExtrema";
    
    /**
     * Add observation identifier (procedure, observableProperty,
     * featureOfInterest) to observation
     *
     * @param observationIdentifiers
     *            Observation identifiers
     * @param observation
     *            Observation to add identifiers
     * @param session
     *            Hibernate session
     * @throws CodedException
     */
    protected abstract void addObservationContextToObservation(ObservationContext observationIdentifiers,
            Observation<?> observation, Session session) throws CodedException;

    /**
     * Get Hibernate Criteria for querying observations with parameters
     * featureOfInterst and procedure
     *
     * @param feature
     *            FeatureOfInterest to query for
     * @param procedure
     *            Procedure to query for
     * @param session
     *            Hiberante Session
     * @return Criteria to query observations
     */
    public abstract Criteria getObservationInfoCriteriaForFeatureOfInterestAndProcedure(String feature,
            String procedure, Session session);

    /**
     * Get Hibernate Criteria for querying observations with parameters
     * featureOfInterst and offering
     *
     * @param feature
     *            FeatureOfInterest to query for
     * @param offering
     *            Offering to query for
     * @param session
     *            Hiberante Session
     * @return Criteria to query observations
     */
    public abstract Criteria getObservationInfoCriteriaForFeatureOfInterestAndOffering(String feature, String offering,
            Session session);

    /**
     * Get Hibernate Criteria for observation with restriction procedure
     *
     * @param procedure
     *            Procedure parameter
     * @param session
     *            Hibernate session
     * @return Hibernate Criteria to query observations
     * @throws CodedException
     */
    public abstract Criteria getObservationCriteriaForProcedure(String procedure, Session session)
            throws CodedException;

    /**
     * Get Hibernate Criteria for observation with restriction
     * observableProperty
     *
     * @param observableProperty
     * @param session
     *            Hibernate session
     * @return Hibernate Criteria to query observations
     * @throws CodedException
     */
    public abstract Criteria getObservationCriteriaForObservableProperty(String observableProperty, Session session)
            throws CodedException;

    /**
     * Get Hibernate Criteria for observation with restriction featureOfInterest
     *
     * @param featureOfInterest
     * @param session
     *            Hibernate session
     * @return Hibernate Criteria to query observations
     * @throws CodedException
     */
    public abstract Criteria getObservationCriteriaForFeatureOfInterest(String featureOfInterest, Session session)
            throws CodedException;

    /**
     * Get Hibernate Criteria for observation with restrictions procedure and
     * observableProperty
     *
     * @param procedure
     * @param observableProperty
     * @param session
     *            Hibernate session
     * @return Hibernate Criteria to query observations
     * @throws CodedException
     */
    public abstract Criteria getObservationCriteriaFor(String procedure, String observableProperty, Session session)
            throws CodedException;

    /**
     * Get Hibernate Criteria for observation with restrictions procedure,
     * observableProperty and featureOfInterest
     *
     * @param procedure
     * @param observableProperty
     * @param featureOfInterest
     * @param session
     *            Hibernate session
     * @return Hibernate Criteria to query observations
     * @throws CodedException
     */
    public abstract Criteria getObservationCriteriaFor(String procedure, String observableProperty,
            String featureOfInterest, Session session) throws CodedException;

    /**
     * Get all observation identifiers for a procedure.
     *
     * @param procedureIdentifier
     * @param session
     * @return Collection of observation identifiers
     */
    public abstract Collection<String> getObservationIdentifiers(String procedureIdentifier, Session session);

    public abstract ScrollableResults getObservations(Set<String> procedure, Set<String> observableProperty,
            Set<String> featureOfInterest, Set<String> offering, Criterion filterCriterion, Session session);

    /**
     * Get Hibernate Criteria for {@link TemporalReferencedObservation}  with restrictions observation identifiers
     * @param bservation 
     *
     * @param observationConstellation The observation with restriction values
     * @param session
     *            Hibernate session
     * @return Hibernate Criteria to query observations
     * @throws CodedException
     */
    public abstract Criteria getTemoralReferencedObservationCriteriaFor(OmObservation bservation, ObservationConstellation observationConstellation, Session session) throws CodedException;

    
    public ResultFilterClasses getResultFilterClasses() {
        return new ResultFilterClasses(getObservationFactory().numericClass(), getObservationFactory().countClass(),
                getObservationFactory().textClass(), getObservationFactory().categoryClass(),
                getObservationFactory().complexClass(), getObservationFactory().profileClass());
    }
    
    /**
     * Query observation by identifier
     *
     * @param identifier
     *            Observation identifier (gml:identifier)
     * @param session
     *            Hiberante session
     * @return Observation
     */
    public Observation<?> getObservationByIdentifier(String identifier, Session session) {
        Criteria criteria = getDefaultObservationCriteria(session);
        addObservationIdentifierToCriteria(criteria, identifier, session);
        return (Observation<?>) criteria.uniqueResult();
    }
    
    /**
     * Query observation by identifiers
     *
     * @param identifiers
     *            Observation identifiers (gml:identifier)
     * @param session
     *            Hiberante session
     * @return Observation
     */
    @SuppressWarnings("unchecked")
    public List<Observation<?>> getObservationByIdentifiers(Set<String> identifiers, Session session) {
        Criteria criteria = getDefaultObservationCriteria(session);
        addObservationIdentifierToCriteria(criteria, identifiers, session);
        return criteria.list();
    }

    /**
     * Check if there are numeric observations for the offering
     *
     * @param offeringIdentifier
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @return If there are observations or not
     */
    public boolean checkNumericObservationsFor(String offeringIdentifier, Session session) {
        return checkObservationFor(getObservationFactory().numericClass(), offeringIdentifier, session);
    }

    /**
     * Check if there are boolean observations for the offering
     *
     * @param offeringIdentifier
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @return If there are observations or not
     */
    public boolean checkBooleanObservationsFor(String offeringIdentifier, Session session) {
        return checkObservationFor(getObservationFactory().truthClass(), offeringIdentifier, session);
    }

    /**
     * Check if there are count observations for the offering
     *
     * @param offeringIdentifier
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @return If there are observations or not
     */
    public boolean checkCountObservationsFor(String offeringIdentifier, Session session) {
        return checkObservationFor(getObservationFactory().countClass(), offeringIdentifier, session);
    }

    /**
     * Check if there are category observations for the offering
     *
     * @param offeringIdentifier
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @return If there are observations or not
     */
    public boolean checkCategoryObservationsFor(String offeringIdentifier, Session session) {
        return checkObservationFor(getObservationFactory().categoryClass(), offeringIdentifier, session);
    }

    /**
     * Check if there are text observations for the offering
     *
     * @param offeringIdentifier
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @return If there are observations or not
     */
    public boolean checkTextObservationsFor(String offeringIdentifier, Session session) {
        return checkObservationFor(getObservationFactory().textClass(), offeringIdentifier, session);
    }
    
    /**
     * Check if there are complex observations for the offering
     *
     * @param offeringIdentifier
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @return If there are observations or not
     */
    public boolean checkComplexObservationsFor(String offeringIdentifier, Session session) {
        return checkObservationFor(getObservationFactory().complexClass(), offeringIdentifier, session);
    }

    /**
     * Check if there are profile observations for the offering
     *
     * @param offeringIdentifier
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @return If there are observations or not
     */
    public boolean checkProfileObservationsFor(String offeringIdentifier, Session session) {
        return checkObservationFor(getObservationFactory().profileClass(), offeringIdentifier, session);
    }
    
    /**
     * Check if there are blob observations for the offering
     *
     * @param offeringIdentifier
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @return If there are observations or not
     */
    public boolean checkBlobObservationsFor(String offeringIdentifier, Session session) {
        return checkObservationFor(getObservationFactory().blobClass(), offeringIdentifier, session);
    }

    /**
     * Check if there are geometry observations for the offering
     *
     * @param offeringIdentifier
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @return If there are observations or not
     */
    public boolean checkGeometryObservationsFor(String offeringIdentifier, Session session) {
        return checkObservationFor(getObservationFactory().geometryClass(), offeringIdentifier, session);
    }

    /**
     * Check if there are geometry observations for the offering
     *
     * @param offeringIdentifier
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @return If there are observations or not
     */
    public boolean checkSweDataArrayObservationsFor(String offeringIdentifier, Session session) {
        return checkObservationFor(getObservationFactory().sweDataArrayClass(), offeringIdentifier, session);
    }
    
    /**
     * Check if there are referenced observations for the offering
     *
     * @param offeringIdentifier
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @return If there are observations or not
     */
    public boolean checkReferenceObservationsFor(String offeringIdentifier, Session session) {
        return checkObservationFor(getObservationFactory().referenceClass(), offeringIdentifier, session);
    }
    
    /**
     * Get Hibernate Criteria for result model
     *
     * @param resultModel
     *            Result model
     * @param session
     *            Hibernate session
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
     *            Hiberante session
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
     *            Hiberante session
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
     *            Hibernate session
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
        criteria.setFetchMode("offerings", org.hibernate.FetchMode.JOIN);
        criteria.setFetchMode("parameters", org.hibernate.FetchMode.JOIN);
//        criteria.setFetchMode("value", org.hibernate.FetchMode.JOIN);
        return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    }

    protected boolean isIncludeChildObservableProperties() {
        return ServiceConfiguration.getInstance().isIncludeChildObservableProperties();
    }

    /**
     * Get Hibernate Criteria for observation with restriction procedure Insert
     * a multi value observation for observation constellations and
     * featureOfInterest
     *
     * @param observationConstellation
     *            Observation constellation object
     * @param feature
     *            FeatureOfInterest object
     * @param containerObservation
     *            SOS observation
     * @param codespaceCache
     *            Map based codespace object cache to prevent redundant queries
     * @param unitCache
     *            Map based unit object cache to prevent redundant queries
     * @param checkForDuplicatedObservations 
     * @param session
     *            Hibernate session
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    public void insertObservationMultiValue(ObservationConstellation observationConstellation,
            AbstractFeatureOfInterest feature, OmObservation containerObservation, Map<String, Codespace> codespaceCache,
            Map<UoM, Unit> unitCache, Set<Offering> hOfferings, boolean checkForDuplicatedObservations, Session session) throws OwsExceptionReport {
        List<OmObservation> unfoldObservations = HibernateObservationUtilities.unfoldObservation(containerObservation);
        for (OmObservation sosObservation : unfoldObservations) {
            insertObservationSingleValue(observationConstellation, feature, sosObservation, codespaceCache, unitCache,
                    hOfferings, checkForDuplicatedObservations, session);
        }
    }

    /**
     * Insert a single observation for observation constellations and
     * featureOfInterest without local caching for codespaces and units
     *
     * @param observationConstellation
     *            Observation constellation object
     * @param hFeature
     *            FeatureOfInterest object
     * @param sosObservation
     *            SOS observation to insert
     * @param session
     *            Hibernate session
     * @throws OwsExceptionReport
     */
    public void insertObservationSingleValue(ObservationConstellation hObservationConstellation,
            FeatureOfInterest hFeature, OmObservation sosObservation, Set<Offering> hOfferings, boolean checkForDuplicatedObservations, Session session) throws OwsExceptionReport {
        insertObservationSingleValue(hObservationConstellation, hFeature, sosObservation, null, null, hOfferings, checkForDuplicatedObservations, session);
    }

    /**
     * Insert a single observation for observation constellations and
     * featureOfInterest with local caching for codespaces and units
     *
     * @param hObservationConstellations
     *            Observation constellation objects
     * @param hFeature
     *            FeatureOfInterest object
     * @param sosObservation
     *            SOS observation to insert
     * @param codespaceCache
     *            Map cache for codespace objects (to prevent redundant
     *            querying)
     * @param unitCache
     *            Map cache for unit objects (to prevent redundant querying)
     * @param b 
     * @param session
     *            Hibernate session
     * @throws OwsExceptionReport
     */
    @SuppressWarnings("rawtypes")
    public void insertObservationSingleValue(ObservationConstellation hObservationConstellation,
            AbstractFeatureOfInterest hFeature, OmObservation sosObservation,
            Map<String, Codespace> codespaceCache,
            Map<UoM, Unit> unitCache, Set<Offering> hOfferings, boolean checkForDuplicatedObservations, Session session)
            throws OwsExceptionReport {
        SingleObservationValue<?> value
                = (SingleObservationValue) sosObservation.getValue();
        ObservationPersister persister = new ObservationPersister(
                this,
                sosObservation,
                hObservationConstellation,
                hFeature,
                codespaceCache,
                unitCache,
                hOfferings,
                checkForDuplicatedObservations,
                session
        );
        value.getValue().accept(persister);
    }

    protected ObservationContext createObservationContext() {
        return new ObservationContext();
    }
    
    protected ObservationContext fillObservationContext(ObservationContext ctx, OmObservation sosObservation, Session session) {
        return ctx;
    }

    /**
     * If the local codespace cache isn't null, use it when retrieving
     * codespaces.
     *
     * @param codespace
     *            Codespace
     * @param localCache
     *            Cache (possibly null)
     * @param session
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
     *            Unit
     * @param localCache
     *            Cache (possibly null)
     * @param session
     * @return Unit
     */
    protected Unit getUnit(String unit, Map<UoM, Unit> localCache, Session session) {
       return getUnit(new UoM(unit), localCache, session);
    }
    
    /**
     * If the local unit cache isn't null, use it when retrieving unit.
     *
     * @param unit
     *            Unit
     * @param localCache
     *            Cache (possibly null)
     * @param session
     * @return Unit
     */
    protected Unit getUnit(UoM unit, Map<UoM, Unit> localCache, Session session) {
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
     *            Hibernate Criteria
     * @param identifier
     *            Observation identifier (gml:identifier)
     * @param session
     *            Hibernate session
     */
    protected void addObservationIdentifierToCriteria(Criteria criteria, String identifier, Session session) {
        criteria.add(Restrictions.eq(Observation.IDENTIFIER, identifier));
    }
    
    /**
     * Add observation identifiers (gml:identifier) to Hibernate Criteria
     *
     * @param criteria
     *            Hibernate Criteria
     * @param identifiers
     *            Observation identifiers (gml:identifier)
     * @param session
     *            Hibernate session
     */
    protected void addObservationIdentifierToCriteria(Criteria criteria, Set<String> identifiers, Session session) {
        criteria.add(Restrictions.in(AbstractObservation.IDENTIFIER, identifiers));
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
     *            om:Parameter to insert
     * @param observation
     *            related observation
     * @param session
     *            Hibernate session
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
     *            Observation sub class
     * @param offeringIdentifier
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @return If there are observations or not
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected boolean checkObservationFor(Class clazz, String offeringIdentifier, Session session) {
        Criteria c = session.createCriteria(clazz).add(Restrictions.eq(Observation.DELETED, false));
        if (EntitiyHelper.getInstance().isSeriesObservationSupported()) {
            c.createCriteria(SeriesObservation.SERIES).createCriteria(Series.OFFERING)
                    .add(Restrictions.eq(Offering.IDENTIFIER, offeringIdentifier));
        } else {
            c.createCriteria(LegacyObservation.OFFERINGS)
                    .add(Restrictions.eq(Offering.IDENTIFIER, offeringIdentifier));
        }
        c.setMaxResults(1);
        LOGGER.debug("QUERY checkObservationFor(clazz, offeringIdentifier): {}", HibernateHelper.getSqlString(c));
        return CollectionHelper.isNotEmpty(c.list());
    }

    /**
     * Get min phenomenon time from observations
     *
     * @param session
     *            Hibernate session Hibernate session
     * @return min time
     */
    public DateTime getMinPhenomenonTime(Session session) {
        Criteria criteria =
                session.createCriteria(getObservationFactory().temporalReferencedClass())
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
     *            Hibernate session Hibernate session
     *
     * @return max time
     */
    public DateTime getMaxPhenomenonTime(Session session) {

        Criteria criteriaStart =
                session.createCriteria(getObservationFactory().temporalReferencedClass())
                        .setProjection(Projections.max(Observation.PHENOMENON_TIME_START))
                        .add(Restrictions.eq(Observation.DELETED, false));
        LOGGER.debug("QUERY getMaxPhenomenonTime() start: {}", HibernateHelper.getSqlString(criteriaStart));
        Object maxStart = criteriaStart.uniqueResult();

        Criteria criteriaEnd =
                session.createCriteria(getObservationFactory().temporalReferencedClass())
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
     *            Hibernate session Hibernate session
     *
     * @return min time
     */
    public DateTime getMinResultTime(Session session) {

        Criteria criteria =
                session.createCriteria(getObservationFactory().temporalReferencedClass())
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
     *            Hibernate session Hibernate session
     *
     * @return max time
     */
    public DateTime getMaxResultTime(Session session) {

        Criteria criteria =
                session.createCriteria(getObservationFactory().temporalReferencedClass())
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
     *            Hibernate session the session
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
                TimePeriod bBox =
                        createTimePeriod((Timestamp) record[0], (Timestamp) record[1], (Timestamp) record[2]);
                return bBox;
            }
        }
        return null;
    }

    /**
     * Get order for {@link SosIndeterminateTime} value
     *
     * @param indetTime
     *            Value to get order for
     * @return Order
     */
    protected Order getOrder(final SosIndeterminateTime indetTime) {
        if (indetTime.equals(SosIndeterminateTime.first)) {
            return Order.asc(Observation.PHENOMENON_TIME_START);
        } else if (indetTime.equals(SosIndeterminateTime.latest)) {
            return Order.desc(Observation.PHENOMENON_TIME_END);
        }
        return null;
    }

    /**
     * Get projection for {@link SosIndeterminateTime} value
     *
     * @param indetTime
     *            Value to get projection for
     * @return Projection to use to determine indeterminate time extrema
     */
    protected Projection getIndeterminateTimeExtremaProjection(final SosIndeterminateTime indetTime) {
        if (indetTime.equals(SosIndeterminateTime.first)) {
            return Projections.min(Observation.PHENOMENON_TIME_START);
        } else if (indetTime.equals(SosIndeterminateTime.latest)) {
            return Projections.max(Observation.PHENOMENON_TIME_END);
        }
        return null;
    }

    /**
     * Get the Observation property to filter on for an
     * {@link SosIndeterminateTime}
     *
     * @param indetTime
     *            Value to get property for
     * @return String property to filter on
     */
    protected String getIndeterminateTimeFilterProperty(final SosIndeterminateTime indetTime) {
        if (indetTime.equals(SosIndeterminateTime.first)) {
            return Observation.PHENOMENON_TIME_START;
        } else if (indetTime.equals(SosIndeterminateTime.latest)) {
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
     *            Criteria to add the restriction to
     * @param sosIndeterminateTime
     *            Indeterminate time restriction to add
     * @return Modified criteria
     */
    protected Criteria addIndeterminateTimeRestriction(Criteria c, SosIndeterminateTime sosIndeterminateTime) {
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
     *            Criteria to add the restriction to
     * @param sosIndeterminateTime
     *            Indeterminate time restriction to add
     * @param indeterminateExtremaTime
     *            Indeterminate time extrema
     * @return Modified criteria
     */
    protected Criteria addIndeterminateTimeRestriction(Criteria c, SosIndeterminateTime sosIndeterminateTime,
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
     *            Class
     * @param session
     *            Hibernate session
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
     *            Observation object
     * @param phenomenonTime
     *            SOS phenomenon time
     * @param resultTime
     *            SOS result Time
     * @throws OwsExceptionReport 
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
     *            Observation object
     * @throws OwsExceptionReport 
     */
    protected void addTime(OmObservation sosObservation, Observation<?> observation) throws OwsExceptionReport {
        addPhenomeonTimeAndResultTimeToObservation(observation, sosObservation.getPhenomenonTime(), sosObservation.getResultTime());
        addValidTimeToObservation(observation, sosObservation.getValidTime());
    }

    /**
     * Add phenomenon time to observation object
     *
     * @param observation
     *            Observation object
     * @param phenomenonTime
     *            SOS phenomenon time
     * @throws OwsExceptionReport
     */
    public void addPhenomenonTimeToObservation(Observation<?> observation, Time phenomenonTime)
            throws OwsExceptionReport {
        if (phenomenonTime instanceof TimeInstant) {
            TimeInstant time = (TimeInstant) phenomenonTime;
            if (time.isSetValue()) {
                observation.setPhenomenonTimeStart(time.getValue().toDate());
                observation.setPhenomenonTimeEnd(time.getValue().toDate());
            } else if (time.isSetIndeterminateValue()) {
                Date now = getDateForTimeIndeterminateValue(time.getIndeterminateValue(),
                        "gml:TimeInstant/gml:timePosition[@indeterminatePosition]");
                observation.setPhenomenonTimeStart(now);
                observation.setPhenomenonTimeEnd(now);
            } else {
                throw new MissingParameterValueException("gml:TimeInstant/gml:timePosition");
            }
        } else if (phenomenonTime instanceof TimePeriod) {
            TimePeriod time = (TimePeriod) phenomenonTime;
            if (time.isSetStart()) {
                observation.setPhenomenonTimeStart(time.getStart().toDate());
            } else if (time.isSetStartIndeterminateValue()) {
                observation.setPhenomenonTimeStart(getDateForTimeIndeterminateValue(time.getStartIndet(),
                        "gml:TimePeriod/gml:beginPosition[@indeterminatePosition]"));
            } else {
                throw new MissingParameterValueException("gml:TimePeriod/gml:beginPosition");
            }
            if (time.isSetEnd()) {
                observation.setPhenomenonTimeEnd(time.getEnd().toDate());
            } else if (time.isSetEndIndeterminateValue()) {
                observation.setPhenomenonTimeEnd(getDateForTimeIndeterminateValue(time.getEndIndet(),
                        "gml:TimePeriod/gml:endPosition[@indeterminatePosition]"));
            } else {
                throw new MissingParameterValueException("gml:TimePeriod/gml:endPosition");
            }

            observation.setPhenomenonTimeEnd(time.getEnd().toDate());
        }
    }

    /**
     * Add result time to observation object
     *
     * @param observation
     *            Observation object
     * @param resultTime
     *            SOS result time
     * @param phenomenonTime
     *            SOS phenomenon time
     * @throws CodedException
     *             If an error occurs
     */
    public void addResultTimeToObservation(Observation<?> observation, TimeInstant resultTime,
            Time phenomenonTime) throws CodedException {
        if (resultTime != null) {
            if (resultTime.isSetValue()) {
                observation.setResultTime(resultTime.getValue().toDate());
            } else if (resultTime.isSetGmlId() && resultTime.getGmlId().contains(Sos2Constants.EN_PHENOMENON_TIME)
                    && phenomenonTime instanceof TimeInstant) {
                if (((TimeInstant) phenomenonTime).isSetValue()) {
                    observation.setResultTime(((TimeInstant) phenomenonTime).getValue().toDate());
                } else if (((TimeInstant) phenomenonTime).isSetIndeterminateValue()) {
                    observation.setResultTime(getDateForTimeIndeterminateValue(((TimeInstant) phenomenonTime).getIndeterminateValue(),
                            "gml:TimeInstant/gml:timePosition[@indeterminatePosition]"));
                } else {
                    throw new NoApplicableCodeException()
                    .withMessage("Error while adding result time to Hibernate Observation entitiy!");
                }
            } else if (resultTime.isSetIndeterminateValue()) {
                observation.setResultTime(getDateForTimeIndeterminateValue(resultTime.getIndeterminateValue(),
                        "gml:TimeInstant/gml:timePosition[@indeterminatePosition]"));
            } else {
                throw new NoApplicableCodeException()
                        .withMessage("Error while adding result time to Hibernate Observation entitiy!");
            }
        } else {
            if (phenomenonTime instanceof TimeInstant) {
                observation.setResultTime(((TimeInstant) phenomenonTime).getValue().toDate());
            } else {
                throw new NoApplicableCodeException()
                        .withMessage("Error while adding result time to Hibernate Observation entitiy!");
            }
        }
    }

    protected Date getDateForTimeIndeterminateValue(TimeIndeterminateValue timeIndeterminateValue, String parameter)
            throws InvalidParameterValueException {
        switch (timeIndeterminateValue) {
        case now:
            return new DateTime().toDate();
        default:
            throw new InvalidParameterValueException(parameter, timeIndeterminateValue.name());
        }
    }

    /**
     * Add valid time to observation object
     *
     * @param observation
     *            Observation object
     * @param validTime
     *            SOS valid time
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
     *            Observations to update
     * @param deleteFlag
     *            New deleted flag value
     * @param session
     *            Hibernate session
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
     *            Criteria to add crtierion
     * @param request
     *            GetObservation request
     * @param session
     *            Hiberante Session
     * @throws OwsExceptionReport
     *             If Spatial Filteirng Profile is not supported or an error
     *             occurs.
     */
    protected void checkAndAddSpatialFilteringProfileCriterion(Criteria c, GetObservationRequest request,
            Session session) throws OwsExceptionReport {
        if (request.hasSpatialFilteringProfileSpatialFilter()) {
            c.add(SpatialRestrictions.filter(Observation.SAMPLING_GEOMETRY, request.getSpatialFilter().getOperator(),
                    GeometryHandler.getInstance()
                            .switchCoordinateAxisFromToDatasourceIfNeeded(request.getSpatialFilter().getGeometry())));
//        } else {
//            // TODO add filter with lat/lon
//            LOGGER.warn("Spatial filtering for lat/lon is not yet implemented!");
        }

    }
    
    protected void checkAndAddResultFilterCriterion(Criteria c, GetObservationRequest request,
            SubQueryIdentifier identifier, Session session)
            throws OwsExceptionReport {
        if (request.hasResultFilter()) {
            Filter<?> resultFilter = request.getResultFilter();
            Criterion resultFilterExpression = ResultFilterRestrictions.getResultFilterExpression(resultFilter,
                    getResultFilterClasses(), BaseObservation.OBS_ID, identifier);
            if (resultFilterExpression != null) {
                c.add(resultFilterExpression);
            }
        }
    }
    
    /**
     * Get all observation identifiers
     *
     * @param session
     *            Hibernate session
     * @return Observation identifiers
     */
    @SuppressWarnings("unchecked")
    public List<String> getObservationIdentifier(Session session) {
        Criteria criteria =
                session.createCriteria(getObservationFactory().contextualReferencedClass())
                        .add(Restrictions.eq(ContextualReferencedObservation.DELETED, false))
                        .add(Restrictions.isNotNull(ContextualReferencedObservation.IDENTIFIER))
                        .setProjection(Projections.distinct(Projections.property(ContextualReferencedObservation.IDENTIFIER)));
        LOGGER.debug("QUERY getObservationIdentifiers(): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public SosEnvelope getSpatialFilteringProfileEnvelopeForOfferingId(String offeringID, Session session)
            throws OwsExceptionReport {
        try {
            // XXX workaround for Hibernate Spatial's lack of support for
            // GeoDB's extent aggregate see
            // http://www.hibernatespatial.org/pipermail/hibernatespatial-users/2013-August/000876.html
            Dialect dialect = ((SessionFactoryImplementor) session.getSessionFactory()).getDialect();
            if (GeometryHandler.getInstance().isSpatialDatasource()
                    && HibernateHelper.supportsFunction(dialect, HibernateConstants.FUNC_EXTENT)) {
                Criteria criteria = getDefaultObservationInfoCriteria(session);
                criteria.setProjection(SpatialProjections.extent(TemporalReferencedObservation.SAMPLING_GEOMETRY));
                if (EntitiyHelper.getInstance().isSeriesObservationSupported()) {
                    criteria.createCriteria(SeriesObservation.SERIES).createCriteria(Series.OFFERING)
                            .add(Restrictions.eq(Offering.IDENTIFIER, offeringID));
                } else {
                    criteria.createCriteria(LegacyObservation.OFFERINGS)
                            .add(Restrictions.eq(Offering.IDENTIFIER, offeringID));
                }
                LOGGER.debug("QUERY getSpatialFilteringProfileEnvelopeForOfferingId(offeringID): {}", HibernateHelper.getSqlString(criteria));
                Geometry geom = (Geometry) criteria.uniqueResult();
                geom = GeometryHandler.getInstance().switchCoordinateAxisFromToDatasourceIfNeeded(geom);
                if (geom != null) {
                    return new SosEnvelope(geom.getEnvelopeInternal(), GeometryHandler.getInstance().getStorageEPSG());
                }
            } else {
                Envelope envelope = null;
                Criteria criteria = getDefaultObservationInfoCriteria(session);
                if (EntitiyHelper.getInstance().isSeriesObservationSupported()) {
                    criteria.createCriteria(SeriesObservation.SERIES).createCriteria(Series.OFFERING)
                            .add(Restrictions.eq(Offering.IDENTIFIER, offeringID));
                } else {
                    criteria.createCriteria(LegacyObservation.OFFERINGS)
                            .add(Restrictions.eq(Offering.IDENTIFIER, offeringID));
                }
                if (HibernateHelper.isColumnSupported(getObservationFactory().contextualReferencedClass(), TemporalReferencedObservation.SAMPLING_GEOMETRY)) {
                    criteria.add(Restrictions.isNotNull(TemporalReferencedObservation.SAMPLING_GEOMETRY));
                    criteria.setProjection(Projections.property(TemporalReferencedObservation.SAMPLING_GEOMETRY));
                    LOGGER.debug("QUERY getSpatialFilteringProfileEnvelopeForOfferingId(offeringID): {}",
                            HibernateHelper.getSqlString(criteria));
                    envelope = new Envelope();
                    for (Geometry geom : (List<Geometry>) criteria.list()) {
                        envelope.expandToInclude(geom.getEnvelopeInternal());
                    }
                } else if (HibernateHelper.isColumnSupported(getObservationFactory().contextualReferencedClass(), TemporalReferencedObservation.LATITUDE)
                        && HibernateHelper.isColumnSupported(getObservationFactory().contextualReferencedClass(), TemporalReferencedObservation.LONGITUDE)) {
                    criteria.add(Restrictions.and(Restrictions.isNotNull(TemporalReferencedObservation.LATITUDE),
                            Restrictions.isNotNull(TemporalReferencedObservation.LONGITUDE)));
                    criteria.setProjection(Projections.projectionList().add(Projections.min(TemporalReferencedObservation.LATITUDE))
                            .add(Projections.min(TemporalReferencedObservation.LONGITUDE))
                            .add(Projections.max(TemporalReferencedObservation.LATITUDE))
                            .add(Projections.max(TemporalReferencedObservation.LONGITUDE)));

                    LOGGER.debug("QUERY getBboxFromSamplingGeometries(feature): {}",
                            HibernateHelper.getSqlString(criteria));
                    MinMaxLatLon minMaxLatLon = new MinMaxLatLon((Object[]) criteria.uniqueResult());
                    envelope = new Envelope(minMaxLatLon.getMinLon(), minMaxLatLon.getMaxLon(),
                            minMaxLatLon.getMinLat(), minMaxLatLon.getMaxLat());
                }
                if (!envelope.isNull()) {
                    return new SosEnvelope(envelope, GeometryHandler.getInstance().getStorageEPSG());
                }

            }
        } catch (final HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he)
                    .withMessage("Exception thrown while requesting feature envelope for observation ids");
        }
        return null;
    }

    public abstract List<Geometry> getSamplingGeometries(String feature, Session session) throws OwsExceptionReport;

    public abstract Long getSamplingGeometriesCount(String feature, Session session) throws OwsExceptionReport;

    public abstract Envelope getBboxFromSamplingGeometries(String feature, Session session) throws OwsExceptionReport;

    public abstract ObservationFactory getObservationFactory();

    public abstract String addProcedureAlias(Criteria criteria);


    protected abstract Criteria addAdditionalObservationIdentification(Criteria c, OmObservation sosObservation);

    /**
     * @param sosObservation {@link OmObservation} to check
     * @param observationConstellation 
     * @param session Hibernate {@link Session}
     * @throws OwsExceptionReport
     */
    public void checkForDuplicatedObservations(OmObservation sosObservation, ObservationConstellation observationConstellation, Session session) throws OwsExceptionReport {
        Criteria c = getTemoralReferencedObservationCriteriaFor(sosObservation, observationConstellation, session);
        // add times check (start/end phen, result)
        List<TemporalFilter> filters = Lists.newArrayListWithCapacity(2);
        filters.add(getPhenomeonTimeFilter(c, sosObservation.getPhenomenonTime()));
        filters.add(getResultTimeFilter(c, sosObservation.getResultTime(), sosObservation.getPhenomenonTime()));
        c.add(TemporalRestrictions.filter(filters));
        if (sosObservation.isSetHeightDepthParameter()) {
            NamedValue<Double> hdp = sosObservation.getHeightDepthParameter();
            addParameterRestriction(c, hdp);
        }
        c.setMaxResults(1);
        LOGGER.debug("QUERY checkForDuplicatedObservations(): {}", HibernateHelper.getSqlString(c));
        if (!c.list().isEmpty()) {
            StringBuilder builder = new StringBuilder();
            builder.append("procedure=").append(sosObservation.getObservationConstellation().getProcedureIdentifier());
            builder.append("observedProperty=").append(sosObservation.getObservationConstellation().getObservablePropertyIdentifier());
            builder.append("featureOfInter=").append(sosObservation.getObservationConstellation().getFeatureOfInterestIdentifier());
            builder.append("phenomenonTime=").append(sosObservation.getPhenomenonTime().toString());
            builder.append("resultTime=").append(sosObservation.getResultTime().toString());
            // TODO for e-Reporting SampligPoint should be added.
            if (sosObservation.isSetHeightDepthParameter()) {
                NamedValue<Double> hdp = sosObservation.getHeightDepthParameter();
                builder.append("height/depth=").append(hdp.getName().getHref()).append("/").append(hdp.getValue().getValue());
            }
            throw new NoApplicableCodeException().withMessage("The observation for %s already exists in the database!", builder.toString());
        }
    }

    public boolean isIdentifierContained(String identifier, Session session) {
            Criteria c = getDefaultObservationCriteria(session).add(Restrictions.eq(Observation.IDENTIFIER, identifier));
            LOGGER.debug("QUERY isIdentifierContained(identifier): {}",
                    HibernateHelper.getSqlString(c));
            return c.list().size() > 0;
    }

    private void addParameterRestriction(Criteria c, NamedValue<?> hdp) throws OwsExceptionReport {
        c.add(Subqueries.propertyIn(AbstractBaseObservation.OBS_ID, getParameterRestriction(c, hdp.getName().getHref(), hdp.getValue().getValue(), hdp.getValue().accept(getParameterFactory()).getClass())));
    }

    protected DetachedCriteria getParameterRestriction(Criteria c, String name, Object value, Class<?> clazz) {
        DetachedCriteria detachedCriteria = DetachedCriteria.forClass(clazz);
        addParameterNameRestriction(detachedCriteria, name);
        addParameterValueRestriction(detachedCriteria, value);
        detachedCriteria.setProjection(Projections.distinct(Projections.property(Parameter.OBS_ID)));
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

    private TemporalFilter getResultTimeFilter(Criteria c, TimeInstant resultTime, Time phenomenonTime) throws CodedException {
        String valueReferencep = Sos2Constants.EN_RESULT_TIME;
        if (resultTime != null) {
            if (resultTime.getValue() != null) {
                return new TemporalFilter(TimeOperator.TM_Equals, resultTime, valueReferencep);
            } else if (TimeIndeterminateValue.contains(Sos2Constants.EN_PHENOMENON_TIME)
                    && phenomenonTime instanceof TimeInstant) {
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
     *            Hibernate session
     * @return <code>true</code>, if the observation table contains
     *         samplingGeometries with values
     */
    public boolean containsSamplingGeometries(Session session) {
        Criteria criteria = getDefaultObservationInfoCriteria(session);
        criteria.setProjection(Projections.rowCount());
        if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_CHECK_SAMPLING_GEOMETRIES, session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_CHECK_SAMPLING_GEOMETRIES);
            LOGGER.debug("QUERY containsSamplingGeometries() with NamedQuery: {}",
                    SQL_QUERY_CHECK_SAMPLING_GEOMETRIES);
            return (boolean) namedQuery.uniqueResult();
        } else if (HibernateHelper.isColumnSupported(getObservationFactory().contextualReferencedClass(), AbstractObservation.SAMPLING_GEOMETRY)) {
            criteria.add(Restrictions.isNotNull(AbstractObservation.SAMPLING_GEOMETRY));
            LOGGER.debug("QUERY containsSamplingGeometries(): {}", HibernateHelper.getSqlString(criteria));
            return (Long) criteria.uniqueResult() > 0;
        } else if (HibernateHelper.isColumnSupported(getObservationFactory().contextualReferencedClass(), AbstractObservation.LONGITUDE)
                && HibernateHelper.isColumnSupported(getObservationFactory().contextualReferencedClass(), AbstractObservation.LATITUDE)) {
            criteria.add(Restrictions.and(Restrictions.isNotNull(AbstractObservation.LONGITUDE),
                    Restrictions.isNotNull(AbstractObservation.LATITUDE)));
            LOGGER.debug("QUERY containsSamplingGeometries(): {}", HibernateHelper.getSqlString(criteria));
            return (Long) criteria.uniqueResult() > 0;
        }
        return false;
    }
    
    

    public TimeExtrema getObservationTimeExtrema(Session session) throws CodedException {
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

    /**
     * Observation time extrema {@link ResultTransformer}
     *
     * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
     * @since 4.4.0
     *
     */
    public static class ObservationTimeTransformer implements ResultTransformer {

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


    public class MinMaxLatLon {
        private Double minLat;

        private Double maxLat;

        private Double minLon;

        private Double maxLon;

        public MinMaxLatLon(Object[] result) {
            setMinLat(JavaHelper.asDouble(result[0]));
            setMinLon(JavaHelper.asDouble(result[1]));
            setMaxLat(JavaHelper.asDouble(result[2]));
            setMaxLon(JavaHelper.asDouble(result[3]));
        }

        /**
         * @return the minLat
         */
        public Double getMinLat() {
            return minLat;
        }

        /**
         * @param minLat
         *            the minLat to set
         */
        public void setMinLat(Double minLat) {
            this.minLat = minLat;
        }

        /**
         * @return the maxLat
         */
        public Double getMaxLat() {
            return maxLat;
        }

        /**
         * @param maxLat
         *            the maxLat to set
         */
        public void setMaxLat(Double maxLat) {
            this.maxLat = maxLat;
        }

        /**
         * @return the minLon
         */
        public Double getMinLon() {
            return minLon;
        }

        /**
         * @param minLon
         *            the minLon to set
         */
        public void setMinLon(Double minLon) {
            this.minLon = minLon;
        }

        /**
         * @return the maxLon
         */
        public Double getMaxLon() {
            return maxLon;
        }

        /**
         * @param maxLon
         *            the maxLon to set
         */
        public void setMaxLon(Double maxLon) {
            this.maxLon = maxLon;
        }
    }
    
}
