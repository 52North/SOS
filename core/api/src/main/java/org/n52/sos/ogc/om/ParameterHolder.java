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
package org.n52.sos.ogc.om;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.om.values.GeometryValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.util.CollectionHelper;

import com.vividsolutions.jts.geom.Geometry;

public class ParameterHolder {
    
    private SortedSet<NamedValue<?>> parameter = new TreeSet<NamedValue<?>>();
    
    public SortedSet<NamedValue<?>> getParameter() {
        return parameter;
    }
    
    public NamedValue<?> getParameter(String name) {
        if (isSetParameter()) {
            for (NamedValue<?> namedValue : getParameter()) {
                if (namedValue.isSetName() && namedValue.getName().isSetHref()
                        && namedValue.getName().getHref().equals(name)) {
                    return namedValue;
                }
            }
        }
        return null;
    }
    
    public ParameterHolder setParameter(Collection<NamedValue<?>> parameter) {
        parameter.clear();
        if (parameter != null) {
            this.parameter.addAll(parameter);
        }
        return this;
    }
    
    public ParameterHolder addParameter(Collection<NamedValue<?>> parameter) {
        if (parameter != null) {
            this.parameter.addAll(parameter);
        }
        return this;
    }
    
    public ParameterHolder addParameter(NamedValue<?> parameter) {
        if (parameter != null) {
            this.parameter.add(parameter);
        }
        return this;
    }
    
    public boolean removeParameter(NamedValue<?> parameter) {
        if (parameter != null) {
            return this.parameter.remove(parameter);
        }
        return false;
    }
    
    public boolean hasParameter(String name) {
        if (isSetParameter()) {
            for (NamedValue<?> namedValue : getParameter()) {
                if (namedValue.isSetName() && namedValue.getName().isSetHref()
                        && namedValue.getName().getHref().equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }
     
    public boolean isSetParameter() {
        return CollectionHelper.isNotEmpty(getParameter());
    }

    /**
     * Check whether height parameter is set
     * 
     * @return <code>true</code>, if height parameter is set
     */
    public boolean isSetHeightParameter() {
        if (isSetParameter()) {
            for (NamedValue<?> namedValue : getParameter()) {
                if (isHeightParameter(namedValue)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get height parameter
     * 
     * @return Height parameter
     */
    @SuppressWarnings("unchecked")
    public NamedValue<Double> getHeightParameter() {
        if (isSetParameter()) {
            for (NamedValue<?> namedValue : getParameter()) {
                if (isHeightParameter(namedValue)) {
                    return (NamedValue<Double>) namedValue;
                }
            }
        }
        return null;
    }
    
    private boolean isHeightParameter(NamedValue<?> namedValue) {
        return namedValue.isSetName() && namedValue.getName().isSetHref()
                && (namedValue.getName().getHref().equals(OmConstants.PARAMETER_NAME_HEIGHT_URL)
                 || namedValue.getName().getHref().equals(OmConstants.PARAMETER_NAME_HEIGHT)
                 || namedValue.getName().getHref().equals(OmConstants.PARAMETER_NAME_ELEVATION)
                 || namedValue.getName().getHref().equals(OmConstants.PARAMETER_NAME_FROM_DEPTH))
                && namedValue.getValue() instanceof QuantityValue;
    }

    /**
     * Check whether depth parameter is set
     * 
     * @return <code>true</code>, if depth parameter is set
     */
    public boolean isSetDepthParameter() {
        if (isSetParameter()) {
            for (NamedValue<?> namedValue : getParameter()) {
                if (isDepthParameter(namedValue)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get depth parameter
     * 
     * @return Depth parameter
     */
    @SuppressWarnings("unchecked")
    public NamedValue<Double> getDepthParameter() {
        if (isSetParameter()) {
            for (NamedValue<?> namedValue : getParameter()) {
                if (isHeightDepthParameter(namedValue)) {
                    return (NamedValue<Double>) namedValue;
                }
            }
        }
        return null;
    }
    
    private boolean isDepthParameter(NamedValue<?> namedValue) {
        return namedValue.isSetName() && namedValue.getName().isSetHref()
                && (namedValue.getName().getHref().equals(OmConstants.PARAMETER_NAME_DEPTH_URL)
                || namedValue.getName().getHref().equals(OmConstants.PARAMETER_NAME_DEPTH))
                && namedValue.getValue() instanceof QuantityValue;
    }
    
    public boolean isSetHeightDepthParameter() {
        if (isSetParameter()) {
            for (NamedValue<?> namedValue : getParameter()) {
                if (isHeightDepthParameter(namedValue)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public NamedValue<Double> getHeightDepthParameter() {
        if (isSetDepthParameter()) {
            return getDepthParameter();
        }
        return getHeightParameter();
    }

    private boolean isHeightDepthParameter(NamedValue<?> namedValue) {
        return isHeightParameter(namedValue) || isDepthParameter(namedValue);
    }

    public boolean isSetFromToParameter() {
        return isSetFromParameter() || isSetToParameter();
    }

    private boolean isSetToParameter() {
        if (isSetParameter()) {
            for (NamedValue<?> namedValue : getParameter()) {
                if (isToParameter(namedValue)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isSetFromParameter() {
        if (isSetParameter()) {
            for (NamedValue<?> namedValue : getParameter()) {
                if (isFromParameter(namedValue)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean isToParameter(NamedValue<?> namedValue) {
        return namedValue.isSetName() && namedValue.getName().isSetHref()
                && (namedValue.getName().getHref().equals(OmConstants.PARAMETER_NAME_TO_DEPTH)
                    || namedValue.getName().getHref().equals(OmConstants.PARAMETER_NAME_TO_HEIGHT)
                    || namedValue.getName().getHref().equals(OmConstants.PARAMETER_NAME_TO))
                && namedValue.getValue() instanceof QuantityValue;
    }
    
    private boolean isFromParameter(NamedValue<?> namedValue) {
        return namedValue.isSetName() && namedValue.getName().isSetHref()
                && (namedValue.getName().getHref().equals(OmConstants.PARAMETER_NAME_FROM_DEPTH)
                    || namedValue.getName().getHref().equals(OmConstants.PARAMETER_NAME_FROM_HEIGHT)
                    || namedValue.getName().getHref().equals(OmConstants.PARAMETER_NAME_FROM))
                && namedValue.getValue() instanceof QuantityValue;
    }

    @SuppressWarnings("unchecked")
    public NamedValue<Double> getToParameter() {
        if (isSetParameter()) {
            for (NamedValue<?> namedValue : getParameter()) {
                if (isToParameter(namedValue)) {
                    return (NamedValue<Double>) namedValue;
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public NamedValue<Double> getFromParameter() {
        if (isSetParameter()) {
            for (NamedValue<?> namedValue : getParameter()) {
                if (isFromParameter(namedValue)) {
                    return (NamedValue<Double>) namedValue;
                }
            }
        }
        return null;
    }
    
    /**
     * Check whether spatial filtering profile parameter is set
     * 
     * @return <code>true</code>, if spatial filtering profile parameter is set
     */
    public boolean isSetSpatialFilteringProfileParameter() {
        if (isSetParameter()) {
            for (NamedValue<?> namedValue : getParameter()) {
                if (isSamplingGeometryParameter(namedValue)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Add sampling geometry to observation
     * 
     * @param samplingGeometry
     *            The sampling geometry to set
     * @return this
     */
    public ParameterHolder addSpatialFilteringProfileParameter(Geometry samplingGeometry) {
        final NamedValue<Geometry> namedValue = new NamedValue<>();
        namedValue.setName(new ReferenceType(OmConstants.PARAM_NAME_SAMPLING_GEOMETRY));
        namedValue.setValue(new GeometryValue(samplingGeometry));
        addParameter(namedValue);
        return this;
    }

    /**
     * Get spatial filtering profile parameter
     * 
     * @return Spatial filtering profile parameter
     */
    @SuppressWarnings("unchecked")
    public NamedValue<Geometry> getSpatialFilteringProfileParameter() {
        if (isSetParameter()) {
            for (NamedValue<?> namedValue : getParameter()) {
                if (isSamplingGeometryParameter(namedValue)) {
                    return (NamedValue<Geometry>) namedValue;
                }
            }
        }
        return null;
    }

    /**
     * Check whether sampling geometry for spatial filtering profile is set
     * 
     * @return <code>true</code>, if sampling geometry for spatial filtering
     *         profile is set
     */
    private boolean isSamplingGeometryParameter(NamedValue<?> namedValue) {
        return hasParameter(OmConstants.PARAM_NAME_SAMPLING_GEOMETRY)
                && namedValue.getValue() instanceof GeometryValue;
    }
}
