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
package org.n52.sos.ds.hibernate.util.procedure.generator;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.n52.sos.cache.ContentCache;
import org.n52.sos.ds.I18NDAO;
import org.n52.sos.ds.hibernate.dao.AbstractObservationDAO;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.ProcedureDAO;
import org.n52.sos.ds.hibernate.entities.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.i18n.I18NDAORepository;
import org.n52.sos.i18n.LocalizedString;
import org.n52.sos.i18n.metadata.I18NProcedureMetadata;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.ProcedureDescriptionSettings;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.util.CollectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

/**
 * Abstract generator class for procedure descriptions
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.2.0
 *
 */
public abstract class AbstractHibernateProcedureDescriptionGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHibernateProcedureDescriptionGenerator.class);

    protected static final Joiner COMMA_JOINER = Joiner.on(",");

    private Locale locale = ServiceConfiguration.getInstance().getDefaultLanguage();

    public abstract SosProcedureDescription generateProcedureDescription(Procedure procedure, Locale i18n,
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

    protected boolean hasChildProcedure(String procedure) {
        return CollectionHelper.isNotEmpty(getCache().getChildProcedures(procedure, false, false));
    }

    /**
     * Set common values to procedure description
     *
     * @param procedure
     *            Hibernate procedure entity
     * @param abstractProcess
     *            SensorML process
     * @param session
     *            the session
     *
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    protected void setCommonData(Procedure procedure, AbstractFeature feature, Session session)
            throws OwsExceptionReport {
        String identifier = procedure.getIdentifier();

        addNameAndDescription(procedure, feature);

        feature.setIdentifier(identifier);

    }

    protected void addNameAndDescription(Procedure procedure, AbstractFeature feature) throws OwsExceptionReport {
        I18NDAO<I18NProcedureMetadata> i18nDAO = I18NDAORepository.getInstance().getDAO(I18NProcedureMetadata.class);
        Locale requestedLocale = getLocale();
        if (i18nDAO == null) {
            // no locale support
            ProcedureDAO featureDAO = new ProcedureDAO();
            feature.addName(featureDAO.getName(procedure));
            feature.setDescription(featureDAO.getDescription(procedure));
        } else {
            if (requestedLocale != null) {
                // specific locale was requested
                I18NProcedureMetadata i18n = i18nDAO.getMetadata(procedure.getIdentifier(), requestedLocale);
                Optional<LocalizedString> name = i18n.getName().getLocalization(requestedLocale);
                if (name.isPresent()) {
                    feature.addName(name.get().asCodeType());
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
                    feature.addName(name.asCodeType());
                }
                // choose always the description in the default locale
                Optional<LocalizedString> description = i18n.getDescription().getLocalization(defaultLocale);
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
    protected List<CodeType> createNames(Procedure procedure) {
        // locale
        return Lists.newArrayList(new CodeType(procedure.getIdentifier()));
    }

    protected List<String> createDescriptions(Procedure procedure, String[] observableProperties) {
        // locale
        String template = procedureSettings().getDescriptionTemplate();
        String identifier = procedure.getIdentifier();
        String obsProps = COMMA_JOINER.join(observableProperties);
        String type = procedure.isSpatial() ? "sensor system" : "procedure";
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
    AbstractObservation getExampleObservation(String identifier, String observableProperty, Session session)
            throws OwsExceptionReport {
        AbstractObservationDAO observationDAO = DaoFactory.getInstance().getObservationDAO();
        final Criteria c = observationDAO.getObservationCriteriaFor(identifier, observableProperty, session);
        c.setMaxResults(1);
        LOGGER.debug("QUERY getExampleObservation(identifier, observableProperty): {}",
                HibernateHelper.getSqlString(c));
        final AbstractObservation example = (AbstractObservation) c.uniqueResult();
        if (example == null) {
            LOGGER.debug(
                    "Could not receive example observation from database for procedure '{}' observing property '{}'.",
                    identifier, observableProperty);
        }
        return example;
    }

    @VisibleForTesting
    ServiceConfiguration getServiceConfig() {
        return ServiceConfiguration.getInstance();
    }

    @VisibleForTesting
    String[] getObservablePropertiesForProcedure(String identifier) {
        Set<String> props = getCache().getObservablePropertiesForProcedure(identifier);
        return props.toArray(new String[props.size()]);
    }

    @VisibleForTesting
    ProcedureDescriptionSettings procedureSettings() {
        return ProcedureDescriptionSettings.getInstance();
    }

    @VisibleForTesting
    ContentCache getCache() {
        return Configurator.getInstance().getCache();
    }

}
