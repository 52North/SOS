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
package org.n52.sos.decode.xml.stream;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

public abstract class XmlReader<T> {
    private static final Logger LOG = LoggerFactory.getLogger(XmlReader.class);
    private final XMLInputFactory inputFactory = XMLInputFactory.newInstance();
    private XMLStreamReader reader;
    private QName root;
    private int rootCount;

    public T read(InputStream in)
            throws XMLStreamException, OwsExceptionReport {
        return XmlReader.this.read(this.inputFactory.createXMLStreamReader(in));
    }

    private T read(XmlReader<?> reader)
            throws XMLStreamException, OwsExceptionReport {
        return read(reader.reader);
    }

    private T read(XMLStreamReader reader)
            throws XMLStreamException, OwsExceptionReport {
        this.reader = reader;
        this.root = toNextBeginTag();
        String cName = this.getClass().getSimpleName();
        this.rootCount = 0;
        if (root != null && !isEndTag()) {
            LOG.trace("{}: root: <{}:{}>", cName, root.getPrefix(),
                      root.getLocalPart());

            begin();

            //begin may proceed to the end of the element...
            if (isEndTag() && tagName().equals(root)) {
                return finish();
            }

            QName current;
            while ((current = toNextTag()) != null) {
                if (isStartTag()) {
                    LOG.trace("{}: begin: <{}:{}>", cName,
                              current.getPrefix(), current.getLocalPart());
                    if (current.equals(root)) {
                        ++rootCount;
                    }
                    read(current);
                } else if (isEndTag()) {
                    LOG.trace("{}: end: <{}:{}>", cName, current.getPrefix(),
                              current.getLocalPart());
                    if (current.equals(root)) {
                        if (rootCount == 0) {
                            break;
                        } else {
                            --rootCount;
                        }
                    }
                }
            }
        }
        return finish();
    }

    protected <T> T delegate(XmlReader<? extends T> reader)
            throws XMLStreamException, OwsExceptionReport {
        return reader.read(this);
    }

    private boolean isStartTag() {
        return this.reader.isStartElement();
    }

    private boolean isEndTag() {
        return this.reader.isEndElement();
    }

    private boolean hasNext()
            throws XMLStreamException {
        return this.reader.hasNext();
    }

    private void next()
            throws XMLStreamException {
        this.reader.next();
    }

    private QName toNextTag()
            throws XMLStreamException {
        if (hasNext()) {
            next();
            return toTag();
        } else {
            return null;
        }
    }

    private QName toNextBeginTag()
            throws XMLStreamException {
        while (!isStartTag() && hasNext()) {
            toNextTag();
        }
        return isStartTag() ? tagName() : null;
    }

    private QName toTag()
            throws XMLStreamException {
        while (!isStartTag() && !isEndTag() && hasNext()) {
            next();
        }
        return isStartTag() || isEndTag() ? tagName() : null;
    }

    protected Map<QName, String> attr() {
        int l = this.reader.getAttributeCount();
        Map<QName, String> attr = new HashMap<>(l);
        for (int i = 0; i < l; ++i) {
            if (this.reader.isAttributeSpecified(i)) {
                attr.put(this.reader.getAttributeName(i),
                         this.reader.getAttributeValue(i));
            }
        }
        return attr;
    }

    protected Iterable<Optional<String>> attr(Iterable<QName> names) {
        return Iterables
                .transform(names, new Function<QName, Optional<String>>() {
                    @Override
                    public Optional<String> apply(QName input) {
                        return XmlReader.this.attr(input);
                    }
                });
    }

    protected Optional<String> attr(QName qn) {
        return Optional.fromNullable(attr().get(qn));
    }

    protected Optional<String> attr(String name) {
        return attr(new QName(name));
    }

    protected QName tagName() {
        return this.reader.getName();
    }

    protected String chars()
            throws XMLStreamException {
        return this.reader.getElementText();
    }

    protected void begin()
            throws XMLStreamException, OwsExceptionReport {
        /* no op */
    }

    protected abstract T finish()
            throws OwsExceptionReport;

    protected void read(QName name)
            throws XMLStreamException, OwsExceptionReport {
        ignore();
    }

    protected void ignore() {
        QName name = this.reader.getName();
        String cName = getClass().getSimpleName();
        LOG.warn("{}: ignoring element {}:{}", cName,
                 name.getPrefix(), name.getLocalPart());
    }

}
