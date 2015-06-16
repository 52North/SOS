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
package org.n52.sos.util;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * Helper class for Java objects.
 *
 * @since 4.0.0
 *
 */
public final class JavaHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaHelper.class);

    /**
     * hexadecimal values
     */
    private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
            'E', 'F' };

    /**
     * Message digest for generating single identifier
     */
    private static MessageDigest messageDigest;

    private static Reflections reflections;

    /**
     * Instantiation of the message digest
     */
    static {
        try {
            messageDigest = MessageDigest.getInstance("SHA1");
            reflections = new Reflections("org.n52.sos");
        } catch (final NoSuchAlgorithmException nsae) {
            LOGGER.error("Error while getting SHA-1 messagedigest!", nsae);
        }
    }

    /**
     * Generates a sensor id from description and current time as long.
     *
     * @param message
     *            sensor description
     * @return generated sensor id as hex SHA-1.
     */
    public static String generateID(final String message) {
        final long autoGeneratredID = new DateTime().getMillis();
        final String concate = message + Long.toString(autoGeneratredID);
        return bytesToHex(messageDigest.digest(concate.getBytes()));
    }

    /**
     * Transforms byte to hex representation
     *
     * @param b
     *            bytes
     * @return hex
     */
    private static String bytesToHex(final byte[] b) {
        final StringBuilder buf = new StringBuilder();
        for (final byte element : b) {
            buf.append(HEX_DIGITS[(element >> 4) & 0x0f]);
            buf.append(HEX_DIGITS[element & 0x0f]);
        }
        return buf.toString();
    }

    public static void appendTextToStringBuilderWithLineBreak(final StringBuilder stringBuilder, final String message) {
        if (stringBuilder != null && StringHelper.isNotEmpty(message)) {
            stringBuilder.append(message);
            stringBuilder.append(Constants.LINE_SEPARATOR_STRING);
        }
    }

    /**
     * return Object value as String
     *
     * @param object
     *            to get as String
     * @return String value
     */
    public static String asString(final Object object) {
        if (object instanceof String) {
            return (String) object;
        } else if (object instanceof BigDecimal) {
            final BigDecimal bdValue = (BigDecimal) object;
            return Double.toString(bdValue.doubleValue());
        } else if (object instanceof Double) {
            return ((Double) object).toString();
        } else if (object instanceof Integer) {
            return ((Integer) object).toString();
        }
        // TODO why not object.toString()?
        return Constants.EMPTY_STRING;
    }

    /**
     * return Object value as Double
     *
     * @param object
     *            to get as Double
     * @return Double value
     */
    public static Double asDouble(final Object object) {
        if (object instanceof String) {
            return Double.parseDouble((String) object);
        } else if (object instanceof BigDecimal) {
            final BigDecimal bdValue = (BigDecimal) object;
            return bdValue.doubleValue();
        } else if (object instanceof Double) {
            return (Double) object;
        }
        return new Double(Double.NaN);
    }

    /**
     * return Object value as Integer
     *
     * @param object
     *            to get as Integer
     * @return Integer value
     */
    public static Integer asInteger(Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof Integer) {
            return (Integer) object;
        } else if (object instanceof String) {
            return Integer.valueOf((String) object);
        }
        return null;
    }

    /**
     * return Object value as Boolean
     *
     * @param object
     *            to get as Boolean
     * @return Boolean value
     */
    public static Boolean asBoolean(Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof Boolean) {
            return (Boolean) object;
        } else if (object instanceof String) {
            return Boolean.valueOf((String) object);
        }
        return null;
    }

    public static <T> Set<Class<? extends T>> getSubclasses(Class<T> clazz) {
        return reflections.getSubTypesOf(clazz);
    }
    
    public static Set<Integer> getIntegerSetFromString(String s) {
        HashSet<Integer> set = Sets.newHashSet();
        if (StringHelper.isNotEmpty(s)) {
            Set<String> splitToSet = StringHelper.splitToSet(s, Constants.COMMA_STRING);
            if (CollectionHelper.isNotEmpty(splitToSet)) {
                for (String string : splitToSet) {
                    set.add(Integer.parseInt(string));
                }
            }
        }
        return set;
    }

    private JavaHelper() {
    }

}
