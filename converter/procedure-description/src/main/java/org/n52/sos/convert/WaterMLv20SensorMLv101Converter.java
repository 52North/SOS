/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.ReferenceType;
import org.n52.shetland.ogc.om.series.wml.ObservationProcess;
import org.n52.shetland.ogc.om.series.wml.WaterMLConstants;
import org.n52.shetland.ogc.sensorML.AbstractProcess;
import org.n52.shetland.ogc.sensorML.AbstractSensorML;
import org.n52.shetland.ogc.sensorML.ProcessModel;
import org.n52.shetland.ogc.sensorML.SensorML;
import org.n52.shetland.ogc.sensorML.SensorMLConstants;
import org.n52.shetland.ogc.sensorML.System;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;

/**
 * @since 4.0.0
 *
 */
public class WaterMLv20SensorMLv101Converter
        extends
        AbstractWaterMLv20SensorMLConverter {

    /*
     * TODO - Add function to read mapping information
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WaterMLv20SensorMLv101Converter.class);

    private static final Set<ConverterKey> CONVERTER_KEY_TYPES = ImmutableSet.<ConverterKey> builder()
            .add(new ConverterKey(WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING,
                    SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL))
            .add(new ConverterKey(WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING,
                    SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE))
            .add(new ConverterKey(SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL,
                    WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING))
            .add(new ConverterKey(SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE,
                    WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING))
            .build();

    public WaterMLv20SensorMLv101Converter() {
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
        if (objectToConvert.getDefaultElementEncoding().equals(WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING)
                && objectToConvert instanceof ObservationProcess) {
            return convertWML2ObservationProcessToSensorML101((ObservationProcess) objectToConvert);
        } else if ((objectToConvert.getDefaultElementEncoding().equals(SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL)
                || objectToConvert.getDefaultElementEncoding()
                        .equals(SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE))
                && objectToConvert instanceof AbstractSensorML) {
            return convertSensorML101ToWML2ObservationProcess((AbstractSensorML) objectToConvert);
        } else if (objectToConvert instanceof SosProcedureDescription) {
            return convert(((SosProcedureDescription) objectToConvert).getProcedureDescription());
        }
        return null;
    }

    private ObservationProcess convertSensorML101ToWML2ObservationProcess(final AbstractSensorML abstractSensorML) {
        ObservationProcess observationProcess = null;
        if (abstractSensorML instanceof SensorML) {
            final SensorML sensorML = (SensorML) abstractSensorML;
            if (sensorML.isWrapper()) {
                for (AbstractProcess member : sensorML.getMembers()) {
                    observationProcess = new ObservationProcess(member.getIdentifierCodeWithAuthority());
                    convertSensorMLToObservationProcess(observationProcess, member);
                    if (member instanceof System) {
                        observationProcess.setProcessType(new ReferenceType(WaterMLConstants.PROCESS_TYPE_SENSOR));
                    } else if (member instanceof ProcessModel) {
                        observationProcess.setProcessType(new ReferenceType(WaterMLConstants.PROCESS_TYPE_ALGORITHM));
                    }
                }
            }
            // TODO add 'else' to get values and add to obsProcess from sensorML
        } else {
            observationProcess = new ObservationProcess(abstractSensorML.getIdentifierCodeWithAuthority());
            observationProcess.setProcessType(new ReferenceType(WaterMLConstants.PROCESS_TYPE_UNKNOWN));
        }
        return observationProcess;
    }

    private AbstractSensorML convertWML2ObservationProcessToSensorML101(final ObservationProcess objectToConvert) {
        AbstractProcess process = null;
        ObservationProcess observationProcess = (ObservationProcess) objectToConvert;
        if (observationProcess.isSetProcessType()) {
            if (checkProcessType(observationProcess.getProcessType(), WaterMLConstants.PROCESS_TYPE_SENSOR)) {
                process = new System();
            } else {
                process = new ProcessModel();
            }
            convertObservationProcessToAbstractProcess(observationProcess, process);
        } else {
            process = new ProcessModel();
        }
        convertObservationProcessToAbstractProcess(observationProcess, process);
        process.setIdentifier(objectToConvert.getIdentifierCodeWithAuthority());
        return process;
    }
}
