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
package org.n52.sos.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.n52.sos.config.SettingsManager;
import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.config.annotation.Setting;
import org.n52.sos.ds.FeatureQuerySettingsProvider;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Range;
import org.n52.sos.ogc.sos.Sos2Constants;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Class to provide some methods for JTS Geometry which is used by
 * {@link org.n52.sos.ds.FeatureQueryHandler} and SpatialFilteringProfile DAO.
 * 
 * @since 4.0.0
 * 
 */
@Configurable
public class GeometryHandler {

    private static GeometryHandler instance;

    private static ReentrantLock creationLock = new ReentrantLock();    
    
    private List<Range> epsgsWithReversedAxisOrder;

    private int defaultEPSG;

    private int default3DEPSG;

    private boolean spatialDatasource;

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
                    instance = newInstance;
                }
            } finally {
                creationLock.unlock();
            }            
        }
        return instance;
    }

    /**
     * Get configured default EPSG code
     * 
     * @return Default EPSG code
     */
    public int getDefaultEPSG() {
        return defaultEPSG;
    }

    /**
     * Get configured default 3D EPSG code
     * 
     * @return Default 3D EPSG code
     */
    public int getDefault3DEPSG() {
        return default3DEPSG;
    }

    /**
     * Set default EPSG code from settings
     * 
     * @param epsgCode
     *            EPSG code from settings
     * @throws ConfigurationException
     *             If an error occurs
     */
    @Setting(FeatureQuerySettingsProvider.DEFAULT_EPSG)
    public void setDefaultEpsg(final int epsgCode) throws ConfigurationException {
        Validation.greaterZero("Default EPSG Code", epsgCode);
        defaultEPSG = epsgCode;
    }

    /**
     * Set default 3D EPSG code from settings
     * 
     * @param epsgCode3D
     *            3D EPSG code from settings
     * @throws ConfigurationException
     *             If an error occurs
     */
    @Setting(FeatureQuerySettingsProvider.DEFAULT_3D_EPSG)
    public void setDefault3DEpsg(final int epsgCode3D) throws ConfigurationException {
        Validation.greaterZero("Default 3D EPSG Code", epsgCode3D);
        default3DEPSG = epsgCode3D;
    }

    /**
     * Set the EPSG code ranges for which the coordinates should be switched
     * 
     * @param codes
     *            EPSG code ranges
     * @throws ConfigurationException
     *             If an error occurs
     */
    @Setting(FeatureQuerySettingsProvider.EPSG_CODES_WITH_REVERSED_AXIS_ORDER)
    public void setEpsgCodesWithReversedAxisOrder(final String codes) throws ConfigurationException {
        Validation.notNullOrEmpty("EPSG Codes to switch coordinates for", codes);
        final String[] splitted = codes.split(";");
        epsgsWithReversedAxisOrder = new ArrayList<Range>(splitted.length);
        for (final String entry : splitted) {
            final String[] splittedEntry = entry.split("-");
            Range r = null;
            if (splittedEntry.length == 1) {
                r = new Range(Integer.parseInt(splittedEntry[0]), Integer.parseInt(splittedEntry[0]));
            } else if (splittedEntry.length == 2) {
                r = new Range(Integer.parseInt(splittedEntry[0]), Integer.parseInt(splittedEntry[1]));
            } else {
                throw new ConfigurationException(String.format("Invalid format of entry in '%s': %s",
                        FeatureQuerySettingsProvider.EPSG_CODES_WITH_REVERSED_AXIS_ORDER, entry));
            }
            epsgsWithReversedAxisOrder.add(r);
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
     * @param epsgCode
     *            <p/>
     * @return boolean indicating if coordinates have to be switched
     */
    public boolean isAxisOrderSwitchRequired(final int epsgCode) {
        for (final Range r : epsgsWithReversedAxisOrder) {
            if (r.contains(epsgCode)) {
                return true;
            }
        }
        return false;
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
     * Switch Geometry coordinates if necessary
     * 
     * @param geom
     *            Geometry to switch coordinates
     * @return Geometry with switched coordinates
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    public Geometry switchCoordinateAxisOrderIfNeeded(final Geometry geom) throws OwsExceptionReport {
        if (geom != null && isAxisOrderSwitchRequired(geom.getSRID() == 0 ? getDefaultEPSG() : geom.getSRID())) {
            return JTSHelper.switchCoordinateAxisOrder(geom);
        } else {
            return geom;
        }
    }

    /**
     * Get Object value as Double value
     * 
     * @param value
     *            Value to check
     * @return Double value
     */
    //TODO replace with JavaHelper.asDouble?
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
            return filter.getGeometry();
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
        builder.append("POINT(");
        builder.append(JavaHelper.asString(longitude)).append(" ");
        builder.append(JavaHelper.asString(latitude)).append(" ");
        builder.append(")");
        return builder.toString();
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
        for (final Geometry envelope : envelopes) {
            if (envelope.contains(geometry)) {
                return true;
            }
        }
        return false;
    }
}
