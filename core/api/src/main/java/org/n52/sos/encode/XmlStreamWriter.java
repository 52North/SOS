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
package org.n52.sos.encode;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.n52.sos.util.Constants;
import org.n52.sos.w3c.W3CConstants;

import com.google.common.xml.XmlEscapers;

/**
 * Abstract {@link XmlWriter} class for {@link XMLStreamWriter}
 *
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.0.2
 *
 */
public abstract class XmlStreamWriter<S> extends XmlWriter<XMLStreamWriter, S> {

    private final Map<String, String> prefixes = new HashMap<>();

    private XMLStreamWriter w;

    @Override
    protected void init(OutputStream out, String encoding, EncodingValues encodingValues) throws XMLStreamException {
        this.w = getXmlOutputFactory().createXMLStreamWriter(out, encoding);
        this.out = out;
        indent = encodingValues.getIndent();
    }

    @Override
    protected XMLStreamWriter getXmlWriter() {
        return w;
    }

    @Override
    protected void attr(QName name, String value) throws XMLStreamException {
        getXmlWriter().writeAttribute(name.getPrefix(), name.getNamespaceURI(), name.getLocalPart(), value);
    }

    @Override
    protected void attr(String name, String value) throws XMLStreamException {
        getXmlWriter().writeAttribute(name, value);
    }

    @Override
    protected void attr(String namespace, String localName, String value) throws XMLStreamException {
        getXmlWriter().writeAttribute(W3CConstants.NS_XSI, W3CConstants.SCHEMA_LOCATION, value);
    }

    @Override
    protected void namespace(String prefix, String namespace) throws XMLStreamException {
        String ns = prefixes.get(prefix);
        if (ns == null) {
            getXmlWriter().writeNamespace(prefix, namespace);
            prefixes.put(prefix, namespace);
        } else {
            if (!ns.equals(namespace)) {
                throw new XMLStreamException("Prefix <" + prefix + "> is already bound to <" + ns + ">");
            }
        }
    }

    @Override
    protected void start(QName name) throws XMLStreamException {
        writeIndent(indent++);
        getXmlWriter().writeStartElement(name.getPrefix(), name.getLocalPart(), name.getNamespaceURI());
    }

    @Override
    protected void start(boolean embedded) throws XMLStreamException {
        if (!embedded) {
            getXmlWriter().writeStartDocument(Constants.DEFAULT_ENCODING, XML_VERSION);
            writeNewLine();
        }
    }

    @Override
    protected void empty(QName name) throws XMLStreamException {
        writeIndent(indent);
        getXmlWriter().writeEmptyElement(name.getPrefix(), name.getLocalPart(), name.getNamespaceURI());
    }

    @Override
    protected void chars(String chars) throws XMLStreamException {
        chars(chars, true);
    }

    @Override
    protected void chars(String chars, boolean escape) throws XMLStreamException {
        if (escape) {
            chars = XmlEscapers.xmlContentEscaper().escape(chars);
        }
        getXmlWriter().writeCharacters(chars);
    }

    @Override
    protected void end(QName name) throws XMLStreamException {
        writeIndent(--indent);
        getXmlWriter().writeEndElement();
        flush();
    }

    @Override
    protected void endInline(QName name) throws XMLStreamException {
        --indent;
        getXmlWriter().writeEndElement();
        flush();
    }

    @Override
    protected void end() throws XMLStreamException {
        getXmlWriter().writeEndDocument();
        flush();
    }

    @Override
    protected void finish() throws XMLStreamException {
        flush();
        getXmlWriter().close();
    }

    @Override
    protected void flush() throws XMLStreamException {
        getXmlWriter().flush();
    }

}
