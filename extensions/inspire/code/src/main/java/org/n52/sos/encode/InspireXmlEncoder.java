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

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.DateTimeFormatException;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.inspire.InspireConstants;
import org.n52.sos.inspire.InspireObject;
import org.n52.sos.inspire.InspireSupportedCRS;
import org.n52.sos.inspire.InspireSupportedLanguages;
import org.n52.sos.inspire.capabilities.FullInspireExtendedCapabilities;
import org.n52.sos.inspire.capabilities.InspireExtendedCapabilities;
import org.n52.sos.inspire.capabilities.MinimalInspireExtendedCapabilities;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.ogc.swes.SwesExtension;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.http.MediaType;
import org.n52.sos.util.http.MediaTypes;
import org.n52.sos.w3c.SchemaLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * XML encoder class for the INSPIRE schema
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 * 
 */
public class InspireXmlEncoder extends AbstractXmlEncoder<Object> {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(InspireXmlEncoder.class);

    private static final Set<EncoderKey> ENCODER_KEYS = Sets.union(
            CodingHelper.encoderKeysForElements(
                    InspireConstants.NS_INSPIRE_DLS, 
                        InspireExtendedCapabilities.class), 
            CodingHelper.encoderKeysForElements(
                    InspireConstants.NS_INSPIRE_COMMON,
                        SwesExtension.class,
                        InspireSupportedLanguages.class, 
                        InspireSupportedCRS.class));

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.emptySet();
    }

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return ENCODER_KEYS;
    }

    @Override
    public XmlObject encode(Object objectToEncode) throws OwsExceptionReport, UnsupportedEncoderInputException {
        return encode(objectToEncode, new EnumMap<HelperValues, String>(HelperValues.class));
    }

    @Override
    public XmlObject encode(Object objectToEncode, Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport, UnsupportedEncoderInputException {
        if (objectToEncode instanceof InspireObject) {
            return encodeObject((InspireObject)objectToEncode);
        } else if (objectToEncode instanceof SwesExtension<?>) {
            SwesExtension<?> swesExtension = (SwesExtension<?>)objectToEncode;
            if (swesExtension.getValue() instanceof InspireObject) {
                return encodeObject((InspireObject)swesExtension.getValue());
            }
        }
       throw new UnsupportedEncoderInputException(this, objectToEncode);
    }

    private XmlObject encodeObject(InspireObject objectToEncode) throws OwsExceptionReport {
        try {
            checkIfSupported(objectToEncode);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            new InspireXmlStreamWriter(objectToEncode).write(out);
            String s = out.toString("UTF8");
            return XmlObject.Factory.parse(s);
        } catch (XMLStreamException ex) {
            throw new NoApplicableCodeException().causedBy(ex).withMessage(
                    "Error encoding Inspire extended capabilities!");
        } catch (DateTimeFormatException ex) {
            throw new NoApplicableCodeException().causedBy(ex).withMessage(
                    "Error encoding Inspire extended capabilities!");
        } catch (XmlException ex) {
            throw new NoApplicableCodeException().causedBy(ex).withMessage(
                    "Error encoding Inspire extended capabilities!");
        } catch (UnsupportedEncodingException ex) {
            throw new NoApplicableCodeException().causedBy(ex).withMessage(
                    "Error encoding Inspire extended capabilities!");
        }
    }

    private void checkIfSupported(InspireObject objectToEncode) throws UnsupportedEncoderInputException {
        if (!(objectToEncode instanceof InspireSupportedLanguages) 
         && !(objectToEncode instanceof InspireSupportedCRS)
         && !(objectToEncode instanceof FullInspireExtendedCapabilities)
         && !(objectToEncode instanceof MinimalInspireExtendedCapabilities)) {
            throw new UnsupportedEncoderInputException(this, objectToEncode);
        }
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Sets.newHashSet(InspireConstants.INSPIRE_COMMON_10_SCHEMA_LOCATION,
                InspireConstants.INSPIRE_DLS_10_SCHEMA_LOCATION);
    }

    @Override
    public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
        return Collections.emptyMap();
    }

    @Override
    public void addNamespacePrefixToMap(Map<String, String> nameSpacePrefixMap) {
        nameSpacePrefixMap.put(InspireConstants.NS_INSPIRE_COMMON, InspireConstants.NS_INSPIRE_COMMON_PREFIX);
        nameSpacePrefixMap.put(InspireConstants.NS_INSPIRE_DLS, InspireConstants.NS_INSPIRE_DLS_PREFIX);
    }

    @Override
    public MediaType getContentType() {
        return MediaTypes.TEXT_XML;
    }

    // private XmlObject createMinimalInspireExtendedCapabilities(
    // MinimalInspireExtendedCapabilities sosInspireExtendedCapabilities) throws
    // OwsExceptionReport {
    // try {
    // ExtendedCapabilitiesType extendedCapabilitiesType =
    // createExtendedCapabilitiesType();
    // List<JAXBElement<?>> content = extendedCapabilitiesType.getContent();
    // if (!sosInspireExtendedCapabilities.isSetMetadataUrl()) {
    //
    // }
    // addMetadataUrl(sosInspireExtendedCapabilities.getMetadataUrl(), content);
    // addSupportedLanguages(sosInspireExtendedCapabilities, content);
    // addResponseLanguage(sosInspireExtendedCapabilities, content);
    // addSpatialDataSetIdentifier(sosInspireExtendedCapabilities.getSpatialDataSetIdentifier(),
    // content);
    // return
    // convertDocumentToXmlObject(createInspireExtendedCapabilities(extendedCapabilitiesType));
    // } catch (MalformedURLException mue) {
    // throw new
    // NoApplicableCodeException().causedBy(mue).withMessage("Error while createing XML document!");
    // } catch (SAXException saxe) {
    // throw new
    // NoApplicableCodeException().causedBy(saxe).withMessage("Error while createing XML document!");
    // }
    // }
    //
    // private XmlObject createFullInspireExtendedCapabilities(
    // FullInspireExtendedCapabilities sosInspireExtendedCapabilities) throws
    // OwsExceptionReport {
    // try {
    // ExtendedCapabilitiesType extendedCapabilitiesType =
    // createExtendedCapabilitiesType();
    // List<JAXBElement<?>> content = extendedCapabilitiesType.getContent();
    // addResourceLocator(sosInspireExtendedCapabilities, content);
    // addResourceType(sosInspireExtendedCapabilities, content);
    // addTemporalReference(sosInspireExtendedCapabilities, content);
    // // addConformity(sosInspireExtendedCapabilities, content);
    // addMetadataPointOfContact(sosInspireExtendedCapabilities, content);
    // addMetadataDate(sosInspireExtendedCapabilities, content);
    // addSpatialDataServiceType(sosInspireExtendedCapabilities, content);
    // addMandatoryKeyword(sosInspireExtendedCapabilities, content);
    // addKeyword(sosInspireExtendedCapabilities, content);
    // addSupportedLanguages(sosInspireExtendedCapabilities, content);
    // addResponseLanguage(sosInspireExtendedCapabilities, content);
    // if (sosInspireExtendedCapabilities.isSetMetadataUrl()) {
    // addMetadataUrl(sosInspireExtendedCapabilities.getMetadataUrl(), content);
    // }
    // addConformity(sosInspireExtendedCapabilities, content);
    // return
    // convertDocumentToXmlObject(createInspireExtendedCapabilities(extendedCapabilitiesType));
    // } catch (MalformedURLException mue) {
    // throw new
    // NoApplicableCodeException().causedBy(mue).withMessage("Error while createing XML document!");
    // } catch (SAXException saxe) {
    // throw new
    // NoApplicableCodeException().causedBy(saxe).withMessage("Error while createing XML document!");
    // }
    // }
    //
    // private void addResourceLocator(FullInspireExtendedCapabilities
    // sosInspireExtendedCapabilities,
    // List<JAXBElement<?>> content) throws CodedException {
    // if (!sosInspireExtendedCapabilities.isSetResourceLocators()) {
    // throw new
    // NoApplicableCodeException().withMessage("Required resource locator is missing!");
    // }
    // // TODO different languages
    // // ResLocInspireInteroperabilityRegulationGer
    // // createResLocInspireInteroperabilityRegulationGer =
    // //
    // objectFactoryInspireCommon.createResLocInspireInteroperabilityRegulationGer();
    // List<InspireResourceLocator> sosInspireResourceLocators =
    // sosInspireExtendedCapabilities.getResourceLocator();
    // for (InspireResourceLocator sosInspireResourceLocator :
    // sosInspireResourceLocators) {
    // content.add(objectFactoryInspireCommon
    // .createExtendedCapabilitiesTypeResourceLocator(createResourceLocatorType(sosInspireResourceLocator)));
    // }
    // }
    //
    // private void addResourceType(FullInspireExtendedCapabilities
    // sosInspireExtendedCapabilities,
    // List<JAXBElement<?>> content) throws CodedException {
    // if (!sosInspireExtendedCapabilities.isSetResourceType()) {
    // throw new
    // NoApplicableCodeException().withMessage("Required resource type is missing!");
    // }
    // content.add(objectFactoryInspireCommon
    // .createExtendedCapabilitiesTypeResourceType(ServiceSpatialDataResourceType.fromValue(ResourceType
    // .fromValue(sosInspireExtendedCapabilities.getResourceType().name()))));
    // }
    //
    // private void addTemporalReference(FullInspireExtendedCapabilities
    // sosInspireExtendedCapabilities,
    // List<JAXBElement<?>> content) throws CodedException {
    // if (!sosInspireExtendedCapabilities.isSetTemporalReferences()) {
    // throw new
    // NoApplicableCodeException().withMessage("Required temporal reference is missing!");
    // }
    // for (InspireTemporalReference sosTemporalReference :
    // sosInspireExtendedCapabilities.getTemporalReferences()) {
    // TemporalReference temporalReference =
    // objectFactoryInspireCommon.createTemporalReference();
    // if (sosTemporalReference.isSetDateOfCreation()) {
    // temporalReference.setDateOfCreation(DateTimeHelper.formatDateTime2String(sosTemporalReference
    // .getDateOfCreation().getTimePosition()));
    // }
    // if (sosTemporalReference.isSetDateOfLastRevision()) {
    // temporalReference.setDateOfLastRevision(DateTimeHelper.formatDateTime2String(sosTemporalReference
    // .getDateOfLastRevision().getTimePosition()));
    // }
    // if (sosTemporalReference.isSetDatesOfPublication()) {
    // for (TimeInstant dateOfPublication :
    // sosTemporalReference.getDatesOfPublication()) {
    // temporalReference.getDateOfPublication().add(
    // DateTimeHelper.formatDateTime2String(dateOfPublication.getTimePosition()));
    // }
    // }
    // if (sosTemporalReference.isSetTemporalExtents()) {
    // List<TemporalExtent> temporalExtents =
    // temporalReference.getTemporalExtent();
    // for (Time sosTemporalExtent : sosTemporalReference.getTemporalExtents())
    // {
    // TemporalExtent temporalExtent =
    // objectFactoryInspireCommon.createTemporalExtent();
    // if (sosTemporalExtent instanceof TimeInstant) {
    // JAXBElement<String> individualDate =
    // objectFactoryInspireCommon.createIndividualDate(DateTimeHelper
    // .formatDateTime2String(((TimeInstant)
    // sosTemporalExtent).getTimePosition()));
    // temporalExtent.getTemporalExtentElement().add(individualDate);
    // } else if (sosTemporalExtent instanceof TimePeriod) {
    // IntervalOfDates intervalOfDates =
    // objectFactoryInspireCommon.createIntervalOfDates();
    // TimePeriod timePeriod = (TimePeriod) sosTemporalExtent;
    // intervalOfDates.setStartingDate(DateTimeHelper.formatDateTime2String(timePeriod
    // .getStartTimePosition()));
    // intervalOfDates.setEndDate(DateTimeHelper.formatDateTime2String(timePeriod
    // .getEndTimePosition()));
    // JAXBElement<IntervalOfDates> intervalDate =
    // objectFactoryInspireCommon.createIntervalOfDates(intervalOfDates);
    // temporalExtent.getTemporalExtentElement().add(intervalDate);
    // }
    // temporalExtents.add(temporalExtent);
    // }
    // }
    // content.add(objectFactoryInspireCommon.createExtendedCapabilitiesTypeTemporalReference(temporalReference));
    // }
    // }
    //
    // private void addConformity(FullInspireExtendedCapabilities
    // sosInspireExtendedCapabilities,
    // List<JAXBElement<?>> content) throws CodedException {
    // if (!sosInspireExtendedCapabilities.isSetConformity()) {
    // throw new
    // NoApplicableCodeException().withMessage("Required conformity is missing!");
    // }
    // for (InspireConformity sosConformity :
    // sosInspireExtendedCapabilities.getConformity()) {
    // Conformity conformity = objectFactoryInspireCommon.createConformity();
    // conformity.setSpecification(createCitationConformity(sosConformity.getInspireSpecification()));
    // conformity.setDegree(DegreeOfConformity.fromValue(sosConformity.getInspireDegreeOfConformity().name()));
    // content.add(objectFactoryInspireCommon.createExtendedCapabilitiesTypeConformity(conformity));
    // }
    // }
    //
    // private void addMetadataPointOfContact(FullInspireExtendedCapabilities
    // sosInspireExtendedCapabilities,
    // List<JAXBElement<?>> content) throws CodedException {
    // if (!sosInspireExtendedCapabilities.isSetMetadataPointOfContact()) {
    // throw new
    // NoApplicableCodeException().withMessage("Required metadata point of contact is missing!");
    // }
    // for (InspireMetadataPointOfContact sosMetadataPointOfContact :
    // sosInspireExtendedCapabilities
    // .getMetadataPointOfContacts()) {
    // MetadataPointOfContact metadataPointOfContact =
    // objectFactoryInspireCommon.createMetadataPointOfContact();
    // metadataPointOfContact.setOrganisationName(sosMetadataPointOfContact.getOrganisationName());
    // metadataPointOfContact.setEmailAddress(sosMetadataPointOfContact.getEmailAddress());
    // content.add(objectFactoryInspireCommon
    // .createExtendedCapabilitiesTypeMetadataPointOfContact(metadataPointOfContact));
    // }
    // }
    //
    // private void addMetadataDate(FullInspireExtendedCapabilities
    // sosInspireExtendedCapabilities,
    // List<JAXBElement<?>> content) throws CodedException {
    // if (!sosInspireExtendedCapabilities.isSetMetadataDate()) {
    // throw new
    // NoApplicableCodeException().withMessage("Required metadata date is missing!");
    // }
    // content.add(objectFactoryInspireCommon.createExtendedCapabilitiesTypeMetadataDate(DateTimeHelper
    // .formatDateTime2String(sosInspireExtendedCapabilities.getMetadataDate().getTimePosition())));
    // }
    //
    // private void addSpatialDataServiceType(FullInspireExtendedCapabilities
    // sosInspireExtendedCapabilities,
    // List<JAXBElement<?>> content) throws CodedException {
    // if (!sosInspireExtendedCapabilities.isSetSpatialDataServiceType()) {
    // throw new
    // NoApplicableCodeException().withMessage("Required spatial data service type is missing!");
    // }
    // content.add(objectFactoryInspireCommon
    // .createExtendedCapabilitiesTypeSpatialDataServiceType(SpatialDataServiceType
    // .fromValue(sosInspireExtendedCapabilities.getSpatialDataServiceType().name())));
    // }
    //
    // private void addMandatoryKeyword(FullInspireExtendedCapabilities
    // sosInspireExtendedCapabilities,
    // List<JAXBElement<?>> content) throws CodedException {
    // if (!sosInspireExtendedCapabilities.isSetMandatoryKeyword()) {
    // throw new
    // NoApplicableCodeException().withMessage("Required mandatory keyword is missing!");
    // }
    // for (InspireMandatoryKeyword sosMandatoryKeyword :
    // sosInspireExtendedCapabilities.getMandatoryKeywords()) {
    // ClassificationOfSpatialDataService classificationOfSpatialDataService =
    // objectFactoryInspireCommon.createClassificationOfSpatialDataService();
    //
    // addOriginatingControlledVocabularyToKeyword(classificationOfSpatialDataService,
    // sosMandatoryKeyword);
    // classificationOfSpatialDataService.setKeywordValue(sosMandatoryKeyword.getKeywordValue().name());
    // content.add(objectFactoryInspireCommon
    // .createExtendedCapabilitiesTypeMandatoryKeyword(classificationOfSpatialDataService));
    // }
    // }
    //
    // private void addKeyword(FullInspireExtendedCapabilities
    // sosInspireExtendedCapabilities,
    // List<JAXBElement<?>> content) throws DateTimeFormatException {
    // if (sosInspireExtendedCapabilities.isSetKeywords()) {
    // for (org.n52.sos.inspire.InspireKeyword sosKeyword :
    // sosInspireExtendedCapabilities.getKeywords()) {
    // InspireKeyword inspireKeyword =
    // objectFactoryInspireCommon.createInspireKeyword();
    // addOriginatingControlledVocabularyToKeyword(inspireKeyword, sosKeyword);
    // inspireKeyword.setKeywordValue(sosKeyword.getKeywordValue());
    // content.add(objectFactoryInspireCommon.createExtendedCapabilitiesTypeKeyword(inspireKeyword));
    // }
    // }
    // }
    //
    // private void addOriginatingControlledVocabularyToKeyword(InspireKeyword
    // inspireKeyword,
    // AbstractInspireKeyword<?> sosKeyword) throws DateTimeFormatException {
    // if (sosKeyword.isSetOriginatingControlledVocabulary()) {
    // inspireKeyword.setOriginatingControlledVocabulary(createOriginatingControlledVocabulary(sosKeyword
    // .getOriginatingControlledVocabulary()));
    // }
    // }
    //
    // private void
    // addSupportedLanguages(InspireExtendedCapabilitiesSupportedLanguage
    // sosInspireExtendedCapabilities,
    // List<JAXBElement<?>> content) throws CodedException {
    // if (!sosInspireExtendedCapabilities.isSetSupportedLanguages()) {
    // throw new
    // NoApplicableCodeException().withMessage("Required supported languages is missing!");
    // }
    // InspireSupportedLanguages sosSupportedLanguages =
    // sosInspireExtendedCapabilities.getSupportedLanguages();
    // SupportedLanguagesType supportedLanguagesType =
    // objectFactoryInspireCommon.createSupportedLanguagesType();
    // supportedLanguagesType.setDefaultLanguage(createLanguageElementISO6392B(sosSupportedLanguages
    // .getDefaultLanguage()));
    // if (sosSupportedLanguages.isSetSupportedLanguages()) {
    // for (InspireLanguageISO6392B sosLanguage :
    // sosSupportedLanguages.getSupportedLanguages()) {
    // supportedLanguagesType.getSupportedLanguage().add(createLanguageElementISO6392B(sosLanguage));
    // }
    // }
    // content.add(objectFactoryInspireCommon
    // .createExtendedCapabilitiesTypeSupportedLanguages(supportedLanguagesType));
    // }
    //
    // private void
    // addResponseLanguage(InspireExtendedCapabilitiesResponseLanguage
    // sosInspireExtendedCapabilities,
    // List<JAXBElement<?>> content) throws CodedException {
    // if (!sosInspireExtendedCapabilities.isSetResponseLanguage()) {
    // throw new
    // NoApplicableCodeException().withMessage("Required response languages is missing!");
    // }
    // content.add(objectFactoryInspireCommon
    // .createExtendedCapabilitiesTypeResponseLanguage(createLanguageElementISO6392B(sosInspireExtendedCapabilities
    // .getResponseLanguage())));
    // }
    //
    // private void
    // addSpatialDataSetIdentifier(Set<InspireUniqueResourceIdentifier>
    // inspireUniqueResourceIdentifier,
    // List<JAXBElement<?>> content) {
    // for (InspireUniqueResourceIdentifier uniqueResourceIdentifier :
    // inspireUniqueResourceIdentifier) {
    // UniqueResourceIdentifier createUniqueResourceIdentifier =
    // objectFactoryInspireCommon.createUniqueResourceIdentifier();
    // createUniqueResourceIdentifier.setCode(uniqueResourceIdentifier.getCode());
    // if (uniqueResourceIdentifier.isSetNamespace()) {
    // createUniqueResourceIdentifier.setNamespace((uniqueResourceIdentifier.getNamespace()));
    // }
    // if (uniqueResourceIdentifier.isSetMetadataUrl()) {
    // createUniqueResourceIdentifier.setMetadataURL(uniqueResourceIdentifier.getMetadataUrl());
    // }
    // content.add(extendedObjectFactoryInspireDls
    // .createInspireExtendedCapabilitiesTypeSpatialDataSetIdentifier(createUniqueResourceIdentifier));
    // }
    // }
    //
    // private void addMetadataUrl(InspireResourceLocator
    // inspireResourceLocator, List<JAXBElement<?>> content) {
    // content.add(objectFactoryInspireCommon
    // .createExtendedCapabilitiesTypeMetadataUrl(createResourceLocatorType(inspireResourceLocator)));
    // }
    //
    // private eu.europa.ec.inspire.schemas.common._1.MediaType
    // getMediaType(MediaType mediaType) {
    // return
    // eu.europa.ec.inspire.schemas.common._1.MediaType.fromValue(mediaType.toString());
    // }
    //
    // private XmlObject convertDocumentToXmlObject(JAXBElement<?> document)
    // throws OwsExceptionReport,
    // MalformedURLException, SAXException {
    // try {
    // Class<?> clazz = document.getValue().getClass();
    // JAXBContext context =
    // JAXBContext.newInstance(clazz.getPackage().getName());
    // SchemaFactory sf = SchemaFactory.newInstance(W3CConstants.NS_XS);
    // sf.newSchema();
    // Schema schema = sf.newSchema(new
    // URL(InspireConstants.SCHEMA_LOCATION_URL_INSPIRE_DLS));
    // Marshaller m = context.createMarshaller();
    // m.setSchema(schema);
    // m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    // final StringWriter writer = new StringWriter();
    // m.marshal(document, writer);
    // return XmlObject.Factory.parse(writer.toString());
    // } catch (JAXBException jaxbe) {
    // throw new
    // NoApplicableCodeException().causedBy(jaxbe).withMessage("Error while marshalling JAXBContext!");
    // } catch (XmlException xmle) {
    // throw new NoApplicableCodeException().causedBy(xmle).withMessage(
    // "Error while converting JAXBContext object to XmlObject!");
    // }
    // }
    //
    // private ResourceLocatorType
    // createResourceLocatorType(InspireResourceLocator sosResourceLocator) {
    // ResourceLocatorType resourceLocator =
    // objectFactoryInspireCommon.createResourceLocatorType();
    // if (sosResourceLocator.isSetUrl()) {
    // resourceLocator.setURL(sosResourceLocator.getURL());
    // }
    // if (sosResourceLocator.isSetMediaTypes()) {
    // for (MediaType mediaType : sosResourceLocator.getMediaTypes()) {
    // resourceLocator.getMediaType().add(getMediaType(mediaType));
    // }
    // }
    // return resourceLocator;
    // }
    //
    // private void addValuesToCitation(Citation citation, InspireCitation
    // sosInspireCitation)
    // throws DateTimeFormatException {
    // citation.setTitle(sosInspireCitation.getTitle());
    // TimeInstant dateOf = (TimeInstant) sosInspireCitation.getDateOf();
    // if (sosInspireCitation.getDateOf() instanceof InspireDateOfCreation) {
    // citation.setDateOfCreation(DateTimeHelper.formatDateTime2String(dateOf.getTimePosition()));
    // } else if (sosInspireCitation.getDateOf() instanceof
    // InspireDateOfLastRevision) {
    // citation.setDateOfLastRevision(DateTimeHelper.formatDateTime2String(dateOf.getTimePosition()));
    // } else if (sosInspireCitation.getDateOf() instanceof
    // InspireDateOfPublication) {
    // citation.setDateOfPublication(DateTimeHelper.formatDateTime2String(dateOf.getTimePosition()));
    // }
    // if (sosInspireCitation.isSetUrls()) {
    // for (String url : sosInspireCitation.getUrls()) {
    // citation.getURI().add(url);
    // }
    // }
    // if (sosInspireCitation.isSetResourceLocators()) {
    // for (InspireResourceLocator sosResourceLocator :
    // sosInspireCitation.getResourceLocator()) {
    // citation.getResourceLocator().add(createResourceLocatorType(sosResourceLocator));
    // }
    // }
    // }
    //
    // private CitationConformity createCitationConformity(InspireCitation
    // sosInspireSpecification)
    // throws DateTimeFormatException {
    // CitationConformity citationConformity =
    // objectFactoryInspireCommon.createCitationConformity();
    // addValuesToCitation(citationConformity, sosInspireSpecification);
    // return citationConformity;
    // }
    //
    // private OriginatingControlledVocabulary
    // createOriginatingControlledVocabulary(
    // InspireOriginatingControlledVocabulary
    // sosOriginatingControlledVocabulary) throws DateTimeFormatException {
    // OriginatingControlledVocabulary originatingControlledVocabulary =
    // objectFactoryInspireCommon.createOriginatingControlledVocabulary();
    // addValuesToCitation(originatingControlledVocabulary,
    // sosOriginatingControlledVocabulary);
    // return originatingControlledVocabulary;
    // }
    //
    // private LanguageElementISO6392B
    // createLanguageElementISO6392B(InspireLanguageISO6392B sosLanguage) {
    // LanguageElementISO6392B languageElementISO6392B =
    // objectFactoryInspireCommon.createLanguageElementISO6392B();
    // languageElementISO6392B.setLanguage(sosLanguage.value());
    // return languageElementISO6392B;
    // }

}
