/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.series.db.beans.PhenomenonEntity;
import org.n52.shetland.ogc.om.AbstractPhenomenon;
import org.n52.shetland.ogc.om.OmCompositePhenomenon;
import org.n52.shetland.ogc.om.OmObservableProperty;
import org.n52.sos.ds.hibernate.H2Configuration;
import org.n52.sos.ds.hibernate.HibernateTestCase;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.ObservablePropertyDAO;


public class ObservablePropertyDAOTest extends HibernateTestCase {

    @After
    public void clean() {
        Session session = getSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            ScrollableIterable<PhenomenonEntity> iter
                    = ScrollableIterable.fromCriteria(session
                            .createCriteria(PhenomenonEntity.class));
            for (PhenomenonEntity observableProperty : iter) {
                session.delete(observableProperty);
            }
            session.flush();
            transaction.commit();
        } catch (HibernateException ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw ex;
        } finally {
            returnSession(session);
        }
    }

    @BeforeClass
    public static void cleanStatic() {
        H2Configuration.truncate();
    }

    @Rule
    public final ErrorCollector errors = new ErrorCollector();

    @Test
    public void testCompositePhenomenon() {
        OmCompositePhenomenon compositePhenomenon = new OmCompositePhenomenon("parent");
        compositePhenomenon.addPhenomenonComponent(new OmObservableProperty("child1"));
        compositePhenomenon.addPhenomenonComponent(new OmObservableProperty("child2"));
        compositePhenomenon.addPhenomenonComponent(new OmObservableProperty("child3"));
        OmObservableProperty simpleObservableProperty = new OmObservableProperty("single");

        Session session = getSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            List<AbstractPhenomenon> abstractPhenomenons
                    = Arrays.asList(compositePhenomenon, simpleObservableProperty);

            check(save(abstractPhenomenons, session));
            session.flush();
            transaction.commit();
            check(load(session));
        } catch (HibernateException ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw ex;
        } finally {
            returnSession(session);
        }
    }

    protected void check(Map<String, PhenomenonEntity> observableProperties) {
        assertThat(observableProperties, is(notNullValue()));
        errors.checkThat(observableProperties.keySet(), hasSize(5));

        assertThat(observableProperties.get("parent"), is(instanceOf(PhenomenonEntity.class)));
        assertThat(observableProperties.get("child1"), is(instanceOf(PhenomenonEntity.class)));
        assertThat(observableProperties.get("child2"), is(instanceOf(PhenomenonEntity.class)));
        assertThat(observableProperties.get("child3"), is(instanceOf(PhenomenonEntity.class)));
        assertThat(observableProperties.get("single"), is(instanceOf(PhenomenonEntity.class)));

        PhenomenonEntity parent = observableProperties.get("parent");
        PhenomenonEntity child1 = observableProperties.get("child1");
        PhenomenonEntity child2 = observableProperties.get("child2");
        PhenomenonEntity child3 = observableProperties.get("child3");
        PhenomenonEntity single = observableProperties.get("single");

        errors.checkThat(parent.getParents(), is(empty()));
        errors.checkThat(parent.getChildren(), containsInAnyOrder(child1, child2, child3));

        errors.checkThat(child1.getParents(), contains(parent));
        errors.checkThat(child1.getChildren(), is(empty()));

        errors.checkThat(child2.getParents(), contains(parent));
        errors.checkThat(child2.getChildren(), is(empty()));

        errors.checkThat(child3.getParents(), contains(parent));
        errors.checkThat(child3.getChildren(), is(empty()));

        errors.checkThat(single.getParents(), is(empty()));
        errors.checkThat(single.getChildren(), is(empty()));
    }

    protected Map<String, PhenomenonEntity> save(List<AbstractPhenomenon> abstractPhenomenons, Session session) {
        I18NDAORepository i18NDAORepository = new I18NDAORepository();
        DaoFactory daoFactory = new DaoFactory();
        daoFactory.setI18NDAORepository(i18NDAORepository);
        ObservablePropertyDAO dao = daoFactory.getObservablePropertyDAO();
        Collection<PhenomenonEntity> savedObservableProperties
                = dao.getOrInsertObservableProperty(abstractPhenomenons, session);
        return asMap(savedObservableProperties);
    }

    @SuppressWarnings("unchecked")
    protected Map<String, PhenomenonEntity> load(Session session) {
        return asMap(session.createCriteria(PhenomenonEntity.class).list());
    }

    protected Map<String, PhenomenonEntity> asMap(Collection<PhenomenonEntity> collection) {
        Map<String, PhenomenonEntity> observableProperties = new HashMap<>(collection.size());
        for (PhenomenonEntity observableProperty : collection) {
            observableProperties.put(observableProperty.getIdentifier(), observableProperty);
        }
        return observableProperties;
    }
}
