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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.n52.sos.ds.hibernate.dao.observation.AbstractObservationDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.SeriesObservationDAO;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.observation.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.observation.legacy.ContextualReferencedLegacyObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.ContextualReferencedSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.Series;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.exception.CodedException;
import org.n52.sos.ogc.om.AbstractPhenomenon;
import org.n52.sos.ogc.om.OmCompositePhenomenon;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObservablePropertyDAO extends AbstractIdentifierNameDescriptionDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObservablePropertyDAO.class);

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
    public List<ObservableProperty> getObservableProperties(final List<String> identifiers, final Session session) {
        Criteria criteria =
                session.createCriteria(ObservableProperty.class).add(
                        Restrictions.in(ObservableProperty.IDENTIFIER, identifiers));
        LOGGER.debug("QUERY getObservableProperties(identifiers): {}", HibernateHelper.getSqlString(criteria));
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
        final boolean flag = HibernateHelper.isEntitySupported(ObservationConstellation.class);
        Criteria c;

        if (flag) {
            c = getDefaultCriteria(session);
            c.add(Subqueries.propertyIn(
                    Procedure.ID,
                    getDetachedCriteriaObservablePropertiesForOfferingFromObservationConstellation(offeringIdentifier,
                            session)));
            c.setProjection(Projections.distinct(Projections.property(ObservableProperty.IDENTIFIER)));
        } else {
            AbstractObservationDAO observationDAO = DaoFactory.getInstance().getObservationDAO();
            c = observationDAO.getDefaultObservationInfoCriteria(session);
            if (observationDAO instanceof SeriesObservationDAO) {
                Criteria seriesCriteria = c.createCriteria(ContextualReferencedSeriesObservation.SERIES);
                seriesCriteria.createCriteria(Series.OBSERVABLE_PROPERTY).setProjection(
                        Projections.distinct(Projections.property(ObservableProperty.IDENTIFIER)));

            } else {
                c.createCriteria(AbstractObservation.OBSERVABLE_PROPERTY).setProjection(
                        Projections.distinct(Projections.property(ObservableProperty.IDENTIFIER)));
            }
            new OfferingDAO().addOfferingRestricionForObservation(c, offeringIdentifier);
        }
        LOGGER.debug(
                "QUERY getProcedureIdentifiersForOffering(offeringIdentifier) using ObservationContellation entitiy ({}): {}",
                flag, HibernateHelper.getSqlString(c));
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
        final boolean flag = HibernateHelper.isEntitySupported(ObservationConstellation.class);
        Criteria c;
        if (flag) {
            c = getDefaultCriteria(session);
            c.setProjection(Projections.distinct(Projections.property(ObservableProperty.IDENTIFIER)));
            c.add(Subqueries.propertyIn(
                    ObservableProperty.ID,
                    getDetachedCriteriaObservablePropertyForProcedureFromObservationConstellation(procedureIdentifier)));
        } else {
            if (HibernateHelper.isEntitySupported(Series.class)) {
                c = getDefaultCriteria(session);
                c.setProjection(Projections.distinct(Projections.property(ObservableProperty.IDENTIFIER)));
                c.add(Subqueries.propertyIn(ObservableProperty.ID,
                        getDetachedCriteriaObservablePropertiesForProcedureFromSeries(procedureIdentifier, session)));
            } else {
                c = session.createCriteria(ContextualReferencedLegacyObservation.class)
                        .add(Restrictions.eq(AbstractObservation.DELETED, false));
                c.createCriteria(ContextualReferencedLegacyObservation.OBSERVABLE_PROPERTY)
                        .setProjection(Projections.distinct(Projections.property(ObservableProperty.IDENTIFIER)));
                c.createCriteria(ContextualReferencedLegacyObservation.PROCEDURE).add(
                        Restrictions.eq(Procedure.IDENTIFIER, procedureIdentifier));
            }
        }
        LOGGER.debug(
                "QUERY getObservablePropertyIdentifiersForProcedure(observablePropertyIdentifier) using ObservationContellation entitiy ({}): {}",
                flag, HibernateHelper.getSqlString(c));
        return c.list();
    }


    /**
     * Get map keyed by observable properties with
     * collections of child observable properties (if supported) as values
     * @param session
     * @return Map keyed by observable properties with values of child observable properties collections
     */
    public Map<ObservableProperty,Collection<ObservableProperty>> getObservablePropertyHierarchy(final Session session) {

            List<ObservableProperty> observablePropertyObjects
                    = getObservablePropertyObjects(session);
            Map<ObservableProperty, Collection<ObservableProperty>> map
                    = new HashMap<>(observablePropertyObjects.size());
            for (ObservableProperty op : observablePropertyObjects) {
                map.put(op, op.getChilds());
            }
            return map;
//        } else {
//            List<ObservableProperty> observablePropertyObjects
//                    = getObservablePropertyObjects(session);
//            Map<ObservableProperty, Collection<ObservableProperty>> map
//                    = new HashMap<>(observablePropertyObjects.size());
//            Set<ObservableProperty> empty = Collections.emptySet();
//            for (ObservableProperty op : observablePropertyObjects) {
//                map.put(op, empty);
//            }
//            return map;
//        }
    }

    private Criteria getDefaultCriteria(Session session) {
        return session.createCriteria(ObservableProperty.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
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
    public ObservableProperty getObservablePropertyForIdentifier(final String identifier, final Session session) {
        Criteria criteria = session.createCriteria(ObservableProperty.class)
                .add(Restrictions.eq(ObservableProperty.IDENTIFIER, identifier));
        LOGGER.debug("QUERY getObservablePropertyForIdentifier(identifier): {}",
                HibernateHelper.getSqlString(criteria));
        return (ObservableProperty) criteria.uniqueResult();
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
    public List<ObservableProperty> getObservablePropertiesForIdentifiers(final Collection<String> identifiers,
            final Session session) {
        Criteria criteria =
                session.createCriteria(ObservableProperty.class).add(
                        Restrictions.in(ObservableProperty.IDENTIFIER, identifiers));
        LOGGER.debug("QUERY getObservablePropertiesForIdentifiers(identifiers): {}",
                HibernateHelper.getSqlString(criteria));
        return (List<ObservableProperty>) criteria.list();
    }

    /**
     * Get all observable property objects
     *
     * @param session
     *            Hibernate session
     * @return Observable property objects
     */
    @SuppressWarnings("unchecked")
    public List<ObservableProperty> getObservablePropertyObjects(final Session session) {
        Criteria criteria = session.createCriteria(ObservableProperty.class);
        LOGGER.debug("QUERY getObservablePropertyObjects(): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }
   
    /**
     * Get all transactional observable property objects
     *
     * @param session
     *                Hibernate session
     *
     * @return Observable property objects
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public List<ObservableProperty> getTObservablePropertyObjects(final Session session) {
        Criteria criteria = session.createCriteria(ObservableProperty.class);
        LOGGER.debug("QUERY getTObservablePropertyObjects(): {}", HibernateHelper
                     .getSqlString(criteria));
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
    public List<ObservableProperty> getOrInsertObservableProperty(
            List<? extends AbstractPhenomenon> observableProperties, boolean hiddenChild, Session session) {
        return new ArrayList<>(getOrInsertObservablePropertyAsMap(observableProperties, hiddenChild, session).values());
    }

    public Map<String, ObservableProperty> getOrInsertObservablePropertyAsMap(
            List<? extends AbstractPhenomenon> observableProperties, boolean hiddenChild, Session session) {
        Map<String, ObservableProperty> existing = getExistingObservableProperties(observableProperties, session);
        insertNonExisting(observableProperties, hiddenChild, existing, session);
        insertHierachy(observableProperties, existing, session);
        return existing;
    }

    public ObservableProperty getOrInsertObservableProperty(AbstractPhenomenon observableProperty, Session session) {
        ObservableProperty obsProp =getObservablePropertyForIdentifier(observableProperty.getIdentifier(), session);
        if (obsProp == null) {
            obsProp = new ObservableProperty();
            addIdentifierNameDescription(observableProperty, obsProp, session);
            obsProp.setHiddenChild(false);
            session.save(obsProp);
            session.flush();
            session.refresh(obsProp);
        }
        return obsProp;
    }

    protected void insertNonExisting(
            List<? extends AbstractPhenomenon> observableProperties,
            boolean hiddenChild,
            Map<String, ObservableProperty> existing,
            Session session)
            throws HibernateException {
        for (AbstractPhenomenon sosObsProp : observableProperties) {
            insertNonExisting(sosObsProp, hiddenChild, existing, session);
        }
    }

    protected void insertNonExisting(AbstractPhenomenon sosObsProp,
                                     boolean hiddenChild,
                                     Map<String, ObservableProperty> existing,
                                     Session session)
            throws HibernateException {
        if (!existing.containsKey(sosObsProp.getIdentifier())) {
            ObservableProperty obsProp = new ObservableProperty();
            addIdentifierNameDescription(sosObsProp, obsProp, session);
            obsProp.setHiddenChild(hiddenChild);
            session.save(obsProp);
            session.flush();
            session.refresh(obsProp);
            existing.put(obsProp.getIdentifier(), obsProp);
        }
        if (sosObsProp instanceof OmCompositePhenomenon) {
            insertNonExisting(((OmCompositePhenomenon) sosObsProp).getPhenomenonComponents(), true, existing, session);
        }
    }

    protected Map<String, ObservableProperty> getExistingObservableProperties(
            List<? extends AbstractPhenomenon> observableProperty,
            Session session) {
        List<String> identifiers = getIdentifiers(observableProperty);
        return getObservablePropertiesAsMap(identifiers, session);
    }

    protected List<String> getIdentifiers(List<? extends AbstractPhenomenon> observableProperty) {
        List<String> identifiers = new ArrayList<>(observableProperty.size());
        for (AbstractPhenomenon sosObservableProperty : observableProperty) {
            identifiers.add(sosObservableProperty.getIdentifier());
            if (sosObservableProperty instanceof OmCompositePhenomenon) {
                OmCompositePhenomenon parent
                        = (OmCompositePhenomenon) sosObservableProperty;
                for (OmObservableProperty child : parent.getPhenomenonComponents()) {
                    identifiers.add(child.getIdentifier());
                }
            }
        }
        return identifiers;
    }

    protected void insertHierachy(List<? extends AbstractPhenomenon> observableProperty,
                                  Map<String, ObservableProperty> existing,
                                  Session session) {
        for (AbstractPhenomenon sosObsProp : observableProperty) {
            if (sosObsProp instanceof OmCompositePhenomenon) {
                insertHierachy((OmCompositePhenomenon) sosObsProp, existing, session);
            }
        }
    }

    protected void insertHierachy(OmCompositePhenomenon parent,
                                  Map<String, ObservableProperty> existing,
                                  Session session) throws HibernateException {
        ObservableProperty parentObsProp = getObservableProperty(parent.getIdentifier(), existing, session);
        for (OmObservableProperty child : parent) {
            ObservableProperty childObsProp = getObservableProperty(child.getIdentifier(), existing, session);
            childObsProp.addParent(parentObsProp);
            session.update(childObsProp);
        }
        // do not save the parent, as it would result in a duplicate key error...
        session.flush();
        session.refresh(parentObsProp);
    }

    private ObservableProperty getObservableProperty(String identifier, Map<String, ObservableProperty> observableProperties, Session session) {
        // TODO check if this is still required
        if (identifier == null) {
            return null;
        }
        ObservableProperty observableProperty = observableProperties.get(identifier);
        if (observableProperty != null) {
            return observableProperty;
        }
        observableProperty = getObservablePropertyForIdentifier(identifier, session);
        observableProperties.put(identifier, observableProperty);
        return observableProperty;
    }

    protected ObservableProperty getObservableProperty(ObservableProperty observableProperty, Session session)
            throws HibernateException {
        long id = observableProperty.getObservablePropertyId();
        return (ObservableProperty) session.get(ObservableProperty.class, id);
    }

    protected Map<String, ObservableProperty> getObservablePropertiesAsMap(
            List<String> identifiers, Session session) {
        List<ObservableProperty> obsProps = getObservableProperties(identifiers, session);
        Map<String, ObservableProperty> existing = new HashMap<>(identifiers.size());
        for (ObservableProperty obsProp  : obsProps) {
            existing.put(obsProp.getIdentifier(), obsProp);
        }
        return existing;
    }

    /**
     * Get Hibernate Detached Criteria to get ObservableProperty entities from
     * ObservationConstellation for procedure identifier
     *
     * @param procedureIdentifier
     *            Procedure identifier parameter
     * @return Hibernate Detached Criteria
     */
    private DetachedCriteria getDetachedCriteriaObservablePropertyForProcedureFromObservationConstellation(
            String procedureIdentifier) {
        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(ObservationConstellation.class);
        detachedCriteria.add(Restrictions.eq(ObservationConstellation.DELETED, false));
        detachedCriteria.createCriteria(ObservationConstellation.PROCEDURE)
                .add(Restrictions.eq(Procedure.IDENTIFIER, procedureIdentifier));
        detachedCriteria.setProjection(Projections.distinct(Projections
                .property(ObservationConstellation.OBSERVABLE_PROPERTY)));
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
        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Series.class);
        detachedCriteria.add(Restrictions.eq(Series.DELETED, false));
        detachedCriteria.createCriteria(Series.PROCEDURE).add(
                Restrictions.eq(Procedure.IDENTIFIER, procedureIdentifier));
        detachedCriteria.setProjection(Projections.distinct(Projections.property(Series.OBSERVABLE_PROPERTY)));
        return detachedCriteria;
    }

    /**
     * Get Hibernate Detached Criteria to get ObservableProperty entities from
     * ObservationConstellation for offering identifier
     *
     * @param offeringIdentifier
     *            Offering identifier parameter
     * @param session
     *            Hibernate session
     * @return Hibernate Detached Criteria
     */
    private DetachedCriteria getDetachedCriteriaObservablePropertiesForOfferingFromObservationConstellation(
            String offeringIdentifier, Session session) {
        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(ObservationConstellation.class);
        detachedCriteria.add(Restrictions.eq(ObservationConstellation.DELETED, false));
        detachedCriteria.createCriteria(ObservationConstellation.OFFERING).add(
                Restrictions.eq(Offering.IDENTIFIER, offeringIdentifier));
        detachedCriteria.setProjection(Projections.distinct(Projections
                .property(ObservationConstellation.OBSERVABLE_PROPERTY)));
        return detachedCriteria;
    }

    @SuppressWarnings("unchecked")
    public List<ObservableProperty> getPublishedObservableProperty(Session session) throws CodedException {
        if (HibernateHelper.isEntitySupported(Series.class)) {
            Criteria c = getDefaultCriteria(session);
            c.add(Subqueries.propertyNotIn(ObservableProperty.ID, getDetachedCriteriaSeries(session)));
            return c.list();
        } 
        return getObservablePropertyObjects(session);
     }
     
     private DetachedCriteria getDetachedCriteriaSeries(Session session) throws CodedException {
         final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(DaoFactory.getInstance().getSeriesDAO().getSeriesClass());
         detachedCriteria.add(Restrictions.disjunction(Restrictions.eq(Series.DELETED, true), Restrictions.eq(Series.PUBLISHED, false)));
         detachedCriteria.setProjection(Projections.distinct(Projections.property(Series.OBSERVABLE_PROPERTY)));
         return detachedCriteria;
     }

}
