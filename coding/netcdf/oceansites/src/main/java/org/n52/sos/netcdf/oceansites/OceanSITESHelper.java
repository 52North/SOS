/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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

import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.shetland.iso.CodeList.CiRoleCodes;

import com.google.common.base.Strings;

/**
 * Helper class for OceanSITES netCDF encoding. Holds the OceanSITES netCDF
 * setting values.
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
@Configurable
public class OceanSITESHelper {

    private String siteDefinition;

    private String platformDefinition;

    private String dataModeDefinition;

    private OceanSITESConstants.DataMode dataMode;

    private String license;

    private String citation;

    private String acknowledgement;

    private String project;

    private String projectDefinition;

    private String arrayDefinition;

    private String networkDefinition;

    private String wmoPlatformCodeDefinition;

    private String formatVersion;

    private CiRoleCodes principalInvestigator;

    private String references;

    private String areaDefinition;

    /**
     * @return the siteDefinition
     */
    public String getSiteDefinition() {
        return siteDefinition;
    }

    /**
     * @param siteDefinition
     *            the siteDefinition to set
     */
    @Setting(OceanSITESSettingsProvider.OCEANSITES_SITE_DEFINITON)
    public void setSiteDefinition(String siteDefinition) {
        this.siteDefinition = siteDefinition;
    }

    /**
     * @return the platformDefinition
     */
    public String getPlatformDefinition() {
        return platformDefinition;
    }

    /**
     * @param platformDefinition
     *            the platformDefinition to set
     */
    @Setting(OceanSITESSettingsProvider.OCEANSITES_PLATFORM_DEFINITION)
    public void setPlatformDefinition(String platformDefinition) {
        this.platformDefinition = platformDefinition;
    }

    /**
     * @return the dataModeDefinition
     */
    public String getDataModeDefinition() {
        return dataModeDefinition;
    }

    /**
     * @param dataModeDefinition
     *            the dataModeDefinition to set
     */
    @Setting(OceanSITESSettingsProvider.OCEANSITES_DATA_MODE_DEFINITION)
    public void setDataModeDefinition(String dataModeDefinition) {
        this.dataModeDefinition = dataModeDefinition;
    }

    /**
     * @return the dataMode
     */
    public OceanSITESConstants.DataMode getDataMode() {
        return dataMode;
    }

    /**
     * @param dataMode
     *            the dataMode to set
     */
    @Setting(OceanSITESSettingsProvider.OCEANSITES_DATA_MODE)
    public void setDataMode(String dataMode) {
        this.dataMode = OceanSITESConstants.DataMode.valueOf(dataMode);
    }

    public boolean isSetDataMode() {
        return getDataMode() != null;
    }

    @Setting(OceanSITESSettingsProvider.OCEANSITES_LICENSE)
    public void setLicense(String license) {
        this.license = license;
    }

    public String getLicense() {
        return license;
    }

    public boolean isSetLicense() {
        return !Strings.isNullOrEmpty(getLicense());
    }

    @Setting(OceanSITESSettingsProvider.OCEANSITES_CITATION)
    public void setCitation(String citation) {
        this.citation = citation;
    }

    /**
     * @return the citation
     */
    public String getCitation() {
        return citation;
    }

    public boolean isSetCitation() {
        return !Strings.isNullOrEmpty(getCitation());
    }

    @Setting(OceanSITESSettingsProvider.OCEANSITES_ACKNOWLEDGEMENT)
    public void setAcknowledgement(String acknowledgement) {
        this.acknowledgement = acknowledgement;
    }

    /**
     * @return the acknowledgement
     */
    public String getAcknowledgement() {
        return acknowledgement;
    }

    @Setting(OceanSITESSettingsProvider.OCEANSITES_PROJECT)
    public void setProject(String project) {
        this.project = project;
    }

    /**
     * @return the project
     */
    public String getProject() {
        return project;
    }

    public boolean isSetProject() {
        return !Strings.isNullOrEmpty(getProject());
    }

    @Setting(OceanSITESSettingsProvider.OCEANSITES_PROJECT_DEFINITION)
    public void setProjectDefinition(String definition) {
        this.projectDefinition = definition;
    }

    /**
     * @return the projectDefinition
     */
    public String getProjectDefinition() {
        return projectDefinition;
    }

    @Setting(OceanSITESSettingsProvider.OCEANSITES_ARRAY_DEFINITION)
    public void setArrayDefinition(String definition) {
        this.arrayDefinition = definition;
    }

    /**
     * @return the arrayDefinition
     */
    public String getArrayDefinition() {
        return arrayDefinition;
    }

    @Setting(OceanSITESSettingsProvider.OCEANSITES_NETWORK_DEFINITION)
    public void setNetworkDefinition(String definition) {
        this.networkDefinition = definition;
    }

    /**
     * @return the networkDefinition
     */
    public String getNetworkDefinition() {
        return networkDefinition;
    }

    @Setting(OceanSITESSettingsProvider.OCEANSITES_WMO_PLATFORM_CODE_DEFINITION)
    public void setWmoPlatformCodeDefinition(String definition) {
        this.wmoPlatformCodeDefinition = definition;
    }

    /**
     * @return the wmoPlatformCodeDefinition
     */
    public String getWmoPlatformCodeDefinition() {
        return wmoPlatformCodeDefinition;
    }

    /**
     * @return the formatVersion
     */
    public String getFormatVersion() {
        return formatVersion;
    }

    /**
     * @param formatVersion
     *            the formatVersion to set
     */
    @Setting(OceanSITESSettingsProvider.OCEANSITES_FORMAT_VERSION)
    public void setFormatVersion(String formatVersion) {
        this.formatVersion = formatVersion;
    }

    /**
     * @return the principalInvestigator
     */
    public CiRoleCodes getPrincipalInvestigator() {
        return principalInvestigator;
    }

    /**
     * @param principalInvestigator
     *            the principalInvestigator to set
     */
    @Setting(OceanSITESSettingsProvider.OCEANSITES_PRINCIPAL_INVESTIGATOR)
    public void setPrincipalInvestigator(String principalInvestigator) {
        this.principalInvestigator = CiRoleCodes.valueOf(principalInvestigator);
    }

    /**
     * @return the references
     */
    public String getReferences() {
        return references;
    }

    /**
     * @param references
     *            the references to set
     */
    @Setting(OceanSITESSettingsProvider.OCEANSITES_REFERENCES)
    public void setReferences(String references) {
        this.references = references;
    }

    public boolean isSetReferences() {
        return !Strings.isNullOrEmpty(getReferences());
    }

    /**
     * @return the area
     */
    public String getAreaDefinition() {
        return areaDefinition;
    }

    /**
     * @param areaDefinition
     *            the area to set
     */
    @Setting(OceanSITESSettingsProvider.OCEANSITES_AREA_DEFINITION)
    public void setAreaDefinition(String areaDefinition) {
        this.areaDefinition = areaDefinition;
    }

    public boolean isSetAreaDefinition() {
        return !Strings.isNullOrEmpty(getAreaDefinition());
    }

}
