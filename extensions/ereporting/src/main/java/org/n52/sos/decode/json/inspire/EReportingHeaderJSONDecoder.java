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
package org.n52.sos.decode.json.inspire;

import java.net.URI;

import org.n52.sos.decode.json.JSONDecoder;
import org.n52.sos.exception.ows.concrete.DateTimeParseException;
import org.n52.sos.inspire.aqd.Address;
import org.n52.sos.inspire.aqd.Contact;
import org.n52.sos.inspire.aqd.EReportingChange;
import org.n52.sos.inspire.aqd.EReportingHeader;
import org.n52.sos.inspire.aqd.GeographicalName;
import org.n52.sos.inspire.aqd.InspireID;
import org.n52.sos.inspire.aqd.Pronunciation;
import org.n52.sos.inspire.aqd.RelatedParty;
import org.n52.sos.inspire.aqd.Spelling;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.AQDJSONConstants;
import org.n52.sos.util.Functions;
import org.n52.sos.util.Nillable;
import org.n52.sos.util.Reference;
import org.n52.sos.util.Referenceable;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Function;

public class EReportingHeaderJSONDecoder extends JSONDecoder<EReportingHeader> {

    public EReportingHeaderJSONDecoder() {
        super(EReportingHeader.class);
    }

    @Override
    public EReportingHeader decodeJSON(JsonNode node, boolean validate)
            throws OwsExceptionReport {
        EReportingHeader header = new EReportingHeader();
        header
                .setChange(parseReportingChange(node
                                .path(AQDJSONConstants.CHANGE)));
        header.setInspireID(parseInspireID(node
                .path(AQDJSONConstants.INSPIRE_ID)));
        header.setReportingAuthority(parseRelatedParty(node
                .path(AQDJSONConstants.REPORTING_AUTHORITY)));
        header.setReportingPeriod(parseReferenceableTime(node
                .path(AQDJSONConstants.REPORTING_PERIOD)));
        for (JsonNode child : node.path(AQDJSONConstants.CONTENT)) {
            header.addContent(parseReferenceableFeature(child));
        }
        for (JsonNode child : node.path(AQDJSONConstants.DELETE)) {
            header.addDelete(parseReferenceableFeature(child));
        }
        return header;
    }

    private EReportingChange parseReportingChange(JsonNode node) {
        boolean changed = node.path(AQDJSONConstants.CHANGED).asBoolean();
        String description = node.path(AQDJSONConstants.DESCRIPTION).textValue();
        return new EReportingChange(changed, description);
    }

    private InspireID parseInspireID(JsonNode node) {
        InspireID inspireID = new InspireID();
        inspireID
                .setNamespace(node.path(AQDJSONConstants.NAMESPACE).textValue());
        inspireID.setLocalId(node.path(AQDJSONConstants.LOCAL_ID).textValue());
        inspireID.setVersionId(parseNillableString(node
                .path(AQDJSONConstants.VERSION_ID)));
        return inspireID;
    }

    private Nillable<String> parseNillableString(JsonNode node) {
        return parseNillable(node).transform(new Function<JsonNode, String>() {
            @Override
            public String apply(JsonNode input) {
                return input.textValue();
            }
        });
    }

    private Nillable<JsonNode> parseNillable(JsonNode node) {
        if (node.isMissingNode() || node.isNull()) {
            return Nillable.absent();
        } else if (node.isObject() && node.path(AQDJSONConstants.NIL)
                   .asBoolean()) {
            return Nillable.nil(node.path(AQDJSONConstants.REASON).textValue());
        }
        return Nillable.of(node);
    }

    private Referenceable<JsonNode> parseReferenceable(
            JsonNode node) {
        Nillable<JsonNode> nillable = parseNillable(node);

        if (nillable.isAbsent() || nillable.isNil()) {
            return Referenceable.of(nillable);
        }

        if (node.has(AQDJSONConstants.HREF)) {
            return Referenceable.of(parseReference(node));
        }

        return Referenceable.of(node);
    }

    private Referenceable<Time> parseReferenceableTime(JsonNode node) {
        return parseReferenceable(node)
                .transform(new Function<JsonNode, Time>() {

                    @Override
                    public Time apply(JsonNode node) {
                        try {
                            return parseTime(node);
                        } catch (DateTimeParseException ex) {
                            throw new RuntimeException(ex);
                        }
                    }

                });
    }

    private RelatedParty parseRelatedParty(JsonNode node) {
        RelatedParty relatedParty = new RelatedParty();
        relatedParty.setContact(parseNillable(node
                .path(AQDJSONConstants.CONTACT))
                .transform(new Function<JsonNode, Contact>() {
                    @Override
                    public Contact apply(JsonNode input) {
                        return parseContact(input);
                    }

                }));
        relatedParty.setIndividualName(parseNillableString(node
                .path(AQDJSONConstants.INDIVIDUAL_NAME)));
        relatedParty.setOrganisationName(parseNillableString(node
                .path(AQDJSONConstants.ORGANISATION_NAME)));
        relatedParty.setPositionName(parseNillableString(node
                .path(AQDJSONConstants.POSITION_NAME)));
        for (JsonNode n : node.path(AQDJSONConstants.ROLES)) {
            relatedParty.addRole(parseNillableReference(n));
        }
        return relatedParty;
    }

    protected Nillable<Reference> parseNillableReference(JsonNode node) {
        return parseNillable(node)
                .transform(new Function<JsonNode, Reference>() {

                    @Override
                    public Reference apply(JsonNode node) {
                        return parseReference(node);
                    }
                });
    }

    private Contact parseContact(JsonNode node) {
        Contact contact = new Contact();

        contact.setAddress(parseNillable(node.path(AQDJSONConstants.ADDRESS))
                .transform(new Function<JsonNode, Address>() {
                    @Override
                    public Address apply(JsonNode node) {
                        return parseAddress(node);
                    }

                }));
        contact.setContactInstructions(parseNillableString(node
                .path(AQDJSONConstants.CONTACT_INSTRUCTIONS)));
        contact.setElectronicMailAddress(parseNillableString(node
                .path(AQDJSONConstants.ELECTRONIC_MAIL_ADDRESS)));
        contact.setHoursOfService(parseNillableString(node
                .path(AQDJSONConstants.HOURS_OF_SERVICE)));
        contact.setWebsite(parseNillableString(node
                .path(AQDJSONConstants.WEBSITE)));
        for (JsonNode n : node.path(AQDJSONConstants.TELEPHONE_FACSIMILE)) {
            contact.addTelephoneFacsimile(parseNillableString(n));
        }
        for (JsonNode n : node.path(AQDJSONConstants.TELEPHONE_VOICE)) {
            contact.addTelephoneVoice(parseNillableString(n));
        }
        return contact;
    }

    private Referenceable<AbstractFeature> parseReferenceableFeature(
            JsonNode child) {
        return parseReferenceable(child)
                .transform(new Function<JsonNode, AbstractFeature>() {

                    @Override
                    public AbstractFeature apply(JsonNode node) {
                        return parseAbstractFeature(node);
                    }
                });
    }

    private Address parseAddress(JsonNode node) {
        Address address = new Address();
        address.setAddressFeature(parseNillableReference(node
                .path(AQDJSONConstants.ADDRESS_FEATURE)));
        address.setPostCode(parseNillableString(node
                .path(AQDJSONConstants.POST_CODE)));
        for (JsonNode n : node.path(AQDJSONConstants.ADDRESS_AREAS)) {
            address.addAddressArea(parseNillableGeographicalName(n));
        }
        for (JsonNode n : node.path(AQDJSONConstants.ADMIN_UNITS)) {
            address.addAdminUnit(parseGeographicalName(n));
        }
        for (JsonNode n : node.path(AQDJSONConstants.LOCATOR_DESIGNATORS)) {
            address.addLocatorDesignator(n.textValue());
        }
        for (JsonNode n : node.path(AQDJSONConstants.LOCATOR_NAMES)) {
            address.addLocatorName(parseGeographicalName(n));
        }
        for (JsonNode n : node.path(AQDJSONConstants.POST_NAMES)) {
            address.addPostName(parseNillableGeographicalName(n));
        }
        for (JsonNode n : node.path(AQDJSONConstants.THOROUGHFARES)) {
            address.addThoroughfare(parseNillableGeographicalName(n));
        }
        return address;
    }

    protected Nillable<GeographicalName> parseNillableGeographicalName(
            JsonNode node) {
        return parseNillable(node)
                .transform(new Function<JsonNode, GeographicalName>() {

                    @Override
                    public GeographicalName apply(JsonNode node) {
                        return parseGeographicalName(node);
                    }

                });
    }

    private GeographicalName parseGeographicalName(JsonNode node) {
        GeographicalName geographicalName = new GeographicalName();
        geographicalName.setGrammaticalGender(parseNillableCodeType(node
                .path(AQDJSONConstants.GRAMMATICAL_GENDER)));
        geographicalName.setGrammaticalNumber(parseNillableCodeType(node
                .path(AQDJSONConstants.GRAMMATICAL_NUMBER)));
        geographicalName.setLanguage(parseNillableString(node
                .path(AQDJSONConstants.LANGUAGE)));
        geographicalName.setNameStatus(parseNillableCodeType(node
                .path(AQDJSONConstants.NAME_STATUS)));
        geographicalName.setNativeness(parseNillableCodeType(node
                .path(AQDJSONConstants.NATIVENESS)));
        geographicalName.setSourceOfName(parseNillableString(node
                .path(AQDJSONConstants.SOURCE_OF_NAME)));
        geographicalName.setPronunciation(parseNillable(node
                .path(AQDJSONConstants.PRONUNCIATION))
                .transform(new Function<JsonNode, Pronunciation>() {
                    @Override
                    public Pronunciation apply(JsonNode node) {
                        return parsePronunciation(node);
                    }

                }));
        geographicalName.setSpelling(parseNillable(node
                .path(AQDJSONConstants.SPELLING))
                .transform(new Function<JsonNode, Spelling>() {

                    @Override
                    public Spelling apply(JsonNode node) {
                        return parseSpelling(node);
                    }

                }));
        return geographicalName;
    }

    private Pronunciation parsePronunciation(JsonNode node) {
        Pronunciation pronunciation = new Pronunciation();
        pronunciation
                .setIPA(parseNillableString(node.path(AQDJSONConstants.IPA)));
        pronunciation.setSoundLink(parseNillableString(node
                .path(AQDJSONConstants.SOUND_LINK)).transform(Functions
                        .stringToURI()));
        return pronunciation;
    }

    private Spelling parseSpelling(JsonNode node) {
        Spelling spelling = new Spelling();
        spelling.setScript(parseNillableString(node
                .path(AQDJSONConstants.SCRIPT)));
        spelling.setText(node.path(AQDJSONConstants.TEXT).textValue());
        spelling.setTransliterationScheme(parseNillableString(node
                .path(AQDJSONConstants.SCRIPT)));
        return spelling;
    }

    protected Nillable<CodeType> parseNillableCodeType(JsonNode node) {
        return parseNillable(node)
                .transform(new Function<JsonNode, CodeType>() {

                    @Override
                    public CodeType apply(JsonNode node) {
                        return parseCodeType(node);
                    }
                });
    }

    private AbstractFeature parseAbstractFeature(JsonNode node) {
        /* TODO implement .parseAbstractFeature() */
        throw new UnsupportedOperationException(".parseAbstractFeature() not yet implemented");
    }

    private Reference parseReference(
            JsonNode node) {
        Reference ref = new Reference();
        ref.setHref(URI.create(node.path(AQDJSONConstants.HREF).textValue()));
        ref.setActuate(node.path(AQDJSONConstants.ACTUATE).textValue());
        ref.setArcrole(node.path(AQDJSONConstants.ARCROLE).textValue());
        ref.setRemoteSchema(node.path(AQDJSONConstants.REMOTE_SCHEMA)
                .textValue());
        ref.setRole(node.path(AQDJSONConstants.ROLE).textValue());
        ref.setShow(node.path(AQDJSONConstants.SHOW).textValue());
        ref.setTitle(node.path(AQDJSONConstants.TITLE).textValue());
        ref.setType(node.path(AQDJSONConstants.TYPE).textValue());
        return ref;
    }

}
