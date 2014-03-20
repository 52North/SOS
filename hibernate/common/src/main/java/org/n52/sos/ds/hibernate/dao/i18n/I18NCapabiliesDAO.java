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
import org.n52.sos.ds.hibernate.entities.Codespace;
import org.n52.sos.ds.hibernate.entities.i18n.I18NCapabilities;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * I18N DAO class for Capabilities values
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.0.0
 * 
 */
public class I18NCapabiliesDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(I18NCapabiliesDAO.class);

    /**
     * Get all I18N Capabilities objects
     * 
     * @param session
     *            Hibernate Session
     * @return List of all I18N Capabilities objects
     */
    @SuppressWarnings({ "unchecked" })
    public List<I18NCapabilities> getI18NCapabilitiesObjects(Session session) {
        Criteria criteria = getDefaultCriteria(session);
        LOGGER.debug("QUERY getI18NCapabilitiesObjects(): {}", HibernateHelper.getSqlString(criteria));
        return (List<I18NCapabilities>) criteria.list();
    }

    /**
     * Get all Codespace objects contained in the table
     * 
     * @param session
     *            Hibernate Session
     * @return List of Codespace objects contained in the table
     */
    @SuppressWarnings({ "unchecked" })
    public List<Codespace> getI18NCapabilitiesCodespace(Session session) {
        Criteria criteria =
                getDefaultCriteria(session).setProjection(
                        Projections.distinct(Projections.property(I18NCapabilities.CODESPACE)));
        LOGGER.debug("QUERY getI18NCapabilitiesCodespace(): {}", HibernateHelper.getSqlString(criteria));
        return (List<Codespace>) criteria.list();

    }

    /**
     * Get all languages/codespaces contained in the table
     * 
     * @param session
     *            Hibernate Session
     * @return List of languages/codespaces contained in the table
     */
    public Set<String> getI18NCapabilitiesCodespaceString(Session session) {
        List<Codespace> codespaces = getI18NCapabilitiesCodespace(session);
        Set<String> codespaceStrings = Sets.newHashSet();
        for (Codespace c : codespaces) {
            codespaceStrings.add(c.getCodespace());
        }
        return codespaceStrings;
    }

    /**
     * Get all I18N Capabilities objects which conform with the requested
     * codespace
     * 
     * @param codespace
     *            Specific codespace
     * @param session
     *            Hibernate Session
     * @return List of all I18N Capabilities objects which conform with the
     *         requested codespace
     */
    @SuppressWarnings("unchecked")
    public List<I18NCapabilities> getI18NCapabilitiesObjects(Codespace codespace, Session session) {
        Criteria criteria = getDefaultCriteria(session).add(Restrictions.eq(I18NCapabilities.CODESPACE, codespace));
        LOGGER.debug("QUERY getI18NCapabilitiesObjects(): {}", HibernateHelper.getSqlString(criteria));
        return (List<I18NCapabilities>) criteria.list();
    }

    /**
     * Get all I18N Capabilities objects which conform with the requested
     * language/codespace
     * 
     * @param codespace
     *            Specific language/codespace
     * @param session
     *            Hibernate Session
     * @return List of all I18N Capabilities objects which conform with the
     *         requested language/codespace
     */
    @SuppressWarnings({ "unchecked" })
    public List<I18NCapabilities> getI18NCapabilitiesObjects(String codespace, Session session) {
        Criteria criteria = getDefaultCriteria(session);
        criteria.createCriteria(I18NCapabilities.CODESPACE).add(Restrictions.eq(Codespace.CODESPACE, codespace));
        LOGGER.debug("QUERY getI18NCapabilitiesObjects(): {}", HibernateHelper.getSqlString(criteria));
        return (List<I18NCapabilities>) criteria.list();
    }

    /**
     * @param session
     *            Hibernate Session
     * @return Hibernate Criteria
     */
    private Criteria getDefaultCriteria(Session session) {
        return session.createCriteria(I18NCapabilities.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    }

}
