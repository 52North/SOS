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
package org.n52.sos.ogc.swes;

import javax.xml.namespace.QName;

import org.n52.sos.util.XmlHelper;
import org.n52.sos.w3c.SchemaLocation;

/**
 * @since 4.0.0
 * 
 */
public interface SwesConstants {

    String EN_SOS_INSERTION_METADATA = "SosInsertionMetadata";

    String NS_SWES_20 = "http://www.opengis.net/swes/2.0";

    String NS_SWES_PREFIX = "swes";
    
    String XPATH_PREFIXES_SWES = XmlHelper.getXPathPrefix(NS_SWES_PREFIX, NS_SWES_20);

    String SCHEMA_LOCATION_URL_SWES_20 = "http://schemas.opengis.net/swes/2.0/swes.xsd";

    String SCHEMA_LOCATION_URL_SWES_20_DESCRIBE_SENSOR = "http://schemas.opengis.net/swes/2.0/swesDescribeSensor.xsd";

    String SCHEMA_LOCATION_URL_SWES_20_DELETE_SENSOR = "http://schemas.opengis.net/swes/2.0/swesDeleteSensor.xsd";

    String SCHEMA_LOCATION_URL_SWES_20_INSERT_SENSOR = "http://schemas.opengis.net/swes/2.0/swesInsertSensor.xsd";

    String SCHEMA_LOCATION_URL_SWES_20_UPDATE_SENSOR_DESCRIPTION =
            "http://schemas.opengis.net/swes/2.0/swesUpdateSensorDescription.xsd";

    SchemaLocation SWES_20_SCHEMA_LOCATION = new SchemaLocation(NS_SWES_20, SCHEMA_LOCATION_URL_SWES_20);

    SchemaLocation SWES_20_DESCRIBE_SENSOR_SCHEMA_LOCATION = new SchemaLocation(NS_SWES_20,
            SCHEMA_LOCATION_URL_SWES_20_DESCRIBE_SENSOR);

    SchemaLocation SWES_20_INSERT_SENSOR_SCHEMA_LOCATION = new SchemaLocation(NS_SWES_20,
            SCHEMA_LOCATION_URL_SWES_20_INSERT_SENSOR);

    SchemaLocation SWES_20_UPDATE_SENSOR_DESCRIPTION_SCHEMA_LOCATION = new SchemaLocation(NS_SWES_20,
            SCHEMA_LOCATION_URL_SWES_20_UPDATE_SENSOR_DESCRIPTION);

    SchemaLocation SWES_20_DELETE_SENSOR_SCHEMA_LOCATION = new SchemaLocation(NS_SWES_20,
            SCHEMA_LOCATION_URL_SWES_20_DELETE_SENSOR);

    // element names
    String EN_EXTENSION = "extension";
    
    String EN_ABSTRACT_OFFERING = "AbstractOffering";

    String EN_DELETE_SENSOR_RESPONSE = "DeleteSensorResponse";

    String EN_DESCRIBE_SENSOR = "DescribeSensor";

    String EN_DELETE_SENSOR = "DeleteSensor";

    String EN_DESCRIBE_SENSOR_RESPONSE = "DescribeSensorResponse";

    String EN_INSERT_SENSOR = "InsertSensor";

    String EN_INSERT_SENSOR_RESPONSE = "InsertSensorResponse";

    String EN_INSERTION_METADATA = "InsertionMetadata";

    String EN_METADATA = "metadata";

    String EN_OFFERING = "offering";

    String EN_UPDATE_SENSOR_DESCRIPTION = "UpdateSensorDescription";

    String EN_UPDATE_SENSOR_DESCRIPTION_RESPONSE = "UpdateSensorDescriptionResponse";

    QName QN_INSERTION_METADATA = new QName(SwesConstants.NS_SWES_20, SwesConstants.EN_INSERTION_METADATA,
            SwesConstants.NS_SWES_PREFIX);

    // QNames for elements
    QName QN_ABSTRACT_OFFERING = new QName(NS_SWES_20, EN_ABSTRACT_OFFERING, NS_SWES_PREFIX);

    QName QN_DELETE_SENSOR = new QName(NS_SWES_20, EN_DELETE_SENSOR, NS_SWES_PREFIX);

    QName QN_DELETE_SENSOR_RESPONSE = new QName(NS_SWES_20, EN_DELETE_SENSOR_RESPONSE, NS_SWES_PREFIX);

    QName QN_DESCRIBE_SENSOR = new QName(NS_SWES_20, EN_DESCRIBE_SENSOR, NS_SWES_PREFIX);

    QName QN_DESCRIBE_SENSOR_RESPONSE = new QName(NS_SWES_20, EN_DESCRIBE_SENSOR_RESPONSE, NS_SWES_PREFIX);

    QName QN_INSERT_SENSOR = new QName(NS_SWES_20, EN_INSERT_SENSOR, NS_SWES_PREFIX);

    QName QN_INSERT_SENSOR_RESPONSE = new QName(NS_SWES_20, EN_INSERT_SENSOR_RESPONSE, NS_SWES_PREFIX);

    QName QN_METADATA = new QName(NS_SWES_20, EN_METADATA, NS_SWES_PREFIX);

    QName QN_OFFERING = new QName(NS_SWES_20, EN_OFFERING, NS_SWES_PREFIX);

    QName QN_UPDATE_SENSOR_DESCRIPTION = new QName(NS_SWES_20, EN_UPDATE_SENSOR_DESCRIPTION, NS_SWES_PREFIX);

    QName QN_UPDATE_SENSOR_DESCRIPTION_RESPONSE = new QName(NS_SWES_20, EN_UPDATE_SENSOR_DESCRIPTION_RESPONSE,
            NS_SWES_PREFIX);

    String SOAP_REASON_INVALID_REQUEST = "The request did not conform to its XML Schema definition.";

    String SOAP_REASON_REQUEST_EXTENSION_NOT_SUPPORTED = ""; // FIXME emtpy
                                                             // constant

    /**
     * Interface to identify if the implemented class supportes
     * {@link SwesExtensions}
     * 
     * @author Carsten Hollmann <c.hollmann@52north.org>
     * @since 4.1.0
     * 
     * @param <T>
     */
    interface HasSwesExtension<T> {
        /**
         * Get the {@link SwesExtension}s
         * 
         * @return {@link SwesExtensions} with {@link SwesExtension}s
         */
        public SwesExtensions getExtensions();

        /**
         * Set the {@link SwesExtensions} object
         * 
         * @param extensions
         *            the {@link SwesExtensions} object to set
         * @return this
         */
        public T setExtensions(final SwesExtensions extensions);
        
        /**
         * Add a {@link SwesExtensions} to this object
         * 
         * @param extension
         *            the {@link SwesExtensions} to add
         * @return this
         */
        public T addExtensions(final SwesExtensions extension);

        /**
         * Add a {@link SwesExtension} to this object
         * 
         * @param extension
         *            the {@link SwesExtension} to add
         * @return this
         */
        @SuppressWarnings("rawtypes")
        public T addExtension(final SwesExtension extension);

        /**
         * Check if {@link SwesExtension}s are set
         * 
         * @return <code>true</code>, if {@link SwesExtensions} is not null or
         *         empty
         */
        public boolean isSetExtensions();
    }

}
