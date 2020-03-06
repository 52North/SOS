/**
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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

import org.hibernate.Session;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.iso.gmd.LocalisedCharacterString;
import org.n52.sos.iso.gmd.PT_FreeText;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.SosServiceProvider;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.CollectionHelper;
import org.n52.svalbard.inspire.base2.Contact;
import org.n52.svalbard.inspire.base2.RelatedParty;
import org.n52.svalbard.inspire.ompr.InspireOMPRConstants;
import org.n52.svalbard.inspire.ompr.Process;

/**
 * Generator class for WaterML 2.0 procedure descriptions
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.2.0
 *
 */
public class HibernateProcedureDescriptionGeneratorFactoryInspireOmpr30
        implements HibernateProcedureDescriptionGeneratorFactory {

    private static final List<HibernateProcedureDescriptionGeneratorFactoryKeyType> GENERATOR_KEY_TYPES =
            CollectionHelper
                    .list(new HibernateProcedureDescriptionGeneratorFactoryKeyType(InspireOMPRConstants.NS_OMPR_30),
                          new HibernateProcedureDescriptionGeneratorFactoryKeyType("http://inspire.ec.europa.eu/featureconcept/Process"));

    @Override
    public List<HibernateProcedureDescriptionGeneratorFactoryKeyType> getHibernateProcedureDescriptionGeneratorFactoryKeyTypes() {
        return GENERATOR_KEY_TYPES;
    }

    @Override
    public SosProcedureDescription create(Procedure procedure, Locale i18n, Session session)
            throws OwsExceptionReport {
        return new HibernateProcedureDescriptionGeneratorInspireOmpr30().generateProcedureDescription(procedure, i18n,
                session);
    }

    private class HibernateProcedureDescriptionGeneratorInspireOmpr30
            extends AbstractHibernateProcedureDescriptionGenerator {

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
        public Process generateProcedureDescription(Procedure p, Locale i18n, Session session)
                throws OwsExceptionReport {
            setLocale(i18n);
            final Process process = new Process();
            setCommonData(p, process, session);
            // process.setType();
            addResponsibleParty(process);
            return process;
        }

        private void addResponsibleParty(Process process) throws OwsExceptionReport {
            SosServiceProvider serviceProvider = Configurator.getInstance().getServiceProvider();
            RelatedParty responsibleParty = new RelatedParty();
            if (serviceProvider.hasIndividualName()) {
                responsibleParty.setIndividualName(createPT_FreeText(serviceProvider.getIndividualName()));
            }
            if (serviceProvider.hasName()) {
                responsibleParty.setOrganisationName(createPT_FreeText(serviceProvider.getName()));
            }
            if (serviceProvider.hasPositionName()) {
                responsibleParty.setPositionName(createPT_FreeText(serviceProvider.getPositionName()));
            }
            responsibleParty.setContact(createContact(serviceProvider));
        }

        private Contact createContact(SosServiceProvider serviceProvider) {
            Contact contact = new Contact();
//            if (serviceProvider.hasAdministrativeArea()) {
//                responsibleParty.setOrganisationName(organisationName);
//            }
//            if (serviceProvider.hasCity()) {
//               responsibleParty.setOrganisationName(organisationName);
//            }
//            if (serviceProvider.hasCountry()) {
//                responsibleParty.setOrganisationName(organisationName);
//            }
//            if (serviceProvider.hasDeliveryPoint()) {
//                responsibleParty.setOrganisationName(organisationName);
//            }
//            if (serviceProvider.hasPostalCode()) {
//                responsibleParty.setOrganisationName(organisationName);
//            }
            if (serviceProvider.hasMailAddress()) {
                contact.setElectronicMailAddress(serviceProvider.getMailAddress());
            }
            if (serviceProvider.hasPhone()) {
                contact.addTelephoneVoice(serviceProvider.getPhone());
            }
            if (serviceProvider.hasSite()) {
                contact.setWebsite(serviceProvider.getSite());
            }
            
            return contact;
        }
    }

    protected PT_FreeText createPT_FreeText(String value) {
        return new PT_FreeText().addTextGroup(createLocalisedCharacterString(value));
    }

    protected LocalisedCharacterString createLocalisedCharacterString(String value) {
        return new LocalisedCharacterString(value);
    }
}
