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
import java.util.List;
import java.util.Set;

import org.n52.sos.inspire.capabilities.InspireCapabilities.InspireExtendedCapabilitiesResourceLocator;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.StringHelper;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Service internal representation of INSPIRE citation
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 * 
 */
public class InspireCitation implements InspireExtendedCapabilitiesResourceLocator {

    /* Title 1..1 */
    private String title;

    /* Choice: DateOfPublication, DateOfCreation, DateOfLastRevision 1..1 */
    private InspireDateOf dateOf;

    /* Url 0..* */
    private Set<String> urls = Sets.newHashSet();

    /* ResourceLocator 0..* */
    private List<InspireResourceLocator> resourceLocator = Lists.newArrayList();

    /**
     * constructor
     * 
     * @param title
     *            the title
     * @param dateOf
     *            the {@link InspireDateOf}
     */
    public InspireCitation(String title, InspireDateOf dateOf) {
        super();
        setTitle(title);
        setDateOf(dateOf);
    }

    /**
     * Get the title
     * 
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the title
     * 
     * @param title
     *            the title to set
     */
    private void setTitle(String title) {
        this.title = title;
    }

    /**
     * Check if the title is set
     * 
     * @return <code>true</code>, if the title is set
     */
    public boolean isSetTitle() {
        return StringHelper.isNotEmpty(getTitle());
    }

    /**
     * Get the {@link InspireDateOf}
     * 
     * @return the dateOf
     */
    public InspireDateOf getDateOf() {
        return dateOf;
    }

    /**
     * Set the {@link InspireDateOf}
     * 
     * @param dateOf
     *            the dateOf to set
     */
    private void setDateOf(InspireDateOf dateOf) {
        this.dateOf = dateOf;
    }

    /**
     * Check if the {@link InspireDateOf} is set
     * 
     * @return <code>true</code>, if the {@link InspireDateOf} is set
     */
    public boolean isSetDateOf() {
        return getDateOf() != null;
    }

    /**
     * Get the URLs
     * 
     * @return the URLs
     */
    public Set<String> getUrls() {
        return urls;
    }

    /**
     * Set the URLs, clears the existing collection
     * 
     * @param urls
     *            the URLs to set
     * @return this
     */
    public InspireCitation setUrls(Collection<String> urls) {
        if (CollectionHelper.isNotEmpty(urls)) {
            getUrls().clear();
            getUrls().addAll(urls);
        }
        return this;
    }

    /**
     * Add the URL
     * 
     * @param url
     *            the URL to add
     * @return this
     */
    public InspireCitation addUrl(String url) {
        getUrls().add(url);
        return this;
    }

    /**
     * Check if the URLs are set
     * 
     * @return <code>true</code>, if URLs are set
     */
    public boolean isSetUrls() {
        return CollectionHelper.isNotEmpty(getUrls());
    }

    @Override
    public List<InspireResourceLocator> getResourceLocator() {
        return resourceLocator;
    }

    @Override
    public InspireExtendedCapabilitiesResourceLocator setResourceLocator(
            Collection<InspireResourceLocator> resourceLocator) {
        getResourceLocator().clear();
        if (CollectionHelper.isNotEmpty(resourceLocator)) {
            getResourceLocator().addAll(resourceLocator);
        }
        return this;
    }

    @Override
    public InspireExtendedCapabilitiesResourceLocator addResourceLocator(InspireResourceLocator resourceLocator) {
        getResourceLocator().add(resourceLocator);
        return this;
    }

    @Override
    public boolean isSetResourceLocators() {
        return CollectionHelper.isNotEmpty(getResourceLocator());
    }

    @Override
    public String toString() {
        return String.format("%s %n[%n title=%s,%n dateOf=%s,%n urls=%s,%n resourceLocator=%s%n]", this.getClass()
                .getSimpleName(), getTitle(), getDateOf(), CollectionHelper.collectionToString(getUrls()),
                CollectionHelper.collectionToString(getResourceLocator()));
    }
}
