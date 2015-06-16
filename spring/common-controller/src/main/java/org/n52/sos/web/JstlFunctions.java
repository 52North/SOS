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

import java.io.File;
import java.util.Map;

import javax.servlet.ServletContext;

import org.n52.sos.service.DatabaseSettingsHandler;
import org.n52.sos.util.JSONUtils;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @since 4.0.0
 *
 */
public class JstlFunctions {
    public static final boolean HAS_INSTALLER = hasClass("org.n52.sos.web.install.InstallIndexController");

    public static final boolean HAS_CLIENT = hasClass("org.n52.sos.web.ClientController");

    public static final boolean HAS_ADMIN = hasClass("org.n52.sos.web.admin.AdminIndexController");

    private JstlFunctions() {
    }

    public static boolean configurated(ServletContext ctx) {
        return DatabaseSettingsHandler.getInstance(ctx).exists();
    }

    public static boolean hasClient() {
        return HAS_CLIENT;
    }

    public static boolean hasInstaller() {
        return HAS_INSTALLER;
    }

    public static boolean hasAdministrator() {
        return HAS_ADMIN;
    }

    public static boolean hasClass(String c) {
        try {
            Class.forName(c);
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    /**
     * Check if the view in exists.
     * 
     * @param ctx
     *            {@link ServletContext} to get real path
     * @param path
     *            View path and name
     * @return <code>true</code>, if view exists
     */
    public static boolean viewExists(ServletContext ctx, String path) {
        return fileExists(ctx, "/WEB-INF/views/" + path);
    }

    /**
     * Check if the {@link File} in '/static/' exists.
     * 
     * @param ctx
     *            {@link ServletContext} to get real path
     * @param path
     *            File path and name in '/static/'
     * @return <code>true</code>, if file exists
     */
    public static boolean staticExtensionExists(ServletContext ctx, String path) {
        return fileExists(ctx, "/static/" + path);
    }

    /**
     * Check if the {@link File} in '/static/doc/' exists.
     * 
     * @param ctx
     *            {@link ServletContext} to get real path
     * @param path
     *            File path and name in '/static/doc/'
     * @return <code>true</code>, if file exists
     */
    public static boolean documentExtensionExists(ServletContext ctx, String path) {
        return fileExists(ctx, "/static/doc/" + path);
    }

    /**
     * Check if the {@link File} exists.
     * 
     * @param ctx
     *            {@link ServletContext} to get real path
     * @param path
     *            File path and name
     * @return <code>true</code>, if file exists
     */
    public static boolean fileExists(ServletContext ctx, String path) {
        return new File(ctx.getRealPath(path)).exists();
    }

    public static String mapToJson(@SuppressWarnings("rawtypes") Map map) {
        ObjectNode node = JSONUtils.nodeFactory().objectNode();
        for (Object key : map.keySet()) {
            node.put(key.toString(), String.valueOf(map.get(key)));
        }
        return JSONUtils.print(node);
    }
}
