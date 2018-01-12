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
package org.n52.sos.ds.procedure.generator;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;

import org.hibernate.Session;
import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.i18n.I18NDAO;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.iceland.i18n.metadata.I18NProcedureMetadata;
import org.n52.iceland.service.ServiceConfiguration;
import org.n52.io.request.IoParameters;
import org.n52.io.request.RequestSimpleParameterSet;
import org.n52.janmayen.i18n.LocalizedString;
import org.n52.series.db.DataAccessException;
import org.n52.series.db.beans.PhenomenonEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.dao.DbQuery;
import org.n52.series.db.dao.PhenomenonDao;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.CodeType;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.cache.SosContentCache;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.ProcedureDescriptionSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Abstract generator class for procedure descriptions
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.2.0
 *
 */
public abstract class AbstractProcedureDescriptionGenerator implements ProcedureDescriptionGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractProcedureDescriptionGenerator.class);

    protected static final Joiner COMMA_JOINER = Joiner.on(",");

    private Locale locale;
    private I18NDAORepository i18NDAORepository;
    private ContentCacheController cacheController;

    public AbstractProcedureDescriptionGenerator(I18NDAORepository i18NDAORepository,
            ContentCacheController cacheController) {
        this.i18NDAORepository = i18NDAORepository;
        this.cacheController = cacheController;

    }

    public abstract SosProcedureDescription<?> generateProcedureDescription(ProcedureEntity procedure, Locale i18n,
            Session session) throws OwsExceptionReport;

    protected void setLocale(Locale i18n) {
        this.locale = i18n;
    }

    protected Locale getLocale() {
        return locale;
    }

    protected boolean isSetLocale() {
        return locale != null;
    }

    protected void setI18NDAORepository(I18NDAORepository i18NDAORepository) {
        this.i18NDAORepository = i18NDAORepository;
    }

    protected I18NDAORepository getI18NDAORepository() {
        return i18NDAORepository;
    }

    protected boolean isSetI18NDAORepository() {
        return i18NDAORepository != null;
    }

    protected boolean hasChildProcedure(String procedure) {
        return CollectionHelper.isNotEmpty(getCache().getChildProcedures(procedure, false, false));
    }

    /**
     * Set common values to procedure description
     *
     * @param procedure
     *            Hibernate procedure entity
     * @param feature
     *            SensorML process
     * @param session
     *            the session
     *
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    protected void setCommonData(ProcedureEntity procedure, AbstractFeature feature, Session session)
            throws OwsExceptionReport {
        String identifier = procedure.getIdentifier();
        addNameAndDescription(procedure, feature);
        feature.setIdentifier(identifier);
    }

    protected void addNameAndDescription(ProcedureEntity procedure, AbstractFeature feature) throws OwsExceptionReport {
        if (isSetI18NDAORepository()) {
            I18NDAO<I18NProcedureMetadata> i18nDAO = getI18NDAORepository().getDAO(I18NProcedureMetadata.class);
            Locale requestedLocale = getLocale();
            if (i18nDAO == null) {
                feature.addName(procedure.getName());
                feature.setDescription(procedure.getDescription());
            } else {
                if (requestedLocale != null) {
                    // specific locale was requested
                    I18NProcedureMetadata i18n = i18nDAO.getMetadata(procedure.getIdentifier(), requestedLocale);
                    Optional<LocalizedString> name = i18n.getName().getLocalization(requestedLocale);
                    if (name.isPresent()) {
                        if (name.isPresent()) {
                            feature.addName(new CodeType(name.get()));
                        }
                    }
                    Optional<LocalizedString> description = i18n.getDescription().getLocalization(requestedLocale);
                    if (description.isPresent()) {
                        feature.setDescription(description.get().getText());
                    }
                } else {
                    Locale defaultLocale = ServiceConfiguration.getInstance().getDefaultLanguage();
                    final I18NProcedureMetadata i18n;
                    if (ServiceConfiguration.getInstance().isShowAllLanguageValues()) {
                        // load all names
                        i18n = i18nDAO.getMetadata(procedure.getIdentifier());
                    } else {
                        // load only name in default locale
                        i18n = i18nDAO.getMetadata(procedure.getIdentifier(), defaultLocale);
                    }
                    for (LocalizedString name : i18n.getName()) {
                        // either all or default only
                        feature.addName(new CodeType(name));
                    }
                    // choose always the description in the default locale
                    Optional<LocalizedString> description = i18n.getDescription().getLocalization(defaultLocale);
                    if (description.isPresent()) {
                        feature.setDescription(description.get().getText());
                    }
                }
            }
        } else {
            feature.addName(procedure.getName());
            feature.setDescription(procedure.getDescription());
        }
    }

    /**
     * Create a names collection for procedure description
     *
     * @param procedure
     *            Hibernate procedure entity
     *
     * @return Collection with names
     */
    protected List<CodeType> createNames(ProcedureEntity procedure) {
        // locale
        return Lists.newArrayList(new CodeType(procedure.getIdentifier()));
    }

    protected List<String> createDescriptions(ProcedureEntity procedure, String[] observableProperties) {
        // locale
        String template = procedureSettings().getDescriptionTemplate();
        String identifier = procedure.getIdentifier();
        String obsProps = COMMA_JOINER.join(observableProperties);
        String type = "procedure";
        return Lists.newArrayList(String.format(template, type, identifier, obsProps));
    }

    @VisibleForTesting
    ServiceConfiguration getServiceConfig() {
        return ServiceConfiguration.getInstance();
    }

    protected List<PhenomenonEntity> getObservablePropertiesForProcedure(ProcedureEntity procedure, Session session)
            throws OwsExceptionReport {
        try {
            PhenomenonDao dao = new PhenomenonDao(session);
            return dao.getAllInstances(createDbQuery(procedure));
        } catch (DataAccessException e) {
            throw new NoApplicableCodeException().causedBy(e).withMessage(
                    "Error while querying observable properties for sensor description!");
        }
    }

    protected TreeSet<String> getIdentifierList(Collection<PhenomenonEntity> observableProperties) {
        TreeSet<String> set = Sets.newTreeSet();
        for (PhenomenonEntity entity : observableProperties) {
            set.add(entity.getIdentifier());
        }
        return set;
    }

    private DbQuery createDbQuery(ProcedureEntity procedure) {
        Map<String, String> map = Maps.newHashMap();
        map.put(IoParameters.PROCEDURES, Long.toString(procedure.getId()));
        return new DbQuery(IoParameters.createFromSingleValueMap(map));
    }

    @VisibleForTesting
    ProcedureDescriptionSettings procedureSettings() {
        return ProcedureDescriptionSettings.getInstance();
    }

    @VisibleForTesting
    SosContentCache getCache() {
        return (SosContentCache) this.cacheController.getCache();
    }

}
