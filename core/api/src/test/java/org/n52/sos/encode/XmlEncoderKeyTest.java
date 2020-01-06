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
package org.n52.sos.encode;

import org.junit.Assert;

import org.junit.Test;

import org.n52.svalbard.encode.XmlEncoderKey;

/**
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 *
 * @since 4.0.0
 */
public class XmlEncoderKeyTest {

    private static final String TEST = "test";
    private static final String TEST_1 = "test1";

    @Test
    public void testHashCode() {
        Assert.assertEquals(new XmlEncoderKey(TEST, C1.class).hashCode(),
                new XmlEncoderKey(TEST, C1.class).hashCode());
        Assert.assertEquals(new XmlEncoderKey(null, C1.class).hashCode(),
                new XmlEncoderKey(null, C1.class).hashCode());
        Assert.assertEquals(new XmlEncoderKey(TEST, null).hashCode(), new XmlEncoderKey(TEST, null).hashCode());
        Assert.assertNotEquals(new XmlEncoderKey(TEST, C1.class).hashCode(),
                new XmlEncoderKey(null, C1.class).hashCode());
        Assert.assertNotEquals(new XmlEncoderKey(TEST, null).hashCode(), new XmlEncoderKey(TEST, C1.class).hashCode());
        Assert.assertNotEquals(new XmlEncoderKey(TEST_1, C1.class).hashCode(),
                new XmlEncoderKey(TEST, C1.class).hashCode());
        Assert.assertNotEquals(new XmlEncoderKey(TEST, C1.class).hashCode(),
                new XmlEncoderKey(TEST_1, C1.class).hashCode());
        Assert.assertNotEquals(new XmlEncoderKey(TEST, C1.class).hashCode(),
                new XmlEncoderKey(TEST, C2.class).hashCode());
        Assert.assertNotEquals(new XmlEncoderKey(TEST, C1.class).hashCode(),
                new XmlEncoderKey(TEST, C2.class).hashCode());
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(new XmlEncoderKey(TEST, C1.class), new XmlEncoderKey(TEST, C1.class));
        Assert.assertEquals(new XmlEncoderKey(null, C1.class), new XmlEncoderKey(null, C1.class));
        Assert.assertEquals(new XmlEncoderKey(TEST, null), new XmlEncoderKey(TEST, null));
        Assert.assertNotEquals(new XmlEncoderKey(TEST, C1.class), new XmlEncoderKey(null, C1.class));
        Assert.assertNotEquals(new XmlEncoderKey(TEST, null), new XmlEncoderKey(TEST, C1.class));
        Assert.assertNotEquals(new XmlEncoderKey(TEST_1, C1.class), new XmlEncoderKey(TEST, C1.class));
        Assert.assertNotEquals(new XmlEncoderKey(TEST, C1.class), new XmlEncoderKey(TEST_1, C1.class));
        Assert.assertNotEquals(new XmlEncoderKey(TEST, C1.class), new XmlEncoderKey(TEST, C2.class));
        Assert.assertNotEquals(new XmlEncoderKey(TEST, C1.class), new XmlEncoderKey(TEST, C2.class));
    }

    private void test(Class<?> a, Class<?> b, int expected) {
        Assert.assertEquals(expected, new XmlEncoderKey(TEST, a).getSimilarity(new XmlEncoderKey(TEST, b)));
    }

    @Test
    public void testSimilartiy() {
        Assert.assertEquals(-1, new XmlEncoderKey(TEST, C1.class).getSimilarity(new XmlEncoderKey(TEST_1, C1.class)));
        test(C1.class, C2.class, 1);
        test(C1.class, C3.class, 2);
        test(C1.class, C4.class, 3);
        test(C3.class, C4.class, 1);
        test(I1.class, C4.class, 5);
        test(I1.class, I4.class, -1);
        test(C1.class, C5.class, -1);
        test(C1.class, I1.class, -1);

        test(C1[].class, C2[].class, 1);
        test(C1[].class, C3[].class, 2);
        test(C1[].class, C4[].class, 3);
        test(C3[].class, C4[].class, 1);
        test(I1[].class, C4[].class, 5);
        test(I1[].class, I4[].class, -1);
        test(C1[].class, C5[].class, -1);
        test(C1[].class, I1[].class, -1);

        test(C1[].class, C1.class, -1);
    }

    private class C1 {
    }

    private class C2 extends C1 implements I3 {
    }

    private class C3 extends C2 {
    }

    private class C4 extends C3 {
    }

    private class C5 {
    }

    private interface I1 {
    }

    private interface I2 extends I1 {
    }

    private interface I3 extends I2 {
    }

    private interface I4 {
    }
}
