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
package org.n52.sos.web.admin.i18n;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import org.n52.sos.cache.ContentCache;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.JSONUtils;
import org.n52.sos.web.ControllerConstants;


@Controller
@RequestMapping(ControllerConstants.Paths.ADMIN_I18N)
public class AdminI18NController {

    private static final String OBSERVABLE_PROPERTIES = "observableProperties";
    private static final String FEATURES = "features";
    private static final String PROCEDURES = "procedures";
    private static final String OFFERINGS = "offerings";

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView view() {
        Map<String, String> model = new HashMap<String, String>(4);
        ContentCache cache = Configurator.getInstance().getCache();
        model.put(OFFERINGS, asJSONArray(cache.getOfferings()));
        model.put(PROCEDURES, asJSONArray(cache.getProcedures()));
        model.put(FEATURES, asJSONArray(cache.getFeaturesOfInterest()));
        model.put(OBSERVABLE_PROPERTIES, asJSONArray(cache
                .getObservableProperties()));
        return new ModelAndView(ControllerConstants.Views.ADMIN_I18N, model);
    }

    private static String asJSONArray(
            Collection<String> coll) {
        return JSONUtils.print(JSONUtils.toJSON(new TreeSet<String>(coll)));
    }
}
