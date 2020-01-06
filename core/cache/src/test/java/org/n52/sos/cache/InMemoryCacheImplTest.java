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
package org.n52.sos.cache;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.n52.sos.ogc.sos.SosEnvelope;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 *
 * @since 4.0.0
 *
 */
public class InMemoryCacheImplTest {
    private static final String OFFERING_IDENTIFIER = "test-offering";
    private static InMemoryCacheImpl instance;

    @Before
    public void initInstance() {
        instance = new InMemoryCacheImpl();
    }

    @After
    public void resetInstance() {
        instance = null;
    }

    @Test
    public void defaultConstructorReturnsObject() {
        initInstance();
        assertNotNull("instance is null", instance);
        assertTrue("right class", instance instanceof InMemoryCacheImpl);
    }

    @Test
    public void equalsWithNewInstances() {
        InMemoryCacheImpl anotherInstance = new InMemoryCacheImpl();
        assertEquals("equals failed", instance, anotherInstance);
    }

    @Test
    public void equalsWithSelf() {
        assertEquals("I am not equal with me", instance, instance);
    }

    @Test
    public void equalsWithNull() {
        assertNotEquals("equal with null", instance, null);
    }

    @Test
    public void equalWithOtherClass() {
        assertNotEquals("equal with Object", instance, new Object());
    }

    @Test
    public void should_return_different_hashCodes_for_different_instances() {
        InMemoryCacheImpl cache = new InMemoryCacheImpl();
        cache.setProcedures(Collections.singleton("p_1"));
        assertNotEquals("hashCode() of different caches are equal", cache.hashCode(), new InMemoryCacheImpl());
    }

    @Test
    public void should_return_empty_global_envelope_when_setEnvelope_is_called_with_null_parameter() {
        instance.setGlobalEnvelope(null);
        final SosEnvelope emptySosEnvelope = new SosEnvelope(null, instance.getDefaultEPSGCode());

        assertThat(instance.getGlobalEnvelope(), not(nullValue()));
        assertThat(instance.getGlobalEnvelope(), is(emptySosEnvelope));
    }

    @Test
    public void should_serialize_to_json() throws JsonProcessingException {
        String json = new ObjectMapper().writeValueAsString(instance);
        assertNotNull(json);
        assertFalse(json.isEmpty());
    }

    @Test
    public void should_return_true_if_min_resulttime_for_offering_is_available() {
        final InMemoryCacheImpl cache = new InMemoryCacheImpl();
        cache.setMinResultTimeForOffering(OFFERING_IDENTIFIER, new DateTime(52l));

        assertThat(cache.hasMinResultTimeForOffering(OFFERING_IDENTIFIER), is(TRUE));
    }

    @Test
    public void should_return_false_if_min_resulttime_for_offering_is_null() {
        final InMemoryCacheImpl readCache = new InMemoryCacheImpl();

        assertThat(readCache.hasMinResultTimeForOffering(OFFERING_IDENTIFIER), is(FALSE));

        final InMemoryCacheImpl cache = new InMemoryCacheImpl();
        cache.setMinResultTimeForOffering(OFFERING_IDENTIFIER, null);

        assertThat(cache.hasMinResultTimeForOffering(OFFERING_IDENTIFIER), is(FALSE));
    }

    @Test
    public void should_return_false_if_relatedFeature_has_no_children() {
        final InMemoryCacheImpl readCache = new InMemoryCacheImpl();
        final String relatedFeature = "test-feature";
        ((WritableContentCache) readCache).addRelatedFeatureForOffering("test-offering", relatedFeature);

        assertThat(readCache.isRelatedFeatureSampled(null), is(FALSE));
        assertThat(readCache.isRelatedFeatureSampled(""), is(FALSE));
        assertThat(readCache.isRelatedFeatureSampled(relatedFeature), is(FALSE));
    }

    @Test
    public void should_return_true_if_relatedFeature_has_one_or_more_children() {
        final InMemoryCacheImpl readCache = new InMemoryCacheImpl();
        final String relatedFeature = "test-feature";
        final String relatedFeature2 = "test-feature-2";
        final String offering = "test-offering";
        ((WritableContentCache) readCache).addRelatedFeatureForOffering(offering, relatedFeature);
        ((WritableContentCache) readCache).addRelatedFeatureForOffering(offering, relatedFeature2);
        ((WritableContentCache) readCache).addParentFeature(relatedFeature2, relatedFeature);

        assertThat(readCache.isRelatedFeatureSampled(relatedFeature), is(TRUE));
    }

}
