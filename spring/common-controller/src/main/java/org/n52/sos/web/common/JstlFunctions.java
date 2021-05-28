/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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
package org.n52.sos.web.common;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;

import org.n52.iceland.service.DatabaseSettingsHandler;
import org.n52.janmayen.Json;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.springframework.core.env.AbstractEnvironment;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @since 4.0.0
 *
 */
public final class JstlFunctions {
    public static final boolean HAS_INSTALLER = hasClass("org.n52.sos.web.install.InstallIndexController");
    public static final boolean HAS_CLIENT = hasClass("org.n52.sos.web.client.ClientController");
    public static final boolean HAS_ADMIN = hasClass("org.n52.sos.web.admin.AdminIndexController");

    private JstlFunctions() {
    }

    public static boolean configurated(ServletContext ctx) {
        DatabaseSettingsHandler handler = new DatabaseSettingsHandler();
        handler.setServletContext(ctx);
        return handler.exists();
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

    public static boolean supportsEReporting(String c, ServletContext ctx) {
        return hasClass(c) ? checkProfile(ctx, HibernateDatasourceConstants.DatabaseConcept.EREPORTING.name()
                .toLowerCase()) : false;
    }

    private static boolean checkProfile(ServletContext ctx, String profile) {
        String initParameter = ctx.getInitParameter(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME);
        return initParameter != null && !initParameter.isEmpty() && initParameter.contains(profile);
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

    public static String mapToJson(Map<?, ?> map) {
        ObjectNode node = Json.nodeFactory().objectNode();
        for (Entry<?, ?> entry : map.entrySet()) {
            node.put(entry.getKey().toString(), String.valueOf(entry.getValue()));
        }
        return Json.print(node);
    }

    public static boolean supportsI18N() {
        return false;
        // return I18NDAORepository.getInstance().isSupported();
    }
}
