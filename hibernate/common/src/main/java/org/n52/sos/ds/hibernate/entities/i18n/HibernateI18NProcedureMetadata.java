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
package org.n52.sos.ds.hibernate.entities.i18n;

import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.util.StringHelper;

/**
 * Implementation of {@link AbstractHibernateI18NMetadata} for {@link Procedure}
 *
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.0.0
 *
 */
public class HibernateI18NProcedureMetadata extends AbstractHibernateI18NMetadata {

    private static final long serialVersionUID = 8640014633908854735L;

    private String shortName;

    private String longName;

    /**
     * Get the short name
     *
     * @return the shortName
     */
    public String getShortname() {
        return shortName;
    }

    /**
     * Set the short name
     *
     * @param shortName
     *            the shortName to set
     */
    public void setShortname(String shortName) {
        this.shortName = shortName;
    }

    /**
     * Check if the short name is set
     *
     * @return <code>true</code>, if short name is set
     */
    public boolean isSetShortname() {
        return StringHelper.isNotEmpty(getShortname());
    }

    /**
     * Get the long name
     *
     * @return the longName
     */
    public String getLongname() {
        return longName;
    }

    /**
     * Set the long name
     *
     * @param longName
     *            the longName to set
     */
    public void setLongname(String longName) {
        this.longName = longName;
    }

    /**
     * Check if the long name is set
     *
     * @return <code>true</code>, if long name is set
     */
    public boolean isSetLongname() {
        return StringHelper.isNotEmpty(getLongname());
    }
}
