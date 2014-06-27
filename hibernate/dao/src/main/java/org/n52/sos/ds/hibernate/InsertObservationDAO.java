/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;

import org.n52.sos.ds.AbstractInsertObservationDAO;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.n52.sos.ds.hibernate.dao.AbstractObservationDAO;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.FeatureOfInterestDAO;
import org.n52.sos.ds.hibernate.dao.ObservationConstellationDAO;
import org.n52.sos.ds.hibernate.entities.Codespace;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.SpatialFilteringProfile;
import org.n52.sos.ds.hibernate.entities.Unit;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.exception.ows.MissingParameterValueException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
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
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.http.HTTPStatus;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

/**
 * Implementation of the abstract class AbstractInsertObservationDAO
 * @since 4.0.0
 *
 */
public class InsertObservationDAO extends AbstractInsertObservationDAO {
    private final HibernateSessionHolder sessionHolder = new HibernateSessionHolder();
    private final ObservationConstellationDAO observationConstellationDAO = new ObservationConstellationDAO();
    private final FeatureOfInterestDAO featureOfInterestDAO = new FeatureOfInterestDAO();

    private static final int FLUSH_THRESHOLD = 50;

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

        // TODO: check unit and set if available and not defined in DB
        try {
            session = sessionHolder.getSession();
            transaction = session.beginTransaction();
            final CompositeOwsException exceptions = new CompositeOwsException();
            final Set<String> allOfferings = Sets.newHashSet();
            allOfferings.addAll(request.getOfferings());

            //cache/tracking objects to avoid redundant queries
            Map<AbstractFeature,FeatureOfInterest> featureCache = Maps.newHashMap();
            Table<OmObservationConstellation,String,ObservationConstellation> obsConstOfferingHibernateObsConstTable =
                    HashBasedTable.create();
            Map<String,Codespace> codespaceCache = Maps.newHashMap();
            Map<String,Unit> unitCache = Maps.newHashMap();

            HashMultimap<OmObservationConstellation, String> obsConstOfferingCheckedMap = HashMultimap.create();
            HashMultimap<AbstractFeature,String> relatedFeatureCheckedMap = HashMultimap.create();

            //counter for batch flushing
            int obsCount = 0;

            for (final OmObservation sosObservation : request.getObservations()) {
                //check
                if (ServiceConfiguration.getInstance().isStrictSpatialFilteringProfile()
                        && HibernateHelper.isEntitySupported(SpatialFilteringProfile.class, session)
                        && !sosObservation.isSetSpatialFilteringProfileParameter()) {
                    throw new MissingParameterValueException(Sos2Constants.InsertObservationParams.parameter)
                            .withMessage("The sampling geometry definition is missing in the observation because"
                                    + " the Spatial Filtering Profile is specification conformant. To use a less"
                                    + " restrictive Spatial Filtering Profile you can change this in the Service-Settings!");
                }

                final OmObservationConstellation sosObsConst = sosObservation.getObservationConstellation();
                Set<String> offerings = getParentProcedureOfferings(sosObsConst);
                sosObsConst.setOfferings(offerings);
                allOfferings.addAll(offerings);

                final Set<ObservationConstellation> hObservationConstellations =
                        new HashSet<ObservationConstellation>(0);
                FeatureOfInterest hFeature = null;

                //TODO cache obsConst and feature (multi obs often have the same)

                for (final String offeringID : sosObsConst.getOfferings()) {
                    ObservationConstellation hObservationConstellation = obsConstOfferingHibernateObsConstTable.get(sosObsConst, offeringID);
                    if (hObservationConstellation == null) {
                        if (!obsConstOfferingCheckedMap.containsEntry(sosObsConst, offeringID)) {
                            try {
                                hObservationConstellation = observationConstellationDAO.checkObservationConstellation(sosObsConst, offeringID, session, Sos2Constants.InsertObservationParams.observationType.name());
                                //add to cache table
                                obsConstOfferingHibernateObsConstTable.put(sosObsConst, offeringID, hObservationConstellation);
                            } catch (final OwsExceptionReport owse) {
                                exceptions.add(owse);
                            }
                            //mark as checked
                            obsConstOfferingCheckedMap.put(sosObsConst, offeringID);
                        }
                    }
                    if (hObservationConstellation != null) {
                        //get feature from local cache or create if necessary
                        hFeature = getFeature(sosObsConst.getFeatureOfInterest(), featureCache, session);

                        //only do feature checking once for each AbstractFeature/offering combo
                        if (!relatedFeatureCheckedMap.containsEntry(sosObsConst.getFeatureOfInterest(), offeringID)) {
                            featureOfInterestDAO.checkOrInsertFeatureOfInterestRelatedFeatureRelation(hFeature, hObservationConstellation.getOffering(), session);
                            relatedFeatureCheckedMap.put(sosObsConst.getFeatureOfInterest(), offeringID);
                        }

                        hObservationConstellations.add(hObservationConstellation);
                    }
                }

                if (!hObservationConstellations.isEmpty()) {
                    final AbstractObservationDAO observationDAO = DaoFactory.getInstance().getObservationDAO(session);
                    if (sosObservation.getValue() instanceof SingleObservationValue) {
                        observationDAO.insertObservationSingleValue(hObservationConstellations, hFeature, sosObservation,
                                codespaceCache, unitCache, session);
                    } else if (sosObservation.getValue() instanceof MultiObservationValues) {
                        observationDAO.insertObservationMultiValue(hObservationConstellations, hFeature, sosObservation,
                                codespaceCache, unitCache, session);
                    }
                }

                //flush every FLUSH_INTERVAL
                if (++obsCount % FLUSH_THRESHOLD == 0) {
                    session.flush();
                    session.clear();
                }
            }
            request.setOfferings(Lists.newArrayList(allOfferings));
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
            HTTPStatus status = HTTPStatus.INTERNAL_SERVER_ERROR;
            final String exceptionMsg = "Error while inserting new observation!";
            if (he instanceof ConstraintViolationException) {
                final ConstraintViolationException cve = (ConstraintViolationException) he;
                /*
                 * if (cve.getConstraintName() != null) { if
                 * (cve.getConstraintName
                 * ().equalsIgnoreCase(CONSTRAINT_OBSERVATION_IDENTITY)) {
                 * exceptionMsg =
                 * "Observation with same values already contained in database";
                 * } else if (cve.getConstraintName().equalsIgnoreCase(
                 * CONSTRAINT_OBSERVATION_IDENTIFIER_IDENTITY)) { exceptionMsg =
                 * "Observation identifier already contained in database"; } }
                 * else if (cve.getMessage() != null) { if
                 * (cve.getMessage().contains(CONSTRAINT_OBSERVATION_IDENTITY))
                 * { exceptionMsg =
                 * "Observation with same values already contained in database";
                 * exceptionMsg =
                 * "Observation identifier already contained in database"; }
                 *
                 * }
                 */

                status = HTTPStatus.BAD_REQUEST;
            }

            // if this is a JDBCException, pass the underlying SQLException as the causedBy exception
            // so that we can show the actual error in the OwsExceptionReport when batching
            if (he instanceof JDBCException) {
                SQLException sqle = ((JDBCException) he).getSQLException();
                CompositeOwsException  e = new CompositeOwsException();
                for (Throwable next : sqle) {
                    e.add(new NoApplicableCodeException().causedBy(next));
                }
                throw e.setStatus(status);
            } else {
                throw new NoApplicableCodeException().causedBy(he).withMessage(exceptionMsg).setStatus(status);
            }
        } finally {
            sessionHolder.returnSession(session);
        }
        /*
         * TODO: ... all the DS insertion stuff Requirement 68
         * proc/obsProp/Offering same obsType;
         */

        return response;
    }

    /**
     * Get the hibernate FeatureOfInterest object for an AbstractFeature, returning it from the local cache if already requested
     * @param sosObsConst
     * @param featureCache
     * @param session
     * @return hibernet FeatureOfInterest
     * @throws OwsExceptionReport
     */
    private FeatureOfInterest getFeature(AbstractFeature abstractFeature,
            Map<AbstractFeature,FeatureOfInterest> featureCache, Session session) throws OwsExceptionReport {
        FeatureOfInterest hFeature = featureCache.get(abstractFeature);
        if (hFeature == null) {
            hFeature = featureOfInterestDAO.checkOrInsertFeatureOfInterest(abstractFeature, session);
            featureCache.put(abstractFeature, hFeature);
        }
        return hFeature;
    }

    /**
     * Get parent offerings for requested procedure and observable property
     *
     * @param sosObsConst
     *            Requested observation constellation
     * @return Requested offering and valid parent procedure offerings.
     */
    private Set<String> getParentProcedureOfferings(OmObservationConstellation sosObsConst) {
        Set<String> offerings = Sets.newHashSet(sosObsConst.getOfferings());
        // get parent procedures
        Set<String> parentProcedures =
                getCache().getParentProcedures(sosObsConst.getProcedure().getIdentifier(), true, false);
        if (CollectionHelper.isNotEmpty(parentProcedures)) {
            for (String parentProcedure : parentProcedures) {
                // get offerings for parent procdure
                Set<String> offeringsForParentProcedure = getCache().getOfferingsForProcedure(parentProcedure);
                if (CollectionHelper.isNotEmpty(offeringsForParentProcedure)) {
                    for (String offering : offeringsForParentProcedure) {
                        /*
                         * get observable properties for offering and check if
                         * observable property is contained in request and if
                         * parent procedure offering is contained in procedure
                         * offerings. If true, add offering to set.
                         */
                        Set<String> observablePropertiesForOffering =
                                getCache().getObservablePropertiesForOffering(offering);
                        Set<String> offeringsForProcedure =
                                getCache().getOfferingsForProcedure(sosObsConst.getProcedure().getIdentifier());
                        if (CollectionHelper.isNotEmpty(observablePropertiesForOffering)
                                && observablePropertiesForOffering.contains(sosObsConst.getObservableProperty()
                                        .getIdentifier()) && offeringsForProcedure.contains(offering)) {
                            offerings.add(offering);
                        }
                    }
                }
            }
        }
        return offerings;
    }
}