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
package org.n52.sos.encode.xml.inspire;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import net.opengis.gml.x32.BaseUnitType;
import net.opengis.gml.x32.CodeType;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.isotc211.x2005.gco.CodeListValueType;
import org.isotc211.x2005.gco.UnitOfMeasurePropertyType;
import org.isotc211.x2005.gmd.CICitationType;
import org.isotc211.x2005.gmd.CIDateType;
import org.isotc211.x2005.gmd.DQConformanceResultType;
import org.isotc211.x2005.gmd.DQDomainConsistencyDocument;
import org.isotc211.x2005.gmd.DQDomainConsistencyPropertyType;
import org.isotc211.x2005.gmd.DQDomainConsistencyType;
import org.isotc211.x2005.gmd.DQQuantitativeResultType;
import org.isotc211.x2005.gmd.DQResultPropertyType;
import org.n52.sos.aqd.AqdConstants;
import org.n52.sos.encode.AbstractXmlEncoder;
import org.n52.sos.encode.EncoderKey;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.gmd.GmdCitationDate;
import org.n52.sos.gmd.GmdConformanceResult;
import org.n52.sos.gmd.GmdDateType;
import org.n52.sos.gmd.GmdDomainConsistency;
import org.n52.sos.gmd.GmdQuantitativeResult;
import org.n52.sos.gmd.GmlBaseUnit;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GmdEncoder extends AbstractXmlEncoder<Object> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(GmdEncoder.class);
    
    @SuppressWarnings("unchecked")
    private static final Set<EncoderKey> ENCODER_KEYS = CollectionHelper.union(
            CodingHelper.encoderKeysForElements(null,
                                    GmdQuantitativeResult.class,
                                    GmdConformanceResult.class),
            CodingHelper.encoderKeysForElements(AqdConstants.NS_GMD,
                                    GmdQuantitativeResult.class,
                                    GmdConformanceResult.class));

    private static final QName QN_GCO_DATE
            = new QName(AqdConstants.NS_GCO, "Date", AqdConstants.NS_GCO_PREFIX);
    private static final QName QN_GMD_CONFORMANCE_RESULT
            = new QName(AqdConstants.NS_GMD, "DQ_ConformanceResult", AqdConstants.NS_GMD_PREFIX);
    private static final QName QN_GMD_QUANTITATIVE_RESULT
            = new QName(AqdConstants.NS_GMD, "DQ_QuantitativeResult", AqdConstants.NS_GMD_PREFIX);
    private static final QName QN_GML_BASE_UNIT
            = new QName(GmlConstants.NS_GML_32, "BaseUnit", GmlConstants.NS_GML_PREFIX);

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return Collections.unmodifiableSet(ENCODER_KEYS);
    }

    @Override
    public void addNamespacePrefixToMap(
            Map<String, String> nameSpacePrefixMap) {
        nameSpacePrefixMap.putAll(AqdConstants.NAMESPACE_PREFIX_MAP);
    }

    @Override
    public XmlObject encode(Object objectToEncode,
                            Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport {
        if (objectToEncode instanceof GmdDomainConsistency){
            XmlObject encodedObject = null;
            if (additionalValues.containsKey(HelperValues.DOCUMENT)) {
                DQDomainConsistencyDocument document= DQDomainConsistencyDocument.Factory.newInstance(getXmlOptions());
                DQResultPropertyType addNewResult = document.addNewDQDomainConsistency().addNewResult();
                 encodeGmdDomainConsistency(addNewResult, (GmdDomainConsistency) objectToEncode);
                 encodedObject = document;
            } else if (additionalValues.containsKey(HelperValues.PROPERTY_TYPE)) {
                DQDomainConsistencyPropertyType propertyType
                        = DQDomainConsistencyPropertyType.Factory.newInstance(getXmlOptions());
                DQResultPropertyType addNewResult = propertyType.addNewDQDomainConsistency().addNewResult();
                encodeGmdDomainConsistency(addNewResult, (GmdDomainConsistency) objectToEncode);
                encodedObject = propertyType;
            } else {
                DQDomainConsistencyType type = DQDomainConsistencyType.Factory.newInstance(getXmlOptions());
                DQResultPropertyType addNewResult = type.addNewResult();
                encodeGmdDomainConsistency(addNewResult, (GmdDomainConsistency) objectToEncode);
                encodedObject = type;
            }
            if (LOGGER.isDebugEnabled() && encodedObject != null) {
                XmlHelper.validateDocument(encodedObject);
            }
            return encodedObject;
        } else {
            throw new UnsupportedEncoderInputException(this, objectToEncode);
        }
    }

    private void encodeGmdDomainConsistency(DQResultPropertyType xbResult, GmdDomainConsistency domainConsistency) throws OwsExceptionReport {
        if (domainConsistency instanceof  GmdConformanceResult) {
            encodeGmdConformanceResult(xbResult, (GmdConformanceResult) domainConsistency);
        } else if (domainConsistency instanceof GmdQuantitativeResult) {
            encodeGmdQuantitativeResult(xbResult, (GmdQuantitativeResult) domainConsistency);
        } else {
            throw new UnsupportedEncoderInputException(this, domainConsistency);
        }
    }

    private void encodeGmdConformanceResult(DQResultPropertyType xbResult,
                                            GmdConformanceResult gmdConformanceResult) {
        DQConformanceResultType dqConformanceResultType = (DQConformanceResultType)xbResult.addNewAbstractDQResult().substitute(QN_GMD_CONFORMANCE_RESULT, DQConformanceResultType.type); 
        if (gmdConformanceResult.isSetPassNilReason()) {
            dqConformanceResultType.addNewPass().setNilReason(gmdConformanceResult.getPassNilReason().name());
        } else {
            dqConformanceResultType.addNewPass().setBoolean(gmdConformanceResult.isPass());
        }
        dqConformanceResultType.addNewExplanation().setCharacterString(gmdConformanceResult.getSpecification().getExplanation());
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
        DQQuantitativeResultType dqQuantitativeResultType = (DQQuantitativeResultType)xbResult.addNewAbstractDQResult().substitute(QN_GMD_QUANTITATIVE_RESULT, DQQuantitativeResultType.type);
        GmlBaseUnit unit = gmdQuantitativeResult.getUnit();
        UnitOfMeasurePropertyType valueUnit = dqQuantitativeResultType.addNewValueUnit();
        BaseUnitType xbBaseUnit = (BaseUnitType)valueUnit.addNewUnitDefinition().substitute(QN_GML_BASE_UNIT, BaseUnitType.type);
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

    private static XmlOptions getXmlOptions() {
        return XmlOptionsHelper.getInstance().getXmlOptions();
    }
    
}
