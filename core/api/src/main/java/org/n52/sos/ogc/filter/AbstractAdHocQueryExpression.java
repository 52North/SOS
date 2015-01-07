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
package org.n52.sos.ogc.filter;

import java.util.Collection;
import java.util.Set;

import javax.xml.namespace.QName;

import org.n52.sos.util.CollectionHelper;

import com.google.common.collect.Sets;

/**
 * SOS class for FES 2.0 AbstractAdHocQueryExpression
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * 
 * @since 4.0.0
 *
 */
public abstract class AbstractAdHocQueryExpression extends AbstractQueryExpression {

    private Set<AbstractProjectionClause> projectionClauses;

    private AbstractSelectionClause selectionClause;

    private AbstractSortingClause sortingClause;

    private Set<QName> typeNames;

    private Set<String> aliases;
    
    public AbstractAdHocQueryExpression(Collection<QName> typeNames) {
        setTypeNames(typeNames);
    }

    /**
     * @return the projectionClauses
     */
    public Set<AbstractProjectionClause> getProjectionClauses() {
        return projectionClauses;
    }
    
    /**
     * @param projectionClause the projectionClause to add
     */
    public AbstractAdHocQueryExpression addProjectionClause(AbstractProjectionClause projectionClause) {
        getProjectionClauses().add(projectionClause);
        return this;
    }
    
    /**
     * @param projectionClauses the projectionClauses to add
     */
    public AbstractAdHocQueryExpression addProjectionClauses(Set<AbstractProjectionClause> projectionClauses) {
        getProjectionClauses().addAll(projectionClauses);
        return this;
    }

    /**
     * @param projectionClauses the projectionClauses to set
     */
    public AbstractAdHocQueryExpression setProjectionClauses(Set<AbstractProjectionClause> projectionClauses) {
        this.projectionClauses = projectionClauses;
        return this;
    }
    
    public boolean isSetProjectionClauses() {
        return CollectionHelper.isNotEmpty(getProjectionClauses());
    }

    /**
     * @return the selectionClause
     */
    public AbstractSelectionClause getSelectionClause() {
        return selectionClause;
    }

    /**
     * @param selectionClause the selectionClause to set
     */
    public AbstractAdHocQueryExpression setSelectionClause(AbstractSelectionClause selectionClause) {
        this.selectionClause = selectionClause;
        return this;
    }
    
    public boolean isSetSelectionClause() {
        return getSelectionClause() != null;
    }

    /**
     * @return the sortingClause
     */
    public AbstractSortingClause getSortingClause() {
        return sortingClause;
    }

    /**
     * @param sortingClause the sortingClause to set
     */
    public AbstractAdHocQueryExpression setSortingClause(AbstractSortingClause sortingClause) {
        this.sortingClause = sortingClause;
        return this;
    }
    
    public boolean isSetSortingClause() {
        return getSortingClause() != null;
    }

    /**
     * @return the typeNames
     */
    public Set<QName> getTypeNames() {
        return typeNames;
    }

    /**
     * @param typeNames the typeNames to set
     */
    private void setTypeNames(Collection<QName> typeNames) {
        this.typeNames = Sets.newHashSet(typeNames);
    }

    /**
     * @return the aliases
     */
    public Set<String> getAliases() {
        return aliases;
    }

    /**
     * @param aliases the aliases to set
     */
    public AbstractAdHocQueryExpression setAliases(Collection<String> aliases) {
        this.aliases = Sets.newHashSet(aliases);
        return this;
    }
    
    public boolean isSetAliases() {
        return CollectionHelper.isNotEmpty(getAliases());
    }
}
