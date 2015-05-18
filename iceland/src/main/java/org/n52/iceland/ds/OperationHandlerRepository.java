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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.n52.iceland.exception.ConfigurationException;
import org.n52.iceland.util.AbstractConfiguringServiceLoaderRepository;

/**
 * In 52N SOS version 4.x called OperationDAORepository
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 1.0.0
 */
public class OperationHandlerRepository extends AbstractConfiguringServiceLoaderRepository<OperationHandler> {

    private static class LazyHolder {
        private static final OperationHandlerRepository INSTANCE = new OperationHandlerRepository();

        private LazyHolder() {
        };
    }
    
    private static String datasourceDaoIdentficator;

    /**
     * @return Returns a singleton instance of the {@link OperationHandlerRepository}.
     */
    public static OperationHandlerRepository getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * @return Returns a singleton instance of the {@link OperationHandlerRepository}.
     */
    public static OperationHandlerRepository createInstance(String datasourceDaoIdentficator) {
        setDatasourceDaoIdentficator(datasourceDaoIdentficator);
        return getInstance();
    }

    private static void setDatasourceDaoIdentficator(String datasourceDaoIdentficator) {
        OperationHandlerRepository.datasourceDaoIdentficator = datasourceDaoIdentficator;
    }

    /** Implemented {@link OperationHandler} */
    private final Map<OperationHandlerKeyType, OperationHandler> operationHandlers =
            new HashMap<OperationHandlerKeyType, OperationHandler>(0);

    /**
     * Load implemented operation handler
     * 
     * @throws ConfigurationException
     *             If no operation handler is implemented
     */
    private OperationHandlerRepository() throws ConfigurationException {
        super(OperationHandler.class, false);
        load(false);
    }

    /**
     * Load the implemented operation handler and add them to a map with operation
     * name as key.
     * 
     * @throws ConfigurationException
     *             If no operation handler is implemented
     */
    @Override
    protected void processConfiguredImplementations(final Set<OperationHandler> daos) throws ConfigurationException {
        operationHandlers.clear();
        for (final OperationHandler dao : daos) {
            if (checkDatasourceDaoIdentifications(dao)) {
                operationHandlers.put(dao.getOperationHandlerKeyType(), dao);
            }
        }
    }

    protected boolean checkDatasourceDaoIdentifications(DatasourceDaoIdentifier datasourceDaoIdentifier) {
        if (datasourceDaoIdentficator.equalsIgnoreCase(datasourceDaoIdentifier
                .getDatasourceDaoIdentifier()) || DatasourceDaoIdentifier.IDEPENDET_IDENTIFIER.equals(datasourceDaoIdentifier
                .getDatasourceDaoIdentifier())) {
            return true;
        }
        return false;
    }

    /**
     * @return the implemented operation Handlers
     */
    public Map<OperationHandlerKeyType, OperationHandler> getOperationDAOs() {
        return Collections.unmodifiableMap(operationHandlers);
    }

    /**
     * @param service
     *            the service name
     * @param operationName
     *            the operation name
     * @return the implemented operation handler
     */
    public OperationHandler getOperationDAO(final String service, final String operationName) {
        return operationHandlers.get(new OperationHandlerKeyType(service, operationName));
    }

    /**
     * @param operationHandlerIdentifier
     *            the operation DAO identifier
     * @return the implemented operation DAO
     */
    public OperationHandler getOperationDAO(final OperationHandlerKeyType operationHandlerIdentifier) {
        return operationHandlers.get(operationHandlerIdentifier);
    }
}
