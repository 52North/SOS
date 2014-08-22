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
package org.n52.sos.ds.hibernate.util.observation;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.n52.sos.cache.ContentCache;
import org.n52.sos.ds.hibernate.entities.AbstractSpatialFilteringProfile;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.om.values.GeometryValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosEnvelope;
import org.n52.sos.ogc.sos.SosOffering;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.JTSHelper;

import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Adds Spatial Filtering Profile data to {@code OmObservation}
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 *
 */
@Deprecated
public class SpatialFilteringProfileAdder {

    private boolean strictSpatialFilteringProfile;

    private int default3DEPSG;

    private int defaultEPSG;

    private Map<Long, AbstractSpatialFilteringProfile> spatialFilteringProfiles;

    /**
     * constructor
     */
    public SpatialFilteringProfileAdder() {
        init();
        this.spatialFilteringProfiles = Collections.emptyMap();
    }

    /**
     * constructor
     * 
     * @param spatialFilteringProfiles
     *            Observation id/Spatial Filtering Profile map
     */
    public SpatialFilteringProfileAdder(Map<Long, AbstractSpatialFilteringProfile> spatialFilteringProfiles) {
        init();
        setSpatialFilteringProfiles(spatialFilteringProfiles);

    }

    /**
     * Add Spatial Filtering Profile mapped with this observation id to
     * {@link OmObservation}
     * 
     * @param oId
     *            Observation id mapped with Spatial Filtering Profile
     * @param observation
     *            {@link OmObservation} to add Spatial Filtering Profile
     * @throws OwsExceptionReport
     *             if an error occurs when creating Spatial Filtering Profile
     */
    public void add(Long oId, OmObservation observation) throws OwsExceptionReport {
        if (spatialFilteringProfiles.containsKey(oId)) {
            AbstractSpatialFilteringProfile sfp = spatialFilteringProfiles.get(oId);
            observation.addParameter(createSpatialFilteringProfileParameter(sfp));
        } else if (strictSpatialFilteringProfile) {
            observation.addParameter(createSpatialFilteringProfileParameterForConstellation(observation
                    .getObservationConstellation()));
        }
    }

    /**
     * Add this Spatial Filtering Profile to {@link OmObservation}
     * 
     * @param sfp
     *            Spatial Filtering Profile to add
     * @param observation
     *            {@link OmObservation}
     * @throws OwsExceptionReport
     *             if an error occurs when creating Spatial Filtering Profile
     */
    public void add(AbstractSpatialFilteringProfile sfp, OmObservation observation) throws OwsExceptionReport {
        if (sfp != null) {
            observation.addParameter(createSpatialFilteringProfileParameter(sfp));
        } else if (strictSpatialFilteringProfile) {
            observation.addParameter(createSpatialFilteringProfileParameterForConstellation(observation
                    .getObservationConstellation()));
        }
    }

    private void init() {
        this.defaultEPSG = GeometryHandler.getInstance().getDefaultEPSG();
        this.default3DEPSG = GeometryHandler.getInstance().getDefault3DEPSG();
        this.strictSpatialFilteringProfile = ServiceConfiguration.getInstance().isStrictSpatialFilteringProfile();
    }

    private ContentCache getCache() {
        return Configurator.getInstance().getCache();
    }

    private void setSpatialFilteringProfiles(Map<Long, AbstractSpatialFilteringProfile> spatialFilteringProfiles) {
        if (CollectionHelper.isNotEmpty(spatialFilteringProfiles)) {
            this.spatialFilteringProfiles = spatialFilteringProfiles;
        } else {
            this.spatialFilteringProfiles = Collections.emptyMap();
        }
    }

    private NamedValue<?> createSpatialFilteringProfileParameter(AbstractSpatialFilteringProfile spatialFilteringProfile)
            throws OwsExceptionReport {
        final NamedValue<Geometry> namedValue = new NamedValue<Geometry>();
        final ReferenceType referenceType = new ReferenceType(spatialFilteringProfile.getDefinition());
        if (spatialFilteringProfile.isSetTitle()) {
            referenceType.setTitle(spatialFilteringProfile.getTitle());
        }
        namedValue.setName(referenceType);
        Geometry geometry;
        if (spatialFilteringProfile.isSetLongLat()) {
            final int epsg;
            if (spatialFilteringProfile.isSetSrid()) {
                epsg = spatialFilteringProfile.getSrid();
            } else {
                epsg = getDefaultEPSG();
            }
            JTSHelper.getGeometryFactoryForSRID(epsg);
            final String wktString =
                    GeometryHandler.getInstance().getWktString(spatialFilteringProfile.getLongitude(),
                            spatialFilteringProfile.getLatitude());

            geometry = JTSHelper.createGeometryFromWKT(wktString, epsg);
            if (spatialFilteringProfile.isSetAltitude()) {
                geometry.getCoordinate().z =
                        GeometryHandler.getInstance().getValueAsDouble(spatialFilteringProfile.getAltitude());
                if (geometry.getSRID() == getDefaultEPSG()) {
                    geometry.setSRID(getDefault3DEPSG());
                }
            }
        } else {
            geometry = spatialFilteringProfile.getGeom();
        }
        namedValue.setValue(new GeometryValue(GeometryHandler.getInstance()
                .switchCoordinateAxisOrderIfNeeded(geometry)));
        return namedValue;
    }

    private NamedValue<?> createSpatialFilteringProfileParameterForConstellation(
            OmObservationConstellation omObservationConstellation) {
        final NamedValue<Geometry> namedValue = new NamedValue<Geometry>();
        namedValue.setName(new ReferenceType(Sos2Constants.HREF_PARAMETER_SPATIAL_FILTERING_PROFILE));
        if (omObservationConstellation.getFeatureOfInterest() instanceof SamplingFeature
                && ((SamplingFeature) omObservationConstellation.getFeatureOfInterest()).isSetGeometry()) {
            namedValue.setValue(new GeometryValue(
                    ((SamplingFeature) omObservationConstellation.getFeatureOfInterest()).getGeometry()));
        } else {
            final String pId = omObservationConstellation.getProcedure().getIdentifier();
            SosEnvelope offeringEnvelope = getMergedBBox(getSosOfferingsForProcedure(pId));
            namedValue.setValue(new GeometryValue(JTSHelper.getGeometryFactoryForSRID(getDefaultEPSG()).createPoint(
                    offeringEnvelope.getEnvelope().centre())));
        }
        return namedValue;
    }

    private Collection<SosOffering> getSosOfferingsForProcedure(final String procedureIdentifier) {
        final Collection<String> offeringIds = getCache().getOfferingsForProcedure(procedureIdentifier);
        final Collection<SosOffering> offerings = Lists.newLinkedList();
        for (final String offeringIdentifier : offeringIds) {
            final String offeringName = getCache().getNameForOffering(offeringIdentifier);
            offerings.add(new SosOffering(offeringIdentifier, offeringName));
        }
        return offerings;
    }

    /**
     * Merge offering envelopes
     * 
     * @param offeringsForProcedure
     *            SOS offerings
     * 
     * @return merged envelopes
     */
    private SosEnvelope getMergedBBox(final Collection<SosOffering> offeringsForProcedure) {
        SosEnvelope mergedEnvelope = null;
        for (final SosOffering sosOffering : offeringsForProcedure) {
            final SosEnvelope offeringEnvelope =
                    getCache().getEnvelopeForOffering(sosOffering.getOfferingIdentifier());
            if (offeringEnvelope != null && offeringEnvelope.isSetEnvelope()) {
                if (mergedEnvelope == null) {
                    mergedEnvelope = offeringEnvelope;
                } else {
                    mergedEnvelope.expandToInclude(offeringEnvelope.getEnvelope());
                }
            }
        }
        return mergedEnvelope;
    }

    private int getDefaultEPSG() {
        return defaultEPSG;
    }

    private int getDefault3DEPSG() {
        return default3DEPSG;
    }
}
