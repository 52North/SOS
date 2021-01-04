/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.util.procedure.generator;

import java.util.Objects;

/**
 * Key class for {@link HibernateProcedureDescriptionGeneratorFactory}
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.2.0
 *
 */
public class HibernateProcedureDescriptionGeneratorKey
        implements Comparable<HibernateProcedureDescriptionGeneratorKey> {

    private final String procedureDescriptionFormat;

    public HibernateProcedureDescriptionGeneratorKey(String procedureDescriptionFormat) {
        this.procedureDescriptionFormat = procedureDescriptionFormat;
    }

    public String getDescriptionFormat() {
        return procedureDescriptionFormat;
    }

    @Override
    public int compareTo(HibernateProcedureDescriptionGeneratorKey o) {
        if (o != null) {
            return Objects.equals(getDescriptionFormat(), o.getDescriptionFormat()) ? 0 : 1;
        } else {
            return -1;
        }
    }

    @Override
    public boolean equals(Object paramObject) {
        if (paramObject instanceof HibernateProcedureDescriptionGeneratorKey) {
            HibernateProcedureDescriptionGeneratorKey toCheck
                    = (HibernateProcedureDescriptionGeneratorKey) paramObject;
            return Objects.equals(getDescriptionFormat(), toCheck.getDescriptionFormat());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.procedureDescriptionFormat);
    }

    @Override
    public String toString() {
        return String.format("%s[format=%s]", getClass().getSimpleName(), getDescriptionFormat());
    }

}
