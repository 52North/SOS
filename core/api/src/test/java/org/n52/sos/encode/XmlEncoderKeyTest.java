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
package org.n52.sos.encode;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class XmlEncoderKeyTest {
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

    @Test
    public void testHashCode() {
        assertEquals(new XmlEncoderKey("test", C1.class).hashCode(), new XmlEncoderKey("test", C1.class).hashCode());
        assertEquals(new XmlEncoderKey(null, C1.class).hashCode(), new XmlEncoderKey(null, C1.class).hashCode());
        assertEquals(new XmlEncoderKey("test", null).hashCode(), new XmlEncoderKey("test", null).hashCode());
        assertNotEquals(new XmlEncoderKey("test", C1.class).hashCode(), new XmlEncoderKey(null, C1.class).hashCode());
        assertNotEquals(new XmlEncoderKey("test", null).hashCode(), new XmlEncoderKey("test", C1.class).hashCode());
        assertNotEquals(new XmlEncoderKey("test1", C1.class).hashCode(),
                new XmlEncoderKey("test", C1.class).hashCode());
        assertNotEquals(new XmlEncoderKey("test", C1.class).hashCode(),
                new XmlEncoderKey("test1", C1.class).hashCode());
        assertNotEquals(new XmlEncoderKey("test", C1.class).hashCode(), new XmlEncoderKey("test", C2.class).hashCode());
        assertNotEquals(new XmlEncoderKey("test", C1.class).hashCode(), new XmlEncoderKey("test", C2.class).hashCode());
    }

    @Test
    public void testEquals() {
        assertEquals(new XmlEncoderKey("test", C1.class), new XmlEncoderKey("test", C1.class));
        assertEquals(new XmlEncoderKey(null, C1.class), new XmlEncoderKey(null, C1.class));
        assertEquals(new XmlEncoderKey("test", null), new XmlEncoderKey("test", null));
        assertNotEquals(new XmlEncoderKey("test", C1.class), new XmlEncoderKey(null, C1.class));
        assertNotEquals(new XmlEncoderKey("test", null), new XmlEncoderKey("test", C1.class));
        assertNotEquals(new XmlEncoderKey("test1", C1.class), new XmlEncoderKey("test", C1.class));
        assertNotEquals(new XmlEncoderKey("test", C1.class), new XmlEncoderKey("test1", C1.class));
        assertNotEquals(new XmlEncoderKey("test", C1.class), new XmlEncoderKey("test", C2.class));
        assertNotEquals(new XmlEncoderKey("test", C1.class), new XmlEncoderKey("test", C2.class));
    }

    private void test(Class<?> a, Class<?> b, int expected) {
        assertEquals(expected, new XmlEncoderKey("test", a).getSimilarity(new XmlEncoderKey("test", b)));
    }

    @Test
    public void testSimilartiy() {
        assertEquals(-1, new XmlEncoderKey("test", C1.class).getSimilarity(new XmlEncoderKey("test1", C1.class)));
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
}
