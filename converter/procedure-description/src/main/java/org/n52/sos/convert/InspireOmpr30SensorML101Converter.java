/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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
package org.n52.sos.convert;

import java.util.Collections;
import java.util.Set;

import org.n52.iceland.convert.ConverterException;
import org.n52.iceland.convert.ConverterKey;
import org.n52.shetland.inspire.ompr.InspireOMPRConstants;
import org.n52.shetland.inspire.ompr.Process;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.CodeType;
import org.n52.shetland.ogc.gml.CodeWithAuthority;
import org.n52.shetland.ogc.sensorML.AbstractProcess;
import org.n52.shetland.ogc.sensorML.Component;
import org.n52.shetland.ogc.sensorML.SensorMLConstants;
import org.n52.shetland.ogc.sensorML.elements.SmlIdentifier;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.shetland.ogc.sos.SosProcedureDescriptionUnknownType;
import org.n52.shetland.util.CollectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class InspireOmpr30SensorML101Converter
        extends
        AbstractInspireOmpr30SensorMLConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(InspireOmpr30SensorML101Converter.class);

    private static final Set<ConverterKey> CONVERTER_KEY_TYPES = CollectionHelper.set(
            new ConverterKey(InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_MIME_TYPE,
                    SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE),
            new ConverterKey(InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_URL,
                    SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE),
            new ConverterKey(InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_MIME_TYPE,
                    SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL),
            new ConverterKey(InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_URL,
                    SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL),
            new ConverterKey(SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE,
                    InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_MIME_TYPE),
            new ConverterKey(SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL,
                    InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_MIME_TYPE),
            new ConverterKey(SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE,
                    InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_URL),
            new ConverterKey(SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL,
                    InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_URL));

    public InspireOmpr30SensorML101Converter() {
        LOGGER.debug("Converter for the following keys initialized successfully: {}!",
                Joiner.on(", ").join(CONVERTER_KEY_TYPES));
    }

    @Override
    public Set<ConverterKey> getKeys() {
        return Collections.unmodifiableSet(CONVERTER_KEY_TYPES);
    }

    @Override
    public AbstractFeature convert(AbstractFeature objectToConvert)
            throws ConverterException {
        if (objectToConvert instanceof SosProcedureDescription<?>) {
            SosProcedureDescription<?> o = (SosProcedureDescription<?>) objectToConvert;
            if (InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_URL.equals(o.getDescriptionFormat())
                    || InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_MIME_TYPE.equals(o.getDescriptionFormat())) {
                return convertInspireOmpr30ToSensorML101(o);
            } else if (SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL.equals(o.getDescriptionFormat())
                    || SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE.equals(o.getDescriptionFormat())) {
                return convertSensorML101ToInspireOmpr30(o);
            }
            throw new ConverterException(String.format("The procedure's description format %s is not supported!",
                    o.getDescriptionFormat()));
        }
        throw new ConverterException(String.format("The procedure's description %s is not supported!",
                objectToConvert.getClass().getName()));
    }

    private SosProcedureDescription<?> convertInspireOmpr30ToSensorML101(SosProcedureDescription<?> objectToConvert) {
        if (objectToConvert.getProcedureDescription() instanceof Process) {
            Process process = (Process) objectToConvert.getProcedureDescription();
            Component component = new Component();
            component.setIdentifier(process.getIdentifierCodeWithAuthority());
            SmlIdentifier inspireIdIdentifier = convertInspireIdToIdentification(process.getInspireId());
            if (component.isSetIdentifications()) {
                component.getIdentifications().add(inspireIdIdentifier);
            } else {
                component.setIdentifications(Lists.newArrayList(inspireIdIdentifier));
            }
            if (process.isSetDocumentation()) {
                component.setDocumentation(convertDocumentationCitationToDocumentation(process.getDocumentation()));
            }
            if (process.isSetName()) {
                SmlIdentifier identifier = convertNameToIdentification(process.getFirstName());
                if (component.isSetIdentifications()) {
                    component.getIdentifications().add(identifier);
                } else {
                    component.setIdentifications(Lists.newArrayList(identifier));
                }
            }
            if (process.isSetProcessParameter()) {
                component.addClassifications(convertProcessParametersToClassifiers(process.getProcessParameter()));
            }
            if (process.isSetResponsibleParty()) {
                component.setContact(convertResponsiblePartiesToContacts(process.getResponsibleParty()));
            }
            return new SosProcedureDescription<AbstractFeature>(component);
        }
        return new SosProcedureDescriptionUnknownType(objectToConvert.getIdentifier());
    }

    private SosProcedureDescription<?> convertSensorML101ToInspireOmpr30(SosProcedureDescription<?> objectToConvert) {
        if (objectToConvert.getProcedureDescription() instanceof AbstractProcess) {
            AbstractProcess abstractProcess = (AbstractProcess) objectToConvert.getProcedureDescription();
            Process process = new Process();
            CodeWithAuthority inspireId = convertIdentificationToInspireId(abstractProcess.getIdentifications());
            if (inspireId != null && inspireId.isSetValue()) {
                process.setIdentifier(inspireId);
            } else {
                process.setIdentifier(abstractProcess.getIdentifierCodeWithAuthority());
            }
            if (abstractProcess.isSetDocumentation()) {
                process.setDocumentation(
                        convertDocumentationToDocumentationCitation(abstractProcess.getDocumentation()));
            }
            if (abstractProcess.isSetIdentifications()) {
                CodeType name = convertIdentifierToName(abstractProcess.getIdentifications());
                if (name != null) {
                    process.addName(name);
                }
            }
            if (abstractProcess.isSetClassifications()) {
                process.setProcessParameter(
                        convertClassifiersToProcessParameters(abstractProcess.getClassifications()));
            }
            if (abstractProcess.isSetContact()) {
                process.setResponsibleParty(convertContactsToResponsibleParties(abstractProcess.getContact()));
            }
            return new SosProcedureDescription<AbstractFeature>(process);
        }
        return new SosProcedureDescriptionUnknownType(objectToConvert.getIdentifier());
    }

}
