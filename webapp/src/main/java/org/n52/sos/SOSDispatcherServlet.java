/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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
package org.n52.sos;

import javax.servlet.ServletContext;

import org.n52.iceland.service.DatabaseSettingsHandler;
import org.n52.sos.context.ContextSwitcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.target.HotSwappableTargetSource;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class SOSDispatcherServlet
        extends DispatcherServlet
        implements ContextSwitcher {
    public static final String UNCONFIGURED_CONFIG_LOCATION_PARAM = "unconfiguredConfigLocations";
    public static final String CONFIGURED_CONFIG_LOCATION_PARAM = "configuredConfigLocations";
    public static final String COMMON_CONFIG_LOCATION_PARAM = "commonConfigLocation";
    public static final String SETTINGS_CONFIG_LOCATION_PARAM = "settingsConfigLocation";
    private static final Logger LOG = LoggerFactory.getLogger(SOSDispatcherServlet.class);
    private static final long serialVersionUID = -5390564503165988702L;
    private static final String CONTEXT_SWITCHER_BEAN_NAME = "contextSwitcherSwapper";
    private boolean configured;
    private boolean loadSettings;

    @Override
    public String getContextConfigLocation() {
        return getConfigLocation(getServletContext());
    }

    private String getConfigLocation(ServletContext servletContext) {
        String def = getDefaultConfigLocations();
        String com = getCommonConfigLocation(servletContext);
        String set = isConfigured(servletContext) || loadSettings ? getSettingsConfigLocation(servletContext) : "";
        String add = isConfigured(servletContext)
                             ? getConfiguredConfigLocation(servletContext)
                             : getUnconfiguredConfigLocation(servletContext);
        return def + " " + com + " " + set + " " + add;
    }

    private String getConfigLocation(ServletContext servletContext, String initParamName) {
        String initParam = servletContext.getInitParameter(initParamName);
        return (initParam == null || initParam.isEmpty()) ? "" : initParam;
    }

    protected String getDefaultConfigLocations() {
        // -> /WEB-INF/dispatcher-servlet.xml
        return XmlWebApplicationContext.DEFAULT_CONFIG_LOCATION_PREFIX +
               getNamespace() +
               XmlWebApplicationContext.DEFAULT_CONFIG_LOCATION_SUFFIX;
    }

    private String getCommonConfigLocation(ServletContext servletContext) {
        return getConfigLocation(servletContext, COMMON_CONFIG_LOCATION_PARAM);
    }

    private String getSettingsConfigLocation(ServletContext servletContext) {
        return getConfigLocation(servletContext, SETTINGS_CONFIG_LOCATION_PARAM);
    }

    private String getUnconfiguredConfigLocation(ServletContext servletContext) {
        return getConfigLocation(servletContext, UNCONFIGURED_CONFIG_LOCATION_PARAM);
    }

    private String getConfiguredConfigLocation(ServletContext servletContext) {
        return getConfigLocation(servletContext, CONFIGURED_CONFIG_LOCATION_PARAM);
    }

    @Override
    public boolean isConfigured() {
        return this.configured;
    }

    protected boolean isConfigured(ServletContext servletContext) {
        if (!configured) {
            DatabaseSettingsHandler handler = new DatabaseSettingsHandler();
            handler.setServletContext(servletContext);
            this.configured = handler.exists();
        }
        return this.configured;
    }

    @Override
    protected void postProcessWebApplicationContext(ConfigurableWebApplicationContext wac) {
        super.postProcessWebApplicationContext(wac);
        if (wac != null) {
            ServletContext servletContext = wac.getServletContext();
            if (servletContext != null) {
                wac.setConfigLocation(getConfigLocation(servletContext));
            }
        }
    }

    @Override
    protected void onRefresh(ApplicationContext context) {
        super.onRefresh(context);
        try {
            Object targetBean = context.getBean(CONTEXT_SWITCHER_BEAN_NAME);
            if (targetBean instanceof HotSwappableTargetSource) {
                ((HotSwappableTargetSource) targetBean).swap(this);
            }
        } catch (NoSuchBeanDefinitionException e) {
            //ignore
        }
    }

    @Override
    public void reloadContext() {
        WebApplicationContext applicationContext = getWebApplicationContext();
        if (!(applicationContext instanceof ConfigurableWebApplicationContext)) {
            throw new IllegalStateException("WebApplicationContext does not support refresh: " + applicationContext);
        }
        LOG.info("Reloading context");

        this.configureAndRefreshWebApplicationContext((ConfigurableWebApplicationContext) applicationContext);
    }

    @Override
    public void loadSettings() {
       this.loadSettings = true;
    }
}
