/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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

import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.janmayen.http.HTTPStatus;
import org.n52.janmayen.lifecycle.Constructable;
import org.n52.series.db.beans.AbstractFeatureEntity;
import org.n52.series.db.beans.CodespaceEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.FormatEntity;
import org.n52.series.db.beans.ProcedureHistoryEntity;
import org.n52.series.db.beans.UnitEntity;
import org.n52.series.db.beans.dataset.DatasetType;
import org.n52.shetland.ogc.UoM;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.om.MultiObservationValues;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.OmObservationConstellation;
import org.n52.shetland.ogc.om.SingleObservationValue;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.CompositeOwsException;
import org.n52.shetland.ogc.ows.exception.MissingParameterValueException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.InsertObservationRequest;
import org.n52.shetland.ogc.sos.response.InsertObservationResponse;
import org.n52.sos.ds.AbstractInsertObservationHandler;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.observation.AbstractObservationDAO;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.TransactionHelper;
import org.n52.sos.service.SosSettings;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

@Configurable
public class InsertObservationHandler extends AbstractInsertObservationHandler
        implements Constructable, TransactionHelper {
    private static final int FLUSH_THRESHOLD = 50;

    private static final String CONSTRAINT_OBSERVATION_IDENTITY = "observationIdentity";

    private static final String CONSTRAINT_OBSERVATION_IDENTIFIER_IDENTITY = "obsIdentifierUK";

    private static final String LOG_OBSERVATION_SAME_VALUES =
            "Observation with same values already contained in database";

    private static final String LOG_OBSERVATION_SAME_IDENTIFIER =
            "Observation identifier already contained in database";

    private static final String LOG_SAMPLING_GEOMETRY =
            "The sampling geometry definition is missing in the observation because"
                    + " the Spatial Filtering Profile is specification conformant. To use a less"
                    + " restrictive Spatial Filtering Profile you can change this in the Service-Settings!";

    @Inject
    private ConnectionProvider connectionProvider;

    @Inject
    private DaoFactory daoFactory;

    private HibernateSessionHolder sessionHolder;

    private boolean strictSpatialFilteringProfile;

    /**
     * constructor
     */
    public InsertObservationHandler() {
        super(SosConstants.SOS);
    }

    @Override
    public void init() {
        this.sessionHolder = new HibernateSessionHolder(connectionProvider);
    }

    @Setting(SosSettings.STRICT_SPATIAL_FILTERING_PROFILE)
    public synchronized void setStrictSpatialFilteringProfile(final boolean strictSpatialFilteringProfile) {
        this.strictSpatialFilteringProfile = strictSpatialFilteringProfile;
    }

    public synchronized boolean isStrictSpatialFilteringProfile() {
        return strictSpatialFilteringProfile;
    }

    @Override
    public boolean isSupported() {
        return HibernateHelper.isEntitySupported(ProcedureHistoryEntity.class);
    }

    @Override
    public synchronized InsertObservationResponse insertObservation(final InsertObservationRequest request)
            throws OwsExceptionReport {
        final InsertObservationResponse response = new InsertObservationResponse();
        response.setService(request.getService());
        response.setVersion(request.getVersion());
        Session session = null;
        Transaction transaction = null;

        // TODO: checkConstellation unit and set if available and not defined in
        // DB
        try {
            session = getHibernateSessionHolder().getSession();
            transaction = getTransaction(session);

            CompositeOwsException exceptions = new CompositeOwsException();
            InsertObservationCache cache = new InsertObservationCache();

            cache.addOfferings(request.getOfferings());

            // counter for batch flushing
            int obsCount = 0;

            for (final OmObservation sosObservation : request.getObservations()) {
                // check strict spatial filtering profile
                if (isStrictSpatialFilteringProfile() && !sosObservation.isSetSpatialFilteringProfileParameter()) {
                    throw new MissingParameterValueException(Sos2Constants.InsertObservationParams.parameter)
                            .withMessage(LOG_SAMPLING_GEOMETRY);
                }

                insertObservation(sosObservation, cache, exceptions, session);

                // flush every FLUSH_INTERVAL
                if (++obsCount % FLUSH_THRESHOLD == 0) {
                    session.flush();
                    session.clear();
                    cache.clearConstellation();
                }
            }

            request.setOfferings(Lists.newArrayList(cache.getAllOfferings()));

            // if no observationConstellation is valid, throw exception
            if (exceptions.size() == request.getObservations().size()) {
                throw exceptions;
            }

            session.flush();
            transaction.commit();
        } catch (PersistenceException pe) {
            if (transaction != null) {
                transaction.rollback();
            }
            handleHibernateException(pe);
        } finally {
            getHibernateSessionHolder().returnSession(session);
        }
        /*
         * TODO: ... all the DS insertion stuff Requirement 68 proc/obsProp/Offering same obsType;
         */

        return response;
    }

    private void insertObservation(OmObservation sosObservation, InsertObservationCache cache,
            CompositeOwsException exceptions, Session session) throws OwsExceptionReport, CodedException {

        checkSpatialFilteringProfile(sosObservation);

        OmObservationConstellation sosObsConst = sosObservation.getObservationConstellation();
        cache.addOfferings(sosObsConst.getOfferings());

        AbstractFeatureEntity hFeature = null;

        // if (sosObsConst.getOfferings().size() > 1) {
        //
        // }

        String offeringID = sosObsConst.getOfferings().iterator().next();
        DatasetEntity hDataset = cache.get(sosObsConst, offeringID);
        if (hDataset == null) {
            if (!cache.isChecked(sosObsConst, offeringID)) {
                try {
                    hDataset = getDaoFactory().getSeriesDAO().checkSeries(sosObsConst, offeringID, session,
                            Sos2Constants.InsertObservationParams.observationType.name());
                    // add to cache table
                    cache.putConstellation(sosObsConst, offeringID, hDataset);
                } catch (OwsExceptionReport owse) {
                    exceptions.add(owse);
                }
                // mark as checked
                cache.checkConstellation(sosObsConst, offeringID);
            }
        }
        if (hDataset != null) {
            // getFeature feature from local cache or create if necessary
            hFeature = getFeature(sosObsConst.getFeatureOfInterest(), cache, session);

            // only do feature checking once for each
            // AbstractFeature/offering combo
            if (!cache.isChecked(sosObsConst.getFeatureOfInterest(), offeringID)) {
                getDaoFactory().getFeatureOfInterestDAO().checkOrInsertRelatedFeatureRelation(hFeature,
                        hDataset.getOffering(), session);
                cache.checkFeature(sosObsConst.getFeatureOfInterest(), offeringID);
            }
            AbstractObservationDAO observationDAO = getDaoFactory().getObservationDAO();
            DatasetEntity dataset = null;
            if (sosObservation.getValue() instanceof SingleObservationValue) {
                dataset = observationDAO.insertObservationSingleValue(hDataset, hFeature, sosObservation,
                        cache.getCodespaceCache(), cache.getUnitCache(), cache.getFormatCache(), session);
            } else if (sosObservation.getValue() instanceof MultiObservationValues) {
                dataset = observationDAO.insertObservationMultiValue(hDataset, hFeature, sosObservation,
                        cache.getCodespaceCache(), cache.getUnitCache(), cache.getFormatCache(), session);
            }
            if (dataset != null && cache.containsConstellation(sosObsConst, offeringID)
                    && !cache.get(sosObsConst, offeringID).equals(dataset)) {
                cache.putConstellation(sosObsConst, offeringID, dataset);
            }
        }
    }

    protected void checkSpatialFilteringProfile(OmObservation sosObservation) throws CodedException {
        // checkConstellation
        if (isStrictSpatialFilteringProfile() && !sosObservation.isSetSpatialFilteringProfileParameter()) {
            throw new MissingParameterValueException(Sos2Constants.InsertObservationParams.parameter)
                    .withMessage(LOG_SAMPLING_GEOMETRY);
        }
    }

    protected void handleHibernateException(PersistenceException pe) throws OwsExceptionReport {
        HTTPStatus status = HTTPStatus.INTERNAL_SERVER_ERROR;
        String exceptionMsg = "Error while inserting new observation!";

        if (pe instanceof ConstraintViolationException) {
            handleConstraintViolationException((ConstraintViolationException) pe, pe, status);
        } else if (pe.getCause() instanceof ConstraintViolationException) {
            handleConstraintViolationException((ConstraintViolationException) pe.getCause(), pe, status);
        } else {
            throw new NoApplicableCodeException().causedBy(pe).withMessage(exceptionMsg).setStatus(status);
        }
    }

    private void handleConstraintViolationException(ConstraintViolationException cve, PersistenceException pe,
            HTTPStatus status) throws OwsExceptionReport {
        CompositeOwsException ce = new CompositeOwsException();
        checkEqualsAndThrow(cve.getConstraintName(), pe);
        checkContainsAndThrow(cve.getMessage(), pe);
        SQLException sqle = cve.getSQLException();
        checkContainsAndThrow(sqle.getMessage(), pe);
        // if this is a JDBCException, pass the underlying SQLException
        // as the causedBy exception so that we can show the actual error in
        // the
        // OwsExceptionReport when batching
        for (Throwable next : sqle) {
            checkContainsAndThrow(next.getMessage(), pe);
            ce.add(new NoApplicableCodeException().causedBy(next));
        }
        throw ce.setStatus(status);
    }

    private void checkEqualsAndThrow(String constraintName, PersistenceException e) throws OwsExceptionReport {
        if (!Strings.isNullOrEmpty(constraintName)) {
            String exceptionMsg = "";
            if (constraintName.equalsIgnoreCase(CONSTRAINT_OBSERVATION_IDENTITY)) {
                exceptionMsg = LOG_OBSERVATION_SAME_VALUES;
            } else if (constraintName.equalsIgnoreCase(CONSTRAINT_OBSERVATION_IDENTIFIER_IDENTITY)) {
                exceptionMsg = LOG_OBSERVATION_SAME_IDENTIFIER;
            }
            if (!Strings.isNullOrEmpty(exceptionMsg)) {
                throw new NoApplicableCodeException().causedBy(e).withMessage(exceptionMsg)
                        .setStatus(HTTPStatus.BAD_REQUEST);
            }
        }
    }

    private void checkContainsAndThrow(String message, PersistenceException e) throws OwsExceptionReport {
        if (!Strings.isNullOrEmpty(message)) {
            String exceptionMsg = "";
            if (message.toLowerCase().contains(CONSTRAINT_OBSERVATION_IDENTITY.toLowerCase())) {
                exceptionMsg = LOG_OBSERVATION_SAME_VALUES;
            } else if (message.toLowerCase().contains(CONSTRAINT_OBSERVATION_IDENTIFIER_IDENTITY.toLowerCase())) {
                exceptionMsg = LOG_OBSERVATION_SAME_IDENTIFIER;
            }
            if (!Strings.isNullOrEmpty(exceptionMsg)) {
                throw new NoApplicableCodeException().causedBy(e).withMessage(exceptionMsg)
                        .setStatus(HTTPStatus.BAD_REQUEST);
            }
        }
    }

    /**
     * Get the hibernate AbstractFeatureOfInterest object for an AbstractFeature, returning it from the local
     * cache if already requested
     *
     * @param abstractFeature
     *            the abstract features
     * @param cache
     *            THe insertion cache
     * @param session
     *            Hiberante session
     * @return hibernet AbstractFeatureOfInterest
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private AbstractFeatureEntity getFeature(AbstractFeature abstractFeature, InsertObservationCache cache,
            Session session) throws OwsExceptionReport {
        AbstractFeatureEntity hFeature = cache.getFeature(abstractFeature);
        if (hFeature == null) {
            hFeature = getDaoFactory().getFeatureOfInterestDAO().checkOrInsert(abstractFeature, session);
            cache.putFeature(abstractFeature, hFeature);
        }
        return hFeature;
    }

    private synchronized DaoFactory getDaoFactory() {
        return daoFactory;
    }

    private synchronized HibernateSessionHolder getHibernateSessionHolder() {
        return sessionHolder;
    }

    @VisibleForTesting
    protected synchronized void initForTesting(DaoFactory daoFactory, ConnectionProvider connectionProvider) {
        this.daoFactory = daoFactory;
        this.connectionProvider = connectionProvider;
    }

    private static class InsertObservationCache {
        private final Set<String> allOfferings = Sets.newHashSet();

        private final Map<AbstractFeature, AbstractFeatureEntity> featureCache = Maps.newHashMap();

        private final Table<OmObservationConstellation, String, DatasetEntity> obsConstOfferingDatasetTable =
                HashBasedTable.create();

        private final Map<String, CodespaceEntity> codespaceCache = Maps.newHashMap();

        private final Map<UoM, UnitEntity> unitCache = Maps.newHashMap();

        private final Map<String, FormatEntity> formatCache = Maps.newHashMap();

        private final HashMultimap<OmObservationConstellation, String> obsConstOfferingCheckedMap =
                HashMultimap.create();

        private final HashMultimap<AbstractFeature, String> relatedFeatureCheckedMap = HashMultimap.create();

        public DatasetEntity get(OmObservationConstellation oc, String offering) {
            return this.obsConstOfferingDatasetTable.get(oc, offering);
        }

        public boolean containsConstellation(OmObservationConstellation oc, String offering) {
            return this.obsConstOfferingDatasetTable.contains(oc, offering);
        }

        public void putConstellation(OmObservationConstellation soc, String offering, DatasetEntity hoc) {
            this.obsConstOfferingDatasetTable.put(soc, offering, hoc);
        }

        public void clearConstellation() {
            Set<Cell<OmObservationConstellation, String, DatasetEntity>> removable = new HashSet<>();
            for (Cell<OmObservationConstellation, String, DatasetEntity> cell : this.obsConstOfferingDatasetTable
                    .cellSet()) {
                DatasetEntity value = cell.getValue();
                if (value != null && DatasetType.not_initialized.equals(value.getDatasetType())) {
                    removable.add(cell);
                }
            }
            for (Cell<OmObservationConstellation, String, DatasetEntity> cell : removable) {
                this.obsConstOfferingDatasetTable.remove(cell.getRowKey(), cell.getColumnKey());
            }
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

        public void putFeature(AbstractFeature sfeature, AbstractFeatureEntity hfeature) {
            getFeatureCache().put(sfeature, hfeature);
        }

        public AbstractFeatureEntity getFeature(AbstractFeature sfeature) {
            return getFeatureCache().get(sfeature);
        }

        public Map<AbstractFeature, AbstractFeatureEntity> getFeatureCache() {
            return featureCache;
        }

        public Map<String, CodespaceEntity> getCodespaceCache() {
            return codespaceCache;
        }

        public Map<UoM, UnitEntity> getUnitCache() {
            return unitCache;
        }

        public Map<String, FormatEntity> getFormatCache() {
            return formatCache;
        }

        public Set<String> getAllOfferings() {
            return allOfferings;
        }

        public void addOfferings(Collection<String> offerings) {
            this.allOfferings.addAll(offerings);
        }
    }
}
