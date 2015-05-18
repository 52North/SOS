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
package org.n52.iceland.ogc.filter;

import org.n52.iceland.ogc.filter.FilterConstants.TimeOperator;
import org.n52.iceland.ogc.gml.time.Time;

/**
 * Temporal filter class
 * 
 * @since 4.0.0
 */
public class TemporalFilter extends Filter<TimeOperator> {

    /**
     * Temporal filter operator
     */
    private TimeOperator operator;

    /**
     * Temporal filter time value
     */
    private Time time;

    /**
     * constructor
     */
    public TemporalFilter() {
        super();
    }

    /**
     * constructor
     * 
     * @param operatorp
     *            Temporal filter operator
     * @param timep
     *            Filter time
     * @param valueReferencep
     *            value reference
     */
    public TemporalFilter(TimeOperator operatorp, Time timep, String valueReferencep) {
        super(valueReferencep);
        this.operator = operatorp;
        this.time = timep;
    }

    /**
     * constructor
     * 
     * @param operatorNamep
     *            Temporal filter operator name
     * @param timep
     *            Filter time
     * @param valueReferencep
     *            value reference
     */
    public TemporalFilter(String operatorNamep, Time timep, String valueReferencep) {
        super(valueReferencep);
        this.operator = TimeOperator.valueOf(operatorNamep);
        this.time = timep;
    }

    @Override
    public TimeOperator getOperator() {
        return operator;
    }

    @Override
    public TemporalFilter setOperator(TimeOperator operator) {
        this.operator = operator;
        return this;
    }

    /**
     * Get filter time
     * 
     * @return filter time
     */
    public Time getTime() {
        return time;
    }

    /**
     * Set filter time
     * 
     * @param time
     *            filter time
     * @return This filter
     */
    public TemporalFilter setTime(Time time) {
        this.time = time;
        return this;
    }

    @Override
    public String toString() {
        return "Temporal filter: " + operator + time.toString();
    }

}
