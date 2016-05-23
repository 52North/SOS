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
package org.n52.sos.ogc.om.features.samplingFeatures;

import static java.lang.Boolean.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.n52.sos.ogc.gml.CodeWithAuthority;

import com.google.common.collect.Lists;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 */
public class SamplingFeatureTest {

    @Test
    public final void addRelatedSamplingFeature_should_add_a_relatedSamplingFeature() {
        final SamplingFeature feature = new SamplingFeature(null);
        final SamplingFeatureComplex relatedSamplingFeature =
                new SamplingFeatureComplex("test-role", new SamplingFeature(new CodeWithAuthority("test-feature")));
        feature.addRelatedSamplingFeature(relatedSamplingFeature);

        assertThat(feature.isSetRelatedSamplingFeatures(), is(TRUE));
        assertThat(feature.getRelatedSamplingFeatures(), hasSize(1));
        assertThat(feature.getRelatedSamplingFeatures().get(0), is(relatedSamplingFeature));

        final SamplingFeatureComplex relatedSamplingFeature2 =
                new SamplingFeatureComplex("test-role", new SamplingFeature(new CodeWithAuthority("test-feature-2")));
        feature.addRelatedSamplingFeature(relatedSamplingFeature2);

        validate(feature, relatedSamplingFeature, relatedSamplingFeature2);

        feature.addRelatedSamplingFeature(null);

        validate(feature, relatedSamplingFeature, relatedSamplingFeature2);
    }

    @Test
    public final void addAllRelatedSamplingFeatures_should_add_all_elements() {
        final SamplingFeature feature = new SamplingFeature(null);
        final SamplingFeatureComplex relatedSamplingFeature =
                new SamplingFeatureComplex("test-role", new SamplingFeature(new CodeWithAuthority("test-feature")));
        final SamplingFeatureComplex relatedSamplingFeature2 =
                new SamplingFeatureComplex("test-role", new SamplingFeature(new CodeWithAuthority("test-feature-2")));

        List<SamplingFeatureComplex> list = Lists.newArrayList(relatedSamplingFeature, relatedSamplingFeature2);

        feature.addAllRelatedSamplingFeatures(list);

        validate(feature, relatedSamplingFeature, relatedSamplingFeature2);

        final SamplingFeatureComplex relatedSamplingFeature3 =
                new SamplingFeatureComplex("test-role", new SamplingFeature(new CodeWithAuthority("test-feature-3")));
        final SamplingFeatureComplex relatedSamplingFeature4 =
                new SamplingFeatureComplex("test-role", new SamplingFeature(new CodeWithAuthority("test-feature-4")));

        list = Lists.newArrayList(relatedSamplingFeature3, relatedSamplingFeature4);

        feature.addAllRelatedSamplingFeatures(list);

        validate(feature, relatedSamplingFeature, relatedSamplingFeature2, relatedSamplingFeature3,
                relatedSamplingFeature4);
    }

    @Test
    public final void setRelatedSamplingFeatures_should_set_all_elements_and_reset_if_set_before() {
        final SamplingFeature feature = new SamplingFeature(null);
        final SamplingFeatureComplex relatedSamplingFeature =
                new SamplingFeatureComplex("test-role", new SamplingFeature(new CodeWithAuthority("test-feature")));
        final SamplingFeatureComplex relatedSamplingFeature2 =
                new SamplingFeatureComplex("test-role", new SamplingFeature(new CodeWithAuthority("test-feature-2")));

        List<SamplingFeatureComplex> list = Lists.newArrayList(relatedSamplingFeature, relatedSamplingFeature2);

        feature.setRelatedSamplingFeatures(list);

        validate(feature, relatedSamplingFeature, relatedSamplingFeature2);

        final SamplingFeatureComplex relatedSamplingFeature3 =
                new SamplingFeatureComplex("test-role", new SamplingFeature(new CodeWithAuthority("test-feature-3")));
        final SamplingFeatureComplex relatedSamplingFeature4 =
                new SamplingFeatureComplex("test-role", new SamplingFeature(new CodeWithAuthority("test-feature-4")));

        list = Lists.newArrayList(relatedSamplingFeature3, relatedSamplingFeature4);

        feature.setRelatedSamplingFeatures(list);

        validate(feature, relatedSamplingFeature3, relatedSamplingFeature4);
    }

    @Test
    public final void isSetRelatedSamplingFeatures_should_return_false_if_not_set() {
        final SamplingFeature feature = new SamplingFeature(null);
        assertThat(feature.isSetRelatedSamplingFeatures(), is(FALSE));

        feature.addRelatedSamplingFeature(new SamplingFeatureComplex("test-role", new SamplingFeature(
                new CodeWithAuthority("test-feature"))));
        assertThat(feature.isSetRelatedSamplingFeatures(), is(TRUE));

        feature.setRelatedSamplingFeatures(null);
        assertThat(feature.isSetRelatedSamplingFeatures(), is(FALSE));

        feature.setRelatedSamplingFeatures(Lists.<SamplingFeatureComplex> newArrayList());
        assertThat(feature.isSetRelatedSamplingFeatures(), is(FALSE));
    }

    private void validate(final SamplingFeature feature, final SamplingFeatureComplex... relatedSamplingFeatures) {
        assertThat(feature.isSetRelatedSamplingFeatures(), is(TRUE));
        assertThat(feature.getRelatedSamplingFeatures(), hasSize(relatedSamplingFeatures.length));
        for (final SamplingFeatureComplex relatedSamplingFeature : relatedSamplingFeatures) {
            assertThat(feature.getRelatedSamplingFeatures(), hasItem(relatedSamplingFeature));
        }
    }

}
