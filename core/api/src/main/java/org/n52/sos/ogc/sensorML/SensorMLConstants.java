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

import javax.xml.namespace.QName;

import org.n52.sos.util.http.MediaType;
import org.n52.sos.w3c.SchemaLocation;

/**
 * Constants class for SensorML
 * 
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 * @author ShaneStClair
 * 
 * @since 4.0.0
 */
public interface SensorMLConstants {

    // namespaces and schema locations
    String NS_SML = "http://www.opengis.net/sensorML/1.0.1";

    String NS_SML_PREFIX = "sml";

    String SCHEMA_LOCATION_URL_SML_101 = "http://schemas.opengis.net/sensorML/1.0.1/sensorML.xsd";

    SchemaLocation SML_101_SCHEMA_LOCATION = new SchemaLocation(NS_SML, SCHEMA_LOCATION_URL_SML_101);

    String SENSORML_OUTPUT_FORMAT_URL = NS_SML;

    MediaType SENSORML_CONTENT_TYPE = new MediaType("text", "xml", "subtype", "sensorML/1.0.1");

    String SENSORML_OUTPUT_FORMAT_MIME_TYPE = SENSORML_CONTENT_TYPE.toString();

    String EN_SYSTEM = "System";

    String EN_PROCESS_MODEL = "ProcessModel";

    String EN_COMPONENT = "Component";

    String EN_ABSTRACT_PROCESS = "AbstractProcess";

    QName SYSTEM_QNAME = new QName(NS_SML, EN_SYSTEM, NS_SML_PREFIX);

    QName PROCESS_MODEL_QNAME = new QName(NS_SML, EN_PROCESS_MODEL, NS_SML_PREFIX);

    QName COMPONENT_QNAME = new QName(NS_SML, EN_COMPONENT, NS_SML_PREFIX);

    QName ABSTRACT_PROCESS_QNAME = new QName(NS_SML, EN_ABSTRACT_PROCESS, NS_SML_PREFIX);

    String VERSION_V101 = "1.0.1";

    /**
     * Name of a SensorML element describing the offerings a procedure/sensor is
     * related to or should be inserted into
     */
    String ELEMENT_NAME_OFFERINGS = "offerings";

    // FIXME use a proper URI/URN for this, e.g. from settings
    String OFFERING_FIELD_DEFINITION = "http://www.opengis.net/def/offering/identifier";

    /**
     * name of System capabilities containing parent procedures
     */
    String ELEMENT_NAME_PARENT_PROCEDURES = "parentProcedures";

    // FIXME use a proper URI/URN for this, e.g. from settings
    String PARENT_PROCEDURE_FIELD_DEFINITION = "http://www.opengis.net/def/procedure/identifier";

    String PARENT_PROCEDURE_FIELD_NAME = "parentProcedureID";

    /**
     * name of System components containing child procedures
     */
    String ELEMENT_NAME_CHILD_PROCEDURES = "childProcedure";

    /**
     * name of System capabilities containing featureOfInterest
     */
    String ELEMENT_NAME_FEATURES_OF_INTEREST = "featuresOfInterest";

    // FIXME use a proper URI/URN for this, e.g. from settings
    String FEATURE_OF_INTEREST_FIELD_DEFINITION = "http://www.opengis.net/def/featureOfInterest/identifier";

    String FEATURE_OF_INTEREST_FIELD_NAME = "featureOfInterestID";

    String ELEMENT_NAME_SHORT_NAME = "shortName";

    String ELEMENT_NAME_LONG_NAME = "longName";

    String ELEMENT_NAME_OBSERVED_BBOX = "observedBBOX";

	String OBSERVED_BBOX_DEFINITION_URN = "urn:ogc:def:property:OGC:1.0:observedBBOX";

    String DEFAULT_FIELD_NAME = "field_";
}
