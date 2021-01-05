/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.observation;

import java.util.Locale;

import javax.inject.Inject;

import org.n52.faroe.ConfigurationError;
import org.n52.faroe.Validation;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.binding.BindingRepository;
import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.convert.ConverterRepository;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.iceland.i18n.I18NSettings;
import org.n52.iceland.ogc.ows.OwsServiceMetadataRepository;
import org.n52.iceland.util.LocalizedProducer;
import org.n52.janmayen.i18n.LocaleHelper;
import org.n52.shetland.ogc.ows.OwsServiceProvider;
import org.n52.sos.cache.SosContentCache;
import org.n52.sos.ds.FeatureQueryHandler;
import org.n52.sos.service.profile.ProfileHandler;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.SosHelper;
import org.n52.svalbard.CodingSettings;
import org.n52.svalbard.decode.DecoderRepository;

public abstract class AbstractOmObservationCreatorContext {

    private OwsServiceMetadataRepository serviceMetadataRepository;
    private I18NDAORepository i18nr;
    private String tokenSeparator;
    private String tupleSeparator;
    private String decimalSeparator;
    private ProfileHandler profileHandler;
    private AdditionalObservationCreatorRepository additionalObservationCreatorRepository;
    private ContentCacheController contentCacheController;
    private FeatureQueryHandler featureQueryHandler;
    private ConverterRepository converterRepository;
    private GeometryHandler geometryHandler;
    private Locale defaultLanguage;
    private DecoderRepository decoderRepository;
    private BindingRepository bindingRepository;
    private SosHelper sosHelper;

    @Inject
    public AbstractOmObservationCreatorContext(
            OwsServiceMetadataRepository serviceMetadataRepository,
            I18NDAORepository i18nr,
            ProfileHandler profileHandler,
            SosHelper sosHelper,
            AdditionalObservationCreatorRepository additionalObservationCreatorRepository,
            ContentCacheController contentCacheController,
            FeatureQueryHandler featureQueryHandler,
            ConverterRepository converterRepository,
            GeometryHandler geometryHandler,
            DecoderRepository decoderRepository,
            BindingRepository bindingRepository) {
        super();
        this.serviceMetadataRepository = serviceMetadataRepository;
        this.i18nr = i18nr;
        this.profileHandler = profileHandler;
        this.sosHelper = sosHelper;
        this.additionalObservationCreatorRepository = additionalObservationCreatorRepository;
        this.contentCacheController = contentCacheController;
        this.featureQueryHandler = featureQueryHandler;
        this.converterRepository = converterRepository;
        this.geometryHandler = geometryHandler;
        this.decoderRepository = decoderRepository;
        this.bindingRepository = bindingRepository;
    }

    @Setting(CodingSettings.TOKEN_SEPARATOR)
    public void setTokenSeparator(final String separator) throws ConfigurationError {
        Validation.notNullOrEmpty("Token separator", separator);
        tokenSeparator = separator;
    }

    @Setting(CodingSettings.TUPLE_SEPARATOR)
    public void setTupleSeparator(final String separator) throws ConfigurationError {
        Validation.notNullOrEmpty("Tuple separator", separator);
        tupleSeparator = separator;
    }

    @Setting(CodingSettings.DECIMAL_SEPARATOR)
    public void setDecimalSeparator(final String separator) throws ConfigurationError {
        Validation.notNullOrEmpty("Decimal separator", separator);
        decimalSeparator = separator;
    }

    @Setting(I18NSettings.I18N_DEFAULT_LANGUAGE)
    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = LocaleHelper.decode(defaultLanguage);
    }

    /**
     * @return the serviceProvider
     */
    public LocalizedProducer<OwsServiceProvider> getServiceProvider(String service) {
        return this.serviceMetadataRepository.getServiceProviderFactory(service);
    }

    /**
     * @return the i18nr
     */
    public I18NDAORepository getI18nr() {
        return i18nr;
    }

    /**
     * @return the tokenSeparator
     */
    public String getTokenSeparator() {
        return tokenSeparator;
    }

    /**
     * @return the tupleSeparator
     */
    public String getTupleSeparator() {
        return tupleSeparator;
    }

    /**
     * @return the decimalSeparator
     */
    public String getDecimalSeparator() {
        return decimalSeparator;
    }

    /**
     * @return the profileHandler
     */
    public ProfileHandler getProfileHandler() {
        return profileHandler;
    }

    /**
     * @return the additionalObservationCreatorRepository
     */
    public AdditionalObservationCreatorRepository getAdditionalObservationCreatorRepository() {
        return additionalObservationCreatorRepository;
    }

    /**
     * @return the cache
     */
    public SosContentCache getCache() {
        return (SosContentCache) contentCacheController.getCache();
    }

    /**
     * @return the featureQueryHandler
     */
    public FeatureQueryHandler getFeatureQueryHandler() {
        return featureQueryHandler;
    }

    /**
     * @return the converterRepository
     */
    public ConverterRepository getConverterRepository() {
        return converterRepository;
    }

    /**
     * @return the geometryHandler
     */
    public GeometryHandler getGeometryHandler() {
        return geometryHandler;
    }

    /**
     * @return the serviceMetadataRepository
     */
    public OwsServiceMetadataRepository getServiceMetadataRepository() {
        return serviceMetadataRepository;
    }

    /**
     * @return the defaultLanguage
     */
    public Locale getDefaultLanguage() {
        return defaultLanguage;
    }

    /**
     * @return the serviceURL
     */
    public String getServiceURL() {
        return sosHelper.getServiceURL();
    }

    /**
     * @return the decoderRepository
     */
    public DecoderRepository getDecoderRepository() {
        return decoderRepository;
    }

    public BindingRepository getBindingRepository() {
        return bindingRepository;
    }

}
