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

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public final class CacheValidation {

    private static final String NOT_NULL =  " may not contain null elements!";

    private CacheValidation() {
    }

    /**
     * Throws a {@code NullPointerExceptions} if value is null or a {@code IllegalArgumentException} if value is <= 0.
     *
     * @param name  the name of the value
     * @param value the value to check
     *
     * @throws NullPointerException     if value is null
     * @throws IllegalArgumentException if value is <= 0
     */
    public static void greaterZero(String name, Integer value)
            throws NullPointerException, IllegalArgumentException {
        if (Objects.requireNonNull(value, name) <= 0) {
            throw new IllegalArgumentException(name + " may not less or equal 0!");
        }
    }

    /**
     * Throws a {@code NullPointerExceptions} if value is null or a {@code IllegalArgumentException} if value is empty.
     *
     * @param name  the name of the value
     * @param value the value to check
     *
     * @throws NullPointerException     if value is null
     * @throws IllegalArgumentException if value is empty
     */
    public static void notNullOrEmpty(String name, String value)
            throws NullPointerException, IllegalArgumentException {
        if (Objects.requireNonNull(value, name).isEmpty()) {
            throw new IllegalArgumentException(name + " may not be empty!");
        }
    }

    /**
     * Throws a {@code NullPointerExceptions} if value is null or any value within is null.
     *
     * @param name  the name of the value
     * @param value the value to check
     *
     * @throws NullPointerException if value == null or value contains null
     */
    public static void noNullValues(String name, Collection<?> value)
            throws NullPointerException {
        if (Objects.requireNonNull(value, name).stream().anyMatch(Objects::isNull)) {
            throw new NullPointerException(name + NOT_NULL);
        }
    }

    /**
     * Throws a {@code NullPointerExceptions} if value is null or any key or value within is null.
     *
     * @param name  the name of the value
     * @param value the value to check
     *
     * @throws NullPointerException if value == null or value contains null values
     */
    public static void noNullValues(String name, Map<?, ?> value) throws NullPointerException {
        if (Objects.requireNonNull(value, name).entrySet().stream().anyMatch(Objects::isNull)) {
            throw new NullPointerException(name + NOT_NULL);
        }
    }

    /**
     * Throws a {@code NullPointerExceptions} if value is null or any value within is null or empty.
     *
     * @param name  the name of the value
     * @param value the value to check
     *
     * @throws NullPointerException     if value == null or value contains null
     * @throws IllegalArgumentException if any value is empty
     */
    public static void noNullOrEmptyValues(String name, Collection<String> value)
            throws NullPointerException, IllegalArgumentException {
        Objects.requireNonNull(value, name).forEach(o -> {
            if (o == null) {
                throw new NullPointerException(name + NOT_NULL);
            }
            if (o.isEmpty()) {
                throw new IllegalArgumentException(name + " may not contain empty elements!");
            }
        });
    }
}
