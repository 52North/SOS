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
package org.n52.sos.web.auth;

import java.io.Serializable;
import java.security.Principal;
import java.util.Collections;

import org.n52.sos.config.AdministratorUser;
import org.n52.sos.config.SettingsManager;
import org.n52.sos.ds.ConnectionProviderException;
import org.n52.sos.exception.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @since 4.0.0
 * 
 */
public class UserService implements AuthenticationProvider, Serializable {
    private static final long serialVersionUID = -3207103212342510378L;

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UsernamePasswordAuthenticationToken authenticate(Authentication authentication)
            throws AuthenticationException {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) authentication;
        AdministratorUser user = authenticate((String) auth.getPrincipal(), (String) auth.getCredentials());
        return new UsernamePasswordAuthenticationToken(new AdministratorUserPrinciple(user,
                user instanceof DefaultAdministratorUser), null, Collections.singleton(new AdministratorAuthority()));
    }

    public AdministratorUser authenticate(final String username, final String password) throws AuthenticationException {
        AdministratorUser user;

        if (username == null || password == null) {
            throw new BadCredentialsException("Bad Credentials");
        }
        try {
            if (!getSettingsManager().hasAdminUser()) {
                LOG.warn("No admin user is defined! Use the default credentials '{}:{}' "
                        + "to authenticate and change the password as soon as possible!",
                        DefaultAdministratorUser.DEFAULT_USERNAME, DefaultAdministratorUser.DEFAULT_PASSWORD);
                if (username.equals(DefaultAdministratorUser.DEFAULT_USERNAME)
                        && password.equals(DefaultAdministratorUser.DEFAULT_PASSWORD)) {
                    return new DefaultAdministratorUser();
                }
            }
        } catch (ConnectionProviderException ex) {
            LOG.error("Error querying admin", ex);
            throw new AuthenticationServiceException("Can not query admin users", ex);
        }

        try {
            user = getSettingsManager().getAdminUser(username);
        } catch (Exception ex) {
            LOG.error("Error querying admin", ex);
            throw new BadCredentialsException("Bad Credentials");
        }

        if (user == null) {
            throw new BadCredentialsException("Bad Credentials");
        }

        if (!username.equals(user.getUsername()) || !getPasswordEncoder().matches(password, user.getPassword())) {
            throw new BadCredentialsException("Bad Credentials");
        }

        return user;
    }

    @Override
    public boolean supports(Class<?> type) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(type);
    }

    public AdministratorUser createAdmin(String username, String password) {
        try {
            return getSettingsManager().createAdminUser(username, getPasswordEncoder().encode(password));
        } catch (Exception ex) {
            LOG.error("Error saving admin", ex);
            throw new RuntimeException(ex);
        }
    }

    public void setAdminUserName(AdministratorUser user, String name) {
        try {
            getSettingsManager().saveAdminUser(user.setUsername(name));
        } catch (Exception ex) {
            LOG.error("Error saving admin", ex);
            throw new RuntimeException(ex);
        }
    }

    public void setAdminPassword(AdministratorUser user, String password) {
        try {
            getSettingsManager().saveAdminUser(user.setPassword(getPasswordEncoder().encode(password)));
        } catch (Exception ex) {
            LOG.error("Error saving admin", ex);
            throw new RuntimeException(ex);
        }
    }

    public AdministratorUser getAdmin(String username) throws ConfigurationException {
        try {
            return getSettingsManager().getAdminUser(username);
        } catch (ConnectionProviderException e) {
            throw new ConfigurationException(e);
        }
    }

    public AdministratorUser getAdmin(Principal user) throws ConfigurationException {
        try {
            return getSettingsManager().getAdminUser(user.getName());
        } catch (ConnectionProviderException e) {
            throw new ConfigurationException(e);
        }
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    protected SettingsManager getSettingsManager() throws ConfigurationException {
        return SettingsManager.getInstance();
    }
}
