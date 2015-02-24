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
package org.n52.sos.ds.hibernate.util.observation;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.n52.sos.convert.ConverterException;
import org.n52.sos.ds.hibernate.dao.ObservationConstellationDAO;
import org.n52.sos.ds.hibernate.entities.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.AbstractSpatialFilteringProfile;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.series.Series;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.CollectionHelper;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @since 4.0.0
 * 
 */
public class HibernateObservationUtilities {
    private HibernateObservationUtilities() {
    }

    /**
     * @deprecated use
     *             {@link ObservationConstellationDAO#getFirstObservationConstellationForOfferings(org.n52.sos.ds.hibernate.entities.Procedure, org.n52.sos.ds.hibernate.entities.ObservableProperty, java.util.Collection, org.hibernate.Session)
     * }
     */
    @Deprecated
    public static ObservationConstellation getFirstObservationConstellation(Procedure p, ObservableProperty op,
            Collection<Offering> o, Session s) {
        return new ObservationConstellationDAO().getFirstObservationConstellationForOfferings(p, op, o, s);
    }

    /**
     * @deprecated use
     *             {@link ObservationConstellationDAO#getObservationConstellationsForOfferings(org.n52.sos.ds.hibernate.entities.Procedure, org.n52.sos.ds.hibernate.entities.ObservableProperty, java.util.Collection, org.hibernate.Session)
     * }
     */
    @Deprecated
    public static List<ObservationConstellation> getObservationConstellations(Procedure p, ObservableProperty op,
            Collection<Offering> o, Session s) {
        return new ObservationConstellationDAO().getObservationConstellationsForOfferings(p, op, o, s);
    }

    /**
     * Create SOS internal observation from Observation objects
     * 
     * @param o
     *            List of Observation objects
     * @param spf
     *            Map with spatial filtering profile entities, key observation
     *            entity id
     * @param v
     *            Service version
     * @param rm
     *            Requested result model
     * @param s
     *            Hibernate session
     * 
     * @return SOS internal observation
     * 
     * @throws OwsExceptionReport
     *             If an error occurs
     * @throws ConverterException
     *             If procedure creation fails
     */
    @Deprecated
    public static List<OmObservation> createSosObservationsFromObservations(Collection<AbstractObservation> o,
            Map<Long, AbstractSpatialFilteringProfile> spf, String v, String rm, Session s) throws OwsExceptionReport,
            ConverterException {
        return new ObservationOmObservationCreator(o, spf, v, rm, s).create();

    }
    
    public static List<OmObservation> createSosObservationsFromObservations(Collection<AbstractObservation> o,
            String v, String rm, Session s) throws OwsExceptionReport,
            ConverterException {
        return new ObservationOmObservationCreator(o, v, rm, s).create();

    }

    @Deprecated
    public static OmObservation createSosObservationFromObservation(AbstractObservation o,
            AbstractSpatialFilteringProfile spf, String v, String rm, Session s) throws OwsExceptionReport, ConverterException {
        Map<Long, AbstractSpatialFilteringProfile> spfMap = Maps.newHashMap();
        if (spf != null) {
            spfMap.put(spf.getObservation().getObservationId(), spf);
        }
        List<OmObservation> c = new ObservationOmObservationCreator(Sets.newHashSet(o), spfMap, v, rm, s).create();
        if (CollectionHelper.isNotEmpty(c)) {
            return (OmObservation) c.iterator().next();
        }
        return null;
    }
    
    public static OmObservation createSosObservationFromObservation(AbstractObservation o,
            String v, String rm, Session s) throws OwsExceptionReport, ConverterException {
        List<OmObservation> c = new ObservationOmObservationCreator(Sets.newHashSet(o), v, rm, s).create();
        if (CollectionHelper.isNotEmpty(c)) {
            return (OmObservation) c.iterator().next();
        }
        return null;
    }

    /**
     * Create SOS internal observation from ObservationConstellation
     * 
     * @param oc
     *            ObservationConstellation object
     * @param fois
     *            List of featureOfInterest identifiers
     * @param version
     *            Service version
     * @param session
     *            Hibernate session
     * @return SOS internal observation
     * @throws OwsExceptionReport
     *             If an error occurs
     * @throws ConverterException
     *             If procedure creation fails
     */
    public static Collection<? extends OmObservation> createSosObservationFromObservationConstellation(
            ObservationConstellation oc, List<String> fois, String version, Session session)
            throws OwsExceptionReport, ConverterException {
        return new ObservationConstellationOmObservationCreator(oc, fois, version, session).create();
    }

    /**
     * Create SOS internal observation from Series
     * 
     * @param series
     *            Series object
     * @param version
     *            Service version
     * @param session
     *            Hibernate session
     * @return SOS internal observation
     * @throws OwsExceptionReport
     *             If an error occurs
     * @throws ConverterException
     *             If procedure creation fails
     */
    public static Collection<? extends OmObservation> createSosObservationFromSeries(Series series, String version,
            Session session) throws OwsExceptionReport, ConverterException {
        return new SeriesOmObservationCreator(series, version, session).create();
    }

    /**
     * Unfold observation with MultiObservationValue to multiple observations
     * with SingleObservationValue
     * 
     * @param o
     *            OmObservation to unfold
     * @return OmObservation list
     * @throws OwsExceptionReport
     *             If unfolding fails
     */
    public static List<OmObservation> unfoldObservation(OmObservation o) throws OwsExceptionReport {
        return new ObservationUnfolder(o).unfold();
    }

    /**
     * Get observation ids from observation objects
     * 
     * @param observations
     *            Collection of observation objects
     * @return Observation ids as Set
     */
    public static Set<Long> getObservationIds(Collection<AbstractObservation> observations) {
        Set<Long> observationIds = Sets.newHashSet();
        for (AbstractObservation observation : observations) {
            observationIds.add(observation.getObservationId());
        }
        return observationIds;
    }
}
