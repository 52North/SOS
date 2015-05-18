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
package org.n52.iceland.util.http;

import com.google.common.collect.ImmutableSetMultimap;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public interface MediaTypes {
    
    String TEXT = "text";
    
    String XML = "xml";
    
    String APPLICATION = "application";
    
    MediaType WILD_CARD = new MediaType();

    MediaType TEXT_PLAIN = new MediaType(TEXT, "plain");

    MediaType TEXT_XML = new MediaType(TEXT, XML);

    MediaType APPLICATION_XML = new MediaType(APPLICATION, XML);

    MediaType APPLICATION_ZIP = new MediaType(APPLICATION, "zip");

    MediaType APPLICATION_JSON = new MediaType(APPLICATION, "json");
    
    MediaType APPLICATION_EXI = new MediaType(APPLICATION, "exi");

    MediaType APPLICATION_KVP = new MediaType(APPLICATION, "x-kvp");

    MediaType APPLICATION_SOAP_XML = new MediaType(APPLICATION, "soap+xml");

    MediaType APPLICATION_NETCDF = new MediaType(APPLICATION, "netcdf");
    
    MediaType APPLICTION_GML_32 = new MediaType(APPLICATION, "gml+xml", "version", "3.2");
    
    MediaType APPLICTION_OM_20 = new MediaType(APPLICATION, "om+xml", "version", "2.0");
    
    ImmutableSetMultimap<MediaType, MediaType> COMPATIBLE_TYPES = new ImmutableSetMultimap
            .Builder<MediaType, MediaType>()
            .put(TEXT_XML, APPLICATION_XML)
            .put(APPLICATION_XML, TEXT_XML)
            .build();
}
