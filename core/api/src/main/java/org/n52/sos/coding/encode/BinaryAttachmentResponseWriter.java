/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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
package org.n52.sos.coding.encode;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import org.n52.iceland.coding.encode.AbstractResponseWriter;
import org.n52.iceland.coding.encode.ResponseProxy;
import org.n52.iceland.coding.encode.ResponseWriterKey;
import org.n52.shetland.ogc.sos.response.BinaryAttachmentResponse;
import org.n52.svalbard.encode.EncoderRepository;

import com.google.common.base.Strings;

/**
 * Writer for ServiceResponse (containing ByteArrayOutputStream)
 *
 * @author <a href="mailto:shane@axiomalaska.com">Shane StClair</a>
 *
 * @since 4.1.0
 */
public class BinaryAttachmentResponseWriter extends AbstractResponseWriter<BinaryAttachmentResponse> {
    public static final ResponseWriterKey KEY = new ResponseWriterKey(BinaryAttachmentResponse.class);

    public BinaryAttachmentResponseWriter(EncoderRepository encoderRepository) {
        super(encoderRepository);
    }

    @Override
    public Set<ResponseWriterKey> getKeys() {
        return Collections.singleton(KEY);
    }

    @Override
    public void write(BinaryAttachmentResponse response, OutputStream out, ResponseProxy responseProxy)
            throws IOException {

        if (response == null) {
            return;
        }

        byte[] bytes = response.getBytes();

        if (!(out instanceof GZIPOutputStream)) {
            responseProxy.setContentLength(bytes.length);
        }

        //binary
        responseProxy.addHeader(HeaderCode.CONTENT_TRANSFER_ENCODING,
                                HeaderCode.CONTENT_TRANSFER_ENCODING_BINARY);

        String fileName = response.getFilename();

        //filename
        if (!Strings.isNullOrEmpty(fileName)) {
            String value = String.format(HeaderCode.CONTENT_ATTACHMENT_FILENAME_FORMAT, fileName);
            responseProxy.addHeader(HeaderCode.CONTENT_DISPOSITION, value);
        }

        //write output now that headers and content length are in place
        out.write(bytes);

    }

    @Override
    public void write(BinaryAttachmentResponse response, OutputStream out) throws IOException {
        if (response == null) {
            return;
        }
        byte[] bytes = response.getBytes();
        //write output now that headers and content length are in place
        out.write(bytes);
    }

    @Override
    public boolean supportsGZip(BinaryAttachmentResponse t) {
        return false;
    }

    public interface HeaderCode {
        String CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";
        String CONTENT_TRANSFER_ENCODING_BINARY = "binary";
        String CONTENT_DISPOSITION = "Content-Disposition";
        String CONTENT_ATTACHMENT_FILENAME_FORMAT = "attachment; filename=\"%s\"";
    }
}
