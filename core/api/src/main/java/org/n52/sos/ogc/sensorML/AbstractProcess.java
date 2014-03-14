/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ogc.sensorML;

import java.util.ArrayList;
import java.util.List;

import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.sensorML.elements.SmlIo;

/**
 * @since 4.0.0
 * 
 */
public class AbstractProcess extends AbstractSensorML {

    private List<String> descriptions = new ArrayList<String>(0);

    private List<CodeType> names = new ArrayList<CodeType>(0);

    private List<SmlIo<?>> inputs = new ArrayList<SmlIo<?>>(0);

    private List<SmlIo<?>> outputs = new ArrayList<SmlIo<?>>(0);

    private List<String> parameters = new ArrayList<String>(0);

    public List<String> getDescriptions() {
        return descriptions;
    }

    public AbstractProcess setDescriptions(final List<String> descriptions) {
        this.descriptions = descriptions;
        return this;
    }

    public AbstractProcess addDescription(final String description) {
        descriptions.add(description);
        return this;
    }

    public List<CodeType> getNames() {
        return names;
    }

    public AbstractProcess setNames(final List<CodeType> names) {
        this.names = names;
        return this;
    }

    public List<SmlIo<?>> getInputs() {
        return inputs;
    }

    public AbstractProcess setInputs(final List<SmlIo<?>> inputs) {
        this.inputs = inputs;
        return this;
    }

    public List<SmlIo<?>> getOutputs() {
        return outputs;
    }

    public AbstractProcess setOutputs(final List<SmlIo<?>> outputs) {
        this.outputs = outputs;
        return this;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public AbstractProcess setParameters(final List<String> parameters) {
        this.parameters = parameters;
        return this;
    }

    public boolean isSetDescriptions() {
        return descriptions != null && !descriptions.isEmpty();
    }

    public boolean isSetNames() {
        return names != null && !names.isEmpty();
    }

    public boolean isSetInputs() {
        return inputs != null && !inputs.isEmpty();
    }

    public boolean isSetOutputs() {
        return outputs != null && !outputs.isEmpty();
    }

    public boolean isSetParameters() {
        return parameters != null && !parameters.isEmpty();
    }

    public AbstractProcess addName(final CodeType name) {
        names.add(name);
        return this;
    }

}
