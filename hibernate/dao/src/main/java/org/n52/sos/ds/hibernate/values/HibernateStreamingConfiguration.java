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
package org.n52.sos.ds.hibernate.values;

import org.n52.sos.config.SettingsManager;
import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.config.annotation.Setting;

/**
 * Configuration class for Hibernate streaming settings
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 *
 */
@Configurable
public class HibernateStreamingConfiguration {
    
    public static int DEFAULT_CHUNK_SIZE = 10000;
    
    public static boolean DEFAULT_STREAMING_DATASOURCE = true;
    
    public static boolean DEFAULT_CHUNK_STREAMING_DATASOURCE = true; 
    
    private static HibernateStreamingConfiguration instance;

    private boolean streamingDatasource = DEFAULT_STREAMING_DATASOURCE;

    private boolean chunkDatasourceStreaming = DEFAULT_CHUNK_STREAMING_DATASOURCE;

    private int chunkSize = DEFAULT_CHUNK_SIZE;

    /**
     * @return Returns a singleton instance of the ServiceConfiguration.
     */
    public static synchronized HibernateStreamingConfiguration getInstance() {
        if (instance == null) {
            instance = new HibernateStreamingConfiguration();
            SettingsManager.getInstance().configure(instance);
        }
        return instance;
    }

    /**
     * private constructor for singleton
     */
    private HibernateStreamingConfiguration() {
    }

    /**
     * Set the indicator to force streaming datasource
     * 
     * @param streamingDatasource
     *            Value to set
     */
    @Setting(HibernateStreamingSettings.FORCE_DATASOURCE_STREAMING)
    public void setForceDatasourceStreaming(boolean streamingDatasource) {
        this.streamingDatasource = streamingDatasource;
    }

    /**
     * Check if streaming values should be used
     * 
     * @return <code>true</code>, if datasource streaming is activated
     */
    public boolean isForceDatasourceStreaming() {
        return streamingDatasource;
    }

    /**
     * Set the indicator to use chunk or scrollable streaming
     * 
     * @param chunkDatasourceStreaming
     *            Value to set
     */
    @Setting(HibernateStreamingSettings.DATASOURCE_STREAMING_APPROACH)
    public void setChunkDatasourceStreaming(boolean chunkDatasourceStreaming) {
        this.chunkDatasourceStreaming = chunkDatasourceStreaming;
    }

    /**
     * Check for streaming mode to use
     * 
     * @return <code>true</code>, if chunk streaming should be used
     *         <code>false</code>, if scrollable should be used
     */
    public boolean isChunkDatasourceStreaming() {
        return chunkDatasourceStreaming;
    }

    /**
     * Set the chunk size for chunk streaming
     * 
     * @param chunkSize
     *            Size to set
     */
    @Setting(HibernateStreamingSettings.CHUNK_SIZE)
    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    /**
     * Get the chunk size
     * 
     * @return the chunk wize
     */
    public int getChunkSize() {
        return chunkSize;
    }

}
