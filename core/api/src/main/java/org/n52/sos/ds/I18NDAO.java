/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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

import java.util.Collection;

import org.n52.sos.i18n.I18NObject;
import org.n52.sos.i18n.request.GetI18NObjectRequest;
import org.n52.sos.i18n.request.InsertI18NObjectRequest;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * Interface for the I18N DAOs
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 * 
 */
public interface I18NDAO {

    /**
     * Get I18N data from the datasource
     * 
     * @param request
     *            The Get request
     * @return Collection with data that match the request
     * @throws OwsExceptionReport
     *             If an error occurs when querying the datasource
     */
    public Collection<I18NObject> getI18NObjects(GetI18NObjectRequest request) throws OwsExceptionReport;

    /**
     * Insert I18N data into the datasource
     * 
     * @param request
     *            Insertion request
     * @throws OwsExceptionReport
     *             If an error occurs when inserting the data into the
     *             datasource
     */
    public void insertI18NObjects(InsertI18NObjectRequest request) throws OwsExceptionReport;

}
