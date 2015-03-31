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
package org.n52.sos.decode;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.aqd.AqdConstants;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.exception.ows.concrete.UnsupportedDecoderInputException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.service.AbstractServiceCommunicationObject;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.XmlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

public class AqdDecoderv10 implements Decoder<AbstractServiceCommunicationObject, XmlObject> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AqdDecoderv10.class);
	
	  private static final Set<DecoderKey> DECODER_KEYS = 
	            CodingHelper.xmlDecoderKeysForOperation(AqdConstants.AQD, AqdConstants.VERSION,
	                    SosConstants.Operations.GetCapabilities, SosConstants.Operations.GetObservation,
	                    SosConstants.Operations.DescribeSensor);
	
	public AqdDecoderv10() {
		  LOGGER.debug("Decoder for the following keys initialized successfully: {}!", Joiner.on(", ")
	                .join(DECODER_KEYS));
	}

	@Override
	public Set<String> getConformanceClasses() {
		 return Collections.emptySet();
	}

	@Override
	public Set<DecoderKey> getDecoderKeyTypes() {
			return Collections.unmodifiableSet(DECODER_KEYS);
	}

	@Override
	public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
		 return Collections.emptyMap();
	}

	@Override
	public AbstractServiceCommunicationObject decode(XmlObject objectToDecode)
			throws OwsExceptionReport, UnsupportedDecoderInputException {
		return (AbstractServiceCommunicationObject) CodingRepository
				.getInstance()
				.getDecoder(
						new XmlNamespaceDecoderKey(XmlHelper
								.getNamespace(objectToDecode), XmlObject.class))
				.decode(objectToDecode);
	}

}
