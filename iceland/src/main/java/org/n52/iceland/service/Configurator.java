/**
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
package org.n52.iceland.service;

import static org.n52.iceland.util.ConfiguringSingletonServiceLoader.loadAndConfigure;

import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.n52.iceland.binding.BindingRepository;
import org.n52.iceland.cache.ContentCache;
import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.coding.CodingRepository;
import org.n52.iceland.config.SettingsManager;
import org.n52.iceland.convert.ConverterRepository;
import org.n52.iceland.convert.RequestResponseModifierRepository;
import org.n52.iceland.ds.CacheFeederHandlerRepository;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.iceland.ds.ConnectionProviderIdentificator;
import org.n52.iceland.ds.DataConnectionProvider;
import org.n52.iceland.ds.Datasource;
import org.n52.iceland.ds.DatasourceDaoIdentifier;
import org.n52.iceland.ds.FeatureQueryHandler;
import org.n52.iceland.ds.HibernateDatasourceConstants;
import org.n52.iceland.ds.IFeatureConnectionProvider;
import org.n52.iceland.ds.OperationHandlerRepository;
import org.n52.iceland.event.ServiceEventBus;
import org.n52.iceland.event.events.ConfiguratorInitializedEvent;
import org.n52.iceland.exception.ConfigurationException;
import org.n52.iceland.exception.ows.NoApplicableCodeException;
import org.n52.iceland.ogc.ows.OwsExceptionReport;
import org.n52.iceland.ogc.ows.OwsExtendedCapabilitiesRepository;
import org.n52.iceland.ogc.ows.OwsServiceIdentification;
import org.n52.iceland.ogc.ows.OwsServiceProvider;
import org.n52.iceland.ogc.ows.ServiceIdentificationFactory;
import org.n52.iceland.ogc.ows.ServiceProviderFactory;
import org.n52.iceland.ogc.sos.CapabilitiesExtensionRepository;
import org.n52.iceland.ogc.swes.OfferingExtensionRepository;
import org.n52.iceland.request.operator.RequestOperatorRepository;
import org.n52.iceland.service.admin.operator.AdminServiceOperator;
import org.n52.iceland.service.admin.request.operator.AdminRequestOperatorRepository;
import org.n52.iceland.service.operator.ServiceOperatorRepository;
import org.n52.iceland.service.profile.DefaultProfileHandler;
import org.n52.iceland.service.profile.ProfileHandler;
import org.n52.iceland.util.Cleanupable;
import org.n52.iceland.util.ConfiguringSingletonServiceLoader;
import org.n52.iceland.util.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private Producer<OwsServiceIdentification> serviceIdentificationFactory;

    private Producer<OwsServiceProvider> serviceProviderFactory;

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
        CacheFeederHandlerRepository.createInstance(getDatasourceDaoIdentificator());

        serviceIdentificationFactory = new ServiceIdentificationFactory();
        serviceProviderFactory = new ServiceProviderFactory();
        OperationHandlerRepository.createInstance(getDatasourceDaoIdentificator());
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
        profileHandler = loadAndConfigure(ProfileHandler.class, false, new DefaultProfileHandler());

        ServiceEventBus.fire(new ConfiguratorInitializedEvent());
        LOGGER.info("\n******\n Configurator initialization finished\n******\n");
    }

    /**
     * @return Returns the service identification
     *         <p/>
     * @throws OwsExceptionReport
     */
    public OwsServiceIdentification getServiceIdentification() throws OwsExceptionReport {
        return get(serviceIdentificationFactory);
    }

    /**
     * @return Returns the service identification for the specific language
     *         <p/>
     * @throws OwsExceptionReport
     */
    public OwsServiceIdentification getServiceIdentification(Locale lanugage) throws OwsExceptionReport {
        return get(serviceIdentificationFactory, lanugage);
    }

    public ServiceIdentificationFactory getServiceIdentificationFactory() throws OwsExceptionReport {
        return (ServiceIdentificationFactory) serviceIdentificationFactory;
    }

    /**
     * @return Returns the service provider
     *         <p/>
     * @throws OwsExceptionReport
     */
    public OwsServiceProvider getServiceProvider() throws OwsExceptionReport {
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

    public ProfileHandler getProfileHandler() {
        return profileHandler;
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
