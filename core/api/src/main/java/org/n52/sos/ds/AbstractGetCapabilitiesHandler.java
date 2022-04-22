/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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
package org.n52.sos.ds;

import static java.util.stream.Collectors.toSet;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.n52.iceland.service.operator.ServiceOperatorRepository;
import org.n52.shetland.ogc.ows.OWSConstants;
import org.n52.shetland.ogc.ows.OWSConstants.GetCapabilitiesParams;
import org.n52.shetland.ogc.ows.OwsAllowedValues;
import org.n52.shetland.ogc.ows.OwsDomain;
import org.n52.shetland.ogc.ows.OwsNoValues;
import org.n52.shetland.ogc.ows.OwsValue;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.service.GetCapabilitiesRequest;
import org.n52.shetland.ogc.ows.service.GetCapabilitiesResponse;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;

/**
 * interface for getting capabilities for a passed GetCapabilities request from
 * the data source
 *
 * Renamed, in version 4.x called AbstractGetCapabilitiesDAO
 *
 * @since 5.0.0
 */
public abstract class AbstractGetCapabilitiesHandler extends AbstractSosOperationHandler {

    @Inject
    private ServiceOperatorRepository serviceOperatorRepository;

    public AbstractGetCapabilitiesHandler(String service) {
        super(service, SosConstants.Operations.GetCapabilities.name());
    }

    protected abstract Set<String> getExtensionSections(String service, String version)
            throws OwsExceptionReport;

    /**
     * Get the SOS capabilities
     *
     * @param request
     *                GetCapabilities request
     *
     * @return internal SOS capabilities representation
     *
     * @throws OwsExceptionReport
     *                            If an error occurs.
     */
    public abstract GetCapabilitiesResponse getCapabilities(GetCapabilitiesRequest request) throws OwsExceptionReport;

    @Override
    protected Set<OwsDomain> getOperationParameters(String service, String version) throws OwsExceptionReport {
        return Stream.of(getSectionsParameter(service, version),
                         getAcceptFormatsParameter(service, version),
                         getAcceptVersionsParameter(service, version),
                         getUpdateSequenceParameter(service, version))
                .collect(toSet());
    }

    private OwsDomain getSectionsParameter(String service, String version) throws OwsExceptionReport {
        // set param Sections
        List<String> sections = new LinkedList<>();
        /* common sections */
        Arrays.stream(SosConstants.CapabilitiesSections.values()).map(e -> e.name()).forEach(sections::add);

        if (version.equals(Sos1Constants.SERVICEVERSION)) {
            sections.add(Sos1Constants.CapabilitiesSections.Filter_Capabilities.name());
        } else if (version.equals(Sos2Constants.SERVICEVERSION)) {
            sections.add(Sos2Constants.CapabilitiesSections.FilterCapabilities.name());
            /* sections of extension points */
            getExtensionSections(service, version).forEach(sections::add);
        }
        GetCapabilitiesParams name = OWSConstants.GetCapabilitiesParams.Sections;
        return  new OwsDomain(name, new OwsAllowedValues(sections.stream().map(OwsValue::new)));
    }

    public ServiceOperatorRepository getServiceOperatorRepository() {
        return serviceOperatorRepository;
    }

    @Inject
    public void setServiceOperatorRepository(ServiceOperatorRepository serviceOperatorRepository) {
        this.serviceOperatorRepository = serviceOperatorRepository;
    }

    private OwsDomain getAcceptFormatsParameter(String service, String version) {
        GetCapabilitiesParams name = OWSConstants.GetCapabilitiesParams.AcceptFormats;
        return new OwsDomain(name, new OwsAllowedValues(SosConstants.ACCEPT_FORMATS.stream().map(OwsValue::new)));
    }

    private OwsDomain getAcceptVersionsParameter(String service, String version) {
        GetCapabilitiesParams name = OWSConstants.GetCapabilitiesParams.AcceptVersions;
        Set<String> versions = getServiceOperatorRepository().getSupportedVersions(service);
        return new OwsDomain(name, new OwsAllowedValues(versions.stream().map(OwsValue::new)));
    }

    private OwsDomain getUpdateSequenceParameter(String service, String version) {
        GetCapabilitiesParams name = OWSConstants.GetCapabilitiesParams.updateSequence;
        return new OwsDomain(name, OwsNoValues.instance());
    }

}
