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
package org.n52.sos.service.operator;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.operator.RequestOperatorRepository;
import org.n52.sos.util.AbstractConfiguringServiceLoaderRepository;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.MultiMaps;
import org.n52.sos.util.SetMultiMap;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class ServiceOperatorRepository extends AbstractConfiguringServiceLoaderRepository<ServiceOperator> {
	private static class LazyHolder {
		private static final ServiceOperatorRepository INSTANCE = new ServiceOperatorRepository();
		
		private LazyHolder() {};
	}


    /**
     * Implemented ServiceOperator
     */
    private final Map<ServiceOperatorKey, ServiceOperator> serviceOperators = Maps.newHashMap();

    /** supported SOS versions */
    private final SetMultiMap<String, String> supportedVersions = MultiMaps.newSetMultiMap();

    /** supported services */
    private final Set<String> supportedServices = Sets.newHashSet();

    /**
     * Load implemented request listener
     * 
     * @throws ConfigurationException
     *             If no request listener is implemented
     */
    private ServiceOperatorRepository() throws ConfigurationException {
        super(ServiceOperator.class, false);
        load(false);
    }

    public static ServiceOperatorRepository getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * Load the implemented request listener and add them to a map with
     * operation name as key
     * 
     * @param implementations
     *            the loaded implementations
     * 
     * @throws ConfigurationException
     *             If no request listener is implemented
     */
    @Override
    protected void processConfiguredImplementations(final Set<ServiceOperator> implementations)
            throws ConfigurationException {
        serviceOperators.clear();
        supportedServices.clear();
        supportedVersions.clear();
        for (final ServiceOperator so : implementations) {
            serviceOperators.put(so.getServiceOperatorKey(), so);
            supportedVersions.add(so.getServiceOperatorKey().getService(), so.getServiceOperatorKey()
                    .getVersion());
            supportedServices.add(so.getServiceOperatorKey().getService());
        }
    }

    /**
     * Update/reload the implemented request listener
     * 
     * @throws ConfigurationException
     *             If no request listener is implemented
     */
    @Override
    public void update() throws ConfigurationException {
        RequestOperatorRepository.getInstance().update();
        super.update();
    }

    /**
     * @return the implemented request listener
     */
    public Map<ServiceOperatorKey, ServiceOperator> getServiceOperators() {
        return Collections.unmodifiableMap(serviceOperators);
    }

    public Set<ServiceOperatorKey> getServiceOperatorKeyTypes() {
        return getServiceOperators().keySet();
    }

    public ServiceOperator getServiceOperator(final ServiceOperatorKey sok) {
        return serviceOperators.get(sok);
    }

    /**
     * @param service
     *            the service
     * @param version
     *            the version
     * @return the implemented request listener
     * 
     * 
     * @throws OwsExceptionReport
     */
    public ServiceOperator getServiceOperator(final String service, final String version) throws OwsExceptionReport {
        return getServiceOperator(new ServiceOperatorKey(service, version));
    }

    /**
     * @return the supportedVersions
     * 
     * @deprecated use getSupporteVersions(String service)
     */
    @Deprecated
    public Set<String> getSupportedVersions() {
        return getAllSupportedVersions();

    }

    public Set<String> getAllSupportedVersions() {
        return CollectionHelper.union(supportedVersions.values());
    }

    /**
     * @param service
     *            the service
     * @return the supportedVersions
     * 
     */
    public Set<String> getSupportedVersions(final String service) {
        if (isServiceSupported(service)) {
            return Collections.unmodifiableSet(supportedVersions.get(service));
        }
        return Sets.newHashSet();
    }

    /**
     * @param version
     *            the version
     * @return the supportedVersions
     * 
     * @deprecated use isVersionSupported(String service, String version)
     */
    @Deprecated
    public boolean isVersionSupported(final String version) {
        return getAllSupportedVersions().contains(version);
    }

    /**
     * @param service
     *            the service
     * @param version
     *            the version
     * @return the supportedVersions
     * 
     */
    public boolean isVersionSupported(final String service, final String version) {
        return isServiceSupported(service) && supportedVersions.get(service).contains(version);
    }

    /**
     * @return the supportedVersions
     */
    public Set<String> getSupportedServices() {
        return Collections.unmodifiableSet(supportedServices);
    }

    public boolean isServiceSupported(final String service) {
        return supportedServices.contains(service);
    }

}
