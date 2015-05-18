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
import org.n52.iceland.ogc.swe.RangeValue;
import org.n52.iceland.ogc.swe.SweConstants.SweDataComponentType;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 */
public class SweTimeRange extends SweAbstractUomType<RangeValue<DateTime>> {

    private RangeValue<DateTime> value;

    @Override
    public RangeValue<DateTime> getValue() {
        return value;
    }

    @Override
    public String getStringValue() {
        return value.toString();
    }

    @Override
    public boolean isSetValue() {
        return value != null && value.isSetStartValue() && value.isSetEndValue();
    }

    @Override
    public SweTimeRange setValue(final RangeValue<DateTime> value) {
        this.value = value;
        return this;
    }

    @Override
    public SweDataComponentType getDataComponentType() {
        return SweDataComponentType.TimeRange;
    }

}
