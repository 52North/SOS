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
package org.n52.sos.ds;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsOperation;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.GetCapabilitiesRequest;
import org.n52.sos.response.GetCapabilitiesResponse;
import org.n52.sos.service.operator.ServiceOperatorRepository;

//import org.n52.sos.service.operator.ServiceOperatorRepository;

/**
 * interface for getting capabilities for a passed GetCapabilities request from
 * the data source
 * 
 * @since 4.0.0
 */
public abstract class AbstractGetCapabilitiesDAO extends AbstractOperationDAO {
    
    protected static final String FALSE = Boolean.FALSE.toString();

    protected static final String TRUE = Boolean.TRUE.toString();

    public AbstractGetCapabilitiesDAO(String service) {
        super(service, SosConstants.Operations.GetCapabilities.name());
    }

    @Override
    protected void setOperationsMetadata(OwsOperation opsMeta, String service, String version)
            throws OwsExceptionReport {
        // set param Sections
        List<String> sectionsValues = new LinkedList<String>();
        /* common sections */
        sectionsValues.add(SosConstants.CapabilitiesSections.ServiceIdentification.name());
        sectionsValues.add(SosConstants.CapabilitiesSections.ServiceProvider.name());
        sectionsValues.add(SosConstants.CapabilitiesSections.OperationsMetadata.name());
        sectionsValues.add(SosConstants.CapabilitiesSections.Contents.name());
        sectionsValues.add(SosConstants.CapabilitiesSections.All.name());

        if (version.equals(Sos1Constants.SERVICEVERSION)) {
            sectionsValues.add(Sos1Constants.CapabilitiesSections.Filter_Capabilities.name());
        } else if (version.equals(Sos2Constants.SERVICEVERSION)) {
            sectionsValues.add(Sos2Constants.CapabilitiesSections.FilterCapabilities.name());
            /* sections of extension points */
            for (String section : getExtensionSections(service, version)) {
                sectionsValues.add(section);
            }
        }

        opsMeta.addPossibleValuesParameter(SosConstants.GetCapabilitiesParams.Sections, sectionsValues);
        opsMeta.addPossibleValuesParameter(SosConstants.GetCapabilitiesParams.AcceptFormats,
                SosConstants.ACCEPT_FORMATS);
        opsMeta.addPossibleValuesParameter(SosConstants.GetCapabilitiesParams.AcceptVersions,
                ServiceOperatorRepository.getInstance().getSupportedVersions(service));
        opsMeta.addAnyParameterValue(SosConstants.GetCapabilitiesParams.updateSequence);
    }

    protected abstract Set<String> getExtensionSections(final String service, final String version)
            throws OwsExceptionReport;

    /**
     * Get the SOS capabilities
     * 
     * @param request
     *            GetCapabilities request
     * @return internal SOS capabilities representation
     * 
     * @throws OwsExceptionReport
     *             If an error occurs.
     */
    public abstract GetCapabilitiesResponse getCapabilities(GetCapabilitiesRequest request) throws OwsExceptionReport;

}
