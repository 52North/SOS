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
package org.n52.sos.service.admin.request.operator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.util.AbstractConfiguringServiceLoaderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class AdminRequestOperatorRepository extends AbstractConfiguringServiceLoaderRepository<AdminRequestOperator> {

    private static final Logger LOG = LoggerFactory.getLogger(AdminRequestOperatorRepository.class);

    private static class LazyHolder {
		private static final AdminRequestOperatorRepository INSTANCE = new AdminRequestOperatorRepository();
		
		private LazyHolder() {};
	}


    public static AdminRequestOperatorRepository getInstance() {
        return LazyHolder.INSTANCE;
    }

    private final Map<String, AdminRequestOperator> operators = new HashMap<String, AdminRequestOperator>(0);

    private AdminRequestOperatorRepository() throws ConfigurationException {
        super(AdminRequestOperator.class, false);
        load(false);
    }

    public AdminRequestOperator getAdminRequestOperator(final String key) {
        return operators.get(key);
    }

    public Map<String, AdminRequestOperator> getAdminRequestOperators() {
        return Collections.unmodifiableMap(operators);
    }

    @Override
    protected void processConfiguredImplementations(final Set<AdminRequestOperator> requestOperators) {
        operators.clear();
        for (final AdminRequestOperator operator : requestOperators) {
            operators.put(operator.getKey(), operator);
        }
        if (operators.isEmpty()) {
            final StringBuilder exceptionText = new StringBuilder();
            exceptionText.append("No IAdminRequestOperator implementation could be loaded!");
            exceptionText.append(" If the SOS is not used as webapp, this has no effect!");
            exceptionText.append(" Else add a IAdminRequestOperator implementation!");
            LOG.warn(exceptionText.toString());
        }
    }
}
