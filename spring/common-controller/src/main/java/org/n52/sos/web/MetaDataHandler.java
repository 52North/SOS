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
package org.n52.sos.web;

import javax.servlet.ServletContext;

import org.n52.sos.util.AbstractEnumPropertiesFileHandler;

/**
 * @since 4.0.0
 * 
 */
public class MetaDataHandler extends AbstractEnumPropertiesFileHandler<MetaDataHandler.Metadata> {

    public enum Metadata {
        GIT_BRANCH, GIT_COMMIT, VERSION, BUILD_DATE, INSTALL_DATE;
    }

    private static final String PROPERTIES = "/WEB-INF/classes/meta.properties";

    private static MetaDataHandler instance;

    static synchronized MetaDataHandler getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Not yet initialized");
        }
        return instance;
    }

    static synchronized MetaDataHandler getInstance(final ServletContext ctx) {
        if (instance == null) {
            instance = new MetaDataHandler(ctx);
        }
        return instance;
    }

    private MetaDataHandler(final ServletContext ctx) {
        super(ctx, PROPERTIES);
    }

}
