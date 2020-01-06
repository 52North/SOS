/**
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
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.service.profile.DefaultProfile;
import org.n52.sos.service.profile.Profile;
import org.n52.sos.service.profile.ProfileHandler;
import org.n52.sos.util.XmlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sensorweb.sos.profile.SosProfileDocument;

import com.google.common.collect.Lists;

/**
 * @since 4.0.0
 * 
 */
public class ProfileHandlerImpl implements ProfileHandler {

    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileHandlerImpl.class);

    private Profile activeProfile;

    private Map<String, Profile> availableProfiles = new HashMap<String, Profile>(1);

    public ProfileHandlerImpl() throws ConfigurationException {
        setActiveProfile(new DefaultProfile());
        addAvailableProvile(getActiveProfile());
        try {
            addProfile(loadProfiles());
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

    private List<Profile> loadProfiles() throws OwsExceptionReport {
        Collection<File> listFiles = loadFiles();
        List<Profile> profiles = Lists.newArrayList();
        for (File file : listFiles) {
            XmlObject xmlDocument = XmlHelper.loadXmlDocumentFromFile(file);
            if (xmlDocument instanceof SosProfileDocument) {
                profiles.add(ProfileParser.parseSosProfile((SosProfileDocument) xmlDocument));
                
            }
        }
        return profiles;
    }
    
    private Collection<File> loadFiles() {
        IOFileFilter fileFilter = new WildcardFileFilter("*-profile.xml");
        File folder = FileUtils.toFile(ProfileHandlerImpl.class.getResource("/"));
        return FileUtils.listFiles(folder, fileFilter, DirectoryFileFilter.DIRECTORY);
    }
    

    private void addProfile(List<Profile> profiles) {
       for (Profile profile : profiles) {
           addProfile(profile);
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
        Collection<File> listFiles = loadFiles();
        for (File file : listFiles) {
            try {
                XmlObject xmlDocument = XmlHelper.loadXmlDocumentFromFile(file);
                if (xmlDocument instanceof SosProfileDocument) {
                    SosProfileDocument doc = (SosProfileDocument) xmlDocument;
                    doc.getSosProfile().setActiveProfile(checkActive(doc.getSosProfile().getIdentifier()));
                    doc.save(file);
                }
            } catch (OwsExceptionReport e) {
                LOGGER.error("Error while loading profile from file!", e);
            } catch (IOException e) {
                LOGGER.error("Error while storing profile to file!", e);
            }
        }
    }

    @Override
    public void reloadProfiles() {
        try {
            addProfile(loadProfiles());
        } catch (OwsExceptionReport e) {
            throw new ConfigurationException("Error while loading profiles", e);
        }
    }

    private boolean checkActive(String identifier) {
        return getAvailableProfiles().get(identifier).isActiveProfile();
    }
}
