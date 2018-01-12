/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.shetland.util.StringHelper;
import org.n52.sos.ds.hibernate.entities.Procedure;

import com.google.common.base.Strings;

/**
 * Strategy to create the {@link SosProcedureDescription} from a file.
 */
public class FileDescriptionCreationStrategy extends XmlStringDescriptionCreationStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileDescriptionCreationStrategy.class);

    @Override
    public SosProcedureDescription<?> create(Procedure p, String descriptionFormat, Locale i18n, Session s)
            throws OwsExceptionReport {
        try {
            SosProcedureDescription<?> desc = new SosProcedureDescription<>(readXml(read(p.getDescriptionFile())));
            desc.setIdentifier(p.getIdentifier());
            desc.setDescriptionFormat(p.getProcedureDescriptionFormat().getProcedureDescriptionFormat());
            return desc;
        } catch (IOException ex) {
            throw new NoApplicableCodeException().causedBy(ex);
        }
    }

    private InputStream getDocumentAsStream(String filename) {
        // check if filename contains placeholder for configured sensor directory
        LOGGER.debug("Procedure description file name '{}'!", filename);
        return FileDescriptionCreationStrategy.class.getResourceAsStream(filename);
    }

    private String read(String path)
            throws IOException {
        InputStream stream = getDocumentAsStream(path);
        return StringHelper.convertStreamToString(stream);
    }

    @Override
    public boolean apply(Procedure p) {
        return !Strings.isNullOrEmpty(p.getDescriptionFile());
    }
}
