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
package org.n52.svalbard.encode.inspire.base;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.n52.sos.encode.AbstractXmlEncoder;
import org.n52.sos.w3c.SchemaLocation;
import org.n52.svalbard.inspire.base.Identifier;
import org.n52.svalbard.inspire.base.InspireBaseConstants;
import org.n52.svalbard.inspire.ef.InspireEfConstants;

import com.google.common.collect.Sets;

import eu.europa.ec.inspire.schemas.base.x33.IdentifierType;
import eu.europa.ec.inspire.schemas.ef.x40.EnvironmentalMonitoringFacilityType;

public abstract class AbstractIdentifierEncoder extends AbstractXmlEncoder<Identifier> {

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.emptySet();
    }

    @Override
    public void addNamespacePrefixToMap(final Map<String, String> nameSpacePrefixMap) {
        nameSpacePrefixMap.put(InspireBaseConstants.NS_BASE, InspireBaseConstants.NS_BASE_PREFIX);
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Sets.newHashSet(InspireBaseConstants.BASE_33_SCHEMA_LOCATION);
    }
    
    protected IdentifierType createIdentifierType(Identifier identifier) {
        IdentifierType it = IdentifierType.Factory.newInstance();
        return encodeIdentifierType(it, identifier);
    }
    
    protected IdentifierType encodeIdentifierType(IdentifierType it, Identifier identifier) {
        it.setLocalId(identifier.getLocalId());
        it.setNamespace(identifier.getNamespace());
        if (identifier.isSetVersionId()) {
            it.addNewVersionId().setStringValue(identifier.getVersionId());
        }
        return it;
    }

}
