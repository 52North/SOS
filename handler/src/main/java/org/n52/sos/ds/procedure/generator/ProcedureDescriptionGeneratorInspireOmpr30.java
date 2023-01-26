/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import org.hibernate.Session;
import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.sensorweb.server.db.old.dao.DbQueryFactory;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.shetland.inspire.base2.Contact;
import org.n52.shetland.inspire.ompr.InspireOMPRConstants;
import org.n52.shetland.inspire.ompr.Process;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.ows.OwsAddress;
import org.n52.shetland.ogc.ows.OwsContact;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.service.ProcedureDescriptionSettings;
import org.n52.sos.service.profile.ProfileHandler;
import org.n52.sos.util.GeometryHandler;

public class ProcedureDescriptionGeneratorInspireOmpr30 extends AbstractProcedureDescriptionGenerator {

    public static final Set<ProcedureDescriptionGeneratorKey> GENERATOR_KEY_TYPES =
            CollectionHelper.set(new ProcedureDescriptionGeneratorKey(InspireOMPRConstants.NS_OMPR_30),
                    new ProcedureDescriptionGeneratorKey(InspireOMPRConstants.FEATURE_CONCEPT_PROCESS));

    public ProcedureDescriptionGeneratorInspireOmpr30(ProfileHandler profileHandler, GeometryHandler geometryHandler,
            I18NDAORepository i18ndaoRepository, ContentCacheController cacheController,
            ProcedureDescriptionSettings procedureSettings, DbQueryFactory dbQueryFactory) {
        super(i18ndaoRepository, cacheController, procedureSettings, dbQueryFactory);
    }

    @Override
    public Set<ProcedureDescriptionGeneratorKey> getKeys() {
        return Collections.unmodifiableSet(GENERATOR_KEY_TYPES);
    }

    /**
     * Generate procedure description from Hibernate procedure entity if no description (file, XML text) is
     * available
     *
     * @param procedure
     *            Hibernate procedure entity
     * @param session
     *            the session
     *
     * @return Generated procedure description
     *
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    public SosProcedureDescription<AbstractFeature> generateProcedureDescription(ProcedureEntity procedure,
            Locale i18n, Session session) throws OwsExceptionReport {
        setLocale(i18n);
        final Process process = new Process();
        setCommonData(procedure, process, session);
        // process.setType();
        addResponsibleParty(process);
        return new SosProcedureDescription<AbstractFeature>(process);
    }

    private void addResponsibleParty(Process process) throws OwsExceptionReport {
        // SosServiceProvider serviceProvider = Configurator.getInstance().ge;
        // RelatedParty responsibleParty = new RelatedParty();
        // if (serviceProvider.hasIndividualName()) {
        // responsibleParty.setIndividualName(createPT_FreeText(serviceProvider.getIndividualName()));
        // }
        // if (serviceProvider.hasName()) {
        // responsibleParty.setOrganisationName(createPT_FreeText(serviceProvider.getName()));
        // }
        // if (serviceProvider.hasPositionName()) {
        // responsibleParty.setPositionName(createPT_FreeText(serviceProvider.getPositionName()));
        // }
        // responsibleParty.setContact(createContact(serviceProvider));
    }

    private Contact createContact(OwsContact owsContact) {
        Contact contact = new Contact();
        // if (serviceProvider.hasAdministrativeArea()) {
        // responsibleParty.setOrganisationName(organisationName);
        // }
        // if (serviceProvider.hasCity()) {
        // responsibleParty.setOrganisationName(organisationName);
        // }
        // if (serviceProvider.hasCountry()) {
        // responsibleParty.setOrganisationName(organisationName);
        // }
        // if (serviceProvider.hasDeliveryPoint()) {
        // responsibleParty.setOrganisationName(organisationName);
        // }
        // if (serviceProvider.hasPostalCode()) {
        // responsibleParty.setOrganisationName(organisationName);
        // }
        if (owsContact.getAddress().isPresent()) {
            OwsAddress owsAddress = owsContact.getAddress().get();
            if (!owsAddress.getElectronicMailAddress().isEmpty()) {
                contact.setElectronicMailAddress(owsAddress.getElectronicMailAddress().iterator().next());
            }
        }
        if (owsContact.getPhone().isPresent()) {
            for (String v : owsContact.getPhone().get().getVoice()) {
                contact.addTelephoneVoice(v);
            }

        }
        if (owsContact.getOnlineResource().isPresent() && owsContact.getOnlineResource().get().getHref().isPresent()) {
            contact.setWebsite(owsContact.getOnlineResource().get().getHref().toString());
        }

        return contact;
    }

    private static Set<ProcedureDescriptionGeneratorKey> getGeneratorKeyTypes() {
        return GENERATOR_KEY_TYPES;
    }

}
