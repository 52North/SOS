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

import org.n52.sos.coding.CodingRepository;
import org.n52.sos.request.ResponseFormat;
import org.n52.sos.util.http.MediaType;
import org.n52.sos.util.http.MediaTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract {@link ResponseWriter} class for response streaming
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 *
 * @param <T>
 *            generic for the element to write
 */
public abstract class AbstractResponseWriter<T> implements ResponseWriter<T> {   
	private static final Logger LOGGER = LoggerFactory
			.getLogger(AbstractResponseWriter.class);
    private MediaType contentType;

    @Override
    public MediaType getContentType() {
        return contentType;
    }

    @Override
    public void setContentType(MediaType contentType) {
        this.contentType = contentType;
    }
    
    /**
     * Check if contentType is set 
     * @return <code>true</code>, if contentType is set
     */
    protected boolean isSetContentType() {
        return getContentType() != null;
    }

    /**
     * Getter for encoder, encapsulates the instance call
     * 
     * @param key
     *            Encoder key
     * @return Matching encoder
     */
    protected <D, S> Encoder<D, S> getEncoder(EncoderKey key) {
        return CodingRepository.getInstance().getEncoder(key);
    }

    
	@Override
	public MediaType getEncodedContentType(ResponseFormat responseFormat) {
		if (responseFormat.isSetResponseFormat()) {
			MediaType contentTypeFromResponseFormat = null;
			try {
				contentTypeFromResponseFormat = MediaType.parse(
						responseFormat.getResponseFormat())
						.withoutParameters();
			} catch (IllegalArgumentException iae) {
				LOGGER.debug("Requested responseFormat {} is not a MediaType",
						responseFormat.getResponseFormat());
			}
			if (contentTypeFromResponseFormat != null) {
				if (MediaTypes.COMPATIBLE_TYPES.containsEntry(contentTypeFromResponseFormat, getContentType())) {
					return getContentType();
				}
				return contentTypeFromResponseFormat;
			}
		}
		return getContentType();
	}
}
