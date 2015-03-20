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
package org.n52.sos.request;

import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.response.DescribeSensorResponse;
import org.n52.sos.util.StringHelper;

/**
 * SOS DescribeSensor request
 * 
 * @since 4.0.0
 */
public class DescribeSensorRequest extends AbstractServiceRequest<DescribeSensorResponse> {

    /**
     * Procedure identifier
     */
    private String procedure;

    /**
     * Output format
     */
    private String procedureDescriptionFormat;

    /**
     * Temporal filters
     */
    private Time validTime;

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sos.request.AbstractSosRequest#getOperationName()
     */
    @Override
    public String getOperationName() {
        return SosConstants.Operations.DescribeSensor.name();
    }

    /**
     * Get output format
     * 
     * @return output format
     */
    public String getProcedureDescriptionFormat() {
        return procedureDescriptionFormat;
    }

    /**
     * Set output format
     * 
     * @param procedureDescriptionFormat
     *            output format
     */
    public void setProcedureDescriptionFormat(String procedureDescriptionFormat) {
        this.procedureDescriptionFormat = procedureDescriptionFormat;
    }

    public boolean isSetProcedureDescriptionFormat() {
        return StringHelper.isNotEmpty(getProcedureDescriptionFormat());
    }

    /**
     * Get Procedure identifier
     * 
     * @return Procedure identifier
     */
    public String getProcedure() {
        return procedure;
    }

    /**
     * Set Procedure identifier
     * 
     * @param procedure
     *            Procedure identifier
     */
    public void setProcedure(String procedure) {
        this.procedure = procedure;
    }

    public boolean isSetProcedure() {
        return StringHelper.isNotEmpty(getProcedure());
    }

    /**
     * Get valid time
     * 
     * @return valid time
     */
    public Time getValidTime() {
        return validTime;
    }

    /**
     * Set valid time
     * 
     * @param validTime
     *            valid time
     */
    public void setValidTime(Time validTime) {
        this.validTime = validTime;
    }

    public boolean isSetValidTime() {
        return getValidTime() != null;
    }

    @Override
    public DescribeSensorResponse getResponse() {
        return (DescribeSensorResponse ) new DescribeSensorResponse().set(this);
    }
}
