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

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.spatial.criterion.SpatialProjections;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.sos.ds.hibernate.entities.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.AbstractObservationTime;
import org.n52.sos.ds.hibernate.entities.interfaces.BlobObservation;
import org.n52.sos.ds.hibernate.entities.interfaces.CategoryObservation;
import org.n52.sos.ds.hibernate.entities.Codespace;
import org.n52.sos.ds.hibernate.entities.interfaces.CountObservation;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.interfaces.GeometryObservation;
import org.n52.sos.ds.hibernate.entities.interfaces.NumericObservation;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.Observation;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.interfaces.SweDataArrayObservation;
import org.n52.sos.ds.hibernate.entities.interfaces.TextObservation;
import org.n52.sos.ds.hibernate.entities.Unit;
import org.n52.sos.ds.hibernate.entities.interfaces.BooleanObservation;
import org.n52.sos.ds.hibernate.util.HibernateConstants;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.ScrollableIterable;
import org.n52.sos.ds.hibernate.util.SpatialRestrictions;
import org.n52.sos.ds.hibernate.util.observation.HibernateObservationUtilities;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.OptionNotSupportedException;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.Time.TimeIndeterminateValue;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.values.BooleanValue;
import org.n52.sos.ogc.om.values.CategoryValue;
import org.n52.sos.ogc.om.values.CountValue;
import org.n52.sos.ogc.om.values.GeometryValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.SweDataArrayValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.om.values.UnknownValue;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants.SosIndeterminateTime;
import org.n52.sos.ogc.sos.SosEnvelope;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Abstract Hibernate data access class for observations.
 *
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.0.0
 *
 */
public abstract class AbstractObservationDAO extends AbstractIdentifierNameDescriptionDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractObservationDAO.class);

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
    protected abstract void addObservationIdentifiersToObservation(ObservationIdentifiers observationIdentifiers,
            AbstractObservation observation, Session session) throws CodedException;

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
    public abstract Criteria getObservationInfoCriteriaForFeatureOfInterestAndOffering(String feature,
            String offering, Session session);

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
    public abstract Criteria getObservationCriteriaForProcedure(String procedure, Session session) throws CodedException;

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
    public abstract Criteria getObservationCriteriaForObservableProperty(String observableProperty, Session session) throws CodedException;

    /**
     * Get Hibernate Criteria for observation with restriction featureOfInterest
     *
     * @param featureOfInterest
     * @param session
     *            Hibernate session
     * @return Hibernate Criteria to query observations
     * @throws CodedException 
     */
    public abstract Criteria getObservationCriteriaForFeatureOfInterest(String featureOfInterest, Session session) throws CodedException;

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
    public abstract Criteria getObservationCriteriaFor(String procedure, String observableProperty, Session session) throws CodedException;

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

    /**
     * Query observation by identifier
     *
     * @param identifier
     *            Observation identifier (gml:identifier)
     * @param session
     *            Hiberante session
     * @return Observation
     */
    public AbstractObservation getObservationByIdentifier(String identifier, Session session) {
        Criteria criteria = getDefaultObservationCriteria(session);
        addObservationIdentifierToCriteria(criteria, identifier, session);
        return (AbstractObservation) criteria.uniqueResult();
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
        return checkObservationFor(getNumericObservationClass(), offeringIdentifier, session);
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
        return checkObservationFor(getBooleanObservationClass(), offeringIdentifier, session);
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
        return checkObservationFor(getCountObservationClass(), offeringIdentifier, session);
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
        return checkObservationFor(getCategoryObservationClass(), offeringIdentifier, session);
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
        return checkObservationFor(getTextObservationClass(), offeringIdentifier, session);
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
        return checkObservationFor(getBlobObservationClass(), offeringIdentifier, session);
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
        return checkObservationFor(getGeometryObservationClass(), offeringIdentifier, session);
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
        return checkObservationFor(getSweDataArrayObservationClass(), offeringIdentifier, session);
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
        if (StringHelper.isNotEmpty(resultModel)) {
            if (resultModel.equals(OmConstants.OBS_TYPE_MEASUREMENT)) {
                return createCriteriaForObservationClass(getNumericObservationClass(), session);
            } else if (resultModel.equals(OmConstants.OBS_TYPE_COUNT_OBSERVATION)) {
                return createCriteriaForObservationClass(getCategoryObservationClass(), session);
            } else if (resultModel.equals(OmConstants.OBS_TYPE_CATEGORY_OBSERVATION)) {
                return createCriteriaForObservationClass(getCategoryObservationClass(), session);
            } else if (resultModel.equals(OmConstants.OBS_TYPE_TRUTH_OBSERVATION)) {
                return createCriteriaForObservationClass(getBooleanObservationClass(), session);
            } else if (resultModel.equals(OmConstants.OBS_TYPE_TEXT_OBSERVATION)) {
                return createCriteriaForObservationClass(getTextObservationClass(), session);
            } else if (resultModel.equals(OmConstants.OBS_TYPE_GEOMETRY_OBSERVATION)) {
                return createCriteriaForObservationClass(getGeometryObservationClass(), session);
            } else if (resultModel.equals(OmConstants.OBS_TYPE_COMPLEX_OBSERVATION)) {
                return createCriteriaForObservationClass(getBlobObservationClass(), session);
            }
        }
        return createCriteriaForObservationClass(getObservationClass(), session);
    }

    /**
     * Create an observation object from SOS value
     *
     * @param value
     *            SOS value
     * @param session
     *            Hibernate session
     * @return Observation object
     * @throws CodedException 
     */
    public AbstractObservation createObservationFromValue(Value<?> value, Session session) throws CodedException {
        try {

            if (value instanceof BooleanValue) {
                AbstractObservation observation = (AbstractObservation) getBooleanObservationClass().newInstance();
                ((BooleanObservation) observation).setValue(((BooleanValue) value).getValue());
                return observation;
            } else if (value instanceof UnknownValue) {
                AbstractObservation observation = (AbstractObservation) getBlobObservationClass().newInstance();
                ((BlobObservation) observation).setValue(((UnknownValue) value).getValue());
                return observation;
            } else if (value instanceof CategoryValue) {
                AbstractObservation observation = (AbstractObservation) getCategoryObservationClass().newInstance();
                ((CategoryObservation) observation).setValue(((CategoryValue) value).getValue());
                return observation;
            } else if (value instanceof CountValue) {
                AbstractObservation observation = (AbstractObservation) getCountObservationClass().newInstance();
                ((CountObservation) observation).setValue(((CountValue) value).getValue());
                return observation;
            } else if (value instanceof GeometryValue) {
                AbstractObservation observation = (AbstractObservation) getGeometryObservationClass().newInstance();
                ((GeometryObservation) observation).setValue(((GeometryValue) value).getValue());
                return observation;
            } else if (value instanceof QuantityValue) {
                AbstractObservation observation = (AbstractObservation) getNumericObservationClass().newInstance();
                ((NumericObservation) observation).setValue(((QuantityValue) value).getValue());
                return observation;
            } else if (value instanceof TextValue) {
                AbstractObservation observation = (AbstractObservation) getTextObservationClass().newInstance();
                ((TextObservation) observation).setValue(((TextValue) value).getValue());
                return observation;
            } else if (value instanceof SweDataArrayValue) {
                AbstractObservation observation = (AbstractObservation) getSweDataArrayObservationClass().newInstance();
                ((SweDataArrayObservation) observation).setValue(((SweDataArrayValue) value).getValue().getXml());
                return observation;
            }
            return (AbstractObservation) getObservationClass().newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new NoApplicableCodeException().causedBy(e).withMessage("Error while creating observation instance for %S", value.getClass().getCanonicalName());
        }
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
       return getDefaultCriteria(getObservationClass(), session);
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
        return getDefaultCriteria(getObservationInfoClass(), session);
    }
    
    /**
     * Get default Hibernate Criteria to query observation time, default flag ==
     * <code>false</code>
     *
     * @param session
     *            Hiberante session
     * @return Default Criteria
     */
    public Criteria getDefaultObservationTimeCriteria(Session session) {
        return getDefaultCriteria(getObservationTimeClass(), session);
    }
    
    @SuppressWarnings("rawtypes")
    private Criteria getDefaultCriteria(Class clazz, Session session) {
        return session.createCriteria(clazz).add(Restrictions.eq(AbstractObservation.DELETED, false))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    }

    /**
     * Get Hibernate Criteria for observation with restriction procedure Insert
     * a multi value observation for observation constellations and
     * featureOfInterest
     *
     * @param observationConstellations
     *            Observation constellation objects
     * @param feature
     *            FeatureOfInterest object
     * @param containerObservation
     *            SOS observation
     * @param codespaceCache
     *            Map based codespace object cache to prevent redundant queries
     * @param unitCache
     *            Map based unit object cache to prevent redundant queries
     * @param session
     *            Hibernate session
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    public void insertObservationMultiValue(Set<ObservationConstellation> observationConstellations,
            FeatureOfInterest feature, OmObservation containerObservation, Map<String, Codespace> codespaceCache,
            Map<String, Unit> unitCache, Session session) throws OwsExceptionReport {
        List<OmObservation> unfoldObservations = HibernateObservationUtilities.unfoldObservation(containerObservation);
        for (OmObservation sosObservation : unfoldObservations) {
            insertObservationSingleValue(observationConstellations, feature, sosObservation, codespaceCache,
                    unitCache, session);
        }
    }

    /**
     * Insert a single observation for observation constellations and
     * featureOfInterest without local caching for codespaces and units
     *
     * @param observationConstellations
     *            Observation constellation objects
     * @param feature
     *            FeatureOfInterest object
     * @param sosObservation
     *            SOS observation to insert
     * @param session
     *            Hibernate session
     * @throws OwsExceptionReport
     */
    public void insertObservationSingleValue(Set<ObservationConstellation> hObservationConstellations,
            FeatureOfInterest hFeature, OmObservation sosObservation, Session session) throws OwsExceptionReport {
        insertObservationSingleValue(hObservationConstellations, hFeature, sosObservation, null, null, session);
    }

    /**
     * Insert a single observation for observation constellations and
     * featureOfInterest with local caching for codespaces and units
     *
     * @param observationConstellations
     *            Observation constellation objects
     * @param feature
     *            FeatureOfInterest object
     * @param sosObservation
     *            SOS observation to insert
     * @param codespaceCache
     *            Map cache for codespace objects (to prevent redundant
     *            querying)
     * @param unitCache
     *            Map cache for unit objects (to prevent redundant querying)
     * @param session
     *            Hibernate session
     * @throws OwsExceptionReport
     */
    @SuppressWarnings("rawtypes")
    public void insertObservationSingleValue(Set<ObservationConstellation> hObservationConstellations,
            FeatureOfInterest hFeature, OmObservation sosObservation, Map<String, Codespace> codespaceCache,
            Map<String, Unit> unitCache, Session session) throws OwsExceptionReport {
        SingleObservationValue<?> value = (SingleObservationValue) sosObservation.getValue();
        AbstractObservation hObservation = createObservationFromValue(value.getValue(), session);
        hObservation.setDeleted(false);
        addIdentifierNameDescription(sosObservation, hObservation, session);
        addPhenomeonTimeAndResultTimeToObservation(hObservation, sosObservation.getPhenomenonTime(),
                sosObservation.getResultTime());

        if (value.getValue().getUnit() != null) {
            hObservation.setUnit(getUnit(value.getValue().getUnit(), unitCache, session));
        }
        addOfferingsToObservation(hObservation, hObservationConstellations);
        ObservationIdentifiers observationIdentifiers = createObservationIdentifiers(hObservationConstellations);
        addProcedureObservablePropertyToObservationIdentifiers(hObservationConstellations, observationIdentifiers);
        addFeatureOfInterestToObservationIdentifiers(hFeature, observationIdentifiers);
        addAdditionalObjectsToObservationIdentifiers(observationIdentifiers, sosObservation, session);
        addObservationIdentifiersToObservation(observationIdentifiers, hObservation, session);
        if (sosObservation.isSetSpatialFilteringProfileParameter()) {
            hObservation.setSamplingGeometry(GeometryHandler.getInstance()
                    .switchCoordinateAxisFromToDatasourceIfNeeded(
                            sosObservation.getSpatialFilteringProfileParameter().getValue().getValue()));
        }

        session.saveOrUpdate(hObservation);
        // don't flush here because we may be batching

        if (sosObservation.isSetParameter()) {
            insertParameter(sosObservation.getParameter(), hObservation, session);
        }
    }

    protected void addOfferingsToObservation(AbstractObservation hObservation,
            Set<ObservationConstellation> hObservationConstellations) {
        for (ObservationConstellation observationConstellation : hObservationConstellations) {
            hObservation.getOfferings().add(observationConstellation.getOffering());
        }
    }

    protected ObservationIdentifiers createObservationIdentifiers(Set<ObservationConstellation> hObservationConstellations) {
        ObservationIdentifiers observationIdentifiers = new ObservationIdentifiers();
        addProcedureObservablePropertyToObservationIdentifiers(hObservationConstellations, observationIdentifiers);
        return observationIdentifiers;
    }

    protected ObservationIdentifiers addProcedureObservablePropertyToObservationIdentifiers(
            Set<ObservationConstellation> hObservationConstellations, ObservationIdentifiers observationIdentifiers) {
        if (CollectionHelper.isNotEmpty(hObservationConstellations)) {
            boolean firstObsConst = true;
            for (ObservationConstellation observationConstellation : hObservationConstellations) {
                if (firstObsConst) {
                    observationIdentifiers.setObservableProperty(observationConstellation.getObservableProperty());
                    observationIdentifiers.setProcedure(observationConstellation.getProcedure());
                    firstObsConst = false;
                }
            }
        }
        return observationIdentifiers;
    }

    protected ObservationIdentifiers addFeatureOfInterestToObservationIdentifiers(FeatureOfInterest hFeature,
            ObservationIdentifiers observationIdentifiers) {
        observationIdentifiers.setFeatureOfInterest(hFeature);
        return observationIdentifiers;
    }

    protected ObservationIdentifiers addAdditionalObjectsToObservationIdentifiers(ObservationIdentifiers observationIdentifiers,
            OmObservation sosObservation, Session session) {
        return observationIdentifiers;
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
     *            Hibernate Criteria
     * @param identifier
     *            Observation identifier (gml:identifier)
     * @param session
     *            Hibernate session
     */
    protected void addObservationIdentifierToCriteria(Criteria criteria, String identifier, Session session) {
        criteria.add(Restrictions.eq(AbstractObservation.IDENTIFIER, identifier));
    }

//    /**
//     * Add offerings to observation and return the observation identifiers
//     * procedure and observableProperty
//     *
//     * @param hObservation
//     *            Observation to add offerings
//     * @param hObservationConstellations
//     *            Observation constellation with offerings, procedure and
//     *            observableProperty
//     * @return ObservaitonIdentifiers object with procedure and
//     *         observableProperty
//     */
//    protected ObservationIdentifiers addOfferingsToObaservationAndGetProcedureObservableProperty(
//            AbstractObservation hObservation, Set<ObservationConstellation> hObservationConstellations) {
//        Iterator<ObservationConstellation> iterator = hObservationConstellations.iterator();
//        boolean firstObsConst = true;
//        ObservationIdentifiers observationIdentifiers = new ObservationIdentifiers();
//        while (iterator.hasNext()) {
//            ObservationConstellation observationConstellation = iterator.next();
//            if (firstObsConst) {
//                observationIdentifiers.setObservableProperty(observationConstellation.getObservableProperty());
//                observationIdentifiers.setProcedure(observationConstellation.getProcedure());
//                firstObsConst = false;
//            }
//            hObservation.getOfferings().add(observationConstellation.getOffering());
//        }
//        return observationIdentifiers;
//    }

    protected void finalizeObservationInsertion(OmObservation sosObservation, AbstractObservation hObservation,
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
    protected void insertParameter(Collection<NamedValue<?>> parameter, AbstractObservation observation,
            Session session) throws OwsExceptionReport {
        throw new OptionNotSupportedException().at("om:parameter").withMessage(
                "The om:parameter support is not yet implemented!");
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
        Criteria c = session.createCriteria(clazz).add(Restrictions.eq(AbstractObservation.DELETED, false));
        c.createCriteria(AbstractObservation.OFFERINGS).add(Restrictions.eq(Offering.IDENTIFIER, offeringIdentifier));
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
                session.createCriteria(getObservationTimeClass())
                        .setProjection(Projections.min(AbstractObservation.PHENOMENON_TIME_START))
                        .add(Restrictions.eq(AbstractObservation.DELETED, false));
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
                session.createCriteria(getObservationTimeClass())
                        .setProjection(Projections.max(AbstractObservation.PHENOMENON_TIME_START))
                        .add(Restrictions.eq(AbstractObservation.DELETED, false));
        LOGGER.debug("QUERY getMaxPhenomenonTime() start: {}", HibernateHelper.getSqlString(criteriaStart));
        Object maxStart = criteriaStart.uniqueResult();

        Criteria criteriaEnd =
                session.createCriteria(getObservationTimeClass())
                        .setProjection(Projections.max(AbstractObservation.PHENOMENON_TIME_END))
                        .add(Restrictions.eq(AbstractObservation.DELETED, false));
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
                session.createCriteria(getObservationTimeClass())
                        .setProjection(Projections.min(AbstractObservation.RESULT_TIME))
                        .add(Restrictions.eq(AbstractObservation.DELETED, false));
        LOGGER.debug("QUERY getMinResultTime(): {}", HibernateHelper.getSqlString(criteria));
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
    public DateTime getMaxResultTime(Session session) {

        Criteria criteria =
                session.createCriteria(getObservationTimeClass())
                        .setProjection(Projections.max(AbstractObservation.RESULT_TIME))
                        .add(Restrictions.eq(AbstractObservation.DELETED, false));
        LOGGER.debug("QUERY getMaxResultTime(): {}", HibernateHelper.getSqlString(criteria));
        Object max = criteria.uniqueResult();
        if (max == null) {
            return null;
        } else {
            return new DateTime(max, DateTimeZone.UTC);
        }
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
            Criteria criteria = session.createCriteria(getObservationTimeClass());
            criteria.add(Restrictions.eq(AbstractObservation.DELETED, false));
            criteria.setProjection(Projections.projectionList()
                    .add(Projections.min(AbstractObservation.PHENOMENON_TIME_START))
                    .add(Projections.max(AbstractObservation.PHENOMENON_TIME_START))
                    .add(Projections.max(AbstractObservation.PHENOMENON_TIME_END)));
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
            return Order.asc(AbstractObservation.PHENOMENON_TIME_START);
        } else if (indetTime.equals(SosIndeterminateTime.latest)) {
            return Order.desc(AbstractObservation.PHENOMENON_TIME_END);
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
            return Projections.min(AbstractObservation.PHENOMENON_TIME_START);
        } else if (indetTime.equals(SosIndeterminateTime.latest)) {
            return Projections.max(AbstractObservation.PHENOMENON_TIME_END);
        }
        return null;
    }

    /**
     * Get the AbstractObservation property to filter on for an
     * {@link SosIndeterminateTime}
     *
     * @param indetTime
     *            Value to get property for
     * @return String property to filter on
     */
    protected String getIndeterminateTimeFilterProperty(final SosIndeterminateTime indetTime) {
        if (indetTime.equals(SosIndeterminateTime.first)) {
            return AbstractObservation.PHENOMENON_TIME_START;
        } else if (indetTime.equals(SosIndeterminateTime.latest)) {
            return AbstractObservation.PHENOMENON_TIME_END;
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
        return session.createCriteria(clazz).add(Restrictions.eq(AbstractObservation.DELETED, false))
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
     * @throws CodedException
     *             If an error occurs
     */
    protected void addPhenomeonTimeAndResultTimeToObservation(AbstractObservation observation, Time phenomenonTime,
            TimeInstant resultTime) throws CodedException {
        addPhenomenonTimeToObservation(observation, phenomenonTime);
        addResultTimeToObservation(observation, resultTime, phenomenonTime);
    }

    /**
     * Add phenomenon time to observation object
     *
     * @param observation
     *            Observation object
     * @param phenomenonTime
     *            SOS phenomenon time
     */
    protected void addPhenomenonTimeToObservation(AbstractObservation observation, Time phenomenonTime) {
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
     *            Observation object
     * @param resultTime
     *            SOS result time
     * @param phenomenonTime
     *            SOS phenomenon time
     * @throws CodedException
     *             If an error occurs
     */
    protected void addResultTimeToObservation(AbstractObservation observation, TimeInstant resultTime,
            Time phenomenonTime) throws CodedException {
        if (resultTime != null) {
            if (resultTime.getValue() != null) {
                observation.setResultTime(resultTime.getValue().toDate());
            } else if (TimeIndeterminateValue.contains(Sos2Constants.EN_PHENOMENON_TIME)
                    && phenomenonTime instanceof TimeInstant) {
                observation.setResultTime(((TimeInstant) phenomenonTime).getValue().toDate());
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

    /**
     * Add valid time to observation object
     *
     * @param observation
     *            Observation object
     * @param validTime
     *            SOS valid time
     */
    protected void addValidTimeToObservation(AbstractObservation observation, TimePeriod validTime) {
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
    protected void updateObservation(ScrollableIterable<AbstractObservation> scroll, boolean deleteFlag,
            Session session) {
        if (scroll != null) {
            try {
                for (AbstractObservation o : scroll) {
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
            c.add(SpatialRestrictions.filter(
                    AbstractObservation.SAMPLING_GEOMETRY,
                    request.getSpatialFilter().getOperator(),
                    GeometryHandler.getInstance().switchCoordinateAxisFromToDatasourceIfNeeded(
                            request.getSpatialFilter().getGeometry())));
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
                session.createCriteria(getObservationInfoClass()).add(Restrictions.eq(Observation.DELETED, false))
                        .add(Restrictions.isNotNull(Observation.IDENTIFIER))
                        .setProjection(Projections.distinct(Projections.property(Observation.IDENTIFIER)));
        LOGGER.debug("QUERY getObservationIdentifiers(): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    public SosEnvelope getSpatialFilteringProfileEnvelopeForOfferingId(String offeringID,
            Session session) throws OwsExceptionReport {
        try {
            // XXX workaround for Hibernate Spatial's lack of support for
            // GeoDB's extent aggregate see
            // http://www.hibernatespatial.org/pipermail/hibernatespatial-users/2013-August/000876.html
            Dialect dialect = ((SessionFactoryImplementor) session.getSessionFactory()).getDialect();
            if (GeometryHandler.getInstance().isSpatialDatasource()
                    && HibernateHelper.supportsFunction(dialect, HibernateConstants.FUNC_EXTENT)) {
                Criteria criteria = getDefaultObservationInfoCriteria(session);
                criteria.setProjection(SpatialProjections.extent(AbstractObservationTime.SAMPLING_GEOMETRY));
                criteria.createCriteria(AbstractObservation.OFFERINGS).add(
                        Restrictions.eq(Offering.IDENTIFIER, offeringID));
                LOGGER.debug("QUERY getSpatialFilteringProfileEnvelopeForOfferingId(offeringID): {}", HibernateHelper.getSqlString(criteria));
                Geometry geom = (Geometry) criteria.uniqueResult();
                geom = GeometryHandler.getInstance().switchCoordinateAxisFromToDatasourceIfNeeded(geom);
                if (geom != null) {
                    return new SosEnvelope(geom.getEnvelopeInternal(), GeometryHandler.getInstance().getStorageEPSG());
                }
            } else {
                final Envelope envelope = new Envelope();
                Criteria criteria = getDefaultObservationInfoCriteria(session);
                criteria.createCriteria(AbstractObservation.OFFERINGS).add(
                        Restrictions.eq(Offering.IDENTIFIER, offeringID));
                LOGGER.debug("QUERY getSpatialFilteringProfileEnvelopeForOfferingId(offeringID): {}", HibernateHelper.getSqlString(criteria));
                @SuppressWarnings("unchecked")
                final List<AbstractObservationTime> observationTimes = criteria.list();
                if (CollectionHelper.isNotEmpty(observationTimes)) {
                    for (final AbstractObservationTime observationTime : observationTimes) {
                        if (observationTime.hasSamplingGeometry()) {
                            final Geometry geom = observationTime.getSamplingGeometry();
                            if (geom != null && geom.getEnvelopeInternal() != null) {
                                envelope.expandToInclude(geom.getEnvelopeInternal());
                            }
                        }
                    }
                    if (!envelope.isNull()) {
                        return new SosEnvelope(envelope, GeometryHandler.getInstance().getStorageEPSG());
                    }
                }
            }
        } catch (final HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he).withMessage(
                    "Exception thrown while requesting feature envelope for observation ids");
        }
        return null;
    }

    public abstract List<Geometry> getSamplingGeometries(String feature, Session session);

    protected abstract Class<?> getObservationClass();

    protected abstract Class<?> getObservationInfoClass();

    protected abstract Class<?> getObservationTimeClass();

    protected abstract Class<?> getBlobObservationClass();

    protected abstract Class<?> getBooleanObservationClass();

    protected abstract Class<?> getCategoryObservationClass();

    protected abstract Class<?> getCountObservationClass();

    protected abstract Class<?> getGeometryObservationClass();

    protected abstract Class<?> getNumericObservationClass();

    protected abstract Class<?> getSweDataArrayObservationClass();

    protected abstract Class<?> getTextObservationClass();

    /**
     * Inner class to carry observation identifiers (featureOfInterest,
     * observableProperty, procedure)
     *
     * @author Carsten Hollmann <c.hollmann@52north.org>
     * @since 4.0.0
     *
     */
    protected class ObservationIdentifiers {
    
        FeatureOfInterest featureOfInterest;
    
        ObservableProperty observableProperty;
    
        Procedure procedure;
    
        /**
         * @return the featureOfInterest
         */
        public FeatureOfInterest getFeatureOfInterest() {
            return featureOfInterest;
        }
    
        /**
         * @param featureOfInterest
         *            the featureOfInterest to set
         */
        public void setFeatureOfInterest(FeatureOfInterest featureOfInterest) {
            this.featureOfInterest = featureOfInterest;
        }
    
        /**
         * @return the observableProperty
         */
        public ObservableProperty getObservableProperty() {
            return observableProperty;
        }
    
        /**
         * @param observableProperty
         *            the observableProperty to set
         */
        public void setObservableProperty(ObservableProperty observableProperty) {
            this.observableProperty = observableProperty;
        }
    
        /**
         * @return the procedure
         */
        public Procedure getProcedure() {
            return procedure;
        }
    
        /**
         * @param procedure
         *            the procedure to set
         */
        public void setProcedure(Procedure procedure) {
            this.procedure = procedure;
        }
    
    }

    /**
     * Check if the observation table contains samplingGeometries with values
     * 
     * @param session
     *            Hibernate session
     * @return <code>true</code>, if the observation table contains samplingGeometries with values
     */
    public boolean containsSamplingGeometries(Session session) {
        Criteria criteria = getDefaultObservationInfoCriteria(session);
        criteria.add(Restrictions.isNotNull(AbstractObservation.SAMPLING_GEOMETRY));
        criteria.setProjection(Projections.rowCount());
        LOGGER.debug("QUERY containsSamplingGeometries(): {}", HibernateHelper.getSqlString(criteria));
        return (Long) criteria.uniqueResult() > 0;
    }
}
