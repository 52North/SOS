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
package org.n52.sos.ds.hibernate.entities.parameter.observation;

import java.util.Objects;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.ds.hibernate.entities.parameter.ParameterVisitor;
import org.n52.sos.ds.hibernate.entities.parameter.VoidParameterVisitor;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class XmlValuedParameter extends Parameter<String> implements org.n52.sos.ds.hibernate.entities.parameter.XmlValuedParameter {
    private static final long serialVersionUID = 3975237789394582239L;

    private String value;

    public XmlValuedParameter(String value) {
        this.value = value;
    }

    public XmlValuedParameter(XmlObject value) {
        this(encode(value));
    }

    public XmlValuedParameter() {
        this((String)null);
    }

    @Override
    public void accept(VoidParameterVisitor visitor)
            throws OwsExceptionReport {
        visitor.visit(this);
    }

    @Override
    public <T> NamedValue<T> accept(ParameterVisitor<T> visitor)
            throws OwsExceptionReport {
        return visitor.visit(this);
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    public void setValue(XmlObject value) {
        this.value = encode(value);
    }

    public XmlObject getValueAsXml() {
        return decode(this.value);
    }


    @Override
    public boolean isSetValue() {
        return this.value != null && !this.value.isEmpty();
    }

    @Override
    public String getValueAsString() {
        return this.value;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.value);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final XmlValuedParameter other = (XmlValuedParameter) obj;
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return true;
    }

    private static XmlObject decode(String xml)
            throws Error {
        try {
            if (xml == null || xml.isEmpty()) {
                return null;
            } else {
                return XmlObject.Factory.parse(xml);
            }
        } catch (XmlException ex) {
            throw new Error(ex);
        }
    }

    private static String encode(XmlObject xml) {
        return (xml == null) ? null : xml.xmlText();
    }

}
