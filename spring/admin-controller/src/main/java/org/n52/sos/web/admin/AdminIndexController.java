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
package org.n52.sos.web.admin;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.web.AbstractController;
import org.n52.sos.web.ControllerConstants;
import org.n52.sos.web.MetaDataHandler;
import org.n52.sos.web.auth.AdministratorUserPrinciple;

/**
 * @since 4.0.0
 *
 */
@Controller
@RequestMapping({ ControllerConstants.Paths.ADMIN_INDEX, ControllerConstants.Paths.ADMIN_ROOT })
public class AdminIndexController extends AbstractController {
    private static final Logger LOG = LoggerFactory.getLogger(AdminIndexController.class);

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView get() {
        boolean warn = false;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof AdministratorUserPrinciple) {
            AdministratorUserPrinciple administratorUserPrinciple = (AdministratorUserPrinciple) principal;
            if (administratorUserPrinciple.isDefaultAdmin()) {
                warn = true;
            }
        }
        Map<String, String> metadata = new HashMap<String, String>(MetaDataHandler.Metadata.values().length);
        try {
            for (MetaDataHandler.Metadata m : MetaDataHandler.Metadata.values()) {
                metadata.put(m.name(), getMetaDataHandler().get(m));
            }
        } catch (ConfigurationException ex) {
            LOG.error("Error reading metadata properties", ex);
        }
        Map<String, Object> model = new HashMap<String, Object>(2);
        model.put("metadata", metadata);
        model.put("warning", warn);
        return new ModelAndView(ControllerConstants.Views.ADMIN_INDEX, model);
    }
}
