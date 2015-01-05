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
package org.n52.sos.ogc.swes;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.n52.sos.ogc.swe.simpleType.SweBoolean;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 */
public class SwesExtensionsTest {
    
    protected static enum TestDefinitions {
        definition1, definition2, definition3;
    }
    
    private static final String DEFINITION_1 = TestDefinitions.definition1.name();
    
    private static final String DEFINITION_2 = TestDefinitions.definition2.name();
    
    private static final String DEFINITION_3= TestDefinitions.definition3.name();
    
    private static final String VALUE_1 = "";
    
    private static final Integer VALUE_2 = new Integer(1);
    
    private static final Boolean VALUE_3 = new Boolean(false);

    @Test
    public void isEmpty_should_return_false_if_extensions_are_null_or_empty() {
        assertThat(new SwesExtensions().isEmpty(), is(TRUE));
    }

    @Test
    public void isEmpty_should_return_false_if_at_least_one_extension_is_set() {
        final SwesExtensions extensions = new SwesExtensions();
        extensions.addSwesExtension(new SwesExtensionImpl<Boolean>().setDefinition(DEFINITION_1));
        assertThat(extensions.isEmpty(), is(FALSE));
    }
    
    @Test
    public void isBooleanExtensionSet_should_return_true_if_set_to_true() {
        final SwesExtensions extensions = new SwesExtensions();
        extensions.addSwesExtension(new SwesExtensionImpl<SweBoolean>().setDefinition(DEFINITION_1).setValue(new SweBoolean().setValue(TRUE)));
        assertThat(extensions.isBooleanExtensionSet(DEFINITION_1), is(TRUE));
    }
    
   @Test
   public void containsExtension_for_string_schould_return_true() {
       final SwesExtensions extensions = new SwesExtensions();
       extensions.addSwesExtension(new SwesExtensionImpl<String>().setDefinition(DEFINITION_1).setValue(new String()));
       extensions.addSwesExtension(new SwesExtensionImpl<String>().setDefinition(DEFINITION_2).setValue(new String()));
       extensions.addSwesExtension(new SwesExtensionImpl<String>().setDefinition(DEFINITION_3).setValue(new String()));
       assertThat(extensions.containsExtension(DEFINITION_1), is(TRUE));
       assertThat(extensions.containsExtension(DEFINITION_2), is(TRUE));
       assertThat(extensions.containsExtension(DEFINITION_3), is(TRUE));
   }
   
   @Test
   public void containsExtension_for_enum_schould_return_true() {
       final SwesExtensions extensions = new SwesExtensions();
       extensions.addSwesExtension(new SwesExtensionImpl<String>().setDefinition(DEFINITION_1).setValue(new String()));
       extensions.addSwesExtension(new SwesExtensionImpl<String>().setDefinition(DEFINITION_2).setValue(new String()));
       extensions.addSwesExtension(new SwesExtensionImpl<String>().setDefinition(DEFINITION_3).setValue(new String()));
       assertThat(extensions.containsExtension(DEFINITION_1), is(TRUE));
       assertThat(extensions.containsExtension(DEFINITION_2), is(TRUE));
       assertThat(extensions.containsExtension(DEFINITION_3), is(TRUE));
   }
   
   @Test
   public void containsExtension_for_enum_created_with_string_schould_return_true() {
       final SwesExtensions extensions = new SwesExtensions();
       extensions.addSwesExtension(new SwesExtensionImpl<String>().setDefinition(DEFINITION_1).setValue(new String()));
       extensions.addSwesExtension(new SwesExtensionImpl<String>().setDefinition(DEFINITION_2).setValue(new String()));
       extensions.addSwesExtension(new SwesExtensionImpl<String>().setDefinition(DEFINITION_3).setValue(new String()));
       assertThat(extensions.containsExtension(DEFINITION_1), is(TRUE));
       assertThat(extensions.containsExtension(DEFINITION_2), is(TRUE));
       assertThat(extensions.containsExtension(DEFINITION_3), is(TRUE));
   }
   
   @Test
   public void containsExtension_for_string_created_with_enum_schould_return_true() {
       final SwesExtensions extensions = new SwesExtensions();
       extensions.addSwesExtension(new SwesExtensionImpl<String>().setDefinition(DEFINITION_1).setValue(new String()));
       extensions.addSwesExtension(new SwesExtensionImpl<String>().setDefinition(DEFINITION_2).setValue(new String()));
       extensions.addSwesExtension(new SwesExtensionImpl<String>().setDefinition(DEFINITION_3).setValue(new String()));
       assertThat(extensions.containsExtension(DEFINITION_1), is(TRUE));
       assertThat(extensions.containsExtension(DEFINITION_2), is(TRUE));
       assertThat(extensions.containsExtension(DEFINITION_3), is(TRUE));
   }
   
   
   @Test
   public void getExtension_for_string_schould_return_true() {
       final SwesExtensions extensions = new SwesExtensions();
       extensions.addSwesExtension(new SwesExtensionImpl<String>().setDefinition(DEFINITION_1).setValue(VALUE_1));
       extensions.addSwesExtension(new SwesExtensionImpl<Integer>().setDefinition(DEFINITION_2).setValue(VALUE_2));
       extensions.addSwesExtension(new SwesExtensionImpl<Boolean>().setDefinition(DEFINITION_3).setValue(VALUE_3));
       assertThat(extensions.getExtension(DEFINITION_1).getValue(), instanceOf(VALUE_1.getClass()));
       assertThat(extensions.getExtension(DEFINITION_2).getValue(), instanceOf(VALUE_2.getClass()));
       assertThat(extensions.getExtension(DEFINITION_3).getValue(), instanceOf(VALUE_3.getClass()));
   }
   
   @Test
   public void getExtension_for_enum_schould_return_true() {
       final SwesExtensions extensions = new SwesExtensions();
       extensions.addSwesExtension(new SwesExtensionImpl<String>().setDefinition(DEFINITION_1).setValue(VALUE_1));
       extensions.addSwesExtension(new SwesExtensionImpl<Integer>().setDefinition(DEFINITION_2).setValue(VALUE_2));
       extensions.addSwesExtension(new SwesExtensionImpl<Boolean>().setDefinition(DEFINITION_3).setValue(VALUE_3));
       assertThat(extensions.getExtension(DEFINITION_1).getValue(), instanceOf(VALUE_1.getClass()));
       assertThat(extensions.getExtension(DEFINITION_2).getValue(), instanceOf(VALUE_2.getClass()));
       assertThat(extensions.getExtension(DEFINITION_3).getValue(), instanceOf(VALUE_3.getClass()));
   }
   
   @Test
   public void getExtension_for_enum_created_with_string_schould_return_true() {
       final SwesExtensions extensions = new SwesExtensions();
       extensions.addSwesExtension(new SwesExtensionImpl<String>().setDefinition(DEFINITION_1).setValue(VALUE_1));
       extensions.addSwesExtension(new SwesExtensionImpl<Integer>().setDefinition(DEFINITION_2).setValue(VALUE_2));
       extensions.addSwesExtension(new SwesExtensionImpl<Boolean>().setDefinition(DEFINITION_3).setValue(VALUE_3));
       assertThat(extensions.getExtension(DEFINITION_1).getValue(), instanceOf(VALUE_1.getClass()));
       assertThat(extensions.getExtension(DEFINITION_2).getValue(), instanceOf(VALUE_2.getClass()));
       assertThat(extensions.getExtension(DEFINITION_3).getValue(), instanceOf(VALUE_3.getClass()));
   }
   
   @Test
   public void getExtension_for_string_created_with_enum_schould_return_true() {
       final SwesExtensions extensions = new SwesExtensions();
       extensions.addSwesExtension(new SwesExtensionImpl<String>().setDefinition(DEFINITION_1).setValue(VALUE_1));
       extensions.addSwesExtension(new SwesExtensionImpl<Integer>().setDefinition(DEFINITION_2).setValue(VALUE_2));
       extensions.addSwesExtension(new SwesExtensionImpl<Boolean>().setDefinition(DEFINITION_3).setValue(VALUE_3));
       assertThat(extensions.getExtension(DEFINITION_1).getValue(), instanceOf(VALUE_1.getClass()));
       assertThat(extensions.getExtension(DEFINITION_2).getValue(), instanceOf(VALUE_2.getClass()));
       assertThat(extensions.getExtension(DEFINITION_3).getValue(), instanceOf(VALUE_3.getClass()));
   }
}
