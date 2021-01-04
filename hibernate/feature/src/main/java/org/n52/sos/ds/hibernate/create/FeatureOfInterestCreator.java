/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.create;

import org.locationtech.jts.geom.Geometry;
import org.n52.series.db.beans.FeatureEntity;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.CodeWithAuthority;
import org.n52.shetland.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;

public class FeatureOfInterestCreator extends AbstractFeatureOfInerestCreator<FeatureEntity> {

    public FeatureOfInterestCreator(FeatureVisitorContext context) {
        super(context);
    }

    @Override
    public AbstractFeature create(FeatureEntity f)
            throws OwsExceptionReport {
        return createFeature(f);
    }

    @Override
    public Geometry createGeometry(FeatureEntity feature) throws OwsExceptionReport {
        return createGeometryFrom(feature);
    }

    @Override
    protected AbstractFeature createFeature(CodeWithAuthority identifier) {
        return new SamplingFeature(identifier);
    }

}
