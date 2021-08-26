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
package org.n52.sos.encode;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.svalbard.encode.exception.EncodingException;
import org.n52.shetland.iso.CodeList.CiRoleCodes;
import org.n52.shetland.ogc.gml.time.Time;
import org.n52.shetland.ogc.om.OmObservableProperty;
import org.n52.shetland.ogc.ows.OwsAddress;
import org.n52.shetland.ogc.ows.OwsContact;
import org.n52.shetland.ogc.ows.OwsOnlineResource;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sensorML.AbstractSensorML;
import org.n52.shetland.ogc.sensorML.SmlResponsibleParty;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.sos.netcdf.data.dataset.AbstractSensorDataset;
import org.n52.sos.netcdf.oceansites.OceanSITESConstants;
import org.n52.sos.netcdf.oceansites.OceanSITESHelper;

import com.axiomalaska.cf4j.constants.ACDDConstants;
import com.axiomalaska.cf4j.constants.CFConstants;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import ucar.ma2.DataType;
import ucar.nc2.Attribute;
import ucar.nc2.CDMNode;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriter;
import ucar.nc2.NetcdfFileWriter.Version;
import ucar.nc2.Variable;
import ucar.nc2.constants.CF.FeatureType;

/**
 * Abstract encoder class of {@link AbstractNetcdfEncoder} for OceanSITES netCDF
 * encoding
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
@SuppressFBWarnings({"EI_EXPOSE_REP2"})
public abstract class AbstractOceanSITESEncoder extends AbstractNetcdfEncoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractOceanSITESEncoder.class);

    private static final DateTime DT_1950 = new DateTime(1950, 1, 1, 0, 0, DateTimeZone.UTC);

    private static final String DOT_NC = ".nc";


    private OceanSITESHelper oceanSITESHelper;

    @Override
    protected void addProfileSpecificGlobalAttributes(NetcdfFileWriter writer, AbstractSensorDataset sensorDataset)
            throws EncodingException {
        // site_code (RECOMMENDED)
        addSiteCode(writer, sensorDataset);
        // data_mode (RECOMMENDED)
        addDataMode(writer, sensorDataset);
        // data_type (RECOMMENDED)
        addDataType(writer, sensorDataset);
        // format_version (RECOMMENDED)
        addFormatVersion(writer, sensorDataset);
        // update_interval (RECOMMENDED)
        addUpdateInterval(writer, sensorDataset);
        // OPTIONAL
        // wmo code
        addWmoCode(writer, sensorDataset);
        // acknowledgement
        addAcknowledge(writer, sensorDataset);
        // array
        addArray(writer, sensorDataset);
        // network
        addNetwork(writer, sensorDataset);
        // project
        addProject(writer, sensorDataset);
        // id
        addId(writer, sensorDataset);
        // area
        addArea(writer, sensorDataset);
        // citation
        addCitation(writer, sensorDataset);
        // processing level
        addProcessingLevel(writer, sensorDataset);
        // QC_indicator
        addQcIndicator(writer, sensorDataset);
        // netcdf_version
        addNetcdfVersion(writer);
        // references
        addReferences(writer, sensorDataset);
        // naming_authority
        addNamingAuthority(writer, sensorDataset);

    }

    @Override
    protected String getConventionsValue() {
        return Joiner.on(',').join(CFConstants.CF_1_6, OceanSITESConstants.OCEANSITES_VERSION,
                OceanSITESConstants.ACCD_VERSION);
    }

    @Override
    protected String getLicenseValue() {
        if (getOceanSITEHelper().isSetLicense()) {
            return getOceanSITEHelper().getLicense();
        }
        return OceanSITESConstants.LICENSE_DEFAULT_TEXT;
    }

    @Override
    protected double getTimeValue(Time time) throws EncodingException {
        return DateTimeHelper.getDaysSinceWithPrecision(DT_1950, getDateTime(time));
    }

    @Override
    protected Variable addVariableTime(NetcdfFileWriter writer, List<Dimension> dims) {
        Variable variable = super.addVariableTime(writer, dims);
        variable.addAttribute(getDefaultQcIndicatorAttribute());
        variable.addAttribute(getValidMin(0.0));
        variable.addAttribute(getValidMax(90000.0));
        return variable;
    }

    @Override
    protected Variable addVariableLatitude(NetcdfFileWriter writer, List<Dimension> dims) {
        Variable variable = super.addVariableLatitude(writer, dims);
        variable.addAttribute(new Attribute(OceanSITESConstants.REFERENCE, OceanSITESConstants.EPSG_REFERENCE));
        variable.addAttribute(getDefaultQcIndicatorAttribute());
        variable.addAttribute(getValidMin(-90.0));
        variable.addAttribute(getValidMax(90.0));
        return variable;
    }

    @Override
    protected Variable addVariableLongitude(NetcdfFileWriter writer, List<Dimension> dims) {
        Variable variable = super.addVariableLongitude(writer, dims);
        variable.addAttribute(new Attribute(OceanSITESConstants.REFERENCE, OceanSITESConstants.EPSG_REFERENCE));
        variable.addAttribute(getDefaultQcIndicatorAttribute());
        variable.addAttribute(getValidMin(-180.0));
        variable.addAttribute(getValidMax(180.0));
        return variable;
    }

    @Override
    protected Variable addVariableHeight(NetcdfFileWriter writer, List<Dimension> dims) {
        Variable variable = super.addVariableHeight(writer, dims);
        variable.addAttribute(new Attribute(OceanSITESConstants.REFERENCE,
                OceanSITESConstants.HEIGHT_DEPTH_REFERENCE_DEFAULT));
        variable.addAttribute(new Attribute(OceanSITESConstants.COORDINATE_REFERENCE_FRAME,
                OceanSITESConstants.EPSG_5829));
        variable.addAttribute(getDefaultQcIndicatorAttribute());
        return variable;
    }

    @Override
    protected Variable addVariableDepth(NetcdfFileWriter writer, List<Dimension> dims) {
        Variable variable = super.addVariableDepth(writer, dims);
        variable.addAttribute(new Attribute(OceanSITESConstants.REFERENCE,
                OceanSITESConstants.HEIGHT_DEPTH_REFERENCE_DEFAULT));
        variable.addAttribute(new Attribute(OceanSITESConstants.COORDINATE_REFERENCE_FRAME,
                OceanSITESConstants.EPSG_5831));
        variable.addAttribute(getDefaultQcIndicatorAttribute());
        variable.addAttribute(getValidMin(0.0));
        variable.addAttribute(getValidMax(12000.0));
        return variable;
    }

    @Override
    protected Variable addVariableForObservedProperty(NetcdfFileWriter writer, OmObservableProperty obsProp,
            List<Dimension> obsPropDims, String coordinateString) {
        Variable variable = super.addVariableForObservedProperty(writer, obsProp, obsPropDims, coordinateString);
        variable.addAttribute(getDefaultQcIndicatorAttribute());
        // sensor, from SensorML?

        return variable;
    }

    protected CDMNode addSiteCode(NetcdfFileWriter writer, AbstractSensorDataset sensorDataset) {
        if (sensorDataset.getSensor().isSetSensorDescription()) {
            boolean exists = addAttributeIfIdentifierExists(writer, sensorDataset.getSensor().getSensorDescription(),
                    getOceanSITEHelper().getSiteDefinition(), OceanSITESConstants.SITE_CODE);
            if (exists) {
                return getAttribute(writer, OceanSITESConstants.SITE_CODE);
            }
        }
        return null;
    }

    @Override
    protected CDMNode addPlatform(NetcdfFileWriter writer, AbstractSensorDataset sensorDataset) {
        // platform_code (RECOMMENDED)
        if (sensorDataset.getSensor().isSetSensorDescription()) {
            boolean exists = addAttributeIfIdentifierExists(writer, sensorDataset.getSensor().getSensorDescription(),
                    getOceanSITEHelper().getPlatformDefinition(), OceanSITESConstants.PLATFORM_CODE);
            if (exists) {
                return getAttribute(writer, OceanSITESConstants.PLATFORM_CODE);
            }
        }
        return super.addPlatform(writer, sensorDataset);
    }

    protected CDMNode addDataMode(NetcdfFileWriter writer, AbstractSensorDataset sensorDataset) {
        if (sensorDataset.getSensor().isSetSensorDescription()) {
            if (!addAttributeIfClassifierExists(writer, sensorDataset.getSensor().getSensorDescription(),
                    getOceanSITEHelper().getDataModeDefinition(), OceanSITESConstants.DATA_MODE)) {
                String dataModeText = OceanSITESConstants.DataMode.R.toString();
                if (getOceanSITEHelper().isSetDataMode()) {
                    dataModeText = getOceanSITEHelper().getDataMode().name();
                }
                return writer.addGroupAttribute(null, new Attribute(OceanSITESConstants.DATA_MODE, dataModeText));
            }
        }
        return getAttribute(writer, OceanSITESConstants.DATA_MODE);
    }

    protected CDMNode addDataType(NetcdfFileWriter writer, AbstractSensorDataset sensorDataset) {
        return writer.addGroupAttribute(null,
                new Attribute(OceanSITESConstants.DATA_TYPE, getDataType(sensorDataset.getFeatureType())));
    }

    protected CDMNode addFormatVersion(NetcdfFileWriter writer, AbstractSensorDataset sensorDataset) {
        return writer.addGroupAttribute(null,
                new Attribute(OceanSITESConstants.UPDATE_INTERVAL, getOceanSITEHelper().getFormatVersion()));
    }

    protected CDMNode addUpdateInterval(NetcdfFileWriter writer, AbstractSensorDataset sensorDataset) {
        return writer.addGroupAttribute(null,
                new Attribute(OceanSITESConstants.UPDATE_INTERVAL, OceanSITESConstants.UPDATE_INTERVAL_TEXT));
    }

    protected CDMNode addWmoCode(NetcdfFileWriter writer, AbstractSensorDataset sensorDataset) {
        if (sensorDataset.getSensor().isSetSensorDescription()) {
            addAttributeIfIdentifierExists(writer, sensorDataset.getSensor().getSensorDescription(),
                    getOceanSITEHelper().getWmoPlatformCodeDefinition(), OceanSITESConstants.WMO_PLATFORM_CODE);
        }
        return getAttribute(writer, OceanSITESConstants.WMO_PLATFORM_CODE);
    }

    protected CDMNode addAcknowledge(NetcdfFileWriter writer, AbstractSensorDataset sensorDataset) {
        if (sensorDataset.getSensor().isSetSensorDescription()) {
            addAttributeIfIdentifierExists(writer, sensorDataset.getSensor().getSensorDescription(),
                    getOceanSITEHelper().getAcknowledgement(), ACDDConstants.ACKNOWLEDGEMENT);

        }
        return getAttribute(writer, ACDDConstants.ACKNOWLEDGEMENT);
    }

    protected boolean addArray(NetcdfFileWriter writer, AbstractSensorDataset sensorDataset) {
        if (sensorDataset.getSensor().isSetSensorDescription()) {
            return addAttributeIfIdentifierExists(writer, sensorDataset.getSensor().getSensorDescription(),
                    getOceanSITEHelper().getArrayDefinition(), OceanSITESConstants.ARRAY);
        }
        return false;
    }

    protected CDMNode addNetwork(NetcdfFileWriter writer, AbstractSensorDataset sensorDataset) {
        if (sensorDataset.getSensor().isSetSensorDescription()) {
            addAttributeIfIdentifierExists(writer, sensorDataset.getSensor().getSensorDescription(),
                    getOceanSITEHelper().getNetworkDefinition(), OceanSITESConstants.NETWORK);

        }
        return getAttribute(writer, OceanSITESConstants.NETWORK);
    }

    protected CDMNode addProject(NetcdfFileWriter writer, AbstractSensorDataset sensorDataset) {
        if (sensorDataset.getSensor().isSetSensorDescription()) {
            if (addAttributeIfIdentifierExists(writer, sensorDataset.getSensor().getSensorDescription(),
                    getOceanSITEHelper().getProjectDefinition(), ACDDConstants.PROJECT)) {
                if (getOceanSITEHelper().isSetProject()) {
                    return writer.addGroupAttribute(null,
                            new Attribute(ACDDConstants.PROJECT, getOceanSITEHelper().getProject()));
                }
            }
        }
        return getAttribute(writer, ACDDConstants.PROJECT);
    }

    @Override
    protected CDMNode addId(NetcdfFileWriter writer, AbstractSensorDataset sensorDataset) throws EncodingException {
        return writer.addGroupAttribute(null,
                new Attribute(ACDDConstants.ID, getFilename(sensorDataset).replace(DOT_NC, "")));
    }

    protected CDMNode addArea(NetcdfFileWriter writer, AbstractSensorDataset sensorDataset) {
        if (sensorDataset.getSensor().isSetSensorDescription()) {
            addAttributeIfIdentifierExists(writer, sensorDataset.getSensor().getSensorDescription(),
                    getOceanSITEHelper().getAreaDefinition(), OceanSITESConstants.AREA);

        }
        return getAttribute(writer, OceanSITESConstants.AREA);
    }

    protected CDMNode addCitation(NetcdfFileWriter writer, AbstractSensorDataset sensorDataset) {
        String citationText = OceanSITESConstants.CITATION_DEFAULT_TEXT;
        if (getOceanSITEHelper().isSetCitation()) {
            citationText = getOceanSITEHelper().getCitation();
        }
        return writer.addGroupAttribute(null, new Attribute(OceanSITESConstants.CITATION, citationText));
    }

    protected CDMNode addProcessingLevel(NetcdfFileWriter writer, AbstractSensorDataset sensorDataset) {
        // TODO get from ???
        return writer.addGroupAttribute(null, new Attribute(ACDDConstants.PROCESSING_LEVEL, ACDDConstants.NONE));
    }

    protected CDMNode addQcIndicator(NetcdfFileWriter writer, AbstractSensorDataset sensorDataset) {
        // TODO get from ???
        return writer.addGroupAttribute(null, new Attribute(OceanSITESConstants.QC_INDICATOR,
                OceanSITESConstants.QCIndicator.UNKNOWN.name()));
    }

    protected CDMNode addNetcdfVersion(NetcdfFileWriter writer) {
        String netCDFVersion = "3.5";
        if (Version.netcdf4.equals(writer.getVersion())) {
            netCDFVersion = "4.0";
        }
        return writer.addGroupAttribute(null, new Attribute(OceanSITESConstants.NETCDF_VERSION, netCDFVersion));

    }

    protected CDMNode addReferences(NetcdfFileWriter writer, AbstractSensorDataset sensorDataset) {
        if (getOceanSITEHelper().isSetReferences()) {
            return writer.addGroupAttribute(null, new Attribute(CFConstants.REFERENCES, getOceanSITEHelper()
                    .getReferences()));
        }
        return null;
    }

    protected CDMNode addNamingAuthority(NetcdfFileWriter writer, AbstractSensorDataset sensorDataset) {
        return writer.addGroupAttribute(null, new Attribute(ACDDConstants.NAMING_AUTHORITY,
                OceanSITESConstants.NAMING_AUTHORITY_TEXT));
    }

    protected boolean addPrincipalInvestigator(AbstractSensorML sml, NetcdfFileWriter writer)
            throws OwsExceptionReport {
        return addContributor(sml, CiRoleCodes.CI_RoleCode_principalInvestigator, writer);
    }

    protected boolean addPrincipalInvestigator(AbstractSensorML sml, CiRoleCodes ciRoleCode, NetcdfFileWriter writer)
            throws OwsExceptionReport {
        return addContributor(sml, ciRoleCode.getIdentifier(), writer);
    }

    protected boolean addPrincipalInvestigator(AbstractSensorML sml, String contactRole, NetcdfFileWriter writer)
            throws OwsExceptionReport {
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
        } else {
            String mail = getServiceProvider().getServiceContact().getContactInfo().flatMap(OwsContact::getAddress)
                    .map(OwsAddress::getElectronicMailAddress).map(l -> Iterables.getFirst(l, null)).orElse(null);
            String name = getServiceProvider().getProviderName();
            String url = getServiceProvider().getProviderSite().flatMap(OwsOnlineResource::getHref).map(URI::toString)
                    .orElse(null);
            writer.addGroupAttribute(null, new Attribute(OceanSITESConstants.PRINCIPAL_INVESTIGATOR, name));
            writer.addGroupAttribute(null, new Attribute(OceanSITESConstants.PRINCIPAL_INVESTIGATOR_EMAIL, mail));
            writer.addGroupAttribute(null, new Attribute(OceanSITESConstants.PRINCIPAL_INVESTIGATOR_URL, url));
            return true;
        }
    }

    private Attribute getValidMin(double d) {
        return new Attribute(CFConstants.VALID_MIN, d);
    }

    private Attribute getValidMax(double d) {
        return new Attribute(CFConstants.VALID_MAX, d);
    }

    private Attribute getDefaultQcIndicatorAttribute() {
        return new Attribute(OceanSITESConstants.QC_INDICATOR,
                OceanSITESConstants.QCIndicatorValues.QCI_0.getMeaning());
    }

    @Override
    protected String getObservedPropertyStandardName(OmObservableProperty obsProp) {
        try {
            return getVariableName(obsProp.getIdentifier()).getStandardName();
        } catch (IllegalArgumentException iae) {
            LOGGER.debug("The observed property is not a defined OceanSITES variable standard name.", iae);
        }
        return obsProp.getIdentifier();
    }

    @Override
    protected String getObservedPropertyLongName(OmObservableProperty obsProp) {
        try {
            return getVariableName(obsProp.getIdentifier()).getStandardName();
        } catch (IllegalArgumentException iae) {
            LOGGER.debug("The observed property is not a defined OceanSITES variable long name.", iae);
        }
        return null;
    }

    private OceanSITESConstants.VariableName getVariableName(String identifier) {
        return OceanSITESConstants.VariableName.valueOf(getPrefixlessIdentifier(identifier).toUpperCase(Locale.ROOT));
    }

    private String getDataType(FeatureType featureType) {
        switch (featureType) {
            case timeSeries:
                return OceanSITESConstants.DataType.OS_TIME_SERIES.getType();
            case timeSeriesProfile:
                return OceanSITESConstants.DataType.OS_PROFILE.getType();
            case trajectory:
                return OceanSITESConstants.DataType.OS_TRAJECTORY.getType();
            default:
                return OceanSITESConstants.DataType.OS_TIME_SERIES.getType();
        }
    }

    @Override
    protected DataType getDataType() {
        return DataType.FLOAT;
    }

    @Override
    protected String getTimeUnits() {
        return OceanSITESConstants.UNITS_TIME;
    }

    @Override
    protected boolean useHeight() {
        return false;
    }

    @Override
    protected String getVariableDimensionCaseName(String name) {
        return name.toUpperCase(Locale.ROOT);
    }

    @Override
    protected Version getDefaultVersion() {
        return Version.netcdf3;
    }

    @Override
    protected String getFilename(AbstractSensorDataset sensorDataset) throws EncodingException {
        List<Time> times = Lists.newArrayList(sensorDataset.getTimes());
        Collections.sort(times);
        DateTime firstTime = getDateTime(times.get(0));
        DateTime lastTime = getDateTime(times.get(times.size() - 1));
        // prefix
        StringBuilder pathBuffer = new StringBuilder("OS_");
        // platform code
        pathBuffer.append("_").append(getPrefixlessIdentifier(sensorDataset.getSensorIdentifier()));
        // deployment code
        pathBuffer.append("_").append(makeDateSafe(new DateTime(DateTimeZone.UTC)));
        // data mode
        pathBuffer.append("_").append(getOceanSITEHelper().getDataMode().name());
        // partx/times
        pathBuffer.append("_").append(makeDateSafe(firstTime)).append("-").append(makeDateSafe(lastTime));
        // todo
        pathBuffer.append("_").append(Long.toString(java.lang.System.nanoTime())).append(DOT_NC);
        return pathBuffer.toString();
        // return super.getFilename(sensorDataset);
    }

    @Inject
    public void setOceanSITESHelper(OceanSITESHelper oceanSITESHelper) {
        this.oceanSITESHelper = Objects.requireNonNull(oceanSITESHelper);
    }

    private OceanSITESHelper getOceanSITEHelper() {
        return this.oceanSITESHelper;
    }

}
