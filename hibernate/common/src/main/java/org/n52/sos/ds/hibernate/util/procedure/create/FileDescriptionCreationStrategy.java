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
package org.n52.sos.ds.hibernate.util.procedure.create;

import java.io.InputStream;
import java.util.Locale;

import org.apache.xmlbeans.XmlObject;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.StringHelper;
import org.n52.sos.util.XmlHelper;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;

/**
 * Strategy to create the {@link SosProcedureDescription} from a file.
 */
public class FileDescriptionCreationStrategy implements
        DescriptionCreationStrategy {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(FileDescriptionCreationStrategy.class);

    @Override
    public SosProcedureDescription create(Procedure p, String descriptionFormat, Locale i18n, Session s)
            throws OwsExceptionReport {
        XmlObject xml = read(p.getDescriptionFile());
        SosProcedureDescription desc = decode(xml);
        desc.setIdentifier(p.getIdentifier());
        desc.setDescriptionFormat(p.getProcedureDescriptionFormat().getProcedureDescriptionFormat());
        return desc;
    }

    private InputStream getDocumentAsStream(String filename) {
        final StringBuilder builder = new StringBuilder();
        // check if filename contains placeholder for configured
        // sensor directory
        if (filename.startsWith("standard")) {
            filename = filename.replace("standard", "");
            builder.append(getServiceConfig().getSensorDir());
            builder.append("/");
        }
        builder.append(filename);
        LOGGER.debug("Procedure description file name '{}'!", filename);
        return Configurator.getInstance().getClass().
                getResourceAsStream(builder.toString());
    }

    private SosProcedureDescription decode(XmlObject xml)
            throws OwsExceptionReport {
        return (SosProcedureDescription) CodingHelper.decodeXmlElement(xml);
    }

    private XmlObject read(String path)
            throws OwsExceptionReport {
        InputStream stream = getDocumentAsStream(path);
        String string = StringHelper.convertStreamToString(stream);
        XmlObject xml = XmlHelper.parseXmlString(string);
        return xml;
    }

    @Override
    public boolean apply(Procedure p) {
        return !Strings.isNullOrEmpty(p.getDescriptionFile());
    }

    @VisibleForTesting
    ServiceConfiguration getServiceConfig() {
        return ServiceConfiguration.getInstance();
    }
}
