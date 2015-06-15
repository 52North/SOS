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
package org.n52.sos.config;

/**
 * Abstract, generic implementation of {@code Ordered}.
 * <p/>
 * 
 * @param <T>
 *            the type of the class extending this class
 *            <p/>
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public abstract class AbstractOrdered<T extends Ordered<T>> implements Ordered<T> {

    private float order;

    @Override
    public float getOrder() {
        return this.order;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setOrder(float order) {
        this.order = order;
        return (T) this;
    }

    @Override
    public int compareTo(Ordered<?> t) {
        int compare = Float.compare(getOrder(), t.getOrder());
        if (compare == 0 && t instanceof AbstractOrdered) {
            AbstractOrdered<?> ao = (AbstractOrdered) t;
            if (getSuborder() == null) {
                return 1;
            } else if (ao.getSuborder() == null) {
                return -1;
            } else {
                return getSuborder().compareTo(ao.getSuborder());
            }
        }
        return compare;
    }

    protected abstract String getSuborder();
}
