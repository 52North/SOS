/**
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.util.http;

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

import org.n52.iceland.encode.ResponseProxy;
import org.n52.iceland.encode.ResponseWriter;
import org.n52.iceland.encode.ResponseWriterRepository;
import org.n52.iceland.exception.HTTPException;
import org.n52.iceland.request.ResponseFormat;
import org.n52.iceland.response.ServiceResponse;
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
