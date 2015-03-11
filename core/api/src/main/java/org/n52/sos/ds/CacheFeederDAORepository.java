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
package org.n52.sos.ds;

import org.n52.sos.config.SettingsManager;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.exception.ows.concrete.NoImplementationFoundException;
import org.n52.sos.util.ServiceLoaderHelper;

/**
 * @author Shane StClair <shane@axiomalaska.com>
 * 
 * @since 4.0.2
 */
public class CacheFeederDAORepository {
	private static class LazyHolder {
		private static final CacheFeederDAORepository INSTANCE = new CacheFeederDAORepository();
		
		private LazyHolder() {};
	}

	private static String datasourceDaoIdentficator;
	
	
    /**
     * @return Returns a singleton instance of the CacheFeederDAORepository.
     */
    public static CacheFeederDAORepository getInstance() {
        return LazyHolder.INSTANCE;
    }
    
    /**
     * @return Returns a singleton instance of the CodingRepository.
     */
    public static CacheFeederDAORepository createInstance(String datasourceDaoIdentficator) {
        setDatasourceDaoIdentficator(datasourceDaoIdentficator);
        return getInstance();
    }
    
    private static void setDatasourceDaoIdentficator(String datasourceDaoIdentficator) {
        CacheFeederDAORepository.datasourceDaoIdentficator = datasourceDaoIdentficator;
    }

    /** Loaded and configured implementation */
    private CacheFeederDAO cacheFeederDAO;

    /**
     * Load implemented CacheFeederDAO
     * 
     * @throws ConfigurationException
     *             If no CacheFeederDAO implementation is found
     * @throws ConfigurationException 
     */
    private CacheFeederDAORepository() throws ConfigurationException {
        try {
            cacheFeederDAO = ServiceLoaderHelper.loadImplementation(CacheFeederDAO.class, datasourceDaoIdentficator);
        } catch (NoImplementationFoundException e) {
            throw new ConfigurationException(e);
        }
        SettingsManager.getInstance().configure(cacheFeederDAO);
    }

    public CacheFeederDAO getCacheFeederDAO() {
        return cacheFeederDAO;
    }
}
