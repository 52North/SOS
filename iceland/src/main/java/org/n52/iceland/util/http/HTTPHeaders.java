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

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public interface HTTPHeaders {
    String AUTHORIZATION = "Authorization";

    String ACCEPT_ENCODING = "Accept-Encoding";

    String CONTENT_ENCODING = "Content-Encoding";

    String ALLOW = "Allow";

    String CONTENT_TYPE = "Content-Type";

    String ACCEPT = "Accept";

    String LOCATION = "Location";

    String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

    String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";

    String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";

    String X_FORWARDED_FOR = "X-Forwarded-For";
}
