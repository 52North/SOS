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
package org.n52.sos.response;

import org.n52.sos.util.http.MediaType;


/**
 * Simple response class for binary data to be included as a response attachment (for download)
 * 
 * @author Shane StClair <shane@axiomalaska.com>
 * 
 * @since 4.1.0
 */
public class BinaryAttachmentResponse {
    private byte[] bytes;
    private MediaType contentType;
    private String filename;

    public BinaryAttachmentResponse(byte[] bytes, MediaType contentType, String filename) {
        this.bytes = bytes;
        this.contentType = contentType;
        this.filename = filename;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public MediaType getContentType() {
        return contentType;
    }

    public void setContentType(MediaType contentType) {
        this.contentType = contentType;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getSize() {
        return bytes == null ? -1 : bytes.length;
    }

    @Override
    public String toString() {
        return "BinaryAttachmentResponse [size = " + getSize()
                + ", contentType=" + contentType
                + ", filename=" + filename + "]";
    }
}
