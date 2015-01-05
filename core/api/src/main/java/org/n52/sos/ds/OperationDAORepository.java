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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.util.AbstractConfiguringServiceLoaderRepository;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class OperationDAORepository extends AbstractConfiguringServiceLoaderRepository<OperationDAO> {

    private static class LazyHolder {
        private static final OperationDAORepository INSTANCE = new OperationDAORepository();

        private LazyHolder() {
        };
    }
    
    private static String datasourceDaoIdentficator;

    /**
     * @return Returns a singleton instance of the CodingRepository.
     */
    public static OperationDAORepository getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * @return Returns a singleton instance of the CodingRepository.
     */
    public static OperationDAORepository createInstance(String datasourceDaoIdentficator) {
        setDatasourceDaoIdentficator(datasourceDaoIdentficator);
        return getInstance();
    }

    private static void setDatasourceDaoIdentficator(String datasourceDaoIdentficator) {
        OperationDAORepository.datasourceDaoIdentficator = datasourceDaoIdentficator;
    }

    /** Implemented ISosOperationDAO */
    private final Map<OperationDAOKeyType, OperationDAO> operationDaos =
            new HashMap<OperationDAOKeyType, OperationDAO>(0);

    /**
     * Load implemented operation dao
     * 
     * @throws ConfigurationException
     *             If no operation dao is implemented
     */
    private OperationDAORepository() throws ConfigurationException {
        super(OperationDAO.class, false);
        load(false);
    }

    /**
     * Load the implemented operation dao and add them to a map with operation
     * name as key.
     * 
     * @throws ConfigurationException
     *             If no operation dao is implemented
     */
    @Override
    protected void processConfiguredImplementations(final Set<OperationDAO> daos) throws ConfigurationException {
        operationDaos.clear();
        for (final OperationDAO dao : daos) {
            if (checkDatasourceDaoIdentifications(dao)) {
                operationDaos.put(dao.getOperationDAOKeyType(), dao);
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
     * @return the implemented operation DAOs
     */
    public Map<OperationDAOKeyType, OperationDAO> getOperationDAOs() {
        return Collections.unmodifiableMap(operationDaos);
    }

    /**
     * @param service
     *            the service name
     * @param operationName
     *            the operation name
     * @return the implemented operation DAO
     */
    public OperationDAO getOperationDAO(final String service, final String operationName) {
        return operationDaos.get(new OperationDAOKeyType(service, operationName));
    }

    /**
     * @param operationDAOIdentifier
     *            the operation DAO identifier
     * @return the implemented operation DAO
     */
    public OperationDAO getOperationDAO(final OperationDAOKeyType operationDAOIdentifier) {
        return operationDaos.get(operationDAOIdentifier);
    }
}
