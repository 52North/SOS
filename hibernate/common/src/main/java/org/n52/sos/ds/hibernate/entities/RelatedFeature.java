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
package org.n52.sos.ds.hibernate.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasFeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasOfferings;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasRelatedFeatureRoles;

/**
 * @since 4.0.0
 * 
 */
public class RelatedFeature implements Serializable, HasFeatureOfInterest, HasRelatedFeatureRoles, HasOfferings {

    private static final long serialVersionUID = -8143897383050691280L;

    public static final String ID = "relatedFeatureId";

    private long relatedFeatureId;

    private FeatureOfInterest featureOfInterest;

    private Set<RelatedFeatureRole> relatedFeatureRoles = new HashSet<RelatedFeatureRole>(0);

    private Set<Offering> offerings = new HashSet<Offering>(0);
    
    public RelatedFeature() {
    }

    public long getRelatedFeatureId() {
        return this.relatedFeatureId;
    }

    public void setRelatedFeatureId(long relatedFeatureId) {
        this.relatedFeatureId = relatedFeatureId;
    }

    @Override
    public FeatureOfInterest getFeatureOfInterest() {
        return this.featureOfInterest;
    }

    @Override
    public void setFeatureOfInterest(FeatureOfInterest featureOfInterest) {
        this.featureOfInterest = featureOfInterest;
    }

    @Override
    public Set<RelatedFeatureRole> getRelatedFeatureRoles() {
        return this.relatedFeatureRoles;
    }

    @Override
    public void setRelatedFeatureRoles(Set<RelatedFeatureRole> relatedFeatureRoles) {
        this.relatedFeatureRoles = relatedFeatureRoles;
    }

    @Override
    public Set<Offering> getOfferings() {
        return this.offerings;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setOfferings(final Object offerings) {
        if (offerings instanceof Set<?>) {
            this.offerings = (Set<Offering>) offerings; 
        } else {
            getOfferings().add((Offering)offerings);
        }
    }
    
//    @Override
//    public Offering getOffering() {
//        return this.offering;
//    }
//    
//    @Override
//    public void setOfferings(final Offering offering) {
//        if (getOfferings() == null)  {
//            setOfferings( new HashSet<Offering>(0));
//        }
//        getOfferings().add(offering);
//        this.offering = offering;
//    }
}
