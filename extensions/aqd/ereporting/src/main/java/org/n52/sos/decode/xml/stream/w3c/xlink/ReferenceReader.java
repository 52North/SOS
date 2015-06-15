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
package org.n52.sos.decode.xml.stream.w3c.xlink;

import java.net.URI;

import javax.xml.stream.XMLStreamException;

import org.n52.sos.decode.xml.stream.XmlReader;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.Reference;
import org.n52.sos.w3c.W3CConstants;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class ReferenceReader extends XmlReader<Reference> {

    private Reference reference;

    @Override
    protected void begin()
            throws XMLStreamException, OwsExceptionReport {
        this.reference = parseReference();
    }

    protected Reference parseReference() {
        Reference ref = new Reference();
        ref.setHref(URI.create(attr(W3CConstants.QN_XLINK_HREF).get()));
        ref.setActuate(attr(W3CConstants.QN_XLINK_ACTUATE).orNull());
        ref.setArcrole(attr(W3CConstants.QN_XLINK_ARCROLE).orNull());
        ref.setRole(attr(W3CConstants.QN_XLINK_ROLE).orNull());
        ref.setShow(attr(W3CConstants.QN_XLINK_SHOW).orNull());
        ref.setTitle(attr(W3CConstants.QN_XLINK_TITLE).orNull());
        ref.setType(attr(W3CConstants.QN_XLINK_TYPE).orNull());
        return ref;
    }

    @Override
    protected Reference finish() {
        return reference;
    }

}
