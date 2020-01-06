/**
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.entities.observation;

import java.util.Date;

import org.joda.time.DateTime;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.util.GmlHelper;

/**
 * Abstract implementation of {@link TemporalReferencedObservation}.
 *
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 *
 */
public abstract class AbstractTemporalReferencedObservation
        extends AbstractBaseObservation
        implements TemporalReferencedObservation {

    private static final long serialVersionUID = 8704397558609682891L;

    private Date phenomenonTimeStart;
    private Date phenomenonTimeEnd;
    private Date resultTime;
    private Date validTimeStart;
    private Date validTimeEnd;

    @Override
    public Date getPhenomenonTimeStart() {
        return phenomenonTimeStart;
    }

    @Override
    public void setPhenomenonTimeStart(Date phenomenonTimeStart) {
        this.phenomenonTimeStart = phenomenonTimeStart;
    }

    @Override
    public Date getPhenomenonTimeEnd() {
        return phenomenonTimeEnd;
    }

    @Override
    public void setPhenomenonTimeEnd(Date phenomenonTimeEnd) {
        this.phenomenonTimeEnd = phenomenonTimeEnd;
    }

    @Override
    public Date getResultTime() {
        return resultTime;
    }

    @Override
    public void setResultTime(Date resultTime) {
        this.resultTime = resultTime;
    }

    @Override
    public Date getValidTimeStart() {
        return validTimeStart;
    }

    @Override
    public void setValidTimeStart(Date validTimeStart) {
        this.validTimeStart = validTimeStart;
    }

    @Override
    public Date getValidTimeEnd() {
        return validTimeEnd;
    }

    @Override
    public void setValidTimeEnd(Date validTimeEnd) {
        this.validTimeEnd = validTimeEnd;
    }

    @Override
    public boolean isSetValidTime() {
        return getValidTimeStart() != null && getValidTimeEnd() != null;
    }

    /**
     * Create the phenomenon time from {@link AbstractValue}
     * 
     * @param abstractValue
     *            {@link AbstractValue} for get time from
     * @return phenomenon time
     */
    public Time createPhenomenonTime() {
        // create time element
        final DateTime phenStartTime = DateTimeHelper.makeDateTime(getPhenomenonTimeStart());
        DateTime phenEndTime;
        if (getPhenomenonTimeEnd() != null) {
            phenEndTime = DateTimeHelper.makeDateTime(getPhenomenonTimeEnd());
        } else {
            phenEndTime = phenStartTime;
        }
        return GmlHelper.createTime(phenStartTime, phenEndTime);
    }

}
