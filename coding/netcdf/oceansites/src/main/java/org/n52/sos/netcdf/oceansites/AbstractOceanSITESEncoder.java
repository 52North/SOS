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
package org.n52.sos.netcdf.oceansites;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.n52.sos.encode.AbstractNetcdfEncoder;
import org.n52.sos.exception.CodedException;
import org.n52.sos.iso.CodeList.CiRoleCodes;
import org.n52.sos.netcdf.data.dataset.AbstractSensorDataset;
import org.n52.sos.netcdf.om.NetCDFObservation;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.AbstractSensorML;
import org.n52.sos.ogc.sensorML.SmlResponsibleParty;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.response.BinaryAttachmentResponse;
import org.n52.sos.util.Constants;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.util.http.MediaType;

import com.axiomalaska.cf4j.constants.CFConstants;
import com.google.common.base.Joiner;

import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFileWriter;
import ucar.nc2.NetcdfFileWriter.Version;

public abstract class AbstractOceanSITESEncoder extends AbstractNetcdfEncoder {
    
    private static final DateTime DT_1950 = new DateTime(1950, 1, 1, 0, 0, DateTimeZone.UTC);

    @Override
    public MediaType getContentType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected BinaryAttachmentResponse encodeNetCDFObsToNetcdf(List<NetCDFObservation> netCDFSosObsList, Version version)
            throws OwsExceptionReport {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void addProfileSpecificGlobalAttributes(NetcdfFileWriter writer, AbstractSensorDataset sensorDataset) {
        // TODO Auto-generated method stub
        
        // In OceanSITES the names a UpperCase
        //writer.renameVariable(oldName, newName);
        //        writer.renameDimension(g, oldName, newName);
        //        writer.renameGlobalAttribute(g, oldName, newName);
        //        writer.renameVariableAttribute(v, attName, newName);
    }

    @Override
    protected String getConventionsValue() {
        return Joiner.on(Constants.COMMA_CHAR).join(CFConstants.CF_1_6,
                        OceanSITESConstants.OCEANSITES_VERSION, OceanSITESConstants.ACCD_VERSION);
    }
    
    @Override
    protected String getLicenseValue() {
        return OceansitesHelper.getInstance().getLicense();
    }
    
    @Override
    protected double getTimeValue(Time time) throws CodedException {
        return DateTimeHelper.getDaysSinceWithPrecision(DT_1950, getDateTime(time), 1);
    }

    protected boolean addPrincipalInvestigator(AbstractSensorML sml, NetcdfFileWriter writer) {
        return addContributor(sml, CiRoleCodes.CI_RoleCode_principalInvestigator, writer);
    }

    protected boolean addPrincipalInvestigator(AbstractSensorML sml, CiRoleCodes ciRoleCode, NetcdfFileWriter writer) {
        return addContributor(sml, ciRoleCode.getIdentifier(), writer);
    }

    protected boolean addPrincipalInvestigator(AbstractSensorML sml, String contactRole, NetcdfFileWriter writer) {
        SmlResponsibleParty responsibleParty = getResponsibleParty(sml, contactRole);
        if (responsibleParty != null) {
            if (responsibleParty.isSetOrganizationName()) {
                writer.addGroupAttribute(null, new Attribute(OceanSITESConstants.PRINCIPAL_INVESTIGATOR,
                        responsibleParty.getOrganizationName()));
            }
            if (responsibleParty.isSetEmail()) {
                writer.addGroupAttribute(null, new Attribute(OceanSITESConstants.PRINCIPAL_INVESTIGATOR_EMAIL,
                        responsibleParty.getEmail()));
            }
            if (responsibleParty.isSetOnlineResources()) {
                writer.addGroupAttribute(null, new Attribute(OceanSITESConstants.PRINCIPAL_INVESTIGATOR_URL,
                        responsibleParty.getOnlineResources().get(0)));
            }
            return true;
        }
        return false;
    }

}
