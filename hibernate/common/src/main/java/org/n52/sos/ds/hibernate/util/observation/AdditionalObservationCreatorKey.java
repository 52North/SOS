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
package org.n52.sos.ds.hibernate.util.observation;

import org.n52.sos.util.ClassHelper;
import org.n52.sos.util.Similar;

import com.google.common.base.Objects;

public class AdditionalObservationCreatorKey implements Similar<AdditionalObservationCreatorKey> {

    private final String namespace;

    private final Class<?> type;

    public AdditionalObservationCreatorKey(String namespace, Class<?> type) {
        this.namespace = namespace;
        this.type = type;
    }

    public String getNamespace() {
        return namespace;
    }

    public Class<?> getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("AdditionalObservationCreatorKey[namespace=%s, type=%s]", getNamespace(), getType()
                .getSimpleName());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && getClass() == obj.getClass()) {
            final AdditionalObservationCreatorKey o = (AdditionalObservationCreatorKey) obj;
            return Objects.equal(getType(), o.getType()) && Objects.equal(getNamespace(), o.getNamespace());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(3, 79, getNamespace(), getType());
    }

    @Override
    public int getSimilarity(AdditionalObservationCreatorKey key) {
        AdditionalObservationCreatorKey aocKey = (AdditionalObservationCreatorKey) key;
        if (Objects.equal(getNamespace(), aocKey.getNamespace())) {
            return ClassHelper.getSimiliarity(getType() != null ? getType() : Object.class,
                    aocKey.getType() != null ? aocKey.getType() : Object.class);
        } else {
            return -1;
        }
    }
}
