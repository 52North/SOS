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
package org.n52.sos.netcdf;

import org.n52.sos.config.SettingsManager;
import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.config.annotation.Setting;
import org.n52.sos.exception.ConfigurationException;

import com.axiomalaska.cf4j.CFStandardName;
import com.axiomalaska.cf4j.CFStandardNames;

import ucar.nc2.NetcdfFileWriter.Version;

@Configurable
public class NetcdfHelper {
    
    private static NetcdfHelper instance;
    
    private Version version;
    
    private int chunkSizeTime;
    
    private double fillValue;
    
    private CFStandardName heightDepth;
    
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
    
    @Setting(NetcdfSettings.NETCDF_VERSION)
    public void setNetcdfVersion(String version) throws ConfigurationException {
        this.version = Version.valueOf(version);
    }
    
    public Version getNetcdfVersion() {
        return version;
    }
    
    public String getNetcdfVersionString() {
        return version.name();
    }
    
    @Setting(NetcdfSettings.NETCDF_CHUNK_SIZE_TIME)
    public void setChunkSizeTime(int chunkSize) {
        this.chunkSizeTime = chunkSize;
    }
    
    public int getChunkSizeTime() {
        return chunkSizeTime;
    }
    
    @Setting(NetcdfSettings.NETCDF_FILL_VALUE)
    public void setFillValue(String fillValue) {
        this.fillValue = Double.parseDouble(fillValue);
    }
    
    // TODO change to use the double method if NumericSettingDefinition is supported
    
//    @Setting(NetcdfSettings.NETCDF_FILL_VALUE)
//    public void setFillValue(double fillValue) {
//        this.fillValue = fillValue;
//    }
    
    public double getFillValue() {
        return fillValue;
    }
    
    public float getFillValueAsFloat() {
        return (float) fillValue;
    }
    
    @Setting(NetcdfSettings.NETCDF_HEIGHT_DEPTH)
    public void setHeightDepth(String heightDepth) {
        this.heightDepth = getStandardName(heightDepth);
    }
    
    public CFStandardName getHeightDepth() {
        return heightDepth;
    }

    private CFStandardName getStandardName(String heightDepth) {
        if (CFStandardNames.HEIGHT.getName().equals(heightDepth)) {
            return CFStandardNames.HEIGHT;
        } 
        return CFStandardNames.DEPTH;
    }

}
