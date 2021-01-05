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
package org.n52.sos.ds.feature.create;

import org.locationtech.jts.geom.Geometry;
import org.n52.series.db.beans.DescribableEntity;
import org.n52.series.db.beans.FeatureEntity;
import org.n52.shetland.ogc.gml.CodeWithAuthority;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.ds.I18nNameDescriptionAdder;

public abstract class AbstractFeatureCreator<T extends FeatureEntity>
        implements FeatureCreator<T>, I18nNameDescriptionAdder {

    public static final String CREATE_FOI_GEOM_FROM_SAMPLING_GEOMS =
            "service.createFeatureGeometryFromSamplingGeometries";

    private FeatureVisitorContext context;

    public AbstractFeatureCreator(FeatureVisitorContext context) {
        this.context = context;
    }

    public CodeWithAuthority getIdentifier(DescribableEntity entity) {
        CodeWithAuthority identifier = new CodeWithAuthority(entity.getIdentifier());
        if (entity.isSetIdentifierCodespace()) {
            identifier.setCodeSpace(entity.getIdentifierCodespace().getName());
        }
        return identifier;
    }

    /**
     * Get the geometry from featureOfInterest object.
     *
     * @param feature
     *            the feature entity
     * @return geometry the geometry
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    protected Geometry createGeometryFrom(FeatureEntity feature) throws OwsExceptionReport {
        if (feature.isSetGeometry()) {
            return getContext().getGeometryHandler()
                    .switchCoordinateAxisFromToDatasourceIfNeeded(feature.getGeometryEntity().getGeometry());
        }
        return null;
    }

    protected FeatureVisitorContext getContext() {
        return context;
    }
}
