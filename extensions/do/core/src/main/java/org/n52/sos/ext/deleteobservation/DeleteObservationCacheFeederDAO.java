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
package org.n52.sos.ext.deleteobservation;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.sos.ds.DatasourceCacheUpdate;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.features.FeatureCollection;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosEnvelope;
import org.n52.sos.util.CacheHelper;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Updates the cache after a Observation was deleted. Uses the deleted
 * observation to determine which cache relations have to be updated.
 * <p/>
 *
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 1.0.0
 */
public abstract class DeleteObservationCacheFeederDAO extends DatasourceCacheUpdate {
    protected static final Logger log = LoggerFactory.getLogger(DeleteObservationCacheFeederDAO.class);

    /**
     * Maximal difference between double values to consider them "equal".
     */
    protected static final double EPSILON = .000001;

    /**
     * Boolean to keep track if we already updated the global bounding box.
     */
    private boolean globalSpatialBoundingBoxUpdated = false;

    /**
     * The deleted observation.
     */
    private OmObservation o;

    /**
     * Set of offering identifiers to keep track for which offerings we already
     * updated the spatial bounding box.
     */
    private final Set<String> updatedOfferingBoundingBoxes = new HashSet<String>(0);

    public void setDeletedObservation(OmObservation deletedObservation) {
        this.o = deletedObservation;
    }

    public OmObservation getDeletedObservation() {
        return this.o;
    }

    /**
     * Translates the dbFeature identifiers to database dbFeature identifiers
     * and queries the FeatureQueryHandler for the envelope.
     *
     * @param features
     *            the dbFeature identifiers
     *
     * @return the envelope for the identifiers
     *
     * @throws OwsExceptionReport
     *             if the FeatureQueryHandler fails
     */
    protected SosEnvelope getEnvelope(Set<String> features) throws OwsExceptionReport {
        final Set<String> dbFeatures = new HashSet<String>(features.size());
        for (String feature : features) {
            dbFeatures.add(CacheHelper.removePrefixAndGetFeatureIdentifier(feature));
        }
        return getFeatureQueryHandler().getEnvelopeForFeatureIDs(dbFeatures, getConnection());
    }

    /**
     * Check if the two envelopes have common edges. If the geometry represented
     * by {@code e1} (or {@code e2}) is removed from a collection of geometries
     * represented by {@code e2} (or {@code e1}), {@code e2} (or {@code e2}) has
     * to be updated.
     *
     * @param e1
     *            the first envelope
     * @param e2
     *            the second envelope
     *
     * @return {@code true} if the envelopes have to be updated
     */
    protected boolean isCritical(Envelope e1, Envelope e2) {
        return e1 != null
                && e2 != null
                && (e1.getMaxX() - e2.getMaxX() < EPSILON || e1.getMinX() - e2.getMinX() < EPSILON
                        || e1.getMaxY() - e2.getMaxY() < EPSILON || e1.getMinY() - e2.getMinY() < EPSILON);
    }

    @Override
    public void execute() {
        try {
            prepare();
            updateFeatureOfInterest();
            updateTemporalBoundingBoxes();
            updateSpatialBoundingBoxes();
        } catch (OwsExceptionReport ex) {
            getErrors().add(ex);
        } finally {
            cleanup();
        }
    }

    /**
     * Disassociates the feature of interest from the procedure and offerings if
     * there are no observations left.
     * @throws CodedException
     */
    protected void updateFeatureOfInterest() throws OwsExceptionReport {
        final String feature = o.getObservationConstellation().getFeatureOfInterest().getIdentifierCodeWithAuthority().getValue();
        final String procedure = o.getObservationConstellation().getProcedure().getIdentifier();
        final String dbFeature = CacheHelper.removePrefixAndGetFeatureIdentifier(feature);
        final String dbProcedure = CacheHelper.removePrefixAndGetProcedureIdentifier(procedure);
        if (isLastForProcedure(dbFeature, dbProcedure)) {
            getCache().removeProcedureForFeatureOfInterest(feature, procedure);
        }
        for (String offering : o.getObservationConstellation().getOfferings()) {
            final String dbOffering = CacheHelper.removePrefixAndGetOfferingIdentifier(offering);
            if (isLastForOffering(dbFeature, dbOffering)) {
                getCache().removeFeatureOfInterestForOffering(offering, feature);
            }
        }
    }

    /**
     * Update the spatial bounding boxes for the deleted observation.
     * <p/>
     * This method will use the cache for dbFeature identifiers. These have to
     * be updated beforehand.
     *
     * @throws OwsExceptionReport
     *             if the dbFeature of interest is not supported or the
     *             FeatureQueryHandler fails.
     */
    protected void updateSpatialBoundingBoxes() throws OwsExceptionReport {
        updateSpatialBoundingBoxes(o.getObservationConstellation().getFeatureOfInterest());
    }

    /**
     * Update the global and offering specific spatial bounding box for the
     * specified dbFeature of interest. The update is conditionally executed if
     * the envelope of the dbFeature and the cached envelope share a edge. The
     * method will recursively check containing features if
     * {@code featureOfInterest} is a {@link FeatureCollection} while keeping
     * track which offerings are already updated.
     * <p/>
     * This method will use the cache for dbFeature identifiers. These have to
     * be updated beforehand.
     *
     * @param featureOfInterest
     *            the dbFeature to check
     *
     * @throws OwsExceptionReport
     *             if the FeatureQueryHandler fails
     */
    protected void updateSpatialBoundingBoxes(AbstractFeature featureOfInterest) throws OwsExceptionReport {
        if (featureOfInterest instanceof SamplingFeature) {
            final SamplingFeature ssf = (SamplingFeature) featureOfInterest;
            if (ssf.getGeometry() != null) {
                if (!globalSpatialBoundingBoxUpdated
                        && getCache().getGlobalEnvelope() != null
                        && isCritical(ssf.getGeometry().getEnvelopeInternal(), getCache().getGlobalEnvelope()
                                .getEnvelope())) {
                    log.debug("Updating global spatial bounding box");
                    globalSpatialBoundingBoxUpdated = true;
                    getCache().setGlobalEnvelope(getEnvelope(getCache().getFeaturesOfInterest()));
                }
                for (String offering : o.getObservationConstellation().getOfferings()) {
                    if (!updatedOfferingBoundingBoxes.contains(offering)
                            && getCache().getEnvelopeForOffering(offering) != null
                            && getCache().getEnvelopeForOffering(offering).getEnvelope() != null
                            && isCritical(ssf.getGeometry().getEnvelopeInternal(),
                                    getCache().getEnvelopeForOffering(offering).getEnvelope())) {
                        log.debug("Updating spatial bounding box for offering {}", offering);
                        updatedOfferingBoundingBoxes.add(offering);
                        getCache().setEnvelopeForOffering(offering,
                                getEnvelope(getCache().getFeaturesOfInterestForOffering(offering)));
                    }
                }
            }
        } else if (featureOfInterest instanceof FeatureCollection) {
            final FeatureCollection sfc = (FeatureCollection) featureOfInterest;
            for (AbstractFeature saf : sfc.getMembers().values()) {
                updateSpatialBoundingBoxes(saf);
            }
        } else {
            throw new NoApplicableCodeException().withMessage("Unsupported feature type: %s", featureOfInterest);
        }
    }

    /**
     * Update the global and offering specific temporal bounding boxes. The
     * updates are conditional: the database is only queried if the observation
     * bounding boxes touch the cached bounding boxes.
     * @throws CodedException
     */
    protected void updateTemporalBoundingBoxes() throws OwsExceptionReport {
        DateTime minPhenomenonTime = null;
        DateTime maxPhenomenonTime = null;
        DateTime resultTime = null;
        if (o.getPhenomenonTime() != null) {
            if (o.getPhenomenonTime() instanceof TimeInstant) {
                minPhenomenonTime = maxPhenomenonTime = ((TimeInstant) o.getPhenomenonTime()).getValue();
            } else {
                minPhenomenonTime = ((TimePeriod) o.getPhenomenonTime()).getStart();
                maxPhenomenonTime = ((TimePeriod) o.getPhenomenonTime()).getEnd();
            }
            DateTime cachedMin = getCache().getMinPhenomenonTime();
            if (cachedMin != null && cachedMin.equals(minPhenomenonTime)) {
                log.debug("Updating global minimal phenomenon time");
                getCache().setMinPhenomenonTime(getMinPhenomenonTime());
            }
            DateTime cachedMax = getCache().getMaxPhenomenonTime();
            if (cachedMax != null && cachedMax.equals(maxPhenomenonTime)) {
                log.debug("Updating global maximal phenomenon time");
                getCache().setMaxPhenomenonTime(getMaxPhenomenonTime());
            }
        }
        if (o.getResultTime() != null) {
            resultTime = o.getResultTime().getValue();
            DateTime cachedMin = getCache().getMinResultTime();
            if (cachedMin != null && cachedMin.equals(resultTime)) {
                log.debug("Updating global minimal result time");
                getCache().setMinResultTime(getMinResultTime());
            }
            DateTime cachedMax = getCache().getMaxResultTime();
            if (cachedMax != null && cachedMax.equals(resultTime)) {
                log.debug("Updating global maximal result time");
                getCache().setMaxResultTime(getMaxResultTime());
            }
        }

        String procedure = o.getObservationConstellation().getProcedure().getIdentifier();
        DateTime minPhenomenonTimeForProcedure = getCache().getMinPhenomenonTimeForProcedure(procedure);
        if (minPhenomenonTimeForProcedure != null && minPhenomenonTimeForProcedure.equals(minPhenomenonTime)) {
            log.debug("Updating minimal phenomenon time for procedure {}", procedure);
            getCache().setMinPhenomenonTimeForProcedure(procedure, getMinDateForProcedure(procedure));
        }
        DateTime maxPhenomenonTimeForProcedure = getCache().getMaxPhenomenonTimeForProcedure(procedure);
        if (maxPhenomenonTimeForProcedure != null && maxPhenomenonTimeForProcedure.equals(maxPhenomenonTime)) {
            log.debug("Updating maximal phenomenon time for procedure {}", procedure);
            getCache().setMaxPhenomenonTimeForProcedure(procedure, getMaxDateForProcedure(procedure));
        }

        for (String offering : o.getObservationConstellation().getOfferings()) {
            DateTime minPhenomenonTimeForOffering = getCache().getMinPhenomenonTimeForOffering(offering);
            final String dsOffering = CacheHelper.removePrefixAndGetOfferingIdentifier(offering);
            if (minPhenomenonTimeForOffering != null && minPhenomenonTimeForOffering.equals(minPhenomenonTime)) {
                log.debug("Updating minimal phenomenon time for offering {}", offering);
                getCache().setMinPhenomenonTimeForOffering(offering, getMinDateForOffering(dsOffering));
            }
            DateTime maxPhenomenonTimeForOffering = getCache().getMaxPhenomenonTimeForOffering(offering);
            if (maxPhenomenonTimeForOffering != null && maxPhenomenonTimeForOffering.equals(maxPhenomenonTime)) {
                log.debug("Updating maximal phenomenon time for offering {}", offering);
                getCache().setMaxPhenomenonTimeForOffering(offering, getMaxDateForOffering(dsOffering));
            }
            DateTime minResultTimeForOffering = getCache().getMinResultTimeForOffering(offering);
            if (minResultTimeForOffering != null && minResultTimeForOffering.equals(resultTime)) {
                log.debug("Updating minimal result time for offering {}", offering);
                getCache().setMinResultTimeForOffering(offering, getMinResultTimeForOffering(dsOffering));
            }
            DateTime maxResultTimeForOffering = getCache().getMaxResultTimeForOffering(offering);
            if (maxResultTimeForOffering != null && maxResultTimeForOffering.equals(resultTime)) {
                log.debug("Updating maximal result time for offering {}", offering);
                getCache().setMaxResultTimeForOffering(offering, getMaxResultTimeForOffering(dsOffering));
            }
        }
    }

    /**
     * Checks if there is no observation with the specified offering/feature
     * combination.
     *
     * @param feature
     *            the feature identifier
     * @param offering
     *            the offering identifier
     *
     * @return if there is no observation with the specified dbFeature and
     *         offering
     */
    protected abstract boolean isLastForOffering(String feature, String offering) throws OwsExceptionReport;

    /**
     * Check if there is no observation with the specified dbProcedure/feature
     * combination.
     *
     * @param feature
     *            the feature identifier
     * @param procedure
     *            the procedure identifier
     *
     * @return if there is no observation with the specified dbFeature and
     *         dbProcedure.
     * @throws CodedException
     */
    protected abstract boolean isLastForProcedure(String feature, String procedure) throws OwsExceptionReport;

    protected abstract DateTime getMaxDateForOffering(String offering) throws OwsExceptionReport;

    protected abstract DateTime getMinDateForOffering(String offering) throws OwsExceptionReport;

    protected abstract DateTime getMaxDateForProcedure(String procedure) throws OwsExceptionReport;

    protected abstract DateTime getMinDateForProcedure(String procedure) throws OwsExceptionReport;

    protected abstract DateTime getMaxResultTime();

    protected abstract DateTime getMinResultTime();

    protected abstract DateTime getMaxPhenomenonTime();

    protected abstract DateTime getMinPhenomenonTime();

    protected abstract DateTime getMaxResultTimeForOffering(String offering) throws OwsExceptionReport;

    protected abstract DateTime getMinResultTimeForOffering(String offering) throws OwsExceptionReport;

    /**
     * Will be called before the update starts.
     *
     * @throws OwsExceptionReport
     *             if an error occures during preperation
     */
    protected abstract void prepare() throws OwsExceptionReport;

    /**
     * Will be called after the update is finished. Regardless if an
     * {@link OwsExceptionReport} is thrown while updating or not.
     */
    protected abstract void cleanup();

    protected abstract Object getConnection();
}
