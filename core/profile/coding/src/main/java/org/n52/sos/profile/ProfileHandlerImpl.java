/*
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.n52.faroe.ConfigurationError;
import org.n52.janmayen.Json;
import org.n52.janmayen.lifecycle.Constructable;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.service.profile.DefaultProfile;
import org.n52.sos.service.profile.Profile;
import org.n52.sos.service.profile.ProfileHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @since 4.0.0
 *
 */
public class ProfileHandlerImpl implements ProfileHandler, Constructable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileHandlerImpl.class);
    private static final String PROFILES = "profiles";
    private Profile activeProfile;
    private final Map<String, Profile> availableProfiles = new HashMap<>(1);

    @Override
    public void init() {
        setActiveProfile(new DefaultProfile());
        addAvailableProfile(getActiveProfile());
        try {
            loadProfiles();
        } catch (OwsExceptionReport e) {
            throw new ConfigurationError("Error while loading profiles", e);
        }
    }

    @Override
    public Profile getActiveProfile() {
        return activeProfile;
    }

    private void setActiveProfile(Profile profile) {
        this.activeProfile = profile;
        addAvailableProfile(profile);
    }

    private void addAvailableProfile(Profile profile) {
        if (availableProfiles.containsKey(profile.getIdentifier())) {
            LOGGER.warn("Profile with the identifier {} still exist! Existing profile is overwritten!",
                    profile.getIdentifier());
        }
        availableProfiles.put(profile.getIdentifier(), profile);
    }

    private void loadProfiles() throws OwsExceptionReport {
        for (File file : loadFiles()) {
            try {
                JsonNode profiles = Json.loadFile(file);
                ProfileParser pp = new ProfileParser();
                JsonNode pNode = profiles.path(PROFILES);
                if (pNode.isArray()) {
                    for (int i = 0; i < pNode.size(); i++) {
                        Profile profile =  pp.parseProfile(pNode.get(i));
                        addProfile(profile);
                    }
                } else {
                    Profile profile = pp.parseProfile(pNode);
                    addProfile(profile);
                }
            } catch (IOException ioe) {
                throw new NoApplicableCodeException().causedBy(ioe).withMessage("Error while loading profies file.");
            }
        }
    }

    private Collection<File> loadFiles() {
        IOFileFilter fileFilter = new WildcardFileFilter("profiles.json");
        File folder = FileUtils.toFile(ProfileHandlerImpl.class.getResource("/"));
        return FileUtils.listFiles(folder, fileFilter, DirectoryFileFilter.DIRECTORY);
    }

    private void addProfile(Profile profile) {
        if (profile != null) {
            if (profile.isActiveProfile()) {
                setActiveProfile(profile);
            } else {
                addAvailableProfile(profile);
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

    @Override
    public void activateProfile(String identifier) {
        if (getAvailableProfiles().containsKey(identifier)) {
            for (Profile profile : getAvailableProfiles().values()) {
                if (profile.getIdentifier().equals(identifier)) {
                    profile.setActiveProfile(true);
                    setActiveProfile(profile);
                } else {
                    profile.setActiveProfile(false);
                }
            }
            persist();
        }
    }

    @Override
    public void persist() {
        ProfileWriter pw = new ProfileWriter();
        for (File file : loadFiles()) {
            try (FileOutputStream fio = new FileOutputStream(file)) {
                Json.print(fio, pw.write(getAvailableProfiles().values()));
            } catch (IOException e) {
                LOGGER.error("Error while storing profile to file!", e);
            }
        }
    }

    @Override
    public void reloadProfiles() {
        try {
            loadProfiles();
        } catch (OwsExceptionReport e) {
            throw new ConfigurationError("Error loading profiles", e);
        }
    }

}
