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
package org.n52.sos.service;


import java.util.Locale;
import java.util.Properties;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.n52.faroe.ConfigurationError;
import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.iceland.ds.DataConnectionProvider;
import org.n52.iceland.event.events.ConfiguratorInitializedEvent;
import org.n52.iceland.util.LocalizedProducer;
import org.n52.janmayen.Producer;
import org.n52.janmayen.event.EventBus;
import org.n52.janmayen.lifecycle.Constructable;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.cache.SosContentCache;
import org.n52.sos.ds.FeatureQueryHandler;

/**
 * Singleton class reads the configFile and builds the RequestOperator and DAO;
 * configures the logger.
 *
 * @since 4.0.0
 */
@Deprecated
public class Configurator implements Constructable {
    private static Configurator instance = null;
    private ServletContext servletContext;
    private String basepath;
    private FeatureQueryHandler featureQueryHandler;
    private ConnectionProvider dataConnectionProvider;
    private ConnectionProvider featureConnectionProvider;
    private ContentCacheController contentCacheController;
    private String connectionProviderIdentificator;
    private String datasourceDaoIdentificator;
    private EventBus eventBus;

    public Configurator() {
        // ugly hack for singleton access
    }

    @Inject
    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Inject
    public void setFeatureQueryHandler(FeatureQueryHandler featureQueryHandler) {
        this.featureQueryHandler = featureQueryHandler;
    }

    public FeatureQueryHandler getFeatureQueryHandler() {
        return featureQueryHandler;
    }

    /**
     * @return the implemented feature connection provider
     */
    public ConnectionProvider getFeatureConnectionProvider() {
        return featureConnectionProvider;
    }

    @Inject
    public void setContentCacheController(ContentCacheController contentCacheController) {
        this.contentCacheController = contentCacheController;
    }

    @Inject
    public void setDataConnectionProvider(DataConnectionProvider dataConnectionProvider) {
        this.dataConnectionProvider = dataConnectionProvider;
    }

    /**
     * @return the implemented data connection provider
     */
    public ConnectionProvider getDataConnectionProvider() {
        return dataConnectionProvider;
    }

    @Inject
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public void init() throws ConfigurationError {
        Configurator.instance = this;
        this.basepath = this.servletContext.getRealPath("/");
        if (featureConnectionProvider == null) {
            featureConnectionProvider = dataConnectionProvider;
        }
        this.eventBus.submit(new ConfiguratorInitializedEvent());
    }

    /**
     * @return the base path for configuration files
     */
    public String getBasePath() {
        return basepath;
    }

    /**
     * @return the current contentCacheController
     */
    public SosContentCache getCache() {
        return (SosContentCache) this.contentCacheController.getCache();
    }

    /**
     * @return the current contentCacheController
     */
    public ContentCacheController getCacheController() {
        return this.contentCacheController;
    }

    /**
     * @return the connectionProviderIdentificator
     */
    public String getConnectionProviderIdentificator() {
        return connectionProviderIdentificator;
    }

    /**
     * @return the datasourceDaoIdentificator
     */
    public String getDatasourceDaoIdentificator() {
        return datasourceDaoIdentificator;
    }

    /**
     * @return Returns the instance of the Configurator. <tt>null</tt> will be
     *         returned if the parameterized
     *         {@link #createInstance(Properties, String)} method was not
     *         invoked before. Usually this will be done in the SOS.
     *         <p/>
     * @see Configurator#createInstance(Properties, String)
     */
    public static Configurator getInstance() {
        return instance;
    }

    /**
     * @param connectionProviderConfig
     * @param basepath
     * @return Returns an instance of the SosConfigurator. This method is used
     *         to implement the singelton pattern
     *
     * @throws ConfigurationError
     *             if the initialization failed
     */
    public static Configurator createInstance(Properties connectionProviderConfig, String basepath) {
        return getInstance();
    }

    private static <T> T get(Producer<T> factory) throws OwsExceptionReport {
        try {
            return factory.get();
        } catch (Exception e) {
            if (e instanceof OwsExceptionReport) {
                throw (OwsExceptionReport) e;
            } else if (e.getCause() instanceof OwsExceptionReport) {
                throw (OwsExceptionReport) e.getCause();
            } else {
                throw new NoApplicableCodeException()
                        .causedBy(e).withMessage("Could not request object from %s", factory);
            }
        }
    }

    private static <T> T get(LocalizedProducer<T> factory, Locale language)
            throws OwsExceptionReport {
        try {
            return factory.get(language);
        } catch (Exception e) {
            if (e instanceof OwsExceptionReport) {
                throw (OwsExceptionReport) e;
            } else if (e.getCause() instanceof OwsExceptionReport) {
                throw (OwsExceptionReport) e.getCause();
            } else {
                throw new NoApplicableCodeException()
                        .causedBy(e).withMessage("Could not request object from %s", factory);
            }
        }
    }
}
