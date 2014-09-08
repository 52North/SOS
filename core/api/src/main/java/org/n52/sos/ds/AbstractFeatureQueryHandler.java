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
package org.n52.sos.ds;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.n52.sos.config.annotation.Setting;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Range;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.util.JTSHelper;
import org.n52.sos.util.JavaHelper;
import org.n52.sos.util.Validation;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Abstract FeatureQueryHandler class to provide default values and mehtods
 * 
 * @since 4.0.0
 * 
 */
@Deprecated
public abstract class AbstractFeatureQueryHandler implements FeatureQueryHandler {

    private List<Range> epsgsWithReversedAxisOrder;

    private int defaultEPSG;

    private int default3DEPSG;

    private boolean spatialDatasource;

    @Override
    public int getDefaultEPSG() {
        return defaultEPSG;
    }

    @Override
    public int getDefault3DEPSG() {
        return default3DEPSG;
    }

    @Setting(FeatureQuerySettingsProvider.DEFAULT_EPSG)
    public void setDefaultEpsg(final int epsgCode) throws ConfigurationException {
        Validation.greaterZero("Default EPSG Code", epsgCode);
        defaultEPSG = epsgCode;
    }

    @Setting(FeatureQuerySettingsProvider.DEFAULT_3D_EPSG)
    public void setDefault3DEpsg(final int epsgCode3D) throws ConfigurationException {
        Validation.greaterZero("Default 3D EPSG Code", epsgCode3D);
        default3DEPSG = epsgCode3D;
    }

    /**
     * @param epsgCode
     *            <p/>
     * @return boolean indicating if coordinates have to be switched
     */
    protected boolean isAxisOrderSwitchRequired(final int epsgCode) {
        for (final Range r : epsgsWithReversedAxisOrder) {
            if (r.contains(epsgCode)) {
                return true;
            }
        }
        return false;
    }

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

    protected boolean isSpatialDatasource() {
        return spatialDatasource;
    }

    @Setting(FeatureQuerySettingsProvider.SPATIAL_DATASOURCE)
    public void setSpatialDatasource(final boolean spatialDatasource) {
        this.spatialDatasource = spatialDatasource;
    }

    protected Geometry switchCoordinateAxisOrderIfNeeded(final Geometry geom) throws OwsExceptionReport {
        if (geom != null && isAxisOrderSwitchRequired(geom.getSRID() == 0 ? getDefaultEPSG() : geom.getSRID())) {
            return JTSHelper.switchCoordinateAxisOrder(geom);
        } else {
            return geom;
        }
    }

    //TODO replace with JavaHelper.asDouble?
    protected double getValueAsDouble(final Object value) {
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

    protected Geometry getFilterForNonSpatialDatasource(final SpatialFilter filter) throws OwsExceptionReport {
        switch (filter.getOperator()) {
        case BBOX:
            return filter.getGeometry();
        default:
            throw new InvalidParameterValueException(Sos2Constants.GetObservationParams.spatialFilter, filter
                    .getOperator().name());
        }
    }

    protected String getWktString(final Object longitude, final Object latitude) {
        final StringBuilder builder = new StringBuilder();
        builder.append("POINT(");
        builder.append(JavaHelper.asString(longitude)).append(" ");
        builder.append(JavaHelper.asString(latitude)).append(" ");
        builder.append(")");
        return builder.toString();
    }

    protected boolean featureIsInFilter(final Geometry geometry, final List<Geometry> envelopes)
            throws OwsExceptionReport {
        for (final Geometry envelope : envelopes) {
            if (envelope.contains(geometry)) {
                return true;
            }
        }
        return false;
    }

}
