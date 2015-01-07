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
package org.n52.sos.inspire;

import org.joda.time.DateTime;
import org.n52.sos.ogc.gml.time.TimeInstant;

/**
 * Service internal representation of INSPIRE date of creation
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 * 
 */
public class InspireDateOfCreation extends TimeInstant implements InspireDateOf {

    private static final long serialVersionUID = -1728343175298116901L;

    /**
     * constructor
     */
    public InspireDateOfCreation() {
        super();
    }

    /**
     * constructor
     * 
     * @param dateTime
     *            the date of creation
     */
    public InspireDateOfCreation(DateTime dateTime) {
        super(dateTime);
    }

    @Override
    public String toString() {
        return String.format("%s %n[%n value=%s%n]", this.getClass().getSimpleName(), super.toString());
    }
}
