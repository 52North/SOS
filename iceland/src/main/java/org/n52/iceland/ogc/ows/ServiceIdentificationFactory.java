/**
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.ogc.ows;

import static org.n52.iceland.ogc.ows.ServiceIdentificationFactorySettings.ABSTRACT;
import static org.n52.iceland.ogc.ows.ServiceIdentificationFactorySettings.ACCESS_CONSTRAINTS;
import static org.n52.iceland.ogc.ows.ServiceIdentificationFactorySettings.FEES;
import static org.n52.iceland.ogc.ows.ServiceIdentificationFactorySettings.FILE;
import static org.n52.iceland.ogc.ows.ServiceIdentificationFactorySettings.KEYWORDS;
import static org.n52.iceland.ogc.ows.ServiceIdentificationFactorySettings.SERVICE_TYPE;
import static org.n52.iceland.ogc.ows.ServiceIdentificationFactorySettings.SERVICE_TYPE_CODE_SPACE;
import static org.n52.iceland.ogc.ows.ServiceIdentificationFactorySettings.TITLE;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import org.n52.iceland.config.SettingsManager;
import org.n52.iceland.config.annotation.Configurable;
import org.n52.iceland.config.annotation.Setting;
import org.n52.iceland.exception.ConfigurationException;
import org.n52.iceland.i18n.I18NSettings;
import org.n52.iceland.i18n.LocaleHelper;
import org.n52.iceland.i18n.MultilingualString;
import org.n52.iceland.ogc.sos.SosConstants;
import org.n52.iceland.service.operator.ServiceOperatorRepository;
import org.n52.iceland.util.LazyThreadSafeProducer;
import org.n52.iceland.util.StringHelper;
import org.n52.iceland.util.Validation;
import org.n52.iceland.util.XmlHelper;

import com.google.common.collect.Sets;

@Configurable
public class ServiceIdentificationFactory extends LazyThreadSafeProducer<OwsServiceIdentification> {

    private File file;

    private String[] keywords;

    private MultilingualString title;

    private MultilingualString abstrakt;

    private String serviceType;

    private String serviceTypeCodeSpace;

    private String fees;

    private String[] constraints;

    public ServiceIdentificationFactory() throws ConfigurationException {
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
    protected OwsServiceIdentification create(Locale language) throws ConfigurationException {
        if (this.file != null) {
            return createFromFile();
        } else {
            return createFromSettings(language);
        }
    }

    private OwsServiceIdentification createFromSettings(Locale locale) {
        OwsServiceIdentification serviceIdentification = new OwsServiceIdentification();
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

    private OwsServiceIdentification createFromFile() throws ConfigurationException {
        try {
            OwsServiceIdentification serviceIdentification = new OwsServiceIdentification();
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
