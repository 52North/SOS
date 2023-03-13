/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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

import org.apache.xmlbeans.XmlException;
import org.n52.janmayen.http.HTTPStatus;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;

public class XmlEncodeException extends NoApplicableCodeException {
    private static final long serialVersionUID = -3574545413710594013L;

    public XmlEncodeException(final String name, final String xml, final XmlException e) {
        withMessage("Error while encoding %s:\n%s", name, xml).causedBy(e);
        setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
    }

    public XmlEncodeException(final String name, final XmlException e) {
        withMessage("Error while encoding %s", name).causedBy(e);
        setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
    }
}
