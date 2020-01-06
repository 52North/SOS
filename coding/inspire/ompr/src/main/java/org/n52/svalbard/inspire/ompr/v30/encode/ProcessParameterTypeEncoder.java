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
package org.n52.svalbard.inspire.ompr.v30.encode;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.encode.AbstractXmlEncoder;
import org.n52.sos.encode.ClassToClassEncoderKey;
import org.n52.sos.encode.EncoderKey;
import org.n52.sos.encode.XmlDocumentEncoderKey;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.http.MediaType;
import org.n52.sos.w3c.SchemaLocation;
import org.n52.svalbard.inspire.ompr.InspireOMPRConstants;
import org.n52.svalbard.inspire.ompr.ProcessParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

import eu.europa.ec.inspire.schemas.ompr.x30.ProcessParameterType;

public class ProcessParameterTypeEncoder extends AbstractXmlEncoder<ProcessParameter> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessParameterTypeEncoder.class);
    
    private static final Set<EncoderKey> ENCODER_KEYS = Sets.newHashSet(
            new ClassToClassEncoderKey(ProcessParameterType.class, ProcessParameter.class),
            new XmlDocumentEncoderKey(InspireOMPRConstants.NS_OMPR_30, ProcessParameter.class));

    public ProcessParameterTypeEncoder() {
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!",
                Joiner.on(", ").join(ENCODER_KEYS));
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
        nameSpacePrefixMap.put(InspireOMPRConstants.NS_OMPR_30, InspireOMPRConstants.NS_OMPR_PREFIX);
    }

    @Override
    public MediaType getContentType() {
        return InspireOMPRConstants.OMPR_30_CONTENT_TYPE;
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Sets.newHashSet(InspireOMPRConstants.OMPR_SCHEMA_LOCATION);
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.emptySet();
    }

    @Override
    public XmlObject encode(ProcessParameter processParameter) throws OwsExceptionReport, UnsupportedEncoderInputException {
        return encode(processParameter, Collections.<HelperValues, String>emptyMap());
    }

    @Override
    public XmlObject encode(ProcessParameter processParameter, Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport, UnsupportedEncoderInputException {
        return createProcessParameter(processParameter);
    }
    
    protected ProcessParameterType createProcessParameter(ProcessParameter processParameter) throws OwsExceptionReport {
        ProcessParameterType ppt = ProcessParameterType.Factory.newInstance();
        ppt.addNewName().set(encodeGML32(processParameter.getName()));
        if (processParameter.isSetDescription()) {
            ppt.setDescription(processParameter.getDescription());
        }
        return ppt;
    }

}
