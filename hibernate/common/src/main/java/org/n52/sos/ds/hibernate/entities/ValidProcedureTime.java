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

import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasProcedure;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasProcedureDescriptionFormat;

/**
 * @since 4.0.0
 * 
 */
public class ValidProcedureTime extends DescriptionXmlEntity implements Serializable, HasProcedure,
        HasProcedureDescriptionFormat {

    private static final long serialVersionUID = -3658568714438752174L;

    public static final String ID = "validProcedureTimeId";

    public static final String START_TIME = "startTime";

    public static final String END_TIME = "endTime";

    private long validProcedureTimeId;

    private Procedure procedure;

    private ProcedureDescriptionFormat procedureDescriptionFormat;

    private Date startTime;

    private Date endTime;

    public long getValidProcedureTimeId() {
        return this.validProcedureTimeId;
    }

    public void setValidProcedureTimeId(long validProcedureTimeId) {
        this.validProcedureTimeId = validProcedureTimeId;
    }

    @Override
    public Procedure getProcedure() {
        return this.procedure;
    }

    @Override
    public void setProcedure(Procedure procedure) {
        this.procedure = procedure;
    }

    @Override
    public ProcedureDescriptionFormat getProcedureDescriptionFormat() {
        return this.procedureDescriptionFormat;
    }

    @Override
    public ValidProcedureTime setProcedureDescriptionFormat(ProcedureDescriptionFormat procedureDescriptionFormat) {
        this.procedureDescriptionFormat = procedureDescriptionFormat;
        return this;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return this.endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
