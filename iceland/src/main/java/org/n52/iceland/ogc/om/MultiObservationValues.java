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
package org.n52.iceland.ogc.om;

import org.n52.iceland.ogc.gml.time.Time;
import org.n52.iceland.ogc.om.values.MultiValue;

/**
 * Class representing a multi value observation value
 * 
 * @since 4.0.0
 * 
 * @param <T>
 *            value type
 */
public class MultiObservationValues<T> extends AbstractObservationValue<MultiValue<T>> {
    /**
     * serial number
     */
    private static final long serialVersionUID = 4481588813229272799L;

    /**
     * Mesurement values
     */
    private MultiValue<T> values;

    /**
     * Phenomenon time
     */
    private Time phenomenonTime;

    @Override
    public Time getPhenomenonTime() {
        if (phenomenonTime == null && getValue() != null) {
            phenomenonTime = getValue().getPhenomenonTime();
        }
        return phenomenonTime;
    }

    @Override
    public MultiValue<T> getValue() {
        return values;
    }

    @Override
    public void setValue(MultiValue<T> value) {
        this.values = value;
    }

    @Override
    public void setPhenomenonTime(Time phenomenonTime) {
        this.phenomenonTime = phenomenonTime;
    }

	@Override
	public boolean isSetValue() {
		return getValue() != null && getValue().isSetValue();
	}

}
