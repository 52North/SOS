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
package org.n52.sos.ds.hibernate.entities.metadata;

public class SeriesMetadata {

    public static final String SERIES_ID = "seriesId";
    public static final String DOMAIN = "domain";
    public static String ID = "metadataId";
    private long metadataId;
    private long seriesId;
    private String identifier;
    private String value;
    private String domain;
    
    /**
     * @return the metadataId
     */
    public long getMetadataId() {
        return metadataId;
    }

    /**
     * @param metadataId the metadataId to set
     */
    public void setMetadataId(long metadataId) {
        this.metadataId = metadataId;
    }

    /**
     * Get series
     *
     * @return Series
     */
    public long getSeriesId() {
        return seriesId;
    }

    /**
     * Set series
     *
     * @param series
     *            Series
     */
    public void setSeriesId(final long seriesId) {
        this.seriesId = seriesId;
    }

    /**
     * @return the identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * @param identifier the identifier to set
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the domain
     */
    public String getDomain() {
        return domain;
    }

    /**
     * @param domain the domain to set
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public String toString() {
        return String.format("SeriesMetadata [metadataId=%s, seriesId=%s, identifier=%s, value=%s, domain=%s]",
                metadataId, seriesId, identifier, value, domain);
    }

}
