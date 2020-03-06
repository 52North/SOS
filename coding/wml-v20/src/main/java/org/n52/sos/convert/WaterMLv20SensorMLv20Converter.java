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
import org.n52.sos.ogc.sensorML.SensorML;
import org.n52.sos.ogc.sensorML.SensorML20Constants;
import org.n52.sos.ogc.sensorML.v20.AbstractPhysicalProcess;
import org.n52.sos.ogc.sensorML.v20.AbstractProcessV20;
import org.n52.sos.ogc.sensorML.v20.PhysicalSystem;
import org.n52.sos.ogc.sensorML.v20.SimpleProcess;
import org.n52.sos.ogc.series.wml.WaterMLConstants;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.ogc.series.wml.ObservationProcess;
import org.n52.sos.util.CollectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

/**
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public class WaterMLv20SensorMLv20Converter extends AbstractWaterMLv20SensorMLConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(WaterMLv20SensorMLv20Converter.class);

    private static final List<ConverterKeyType> CONVERTER_KEY_TYPES = CollectionHelper.list(
            new ConverterKeyType(WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING, SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL), 
            new ConverterKeyType(WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING, SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_MIME_TYPE),
            new ConverterKeyType(SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL,WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING),
            new ConverterKeyType(SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_MIME_TYPE,WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING));

    public WaterMLv20SensorMLv20Converter() {
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
            return convertWML2ObservationProcessToSensorML20(objectToConvert);
        } else if (objectToConvert.getDescriptionFormat().equals(SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL)
                || objectToConvert.getDescriptionFormat().equals(SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_MIME_TYPE)) {
            return convertSensorML20ToWML2ObservationProcess(objectToConvert);
        }
        return null;
    }

    private SosProcedureDescription convertSensorML20ToWML2ObservationProcess(final SosProcedureDescription objectToConvert) {
        final ObservationProcess observationProcess = new ObservationProcess();

        if (objectToConvert instanceof AbstractProcessV20) {
        	convertSensorMLToObservationProcess(observationProcess, (AbstractProcessV20)objectToConvert);
        	if (objectToConvert instanceof AbstractPhysicalProcess) {
        		observationProcess.setProcessType(new ReferenceType(WaterMLConstants.PROCESS_TYPE_SENSOR));
        	} else {
        		observationProcess.setProcessType(new ReferenceType(WaterMLConstants.PROCESS_TYPE_ALGORITHM));
        	}
        } else {
            observationProcess.setProcessType(new ReferenceType(WaterMLConstants.PROCESS_TYPE_UNKNOWN));
        }
        observationProcess.setIdentifier(objectToConvert.getIdentifierCodeWithAuthority());
        observationProcess.setDescriptionFormat(WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING);
        return observationProcess;
    }

    private SosProcedureDescription convertWML2ObservationProcessToSensorML20(final SosProcedureDescription objectToConvert) {
        final SensorML sensorML = new SensorML();
        if (objectToConvert instanceof ObservationProcess) {
            final ObservationProcess observationProcess = new ObservationProcess();
            if (observationProcess.isSetProcessType()) {
                AbstractProcess process;
                if (checkProcessType(observationProcess.getProcessType(), WaterMLConstants.PROCESS_TYPE_SENSOR)) {
                    process = new PhysicalSystem();
                } else {
                    process = new SimpleProcess();
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
