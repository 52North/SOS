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
package org.n52.sos.ogc.sensorML.elements;

import static java.lang.Boolean.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 */
public class SmlIdentifierTest {

    @Test
    public void should_return_false_if_name_not_set_correct() {
        final SmlIdentifier smlIdentifier = new SmlIdentifier(null, "tmp", "tmp");
        final SmlIdentifier smlId2 = new SmlIdentifier("", "tmp", "tmp");

        assertThat(smlIdentifier.isSetName(), is(FALSE));
        assertThat(smlId2.isSetName(), is(FALSE));
    }

    @Test
    public void should_return_true_if_name_is_set() {
        final String name = "name";
        final SmlIdentifier identifier = new SmlIdentifier(name, null, null);

        assertThat(identifier.isSetName(), is(TRUE));
        assertThat(identifier.getName(), is(name));
    }

    @Test
    public void should_return_true_if_definition_is_set() {
        final String definition = "definition";
        final SmlIdentifier identifier = new SmlIdentifier(null, definition, null);

        assertThat(identifier.isSetDefinition(), is(TRUE));
        assertThat(identifier.getDefinition(), is(definition));
    }

    @Test
    public void should_return_true_if_value_is_set() {
        final String value = "value";
        final SmlIdentifier identifier = new SmlIdentifier(null, null, value);

        assertThat(identifier.isSetValue(), is(TRUE));
        assertThat(identifier.getValue(), is(value));
    }

    @Test
    public void should_return_false_if_value_not_set_correct() {
        final SmlIdentifier smlIdentifier = new SmlIdentifier("tmp", "tmp", null);
        final SmlIdentifier smlId2 = new SmlIdentifier("tmp", "tmp", "");

        assertThat(smlIdentifier.isSetValue(), is(FALSE));
        assertThat(smlId2.isSetValue(), is(FALSE));
    }

    @Test
    public void should_return_false_if_definition_not_set_correct() {
        final SmlIdentifier smlIdentifier = new SmlIdentifier("tmp", null, "tmp");
        final SmlIdentifier smlId2 = new SmlIdentifier("tmp", "", "tmp");

        assertThat(smlIdentifier.isSetDefinition(), is(FALSE));
        assertThat(smlId2.isSetDefinition(), is(FALSE));
    }
}
