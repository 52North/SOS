/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.n52.sos.service.DatabaseSettingsHandler;

/**
 * @since 4.0.0
 * 
 */
public class JstlFunctions {
    public static final boolean HAS_INSTALLER = hasClass("org.n52.sos.web.install.InstallIndexController");

    public static final boolean HAS_CLIENT = hasClass("org.n52.sos.web.ClientController");

    public static final boolean HAS_ADMIN = hasClass("org.n52.sos.web.admin.AdminIndexController");

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

    private static boolean hasClass(String c) {
        try {
            Class.forName(c);
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    public static boolean viewExists(ServletContext ctx, String path) {
        return new File(ctx.getRealPath("/WEB-INF/views/" + path)).exists();
    }
    
    public static boolean staticExtensionExists(ServletContext ctx, String path) {
            return new File(ctx.getRealPath("/static/" + path)).exists();
    }

    public static String mapToJson(@SuppressWarnings("rawtypes") Map map) throws JSONException {
        return new JSONObject(map).toString(2);
    }
    
    private JstlFunctions() {
    }
}
