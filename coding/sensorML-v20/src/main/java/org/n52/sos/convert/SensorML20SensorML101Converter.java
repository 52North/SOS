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
package org.n52.sos.convert;

import java.util.Collections;
import java.util.List;

import org.n52.sos.ogc.sensorML.Component;
import org.n52.sos.ogc.sensorML.ProcessChain;
import org.n52.sos.ogc.sensorML.ProcessModel;
import org.n52.sos.ogc.sensorML.SensorML;
import org.n52.sos.ogc.sensorML.SensorML20Constants;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sensorML.System;
import org.n52.sos.ogc.sensorML.v20.AggregateProcess;
import org.n52.sos.ogc.sensorML.v20.PhysicalComponent;
import org.n52.sos.ogc.sensorML.v20.PhysicalSystem;
import org.n52.sos.ogc.sensorML.v20.SimpleProcess;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.util.CollectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

/**
 * {@link Converter} class to convert SensorML 2.0 to SensorML 1.0.1 and the
 * other way round.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.2.0
 *
 */
public class SensorML20SensorML101Converter implements Converter<SosProcedureDescription, SosProcedureDescription> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SensorML20UrlMimeTypeConverter.class);

    private static final List<ConverterKeyType> CONVERTER_KEY_TYPES = CollectionHelper.list(new ConverterKeyType(
            SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_MIME_TYPE,
            SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE), new ConverterKeyType(
            SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL, SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE),
            new ConverterKeyType(SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_MIME_TYPE,
                    SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL), new ConverterKeyType(
                    SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL, SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL),
            new ConverterKeyType(SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE,
                    SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_MIME_TYPE), new ConverterKeyType(
                    SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL,
                    SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_MIME_TYPE), new ConverterKeyType(
                    SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE,
                    SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL), new ConverterKeyType(
                    SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL, SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL));

    public SensorML20SensorML101Converter() {
        LOGGER.debug("Converter for the following keys initialized successfully: {}!",
                Joiner.on(", ").join(CONVERTER_KEY_TYPES));
    }

    @Override
    public List<ConverterKeyType> getConverterKeyTypes() {
        return Collections.unmodifiableList(CONVERTER_KEY_TYPES);
    }

    @Override
    public SosProcedureDescription convert(SosProcedureDescription objectToConvert) throws ConverterException {
        if (objectToConvert.getDescriptionFormat().equals(SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL)
                || objectToConvert.getDescriptionFormat().equals(
                        SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_MIME_TYPE)) {
            return convertSensorML20ToSensorML101(objectToConvert);
        } else if (objectToConvert.getDescriptionFormat().equals(SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL)
                || objectToConvert.getDescriptionFormat().equals(SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE)) {
            return convertSensorML101ToSensorML20(objectToConvert);
        }
        throw new ConverterException(String.format("The procedure's description format %s is not supported!",
                objectToConvert.getDescriptionFormat()));
    }

    private SosProcedureDescription convertSensorML20ToSensorML101(SosProcedureDescription objectToConvert)
            throws ConverterException {
        if (objectToConvert instanceof PhysicalSystem) {
            return toSystem((PhysicalSystem) objectToConvert);
        } else if (objectToConvert instanceof PhysicalComponent) {
            return toComponent((PhysicalComponent) objectToConvert);
        } else if (objectToConvert instanceof SimpleProcess) {
            return toProcessModel((SimpleProcess) objectToConvert);
        } else if (objectToConvert instanceof AggregateProcess) {
            return toProcessChain((AggregateProcess) objectToConvert);
        }
        throw new ConverterException(String.format("The procedure type  %s is not supported!", objectToConvert
                .getClass().getName()));
    }

    private SosProcedureDescription toSystem(PhysicalSystem objectToConvert) {
        System system = new System();
        objectToConvert.copyTo(system);
        if (objectToConvert.isSetPosition()) {
            system.setPosition(objectToConvert.getPosition());
        }
        if (objectToConvert.isSetComponents()) {
            system.addComponents(objectToConvert.getComponents());
        }
        // TODO
        return new SensorML().addMember(system);
    }

    private SosProcedureDescription toComponent(PhysicalComponent objectToConvert) {
        Component component = new Component();
        objectToConvert.copyTo(component);
        if (objectToConvert.isSetPosition()) {
            component.setPosition(objectToConvert.getPosition());
        }
        // TODO
        return new SensorML().addMember(component);
    }

    private SosProcedureDescription toProcessModel(SimpleProcess objectToConvert) {
        ProcessModel model = new ProcessModel();
        objectToConvert.copyTo(model);
        // TODO
        return new SensorML().addMember(model);
    }

    private SosProcedureDescription toProcessChain(AggregateProcess objectToConvert) {
        ProcessChain chain = new ProcessChain();
        objectToConvert.copyTo(chain);
        // TODO
        return new SensorML().addMember(chain);
    }

    private SosProcedureDescription convertSensorML101ToSensorML20(SosProcedureDescription objectToConvert)
            throws ConverterException {
        if (objectToConvert instanceof SensorML) {
            if (((SensorML) objectToConvert).isSetMembers()) {
                return convertSml101AbstractProcess(((SensorML) objectToConvert).getMembers().iterator().next());
            }
        } else {
            return convertSml101AbstractProcess(objectToConvert);
        }
        throw new ConverterException(String.format("The procedure type  %s is not supported!", objectToConvert
                .getClass().getName()));
    }

    private SosProcedureDescription convertSml101AbstractProcess(SosProcedureDescription objectToConvert)
            throws ConverterException {
        if (objectToConvert instanceof System) {
            return toPhysicalSystem((System) objectToConvert);
        } else if (objectToConvert instanceof Component) {
            return toPhysicalComponent((Component) objectToConvert);
        } else if (objectToConvert instanceof ProcessModel) {
            return toSimpleProcess((ProcessModel) objectToConvert);
        } else if (objectToConvert instanceof ProcessChain) {
            return toAggregateProcess((ProcessChain) objectToConvert);
        }
        throw new ConverterException(String.format("The procedure type  %s is not supported!", objectToConvert
                .getClass().getName()));
    }

    private SosProcedureDescription toPhysicalSystem(System objectToConvert) {
        PhysicalSystem system = new PhysicalSystem();
        objectToConvert.copyTo(system);
        if (objectToConvert.isSetPosition()) {
            system.setPosition(objectToConvert.getPosition());
        }
        if (objectToConvert.isSetComponents()) {
            system.addComponents(objectToConvert.getComponents());
        }
        // TODO
        return system;
    }

    private SosProcedureDescription toPhysicalComponent(Component objectToConvert) {
        PhysicalComponent component = new PhysicalComponent();
        if (objectToConvert.isSetPosition()) {
            component.setPosition(objectToConvert.getPosition());
        }
        objectToConvert.copyTo(component);
        // TODO
        return component;
    }

    private SosProcedureDescription toSimpleProcess(ProcessModel objectToConvert) {
        SimpleProcess process = new SimpleProcess();
        objectToConvert.copyTo(process);
        // TODO
        return process;
    }

    private SosProcedureDescription toAggregateProcess(ProcessChain objectToConvert) {
        AggregateProcess process = new AggregateProcess();
        objectToConvert.copyTo(process);
        // TODO
        return process;
    }

}
