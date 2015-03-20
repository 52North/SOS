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

import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import org.n52.sos.config.AdministratorUser;
import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingValue;
import org.n52.sos.ds.ConnectionProviderException;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.exception.JSONException;
import org.n52.sos.util.JSONUtils;
import org.n52.sos.util.StringHelper;
import org.n52.sos.web.AbstractController;
import org.n52.sos.web.ControllerConstants;
import org.n52.sos.web.auth.DefaultAdministratorUser;
import org.n52.sos.web.auth.UserService;




@Controller
public class AdminSettingsController extends AbstractController {
    private static final Logger LOG = LoggerFactory.getLogger(AdminSettingsController.class);

    @Autowired
    private UserService userService;

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public String unauthorized(BadCredentialsException ex) {
        return ex.getMessage();
    }

    @RequestMapping(value = ControllerConstants.Paths.ADMIN_SETTINGS, method = RequestMethod.GET)
    public ModelAndView displaySettings(Principal user) throws ConfigurationException, JSONException, ConnectionProviderException {
        Map<String, Object> model = new HashMap<String, Object>(2);
        model.put(ControllerConstants.SETTINGS_MODEL_ATTRIBUTE, getSettingsJsonString());
        model.put(ControllerConstants.ADMIN_USERNAME_REQUEST_PARAMETER, user.getName());
        return new ModelAndView(ControllerConstants.Views.ADMIN_SETTINGS, model);
    }

    @RequestMapping(value = ControllerConstants.Paths.ADMIN_SETTINGS_UPDATE, method = RequestMethod.POST)
    public void updateSettings(HttpServletRequest request, HttpServletResponse response, Principal user)
            throws AuthenticationException, ConfigurationException {
        LOG.info("Updating Settings");
        try {
            updateAdminUser(request, user);
            updateSettings(request);
        } catch (ConnectionProviderException e1) {
            LOG.error("Error saving settings", e1);
            throw new RuntimeException(e1.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping(value = ControllerConstants.Paths.ADMIN_SETTINGS_DUMP, method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public String dump() {
        try {
            return getSettingsJsonString();
        } catch (Exception ex) {
            LOG.error("Could not load settings", ex);
            throw new RuntimeException(ex);
        }
    }

    private String getSettingsJsonString()
            throws ConfigurationException, JSONException,
                   ConnectionProviderException {
        return JSONUtils.print(toJSONValueMap(getSettingsManager().getSettings()));
    }

    private void logSettings(Collection<SettingValue<?>> values) {
        if (LOG.isDebugEnabled()) {
            for (SettingValue<?> value : values) {
                LOG.info("Saving Setting: ('{}'({}) => '{}')", value.getKey(), value.getType(), value.getValue());
            }
        }
    }

    private void updateSettings(HttpServletRequest request) throws ConnectionProviderException {
        Map<SettingDefinition<?, ?>, SettingValue<?>> changedSettings =
                new HashMap<SettingDefinition<?, ?>, SettingValue<?>>();
        for (SettingDefinition<?, ?> def : getSettingsManager().getSettingDefinitions()) {
            SettingValue<?> newValue =
                    getSettingsManager().getSettingFactory().newSettingValue(def, request.getParameter(def.getKey()));
            changedSettings.put(def, newValue);
        }
        logSettings(changedSettings.values());
        for (SettingValue<?> e : changedSettings.values()) {
            getSettingsManager().changeSetting(e);
        }
    }

    private void updateAdminUser(HttpServletRequest request, Principal user) throws AuthenticationException,
            ConfigurationException {
        String password = request.getParameter(ControllerConstants.ADMIN_PASSWORD_REQUEST_PARAMETER);
        String username = request.getParameter(ControllerConstants.ADMIN_USERNAME_REQUEST_PARAMETER);
        String currentPassword = request.getParameter(ControllerConstants.ADMIN_CURRENT_PASSWORD_REQUEST_PARAMETER);
        updateAdminUser(request, password, username, currentPassword, user.getName());
    }

    private void updateAdminUser(HttpServletRequest req, String newPassword, String newUsername,
            String currentPassword, String currentUsername) throws AuthenticationException, ConfigurationException {
        if (StringHelper.isNotEmpty(newPassword) || !currentUsername.equals(newUsername)) {
            if (currentPassword == null) {
                throw new BadCredentialsException("You have to submit your current password.");
            }
            AdministratorUser loggedInAdmin = getUserService().authenticate(currentUsername, currentPassword);
            if (loggedInAdmin instanceof DefaultAdministratorUser) {
                getUserService().createAdmin(newUsername, newPassword);
                HttpSession session = req.getSession(false);
                if (session != null) {
                    session.invalidate();
                }
                SecurityContextHolder.clearContext();
            } else {
                if (!currentPassword.equals(newPassword)) {
                    getUserService().setAdminPassword(loggedInAdmin, newPassword);
                }
                if (!currentUsername.equals(newUsername)) {
                    getUserService().setAdminUserName(loggedInAdmin, newUsername);
                }
            }
        }
    }
}
