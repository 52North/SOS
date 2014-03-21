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
package org.n52.sos.web.admin.i18n;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.n52.sos.exception.NoSuchOfferingException;
import org.n52.sos.exception.ows.concrete.NoImplementationFoundException;
import org.n52.sos.i18n.I18NDAORepository;
import org.n52.sos.i18n.I18NObject;
import org.n52.sos.i18n.I18NObservablePropertyObject;
import org.n52.sos.i18n.I18NProcedureObject;
import org.n52.sos.i18n.request.GetI18NObjectRequest;
import org.n52.sos.i18n.request.InsertI18NObjectRequest;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.web.ControllerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 *
 */
@Controller
@RequestMapping(ControllerConstants.Paths.OBSERVABLE_PROPERTY_I18N_AJAX_ENDPOINT)
public class ObservablePropertyI18NAjaxEndpoint extends AbstractAdminI18NAjaxEndpoint {
    
    @Override
    public String get() throws NoImplementationFoundException, OwsExceptionReport, JSONException {
        return get(new GetI18NObjectRequest(I18NObservablePropertyObject.class));
    }

    @Override
    public void insert(final String i18nObjectJson) throws NoImplementationFoundException, OwsExceptionReport, JSONException, NoSuchOfferingException {
        final JSONObject request = new JSONObject(i18nObjectJson);
        I18NObject object = toObject(new I18NObservablePropertyObject(request.optString(OBJECT_ID)), request);
        check(object.getObjectIdentifier(), getCache().getProcedures());
        I18NDAORepository.getInstance().getDAO().insertI18NObjects(new InsertI18NObjectRequest(object));
    }

}
