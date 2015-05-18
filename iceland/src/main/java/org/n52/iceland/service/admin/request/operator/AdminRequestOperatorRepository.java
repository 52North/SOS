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
package org.n52.iceland.service.admin.request.operator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.n52.iceland.exception.ConfigurationException;
import org.n52.iceland.util.AbstractConfiguringServiceLoaderRepository;
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
