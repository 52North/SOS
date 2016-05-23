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

import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.decode.Decoder;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.sensorML.AbstractSensorML;
import org.n52.sos.ogc.sensorML.elements.SmlIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

/**
 * Abstract {@link Decoder} class to decode OGC SensorML
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.2.0
 *
 */
public abstract class AbstractSensorMLDecoder implements Decoder<AbstractSensorML, XmlObject> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSensorMLDecoder.class);

    /**
     * Determine if an SosSMLIdentifier is the unique identifier for a procedure
     * 
     * @param identifier
     *            SosSMLIdentifier to example for unique identifier
     * @return whether the SosSMLIdentifier contains the unique identifier
     */
    protected boolean isIdentificationProcedureIdentifier(final SmlIdentifier identifier) {
        return (checkIdentificationNameForProcedureIdentifier(identifier.getName()) || checkIdentificationDefinitionForProcedureIdentifier(identifier
                .getDefinition()));
    }

    private boolean checkIdentificationNameForProcedureIdentifier(final String name) {
        return !Strings.isNullOrEmpty(name) && name.equals(OGCConstants.URN_UNIQUE_IDENTIFIER_END);
    }

    private boolean checkIdentificationDefinitionForProcedureIdentifier(final String definition) {
        if (Strings.isNullOrEmpty(definition)) {
            return false;
        }
        final Set<String> definitionValues =
                Sets.newHashSet(OGCConstants.URN_UNIQUE_IDENTIFIER, OGCConstants.URN_IDENTIFIER_IDENTIFICATION);
        return definitionValues.contains(definition) || checkDefinitionStartsWithAndContains(definition);
    }

    private boolean checkDefinitionStartsWithAndContains(final String definition) {
        return definition.startsWith(OGCConstants.URN_UNIQUE_IDENTIFIER_START)
                && definition.contains(OGCConstants.URN_UNIQUE_IDENTIFIER_END);
    }
}
