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
package org.n52.sos.cache;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.joda.time.DateTime;
import org.junit.Test;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 * 
 */
public class ReadableCacheTest {

    private static final String OFFERING_IDENTIFIER = "test-offering";

    @Test
    public final void should_return_true_if_min_resulttime_for_offering_is_available() {
        final WritableCache cache = new WritableCache();
        cache.setMinResultTimeForOffering(ReadableCacheTest.OFFERING_IDENTIFIER, new DateTime(52l));

        assertThat(cache.hasMinResultTimeForOffering(ReadableCacheTest.OFFERING_IDENTIFIER), is(TRUE));
    }

    @Test
    public void should_return_false_if_min_resulttime_for_offering_is_null() {
        final ReadableCache readCache = new ReadableCache();

        assertThat(readCache.hasMinResultTimeForOffering(OFFERING_IDENTIFIER), is(FALSE));

        final WritableCache cache = new WritableCache();
        cache.setMinResultTimeForOffering(OFFERING_IDENTIFIER, null);

        assertThat(cache.hasMinResultTimeForOffering(OFFERING_IDENTIFIER), is(FALSE));
    }

    @Test
    public void should_return_false_if_relatedFeature_has_no_children() {
        final ReadableCache readCache = new WritableCache();
        final String relatedFeature = "test-feature";
        ((WritableContentCache) readCache).addRelatedFeatureForOffering("test-offering", relatedFeature);

        assertThat(readCache.isRelatedFeatureSampled(null), is(FALSE));
        assertThat(readCache.isRelatedFeatureSampled(""), is(FALSE));
        assertThat(readCache.isRelatedFeatureSampled(relatedFeature), is(FALSE));
    }

    @Test
    public void should_return_true_if_relatedFeature_has_one_or_more_children() {
        final ReadableCache readCache = new WritableCache();
        final String relatedFeature = "test-feature";
        final String relatedFeature2 = "test-feature-2";
        final String offering = "test-offering";
        ((WritableContentCache) readCache).addRelatedFeatureForOffering(offering, relatedFeature);
        ((WritableContentCache) readCache).addRelatedFeatureForOffering(offering, relatedFeature2);
        ((WritableContentCache) readCache).addParentFeature(relatedFeature2, relatedFeature);

        assertThat(readCache.isRelatedFeatureSampled(relatedFeature), is(TRUE));
    }

}
