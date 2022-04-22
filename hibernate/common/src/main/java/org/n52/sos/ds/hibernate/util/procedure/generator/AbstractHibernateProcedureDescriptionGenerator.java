/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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
package org.n52.sos.ds.hibernate.util.procedure.generator;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.n52.faroe.Validation;
import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.iceland.i18n.I18NSettings;
import org.n52.iceland.i18n.metadata.I18NProcedureMetadata;
import org.n52.janmayen.i18n.LocaleHelper;
import org.n52.janmayen.i18n.LocalizedString;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.CodeType;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.cache.SosContentCache;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.ProcedureDAO;
import org.n52.sos.ds.hibernate.dao.i18n.HibernateI18NDAO;
import org.n52.sos.ds.hibernate.dao.observation.AbstractObservationDAO;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.service.ProcedureDescriptionSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Abstract generator class for procedure descriptions
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.2.0
 *
 */
@Configurable
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public abstract class AbstractHibernateProcedureDescriptionGenerator
        implements HibernateProcedureDescriptionGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHibernateProcedureDescriptionGenerator.class);

    private final DaoFactory daoFactory;

    private final I18NDAORepository i18NDAORepository;

    private final ContentCacheController cacheController;

    private Locale defaultLanguage;

    private boolean showAllLanguages;

    private String descriptionTemplate;

    private Locale locale;

    public AbstractHibernateProcedureDescriptionGenerator(DaoFactory daoFactory, I18NDAORepository i18NDAORepository,
            ContentCacheController cacheController) {
        this.daoFactory = daoFactory;
        this.i18NDAORepository = i18NDAORepository;
        this.cacheController = cacheController;
    }

    @Setting(ProcedureDescriptionSettings.DESCRIPTION_TEMPLATE)
    public void setDescriptionTemplate(String descriptionTemplate) {
        Validation.notNullOrEmpty(ProcedureDescriptionSettings.DESCRIPTION_TEMPLATE, descriptionTemplate);
        this.descriptionTemplate = descriptionTemplate;
    }

    @Setting(I18NSettings.I18N_DEFAULT_LANGUAGE)
    public void setDefaultLanguage(String defaultLanguage) {
        Validation.notNullOrEmpty("Default language as three character string", defaultLanguage);
        this.defaultLanguage = LocaleHelper.decode(defaultLanguage);
    }

    @Setting(I18NSettings.I18N_SHOW_ALL_LANGUAGE_VALUES)
    public void setShowAllLanguageValues(boolean showAllLanguages) {
        this.showAllLanguages = showAllLanguages;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    protected Locale getDefaultLocale() {
        return this.defaultLanguage;
    }

    protected DaoFactory getDaoFactory() {
        return this.daoFactory;
    }

    protected SosContentCache getCache() {
        return (SosContentCache) this.cacheController.getCache();
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
        addNameAndDescription(procedure, feature, session);
        feature.setIdentifier(identifier);
    }

    protected void addNameAndDescription(ProcedureEntity procedure, AbstractFeature feature, Session session)
            throws OwsExceptionReport {
        HibernateI18NDAO<I18NProcedureMetadata> i18nDAO =
                (HibernateI18NDAO<I18NProcedureMetadata>) i18NDAORepository.getDAO(I18NProcedureMetadata.class);
        Locale requestedLocale = getLocale();
        if (i18nDAO == null) {
            // no locale support
            ProcedureDAO dao = daoFactory.getProcedureDAO();
            feature.addName(dao.getName(procedure));
            feature.setDescription(dao.getDescription(procedure));
        } else {
            if (requestedLocale != null) {
                // specific locale was requested
                I18NProcedureMetadata i18n = i18nDAO.getMetadata(procedure.getIdentifier(), requestedLocale, session);
                Optional<LocalizedString> name = i18n.getName().getLocalization(requestedLocale);
                if (name.isPresent()) {
                    feature.addName(new CodeType(name.get()));
                }
                Optional<LocalizedString> description = i18n.getDescription().getLocalization(requestedLocale);
                if (description.isPresent()) {
                    feature.setDescription(description.get().getText());
                }
            } else {
                final I18NProcedureMetadata i18n;
                if (this.showAllLanguages) {
                    // load all names
                    i18n = i18nDAO.getMetadata(procedure.getIdentifier(), session);
                } else {
                    // load only name in default locale
                    i18n = i18nDAO.getMetadata(procedure.getIdentifier(), defaultLanguage, session);
                }
                for (LocalizedString name : i18n.getName()) {
                    // either all or default only
                    feature.addName(new CodeType(name));
                }
                // choose always the description in the default locale
                Optional<LocalizedString> description = i18n.getDescription().getLocalization(defaultLanguage);
                if (description.isPresent()) {
                    feature.setDescription(description.get().getText());
                }
            }
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
        String template = this.descriptionTemplate;
        String identifier = procedure.getIdentifier();
        String obsProps = Arrays.stream(observableProperties).collect(Collectors.joining(","));
        // String type = procedure.isSpatial() ? "sensor system" : "procedure";
        String type = "procedure";
        return Lists.newArrayList(String.format(template, type, identifier, obsProps));
    }

    /**
     * Get example observation for output list creation
     *
     * @param identifier
     *            Procedure identifier
     * @param observableProperty
     *            ObservableProperty identifier
     * @param session
     *            the session
     *
     * @return Example observation
     *
     * @throws OwsExceptionReport
     *             If an error occurs.
     */
    @VisibleForTesting
    DataEntity<?> getExampleObservation(String identifier, String observableProperty, Session session)
            throws OwsExceptionReport {
        AbstractObservationDAO observationDAO = daoFactory.getObservationDAO();
        final Criteria c = observationDAO.getObservationCriteriaFor(identifier, observableProperty, session);
        c.setMaxResults(1);
        LOGGER.trace("QUERY getExampleObservation(identifier, observableProperty): {}",
                HibernateHelper.getSqlString(c));
        final DataEntity<?> example = (DataEntity) c.uniqueResult();
        if (example == null) {
            LOGGER.debug(
                    "Could not receive example observation from database for procedure '{}' observing property '{}'.",
                    identifier, observableProperty);
        }
        return example;
    }

    @VisibleForTesting
    String[] getObservablePropertiesForProcedure(String identifier) {
        SortedSet<String> props = new TreeSet<>();
        Set<String> obsProps = getCache().getObservablePropertiesForProcedure(identifier);
        for (String obsProp : obsProps) {
            if (getCache().getPublishedObservableProperties().contains(obsProp)) {
                props.add(obsProp);
            }
        }
        return props.toArray(new String[props.size()]);
    }
}
