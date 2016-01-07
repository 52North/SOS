/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.exception.ows.concrete;

import static org.n52.sos.util.http.HTTPStatus.BAD_REQUEST;

import org.joda.time.DateTime;
import org.n52.sos.ogc.gml.time.Time;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 */
public class DateTimeFormatException extends DateTimeException {
    private static final long serialVersionUID = 4594521785170898431L;

    public DateTimeFormatException(final Time value) {
        this(value, null);
    }

    public DateTimeFormatException(final Time value, final Throwable cause) {
        withMessage("Error formatting %s %s", value.getClass().getSimpleName(), value);
        if (cause != null) {
            causedBy(cause);
        }
        setStatus(BAD_REQUEST);
    }

    public DateTimeFormatException(final DateTime value) {
        this(value, null);
    }

    public DateTimeFormatException(final DateTime value, final Throwable cause) {
        withMessage("Error formatting %s %s", value.getClass().getSimpleName(), value);
        if (cause != null) {
            causedBy(cause);
        }
        setStatus(BAD_REQUEST);
    }
}
