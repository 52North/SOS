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
package org.n52.iceland.exception;


import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonProcessingException;

public class JSONException extends JsonProcessingException {
    private static final long serialVersionUID = -62239056976814895L;

    public JSONException(String msg, JsonLocation loc, Throwable rootCause) {
        super(msg, loc, rootCause);
    }

    public JSONException(String msg) {
        super(msg);
    }

    public JSONException(String msg, JsonLocation loc) {
        super(msg, loc);
    }

    public JSONException(String msg, Throwable rootCause) {
        super(msg, rootCause);
    }

    public JSONException(Throwable rootCause) {
        super(rootCause);
    }

}
