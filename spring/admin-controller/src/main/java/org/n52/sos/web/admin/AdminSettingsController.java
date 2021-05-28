/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.n52.faroe.ConfigurationError;
import org.n52.faroe.SettingDefinition;
import org.n52.faroe.SettingValue;
import org.n52.faroe.SettingsService;
import org.n52.iceland.config.AdministratorUser;
import org.n52.iceland.ds.ConnectionProviderException;
import org.n52.iceland.exception.JSONException;
import org.n52.janmayen.Json;
import org.n52.sos.web.common.AbstractController;
import org.n52.sos.web.common.ControllerConstants;
import org.n52.sos.web.common.auth.DefaultAdministratorUser;
import org.n52.sos.web.common.auth.SosAuthenticationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.google.common.base.Strings;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Controller
public class AdminSettingsController extends AbstractController {
    private static final Logger LOG = LoggerFactory.getLogger(AdminSettingsController.class);

    @Inject
    private SosAuthenticationProvider userService;

    @Inject
    private SettingsService settingsManager;

    public SosAuthenticationProvider getUserService() {
        return userService;
    }

    public void setUserService(SosAuthenticationProvider userService) {
        this.userService = userService;
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public String unauthorized(BadCredentialsException ex) {
        return ex.getMessage();
    }

    @RequestMapping(value = ControllerConstants.Paths.ADMIN_SETTINGS, method = RequestMethod.GET)
    public ModelAndView displaySettings(Principal user)
            throws ConfigurationError, JSONException, ConnectionProviderException {
        Map<String, Object> model = new HashMap<>(2);
        model.put(ControllerConstants.SETTINGS_MODEL_ATTRIBUTE, getSettingsJsonString());
        model.put(ControllerConstants.ADMIN_USERNAME_REQUEST_PARAMETER, user.getName());
        return new ModelAndView(ControllerConstants.Views.ADMIN_SETTINGS, model);
    }

    @ResponseBody
    @RequestMapping(value = ControllerConstants.Paths.ADMIN_SETTINGS_DUMP,
                    method = RequestMethod.GET,
                    produces = "application/json; charset=UTF-8")
    public String dump() {
        try {
            return getSettingsJsonString();
        } catch (Exception ex) {
            LOG.error("Could not load settings", ex);
            throw new RuntimeException(ex);
        }
    }

    @RequestMapping(value = ControllerConstants.Paths.ADMIN_SETTINGS_UPDATE, method = RequestMethod.POST)
    public void updateSettings(HttpServletRequest request, HttpServletResponse response, Principal user)
            throws AuthenticationException, ConfigurationError {
        LOG.info("Updating Settings");
        updateAdminUser(request, user);
        updateSettings(request);
    }

    @SuppressFBWarnings("PT_ABSOLUTE_PATH_TRAVERSAL")
    private void updateSettings(HttpServletRequest request) {
        Map<SettingDefinition<?>, SettingValue<?>> changedSettings = new HashMap<>();
        for (SettingDefinition<?> def : settingsManager.getSettingDefinitions()) {
            SettingValue<?> newValue =
                    settingsManager.getSettingFactory().newSettingValue(def, request.getParameter(def.getKey()));
            changedSettings.put(def, newValue);
        }
        logSettings(changedSettings.values());
        for (SettingValue<?> e : changedSettings.values()) {
            settingsManager.changeSetting(e);
        }
    }

    private String getSettingsJsonString() throws ConfigurationError, JSONException, ConnectionProviderException {
        return Json.print(encodeValues(settingsManager.getSettings()));
    }

    private void logSettings(Collection<SettingValue<?>> values) {
        if (LOG.isDebugEnabled()) {
            for (SettingValue<?> value : values) {
                LOG.info("Saving Setting: ('{}'({}) => '{}')", value.getKey(), value.getType(), value.getValue());
            }
        }
    }

    private void updateAdminUser(HttpServletRequest request, Principal user)
            throws AuthenticationException, ConfigurationError {
        String password = request.getParameter(ControllerConstants.ADMIN_PASSWORD_REQUEST_PARAMETER);
        String username = request.getParameter(ControllerConstants.ADMIN_USERNAME_REQUEST_PARAMETER);
        String currentPassword = request.getParameter(ControllerConstants.ADMIN_CURRENT_PASSWORD_REQUEST_PARAMETER);
        updateAdminUser(request, password, username, currentPassword, user.getName());
    }

    private void updateAdminUser(HttpServletRequest req, String newPassword, String newUsername,
            String currentPassword, String currentUsername) throws AuthenticationException, ConfigurationError {
        if (!Strings.isNullOrEmpty(newPassword) || !currentUsername.equals(newUsername)) {
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
