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
package org.n52.svalbard.encode.inspire.base2;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.encode.AbstractXmlEncoder;
import org.n52.sos.encode.ClassToClassEncoderKey;
import org.n52.sos.encode.EncoderKey;
import org.n52.sos.encode.XmlEncoderKey;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.iso.gmd.GmdConstants;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.util.CodingHelper;
import org.n52.svalbard.inspire.base2.Contact;
import org.n52.svalbard.inspire.base2.InspireBase2Constants;
import org.n52.svalbard.inspire.base2.RelatedParty;
import org.n52.svalbard.inspire.ompr.InspireOMPRConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import eu.europa.ec.inspire.schemas.base2.x20.ContactType;
import eu.europa.ec.inspire.schemas.base2.x20.ContactType.TelephoneFacsimile;
import eu.europa.ec.inspire.schemas.base2.x20.ContactType.TelephoneVoice;
import eu.europa.ec.inspire.schemas.base2.x20.ContactType.Website;
import net.opengis.gml.x32.NilReasonType;
import eu.europa.ec.inspire.schemas.base2.x20.RelatedPartyType;

public class RelatedPartyTypeEncoder extends AbstractXmlEncoder<RelatedParty> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RelatedPartyTypeEncoder.class);

    private static final Set<EncoderKey> ENCODER_KEYS =
            Sets.newHashSet(new ClassToClassEncoderKey(RelatedPartyType.class, RelatedParty.class),
                    new XmlEncoderKey(InspireBase2Constants.NS_BASE2, RelatedParty.class));

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return Collections.unmodifiableSet(ENCODER_KEYS);
    }

    @Override
    public XmlObject encode(RelatedParty relatedParty, Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport, UnsupportedEncoderInputException {
        return createRelatedParty(relatedParty);
    }

    private XmlObject createRelatedParty(RelatedParty relatedParty) throws OwsExceptionReport {
        RelatedPartyType rpt = RelatedPartyType.Factory.newInstance();
        addContact(rpt, relatedParty);
        addIndividualName(rpt, relatedParty);
        addOrganisationName(rpt, relatedParty);
        addPositionName(rpt, relatedParty);
        addRole(rpt, relatedParty);
        return rpt;
    }

    private void addContact(RelatedPartyType rpt, RelatedParty relatedParty) {
        if (relatedParty.isSetContact()) {
            rpt.addNewContact().setContact(createContact(relatedParty.getContact()));
        } else {
            rpt.setNilContact();
        }
    }

    private ContactType createContact(Contact contact) {
        ContactType ct = ContactType.Factory.newInstance();
        ct.addNewAddress().setNil();
        if (contact.getAddress().isNil() && contact.getAddress().getNilReason().isPresent()) {
            ct.getAddress().setNilReason(contact.getAddress().getNilReason().get());
        }
        ct.addNewContactInstructions();
        if (contact.getElectronicMailAddress().isPresent()) {
            ct.addNewElectronicMailAddress().setStringValue(contact.getElectronicMailAddress().get());
        } else if (contact.getElectronicMailAddress().isNil()) {
            ct.addNewElectronicMailAddress().setNil();
            if (contact.getElectronicMailAddress().getNilReason().isPresent()) {
                ct.getElectronicMailAddress().setNilReason(contact.getElectronicMailAddress().getNilReason().get());
            }
        }
        if (contact.getTelephoneFacsimile().isPresent()) {
            for (String telephoneFacsimile : contact.getTelephoneFacsimile().get()) {
                ct.addNewTelephoneFacsimile().setStringValue(telephoneFacsimile);
            }
        } else if (contact.getTelephoneFacsimile().isNil()) {
            TelephoneFacsimile tf = ct.addNewTelephoneFacsimile();
            tf.setNil();
            if (contact.getTelephoneFacsimile().getNilReason().isPresent()) {
                tf.setNilReason(contact.getTelephoneFacsimile().getNilReason().get());
            }
        }
        if (contact.getTelephoneVoice().isPresent()) {
            for (String telephoneVoice : contact.getTelephoneVoice().get()) {
                ct.addNewTelephoneVoice().setStringValue(telephoneVoice);
            }
        } else if (contact.getTelephoneVoice().isNil()) {
            TelephoneVoice tv = ct.addNewTelephoneVoice();
            tv.setNil();
            if (contact.getTelephoneVoice().getNilReason().isPresent()) {
                tv.setNilReason(contact.getTelephoneVoice().getNilReason().get());
            }
        }
        if (contact.getWebsite().isPresent()) {
            ct.addNewWebsite().setStringValue(contact.getWebsite().get());
        } else if (contact.getWebsite().isNil()) {
            Website w = ct.addNewWebsite();
            w.setNil();
            if (contact.getWebsite().getNilReason().isPresent()) {
                w.setNilReason(contact.getWebsite().getNilReason().get());
            }
        }
        return ct;
    }

    private void addIndividualName(RelatedPartyType rpt, RelatedParty relatedParty) throws OwsExceptionReport {
        if (relatedParty.isSetIndividualName()) {
            rpt.addNewIndividualName().addNewPTFreeText().set(encodeGMD(relatedParty.getIndividualName()));
        }
    }

    private void addOrganisationName(RelatedPartyType rpt, RelatedParty relatedParty) throws OwsExceptionReport {
        if (relatedParty.isSetOrganisationName()) {
            rpt.addNewOrganisationName().addNewPTFreeText().set(encodeGMD(relatedParty.getOrganisationName()));
        }
    }

    private void addPositionName(RelatedPartyType rpt, RelatedParty relatedParty) throws OwsExceptionReport {
        if (relatedParty.isSetPositionName()) {
            rpt.addNewPositionName().addNewPTFreeText().set(encodeGMD(relatedParty.getPositionName()));
        }
    }

    private void addRole(RelatedPartyType rpt, RelatedParty relatedParty) throws OwsExceptionReport {
        if (relatedParty.isSetRole()) {
            for (ReferenceType role : relatedParty.getRole()) {
                rpt.addNewRole().set(encodeGML32(role));
            }
        }
    }

    protected static XmlObject encodeGMD(Object o) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(GmdConstants.NS_GMD, o);
    }

    protected static XmlObject encodeGMD(Object o, Map<HelperValues, String> helperValues) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(GmdConstants.NS_GMD, o, helperValues);
    }

}
