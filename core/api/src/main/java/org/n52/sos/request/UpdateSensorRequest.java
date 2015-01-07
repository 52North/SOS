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

import java.util.ArrayList;
import java.util.List;

import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.response.UpdateSensorResponse;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.StringHelper;

/**
 * class represents a updateSensor request
 * 
 * @since 4.0.0
 */
public class UpdateSensorRequest extends AbstractServiceRequest<UpdateSensorResponse> {

    private String procedureIdentifier;

    private String procedureDescriptionFormat;

    /** SOS SensorML description */
    private List<SosProcedureDescription> procedureDescriptions;

    /**
     * default constructor
     */
    public UpdateSensorRequest() {
        super();
    }

    @Override
    public String getOperationName() {
        return Sos2Constants.Operations.UpdateSensorDescription.name();
    }

    /**
     * @return the procedureIdentifier
     */
    public String getProcedureIdentifier() {
        return procedureIdentifier;
    }

    /**
     * @param procedureIdentifier
     *            the procedureIdentifier to set
     */
    public void setProcedureIdentifier(String procedureIdentifier) {
        this.procedureIdentifier = procedureIdentifier;
    }

    public boolean isSetProcedureIdentifier() {
        return StringHelper.isNotEmpty(getProcedureIdentifier());
    }

    public String getProcedureDescriptionFormat() {
        return procedureDescriptionFormat;
    }

    public void setProcedureDescriptionFormat(String procedureDescriptionFormat) {
        this.procedureDescriptionFormat = procedureDescriptionFormat;
    }

    public boolean isSetProcedureDescriptionFormat() {
        return StringHelper.isNotEmpty(getProcedureDescriptionFormat());
    }

    public List<SosProcedureDescription> getProcedureDescriptions() {
        return procedureDescriptions;
    }

    public void setProcedureDescriptions(List<SosProcedureDescription> procedureDescriptions) {
        this.procedureDescriptions = procedureDescriptions;
    }

    public void addProcedureDescriptionString(SosProcedureDescription procedureDescription) {
        if (procedureDescriptions == null) {
            procedureDescriptions = new ArrayList<SosProcedureDescription>();
        }
        procedureDescriptions.add(procedureDescription);
    }

    public boolean isSetProcedureDescriptions() {
        return CollectionHelper.isNotEmpty(getProcedureDescriptions());
    }

    @Override
    public UpdateSensorResponse getResponse() throws OwsExceptionReport {
        return (UpdateSensorResponse) new UpdateSensorResponse().set(this);
    }
}
