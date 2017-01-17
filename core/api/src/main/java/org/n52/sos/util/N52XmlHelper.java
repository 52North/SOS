/*
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.util;


import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPConstants;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlCursor.TokenType;
import org.apache.xmlbeans.XmlObject;

import org.n52.shetland.ogc.OGCConstants;
import org.n52.shetland.ogc.gml.GmlConstants;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.features.SfConstants;
import org.n52.shetland.ogc.ows.OWSConstants;
import org.n52.shetland.ogc.sensorML.SensorMLConstants;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.swe.SweConstants;
import org.n52.shetland.ogc.swes.SwesConstants;
import org.n52.shetland.w3c.SchemaLocation;
import org.n52.shetland.w3c.W3CConstants;

import com.google.common.collect.Sets;

/**
 * Utility class for 52N
 *
 * @since 4.0.0
 *
 */
public final class N52XmlHelper {
    public static final SchemaLocation SCHEMA_LOCATION_OM_100 = new SchemaLocation(OmConstants.NS_OM, OmConstants.SCHEMA_LOCATION_URL_OM_CONSTRAINT);
    public static final SchemaLocation SCHEMA_LOCATION_SOAP_12 = new SchemaLocation(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE, SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE);

    /**
     * Sets the schema location to a XmlObject
     *
     * @param document
     *            XML document
     * @param schemaLocations
     *            schema location
     */
    public static void setSchemaLocationToDocument(XmlObject document, String schemaLocations) {
        XmlCursor cursor = document.newCursor();
        if (cursor.toFirstChild()) {
            cursor.setAttributeText(getSchemaLocationQName(), schemaLocations);
        }
        cursor.dispose();
    }

    /**
     * Sets the schema locations to a XmlObject
     *
     * @param document
     *            XML document
     * @param schemaLocations
     *            List of schema locations
     */
    public static void setSchemaLocationsToDocument(XmlObject document, Collection<SchemaLocation> schemaLocations) {
        setSchemaLocationToDocument(document, mergeSchemaLocationsToString(schemaLocations));
    }

    public static String mergeSchemaLocationsToString(Iterable<SchemaLocation> schemaLocations) {
        if (schemaLocations != null) {
            Iterator<SchemaLocation> it = schemaLocations.iterator();
            if (it.hasNext()) {
                StringBuilder builder = new StringBuilder();
                builder.append(it.next().getSchemaLocationString());
                while(it.hasNext()) {
                    builder.append(" ").append(it.next().getSchemaLocationString());
                }
                return builder.toString();
            }
        }
        return "";
    }

    public static Set<String> getNamespaces(XmlObject xmlObject) {
        Set<String> namespaces = Sets.newHashSet();
        XmlCursor newCursor = xmlObject.newCursor();
        while (newCursor.hasNextToken()) {
            TokenType evt = newCursor.toNextToken();
            if (evt == TokenType.START) {
                QName qn = newCursor.getName();
                if (qn != null) {
                    namespaces.add(qn.getNamespaceURI());
                }
            }
        }
        return namespaces;
    }

    /**
     * W3C XSI schema location
     *
     * @return QName of schema location
     */
    public static QName getSchemaLocationQName() {
        return W3CConstants.QN_SCHEMA_LOCATION;
    }

    /**
     * W3C XSI schema location with prefix
     *
     * @return QName of schema location
     */
    public static QName getSchemaLocationQNameWithPrefix() {
        return W3CConstants.QN_SCHEMA_LOCATION_PREFIXED;
    }

    /**
     * SOS 1.0.0 schema location
     *
     * @return QName of schema location
     */
    public static SchemaLocation getSchemaLocationForSOS100() {
        return Sos1Constants.SOS1_SCHEMA_LOCATION;
    }

    /**
     * SOS 2.0 schema location
     *
     * @return QName of schema location
     */
    public static SchemaLocation getSchemaLocationForSOS200() {
        return Sos2Constants.SOS_SCHEMA_LOCATION;
    }

    /**
     * OM 1.0.0 schema location
     *
     * @return QName of schema location
     */
    public static SchemaLocation getSchemaLocationForOM100() {
        return SCHEMA_LOCATION_OM_100;
    }

    /**
     * OM 2.0 schema location
     *
     * @return QName of schema location
     */
    public static SchemaLocation getSchemaLocationForOM200() {
        return OmConstants.OM_20_SCHEMA_LOCATION;
    }

    /**
     * GML 3.1.1 schema location
     *
     * @return QName of schema location
     */
    public static SchemaLocation getSchemaLocationForGML311() {
        return GmlConstants.GML_311_SCHEMAL_LOCATION;
    }

    /**
     * GML 3.2.1 schema location
     *
     * @return QName of schema location
     */
    public static SchemaLocation getSchemaLocationForGML321() {
        return GmlConstants.GML_32_SCHEMAL_LOCATION;
    }

    /**
     * SOS OGC schema location
     *
     * @return QName of schema location
     */
    public static SchemaLocation getSchemaLocationForOGC() {
        return OGCConstants.OGC_SCHEMA_LOCATION;
    }

    /**
     * OWS 1.1.0 schema location
     *
     * @return QName of schema location
     */
    public static SchemaLocation getSchemaLocationForOWS110() {
        return OWSConstants.OWS_110_SCHEMA_LOCATION;
    }

    /**
     * OWS 1.1.0 schema location
     *
     * @return QName of schema location
     */
    public static SchemaLocation getSchemaLocationForOWS110Exception() {
        return OWSConstants.OWS_110_EXCEPTION_REPORT_SCHEMA_LOCATION;
    }

    /**
     * Sampling 1.0.0 schema location
     *
     * @return QName of schema location
     */
    public static SchemaLocation getSchemaLocationForSA100() {
        return SfConstants.SA_SCHEMA_LOCATION;
    }

    /**
     * Sampling 2.0 schema location
     *
     * @return QName of schema location
     */
    public static SchemaLocation getSchemaLocationForSF200() {
        return SfConstants.SF_SCHEMA_LOCATION;
    }

    /**
     * SamplingSpatial 2.0 schema location
     *
     * @return QName of schema location
     */
    public static SchemaLocation getSchemaLocationForSAMS200() {
        return SfConstants.SAMS_SCHEMA_LOCATION;
    }

    /**
     * SensorML 1.0.1 schema location
     *
     * @return QName of schema location
     */
    public static SchemaLocation getSchemaLocationForSML101() {
        return SensorMLConstants.SML_101_SCHEMA_LOCATION;
    }

    /**
     * SWECommon 1.0.1 schema location
     *
     * @return QName of schema location
     */
    public static SchemaLocation getSchemaLocationForSWE101() {
        return SweConstants.SWE_101_SCHEMA_LOCATION;
    }

    /**
     * SWECommon 2.0 schema location
     *
     * @return QName of schema location
     */
    public static SchemaLocation getSchemaLocationForSWE200() {
        return SweConstants.SWE_20_SCHEMA_LOCATION;
    }

    /**
     * SWECommon 2.0 schema location
     *
     * @return QName of schema location
     */
    public static SchemaLocation getSchemaLocationForSWES200() {
        return SwesConstants.SWES_20_SCHEMA_LOCATION;
    }

    /**
     * W3C XLINK schema location
     *
     * @return QName of schema location
     */
    public static SchemaLocation getSchemaLocationForXLINK() {
        return W3CConstants.XLINK_SCHEMA_LOCATION;
    }

    public static SchemaLocation getSchemaLocationForSOAP12() {
        return SCHEMA_LOCATION_SOAP_12;
    }

    private N52XmlHelper() {
    }
}
