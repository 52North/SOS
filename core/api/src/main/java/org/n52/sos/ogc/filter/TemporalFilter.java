/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.sos.ogc.filter;

import org.n52.sos.ogc.filter.FilterConstants.TimeOperator;
import org.n52.sos.ogc.gml.time.Time;

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
