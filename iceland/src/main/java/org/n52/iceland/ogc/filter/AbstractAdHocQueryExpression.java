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
package org.n52.iceland.ogc.filter;

import java.util.Collection;
import java.util.Set;

import javax.xml.namespace.QName;

import org.n52.iceland.util.CollectionHelper;

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
