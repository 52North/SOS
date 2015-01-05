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
import org.apache.xmlbeans.impl.values.XmlAnyTypeImpl;
import org.n52.sos.exception.ows.concrete.UnsupportedDecoderInputException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swes.SwesExtension;
import org.n52.sos.ogc.swes.SwesConstants;
import org.n52.sos.ogc.swes.SwesExtensionImpl;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.w3c.W3CConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 */
public class SwesExtensionDecoderv20 implements Decoder<SwesExtension<?>, XmlObject> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SwesDecoderv20.class);

    @SuppressWarnings("unchecked")
    private static final Set<DecoderKey> DECODER_KEYS = CollectionHelper.union(CodingHelper.decoderKeysForElements(
            W3CConstants.NS_XS , XmlAnyTypeImpl.class), CodingHelper.decoderKeysForElements(
            SwesConstants.NS_SWES_20, XmlAnyTypeImpl.class));

    public SwesExtensionDecoderv20() {
        LOGGER.debug("Decoder for the following keys initialized successfully: {}!", Joiner.on(", ")
                .join(DECODER_KEYS));
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
    public Set<String> getConformanceClasses() {
        return Collections.emptySet();
    }

    @Override
    public SwesExtension<?> decode(final XmlObject xmlObject) throws OwsExceptionReport,
            UnsupportedDecoderInputException {
        if (isSwesExtension(xmlObject)) {
            final XmlObject[] children = xmlObject.selectPath("./*");

            if (children.length == 1) {
                final Object xmlObj = CodingHelper.decodeXmlElement(children[0]);
                final SwesExtension<Object> extension = new SwesExtensionImpl<Object>();
                extension.setValue(xmlObj);
                if (isSweAbstractDataComponent(xmlObj)) {
                    extension.setDefinition(((SweAbstractDataComponent) xmlObj).getDefinition());
                }
                return extension;
            }
        }
        throw new UnsupportedDecoderInputException(this, xmlObject);
    }

    private boolean isSweAbstractDataComponent(final Object xmlObj) {
        return xmlObj instanceof SweAbstractDataComponent && ((SweAbstractDataComponent) xmlObj).isSetDefinition();
    }

    private boolean isSwesExtension(final XmlObject xmlObject) {
        return xmlObject.getDomNode().getNamespaceURI().equalsIgnoreCase(SwesConstants.NS_SWES_20)
                && xmlObject.getDomNode().getLocalName().equalsIgnoreCase("extension");
    }

}
