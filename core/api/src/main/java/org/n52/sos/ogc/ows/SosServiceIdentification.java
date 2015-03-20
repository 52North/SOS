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
package org.n52.sos.ogc.ows;


import java.util.Collection;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.xmlbeans.XmlObject;

import org.n52.sos.i18n.MultilingualString;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.StringHelper;

/**
 * @since 4.0.0
 *
 */
public class SosServiceIdentification {
    private XmlObject serviceIdentification;
    private MultilingualString title;
    private MultilingualString abstrakt;
    private String serviceType;
    private String serviceTypeCodeSpace;
    private String fees;
    private final SortedSet<String> accessConstraints = new TreeSet<String>();
    private final SortedSet<String> versions = new TreeSet<String>();
    private final SortedSet<String> profiles = new TreeSet<String>();
    private final SortedSet<String> keywords = new TreeSet<String>();

    public XmlObject getServiceIdentification() {
        return serviceIdentification;
    }

    public void setServiceIdentification(final XmlObject serviceIdentification) {
        this.serviceIdentification = serviceIdentification;
    }

    public SortedSet<String> getVersions() {
        return Collections.unmodifiableSortedSet(versions);
    }

    public void setVersions(final Collection<String> versions) {
        this.versions.clear();
        if (versions != null) {
            this.versions.addAll(versions);
        }
    }

    public boolean hasVersions() {
        return CollectionHelper.isNotEmpty(getVersions());
    }

    public SortedSet<String> getProfiles() {
        return Collections.unmodifiableSortedSet(profiles);
    }

    public synchronized void setProfiles(final Collection<String> profiles) {
        this.profiles.clear();
        if (profiles != null) {
            this.profiles.addAll(profiles);
        }
    }

    public boolean hasProfiles() {
        return CollectionHelper.isNotEmpty(getProfiles());
    }

    public SortedSet<String> getKeywords() {
        return Collections.unmodifiableSortedSet(keywords);
    }

    public void setKeywords(final Collection<String> keywords) {
        this.keywords.clear();
        if (keywords != null) {
            this.keywords.addAll(keywords);
        }
    }

    public boolean hasKeywords() {
        return CollectionHelper.isNotEmpty(getKeywords());
    }

    public MultilingualString getTitle() {
        return title;
    }

    public void setTitle(MultilingualString title) {
        this.title = title;
    }

    public boolean hasTitle() {
        return this.title != null;
    }

    public MultilingualString getAbstract() {
        return abstrakt;
    }

    public void setAbstract(MultilingualString abstrakt) {
        this.abstrakt = abstrakt;
    }

    public boolean hasAbstract() {
        return this.abstrakt != null;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(final String serviceType) {
        this.serviceType = serviceType;
    }

    public boolean hasServiceType() {
        return StringHelper.isNotEmpty(getServiceType());
    }

    public String getServiceTypeCodeSpace() {
        return serviceTypeCodeSpace;
    }

    public void setServiceTypeCodeSpace(final String serviceTypeCodeSpace) {
        this.serviceTypeCodeSpace = serviceTypeCodeSpace;
    }

    public boolean hasServiceTypeCodeSpace() {
        return StringHelper.isNotEmpty(getServiceTypeCodeSpace());
    }

    public String getFees() {
        return fees;
    }

    public void setFees(final String fees) {
        this.fees = fees;
    }

    public boolean hasFees() {
        return StringHelper.isNotEmpty(getFees());
    }

    public SortedSet<String> getAccessConstraints() {
        return Collections.unmodifiableSortedSet(accessConstraints);
    }

    public void setAccessConstraints(Collection<String> accessConstraints) {
        this.accessConstraints.addAll(accessConstraints);
    }

    public boolean hasAccessConstraints() {
        return CollectionHelper.isNotEmpty(getAccessConstraints());
    }

    public void addAccessConstraint(final String accessConstraints) {
        this.accessConstraints.add(accessConstraints);
    }
}
