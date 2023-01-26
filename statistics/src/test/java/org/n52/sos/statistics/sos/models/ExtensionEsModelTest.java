/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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
package org.n52.sos.statistics.sos.models;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import org.n52.iceland.statistics.api.parameters.ObjectEsParameterFactory;
import org.n52.shetland.ogc.ows.extension.Extension;
import org.n52.shetland.ogc.swe.simpleType.SweBoolean;
import org.n52.shetland.ogc.swes.SwesExtension;

public class ExtensionEsModelTest {

    private static final String IDENTIFIER = "identifier";

    private static final String DEFINITION = "definition";

    @Test
    public void transformExtensionToEsModel() {
        SweBoolean bool = new SweBoolean();
        bool.setValue(true);
        Extension<SweBoolean> ext = new SwesExtension<>(bool);
        ext.setDefinition(DEFINITION);
        ext.setIdentifier(IDENTIFIER);
        ext.setNamespace("namespace");

        Map<String, Object> map = ExtensionEsModel.convert(ext);

        Assert.assertEquals("SweBoolean [value=true; quality=null; simpleType=Boolean]",
                map.get(ObjectEsParameterFactory.EXTENSION_VALUE.getName()));
        Assert.assertEquals(DEFINITION, map.get(ObjectEsParameterFactory.EXTENSION_DEFINITION.getName()));
        Assert.assertEquals(IDENTIFIER, map.get(ObjectEsParameterFactory.EXTENSION_IDENTIFIER.getName()));
    }

    @Test
    public void resultsInNullExtension() {
        Map<String, Object> map = ExtensionEsModel.convert((Extension<?>) null);
        Assert.assertNull(map);
    }

}
