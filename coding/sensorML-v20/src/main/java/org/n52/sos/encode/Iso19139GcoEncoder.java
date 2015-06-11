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

import static org.n52.sos.util.CodingHelper.encoderKeysForElements;
import static org.n52.sos.util.CollectionHelper.union;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.isotc211.x2005.gco.CodeListValueType;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.iso.GcoConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.Role;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.w3c.SchemaLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

/**
 * {@link AbstractXmlEncoder} class to decode ISO TC211 Geographic COmmon (GCO) extensible
 * markup language.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.2.0
 *
 */
public class Iso19139GcoEncoder extends AbstractXmlEncoder<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iso19139GcoEncoder.class);

    @SuppressWarnings("unchecked")
    private static final Set<EncoderKey> ENCODER_KEYS = union(encoderKeysForElements(GcoConstants.NS_GCO,
            Role.class));

    public Iso19139GcoEncoder() {
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!", Joiner.on(", ")
                .join(ENCODER_KEYS));
    }

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return Collections.unmodifiableSet(ENCODER_KEYS);
    }

    @Override
    public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
        return Collections.emptyMap();
    }

    @Override
    public void addNamespacePrefixToMap(final Map<String, String> nameSpacePrefixMap) {
        nameSpacePrefixMap.put(GcoConstants.NS_GCO, GcoConstants.NS_GCO_PREFIX);
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Sets.newHashSet(GcoConstants.GCO_SCHEMA_LOCATION);
    }

    @Override
    public XmlObject encode(Object element, Map<HelperValues, String> additionalValues) throws OwsExceptionReport,
            UnsupportedEncoderInputException {
        XmlObject encodedObject = null;
        if (element instanceof Role) {
            encodedObject = encodeRole((Role) element);
        } else {
            throw new UnsupportedEncoderInputException(this, element);
        }
        LOGGER.debug("Encoded object {} is valid: {}", encodedObject.schemaType().toString(),
                XmlHelper.validateDocument(encodedObject));
        return encodedObject;
    }

    private XmlObject encodeRole(Role role) {
        CodeListValueType circ =
                CodeListValueType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        circ.setStringValue(role.getValue());
        circ.setCodeList(role.getCodeList());
        circ.setCodeListValue(role.getCodeListValue());
        return circ;
    }

}
