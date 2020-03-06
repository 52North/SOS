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
import org.n52.sos.ds.hibernate.H2Configuration;
import org.n52.sos.ds.hibernate.HibernateTestCase;
import org.n52.sos.ds.hibernate.dao.ObservablePropertyDAO;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ogc.om.AbstractPhenomenon;
import org.n52.sos.ogc.om.OmCompositePhenomenon;
import org.n52.sos.ogc.om.OmObservableProperty;


public class ObservablePropertyDAOTest extends HibernateTestCase {

    @After
    public void clean() {
        Session session = getSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            ScrollableIterable<ObservableProperty> iter
                    = ScrollableIterable.fromCriteria(session
                            .createCriteria(ObservableProperty.class));
            for (ObservableProperty observableProperty : iter) {
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
        try {
            List<AbstractPhenomenon> abstractPhenomenons
                    = Arrays.asList(compositePhenomenon, simpleObservableProperty);

            check(save(abstractPhenomenons, session));
            check(load(session));

        } finally {
            returnSession(session);
        }
    }

    protected void check(Map<String, ObservableProperty> observableProperties) {
        assertThat(observableProperties, is(notNullValue()));
        errors.checkThat(observableProperties.keySet(), hasSize(5));

        assertThat(observableProperties.get("parent"), is(instanceOf(ObservableProperty.class)));
        assertThat(observableProperties.get("child1"), is(instanceOf(ObservableProperty.class)));
        assertThat(observableProperties.get("child2"), is(instanceOf(ObservableProperty.class)));
        assertThat(observableProperties.get("child3"), is(instanceOf(ObservableProperty.class)));
        assertThat(observableProperties.get("single"), is(instanceOf(ObservableProperty.class)));

        ObservableProperty parent = (ObservableProperty) observableProperties.get("parent");
        ObservableProperty child1 = (ObservableProperty) observableProperties.get("child1");
        ObservableProperty child2 = (ObservableProperty) observableProperties.get("child2");
        ObservableProperty child3 = (ObservableProperty) observableProperties.get("child3");
        ObservableProperty single = (ObservableProperty) observableProperties.get("single");

        errors.checkThat(parent.isHiddenChild(), is(false));
        errors.checkThat(parent.getParents(), is(empty()));
        errors.checkThat(parent.getChilds(), containsInAnyOrder(
                         (ObservableProperty) child1,
                         (ObservableProperty) child2,
                         (ObservableProperty) child3));

        errors.checkThat(child1.isHiddenChild(), is(true));
        errors.checkThat(child1.getParents(), contains((ObservableProperty) parent));
        errors.checkThat(child1.getChilds(), is(empty()));

        errors.checkThat(child2.isHiddenChild(), is(true));
        errors.checkThat(child2.getParents(), contains((ObservableProperty) parent));
        errors.checkThat(child2.getChilds(), is(empty()));

        errors.checkThat(child3.isHiddenChild(), is(true));
        errors.checkThat(child3.getParents(), contains((ObservableProperty) parent));
        errors.checkThat(child3.getChilds(), is(empty()));

        errors.checkThat(single.isHiddenChild(), is(false));
        errors.checkThat(single.getParents(), is(empty()));
        errors.checkThat(single.getChilds(), is(empty()));
    }

    protected Map<String, ObservableProperty> save(List<AbstractPhenomenon> abstractPhenomenons, Session session) {
        ObservablePropertyDAO dao = new ObservablePropertyDAO();
        Collection<ObservableProperty> savedObservableProperties
                = dao.getOrInsertObservableProperty(abstractPhenomenons, false, session);
        return asMap(savedObservableProperties);
    }

    @SuppressWarnings("unchecked")
    protected Map<String, ObservableProperty> load(Session session) {
        return asMap(session.createCriteria(ObservableProperty.class).list());
    }

    protected Map<String, ObservableProperty> asMap(Collection<ObservableProperty> collection) {
        Map<String, ObservableProperty> observableProperties = new HashMap<>(collection.size());
        for (ObservableProperty observableProperty : collection) {
            observableProperties.put(observableProperty.getIdentifier(), observableProperty);
        }
        return observableProperties;
    }
}
