/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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
import org.n52.shetland.ogc.sensorML.SensorML20Constants;
import org.n52.shetland.ogc.sensorML.v20.AbstractPhysicalProcess;
import org.n52.shetland.ogc.sensorML.v20.AbstractProcessV20;
import org.n52.shetland.ogc.sensorML.v20.PhysicalSystem;
import org.n52.shetland.ogc.sensorML.v20.SimpleProcess;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.shetland.util.CollectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

/**
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public class WaterMLv20SensorMLv20Converter
        extends
        AbstractWaterMLv20SensorMLConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(WaterMLv20SensorMLv101Converter.class);

    private static final Set<ConverterKey> CONVERTER_KEYS = CollectionHelper.set(
            new ConverterKey(WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING,
                    SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL),
            new ConverterKey(WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING,
                    SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_MIME_TYPE),
            new ConverterKey(SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL,
                    WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING),
            new ConverterKey(SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_MIME_TYPE,
                    WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING));

    public WaterMLv20SensorMLv20Converter() {
        LOGGER.debug("Converter for the following keys initialized successfully: {}!",
                Joiner.on(", ").join(CONVERTER_KEYS));
    }

    @Override
    public AbstractFeature convert(final AbstractFeature objectToConvert)
            throws ConverterException {
        if (objectToConvert.getDefaultElementEncoding().equals(WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING)
                && objectToConvert instanceof ObservationProcess) {
            return convertWML2ObservationProcessToSensorML20((ObservationProcess) objectToConvert);
        } else if ((objectToConvert.getDefaultElementEncoding()
                .equals(SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL)
                || objectToConvert.getDefaultElementEncoding()
                        .equals(SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL))
                && objectToConvert instanceof AbstractSensorML) {
            return convertSensorML20ToWML2ObservationProcess((AbstractSensorML) objectToConvert);
        } else if (objectToConvert instanceof SosProcedureDescription) {
            return convert(((SosProcedureDescription) objectToConvert).getProcedureDescription());
        }
        return null;
    }

    @Override
    public Set<ConverterKey> getKeys() {
        return Collections.unmodifiableSet(CONVERTER_KEYS);
    }

    private ObservationProcess convertSensorML20ToWML2ObservationProcess(AbstractSensorML objectToConvert) {
        final ObservationProcess observationProcess = new ObservationProcess();
        if (objectToConvert instanceof AbstractProcessV20) {
            convertSensorMLToObservationProcess(observationProcess, (AbstractProcessV20) objectToConvert);
            if (objectToConvert instanceof AbstractPhysicalProcess) {
                observationProcess.setProcessType(new ReferenceType(WaterMLConstants.PROCESS_TYPE_SENSOR));
            } else {
                observationProcess.setProcessType(new ReferenceType(WaterMLConstants.PROCESS_TYPE_ALGORITHM));
            }
        } else {
            observationProcess.setProcessType(new ReferenceType(WaterMLConstants.PROCESS_TYPE_ALGORITHM));
        }
        observationProcess.setIdentifier(objectToConvert.getIdentifierCodeWithAuthority());
        return observationProcess;
    }

    private AbstractSensorML convertWML2ObservationProcessToSensorML20(ObservationProcess observationProcess) {
        AbstractProcess process = null;
        if (observationProcess.isSetProcessType()) {
            if (checkProcessType(observationProcess.getProcessType(), WaterMLConstants.PROCESS_TYPE_SENSOR)) {
                process = new PhysicalSystem();
            } else {
                process = new SimpleProcess();
            }
        } else {
            process = new SimpleProcess();
        }
        process.setIdentifier(observationProcess.getIdentifierCodeWithAuthority());
        convertObservationProcessToAbstractProcess(observationProcess, process);
        return process;
    }
}
