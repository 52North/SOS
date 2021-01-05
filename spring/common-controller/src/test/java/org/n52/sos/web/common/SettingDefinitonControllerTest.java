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
package org.n52.sos.web.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Test;
import org.n52.faroe.SettingDefinition;
import org.n52.faroe.SettingDefinitionGroup;
import org.n52.faroe.settings.StringSettingDefinition;
import org.n52.shetland.util.StringHelper;

public class SettingDefinitonControllerTest {

    SettingDefinitonController controller = new SettingDefinitonController();

    SettingDefinition<?> def = new StringSettingDefinition().setGroup(new SettingDefinitionGroup("test"));

     @Test
     public void test_all_empty_only_empty_exclude() {
         assertTrue(controller.checkGroup(def, true, Collections.emptySet(), Collections.emptySet()));
     }

     @Test
     public void test_all_empty_only_exclude() {
         assertTrue(controller.checkGroup(def, true, Collections.emptySet(), StringHelper.splitToSet("test")));
     }

     @Test
     public void test_all_only_empty_exclude() {
         assertTrue(controller.checkGroup(def, true, StringHelper.splitToSet("test"), Collections.emptySet()));
     }

     @Test
     public void test_only_defined() {
         assertTrue(controller.checkGroup(def, false, StringHelper.splitToSet("test"), Collections.emptySet()));
     }

     @Test
     public void test_only_not_defined() {
         assertFalse(controller.checkGroup(def, false, StringHelper.splitToSet("test2"), Collections.emptySet()));
     }

     @Test
     public void test_exclude_defined() {
         assertFalse(controller.checkGroup(def, false, Collections.emptySet(), StringHelper.splitToSet("test")));
     }

     @Test
     public void test_exclude_not_defined() {
         assertTrue(controller.checkGroup(def, false, Collections.emptySet(), StringHelper.splitToSet("test2")));
     }
}
