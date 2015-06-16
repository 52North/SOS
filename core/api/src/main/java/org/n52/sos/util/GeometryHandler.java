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
package org.n52.sos.util;

import static org.geotools.factory.Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER;
import static org.geotools.referencing.ReferencingFactoryFinder.getCRSAuthorityFactory;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.geotools.factory.Hints;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.factory.AbstractAuthorityFactory;
import org.geotools.referencing.factory.DeferredAuthorityFactory;
import org.geotools.util.WeakCollectionCleaner;
import org.n52.sos.config.SettingsManager;
import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.config.annotation.Setting;
import org.n52.sos.ds.FeatureQuerySettingsProvider;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Range;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.service.ServiceConfiguration;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Class to provide some methods for JTS Geometry which is used by
 * {@link org.n52.sos.ds.FeatureQueryHandler} and SpatialFilteringProfile DAO.
 * 
 * @since 4.0.0
 * 
 */
@Configurable
public class GeometryHandler implements Cleanupable, EpsgConstants {

    /*
     * longitude = east-west latitude = north-south
     */

    private static final Logger LOGGER = LoggerFactory.getLogger(GeometryHandler.class);

    private static GeometryHandler instance;

    private static ReentrantLock creationLock = new ReentrantLock();

    private boolean datasoureUsesNorthingFirst;

    private List<Range> epsgsWithNorthingFirstAxisOrder = Lists.newArrayList();

    private int storageEPSG;

    private int storage3DEPSG;

    private int defaultResponseEPSG;

    private int defaultResponse3DEPSG;

    private Set<String> supportedCRS = Sets.newHashSet();

    private boolean spatialDatasource;

    private String authority;

    private CRSAuthorityFactory crsAuthority;

    private Map<Integer, CoordinateReferenceSystem> supportedCRSMap = Maps.newHashMap();;

    /**
     * Private constructor
     */
    private GeometryHandler() {
    }

    /**
     * @return Returns a singleton instance of the GeometryHandler.
     */
    public static GeometryHandler getInstance() {
        if (instance == null) {
            creationLock.lock();
            try {
                if (instance == null) {
                    // don't set instance before configuring, or other threads
                    // can get access to unconfigured instance!
                    final GeometryHandler newInstance = new GeometryHandler();
                    SettingsManager.getInstance().configure(newInstance);
                    newInstance.initCrsAuthoritycrsAuthority();
                    instance = newInstance;
                }
            } finally {
                creationLock.unlock();
            }
        }
        return instance;
    }

    private void initCrsAuthoritycrsAuthority() {
        crsAuthority =
                getCRSAuthorityFactory(authority, new Hints(FORCE_LONGITUDE_FIRST_AXIS_ORDER,
                        isEastingFirstEpsgCode(getStorageEPSG())));

    }

    @Override
    public void cleanup() {
        if (getCrsAuthorityFactory() != null) {
            if (getCrsAuthorityFactory() instanceof DeferredAuthorityFactory) {
                DeferredAuthorityFactory.exit();
            }
            if (getCrsAuthorityFactory() instanceof AbstractAuthorityFactory) {
                try {
                    ((AbstractAuthorityFactory) getCrsAuthorityFactory()).dispose();
                } catch (FactoryException fe) {
                    LOGGER.error("Error while GeometryHandler clean up", fe);
                }
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
     * @throws ConfigurationException
     *             If an error occurs
     */
    @Setting(FeatureQuerySettingsProvider.STORAGE_EPSG)
    public void setStorageEpsg(final int epsgCode) throws ConfigurationException {
        Validation.greaterZero("Storage EPSG Code", epsgCode);
        storageEPSG = epsgCode;
        addToSupportedCrs(epsgCode);
    }

    /**
     * Set storage 3D EPSG code from settings
     * 
     * @param epsgCode3D
     *            3D EPSG code from settings
     * @throws ConfigurationException
     *             If an error occurs
     */
    @Setting(FeatureQuerySettingsProvider.STORAGE_3D_EPSG)
    public void setStorage3DEpsg(final int epsgCode3D) throws ConfigurationException {
        Validation.greaterZero("Storage 3D EPSG Code", epsgCode3D);
        storage3DEPSG = epsgCode3D;
        addToSupportedCrs(epsgCode3D);
    }

    /**
     * Set default response EPSG code from settings
     * 
     * @param epsgCode
     *            EPSG code from settings
     * @throws ConfigurationException
     *             If an error occurs
     */
    @Setting(FeatureQuerySettingsProvider.DEFAULT_RESPONSE_EPSG)
    public void setDefaultResponseEpsg(final int epsgCode) throws ConfigurationException {
        Validation.greaterZero("Storage EPSG Code", epsgCode);
        defaultResponseEPSG = epsgCode;
        addToSupportedCrs(epsgCode);
    }

    /**
     * Set default response 3D EPSG code from settings
     * 
     * @param epsgCode3D
     *            3D EPSG code from settings
     * @throws ConfigurationException
     *             If an error occurs
     */
    @Setting(FeatureQuerySettingsProvider.DEFAULT_RESPONSE_3D_EPSG)
    public void setDefaultResponse3DEpsg(final int epsgCode3D) throws ConfigurationException {
        Validation.greaterZero("Storage 3D EPSG Code", epsgCode3D);
        defaultResponse3DEPSG = epsgCode3D;
        addToSupportedCrs(epsgCode3D);
    }

    /**
     * Set the supported EPSG codes
     * 
     * @param supportedCRS
     *            Supported EPSG codes
     * @throws ConfigurationException
     */
    @Setting(FeatureQuerySettingsProvider.SUPPORTED_CRS_KEY)
    public void setSupportedCRS(final String supportedCRS) throws ConfigurationException {
        // Validation.notNull("Supported CRS codes as CSV string",
        // supportedCRS);
        this.supportedCRS.addAll(StringHelper.splitToSet(supportedCRS, Constants.COMMA_STRING));
    }

    @Setting(FeatureQuerySettingsProvider.AUTHORITY)
    public void setAuthority(final String authority) {
        Validation.notNull("The CRS authority", authority);
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }

    /**
     * Add integer EPSG code to supported CRS set
     * 
     * @param epsgCode
     *            Integer EPSG code
     */
    private void addToSupportedCrs(int epsgCode) {
        this.supportedCRS.add(Integer.toString(epsgCode));
    }

    /**
     * Set the northing first indicator for the datasource
     * 
     * @param datasoureUsesNorthingFirst
     *            Northing first indicator
     */
    @Setting(FeatureQuerySettingsProvider.DATASOURCE_NORTHING_FIRST)
    public void setDatasourceNorthingFirst(final boolean datasoureUsesNorthingFirst) {
        this.datasoureUsesNorthingFirst = datasoureUsesNorthingFirst;
    }

    /**
     * Check if the datasource uses northing first coordinates
     * 
     * @return <code>true</code>, if the datasource uses northing first
     *         coordinates
     */
    public boolean isDatasourceNorthingFirst() {
        return datasoureUsesNorthingFirst;
    }

    /**
     * Set the EPSG code ranges for which the coordinates should be switched
     * 
     * @param codes
     *            EPSG code ranges
     * @throws ConfigurationException
     *             If an error occurs
     */
    @Setting(FeatureQuerySettingsProvider.EPSG_CODES_WITH_NORTHING_FIRST)
    public void setEpsgCodesWithNorthingFirstAxisOrder(final String codes) throws ConfigurationException {
        Validation.notNullOrEmpty("EPSG Codes to switch coordinates for", codes);
        final String[] splitted = codes.split(";");
        for (final String entry : splitted) {
            final String[] splittedEntry = entry.split("-");
            Range r = null;
            if (splittedEntry.length == 1) {
                r = new Range(Integer.parseInt(splittedEntry[0]), Integer.parseInt(splittedEntry[0]));
            } else if (splittedEntry.length == 2) {
                r = new Range(Integer.parseInt(splittedEntry[0]), Integer.parseInt(splittedEntry[1]));
            } else {
                throw new ConfigurationException(String.format("Invalid format of entry in '%s': %s",
                        FeatureQuerySettingsProvider.EPSG_CODES_WITH_NORTHING_FIRST, entry));
            }
            epsgsWithNorthingFirstAxisOrder.add(r);
        }
    }

    /**
     * Set flag if the used datasource is a spatial datasource (provides spatial
     * functions)
     * 
     * @param spatialDatasource
     *            Flag if spatial datasource
     */
    @Setting(FeatureQuerySettingsProvider.SPATIAL_DATASOURCE)
    public void setSpatialDatasource(final boolean spatialDatasource) {
        this.spatialDatasource = spatialDatasource;
    }

    /**
     * Check if the EPSG code is northing first
     * 
     * @param epsgCode
     *            EPSG code to check
     * @return <code>true</code>, if the EPSG code is northing first
     */
    public boolean isNorthingFirstEpsgCode(final int epsgCode) {
        for (final Range r : epsgsWithNorthingFirstAxisOrder) {
            if (r.contains(epsgCode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the EPSG code is easting first
     * 
     * @param epsgCode
     *            EPSG code to check
     * @return <code>true</code>, if the EPSG code is easting first
     */
    public boolean isEastingFirstEpsgCode(final int epsgCode) {
        return !isNorthingFirstEpsgCode(epsgCode);
    }

    /**
     * Is datasource a spatial datasource
     * 
     * @return Spatial datasource or not
     */
    public boolean isSpatialDatasource() {
        return spatialDatasource;
    }

    /**
     * Switch the coordinate axis of geometry from or for datasource
     * 
     * @param geom
     *            Geometry to switch coordinate axis
     * @return Geometry with switched coordinate axis if needed
     * @throws OwsExceptionReport
     *             If coordinate axis switching fails
     */
    public Geometry switchCoordinateAxisFromToDatasourceIfNeeded(final Geometry geom) throws OwsExceptionReport {
        if (geom != null && !geom.isEmpty()) {
            if (isDatasourceNorthingFirst()) {
                if (!isNorthingFirstEpsgCode(geom.getSRID())) {
                    return JTSHelper.switchCoordinateAxisOrder(geom);
                }
                return geom;
            } else {
                if (isNorthingFirstEpsgCode(geom.getSRID())) {
                    return JTSHelper.switchCoordinateAxisOrder(geom);
                }
                return geom;
            }
        }
        return geom;
    }

    private Geometry switchCoordinateAxisIfNeeded(Geometry geometry, int targetSRID) throws OwsExceptionReport {
        if (geometry != null && !geometry.isEmpty()) {
            if ((isNorthingFirstEpsgCode(geometry.getSRID()) && isNorthingFirstEpsgCode(targetSRID))
                    || (isEastingFirstEpsgCode(geometry.getSRID()) && isEastingFirstEpsgCode(targetSRID))) {
                return geometry;
            }
            return JTSHelper.switchCoordinateAxisOrder(geometry);
        }
        return geometry;
    }

    /**
     * Get Object value as Double value
     * 
     * @param value
     *            Value to check
     * @return Double value
     */
    // TODO replace with JavaHelper.asDouble?
    @Deprecated
    public double getValueAsDouble(final Object value) {
        if (value instanceof String) {
            return Double.valueOf((String) value).doubleValue();
        } else if (value instanceof BigDecimal) {
            final BigDecimal bdValue = (BigDecimal) value;
            return bdValue.doubleValue();
        } else if (value instanceof Double) {
            return ((Double) value).doubleValue();
        }
        return 0;
    }

    /**
     * Get filter geometry for BBOX spatial filter and non spatial datasource
     * 
     * @param filter
     *            SpatialFilter
     * @return SpatialFilter geometry
     * @throws OwsExceptionReport
     *             If SpatialFilter is not supported
     */
    public Geometry getFilterForNonSpatialDatasource(final SpatialFilter filter) throws OwsExceptionReport {
        switch (filter.getOperator()) {
        case BBOX:
            return switchCoordinateAxisFromToDatasourceIfNeeded(filter.getGeometry());
        default:
            throw new InvalidParameterValueException(Sos2Constants.GetObservationParams.spatialFilter, filter
                    .getOperator().name());
        }
    }

    /**
     * Get WKT string from longitude and latitude
     * 
     * @param longitude
     *            Longitude coordinate
     * @param latitude
     *            Latitude coordinate
     * @return WKT string
     */
    public String getWktString(final Object longitude, final Object latitude) {
        final StringBuilder builder = new StringBuilder();
        builder.append("POINT ").append(Constants.OPEN_BRACE_CHAR);
        if (datasoureUsesNorthingFirst) {
            builder.append(JavaHelper.asString(latitude)).append(Constants.BLANK_CHAR);
            builder.append(JavaHelper.asString(longitude));
        } else {
            builder.append(JavaHelper.asString(longitude)).append(Constants.BLANK_CHAR);
            builder.append(JavaHelper.asString(latitude));
        }
        builder.append(Constants.CLOSE_BRACE_CHAR);
        return builder.toString();
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
     * @return WKT string
     */
    public String getWktString(Object longitude, Object latitude, int epsg) {
        if (isNorthingFirstEpsgCode(epsg)) {
            return getWktString(latitude, longitude);
        }
        return getWktString(longitude, latitude);
    }

    /**
     * Check if geometry is in SpatialFilter envelopes
     * 
     * @param geometry
     *            Geometry to check
     * @param envelopes
     *            SpatialFilter envelopes
     * @return True if geometry is contained in envelopes
     */
    public boolean featureIsInFilter(final Geometry geometry, final List<Geometry> envelopes) {
        if (geometry != null && !geometry.isEmpty()) {
            for (final Geometry envelope : envelopes) {
                if (envelope.contains(geometry)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Transforms the geometry to the storage EPSG code
     * 
     * @param geometry
     *            Geometry to transform
     * @return Transformed geometry
     * @throws OwsExceptionReport
     */
    public Geometry transformToStorageEpsg(final Geometry geometry) throws OwsExceptionReport {
        if (geometry != null && !geometry.isEmpty()) {
            CoordinateReferenceSystem sourceCRS = getCRS(geometry.getSRID());
            int targetSRID;
            if (sourceCRS.getCoordinateSystem().getDimension() == 3) {
                targetSRID = getStorage3DEPSG();
            } else {
                targetSRID = getStorageEPSG();
            }
            return transform(geometry, targetSRID, sourceCRS, getCRS(targetSRID));
        }
        return geometry;
    }

    /**
     * Transform geometry to this EPSG code
     * 
     * @param geometry
     *            Geometry to transform
     * @param targetSRID
     *            Target EPSG code
     * @return Transformed geometry
     * @throws OwsExceptionReport
     */
    public Geometry transform(final Geometry geometry, final int targetSRID) throws OwsExceptionReport {
        if (geometry != null && !geometry.isEmpty()) {
            if (geometry.getSRID() == targetSRID) {
                return geometry;
            }
            CoordinateReferenceSystem sourceCRS = getCRS(geometry.getSRID());
            CoordinateReferenceSystem targetCRS = getCRS(targetSRID);
            return transform(geometry, targetSRID, sourceCRS, targetCRS);
        }
        return geometry;
    }

    /**
     * Transform geometry
     * 
     * @param geometry
     *            Geometry to transform
     * @param targetSRID
     *            TargetEPSG code
     * @param sourceCRS
     *            Source CRS
     * @param targetCRS
     *            Target CRS
     * @return Transformed geometry
     * @throws OwsExceptionReport
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
        } catch (FactoryException fe) {
            throw new NoApplicableCodeException().causedBy(fe).withMessage("The EPSG code '%s' is not supported!",
                    switchedCoordiantes.getSRID());
        } catch (MismatchedDimensionException mde) {
            throw new NoApplicableCodeException().causedBy(mde).withMessage("The EPSG code '%s' is not supported!",
                    switchedCoordiantes.getSRID());
        } catch (TransformException te) {
            throw new NoApplicableCodeException().causedBy(te).withMessage("The EPSG code '%s' is not supported!",
                    switchedCoordiantes.getSRID());
        }
    }

    /**
     * Get CRS from EPSG code
     * 
     * @param epsgCode
     *            EPSG code to get CRS for
     * @return CRS fro EPSG code
     * @throws CodedException
     *             If the geometry EPSG code is not supported
     */
    private CoordinateReferenceSystem getCRS(final int epsgCode) throws CodedException {
        CoordinateReferenceSystem coordinateReferenceSystem = supportedCRSMap.get(epsgCode);
        if (coordinateReferenceSystem == null) {
            coordinateReferenceSystem = createCRS(epsgCode);
            supportedCRSMap.put(epsgCode, coordinateReferenceSystem);
        }
        return coordinateReferenceSystem;
    }

    /**
     * Create CRS for EPSG code
     * 
     * @param epsgCode
     *            EPSG code to create CRS for
     * @return Created CRS
     * @throws CodedException
     *             If the geometry EPSG code is not supported
     */
    private CoordinateReferenceSystem createCRS(final int epsgCode) throws CodedException {
        try {
            return getCrsAuthorityFactory().createCoordinateReferenceSystem(EPSG_PREFIX + epsgCode);
        } catch (NoSuchAuthorityCodeException nsace) {
            throw new NoApplicableCodeException().causedBy(nsace).withMessage("The EPSG code '%s' is not supported!",
                    epsgCode);
        } catch (FactoryException fe) {
            throw new NoApplicableCodeException().causedBy(fe).withMessage("The EPSG code '%s' is not supported!",
                    epsgCode);
        }
    }

    /**
     * Get CSR authority
     * 
     * @return CRS authority
     */
    private CRSAuthorityFactory getCrsAuthorityFactory() {
        return crsAuthority;
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

    /**
     * Transform envelope from source to target EPSG code
     * 
     * @param envelope
     *            Envelope to transform
     * @param sourceSRID
     *            Source EPSG code
     * @param targetSRID
     *            Target EPSG code
     * @return Transformed envelope
     * @throws CodedException
     *             If the geometry EPSG code is not supported
     */
    public Envelope transformEnvelope(Envelope envelope, int sourceSRID, int targetSRID) throws CodedException {
        if (envelope != null && !envelope.isNull() && targetSRID > 0 && sourceSRID != targetSRID) {
            CoordinateReferenceSystem sourceCRS = getCRS(sourceSRID);
            CoordinateReferenceSystem targetCRS = getCRS(targetSRID);
            try {
                MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
                Envelope transformed = JTS.transform(envelope, transform);
                return transformed;
            } catch (FactoryException fe) {
                throw new NoApplicableCodeException().causedBy(fe).withMessage("The EPSG code '%s' is not supported!",
                        sourceSRID);
            } catch (MismatchedDimensionException mde) {
                throw new NoApplicableCodeException().causedBy(mde).withMessage(
                        "Transformation from EPSG code '%s' to '%s' fails!", sourceSRID, targetSRID);
            } catch (TransformException te) {
                throw new NoApplicableCodeException().causedBy(te).withMessage(
                        "TTransformation from EPSG code '%s' to '%s' fails!", sourceSRID, targetSRID);
            }
        }
        return envelope;
    }

    /**
     * Clears the supported Coordinate Reference Systems map
     */
    @VisibleForTesting
    protected void clearSupportedCRSMap() {
        supportedCRSMap.clear();
    }

    public Set<String> addAuthorityCrsPrefix(Collection<Integer> crses) {
        HashSet<String> withPrefix = Sets.newHashSetWithExpectedSize(crses.size());
        for (Integer crs : crses) {
            withPrefix.add(addAuthorityCrsPrefix(crs));
        }
        return withPrefix;
    }

    public String addAuthorityCrsPrefix(int crs) {
        return new StringBuilder(getAuthority()).append(Constants.DOUBLE_COLON_STRING).append(crs).toString();
    }

    public Set<String> addOgcCrsPrefix(Collection<Integer> crses) {
        HashSet<String> withPrefix = Sets.newHashSetWithExpectedSize(crses.size());
        for (Integer crs : crses) {
            withPrefix.add(addOgcCrsPrefix(crs));
        }
        return withPrefix;
    }

    public String addOgcCrsPrefix(int crs) {
        return new StringBuilder(ServiceConfiguration.getInstance().getSrsNamePrefixSosV2()).append(crs).toString();
    }

}
