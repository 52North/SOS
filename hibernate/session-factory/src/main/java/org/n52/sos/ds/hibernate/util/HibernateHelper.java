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
package org.n52.sos.ds.hibernate.util;

import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.NamedQueryDefinition;
import org.hibernate.engine.spi.NamedSQLQueryDefinition;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory;
import org.hibernate.hql.internal.ast.QueryTranslatorImpl;
import org.hibernate.hql.spi.QueryTranslatorFactory;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.internal.SessionImpl;
import org.hibernate.loader.criteria.CriteriaJoinWalker;
import org.hibernate.loader.criteria.CriteriaQueryTranslator;
import org.hibernate.persister.entity.OuterJoinLoadable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Hibernate helper class.
 *
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.0.0
 *
 */
public final class HibernateHelper {
    /**
     * Private constructor
     */
    private HibernateHelper() {

    }

    /**
     * Get the SQL query string from Criteria.
     *
     * @param criteria
     *            Criteria to get SQL query string from
     * @return SQL query string from criteria
     */
    public static String getSqlString(Criteria criteria) {
        CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
        SessionImplementor session = criteriaImpl.getSession();
        SessionFactoryImplementor factory = session.getFactory();
        CriteriaQueryTranslator translator =
                new CriteriaQueryTranslator(factory, criteriaImpl, criteriaImpl.getEntityOrClassName(),
                        CriteriaQueryTranslator.ROOT_SQL_ALIAS);
        String[] implementors = factory.getImplementors(criteriaImpl.getEntityOrClassName());

        CriteriaJoinWalker walker =
                new CriteriaJoinWalker((OuterJoinLoadable) factory.getEntityPersister(implementors[0]), translator,
                        factory, criteriaImpl, criteriaImpl.getEntityOrClassName(), session.getLoadQueryInfluencers());

        return walker.getSQLString();
    }

    /**
     * Get the SQL query string from HQL Query.
     *
     * @param query
     *            HQL query to convert to SQL
     * @return SQL query string from HQL
     */
    public static String getSqlString(Query query, Session session) {
        final QueryTranslatorFactory ast = new ASTQueryTranslatorFactory();
        SessionFactory sessionFactory = session.getSessionFactory();
        final QueryTranslatorImpl qt =
                (QueryTranslatorImpl) ast.createQueryTranslator("id", query.getQueryString(), Maps.newHashMap(),
                        (SessionFactoryImplementor) sessionFactory, null);
        qt.compile(null, false);
        return qt.getSQLString();
    }

    /**
     * Checks if the specified entity is supported.
     *
     * @param clazz
     *            the class
     *
     * @return if the entity supported

     */
    public static boolean isEntitySupported(Class<?> clazz) {
        return HibernateMetadataCache.getInstance().isEntitySupported(clazz);
    }

    /**
     * Checks if the specified column is supported by this entity.
     * 
     * @param clazz
     *            the class
     * @param column
     *            the column
     * @return if the column supported
     */
    public static boolean isColumnSupported(Class<?> clazz, String column) {
        return HibernateMetadataCache.getInstance().isColumnSupported(clazz, column);
    }

    /**
     * Checks if the specified named query is supported.
     * 
     * @param namedQuery
     *            the named query
     * @param session
     *            Hibernate session
     * @return if the named query supported
     */
    public static boolean isNamedQuerySupported(String namedQuery, Session session) {
        NamedQueryDefinition namedQueryDef = ((SessionImpl) session).getSessionFactory().getNamedQuery(namedQuery);
        NamedSQLQueryDefinition namedSQLQueryDef =
                ((SessionImpl) session).getSessionFactory().getNamedSQLQuery(namedQuery);
        return namedQueryDef != null || namedSQLQueryDef != null;
    }

    public static Dialect getDialect(Session session) {
        return ((SessionFactoryImplementor) session.getSessionFactory()).getDialect();
    }

    public static List<List<Long>> getValidSizedLists(Collection<Long> queryIds) {
        List<Long> queryIdsList = Lists.newArrayList(queryIds);
        List<List<Long>> lists = Lists.newArrayList();
        if (queryIds.size() > HibernateConstants.LIMIT_EXPRESSION_DEPTH) {
            int startIndex = 0;
            int endIndex = HibernateConstants.LIMIT_EXPRESSION_DEPTH - 1;
            while (startIndex < queryIdsList.size() - 1) {
                if (endIndex > (queryIdsList.size())) {
                    endIndex = (queryIdsList.size());
                }
                lists.add(queryIdsList.subList(startIndex, endIndex));
                startIndex = endIndex;
                endIndex = endIndex + HibernateConstants.LIMIT_EXPRESSION_DEPTH - 1;
            }
        } else {
            lists.add(queryIdsList);
        }
        return lists;
    }

    /**
     * Check if the requested function is supported by the requested dialect
     *
     * @param dialect
     *            Dialect to check
     * @param function
     *            Function to check
     * @return <code>true</code>, if function is supported
     */
    public static boolean supportsFunction(Dialect dialect, String function) {
        return dialect.getFunctions().containsKey(function);
    }

}
