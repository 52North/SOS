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
package org.n52.sos.ogc.ows;

import static org.n52.sos.ogc.ows.SosServiceIdentificationFactorySettings.ABSTRACT;
import static org.n52.sos.ogc.ows.SosServiceIdentificationFactorySettings.ACCESS_CONSTRAINTS;
import static org.n52.sos.ogc.ows.SosServiceIdentificationFactorySettings.FEES;
import static org.n52.sos.ogc.ows.SosServiceIdentificationFactorySettings.FILE;
import static org.n52.sos.ogc.ows.SosServiceIdentificationFactorySettings.KEYWORDS;
import static org.n52.sos.ogc.ows.SosServiceIdentificationFactorySettings.SERVICE_TYPE;
import static org.n52.sos.ogc.ows.SosServiceIdentificationFactorySettings.SERVICE_TYPE_CODE_SPACE;
import static org.n52.sos.ogc.ows.SosServiceIdentificationFactorySettings.TITLE;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import org.n52.sos.config.SettingsManager;
import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.config.annotation.Setting;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.i18n.I18NSettings;
import org.n52.sos.i18n.LocaleHelper;
import org.n52.sos.i18n.MultilingualString;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.service.operator.ServiceOperatorRepository;
import org.n52.sos.util.LazyThreadSafeProducer;
import org.n52.sos.util.StringHelper;
import org.n52.sos.util.Validation;
import org.n52.sos.util.XmlHelper;

import com.google.common.collect.Sets;

@Configurable
public class SosServiceIdentificationFactory extends LazyThreadSafeProducer<SosServiceIdentification> {

    private File file;

    private String[] keywords;

    private MultilingualString title;

    private MultilingualString abstrakt;

    private String serviceType;

    private String serviceTypeCodeSpace;

    private String fees;

    private String[] constraints;

    public SosServiceIdentificationFactory() throws ConfigurationException {
        SettingsManager.getInstance().configure(this);
    }

    @Setting(FILE)
    public void setFile(File file) {
        this.file = file;
        setRecreate();
    }

    public void setKeywords(String[] keywords) {
        this.keywords = copyOf(keywords);
        setRecreate();
    }

    @Setting(KEYWORDS)
    public void setKeywords(String keywords) {
        setKeywords(StringHelper.splitToArray(keywords));
    }

    @Setting(TITLE)
    public void setTitle(Object title) throws ConfigurationException {
        Validation.notNull("Service Identification Title", title);
        if (title instanceof MultilingualString) {
            this.title = (MultilingualString) title;
        } else if (title instanceof String) {
            Locale locale = LocaleHelper.fromString(I18NSettings.I18N_DEFAULT_LANGUAGE_DEFINITION.getDefaultValue());
            this.title = new MultilingualString().addLocalization(locale, (String)title);
        } else {
            throw new ConfigurationException(
                    String.format("%s is not supported as title!", title.getClass().getName()));
        }
        setRecreate();
    }

    @Setting(ABSTRACT)
    public void setAbstract(Object description) throws ConfigurationException {
        Validation.notNull("Service Identification Abstract", description);
        if (description instanceof MultilingualString) {
            this.abstrakt = (MultilingualString) description;
        } else if (description instanceof String) {
            Locale locale = LocaleHelper.fromString(I18NSettings.I18N_DEFAULT_LANGUAGE_DEFINITION.getDefaultValue());
            this.abstrakt = new MultilingualString().addLocalization(locale, (String)description);
        } else {
            throw new ConfigurationException(
                    String.format("%s is not supported as abstract!", description.getClass().getName()));
        }
        setRecreate();
    }

    @Setting(SERVICE_TYPE)
    public void setServiceType(String serviceType) throws ConfigurationException {
        Validation.notNullOrEmpty("Service Identification Service Type", serviceType);
        this.serviceType = serviceType;
        setRecreate();
    }

    @Setting(SERVICE_TYPE_CODE_SPACE)
    public void setServiceTypeCodeSpace(String serviceTypeCodeSpace) throws ConfigurationException {
        this.serviceTypeCodeSpace = serviceTypeCodeSpace;
        setRecreate();
    }

    @Setting(FEES)
    public void setFees(String fees) throws ConfigurationException {
        // Validation.notNullOrEmpty("Service Identification Fees", fees);
        this.fees = fees;
        setRecreate();
    }

    public void setConstraints(String[] constraints) {
        this.constraints = copyOf(constraints);
        setRecreate();
    }

    @Setting(ACCESS_CONSTRAINTS)
    public void setConstraints(String constraints) {
        setConstraints(StringHelper.splitToArray(constraints));
    }

    @Override
    protected SosServiceIdentification create(Locale language) throws ConfigurationException {
        if (this.file != null) {
            return createFromFile();
        } else {
            return createFromSettings(language);
        }
    }

    private SosServiceIdentification createFromSettings(Locale locale) {
        SosServiceIdentification serviceIdentification = new SosServiceIdentification();
        if (this.title != null) {
            serviceIdentification.setTitle(this.title.filter(locale));
        }
        if (this.abstrakt != null) {
            serviceIdentification.setAbstract(this.abstrakt.filter(locale));
        }
        if (this.constraints != null) {
            serviceIdentification.setAccessConstraints(Arrays.asList(this.constraints));
        }
        serviceIdentification.setFees(this.fees);
        serviceIdentification.setServiceType(this.serviceType);
        serviceIdentification.setServiceTypeCodeSpace(this.serviceTypeCodeSpace);
        Set<String> supportedVersions = ServiceOperatorRepository.getInstance().getSupportedVersions(SosConstants.SOS);
        serviceIdentification.setVersions(supportedVersions);
        if (this.keywords != null) {
            serviceIdentification.setKeywords(Arrays.asList(this.keywords));
        }
        return serviceIdentification;
    }

    private SosServiceIdentification createFromFile() throws ConfigurationException {
        try {
            SosServiceIdentification serviceIdentification = new SosServiceIdentification();
            serviceIdentification.setServiceIdentification(XmlHelper.loadXmlDocumentFromFile(this.file));
            return serviceIdentification;
        } catch (OwsExceptionReport ex) {
            throw new ConfigurationException(ex);
        }
    }

    public Set<Locale> getAvailableLocales() {
        if (this.title == null) {
            if (this.abstrakt == null) {
                return Collections.emptySet();
            } else {
                return this.abstrakt.getLocales();
            }
        } else {
            if (this.abstrakt == null) {
                return this.title.getLocales();
            } else {
                return Sets.union(this.title.getLocales(), this.abstrakt.getLocales());
            }
        }
    }

    private static String[] copyOf(String[] a) {
        return a == null ? new String[0] : Arrays.copyOf(a, a.length);
    }
}
