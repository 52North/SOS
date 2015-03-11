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
package org.n52.sos.web.install;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.exception.JSONException;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 *
 * @since 4.0.0
 */
@Controller
public abstract class AbstractProcessingInstallationController extends AbstractInstallController {

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView get(HttpServletRequest req)
            throws InstallationRedirectError,
                   ConfigurationException, JSONException {
        return new ModelAndView(getStep().getView(), toModel(getSettings(checkPrevious(req))));
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView post(HttpServletRequest req,
                             HttpServletResponse resp)
            throws InstallationSettingsError,
                   InstallationRedirectError {
        HttpSession session = checkPrevious(req);
        InstallationConfiguration c = getSettings(session);
        process(getParameters(req), c);
        setSettings(session, c);
        setComplete(session);
        return redirect(getStep().getNext().getPath());
    }

    protected abstract void process(Map<String, String> parameters,
                                    InstallationConfiguration c)
            throws InstallationSettingsError;

}
