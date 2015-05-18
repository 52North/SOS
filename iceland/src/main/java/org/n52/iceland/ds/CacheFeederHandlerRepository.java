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
package org.n52.iceland.ds;

import org.n52.iceland.config.SettingsManager;
import org.n52.iceland.exception.ConfigurationException;
import org.n52.iceland.exception.ows.concrete.NoImplementationFoundException;
import org.n52.iceland.util.ServiceLoaderHelper;

/**
 * @author Shane StClair <shane@axiomalaska.com>
 * 
 * @since 4.0.2
 */
public class CacheFeederHandlerRepository {
	private static class LazyHolder {
		private static final CacheFeederHandlerRepository INSTANCE = new CacheFeederHandlerRepository();
		
		private LazyHolder() {};
	}

	private static String datasourceDaoIdentficator;
	
	
    /**
     * @return Returns a singleton instance of the {@link CacheFeederHandlerRepository}.
     */
    public static CacheFeederHandlerRepository getInstance() {
        return LazyHolder.INSTANCE;
    }
    
    /**
     * @return Returns a singleton instance of the {@link CacheFeederHandlerRepository}.
     */
    public static CacheFeederHandlerRepository createInstance(String datasourceDaoIdentficator) {
        setDatasourceDaoIdentficator(datasourceDaoIdentficator);
        return getInstance();
    }
    
    private static void setDatasourceDaoIdentficator(String datasourceDaoIdentficator) {
        CacheFeederHandlerRepository.datasourceDaoIdentficator = datasourceDaoIdentficator;
    }

    /** Loaded and configured implementation */
    private CacheFeederHandler cacheFeederHandler;

    /**
     * Load implemented {@link CacheFeederHandler}
     * 
     * @throws ConfigurationException
     *             If no {@link CacheFeederHandler} implementation is found
     * @throws ConfigurationException 
     */
    private CacheFeederHandlerRepository() throws ConfigurationException {
        try {
            cacheFeederHandler = ServiceLoaderHelper.loadImplementation(CacheFeederHandler.class, datasourceDaoIdentficator);
        } catch (NoImplementationFoundException e) {
            throw new ConfigurationException(e);
        }
        SettingsManager.getInstance().configure(cacheFeederHandler);
    }

    public CacheFeederHandler getCacheFeederHandler() {
        return cacheFeederHandler;
    }
}
