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
package org.n52.sos.ogc.om.features;

import org.n52.sos.w3c.SchemaLocation;

/**
 * Interface for SamplingFeature constants
 * 
 * @since 4.0.0
 */
public interface SfConstants {

    // namespaces and schema lcations
    String NS_SA = "http://www.opengis.net/sampling/1.0";

    String NS_SA_PREFIX = "sa";

    String NS_SF = "http://www.opengis.net/sampling/2.0";

    String NS_SF_PREFIX = "sf";

    String NS_SAMS = "http://www.opengis.net/samplingSpatial/2.0";

    String NS_SAMS_PREFIX = "sams";

    String SCHEMA_LOCATION_URL_SA = "http://schemas.opengis.net/sampling/1.0.0/sampling.xsd";

    String SCHEMA_LOCATION_URL_SF = "http://schemas.opengis.net/sampling/2.0/samplingFeature.xsd";

    String SCHEMA_LOCATION_URL_SAMS = "http://schemas.opengis.net/samplingSpatial/2.0/spatialSamplingFeature.xsd";

    SchemaLocation SA_SCHEMA_LOCATION = new SchemaLocation(NS_SA, SCHEMA_LOCATION_URL_SA);

    SchemaLocation SF_SCHEMA_LOCATION = new SchemaLocation(NS_SF, SCHEMA_LOCATION_URL_SF);

    SchemaLocation SAMS_SCHEMA_LOCATION = new SchemaLocation(NS_SAMS, SCHEMA_LOCATION_URL_SAMS);

    // feature types
    String SAMPLING_FEAT_TYPE_SF_SAMPLING_FEATURE =
            "http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingFeature";

    String SAMPLING_FEAT_TYPE_SF_SPATIAL_SAMPLING_FEATURE =
            "http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SpatialSamplingFeature";

    String SAMPLING_FEAT_TYPE_SF_SAMPLING_POINT =
            "http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingPoint";

    String SAMPLING_FEAT_TYPE_SF_SAMPLING_CURVE =
            "http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingCurve";

    String SAMPLING_FEAT_TYPE_SF_SAMPLING_SURFACE =
            "http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingSurface";

    String SAMPLING_FEAT_TYPE_SF_SAMPLING_SOLID =
            "http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingSolid";

    String SAMPLING_FEAT_TYPE_SF_SAMPLING_SPECIMEN =
            "http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingSpecimen";

    // element names
    String EN_SAMPLINGPOINT = "SamplingPoint";

    String EN_SAMPLINGSURFACE = "SamplingSurface";

    String EN_SAMPLINGCURVE = "SamplingCurve";

    String FT_SAMPLINGPOINT = NS_SA_PREFIX + ":" + EN_SAMPLINGPOINT;

    String FT_SAMPLINGSURFACE = NS_SA_PREFIX + ":" + EN_SAMPLINGSURFACE;

    String FT_SAMPLINGCURVE = NS_SA_PREFIX + ":" + EN_SAMPLINGCURVE;

}
