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

import java.io.OutputStream;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.n52.sos.exception.ows.concrete.DateTimeFormatException;
import org.n52.sos.inspire.InspireCitation;
import org.n52.sos.inspire.InspireConformity;
import org.n52.sos.inspire.InspireConformity.InspireDegreeOfConformity;
import org.n52.sos.inspire.InspireConformityCitation;
import org.n52.sos.inspire.InspireConstants;
import org.n52.sos.inspire.InspireDateOf;
import org.n52.sos.inspire.InspireDateOfCreation;
import org.n52.sos.inspire.InspireDateOfLastRevision;
import org.n52.sos.inspire.InspireDateOfPublication;
import org.n52.sos.inspire.InspireKeyword;
import org.n52.sos.inspire.InspireLanguageISO6392B;
import org.n52.sos.inspire.InspireMandatoryKeyword;
import org.n52.sos.inspire.InspireMandatoryKeywordValue;
import org.n52.sos.inspire.InspireMetadataPointOfContact;
import org.n52.sos.inspire.InspireObject;
import org.n52.sos.inspire.InspireOriginatingControlledVocabulary;
import org.n52.sos.inspire.InspireResourceLocator;
import org.n52.sos.inspire.InspireSupportedCRS;
import org.n52.sos.inspire.InspireSupportedLanguages;
import org.n52.sos.inspire.InspireTemporalReference;
import org.n52.sos.inspire.InspireUniqueResourceIdentifier;
import org.n52.sos.inspire.capabilities.FullInspireExtendedCapabilities;
import org.n52.sos.inspire.capabilities.InspireCapabilities.InspireServiceSpatialDataResourceType;
import org.n52.sos.inspire.capabilities.InspireCapabilities.InspireSpatialDataServiceType;
import org.n52.sos.inspire.capabilities.MinimalInspireExtendedCapabilities;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.gml.time.TimePosition;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.http.MediaType;

import com.google.common.html.HtmlEscapers;

/**
 * XML stream writer for INSPIRE DLS ExtendedCapabilities
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 * 
 */
public class InspireXmlStreamWriter extends XmlStreamWriter<InspireObject> implements InspireConstants {

    private InspireObject inspireObject;

    /**
     * constructor
     * 
     * @param inspireObject
     *            SOS internal representation of the INSPIRE object to encode
     */
    public InspireXmlStreamWriter(InspireObject inspireObject) {
        this.setInspireObject(inspireObject);
    }

    @Override
    public void write(OutputStream out) throws XMLStreamException, DateTimeFormatException {
        init(out);
        if (getInspireObject() instanceof FullInspireExtendedCapabilities) {
            writeFullInspireExtendedCapabilities((FullInspireExtendedCapabilities) getInspireObject());
        } else if (getInspireObject() instanceof MinimalInspireExtendedCapabilities) {
            writeMinimlaInspireExtendedCapabilities((MinimalInspireExtendedCapabilities) getInspireObject());
        } else if (getInspireObject() instanceof InspireSupportedLanguages) {
            writeSupportedLanguages((InspireSupportedLanguages) getInspireObject(), true);
        } else if (getInspireObject() instanceof InspireSupportedCRS) {
            writeSupportedCRS((InspireSupportedCRS) getInspireObject(), true);
        }
        finish();
    }

    @Override
    public void write(OutputStream out, EncodingValues encodingValues) throws XMLStreamException, OwsExceptionReport {
      write(out);
    }

    @Override
    public void write(InspireObject elementToStream, OutputStream out) throws XMLStreamException, OwsExceptionReport {
        this.inspireObject = elementToStream;
        write(out);
    }

    @Override
    public void write(InspireObject elementToStream, OutputStream out, EncodingValues encodingValues) throws XMLStreamException,
            OwsExceptionReport {
        this.inspireObject = elementToStream;
        write(out);
        
    }

    /**
     * Get the INSPIRE DLS ExtendedCapabilities to write
     * 
     * @return the INSPIRE DLS ExtendedCapabilities to write
     */
    private InspireObject getInspireObject() {
        return inspireObject;
    }

    /**
     * Get the INSPIRE object to write
     * 
     * @param inspireObject
     *            the INSPIRE object to set
     */
    private void setInspireObject(InspireObject inspireObject) {
        this.inspireObject = inspireObject;
    }

    /**
     * Write minimal INSPIRE DLS ExtendedCapabilities
     * 
     * @param minimalInspireExtendedCapabilities
     *            INSPIRE DLS ExtendedCapabilities to write
     * @throws XMLStreamException
     *             If an error occurs when writing the INSPIRE DLS
     *             ExtendedCapabilities to stream
     */
    private void writeMinimlaInspireExtendedCapabilities(
            MinimalInspireExtendedCapabilities minimalInspireExtendedCapabilities) throws XMLStreamException {
        start(QN_EXTENDED_CAPABILITIES);
        writeInspireCommonNamespaces(true);
        writeInspireDLSNamespaces();
        writeNewLine();
        writeMetadataUrl(minimalInspireExtendedCapabilities.getMetadataUrl());
        writeNewLine();
        writeSupportedLanguages(minimalInspireExtendedCapabilities.getSupportedLanguages(), false);
        writeNewLine();
        writeResponseLanguage(minimalInspireExtendedCapabilities.getResponseLanguage());
        writeNewLine();
        for (InspireUniqueResourceIdentifier inspireUniqueResourceIdentifier : minimalInspireExtendedCapabilities
                .getSpatialDataSetIdentifier()) {
            writeSpatialDataSetIdentifier(inspireUniqueResourceIdentifier);
            writeNewLine();
        }
        writeSupportedCRS(minimalInspireExtendedCapabilities.getSupportedCRS(), false);
        writeNewLine();
        end(QN_EXTENDED_CAPABILITIES);
    }

    /**
     * Write full INSPIRE DLS ExtendedCapabilities
     * 
     * @param fullInspireExtendedCapabilities
     *            INSPIRE DLS ExtendedCapabilities to write
     * @throws XMLStreamException
     *             If an error occurs when writing the INSPIRE DLS
     *             ExtendedCapabilities to stream
     */
    private void writeFullInspireExtendedCapabilities(FullInspireExtendedCapabilities fullInspireExtendedCapabilities)
            throws XMLStreamException {
        start(QN_EXTENDED_CAPABILITIES);
        writeInspireCommonNamespaces(true);
        writeInspireDLSNamespaces();
        writeNewLine();
        for (InspireResourceLocator resourceLocator : fullInspireExtendedCapabilities.getResourceLocator()) {
            writeResourceLocator(resourceLocator);
            writeNewLine();
        }
        writeResourceType(fullInspireExtendedCapabilities.getResourceType());
        writeNewLine();
        writeTemporalReference(fullInspireExtendedCapabilities.getTemporalReferences());
        for (InspireConformity conformity : fullInspireExtendedCapabilities.getConformity()) {
            writeConformity(conformity);
            writeNewLine();
        }
        for (InspireMetadataPointOfContact metadataPointOfContact : fullInspireExtendedCapabilities
                .getMetadataPointOfContacts()) {
            writeMetadataPointOfContact(metadataPointOfContact);
            writeNewLine();
        }
        writeMetadataDate(fullInspireExtendedCapabilities.getMetadataDate());
        writeNewLine();
        writeSpatialDataServiceType(fullInspireExtendedCapabilities.getSpatialDataServiceType());
        writeNewLine();
        for (InspireMandatoryKeyword mandatoryKeyword : fullInspireExtendedCapabilities.getMandatoryKeywords()) {
            writeMandatoryKeyword(mandatoryKeyword);
            writeNewLine();
        }
        if (fullInspireExtendedCapabilities.isSetKeywords()) {
            for (InspireKeyword keyword : fullInspireExtendedCapabilities.getKeywords()) {
                writeKeyword(keyword);
                writeNewLine();
            }
        }
        writeSupportedLanguages(fullInspireExtendedCapabilities.getSupportedLanguages(), false);
        writeNewLine();
        writeResponseLanguage(fullInspireExtendedCapabilities.getResponseLanguage());
        writeNewLine();
        if (fullInspireExtendedCapabilities.isSetMetadataUrl()) {
            writeMetadataUrl(fullInspireExtendedCapabilities.getMetadataUrl());
            writeNewLine();
        }
        for (InspireUniqueResourceIdentifier inspireUniqueResourceIdentifier : fullInspireExtendedCapabilities
                .getSpatialDataSetIdentifier()) {
            writeSpatialDataSetIdentifier(inspireUniqueResourceIdentifier);
            writeNewLine();
        }
        writeSupportedCRS(fullInspireExtendedCapabilities.getSupportedCRS(), false);
        writeNewLine();
        end(QN_EXTENDED_CAPABILITIES);
    }

    /**
     * Write {@link InspireCitation} to stream
     * 
     * @param citation
     *            {@link InspireCitation} to write to stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeCitationContent(InspireCitation citation) throws XMLStreamException {
        writeTitle(citation.getTitle());
        writeDateOf(citation.getDateOf());
        writeNewLine();
        if (citation.isSetUrls()) {
            for (String url : citation.getUrls()) {
                writeURI(url);
                writeNewLine();
            }
        }
        if (citation.isSetResourceLocators()) {
            for (InspireResourceLocator resourceLocator : citation.getResourceLocator()) {
                writeResourceLocator(resourceLocator);
                writeNewLine();
            }
        }
    }

    /**
     * Write code element to stream
     * 
     * @param code
     *            element value
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeCode(String code) throws XMLStreamException {
        start(QN_CODE);
        chars(code);
        endInline(QN_CODE);
    }

    /**
     * Write {@link InspireConformity} to stream
     * 
     * @param conformity
     *            {@link InspireConformity} to write to stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeConformity(InspireConformity conformity) throws XMLStreamException {
        start(QN_CONFORMITY);
        writeNewLine();
        writeSpecification(conformity.getInspireSpecification());
        writeNewLine();
        writeDegree(conformity.getInspireDegreeOfConformity());
        writeNewLine();
        end(QN_CONFORMITY);

    }

    /**
     * Write {@link InspireLanguageISO6392B} as default language element to
     * stream
     * 
     * @param defaultLanguage
     *            {@link InspireLanguageISO6392B} to write to stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeDefaultLanguage(InspireLanguageISO6392B defaultLanguage) throws XMLStreamException {
        start(QN_DEFAULT_LANGUAGE);
        writeNewLine();
        writeLanguage(defaultLanguage);
        writeNewLine();
        end(QN_DEFAULT_LANGUAGE);
    }

    /**
     * Write {@link InspireDegreeOfConformity} to stream
     * 
     * @param inspireDegreeOfConformity
     *            {@link InspireDegreeOfConformity} to write to stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeDegree(InspireDegreeOfConformity inspireDegreeOfConformity) throws XMLStreamException {
        start(QN_DEGREE);
        chars(inspireDegreeOfConformity.toString());
        endInline(QN_DEGREE);
    }

    /**
     * Write {@link InspireDateOf} to stream
     * 
     * @param dateOf
     *            {@link InspireDateOf} to write to stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeDateOf(InspireDateOf dateOf) throws XMLStreamException {
        if (dateOf instanceof InspireDateOfPublication) {
            writeDateOfPublication((InspireDateOfPublication) dateOf);
        } else if (dateOf instanceof InspireDateOfCreation) {
            writeDateOfCreation((InspireDateOfCreation) dateOf);
        } else if (dateOf instanceof InspireDateOfLastRevision) {
            writeDateOfLastRevision((InspireDateOfLastRevision) dateOf);
        }
    }

    /**
     * Write {@link InspireDateOfPublication} to stream
     * 
     * @param dateOf
     *            {@link InspireDateOfPublication} to write to stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeDateOfPublication(InspireDateOfPublication dateOf) throws XMLStreamException {
        start(QN_DATE_OF_PUBLICATION);
        time(dateOf);
        endInline(QN_DATE_OF_PUBLICATION);
    }

    /**
     * Write {@link InspireDateOfCreation} to stream
     * 
     * @param dateOf
     *            {@link InspireDateOfCreation} to write to stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeDateOfCreation(InspireDateOfCreation dateOf) throws XMLStreamException {
        start(QN_DATE_OF_CREATION);
        time(dateOf);
        endInline(QN_DATE_OF_CREATION);
    }

    /**
     * Write {@link InspireDateOfLastRevision} to stream
     * 
     * @param dateOf
     *            {@link InspireDateOfLastRevision} to write to stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeDateOfLastRevision(InspireDateOfLastRevision dateOf) throws XMLStreamException {
        start(QN_DATE_OF_LAST_REVISION);
        time(dateOf);
        endInline(QN_DATE_OF_LAST_REVISION);
    }

    /**
     * Write email address element to stream
     * 
     * @param emailAddress
     *            email adress element to write to stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeEmailAddress(String emailAddress) throws XMLStreamException {
        start(QN_EMAIL_ADDRESS);
        chars(emailAddress);
        endInline(QN_EMAIL_ADDRESS);
    }

    /**
     * Write {@link TimePosition} as end date element to stream
     * 
     * @param time
     *            {@link TimePosition} to write as end date element to stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeEndDate(TimePosition time) throws XMLStreamException {
        start(QN_END_DATE);
        time(time);
        endInline(QN_END_DATE);

    }

    /**
     * Write {@link TimeInstant} as individual date element to stream
     * 
     * @param time
     *            {@link TimeInstant} to write as individual date element to
     *            stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeIndividualDate(TimeInstant time) throws XMLStreamException {
        start(QN_INDIVIDUAL_DATE);
        time(time);
        endInline(QN_INDIVIDUAL_DATE);

    }

    /**
     * Write namespaces to stream
     * 
     * @param root
     *            indicator if this is a root element and namespaces should be
     *            added
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeInspireDLSNamespaces() throws XMLStreamException {
        namespace(NS_INSPIRE_DLS_PREFIX, NS_INSPIRE_DLS);
    }
    
    /**
     * Write namespaces to stream
     * 
     * @param root
     *            indicator if this is a root element and namespaces should be
     *            added
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeInspireCommonNamespaces(boolean root) throws XMLStreamException {
        if (root) {
            namespace(NS_INSPIRE_COMMON_PREFIX, NS_INSPIRE_COMMON);
        }
    }

    /**
     * Write {@link TimePeriod} as interval of dates element to stream
     * 
     * @param time
     *            {@link TimePeriod} to write as interval of dates element to
     *            stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeIntervalOfTime(TimePeriod time) throws XMLStreamException {
        start(QN_INTERVAL_OF_DATES);
        writeStartingDate(time.getStartTimePosition());
        writeEndDate(time.getEndTimePosition());
        end(QN_INTERVAL_OF_DATES);

    }

    /**
     * Write {@link InspireKeyword} to stream
     * 
     * @param keyword
     *            {@link InspireKeyword} to write to stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeKeyword(InspireKeyword keyword) throws XMLStreamException {
        if (keyword.isSetOriginatingControlledVocabulary()) {
            writeOriginatingControlledVocabulary(keyword.getOriginatingControlledVocabulary());
        }
        start(QN_KEYWORD);
        writeKeywordValue(keyword.getKeywordValue());
        writeNewLine();
        end(QN_KEYWORD);

    }

    /**
     * Write keyword value element to stream
     * 
     * @param keywordValue
     *            keyword value element to write to stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeKeywordValue(String keywordValue) throws XMLStreamException {
        start(QN_KEYWORD_VALUE);
        chars(keywordValue);
        endInline(QN_KEYWORD_VALUE);
    }

    /**
     * Write {@link InspireMandatoryKeywordValue} to stream
     * 
     * @param keywordValue
     *            {@link InspireMandatoryKeywordValue} to write to stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeKeywordValue(InspireMandatoryKeywordValue keywordValue) throws XMLStreamException {
        writeKeywordValue(keywordValue.toString());
    }

    /**
     * Write {@link InspireLanguageISO6392B} to stream
     * 
     * @param language
     *            {@link InspireLanguageISO6392B} to write to stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeLanguage(InspireLanguageISO6392B language) throws XMLStreamException {
        start(QN_LANGUAGE);
        chars(language.value());
        endInline(QN_LANGUAGE);
    }

    /**
     * Write {@link InspireMandatoryKeyword} to stream
     * 
     * @param mandatoryKeyword
     *            {@link InspireMandatoryKeyword} to write to stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeMandatoryKeyword(InspireMandatoryKeyword mandatoryKeyword) throws XMLStreamException {
        start(QN_MANDATORY_KEYWORD);
        writeNewLine();
        writeKeywordValue(mandatoryKeyword.getKeywordValue());
        writeNewLine();
        end(QN_MANDATORY_KEYWORD);
    }

    /**
     * Write {@link MediaType} to stream
     * 
     * @param mediaType
     *            {@link MediaType} to write to stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeMediaType(MediaType mediaType) throws XMLStreamException {
        start(QN_MEDIA_TYPE);
        chars(mediaType.toString());
        endInline(QN_MEDIA_TYPE);
    }

    /**
     * Write {@link TimeInstant} as metadata date element to stream
     * 
     * @param metadataDate
     *            {@link TimeInstant} to write as metadata date element to
     *            stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeMetadataDate(TimeInstant metadataDate) throws XMLStreamException {
        start(QN_METADATA_DATE);
        time(metadataDate);
        endInline(QN_METADATA_DATE);
    }

    /**
     * Write {@link InspireMetadataPointOfContact} to stream
     * 
     * @param metadataPointOfContact
     *            {@link InspireMetadataPointOfContact} to write to stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeMetadataPointOfContact(InspireMetadataPointOfContact metadataPointOfContact)
            throws XMLStreamException {
        start(QN_METADATA_POINT_OF_CONTACT);
        writeNewLine();
        writeOrganisationName(metadataPointOfContact.getOrganisationName());
        writeNewLine();
        writeEmailAddress(metadataPointOfContact.getEmailAddress());
        writeNewLine();
        end(QN_METADATA_POINT_OF_CONTACT);
    }

    /**
     * Write {@link InspireResourceLocator} as metadata URL element to stream
     * 
     * @param metadataUrl
     *            {@link InspireResourceLocator} to write as metadata URL
     *            element to stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeMetadataUrl(InspireResourceLocator metadataUrl) throws XMLStreamException {
        start(QN_METADATA_URL);
        writeNewLine();
        writeResourceLocatorContent(metadataUrl);
        end(QN_METADATA_URL);
    }

    /**
     * Write namespace element to stream
     * 
     * @param namespace
     *            namespace element value to write to stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeNamespace(String namespace) throws XMLStreamException {
        start(QN_NAMESPACE);
        chars(namespace);
        end(QN_NAMESPACE);
    }

    /**
     * Write {@link InspireOriginatingControlledVocabulary} to stream
     * 
     * @param originatingControlledVocabulary
     *            {@link InspireOriginatingControlledVocabulary} to write to
     *            stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeOriginatingControlledVocabulary(
            InspireOriginatingControlledVocabulary originatingControlledVocabulary) throws XMLStreamException {
        start(QN_ORIGINATING_CONTROLLED_VOCABULARY);
        writeCitationContent(originatingControlledVocabulary);
        end(QN_ORIGINATING_CONTROLLED_VOCABULARY);
    }

    /**
     * Write organisation name element to stream
     * 
     * @param organisationName
     *            organisation name element value to write to stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeOrganisationName(String organisationName) throws XMLStreamException {
        start(QN_ORGANISATION_NAME);
        chars(organisationName);
        endInline(QN_SPATIAL_DATA_SERVICE_TYPE);
    }

    /**
     * Write {@link InspireLanguageISO6392B} as response language element to
     * stream
     * 
     * @param responseLanguage
     *            {@link InspireLanguageISO6392B} to write as response language
     *            element to stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeResponseLanguage(InspireLanguageISO6392B responseLanguage) throws XMLStreamException {
        start(QN_RESPONSE_LANGUAGE);
        writeNewLine();
        writeLanguage(responseLanguage);
        writeNewLine();
        end(QN_RESPONSE_LANGUAGE);

    }

    /**
     * Write {@link InspireResourceLocator} to stream
     * 
     * @param resourceLocator
     *            {@link InspireResourceLocator} to write to stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeResourceLocatorContent(InspireResourceLocator resourceLocator) throws XMLStreamException {
        writeUrl(resourceLocator.getURL());
        writeNewLine();
        if (resourceLocator.isSetMediaTypes()) {
            for (MediaType mediaType : resourceLocator.getMediaTypes()) {
                writeMediaType(mediaType);
                writeNewLine();
            }
        }
    }

    /**
     * Write {@link InspireResourceLocator} to stream
     * 
     * @param resourceLocator
     *            {@link InspireResourceLocator} to write to stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeResourceLocator(InspireResourceLocator resourceLocator) throws XMLStreamException {
        start(QN_RESOURCE_LOCATOR);
        writeNewLine();
        writeResourceLocatorContent(resourceLocator);
        indent--;
        end(QN_RESOURCE_LOCATOR);
        indent++;
    }

    /**
     * Write {@link InspireServiceSpatialDataResourceType} as resource type
     * element to stream
     * 
     * @param resourceType
     *            {@link InspireServiceSpatialDataResourceType} to write as
     *            resource type element to stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeResourceType(InspireServiceSpatialDataResourceType resourceType) throws XMLStreamException {
        start(QN_RESOURCE_TYPE);
        chars(resourceType.toString());
        endInline(QN_RESOURCE_TYPE);
    }

    /**
     * Write {@link InspireSupportedLanguages} to stream
     * 
     * @param supportedLanguages
     *            {@link InspireSupportedLanguages} to write to stream
     * @param root
     *            indicator if this is a root element and namespaces should be
     *            added
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeSupportedLanguages(InspireSupportedLanguages supportedLanguages, boolean root)
            throws XMLStreamException {
        start(QN_SUPPORTED_LANGUAGES);
        writeInspireCommonNamespaces(root);
        writeDefaultLanguage(supportedLanguages.getDefaultLanguage());
        writeNewLine();
        if (supportedLanguages.isSetSupportedLanguages()) {
            for (InspireLanguageISO6392B supportedLanguage : supportedLanguages.getSupportedLanguages()) {
                writeSupportedLanguage(supportedLanguage);
                writeNewLine();
            }
        }
        end(QN_SUPPORTED_LANGUAGES);

    }

    /**
     * Write {@link InspireSpatialDataServiceType} to stream
     * 
     * @param spatialDataServiceType
     *            {@link InspireSpatialDataServiceType} to write to stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeSpatialDataServiceType(InspireSpatialDataServiceType spatialDataServiceType)
            throws XMLStreamException {
        start(QN_SPATIAL_DATA_SERVICE_TYPE);
        chars(spatialDataServiceType.toString());
        endInline(QN_SPATIAL_DATA_SERVICE_TYPE);

    }

    /**
     * Write {@link InspireUniqueResourceIdentifier} as spatial dataset
     * identifier element to stream
     * 
     * @param inspireUniqueResourceIdentifier
     *            {@link InspireUniqueResourceIdentifier} to write as spatial
     *            dataset identifier element to stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeSpatialDataSetIdentifier(InspireUniqueResourceIdentifier inspireUniqueResourceIdentifier)
            throws XMLStreamException {
        start(QN_SPATIAL_DATASET_IDENTIFIER);
        writeNewLine();
        writeUniqueResourceIdentifierContent(inspireUniqueResourceIdentifier);
        writeNewLine();
        endInline(QN_SPATIAL_DATASET_IDENTIFIER);
    }

    /**
     * Write {@link InspireConformityCitation} as specification element to
     * stream
     * 
     * @param inspireSpecification
     *            {@link InspireConformityCitation} to write as specification
     *            element to stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeSpecification(InspireConformityCitation inspireSpecification) throws XMLStreamException {
        start(QN_SPECIFICATION);
        writeNewLine();
        writeCitationContent(inspireSpecification);
        end(QN_SPECIFICATION);
    }

    /**
     * Write {@link TimePosition} as starting date element to stream
     * 
     * @param time
     *            {@link TimePosition} to write as starting date element to
     *            stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeStartingDate(TimePosition time) throws XMLStreamException {
        start(QN_STARTING_DATE);
        time(time);
        endInline(QN_STARTING_DATE);

    }

    /**
     * Write {@link InspireLanguageISO6392B} as supported language element to
     * stream
     * 
     * @param supportedLanguage
     *            {@link InspireLanguageISO6392B} to write as supported language
     *            element to stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeSupportedLanguage(InspireLanguageISO6392B supportedLanguage) throws XMLStreamException {
        start(QN_SUPPORTED_LANGUAGE);
        writeNewLine();
        writeLanguage(supportedLanguage);
        writeNewLine();
        end(QN_SUPPORTED_LANGUAGE);

    }

    /**
     * Write {@link Time} as temporal extent element to stream
     * 
     * @param temporalExtent
     *            {@link Time} to write as temporal extent element to stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeTemporalExtent(Time temporalExtent) throws XMLStreamException {
        start(QN_TEMPORAL_EXTENT);
        if (temporalExtent instanceof TimeInstant) {
            writeIndividualDate((TimeInstant) temporalExtent);
        } else if (temporalExtent instanceof TimePeriod) {
            writeIntervalOfTime((TimePeriod) temporalExtent);
        }
        end(QN_TEMPORAL_EXTENT);

    }

    /**
     * Write {@link InspireTemporalReference} to stream
     * 
     * @param temporalReference
     *            {@link InspireTemporalReference} to write to stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeTemporalReference(InspireTemporalReference temporalReference) throws XMLStreamException {
        if (!temporalReference.isSetDateOfCreation() && !temporalReference.isSetDateOfLastRevision()
                && !temporalReference.isSetDatesOfPublication() && !temporalReference.isSetTemporalExtents()) {
            empty(QN_TEMPORAL_REFERENCE);
        } else {
            start(QN_TEMPORAL_REFERENCE);
            if (temporalReference.isSetDateOfCreation()) {
                writeNewLine();
                writeDateOfCreation(temporalReference.getDateOfCreation());
            }
            if (temporalReference.isSetDateOfLastRevision()) {
                writeNewLine();
                writeDateOfLastRevision(temporalReference.getDateOfLastRevision());
            }
            if (temporalReference.isSetDatesOfPublication()) {
                for (InspireDateOfPublication dateOfPublication : temporalReference.getDatesOfPublication()) {
                    writeNewLine();
                    writeDateOfPublication(dateOfPublication);
                }
            }
            if (temporalReference.isSetTemporalExtents()) {
                writeNewLine();
                for (Time temporalExtent : temporalReference.getTemporalExtents()) {
                    writeNewLine();
                    writeTemporalExtent(temporalExtent);
                }
            }
            writeNewLine();
            end(QN_TEMPORAL_REFERENCE);
        }
    }

    /**
     * Write {@link InspireTemporalReference} to stream
     * 
     * @param temporalReferences
     *            {@link InspireTemporalReference} to write to stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeTemporalReference(List<InspireTemporalReference> temporalReferences) throws XMLStreamException {
        if (CollectionHelper.isNotEmpty(temporalReferences)) {
            for (InspireTemporalReference temporalReference : temporalReferences) {
                writeTemporalReference(temporalReference);
                writeNewLine();
            }
        } else {
            empty(QN_TEMPORAL_REFERENCE);
            writeNewLine();
        }
    }

    /**
     * Write title element to stream
     * 
     * @param title
     *            title element value to write to stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeTitle(String title) throws XMLStreamException {
        start(QN_TITLE);
        chars(title);
        endInline(QN_TITLE);
    }

    /**
     * Write {@link InspireUniqueResourceIdentifier} to stream
     * 
     * @param uniqueResourceIdentifier
     *            {@link InspireUniqueResourceIdentifier} to write to stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeUniqueResourceIdentifierContent(InspireUniqueResourceIdentifier uniqueResourceIdentifier)
            throws XMLStreamException {
        // TODO Check if metadataURL and/or Code/Namespace (attr(name, value);)
        writeCode(uniqueResourceIdentifier.getCode());
        if (uniqueResourceIdentifier.isSetNamespace()) {
            writeNewLine();
            writeNamespace(uniqueResourceIdentifier.getNamespace());
        }
    }

    /**
     * Write URI element to stream
     * 
     * @param url
     *            URI element value to write to stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeURI(String url) throws XMLStreamException {
        start(QN_URI);
        chars(HtmlEscapers.htmlEscaper().escape(url));
        endInline(QN_URI);
    }

    /**
     * Write URL element to stream
     * 
     * @param url
     *            URL element value to write to stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeUrl(String url) throws XMLStreamException {
        start(QN_URL);
        chars(HtmlEscapers.htmlEscaper().escape(url));
        endInline(QN_URL);
    }

    /**
     * Write {@link InspireSupportedCRS} to stream
     * 
     * @param supportedCRSs
     *            {@link InspireSupportedCRS} to write to stream
     * @param root
     *            indicator if this is a root element and namespaces should be
     *            added
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeSupportedCRS(InspireSupportedCRS supportedCRSes, boolean root) throws XMLStreamException {
        start(QN_SUPPORTED_CRS);
        if (root) {
            writeInspireDLSNamespaces();
        }
        writeNewLine();
        writeDefaultCRS(supportedCRSes.getDefaultCRS());
        writeNewLine();
        if (supportedCRSes.isSetSupportedCRSs()) {
            for (String supportedCRS : supportedCRSes.getOtherCRS()) {
                writeOtherCRS(supportedCRS);
                writeNewLine();
            }
        }
        end(QN_SUPPORTED_CRS);

    }

    /**
     * Write default CRS string as default CRS element to stream
     * 
     * @param defaultCRS
     *            default CRS string to write to stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeDefaultCRS(String crs) throws XMLStreamException {
        start(QN_DEFAULT_CRS);
        chars(crs);
        endInline(QN_DEFAULT_CRS);
    }

    /**
     * Write CRS string as other CRS element to stream
     * 
     * @param crs
     *            CRS string to write as other CRS element to stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeOtherCRS(String crs) throws XMLStreamException {
        start(QN_OTHER_CRS);
        chars(crs);
        endInline(QN_OTHER_CRS);
    }

    /**
     * Write CRS string to stream
     * 
     * @param CRS
     *            CRS string to write to stream
     * @throws XMLStreamException
     *             If an error occurs when writing the object to stream
     */
    private void writeCRS(String crs) throws XMLStreamException {
        start(QN_CRS);
        chars(crs);
        endInline(QN_CRS);
    }

}
