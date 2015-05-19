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
package org.n52.sos.service.profile;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.concurrent.locks.ReentrantLock;

import org.n52.iceland.exception.ConfigurationException;
import org.n52.iceland.util.Comparables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 4.0.0
 * 
 */
public abstract class ProfileHandler {


    private static final Logger LOG = LoggerFactory.getLogger(ProfileHandler.class);

    private static final ReentrantLock creationLock = new ReentrantLock();

    private static ProfileHandler instance;
    
    /**
     * Gets the singleton instance of the ProfileHandler.
     * <p/>
     *
     * @return the profile handler
     *         <p/>
     * @throws ConfigurationException
     *             if no implementation can be found
     */
    public static ProfileHandler getInstance() throws ConfigurationException {
        if (instance == null) {
            creationLock.lock();
            try {
                if (instance == null) {
                    instance = createInstance();
                }
            } finally {
                creationLock.unlock();
            }
        }
        return instance;
    }

    /**
     * Creates a new {@code ProfileHandler} with the {@link ServiceLoader}
     * interface.
     * <p/>
     *
     * @return the implementation
     *         <p/>
     * @throws ConfigurationException
     *             if no implementation can be found
     */
    private static ProfileHandler createInstance() throws ConfigurationException {
        List<ProfileHandler> profileHandlers = new LinkedList<ProfileHandler>();
        Iterator<ProfileHandler> it = ServiceLoader.load(ProfileHandler.class).iterator();
        while(it.hasNext()) {
            try {
                profileHandlers.add(it.next());
            } catch (ServiceConfigurationError e) {
                LOG.error("Could not instantiate ProfileHandler", e);
            }
        }
        if (profileHandlers.isEmpty()) {
            return new DefaultProfileHandler();
        } else {
            try {
                return Comparables.inheritance().min(profileHandlers);
            } catch (NoSuchElementException e) {
                throw new ConfigurationException("No ProfileHandler implementation loaded", e);
            }
        }
    }
    
    public abstract Profile getActiveProfile();

    public abstract Map<String, Profile> getAvailableProfiles();

    public abstract boolean isSetActiveProfile();
}
