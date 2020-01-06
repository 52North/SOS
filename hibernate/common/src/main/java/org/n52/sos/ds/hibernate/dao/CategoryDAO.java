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
package org.n52.sos.ds.hibernate.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.n52.sos.ds.hibernate.entities.Category;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ogc.om.AbstractPhenomenon;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CategoryDAO extends AbstractIdentifierNameDescriptionDAO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryDAO.class);
    
    /**
     * Get category by identifier
     *
     * @param identifier
     *            The category's identifier
     * @param session
     *            Hibernate session
     * @return Category object
     */
    public Category getCategoryForIdentifier(final String identifier, final Session session) {
        Criteria criteria = session.createCriteria(Category.class)
                .add(Restrictions.eq(ObservableProperty.IDENTIFIER, identifier));
        LOGGER.debug("QUERY getCategoryForIdentifier(identifier): {}",
                HibernateHelper.getSqlString(criteria));
        return (Category) criteria.uniqueResult();
    }
    
    public Category getOrInsertCategory(NamedValue<String> category, Session session) {
        if (category.getValue() instanceof SweText) {
            return getOrInsertCategory((SweText) category.getValue(), session);
        } else {
            return getOrInsertCategory(category.getValue().getValue(), session);
        }
    }
    
    public Category getOrInsertCategory(AbstractPhenomenon observableProperty, Session session) {
        Category category = getCategoryForIdentifier(observableProperty.getIdentifier(), session);
        if (category == null) {
            category = new Category();
            addIdentifierNameDescription(observableProperty, category, session);
            session.save(category);
            session.flush();
            session.refresh(category);
        }
        return category;
    }

    public Category getOrInsertCategory(ObservableProperty observableProperty, Session session) {
        return getOrInsertCategory((OmObservableProperty) getAndAddIdentifierNameDescription(
                new OmObservableProperty(""), observableProperty), session);
    }

    private Category getOrInsertCategory(String value, Session session) {
        Category category = getCategoryForIdentifier(value, session);
        if (category == null) {
            category = new Category();
            addIdentifier(category, value, session);
            session.save(category);
            session.flush();
            session.refresh(category);
        }
        return category;
        
    }

    private Category getOrInsertCategory(SweText text, Session session) {
        Category category = getCategoryForIdentifier(text.getValue(), session);
        if (category == null) {
            category = new Category();
            addIdentifier(category, text.getValue(), session);
            if (text.isSetName()) {
                addName(category, text.getName(), session);
            }
            if (text.isSetDescription()) {
                addDescription(category, text.getDescription());
            }
            session.save(category);
            session.flush();
            session.refresh(category);
        }
        return category;
    }
    
}
