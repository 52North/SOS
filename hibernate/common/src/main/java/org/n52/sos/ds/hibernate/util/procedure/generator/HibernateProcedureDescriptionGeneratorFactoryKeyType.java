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
package org.n52.sos.ds.hibernate.util.procedure.generator;

/**
 * Key class for {@link HibernateProcedureDescriptionGeneratorFactory}
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.2.0
 *
 */
public class HibernateProcedureDescriptionGeneratorFactoryKeyType implements
        Comparable<HibernateProcedureDescriptionGeneratorFactoryKeyType> {

    private String procedureDescriptionFormat;

    public HibernateProcedureDescriptionGeneratorFactoryKeyType(String procedureDescriptionFormat) {
        this.procedureDescriptionFormat = procedureDescriptionFormat;
    }

    public String getDescriptionFormat() {
        return procedureDescriptionFormat;
    }

    @Override
    public int compareTo(HibernateProcedureDescriptionGeneratorFactoryKeyType o) {
        if (o instanceof HibernateProcedureDescriptionGeneratorFactoryKeyType) {
            if (checkParameter(getDescriptionFormat(), o.getDescriptionFormat())) {
                return 0;
            }
            return 1;
        }
        return -1;
    }

    @Override
    public boolean equals(Object paramObject) {
        if (paramObject instanceof HibernateProcedureDescriptionGeneratorFactoryKeyType) {
            HibernateProcedureDescriptionGeneratorFactoryKeyType toCheck =
                    (HibernateProcedureDescriptionGeneratorFactoryKeyType) paramObject;
            return (checkParameter(getDescriptionFormat(), toCheck.getDescriptionFormat()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 7;
        hash = prime * hash + (this.getDescriptionFormat() != null ? this.getDescriptionFormat().hashCode() : 0);
        return hash;
    }

    private boolean checkParameter(String localParameter, String parameterToCheck) {
        if (localParameter == null && parameterToCheck == null) {
            return true;
        }
        return localParameter != null && parameterToCheck != null && localParameter.equals(parameterToCheck);
    }

    @Override
    public String toString() {
        return String.format("%s[from=%s, to=%s]", getClass().getSimpleName(), getDescriptionFormat());
    }

}
