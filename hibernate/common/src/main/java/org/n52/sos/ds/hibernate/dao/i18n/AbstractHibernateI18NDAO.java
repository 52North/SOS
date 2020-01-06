/*
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
package org.n52.sos.ds.hibernate.dao.i18n;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.iceland.i18n.I18NDAO;
import org.n52.iceland.i18n.metadata.AbstractI18NMetadata;
import org.n52.janmayen.i18n.LocaleHelper;
import org.n52.janmayen.i18n.LocalizedString;
import org.n52.series.db.beans.DescribableEntity;
import org.n52.series.db.beans.i18n.I18nEntity;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.ds.hibernate.HibernateSessionHolder;
import org.n52.sos.ds.hibernate.util.HibernateHelper;

import com.google.common.collect.Maps;

public abstract class AbstractHibernateI18NDAO<T extends DescribableEntity,
                                               S extends AbstractI18NMetadata,
                                               H extends I18nEntity>
        implements I18NDAO<S>, HibernateI18NDAO<S> {

    private HibernateSessionHolder sessionHolder;

    @Inject
    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.sessionHolder = new HibernateSessionHolder(connectionProvider);
    }

    @Override
    public S getMetadata(String id)
            throws OwsExceptionReport {
        Session session = null;
        try {
            session = sessionHolder.getSession();
            return getMetadata(id, session);
        } finally {
            sessionHolder.returnSession(session);
        }
    }

    @Override
    public Collection<S> getMetadata(Collection<String> id)
            throws OwsExceptionReport {
        Session session = null;
        try {
            session = sessionHolder.getSession();
            return getMetadata(id, session);
        } finally {
            sessionHolder.returnSession(session);
        }
    }

    @Override
    public S getMetadata(String id, Locale locale)
            throws OwsExceptionReport {
        Session session = null;
        try {
            session = sessionHolder.getSession();
            return getMetadata(id, locale, session);
        } finally {
            sessionHolder.returnSession(session);
        }
    }

    @Override
    public S getMetadata(String id, String locale) throws OwsExceptionReport {
        Session session = null;
        try {
            session = sessionHolder.getSession();
            return getMetadata(id, locale, session);
        } finally {
            sessionHolder.returnSession(session);
        }
    }

    @Override
    public Collection<S> getMetadata(Collection<String> id, Locale locale)
            throws OwsExceptionReport {
        Session session = null;
        try {
            session = sessionHolder.getSession();
            return getMetadata(id, locale, session);
        } finally {
            sessionHolder.returnSession(session);
        }
    }

    @Override
    public Collection<S> getMetadata(Collection<String> id, String locale)
            throws OwsExceptionReport {
        Session session = null;
        try {
            session = sessionHolder.getSession();
            return getMetadata(id, locale, session);
        } finally {
            sessionHolder.returnSession(session);
        }
    }

    @Override
    public Collection<S> getMetadata()
            throws OwsExceptionReport {
        Session session = null;
        try {
            session = sessionHolder.getSession();
            return getMetadata(session);
        } finally {
            sessionHolder.returnSession(session);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public S getMetadata(String id, Session session)
            throws OwsExceptionReport {
        Criteria criteria = session.createCriteria(getHibernateEntityClass());
        criteria.createCriteria(I18nEntity.PROPERTY_ENTITY)
                .add(Restrictions.eq(DescribableEntity.IDENTIFIER, id));
        List<H> list = criteria.list();
        return createSosObject(id, list);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<S> getMetadata(Collection<String> id, Session session)
            throws OwsExceptionReport {
        Criteria criteria = session.createCriteria(getHibernateEntityClass());
        criteria.createCriteria(I18nEntity.PROPERTY_ENTITY)
                .add(Restrictions.in(DescribableEntity.IDENTIFIER, id));
        List<H> list = criteria.list();
        return createSosObject(list);
    }

    @Override
    public S getMetadata(String id, Locale locale, Session session)
            throws OwsExceptionReport {
        return getMetadata(id, locale.toString(), session);
    }

    private S getMetadata(String id, String locale, Session session)
            throws OwsExceptionReport {
        Criteria criteria = session.createCriteria(getHibernateEntityClass());
        criteria.createCriteria(I18nEntity.PROPERTY_ENTITY)
                .add(Restrictions.eq(DescribableEntity.IDENTIFIER, id));
        criteria.add(Restrictions.eq(I18nEntity.PROPERTY_LOCALE, locale));
        List<H> list = criteria.list();
        if (list.isEmpty()) {
            return getMetadata(id, LocaleHelper.getEquivalents(locale), session);
        }
        return createSosObject(id, list);
    }

    private S getMetadata(String id, Set<String> locales, Session session)
            throws OwsExceptionReport {
        Criteria criteria = session.createCriteria(getHibernateEntityClass());
        criteria.createCriteria(I18nEntity.PROPERTY_ENTITY)
                .add(Restrictions.eq(DescribableEntity.IDENTIFIER, id));
        criteria.add(Restrictions.in(I18nEntity.PROPERTY_LOCALE, locales));
        List<H> list = criteria.list();
        return createSosObject(id, list);
    }

    @Override
    public Collection<S> getMetadata(Collection<String> id, Locale locale, Session session)
            throws OwsExceptionReport {
        return getMetadata(id, locale.toString(), session);
    }

    private Collection<S> getMetadata(Collection<String> id, String locale, Session session)
            throws OwsExceptionReport {
        Criteria criteria = session.createCriteria(getHibernateEntityClass());
        criteria.createCriteria(I18nEntity.PROPERTY_ENTITY)
                .add(Restrictions.in(DescribableEntity.IDENTIFIER, id));
        criteria.add(Restrictions.eq(I18nEntity.PROPERTY_LOCALE, locale));
        List<H> list = criteria.list();
        if (list.isEmpty()) {
            return getMetadata(id, LocaleHelper.getEquivalents(locale), session);
        }
        return createSosObject(list);
    }

    private Collection<S> getMetadata(Collection<String> id, Set<String> locales, Session session)
            throws OwsExceptionReport {
        Criteria criteria = session.createCriteria(getHibernateEntityClass());
        criteria.createCriteria(I18nEntity.PROPERTY_ENTITY)
                .add(Restrictions.in(DescribableEntity.IDENTIFIER, id));
        criteria.add(Restrictions.in(I18nEntity.PROPERTY_LOCALE, locales));
        List<H> list = criteria.list();
        return createSosObject(list);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<S> getMetadata(Session session)
            throws OwsExceptionReport {
        Criteria criteria = session.createCriteria(getHibernateEntityClass());
        List<H> list = criteria.list();
        return createSosObject(list);
    }

    @Override
    public void saveMetadata(S i18n, Session session)
            throws OwsExceptionReport {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            deleteOldValues(i18n.getIdentifier(), session);
            T entity = getEntity(i18n.getIdentifier(), session);
            for (Locale locale : i18n.getLocales()) {
                H h18n = createHibernateObject();
                h18n.setEntity(entity);
                h18n.setLocale(locale.toString());
                fillHibernateObject(i18n, h18n);
                session.save(h18n);
            }
            session.flush();
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public void saveMetadata(S i18n)
            throws OwsExceptionReport {
        Session session = null;
        try {
            session = sessionHolder.getSession();
            saveMetadata(i18n, session);
        } finally {
            sessionHolder.returnSession(session);
        }
    }

    @Override
    public Collection<Locale> getAvailableLocales()
            throws OwsExceptionReport {
        Session session = null;
        try {
            session = sessionHolder.getSession();
            return getAvailableLocales(session).stream().map(l -> LocaleHelper.decode(l)).collect(Collectors.toSet());
        } finally {
            sessionHolder.returnSession(session);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<String> getAvailableLocales(Session session)
            throws OwsExceptionReport {
        Criteria criteria = session.createCriteria(getHibernateEntityClass());
        criteria.setProjection(Projections.distinct(Projections.property(I18nEntity.PROPERTY_LOCALE)));
        return criteria.list();
    }

    @Override
    public boolean isSupported() {
        return HibernateHelper.isEntitySupported(getHibernateEntityClass());
    }

    protected abstract S createSosObject(String id);

    protected Collection<S> createSosObject(List<H> hi18ns) {
        Map<String, S> map = Maps.newHashMap();
        for (H h18n : hi18ns) {
            String id = h18n.getId().toString();
            if (h18n.getEntity() instanceof DescribableEntity) {
                id = ((DescribableEntity) h18n.getEntity()).getIdentifier();
            }
            S i18n = map.get(id);
            if (i18n == null) {
                i18n = createSosObject(id);
                map.put(id, i18n);
            }
            fillSosObject(h18n, i18n);
        }

        return map.values();
    }

    protected S createSosObject(String id, List<H> h18ns) {
        S i18n = createSosObject(id);
        h18ns.forEach(h18n -> fillSosObject(h18n, i18n));
        return i18n;
    }

    protected void deleteOldValues(String id, Session session) {
        Criteria criteria = session.createCriteria(getHibernateEntityClass());
        criteria.createCriteria(I18nEntity.PROPERTY_ENTITY)
                .add(Restrictions.eq(DescribableEntity.IDENTIFIER, id));
        ScrollableResults scroll = null;
        try {
            scroll = criteria.scroll();
            while (scroll.next()) {
                @SuppressWarnings("unchecked")
                H h18n = (H) scroll.get()[0];
                session.delete(h18n);
            }
        } finally {
            if (scroll != null) {
                scroll.close();
            }
        }
        session.flush();
    }

    protected void fillSosObject(H h18n, S i18n) {
        if (h18n.hasName()) {
            i18n.getName().addLocalization(h18n.getLocale(),
                                           h18n.getName());
        }
        if (h18n.hasDescription()) {
            i18n.getDescription()
                    .addLocalization(h18n.getLocale(),
                                     h18n.getDescription());
        }
    }

    protected void fillHibernateObject(S i18n, H h18n) {
        Optional<LocalizedString> name = i18n.getName()
                .getLocalization(LocaleHelper.decode(h18n.getLocale()));
        if (name.isPresent()) {
            h18n.setName(name.get().getText());
        }
        Optional<LocalizedString> description = i18n.getDescription()
                .getLocalization(LocaleHelper.decode(h18n.getLocale()));
        if (description.isPresent()) {
            h18n.setDescription(description.get().getText());
        }
    }

    protected abstract T getEntity(String id, Session session);

    protected abstract Class<H> getHibernateEntityClass();

    protected abstract H createHibernateObject();
}
