/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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
package org.n52.sos.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.geolatte.geom.crs.CrsRegistry;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.CRS.AxisOrder;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.geotools.referencing.factory.AbstractAuthorityFactory;
import org.geotools.referencing.factory.DeferredAuthorityFactory;
import org.geotools.util.factory.Hints;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.n52.faroe.ConfigurationError;
import org.n52.faroe.Validation;
import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.util.Range;
import org.n52.janmayen.lifecycle.Constructable;
import org.n52.janmayen.lifecycle.Destroyable;
import org.n52.shetland.ogc.filter.SpatialFilter;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.InvalidParameterValueException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.util.CollectionHelper;
import org.n52.shetland.util.EnvelopeOrGeometry;
import org.n52.shetland.util.GeometryTransformer;
import org.n52.shetland.util.JTSHelper;
import org.n52.shetland.util.JavaHelper;
import org.n52.shetland.util.StringHelper;
import org.n52.sos.ds.FeatureQuerySettingsProvider;
import org.n52.svalbard.CodingSettings;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Class to provide some methods for JTS Geometry which is used by
 * {@link org.n52.sos.ds.FeatureQueryHandler}.
 *
 * @since 4.0.0
 *
 */
@Configurable
public class GeometryHandler implements GeometryTransformer, Constructable, Destroyable {

    /*
     * longitude = east-west latitude = north-south
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GeometryHandler.class);

    private static final String EPSG = "EPSG";

    private static final String EPSG_PREFIX = EPSG + ":";

    private static final String EPSG_NOT_SUPPORTED_TMEPLATE = "The EPSG code '%s' is not supported!";

    private boolean datasoureUsesNorthingFirst;

    private final List<Range> epsgsWithNorthingFirstAxisOrder = Lists.newArrayList();

    private int storageEPSG;

    private int storage3DEPSG;

    private int defaultResponseEPSG;

    private int defaultResponse3DEPSG;

    private final Set<String> supportedCRS = Sets.newHashSet();

    private boolean spatialDatasource;

    private String authority;

    private CRSAuthorityFactory crsAuthority;

    private final Map<Integer, CoordinateReferenceSystem> crsCache = Maps.newConcurrentMap();

    private String srsNamePrefixUrl;

    @Setting(CodingSettings.SRS_NAME_PREFIX_URL)
    public GeometryHandler setSrsNamePrefixUrl(String srsNamePrefixUrl) {
        this.srsNamePrefixUrl = srsNamePrefixUrl;
        return this;
    }

    @Override
    public void init() {
        boolean eastingFirstEpsgCode = true;
        try {
            eastingFirstEpsgCode = isEastingFirstEpsgCode(getStorageEPSG());
        } catch (Exception e) {
            LOGGER.error("The storage EPSG code '{}' is invalid. Easting first = true would be used!",
                    getStorageEPSG());
        }
        Hints hints = new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, eastingFirstEpsgCode);
        this.crsAuthority = ReferencingFactoryFinder.getCRSAuthorityFactory(this.authority, hints);

        supportedCRS.stream().forEach(crs -> {
            CrsRegistry.ifAbsentReturnProjected2D(Integer.parseInt(crs));
        });
    }

    @Override
    public void destroy() {
        if (this.crsAuthority == null) {
            return;
        }

        if (this.crsAuthority instanceof DeferredAuthorityFactory) {
            DeferredAuthorityFactory.exit();
        }
        if (this.crsAuthority instanceof AbstractAuthorityFactory) {
            try {
                ((AbstractAuthorityFactory) this.crsAuthority).dispose();
            } catch (FactoryException fe) {
                LOGGER.error("Error while GeometryHandler clean up", fe);
            }
        }
        /*
         * close {@link WeakCollectionCleaner}
         *
         * Note: Not required if
         * se.jiderhamn.classloader.leak.prevention.ClassLoaderLeakPreventor is
         * defined in the web.xml
         */
        // WeakCollectionCleaner.DEFAULT.exit();
    }

    /**
     * Get configured storage EPSG code
     *
     * @return Storage EPSG code
     */
    public int getStorageEPSG() {
        return storageEPSG;
    }

    /**
     * Get configured storage 3D EPSG code
     *
     * @return Storage 3D EPSG code
     */
    public int getStorage3DEPSG() {
        return storage3DEPSG;
    }

    /**
     * Get configured default response EPSG code
     *
     * @return Default response EPSG code
     */
    public int getDefaultResponseEPSG() {
        return defaultResponseEPSG;
    }

    /**
     * Get configured default response 3D EPSG code
     *
     * @return Default response 3D EPSG code
     */
    public int getDefaultResponse3DEPSG() {
        return defaultResponse3DEPSG;
    }

    /**
     * Set storage EPSG code from settings
     *
     * @param epsgCode
     *            EPSG code from settings
     *
     * @throws ConfigurationError
     *             If an error occurs
     */
    @Setting(FeatureQuerySettingsProvider.STORAGE_EPSG)
    public void setStorageEpsg(int epsgCode) throws ConfigurationError {
        Validation.greaterZero("Storage EPSG Code", epsgCode);
        this.storageEPSG = epsgCode;
        addToSupportedCrs(epsgCode);
    }

    /**
     * Set storage 3D EPSG code from settings
     *
     * @param epsgCode3D
     *            3D EPSG code from settings
     *
     * @throws ConfigurationError
     *             If an error occurs
     */
    @Setting(FeatureQuerySettingsProvider.STORAGE_3D_EPSG)
    public void setStorage3DEpsg(int epsgCode3D) throws ConfigurationError {
        Validation.greaterZero("Storage 3D EPSG Code", epsgCode3D);
        this.storage3DEPSG = epsgCode3D;
        addToSupportedCrs(epsgCode3D);
    }

    /**
     * Set default response EPSG code from settings
     *
     * @param epsgCode
     *            EPSG code from settings
     *
     * @throws ConfigurationError
     *             If an error occurs
     */
    @Setting(FeatureQuerySettingsProvider.DEFAULT_RESPONSE_EPSG)
    public void setDefaultResponseEpsg(int epsgCode) throws ConfigurationError {
        Validation.greaterZero("Default response EPSG Code", epsgCode);
        this.defaultResponseEPSG = epsgCode;
        addToSupportedCrs(epsgCode);
    }

    /**
     * Set default response 3D EPSG code from settings
     *
     * @param epsgCode3D
     *            3D EPSG code from settings
     *
     * @throws ConfigurationError
     *             If an error occurs
     */
    @Setting(FeatureQuerySettingsProvider.DEFAULT_RESPONSE_3D_EPSG)
    public void setDefaultResponse3DEpsg(int epsgCode3D) throws ConfigurationError {
        Validation.greaterZero("Default response 3D EPSG Code", epsgCode3D);
        this.defaultResponse3DEPSG = epsgCode3D;
        addToSupportedCrs(epsgCode3D);
    }

    /**
     * Set the supported EPSG codes
     *
     * @param supportedCRS
     *            Supported EPSG codes
     *
     * @throws ConfigurationError
     *             If an error occurs
     */
    @Setting(FeatureQuerySettingsProvider.SUPPORTED_CRS_KEY)
    public void setSupportedCRS(final String supportedCRS) throws ConfigurationError {
        // Validation.notNull("Supported CRS codes as CSV string",
        // supportedCRS);
        this.supportedCRS.clear();
        this.supportedCRS.addAll(StringHelper.splitToSet(supportedCRS, ","));
        addDefaultCrs();
    }

    private void addDefaultCrs() {
        if (storageEPSG > 0) {
            addToSupportedCrs(getStorageEPSG());
        }
        if (storage3DEPSG > 0) {
            addToSupportedCrs(getStorage3DEPSG());
        }
        if (defaultResponseEPSG > 0) {
            addToSupportedCrs(getDefaultResponseEPSG());
        }
        if (defaultResponse3DEPSG > 0) {
            addToSupportedCrs(getDefaultResponse3DEPSG());
        }
    }

    /**
     * Get List of supported EPSG codes
     *
     * @return Supported EPSG codes
     */
    public Set<String> getSupportedCRS() {
        try {
            Set<String> authorityCodes = getCrsAuthorityFactory().getAuthorityCodes(CoordinateReferenceSystem.class);
            if (CollectionHelper.isNotEmpty(authorityCodes) && CollectionHelper.isNotEmpty(this.supportedCRS)) {
                return CollectionHelper.conjunctCollectionsToSet(authorityCodes, this.supportedCRS);
            } else if (CollectionHelper.isEmpty(authorityCodes)) {
                return Sets.newHashSet(Integer.toString(getStorageEPSG()), Integer.toString(getStorage3DEPSG()));
            }
            return authorityCodes;
        } catch (FactoryException fe) {
            LOGGER.warn("Error while querying supported EPSG codes", fe);
        }
        return Collections.emptySet();
    }

    @Setting(FeatureQuerySettingsProvider.AUTHORITY)
    public GeometryHandler setAuthority(final String authority) {
        Validation.notNull("The CRS authority", authority);
        this.authority = authority;
        return this;
    }

    public String getAuthority() {
        return authority;
    }

    /**
     * Add integer EPSG code to supported CRS set.
     *
     * @param epsgCode
     *            Integer EPSG code
     *
     * @return this
     */
    private GeometryHandler addToSupportedCrs(int epsgCode) {
        this.supportedCRS.add(Integer.toString(epsgCode));
        return this;
    }

    /**
     * Set the northing first indicator for the datasource.
     *
     * @param datasoureUsesNorthingFirst
     *            Northing first indicator
     *
     * @return this
     */
    @Setting(FeatureQuerySettingsProvider.DATASOURCE_NORTHING_FIRST)
    public GeometryHandler setDatasourceNorthingFirst(boolean datasoureUsesNorthingFirst) {
        this.datasoureUsesNorthingFirst = datasoureUsesNorthingFirst;
        return this;
    }

    /**
     * Check if the datasource uses northing first coordinates.
     *
     * @return <code>true</code>, if the datasource uses northing first
     *         coordinates
     */
    public boolean isDatasourceNorthingFirst() {
        return datasoureUsesNorthingFirst;
    }

    /**
     * Set the EPSG code ranges for which the coordinates should be switched.
     *
     * @param codes
     *            EPSG code ranges
     *
     * @throws ConfigurationError
     *             If an error occurs
     * @return this
     */
    @Setting(FeatureQuerySettingsProvider.EPSG_CODES_WITH_NORTHING_FIRST)
    public GeometryHandler setEpsgCodesWithNorthingFirstAxisOrder(String codes) throws ConfigurationError {
        Validation.notNullOrEmpty("EPSG Codes to switch coordinates for", codes);
        final String[] splitted = codes.split(";");
        List<Range> newEpsgCodes = Lists.newArrayListWithCapacity(splitted.length);
        for (final String entry : splitted) {
            final String[] splittedEntry = entry.split("-");
            Range r = null;
            try {
                switch (splittedEntry.length) {
                    case 1:
                        r = new Range(Integer.parseInt(splittedEntry[0]), Integer.parseInt(splittedEntry[0]));
                        break;
                    case 2:
                        r = new Range(Integer.parseInt(splittedEntry[0]), Integer.parseInt(splittedEntry[1]));
                        break;
                    default:
                        throw createException(entry, null);
                }
            } catch (NumberFormatException ex) {
                throw createException(entry, ex);
            }
            newEpsgCodes.add(r);
            epsgsWithNorthingFirstAxisOrder.add(r);
        }
        epsgsWithNorthingFirstAxisOrder.clear();
        epsgsWithNorthingFirstAxisOrder.addAll(newEpsgCodes);
        return this;
    }

    /**
     * Set flag if the used datasource is a spatial datasource (provides spatial
     * functions).
     *
     * @param spatialDatasource
     *            Flag if spatial datasource
     */
    @Setting(FeatureQuerySettingsProvider.SPATIAL_DATASOURCE)
    public void setSpatialDatasource(boolean spatialDatasource) {
        this.spatialDatasource = spatialDatasource;
    }

    /**
     * Is datasource a spatial datasource.
     *
     * @return Spatial datasource or not
     */
    public boolean isSpatialDatasource() {
        return spatialDatasource;
    }

    /**
     * Check if the EPSG code is northing first.
     *
     * @param epsgCode
     *            EPSG code to check
     *
     * @return <code>true</code>, if the EPSG code is northing first
     * @throws CodedException
     *             If an error occurs
     */
    public boolean isNorthingFirstEpsgCode(int epsgCode) throws CodedException {
        try {
            return AxisOrder.NORTH_EAST.equals(CRS.getAxisOrder(CRS.decode(EPSG_PREFIX + epsgCode))) ? true : false;
        } catch (FactoryException e) {
            throw new NoApplicableCodeException().causedBy(e).withMessage("The EPSG '%d' is invalid", epsgCode);
        }
        // return this.epsgsWithNorthingFirstAxisOrder.stream()
        // .filter(r -> r.contains(epsgCode))
        // .findAny().isPresent();
    }

    /**
     * Check if the EPSG code is easting first.
     *
     * @param epsgCode
     *            EPSG code to check
     *
     * @return <code>true</code>, if the EPSG code is easting first
     * @throws CodedException
     *             If an error occurs
     */
    public boolean isEastingFirstEpsgCode(int epsgCode) throws CodedException {
        return !isNorthingFirstEpsgCode(epsgCode);
    }

    /**
     * Switch the coordinate axis of geometry from or for datasource.
     *
     * @param geom
     *            Geometry to switch coordinate axis
     *
     * @return Geometry with switched coordinate axis if needed
     *
     * @throws OwsExceptionReport
     *             If coordinate axis switching fails
     */
    public Geometry switchCoordinateAxisFromToDatasourceIfNeeded(EnvelopeOrGeometry geom) throws OwsExceptionReport {
        return switchCoordinateAxisFromToDatasourceIfNeeded(geom.toGeometry());
    }

    /**
     * Switch the coordinate axis of geometry from or for datasource.
     *
     * @param geom
     *            Geometry to switch coordinate axis
     *
     * @return Geometry with switched coordinate axis if needed
     *
     * @throws OwsExceptionReport
     *             If coordinate axis switching fails
     */
    public Geometry switchCoordinateAxisFromToDatasourceIfNeeded(Geometry geom) throws OwsExceptionReport {
        if (!shouldSwitchCoordinateAxis(geom)) {
            return geom;
        }
        return JTSHelper.switchCoordinateAxisOrder(geom);
    }

    private Geometry switchCoordinateAxisIfNeeded(Geometry geometry, int targetSRID) throws OwsExceptionReport {
        if (!shouldSwitchCoordinateAxis(geometry, targetSRID)) {
            return geometry;
        }
        return JTSHelper.switchCoordinateAxisOrder(geometry);
    }

    private boolean shouldSwitchCoordinateAxis(Geometry geom) throws CodedException {
        if (geom == null || geom.isEmpty()) {
            return false;
        }
        return isDatasourceNorthingFirst() != isNorthingFirstEpsgCode(geom.getSRID());
    }

    private boolean shouldSwitchCoordinateAxis(Geometry geom, int targetSRID) throws CodedException {
        if (geom == null || geom.isEmpty()) {
            return false;
        }
        return isNorthingFirstEpsgCode(geom.getSRID()) != isNorthingFirstEpsgCode(targetSRID);
    }

    /**
     * Get filter geometry for BBOX spatial filter and non spatial datasource.
     *
     * @param filter
     *            SpatialFilter
     *
     * @return SpatialFilter geometry
     *
     * @throws OwsExceptionReport
     *             If SpatialFilter is not supported
     */
    public Geometry getFilterForNonSpatialDatasource(SpatialFilter filter) throws OwsExceptionReport {
        switch (filter.getOperator()) {
            case BBOX:
                return switchCoordinateAxisFromToDatasourceIfNeeded(filter.getGeometry());
            default:
                throw new InvalidParameterValueException("spatialFilter", filter.getOperator().name());
            // Sos2Constants.GetObservationParams.spatialFilter =
            // "spatialFilter"
        }
    }

    /**
     * Get WKT string from longitude and latitude.
     *
     * @param longitude
     *            Longitude coordinate
     * @param latitude
     *            Latitude coordinate
     *
     * @return WKT string
     */
    public String getWktString(Object longitude, Object latitude) {
        return getWktString(latitude, longitude, datasoureUsesNorthingFirst);
    }

    /**
     * Get WKT string from longitude and latitude with axis order as defined by
     * EPSG code.
     *
     * @param longitude
     *            Longitude coordinate
     * @param latitude
     *            Latitude coordinate
     * @param epsg
     *            EPSG code to check for axis order
     *
     * @return WKT string
     * @throws CodedException
     *             If an error occurs
     */
    public String getWktString(Object longitude, Object latitude, int epsg) throws CodedException {
        return getWktString(latitude, longitude, isNorthingFirstEpsgCode(epsg));
    }

    private String getWktString(Object latitude, Object longitude, boolean northingFirst) {
        return northingFirst ? createWktString(latitude, longitude) : createWktString(longitude, latitude);
    }

    private String createWktString(Object x, Object y) {
        StringBuilder builder = new StringBuilder();
        builder.append("POINT (");
        builder.append(JavaHelper.asString(x));
        builder.append(' ');
        builder.append(JavaHelper.asString(y));
        builder.append(')');
        return builder.toString();
    }

    /**
     * Check if geometry is in SpatialFilter envelopes.
     *
     * @param geometry
     *            Geometry to check
     * @param envelopes
     *            SpatialFilter envelopes
     *
     * @return True if geometry is contained in envelopes
     */
    public boolean featureIsInFilter(Geometry geometry, List<Geometry> envelopes) {
        return geometry != null && !geometry.isEmpty() && envelopes.stream().anyMatch(e -> e.contains(geometry));
    }

    /**
     * Transforms the geometry to the storage EPSG code.
     *
     * @param geometry
     *            Geometry to transform
     *
     * @return Transformed geometry
     *
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    public Geometry transformToStorageEpsg(EnvelopeOrGeometry geometry) throws OwsExceptionReport {
        return transformToStorageEpsg(geometry.toGeometry());
    }

    /**
     * Transforms the geometry to the storage EPSG code.
     *
     * @param geometry
     *            Geometry to transform
     *
     * @return Transformed geometry
     *
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    public Geometry transformToStorageEpsg(Geometry geometry) throws OwsExceptionReport {
        if (geometry == null || geometry.isEmpty()) {
            return geometry;
        }
        CoordinateReferenceSystem sourceCRS = getCRS(geometry.getSRID());
        int targetSRID = sourceCRS.getCoordinateSystem().getDimension() == 3 ? getStorage3DEPSG() : getStorageEPSG();
        return transform(geometry, targetSRID, sourceCRS, getCRS(targetSRID));

    }

    /**
     * Transform geometry to this EPSG code.
     *
     * @param geometry
     *            Geometry to transform
     * @param targetSRID
     *            Target EPSG code
     *
     * @return Transformed geometry
     *
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    @Override
    public Geometry transform(Geometry geometry, int targetSRID) throws OwsExceptionReport {
        if (geometry == null || geometry.isEmpty() || geometry.getSRID() == targetSRID) {
            return geometry;
        }
        CoordinateReferenceSystem sourceCRS = getCRS(geometry.getSRID());
        CoordinateReferenceSystem targetCRS = getCRS(targetSRID);
        return transform(geometry, targetSRID, sourceCRS, targetCRS);
    }

    /**
     * Transform geometry.
     *
     * @param geometry
     *            Geometry to transform
     * @param targetSRID
     *            TargetEPSG code
     * @param sourceCRS
     *            Source CRS
     * @param targetCRS
     *            Target CRS
     *
     * @return Transformed geometry
     *
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private Geometry transform(final Geometry geometry, final int targetSRID,
            final CoordinateReferenceSystem sourceCRS, final CoordinateReferenceSystem targetCRS)
            throws OwsExceptionReport {
        if (sourceCRS.equals(targetCRS)) {
            return geometry;
        }
        Geometry switchedCoordiantes = switchCoordinateAxisIfNeeded(geometry, targetSRID);
        try {
            MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
            Geometry transformed = JTS.transform(switchedCoordiantes, transform);
            transformed.setSRID(targetSRID);
            return transformed;
        } catch (FactoryException | MismatchedDimensionException | TransformException fe) {
            throw new NoApplicableCodeException().causedBy(fe).withMessage(EPSG_NOT_SUPPORTED_TMEPLATE,
                    switchedCoordiantes.getSRID());
        }
    }

    /**
     * Get CRS from EPSG code.
     *
     * @param epsgCode
     *            EPSG code to get CRS for
     *
     * @return CRS fro EPSG code
     *
     * @throws CodedException
     *             If the geometry EPSG code is not supported
     */
    private CoordinateReferenceSystem getCRS(int epsgCode) throws OwsExceptionReport {
        try {
            return this.crsCache.computeIfAbsent(epsgCode, code -> {
                try {
                    return createCRS(code);
                } catch (OwsExceptionReport ex) {
                    throw new RuntimeException(ex);
                }
            });
        } catch (RuntimeException ex) {
            Throwables.throwIfInstanceOf(ex.getCause(), OwsExceptionReport.class);
            throw ex;
        }
    }

    public boolean is3dCrs(int epsgCode) throws OwsExceptionReport {
        return getCRS(epsgCode).getCoordinateSystem().getDimension() == 3;
    }

    /**
     * Create CRS for EPSG code.
     *
     * @param epsgCode
     *            EPSG code to create CRS for
     *
     * @return Created CRS
     *
     * @throws CodedException
     *             If the geometry EPSG code is not supported
     */
    private CoordinateReferenceSystem createCRS(int epsgCode) throws OwsExceptionReport {
        try {
            return getCrsAuthorityFactory().createCoordinateReferenceSystem(EPSG_PREFIX + epsgCode);
        } catch (FactoryException nsace) {
            throw new NoApplicableCodeException().causedBy(nsace).withMessage(EPSG_NOT_SUPPORTED_TMEPLATE,
                    epsgCode);
        }
    }

    /**
     * Get CSR authority.
     *
     * @return CRS authority
     */
    private CRSAuthorityFactory getCrsAuthorityFactory() {
        return crsAuthority;
    }

    /**
     * Transform envelope from source to target EPSG code.
     *
     * @param envelope
     *            Envelope to transform
     * @param sourceSRID
     *            Source EPSG code
     * @param targetSRID
     *            Target EPSG code
     *
     * @return Transformed envelope
     *
     * @throws CodedException
     *             If the geometry EPSG code is not supported
     */
    public Envelope transformEnvelope(Envelope envelope, int sourceSRID, int targetSRID) throws OwsExceptionReport {
        if (envelope != null && !envelope.isNull() && targetSRID > 0 && sourceSRID != targetSRID) {
            CoordinateReferenceSystem sourceCRS = getCRS(sourceSRID);
            CoordinateReferenceSystem targetCRS = getCRS(targetSRID);
            try {
                if (sourceCRS.getCoordinateSystem().getDimension() == targetCRS.getCoordinateSystem().getDimension()) {
                    MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
                    Envelope transformed = JTS.transform(envelope, transform);
                    return transformed;
                }
                return envelope;
            } catch (FactoryException fe) {
                throw new NoApplicableCodeException().causedBy(fe).withMessage(EPSG_NOT_SUPPORTED_TMEPLATE,
                        sourceSRID);
            } catch (MismatchedDimensionException | TransformException mde) {
                throw new NoApplicableCodeException().causedBy(mde)
                        .withMessage("Transformation from EPSG code '%s' to '%s' fails!", sourceSRID, targetSRID);
            }
        }
        return envelope;
    }

    /**
     * Clears the supported Coordinate Reference Systems map.
     */
    @VisibleForTesting
    protected void clearSupportedCRSMap() {
        crsCache.clear();
    }

    public Set<String> addAuthorityCrsPrefix(Collection<String> crses) {
        return crses.stream().map(this::addAuthorityCrsPrefix).collect(Collectors.toSet());
    }

    public String addAuthorityCrsPrefix(int crs) {
        return addAuthorityCrsPrefix(Integer.toString(crs));
    }

    public String addAuthorityCrsPrefix(String crs) {
        return new StringBuilder(getAuthority()).append("::").append(crs).toString();
    }

    public Set<String> addOgcCrsPrefix(Collection<String> crses) {
        return crses.stream().map(this::addOgcCrsPrefix).collect(Collectors.toSet());
    }

    public String addOgcCrsPrefix(int crs) {
        return this.srsNamePrefixUrl + crs;
    }

    public String addOgcCrsPrefix(String crs) {
        return this.srsNamePrefixUrl + crs;
    }

    private ConfigurationError createException(String entry, Throwable ex) {
        return new ConfigurationError(String.format("Invalid format of entry in '%s': %s",
                FeatureQuerySettingsProvider.EPSG_CODES_WITH_NORTHING_FIRST, entry), ex);
    }

}
