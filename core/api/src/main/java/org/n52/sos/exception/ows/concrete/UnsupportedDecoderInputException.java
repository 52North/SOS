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
package org.n52.sos.exception.ows.concrete;

import static org.n52.sos.util.http.HTTPStatus.INTERNAL_SERVER_ERROR;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.decode.Decoder;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.w3c.dom.Node;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 */
public class UnsupportedDecoderInputException extends NoApplicableCodeException {
    private static final long serialVersionUID = 5561451567407304739L;

    public UnsupportedDecoderInputException(final Decoder<?, ?> decoder, final Object o) {
        if (o == null) {
            withMessage("Decoder %s can not decode 'null'", decoder.getClass().getSimpleName());
        } else {
            String name;
            if (o instanceof XmlObject) {
                final Node n = ((XmlObject) o).getDomNode();
                name = n.getPrefix() != null ? n.getPrefix() + ":" + n.getLocalName() : n.getLocalName();
            } else {
                name = o.getClass().getName();
            }
            withMessage("%s can not be decoded by %s because it is not yet implemented!", name, decoder.getClass()
                    .getName());
        }
        setStatus(INTERNAL_SERVER_ERROR);
    }
}
