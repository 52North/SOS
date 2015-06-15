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
package org.n52.sos.ogc.om.features;

import static java.lang.Boolean.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 */
public class FeatureCollectionTest {

    @Test
    public final void should_remove_member_from_feature_collection() {
        final FeatureCollection features = new FeatureCollection();
        final String feature1Id = "feature-1";
        final SamplingFeature feature1 = new SamplingFeature(new CodeWithAuthority(feature1Id));
        features.addMember(feature1);
        final String feature2Id = "feature-2";
        final SamplingFeature feature2 = new SamplingFeature(new CodeWithAuthority(feature2Id));
        features.addMember(feature2);

        final SamplingFeature removedFeature = (SamplingFeature) features.removeMember(feature2Id);

        assertThat(removedFeature, is(equalTo(feature2)));
        assertThat(features.getMembers().size(), is(1));
        assertThat(features.getMembers().containsKey(feature2Id), is(FALSE));
        assertThat(features.getMembers().containsValue(feature2), is(FALSE));
        assertThat(features.getMembers().containsKey(feature1Id), is(TRUE));
        assertThat(features.getMembers().containsValue(feature1), is(TRUE));
    }

}
