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

import java.util.Set;

import org.n52.iceland.ogc.gml.time.Time;
import org.n52.iceland.ogc.om.quality.OmResultQuality;
import org.n52.iceland.ogc.om.values.Value;
import org.n52.iceland.util.CollectionHelper;

import com.google.common.collect.Sets;

/**
 * Class representing a single value observation value
 * 
 * @since 4.0.0
 * 
 * @param <T>
 *            value type
 */
public class SingleObservationValue<T> extends AbstractObservationValue<Value<T>> {
    /**
     * serial number
     */
    private static final long serialVersionUID = -8162038672393523937L;

    /**
     * Phenomenon time
     */
    private Time phenomenonTime;

    /**
     * Measurement value
     */
    private Value<T> value;

    /**
     * Measurment quality
     */
    private Set<OmResultQuality> qualityList = Sets.newHashSet();

    /**
     * constructor
     */
    public SingleObservationValue() {
    }

    /**
     * constructor
     * 
     * @param value
     *            Measurement value
     */
    public SingleObservationValue(Value<T> value) {
        this.value = value;
    }

    /**
     * constructor
     * 
     * @param phenomenonTime
     *            Phenomenon time
     * @param value
     *            Measurement value
     * @param qualityList
     *            Measurment quality
     */
    public SingleObservationValue(Time phenomenonTime, Value<T> value, Set<OmResultQuality> qualityList) {
        this.phenomenonTime = phenomenonTime;
        this.value = value;
        this.qualityList = qualityList;
    }

    /**
     * constructor
     * 
     * @param phenomenonTime
     *            Phenomenon time
     * @param value
     *            Measurement value
     */
    public SingleObservationValue(Time phenomenonTime, Value<T> value) {
        this.phenomenonTime = phenomenonTime;
        this.value = value;
    }

    @Override
    public Time getPhenomenonTime() {
        return phenomenonTime;
    }

    @Override
    public void setPhenomenonTime(Time phenomenonTime) {
        this.phenomenonTime = phenomenonTime;
    }

    @Override
    public Value<T> getValue() {
        return value;
    }

    @Override
    public void setValue(Value<T> value) {
        this.value = value;
    }

    /**
     * Set measurement quality
     * 
     * @param qualityList
     *            Measurement quality to set
     */
    public SingleObservationValue<T> setQualityList(Set<OmResultQuality> qualityList) {
        this.qualityList = qualityList;
        return this;
    }
    
    public SingleObservationValue<T> addQualityList(Set<OmResultQuality> qualityList) {
        this.qualityList.addAll(qualityList);
        return this;
    }
    
    public SingleObservationValue<T> addQuality(OmResultQuality qualityList) {
        this.qualityList.add(qualityList);
        return this;
    }

    /**
     * Get measurement quality
     * 
     * @return Measurement quality
     */
    public Set<OmResultQuality> getQualityList() {
        return qualityList;
    }
    
    public boolean isSetQualityList() {
        return CollectionHelper.isNotEmpty(getQualityList());
    }
    
	@Override
	public boolean isSetValue() {
		return getValue() != null && getValue().isSetValue();
	}
}
