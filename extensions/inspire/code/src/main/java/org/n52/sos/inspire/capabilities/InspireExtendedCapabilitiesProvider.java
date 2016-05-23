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

import java.util.Set;

import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.DateTimeParseException;
import org.n52.sos.inspire.AbstractInspireProvider;
import org.n52.sos.inspire.InspireConformity;
import org.n52.sos.inspire.InspireConformity.InspireDegreeOfConformity;
import org.n52.sos.inspire.InspireConformityCitation;
import org.n52.sos.inspire.InspireConstants;
import org.n52.sos.inspire.InspireDateOfCreation;
import org.n52.sos.inspire.InspireHelper;
import org.n52.sos.inspire.InspireLanguageISO6392B;
import org.n52.sos.inspire.InspireMandatoryKeyword;
import org.n52.sos.inspire.InspireMandatoryKeywordValue;
import org.n52.sos.inspire.InspireMetadataPointOfContact;
import org.n52.sos.inspire.InspireResourceLocator;
import org.n52.sos.inspire.InspireTemporalReference;
import org.n52.sos.inspire.InspireUniqueResourceIdentifier;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.ows.OWSConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsExtendedCapabilities;
import org.n52.sos.ogc.ows.OwsExtendedCapabilitiesKey;
import org.n52.sos.ogc.ows.OwsExtendedCapabilitiesProvider;
import org.n52.sos.ogc.ows.SosServiceProvider;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.swe.simpleType.SweCount;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.request.GetCapabilitiesRequest;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.SosHelper;
import org.n52.sos.util.http.MediaType;
import org.n52.sos.util.http.MediaTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * Provider for the INSPIRE ExtendedCapabilities
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 * 
 */
public class InspireExtendedCapabilitiesProvider extends AbstractInspireProvider implements
        OwsExtendedCapabilitiesProvider {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(InspireExtendedCapabilitiesProvider.class);

    Set<OwsExtendedCapabilitiesKey> providerKeys = Sets.newHashSet(new OwsExtendedCapabilitiesKey(SosConstants.SOS,
            Sos2Constants.SERVICEVERSION, InspireConstants.INSPIRE));

    /**
     * constructor
     */
    public InspireExtendedCapabilitiesProvider() {
        InspireHelper.getInstance();
    }

    @Override
    public Set<OwsExtendedCapabilitiesKey> getExtendedCapabilitiesKeyType() {
        return providerKeys;
    }

    @Override
    public OwsExtendedCapabilities getOwsExtendedCapabilities(GetCapabilitiesRequest request)
            throws OwsExceptionReport {
        if (getInspireHelper().isFullExtendedCapabilities()) {
            return getFullInspireExtendedCapabilities(request.getRequestedLanguage(), getRequestedCrs(request),
                    request.getVersion());
        } else {
            return getMinimalInspireExtendedCapabilities(request.getRequestedLanguage(), getRequestedCrs(request),
                    request.getVersion());
        }
    }

    @Override
    public boolean hasExtendedCapabilitiesFor(GetCapabilitiesRequest request) {
        return getInspireHelper().isEnabled();
    }

    /**
     * Get the SOS internal representation of the
     * {@link MinimalInspireExtendedCapabilities}
     * 
     * @param language
     *            the requested language
     * @param crs
     *            the requested coordinate reference system
     * @param version
     *            the requested version
     * @return SOS internal representation of the
     *         {@link MinimalInspireExtendedCapabilities}
     */
    private MinimalInspireExtendedCapabilities getMinimalInspireExtendedCapabilities(String language, int crs,
            String version) {
        InspireLanguageISO6392B responseLanguage = getInspireHelper().checkRequestedLanguage(language);
        return new MinimalInspireExtendedCapabilities(getMetadataUrl(), getSupportedLanguages(), responseLanguage,
                getSpatialDataSetIdentifier(version), getSupportedCRS());
    }

    /**
     * Get the SOS internal representation of the
     * {@link FullInspireExtendedCapabilities}
     * 
     * @param language
     *            the requested language
     * @param crs
     *            the requested coordinate reference system
     * @param version
     *            the requested version
     * @return SOS internal representation of the
     *         {@link FullInspireExtendedCapabilities}
     * @throws OwsExceptionReport
     *             If an error occurs when creating the capabilities
     */
    private FullInspireExtendedCapabilities getFullInspireExtendedCapabilities(String language, int crs, String version)
            throws OwsExceptionReport {
        InspireLanguageISO6392B responseLanguage = getInspireHelper().checkRequestedLanguage(language);
        /* ResourceLocator 1..* */
        /* SpatialDataSetIdentifier 1..* */
        /* SupportedLanguages 1..1 */
        /* ResponseLanguage 1..1 */
        /* ResourceType 1..1 */
        /* SpatialDataServiceType 1..1 */
        FullInspireExtendedCapabilities fullInspireExtendedCapabilities =
                new FullInspireExtendedCapabilities(getResourceLocator(), getSupportedLanguages(), responseLanguage,
                        getSpatialDataSetIdentifier(version), getSupportedCRS());
        /* MetadataPointOfContact 1..* */
        fullInspireExtendedCapabilities.addMetadataPointOfContact(getMetadataPointOfContact());

        // TODO add ...
        /* Conformity 1..* */
        fullInspireExtendedCapabilities.addConformity(getConformity());

        /* TemporalReference 1..* */
        fullInspireExtendedCapabilities.addTemporalReference(getTemporalReference());

        /* MetadataDate 1..1 */
        fullInspireExtendedCapabilities.setMetadataDate((TimeInstant) DateTimeHelper
                .parseIsoString2DateTime2Time(getInspireHelper().getMetadataDate()));

        /* MandatoryKeyword 1..* */
        fullInspireExtendedCapabilities.addMandatoryKeyword(new InspireMandatoryKeyword(
                InspireMandatoryKeywordValue.infoFeatureAccessService));

        /* Keyword 0..* */

        /* MetadataUrl" 0..1 */
        addMetadataUrl(fullInspireExtendedCapabilities);
        return fullInspireExtendedCapabilities;
    }

    private InspireResourceLocator getMetadataUrl() {
        if (getInspireHelper().isSetMetadataUrlURL()) {
            InspireResourceLocator inspireResourceLocator =
                    new InspireResourceLocator(getInspireHelper().getMetadataUrlURL().toString());
            if (getInspireHelper().isSetMetadataUrlMediaType()) {
                inspireResourceLocator.addMediaType(MediaType.parse(getInspireHelper().getMetadataUrlMediaType()));
            }
            return inspireResourceLocator;
        }
        return null;
    }

    private void addMetadataUrl(FullInspireExtendedCapabilities fullInspireExtendedCapabilities) {
        if (getInspireHelper().isSetMetadataUrlURL()) {
            fullInspireExtendedCapabilities.setMetadataUrl(getMetadataUrl());
        }
    }

    /**
     * Get the resource locator
     * 
     * @return the resource locator
     */
    private InspireResourceLocator getResourceLocator() {
        InspireResourceLocator resourceLocator = new InspireResourceLocator(SosHelper.getGetCapabilitiesKVPRequest());
        resourceLocator.addMediaType(MediaTypes.APPLICATION_XML);
        return resourceLocator;
    }

    /**
     * Get the metadata point of contact
     * 
     * @return the metadata point of contact
     * @throws OwsExceptionReport
     *             If an error occurs when creating the metadata point of
     *             contact
     */
    private InspireMetadataPointOfContact getMetadataPointOfContact() throws OwsExceptionReport {
        SosServiceProvider serviceProvider = Configurator.getInstance().getServiceProvider();
        return new InspireMetadataPointOfContact(serviceProvider.getName(), serviceProvider.getMailAddress());
    }

    /**
     * Get the conformity
     * 
     * @return the conformity
     * @throws CodedException
     */
    private InspireConformity getConformity() throws CodedException {
        try {
            InspireConformityCitation citation =
                    new InspireConformityCitation(getInspireHelper().getConformityTitle(), new InspireDateOfCreation(
                            DateTimeHelper.parseIsoString2DateTime(getInspireHelper().getConformityDateOfCreation())));
            return new InspireConformity(citation, InspireDegreeOfConformity.notEvaluated);
        } catch (DateTimeParseException e) {
            throw new NoApplicableCodeException().causedBy(e);
        }
    }

    /**
     * Get the temporal reference
     * 
     * @return the temporal reference
     */
    private InspireTemporalReference getTemporalReference() {
        return new InspireTemporalReference();
    }

    /**
     * Get the spatial dataset identifiers
     * 
     * @param version
     *            the service version
     * @return the spatial dataset identifiers
     */
    private Set<InspireUniqueResourceIdentifier> getSpatialDataSetIdentifier(String version) {
        Set<InspireUniqueResourceIdentifier> spatialDataSetIdentifier = Sets.newHashSet();

        for (String offering : Configurator.getInstance().getCache().getOfferings()) {
            InspireUniqueResourceIdentifier iuri = new InspireUniqueResourceIdentifier(offering);
            if (InspireHelper.getInstance().isSetNamespace()) {
                iuri.setNamespace(InspireHelper.getInstance().getNamespace());
            } else {
                iuri.setNamespace(ServiceConfiguration.getInstance().getServiceURL());
            }
            spatialDataSetIdentifier.add(iuri);
        }
        return spatialDataSetIdentifier;
    }

    /**
     * Get the coordinate reference system from the request
     * 
     * @param request
     *            the request
     * @return the coordinate reference system
     */
    private int getRequestedCrs(AbstractServiceRequest<?> request) {
        int targetSrid = -1;
        if (request.isSetExtensions()) {
            if (request.getExtensions().containsExtension(OWSConstants.AdditionalRequestParams.crs)) {
                Object value =
                        request.getExtensions().getExtension(OWSConstants.AdditionalRequestParams.crs).getValue();
                if (value instanceof SweCount) {
                    targetSrid = ((SweCount) value).getValue();
                } else if (value instanceof Integer) {
                    targetSrid =
                            (Integer) request.getExtensions().getExtension(OWSConstants.AdditionalRequestParams.crs)
                                    .getValue();
                }
            }
        }
        if (GeometryHandler.getInstance().getSupportedCRS().contains(Integer.toString(targetSrid))) {
            return targetSrid;
        }
        return GeometryHandler.getInstance().getDefaultResponseEPSG();
    }

}
