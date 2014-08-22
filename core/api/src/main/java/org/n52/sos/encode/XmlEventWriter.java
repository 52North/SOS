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
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

import org.n52.sos.util.Constants;

/**
 * Abstract {@link XmlWriter} class for {@link XMLEventWriter}
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 *
 */
public abstract class XmlEventWriter<S> extends XmlWriter<XMLEventWriter, S> {

    private XMLEventWriter w;
    
    private final XMLEventFactory eventFactory = XMLEventFactory.newInstance();

    @Override
    protected void init(OutputStream out, String encoding, EncodingValues encodingValues) throws XMLStreamException {
        this.w = getXmlOutputFactory().createXMLEventWriter(out, encoding);
        this.out = out;
        indent = encodingValues.getIndent();
    }

    @Override
    protected XMLEventWriter getXmlWriter() {
        return w;
    }

    @Override
    protected void attr(QName name, String value) throws XMLStreamException {
        getXmlWriter().add(getXmlEventFactory().createAttribute(name, value));
    }

    @Override
    protected void attr(String name, String value) throws XMLStreamException {
        getXmlWriter().add(getXmlEventFactory().createAttribute(name, value));
    }
    
    protected void attr(String namespace, String localName, String value) throws XMLStreamException {
        attr(new QName(namespace, localName), value);
    }


    @Override
    protected void namespace(String prefix, String namespace) throws XMLStreamException {
        getXmlWriter().add(getXmlEventFactory().createNamespace(prefix, namespace));
    }

    @Override
    protected void start(QName name) throws XMLStreamException {
        getXmlWriter()
                .add(getXmlEventFactory().createStartElement(name.getPrefix(), name.getNamespaceURI(),
                        name.getLocalPart()));
    }

    @Override
    protected void start(boolean embedded) throws XMLStreamException {
        if (!embedded) {
            getXmlWriter().add(getXmlEventFactory().createStartDocument(Constants.DEFAULT_ENCODING, XML_VERSION));
        }
    }

    @Override
    protected void empty(QName name) throws XMLStreamException {
        start(name);
        end(name);
    }

    @Override
    protected void chars(String chars) throws XMLStreamException {
        getXmlWriter().add(getXmlEventFactory().createCharacters(chars));
    }

    @Override
    protected void end(QName name) throws XMLStreamException {
        getXmlWriter().add(
                getXmlEventFactory().createEndElement(name.getPrefix(), name.getNamespaceURI(), name.getLocalPart()));
    }
    
    @Override
    protected void endInline(QName name) throws XMLStreamException {
        getXmlWriter().add(
                getXmlEventFactory().createEndElement(name.getPrefix(), name.getNamespaceURI(), name.getLocalPart()));
    }

    @Override
    protected void end() throws XMLStreamException {
        getXmlWriter().add(getXmlEventFactory().createEndDocument());
    }

    protected void finish() throws XMLStreamException {
        flush();
        getXmlWriter().close();
    }

    @Override
    protected void flush() throws XMLStreamException {
        getXmlWriter().flush();
    }
    
    /**
     * @return
     */
    protected XMLEventFactory getXmlEventFactory() {
        return this.eventFactory;
    }
}
