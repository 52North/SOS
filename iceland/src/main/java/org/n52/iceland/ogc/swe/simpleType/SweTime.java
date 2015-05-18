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
package org.n52.iceland.ogc.swe.simpleType;

import org.joda.time.DateTime;
import org.n52.iceland.ogc.swe.SweConstants.SweDataComponentType;
import org.n52.iceland.util.DateTimeHelper;

/**
 * SOS internal representation of SWE simpleType time
 * 
 * @since 4.0.0
 */
public class SweTime extends SweAbstractUomType<DateTime> {

    /**
     * value
     */
    private DateTime value;

    @Override
    public DateTime getValue() {
        return value;
    }

    @Override
    public SweTime setValue(final DateTime value) {
        this.value = value;
        return this;
    }

    @Override
    public String getStringValue() {
        if (isSetValue()) {
            return DateTimeHelper.formatDateTime2IsoString(value);
        }
        return null;
    }

    @Override
    public boolean isSetValue() {
        return value != null;
    }

    @Override
    public SweDataComponentType getDataComponentType() {
        return SweDataComponentType.Time;
    }

}
