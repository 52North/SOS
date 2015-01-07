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
package org.n52.sos.response;

import java.util.List;

import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.util.CollectionHelper;

/**
 * @since 4.0.0
 * 
 */
public class DescribeSensorResponse extends AbstractServiceResponse {

    private String outputFormat;

    private List<SosProcedureDescription> procedureDescriptions;

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    @Deprecated
    public SosProcedureDescription getSensorDescription() {
        if (isSetProcedureDescriptions()) {
            return getProcedureDescriptions().get(0);
        }
        return null;
    }

    @Deprecated
    public void setSensorDescription(SosProcedureDescription procedureDescription) {
        addSensorDescription(procedureDescription);
    }

    @Override
    public String getOperationName() {
        return SosConstants.Operations.DescribeSensor.name();
    }

    public void setSensorDescriptions(List<SosProcedureDescription> procedureDescriptions) {
        if (isSetProcedureDescriptions()) {
            this.procedureDescriptions =
                    CollectionHelper.conjunctCollections(getProcedureDescriptions(), procedureDescriptions);
        } else {
            this.procedureDescriptions = procedureDescriptions;
        }
    }

    public boolean isSetProcedureDescriptions() {
        return CollectionHelper.isNotEmpty(getProcedureDescriptions());
    }

    public List<SosProcedureDescription> getProcedureDescriptions() {
        return this.procedureDescriptions;
    }

    public void addSensorDescription(SosProcedureDescription procedureDescription) {
        if (isSetProcedureDescriptions()) {
            getProcedureDescriptions().add(procedureDescription);
        } else {
            this.procedureDescriptions = CollectionHelper.list(procedureDescription);
        }
    }
}
