/*
 * Copyright (C) 2012-2022 52°North Initiative for Geospatial Open Source
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
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.service.profile.Profile;
import org.n52.sos.service.profile.ProfileHandler;
import org.n52.sos.web.common.ControllerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;

@Controller
@RequestMapping(ControllerConstants.Paths.ADMIN_PROFILES)
public class AdminProfileController {

    private static final Logger log = LoggerFactory.getLogger(AdminProfileController.class);

    private static final String ACTIVE = "active";

    private static final String PROFILES = "profiles";

    private ProfileHandler profileHandler;

    protected ProfileHandler getProfileHandler() {
        return profileHandler;
    }

    @Inject
    public void setProfileHandler(ProfileHandler profileHandler) {
        this.profileHandler = profileHandler;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView getProfiles() {
        Map<String, Object> model = new HashMap<String, Object>(5);
        String current = getSelectedProfile();
        if (current != null && !current.isEmpty()) {
            model.put(ACTIVE, current);
        }
        List<String> profiles = Lists.newArrayList();
        for (Profile prof : getProfileHandler().getAvailableProfiles().values()) {
            profiles.add(prof.getIdentifier());
        }
        model.put(PROFILES, profiles);
        return new ModelAndView(ControllerConstants.Views.ADMIN_PROFILES, model);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/activate", method = RequestMethod.GET)
    public void activateProfile(@RequestParam("identifier") String identifier) throws OwsExceptionReport {
        getProfileHandler().activateProfile(identifier);
    }

    // @ResponseStatus(HttpStatus.OK)
    // @RequestMapping(value = "/description", method = RequestMethod.GET,
    // produces = MediaType.APPLICATION_JSON_VALUE)
    // public String getProfileDefinition(@RequestParam("identifier") String
    // identifier) throws OwsExceptionReport {
    // ObjectNode response = JSONUtils.nodeFactory().objectNode();
    // ObjectNode staticCapabilities = response.putObject("description");
    // staticCapabilities.put(identifier,
    // getProfileHandler().getAvailableProfiles().get(identifier).getDefinition());
    // return response.toString();
    // }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/reload", method = RequestMethod.GET)
    public void reloadProfiles() {
        getProfileHandler().reloadProfiles();
    }

    protected String getSelectedProfile() {
        return getProfileHandler().getActiveProfile().getIdentifier();
    }

}
