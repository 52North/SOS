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
package org.n52.sos.w3c;

import javax.xml.namespace.QName;

/**
 * Constants class for W3C
 *
 * @since 4.0.0
 *
 */
public interface W3CConstants {
    // attribute names
    String AN_HREF = "href";

    String AN_TITLE = "title";

    String AN_TYPE = "type";

    String AN_NIL = "nil";
    String AN_SHOW = "show";
    String AN_ARCROLE = "arcrole";
    String AN_ACTUATE = "actuate";
    String AN_ROLE = "role";

    String AN_SCHEMA_LOCATION = "schemaLocation";

    // namespaces and schema locations
    String NS_XLINK = "http://www.w3.org/1999/xlink";

    String NS_XLINK_PREFIX = "xlink";

    String NS_XSI = "http://www.w3.org/2001/XMLSchema-instance";

    String NS_XSI_PREFIX = "xsi";

    String SCHEMA_LOCATION_XLINK = "http://www.w3.org/1999/xlink.xsd";

    String SCHEMA_LOCATION = "schemaLocation";

    String NS_XS = "http://www.w3.org/2001/XMLSchema";

    String NS_XS_PREFIX = "xs";

    QName QN_SCHEMA_LOCATION = new QName(NS_XSI, AN_SCHEMA_LOCATION);

    QName QN_SCHEMA_LOCATION_PREFIXED = new QName(NS_XSI, AN_SCHEMA_LOCATION, NS_XSI_PREFIX);

    QName QN_XSI_TYPE = new QName(NS_XSI, AN_TYPE, NS_XSI_PREFIX);

    QName QN_XLINK_HREF = new QName(NS_XLINK, AN_HREF, NS_XLINK_PREFIX);

    QName QN_XSI_NIL = new QName(NS_XSI, AN_NIL, NS_XSI_PREFIX);

    QName QN_XLINK_TITLE = new QName(NS_XLINK, AN_TITLE, NS_XLINK_PREFIX);

    QName QN_XLINK_TYPE = new QName(NS_XLINK, AN_TYPE, NS_XLINK_PREFIX);
    QName QN_XLINK_ROLE = new QName(NS_XLINK, AN_ROLE, NS_XLINK_PREFIX);
    QName QN_XLINK_ARCROLE = new QName(NS_XLINK, AN_ARCROLE, NS_XLINK_PREFIX);
    QName QN_XLINK_SHOW = new QName(NS_XLINK, AN_SHOW, NS_XLINK_PREFIX);
    QName QN_XLINK_ACTUATE = new QName(NS_XLINK, AN_ACTUATE, NS_XLINK_PREFIX);

    /**
     * Enum for xlink type attribute types
     *
     * @since 4.0.0
     *
     */
    enum TypeType {
        simple, extended, title, resource, locator, arc
    }

    /**
     * Enum for xlink show attribute types. Use toString() to get the value.
     *
     * @since 4.0.0
     *
     */
    enum ShowType {
        newNew("new"), replace("replace"), embed("embed"), other("other"), none("none");

        private String value;

        private ShowType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

    }

    /**
     * Enum for xlink sctuate attribute types
     *
     * @since 4.0.0
     *
     */
    enum ActuateType {
        onLoad, onRequest, other, none
    }
}
