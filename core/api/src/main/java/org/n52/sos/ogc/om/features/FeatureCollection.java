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
package org.n52.sos.ogc.om.features;

import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;

import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.EmptyIterator;

import com.google.common.collect.Maps;

/**
 * class represents a GMl feature collection
 * 
 * @since 4.0.0
 */
public class FeatureCollection extends AbstractFeature implements Iterable<AbstractFeature> {
    private static final long serialVersionUID = -6527441724827160710L;

    /**
     * members of this feature collection
     */
    private SortedMap<String, AbstractFeature> members = Maps.<String, AbstractFeature> newTreeMap();

    /**
     * constructor
     */
    public FeatureCollection() {
        super(new CodeWithAuthority("gml:FeatureCollection"));
    }

    /**
     * constructor
     * 
     * @param members
     *            collection with feature members of this collection
     */
    public FeatureCollection(final Map<String, AbstractFeature> members) {
        super(new CodeWithAuthority("gml:FeatureCollection"));
        this.members.clear();
        this.members.putAll(members);
    }

    /**
     * Get features
     * 
     * @return the members
     */
    public Map<String, AbstractFeature> getMembers() {
        return members;
    }

    /**
     * Set features
     * 
     * @param members
     *            the members to set
     */
    public void setMembers(Map<String, AbstractFeature> members) {
        this.members.putAll(members);
    }

    public FeatureCollection addMember(AbstractFeature member) {
        if (member.isSetIdentifier()) {
            members.put(member.getIdentifierCodeWithAuthority().getValue(), member);
            return this;
        } else if (member.isSetGmlID()) {
            members.put(member.getGmlId(), member);
            return this;
        }
        members.put(member.toString(), member);
        return this;
    }

    /**
     * @param featureIdentifier
     *            the id
     * @return the removed feature
     * @see Map#remove(Object)
     */
    public AbstractFeature removeMember(String featureIdentifier) {
        return members.remove(featureIdentifier);
    }

    /**
     * Check whether members are set
     * 
     * @return <code>true</code>, if members are set
     */
    public boolean isSetMembers() {
        return CollectionHelper.isNotEmpty(getMembers());
    }

    @Override
    public Iterator<AbstractFeature> iterator() {
        if (isSetMembers()) {
            return getMembers().values().iterator();
        } else {
            return EmptyIterator.instance();
        }
    }
}
