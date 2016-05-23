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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.n52.sos.ogc.gml.CodeWithAuthority;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 * 
 */
public class SamplingFeatureComplexTest {

    @Test(expected = IllegalArgumentException.class)
    public void constructor_should_throw_exception_when_role_is_not_provided_1() {
        new SamplingFeatureComplex(null, new SamplingFeature(new CodeWithAuthority("test-feature")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_should_throw_exception_when_role_is_not_provided_2() {
        new SamplingFeatureComplex("", new SamplingFeature(new CodeWithAuthority("test-feature")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_should_throw_exception_when_feature_is_not_provided_1() {
        new SamplingFeatureComplex("test-role", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_should_throw_exception_when_feature_is_not_provided_2() {
        new SamplingFeatureComplex("test-role", new SamplingFeature(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_should_throw_exception_when_feature_is_not_provided_3() {
        new SamplingFeatureComplex(null, new SamplingFeature(new CodeWithAuthority(null)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_should_throw_exception_when_feature_is_not_provided_4() {
        new SamplingFeatureComplex(null, new SamplingFeature(new CodeWithAuthority("")));
    }

    @Test
    public void should_set_role_correct() {
        final String role = "test-role";
        final SamplingFeatureComplex sfc =
                new SamplingFeatureComplex(role, new SamplingFeature(new CodeWithAuthority("test-feature")));

        assertThat(sfc.getRelatedSamplingFeatureRole(), is(role));
    }

    @Test
    public void should_set_relatedSamplingFeature_correct() {
        final SamplingFeature feature = new SamplingFeature(new CodeWithAuthority("test-feature"));
        final SamplingFeatureComplex sfc = new SamplingFeatureComplex("test-role", feature);

        assertThat(sfc.getRelatedSamplingFeature(), is(feature));
    }

}
