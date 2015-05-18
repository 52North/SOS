/**
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.ogc.om.features;

import org.n52.iceland.w3c.SchemaLocation;

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
