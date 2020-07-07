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
package org.n52.sos.ds.hibernate.util;

/**
 * Class that describes a time primitive of an entity. Instants are represented
 * by one field and periods by two.
 *
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 * @since 4.0.0
 */
public abstract class AbstractTimePrimitiveFieldDescriptor {
    private final String begin;
    private final String end;

    /**
     * Creates a new descriptor for a period.
     *
     * @param begin
     *            the begin field
     * @param end
     *            the end field
     */
    public AbstractTimePrimitiveFieldDescriptor(String begin, String end) {
        if (begin == null) {
            throw new NullPointerException("start may not be null");
        }
        this.begin = begin;
        this.end = end;
    }

    /**
     * Creates a new descriptor for a time instant.
     *
     * @param position
     *            the field name
     */
    public AbstractTimePrimitiveFieldDescriptor(String position) {
        this(position, null);
    }

    /**
     * @return the begin position of the period
     */
    public String getBeginPosition() {
        return begin;
    }

    /**
     * @return the end position of the period
     */
    public String getEndPosition() {
        return end;
    }

    /**
     * @return if this descriptor describes a period
     */
    public boolean isPeriod() {
        return getEndPosition() != null;
    }

    /**
     * @return the field name of the instant
     */
    public String getPosition() {
        return getBeginPosition();
    }

    /**
     * @return if this descriptor describes a instant
     */
    public boolean isInstant() {
        return !isPeriod();
    }

}
