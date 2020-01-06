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
package org.n52.sos.convert;

import java.util.Collections;
import java.util.List;

import org.n52.sos.convert.ConverterException;
import org.n52.sos.convert.ConverterKeyType;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.sensorML.AbstractProcess;
import org.n52.sos.ogc.sensorML.SensorML20Constants;
import org.n52.sos.ogc.sensorML.elements.SmlIdentifier;
import org.n52.sos.ogc.sensorML.v20.PhysicalComponent;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.util.CollectionHelper;
import org.n52.svalbard.inspire.ompr.InspireOMPRConstants;
import org.n52.svalbard.inspire.ompr.Process;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class InspireOmpr30SensorML20Converter extends AbstractInspireOmpr30SensorMLConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(InspireOmpr30SensorML20Converter.class);

    private static final List<ConverterKeyType> CONVERTER_KEY_TYPES = CollectionHelper.list(new ConverterKeyType(
            SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_MIME_TYPE,
            InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_MIME_TYPE), new ConverterKeyType(
            SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL, InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_MIME_TYPE),
            new ConverterKeyType(SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_MIME_TYPE,
                    InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_URL), new ConverterKeyType(
                    SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL, InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_URL),
            new ConverterKeyType(InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_MIME_TYPE,
                    SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_MIME_TYPE), new ConverterKeyType(
                            InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_URL,
                    SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_MIME_TYPE), new ConverterKeyType(
                            InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_MIME_TYPE,
                    SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL), new ConverterKeyType(
                            InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_URL, SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL));

    public InspireOmpr30SensorML20Converter() {
        LOGGER.debug("Converter for the following keys initialized successfully: {}!",
                Joiner.on(", ").join(CONVERTER_KEY_TYPES));
    }

    @Override
    public List<ConverterKeyType> getConverterKeyTypes() {
        return Collections.unmodifiableList(CONVERTER_KEY_TYPES);
    }

    @Override
    public SosProcedureDescription convert(SosProcedureDescription objectToConvert) throws ConverterException {
        if (SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL.equals(objectToConvert.getDescriptionFormat())
                || SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_MIME_TYPE.equals(objectToConvert.getDescriptionFormat())) {
            return convertSensorML20ToInspireOmpr30(objectToConvert);
        } else if (InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_URL.equals(objectToConvert.getDescriptionFormat())
                || InspireOMPRConstants.OMPR_30_OUTPUT_FORMAT_MIME_TYPE.equals(objectToConvert.getDescriptionFormat())) {
            return convertInspireOmpr30ToSensorML20(objectToConvert);
        }
        throw new ConverterException(String.format("The procedure's description format %s is not supported!",
                objectToConvert.getDescriptionFormat()));
    }

    private SosProcedureDescription convertInspireOmpr30ToSensorML20(SosProcedureDescription objectToConvert) {
        Process process = (Process) objectToConvert;
        PhysicalComponent component = new PhysicalComponent();
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
        return component;
    }

    private SosProcedureDescription convertSensorML20ToInspireOmpr30(SosProcedureDescription objectToConvert) {
        AbstractProcess abstractProcess = (AbstractProcess) objectToConvert;
        Process process = new Process();
        CodeWithAuthority inspireId = convertIdentificationToInspireId(abstractProcess.getIdentifications());
        if (inspireId != null && inspireId.isSetValue()) {
            process.setIdentifier(inspireId);
        } else {
            process.setIdentifier(abstractProcess.getIdentifierCodeWithAuthority());
        }
        if (abstractProcess.isSetDocumentation()) {
            process.setDocumentation(convertDocumentationToDocumentationCitation(abstractProcess.getDocumentation()));
        }
        if (abstractProcess.isSetIdentifications()) {
            CodeType name = convertIdentifierToName(abstractProcess.getIdentifications());
            if (name != null) {
                process.addName(name);
            }
        }
        if (abstractProcess.isSetClassifications()) {
            process.setProcessParameter(convertClassifiersToProcessParameters(abstractProcess.getClassifications()));
        }
        if (abstractProcess.isSetContact()) {
            process.setResponsibleParty(convertContactsToResponsibleParties(abstractProcess.getContact()));
        }
        return process;
    }

}
