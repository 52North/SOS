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
package org.n52.iceland.util;


public interface EpsgConstants {

    String EPSG = "EPSG";

    String EPSG_PREFIX = EPSG + Constants.COLON_STRING;
    
    String EPSG_PREFIX_DOUBLE_COLON = EPSG_PREFIX + Constants.COLON_STRING;

    int EPSG_WGS84_3D = 4979;

    int EPSG_WGS84 = 4326;

    int NOT_SET_EPSG = -1;

}
