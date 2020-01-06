/*
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
package org.n52.sos.coding.encode;

import org.n52.shetland.ogc.ows.service.OwsServiceKey;

import com.google.common.base.Objects;

/**
 * @since 4.0.0
 *
 */
public class ProcedureDescriptionFormatKey {
    private OwsServiceKey serviceOperatorKey;

    private String procedureDescriptionFormat;

    public ProcedureDescriptionFormatKey(OwsServiceKey serviceOperatorKey, String responseFormat) {
        this.serviceOperatorKey = serviceOperatorKey;
        this.procedureDescriptionFormat = responseFormat;
    }

    public ProcedureDescriptionFormatKey() {
        this(null, null);
    }

    @Deprecated
    public OwsServiceKey getServiceOperatorKeyType() {
        return getServiceOperatorKey();
    }

    @Deprecated
    public void setServiceOperatorKeyType(OwsServiceKey serviceOperatorKeyType) {
        setServiceOperatorKey(serviceOperatorKeyType);
    }


    public OwsServiceKey getServiceOperatorKey() {
        return serviceOperatorKey;
    }

    public void setServiceOperatorKey(OwsServiceKey serviceOperatorKey) {
        this.serviceOperatorKey = serviceOperatorKey;
    }

    public String getProcedureDescriptionFormat() {
        return procedureDescriptionFormat;
    }

    public void setProcedureDescriptionFormat(String responseFormat) {
        this.procedureDescriptionFormat = responseFormat;
    }

    public String getService() {
        return getServiceOperatorKey() != null ? getServiceOperatorKey().getService() : null;
    }

    public String getVersion() {
        return getServiceOperatorKey() != null ? getServiceOperatorKey().getVersion() : null;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getServiceOperatorKey(), getProcedureDescriptionFormat());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ProcedureDescriptionFormatKey) {
            ProcedureDescriptionFormatKey o = (ProcedureDescriptionFormatKey) obj;
            return Objects.equal(getServiceOperatorKey(), o.getServiceOperatorKey())
                    && Objects.equal(getProcedureDescriptionFormat(), o.getProcedureDescriptionFormat());
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s[serviceOperatorKeyType=%s, procedureDescriptionFormat=%s]", getClass()
                .getSimpleName(), getServiceOperatorKey(), getProcedureDescriptionFormat());
    }
}
