/*
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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


import net.opengis.swes.x20.ExtensibleRequestType;

import org.apache.xmlbeans.XmlObject;

import org.n52.svalbard.decode.exception.DecodingException;
import org.n52.shetland.ogc.swe.SweAbstractDataComponent;
import org.n52.shetland.ogc.swes.SwesExtension;
import org.n52.shetland.ogc.swes.SwesExtensions;
import org.n52.shetland.util.CollectionHelper;
import org.n52.svalbard.xml.AbstractXmlDecoder;

public abstract class AbstractSwesDecoderv20<S> extends AbstractXmlDecoder<XmlObject, S> {

    protected SwesExtensions parseExtensibleRequest(ExtensibleRequestType extensibleRequestType) throws DecodingException {
        return parseExtensibleRequestExtension(extensibleRequestType.getExtensionArray());
    }

    protected SwesExtensions parseExtensibleRequestExtension(XmlObject[] extensionArray) throws DecodingException {
        if (CollectionHelper.isNotNullOrEmpty(extensionArray)) {
            final SwesExtensions extensions = new SwesExtensions();
            for (final XmlObject xbSwesExtension : extensionArray) {

                final Object obj = decodeXmlElement(xbSwesExtension);
                if (obj instanceof SwesExtension<?>) {
                    extensions.addExtension((SwesExtension<?>) obj);
                } else {
                    SwesExtension<Object> swesExtension = new SwesExtension<>();
                    if (obj instanceof SweAbstractDataComponent) {
                        swesExtension.setIdentifier(((SweAbstractDataComponent) obj).getIdentifier());
                        swesExtension.setDefinition(((SweAbstractDataComponent) obj).getDefinition());
                    }
                    swesExtension.setValue(obj);
                    extensions.addExtension(swesExtension);
                }
            }
            return extensions;
        }
        return null;
    }
}
