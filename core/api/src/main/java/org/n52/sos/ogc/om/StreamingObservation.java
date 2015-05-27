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
package org.n52.sos.ogc.om;

import org.n52.iceland.exception.ows.OwsExceptionReport;
import org.n52.iceland.ogc.gml.time.Time;
import org.n52.iceland.ogc.ows.OWSConstants.AdditionalRequestParams;
import org.n52.iceland.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.sos.AbstractStreaming;
import org.n52.sos.util.GeometryHandler;

import com.vividsolutions.jts.geom.Geometry;

/**
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.1.0
 *
 * @param <T>
 */
public abstract class StreamingObservation extends AbstractStreaming {

    /**
     * serial number
     */
    private static final long serialVersionUID = -5759256296641975519L;
    
    @Override
    public Time getPhenomenonTime() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setPhenomenonTime(Time phenomenonTime) {
        // TODO Auto-generated method stub
    }

    @Override
    public Value<OmObservation> getValue() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setValue(Value<OmObservation> value) {
        // TODO Auto-generated method stub
    }
    
    /**
     * Check and modify observation for Spatial Filtering Profile and requested
     * crs
     * 
     * @param observation
     *            {@link OmObservation} to check
     * @throws OwsExceptionReport
     *             If an error occurs when modifying the {@link OmObservation}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void checkForModifications(OmObservation observation) throws OwsExceptionReport {
        if (isSetAdditionalRequestParams() && contains(AdditionalRequestParams.crs)) {
            Object additionalRequestParam = getAdditionalRequestParams(AdditionalRequestParams.crs);
            int targetCRS = -1;
            if (additionalRequestParam instanceof Integer) {
                targetCRS = (Integer) additionalRequestParam;
            } else if (additionalRequestParam instanceof String) {
                targetCRS = Integer.parseInt((String) additionalRequestParam);
            }
            if (observation.isSetParameter()) {
                for (NamedValue<?> namedValue : observation.getParameter()) {
                    if (Sos2Constants.HREF_PARAMETER_SPATIAL_FILTERING_PROFILE.equals(namedValue.getName().getHref())) {
                        NamedValue<Geometry> spatialFilteringProfileParameter = (NamedValue<Geometry>) namedValue;
                        spatialFilteringProfileParameter.getValue().setValue(
                                GeometryHandler.getInstance().transform(
                                        spatialFilteringProfileParameter.getValue().getValue(), targetCRS));
                    }
                }
            }
        }
    }
    
}
