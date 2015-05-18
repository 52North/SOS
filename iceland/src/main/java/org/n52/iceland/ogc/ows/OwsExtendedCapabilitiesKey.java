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
package org.n52.iceland.ogc.ows;

import org.n52.iceland.ogc.AbstractComparableServiceVersionDomainKey;
import org.n52.iceland.service.operator.ServiceOperatorKey;

/**
 * Key class to identify {@link OwsExtendedCapabilitiesProvider}.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 *
 */
public class OwsExtendedCapabilitiesKey extends AbstractComparableServiceVersionDomainKey<OwsExtendedCapabilitiesKey>  {

    /**
     * constructor
     * 
     * @param sok
     *            the {@link ServiceOperatorKey} to set
     * @param domain
     *            the domain to set
     */
    public OwsExtendedCapabilitiesKey(ServiceOperatorKey sok, String domain) {
        super(sok, domain);
    }

    /**
     * constructor
     * 
     * @param service
     *            the service to set
     * @param version
     *            the version to set
     * @param domain
     *            the domain to set
     */
    public OwsExtendedCapabilitiesKey(String service, String version, String domain) {
        super(service, version, domain);
    }
}
