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
package org.n52.sos.ogc.sensorML;

import org.n52.sos.iso.CodeList;

/**
 * Class that represents SensorML Role
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.2.0
 *
 */
public class Role {

    private String value;

    private String codeList = CodeList.CI_ROLE_CODE_URL;

    private String codeListValue = CodeList.CiRoleCodes.CI_RoleCode_pointOfContact.name();

    public Role(String value) {
        this.value = value;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public Role setValue(String value) {
        this.value = value;
        return this;
    }

    /**
     * @return the codeList
     */
    public String getCodeList() {
        return codeList;
    }

    /**
     * @param codeList
     *            the codeList to set
     */
    public Role setCodeList(String codeList) {
        this.codeList = codeList;
        return this;
    }

    /**
     * @return the codeListValue
     */
    public String getCodeListValue() {
        return codeListValue;
    }

    /**
     * @param codeListValue
     *            the codeListValue to set
     */
    public Role setCodeListValue(String codeListValue) {
        this.codeListValue = codeListValue;
        return this;
    }

    /**
     * @param codeListValue
     *            the codeListValue to set
     */
    public Role setCodeListValue(CodeList.CiRoleCodes codeListValue) {
        this.codeListValue = codeListValue.name();
        return this;
    }

}
