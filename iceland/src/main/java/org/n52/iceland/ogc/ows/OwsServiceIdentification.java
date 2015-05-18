/**
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.ogc.ows;


import java.util.Collection;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.xmlbeans.XmlObject;
import org.n52.iceland.i18n.MultilingualString;
import org.n52.iceland.util.CollectionHelper;
import org.n52.iceland.util.StringHelper;

/**
 * @since 4.0.0
 *
 */
public class OwsServiceIdentification {
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
