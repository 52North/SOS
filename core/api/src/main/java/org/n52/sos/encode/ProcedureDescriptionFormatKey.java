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
package org.n52.sos.encode;

import org.n52.sos.service.operator.ServiceOperatorKey;

import com.google.common.base.Objects;

/**
 * @since 4.0.0
 * 
 */
public class ProcedureDescriptionFormatKey {
    private ServiceOperatorKey serviceOperatorKeyType;

    private String procedureDescriptionFormat;

    public ProcedureDescriptionFormatKey(ServiceOperatorKey serviceOperatorKeyType, String responseFormat) {
        this.serviceOperatorKeyType = serviceOperatorKeyType;
        this.procedureDescriptionFormat = responseFormat;
    }

    public ProcedureDescriptionFormatKey() {
        this(null, null);
    }

    public ServiceOperatorKey getServiceOperatorKeyType() {
        return serviceOperatorKeyType;
    }

    public void setServiceOperatorKeyType(ServiceOperatorKey serviceOperatorKeyType) {
        this.serviceOperatorKeyType = serviceOperatorKeyType;
    }

    public String getProcedureDescriptionFormat() {
        return procedureDescriptionFormat;
    }

    public void setProcedureDescriptionFormat(String responseFormat) {
        this.procedureDescriptionFormat = responseFormat;
    }

    public String getService() {
        return getServiceOperatorKeyType() != null ? getServiceOperatorKeyType().getService() : null;
    }

    public String getVersion() {
        return getServiceOperatorKeyType() != null ? getServiceOperatorKeyType().getVersion() : null;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getServiceOperatorKeyType(), getProcedureDescriptionFormat());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ProcedureDescriptionFormatKey) {
            ProcedureDescriptionFormatKey o = (ProcedureDescriptionFormatKey) obj;
            return Objects.equal(getServiceOperatorKeyType(), o.getServiceOperatorKeyType())
                    && Objects.equal(getProcedureDescriptionFormat(), o.getProcedureDescriptionFormat());
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s[serviceOperatorKeyType=%s, procedureDescriptionFormat=%s]", getClass()
                .getSimpleName(), getServiceOperatorKeyType(), getProcedureDescriptionFormat());
    }
}
