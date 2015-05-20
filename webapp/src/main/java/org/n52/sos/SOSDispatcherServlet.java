/*
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.sos;

import javax.servlet.ServletContext;

import org.springframework.aop.target.HotSwappableTargetSource;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import org.n52.iceland.service.DatabaseSettingsHandler;
import org.n52.sos.context.ContextSwitcher;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class SOSDispatcherServlet extends DispatcherServlet implements ContextSwitcher {
    private static final long serialVersionUID = -5390564503165988702L;
    private static final String CONTEXT_SWITCHER_BEAN_NAME = "contextSwitcherSwapper";
    public static final String UNCONFIGURED_CONFIG_LOCATION_PARAM = "unconfiguredConfigLocations";
    public static final String CONFIGURED_CONFIG_LOCATION_PARAM = "configuredConfigLocations";

    private String getConfigLocation(ServletContext servletContext) {
        String def = getDefaultConfigLocations();
        String com = getCommonConfigLocation(servletContext);
        String add = isConfigured(servletContext)
                ? getConfiguredConfigLocation(servletContext)
                : getUnconfiguredConfigLocation(servletContext);
        return def + " " + com + " " + add;
    }

	protected String getDefaultConfigLocations() {
        return XmlWebApplicationContext.DEFAULT_CONFIG_LOCATION_PREFIX +
               getNamespace() +
               XmlWebApplicationContext.DEFAULT_CONFIG_LOCATION_SUFFIX;
	}

    private String getConfigLocation(ServletContext servletContext, String initParamName) {
        String initParam = servletContext.getInitParameter(initParamName);
        return (initParam == null || initParam.isEmpty()) ? "" : initParam;
    }

    private String getCommonConfigLocation(ServletContext servletContext) {
        return getConfigLocation(servletContext, ContextLoader.CONFIG_LOCATION_PARAM);
    }

    private String getUnconfiguredConfigLocation(ServletContext servletContext) {
        return getConfigLocation(servletContext, UNCONFIGURED_CONFIG_LOCATION_PARAM);
    }

    private String getConfiguredConfigLocation(ServletContext servletContext) {
        return getConfigLocation(servletContext, CONFIGURED_CONFIG_LOCATION_PARAM);
    }

    protected boolean isConfigured(ServletContext servletContext) {
        return DatabaseSettingsHandler.getInstance(servletContext).exists();
    }

    @Override
    protected void postProcessWebApplicationContext(ConfigurableWebApplicationContext wac) {
        super.postProcessWebApplicationContext(wac);
        ServletContext servletContext = wac.getServletContext();
        wac.setConfigLocation(getConfigLocation(servletContext));
    }

    @Override
    protected void onRefresh(ApplicationContext context) {
        super.onRefresh(context);
        try {
            Object targetBean = context.getBean(CONTEXT_SWITCHER_BEAN_NAME);
            if (targetBean instanceof HotSwappableTargetSource) {
                ((HotSwappableTargetSource) targetBean).swap(this);
            }
        } catch(NoSuchBeanDefinitionException e) {
            //ignore
        }
    }

    @Override
    public void reloadContext() {
        WebApplicationContext applicationContext = getWebApplicationContext();
        if (!(applicationContext instanceof ConfigurableWebApplicationContext)) {
            throw new IllegalStateException("WebApplicationContext does not support refresh: " + applicationContext);
        }
        this.configureAndRefreshWebApplicationContext((ConfigurableWebApplicationContext) applicationContext);
    }
}
