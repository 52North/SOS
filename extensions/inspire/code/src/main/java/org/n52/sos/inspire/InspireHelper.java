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
package org.n52.sos.inspire;

import java.net.URI;
import java.util.Locale;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.n52.sos.config.SettingsManager;
import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.config.annotation.Setting;
import org.n52.sos.i18n.I18NSettings;
import org.n52.sos.inspire.settings.InspireSettings;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.StringHelper;
import org.n52.sos.util.Validation;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

/**
 * Helper class for INSPIRE
 *
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 *
 */
@Configurable
public class InspireHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(InspireHelper.class);

    private static InspireHelper instance;

    private String inspireId;

    private boolean enabled = false;

    private boolean fullExtendedCapabilities = false;

    private URI metadataUrlURL;

    private String metadataUrlMediatType;

    private String conformityTitle;

    private String conformityDateOfCreation;

    private String metadataDate;

    private InspireLanguageISO6392B defaultLanguage = null;

    private Set<InspireLanguageISO6392B> supportedLanguages = Sets.newHashSet();

    private boolean useAuthority = false;
    
    private String namespace;

    /**
     * @return Returns a singleton instance of the ServiceConfiguration.
     */
    public static synchronized InspireHelper getInstance() {
        if (instance == null) {
            instance = new InspireHelper();
            SettingsManager.getInstance().configure(instance);
        }
        return instance;
    }

    /**
     * private constructor for singleton
     */
    private InspireHelper() {
    }

    /**
     * Set the INSPIRE id
     *
     * @param inspireId
     *            the INSPIRE id to set
     */
//    @Setting(InspireSettings.INSPIRE_ID_KEY)
    public void setInspireId(String inspireId) {
        Validation.notNullOrEmpty("The INSPIRE id", inspireId);
        this.inspireId = inspireId;
    }

    /**
     * Get the INSPIRE id
     *
     * @return the INSPIRE id
     */
    public String getInspireId() {
        return inspireId;
    }

    /**
     * Set the default language
     *
     * @param defaultLanguage
     *            the default language to set
     */
    @Setting(I18NSettings.I18N_DEFAULT_LANGUAGE)
    public void setDefaultLanguage(final String defaultLanguage) {
        Validation.notNullOrEmpty("Default language as three character string", defaultLanguage);
        this.defaultLanguage = InspireLanguageISO6392B.fromValue(defaultLanguage);
    }

    /**
     * Get the default language
     *
     * @return the default language
     */
    public InspireLanguageISO6392B getDefaultLanguage() {
        return defaultLanguage;
    }

    /**
     * Get the supported languages
     *
     * @return the supporte languages
     */
    public Set<InspireLanguageISO6392B> getSupportedLanguages() {
        if (supportedLanguages.size() != Configurator.getInstance().getCache().getSupportedLanguages().size()) {
            updateSupportedLanguages();
        }
        return supportedLanguages;
    }

    /**
     * Update the local supported languages storage
     */
    private void updateSupportedLanguages() {
        supportedLanguages.clear();
        supportedLanguages.add(getDefaultLanguage());
        for (Locale language : Configurator.getInstance().getCache().getSupportedLanguages()) {
            try {
                supportedLanguages.add(InspireLanguageISO6392B.fromValue(language));
            } catch (IllegalArgumentException iae) {
                LOGGER.error(String.format("The supported language %s is not valid for INSPIRE", language), iae);
            }
        }
    }

    /**
     * Set the indicator to enable/disable the INSPIRE ExtendedCapabilities
     *
     * @param enabled
     *            the indicator to set
     */
    @Setting(InspireSettings.INSPIRE_ENABLED_KEY)
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Is the INSPIRE ExtendedCapabilities enabled
     *
     * @return <code>true</code>, if the INSPIRE ExtendedCapabilities enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Set the indicator to use the full or minimal INSPIRE ExtendedCapabilities
     *
     * @param fullExtendedCapabilities
     *            indicator to set
     */
    @Setting(InspireSettings.INSPIRE_FULL_EXTENDED_CAPABILITIES_KEY)
    public void setFullExtendedCapabilities(boolean fullExtendedCapabilities) {
        this.fullExtendedCapabilities = fullExtendedCapabilities;
    }

    /**
     * Should the full or minimal INSPIRE ExtendedCapabilities be used
     *
     * @return <code>true</code>, if the full INSPIRE ExtendedCapabilities
     *         should be use
     */
    public boolean isFullExtendedCapabilities() {
        return fullExtendedCapabilities;
    }


    @Setting(InspireSettings.INSPIRE_METADATA_URL_URL_KEY)
    public void setMetadataUrlURL(URI url) {
        this.metadataUrlURL = url;
    }

    public URI getMetadataUrlURL() {
        return metadataUrlURL;
    }

    public boolean isSetMetadataUrlURL() {
        return getMetadataUrlURL() != null;
    }

    @Setting(InspireSettings.INSPIRE_METADATA_URL_MEDIA_TYPE_KEY)
    public void setMetadataUrlMediaType(String mediaType) {
        this.metadataUrlMediatType = mediaType;
    }

    public String getMetadataUrlMediaType() {
        return metadataUrlMediatType;
    }

    public boolean isSetMetadataUrlMediaType() {
        return StringHelper.isNotEmpty(getMetadataUrlMediaType());
    }

    @Setting(InspireSettings.INSPIRE_METADATA_DATE_KEY)
    public void setMetadataDate(String time) {
        this.metadataDate = time;
    }

    public String getMetadataDate() {
        return metadataDate;
    }

    @Setting(InspireSettings.INSPIRE_CONFORMITY_TITLE_KEY)
    public void setConformityTitle(String title) {
        this.conformityTitle = title;
    }

    public String getConformityTitle() {
        return conformityTitle;
    }

    @Setting(InspireSettings.INSPIRE_CONFORMITY_DATE_OF_CREATION_KEY)
    public void setConformityDateOfCreation(String time) {
        this.conformityDateOfCreation = time;
    }

    public String getConformityDateOfCreation() {
        return conformityDateOfCreation;
    }

    @Setting(InspireSettings.INSPIRE_USE_AUTHORITY_KEY)
    public void setUseAuthority(boolean useAuthority) {
        this.useAuthority = useAuthority;
    }

    public boolean isUseAuthority() {
        return useAuthority;
    }

    /**
     * @return the namespace
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * @param namespace the namespace to set
     */
    @Setting(InspireSettings.INSPIRE_NAMESPACE_KEY)
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
    
    /**
     * @return the namespace
     */
    public boolean isSetNamespace() {
        return !Strings.isNullOrEmpty(getNamespace());
    }

    /**
     * Check the requested language.
     *
     * @param language
     *            Requested language
     * @return {@link InspireLanguageISO6392B} from the requested language or
     *         the configured default language
     */
    public InspireLanguageISO6392B checkRequestedLanguage(String language) {
        if (StringHelper.isNotEmpty(language)) {
            try {
                InspireLanguageISO6392B requestedLanguage = InspireLanguageISO6392B.fromValue(language);
                if (requestedLanguage != null && getSupportedLanguages().contains(requestedLanguage)) {
                    return requestedLanguage;
                }
            } catch (Exception e) {
                LOGGER.debug("Requested language '{}' is invalid!", language);
            }
        }
        return getDefaultLanguage();
    }

}
