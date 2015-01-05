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
package org.n52.sos.web;

import java.net.URI;

import org.n52.sos.config.SettingValue;
import org.n52.sos.ds.ConnectionProviderException;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.service.ServiceSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
@Controller
@RequestMapping(ControllerConstants.Paths.GET_INVOLVED)
public class GetInvolvedController extends AbstractController {
    private static final Logger LOG = LoggerFactory.getLogger(GetInvolvedController.class);

    public static final String SERVICE_URL_MODEL_ATTRIBUTE = "serviceUrl";

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView view() {
        SettingValue<URI> setting = null;
        try {
            setting = getSettingsManager().getSetting(ServiceSettings.SERVICE_URL_DEFINITION);
        } catch (ConfigurationException ex) {
            LOG.error("Could not load service url", ex);
        } catch (ConnectionProviderException ex) {
            LOG.error("Could not load service url", ex);
        }

        return new ModelAndView(ControllerConstants.Views.GET_INVOLVED, SERVICE_URL_MODEL_ATTRIBUTE,
                (setting == null) ? "" : setting.getValue() == null ? "" : setting.getValue());
    }
}
