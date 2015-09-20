/*
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

import org.apache.xmlbeans.XmlObject;
import org.n52.iceland.exception.ows.OwsExceptionReport;
import org.n52.iceland.ogc.swes.SwesExtension;
import org.n52.iceland.util.CollectionHelper;
import org.n52.sos.ogc.swes.SwesExtensions;
import org.n52.sos.util.CodingHelper;

import net.opengis.swes.x20.ExtensibleResponseType;

/**
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 5.0.0
 *
 */
public interface ExtensibleResponseDecoder {

    default SwesExtensions parseExtensibleResponse(ExtensibleResponseType ert) throws OwsExceptionReport {
        return parseExtensibleResponseExtension(ert.getExtensionArray());
    }

    default SwesExtensions parseExtensibleResponseExtension(XmlObject[] extensionArray) throws OwsExceptionReport {
        if (CollectionHelper.isNotNullOrEmpty(extensionArray)) {
            final SwesExtensions extensions = new SwesExtensions();
            for (final XmlObject xbSwesExtension : extensionArray) {

                final Object obj = CodingHelper.decodeXmlElement(xbSwesExtension);
                if (obj instanceof SwesExtension<?>) {
                    extensions.addExtension((SwesExtension<?>) obj);
                } else {
                    extensions.addExtension(new SwesExtension<>().setValue(obj));
                }
            }
            return extensions;
        }
        return null;
    }

}
