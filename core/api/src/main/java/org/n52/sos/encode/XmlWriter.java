/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.encode;

import java.io.OutputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePosition;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.DateTimeHelper;

public abstract class XmlWriter<T> {
    
    private final XMLEventFactory eventFactory = XMLEventFactory.newInstance();

    private final XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();

    protected abstract void init(OutputStream out) throws XMLStreamException;
    
    protected abstract void init(OutputStream out, String encoding) throws XMLStreamException;
    
    protected abstract T getXmlWriter();
    
    protected abstract void finish() throws XMLStreamException;
    
    protected abstract void attr(QName name, String value) throws XMLStreamException;

    protected abstract void attr(String name, String value) throws XMLStreamException;

    protected abstract void chars(String chars) throws XMLStreamException;
    
    protected abstract void end(QName name) throws XMLStreamException;

    protected abstract void end() throws XMLStreamException;

    protected abstract void namespace(String prefix, String namespace) throws XMLStreamException;

    protected abstract void start(QName name) throws XMLStreamException;

    protected abstract void start() throws XMLStreamException;
    
    protected abstract void empty(QName name) throws XMLStreamException;

    public abstract void write(OutputStream out) throws XMLStreamException, OwsExceptionReport;

    protected void time(TimeInstant time) throws XMLStreamException {
        time(time.getTimePosition());
    }
    
    protected void time(TimePosition time) throws XMLStreamException {
        chars(DateTimeHelper.formatDateTime2IsoString(time.getTime()));
    }
    
    protected XMLEventFactory getXmlEventFactory() {
        return this.eventFactory;
    }
    
    protected XMLOutputFactory getXmlOutputFactory() {
        return this.outputFactory;
    }
}
