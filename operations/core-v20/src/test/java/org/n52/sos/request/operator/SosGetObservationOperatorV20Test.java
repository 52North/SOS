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
package org.n52.sos.request.operator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.n52.sos.cache.SosContentCache;

import com.google.common.collect.Lists;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 *
 * @since 4.0.0
 */
public class SosGetObservationOperatorV20Test {
    private static final String FEATURE = "feature";

    private static final String CHILD_FEATURE = "child-feature";

    @Test
    public void should_return_empty_list_for_bad_parameters() {
        final SosGetObservationOperatorV20 operator = Mockito.mock(SosGetObservationOperatorV20.class);
        Mockito.when(operator.addChildFeatures(Matchers.anyCollectionOf(String.class))).thenCallRealMethod();

        // null
        List<String> childFeatures = operator.addChildFeatures(null);
       MatcherAssert.assertThat(childFeatures.isEmpty(), Is.is(Boolean.TRUE));

        // empty list
        childFeatures = operator.addChildFeatures(new ArrayList<String>(0));
       MatcherAssert.assertThat(childFeatures.isEmpty(), Is.is(Boolean.TRUE));
    }

    @Test
    public void should_add_childs_for_features() {
        final SosGetObservationOperatorV20 operator = Mockito.mock(SosGetObservationOperatorV20.class);
        final SosContentCache cache = Mockito.mock(SosContentCache.class);
        final Set<String> myChildFeatures = new HashSet<String>(1);
        myChildFeatures.add(CHILD_FEATURE);
        Mockito.when(cache.getChildFeatures(Matchers.anyString(), Matchers.anyBoolean(), Matchers.anyBoolean()))
                .thenReturn(myChildFeatures);
        Mockito.when(operator.getCache()).thenReturn(cache);
        Mockito.when(operator.addChildFeatures(Matchers.anyCollectionOf(String.class))).thenCallRealMethod();

        final List<String> childFeatures = operator.addChildFeatures(Lists.newArrayList(FEATURE));

       MatcherAssert.assertThat(childFeatures.isEmpty(), Is.is(Boolean.FALSE));
       MatcherAssert.assertThat(childFeatures.size(), Is.is(2));
       MatcherAssert.assertThat(childFeatures, IsCollectionContaining.hasItem(CHILD_FEATURE));
       MatcherAssert.assertThat(childFeatures, IsCollectionContaining.hasItem(FEATURE));
    }

}
