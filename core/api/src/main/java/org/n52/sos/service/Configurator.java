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
package org.n52.sos.service;

import static org.n52.sos.util.ConfiguringSingletonServiceLoader.loadAndConfigure;

import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.sos.binding.BindingRepository;
import org.n52.sos.cache.ContentCache;
import org.n52.sos.cache.ContentCacheController;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.config.SettingsManager;
import org.n52.sos.convert.ConverterRepository;
import org.n52.sos.convert.RequestResponseModifierRepository;
import org.n52.sos.ds.CacheFeederDAO;
import org.n52.sos.ds.CacheFeederDAORepository;
import org.n52.sos.ds.ConnectionProvider;
import org.n52.sos.ds.DataConnectionProvider;
import org.n52.sos.ds.ConnectionProviderIdentificator;
import org.n52.sos.ds.Datasource;
import org.n52.sos.ds.DatasourceDaoIdentifier;
import org.n52.sos.ds.FeatureQueryHandler;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.n52.sos.ds.IFeatureConnectionProvider;
import org.n52.sos.ds.OperationDAORepository;
import org.n52.sos.event.SosEventBus;
import org.n52.sos.event.events.ConfiguratorInitializedEvent;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsExtendedCapabilitiesRepository;
import org.n52.sos.ogc.ows.SosServiceIdentification;
import org.n52.sos.ogc.ows.SosServiceIdentificationFactory;
import org.n52.sos.ogc.ows.SosServiceProvider;
import org.n52.sos.ogc.ows.SosServiceProviderFactory;
import org.n52.sos.ogc.sos.CapabilitiesExtensionRepository;
import org.n52.sos.ogc.swes.OfferingExtensionRepository;
import org.n52.sos.request.operator.RequestOperatorRepository;
import org.n52.sos.service.admin.operator.AdminServiceOperator;
import org.n52.sos.service.admin.request.operator.AdminRequestOperatorRepository;
import org.n52.sos.service.operator.ServiceOperatorRepository;
import org.n52.sos.service.profile.DefaultProfileHandler;
import org.n52.sos.service.profile.ProfileHandler;
import org.n52.sos.tasking.Tasking;
import org.n52.sos.util.Cleanupable;
import org.n52.sos.util.ConfiguringSingletonServiceLoader;
import org.n52.sos.util.Producer;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

/**
 * Singleton class reads the configFile and builds the RequestOperator and DAO;
 * configures the logger.
 *
 * @since 4.0.0
 */
public class Configurator implements Cleanupable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Configurator.class);

    /**
     * instance attribute, due to the singleton pattern.
     */
    private static Configurator instance = null;

    private static final Lock INIT_LOCK = new ReentrantLock();

    /**
     * @return Returns the instance of the Configurator. <tt>null</tt> will be
     *         returned if the parameterized
     *         {@link #createInstance(Properties, String)} method was not
     *         invoked before. Usually this will be done in the SOS.
     *         <p/>
     * @see Configurator#createInstance(Properties, String)
     */
    public static Configurator getInstance() {
        INIT_LOCK.lock();
        try {
            return instance;
        } finally {
            INIT_LOCK.unlock();
        }
    }

    /**
     * @param connectionProviderConfig
     * @param basepath
     * @return Returns an instance of the SosConfigurator. This method is used
     *         to implement the singelton pattern
     *
     * @throws ConfigurationException
     *             if the initialization failed
     */
    public static Configurator createInstance(final Properties connectionProviderConfig, final String basepath)
            throws ConfigurationException {
        if (instance == null) {
            boolean initialize = false;
            INIT_LOCK.lock();
            try {
                if (instance == null) {
                    try {
                        instance = new Configurator(connectionProviderConfig, basepath);
                        initialize = true;
                    } catch (final RuntimeException t) {
                        cleanUpAndThrow(t);
                    } catch (final ConfigurationException t) {
                        cleanUpAndThrow(t);
                    }
                }
            } finally {
                INIT_LOCK.unlock();
            }
            if (initialize) {
                try {
                    instance.initialize();
                } catch (final RuntimeException t) {
                    cleanUpAndThrow(t);
                } catch (final ConfigurationException t) {
                    cleanUpAndThrow(t);
                }
            }
        }
        return instance;
    }

    private static void cleanUpAndThrow(final ConfigurationException t) throws ConfigurationException {
        if (instance != null) {
            instance.cleanup();
            instance = null;
        }
        throw t;
    }

    private static void cleanUpAndThrow(final RuntimeException t) {
        if (instance != null) {
            instance.cleanup();
            instance = null;
        }
        throw t;
    }

    private static void cleanup(final Cleanupable c) {
        if (c != null) {
            c.cleanup();
        }
    }

    protected static <T> T get(final Producer<T> factory) throws OwsExceptionReport {
        try {
            return factory.get();
        } catch (final Exception e) {
            if (e.getCause() != null && e.getCause() instanceof OwsExceptionReport) {
                throw (OwsExceptionReport) e.getCause();
            } else {
                throw new NoApplicableCodeException().withMessage("Could not request object from %s", factory).causedBy(e);
            }
        }
    }

    protected static <T> T get(final Producer<T> factory, Locale language) throws OwsExceptionReport {
        try {
            return factory.get(language);
        } catch (final Exception e) {
            if (e.getCause() != null && e.getCause() instanceof OwsExceptionReport) {
                throw (OwsExceptionReport) e.getCause();
            } else {
                throw new NoApplicableCodeException().withMessage("Could not request object from %s", factory);
            }
        }
    }

    /**
     * base path for configuration files.
     */
    private final String basepath;

    private final Properties dataConnectionProviderProperties;

    private Properties featureConnectionProviderProperties;

    private FeatureQueryHandler featureQueryHandler;

    private ConnectionProvider dataConnectionProvider;

    private ConnectionProvider featureConnectionProvider;

    private ContentCacheController contentCacheController;

    private ProfileHandler profileHandler;

    private AdminServiceOperator adminServiceOperator;

    private Producer<SosServiceIdentification> serviceIdentificationFactory;

    private Producer<SosServiceProvider> serviceProviderFactory;

    private Tasking tasking;

    private Set<String> providedJdbcDrivers = Sets.newHashSet();

    private String connectionProviderIdentificator;

    private String datasourceDaoIdentificator;

    /**
     * private constructor due to the singelton pattern.
     *
     * @param connectionProviderConfig
     *            Connection provider configuration properties
     * @param basepath
     *            base path for this service
     * @throws ConfigurationException
     *             If an error occurs during initialisation
     */
    private Configurator(final Properties connectionProviderConfig, final String basepath)
            throws ConfigurationException {
        if (basepath == null) {
            logAndThrowConfigurationException("No basepath available!");
        }
        if (connectionProviderConfig == null) {
            logAndThrowConfigurationException("No connection provider configuration available!");
        }
        this.basepath = basepath;
        dataConnectionProviderProperties = connectionProviderConfig;
        getIdentificators(dataConnectionProviderProperties);
        if (Strings.isNullOrEmpty(connectionProviderIdentificator)) {
            logAndThrowConfigurationException("No connection provider identificator available!");
        }
        if (Strings.isNullOrEmpty(datasourceDaoIdentificator)) {
            logAndThrowConfigurationException("No datasource DAO identificator available!");
        }
        LOGGER.info("Configurator initialized: [basepath={}]", this.basepath, dataConnectionProviderProperties);
    }

    /**
     * Get the {@link ConnectionProviderIdentificator} and
     * {@link DatasourceDaoIdentifier} values from {@link Datasource}
     * implementation
     * 
     * @param dataConnectionProviderProperties
     *            Datasource properties
     */
    private void getIdentificators(Properties dataConnectionProviderProperties2) {
        String className = dataConnectionProviderProperties.getProperty(Datasource.class.getCanonicalName());
        if (className == null) {
            LOGGER.error("Can not find datasource class in datasource.properties!");
            throw new ConfigurationException("Missing Datasource Property!");
        }
        try {
            Datasource datasource = (Datasource) Class.forName(className).newInstance();
            connectionProviderIdentificator = datasource.getConnectionProviderIdentifier();
            datasourceDaoIdentificator = datasource.getDatasourceDaoIdentifier();
        } catch (ClassNotFoundException ex) {
            LOGGER.error("Can not instantiate Datasource!", ex);
            throw new ConfigurationException(ex);
        } catch (InstantiationException ex) {
            LOGGER.error("Can not instantiate Datasource!", ex);
            throw new ConfigurationException(ex);
        } catch (IllegalAccessException ex) {
            LOGGER.error("Can not instantiate Datasource!", ex);
            throw new ConfigurationException(ex);
        }

    }

    private void logAndThrowConfigurationException(String message) {
        LOGGER.info(message);
        throw new ConfigurationException(message);
    }

    /**
     * Initialize this class. Since this initialization is not done in the
     * constructor, dependent classes can use the SosConfigurator already when
     * called from here.
     */
    private void initialize() throws ConfigurationException {
        LOGGER.info("\n******\n Configurator initialization started\n******\n");

        SettingsManager.getInstance();
        ServiceConfiguration.getInstance();

        initializeConnectionProviders();
        CacheFeederDAORepository.createInstance(getDatasourceDaoIdentificator());

        serviceIdentificationFactory = new SosServiceIdentificationFactory();
        serviceProviderFactory = new SosServiceProviderFactory();
        OperationDAORepository.createInstance(getDatasourceDaoIdentificator());
        ServiceOperatorRepository.getInstance();
        CodingRepository.getInstance();
        featureQueryHandler = loadAndConfigure(FeatureQueryHandler.class, false, getDatasourceDaoIdentificator());
        ConverterRepository.getInstance();
        RequestResponseModifierRepository.getInstance();
        RequestOperatorRepository.getInstance();
        BindingRepository.getInstance();
        CapabilitiesExtensionRepository.getInstance();
        OwsExtendedCapabilitiesRepository.getInstance();
        OfferingExtensionRepository.getInstance();
        adminServiceOperator = loadAndConfigure(AdminServiceOperator.class, false);
        AdminRequestOperatorRepository.getInstance();
        contentCacheController = loadAndConfigure(ContentCacheController.class, false);
        tasking = new Tasking();
        profileHandler = loadAndConfigure(ProfileHandler.class, false, new DefaultProfileHandler());

        SosEventBus.fire(new ConfiguratorInitializedEvent());
        LOGGER.info("\n******\n Configurator initialization finished\n******\n");
    }

    /**
     * @return Returns the service identification
     *         <p/>
     * @throws OwsExceptionReport
     */
    public SosServiceIdentification getServiceIdentification() throws OwsExceptionReport {
        return get(serviceIdentificationFactory);
    }

    /**
     * @return Returns the service identification for the specific language
     *         <p/>
     * @throws OwsExceptionReport
     */
    public SosServiceIdentification getServiceIdentification(Locale lanugage) throws OwsExceptionReport {
        return get(serviceIdentificationFactory, lanugage);
    }

    public SosServiceIdentificationFactory getServiceIdentificationFactory() throws OwsExceptionReport {
        return (SosServiceIdentificationFactory) serviceIdentificationFactory;
    }

    /**
     * @return Returns the service provider
     *         <p/>
     * @throws OwsExceptionReport
     */
    public SosServiceProvider getServiceProvider() throws OwsExceptionReport {
        return get(serviceProviderFactory);
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
    public ContentCache getCache() {
        return getCacheController().getCache();
    }

    /**
     * @return the current contentCacheController
     */
    public ContentCacheController getCacheController() {
        return contentCacheController;
    }

    /**
     * @return the implemented cache feeder DAO
     * @deprecated use {@link CacheFeederDAORepository.getCacheFeederDAO()} instead.
     */
    @Deprecated
    public CacheFeederDAO getCacheFeederDAO() {
        return CacheFeederDAORepository.getInstance().getCacheFeederDAO();
    }

    /**
     * @return the implemented data connection provider
     */
    public ConnectionProvider getDataConnectionProvider() {
        return dataConnectionProvider;
    }

    /**
     * @return the implemented feature connection provider
     */
    public ConnectionProvider getFeatureConnectionProvider() {
        return featureConnectionProvider;
    }

    /**
     * @return the implemented feature query handler
     */
    public FeatureQueryHandler getFeatureQueryHandler() {
        return featureQueryHandler;
    }

    /**
     * @return the implemented SOS administration request operator
     */
    public AdminServiceOperator getAdminServiceOperator() {
        return adminServiceOperator;
    }

    public void addProvidedJdbcDriver(String providedJdbcDriver) {
        this.providedJdbcDrivers.add(providedJdbcDriver);
    }

    public Set<String> getProvidedJdbcDriver() {
        return this.providedJdbcDrivers;
    }

    /**
     * @deprecated Use {@link OperationDAORepository#getInstance()}
     */
    @Deprecated
    public OperationDAORepository getOperationDaoRepository() {
        return OperationDAORepository.getInstance();
    }

    /**
     * @deprecated Use {@link BindingRepository#getInstance()}
     */
    @Deprecated
    public BindingRepository getBindingRepository() {
        return BindingRepository.getInstance();
    }

    /**
     * @deprecated Use {@link ConverterRepository#getInstance()}
     */
    @Deprecated
    public ConverterRepository getConverterRepository() {
        return ConverterRepository.getInstance();
    }

    /**
     * @deprecated Use {@link AdminRequestOperatorRepository#getInstance()}
     */
    @Deprecated
    public AdminRequestOperatorRepository getAdminRequestOperatorRepository() {
        return AdminRequestOperatorRepository.getInstance();
    }

    public ProfileHandler getProfileHandler() {
        return profileHandler;
    }

    /**
     * Returns the default token seperator for results.
     * <p/>
     *
     * @return the tokenSeperator.
     * @deprecated Use ServiceConfiguration.getInstance().getTokenSeparator()
     */
    @Deprecated
    public String getTokenSeparator() {
        return ServiceConfiguration.getInstance().getTokenSeparator();
    }

    /**
     * @deprecated Use ServiceConfiguration.getInstance().getTupleSeparator()
     */
    @Deprecated
    public String getTupleSeparator() {
        return ServiceConfiguration.getInstance().getTupleSeparator();
    }

    /**
     * @deprecated Use
     *             ServiceConfiguration.getInstance().getDefaultOfferingPrefix()
     */
    @Deprecated
    public String getDefaultOfferingPrefix() {
        return ServiceConfiguration.getInstance().getDefaultOfferingPrefix();
    }

    /**
     * @deprecated Use
     *             ServiceConfiguration.getInstance().getDefaultProcedurePrefix
     *             ()
     */
    @Deprecated
    public String getDefaultProcedurePrefix() {
        return ServiceConfiguration.getInstance().getDefaultProcedurePrefix();
    }

    /**
     * @deprecated Use
     *             ServiceConfiguration.getInstance().getDefaultFeaturePrefix()
     */
    @Deprecated
    public String getDefaultFeaturePrefix() {
        return ServiceConfiguration.getInstance().getDefaultFeaturePrefix();
    }

    /**
     * @deprecated Use
     *             ServiceConfiguration.getInstance().getDefaultObservablePropertyPrefix
     *             ()
     */
    @Deprecated
    public String getDefaultObservablePropertyPrefix() {
        return ServiceConfiguration.getInstance().getDefaultObservablePropertyPrefix();
    }

    /**
     * @deprecated Use ServiceConfiguration.getInstance().isUseDefaultPrefixes()
     */
    @Deprecated
    public boolean isUseDefaultPrefixes() {
        return ServiceConfiguration.getInstance().isUseDefaultPrefixes();
    }

    /**
     * @deprecated Use
     *             ServiceConfiguration.getInstance().isEncodeFullChildrenInDescribeSensor
     *             ()
     */
    @Deprecated
    public boolean isEncodeFullChildrenInDescribeSensor() {
        return ServiceConfiguration.getInstance().isEncodeFullChildrenInDescribeSensor();
    }

    /**
     * @return the supportsQuality
     * @deprecated Use ServiceConfiguration.getInstance().isSupportsQuality()
     */
    @Deprecated
    public boolean isSupportsQuality() {
        return ServiceConfiguration.getInstance().isSupportsQuality();
    }

    /**
     * @return Returns the sensor description directory
     * @deprecated Use ServiceConfiguration.getInstance().getSensorDir()
     */
    @Deprecated
    public String getSensorDir() {
        return ServiceConfiguration.getInstance().getSensorDir();
    }

    /**
     * Get service URL.
     *
     * @return the service URL
     * @deprecated Use ServiceConfiguration.getInstance().getServiceURL()
     */
    @Deprecated
    public String getServiceURL() {
        return ServiceConfiguration.getInstance().getServiceURL();
    }

    /**
     * @return prefix URN for the spatial reference system
     * @deprecated Use ServiceConfiguration.getInstance().getSrsNamePrefix()
     */
    @Deprecated
    public String getSrsNamePrefix() {
        return ServiceConfiguration.getInstance().getSrsNamePrefix();
    }

    /**
     * @return prefix URN for the spatial reference system
     * @deprecated Use
     *             ServiceConfiguration.getInstance().getSrsNamePrefixSosV2()
     */
    @Deprecated
    public String getSrsNamePrefixSosV2() {
        return ServiceConfiguration.getInstance().getSrsNamePrefixSosV2();
    }

    /**
     * @deprecated Use ServiceConfiguration.getInstance() instead
     */
    @Deprecated
    public ServiceConfiguration getServiceConfiguration() {
        return ServiceConfiguration.getInstance();
    }

    protected void initializeConnectionProviders() throws ConfigurationException {
        checkForProvidedJdbc();
        dataConnectionProvider =
                ConfiguringSingletonServiceLoader.<ConnectionProvider> loadAndConfigure(DataConnectionProvider.class,
                        true, getConnectionProviderIdentificator());
        featureConnectionProvider =
                ConfiguringSingletonServiceLoader.<ConnectionProvider> loadAndConfigure(
                        IFeatureConnectionProvider.class, false, getConnectionProviderIdentificator());
        dataConnectionProvider.initialize(dataConnectionProviderProperties);
        if (featureConnectionProvider != null) {
            featureConnectionProvider
                    .initialize(featureConnectionProviderProperties != null ? featureConnectionProviderProperties
                            : dataConnectionProviderProperties);
        } else {
            featureConnectionProvider = dataConnectionProvider;
        }
    }

    /**
     * Check method if JDBC driver is provided.
     */
    private void checkForProvidedJdbc() {
        if (!dataConnectionProviderProperties.containsKey(HibernateDatasourceConstants.PROVIDED_JDBC)
                || (dataConnectionProviderProperties.containsKey(HibernateDatasourceConstants.PROVIDED_JDBC) && dataConnectionProviderProperties
                        .getProperty(HibernateDatasourceConstants.PROVIDED_JDBC).equals("true"))) {
            addProvidedJdbcDriver(dataConnectionProviderProperties
                    .getProperty(HibernateDatasourceConstants.HIBERNATE_DRIVER_CLASS));
        }
    }

    /**
     * Eventually cleanup everything created by the constructor
     */
    @Override
    public synchronized void cleanup() {
        cleanup(dataConnectionProvider);
        cleanup(featureConnectionProvider);
        cleanup(contentCacheController);
        cleanup(tasking);
        instance = null;
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
}
