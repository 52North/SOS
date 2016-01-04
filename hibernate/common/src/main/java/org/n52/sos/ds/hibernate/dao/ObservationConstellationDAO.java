/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.ObservationType;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.ObservationConstellationInfo;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.ogc.om.AbstractPhenomenon;
import org.n52.sos.ogc.om.OmCompositePhenomenon;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.CollectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


public class ObservationConstellationDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObservationConstellationDAO.class);

    /**
     * Get observation constellation objects for procedure and observable
     * property object and offering identifiers
     *
     * @param procedure
     *            Procedure object
     * @param observableProperty
     *            Observable property object
     * @param offerings
     *            Offering identifiers
     * @param session
     *            Hibernate session
     * @return Observation constellation objects
     */
    @SuppressWarnings("unchecked")
    public List<ObservationConstellation> getObservationConstellation(Procedure procedure,
            ObservableProperty observableProperty, Collection<String> offerings, Session session) {
        Criteria criteria =
                session.createCriteria(ObservationConstellation.class)
                        .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                        .add(Restrictions.eq(ObservationConstellation.PROCEDURE, procedure))
                        .add(Restrictions.eq(ObservationConstellation.OBSERVABLE_PROPERTY, observableProperty))
                        .createAlias(ObservationConstellation.OFFERING, "o")
                        .add(Restrictions.in("o." + Offering.IDENTIFIER, offerings))
                        .add(Restrictions.eq(ObservationConstellation.DELETED, false));
        LOGGER.debug("QUERY getObservationConstellation(procedure, observableProperty, offerings): {}",
                HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    /**
     * Get ObservationConstellations for procedure, observableProperty and
     * offerings
     *
     * @param procedure
     *            Procedure to get ObservaitonConstellation for
     * @param observableProperty
     *            observableProperty to get ObservaitonConstellation for
     * @param offerings
     *            Offerings to get ObservaitonConstellation for
     * @param session
     *            Hibernate session
     * @return ObservationConstellations
     */
    @SuppressWarnings("unchecked")
    public List<ObservationConstellation> getObservationConstellationsForOfferings(Procedure procedure,
            ObservableProperty observableProperty, Collection<Offering> offerings, Session session) {
        return session.createCriteria(ObservationConstellation.class)
                .add(Restrictions.eq(ObservationConstellation.DELETED, false))
                .add(Restrictions.eq(ObservationConstellation.PROCEDURE, procedure))
                .add(Restrictions.in(ObservationConstellation.OFFERING, offerings))
                .add(Restrictions.eq(ObservationConstellation.OBSERVABLE_PROPERTY, observableProperty)).list();
    }

    /**
     * Get first ObservationConstellation for procedure, observableProperty and
     * offerings
     *
     * @param p
     *            Procedure to get ObservaitonConstellation for
     * @param op
     *            ObservedProperty to get ObservaitonConstellation for
     * @param o
     *            Offerings to get ObservaitonConstellation for
     * @param session
     *            Hibernate session
     * @return First ObservationConstellation
     */
    public ObservationConstellation getFirstObservationConstellationForOfferings(Procedure p, ObservableProperty op,
            Collection<Offering> o, Session session) {
        final List<ObservationConstellation> oc = getObservationConstellationsForOfferings(p, op, o, session);
        return oc.isEmpty() ? null : oc.get(0);
    }

    /**
     * Get ObservationConstellations for procedure and observableProperty
     *
     * @param procedure
     *            Procedure to get ObservaitonConstellation for
     * @param observableProperty
     *            observableProperty to get ObservaitonConstellation for
     * @param session
     *            Hibernate session
     * @return ObservationConstellations
     */
    @SuppressWarnings("unchecked")
    public List<ObservationConstellation> getObservationConstellations(String procedure, String observableProperty,
            Session session) {
        Criteria criteria =
                session.createCriteria(ObservationConstellation.class)
                        .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                        .add(Restrictions.eq(ObservationConstellation.DELETED, false));
        criteria.createCriteria(ObservationConstellation.PROCEDURE).add(
                Restrictions.eq(Procedure.IDENTIFIER, procedure));
        criteria.createCriteria(ObservationConstellation.OBSERVABLE_PROPERTY).add(
                Restrictions.eq(ObservableProperty.IDENTIFIER, observableProperty));
        LOGGER.debug("QUERY getObservationConstellation(procedure, observableProperty): {}",
                HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    /**
     * Get all observation constellation objects
     *
     * @param session
     *            Hibernate session
     * @return Observation constellation objects
     */
    @SuppressWarnings("unchecked")
    public List<ObservationConstellation> getObservationConstellations(Session session) {
        Criteria criteria =
                session.createCriteria(ObservationConstellation.class)
                        .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                        .add(Restrictions.eq(ObservationConstellation.DELETED, false));
        LOGGER.debug("QUERY getObservationConstellations(): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    /**
     * Get info for all observation constellation objects
     *
     * @param session
     *            Hibernate session
     * @return Observation constellation info objects
     */
    public List<ObservationConstellationInfo> getObservationConstellationInfo(Session session) {
        List<ObservationConstellationInfo> ocis = Lists.newArrayList();
        if (HibernateHelper.isEntitySupported(ObservationConstellation.class)) {
            Criteria criteria = session.createCriteria(ObservationConstellation.class, "oc")
                    .createAlias(ObservationConstellation.OFFERING, "o")
                    .createAlias(ObservationConstellation.PROCEDURE, "p")
                    .createAlias(ObservationConstellation.OBSERVABLE_PROPERTY, "op")
                    .add(Restrictions.eq("op." + ObservableProperty.HIDDEN_CHILD, false))
                    .createAlias(ObservationConstellation.OBSERVATION_TYPE, "ot", JoinType.LEFT_OUTER_JOIN)
                    .add(Restrictions.eq(ObservationConstellation.DELETED, false))
                    .setProjection(Projections.projectionList()
                        .add(Projections.property("o." + Offering.IDENTIFIER))
                        .add(Projections.property("p." + Procedure.IDENTIFIER))
                        .add(Projections.property("op." + ObservableProperty.IDENTIFIER))
                        .add(Projections.property("ot." + ObservationType.OBSERVATION_TYPE))
                        .add(Projections.property("oc." + ObservationConstellation.HIDDEN_CHILD)));
            LOGGER.debug("QUERY getObservationConstellationInfo(): {}", HibernateHelper.getSqlString(criteria));

            @SuppressWarnings("unchecked")
            List<Object[]> results = criteria.list();
            for (Object[] result : results) {
                ObservationConstellationInfo oci = new ObservationConstellationInfo();
                oci.setOffering((String) result[0]);
                oci.setProcedure((String) result[1]);
                oci.setObservableProperty((String) result[2]);
                oci.setObservationType((String) result[3]);
                oci.setHiddenChild((Boolean) result[4]);
                ocis.add(oci);
            }
        }
        return ocis;
    }

    /**
     * Insert or update and get observation constellation for procedure,
     * observable property and offering
     *
     * @param procedure
     *            Procedure object
     * @param observableProperty
     *            Observable property object
     * @param offering
     *            Offering object
     * @param hiddenChild
     *            Is observation constellation hidden child
     * @param session
     *            Hibernate session
     * @return Observation constellation object
     */
    public ObservationConstellation checkOrInsertObservationConstellation(Procedure procedure,
            ObservableProperty observableProperty, Offering offering, boolean hiddenChild, Session session) {
        Criteria criteria =
                session.createCriteria(ObservationConstellation.class)
                        .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                        .add(Restrictions.eq(ObservationConstellation.OFFERING, offering))
                        .add(Restrictions.eq(ObservationConstellation.OBSERVABLE_PROPERTY, observableProperty))
                        .add(Restrictions.eq(ObservationConstellation.PROCEDURE, procedure));
        LOGGER.debug(
                "QUERY checkOrInsertObservationConstellation(procedure, observableProperty, offering, hiddenChild): {}",
                HibernateHelper.getSqlString(criteria));
        ObservationConstellation obsConst = (ObservationConstellation) criteria.uniqueResult();
        if (obsConst == null) {
            obsConst = new ObservationConstellation();
            obsConst.setObservableProperty(observableProperty);
            obsConst.setProcedure(procedure);
            obsConst.setOffering(offering);
            obsConst.setDeleted(false);
            obsConst.setHiddenChild(hiddenChild);
            session.save(obsConst);
            session.flush();
            session.refresh(obsConst);
        } else if (obsConst.getDeleted()) {
            obsConst.setDeleted(false);
            session.update(obsConst);
            session.flush();
            session.refresh(obsConst);
        }
        return obsConst;
    }

    /**
     * Check and Update and/or get observation constellation objects
     *
     * @param sosObservationConstellation
     *            SOS observation constellation
     * @param offering
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @param parameterName
     *            Parameter name for exception
     * @return Observation constellation object
     * @throws OwsExceptionReport
     *             If the requested observation type is invalid
     */
    public ObservationConstellation checkObservationConstellation(
            OmObservationConstellation sosObservationConstellation, String offering, Session session,
            String parameterName) throws OwsExceptionReport {
        AbstractPhenomenon observableProperty = sosObservationConstellation.getObservableProperty();
        String observablePropertyIdentifier = observableProperty.getIdentifier();
        String procedure = sosObservationConstellation.getProcedure().getIdentifier();

        Criteria c =
                session.createCriteria(ObservationConstellation.class).setResultTransformer(
                        Criteria.DISTINCT_ROOT_ENTITY);

        c.createCriteria(ObservationConstellation.OFFERING)
                .add(Restrictions.eq(Offering.IDENTIFIER, offering));
        c.createCriteria(ObservationConstellation.OBSERVABLE_PROPERTY)
                .add(Restrictions.eq(ObservableProperty.IDENTIFIER, observablePropertyIdentifier));
        c.createCriteria(ObservationConstellation.PROCEDURE)
                .add(Restrictions.eq(Procedure.IDENTIFIER, procedure));

        LOGGER.debug("QUERY checkObservationConstellation(sosObservationConstellation, offering): {}",
                HibernateHelper.getSqlString(c));
        ObservationConstellation hoc = (ObservationConstellation) c.uniqueResult();

        if (hoc == null) {
            throw new InvalidParameterValueException()
                    .at(Sos2Constants.InsertObservationParams.observation)
                    .withMessage(
                            "The requested observation constellation (procedure=%s, observedProperty=%s and offering=%s) is invalid!",
                            procedure, observablePropertyIdentifier, sosObservationConstellation.getOfferings());
        }
        String observationType = sosObservationConstellation.getObservationType();

        if (!checkObservationType(hoc, observationType, session)) {
            throw new InvalidParameterValueException()
                    .at(parameterName)
                    .withMessage(
                            "The requested observationType (%s) is invalid for procedure = %s, observedProperty = %s and offering = %s! The valid observationType is '%s'!",
                            observationType, procedure,observablePropertyIdentifier, sosObservationConstellation.getOfferings(), hoc.getObservationType().getObservationType());
        }


        // add parent/childs
        if (observableProperty instanceof OmCompositePhenomenon) {
            OmCompositePhenomenon omCompositePhenomenon = (OmCompositePhenomenon) observableProperty;
            ObservablePropertyDAO dao = new ObservablePropertyDAO();
            Map<String, ObservableProperty> obsprop = dao.getOrInsertObservablePropertyAsMap(Arrays.asList(observableProperty), false, session);
            for (OmObservableProperty child : omCompositePhenomenon) {
                checkOrInsertObservationConstellation(
                        hoc.getProcedure(),
                        obsprop.get(child.getIdentifier()),
                        hoc.getOffering(),
                        true,
                        session);
            }

        }
        return hoc;
    }

    public boolean checkObservationType(ObservationConstellation hoc, String observationType, Session session) {
        String hObservationType = hoc.getObservationType() == null ? null : hoc.getObservationType().getObservationType();
        if (hObservationType == null || hObservationType.isEmpty() || hObservationType.equals("NOT_DEFINED")) {
            updateObservationConstellation(hoc, observationType, session);
        } else  if (!hObservationType.equals(observationType)) {
            return false;
        }
        return true;
    }

    /**
     * Update observation constellation with observation type
     *
     * @param observationConstellation
     *            Observation constellation object
     * @param observationType
     *            Observation type
     * @param session
     *            Hibernate session
     */
    @SuppressWarnings("unchecked")
    public void updateObservationConstellation(ObservationConstellation observationConstellation,
                                               String observationType, Session session) {
        ObservationType obsType = new ObservationTypeDAO().getObservationTypeObject(observationType, session);
        observationConstellation.setObservationType(obsType);
        session.saveOrUpdate(observationConstellation);

        // update hidden child observation constellations
        // TODO should hidden child observation constellations be restricted to
        // the parent observation type?
        Set<String> offerings =
                new HashSet<>(Configurator.getInstance().getCache()
                        .getOfferingsForProcedure(observationConstellation.getProcedure().getIdentifier()));
        offerings.remove(observationConstellation.getOffering().getIdentifier());

        if (CollectionHelper.isNotEmpty(offerings)) {
            Criteria c =
                    session.createCriteria(ObservationConstellation.class)
                            .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                            .add(Restrictions.eq(ObservationConstellation.OBSERVABLE_PROPERTY,
                                    observationConstellation.getObservableProperty()))
                            .add(Restrictions.eq(ObservationConstellation.PROCEDURE,
                                    observationConstellation.getProcedure()))
                            .add(Restrictions.eq(ObservationConstellation.HIDDEN_CHILD, true));
            c.createCriteria(ObservationConstellation.OFFERING).add(Restrictions.in(Offering.IDENTIFIER, offerings));
            LOGGER.debug("QUERY updateObservationConstellation(observationConstellation, observationType): {}",
                    HibernateHelper.getSqlString(c));
            List<ObservationConstellation> hiddenChildObsConsts = c.list();
            for (ObservationConstellation hiddenChildObsConst : hiddenChildObsConsts) {
                hiddenChildObsConst.setObservationType(obsType);
                session.saveOrUpdate(hiddenChildObsConst);
            }
        }
    }

    /**
     * Return the non-deleted observation constellations for an offering
     *
     * @param offering
     *            Offering to fetch observation constellations for
     * @param session
     *            Session to use
     * @return Offering's observation constellations
     */
    @SuppressWarnings("unchecked")
    public List<ObservationConstellation> getObservationConstellationsForOffering(Offering offering, Session session) {
        if (HibernateHelper.isEntitySupported(ObservationConstellation.class)) {
            Criteria criteria =
                    session.createCriteria(ObservationConstellation.class)
                            .add(Restrictions.eq(ObservationConstellation.DELETED, false))
                            .add(Restrictions.eq(ObservationConstellation.OFFERING, offering));
            LOGGER.debug("QUERY getObservationConstellationsForOffering(offering): {}",
                    HibernateHelper.getSqlString(criteria));
            return criteria.list();
        }
        return Lists.newLinkedList();
    }

    /**
     * Update ObservationConstellation for procedure and set deleted flag
     *
     * @param procedure
     *            Procedure for which the ObservationConstellations should be
     *            changed
     * @param deleteFlag
     *            New deleted flag value
     * @param session
     *            Hibernate session
     */
    public void updateObservatioConstellationSetAsDeletedForProcedure(String procedure, boolean deleteFlag,
            Session session) {
        @SuppressWarnings("unchecked")
        List<ObservationConstellation> hObservationConstellations =
                session.createCriteria(ObservationConstellation.class)
                        .createCriteria(ObservationConstellation.PROCEDURE)
                        .add(Restrictions.eq(Procedure.IDENTIFIER, procedure)).list();
        for (ObservationConstellation hObservationConstellation : hObservationConstellations) {
            hObservationConstellation.setDeleted(deleteFlag);
            session.saveOrUpdate(hObservationConstellation);
            session.flush();
        }
    }

    /**
     * Get ObservationCollection entities for procedures, observableProperties
     * and offerings where observationType is not null;
     *
     * @param procedures
     *            Procedures to get ObservationCollection entities for
     * @param observedProperties
     *            ObservableProperties to get ObservationCollection entities for
     * @param offerings
     *            Offerings to get ObservationCollection entities for
     * @param session
     *            Hibernate session
     * @return Resulting ObservationCollection entities
     */
    @SuppressWarnings("unchecked")
    public List<ObservationConstellation> getObservationConstellations(List<String> procedures,
            List<String> observedProperties, List<String> offerings, Session session) {
        final Criteria c =
                session.createCriteria(ObservationConstellation.class)
                        .add(Restrictions.eq(ObservationConstellation.DELETED, false))
                        .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        if (CollectionHelper.isNotEmpty(offerings)) {
            c.createCriteria(ObservationConstellation.OFFERING).add(Restrictions.in(Offering.IDENTIFIER, offerings));
        }
        if (CollectionHelper.isNotEmpty(observedProperties)) {
            c.createCriteria(ObservationConstellation.OBSERVABLE_PROPERTY).add(
                    Restrictions.in(ObservableProperty.IDENTIFIER, observedProperties));
        }
        if (CollectionHelper.isNotEmpty(procedures)) {
            c.createCriteria(ObservationConstellation.PROCEDURE)
                    .add(Restrictions.in(Procedure.IDENTIFIER, procedures));
        }
        c.add(Restrictions.isNotNull(ObservationConstellation.OBSERVATION_TYPE));
        LOGGER.debug("QUERY getObservationConstellations(): {}", HibernateHelper.getSqlString(c));
        return c.list();

    }

    @SuppressWarnings("unchecked")
    protected Set<ObservationConstellation> getObservationConstellations(Session session, Procedure procedure) {
        return Sets.newHashSet(session.createCriteria(ObservationConstellation.class)
                .add(Restrictions.eq(ObservationConstellation.DELETED, false))
                .add(Restrictions.eq(ObservationConstellation.PROCEDURE, procedure))
                .list());
    }
}
