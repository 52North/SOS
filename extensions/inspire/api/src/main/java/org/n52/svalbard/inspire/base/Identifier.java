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
package org.n52.svalbard.inspire.base;

import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.util.StringHelper;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

public class Identifier extends CodeWithAuthority {
    
    private static final long serialVersionUID = -1604778811204130647L;
    private String versionId;

    /**
     * @param localId
     * @param namespace
     */
    public Identifier(String localId, String namespace) {
        super(localId, namespace);
    }
    
    public Identifier(CodeWithAuthority codeWithAuthority) {
        super(codeWithAuthority.getValue(), codeWithAuthority.getCodeSpace());
    }
    
    /**
     * Get localId
     * 
     * @return LocalId
     */
    public String getLocalId() {
        return getValue();
    }

    /**
     * Get namespace
     * 
     * @return Code space
     */
    public String getNamespace() {
        return getCodeSpace();
    }

    /**
     * Set localId and return this Identifier object
     * 
     * @param localId
     *            LocalId to set
     * @return This Identifier object
     */
    public Identifier setLocalId(String localId) {
        setValue(localId);
        return this;
    }

    /**
     * Set namespace and return this Identifier object
     * 
     * @param namespace
     *            Code space to set
     * @return This Identifier object
     */
    public Identifier setNamespace(String namespace) {
        setCodeSpace(namespace);
        return this;
    }

    /**
     * Check whether localId is set
     * 
     * @return <code>true</code> if localId is set
     */
    public boolean isSetLocalId() {
        return StringHelper.isNotEmpty(getLocalId());
    }

    /**
     * Check whether namespace is set
     * 
     * @return <code>true</code> if namespace is set
     */
    public boolean isSetNamespace() {
        return StringHelper.isNotEmpty(getNamespace());
    }

    /**
     * @return the versionId
     */
    public String getVersionId() {
        return versionId;
    }

    /**
     * @param versionId the versionId to set
     */
    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    /**
     * @return <code>true</code>, if versionId is set
     */
    public boolean isSetVersionId() {
        return !Strings.isNullOrEmpty(versionId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getNamespace(), getLocalId());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Identifier)) {
            return false;
        }
        Identifier other = (Identifier) obj;
        if (getNamespace() == null) {
            if (other.getNamespace() != null) {
                return false;
            }
        } else if (!getNamespace().equals(other.getNamespace())) {
            return false;
        }
        if (getLocalId() == null) {
            if (other.getLocalId() != null) {
                return false;
            }
        } else if (!getLocalId().equals(other.getLocalId())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("Identifier [localId=%s, namespace=%s]", getLocalId(), getNamespace());
    }

}
