/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.dao.i18n;

import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.n52.sos.ds.hibernate.entities.AbstractIdentifierNameDescriptionEntity;
import org.n52.sos.ds.hibernate.entities.Codespace;
import org.n52.sos.ds.hibernate.entities.i18n.AbstractFeatureI18N;
import org.n52.sos.ds.hibernate.entities.i18n.I18NInsertionObject;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * Abstract DAO class for i18n support.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.0.0
 * 
 */
public abstract class AbstractFeatureI18NDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFeatureI18NDAO.class);

    /**
     * Get all I18N objects
     * 
     * @param session
     *            Hibernate Session
     * @return All I18N objects
     */
    public abstract List<AbstractFeatureI18N> getObjects(Session session);

    /**
     * Get all I18N objects for a specific {@link Codespace}
     * 
     * @param codespace
     *            Specific {@link Codespace}
     * @param session
     *            Hibernate Session
     * @return
     */
    public abstract List<AbstractFeatureI18N> getObjects(final Codespace codespace, Session session);

    /**
     * Get all I18N objects for a specific object identifier
     * 
     * @param objectIdentifier
     * @param session
     *            Hibernate Session
     * @return All I18N objects for a specific object identifier
     */
    public abstract List<AbstractFeatureI18N> getObjectsForIdentifier(final String objectIdentifier, Session session);

    /**
     * Get all I18N objects for a specific language/codespace
     * 
     * @param codespace
     *            Specific language/codespace
     * @param session
     *            Hibernate Session
     * @return All I18N objects for a specific language/codespace
     */
    public abstract List<AbstractFeatureI18N> getObjectsForCodespaceString(final String codespace, Session session);

    /**
     * Get all I18N objects for a specific object id
     * 
     * @param objectId
     *            Specific object id
     * @param session
     *            Hibernate Session
     * @return All I18N objects for a specific object id
     */
    public abstract List<AbstractFeatureI18N> getObjects(final long objectId, Session session);

    /**
     * Get I18N object for a specific object id and {@link Codespace}
     * 
     * @param objectId
     *            Specific object id
     * @param codespace
     *            Specific {@link Codespace}
     * @param session
     *            Hibernate Session
     * @return I18N object for a specific object id and {@link Codespace}
     */
    public abstract AbstractFeatureI18N getObject(final long objectId, final Codespace codespace, Session session);

    /**
     * Get I18N object for a specific object id and language/codespace
     * 
     * @param objectId
     *            Specific object id
     * @param codespace
     *            Specific language/codespace
     * @param session
     *            Hibernate Session
     * @return I18N object for a specific object id and language/codespace
     */
    public abstract AbstractFeatureI18N getObject(final long objectId, final String codespace, Session session);

    /**
     * Get all I18N objects for a specific entity of type
     * {@link AbstractIdentifierNameDescriptionEntity}
     * 
     * @param object
     *            Specific entity
     * @param session
     *            Hibernate Session
     * @return All I18N objects for a specific entity of type
     */
    public abstract List<AbstractFeatureI18N> getObjects(final AbstractIdentifierNameDescriptionEntity object,
            Session session);

    /**
     * Get I18N object for a specific entity of type
     * {@link AbstractIdentifierNameDescriptionEntity} and {@link Codespace}
     * 
     * @param object
     *            Specific entity
     * @param codespace
     *            Specific {@link Codespace}
     * @param session
     *            Hibernate Session
     * @return I18N object for a specific entity of type
     *         {@link AbstractIdentifierNameDescriptionEntity} and
     *         {@link Codespace}
     */
    public abstract AbstractFeatureI18N getObject(final AbstractIdentifierNameDescriptionEntity object,
            final Codespace codespace, Session session);

    /**
     * Get I18N object for a specific entity of type
     * {@link AbstractIdentifierNameDescriptionEntity} and language/codespace
     * 
     * @param object
     *            Specific entity
     * @param codespace
     *            Specific language/codespace
     * @param session
     *            Hibernate Session
     * @return I18N object for a specific entity of type
     *         {@link AbstractIdentifierNameDescriptionEntity} and
     *         language/codespace
     */
    public abstract AbstractFeatureI18N getObject(final AbstractIdentifierNameDescriptionEntity object,
            final String codespace, Session session);

    /**
     * Get all I18N object for a specific {@link AbstractFeatureI18N}
     * 
     * @param abstractI18N
     *            Specific {@link AbstractFeatureI18N}
     * @param session
     *            Hibernate Session
     * @return All I18N object for a specific {@link AbstractFeatureI18N}
     */
    public abstract List<AbstractFeatureI18N> getObjects(final AbstractFeatureI18N abstractI18N, Session session);

    /**
     * Get I18N object for a specific {@link AbstractFeatureI18N} and
     * {@link Codespace}
     * 
     * @param abstractI18N
     *            Specific {@link AbstractFeatureI18N}
     * @param codespace
     *            Dpecific {@link Codespace}
     * @param session
     *            Hibernate Session
     * @return I18N object for a specific {@link AbstractFeatureI18N} and
     *         {@link Codespace}
     */
    public abstract AbstractFeatureI18N getObject(final AbstractFeatureI18N abstractI18N, final Codespace codespace,
            Session session);

    /**
     * Get I18N object for a specific {@link AbstractFeatureI18N} and
     * language/codespace
     * 
     * @param abstractI18N
     *            Specific {@link AbstractFeatureI18N}
     * @param codespace
     *            Specific language/codespace
     * @param session
     *            Hibernate Session
     * @return I18N object for a specific {@link AbstractFeatureI18N} and
     *         language/codespace
     */
    public abstract AbstractFeatureI18N getObject(final AbstractFeatureI18N abstractI18N, final String codespace,
            Session session);

    /**
     * Get I18N object for a specific object identifier and language/codespace
     * 
     * @param objectIdentifier
     *            Specific object identifier
     * @param codespace
     *            Specific language/codespace
     * @param session
     *            Hibernate Session
     * @return I18N object for a specific object identifier and
     *         language/codespace
     */
    public abstract AbstractFeatureI18N getObject(final String objectIdentifier, final String codespace,
            Session session);

    /**
     * Get all {@link Codespace} contained in the table
     * 
     * @param session
     *            Hibernate Session
     * @return All {@link Codespace} contained in the table
     */
    public abstract List<Codespace> getCodespace(Session session);

    /**
     * Get all languages/codespace strings contained in the table
     * 
     * @param session
     *            Hibernate Session
     * @return All languages/codespace strings contained in the table
     */
    public abstract Set<String> getCodespaceAsString(Session session);

    /**
     * Insert language specific values into the database for
     * {@link AbstractIdentifierNameDescriptionEntity}
     * 
     * @param object
     *            Entity to insert values for
     * @param insertionObject
     *            Values to insert
     * @param session
     *            Hibernate Session
     */
    public abstract void insertI18N(final AbstractIdentifierNameDescriptionEntity object,
            final I18NInsertionObject insertionObject, Session session);

    /**
     * Insert language specific values into the database for
     * {@link AbstractIdentifierNameDescriptionEntity}
     * 
     * @param object
     *            Entity to insert values for
     * @param insertionObjects
     *            List of values to insert
     * @param session
     *            Hibernate Session
     */
    public abstract void insertI18N(final AbstractIdentifierNameDescriptionEntity object,
            final List<I18NInsertionObject> insertionObjects, Session session);

    /**
     * Add {@link AbstractFeatureI18N} implementation specific values
     * 
     * @param abstractI18N
     *            {@link AbstractFeatureI18N} implementation
     * @param insertionObject
     *            {@link I18NInsertionObject} implementation with specific
     *            values
     */
    protected abstract void addSpecificValuesToObject(AbstractFeatureI18N abstractI18N,
            I18NInsertionObject insertionObject);

    /**
     * Get the specific implementation
     * 
     * @return The specific implementation
     */
    protected abstract AbstractFeatureI18N getI18NImpl();

    /**
     * Insert the values into the database
     * 
     * @param clazz
     *            Class of {@link AbstractFeatureI18N} implementation
     * @param object
     *            {@link AbstractIdentifierNameDescriptionEntity} to insert
     *            values for
     * @param insertionObject
     *            Values to insert
     * @param session
     *            Hibernate Session
     */
    @SuppressWarnings("rawtypes")
    protected void insertAbstractI18N(Class clazz, AbstractIdentifierNameDescriptionEntity object,
            I18NInsertionObject insertionObject, Session session) {
        if (HibernateHelper.isEntitySupported(clazz, session) && insertionObject != null
                && insertionObject.isNotEmpty()) {
            AbstractFeatureI18N abstractI18N = getI18NImpl();
            abstractI18N.setObjectId(object);
            abstractI18N.setCodespace(insertionObject.getCodespace());
            if (insertionObject.isSetName()) {
                abstractI18N.setName(insertionObject.getName());
            }
            if (insertionObject.isSetDescription()) {
                abstractI18N.setDescription(insertionObject.getDescription());
            }
            addSpecificValuesToObject(abstractI18N, insertionObject);
            session.saveOrUpdate(abstractI18N);
            session.flush();
        }
    }

    /**
     * Query all {@link AbstractFeatureI18N} for the class of
     * {@link AbstractFeatureI18N} implementation
     * 
     * @param clazz
     *            Class of {@link AbstractFeatureI18N} implementation
     * @param session
     *            Hibernate Session
     * @return All {@link AbstractFeatureI18N} for the class of
     *         {@link AbstractFeatureI18N} implementation
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected List<AbstractFeatureI18N> getAbstractI18NObjects(Class clazz, Session session) {
        Criteria criteria = getDefaultCriteria(clazz, session);
        LOGGER.debug("QUERY getProcedureObjects(): {}", HibernateHelper.getSqlString(criteria));
        return (List<AbstractFeatureI18N>) criteria.list();
    }

    /**
     * Query all {@link Codespace} for the class of {@link AbstractFeatureI18N}
     * implementation
     * 
     * @param clazz
     *            Class of {@link AbstractFeatureI18N} implementation
     * @param session
     *            Hibernate Session
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected List<Codespace> getAbstractI18NCodespace(Class clazz, Session session) {
        Criteria criteria =
                getDefaultCriteria(clazz, session).setProjection(
                        Projections.distinct(Projections.property(AbstractFeatureI18N.CODESPACE)));
        LOGGER.debug("QUERY getProcedureObjects(): {}", HibernateHelper.getSqlString(criteria));
        return (List<Codespace>) criteria.list();

    }

    /**
     * Query all languages/codespace strings for the class of
     * {@link AbstractFeatureI18N} implementation
     * 
     * @param clazz
     *            Class of {@link AbstractFeatureI18N} implementation
     * @param session
     *            Hibernate Session
     * @return
     */
    @SuppressWarnings("rawtypes")
    protected Set<String> getAbstractI18NCodespaceString(Class clazz, Session session) {
        List<Codespace> codespaces = getAbstractI18NCodespace(clazz, session);
        Set<String> codespaceStrings = Sets.newHashSet();
        for (Codespace c : codespaces) {
            codespaceStrings.add(c.getCodespace());
        }
        return codespaceStrings;
    }

    /**
     * Query all {@link AbstractFeatureI18N} for the class of
     * {@link AbstractFeatureI18N} implementation and the specific
     * {@link Codespace}
     * 
     * @param clazz
     *            Class of {@link AbstractFeatureI18N} implementation
     * @param codespace
     *            the specific {@link Codespace}
     * @param session
     *            Hibernate Session
     * @return All {@link AbstractFeatureI18N} for the class of
     *         {@link AbstractFeatureI18N} implementation and the specific
     *         {@link Codespace}
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected List<AbstractFeatureI18N> getAbstractI18NObjects(Class clazz, Codespace codespace, Session session) {
        Criteria criteria =
                getDefaultCriteria(clazz, session).add(Restrictions.eq(AbstractFeatureI18N.CODESPACE, codespace));
        LOGGER.debug("QUERY getProcedureObjects(): {}", HibernateHelper.getSqlString(criteria));
        return (List<AbstractFeatureI18N>) criteria.list();
    }

    /**
     * Query all {@link AbstractFeatureI18N} for the class of
     * {@link AbstractFeatureI18N} implementation for the specific object
     * identifier
     * 
     * @param clazz
     *            Class of {@link AbstractFeatureI18N} implementation
     * @param objectIdentifier
     * @param session
     *            Hibernate Session
     * @return All {@link AbstractFeatureI18N} for the class of
     *         {@link AbstractFeatureI18N} implementation for the specific
     *         object identifier
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected List<AbstractFeatureI18N> getAbstractI18NObjectsFor(Class clazz, String objectIdentifier, Session session) {
        Criteria criteria = getDefaultCriteria(clazz, session);
        criteria.createCriteria(AbstractFeatureI18N.OBJECT_ID).add(
                Restrictions.eq(AbstractIdentifierNameDescriptionEntity.IDENTIFIER, objectIdentifier));
        LOGGER.debug("QUERY getProcedureObjects(): {}", HibernateHelper.getSqlString(criteria));
        return (List<AbstractFeatureI18N>) criteria.list();
    }

    /**
     * Query all {@link AbstractFeatureI18N} for the class of
     * {@link AbstractFeatureI18N} implementation and the specific
     * language/codespace string
     * 
     * @param clazz
     *            Class of {@link AbstractFeatureI18N} implementation
     * @param codespace
     *            the specific language/codespace string
     * @param session
     *            Hibernate Session
     * @return All {@link AbstractFeatureI18N} for the class of
     *         {@link AbstractFeatureI18N} implementation and the specific
     *         language/codespace string
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected List<AbstractFeatureI18N> getAbstractI18NObjectsForCodespaceString(Class clazz, String codespace,
            Session session) {
        Criteria criteria = getDefaultCriteria(clazz, session);
        criteria.createCriteria(AbstractFeatureI18N.CODESPACE).add(Restrictions.eq(Codespace.CODESPACE, codespace));
        LOGGER.debug("QUERY getProcedureObjects(): {}", HibernateHelper.getSqlString(criteria));
        return (List<AbstractFeatureI18N>) criteria.list();
    }

    /**
     * Query {@link AbstractFeatureI18N} for the class of
     * {@link AbstractFeatureI18N} implementation, the specific object id and
     * {@link Codespace}
     * 
     * @param clazz
     *            Class of {@link AbstractFeatureI18N} implementation
     * @param objectId
     *            he specific object id
     * @param codespace
     *            he specific {@link Codespace}
     * @param session
     *            Hibernate Session
     * @return {@link AbstractFeatureI18N} for the class of
     *         {@link AbstractFeatureI18N} implementation, the specific object
     *         id and {@link Codespace}
     */
    @SuppressWarnings({ "rawtypes" })
    protected AbstractFeatureI18N getAbstractI18NObjects(Class clazz, long objectId, Codespace codespace,
            Session session) {
        Criteria criteria =
                getDefaultCriteria(clazz, session).add(Restrictions.eq(AbstractFeatureI18N.OBJECT_ID, objectId)).add(
                        Restrictions.eq(AbstractFeatureI18N.CODESPACE, codespace));
        LOGGER.debug("QUERY getProcedureObjects(): {}", HibernateHelper.getSqlString(criteria));
        return (AbstractFeatureI18N) criteria.uniqueResult();
    }

    /**
     * Query {@link AbstractFeatureI18N} for the class of
     * {@link AbstractFeatureI18N} implementation , the specific object id and
     * language/codespace string
     * 
     * @param clazz
     *            Class of {@link AbstractFeatureI18N} implementation
     * @param objectId
     *            the specific object id
     * @param codespace
     *            the specific language/codespace string
     * @param session
     *            Hibernate Session
     * @return {@link AbstractFeatureI18N} for the class of
     *         {@link AbstractFeatureI18N} implementation , the specific object
     *         id and language/codespace string
     */
    @SuppressWarnings({ "rawtypes" })
    protected AbstractFeatureI18N getAbstractI18NObjects(Class clazz, long objectId, String codespace, Session session) {
        Criteria criteria =
                getDefaultCriteria(clazz, session).add(Restrictions.eq(AbstractFeatureI18N.OBJECT_ID, objectId));
        criteria.createCriteria(AbstractFeatureI18N.CODESPACE).add(Restrictions.eq(Codespace.CODESPACE, codespace));
        LOGGER.debug("QUERY getProcedureObjects(): {}", HibernateHelper.getSqlString(criteria));
        return (AbstractFeatureI18N) criteria.uniqueResult();
    }

    /**
     * Query all {@link AbstractFeatureI18N} for the class of
     * {@link AbstractFeatureI18N} implementation and the specific object id
     * 
     * @param clazz
     *            Class of {@link AbstractFeatureI18N} implementation
     * @param objectId
     *            the specific object id
     * @param session
     *            Hibernate Session
     * @return All {@link AbstractFeatureI18N} for the class of
     *         {@link AbstractFeatureI18N} implementation and the specific
     *         object id
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected List<AbstractFeatureI18N> getAbstractI18NObjects(Class clazz, long objectId, Session session) {
        Criteria criteria =
                getDefaultCriteria(clazz, session).add(Restrictions.eq(AbstractFeatureI18N.OBJECT_ID, objectId));
        LOGGER.debug("QUERY getProcedureObjects(): {}", HibernateHelper.getSqlString(criteria));
        return (List<AbstractFeatureI18N>) criteria.list();
    }

    /**
     * Query all {@link AbstractFeatureI18N} for the class of
     * {@link AbstractFeatureI18N} implementation, the specific
     * {@link AbstractIdentifierNameDescriptionEntity} and {@link Codespace}
     * 
     * @param clazz
     *            Class of {@link AbstractFeatureI18N} implementation
     * @param object
     *            the specific {@link AbstractIdentifierNameDescriptionEntity}
     * @param codespace
     *            the specific {@link Codespace}
     * @param session
     *            Hibernate Session
     * @return {@link AbstractFeatureI18N} for the class of
     *         {@link AbstractFeatureI18N} implementation, the specific
     *         {@link AbstractIdentifierNameDescriptionEntity} and
     *         {@link Codespace}
     * 
     */
    @SuppressWarnings({ "rawtypes" })
    protected AbstractFeatureI18N getAbstractI18NObjects(Class clazz, AbstractIdentifierNameDescriptionEntity object,
            Codespace codespace, Session session) {
        Criteria criteria =
                getDefaultCriteria(clazz, session).add(
                        Restrictions.eq(AbstractFeatureI18N.CODESPACE, codespace.getCodespace()));
        criteria.createCriteria(AbstractFeatureI18N.OBJECT_ID).add(
                Restrictions.eq(AbstractIdentifierNameDescriptionEntity.IDENTIFIER, object.getIdentifier()));
        LOGGER.debug("QUERY getProcedureObjects(): {}", HibernateHelper.getSqlString(criteria));
        return (AbstractFeatureI18N) criteria.uniqueResult();
    }

    /**
     * Query all {@link AbstractFeatureI18N} for the class of
     * {@link AbstractFeatureI18N} implementation, the specific
     * {@link AbstractIdentifierNameDescriptionEntity} and language/codespace
     * string
     * 
     * @param clazz
     *            Class of {@link AbstractFeatureI18N} implementation
     * @param object
     *            the specific {@link AbstractIdentifierNameDescriptionEntity}
     * @param codespace
     *            the specific language/codespace string
     * @param session
     *            Hibernate Session
     * @return all {@link AbstractFeatureI18N} for the class of
     *         {@link AbstractFeatureI18N} implementation, the specific
     *         {@link AbstractIdentifierNameDescriptionEntity} and
     *         language/codespace string
     * 
     */
    @SuppressWarnings({ "rawtypes" })
    protected AbstractFeatureI18N getAbstractI18NObjects(Class clazz, AbstractIdentifierNameDescriptionEntity object,
            String codespace, Session session) {
        Criteria criteria = getDefaultCriteria(clazz, session);
        criteria.createCriteria(AbstractFeatureI18N.OBJECT_ID).add(
                Restrictions.eq(AbstractIdentifierNameDescriptionEntity.IDENTIFIER, object.getIdentifier()));
        criteria.createCriteria(AbstractFeatureI18N.CODESPACE).add(Restrictions.eq(Codespace.CODESPACE, codespace));
        LOGGER.debug("QUERY getProcedureObjects(): {}", HibernateHelper.getSqlString(criteria));
        return (AbstractFeatureI18N) criteria.uniqueResult();
    }

    /**
     * Query all {@link AbstractFeatureI18N} for the class of
     * {@link AbstractFeatureI18N} implementation and the specific
     * {@link AbstractIdentifierNameDescriptionEntity}
     * 
     * @param clazz
     *            Class of {@link AbstractFeatureI18N} implementation
     * @param object
     *            the specific {@link AbstractIdentifierNameDescriptionEntity}
     * @param session
     *            Hibernate Session
     * @return All {@link AbstractFeatureI18N} for the class of
     *         {@link AbstractFeatureI18N} implementation and the specific
     *         {@link AbstractIdentifierNameDescriptionEntity}
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected List<AbstractFeatureI18N> getAbstractI18NObjects(Class clazz,
            AbstractIdentifierNameDescriptionEntity object, Session session) {
        Criteria criteria = getDefaultCriteria(clazz, session);
        criteria.createCriteria(AbstractFeatureI18N.OBJECT_ID).add(
                Restrictions.eq(AbstractIdentifierNameDescriptionEntity.IDENTIFIER, object.getIdentifier()));
        LOGGER.debug("QUERY getProcedureObjects(): {}", HibernateHelper.getSqlString(criteria));
        return (List<AbstractFeatureI18N>) criteria.list();
    }

    /**
     * Query {@link AbstractFeatureI18N} for the class of
     * {@link AbstractFeatureI18N} implementation, the specific
     * {@link AbstractFeatureI18N} and {@link Codespace}
     * 
     * @param clazz
     *            Class of {@link AbstractFeatureI18N} implementation
     * @param abstractI18N
     *            the specific {@link AbstractFeatureI18N}
     * @param codespace
     *            the specific {@link Codespace}
     * @param session
     *            Hibernate Session
     * @return {@link AbstractFeatureI18N} for the class of
     *         {@link AbstractFeatureI18N} implementation, the specific
     *         {@link AbstractFeatureI18N} and {@link Codespace}
     */
    @SuppressWarnings({ "rawtypes" })
    protected AbstractFeatureI18N getAbstractI18NObjects(Class clazz, AbstractFeatureI18N abstractI18N,
            Codespace codespace, Session session) {
        Criteria criteria =
                getDefaultCriteria(clazz, session).add(Restrictions.eq(AbstractFeatureI18N.OBJECT_ID, abstractI18N))
                        .add(Restrictions.eq(AbstractFeatureI18N.CODESPACE, codespace));
        LOGGER.debug("QUERY getProcedureObjects(): {}", HibernateHelper.getSqlString(criteria));
        return (AbstractFeatureI18N) criteria.uniqueResult();
    }

    /**
     * Query {@link AbstractFeatureI18N} for the class of
     * {@link AbstractFeatureI18N} implementation, the specific
     * {@link AbstractFeatureI18N} and language/codespace string
     * 
     * @param clazz
     *            Class of {@link AbstractFeatureI18N} implementation
     * @param abstractI18N
     *            the specific {@link AbstractFeatureI18N}
     * @param codespace
     *            the specific language/codespace string
     * @param session
     *            Hibernate Session
     * @return {@link AbstractFeatureI18N} for the class of
     *         {@link AbstractFeatureI18N} implementation, the specific
     *         {@link AbstractFeatureI18N} and {@link Codespace}
     */
    @SuppressWarnings({ "rawtypes" })
    protected AbstractFeatureI18N getAbstractI18NObjects(Class clazz, AbstractFeatureI18N abstractI18N,
            String codespace, Session session) {
        Criteria criteria =
                getDefaultCriteria(clazz, session).add(Restrictions.eq(AbstractFeatureI18N.OBJECT_ID, abstractI18N));
        criteria.createCriteria(AbstractFeatureI18N.CODESPACE).add(Restrictions.eq(Codespace.CODESPACE, codespace));
        LOGGER.debug("QUERY getProcedureObjects(): {}", HibernateHelper.getSqlString(criteria));
        return (AbstractFeatureI18N) criteria.uniqueResult();
    }

    /**
     * Query all {@link AbstractFeatureI18N} for the class of
     * {@link AbstractFeatureI18N} implementation and the specific
     * {@link AbstractFeatureI18N}
     * 
     * @param clazz
     *            Class of {@link AbstractFeatureI18N} implementation
     * @param abstractI18N
     *            the specific {@link AbstractFeatureI18N}
     * @param session
     *            Hibernate Session
     * @return All {@link AbstractFeatureI18N} for the class of
     *         {@link AbstractFeatureI18N} implementation and the specific
     *         {@link AbstractFeatureI18N}
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected List<AbstractFeatureI18N> getAbstractI18NObjects(Class clazz, AbstractFeatureI18N abstractI18N,
            Session session) {
        Criteria criteria =
                getDefaultCriteria(clazz, session).add(Restrictions.eq(AbstractFeatureI18N.OBJECT_ID, abstractI18N));
        LOGGER.debug("QUERY getProcedureObjects(): {}", HibernateHelper.getSqlString(criteria));
        return (List<AbstractFeatureI18N>) criteria.list();
    }

    /**
     * Query all {@link AbstractFeatureI18N} for the class of
     * {@link AbstractFeatureI18N} implementation Get the default Criteria for
     * the class
     * 
     * @param clazz
     *            Class of {@link AbstractFeatureI18N} implementation
     * @param session
     *            Hibernate Session
     * @return Default criteria
     */
    @SuppressWarnings("rawtypes")
    private Criteria getDefaultCriteria(Class clazz, Session session) {
        return session.createCriteria(clazz).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    }
}
