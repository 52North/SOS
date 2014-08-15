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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;

import org.n52.sos.response.BinaryAttachmentResponse;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

/**
 * Writer for ServiceResponse (containing ByteArrayOutputStream)
 * 
 * @author Shane StClair <shane@axiomalaska.com>
 * 
 * @since 4.1.0
 */
public class BinaryAttachmentResponseWriter extends AbstractResponseWriter<BinaryAttachmentResponse> {
    public class HeaderCode {
        public static final String CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";
        public static final String CONTENT_TRANSFER_ENCODING_BINARY = "binary";

        public static final String CONTENT_DISPOSITION = "Content-Disposition";
        public static final String CONTENT_ATTACHMENT_FILENAME_FORMAT = "attachment; filename=\"%s\"";
    }

    @Override
    public Class<BinaryAttachmentResponse> getType() {
        return BinaryAttachmentResponse.class;
    }

    @Override
    public void write(BinaryAttachmentResponse binaryAttachmentResponse, OutputStream out) throws IOException {
        out.write(binaryAttachmentResponse.getBytes());
    }

    @Override
    public boolean supportsGZip(BinaryAttachmentResponse t) {
        return false;
    }
    
    @Override
    public Map<String,String> getResponseHeaders(BinaryAttachmentResponse binaryAttachmentResponse) {
        Map<String,String> responseHeaders = Maps.newHashMap();

        //binary
        responseHeaders.put(HeaderCode.CONTENT_DISPOSITION, String.format(
                HeaderCode.CONTENT_ATTACHMENT_FILENAME_FORMAT, binaryAttachmentResponse.getFilename()));

        if (binaryAttachmentResponse != null) {
            //filename
            if (!Strings.isNullOrEmpty(binaryAttachmentResponse.getFilename())) {
                responseHeaders.put(HeaderCode.CONTENT_DISPOSITION, String.format(
                        HeaderCode.CONTENT_ATTACHMENT_FILENAME_FORMAT, binaryAttachmentResponse.getFilename()));
            }
        }

        return Collections.unmodifiableMap(responseHeaders);
    }

    @Override
    public int getContentLength(BinaryAttachmentResponse t) {
        return t.getSize();
    }
}
