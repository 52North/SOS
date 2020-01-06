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
package org.n52.sos.ds.hibernate.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.Describable;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.series.db.beans.PhenomenonEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.beans.i18n.I18nEntity;
import org.n52.series.db.beans.i18n.I18nPhenomenonEntity;
import org.n52.shetland.ogc.om.AbstractPhenomenon;
import org.n52.shetland.ogc.om.OmCompositePhenomenon;
import org.n52.shetland.ogc.om.OmObservableProperty;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObservablePropertyDAO extends AbstractIdentifierNameDescriptionDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObservablePropertyDAO.class);

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
    @SuppressWarnings("unchecked")
    public List<PhenomenonEntity> getObservableProperties(final List<String> identifiers, final Session session) {
        Criteria criteria = session.createCriteria(PhenomenonEntity.class)
                .add(Restrictions.in(PhenomenonEntity.IDENTIFIER, identifiers));
        LOGGER.trace("QUERY getObservableProperties(identifiers): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    /**
     * Get observable property identifiers for offering identifier
     *
     * @param offeringIdentifier
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @return Observable property identifiers
     * @throws CodedException
     *             If an error occurs
     */
    @SuppressWarnings("unchecked")
    public List<String> getObservablePropertyIdentifiersForOffering(final String offeringIdentifier,
            final Session session) throws OwsExceptionReport {
        Criteria c = getDefaultCriteria(session);
        c.add(Subqueries.propertyIn(ProcedureEntity.PROPERTY_ID,
                getDetachedCriteriaObservablePropertiesForOfferingFromDatasetEntity(offeringIdentifier, session)));
        c.setProjection(Projections.distinct(Projections.property(PhenomenonEntity.IDENTIFIER)));
        LOGGER.trace("QUERY getProcedureIdentifiersForOffering(offeringIdentifier): {}",
                HibernateHelper.getSqlString(c));
        return c.list();
    }

    /**
     * Get observable property identifiers for procedure identifier
     *
     * @param procedureIdentifier
     *            Procedure identifier
     * @param session
     *            Hibernate session
     * @return Observable property identifiers
     */
    @SuppressWarnings("unchecked")
    public List<String> getObservablePropertyIdentifiersForProcedure(final String procedureIdentifier,
            final Session session) {
        Criteria c = getDefaultCriteria(session);
        c.setProjection(Projections.distinct(Projections.property(PhenomenonEntity.IDENTIFIER)));
        c.add(Subqueries.propertyIn(PhenomenonEntity.PROPERTY_ID,
                getDetachedCriteriaObservablePropertyForProcedureFromDatasetEntity(procedureIdentifier)));
        LOGGER.trace("QUERY getObservablePropertyIdentifiersForProcedure(observablePropertyIdentifier): {}",
                HibernateHelper.getSqlString(c));
        return c.list();
    }

    /**
     * Get map keyed by observable properties with collections of child
     * observable properties (if supported) as values
     *
     * @param session
     *            the session
     * @return Map keyed by observable properties with values of child
     *         observable properties collections
     */
    public Map<PhenomenonEntity, Collection<PhenomenonEntity>> getObservablePropertyHierarchy(final Session session) {

        List<PhenomenonEntity> observablePropertyObjects = getObservablePropertyObjects(session);
        Map<PhenomenonEntity, Collection<PhenomenonEntity>> map = new HashMap<>(observablePropertyObjects.size());
        for (PhenomenonEntity op : observablePropertyObjects) {
            map.put(op, op.getChildren());
        }
        return map;
        // } else {
        // List<PhenomenonEntity> observablePropertyObjects
        // = getObservablePropertyObjects(session);
        // Map<ObservableProperty, Collection<PhenomenonEntity>> map
        // = new HashMap<>(observablePropertyObjects.size());
        // Set<PhenomenonEntity> empty = Collections.emptySet();
        // for (PhenomenonEntity op : observablePropertyObjects) {
        // map.put(op, empty);
        // }
        // return map;
        // }
    }

    private Criteria getDefaultCriteria(Session session) {
        return session.createCriteria(PhenomenonEntity.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
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
        Criteria criteria = session.createCriteria(PhenomenonEntity.class)
                .add(Restrictions.eq(PhenomenonEntity.IDENTIFIER, identifier));
        LOGGER.trace("QUERY getObservablePropertyForIdentifier(identifier): {}",
                HibernateHelper.getSqlString(criteria));
        return (PhenomenonEntity) criteria.uniqueResult();
    }

    /**
     * Get observable properties by identifiers
     *
     * @param identifiers
     *            The observable property identifiers
     * @param session
     *            Hibernate session
     * @return Observable property objects
     */
    @SuppressWarnings("unchecked")
    public List<PhenomenonEntity> getObservablePropertiesForIdentifiers(final Collection<String> identifiers,
            final Session session) {
        Criteria criteria = session.createCriteria(PhenomenonEntity.class)
                .add(Restrictions.in(PhenomenonEntity.IDENTIFIER, identifiers));
        LOGGER.trace("QUERY getObservablePropertiesForIdentifiers(identifiers): {}",
                HibernateHelper.getSqlString(criteria));
        return (List<PhenomenonEntity>) criteria.list();
    }

    /**
     * Get all observable property objects
     *
     * @param session
     *            Hibernate session
     * @return Observable property objects
     */
    @SuppressWarnings("unchecked")
    public List<PhenomenonEntity> getObservablePropertyObjects(final Session session) {
        Criteria criteria = session.createCriteria(PhenomenonEntity.class);
        LOGGER.trace("QUERY getObservablePropertyObjects(): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
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
        if (sosObsProp instanceof OmCompositePhenomenon) {
            insertNonExisting(((OmCompositePhenomenon) sosObsProp).getPhenomenonComponents(), existing, session);
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
            if (sosObservableProperty instanceof OmCompositePhenomenon) {
                OmCompositePhenomenon parent = (OmCompositePhenomenon) sosObservableProperty;
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
            if (sosObsProp instanceof OmCompositePhenomenon) {
                insertHierachy((OmCompositePhenomenon) sosObsProp, existing, session);
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

    /**
     * Get Hibernate Detached Criteria to get ObservableProperty entities from
     * DatasetEntity for procedure identifier
     *
     * @param procedureIdentifier
     *            Procedure identifier parameter
     * @return Hibernate Detached Criteria
     */
    private DetachedCriteria getDetachedCriteriaObservablePropertyForProcedureFromDatasetEntity(
            String procedureIdentifier) {
        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(DatasetEntity.class);
        detachedCriteria.add(Restrictions.eq(DatasetEntity.PROPERTY_DELETED, false));
        detachedCriteria.createCriteria(DatasetEntity.PROPERTY_PROCEDURE)
                .add(Restrictions.eq(ProcedureEntity.IDENTIFIER, procedureIdentifier));
        detachedCriteria.setProjection(Projections.distinct(Projections.property(DatasetEntity.PROPERTY_PHENOMENON)));
        return detachedCriteria;
    }

    /**
     * Get Hibernate Detached Criteria to get ObservableProperty entities from
     * Series for procedure identifier
     *
     * @param procedureIdentifier
     *            Procedure identifier parameter
     * @param session
     *            Hibernate session
     * @return Hibernate Detached Criteria
     */
    private DetachedCriteria getDetachedCriteriaObservablePropertiesForProcedureFromSeries(String procedureIdentifier,
            Session session) {
        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(DatasetEntity.class);
        detachedCriteria.add(Restrictions.eq(DatasetEntity.PROPERTY_DELETED, false));
        detachedCriteria.createCriteria(DatasetEntity.PROPERTY_PROCEDURE)
                .add(Restrictions.eq(ProcedureEntity.IDENTIFIER, procedureIdentifier));
        detachedCriteria.setProjection(Projections.distinct(Projections.property(DatasetEntity.PROPERTY_PHENOMENON)));
        return detachedCriteria;
    }

    /**
     * Get Hibernate Detached Criteria to get ObservableProperty entities from
     * DatasetEntity for offering identifier
     *
     * @param offeringIdentifier
     *            Offering identifier parameter
     * @param session
     *            Hibernate session
     * @return Hibernate Detached Criteria
     */
    private DetachedCriteria getDetachedCriteriaObservablePropertiesForOfferingFromDatasetEntity(
            String offeringIdentifier, Session session) {
        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(DatasetEntity.class);
        detachedCriteria.add(Restrictions.eq(DatasetEntity.PROPERTY_DELETED, false));
        detachedCriteria.createCriteria(DatasetEntity.PROPERTY_OFFERING)
                .add(Restrictions.eq(OfferingEntity.IDENTIFIER, offeringIdentifier));
        detachedCriteria.setProjection(Projections.distinct(Projections.property(DatasetEntity.PROPERTY_PHENOMENON)));
        return detachedCriteria;
    }

    @SuppressWarnings("unchecked")
    public List<PhenomenonEntity> getPublishedObservableProperty(Session session) throws OwsExceptionReport {
        if (HibernateHelper.isEntitySupported(DatasetEntity.class)) {
            Criteria c = getDefaultCriteria(session);
            c.add(Subqueries.propertyNotIn(PhenomenonEntity.PROPERTY_ID, getDetachedCriteriaSeries(session)));
            return c.list();
        }
        return getObservablePropertyObjects(session);
    }

    private DetachedCriteria getDetachedCriteriaSeries(Session session) throws OwsExceptionReport {
        final DetachedCriteria detachedCriteria =
                DetachedCriteria.forClass(getDaoFactory().getSeriesDAO().getSeriesClass());
        detachedCriteria.add(Restrictions.disjunction(Restrictions.eq(DatasetEntity.PROPERTY_DELETED, true),
                Restrictions.eq(DatasetEntity.PROPERTY_PUBLISHED, false)));
        detachedCriteria.setProjection(Projections.distinct(Projections.property(DatasetEntity.PROPERTY_PHENOMENON)));
        return detachedCriteria;
    }

}
