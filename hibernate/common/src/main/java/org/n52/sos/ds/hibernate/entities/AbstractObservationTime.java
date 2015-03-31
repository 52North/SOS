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
package org.n52.sos.ds.hibernate.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasDeletedFlag;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasObservationId;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasOfferings;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasPhenomenonTime;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasResultTime;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasSamplingGeometry;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasValidTime;
import org.n52.sos.ds.hibernate.entities.values.AbstractValue;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.util.GmlHelper;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Abstract class for value time
 *
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 *
 */
public abstract class AbstractObservationTime
        extends AbstractIdentifierNameDescriptionEntity
        implements Serializable,
                   HasDeletedFlag,
                   HasOfferings,
                   HasPhenomenonTime,
                   HasResultTime,
                   HasValidTime,
                   HasObservationId,
                   HasSamplingGeometry {

    private static final long serialVersionUID = 8704397558609682891L;

    private long observationId;
    private Date phenomenonTimeStart;
    private Date phenomenonTimeEnd;
    private Date resultTime;
    private Date validTimeStart;
    private Date validTimeEnd;
    private Set<Offering> offerings = new HashSet<Offering>(0);
    private boolean deleted;

    private Geometry samplingGeometry;

    @Override
    public long getObservationId() {
        return observationId;
    }

    @Override
    public void setObservationId(final long observationId) {
        this.observationId = observationId;
    }

    @Override
    public Date getPhenomenonTimeStart() {
        return phenomenonTimeStart;
    }

    @Override
    public void setPhenomenonTimeStart(final Date phenomenonTimeStart) {
        this.phenomenonTimeStart = phenomenonTimeStart;
    }

    @Override
    public Date getPhenomenonTimeEnd() {
        return phenomenonTimeEnd;
    }

    @Override
    public void setPhenomenonTimeEnd(final Date phenomenonTimeEnd) {
        this.phenomenonTimeEnd = phenomenonTimeEnd;
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

    @Override
    public Date getResultTime() {
        return resultTime;
    }

    @Override
    public void setResultTime(final Date resultTime) {
        this.resultTime = resultTime;
    }

    @Override
    public Date getValidTimeStart() {
        return validTimeStart;
    }

    @Override
    public void setValidTimeStart(final Date validTimeStart) {
        this.validTimeStart = validTimeStart;
    }

    @Override
    public Date getValidTimeEnd() {
        return validTimeEnd;
    }

    @Override
    public void setValidTimeEnd(final Date validTimeEnd) {
        this.validTimeEnd = validTimeEnd;
    }

    @Override
    public boolean isSetValidTime() {
        return getValidTimeStart() != null && getValidTimeEnd() != null;
    }

    @Override
    public HasDeletedFlag setDeleted(boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public boolean getDeleted() {
        return deleted;
    }

    @Override
    public Set<Offering> getOfferings() {
        return offerings;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setOfferings(final Object offerings) {
        if (offerings instanceof Set<?>) {
            this.offerings = (Set<Offering>) offerings;
        } else {
            getOfferings().add((Offering) offerings);
        }
    }
    
    @Override
    public Geometry getSamplingGeometry() {
        return samplingGeometry;
    }
    
    @Override
    public void setSamplingGeometry(Geometry samplingGeometry) {
        this.samplingGeometry = samplingGeometry;
    }
    
    @Override
    public boolean hasSamplingGeometry() {
        return getSamplingGeometry() != null && !getSamplingGeometry().isEmpty();
    }

}
