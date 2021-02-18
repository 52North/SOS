/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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
import java.util.Set;

import org.n52.iceland.convert.Converter;
import org.n52.iceland.convert.ConverterException;
import org.n52.iceland.convert.ConverterKey;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.sensorML.AbstractSensorML;
import org.n52.shetland.ogc.sensorML.Component;
import org.n52.shetland.ogc.sensorML.ProcessChain;
import org.n52.shetland.ogc.sensorML.ProcessModel;
import org.n52.shetland.ogc.sensorML.SensorML;
import org.n52.shetland.ogc.sensorML.SensorML20Constants;
import org.n52.shetland.ogc.sensorML.SensorMLConstants;
import org.n52.shetland.ogc.sensorML.System;
import org.n52.shetland.ogc.sensorML.v20.AggregateProcess;
import org.n52.shetland.ogc.sensorML.v20.PhysicalComponent;
import org.n52.shetland.ogc.sensorML.v20.PhysicalSystem;
import org.n52.shetland.ogc.sensorML.v20.SimpleProcess;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;

/**
 * {@link Converter} class to convert SensorML 2.0 to SensorML 1.0.1 and the
 * other way round.
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.2.0
 *
 */
public class SensorML20SensorML101Converter
        extends
        ProcedureDescriptionConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SensorML20SensorML101Converter.class);
    private static final String NOT_SUPPORTED_FORMAT = "The procedure's description format %s is not supported!";
    private static final String NOT_SUPPORTED_TYPE = "The procedure type  %s is not supported!";

    private static final Set<ConverterKey> CONVERTER_KEY_TYPES = ImmutableSet.<ConverterKey> builder()
            .add(new ConverterKey(SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_MIME_TYPE,
                    SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE))
            .add(new ConverterKey(SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL,
                    SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE))
            .add(new ConverterKey(SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_MIME_TYPE,
                    SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL))
            .add(new ConverterKey(SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL,
                    SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL))
            .add(new ConverterKey(SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE,
                    SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_MIME_TYPE))
            .add(new ConverterKey(SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL,
                    SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_MIME_TYPE))
            .add(new ConverterKey(SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE,
                    SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL))
            .add(new ConverterKey(SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL,
                    SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL))
            .build();

    public SensorML20SensorML101Converter() {
        LOGGER.debug("Converter for the following keys initialized successfully: {}!",
                Joiner.on(", ").join(CONVERTER_KEY_TYPES));
    }

    @Override
    public Set<ConverterKey> getKeys() {
        return Collections.unmodifiableSet(CONVERTER_KEY_TYPES);
    }

    @Override
    public AbstractFeature convert(AbstractFeature objectToConvert) throws ConverterException {
        if (objectToConvert instanceof AbstractSensorML) {
            return convert((AbstractSensorML) objectToConvert);
        } else if (objectToConvert instanceof SosProcedureDescription
                && ((SosProcedureDescription) objectToConvert).getProcedureDescription() instanceof AbstractSensorML) {
            AbstractSensorML convert =
                    convert((AbstractSensorML) ((SosProcedureDescription) objectToConvert).getProcedureDescription());
            SosProcedureDescription sosProcedureDescription = new SosProcedureDescription(convert);
            sosProcedureDescription.add((SosProcedureDescription) objectToConvert);
            sosProcedureDescription.setDescriptionFormat(convert.getDefaultElementEncoding());
            return sosProcedureDescription;
        }
        throw new ConverterException(String.format(NOT_SUPPORTED_FORMAT,
                objectToConvert.getDefaultElementEncoding()));
    }

    private  AbstractSensorML convert(AbstractSensorML asml)
            throws ConverterException {
        if (asml.getDefaultElementEncoding().equals(SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL)
                || asml.getDefaultElementEncoding()
                        .equals(SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_MIME_TYPE)) {
            return convertSensorML20ToSensorML101(asml);
        } else if (asml.getDefaultElementEncoding().equals(SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL)
                || asml.getDefaultElementEncoding().equals(SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE)) {
            return convertSensorML101ToSensorML20(asml);
        }
        throw new ConverterException(String.format(NOT_SUPPORTED_FORMAT,
                asml.getDefaultElementEncoding()));
    }

    private AbstractSensorML convertSensorML20ToSensorML101(AbstractSensorML objectToConvert)
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
        throw new ConverterException(
                String.format(NOT_SUPPORTED_FORMAT, objectToConvert.getClass().getName()));
    }

    private AbstractSensorML toSystem(PhysicalSystem objectToConvert) {
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

    private AbstractSensorML toComponent(PhysicalComponent objectToConvert) {
        Component component = new Component();
        objectToConvert.copyTo(component);
        if (objectToConvert.isSetPosition()) {
            component.setPosition(objectToConvert.getPosition());
        }
        // TODO
        return new SensorML().addMember(component);
    }

    private AbstractSensorML toProcessModel(SimpleProcess objectToConvert) {
        ProcessModel model = new ProcessModel();
        objectToConvert.copyTo(model);
        // TODO
        return new SensorML().addMember(model);
    }

    private AbstractSensorML toProcessChain(AggregateProcess objectToConvert) {
        ProcessChain chain = new ProcessChain();
        objectToConvert.copyTo(chain);
        // TODO
        return new SensorML().addMember(chain);
    }

    private AbstractSensorML convertSensorML101ToSensorML20(AbstractSensorML objectToConvert)
            throws ConverterException {
        if (objectToConvert instanceof SensorML) {
            if (((SensorML) objectToConvert).isSetMembers()) {
                return convertSml101AbstractProcess(((SensorML) objectToConvert).getMembers().iterator().next());
            }
        } else {
            return convertSml101AbstractProcess(objectToConvert);
        }
        throw new ConverterException(
                String.format(NOT_SUPPORTED_TYPE, objectToConvert.getClass().getName()));
    }

    private AbstractSensorML convertSml101AbstractProcess(AbstractSensorML objectToConvert)
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
        throw new ConverterException(
                String.format(NOT_SUPPORTED_TYPE, objectToConvert.getClass().getName()));
    }

    private AbstractSensorML toPhysicalSystem(System objectToConvert) {
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

    private AbstractSensorML toPhysicalComponent(Component objectToConvert) {
        PhysicalComponent component = new PhysicalComponent();
        if (objectToConvert.isSetPosition()) {
            component.setPosition(objectToConvert.getPosition());
        }
        objectToConvert.copyTo(component);
        // TODO
        return component;
    }

    private AbstractSensorML toSimpleProcess(ProcessModel objectToConvert) {
        SimpleProcess process = new SimpleProcess();
        objectToConvert.copyTo(process);
        // TODO
        return process;
    }

    private AbstractSensorML toAggregateProcess(ProcessChain objectToConvert) {
        AggregateProcess process = new AggregateProcess();
        objectToConvert.copyTo(process);
        // TODO
        return process;
    }

}
