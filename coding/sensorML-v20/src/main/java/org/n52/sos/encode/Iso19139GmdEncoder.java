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
package org.n52.sos.encode;

import static org.n52.sos.util.CodingHelper.encoderKeysForElements;
import static org.n52.sos.util.CollectionHelper.union;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.isotc211.x2005.gco.CharacterStringPropertyType;
import org.isotc211.x2005.gco.CodeListValueType;
import org.isotc211.x2005.gco.RealPropertyType;
import org.isotc211.x2005.gco.UnitOfMeasurePropertyType;
import org.isotc211.x2005.gmd.AbstractMDIdentificationType;
import org.isotc211.x2005.gmd.CIAddressPropertyType;
import org.isotc211.x2005.gmd.CIAddressType;
import org.isotc211.x2005.gmd.CICitationPropertyType;
import org.isotc211.x2005.gmd.CICitationType;
import org.isotc211.x2005.gmd.CIContactPropertyType;
import org.isotc211.x2005.gmd.CIContactType;
import org.isotc211.x2005.gmd.CIDateType;
import org.isotc211.x2005.gmd.CIOnlineResourceDocument;
import org.isotc211.x2005.gmd.CIOnlineResourcePropertyType;
import org.isotc211.x2005.gmd.CIOnlineResourceType;
import org.isotc211.x2005.gmd.CIResponsiblePartyDocument;
import org.isotc211.x2005.gmd.CIResponsiblePartyPropertyType;
import org.isotc211.x2005.gmd.CIResponsiblePartyType;
import org.isotc211.x2005.gmd.CIRoleCodePropertyType;
import org.isotc211.x2005.gmd.CITelephonePropertyType;
import org.isotc211.x2005.gmd.CITelephoneType;
import org.isotc211.x2005.gmd.DQConformanceResultType;
import org.isotc211.x2005.gmd.DQDomainConsistencyDocument;
import org.isotc211.x2005.gmd.DQDomainConsistencyPropertyType;
import org.isotc211.x2005.gmd.DQDomainConsistencyType;
import org.isotc211.x2005.gmd.DQQuantitativeResultType;
import org.isotc211.x2005.gmd.DQResultPropertyType;
import org.isotc211.x2005.gmd.EXExtentDocument;
import org.isotc211.x2005.gmd.EXExtentPropertyType;
import org.isotc211.x2005.gmd.EXExtentType;
import org.isotc211.x2005.gmd.EXVerticalExtentDocument;
import org.isotc211.x2005.gmd.EXVerticalExtentPropertyType;
import org.isotc211.x2005.gmd.EXVerticalExtentType;
import org.isotc211.x2005.gmd.LocalisedCharacterStringPropertyType;
import org.isotc211.x2005.gmd.LocalisedCharacterStringType;
import org.isotc211.x2005.gmd.MDDataIdentificationDocument;
import org.isotc211.x2005.gmd.MDDataIdentificationPropertyType;
import org.isotc211.x2005.gmd.MDDataIdentificationType;
import org.isotc211.x2005.gmd.MDIdentificationPropertyType;
import org.isotc211.x2005.gmd.MDMetadataDocument;
import org.isotc211.x2005.gmd.MDMetadataPropertyType;
import org.isotc211.x2005.gmd.MDMetadataType;
import org.isotc211.x2005.gmd.PTFreeTextType;
import org.isotc211.x2005.gsr.SCCRSPropertyType;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.iso.GcoConstants;
import org.n52.sos.iso.gco.AbstractRole;
import org.n52.sos.iso.gco.Role;
import org.n52.sos.iso.gmd.AbstractMDIdentification;
import org.n52.sos.iso.gmd.CiAddress;
import org.n52.sos.iso.gmd.CiContact;
import org.n52.sos.iso.gmd.CiOnlineResource;
import org.n52.sos.iso.gmd.CiResponsibleParty;
import org.n52.sos.iso.gmd.CiTelephone;
import org.n52.sos.iso.gmd.EXExtent;
import org.n52.sos.iso.gmd.EXVerticalExtent;
import org.n52.sos.iso.gmd.GmdCitation;
import org.n52.sos.iso.gmd.GmdCitationDate;
import org.n52.sos.iso.gmd.GmdConformanceResult;
import org.n52.sos.iso.gmd.GmdConstants;
import org.n52.sos.iso.gmd.GmdDateType;
import org.n52.sos.iso.gmd.GmdDomainConsistency;
import org.n52.sos.iso.gmd.GmdQuantitativeResult;
import org.n52.sos.iso.gmd.GmlBaseUnit;
import org.n52.sos.iso.gmd.LocalisedCharacterString;
import org.n52.sos.iso.gmd.MDDataIdentification;
import org.n52.sos.iso.gmd.MDMetadata;
import org.n52.sos.iso.gmd.PT_FreeText;
import org.n52.sos.iso.gmd.ScCRS;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.SmlResponsibleParty;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.w3c.Nillable;
import org.n52.sos.w3c.SchemaLocation;
import org.n52.sos.w3c.xlink.Reference;
import org.n52.sos.w3c.xlink.Referenceable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3.x1999.xlink.ActuateType;
import org.w3.x1999.xlink.ShowType;
import org.w3.x1999.xlink.TypeType;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.opengis.gml.x32.AbstractCRSType;
import net.opengis.gml.x32.BaseUnitType;
import net.opengis.gml.x32.CodeType;

/**
 * {@link AbstractXmlEncoder} class to decode ISO TC211 Geographic MetaData
 * (GMD) extensible markup language.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.2.0
 *
 */
public class Iso19139GmdEncoder extends AbstractIso19139GcoEncoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iso19139GmdEncoder.class);

    private static final QName QN_GCO_DATE = new QName(GcoConstants.NS_GCO, "Date", GcoConstants.NS_GCO_PREFIX);

    private static final QName QN_GMD_CONFORMANCE_RESULT = new QName(GmdConstants.NS_GMD, "DQ_ConformanceResult",
            GmdConstants.NS_GMD_PREFIX);

    private static final QName QN_GMD_QUANTITATIVE_RESULT = new QName(GmdConstants.NS_GMD, "DQ_QuantitativeResult",
            GmdConstants.NS_GMD_PREFIX);

    private static final QName QN_GML_BASE_UNIT = new QName(GmlConstants.NS_GML_32, "BaseUnit",
            GmlConstants.NS_GML_PREFIX);

    @SuppressWarnings("unchecked")
    private static final Set<EncoderKey> ENCODER_KEYS = union(
            encoderKeysForElements(GmdConstants.NS_GMD, SmlResponsibleParty.class, GmdQuantitativeResult.class,
                    GmdConformanceResult.class, CiResponsibleParty.class, MDMetadata.class, PT_FreeText.class, CiOnlineResource.class
                    , EXExtent.class, EXVerticalExtent.class), 
            encoderKeysForElements(null, GmdQuantitativeResult.class, GmdConformanceResult.class));

    public Iso19139GmdEncoder() {
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!", Joiner.on(", ")
                .join(ENCODER_KEYS));
    }

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return Collections.unmodifiableSet(ENCODER_KEYS);
    }

    @Override
    public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
        return Collections.emptyMap();
    }

    @Override
    public void addNamespacePrefixToMap(final Map<String, String> nameSpacePrefixMap) {
        nameSpacePrefixMap.put(GmdConstants.NS_GMD, GmdConstants.NS_GMD_PREFIX);
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Sets.newHashSet(GmdConstants.GMD_SCHEMA_LOCATION);
    }

    @Override
    public XmlObject encode(Object element, Map<HelperValues, String> additionalValues) throws OwsExceptionReport,
            UnsupportedEncoderInputException {
        XmlObject encodedObject = null;
        // try {
        if (element instanceof SmlResponsibleParty) {
            encodedObject = encodeResponsibleParty((SmlResponsibleParty) element, additionalValues);
        } else if (element instanceof CiResponsibleParty) {
            encodedObject = encodeResponsibleParty((CiResponsibleParty) element, additionalValues);
        } else if (element instanceof MDMetadata) {
            encodedObject = encodeMDMetadata((MDMetadata) element, additionalValues);
        } else if (element instanceof MDDataIdentification) {
            encodedObject = encodeMDDataIdentification((MDDataIdentification) element, additionalValues);
        } else if (element instanceof PT_FreeText) {
            encodedObject = encodePTFreeText((PT_FreeText) element, additionalValues);
        } else if (element instanceof CiOnlineResource) {
            encodedObject = encodeCiOnlineResource((CiOnlineResource) element, additionalValues);
        } else if (element instanceof EXExtent) {
            encodedObject = encodeEXExtent((EXExtent) element, additionalValues);
        } else if (element instanceof EXVerticalExtent) {
            encodedObject = encodeEXVerticalExtent((EXVerticalExtent) element, additionalValues);
        } else {
            if (element instanceof GmdDomainConsistency) {
                encodedObject = encodeGmdDomainConsistency((GmdDomainConsistency)element, additionalValues);
            } else {
                throw new UnsupportedEncoderInputException(this, element);
            }
        }
        // } catch (final XmlException xmle) {
        // throw new NoApplicableCodeException().causedBy(xmle);
        // }
        if (LOGGER.isTraceEnabled() && encodedObject != null) {
            LOGGER.debug("Encoded object {} is valid: {}", encodedObject.schemaType().toString(),
                    XmlHelper.validateDocument(encodedObject));
        }
        return encodedObject;
    }

    private XmlObject encodeMDMetadata(MDMetadata mdMetadata, Map<HelperValues, String> additionalValues) throws OwsExceptionReport {
        if (mdMetadata.isSetSimpleAttrs()) {
            MDMetadataPropertyType mdmpt =
                    MDMetadataPropertyType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            mdmpt.setHref(mdMetadata.getSimpleAttrs().getHref());
            if (mdMetadata.getSimpleAttrs().isSetTitle()) {
                mdmpt.setTitle(mdMetadata.getSimpleAttrs().getTitle());
            }
            if (mdMetadata.getSimpleAttrs().isSetRole()) {
                mdmpt.setRole(mdMetadata.getSimpleAttrs().getRole());
            }
            return mdmpt;
        }
        MDMetadataType mdmt = MDMetadataType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        encodeAbstractObject(mdmt, mdMetadata);
        Map<HelperValues, String> av = Maps.newHashMap();
        av.put(HelperValues.PROPERTY_TYPE, "true");
        // add contacts
        for (CiResponsibleParty contact : mdMetadata.getContact()) {
            mdmt.addNewContact().set(encodeResponsibleParty(contact, av));
        }
        // add dateStamp
        mdmt.addNewDateStamp().setDateTime(mdMetadata.getDateStamp().toCalendar(null));
        // add identificationInfo
        for (AbstractMDIdentification identificationInfo : mdMetadata.getIdentificationInfo()) {
            if (identificationInfo.isSetSimpleAttrs()) {
                MDIdentificationPropertyType mdipt = mdmt.addNewIdentificationInfo();
                mdipt.setHref(identificationInfo.getSimpleAttrs().getHref());
                if (identificationInfo.getSimpleAttrs().isSetTitle()) {
                    mdipt.setTitle(identificationInfo.getSimpleAttrs().getTitle());
                }
                if (identificationInfo.getSimpleAttrs().isSetRole()) {
                    mdipt.setRole(identificationInfo.getSimpleAttrs().getRole());
                }
            } else {
                mdmt.addNewIdentificationInfo().addNewAbstractMDIdentification().set(encode(identificationInfo));
                // TODO substitution???
            }
        }
        // TODO all other optional elements if required
        if (additionalValues.containsKey(HelperValues.PROPERTY_TYPE)) {
            MDMetadataPropertyType mdmpt =
                    MDMetadataPropertyType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            mdmpt.setMDMetadata(mdmt);
            return mdmpt;
        } else if (additionalValues.containsKey(HelperValues.DOCUMENT)) {
            MDMetadataDocument mdmd =
                    MDMetadataDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            mdmd.setMDMetadata(mdmt);
            return mdmd;
        }
        return mdmt;
    }
    
    private void encodeIdentificationInfo(AbstractMDIdentificationType amdit, AbstractMDIdentification abstractMDIdentification) {
        encodeAbstractObject(amdit, abstractMDIdentification);
        // citation
        encodeCiCitation(amdit.addNewCitation(), abstractMDIdentification.getCitation());
        // abstract
        amdit.addNewAbstract().setCharacterString(abstractMDIdentification.getAbstrakt());
        // TODO all other optional elements if required
    }
    
    private void encodeCiCitation(CICitationPropertyType cicpt, GmdCitation citation) {
        if (citation.isSetSimpleAttrs()) {
            cicpt.setHref(citation.getSimpleAttrs().getHref());
            if (citation.getSimpleAttrs().isSetTitle()) {
                cicpt.setTitle(citation.getSimpleAttrs().getTitle());
            }
            if (citation.getSimpleAttrs().isSetRole()) {
                cicpt.setRole(citation.getSimpleAttrs().getRole());
            }
        } else {
            CICitationType cict = cicpt.addNewCICitation();
            cict.addNewTitle().setCharacterString(citation.getTitle());
            CIDateType cidt = cict.addNewDate().addNewCIDate();
            CodeListValueType clvt = cidt.addNewDateType().addNewCIDateTypeCode();
            GmdCitationDate gmdCitationDate = citation.getDate();
            GmdDateType gmdDateType = gmdCitationDate.getDateType();
            clvt.setCodeList(gmdDateType.getCodeList());
            clvt.setCodeListValue(gmdDateType.getCodeListValue());
            if (gmdDateType.getCodeSpace() != null && !gmdDateType.getCodeSpace().isEmpty()) {
                clvt.setCodeSpace(gmdDateType.getCodeSpace());
            }
            clvt.setStringValue(gmdDateType.getValue());
            XmlCursor newCursor = cidt.addNewDate().newCursor();
            newCursor.toNextToken();
            newCursor.beginElement(QN_GCO_DATE);
            newCursor.insertChars(gmdCitationDate.getDate());
            newCursor.dispose();
        }
    }

    private XmlObject encodeMDDataIdentification(MDDataIdentification mdDataIdentification,
            Map<HelperValues, String> additionalValues) {
        if (mdDataIdentification.isSetSimpleAttrs()) {
            MDDataIdentificationPropertyType mddipt =
                    MDDataIdentificationPropertyType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            mddipt.setHref(mdDataIdentification.getSimpleAttrs().getHref());
            if (mdDataIdentification.getSimpleAttrs().isSetTitle()) {
                mddipt.setTitle(mdDataIdentification.getSimpleAttrs().getTitle());
            }
            if (mdDataIdentification.getSimpleAttrs().isSetRole()) {
                mddipt.setRole(mdDataIdentification.getSimpleAttrs().getRole());
            }
            return mddipt;
        }
        MDDataIdentificationType mddit =
                MDDataIdentificationType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        encodeIdentificationInfo(mddit, mdDataIdentification);
        // language
        mddit.addNewLanguage().setCharacterString(mdDataIdentification.getLanguage());
        // TODO all other optional elements if required
        if (additionalValues.containsKey(HelperValues.PROPERTY_TYPE)) {
            MDDataIdentificationPropertyType mddipt =
                    MDDataIdentificationPropertyType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            mddipt.setMDDataIdentification(mddit);
            return mddipt;
        } else if (additionalValues.containsKey(HelperValues.DOCUMENT)) {
            MDDataIdentificationDocument mddid =
                    MDDataIdentificationDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            mddid.setMDDataIdentification(mddit);
            return mddit;
        }
        return mddit;
    }

    private XmlObject encodeResponsibleParty(CiResponsibleParty responsibleParty, Map<HelperValues, String> additionalValues) throws OwsExceptionReport {
        if (responsibleParty.isSetSimpleAttrs()) {
            CIResponsiblePartyPropertyType cirppt =
                    CIResponsiblePartyPropertyType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            cirppt.setHref(responsibleParty.getSimpleAttrs().getHref());
            if (responsibleParty.getSimpleAttrs().isSetTitle()) {
                cirppt.setTitle(responsibleParty.getSimpleAttrs().getTitle());
            }
            if (responsibleParty.getSimpleAttrs().isSetRole()) {
                cirppt.setRole(responsibleParty.getSimpleAttrs().getRole());
            }
            return cirppt;
        }
        CIResponsiblePartyType cirpt =
                CIResponsiblePartyType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        if (responsibleParty.isSetIndividualName()) {
            cirpt.addNewIndividualName().setCharacterString(responsibleParty.getIndividualName());
        }
        if (responsibleParty.isSetOrganizationName()) {
            cirpt.addNewOrganisationName().setCharacterString(responsibleParty.getOrganizationName());
        }
        if (responsibleParty.isSetPositionName()) {
            cirpt.addNewPositionName().setCharacterString(responsibleParty.getPositionName());
        }
        // set contact
        if (responsibleParty.isSetContactInfo()) {
            encodeContact(cirpt.addNewContactInfo(), responsibleParty.getContactInfo());
        }
        // set role
        encodeRole(cirpt.addNewRole(), responsibleParty.getRoleNillable());
        if (responsibleParty.isSetId()) {
            cirpt.setId(responsibleParty.getId());
        }
        if (responsibleParty.isSetUuid()) {
            cirpt.setUuid(responsibleParty.getUuid());
        }
        if (additionalValues.containsKey(HelperValues.PROPERTY_TYPE)) {
            CIResponsiblePartyPropertyType cirppt =
                    CIResponsiblePartyPropertyType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            cirppt.setCIResponsibleParty(cirpt);
            return cirppt;
        } else if (additionalValues.containsKey(HelperValues.DOCUMENT)) {
            CIResponsiblePartyDocument cirpd =
                    CIResponsiblePartyDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            cirpd.setCIResponsibleParty(cirpt);
            return cirpd;
        }
        return cirpt;
    }

    private XmlObject encodeResponsibleParty(SmlResponsibleParty responsibleParty,
            Map<HelperValues, String> additionalValues) throws OwsExceptionReport {
        if (responsibleParty.isSetHref()) {
            CIResponsiblePartyPropertyType cirppt =
                    CIResponsiblePartyPropertyType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            cirppt.setHref(responsibleParty.getHref());
            if (responsibleParty.isSetTitle()) {
                cirppt.setTitle(responsibleParty.getTitle());
            }
            if (responsibleParty.isSetRole()) {
                cirppt.setRole(responsibleParty.getRole());
            }
            return cirppt;
        }
        CIResponsiblePartyType cirpt =
                CIResponsiblePartyType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        if (responsibleParty.isSetIndividualName()) {
            cirpt.addNewIndividualName().setCharacterString(responsibleParty.getIndividualName());
        }
        if (responsibleParty.isSetOrganizationName()) {
            cirpt.addNewOrganisationName().setCharacterString(responsibleParty.getOrganizationName());
        }
        if (responsibleParty.isSetPositionName()) {
            cirpt.addNewPositionName().setCharacterString(responsibleParty.getPositionName());
        }
        // set contact
        encodeContact(cirpt.addNewContactInfo().addNewCIContact(), responsibleParty);
        // set role
        encodeRole(cirpt.addNewRole(), responsibleParty.getRoleObject());
        if (additionalValues.containsKey(HelperValues.PROPERTY_TYPE)) {
            CIResponsiblePartyPropertyType cirppt =
                    CIResponsiblePartyPropertyType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            cirppt.setCIResponsibleParty(cirpt);
            return cirppt;
        } else if (additionalValues.containsKey(HelperValues.DOCUMENT)) {
            CIResponsiblePartyDocument cirpd =
                    CIResponsiblePartyDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            cirpd.setCIResponsibleParty(cirpt);
            return cirpd;
        }
        return cirpt;
    }

    private void encodeContact(CIContactPropertyType cicpt, Referenceable<CiContact> referenceable) {
        if (referenceable.isReference()) {
            Reference reference = referenceable.getReference();
            if (reference.getActuate().isPresent()) {
                cicpt.setActuate(ActuateType.Enum.forString(reference.getActuate().get()));
            }
            if (reference.getArcrole().isPresent()) {
                cicpt.setHref(reference.getArcrole().get());
            }
            if (reference.getHref().isPresent()) {
                cicpt.setHref(reference.getHref().get().toString());
            }
            if (reference.getRole().isPresent()) {
                cicpt.setRole(reference.getRole().get());
            }
            if (reference.getShow().isPresent()) {
                cicpt.setShow(ShowType.Enum.forString(reference.getShow().get()));
            }
            if (reference.getTitle().isPresent()) {
                cicpt.setTitle(reference.getTitle().get());
            }
            if (reference.getType().isPresent()) {
                cicpt.setType(TypeType.Enum.forString(reference.getType().get()));
            }
        } else { 
            if (referenceable.isInstance()) {
                Nillable<CiContact> nillable = referenceable.getInstance();
                if (nillable.isPresent()) {
                    CiContact ciContact = referenceable.getInstance().get();
                    CIContactType cict = cicpt.addNewCIContact();
                    if (ciContact.getAddress() != null) {
                        encodeCiAddress(cict.addNewAddress(), ciContact.getAddress());
                    }
                    if (ciContact.getContactInstructionsNillable() != null) {
                        if (ciContact.getContactInstructionsNillable().isPresent()) {
                            cict.addNewContactInstructions().setCharacterString(ciContact.getContactInstructions());
                        } else if (ciContact.getContactInstructionsNillable().hasReason()) {
                            cict.addNewContactInstructions().setNilReason(ciContact.getContactInstructionsNillable().getNilReason().get());
                        }
                    }
                    if (ciContact.isSetHoursOfService()) {
                        if (ciContact.getHoursOfServiceNillable().isPresent()) {
                            cict.addNewHoursOfService().setCharacterString(ciContact.getHoursOfService());
                        } else if (ciContact.getHoursOfServiceNillable().hasReason()) {
                            cict.addNewHoursOfService().setNilReason(ciContact.getHoursOfServiceNillable().getNilReason().get());
                        }
                    }
                    if (ciContact.getOnlineResourceReferenceable() != null) {
                        encodeOnlineResource(cict.addNewOnlineResource(), ciContact.getOnlineResourceReferenceable());
                    }
                    if (ciContact.isSetPhone()) {
                        encodePhone(cict.addNewPhone(), ciContact.getPhone());
                    }
                } else {
                    if (nillable.hasReason()) {
                        cicpt.setNilReason(nillable.getNilReason().get());
                    }
                }
            }
        }
    }

    private void encodeContact(CIContactType cic, SmlResponsibleParty responsibleParty) {
        if (responsibleParty.isSetAddress()) {
            encodeCiAddress(cic.addNewAddress().addNewCIAddress(), responsibleParty);
        }
        if (responsibleParty.isSetContactInstructions()) {
            cic.addNewContactInstructions().setCharacterString(responsibleParty.getContactInstructions());
        }
        if (responsibleParty.isSetHoursOfService()) {
            cic.addNewHoursOfService().setCharacterString(responsibleParty.getHoursOfService());
        }
        if (responsibleParty.isSetOnlineResources()) {
            cic.addNewOnlineResource().setHref(responsibleParty.getOnlineResources().get(0));
        }
        if (responsibleParty.isSetPhone()) {
            encodePhone(cic.addNewPhone().addNewCITelephone(), responsibleParty);
        }

    }

    private void encodeCiAddress(CIAddressPropertyType ciapt, Referenceable<CiAddress> referenceable) {
        if (referenceable.isReference()) {
            Reference reference = referenceable.getReference();
            if (reference.getActuate().isPresent()) {
                ciapt.setActuate(ActuateType.Enum.forString(reference.getActuate().get()));
            }
            if (reference.getArcrole().isPresent()) {
                ciapt.setHref(reference.getArcrole().get());
            }
            if (reference.getHref().isPresent()) {
                ciapt.setHref(reference.getHref().get().toString());
            }
            if (reference.getRole().isPresent()) {
                ciapt.setRole(reference.getRole().get());
            }
            if (reference.getShow().isPresent()) {
                ciapt.setShow(ShowType.Enum.forString(reference.getShow().get()));
            }
            if (reference.getTitle().isPresent()) {
                ciapt.setTitle(reference.getTitle().get());
            }
            if (reference.getType().isPresent()) {
                ciapt.setType(TypeType.Enum.forString(reference.getType().get()));
            }
        } else {
            if (referenceable.isInstance()) {
                Nillable<CiAddress> nillable = referenceable.getInstance();
                if (nillable.isPresent()) {
                    CiAddress ciAddress = referenceable.getInstance().get();
                    CIAddressType ciat = ciapt.addNewCIAddress();
                    if (ciAddress.isSetAdministrativeArea()) {
                        ciat.addNewAdministrativeArea().setCharacterString(ciAddress.getAdministrativeArea());
                    }
                    if (ciAddress.isSetCity()) {
                        ciat.addNewCity().setCharacterString(ciAddress.getCity());
                    }
                    if (ciAddress.isSetCountry()) {
                        ciat.addNewCountry().setCharacterString(ciAddress.getCountry());
                    }
                    if (ciAddress.isSetPostalCode()) {
                        ciat.addNewPostalCode().setCharacterString(ciAddress.getPostalCode());
                    }
                    if (ciAddress.hasDeliveryPoints()) {
                        ciat.setDeliveryPointArray(listToCharacterStringPropertyTypeArray(ciAddress.getDeliveryPoints()));
    
                    }
                    if (ciAddress.hasElectronicMailAddresses()) {
                        ciat.setElectronicMailAddressArray(listToCharacterStringPropertyTypeArray(Lists
                                .newArrayList(ciAddress.getElectronicMailAddresses())));
                    }
                    if (ciAddress.isSetId()) {
                        ciat.setId(ciAddress.getId());
                    }
                    if (ciAddress.isSetUuid()) {
                        ciat.setUuid(ciAddress.getUuid());
                    }
                } else {
                    if (nillable.hasReason()) {
                        ciapt.setNilReason(nillable.getNilReason().get());
                    }
                }
            }
        }
    }

    private void encodeCiAddress(CIAddressType ciat, SmlResponsibleParty responsibleParty) {
        if (responsibleParty.isSetAdministrativeArea()) {
            ciat.addNewAdministrativeArea().setCharacterString(responsibleParty.getAdministrativeArea());
        }
        if (responsibleParty.isSetCity()) {
            ciat.addNewCity().setCharacterString(responsibleParty.getCity());
        }
        if (responsibleParty.isSetCountry()) {
            ciat.addNewCountry().setCharacterString(responsibleParty.getCountry());
        }
        if (responsibleParty.isSetPostalCode()) {
            ciat.addNewPostalCode().setCharacterString(responsibleParty.getPostalCode());
        }
        if (responsibleParty.isSetDeliveryPoint()) {
            ciat.setDeliveryPointArray(listToCharacterStringPropertyTypeArray(responsibleParty.getDeliveryPoint()));

        }
        if (responsibleParty.isSetEmail()) {
            ciat.setElectronicMailAddressArray(listToCharacterStringPropertyTypeArray(Lists
                    .newArrayList(responsibleParty.getEmail())));
        }
    }

    private void encodePhone(CITelephonePropertyType citpt, Referenceable<CiTelephone> referenceable) {
        if (referenceable.isReference()) {
            Reference reference = referenceable.getReference();
            if (reference.getActuate().isPresent()) {
                citpt.setActuate(ActuateType.Enum.forString(reference.getActuate().get()));
            }
            if (reference.getArcrole().isPresent()) {
                citpt.setHref(reference.getArcrole().get());
            }
            if (reference.getHref().isPresent()) {
                citpt.setHref(reference.getHref().get().toString());
            }
            if (reference.getRole().isPresent()) {
                citpt.setRole(reference.getRole().get());
            }
            if (reference.getShow().isPresent()) {
                citpt.setShow(ShowType.Enum.forString(reference.getShow().get()));
            }
            if (reference.getTitle().isPresent()) {
                citpt.setTitle(reference.getTitle().get());
            }
            if (reference.getType().isPresent()) {
                citpt.setType(TypeType.Enum.forString(reference.getType().get()));
            }
        } else {
            if (referenceable.isInstance()) {
                Nillable<CiTelephone> nillable = referenceable.getInstance();
                if (nillable.isPresent()) {
                    CiTelephone ciTelephone = referenceable.getInstance().get();
                    CITelephoneType citt = citpt.addNewCITelephone();
                    if (ciTelephone.isSetVoice()) {
                        citt.setVoiceArray(listToCharacterStringPropertyTypeArray(ciTelephone.getVoice()));
                    }
                    if (ciTelephone.isSetFacsimile()) {
                        citt.setFacsimileArray(listToCharacterStringPropertyTypeArray(ciTelephone.getFacsimile()));
                    }
                    if (ciTelephone.isSetId()) {
                        citt.setId(ciTelephone.getId());
                    }
                    if (ciTelephone.isSetUuid()) {
                        citt.setUuid(ciTelephone.getUuid());
                    }
                } else {
                    if (nillable.hasReason()) {
                        citpt.setNilReason(nillable.getNilReason().get());
                    }
                }
            }
        }
    }
    
    private void encodePhone(CITelephoneType citt, SmlResponsibleParty responsibleParty) {
        if (responsibleParty.isSetPhoneVoice()) {
            citt.setVoiceArray(listToCharacterStringPropertyTypeArray(responsibleParty.getPhoneVoice()));
        }
        if (responsibleParty.isSetPhoneFax()) {
            citt.setFacsimileArray(listToCharacterStringPropertyTypeArray(responsibleParty.getPhoneFax()));
        }
    }

    private void encodeRole(CIRoleCodePropertyType circpt, Nillable<Role> nillable) throws OwsExceptionReport {
        if (nillable.isPresent()) {
            XmlObject encodeObjectToXml = CodingHelper.encodeObjectToXml(GcoConstants.NS_GCO, nillable.get());
            if (encodeObjectToXml != null) {
                circpt.addNewCIRoleCode().set(encodeObjectToXml);
            }
        } else {
            if (nillable.hasReason()) {
                circpt.setNilReason(nillable.getNilReason().get());
            }
        }
    }

    private void encodeRole(CIRoleCodePropertyType circpt, AbstractRole role) throws OwsExceptionReport {
        XmlObject encodeObjectToXml = CodingHelper.encodeObjectToXml(GcoConstants.NS_GCO, role);
        if (encodeObjectToXml != null) {
            circpt.addNewCIRoleCode().set(encodeObjectToXml);
        }
    }

    private XmlObject encodeGmdDomainConsistency(GmdDomainConsistency element, Map<HelperValues, String> additionalValues) throws OwsExceptionReport {
        if (additionalValues.containsKey(HelperValues.DOCUMENT)) {
            DQDomainConsistencyDocument document =
                    DQDomainConsistencyDocument.Factory.newInstance(getXmlOptions());
            DQResultPropertyType addNewResult = document.addNewDQDomainConsistency().addNewResult();
            encodeGmdDomainConsistency(addNewResult, (GmdDomainConsistency) element);
            return document;
        } else if (additionalValues.containsKey(HelperValues.PROPERTY_TYPE)) {
            DQDomainConsistencyPropertyType propertyType =
                    DQDomainConsistencyPropertyType.Factory.newInstance(getXmlOptions());
            DQResultPropertyType addNewResult = propertyType.addNewDQDomainConsistency().addNewResult();
            encodeGmdDomainConsistency(addNewResult, (GmdDomainConsistency) element);
            return propertyType;
        } else {
            DQDomainConsistencyType type = DQDomainConsistencyType.Factory.newInstance(getXmlOptions());
            DQResultPropertyType addNewResult = type.addNewResult();
            encodeGmdDomainConsistency(addNewResult, (GmdDomainConsistency) element);
            return type;
        }
    }

    private void encodeGmdDomainConsistency(DQResultPropertyType xbResult, GmdDomainConsistency domainConsistency)
            throws OwsExceptionReport {
        if (domainConsistency instanceof GmdConformanceResult) {
            encodeGmdConformanceResult(xbResult, (GmdConformanceResult) domainConsistency);
        } else if (domainConsistency instanceof GmdQuantitativeResult) {
            encodeGmdQuantitativeResult(xbResult, (GmdQuantitativeResult) domainConsistency);
        } else {
            throw new UnsupportedEncoderInputException(this, domainConsistency);
        }
    }

    private void encodeGmdConformanceResult(DQResultPropertyType xbResult, GmdConformanceResult gmdConformanceResult) {
        DQConformanceResultType dqConformanceResultType =
                (DQConformanceResultType) xbResult.addNewAbstractDQResult().substitute(QN_GMD_CONFORMANCE_RESULT,
                        DQConformanceResultType.type);
        if (gmdConformanceResult.isSetPassNilReason()) {
            dqConformanceResultType.addNewPass().setNilReason(gmdConformanceResult.getPassNilReason().name());
        } else {
            dqConformanceResultType.addNewPass().setBoolean(gmdConformanceResult.isPass());
        }
        dqConformanceResultType.addNewExplanation().setCharacterString(
                gmdConformanceResult.getSpecification().getExplanation());
        
        encodeCiCitation(dqConformanceResultType.addNewSpecification(), gmdConformanceResult.getSpecification().getCitation());
//        CICitationType xbCitation = dqConformanceResultType.addNewSpecification().addNewCICitation();
//        xbCitation.addNewTitle().setCharacterString(gmdConformanceResult.getSpecification().getCitation().getTitle());
//        CIDateType xbCiDate = xbCitation.addNewDate().addNewCIDate();
//        CodeListValueType xbCIDateTypeCode = xbCiDate.addNewDateType().addNewCIDateTypeCode();
//        GmdCitationDate gmdCitationDate = gmdConformanceResult.getSpecification().getCitation().getDate();
//        GmdDateType gmdDateType = gmdCitationDate.getDateType();
//        xbCIDateTypeCode.setCodeList(gmdDateType.getCodeList());
//        xbCIDateTypeCode.setCodeListValue(gmdDateType.getCodeListValue());
//        if (gmdDateType.getCodeSpace() != null && !gmdDateType.getCodeSpace().isEmpty()) {
//            xbCIDateTypeCode.setCodeSpace(gmdDateType.getCodeSpace());
//        }
//        xbCIDateTypeCode.setStringValue(gmdDateType.getValue());
//        XmlCursor newCursor = xbCiDate.addNewDate().newCursor();
//        newCursor.toNextToken();
//        newCursor.beginElement(QN_GCO_DATE);
//        newCursor.insertChars(gmdCitationDate.getDate());
//        newCursor.dispose();
    }

    private void encodeGmdQuantitativeResult(DQResultPropertyType xbResult, GmdQuantitativeResult gmdQuantitativeResult) {
        DQQuantitativeResultType dqQuantitativeResultType =
                (DQQuantitativeResultType) xbResult.addNewAbstractDQResult().substitute(QN_GMD_QUANTITATIVE_RESULT,
                        DQQuantitativeResultType.type);
        GmlBaseUnit unit = gmdQuantitativeResult.getUnit();
        UnitOfMeasurePropertyType valueUnit = dqQuantitativeResultType.addNewValueUnit();
        BaseUnitType xbBaseUnit =
                (BaseUnitType) valueUnit.addNewUnitDefinition().substitute(QN_GML_BASE_UNIT, BaseUnitType.type);
        CodeType xbCatalogSymbol = xbBaseUnit.addNewCatalogSymbol();
        xbCatalogSymbol.setCodeSpace(unit.getCatalogSymbol().getCodeSpace());
        xbCatalogSymbol.setStringValue(unit.getCatalogSymbol().getValue());
        xbBaseUnit.setId(unit.getId());
        xbBaseUnit.addNewUnitsSystem().setHref(unit.getUnitSystem());
        xbBaseUnit.addNewIdentifier().setCodeSpace(unit.getIdentifier());
        if (gmdQuantitativeResult.isSetValueNilReason()) {
            dqQuantitativeResultType.addNewValue().setNilReason(gmdQuantitativeResult.getValueNilReason().name());
        } else {
            XmlCursor cursor = dqQuantitativeResultType.addNewValue().addNewRecord().newCursor();
            cursor.toNextToken();
            cursor.insertChars(gmdQuantitativeResult.getValue());
            cursor.dispose();
        }
    }

    private PTFreeTextType encodePTFreeText(PT_FreeText element, Map<HelperValues, String> additionalValues) {
        PTFreeTextType ptftt = PTFreeTextType.Factory.newInstance();
        for (LocalisedCharacterString localisedCharacterString : element.getTextGroup()) {
            ptftt.addNewTextGroup().set(encodeLocalisedCharacterStringPropertyType(localisedCharacterString));
        }
        return ptftt;
    }

    private LocalisedCharacterStringPropertyType encodeLocalisedCharacterStringPropertyType(LocalisedCharacterString localisedCharacterString) {
        LocalisedCharacterStringPropertyType lcspt = LocalisedCharacterStringPropertyType.Factory.newInstance();
        lcspt.setLocalisedCharacterString(encodeLocalisedCharacterStringType(localisedCharacterString));
        return lcspt;
    }
    
    private LocalisedCharacterStringType encodeLocalisedCharacterStringType(LocalisedCharacterString localisedCharacterString) {
        LocalisedCharacterStringType lcst = LocalisedCharacterStringType.Factory.newInstance();
        lcst.setStringValue(localisedCharacterString.getValue());
        if (localisedCharacterString.isSetLocale()) {
            lcst.setLocale(localisedCharacterString.getLocale());
        }
        return lcst;
    }

    private CharacterStringPropertyType[] listToCharacterStringPropertyTypeArray(List<String> list) {
        List<CharacterStringPropertyType> values = Lists.newArrayList();
        for (String string : list) {
            CharacterStringPropertyType cspt = CharacterStringPropertyType.Factory.newInstance();
            cspt.setCharacterString(string);
            values.add(cspt);
        }
        return values.toArray(new CharacterStringPropertyType[0]);
    }

    private void encodeOnlineResource(CIOnlineResourcePropertyType ciorpt, Referenceable<CiOnlineResource> referenceable) {
        if (referenceable.isReference()) {
            Reference reference = referenceable.getReference();
            if (reference.getActuate().isPresent()) {
                ciorpt.setActuate(ActuateType.Enum.forString(reference.getActuate().get()));
            }
            if (reference.getArcrole().isPresent()) {
                ciorpt.setHref(reference.getArcrole().get());
            }
            if (reference.getHref().isPresent()) {
                ciorpt.setHref(reference.getHref().get().toString());
            }
            if (reference.getRole().isPresent()) {
                ciorpt.setRole(reference.getRole().get());
            }
            if (reference.getShow().isPresent()) {
                ciorpt.setShow(ShowType.Enum.forString(reference.getShow().get()));
            }
            if (reference.getTitle().isPresent()) {
                ciorpt.setTitle(reference.getTitle().get());
            }
            if (reference.getType().isPresent()) {
                ciorpt.setType(TypeType.Enum.forString(reference.getType().get()));
            }
        } else {
            if (referenceable.isInstance()) {
                Nillable<CiOnlineResource> nillable = referenceable.getInstance();
                if (nillable.isPresent()) {
                    CIOnlineResourceType ciort = ciorpt.addNewCIOnlineResource();
                    encodeOnlineResource(ciort, referenceable.getInstance().get());
                } else {
                    if (nillable.hasReason()) {
                        ciorpt.setNilReason(nillable.getNilReason().get());
                    }
                }
            }
        }
        
    }

    private void encodeOnlineResource(CIOnlineResourceType ciort, CiOnlineResource onlineResource) {
        // linkage
        if (onlineResource.getLinkage().isPresent()) {
            ciort.addNewLinkage().setURL(onlineResource.getLinkage().get().toString());
        } else {
            if (onlineResource.getLinkage().hasReason()) {
                ciort.addNewLinkage().setNilReason(onlineResource.getLinkage().getNilReason().get());
            }
        }
        // protocol
        if (onlineResource.isSetProtocol()) {
            if (onlineResource.getProtocol().isPresent()) {
                ciort.addNewProtocol().setCharacterString(onlineResource.getProtocol().get().toString());
            } else {
                if (onlineResource.getProtocol().getNilReason().isPresent()) {
                    ciort.addNewProtocol().setNilReason(onlineResource.getProtocol().getNilReason().get());
                }
            }
        }
        if (onlineResource.isSetApplicationProfile()) {
            ciort.addNewApplicationProfile().setCharacterString(onlineResource.getApplicationProfile());
        }
        if (onlineResource.isSetDescription()) {
            ciort.addNewDescription().setCharacterString(onlineResource.getDescription());
        }
        if (onlineResource.isSetName()) {
            ciort.addNewName().setCharacterString(onlineResource.getName());
        }
        if (onlineResource.isSetFunction()) {
            ciort.addNewFunction().addNewCIOnLineFunctionCode().setStringValue(onlineResource.getFunction());
        }
        if (onlineResource.isSetId()) {
            ciort.setId(onlineResource.getId());
        }
        if (onlineResource.isSetUuid()) {
            ciort.setUuid(onlineResource.getUuid());
        }
    }

    private XmlObject encodeCiOnlineResource(CiOnlineResource element, Map<HelperValues, String> additionalValues) {
        CIOnlineResourceType ciort = CIOnlineResourceType.Factory.newInstance(getXmlOptions());
        encodeOnlineResource(ciort, element);
        if (additionalValues.containsKey(HelperValues.PROPERTY_TYPE)) {
            CIOnlineResourcePropertyType ciorpt = CIOnlineResourcePropertyType.Factory.newInstance(getXmlOptions());
            ciorpt.setCIOnlineResource(ciort);
            return ciorpt;
        } else if (additionalValues.containsKey(HelperValues.DOCUMENT)) {
            CIOnlineResourceDocument ciord = CIOnlineResourceDocument.Factory.newInstance(getXmlOptions());
            ciord.setCIOnlineResource(ciort);
            return ciord;
        }
        return ciort;
    }

    private XmlObject encodeEXExtent(EXExtent exExtent, Map<HelperValues, String> additionalValues) throws OwsExceptionReport {
        EXExtentType exet = EXExtentType.Factory.newInstance();
        if (exExtent.hasDescription()) {
            exet.addNewDescription().setCharacterString(exExtent.getDescription());
        }
        if (exExtent.hasVerticalExtent()) {
            for (Referenceable<EXVerticalExtent> verticalExtent : exExtent.getExVerticalExtent()) {
                EXVerticalExtentPropertyType exvept = exet.addNewVerticalElement();
                if (verticalExtent.isReference()) {
                    Reference reference = verticalExtent.getReference();
                    if (reference.getActuate().isPresent()) {
                        exvept.setActuate(ActuateType.Enum.forString(reference.getActuate().get()));
                    }
                    if (reference.getArcrole().isPresent()) {
                        exvept.setHref(reference.getArcrole().get());
                    }
                    if (reference.getHref().isPresent()) {
                        exvept.setHref(reference.getHref().get().toString());
                    }
                    if (reference.getRole().isPresent()) {
                        exvept.setRole(reference.getRole().get());
                    }
                    if (reference.getShow().isPresent()) {
                        exvept.setShow(ShowType.Enum.forString(reference.getShow().get()));
                    }
                    if (reference.getTitle().isPresent()) {
                        exvept.setTitle(reference.getTitle().get());
                    }
                    if (reference.getType().isPresent()) {
                        exvept.setType(TypeType.Enum.forString(reference.getType().get()));
                    }
                } else { 
                    if (verticalExtent.isInstance()) {
                        Nillable<EXVerticalExtent> nillable = verticalExtent.getInstance();
                        if (nillable.isPresent()) {
                            XmlObject xml = encodeEXVerticalExtent(nillable.get(), new EnumMap<HelperValues, String>(HelperValues.class));
                            if (xml != null && xml instanceof EXVerticalExtentType) {
                                exvept.setEXVerticalExtent((EXVerticalExtentType) xml);
                            } else {
                                exvept.setNil();
                                exvept.setNilReason(Nillable.missing().get());
                            }
                        } else {
                            exvept.setNil();
                            if (nillable.hasReason()) {
                                exvept.setNilReason(nillable.getNilReason().get());
                            } else {
                                exvept.setNilReason(Nillable.missing().get());
                            }
                        }
                    }
                }
            }
        }
        if (additionalValues.containsKey(HelperValues.PROPERTY_TYPE)) {
            EXExtentPropertyType exept = EXExtentPropertyType.Factory.newInstance(getXmlOptions());
            exept.setEXExtent(exet);
            return exept;
        } else if (additionalValues.containsKey(HelperValues.DOCUMENT)) {
            EXExtentDocument exed = EXExtentDocument.Factory.newInstance(getXmlOptions());
            exed.setEXExtent(exet);
            return exed;
        }
        return exet;
    }

    private XmlObject encodeEXVerticalExtent(EXVerticalExtent exVerticalExtent,
            Map<HelperValues, String> additionalValues) throws OwsExceptionReport {
        EXVerticalExtentType exvet = EXVerticalExtentType.Factory.newInstance();
        if (exVerticalExtent.isSetId()) {
            exvet.setId(exVerticalExtent.getId());
        }
        if (exVerticalExtent.isSetUuid()) {
            exvet.setUuid(exVerticalExtent.getUuid());
        }
        // min value
        Nillable<Double> minNillable = exVerticalExtent.getMinimumValue();
        RealPropertyType rptMinValue = exvet.addNewMinimumValue();
        if (minNillable.isPresent()) {
            rptMinValue.setReal(minNillable.get());
        } else {
            rptMinValue.setNil();
            if (minNillable.hasReason()) {
                rptMinValue.setNilReason(minNillable.getNilReason().get());
            } else {
                rptMinValue.setNilReason(Nillable.missing().get());
            }
        }
        // max value
        Nillable<Double> maxNillable = exVerticalExtent.getMaximumValue();
        RealPropertyType rptMinMaxValue = exvet.addNewMaximumValue();
        if (maxNillable.isPresent()) {
            rptMinMaxValue.setReal(maxNillable.get());
        } else {
            rptMinMaxValue.setNil();
            if (maxNillable.hasReason()) {
                rptMinMaxValue.setNilReason(maxNillable.getNilReason().get());
            } else {
                rptMinMaxValue.setNilReason(Nillable.missing().get());
            }
        }
        // verticalCRS
        SCCRSPropertyType sccrspt = exvet.addNewVerticalCRS();
        Referenceable<ScCRS> verticalCRS = exVerticalExtent.getVerticalCRS();
        if (verticalCRS.isReference()) {
            Reference reference = verticalCRS.getReference();
            if (reference.getActuate().isPresent()) {
                sccrspt.setActuate(ActuateType.Enum.forString(reference.getActuate().get()));
            }
            if (reference.getArcrole().isPresent()) {
                sccrspt.setHref(reference.getArcrole().get());
            }
            if (reference.getHref().isPresent()) {
                sccrspt.setHref(reference.getHref().get().toString());
            }
            if (reference.getRole().isPresent()) {
                sccrspt.setRole(reference.getRole().get());
            }
            if (reference.getShow().isPresent()) {
                sccrspt.setShow(ShowType.Enum.forString(reference.getShow().get()));
            }
            if (reference.getTitle().isPresent()) {
                sccrspt.setTitle(reference.getTitle().get());
            }
            if (reference.getType().isPresent()) {
                sccrspt.setType(TypeType.Enum.forString(reference.getType().get()));
            }
        } else { 
            if (verticalCRS.isInstance()) {
                Nillable<ScCRS> nillable = verticalCRS.getInstance();
                if (nillable.isPresent()) {
                    XmlObject xml = encodeGML32(nillable.get().getAbstractCrs());
                    if (xml != null && xml instanceof AbstractCRSType) {
                        final XmlObject substituteElement =
                        XmlHelper.substituteElement(sccrspt.addNewAbstractCRS(), xml);
                        substituteElement.set(xml);
                    } else {
                        sccrspt.setNil();
                        sccrspt.setNilReason(Nillable.missing().get());
                    }
                } else {
                    sccrspt.setNil();
                    if (nillable.hasReason()) {
                        sccrspt.setNilReason(nillable.getNilReason().get());
                    } else {
                        sccrspt.setNilReason(Nillable.missing().get());
                    }
                }
            }
        }
        if (additionalValues.containsKey(HelperValues.PROPERTY_TYPE)) {
            EXVerticalExtentPropertyType exvept = EXVerticalExtentPropertyType.Factory.newInstance(getXmlOptions());
            exvept.setEXVerticalExtent(exvet);
            return exvept;
        } else if (additionalValues.containsKey(HelperValues.DOCUMENT)) {
            EXVerticalExtentDocument exved = EXVerticalExtentDocument.Factory.newInstance(getXmlOptions());
            exved.setEXVerticalExtent(exvet);
            return exved;
        }
        return exvet;
    }

}
