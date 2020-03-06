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
package org.n52.sos.decode;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.isotc211.x2005.gco.CharacterStringPropertyType;
import org.isotc211.x2005.gmd.CIAddressType;
import org.isotc211.x2005.gmd.CIContactPropertyType;
import org.isotc211.x2005.gmd.CIContactType;
import org.isotc211.x2005.gmd.CIOnlineResourceType;
import org.isotc211.x2005.gmd.CIResponsiblePartyDocument;
import org.isotc211.x2005.gmd.CIResponsiblePartyPropertyType;
import org.isotc211.x2005.gmd.CIResponsiblePartyType;
import org.isotc211.x2005.gmd.CITelephoneType;
import org.isotc211.x2005.gmd.LocalisedCharacterStringPropertyType;
import org.isotc211.x2005.gmd.PTFreeTextDocument;
import org.isotc211.x2005.gmd.PTFreeTextPropertyType;
import org.isotc211.x2005.gmd.PTFreeTextType;
import org.n52.sos.exception.ows.concrete.UnsupportedDecoderInputException;
import org.n52.sos.iso.gmd.GmdConstants;
import org.n52.sos.iso.gmd.LocalisedCharacterString;
import org.n52.sos.iso.gmd.PT_FreeText;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.Role;
import org.n52.sos.ogc.sensorML.SmlResponsibleParty;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.CollectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * {@link Decoder} class to decode ISO TC211 Geographic MetaData (GMD)
 * extensible markup language.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.2.0
 *
 */
public class Iso19139GmdDecoder implements Decoder<Object, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iso19139GmdDecoder.class);

    private static final Set<DecoderKey> DECODER_KEYS = CodingHelper.decoderKeysForElements(GmdConstants.NS_GMD,
            CIResponsiblePartyDocument.class, CIResponsiblePartyPropertyType.class, CIResponsiblePartyType.class,
            PTFreeTextPropertyType.class, PTFreeTextDocument.class, PTFreeTextType.class);

    public Iso19139GmdDecoder() {
        LOGGER.debug("Decoder for the following keys initialized successfully: {}!", Joiner.on(", ")
                .join(DECODER_KEYS));
    }

    @Override
    public Set<DecoderKey> getDecoderKeyTypes() {
        return Collections.unmodifiableSet(DECODER_KEYS);
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.emptySet();
    }

    @Override
    public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
        return Collections.emptyMap();
    }

    @Override
    public Object decode(Object element) throws OwsExceptionReport, UnsupportedDecoderInputException {
        if (element instanceof CIResponsiblePartyDocument) {
            return decodeCIResponsibleParty(((CIResponsiblePartyDocument) element).getCIResponsibleParty());
        } else if (element instanceof CIResponsiblePartyPropertyType) {
            return decodeCIResponsiblePartyPropertyType((CIResponsiblePartyPropertyType) element);
        } else if (element instanceof CIResponsiblePartyType) {
            return decodeCIResponsibleParty((CIResponsiblePartyType) element);
        } else if (element instanceof PTFreeTextDocument) {
            return decodePTFreeTextType(((PTFreeTextDocument)element).getPTFreeText());
        } else if (element instanceof PTFreeTextPropertyType) {
            return decodePTFreeTextType(((PTFreeTextPropertyType)element).getPTFreeText());
        } else if (element instanceof PTFreeTextType) {
            return decodePTFreeTextType((PTFreeTextType)element);
        } else {
            throw new UnsupportedDecoderInputException(this, element);
        }
    }

    private PT_FreeText decodePTFreeTextType(PTFreeTextType ptftt) {
        PT_FreeText ptFreeText = new PT_FreeText();
        for (LocalisedCharacterStringPropertyType lcspt : ptftt.getTextGroupArray()) {
            ptFreeText.addTextGroup(new LocalisedCharacterString(lcspt.getLocalisedCharacterString().getStringValue()));
        }
        return ptFreeText;
    }

    private Object decodeCIResponsiblePartyPropertyType(CIResponsiblePartyPropertyType element) throws OwsExceptionReport {
        if (element.isSetCIResponsibleParty()) {
            return decodeCIResponsibleParty(element.getCIResponsibleParty());
        } else if (element.isSetHref()) {
            SmlResponsibleParty responsibleParty = new SmlResponsibleParty();
            responsibleParty.setHref(element.getHref());
            if (element.isSetTitle()) {
                responsibleParty.setTitle(element.getTitle());
            }
            if (element.isSetRole()) {
                responsibleParty.setRole(element.getRole());
            }
            return responsibleParty;
        }
        throw new UnsupportedDecoderInputException(this, element);
    }

    private Object decodeCIResponsibleParty(CIResponsiblePartyType element) throws OwsExceptionReport {
        SmlResponsibleParty responsibleParty = new SmlResponsibleParty();
        if (element.isSetIndividualName()) {
            responsibleParty.setIndividualName(element.getIndividualName().getCharacterString());
        }
        if (element.isSetOrganisationName()) {
            responsibleParty.setOrganizationName(element.getOrganisationName().getCharacterString());
        }
        if (element.isSetPositionName()) {
            responsibleParty.setPositionName(element.getPositionName().getCharacterString());
        }
        if (element.isSetContactInfo()) {
            decodeContactInfo(element.getContactInfo(), responsibleParty);
        }
        if (element.getRole().isSetCIRoleCode()) {
            Object decodeXmlElement = CodingHelper.decodeXmlElement(element.getRole().getCIRoleCode());
            if (decodeXmlElement instanceof Role) {
                responsibleParty.setRole((Role) decodeXmlElement);
            }
        }
        return responsibleParty;
    }

    private void decodeContactInfo(CIContactPropertyType cicpt, SmlResponsibleParty responsibleParty) {
        if (cicpt.isSetCIContact()) {
            decodeContact(cicpt.getCIContact(), responsibleParty);
        }
    }

    private void decodeContact(CIContactType cic, SmlResponsibleParty responsibleParty) {
        if (cic.isSetAddress()) {
            decodeCiAddress(cic.getAddress().getCIAddress(), responsibleParty);
        }
        if (cic.isSetContactInstructions()) {
            responsibleParty.setContactInstructions(cic.getContactInstructions().getCharacterString());
        }
        if (cic.isSetHoursOfService()) {
            responsibleParty.setHoursOfService(cic.getHoursOfService().getCharacterString());
        }
        if (cic.isSetOnlineResource() && cic.getOnlineResource().isSetHref()) {
            responsibleParty.setOnlineResource(Lists.newArrayList(cic.getOnlineResource().getHref()));
        }
        if (cic.isSetPhone() && cic.getPhone().isSetCITelephone()) {
            decodePhone(cic.getPhone().getCITelephone(), responsibleParty);
        }
    }

    private void decodeCiAddress(CIAddressType ciat, SmlResponsibleParty responsibleParty) {
        if (ciat.isSetAdministrativeArea()) {
            responsibleParty.setAdministrativeArea(ciat.getAdministrativeArea().getCharacterString());
        }
        if (ciat.isSetCity()) {
            responsibleParty.setCity(ciat.getCity().getCharacterString());
        }
        if (ciat.isSetCountry()) {
            responsibleParty.setCountry(ciat.getCountry().getCharacterString());
        }
        if (ciat.isSetPostalCode()) {
            responsibleParty.setPostalCode(ciat.getPostalCode().getCharacterString());
        }
    }

    private void decodeOnlineResource(CIOnlineResourceType ciort, SmlResponsibleParty responsibleParty) {
        // TODO Auto-generated method stub
    }

    private void decodePhone(CITelephoneType citt, SmlResponsibleParty responsibleParty) {
        if (CollectionHelper.isNotNullOrEmpty(citt.getVoiceArray())) {
            responsibleParty.setPhoneVoice(characterStringPropertyTypeArrayToList(citt.getVoiceArray()));
        }
        if (CollectionHelper.isNotNullOrEmpty(citt.getFacsimileArray())) {
            responsibleParty.setPhoneFax(characterStringPropertyTypeArrayToList(citt.getFacsimileArray()));
        }

    }

    private List<String> characterStringPropertyTypeArrayToList(CharacterStringPropertyType[] array) {
        List<String> values = Lists.newArrayList();
        for (CharacterStringPropertyType cspt : array) {
            values.add(cspt.getCharacterString());
        }
        return values;
    }

}
