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
package org.n52.sos.ds.hibernate.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.sos.ds.hibernate.dao.series.SeriesObservationDAO;
import org.n52.sos.ds.hibernate.entities.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.ObservationInfo;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.series.Series;
import org.n52.sos.ds.hibernate.entities.series.SeriesObservationInfo;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.exception.CodedException;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * Hibernate data access class for observable properties
 *
 * @author CarstenHollmann
 * @since 4.0.0
 */
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
        Criteria c = null;

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
                Criteria seriesCriteria = c.createCriteria(SeriesObservationInfo.SERIES);
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
        Criteria c = null;
        if (flag) {
            c = getDefaultCriteria(session);
            c.setProjection(Projections.distinct(Projections.property(ObservableProperty.IDENTIFIER)));
            c.add(Subqueries.propertyIn(
                    ObservableProperty.ID,
                    getDetachedCriteriaObservablePropertyForProcedureFromObservationConstellation(procedureIdentifier,
                            session)));
        } else {
            if (HibernateHelper.isEntitySupported(Series.class)) {
                c = getDefaultCriteria(session);
                c.setProjection(Projections.distinct(Projections.property(ObservableProperty.IDENTIFIER)));
                c.add(Subqueries.propertyIn(ObservableProperty.ID,
                        getDetachedCriteriaObservablePropertiesForProcedureFromSeries(procedureIdentifier, session)));
            } else {
                c =
                        session.createCriteria(ObservationInfo.class).add(
                                Restrictions.eq(AbstractObservation.DELETED, false));
                c.createCriteria(ObservationInfo.OBSERVABLE_PROPERTY).setProjection(
                        Projections.distinct(Projections.property(ObservableProperty.IDENTIFIER)));
                c.createCriteria(ObservationInfo.PROCEDURE).add(
                        Restrictions.eq(Procedure.IDENTIFIER, procedureIdentifier));
            }
        }
        LOGGER.debug(
                "QUERY getObservablePropertyIdentifiersForProcedure(observablePropertyIdentifier) using ObservationContellation entitiy ({}): {}",
                flag, HibernateHelper.getSqlString(c));
        return c.list();
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
     * Insert and/or get observable property objects for SOS observable
     * properties
     *
     * @param observableProperty
     *            SOS observable properties
     * @param session
     *            Hibernate session
     * @return Observable property objects
     */
    public List<ObservableProperty> getOrInsertObservableProperty(final List<OmObservableProperty> observableProperty,
            final Session session) {
        final List<String> identifiers = new ArrayList<String>(observableProperty.size());
        for (final OmObservableProperty sosObservableProperty : observableProperty) {
            identifiers.add(sosObservableProperty.getIdentifierCodeWithAuthority().getValue());
        }
        final List<ObservableProperty> obsProps = getObservableProperties(identifiers, session);
        for (final OmObservableProperty sosObsProp : observableProperty) {
            boolean exists = false;
            for (final ObservableProperty obsProp : obsProps) {
                if (obsProp.getIdentifier().equals(sosObsProp.getIdentifierCodeWithAuthority().getValue())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                final ObservableProperty obsProp = new ObservableProperty();
                addIdentifierNameDescription(sosObsProp, obsProp, session);
                session.save(obsProp);
                session.flush();
                session.refresh(obsProp);
                obsProps.add(obsProp);
            }
        }
        return obsProps;
    }

    /**
     * Get Hibernate Detached Criteria to get ObservableProperty entities from
     * ObservationConstellation for procedure identifier
     *
     * @param procedureIdentifier
     *            Procedure identifier parameter
     * @param session
     *            Hibernate session
     * @return Hibernate Detached Criteria
     */
    private DetachedCriteria getDetachedCriteriaObservablePropertyForProcedureFromObservationConstellation(
            String procedureIdentifier, Session session) {
        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(ObservationConstellation.class);
        detachedCriteria.add(Restrictions.eq(ObservationConstellation.DELETED, false));
        detachedCriteria.createCriteria(ObservationConstellation.PROCEDURE).add(
                Restrictions.eq(Procedure.IDENTIFIER, procedureIdentifier));
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

}
