/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.n52.series.db.beans.Describable;
import org.n52.series.db.beans.PhenomenonEntity;
import org.n52.series.db.beans.i18n.I18nEntity;
import org.n52.series.db.beans.i18n.I18nPhenomenonEntity;
import org.n52.shetland.ogc.om.AbstractPhenomenon;
import org.n52.shetland.ogc.om.OmCompositePhenomenon;
import org.n52.shetland.ogc.om.OmObservableProperty;

public class ObservablePropertyDAO extends AbstractIdentifierNameDescriptionDAO {

    public ObservablePropertyDAO(DaoFactory daoFactory) {
        super(daoFactory);
    }

    /**
     * Get observable property objects for observable property identifiers
     *
     * @param identifiers
     *            Observable property identifiers
     * @param session
     *            Hibernate session
     * @return Observable property objects
     */
    public List<PhenomenonEntity> getObservableProperties(final List<String> identifiers, final Session session) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<PhenomenonEntity> query = cb.createQuery(PhenomenonEntity.class);
        Root<PhenomenonEntity> root = query.from(PhenomenonEntity.class);
        query.where(root.get(PhenomenonEntity.IDENTIFIER).in(identifiers));
        return session.createQuery(query).list();
    }

    /**
     * Get observable property by identifier
     *
     * @param identifier
     *            The observable property's identifier
     * @param session
     *            Hibernate session
     * @return Observable property object
     */
    public PhenomenonEntity getObservablePropertyForIdentifier(final String identifier, final Session session) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<PhenomenonEntity> query = cb.createQuery(PhenomenonEntity.class);
        Root<PhenomenonEntity> root = query.from(PhenomenonEntity.class);
        query.where(cb.equal(root.get(PhenomenonEntity.IDENTIFIER), identifier));
        return session.createQuery(query).uniqueResult();
    }

    /**
     * Insert and/or get observable property objects for SOS observable
     * properties
     *
     * @param observableProperties
     *            SOS observable properties
     * @param session
     *            Hibernate session
     * @return Observable property objects
     */
    public List<PhenomenonEntity> getOrInsertObservableProperty(
            List<? extends AbstractPhenomenon> observableProperties, Session session) {
        return new ArrayList<>(getOrInsertObservablePropertyAsMap(observableProperties, session).values());
    }

    public PhenomenonEntity getOrInsertObservableProperty(AbstractPhenomenon observableProperty, Session session) {
        PhenomenonEntity obsProp = getObservablePropertyForIdentifier(observableProperty.getIdentifier(), session);
        if (obsProp == null) {
            obsProp = new PhenomenonEntity();
            addIdentifierNameDescription(observableProperty, obsProp, session);
            session.save(obsProp);
            session.flush();
            session.refresh(obsProp);
        }
        return obsProp;
    }

    public PhenomenonEntity getOrInsertObservableProperty(PhenomenonEntity phenomenon, Session session) {
        PhenomenonEntity result = getObservablePropertyForIdentifier(phenomenon.getIdentifier(), session);
        if (result == null) {
            result = phenomenon;
            session.save(result);
            session.flush();
            session.refresh(result);
            if (phenomenon.hasTranslations()) {
                insertTranslations(result, phenomenon.getTranslations(), session);
            }
        }
        return result;
    }

    public Map<String, PhenomenonEntity> getOrInsertObservablePropertyAsMap(
            List<? extends AbstractPhenomenon> observableProperties, Session session) {
        Map<String, PhenomenonEntity> existing = getExistingObservableProperties(observableProperties, session);
        insertNonExisting(observableProperties, existing, session);
        insertHierachy(observableProperties, existing, session);
        return existing;
    }

    private void insertTranslations(PhenomenonEntity result, Set<I18nEntity<? extends Describable>> translations,
            Session session) {
        for (I18nEntity<? extends Describable> i18nEntity : translations) {
            ((I18nPhenomenonEntity) i18nEntity).setEntity(result);
            session.save(i18nEntity);
            session.flush();
            session.refresh(i18nEntity);
        }
    }

    protected void insertNonExisting(List<? extends AbstractPhenomenon> observableProperties,
            Map<String, PhenomenonEntity> existing, Session session) throws HibernateException {
        for (AbstractPhenomenon sosObsProp : observableProperties) {
            insertNonExisting(sosObsProp, existing, session);
        }
    }

    protected void insertNonExisting(AbstractPhenomenon sosObsProp, Map<String, PhenomenonEntity> existing,
            Session session) throws HibernateException {
        if (!existing.containsKey(sosObsProp.getIdentifier())) {
            PhenomenonEntity obsProp = new PhenomenonEntity();
            addIdentifierNameDescription(sosObsProp, obsProp, session);
            session.save(obsProp);
            session.flush();
            session.refresh(obsProp);
            existing.put(obsProp.getIdentifier(), obsProp);
        }
        if (sosObsProp instanceof OmCompositePhenomenon phenomenon) {
            insertNonExisting(phenomenon.getPhenomenonComponents(), existing, session);
        }
    }

    protected Map<String, PhenomenonEntity> getExistingObservableProperties(
            List<? extends AbstractPhenomenon> observableProperty, Session session) {
        List<String> identifiers = getIdentifiers(observableProperty);
        return getObservablePropertiesAsMap(identifiers, session);
    }

    protected List<String> getIdentifiers(List<? extends AbstractPhenomenon> observableProperty) {
        List<String> identifiers = new ArrayList<>(observableProperty.size());
        for (AbstractPhenomenon sosObservableProperty : observableProperty) {
            identifiers.add(sosObservableProperty.getIdentifier());
            if (sosObservableProperty instanceof OmCompositePhenomenon parent) {
                for (OmObservableProperty child : parent.getPhenomenonComponents()) {
                    identifiers.add(child.getIdentifier());
                }
            }
        }
        return identifiers;
    }

    protected void insertHierachy(List<? extends AbstractPhenomenon> observableProperty,
            Map<String, PhenomenonEntity> existing, Session session) {
        for (AbstractPhenomenon sosObsProp : observableProperty) {
            if (sosObsProp instanceof OmCompositePhenomenon phenomenon) {
                insertHierachy(phenomenon, existing, session);
            }
        }
    }

    protected void insertHierachy(OmCompositePhenomenon parent, Map<String, PhenomenonEntity> existing,
            Session session) throws HibernateException {
        PhenomenonEntity parentObsProp = getObservableProperty(parent.getIdentifier(), existing, session);
        for (OmObservableProperty child : parent) {
            PhenomenonEntity childObsProp = getObservableProperty(child.getIdentifier(), existing, session);
            childObsProp.addParent(parentObsProp);
            session.update(childObsProp);
        }
        // do not save the parent, as it would result in a duplicate key
        // error...
        session.flush();
        session.refresh(parentObsProp);
    }

    private PhenomenonEntity getObservableProperty(String identifier,
            Map<String, PhenomenonEntity> observableProperties, Session session) {
        // TODO check if this is still required
        if (identifier == null) {
            return null;
        }
        PhenomenonEntity observableProperty = observableProperties.get(identifier);
        if (observableProperty != null) {
            return observableProperty;
        }
        observableProperty = getObservablePropertyForIdentifier(identifier, session);
        observableProperties.put(identifier, observableProperty);
        return observableProperty;
    }

    protected PhenomenonEntity getObservableProperty(PhenomenonEntity observableProperty, Session session)
            throws HibernateException {
        long id = observableProperty.getId();
        return (PhenomenonEntity) session.get(PhenomenonEntity.class, id);
    }

    protected Map<String, PhenomenonEntity> getObservablePropertiesAsMap(List<String> identifiers, Session session) {
        List<PhenomenonEntity> obsProps = getObservableProperties(identifiers, session);
        Map<String, PhenomenonEntity> existing = new HashMap<>(identifiers.size());
        for (PhenomenonEntity obsProp : obsProps) {
            existing.put(obsProp.getIdentifier(), obsProp);
        }
        return existing;
    }

}