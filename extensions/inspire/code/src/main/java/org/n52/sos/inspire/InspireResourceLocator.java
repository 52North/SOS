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
package org.n52.sos.inspire;

import java.util.Collection;
import java.util.Set;

import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.StringHelper;
import org.n52.sos.util.http.MediaType;

import com.google.common.collect.Sets;

/**
 * Service internal representation of INSPIRE resource locator
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 * 
 */
public class InspireResourceLocator {

    /* Element URL 1..1 */
    private String url;

    /* Element MediaType 0..* */
    private Set<MediaType> mediaTypes = Sets.newHashSet();
    
    public InspireResourceLocator(String url) {
        setURL(url);
    }

    /**
     * Get the URL
     * 
     * @return the URL
     */
    public String getURL() {
        return url;
    }

    /**
     * Set the URL
     * 
     * @param url
     *            the URL to set
     */
    private void setURL(String url) {
        this.url = url;
    }

    /**
     * Check if the URL is set
     * 
     * @return <code>true</code>, if the URL is set
     */
    public boolean isSetUrl() {
        return StringHelper.isNotEmpty(getURL());
    }

    /**
     * Get the {@link MediaType}s
     * 
     * @return the media types
     */
    public Set<MediaType> getMediaTypes() {
        return mediaTypes;
    }

    /**
     * Set the {@link MediaType}s, clears the existing collection
     * 
     * @param mediaTypes
     *            the media types to set
     * @return this
     * 
     */
    public InspireResourceLocator setMediaTypes(Collection<MediaType> mediaTypes) {
        getMediaTypes().clear();
        if (CollectionHelper.isNotEmpty(mediaTypes)) {
            getMediaTypes().addAll(mediaTypes);
        }
        return this;
    }

    /**
     * Add a {@link MediaType}
     * 
     * @param mediaType
     *            the media type to add
     * @return this
     */
    public InspireResourceLocator addMediaType(MediaType mediaType) {
        getMediaTypes().add(mediaType);
        return this;
    }

    /**
     * Check if media types are set
     * 
     * @return <code>true</code>, if media types are set
     */
    public boolean isSetMediaTypes() {
        return CollectionHelper.isNotEmpty(getMediaTypes());
    }

    @Override
    public String toString() {
        return String.format("%s %n[%n url=%s,%n mediaTypes=%s%n]", this.getClass().getSimpleName(), getURL(),
                CollectionHelper.collectionToString(getMediaTypes()));
    }

}
