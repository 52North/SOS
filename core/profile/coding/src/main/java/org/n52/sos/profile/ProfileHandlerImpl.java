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
package org.n52.sos.profile;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.n52.iceland.exception.ConfigurationException;
import org.n52.iceland.exception.ows.NoApplicableCodeException;
import org.n52.iceland.ogc.ows.OwsExceptionReport;
import org.n52.iceland.service.profile.DefaultProfile;
import org.n52.iceland.service.profile.Profile;
import org.n52.iceland.service.profile.ProfileHandler;
import org.n52.iceland.util.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @since 4.0.0
 * 
 */
public class ProfileHandlerImpl implements ProfileHandler {

    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileHandlerImpl.class);

    private static final String PROFILES = "profiles";

    private Profile activeProfile;

    private Map<String, Profile> availableProfiles = new HashMap<String, Profile>(1);

    public ProfileHandlerImpl() throws ConfigurationException {
        setActiveProfile(new DefaultProfile());
        addAvailableProvile(getActiveProfile());
        try {
            loadProfiles();
        } catch (OwsExceptionReport e) {
            throw new ConfigurationException("Error while loading profiles", e);
        }
    }

    @Override
    public Profile getActiveProfile() {
        return activeProfile;
    }

    private void setActiveProfile(Profile profile) {
        this.activeProfile = profile;
        addAvailableProvile(profile);
    }

    private void addAvailableProvile(Profile profile) {
        if (availableProfiles.containsKey(profile.getIdentifier())) {
            LOGGER.warn("Profile with the identifier {} still exist! Existing profile is overwritten!",
                    profile.getIdentifier());
        }
        availableProfiles.put(profile.getIdentifier(), profile);
    }

    private void loadProfiles() throws OwsExceptionReport {
        IOFileFilter fileFilter = new WildcardFileFilter("profiles.json");
        File folder = FileUtils.toFile(ProfileHandlerImpl.class.getResource("/"));
        Collection<File> listFiles = FileUtils.listFiles(folder, fileFilter, DirectoryFileFilter.DIRECTORY);
        for (File file : listFiles) {
            try {
                JsonNode profiles = JSONUtils.loadFile(file);
                ProfileParser pp = new ProfileParser();
                JsonNode pNode = profiles.path(PROFILES);
                if (pNode.isArray()) {
                    for (int i = 0; i < pNode.size(); i++) {
                        Profile profile =  pp.parseSosProfile(pNode.get(i));
                        addProfile(profile);
                    }
                } else {
                    Profile profile = pp.parseSosProfile(pNode);
                    addProfile(profile);
                }
            } catch (IOException ioe) {
                throw new NoApplicableCodeException().causedBy(ioe).withMessage("Error while loading profies file.");
            }
        }
    }

    private void addProfile(Profile profile) {
        if (profile != null) {
            if (profile.isActiveProfile()) {
                setActiveProfile(profile);
            } else {
                addAvailableProvile(profile);
            }
        }
    }

    @Override
    public Map<String, Profile> getAvailableProfiles() {
        return Collections.unmodifiableMap(availableProfiles);
    }

    @Override
    public boolean isSetActiveProfile() {
        return activeProfile != null;
    }

}
