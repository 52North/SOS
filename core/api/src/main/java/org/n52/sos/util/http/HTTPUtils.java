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
package org.n52.sos.util.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.n52.sos.encode.ResponseProxy;
import org.n52.sos.encode.ResponseWriter;
import org.n52.sos.encode.ResponseWriterRepository;
import org.n52.sos.exception.HTTPException;
import org.n52.sos.request.ResponseFormat;
import org.n52.sos.response.ServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class HTTPUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(HTTPUtils.class);

    private HTTPUtils() {
    }

    public static boolean supportsGzipEncoding(HttpServletRequest req) {
        return checkHeader(req, HTTPHeaders.ACCEPT_ENCODING, HTTPConstants.GZIP_ENCODING);
    }

    public static boolean isGzipEncoded(HttpServletRequest req) {
        return checkHeader(req, HTTPHeaders.CONTENT_ENCODING, HTTPConstants.GZIP_ENCODING);
    }

    private static boolean checkHeader(HttpServletRequest req, String headerName, String value) {
        Enumeration<?> headers = req.getHeaders(headerName);
        while (headers.hasMoreElements()) {
            String header = (String) headers.nextElement();
            if ((header != null) && !header.isEmpty()) {
                String[] split = header.split(",");
                for (String string : split) {
                    if (string.equalsIgnoreCase(value)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static List<MediaType> getAcceptHeader(HttpServletRequest req) throws HTTPException {
        String header = req.getHeader(HTTPHeaders.ACCEPT);
        if (header == null || header.isEmpty()) {
            return Collections.singletonList(MediaTypes.WILD_CARD);
        }
        String[] values = header.split(",");
        ArrayList<MediaType> mediaTypes = new ArrayList<MediaType>(values.length);
        for (int i = 0; i < values.length; ++i) {
            try {
                // Fix for invalid HTTP-Accept header send by OGC OWS-Cite tests
                if (!" *; q=.2".equals(values[i]) && !"*; q=.2".equals(values[i]) && !" *; q=0.2".equals(values[i])
                        && !"*; q=0.2".equals(values[i])) {
                    mediaTypes.add(MediaType.parse(values[i]));
                } else {
                    LOGGER.warn("The HTTP-Accept header contains an invalid value: {}", values[i]);
                }
            } catch (IllegalArgumentException e) {
                throw new HTTPException(HTTPStatus.BAD_REQUEST, e);
            }
        }
        return mediaTypes;
    }

    public static InputStream getInputStream(HttpServletRequest req) throws IOException {
        if (isGzipEncoded(req)) {
            return new GZIPInputStream(req.getInputStream());
        } else {
            return req.getInputStream();
        }
    }

    public static void writeObject(HttpServletRequest request, HttpServletResponse response, MediaType contentType,
            Object object) throws IOException {
        writeObject(request, response, contentType, new GenericWritable(object, contentType));
    }

    public static void writeObject(HttpServletRequest request, HttpServletResponse response, ServiceResponse sr)
            throws IOException {
        response.setStatus(sr.getStatus().getCode());

        for (Entry<String, String> header : sr.getHeaderMap().entrySet()) {
            response.addHeader(header.getKey(), header.getValue());
        }

        if (!sr.isContentLess()) {
            writeObject(request, response, sr.getContentType(), new ServiceResponseWritable(sr));
        }
    }

    public static void writeObject(HttpServletRequest request, HttpServletResponse response, MediaType contentType,
            Writable writable) throws IOException {
        OutputStream out = null;
        response.setContentType(writable.getEncodedContentType().toString());

        try {
            out = response.getOutputStream();
            if (supportsGzipEncoding(request) && writable.supportsGZip()) {
                out = new GZIPOutputStream(out);
                response.setHeader(HTTPHeaders.CONTENT_ENCODING, HTTPConstants.GZIP_ENCODING);
            }

            writable.write(out, new ResponseProxy(response));
            out.flush();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private static class GenericWritable implements Writable {
        private final Object o;

        private ResponseWriter<Object> writer;

        /**
         * constructor
         * 
         * @param o
         *            {@link Object} to write
         * @param ct
         *            contentType to encode to
         */
        GenericWritable(Object o, MediaType ct) {
            this.o = o;
            writer = ResponseWriterRepository.getInstance().getWriter(o.getClass());
            if (writer == null) {
                throw new RuntimeException("no writer for " + o.getClass() + " found!");
            }
            writer.setContentType(ct);
        }

        @Override
        public boolean supportsGZip() {
            return writer.supportsGZip(o);
        }

        @Override
        public void write(OutputStream out, ResponseProxy responseProxy) throws IOException {
            writer.write(o, out, responseProxy);
        }

        public MediaType getEncodedContentType() {
        	if (o instanceof ResponseFormat) {
        		return writer.getEncodedContentType((ResponseFormat)o);
        	}
        	return writer.getContentType();
        }
    }

    private static class ServiceResponseWritable implements Writable {
        private final ServiceResponse response;

        ServiceResponseWritable(ServiceResponse response) {
            this.response = response;
        }

        @Override
        public void write(OutputStream out, ResponseProxy responseProxy) throws IOException {
            //set content length if not gzipped
            if (!(out instanceof GZIPOutputStream) && response.getContentLength() > -1) {
                responseProxy.setContentLength(response.getContentLength());
            }
            response.writeToOutputStream(out);
        }

        @Override
        public boolean supportsGZip() {
            return response.supportsGZip();
        }

		@Override
		public MediaType getEncodedContentType() {
			return response.getContentType();
		}
    }

    public interface Writable {
        void write(OutputStream out, ResponseProxy responseProxy) throws IOException;

        boolean supportsGZip();        
        
        MediaType getEncodedContentType();
    }

}
