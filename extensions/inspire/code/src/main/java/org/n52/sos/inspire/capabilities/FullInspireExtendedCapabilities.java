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
package org.n52.sos.inspire.capabilities;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.n52.sos.inspire.InspireConformity;
import org.n52.sos.inspire.InspireKeyword;
import org.n52.sos.inspire.InspireLanguageISO6392B;
import org.n52.sos.inspire.InspireMandatoryKeyword;
import org.n52.sos.inspire.InspireMetadataPointOfContact;
import org.n52.sos.inspire.InspireResourceLocator;
import org.n52.sos.inspire.InspireSupportedCRS;
import org.n52.sos.inspire.InspireSupportedLanguages;
import org.n52.sos.inspire.InspireTemporalReference;
import org.n52.sos.inspire.InspireUniqueResourceIdentifier;
import org.n52.sos.inspire.capabilities.InspireCapabilities.InspireExtendedCapabilitiesConformity;
import org.n52.sos.inspire.capabilities.InspireCapabilities.InspireExtendedCapabilitiesKeyword;
import org.n52.sos.inspire.capabilities.InspireCapabilities.InspireExtendedCapabilitiesMandatoryKeyword;
import org.n52.sos.inspire.capabilities.InspireCapabilities.InspireExtendedCapabilitiesMetadataDate;
import org.n52.sos.inspire.capabilities.InspireCapabilities.InspireExtendedCapabilitiesMetadataPointOfContact;
import org.n52.sos.inspire.capabilities.InspireCapabilities.InspireExtendedCapabilitiesMetadataURL;
import org.n52.sos.inspire.capabilities.InspireCapabilities.InspireExtendedCapabilitiesResourceLocator;
import org.n52.sos.inspire.capabilities.InspireCapabilities.InspireExtendedCapabilitiesResourceType;
import org.n52.sos.inspire.capabilities.InspireCapabilities.InspireExtendedCapabilitiesSpatialDataServiceType;
import org.n52.sos.inspire.capabilities.InspireCapabilities.InspireExtendedCapabilitiesTemporalReference;
import org.n52.sos.inspire.capabilities.InspireCapabilities.InspireServiceSpatialDataResourceType;
import org.n52.sos.inspire.capabilities.InspireCapabilities.InspireSpatialDataServiceType;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.util.CollectionHelper;

import com.google.common.collect.Lists;

/**
 * Service internal object to represent the full INSPIRE DLS ExtendedCapabilities
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 * 
 */
public class FullInspireExtendedCapabilities extends InspireExtendedCapabilitiesDLS implements
        InspireExtendedCapabilitiesResourceLocator, InspireExtendedCapabilitiesMetadataURL,
        InspireExtendedCapabilitiesResourceType, InspireExtendedCapabilitiesTemporalReference<FullInspireExtendedCapabilities>,
        InspireExtendedCapabilitiesConformity, InspireExtendedCapabilitiesMetadataPointOfContact,
        InspireExtendedCapabilitiesMetadataDate, InspireExtendedCapabilitiesMandatoryKeyword,
        InspireExtendedCapabilitiesKeyword, InspireExtendedCapabilitiesSpatialDataServiceType {

    /* ResourceLocator 1..* */
    private List<InspireResourceLocator> resourceLocator = Lists.newArrayList();

    /* ResourceType 1..1 */
    private InspireServiceSpatialDataResourceType resourceType = InspireServiceSpatialDataResourceType.service;

    /* TemporalReference 1..* */
    private List<InspireTemporalReference> temporalReferences = Lists.newArrayList();

    /* Conformity 1..* */
    private List<InspireConformity> conformities = Lists.newArrayList();

    /* MetadataPointOfContact 1..* */
    private List<InspireMetadataPointOfContact> metadataPointOfContacts = Lists.newArrayList();

    /* MetadataDate 1..1 */
    private TimeInstant metadataDate;

    /* SpatialDataServiceType 1..1 */
    private InspireSpatialDataServiceType spatialDataServiceType = InspireSpatialDataServiceType.download;

    /* MandatoryKeyword 1..* */
    private List<InspireMandatoryKeyword> mandatoryKeywords = Lists.newArrayList();

    /* Keyword 0..* */
    private List<InspireKeyword> keywords = Lists.newArrayList();

    /* MetadataUrl" 0..1 */
    private InspireResourceLocator metadataUrl;

    /**
     * constructor
     * 
     * @param resourceLocator
     *            resourceLocator to set
     * @param supportedLanguages
     *            Supported languages to set
     * @param responseLanguage
     *            Response language to set
     * @param spatialDataSetIdentifier
     *            Spatial dataset identifier to set
     *  @param supportedCRS
     *            Supported CRSes to set
     */
    public FullInspireExtendedCapabilities(InspireResourceLocator resourceLocator,
            InspireSupportedLanguages supportedLanguages, InspireLanguageISO6392B responseLanguage,
            InspireUniqueResourceIdentifier spatialDataSetIdentifier, InspireSupportedCRS supportedCRS) {
        super(supportedLanguages, responseLanguage, spatialDataSetIdentifier,supportedCRS);
        addResourceLocator(resourceLocator);
    }

    /**
     * constructor
     * 
     * @param resourceLocators
     *            resourceLocators to set
     * @param supportedLanguages
     *            Supported languages to set
     * @param responseLanguage
     *            Response language to set
     * @param spatialDataSetIdentifier
     *            Spatial dataset identifier to set
     *  @param supportedCRS
     *            Supported CRSes to set
     */
    public FullInspireExtendedCapabilities(List<InspireResourceLocator> resourceLocators,
            InspireSupportedLanguages supportedLanguages, InspireLanguageISO6392B responseLanguage,
            InspireUniqueResourceIdentifier spatialDataSetIdentifier, InspireSupportedCRS supportedCRS) {
        super(supportedLanguages, responseLanguage, spatialDataSetIdentifier, supportedCRS);
        setResourceLocator(resourceLocators);
    }

    /**
     * constructor
     * 
     * @param resourceLocator
     *            resourceLocator to set
     * @param supportedLanguages
     *            Supported languages to set
     * @param responseLanguage
     *            Response language to set
     * @param spatialDataSetIdentifiers
     *            Spatial dataset identifiers to set
     *  @param supportedCRS
     *            Supported CRSes to set
     */
    public FullInspireExtendedCapabilities(InspireResourceLocator resourceLocator,
            InspireSupportedLanguages supportedLanguages, InspireLanguageISO6392B responseLanguage,
            Set<InspireUniqueResourceIdentifier> spatialDataSetIdentifiers, InspireSupportedCRS supportedCRS) {
        super(supportedLanguages, responseLanguage, spatialDataSetIdentifiers, supportedCRS);
        addResourceLocator(resourceLocator);
    }

    /**
     * constructor
     * 
     * @param resourceLocators
     *            resourceLocators to set
     * @param supportedLanguages
     *            Supported languages to set
     * @param responseLanguage
     *            Response language to set
     * @param spatialDataSetIdentifiers
     *            Spatial dataset identifiers to set
     *  @param supportedCRS
     *            Supported CRSes to set
     */
    public FullInspireExtendedCapabilities(List<InspireResourceLocator> resourceLocators,
            InspireSupportedLanguages supportedLanguages, InspireLanguageISO6392B responseLanguage,
            Set<InspireUniqueResourceIdentifier> spatialDataSetIdentifiers, InspireSupportedCRS supportedCRS) {
        super(supportedLanguages, responseLanguage, spatialDataSetIdentifiers,supportedCRS);
        setResourceLocator(resourceLocators);
    }

    @Override
    public List<InspireResourceLocator> getResourceLocator() {
        return resourceLocator;
    }

    @Override
    public InspireExtendedCapabilitiesResourceLocator setResourceLocator(
            Collection<InspireResourceLocator> resourceLocator) {
        if (CollectionHelper.isNotEmpty(resourceLocator)) {
            getResourceLocator().clear();
            this.resourceLocator.addAll(resourceLocator);
        }
        return this;
    }

    @Override
    public InspireExtendedCapabilitiesResourceLocator addResourceLocator(InspireResourceLocator resourceLocator) {
        getResourceLocator().add(resourceLocator);
        return this;
    }

    @Override
    public boolean isSetResourceLocators() {
        return CollectionHelper.isNotEmpty(getResourceLocator());
    }

    @Override
    public InspireResourceLocator getMetadataUrl() {
        return metadataUrl;
    }

    @Override
    public FullInspireExtendedCapabilities setMetadataUrl(InspireResourceLocator metadataUrl) {
        this.metadataUrl = metadataUrl;
        return this;
    }

    @Override
    public boolean isSetMetadataUrl() {
        return getMetadataUrl() != null;
    }

    @Override
    public String toString() {
        return String.format("%s %n[%n \tresourceLocator=%s," + "%n resourceType=%s," + "%n temporalReferences=%s,"
                + "%n conformity=%s," + "%n metadataPointOfContacts=%s," + "%n metadataDate=%s,"
                + "%n spatialDataServiceType=%s," + "%n mandatoryKeywords=%s," + "%n keywords=%s,"
                + "%n supportedLanguages=%s," + "%n responseLanguage=%s," + "%n metadataUrl=%s%n]", this.getClass()
                .getSimpleName(), CollectionHelper.collectionToString(getResourceLocator()), getResourceType(),
                CollectionHelper.collectionToString(getTemporalReferences()), CollectionHelper
                        .collectionToString(getConformity()), CollectionHelper
                        .collectionToString(getMetadataPointOfContacts()), getMetadataDate(),
                getSpatialDataServiceType(), CollectionHelper.collectionToString(getMandatoryKeywords()),
                CollectionHelper.collectionToString(getKeywords()), getSupportedLanguages(), getResponseLanguage(),
                getMetadataUrl());
    }

    @Override
    public InspireServiceSpatialDataResourceType getResourceType() {
        return resourceType;
    }
    
    @Override
    public InspireExtendedCapabilitiesResourceType setResourceType(InspireServiceSpatialDataResourceType resourceType) {
        this.resourceType = resourceType;
        return this;
    }

    @Override
    public boolean isSetResourceType() {
        return getResourceType() != null;
    }
    @Override
    public List<InspireTemporalReference> getTemporalReferences() {
        return temporalReferences;
    }
    @Override
    public FullInspireExtendedCapabilities setTemporalReferences(
            Collection<InspireTemporalReference> temporalReferences) {
        if (CollectionHelper.isNotEmpty(temporalReferences)) {
            getTemporalReferences().clear();
            getTemporalReferences().addAll(temporalReferences);
        }
        return this;
    }
    @Override
    public FullInspireExtendedCapabilities addTemporalReference(InspireTemporalReference temporalReference) {
        getTemporalReferences().add(temporalReference);
        return this;
    }

    @Override
    public boolean isSetTemporalReferences() {
        return CollectionHelper.isNotEmpty(getTemporalReferences());
    }

    @Override
    public List<InspireConformity> getConformity() {
        return conformities;
    }

    @Override
    public FullInspireExtendedCapabilities setConformity(Collection<InspireConformity> conformities) {
        if (CollectionHelper.isNotEmpty(conformities)) {
            getConformity().clear();
            getConformity().addAll(conformities);
        }
        return this;
    }

    @Override
    public FullInspireExtendedCapabilities addConformity(InspireConformity conformity) {
        getConformity().add(conformity);
        return this;
    }

    @Override
    public boolean isSetConformity() {
        return CollectionHelper.isNotEmpty(getConformity());
    }

    @Override
    public List<InspireMetadataPointOfContact> getMetadataPointOfContacts() {
        return metadataPointOfContacts;
    }

    @Override
    public FullInspireExtendedCapabilities setMetadataPointOfContacts(
            Collection<InspireMetadataPointOfContact> metadataPointOfContacts) {
        if (CollectionHelper.isNotEmpty(metadataPointOfContacts)) {
            getMetadataPointOfContacts().clear();
            getMetadataPointOfContacts().addAll(metadataPointOfContacts);
        }
        return this;
    }

    @Override
    public FullInspireExtendedCapabilities addMetadataPointOfContact(
            InspireMetadataPointOfContact metadataPointOfContact) {
        getMetadataPointOfContacts().add(metadataPointOfContact);
        return this;
    }

    @Override
    public boolean isSetMetadataPointOfContact() {
        return CollectionHelper.isNotEmpty(getMetadataPointOfContacts());
    }


    public TimeInstant getMetadataDate() {
        return metadataDate;
    }

    @Override
    public FullInspireExtendedCapabilities setMetadataDate(TimeInstant metadataDate) {
        this.metadataDate = metadataDate;
        return this;
    }

    @Override
    public boolean isSetMetadataDate() {
        return getMetadataDate() != null;
    }

    @Override
    public InspireSpatialDataServiceType getSpatialDataServiceType() {
        return spatialDataServiceType;
    }

    @Override
    public InspireExtendedCapabilitiesSpatialDataServiceType setSpatialDataServiceType(
            InspireSpatialDataServiceType spatialDataServiceType) {
        this.spatialDataServiceType = spatialDataServiceType;
        return this;
    }

    @Override
    public boolean isSetSpatialDataServiceType() {
        return getSpatialDataServiceType() != null;
    }

    @Override
    public List<InspireMandatoryKeyword> getMandatoryKeywords() {
        return mandatoryKeywords;
    }

    @Override
    public FullInspireExtendedCapabilities setMandatoryKeywords(Collection<InspireMandatoryKeyword> mandatoryKeywords) {
        if (CollectionHelper.isNotEmpty(mandatoryKeywords)) {
            getMandatoryKeywords().clear();
            getMandatoryKeywords().addAll(mandatoryKeywords);
        }
        return this;
    }

    @Override
    public FullInspireExtendedCapabilities addMandatoryKeyword(InspireMandatoryKeyword mandatoryKeyword) {
        getMandatoryKeywords().add(mandatoryKeyword);
        return this;
    }

    @Override
    public boolean isSetMandatoryKeyword() {
        return getMandatoryKeywords() != null;
    }

    @Override
    public List<InspireKeyword> getKeywords() {
        return keywords;
    }

    @Override
    public FullInspireExtendedCapabilities setKeywords(Collection<InspireKeyword> keywords) {
        if (CollectionHelper.isNotEmpty(keywords)) {
            getKeywords().clear();
            getKeywords().addAll(keywords);
        }
        return this;
    }

    @Override
    public FullInspireExtendedCapabilities addKeyword(InspireKeyword keyword) {
        keywords.add(keyword);
        return this;
    }

    @Override
    public boolean isSetKeywords() {
        return CollectionHelper.isNotEmpty(getKeywords());
    }
}
