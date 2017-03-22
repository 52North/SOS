/**
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ogc.swes;

import org.n52.sos.ogc.AbstractComparableServiceVersionDomainKey;
import org.n52.sos.service.operator.ServiceOperatorKey;

/**
 * Key class to identify {@link OfferingExtensionProvider}.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 * 
 */
public class OfferingExtensionKey extends AbstractComparableServiceVersionDomainKey<OfferingExtensionKey> {

    /**
     * constructor
     * 
     * @param sok
     *            the {@link ServiceOperatorKey} to set
     * @param domain
     *            the domain to set
     */
    public OfferingExtensionKey(ServiceOperatorKey sok, String domain) {
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
    public OfferingExtensionKey(String service, String version, String domain) {
        super(service, version, domain);
    }

}
