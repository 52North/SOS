/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.procedure;

import java.util.Locale;

import org.hibernate.Session;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosProcedureDescription;

public abstract class AbstractProcedureConverter<T> {

    /**
     * Create procedure description from file, single XML text or generate
     *
     * @param procedure
     *            Hibernate procedure entity
     * @param requestedDescriptionFormat
     *            Requested procedure descriptionFormat
     * @param requestedServiceVersion
     *            Requested SOS version
     * @param session
     *            Hibernate session
     *
     * @return created SosProcedureDescription
     *
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    public abstract SosProcedureDescription<?> createSosProcedureDescription(T procedure,
            String requestedDescriptionFormat, String requestedServiceVersion, Locale i18n, Session session)
            throws OwsExceptionReport;

    /**
     * Create procedure description from file, single XML text or generate
     *
     * @param procedure
     *            Hibernate procedure entity
     * @param requestedDescriptionFormat
     *            Requested procedure descriptionFormat
     * @param requestedServiceVersion
     *            Requested SOS version
     * @param session
     *            Hibernate session
     *
     * @return created SosProcedureDescription
     *
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    public SosProcedureDescription<?> createSosProcedureDescription(T procedure, String requestedDescriptionFormat,
            String requestedServiceVersion, Session session) throws OwsExceptionReport {
        // child hierarchy procedures haven't been queried yet, pass null
        return createSosProcedureDescription(procedure, requestedDescriptionFormat, requestedServiceVersion, null,
                session);
    }
}
