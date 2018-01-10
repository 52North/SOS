/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.web.common.auth;

import java.io.Serializable;
import java.security.Principal;
import java.util.Collections;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.n52.faroe.ConfigurationError;
import org.n52.iceland.config.AdminUserService;
import org.n52.iceland.config.AdministratorUser;

/**
 * @since 4.0.0
 *
 */
public class SosAuthenticationProvider implements AuthenticationProvider, Serializable {
    private static final long serialVersionUID = -3207103212342510378L;

    private static final Logger LOG = LoggerFactory.getLogger(SosAuthenticationProvider.class);

    private static final Set<AdministratorAuthority> ADMIN_AUTHORITIES
            = Collections.singleton(new AdministratorAuthority());

    private AdminUserService adminUserService;
    private PasswordEncoder passwordEncoder;

    @Override
    public UsernamePasswordAuthenticationToken authenticate(Authentication authentication)
            throws AuthenticationException {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) authentication;
        AdministratorUser user = authenticate((String) auth.getPrincipal(), (String) auth.getCredentials());
        boolean isDefaultAdmin = user instanceof DefaultAdministratorUser;
        AdministratorUserPrinciple principle = new AdministratorUserPrinciple(user, isDefaultAdmin);
        return new UsernamePasswordAuthenticationToken(principle, null, ADMIN_AUTHORITIES);
    }

    public AdministratorUser authenticate(String username, String password) throws AuthenticationException {

        if (username == null || password == null) {
            throw new BadCredentialsException("Bad Credentials");
        }
        if (!this.adminUserService.hasAdminUser()) {
            LOG.warn("No admin user is defined! Use the default credentials '{}:{}' "
                    + "to authenticate and change the password as soon as possible!",
                    DefaultAdministratorUser.DEFAULT_USERNAME, DefaultAdministratorUser.DEFAULT_PASSWORD);
            if (username.equals(DefaultAdministratorUser.DEFAULT_USERNAME)
                    && password.equals(DefaultAdministratorUser.DEFAULT_PASSWORD)) {
                return new DefaultAdministratorUser();
            }
        }

        AdministratorUser user = this.adminUserService.getAdminUser(username);

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
        return this.adminUserService.createAdminUser(username, getPasswordEncoder().encode(password));
    }

    public void setAdminUserName(AdministratorUser user, String name) {
        user.setUsername(name);
        this.adminUserService.saveAdminUser(user);
    }

    public void setAdminPassword(AdministratorUser user, String password) {
        user.setPassword(getPasswordEncoder().encode(password));
        this.adminUserService.saveAdminUser(user);
    }

    public AdministratorUser getAdmin(String username) throws ConfigurationError {
        return this.adminUserService.getAdminUser(username);
    }

    public AdministratorUser getAdmin(Principal user) throws ConfigurationError {
        return this.adminUserService.getAdminUser(user.getName());
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    @Inject
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Inject
    public void setAdminUserService(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }
}
