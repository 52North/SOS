/*
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

import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
import org.junit.Assert;

import java.util.Collections;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.n52.iceland.coding.SupportedTypeRepository;
import org.n52.shetland.util.ReferencedEnvelope;
import org.n52.svalbard.decode.DecoderRepository;
import org.n52.svalbard.encode.EncoderRepository;

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
    private static final String FEATURE_IDENTIFIER = "test-feature";
    private static InMemoryCacheImpl instance;

    @Before
    public void initInstance() {
        // overwrite these methods as these are doing getInstance()-calls
        instance = new InMemoryCacheImpl() {
            private static final long serialVersionUID = -2571450058666530166L;

            @Override
            public Set<String> getFeatureOfInterestTypes() {
                return Collections.emptySet();
            }

            @Override
            public Set<String> getObservationTypes() {
                return Collections.emptySet();
            }
        };
        SupportedTypeRepository supportedTypeRepository = new SupportedTypeRepository();
        supportedTypeRepository.init(new DecoderRepository(), new EncoderRepository());
        instance.setSupportedTypeRepository(supportedTypeRepository);
    }

    @After
    public void resetInstance() {
        instance = null;
    }

    @Test
    public void defaultConstructorReturnsObject() {
        initInstance();
        Assert.assertNotNull("instance is null", instance);
        Assert.assertTrue("right class", instance instanceof InMemoryCacheImpl);
    }

    @Test
    public void equalsWithNewInstances() {
        Assert.assertEquals("equals failed", new InMemoryCacheImpl(), new InMemoryCacheImpl());
    }

    @Test
    public void equalsWithSelf() {
        Assert.assertEquals("I am not equal with me", instance, instance);
    }

    @Test
    public void equalsWithNull() {
        Assert.assertNotEquals("equal with null", instance, null);
    }

    @Test
    public void equalWithOtherClass() {
        Assert.assertNotEquals("equal with Object", instance, new Object());
    }

    @Test
    public void should_return_different_hashCodes_for_different_instances() {
        InMemoryCacheImpl cache = new InMemoryCacheImpl();
        cache.setProcedures(Collections.singleton("p_1"));
        Assert.assertNotEquals("hashCode() of different caches are equal", cache.hashCode(), new InMemoryCacheImpl());
    }

    @Test
    public void should_return_empty_global_envelope_when_setEnvelope_is_called_with_null_parameter() {
        instance.setGlobalEnvelope(null);
        final ReferencedEnvelope emptyReferencedEnvelope = new ReferencedEnvelope(null, instance.getDefaultEPSGCode());

        Assert.assertThat(instance.getGlobalEnvelope(), IsNot.not(IsNull.nullValue()));
        Assert.assertThat(instance.getGlobalEnvelope(), Is.is(emptyReferencedEnvelope));
    }

    @Test
    public void should_serialize_to_json() throws JsonProcessingException {
        String json = new ObjectMapper().writeValueAsString(instance);
        Assert.assertNotNull(json);
        Assert.assertFalse(json.isEmpty());
    }

    @Test
    public void should_return_true_if_min_resulttime_for_offering_is_available() {
        final InMemoryCacheImpl cache = new InMemoryCacheImpl();
        cache.setMinResultTimeForOffering(OFFERING_IDENTIFIER, new DateTime(52L));

        Assert.assertThat(cache.hasMinResultTimeForOffering(OFFERING_IDENTIFIER), Is.is(Boolean.TRUE));
    }

    @Test
    public void should_return_false_if_min_resulttime_for_offering_is_null() {
        final InMemoryCacheImpl readCache = new InMemoryCacheImpl();

        Assert.assertThat(readCache.hasMinResultTimeForOffering(OFFERING_IDENTIFIER), Is.is(Boolean.FALSE));

        final InMemoryCacheImpl cache = new InMemoryCacheImpl();
        cache.setMinResultTimeForOffering(OFFERING_IDENTIFIER, null);

        Assert.assertThat(cache.hasMinResultTimeForOffering(OFFERING_IDENTIFIER), Is.is(Boolean.FALSE));
    }

    @Test
    public void should_return_false_if_relatedFeature_has_no_children() {
        final InMemoryCacheImpl readCache = new InMemoryCacheImpl();
        final String relatedFeature = FEATURE_IDENTIFIER;
        ((SosWritableContentCache) readCache).addRelatedFeatureForOffering(OFFERING_IDENTIFIER, relatedFeature);

        Assert.assertThat(readCache.isRelatedFeatureSampled(null), Is.is(Boolean.FALSE));
        Assert.assertThat(readCache.isRelatedFeatureSampled(""), Is.is(Boolean.FALSE));
        Assert.assertThat(readCache.isRelatedFeatureSampled(relatedFeature), Is.is(Boolean.FALSE));
    }

    @Test
    public void should_return_true_if_relatedFeature_has_one_or_more_children() {
        final InMemoryCacheImpl readCache = new InMemoryCacheImpl();
        final String relatedFeature = FEATURE_IDENTIFIER;
        final String relatedFeature2 = "test-feature-2";
        final String offering = OFFERING_IDENTIFIER;
        ((SosWritableContentCache) readCache).addRelatedFeatureForOffering(offering, relatedFeature);
        ((SosWritableContentCache) readCache).addRelatedFeatureForOffering(offering, relatedFeature2);
        ((SosWritableContentCache) readCache).addParentFeature(relatedFeature2, relatedFeature);

        Assert.assertThat(readCache.isRelatedFeatureSampled(relatedFeature), Is.is(Boolean.TRUE));
    }

}
