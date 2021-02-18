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
package org.n52.sos.ds.hibernate.util.procedure.generator;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import org.hibernate.Session;
import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.i18n.I18NDAORepository;
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
import org.n52.sos.ds.hibernate.dao.DaoFactory;

public class HibernateProcedureDescriptionGeneratorInspireOmpr30
        extends AbstractHibernateProcedureDescriptionGenerator {

    public static final Set<HibernateProcedureDescriptionGeneratorKey> GENERATOR_KEY_TYPES =
            CollectionHelper.set(new HibernateProcedureDescriptionGeneratorKey(InspireOMPRConstants.NS_OMPR_30));

    public HibernateProcedureDescriptionGeneratorInspireOmpr30(
            DaoFactory daoFactory, I18NDAORepository i18NDAORepository, ContentCacheController cacheController) {
        super(daoFactory, i18NDAORepository, cacheController);
    }

    @Override
    public Set<HibernateProcedureDescriptionGeneratorKey> getKeys() {
        return Collections.unmodifiableSet(GENERATOR_KEY_TYPES);
    }

    /**
     * Generate procedure description from Hibernate procedure entity if no
     * description (file, XML text) is available
     *
     * @param p
     *            Hibernate procedure entity
     * @param session
     *            the session
     *
     * @return Generated procedure description
     *
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    public SosProcedureDescription<AbstractFeature> generateProcedureDescription(ProcedureEntity p, Locale i18n,
            Session session) throws OwsExceptionReport {
        setLocale(i18n);
        final Process process = new Process();
        setCommonData(p, process, session);
        // process.setType();
        addResponsibleParty(process);
        return new SosProcedureDescription<AbstractFeature>(process);
    }

    private void addResponsibleParty(Process process) throws OwsExceptionReport {
        // SosServiceProvider serviceProvider =
        // Configurator.getInstance().getServiceProvider();
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
}