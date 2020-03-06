/**
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.netcdf;

import java.util.Collections;
import java.util.Set;

import org.n52.sos.config.SettingsManager;
import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.config.annotation.Setting;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.iso.CodeList.CiRoleCodes;
import org.n52.sos.util.Constants;

import com.axiomalaska.cf4j.CFStandardName;
import com.axiomalaska.cf4j.CFStandardNames;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import ucar.nc2.NetcdfFileWriter.Version;

/**
 * Helper class for netCDF encoding. Holds the netCDF setting values.
 * 
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
@Configurable
public class NetcdfHelper {
    
    private static NetcdfHelper instance;
    
    private Version version;
    
    private int chunkSizeTime;
    
    private double fillValue;
    
    private CFStandardName heightDepth;
    
    private String variableType;
    
    private boolean upperCaseNames;
    
    private CiRoleCodes contributor;
    
    private CiRoleCodes publisher;
    
    private Set<String> latitude = Collections.emptySet();
    
    private Set<String> longitude = Collections.emptySet();
    
    private Set<String> z = Collections.emptySet();
    
    /**
     * @return Returns a singleton instance of the AqdHelper.
     */
    public static synchronized NetcdfHelper getInstance() {
        if (instance == null) {
            instance = new NetcdfHelper();
            SettingsManager.getInstance().configure(instance);
        }
        return instance;
    }

    private NetcdfHelper() {
        // private constructor
    }
    
    /**
     * @param version
     * @throws ConfigurationException
     */
    @Setting(NetcdfSettings.NETCDF_VERSION)
    public void setNetcdfVersion(String version) throws ConfigurationException {
        this.version = Version.valueOf(version);
    }
    
    /**
     * @return
     */
    public Version getNetcdfVersion() {
        return version;
    }
    
    
    /**
     * @return
     */
    public String getNetcdfVersionString() {
        return version.name();
    }
    
    /**
     * @param chunkSize
     */
    @Setting(NetcdfSettings.NETCDF_CHUNK_SIZE_TIME)
    public void setChunkSizeTime(int chunkSize) {
        this.chunkSizeTime = chunkSize;
    }
    
    /**
     * @return
     */
    public int getChunkSizeTime() {
        return chunkSizeTime;
    }
    
    /**
     * @param fillValue
     */
    @Setting(NetcdfSettings.NETCDF_FILL_VALUE)
    public void setFillValue(double fillValue) {
        this.fillValue = fillValue;
    }
    
    /**
     * @return
     */
    public double getFillValue() {
        return fillValue;
    }
    
    /**
     * @return
     */
    public float getFillValueAsFloat() {
        return (float) fillValue;
    }
    
    /**
     * @param heightDepth
     */
    @Setting(NetcdfSettings.NETCDF_HEIGHT_DEPTH)
    public void setHeightDepth(String heightDepth) {
        this.heightDepth = getStandardName(heightDepth);
    }
    
    /**
     * @return
     */
    public CFStandardName getHeightDepth() {
        return heightDepth;
    }

    /**
     * @param heightDepth
     * @return
     */
    private CFStandardName getStandardName(String heightDepth) {
        if (CFStandardNames.HEIGHT.getName().equals(heightDepth)) {
            return CFStandardNames.HEIGHT;
        } 
        return CFStandardNames.DEPTH;
    }
    
    /**
     * @param type
     */
    @Setting(NetcdfSettings.NETCDF_VARIABLE_TYPE)
    public void setVariableType(String type) {
        this.variableType = type;
    }
    
    /**
     * @return
     */
    public String getVariableType() {
        return variableType;
    }

    /**
     * @return the upperCaseNames
     */
    public boolean isUpperCaseNames() {
        return upperCaseNames;
    }

    /**
     * @param upperCaseNames the upperCaseNames to set
     */
    @Setting(NetcdfSettings.NETCDF_VARIABLE_UPPER_CASE)
    public void setUpperCaseNames(boolean upperCaseNames) {
        this.upperCaseNames = upperCaseNames;
    }

    /**
     * @return the contributor
     */
    public CiRoleCodes getContributor() {
        return contributor;
    }

    /**
     * @param contributor the contributor to set
     */
    @Setting(NetcdfSettings.NETCDF_CONTRIBUTOR)
    public void setContributor(String contributor) {
        this.contributor = CiRoleCodes.valueOf(contributor);
    }

    /**
     * @return the publisher
     */
    public CiRoleCodes getPublisher() {
        return publisher;
    }

    /**
     * @param publisher the publisher to set
     */
    @Setting(NetcdfSettings.NETCDF_PUBLISHER)
    public void setPublisher(String publisher) {
        this.publisher = CiRoleCodes.valueOf(publisher);
    }

    /**
     * @return the latitude
     */
    public Set<String> getLatitude() {
        return latitude;
    }

    /**
     * @param latitude the latitude to set
     */
    @Setting(NetcdfSettings.NETCDF_PHEN_LATITUDE)
    public void setLatitude(String latitude) {
        if (!Strings.isNullOrEmpty(latitude)) {
            this.latitude = Sets.newHashSet(latitude.split(Constants.COMMA_STRING));
        } else {
            this.latitude.clear();
        }
    }

    /**
     * @return the longitude
     */
    public Set<String> getLongitude() {
        return longitude;
    }

    /**
     * @param longitude the longitude to set
     */
    @Setting(NetcdfSettings.NETCDF_PHEN_LONGITUDE)
    public void setLongitude(String longitude) {
        if (!Strings.isNullOrEmpty(longitude)) {
            this.longitude = Sets.newHashSet(longitude.split(Constants.COMMA_STRING));
        } else {
            this.longitude.clear();
        }
    }

    /**
     * @return the z
     */
    public Set<String> getZ() {
        return z;
    }

    /**
     * @param z the z to set
     */
    @Setting(NetcdfSettings.NETCDF_PHEN_Z)
    public void setZ(String z) {
        if (!Strings.isNullOrEmpty(z)) {
            this.z = Sets.newHashSet(z.split(Constants.COMMA_STRING));
        } else {
            this.z.clear();
        }
    }

}
