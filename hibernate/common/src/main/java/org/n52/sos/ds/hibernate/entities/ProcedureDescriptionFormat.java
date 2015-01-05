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

import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasProcedureDescriptionFormat;

/**
 * @since 4.0.0
 * 
 */
public class ProcedureDescriptionFormat implements Serializable {

    private static final long serialVersionUID = 5252037145878336688L;

    public static final String PROCEDURE_DESCRIPTION_FORMAT =
            HasProcedureDescriptionFormat.PROCEDURE_DESCRIPTION_FORMAT;

    private long procedureDescriptionFormatId;

    private String procedureDescriptionFormat;

    public ProcedureDescriptionFormat() {
    }

    public long getProcedureDescriptionFormatId() {
        return this.procedureDescriptionFormatId;
    }

    public void setProcedureDescriptionFormatId(long procedureDescriptionFormatId) {
        this.procedureDescriptionFormatId = procedureDescriptionFormatId;
    }

    public String getProcedureDescriptionFormat() {
        return this.procedureDescriptionFormat;
    }

    public ProcedureDescriptionFormat setProcedureDescriptionFormat(String procedureDescriptionFormat) {
        this.procedureDescriptionFormat = procedureDescriptionFormat;
        return this;
    }

}
