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
package org.n52.sos.convert;

import java.util.ArrayList;
import java.util.List;

import org.n52.shetland.inspire.ad.AddressRepresentation;
import org.n52.shetland.inspire.base.Identifier;
import org.n52.shetland.inspire.base2.Contact;
import org.n52.shetland.inspire.base2.DocumentCitation;
import org.n52.shetland.inspire.base2.RelatedParty;
import org.n52.shetland.inspire.ompr.ProcessParameter;
import org.n52.shetland.iso.gmd.LocalisedCharacterString;
import org.n52.shetland.iso.gmd.PT_FreeText;
import org.n52.shetland.ogc.gml.CodeType;
import org.n52.shetland.ogc.gml.CodeWithAuthority;
import org.n52.shetland.ogc.gml.ReferenceType;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.sensorML.SmlContact;
import org.n52.shetland.ogc.sensorML.SmlResponsibleParty;
import org.n52.shetland.ogc.sensorML.elements.AbstractSmlDocumentation;
import org.n52.shetland.ogc.sensorML.elements.SmlClassifier;
import org.n52.shetland.ogc.sensorML.elements.SmlDocumentation;
import org.n52.shetland.ogc.sensorML.elements.SmlDocumentationList;
import org.n52.shetland.ogc.sensorML.elements.SmlDocumentationListMember;
import org.n52.shetland.ogc.sensorML.elements.SmlIdentifier;
import org.n52.shetland.w3c.Nillable;
import org.n52.shetland.w3c.xlink.Reference;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public abstract class AbstractInspireOmpr30SensorMLConverter
        extends
        ProcedureDescriptionConverter {

    private static final String PROCEDURE_NAME = "procedureName";
    private static final String INSPIRE_ID = "inspireId";

    protected List<DocumentCitation> convertDocumentationToDocumentationCitation(
            List<AbstractSmlDocumentation> documentations) {
        List<DocumentCitation> documentationCitations = Lists.newArrayList();
        for (AbstractSmlDocumentation documentation : documentations) {
            if (documentation instanceof SmlDocumentationList) {
                for (SmlDocumentationListMember member : ((SmlDocumentationList) documentation).getMember()) {
                    documentationCitations.add(convertDocumentationCitationToDocumentation(member.getDocumentation()));
                }
            } else if (documentation instanceof SmlDocumentation) {
                documentationCitations
                        .add(convertDocumentationCitationToDocumentation((SmlDocumentation) documentation));
            }
        }
        return documentationCitations;
    }

    private DocumentCitation convertDocumentationCitationToDocumentation(SmlDocumentation documentation) {
        DocumentCitation documentCitation = new DocumentCitation();
        if (documentation.isSetDescription()) {
            documentCitation.addName(documentation.getDescription());
        }
        if (documentation.isSetDate()) {
            documentCitation.setDate(documentation.getDate().getTimePosition().getTime());
        }
        if (documentation.isSeOnlineResource()) {
            documentCitation.addLink(documentation.getOnlineResource());
        }
        return documentCitation;
    }

    protected List<AbstractSmlDocumentation> convertDocumentationCitationToDocumentation(
            List<DocumentCitation> documentationCitations) {
        List<AbstractSmlDocumentation> smlDocumentations = Lists.newArrayList();
        for (DocumentCitation documentationCitation : documentationCitations) {
            SmlDocumentation smlDocumentation = new SmlDocumentation();
            if (documentationCitation.isSetName()) {
                smlDocumentation.setDescription(documentationCitation.getFirstName().getValue());
            }
            if (documentationCitation.isSetDate()) {
                smlDocumentation.setDate(new TimeInstant(documentationCitation.getDate().get()));
            }
            if (documentationCitation.isSetLinks()) {
                for (Nillable<String> link : documentationCitation.getLinks()) {
                    if (link.isPresent()) {
                        smlDocumentation.setOnlineResource(link.get());
                    }
                }
            }
            smlDocumentations.add(smlDocumentation);
        }
        return smlDocumentations;
    }

    public SmlIdentifier convertInspireIdToIdentification(Identifier inspireId) {
        SmlIdentifier identifier = new SmlIdentifier();
        identifier.setName(INSPIRE_ID);
        identifier.setValue(inspireId.getLocalId());
        identifier.setCodeSpace(inspireId.getCodeSpace());
        return identifier;
    }

    public CodeWithAuthority convertIdentificationToInspireId(List<SmlIdentifier> identifications) {
        for (SmlIdentifier smlIdentifier : identifications) {
            if (smlIdentifier.isSetName() && INSPIRE_ID.equals(smlIdentifier.getName())) {
                return new CodeWithAuthority(smlIdentifier.getValue(), smlIdentifier.getCodeSpace());
            }
        }
        return null;
    }

    protected SmlIdentifier convertNameToIdentification(CodeType firstName) {
        SmlIdentifier identifier = new SmlIdentifier();
        identifier.setName(PROCEDURE_NAME);
        identifier.setValue(firstName.getValue());
        return identifier;
    }

    protected CodeType convertIdentifierToName(List<SmlIdentifier> identifications) {
        for (SmlIdentifier smlIdentifier : identifications) {
            if (smlIdentifier.isSetName() && PROCEDURE_NAME.equals(smlIdentifier.getName())) {
                return new CodeType(smlIdentifier.getValue());
            }
        }
        return null;
    }

    protected List<SmlClassifier> convertProcessParametersToClassifiers(List<ProcessParameter> processParameters) {
        List<SmlClassifier> classifiers = Lists.newArrayList();
        for (ProcessParameter processParameter : processParameters) {
            SmlClassifier classifier = new SmlClassifier();
            classifier.setName(processParameter.getDescription());
            if (processParameter.getName().isSetTitle()) {
                classifier.setValue(processParameter.getName().getTitle());
            } else if (processParameter.getName().isSetHref()) {
                classifier.setValue(processParameter.getName().getHref());
            }
            classifiers.add(classifier);
        }
        return classifiers;
    }

    protected List<ProcessParameter> convertClassifiersToProcessParameters(List<SmlClassifier> classifications) {
        List<ProcessParameter> processParameters = Lists.newArrayList();
        for (SmlClassifier classifier : classifications) {
            ProcessParameter processParameter = new ProcessParameter();
            processParameter.setDescription(classifier.getName());
            processParameter.setName(new ReferenceType("", classifier.getValue()));
            processParameters.add(processParameter);
        }
        return processParameters;
    }

    protected List<SmlContact> convertResponsiblePartiesToContacts(List<RelatedParty> responsibleParties) {
        List<SmlContact> contacts = Lists.newArrayList();
        for (RelatedParty relatedParty : responsibleParties) {
            SmlResponsibleParty smlResponsibleParty = new SmlResponsibleParty();
            if (relatedParty.isSetIndividualName()) {
                smlResponsibleParty.setIndividualName(
                        relatedParty.getIndividualName().get().getTextGroup().iterator().next().getValue());
            }
            if (relatedParty.isSetOrganisationName()) {
                smlResponsibleParty.setOrganizationName(
                        relatedParty.getOrganisationName().get().getTextGroup().iterator().next().getValue());
            }
            if (relatedParty.isSetPositionName()) {
                smlResponsibleParty.setPositionName(
                        relatedParty.getPositionName().get().getTextGroup().iterator().next().getValue());
            }
            if (relatedParty.isSetRole()) {
                Nillable<Reference> next = relatedParty.getRoles().iterator().next();
                if (next.isPresent()) {
                    if (next.get().getTitle().isPresent()) {
                        smlResponsibleParty.setRole(next.get().getTitle().get());
                    } else if (next.get().getHref().isPresent()) {
                        smlResponsibleParty.setRole(next.get().getHref().get().toString());
                    }
                }
            }
            if (relatedParty.isSetContact()) {
                Contact contact = relatedParty.getContact().get();
                // if (contact.getAddress().isPresent()) {
                // AddressRepresentation addressRepresentation =
                // contact.getAddress().get();
                // TODO
                // }
                if (contact.getContactInstructions().isPresent()) {
                    smlResponsibleParty.setContactInstructions(
                            contact.getContactInstructions().get().getTextGroup().iterator().next().getValue());
                }
                if (contact.getElectronicMailAddress().isPresent()) {
                    smlResponsibleParty.setEmail(contact.getElectronicMailAddress().get());
                }
                if (contact.getTelephoneFacsimile().isPresent()) {
                    smlResponsibleParty.setPhoneFax(toList(contact.getTelephoneFacsimile().get()));
                }
                if (contact.getTelephoneVoice().isPresent()) {
                    smlResponsibleParty.setPhoneVoice(toList(contact.getTelephoneVoice().get()));
                }
                if (contact.getWebsite().isPresent()) {
                    smlResponsibleParty.setOnlineResource(Lists.newArrayList(contact.getWebsite().get()));
                }
            }
            contacts.add(smlResponsibleParty);
        }
        return contacts;
    }

    protected List<RelatedParty> convertContactsToResponsibleParties(List<SmlContact> contacts) {
        List<RelatedParty> relatedParties = Lists.newArrayList();
        for (SmlContact contact : contacts) {
            if (contact instanceof SmlResponsibleParty) {
                SmlResponsibleParty smlResponsibleParty = (SmlResponsibleParty) contact;
                RelatedParty relatedParty = new RelatedParty();
                if (smlResponsibleParty.isSetIndividualName()) {
                    relatedParty.setIndividualName(getPTFreeText(smlResponsibleParty.getIndividualName()));
                }
                if (smlResponsibleParty.isSetOrganizationName()) {
                    relatedParty.setOrganisationName(getPTFreeText(smlResponsibleParty.getOrganizationName()));
                }
                if (smlResponsibleParty.isSetPositionName()) {
                    relatedParty.setPositionName(getPTFreeText(smlResponsibleParty.getPositionName()));
                }
                if (smlResponsibleParty.isSetRole()) {
                    relatedParty.addRole(Nillable.of(new Reference().setTitle(smlResponsibleParty.getRole())));
                }
                if (smlResponsibleParty.isSetPhone() || smlResponsibleParty.isSetPhoneFax()) {
                    Contact contactOmpr = new Contact();
                    contactOmpr.setAddress(checkAddressRepresentation(smlResponsibleParty));
                    if (smlResponsibleParty.isSetContactInstructions()) {
                        contactOmpr
                                .setContactInstructions(getPTFreeText(smlResponsibleParty.getContactInstructions()));
                    }
                    if (smlResponsibleParty.isSetEmail()) {
                        contactOmpr.setElectronicMailAddress(smlResponsibleParty.getEmail());
                    }
                    if (smlResponsibleParty.isSetPhone()) {
                        contactOmpr.setTelephoneVoice(toNillableValueList(smlResponsibleParty.getPhoneVoice()));
                    }
                    if (smlResponsibleParty.isSetPhoneFax()) {
                        contactOmpr.setTelephoneFacsimile(toNillableValueList(smlResponsibleParty.getPhoneFax()));
                    }
                    if (smlResponsibleParty.isSetOnlineResources()) {
                        contactOmpr.setWebsite(smlResponsibleParty.getOnlineResources().iterator().next());
                    }
                    relatedParty.setContact(contactOmpr);
                }
                relatedParties.add(relatedParty);
            }
        }
        return relatedParties;
    }

    private Nillable<AddressRepresentation> checkAddressRepresentation(SmlResponsibleParty smlResponsibleParty) {
        // AddressRepresentation addressRepresentation = new
        // AddressRepresentation();
        // TODO
        return Nillable.<AddressRepresentation> nil();
    }

    private PT_FreeText getPTFreeText(String value) {
        return new PT_FreeText().addTextGroup(new LocalisedCharacterString(value));
    }

    private List<String> toList(List<Nillable<String>> list) {
        List<String> l = new ArrayList<>();
        for (Nillable<String> s : list) {
            if (s.isPresent()) {
                l.add(s.get());
            }
        }
        return l;
    }

    private List<Nillable<String>> toNillableValueList(List<String> list) {
        List<Nillable<String>> l = new ArrayList<>();
        for (String s : list) {
            if (Strings.isNullOrEmpty(s)) {
                l.add(Nillable.<String> nil());
            } else {
                l.add(Nillable.of(s));
            }
        }
        return l;
    }

}
