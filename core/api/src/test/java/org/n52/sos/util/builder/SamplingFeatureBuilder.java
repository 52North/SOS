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
package org.n52.sos.util.builder;

import org.n52.sos.exception.ows.concrete.InvalidSridException;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.util.JTSHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * @since 4.0.0
 */
public class SamplingFeatureBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(SamplingFeatureBuilder.class);

    public static SamplingFeatureBuilder aSamplingFeature() {
        return new SamplingFeatureBuilder();
    }

    private String featureIdentifier;

    private String codespace;

    private double xCoord = Integer.MIN_VALUE;

    private double yCoord = Integer.MIN_VALUE;

    private int epsgCode = Integer.MIN_VALUE;

    private String featureType;

    public SamplingFeatureBuilder setIdentifier(String featureIdentifier) {
        this.featureIdentifier = featureIdentifier;
        return this;
    }

    public SamplingFeatureBuilder setIdentifierCodeSpace(String codespace) {
        this.codespace = codespace;
        return this;
    }

    public SamplingFeatureBuilder setGeometry(double yCoord, double xCoord, int epsgCode) {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.epsgCode = epsgCode;
        return this;
    }

    public AbstractFeature build() {
        SamplingFeature feature = new SamplingFeature(new CodeWithAuthority(featureIdentifier));
        if (codespace != null && !codespace.isEmpty()) {
            feature.getIdentifierCodeWithAuthority().setCodeSpace(codespace);
        }
        if (xCoord != Integer.MIN_VALUE && yCoord != Integer.MIN_VALUE && epsgCode != Integer.MIN_VALUE) {
            GeometryFactory geometryFactory = JTSHelper.getGeometryFactoryForSRID(epsgCode);
            Geometry geom = geometryFactory.createPoint(new Coordinate(xCoord, yCoord));
            try {
                feature.setGeometry(geom);
            } catch (InvalidSridException e) {
                // This should never happen
                e.printStackTrace();
            }
        }
        if (featureType != null && !featureType.isEmpty()) {
            feature.setFeatureType(featureType);
        }
        return feature;
    }

    public SamplingFeatureBuilder setFeatureType(String featureType) {
        this.featureType = featureType;
        return this;
    }

}
