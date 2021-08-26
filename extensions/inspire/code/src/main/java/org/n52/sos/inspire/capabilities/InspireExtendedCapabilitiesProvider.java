/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Set;

import javax.inject.Inject;

import org.n52.faroe.annotation.Configurable;
import org.n52.iceland.ogc.ows.OwsServiceMetadataRepository;
import org.n52.iceland.ogc.ows.extension.OwsOperationMetadataExtensionProvider;
import org.n52.iceland.ogc.ows.extension.OwsOperationMetadataExtensionProviderKey;
import org.n52.janmayen.http.MediaType;
import org.n52.janmayen.http.MediaTypes;
import org.n52.shetland.inspire.InspireConformity;
import org.n52.shetland.inspire.InspireConformity.InspireDegreeOfConformity;
import org.n52.shetland.inspire.InspireConformityCitation;
import org.n52.shetland.inspire.InspireConstants;
import org.n52.shetland.inspire.InspireDateOfCreation;
import org.n52.shetland.inspire.InspireLanguageISO6392B;
import org.n52.shetland.inspire.InspireMandatoryKeyword;
import org.n52.shetland.inspire.InspireMandatoryKeywordValue;
import org.n52.shetland.inspire.InspireMetadataPointOfContact;
import org.n52.shetland.inspire.InspireResourceLocator;
import org.n52.shetland.inspire.InspireTemporalReference;
import org.n52.shetland.inspire.InspireUniqueResourceIdentifier;
import org.n52.shetland.inspire.dls.FullInspireExtendedCapabilities;
import org.n52.shetland.inspire.dls.MinimalInspireExtendedCapabilities;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.ows.OWSConstants;
import org.n52.shetland.ogc.ows.OwsAddress;
import org.n52.shetland.ogc.ows.OwsContact;
import org.n52.shetland.ogc.ows.OwsOperationMetadataExtension;
import org.n52.shetland.ogc.ows.OwsServiceProvider;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.extension.Extension;
import org.n52.shetland.ogc.ows.service.GetCapabilitiesRequest;
import org.n52.shetland.ogc.ows.service.OwsServiceRequest;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.swe.simpleType.SweCount;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.shetland.util.DateTimeParseException;
import org.n52.sos.inspire.AbstractInspireProvider;
import org.n52.sos.util.SosHelper;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Provider for the INSPIRE ExtendedCapabilities
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.1.0
 *
 */
@Configurable
@SuppressFBWarnings({"EI_EXPOSE_REP2"})
public class InspireExtendedCapabilitiesProvider extends AbstractInspireProvider
        implements OwsOperationMetadataExtensionProvider {

    private final OwsOperationMetadataExtensionProviderKey key = new OwsOperationMetadataExtensionProviderKey(
            SosConstants.SOS, Sos2Constants.SERVICEVERSION, InspireConstants.INSPIRE);

    private OwsServiceMetadataRepository serviceMetadataRepository;

    private SosHelper sosHelper;

    @Inject
    public void setServiceMetadataRepository(OwsServiceMetadataRepository repo) {
        this.serviceMetadataRepository = repo;
    }

    @Inject
    public void setSosHelperL(SosHelper sosHelper) {
        this.sosHelper = sosHelper;
    }

    public OwsServiceProvider getOwsServiceProvider() {
        return serviceMetadataRepository.getServiceProviderFactory(SosConstants.SOS).get();
    }

    @Override
    public Set<OwsOperationMetadataExtensionProviderKey> getKeys() {
        return Collections.singleton(key);
    }

    @Override
    public OwsOperationMetadataExtension getOwsExtendedCapabilities(GetCapabilitiesRequest request)
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
                getSpatialDataSetIdentifier(version));
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
    private FullInspireExtendedCapabilities getFullInspireExtendedCapabilities(String language, int crs,
            String version) throws OwsExceptionReport {
        InspireLanguageISO6392B responseLanguage = getInspireHelper().checkRequestedLanguage(language);
        /* ResourceLocator 1..* */
        /* SpatialDataSetIdentifier 1..* */
        /* SupportedLanguages 1..1 */
        /* ResponseLanguage 1..1 */
        /* ResourceType 1..1 */
        /* SpatialDataServiceType 1..1 */
        FullInspireExtendedCapabilities fullInspireExtendedCapabilities =
                new FullInspireExtendedCapabilities(getResourceLocator(), getSupportedLanguages(), responseLanguage,
                        getSpatialDataSetIdentifier(version));
        /* MetadataPointOfContact 1..* */
        fullInspireExtendedCapabilities.addMetadataPointOfContact(getMetadataPointOfContact());

        // TODO add ...
        /* Conformity 1..* */
        fullInspireExtendedCapabilities.addConformity(getConformity());

        /* TemporalReference 1..* */
        fullInspireExtendedCapabilities.addTemporalReference(getTemporalReference());

        /* MetadataDate 1..1 */
        fullInspireExtendedCapabilities.setMetadataDate(
                (TimeInstant) DateTimeHelper.parseIsoString2DateTime2Time(getInspireHelper().getMetadataDate()));

        /* MandatoryKeyword 1..* */
        fullInspireExtendedCapabilities.addMandatoryKeyword(
                new InspireMandatoryKeyword(InspireMandatoryKeywordValue.infoFeatureAccessService));

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
    private InspireResourceLocator getResourceLocator() throws OwsExceptionReport {
        try {
            InspireResourceLocator resourceLocator =
                    new InspireResourceLocator(SosHelper.getGetCapabilitiesKVPRequest(sosHelper.getServiceURL())
                            .toString());
            resourceLocator.addMediaType(MediaTypes.APPLICATION_XML);
            return resourceLocator;
        } catch (MalformedURLException ex) {
            throw new NoApplicableCodeException().causedBy(ex);
        }
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
        OwsServiceProvider serviceProvider = getOwsServiceProvider();
        String mail = serviceProvider.getServiceContact().getContactInfo().flatMap(OwsContact::getAddress)
                .map(OwsAddress::getElectronicMailAddress).map(l -> Iterables.getFirst(l, null)).orElse(null);
        return new InspireMetadataPointOfContact(serviceProvider.getProviderName(), mail);
    }

    /**
     * Get the conformity
     *
     * @return the conformity
     * @throws CodedException
     *             If an error occurs
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

        for (String offering : getCache().getOfferings()) {
            InspireUniqueResourceIdentifier iuri = new InspireUniqueResourceIdentifier(offering);
            if (getInspireHelper().isSetNamespace()) {
                iuri.setNamespace(getInspireHelper().getNamespace());
            } else {
                iuri.setNamespace(sosHelper.getServiceURL());
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
    private int getRequestedCrs(OwsServiceRequest request) {
        int targetSrid =
                request.getExtension(OWSConstants.AdditionalRequestParams.crs).map(Extension::getValue).map(value -> {
                    if (value instanceof SweCount) {
                        return ((SweCount) value).getValue();
                    } else if (value instanceof Integer) {
                        return (Integer) value;
                    } else {
                        return null;
                    }
                }).orElse(-1);

        return getGeometryHandler().getSupportedCRS().contains(Integer.toString(targetSrid)) ? targetSrid
                : getGeometryHandler().getDefaultResponseEPSG();
    }

}
