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
package org.n52.svalbard.decode.inspire;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.decode.AbstractXmlDecoder;
import org.n52.sos.decode.DecoderKey;
import org.n52.sos.exception.ows.concrete.UnsupportedDecoderInputException;
import org.n52.sos.iso.gmd.PT_FreeText;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.w3c.Nillable;
import org.n52.svalbard.inspire.ad.AddressRepresentation;
import org.n52.svalbard.inspire.base2.Contact;
import org.n52.svalbard.inspire.base2.RelatedParty;
import org.n52.svalbard.inspire.ompr.InspireOMPRConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import eu.europa.ec.inspire.schemas.base2.x20.ContactType;
import eu.europa.ec.inspire.schemas.base2.x20.ContactType.TelephoneFacsimile;
import eu.europa.ec.inspire.schemas.base2.x20.ContactType.TelephoneVoice;
import eu.europa.ec.inspire.schemas.base2.x20.RelatedPartyType;

public class RelatedPartyTypeDecoder extends AbstractXmlDecoder<RelatedParty> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RelatedPartyTypeDecoder.class);

    private static final Set<DecoderKey> DECODER_KEYS = CodingHelper.decoderKeysForElements(
            InspireOMPRConstants.NS_OMPR_30, RelatedPartyType.class);

    @Override
    public Set<DecoderKey> getDecoderKeyTypes() {
        return Collections.unmodifiableSet(DECODER_KEYS);
    }

    @Override
    public RelatedParty decode(XmlObject xmlObject)
            throws OwsExceptionReport, UnsupportedDecoderInputException {
        if (xmlObject instanceof RelatedPartyType) {
            RelatedPartyType rpt = (RelatedPartyType) xmlObject;
            RelatedParty relatedParty = new RelatedParty();
            relatedParty.setContact(parseContact(rpt));
            relatedParty.setIndividualName((PT_FreeText)CodingHelper.decodeXmlElement(rpt.getIndividualName()));
            relatedParty.setOrganisationName((PT_FreeText)CodingHelper.decodeXmlElement(rpt.getOrganisationName()));
            relatedParty.setPositionName((PT_FreeText)CodingHelper.decodeXmlElement(rpt.getPositionName()));
            relatedParty.setRole(parseRole(rpt));
            return relatedParty;
        }
        throw new UnsupportedDecoderInputException(this, xmlObject);
    }

    private Contact parseContact(RelatedPartyType rpt) throws OwsExceptionReport {
        ContactType ct = rpt.getContact().getContact();
        Contact contact = new Contact();
        contact.setAddress(parseAddress(ct));
        contact.setContactInstructions(parseContactInstructions(ct));
        contact.setElectronicMailAddress(parseElectronicMailAddress(ct));
        contact.setTelephoneFacsimile(parseTelephoneFacsimile(ct));
        contact.setTelephoneVoice(parseTelephoneVoice(ct));
        contact.setWebsite(parseWebsite(ct));
        return contact;
    }

    private Nillable<AddressRepresentation> parseAddress(ContactType ct) {
        return Nillable.<AddressRepresentation>nil();
    }

    private Nillable<PT_FreeText> parseContactInstructions(ContactType ct) throws OwsExceptionReport {
        if (ct.isNilContactInstructions()) {
            if (ct.getContactInstructions().isSetNilReason()) {
                return Nillable.<PT_FreeText>nil(ct.getContactInstructions().getNilReason().toString());
            }
            return Nillable.<PT_FreeText>nil();
        }
        return Nillable.<PT_FreeText>of((PT_FreeText)CodingHelper.decodeXmlElement(ct.getContactInstructions()));
    }

    private Nillable<String> parseElectronicMailAddress(ContactType ct) {
        if (ct.isNilElectronicMailAddress()) {
            if (ct.getElectronicMailAddress().isSetNilReason()) {
                return Nillable.<String>nil(ct.getElectronicMailAddress().getNilReason().toString());
            }
            return Nillable.<String>nil();
        }
        return Nillable.<String>of(ct.getElectronicMailAddress().getStringValue());
    }

    private Nillable<List<String>> parseTelephoneFacsimile(ContactType ct) {
        List<String> list = Lists.newArrayList();
        for (TelephoneFacsimile tf : ct.getTelephoneFacsimileArray()) {
            if (tf.isNil() && tf.isSetNilReason()) {
                return Nillable.<List<String>>nil(tf.getNilReason().toString());
            } else {
                list.add(tf.getStringValue());
            }
        }
        return Nillable.of(list);
    }

    private Nillable<List<String>> parseTelephoneVoice(ContactType ct) {
        List<String> list = Lists.newArrayList();
        for (TelephoneVoice tv : ct.getTelephoneVoiceArray()) {
            if (tv.isNil() && tv.isSetNilReason()) {
                return Nillable.<List<String>>nil(tv.getNilReason().toString());
            } else {
                list.add(tv.getStringValue());
            }
        }
        return Nillable.of(list);
    }

    private Nillable<String> parseWebsite(ContactType ct) {
        if (ct.isNilWebsite()) {
            if (ct.getWebsite().isSetNilReason()) {
                return Nillable.<String>nil(ct.getWebsite().getNilReason().toString());
            }
            return Nillable.<String>nil();
        } else {
            return Nillable.<String>present(ct.getWebsite().getStringValue());
        }
    }

    private Set<ReferenceType> parseRole(RelatedPartyType rpt) throws OwsExceptionReport {
        Set<ReferenceType> set = Sets.newHashSet();
        for (net.opengis.gml.x32.ReferenceType rt : rpt.getRoleArray()) {
            set.add((ReferenceType)CodingHelper.decodeXmlElement(rt));
        }
        return set;
    }

}
