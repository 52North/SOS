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
package org.n52.sos.ds.hibernate;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.common.util.StringHelper;
import org.hibernate.exception.ConstraintViolationException;
import org.n52.sos.ds.AbstractInsertObservationDAO;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.FeatureOfInterestDAO;
import org.n52.sos.ds.hibernate.dao.ObservationConstellationDAO;
import org.n52.sos.ds.hibernate.dao.observation.AbstractObservationDAO;
import org.n52.sos.ds.hibernate.entities.Codespace;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Unit;
import org.n52.sos.ds.hibernate.entities.ValidProcedureTime;
import org.n52.sos.ds.hibernate.entities.feature.AbstractFeatureOfInterest;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.MissingParameterValueException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.UoM;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.om.MultiObservationValues;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.InsertObservationRequest;
import org.n52.sos.response.InsertObservationResponse;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.util.http.HTTPStatus;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;


public class InsertObservationDAO extends AbstractInsertObservationDAO {
    private static final int FLUSH_THRESHOLD = 50;
    private static final String CONSTRAINT_OBSERVATION_IDENTITY = "observationIdentity";
    private static final String CONSTRAINT_OBSERVATION_IDENTIFIER_IDENTITY = "obsIdentifierUK";

    private final HibernateSessionHolder sessionHolder = new HibernateSessionHolder();
    private final ObservationConstellationDAO observationConstellationDAO = new ObservationConstellationDAO();
    private final FeatureOfInterestDAO featureOfInterestDAO = new FeatureOfInterestDAO();

    /**
     * constructor
     */
    public InsertObservationDAO() {
        super(SosConstants.SOS);
    }

    @Override
    public String getDatasourceDaoIdentifier() {
        return HibernateDatasourceConstants.ORM_DATASOURCE_DAO_IDENTIFIER;
    }

    @Override
    public synchronized InsertObservationResponse insertObservation(final InsertObservationRequest request)
            throws OwsExceptionReport {
        final InsertObservationResponse response = new InsertObservationResponse();
        response.setService(request.getService());
        response.setVersion(request.getVersion());
        Session session = null;
        Transaction transaction = null;

        // TODO: checkConstellation unit and set if available and not defined in DB
        try {
            session = sessionHolder.getSession();
            transaction = session.beginTransaction();
            final CompositeOwsException exceptions = new CompositeOwsException();

            InsertObservationCache cache = new InsertObservationCache();
            // counter for batch flushing
            int obsCount = 0;

            for (final OmObservation sosObservation : request.getObservations()) {
                // check strict spatial filtering profile
                if (ServiceConfiguration.getInstance().isStrictSpatialFilteringProfile()
                        && !sosObservation.isSetSpatialFilteringProfileParameter()) {
                    throw new MissingParameterValueException(Sos2Constants.InsertObservationParams.parameter)
                            .withMessage("The sampling geometry definition is missing in the observation because"
                                    + " the Spatial Filtering Profile is specification conformant. To use a less"
                                    + " restrictive Spatial Filtering Profile you can change this in the Service-Settings!");
                }
                if (sosObservation.isSetIdentifier()) {
                    if (DaoFactory.getInstance().getObservationDAO()
                            .isIdentifierContained(sosObservation.getIdentifier(), session)) {
                        throw new NoApplicableCodeException().withMessage(
                                "The observation identifier '%s' already exists in the database!",
                                sosObservation.getIdentifier());
                    }
                }
                insertObservation(sosObservation, cache, exceptions, session);

                // flush every FLUSH_INTERVAL
                if (++obsCount % FLUSH_THRESHOLD == 0) {
                    session.flush();
                    session.clear();
                }
            }

            request.setOfferings(Lists.newArrayList(cache.getAllOfferings()));

            // if no observationConstellation is valid, throw exception
            if (exceptions.size() == request.getObservations().size()) {
                throw exceptions;
            }

            session.flush();
            transaction.commit();
        } catch (final HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
            handleHibernateException(he);
        } finally {
            sessionHolder.returnSession(session);
        }
        /*
         * TODO: ... all the DS insertion stuff Requirement 68
         * proc/obsProp/Offering same obsType;
         */

        return response;
    }
    
    private Set<Offering> getOfferings(Set<ObservationConstellation> hObservationConstellations) {
        Set<Offering> offerings = Sets.newHashSet();
        for (ObservationConstellation observationConstellation : hObservationConstellations) {
            offerings.add(observationConstellation.getOffering());
        }
        return offerings;
    }

    private void insertObservation(OmObservation sosObservation,
                                     InsertObservationCache cache,
                                     CompositeOwsException exceptions,
                                     Session session)
            throws OwsExceptionReport, CodedException {

        checkSpatialFilteringProfile(sosObservation);

        OmObservationConstellation sosObsConst = sosObservation.getObservationConstellation();
        cache.addOfferings(sosObsConst.getOfferings());

        Set<ObservationConstellation> hObservationConstellations = new HashSet<>();
        AbstractFeatureOfInterest hFeature = null;

        for (String offeringID : sosObsConst.getOfferings()) {
            ObservationConstellation hObservationConstellation = cache.get(sosObsConst, offeringID);
            if (hObservationConstellation == null) {
                if (!cache.isChecked(sosObsConst, offeringID)) {
                    try {
                        hObservationConstellation =
                                observationConstellationDAO.checkObservationConstellation(
                                        sosObsConst, offeringID, session, Sos2Constants.InsertObservationParams.observationType.name());
                        // add to cache table
                        cache.putConstellation(sosObsConst, offeringID, hObservationConstellation);
                    } catch (OwsExceptionReport owse) {
                        exceptions.add(owse);
                    }
                    // mark as checked
                    cache.checkConstellation(sosObsConst, offeringID);
                }
            }
            if (hObservationConstellation != null) {
                // getFeature feature from local cache or create if necessary
                hFeature = getFeature(sosObsConst.getFeatureOfInterest(), cache, session);

                // only do feature checking once for each
                // AbstractFeature/offering combo
                if (!cache.isChecked(sosObsConst.getFeatureOfInterest(), offeringID)) {
                    featureOfInterestDAO.checkOrInsertFeatureOfInterestRelatedFeatureRelation(
                            hFeature, hObservationConstellation.getOffering(), session);
                    cache.checkFeature(sosObsConst.getFeatureOfInterest(), offeringID);
                }

                hObservationConstellations.add(hObservationConstellation);
            }
        }

        if (!hObservationConstellations.isEmpty()) {
            AbstractObservationDAO observationDAO = DaoFactory.getInstance().getObservationDAO();
            for (ObservationConstellation hObservationConstellation : hObservationConstellations) {
                if (sosObservation.getValue() instanceof SingleObservationValue) {
                    observationDAO.insertObservationSingleValue(
                            hObservationConstellation, hFeature, sosObservation,
                            cache.getCodespaceCache(), cache.getUnitCache(), getOfferings(hObservationConstellations), checkForDuplicatedObservations(), session);
                } else if (sosObservation.getValue() instanceof MultiObservationValues) {
                    observationDAO.insertObservationMultiValue(
                            hObservationConstellation, hFeature, sosObservation,
                            cache.getCodespaceCache(), cache.getUnitCache(), getOfferings(hObservationConstellations), checkForDuplicatedObservations(), session);
                }
            }
        }
    }

    protected void checkSpatialFilteringProfile(OmObservation sosObservation)
            throws CodedException {
        // checkConstellation
        if (ServiceConfiguration.getInstance().isStrictSpatialFilteringProfile()
            && !sosObservation.isSetSpatialFilteringProfileParameter()) {
            throw new MissingParameterValueException(Sos2Constants.InsertObservationParams.parameter)
                    .withMessage("The sampling geometry definition is missing in the observation because"
                            + " the Spatial Filtering Profile is specification conformant. To use a less"
                            + " restrictive Spatial Filtering Profile you can change this in the Service-Settings!");
        }
    }

    protected void handleHibernateException(HibernateException he)
            throws OwsExceptionReport {
        HTTPStatus status = HTTPStatus.INTERNAL_SERVER_ERROR;
        String exceptionMsg = "Error while inserting new observation!";

        if (he instanceof JDBCException) {
            if (he instanceof ConstraintViolationException) {
                final ConstraintViolationException cve = (ConstraintViolationException) he;
                checkEqualsAndThrow(cve.getConstraintName(), he);
                checkContainsAndThrow(cve.getMessage(), he);
            }
            SQLException sqle =((JDBCException) he).getSQLException();
            checkContainsAndThrow(sqle.getMessage(), he);
            // if this is a JDBCException, pass the underlying SQLException
            // as the causedBy exception so that we can show the actual error in the
            // OwsExceptionReport when batching
            CompositeOwsException e = new CompositeOwsException();
            for (Throwable next : sqle) {
                checkContainsAndThrow(next.getMessage(), he);
                e.add(new NoApplicableCodeException().causedBy(next));
            }
            throw e.setStatus(status);
        } else {
            throw new NoApplicableCodeException().causedBy(he).withMessage(exceptionMsg).setStatus(status);
        }
    }

    private void checkEqualsAndThrow(String constraintName, HibernateException he) throws OwsExceptionReport {
        if (StringHelper.isNotEmpty(constraintName)) {
            String exceptionMsg = null;
            if (constraintName.equalsIgnoreCase(CONSTRAINT_OBSERVATION_IDENTITY)) {
                exceptionMsg = "Observation with same values already contained in database";
            } else if (constraintName.equalsIgnoreCase(CONSTRAINT_OBSERVATION_IDENTIFIER_IDENTITY)) {
                exceptionMsg = "Observation identifier already contained in database";
            }
            if(StringHelper.isNotEmpty(exceptionMsg)) {
                throw new NoApplicableCodeException().causedBy(he).withMessage(exceptionMsg)
                .setStatus(HTTPStatus.BAD_REQUEST);
            }
        }
    }

    private void checkContainsAndThrow(String message, HibernateException he) throws OwsExceptionReport {
        if (StringHelper.isNotEmpty(message)) {
            String exceptionMsg = null;
            if (message.toLowerCase().contains(CONSTRAINT_OBSERVATION_IDENTITY.toLowerCase())) {
                exceptionMsg = "Observation with same values already contained in database";
            } else if (message.toLowerCase().contains(CONSTRAINT_OBSERVATION_IDENTIFIER_IDENTITY.toLowerCase())) {
                exceptionMsg = "Observation identifier already contained in database";
            }
            if (StringHelper.isNotEmpty(exceptionMsg)) {
                throw new NoApplicableCodeException().causedBy(he).withMessage(exceptionMsg)
                .setStatus(HTTPStatus.BAD_REQUEST);
            }
        }
    }

    /**
     * Get the hibernate FeatureOfInterest object for an AbstractFeature,
     * returning it from the local cache if already requested
     *
     * @param abstractFeature
     * @param cache
     * @param session
     * @return hibernate AbstractFeatureOfInterest
     * @throws OwsExceptionReport
     */
    private AbstractFeatureOfInterest getFeature(AbstractFeature abstractFeature,
            InsertObservationCache cache, Session session) throws OwsExceptionReport {
        AbstractFeatureOfInterest hFeature = cache.getFeature(abstractFeature);
        if (hFeature == null) {
            hFeature = featureOfInterestDAO.checkOrInsertFeatureOfInterest(abstractFeature, session);
            cache.putFeature(abstractFeature, hFeature);
        }
        if (!hFeature.isSetName() && abstractFeature.isSetName()) {
            featureOfInterestDAO.updateFeatureOfInterest(hFeature, abstractFeature, session);
        }
        return hFeature;
    }
    
    private boolean checkForDuplicatedObservations() {
        return ServiceConfiguration.getInstance().isCheckForDuplicatedObservations();
    }
    
    @Override
    public boolean isSupported() {
        return HibernateHelper.isEntitySupported(ValidProcedureTime.class);
    }

    private static class InsertObservationCache {
        private final Set<String> allOfferings = Sets.newHashSet();
        private final Map<AbstractFeature, AbstractFeatureOfInterest> featureCache = Maps.newHashMap();
        private final Table<OmObservationConstellation, String, ObservationConstellation> obsConstOfferingHibernateObsConstTable = HashBasedTable.create();
        private final Map<String, Codespace> codespaceCache = Maps.newHashMap();
        private final Map<UoM, Unit> unitCache = Maps.newHashMap();
        private final HashMultimap<OmObservationConstellation, String> obsConstOfferingCheckedMap = HashMultimap.create();
        private final HashMultimap<AbstractFeature, String> relatedFeatureCheckedMap = HashMultimap.create();


        public ObservationConstellation get(OmObservationConstellation oc, String offering) {
            return this.obsConstOfferingHibernateObsConstTable.get(oc, offering);
        }
        public void putConstellation(OmObservationConstellation soc, String offering, ObservationConstellation hoc) {
            this.obsConstOfferingHibernateObsConstTable.put(soc, offering, hoc);
        }
        public boolean isChecked(OmObservationConstellation oc, String offering) {
            return this.obsConstOfferingCheckedMap.containsEntry(oc, offering);
        }
        public boolean isChecked(AbstractFeature feature, String offering) {
            return this.relatedFeatureCheckedMap.containsEntry(feature, offering);
        }
        public void checkConstellation(OmObservationConstellation oc, String offering) {
            this.obsConstOfferingCheckedMap.put(oc, offering);
        }
        public void checkFeature(AbstractFeature feature, String offering) {
            this.relatedFeatureCheckedMap.put(feature, offering);
        }
        public void putFeature(AbstractFeature sfeature, AbstractFeatureOfInterest hfeature) {
            this.featureCache.put(sfeature, hfeature);
        }
        public AbstractFeatureOfInterest getFeature(AbstractFeature sfeature) {
            return this.featureCache.get(sfeature);
        }
        public Map<AbstractFeature, AbstractFeatureOfInterest> getFeatureCache() {
            return featureCache;
        }
        public Map<String, Codespace> getCodespaceCache() {
            return codespaceCache;
        }
        public Map<UoM, Unit> getUnitCache() {
            return unitCache;
        }
        public Set<String> getAllOfferings() {
            return allOfferings;
        }
        public void addOfferings(Collection<String> offerings) {
            this.allOfferings.addAll(offerings);
        }
    }
}