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
import org.n52.sos.ogc.swe.simpleType.SweBoolean;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 */
public class SmlIoTest {

    @Test
    public void should_return_false_if_ioValue_is_not_set() {
        final SmlIo<?> smlIo = new SmlIo<Object>();
        assertThat(smlIo.isSetValue(), is(FALSE));
    }

    @Test
    public void should_return_true_if_ioValue_is_set() {
        final SweBoolean ioValue = new SweBoolean();
        final SmlIo<Boolean> smlIo = new SmlIo<Boolean>(ioValue);
        assertThat(smlIo.isSetValue(), is(TRUE));
    }

    @Test
    public void should_return_false_if_ioName_is_not_set() {
        final SmlIo<?> smlIo = new SmlIo<Object>();
        assertThat(smlIo.isSetName(), is(FALSE));

        smlIo.setIoName("");
        assertThat(smlIo.isSetName(), is(FALSE));
    }

    @Test
    public void should_return_true_if_ioName_is_set() {
        final SmlIo<Boolean> smlIo = new SmlIo<Boolean>();
        final String inputName = "inputName";
        smlIo.setIoName(inputName);
        assertThat(smlIo.isSetName(), is(TRUE));
    }
}
