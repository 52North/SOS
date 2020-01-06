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
package org.n52.sos.ds.hibernate.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasCoordinate;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasDeletedFlag;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasDisabledFlag;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasGeometry;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasParentChilds;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasProcedureDescriptionFormat;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.Constants;
import org.n52.sos.util.StringHelper;

import com.google.common.collect.Sets;

/**
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 *
 * @since 4.0.0
 *
 */
public class Procedure extends SpatialEntity implements Serializable, HasDeletedFlag, HasProcedureDescriptionFormat,
        HasGeometry, HasCoordinate, HasDisabledFlag, HasParentChilds<Procedure> {

    private static final long serialVersionUID = -3115365895730874831L;

    public static final String ID = "procedureId";

    public static final String DESCRIPTION_URL = "descriptionUrl";

    public static final String ALIAS = "proc";

    public static final String ALIAS_DOT = ALIAS + Constants.DOT_STRING;

    private long procedureId;

    private ProcedureDescriptionFormat procedureDescriptionFormat;

    private boolean deleted;

    private String descriptionFile;

    private boolean disabled;

    private boolean reference;

    private Procedure typeOf;

    private boolean isType;

    private boolean isAggregation;

    private boolean mobile = false;

    private boolean insitu = true;

    private Set<Procedure> childs = Sets.newHashSet();

    private Set<Procedure> parents = Sets.newHashSet();

    public long getProcedureId() {
        return procedureId;
    }

    public void setProcedureId(long procedureId) {
        this.procedureId = procedureId;
    }

    @Override
    public ProcedureDescriptionFormat getProcedureDescriptionFormat() {
        return procedureDescriptionFormat;
    }

    @Override
    public void setProcedureDescriptionFormat(ProcedureDescriptionFormat procedureDescriptionFormat) {
        this.procedureDescriptionFormat = procedureDescriptionFormat;
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getDescriptionFile() {
        return descriptionFile;
    }

    public void setDescriptionFile(String descriptionFile) {
        this.descriptionFile = descriptionFile;
    }

    public boolean isSetDescriptionFile() {
        return StringHelper.isNotEmpty(descriptionFile);
    }

    @Override
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public boolean getDisabled() {
        return disabled;
    }

    @Override
    public boolean isDisabled() {
        return getDisabled();
    }

    @Override
    public boolean getDeleted() {
        return deleted;
    }

    public boolean isReference() {
        return reference;
    }

    public void setReference(boolean reference) {
        this.reference = reference;
    }

    public Procedure getTypeOf() {
        return typeOf;
    }

    public void setTypeOf(Procedure typeOf) {
        this.typeOf = typeOf;
    }

    /**
     * @return <code>true</code>, if is not null
     */
    public boolean isSetTypeOf() {
        return getTypeOf() != null;
    }

    public boolean isType() {
        return isType;
    }

    public boolean getIsType() {
        return isType;
    }

    /**
     * @param isType the isType to set
     */
    public void setIsType(boolean isType) {
        this.isType = isType;
    }

    /**
     * @return the isAggregation
     */
    public boolean isAggregation() {
        return isAggregation;
    }

    /**
     * @return the isAggregation
     */
    public boolean getIsAggregation() {
        return isAggregation;
    }

    /**
     * @param isAggregation the isAggregation to set
     */
    public void setIsAggregation(boolean isAggregation) {
        this.isAggregation = isAggregation;
    }

    /**
     * @return the mobile
     */
    public boolean isMobile() {
        return mobile;
    }

    /**
     * @return the mobile
     */
    public boolean getMobile() {
        return mobile;
    }

    /**
     * @param mobile the mobile to set
     */
    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }

    /**
     * @return the insitu
     */
    public boolean isInsitu() {
        return insitu;
    }

    /**
     * @return the insitu
     */
    public boolean getInsitu() {
        return insitu;
    }

    /**
     * @param insitu the insitu to set
     */
    public void setInsitu(boolean insitu) {
        this.insitu = insitu;
    }

    @Override
    public Set<Procedure> getParents() {
        return parents;
    }

    @Override
    public void setParents(Set<Procedure> parents) {
        this.parents = parents;
    }

    @Override
    public Set<Procedure> getChilds() {
        return childs;
    }

    @Override
    public void setChilds(Set<Procedure> childs) {
        this.childs = childs;
    }

    @Override
    public void addParent(Procedure parent) {
        if (parent == null) {
            return;
        }
        if (parents == null) {
            parents = new HashSet<>();
        }
        parents.add(parent);
    }

    @Override
    public void addChild(Procedure child) {
        if (child == null) {
            return;
        }
        if (childs == null) {
            childs = new HashSet<>();
        }
        childs.add(child);
    }

    @Override
    public boolean hasParents() {
        return CollectionHelper.isNotEmpty(getParents());
    }

    @Override
    public boolean hasChilds() {
        return CollectionHelper.isNotEmpty(getChilds());
    }
}
