/*
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
package org.n52.sos.encode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import org.n52.iceland.coding.encode.AbstractResponseWriter;
import org.n52.iceland.coding.encode.ResponseProxy;
import org.n52.iceland.coding.encode.ResponseWriterKey;
import org.n52.janmayen.Producer;
import org.n52.janmayen.http.MediaType;
import org.n52.janmayen.http.MediaTypes;
import org.n52.sos.exi.EXIObject;
import org.n52.sos.exi.EXISettings;
import org.n52.svalbard.encode.EncoderRepository;
import org.n52.svalbard.encode.exception.EncodingException;

import com.google.common.base.Charsets;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.api.sax.EXIResult;
import com.siemens.ct.exi.exceptions.EXIException;

/**
 * Writer class for {@link EXIObject}
 *
 * Converts XML documents via EXI encoding using {@link EXISettings}.
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.2.0
 *
 */
public class EXIResponseWriter extends AbstractResponseWriter<EXIObject> {

    public static final ResponseWriterKey KEY = new ResponseWriterKey(EXIObject.class);

    private final Producer<EXIFactory> exiFactory;
    private final Producer<XmlOptions> xmlOptions;

    // we can not use injection in this class as it is manually created by a factory
    public EXIResponseWriter(EncoderRepository encoderRepository,
                             Producer<EXIFactory> exiFactory,
                             Producer<XmlOptions> xmlOptions) {
        super(encoderRepository);
        this.exiFactory = exiFactory;
        this.xmlOptions = xmlOptions;
    }

    @Override
    public Set<ResponseWriterKey> getKeys() {
        return Collections.singleton(KEY);
    }

    @Override
    public void write(EXIObject exiObject, OutputStream out, ResponseProxy responseProxy) throws IOException, EncodingException {
        byte[] bytes = getBytes(exiObject);
        try (InputStream is = new ByteArrayInputStream(bytes)) {
            EXIResult result = new EXIResult(this.exiFactory.get());
            result.setOutputStream(out);
            XMLReader xmlReader = XMLReaderFactory.createXMLReader();
            xmlReader.setContentHandler(result.getHandler());
            xmlReader.parse(new InputSource(is));
        } catch (EXIException | SAXException e) {
            throw new EncodingException(e);
        }
    }

    private byte[] getBytes(EXIObject exi) {
        XmlObject doc = exi.getDoc();
        String text = doc.xmlText(this.xmlOptions.get());
        return text.getBytes(Charsets.UTF_8);
    }

    @Override
    public MediaType getContentType() {
        return MediaTypes.APPLICATION_EXI;
    }

    @Override
    public void setContentType(MediaType contentType) {
        // ignore
    }

    @Override
    public boolean supportsGZip(EXIObject t) {
        return false;
    }

}
