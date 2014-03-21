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
package org.n52.sos.ds.hibernate.admin;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.n52.sos.ds.I18NDAO;
import org.n52.sos.ds.hibernate.HibernateSessionHolder;
import org.n52.sos.ds.hibernate.dao.CodespaceDAO;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.FeatureOfInterestDAO;
import org.n52.sos.ds.hibernate.dao.ObservablePropertyDAO;
import org.n52.sos.ds.hibernate.dao.OfferingDAO;
import org.n52.sos.ds.hibernate.dao.ProcedureDAO;
import org.n52.sos.ds.hibernate.dao.i18n.AbstractFeatureI18NDAO;
import org.n52.sos.ds.hibernate.entities.AbstractIdentifierNameDescriptionEntity;
import org.n52.sos.ds.hibernate.entities.Codespace;
import org.n52.sos.ds.hibernate.entities.i18n.AbstractFeatureI18N;
import org.n52.sos.ds.hibernate.entities.i18n.I18NFeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.i18n.I18NInsertionObject;
import org.n52.sos.ds.hibernate.entities.i18n.I18NObservableProperty;
import org.n52.sos.ds.hibernate.entities.i18n.I18NOffering;
import org.n52.sos.ds.hibernate.entities.i18n.I18NProcedure;
import org.n52.sos.i18n.I18NFeatureObject;
import org.n52.sos.i18n.I18NLanguageObject;
import org.n52.sos.i18n.I18NObject;
import org.n52.sos.i18n.I18NObservablePropertyObject;
import org.n52.sos.i18n.I18NOfferingObject;
import org.n52.sos.i18n.I18NProcedureObject;
import org.n52.sos.i18n.request.GetI18NObjectRequest;
import org.n52.sos.i18n.request.InsertI18NObjectRequest;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.CollectionHelper;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Implementation of {@link I18NDAO} to query and insert I18N data into the
 * database
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 * 
 */
public class HibernateI18NDAO implements I18NDAO {

    private HibernateSessionHolder sessionHolder = new HibernateSessionHolder();

    @Override
    public Collection<I18NObject> getI18NObjects(GetI18NObjectRequest request) throws OwsExceptionReport {
        Session session = null;
        try {
            session = sessionHolder.getSession();
            AbstractFeatureI18NDAO i18ndao = DaoFactory.getInstance().getI18NDAO(request.getType(), session);
            if (request.isSetObjectIdentifier() && request.isSetObjectIdentifier()) {
                return convert(i18ndao.getObject(request.getObjectIdentifier(), request.getLanguage(), session));
            } else if (request.isSetObjectIdentifier()) {
                return convert(i18ndao.getObjectsForIdentifier(request.getObjectIdentifier(), session));
            } else if (request.isSetLanguage()) {
                return convert(i18ndao.getObjectsForCodespaceString(request.getLanguage(), session));
            } else {
                return convert(i18ndao.getObjects(session));
            }
        } finally {
            sessionHolder.returnSession(session);
        }
    }

    @Override
    public void insertI18NObjects(InsertI18NObjectRequest request) throws OwsExceptionReport {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionHolder.getSession();
            transaction = session.beginTransaction();
            AbstractIdentifierNameDescriptionEntity objectEntity =
                    getEntityForObjectId(request.getI18nObject(), session);
            if (objectEntity != null) {
                AbstractFeatureI18NDAO i18ndao =
                        DaoFactory.getInstance().getI18NDAO(request.getI18nObject().getClass(), session);
                for (I18NLanguageObject i18Nvalue : request.getI18nObject().getI18NValues()) {
                    Codespace codespace = getCodeSpace(i18Nvalue.getLanguage(), session);
                    i18ndao.insertI18N(objectEntity,
                            new I18NInsertionObject(codespace, i18Nvalue.getName(), i18Nvalue.getDescription()),
                            session);
                    session.flush();
                }
            }
            transaction.commit();
        } catch (HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw he;
        } finally {
            sessionHolder.returnSession(session);
        }
    }

    /**
     * Converts the SOS internal I18N objects to Hibernate I18N entities
     * 
     * @param objects
     *            SOS internal I18N objects
     * @return converted Hibernate I18N entities
     */
    private Collection<I18NObject> convert(List<AbstractFeatureI18N> objects) {
        Map<String, I18NObject> map = Maps.newHashMap();
        for (AbstractFeatureI18N abstractI18N : objects) {
            Collection<I18NObject> converted = convert(abstractI18N);
            if (CollectionHelper.isEmpty(converted)) {
                I18NObject i18NObject = converted.iterator().next();
                if (map.containsKey(i18NObject.getObjectIdentifier())) {
                    map.get(i18NObject.getObjectIdentifier()).addI18NValue(i18NObject.getI18NValues());
                } else {
                    map.put(i18NObject.getObjectIdentifier(), i18NObject);
                }
            }
        }
        return map.values();
    }

    /**
     * Converts the SOS internal I18N object to Hibernate I18N entities
     * 
     * @param object
     *            SOS internal I18N object
     * @return converted Hibernate I18N entities
     */
    private Collection<I18NObject> convert(AbstractFeatureI18N object) {
        if (object != null) {
            I18NObject i18NObject = null;
            if (object instanceof I18NFeatureOfInterest) {
                i18NObject = new I18NFeatureObject(object.getObjectId().getIdentifier());
            } else if (object instanceof I18NProcedure) {
                i18NObject = new I18NProcedureObject(object.getObjectId().getIdentifier());
            } else if (object instanceof I18NOffering) {
                i18NObject = new I18NOfferingObject(object.getObjectId().getIdentifier());
            } else if (object instanceof I18NObservableProperty) {
                i18NObject = new I18NObservablePropertyObject(object.getObjectId().getIdentifier());
            }
            if (i18NObject != null) {
                i18NObject.addI18NValue(new I18NLanguageObject(object.getCodespace().getCodespace(), object.getName(),
                        object.getDescription()));
                return Sets.newHashSet(i18NObject);
            }
        }
        return Collections.emptySet();
    }

    /**
     * Get and/or insert the {@link Codespace} entity for the specified language
     * 
     * @param language
     *            The specified language to get {@link Codespace} entity for
     * @param session
     *            Hiberante session
     * @return {@link Codespace} entity for the specified language
     */
    private Codespace getCodeSpace(String language, Session session) {
        return new CodespaceDAO().getOrInsertCodespace(language, session);
    }

    /**
     * Get the entity for the specific {@link I18NObject}
     * 
     * @param i18nObject
     *            the specific {@link I18NObject} to get entity for
     * @param session
     *            Hiberante session
     * @return entity for the specific {@link I18NObject}
     */
    private AbstractIdentifierNameDescriptionEntity getEntityForObjectId(I18NObject i18nObject, Session session) {
        if (i18nObject instanceof I18NProcedureObject) {
            return new ProcedureDAO().getProcedureForIdentifier(i18nObject.getObjectIdentifier(), session);
        } else if (i18nObject instanceof I18NFeatureObject) {
            return new FeatureOfInterestDAO().getFeatureOfInterest(i18nObject.getObjectIdentifier(), session);
        } else if (i18nObject instanceof I18NOfferingObject) {
            return new OfferingDAO().getOfferingForIdentifier(i18nObject.getObjectIdentifier(), session);
        } else if (i18nObject instanceof I18NObservablePropertyObject) {
            return new ObservablePropertyDAO().getObservablePropertyForIdentifier(i18nObject.getObjectIdentifier(),
                    session);
        }
        return null;
    }

}
