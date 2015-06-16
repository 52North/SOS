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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Set;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.xmlbeans.XmlObject;

import org.n52.iceland.coding.encode.Encoder;
import org.n52.iceland.coding.encode.EncoderKey;
import org.n52.iceland.coding.encode.ResponseProxy;
import org.n52.iceland.coding.encode.ResponseWriterKey;
import org.n52.iceland.exception.ows.OwsExceptionReport;
import org.n52.iceland.exception.ows.concrete.NoEncoderForKeyException;
import org.n52.iceland.service.ServiceConfiguration;
import org.n52.iceland.w3c.soap.SoapChain;
import org.n52.iceland.w3c.soap.SoapResponse;
import org.n52.iceland.coding.encode.AbstractResponseWriter;
import org.n52.sos.encode.streaming.StreamingEncoder;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.XmlOptionsHelper;


/**
 * Streaming SOAP response writer implementation.
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.1.0
 *
 */
public class SoapChainResponseWriter extends AbstractResponseWriter<SoapChain> {
    public static final ResponseWriterKey KEY = new ResponseWriterKey(SoapChain.class);

    @Override
    public Set<ResponseWriterKey> getKeys() {
        return Collections.singleton(KEY);
    }

    @Override
    public void write(SoapChain chain, OutputStream out, ResponseProxy responseProxy) throws IOException {
        try {
            Object o = encodeSoapResponse(chain, out);
            if (o != null) {
                if (o instanceof SOAPMessage) {
                    ((SOAPMessage) o).writeTo(out);
                } else if (o instanceof XmlObject) {
                    ((XmlObject) o).save(out, XmlOptionsHelper.getInstance().getXmlOptions());
                }
            }
        } catch (SOAPException | OwsExceptionReport ex) {
            throw new IOException(ex);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Object encodeSoapResponse(SoapChain chain, OutputStream out) throws OwsExceptionReport {
        EncoderKey key =
                CodingHelper.getEncoderKey(chain.getSoapResponse().getSoapNamespace(), chain.getSoapResponse());
        Encoder<?, SoapResponse> encoder = getEncoder(key);
        if (encoder != null) {
            if (isForceStreamingEncoding() && encoder instanceof StreamingEncoder) {
                ((StreamingEncoder) encoder).encode(chain.getSoapResponse(), out);
                return null;
            } else {
                return encoder.encode(chain.getSoapResponse());
            }
        } else {
            throw new NoEncoderForKeyException(key);
        }
    }

    private boolean isForceStreamingEncoding() {
        return ServiceConfiguration.getInstance().isForceStreamingEncoding();
    }

    @Override
    public boolean supportsGZip(SoapChain t) {
        return false;
    }

}
