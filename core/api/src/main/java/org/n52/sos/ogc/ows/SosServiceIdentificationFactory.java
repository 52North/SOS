/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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

import static org.n52.sos.ogc.ows.SosServiceIdentificationFactorySettings.*;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.n52.sos.config.SettingsManager;
import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.config.annotation.Setting;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.service.operator.ServiceOperatorRepository;
import org.n52.sos.util.LazyThreadSafeProducer;
import org.n52.sos.util.StringHelper;
import org.n52.sos.util.Validation;
import org.n52.sos.util.XmlHelper;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
@Configurable
public class SosServiceIdentificationFactory extends LazyThreadSafeProducer<SosServiceIdentification> {

    private File file;

    private String[] keywords;

    private Map<String, Set<String>> languageTitleMap = Maps.newHashMap();

    private Map<String, Set<String>> languageAbstractMap = Maps.newHashMap();

    private String serviceType;

    private String serviceTypeCodeSpace;

    private String fees;

    private String[] constraints;
    
    private String defaultLanguage;
    
    private String defaultTitle;
    
    private String defaultAbstract;
    
    public SosServiceIdentificationFactory() throws ConfigurationException {
        SettingsManager.getInstance().configure(this);
   }

    @Setting(FILE)
    public void setFile(File file) {
        this.file = file;
        setRecreate();
    }

    public void setKeywords(String[] keywords) {
        this.keywords = keywords == null ? new String[0] : Arrays.copyOf(keywords, keywords.length);
        setRecreate();
    }

    @Setting(KEYWORDS)
    public void setKeywords(String keywords) {
        setKeywords(StringHelper.splitToArray(keywords));
//        if (keywords != null) {
//            String[] keywordArray = keywords.split(",");
//            ArrayList<String> keywordList = new ArrayList<String>(keywordArray.length);
//            for (String s : keywordArray) {
//                if (s != null && !s.trim().isEmpty()) {
//                    keywordList.add(s.trim());
//                }
//            }
//            setKeywords(keywordList.toArray(new String[keywordList.size()]));
//        } else {
//            setKeywords(new String[0]);
//        }
    }
    
    @Setting(ABTRACT_TITLE_LANGUAGE)
    public void setDefaultLanguage(String defaultLanguage) {
        Validation.notNullOrEmpty("Defualt language as three character string", defaultLanguage);
        this.defaultLanguage = defaultLanguage;
        if (StringHelper.isNotEmpty(defaultTitle)) {
            addToMap(languageTitleMap, defaultLanguage, defaultTitle);
        }
        if (StringHelper.isNotEmpty(defaultAbstract)) {
            addToMap(languageAbstractMap, defaultLanguage, defaultAbstract);
        }
    }
    
    private boolean isSetDefaultLanguage() {
        return StringHelper.isNotEmpty(defaultLanguage);
    }

    @Setting(TITLE)
    public void setTitle(String title) throws ConfigurationException {
        Validation.notNullOrEmpty("Service Identification Title", title);
        if (isSetDefaultLanguage()) {
            addLanguageTitle(defaultLanguage, title);
        } else {
            defaultTitle = title;
        }
        setRecreate();
    }

    @Setting(ABSTRACT)
    public void setAbstract(String description) throws ConfigurationException {
        Validation.notNullOrEmpty("Service Identification Abstract", description);
        if (isSetDefaultLanguage()) {
            addLanguageAbstract(defaultLanguage, description);
        } else {
            defaultAbstract = description;
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
//        Validation.notNullOrEmpty("Service Identification Fees", fees);
        this.fees = fees;
        setRecreate();
    }

    public void setConstraints(String[] constraints) {
        this.constraints = constraints == null ? new String[0] : Arrays.copyOf(constraints, constraints.length);
        setRecreate();
    }

    @Setting(ACCESS_CONSTRAINTS)
    public void setConstraints(String constraints) {
        setConstraints(StringHelper.splitToArray(constraints));
    }
    
    public void setLanguageTitle(Map<String, Set<String>> languageTitleMap) {
        this.languageTitleMap = languageTitleMap;
    }
    
    public void setLanguageTitle(String language, Set<String> titles) {
        this.languageTitleMap.put(language, titles);
    }
    
    public void addLanguageTitle(String language, Set<String> titles) {
        addToMap(languageTitleMap, language, titles);
    }
    
    public void addLanguageTitle(String language, String title) {
        addToMap(languageTitleMap, language, title);
    }
    
    public void setLanguageAbstract(Map<String, Set<String>> languageAbstractMap) {
        this.languageAbstractMap = languageAbstractMap;
    }
    
    public void setLanguageAbstract(String language, Set<String> abstrakts) {
        this.languageAbstractMap.put(language, abstrakts);
    }
    
    public void addLanguageAbstract(String language, Set<String> abstrakts) {
        addToMap(languageAbstractMap, language, abstrakts);
    }
    
    public void addLanguageAbstract(String language, String abstrakt) {
        addToMap(languageAbstractMap, language, abstrakt);
    }
    
   private void addToMap(Map<String, Set<String>> map, String key, Set<String> values) {
        if (map.containsKey(key)) {
            map.get(key).addAll(values);
        } else {
            map.put(key, values);
        }
    }
    
   private void addToMap(Map<String, Set<String>> map, String key, String value) {
        if (map.containsKey(key)) {
            map.get(key).add(value);
        } else {
            map.put(key, Sets.newHashSet(value));
        }
    }
    
    @Override
    protected SosServiceIdentification create() throws ConfigurationException {
        SosServiceIdentification serviceIdentification = new SosServiceIdentification();
        if (this.file != null) {
            try {
                serviceIdentification.setServiceIdentification(XmlHelper.loadXmlDocumentFromFile(this.file));
            } catch (OwsExceptionReport ex) {
                throw new ConfigurationException(ex);
            }
        } else {
            addTitlesToServiceIdentification(serviceIdentification, defaultLanguage);
            addAbstractsToServiceIdentification(serviceIdentification, defaultLanguage);
            serviceIdentification.setAccessConstraints(Arrays.asList(this.constraints));
            serviceIdentification.setFees(this.fees);
            serviceIdentification.setServiceType(this.serviceType);
            serviceIdentification.setServiceTypeCodeSpace(this.serviceTypeCodeSpace);
            serviceIdentification.setVersions(ServiceOperatorRepository.getInstance().getSupportedVersions(
                    SosConstants.SOS));
            serviceIdentification.setKeywords(Arrays.asList(this.keywords));
        }
        return serviceIdentification;
    }

    @Override
    protected SosServiceIdentification create(String language) throws ConfigurationException {
        SosServiceIdentification serviceIdentification = create();
        if (!defaultLanguage.equals(language)) {
            addTitlesToServiceIdentification(serviceIdentification, language);
            addAbstractsToServiceIdentification(serviceIdentification, language);
        }
        return serviceIdentification;
    }
    
    private void addTitlesToServiceIdentification(SosServiceIdentification serviceIdentification, String language) {
        if (languageTitleMap.containsKey(language)) {
            serviceIdentification.clearTitles();
            for (String title : languageTitleMap.get(language)) {
                serviceIdentification.addTitle(new OwsLanguageString(title, language));
            }
        }
    }
    
    private void addAbstractsToServiceIdentification(SosServiceIdentification serviceIdentification, String language) {
        if (languageAbstractMap.containsKey(language)) {
            serviceIdentification.clearAbstracts();
            for (String abstrakt : languageAbstractMap.get(language)) {
                serviceIdentification.addAbstract(new OwsLanguageString(abstrakt, language));
            }
        }
    }
}
