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

import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.sensorML.AbstractProcess;
import org.n52.sos.ogc.sensorML.ProcessModel;
import org.n52.sos.ogc.sensorML.SensorML;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sensorML.System;
import org.n52.sos.ogc.series.wml.WaterMLConstants;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.ogc.series.wml.ObservationProcess;
import org.n52.sos.util.CollectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

/**
 * @since 4.0.0
 * 
 */
public class WaterMLv20SensorMLv101Converter extends AbstractWaterMLv20SensorMLConverter {

    /*
     * TODO - Add function to read mapping information
     */

    private static final Logger LOGGER = LoggerFactory.getLogger(WaterMLv20SensorMLv101Converter.class);

    private static final List<ConverterKeyType> CONVERTER_KEY_TYPES = CollectionHelper.list(
            new ConverterKeyType(WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING, SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL), 
            new ConverterKeyType(WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING, SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE),
            new ConverterKeyType(SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL,WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING),
            new ConverterKeyType(SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE,WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING));

    public WaterMLv20SensorMLv101Converter() {
        LOGGER.debug("Converter for the following keys initialized successfully: {}!",
                Joiner.on(", ").join(CONVERTER_KEY_TYPES));
    }

    @Override
    public List<ConverterKeyType> getConverterKeyTypes() {
        return Collections.unmodifiableList(CONVERTER_KEY_TYPES);
    }

    @Override
    public SosProcedureDescription convert(final SosProcedureDescription objectToConvert) throws ConverterException {
        if (objectToConvert.getDescriptionFormat().equals(WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING)) {
            return convertWML2ObservationProcessToSensorML101(objectToConvert);
        } else if (objectToConvert.getDescriptionFormat().equals(SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL)
                || objectToConvert.getDescriptionFormat().equals(SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE)) {
            return convertSensorML101ToWML2ObservationProcess(objectToConvert);
        }
        return null;
    }

    private SosProcedureDescription convertSensorML101ToWML2ObservationProcess(
            final SosProcedureDescription objectToConvert) {
        final ObservationProcess observationProcess = new ObservationProcess();

        if (objectToConvert instanceof SensorML) {
            final SensorML sensorML = (SensorML) objectToConvert;
            if (sensorML.isWrapper()) {
                for (final AbstractProcess member : sensorML.getMembers()) {
                    convertSensorMLToObservationProcess(observationProcess, member);
                    if (member.isSetIdentifier()) {
                        observationProcess.setIdentifier(member.getIdentifierCodeWithAuthority());
                    }
                    if (member instanceof System) {
                        observationProcess.setProcessType(new ReferenceType(WaterMLConstants.PROCESS_TYPE_SENSOR));
                    } else if (member instanceof ProcessModel) {
                        observationProcess.setProcessType(new ReferenceType(WaterMLConstants.PROCESS_TYPE_ALGORITHM));
                    }
                }
            }
            // TODO add 'else' to get values and add to obsProcess from sensorML
        } else {
            observationProcess.setProcessType(new ReferenceType(WaterMLConstants.PROCESS_TYPE_UNKNOWN));
        }
        observationProcess.setIdentifier(objectToConvert.getIdentifierCodeWithAuthority());
        observationProcess.setDescriptionFormat(WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING);
        return observationProcess;
    }

    private SosProcedureDescription convertWML2ObservationProcessToSensorML101(
            final SosProcedureDescription objectToConvert) {
        final SensorML sensorML = new SensorML();
        if (objectToConvert instanceof ObservationProcess) {
            final ObservationProcess observationProcess = new ObservationProcess();
            if (observationProcess.isSetProcessType()) {
                AbstractProcess process;
                if (checkProcessType(observationProcess.getProcessType(), WaterMLConstants.PROCESS_TYPE_SENSOR)) {
                    process = new System();
                } else {
                    process = new ProcessModel();
                }
                convertObservationProcessToAbstractProcess(observationProcess, process);
                sensorML.addMember(process);
            }
        } else {
            sensorML.addIdentifier(createUniqueIDIdentifier(objectToConvert.getIdentifier()));
        }
        return sensorML;
    }
}
