/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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
package org.n52.sos.ds.procedure;

import java.util.Locale;

import javax.inject.Inject;

import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.binding.BindingRepository;
import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.convert.ConverterRepository;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.iceland.i18n.I18NSettings;
import org.n52.iceland.ogc.ows.OwsServiceMetadataRepository;
import org.n52.iceland.service.operator.ServiceOperatorRepository;
import org.n52.janmayen.i18n.LocaleHelper;
import org.n52.sos.cache.SosContentCache;
import org.n52.sos.ds.procedure.generator.AbstractProcedureDescriptionGeneratorFactoryRepository;
import org.n52.sos.service.ProcedureDescriptionSettings;
import org.n52.sos.service.SosSettings;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.SosHelper;
import org.n52.svalbard.decode.DecoderRepository;

@Configurable
public class AbstractProcedureCreationContext {

    private DecoderRepository decoderRepository;
    private AbstractProcedureDescriptionGeneratorFactoryRepository factoryRepository;
    private String sensorDirectory;
    private I18NDAORepository i18nr;
    private ConverterRepository converterRepository;
    private GeometryHandler geometryHandler;
    private OwsServiceMetadataRepository serviceMetadataRepository;
    private BindingRepository bindingRepository;
    private boolean showAllLanguageValues;
    private ServiceOperatorRepository serviceOperatorRepository;
    private SosHelper sosHelper;
    private Locale defaultLocale;
    private ContentCacheController contentCacheController;
    private ProcedureDescriptionSettings procedureSettings;

    public AbstractProcedureCreationContext(
            OwsServiceMetadataRepository serviceMetadataRepository,
            DecoderRepository decoderRepository,
            AbstractProcedureDescriptionGeneratorFactoryRepository factoryRepository,
            I18NDAORepository i18nr,
            ConverterRepository converterRepository,
            GeometryHandler geometryHandler,
            BindingRepository bindingRepository,
            ServiceOperatorRepository serviceOperatorRepository,
            ContentCacheController contentCacheController,
            ProcedureDescriptionSettings procedureSettings) {
        this.serviceMetadataRepository = serviceMetadataRepository;
        this.decoderRepository = decoderRepository;
        this.factoryRepository = factoryRepository;
        this.i18nr = i18nr;
        this.converterRepository = converterRepository;
        this.geometryHandler = geometryHandler;
        this.bindingRepository = bindingRepository;
        this.serviceOperatorRepository = serviceOperatorRepository;
        this.contentCacheController = contentCacheController;
        this.procedureSettings = procedureSettings;
    }

    /**
     * @return the decoderRepository
     */
    public DecoderRepository getDecoderRepository() {
        return decoderRepository;
    }

    /**
     * @return the factoryRepository
     */
    public AbstractProcedureDescriptionGeneratorFactoryRepository getFactoryRepository() {
        return factoryRepository;
    }

    @Setting(SosSettings.SENSOR_DIRECTORY)
    public void setSensorDirectory(String dir) {
        this.sensorDirectory = dir;
    }

    public String getSensorDirectory() {
        return sensorDirectory;
    }

    @Setting(I18NSettings.I18N_SHOW_ALL_LANGUAGE_VALUES)
    public void setShowAllLanguageValues(boolean showAllLanguageValues) {
        this.showAllLanguageValues = showAllLanguageValues;
    }

    public boolean isShowAllLanguageValues() {
        return showAllLanguageValues;
    }

    @Setting(I18NSettings.I18N_DEFAULT_LANGUAGE)
    public void setDefaultLanguage(String defaultLocale) {
        this.defaultLocale = LocaleHelper.decode(defaultLocale);
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    @Inject
    public void setSosHelperL(SosHelper sosHelper) {
        this.sosHelper = sosHelper;
    }

    public String getServiceURL() {
        return sosHelper.getServiceURL();
    }

    /**
     * @return the i18nr
     */
    public I18NDAORepository getI18nr() {
        return i18nr;
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
     * @return the bindingRepository
     */
    public BindingRepository getBindingRepository() {
        return bindingRepository;
    }

    /**
     * @return the serviceOperatorRepository
     */
    public ServiceOperatorRepository getServiceOperatorRepository() {
        return serviceOperatorRepository;
    }

    /**
     * @return the contentCacheController
     */
    public SosContentCache getCache() {
        return (SosContentCache) contentCacheController.getCache();
    }

    /**
     * @return the contentCacheController
     */
    public ContentCacheController getContentCacheController() {
        return contentCacheController;
    }

    /**
     * @return the procedureSettings
     */
    public ProcedureDescriptionSettings getProcedureSettings() {
        return procedureSettings;
    }

}
