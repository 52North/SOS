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
import org.n52.sos.ogc.gml.time.TimeInstant;

/**
 * Super interface for INSPIRE capabilities
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 * 
 */
public interface InspireCapabilities {

    /**
     * INSPIRE capabilities resource locator interface
     * 
     * @author Carsten Hollmann <c.hollmann@52north.org>
     * @since 4.1.0
     * 
     */
    interface InspireExtendedCapabilitiesResourceLocator {

        /**
         * Get the resource locators
         * 
         * @return the resource locators
         */
        List<InspireResourceLocator> getResourceLocator();

        /**
         * Set the the resource locators, clears the existing collection
         * 
         * @param resourceLocator
         *            the resource locators to set
         * @return this
         */
        InspireExtendedCapabilitiesResourceLocator setResourceLocator(
                Collection<InspireResourceLocator> resourceLocator);

        /**
         * Add a the resource locator
         * 
         * @param resourceLocator
         *            the the resource locator to add
         * @return this
         */
        InspireExtendedCapabilitiesResourceLocator addResourceLocator(InspireResourceLocator resourceLocator);

        /**
         * Check if resource locator is set
         * 
         * @return <code>true</code>, if resource locator is set
         */
        boolean isSetResourceLocators();
    }

    /**
     * INSPIRE capabilities metadata URL interface
     * 
     * @author Carsten Hollmann <c.hollmann@52north.org>
     * @since 4.1.0
     * 
     */
    interface InspireExtendedCapabilitiesMetadataURL {
        /**
         * Get the metadata URL is
         * 
         * @return the matadataURL
         */
        InspireResourceLocator getMetadataUrl();

        /**
         * Set the metadata URL
         * 
         * @param metadataUrl
         *            the metadataUrl to set
         */
        InspireExtendedCapabilitiesMetadataURL setMetadataUrl(InspireResourceLocator metadataUrl);

        /**
         * Check if metadata URL is set
         * 
         * @return <code>true</code>, if metadata URL is set
         */
        boolean isSetMetadataUrl();

    }

    /**
     * INSPIRE capabilities supported languages interface
     * 
     * @author Carsten Hollmann <c.hollmann@52north.org>
     * @since 4.1.0
     * 
     */
    interface InspireExtendedCapabilitiesSupportedLanguage {
        /**
         * Get the supported languages
         * 
         * @return the supportedLanguages
         */
        InspireSupportedLanguages getSupportedLanguages();

        /**
         * Set the supported languages
         * 
         * @param supportedLanguages
         *            the supported languages to set
         */
        InspireExtendedCapabilitiesSupportedLanguage setSupportedLanguages(InspireSupportedLanguages supportedLanguages);

        /**
         * Check if supported languages are set
         * 
         * @return <code>true</code>, if supported languages are set
         */
        boolean isSetSupportedLanguages();

    }

    /**
     * INSPIRE capabilities response language interface
     * 
     * @author Carsten Hollmann <c.hollmann@52north.org>
     * @since 4.1.0
     * 
     */
    interface InspireExtendedCapabilitiesResponseLanguage {
        /**
         * Get the response language
         * 
         * @return the responseLanguage
         */
        InspireLanguageISO6392B getResponseLanguage();

        /**
         * Set the response language
         * 
         * @param responseLanguage
         *            the response language to set
         */
        InspireExtendedCapabilitiesResponseLanguage setResponseLanguage(InspireLanguageISO6392B responseLanguage);

        /**
         * Check if response language is set
         * 
         * @return <code>true</code>, if response language is set
         */
        boolean isSetResponseLanguage();;

    }

    /**
     * INSPIRE capabilities spatial dataset identifier interface
     * 
     * @author Carsten Hollmann <c.hollmann@52north.org>
     * @since 4.1.0
     * 
     */
    interface InspireExtendedCapabilitiesSpatialDataSetIdentifier {

        /**
         * Get the spatial dataset identifiers
         * 
         * @return the spatialDataSetIdentifier
         */
        Set<InspireUniqueResourceIdentifier> getSpatialDataSetIdentifier();

        /**
         * Set the spatial dataset identifiers, clears the existing collection
         * 
         * @param spatialDataSetIdentifier
         *            the spatial dataset identifiers to set
         */
        InspireExtendedCapabilitiesSpatialDataSetIdentifier setSpatialDataSetIdentifier(
                Collection<InspireUniqueResourceIdentifier> spatialDataSetIdentifier);

        /**
         * Add the spatial dataset identifier
         * 
         * @param spatialDataSetIdentifier
         *            the spatial dataset identifier to add
         */
        InspireExtendedCapabilitiesSpatialDataSetIdentifier addSpatialDataSetIdentifier(
                InspireUniqueResourceIdentifier spatialDataSetIdentifier);

        /**
         * Check if spatial dataset identifiers are set
         * 
         * @return <code>true</code>, if spatial dataset identifiers are set
         */
        boolean isSetSpatialDataSetIdentifier();

    }

    /**
     * INSPIRE capabilities resource type interface
     * 
     * @author Carsten Hollmann <c.hollmann@52north.org>
     * @since 4.1.0
     * 
     */
    interface InspireExtendedCapabilitiesResourceType {
        /**
         * Get the resource type
         * 
         * @return the resource type
         */
        InspireServiceSpatialDataResourceType getResourceType();

        /**
         * Set the resource type
         * 
         * @param resourceType
         *            the resourceType to set
         */
        InspireExtendedCapabilitiesResourceType setResourceType(InspireServiceSpatialDataResourceType resourceType);

        /**
         * Check if resource type is set
         * 
         * @return <code>true</code>, if resource type is set
         */
        boolean isSetResourceType();

    }

    /**
     * INSPIRE capabilities temporal reference interface
     * 
     * @author Carsten Hollmann <c.hollmann@52north.org>
     * @since 4.1.0
     * 
     */
    interface InspireExtendedCapabilitiesTemporalReference<T> {
        /**
         * Get the temporal references
         * 
         * @return the temporal references
         */
        List<InspireTemporalReference> getTemporalReferences();

        /**
         * Set the temporal references, clears the existing collection
         * 
         * @param temporalReferences
         *            Temporal references to set
         * @return this
         */
        T setTemporalReferences(Collection<InspireTemporalReference> temporalReferences);

        /**
         * Add a temporal reference
         * 
         * @param temporalReference
         *            Temporal reference to add
         * @return this
         */
        T addTemporalReference(InspireTemporalReference temporalReference);

        /**
         * Check if temporal references are set
         * 
         * @return <code>true</code>, if temporal references are set
         */
        boolean isSetTemporalReferences();
    }

    /**
     * INSPIRE capabilities conformity interface
     * 
     * @author Carsten Hollmann <c.hollmann@52north.org>
     * @since 4.1.0
     * 
     */
    interface InspireExtendedCapabilitiesConformity {
        /**
         * Get the conformities
         * 
         * @return the conformities
         */
        List<InspireConformity> getConformity();

        /**
         * Set the conformities, clears the existing collection
         * 
         * @param conformities
         *            the conformities to set
         * @return this
         */
        InspireExtendedCapabilitiesConformity setConformity(Collection<InspireConformity> conformities);

        /**
         * Add the conformity
         * 
         * @param conformity
         *            the conformity to add
         * @return this
         */
        FullInspireExtendedCapabilities addConformity(InspireConformity conformity);

        /**
         * Check if conformities are set
         * 
         * @return <code>true</code>, if conformities are set
         */
        boolean isSetConformity();
    }

    /**
     * INSPIRE capabilities metadata point of contact interface
     * 
     * @author Carsten Hollmann <c.hollmann@52north.org>
     * @since 4.1.0
     * 
     */
    interface InspireExtendedCapabilitiesMetadataPointOfContact {
        /**
         * Get the metadata points of contact
         * 
         * @return this
         */
        List<InspireMetadataPointOfContact> getMetadataPointOfContacts();

        /**
         * Set the metadata points of contact, clears the existing collection
         * 
         * @param metadataPointOfContacts
         *            metadata points of contact to set
         * @return this
         */
        InspireExtendedCapabilitiesMetadataPointOfContact setMetadataPointOfContacts(
                Collection<InspireMetadataPointOfContact> metadataPointOfContacts);

        /**
         * Add metadata point of contact
         * 
         * @param metadataPointOfContact
         *            metadata point of contact to add
         * @return this
         */
        InspireExtendedCapabilitiesMetadataPointOfContact addMetadataPointOfContact(
                InspireMetadataPointOfContact metadataPointOfContact);

        /**
         * Check if metadata points of contact are set
         * 
         * @return <code>true</code>, if metadata points of contact are set
         */
        boolean isSetMetadataPointOfContact();
    }

    /**
     * INSPIRE capabilities metadata date interface
     * 
     * @author Carsten Hollmann <c.hollmann@52north.org>
     * @since 4.1.0
     * 
     */
    interface InspireExtendedCapabilitiesMetadataDate {
        /**
         * Get the metadata date
         * 
         * @return the metadat date
         */
        TimeInstant getMetadataDate();

        /**
         * Set the metadata date
         * 
         * @param metadataDate
         *            the metadate date to set
         * @return this
         */
        InspireExtendedCapabilitiesMetadataDate setMetadataDate(TimeInstant metadataDate);

        /**
         * Check if metadata date is set
         * 
         * @return <code>true</code>, if metadata date is set
         */
        boolean isSetMetadataDate();
    }

    /**
     * INSPIRE capabilities spatial data service type interface
     * 
     * @author Carsten Hollmann <c.hollmann@52north.org>
     * @since 4.1.0
     * 
     */
    interface InspireExtendedCapabilitiesSpatialDataServiceType {
        /**
         * Get tje spatial data service type
         * 
         * @return the spatial data service type
         */
        InspireSpatialDataServiceType getSpatialDataServiceType();

        /**
         * @param spatialDataServiceType
         *            the spatialDataServiceType to set
         */
        InspireExtendedCapabilitiesSpatialDataServiceType setSpatialDataServiceType(
                InspireSpatialDataServiceType spatialDataServiceType);

        /**
         * @return <code>true</code>, if spatialDataServiceType is set
         */
        boolean isSetSpatialDataServiceType();
    }

    /**
     * INSPIRE capabilities mandatory keywords interface
     * 
     * @author Carsten Hollmann <c.hollmann@52north.org>
     * @since 4.1.0
     * 
     */
    interface InspireExtendedCapabilitiesMandatoryKeyword {
        /**
         * Get the mandatory keywords
         * 
         * @return the mandatory keywords
         */
        List<InspireMandatoryKeyword> getMandatoryKeywords();

        /**
         * Set the mandatory keywords, clears the existing collection
         * 
         * @param mandatoryKeywords
         *            the mandatory keywords to set
         * @return this
         */
        InspireExtendedCapabilitiesMandatoryKeyword setMandatoryKeywords(
                Collection<InspireMandatoryKeyword> mandatoryKeywords);

        /**
         * Add a mandatory keyword
         * 
         * @param mandatoryKeyword
         *            the mandatory keyword to set
         * @return this
         */
        InspireExtendedCapabilitiesMandatoryKeyword addMandatoryKeyword(InspireMandatoryKeyword mandatoryKeyword);

        /**
         * Check if mandatory keywords are set
         * 
         * @return <code>true</code>, if mandatory keywords are set
         */
        boolean isSetMandatoryKeyword();
    }

    /**
     * INSPIRE capabilities keywords interface
     * 
     * @author Carsten Hollmann <c.hollmann@52north.org>
     * @since 4.1.0
     * 
     */
    interface InspireExtendedCapabilitiesKeyword {
        /**
         * Get the keywords
         * 
         * @return the keywords
         */
        List<InspireKeyword> getKeywords();

        /**
         * Set the keywords, clears the existing collection
         * 
         * @param keywords
         *            the keywords to set
         * @return this
         */
        InspireExtendedCapabilitiesKeyword setKeywords(Collection<InspireKeyword> keywords);

        /**
         * Add the keyword
         * 
         * @param keyword
         *            the keyword to add
         * @return this
         */
        InspireExtendedCapabilitiesKeyword addKeyword(InspireKeyword keyword);

        /**
         * Check if keywords are set
         * 
         * @return <code>true</code>, if keywords are set
         */
        boolean isSetKeywords();
    }
    
    /**
     * INSPIRE capabilities supported CRSes interface
     * 
     * @author Carsten Hollmann <c.hollmann@52north.org>
     * @since 4.1.0
     * 
     */
    interface InspireExtendedCapabilitiesSupportedCRS {
        /**
         * Get the supported CRSes
         * 
         * @return the supportedCRSes
         */
        InspireSupportedCRS getSupportedCRS();

        /**
         * Set the supported CRSes
         * 
         * @param supportedCRSes
         *            the supported CRSes to set
         */
        InspireExtendedCapabilitiesSupportedCRS setSupportedCRS(InspireSupportedCRS supportedCRS);

        /**
         * Check if supported CRSes are set
         * 
         * @return <code>true</code>, if supported CRSes are set
         */
        boolean isSetSupportedCRS();

    }
    /**
     * INSPIRE capabilities spatial data service type
     * 
     * @author Carsten Hollmann <c.hollmann@52north.org>
     * @since 4.1.0
     * 
     */
    public enum InspireSpatialDataServiceType {
        discovery, view, download, transformation, invoke, other;
    }

    /**
     * INSPIRE capabilities service spatial data resource type
     * 
     * @author Carsten Hollmann <c.hollmann@52north.org>
     * @since 4.1.0
     * 
     */
    public enum InspireServiceSpatialDataResourceType {
        service;
    }
}
