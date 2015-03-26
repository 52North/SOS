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
package org.n52.sos.encode;

import static org.n52.sos.util.CodingHelper.encoderKeysForElements;
import static org.n52.sos.util.CollectionHelper.union;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import net.opengis.gml.x32.BaseUnitType;
import net.opengis.gml.x32.CodeType;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.isotc211.x2005.gco.CharacterStringPropertyType;
import org.isotc211.x2005.gco.CodeListValueType;
import org.isotc211.x2005.gco.UnitOfMeasurePropertyType;
import org.isotc211.x2005.gmd.CIAddressType;
import org.isotc211.x2005.gmd.CICitationType;
import org.isotc211.x2005.gmd.CIContactType;
import org.isotc211.x2005.gmd.CIDateType;
import org.isotc211.x2005.gmd.CIResponsiblePartyDocument;
import org.isotc211.x2005.gmd.CIResponsiblePartyPropertyType;
import org.isotc211.x2005.gmd.CIResponsiblePartyType;
import org.isotc211.x2005.gmd.CIRoleCodePropertyType;
import org.isotc211.x2005.gmd.CITelephoneType;
import org.isotc211.x2005.gmd.DQConformanceResultType;
import org.isotc211.x2005.gmd.DQDomainConsistencyDocument;
import org.isotc211.x2005.gmd.DQDomainConsistencyPropertyType;
import org.isotc211.x2005.gmd.DQDomainConsistencyType;
import org.isotc211.x2005.gmd.DQQuantitativeResultType;
import org.isotc211.x2005.gmd.DQResultPropertyType;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.iso.GcoConstants;
import org.n52.sos.iso.gmd.GmdCitationDate;
import org.n52.sos.iso.gmd.GmdConformanceResult;
import org.n52.sos.iso.gmd.GmdConstants;
import org.n52.sos.iso.gmd.GmdDateType;
import org.n52.sos.iso.gmd.GmdDomainConsistency;
import org.n52.sos.iso.gmd.GmdQuantitativeResult;
import org.n52.sos.iso.gmd.GmlBaseUnit;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.Role;
import org.n52.sos.ogc.sensorML.SmlResponsibleParty;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.w3c.SchemaLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * {@link AbstractXmlEncoder} class to decode ISO TC211 Geographic MetaData
 * (GMD) extensible markup language.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.2.0
 *
 */
public class Iso19139GmdEncoder extends AbstractXmlEncoder<Object> {

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
                    GmdConformanceResult.class), 
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
        if (LOGGER.isDebugEnabled() && encodedObject != null) {
            LOGGER.debug("Encoded object {} is valid: {}", encodedObject.schemaType().toString(),
                    XmlHelper.validateDocument(encodedObject));
        }
        return encodedObject;
    }

    private XmlObject encodeResponsibleParty(SmlResponsibleParty responsibleParty,
            Map<HelperValues, String> additionalValues) throws OwsExceptionReport {
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
        }
        return cirpt;
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

    private void encodePhone(CITelephoneType citt, SmlResponsibleParty responsibleParty) {
        if (responsibleParty.isSetPhoneVoice()) {
            citt.setVoiceArray(listToCharacterStringPropertyTypeArray(responsibleParty.getPhoneVoice()));
        }
        if (responsibleParty.isSetPhoneFax()) {
            citt.setFacsimileArray(listToCharacterStringPropertyTypeArray(responsibleParty.getPhoneFax()));
        }
    }

    private void encodeRole(CIRoleCodePropertyType circpt, Role role) throws OwsExceptionReport {
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
        CICitationType xbCitation = dqConformanceResultType.addNewSpecification().addNewCICitation();
        xbCitation.addNewTitle().setCharacterString(gmdConformanceResult.getSpecification().getCitation().getTitle());
        CIDateType xbCiDate = xbCitation.addNewDate().addNewCIDate();
        CodeListValueType xbCIDateTypeCode = xbCiDate.addNewDateType().addNewCIDateTypeCode();
        GmdCitationDate gmdCitationDate = gmdConformanceResult.getSpecification().getCitation().getDate();
        GmdDateType gmdDateType = gmdCitationDate.getDateType();
        xbCIDateTypeCode.setCodeList(gmdDateType.getCodeList());
        xbCIDateTypeCode.setCodeListValue(gmdDateType.getCodeListValue());
        if (gmdDateType.getCodeSpace() != null && !gmdDateType.getCodeSpace().isEmpty()) {
            xbCIDateTypeCode.setCodeSpace(gmdDateType.getCodeSpace());
        }
        xbCIDateTypeCode.setStringValue(gmdDateType.getValue());
        XmlCursor newCursor = xbCiDate.addNewDate().newCursor();
        newCursor.toNextToken();
        newCursor.beginElement(QN_GCO_DATE);
        newCursor.insertChars(gmdCitationDate.getDate());
        newCursor.dispose();
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

    private CharacterStringPropertyType[] listToCharacterStringPropertyTypeArray(List<String> list) {
        List<CharacterStringPropertyType> values = Lists.newArrayList();
        for (String string : list) {
            CharacterStringPropertyType cspt = CharacterStringPropertyType.Factory.newInstance();
            cspt.setCharacterString(string);
            values.add(cspt);
        }
        return values.toArray(new CharacterStringPropertyType[0]);
    }

    private static XmlOptions getXmlOptions() {
        return XmlOptionsHelper.getInstance().getXmlOptions();
    }

}
