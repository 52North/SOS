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
package org.n52.sos.ogc.sensorML;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.sensorML.elements.SmlComponent;
import org.n52.sos.ogc.sensorML.elements.SmlIo;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.Constants;

/**
 * @since 4.0.0
 * 
 */
public class AbstractProcess extends AbstractSensorML {

    private static final long serialVersionUID = 90768395878987095L;

    private List<SmlIo<?>> inputs = new ArrayList<SmlIo<?>>(0);

    private List<SmlIo<?>> outputs = new ArrayList<SmlIo<?>>(0);

    private List<String> parameters = new ArrayList<String>(0);

    public AbstractProcess setDescriptions(final List<String> descriptions) {
        if (descriptions != null) {
            if (descriptions.size() == 1) {
                setDescription(descriptions.iterator().next());
            } else {
                setDescription(Arrays.toString(descriptions.toArray(new String[descriptions.size()])));
            }
        }
        return this;
    }

    public AbstractProcess addDescription(final String description) {
        if (isSetDescription()) {
            setDescription(new StringBuilder(getDescription()).append(Constants.COMMA_SPACE_STRING)
                    .append(description).toString());
        } else {
            setDescription(description);
        }

        return this;
    }

    public List<CodeType> getNames() {
        return getName();
    }

    public AbstractProcess setNames(final List<CodeType> names) {
        setName(names);
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

    public boolean isSetName() {
        return CollectionHelper.isNotEmpty(getName());
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
        super.addName(name);
        return this;
    }
    
    protected void checkAndSetChildProcedures(final List<SmlComponent> components) {
        if (components != null) {
            for (final SmlComponent component : components) {
                checkAndSetChildProcedures(component);
            }
        }
    }

    protected void checkAndSetChildProcedures(final SmlComponent component) {
        if (component != null && component.isSetName()
                && component.getName().contains(SensorMLConstants.ELEMENT_NAME_CHILD_PROCEDURES)) {
            addChildProcedure(component.getProcess());
        }
    }
    
    public void copyTo(AbstractProcess copyOf) {
        super.copyTo(copyOf);
        copyOf.setInputs(getInputs());
        copyOf.setOutputs(getOutputs());
        copyOf.setParameters(getParameters());
    }

}
