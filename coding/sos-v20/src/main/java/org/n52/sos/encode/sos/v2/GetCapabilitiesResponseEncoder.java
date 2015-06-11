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
package org.n52.sos.encode.sos.v2;

import java.util.Collection;
import java.util.Set;

import net.opengis.sos.x20.CapabilitiesDocument;
import net.opengis.sos.x20.CapabilitiesType;
import net.opengis.sos.x20.CapabilitiesType.Contents;
import net.opengis.sos.x20.ContentsType;
import net.opengis.sos.x20.InsertionCapabilitiesDocument;
import net.opengis.sos.x20.InsertionCapabilitiesType;
import net.opengis.sos.x20.ObservationOfferingType;
import net.opengis.swes.x20.AbstractContentsType.Offering;
import net.opengis.swes.x20.FeatureRelationshipType;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.OptionNotSupportedException;
import org.n52.sos.exception.ows.concrete.XmlDecodingException;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.ows.OfferingExtension;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.StringBasedExtension;
import org.n52.sos.ogc.sos.CapabilitiesExtension;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosCapabilities;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosInsertionCapabilities;
import org.n52.sos.ogc.sos.SosObservationOffering;
import org.n52.sos.ogc.sos.SosOffering;
import org.n52.sos.ogc.swes.SwesExtension;
import org.n52.sos.response.GetCapabilitiesResponse;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.w3c.SchemaLocation;
import org.n52.sos.w3c.W3CConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class GetCapabilitiesResponseEncoder extends AbstractSosResponseEncoder<GetCapabilitiesResponse> {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(GetCapabilitiesResponseEncoder.class);

    public GetCapabilitiesResponseEncoder() {
        super(SosConstants.Operations.GetCapabilities.name(), GetCapabilitiesResponse.class);
    }

    @Override
    protected XmlObject create(GetCapabilitiesResponse response) throws OwsExceptionReport {
        CapabilitiesDocument doc = CapabilitiesDocument.Factory.newInstance(getXmlOptions());
        CapabilitiesType xbCaps = doc.addNewCapabilities();
		
        if (response.isStatic()) {
            String xml = response.getXmlString();
            LOGGER.trace("Response is static. XML-String:\n{}\n",xml);
            try {
                doc.set(XmlObject.Factory.parse(xml));
                return doc;
            } catch (XmlException ex) {
                throw new XmlDecodingException("Static Capabilities", xml, ex);
            }
        }
		
        // set version.
        SosCapabilities caps = response.getCapabilities();
        if (caps.isSetVersion()) {
            xbCaps.setVersion(caps.getVersion());
        } else {
            xbCaps.setVersion(response.getVersion());
        }
        encodeServiceIdentification(caps, xbCaps);
        encodeServiceProvider(caps, xbCaps);
        encodeOperationsMetadata(caps, xbCaps);
        encodeFilterCapabilities(caps, xbCaps);
        encodeContents(caps, xbCaps, response.getVersion());
        encodeExtensions(caps, xbCaps);
        return doc;
    }

    private void setExtensions(XmlObject addNewExtension, CapabilitiesExtension extension) throws CodedException {
        if (extension instanceof SosInsertionCapabilities) {
            addNewExtension.set(createInsertionCapabilities((SosInsertionCapabilities) extension));
        } else if (extension instanceof StringBasedExtension) {
            String xml = ((StringBasedExtension) extension).getExtension();
            try {
                addNewExtension.set(XmlObject.Factory.parse(xml));
            } catch (XmlException ex) {
                throw new XmlDecodingException("SwesExtension", xml, ex);
            }
        } else {
            throw new OptionNotSupportedException()
                    .withMessage("The extension element is not supported by this service!");
        }
    }

    private XmlObject createInsertionCapabilities(SosInsertionCapabilities caps) {
        InsertionCapabilitiesDocument doc = InsertionCapabilitiesDocument.Factory.newInstance(getXmlOptions());
        InsertionCapabilitiesType xbCaps = doc.addNewInsertionCapabilities();
        if (caps.isSetFeatureOfInterestTypes()) {
            for (String foiType : caps.getFeatureOfInterestTypes()) {
                if (!foiType.equals(SosConstants.NOT_DEFINED)) {
                    xbCaps.addFeatureOfInterestType(foiType);
                }
            }
        }
        if (caps.isSetObservationTypes()) {
            for (String oType : caps.getObservationTypes()) {
                if (!oType.equals(SosConstants.NOT_DEFINED)) {
                    xbCaps.addObservationType(oType);
                }
            }
        }
        if (caps.isSetProcedureDescriptionFormats()) {
            for (String pdf : caps.getProcedureDescriptionFormats()) {
                if (!pdf.equals(SosConstants.NOT_DEFINED)) {
                    xbCaps.addProcedureDescriptionFormat(pdf);
                }
            }
        }
        if (caps.isSetSupportedEncodings()) {
            for (String se : caps.getSupportedEncodings()) {
                if (!se.equals(SosConstants.NOT_DEFINED)) {
                    xbCaps.addSupportedEncoding(se);
                }
            }
        }
        return doc;
    }

    /**
     * Sets the content section to the Capabilities document.
     * 
     * @param xbContents
     *            SOS 2.0 contents section
     * @param offerings
     *            SOS offerings for contents
     * @param version
     *            SOS response version
     * 
     * 
     * @throws OwsExceptionReport
     *             * if an error occurs.
     */
    protected void setContents(Contents xbContents, Collection<SosObservationOffering> offerings, String version)
            throws OwsExceptionReport {
        final ContentsType xbContType = xbContents.addNewContents();

        int offeringCounter = 0; // for gml:id generation
        for (final SosObservationOffering offering : offerings) {
            if (offering.isValidObservationOffering()) {
                ++offeringCounter;
                encodeObservationOffering(offering, offeringCounter, xbContType);

            }
        }
        // FIXME: change swes:AbstractOffering to sos:ObservationOffering and
        // the namespace prefix ns to sos due to
        // XMLBeans problems with substitution
        // (http://www.mail-archive.com/dev%40xmlbeans.apache.org/msg00962.html).
        renameContentsElementNames(xbContents);
    }

    /**
     * Creates a XML FeatureRelationship for the relatedFeature
     * 
     * @param featureRelationship
     *            XML feature relationship
     * @param relatedFeatureTarget
     *            Feature target identifier
     * @param roles
     *            Features role
     */
    private void createRelatedFeature(final FeatureRelationshipType featureRelationship,
            final String relatedFeatureTarget, final Collection<String> roles) {
        featureRelationship.addNewTarget().setHref(relatedFeatureTarget);
        if (roles != null) {
            for (final String role : roles) {
                featureRelationship.setRole(role);
            }
        }
    }

    private void renameContentsElementNames(final Contents xbContents) {
        for (final Offering offering : xbContents.getContents().getOfferingArray()) {
            final XmlCursor cursor = offering.getAbstractOffering().newCursor();
            cursor.setName(Sos2Constants.QN_OBSERVATION_OFFERING);
            cursor.removeAttribute(W3CConstants.QN_XSI_TYPE);
            if (cursor.toChild(Sos2Constants.QN_SOS_OBSERVED_AREA)) {
                cursor.setName(Sos2Constants.QN_SOS_OBSERVED_AREA);
                cursor.toParent();
            }
            if (cursor.toChild(Sos2Constants.QN_SOS_PHENOMENON_TIME)) {
                cursor.setName(Sos2Constants.QN_SOS_PHENOMENON_TIME);
                cursor.toParent();
            }
            if (cursor.toChild(Sos2Constants.QN_SOS_RESULT_TIME)) {
                cursor.setName(Sos2Constants.QN_SOS_RESULT_TIME);
                cursor.toParent();
            }
            if (cursor.toChild(Sos2Constants.QN_SOS_RESPONSE_FORMAT)) {
                cursor.setName(Sos2Constants.QN_SOS_RESPONSE_FORMAT);
                while (cursor.toNextSibling(Sos2Constants.QN_SOS_RESPONSE_FORMAT)) {
                    cursor.setName(Sos2Constants.QN_SOS_RESPONSE_FORMAT);
                }
                cursor.toParent();
            }
            if (cursor.toChild(Sos2Constants.QN_SOS_OBSERVATION_TYPE)) {
                cursor.setName(Sos2Constants.QN_SOS_OBSERVATION_TYPE);
                while (cursor.toNextSibling(Sos2Constants.QN_SOS_OBSERVATION_TYPE)) {
                    cursor.setName(Sos2Constants.QN_SOS_OBSERVATION_TYPE);
                }
                cursor.toParent();
            }
            if (cursor.toChild(Sos2Constants.QN_SOS_FEATURE_OF_INTEREST_TYPE)) {
                cursor.setName(Sos2Constants.QN_SOS_FEATURE_OF_INTEREST_TYPE);
                while (cursor.toNextSibling(Sos2Constants.QN_SOS_FEATURE_OF_INTEREST_TYPE)) {
                    cursor.setName(Sos2Constants.QN_SOS_FEATURE_OF_INTEREST_TYPE);
                }
            }
            cursor.dispose();
        }
    }

    @Override
    public Set<SchemaLocation> getConcreteSchemaLocations() {
        return Sets.newHashSet(Sos2Constants.SOS_GET_CAPABILITIES_SCHEMA_LOCATION);
    }

    private void encodeServiceIdentification(SosCapabilities caps, CapabilitiesType xbCaps) throws OwsExceptionReport {
        if (caps.isSetServiceIdentification()) {
            xbCaps.addNewServiceIdentification().set(encodeOws(caps.getServiceIdentification()));
        }
    }

    private void encodeServiceProvider(SosCapabilities caps, CapabilitiesType xbCaps) throws OwsExceptionReport {
        if (caps.isSetServiceProvider()) {
            xbCaps.addNewServiceProvider().set(encodeOws(caps.getServiceProvider()));
        }
    }

    private void encodeOperationsMetadata(SosCapabilities caps, CapabilitiesType xbCaps) throws OwsExceptionReport {
        if (caps.isSetOperationsMetadata() && caps.getOperationsMetadata().isSetOperations()) {
            xbCaps.addNewOperationsMetadata().set(encodeOws(caps.getOperationsMetadata()));
        }
    }

    private void encodeFilterCapabilities(SosCapabilities caps, CapabilitiesType xbCaps) throws OwsExceptionReport {
        if (caps.isSetFilterCapabilities()) {
            xbCaps.addNewFilterCapabilities().addNewFilterCapabilities().set(encodeFes(caps.getFilterCapabilities()));
        }
    }

    private void encodeContents(SosCapabilities caps, CapabilitiesType xbCaps, String version)
            throws OwsExceptionReport {
        if (caps.isSetContents()) {
            setContents(xbCaps.addNewContents(), caps.getContents(), version);
        }
    }

    private void encodeExtensions(SosCapabilities caps, CapabilitiesType xbCaps) throws OwsExceptionReport {
        if (caps.isSetExtensions()) {
            for (CapabilitiesExtension e : caps.getExtensions()) {
                setExtensions(xbCaps.addNewExtension(), e);
            }
        }
    }

    private void encodeObservationOffering(SosObservationOffering offering, int offeringCounter,
            ContentsType xbContType) throws OwsExceptionReport {
        final ObservationOfferingType xbObsOff = ObservationOfferingType.Factory.newInstance(getXmlOptions());
        
	SosOffering sosOffering = offering.getOffering();
        xbObsOff.setIdentifier(sosOffering.getIdentifier());
        if (sosOffering.isSetName()) {
            for (CodeType name : sosOffering.getName()) {
                xbObsOff.addNewName().set(CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, name)); 
            }
        }
        if (sosOffering.isSetDescription()) {
            xbObsOff.setDescription(sosOffering.getDescription());
        }
        encodeOfferingExtension(offering, xbObsOff);
        
        for (String procedure : offering.getProcedures()) {
            xbObsOff.setProcedure(procedure);
        }
        encodeObservableProperties(offering, xbObsOff);
        encodeRelatedFeatures(offering, xbObsOff);
        encodeObservedArea(offering, xbObsOff);
        encodePhenomenonTime(offering, offeringCounter, xbObsOff);
        encodeResultTime(offering, offeringCounter, xbObsOff);
        encodeResponseFormat(offering, xbObsOff);
        encodeObservationType(offering, xbObsOff);
        encodeFeatureOfInterestTypes(offering, xbObsOff);
        encodeProcedureDescriptionFormats(offering, xbObsOff);
        xbContType.addNewOffering().setAbstractOffering(xbObsOff);
        /*
         * Offering addNewOffering = xbContType.addNewOffering();
         * addNewOffering.addNewAbstractOffering().set(xbObsOff); XmlHelper
         * .substituteElement(addNewOffering.getAbstractOffering(), xbObsOff);
         */
    }

    private void encodeOfferingExtension(SosObservationOffering sosOffering, ObservationOfferingType xbObsOff) throws OwsExceptionReport {
        if (sosOffering.isSetExtensions()) {
            for (SwesExtension<?> swesExtention : sosOffering.getExtensions().getExtensions()) {
                if (swesExtention.getValue() instanceof OfferingExtension) {
                    OfferingExtension extension = (OfferingExtension) swesExtention.getValue();
                    try {
                        xbObsOff.addNewExtension().set(XmlObject.Factory.parse(extension.getExtension()));
                    } catch (XmlException ex) {
                            throw new XmlDecodingException("SwesExtension", extension.getExtension(), ex);
                    }
                } else {
                    xbObsOff.addNewExtension().set(CodingHelper.encodeObjectToXml(swesExtention.getNamespace(), swesExtention));
                }
                
            }
        }
    }

    private void encodeObservableProperties(SosObservationOffering offering, ObservationOfferingType xbObsOff) {
        // TODO: pdf [0..*]
        // set observableProperties [0..*]
        for (String phenomenon : offering.getObservableProperties()) {
            xbObsOff.addObservableProperty(phenomenon);
        }
    }

    private void encodeRelatedFeatures(SosObservationOffering offering, ObservationOfferingType xbObsOff) {
        // set relatedFeatures [0..*]
        if (offering.isSetRelatedFeature()) {
            for (String target : offering.getRelatedFeatures().keySet()) {
                createRelatedFeature(xbObsOff.addNewRelatedFeature().addNewFeatureRelationship(), target, offering
                        .getRelatedFeatures().get(target));
            }
        }
    }

    private void encodeObservedArea(SosObservationOffering offering, ObservationOfferingType xbObsOff)
            throws OwsExceptionReport {
        // set observed area [0..1]
        if (offering.isSetObservedArea() && offering.getObservedArea().isSetEnvelope()
                && offering.getObservedArea().isSetSrid()) {
            XmlObject encodeObjectToXml = encodeGml(offering.getObservedArea());
            xbObsOff.addNewObservedArea().addNewEnvelope().set(encodeObjectToXml);
        }
    }

    private void encodePhenomenonTime(SosObservationOffering offering, int offeringCounter,
            ObservationOfferingType xbObsOff) throws OwsExceptionReport {
        // set up phenomenon time [0..1]
        if (offering.getPhenomenonTime() instanceof TimePeriod) {
            TimePeriod tp = (TimePeriod) offering.getPhenomenonTime();
            if (!tp.isEmpty()) {
                tp.setGmlId(String.format("%s_%d", Sos2Constants.EN_PHENOMENON_TIME, offeringCounter));
                XmlObject xmlObject = encodeGml(tp);
                xbObsOff.addNewPhenomenonTime().addNewTimePeriod().set(xmlObject);
                xbObsOff.getPhenomenonTime().substitute(Sos2Constants.QN_SOS_PHENOMENON_TIME,
                        xbObsOff.getPhenomenonTime().schemaType());
            }
        }
    }

    private void encodeResultTime(SosObservationOffering offering, int offeringCounter,
            ObservationOfferingType xbObsOff) throws OwsExceptionReport {
        // set resultTime [0..1]
        if (offering.getResultTime() instanceof TimePeriod) {
            TimePeriod tp = (TimePeriod) offering.getResultTime();
            tp.setGmlId(String.format("%s_%d", Sos2Constants.EN_RESULT_TIME, offeringCounter));
            if (!tp.isEmpty()) {
                XmlObject xmlObject = encodeGml(tp);
                xbObsOff.addNewResultTime().addNewTimePeriod().set(xmlObject);
                xbObsOff.getResultTime().substitute(Sos2Constants.QN_SOS_RESULT_TIME,
                        xbObsOff.getResultTime().schemaType());
            }
        }
    }

    private void encodeResponseFormat(SosObservationOffering offering, ObservationOfferingType xbObsOff) {
        // set responseFormat [0..*]
        if (offering.isSetResponseFormats()) {
            for (String responseFormat : offering.getResponseFormats()) {
                xbObsOff.addResponseFormat(responseFormat);
            }
        }
    }

    private void encodeObservationType(SosObservationOffering offering, ObservationOfferingType xbObsOff) {
        // set observationType [0..*]
        if (offering.isSetObservationTypes()) {
            for (String obsType : offering.getObservationTypes()) {
                xbObsOff.addObservationType(obsType);
            }
        }
    }

    private void encodeFeatureOfInterestTypes(SosObservationOffering offering, ObservationOfferingType xbObsOff) {
        // set featureOfInterestType [0..1]
        if (offering.isSetFeatureOfInterestTypes()) {
            for (String foit : offering.getFeatureOfInterestTypes()) {
                xbObsOff.addFeatureOfInterestType(foit);
            }
        }
    }

    private void encodeProcedureDescriptionFormats(SosObservationOffering offering, ObservationOfferingType xbObsOff) {
        if (offering.isSetProcedureDescriptionFormats()) {
            for (String pdf : offering.getProcedureDescriptionFormats()) {
                xbObsOff.addProcedureDescriptionFormat(pdf);
            }
        }
    }
}
