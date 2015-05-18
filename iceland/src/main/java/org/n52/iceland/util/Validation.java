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

import org.n52.iceland.exception.ConfigurationException;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 * 
 */
public final class Validation {

    public static void notNull(String name, Object val) throws ConfigurationException {
        if (val == null) {
            throw new ConfigurationException(String.format("%s can not be null!", name));
        }
    }

    public static void greaterZero(String name, int i) throws ConfigurationException {
        if (i <= 0) {
            throw new ConfigurationException(String.format("%s can not be smaller or equal zero (was %d)!", name, i));
        }
    }

    public static void greaterEqualZero(String name, int i) throws ConfigurationException {
        if (i < 0) {
            throw new ConfigurationException(String.format("%s can not be smaller than zero (was %d)!", name, i));
        }
    }

    public static void notNullOrEmpty(String name, String val) throws ConfigurationException {
        notNull(name, val);
        if (val.isEmpty()) {
            throw new ConfigurationException(String.format("%s can not be empty!", name));
        }
    }

    private Validation() {
    }
}
