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
package org.n52.sos.encode.json.inspire;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.rules.ExternalResource;
import org.n52.sos.service.SosContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class ConfiguredSettingsManager extends ExternalResource {
    private static final Logger LOG = LoggerFactory
            .getLogger(ConfiguredSettingsManager.class);

    private File tempDir;

    @Override
    protected void before()
            throws Throwable {
        createDirectory();
    }

    @Override
    protected void after() {
        deleteDirectory();
    }

    protected void createDirectory()
            throws IOException {
        tempDir = File.createTempFile("sos-test-case", "");
        FileUtils.forceDelete(tempDir);
        FileUtils.forceMkdir(tempDir);
        SosContextListener.setPath(tempDir.getAbsolutePath());
    }

    protected void deleteDirectory() {
        try {
            if (tempDir != null) {
                FileUtils.deleteDirectory(tempDir);
            }
        } catch (IOException ex) {
            LOG.error("Error deleting temp dir", ex);
        }
    }
}
