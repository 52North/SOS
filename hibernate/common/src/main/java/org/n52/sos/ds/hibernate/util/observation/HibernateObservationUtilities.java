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
import java.util.Locale;
import java.util.Set;

import org.hibernate.Session;
import org.n52.sos.convert.ConverterException;
import org.n52.sos.ds.hibernate.entities.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingSeries;
import org.n52.sos.ds.hibernate.entities.series.Series;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.AbstractObservationRequest;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.util.CollectionHelper;

import com.google.common.collect.Sets;

/**
 * @since 4.0.0
 *
 */
public class HibernateObservationUtilities {
    private HibernateObservationUtilities() {
    }

    public static List<OmObservation> createSosObservationsFromObservations(Collection<AbstractObservation> o,
            AbstractObservationRequest r, Session s) throws OwsExceptionReport, ConverterException {
        return new ObservationOmObservationCreator(o, r, s).create();
    }

    public static List<OmObservation> createSosObservationsFromObservations(Collection<AbstractObservation> o,
             AbstractObservationRequest r, Locale l, Session s) throws OwsExceptionReport, ConverterException {
        return new ObservationOmObservationCreator(o, r, s).create();
    }

    public static OmObservation createSosObservationFromObservation(AbstractObservation o, AbstractObservationRequest r,
            Session s) throws OwsExceptionReport, ConverterException {
        List<OmObservation> c = new ObservationOmObservationCreator(Sets.newHashSet(o), r, s).create();
        if (CollectionHelper.isNotEmpty(c)) {
            return (OmObservation) c.iterator().next();
        }
        return null;
    }

    public static OmObservation createSosObservationFromObservation(AbstractObservation o, AbstractObservationRequest r,
            Locale l, Session s) throws OwsExceptionReport, ConverterException {
        List<OmObservation> c = new ObservationOmObservationCreator(Sets.newHashSet(o), r, s).create();
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
            ObservationConstellation oc, List<String> fois, AbstractObservationRequest request, Session session)
            throws OwsExceptionReport, ConverterException {
        return createSosObservationFromObservationConstellation(oc, fois, request, ServiceConfiguration.getInstance()
                .getDefaultLanguage(), session);
    }

    public static Collection<? extends OmObservation> createSosObservationFromObservationConstellation(
            ObservationConstellation oc, List<String> fois, AbstractObservationRequest request, Locale language, Session session)
            throws OwsExceptionReport, ConverterException {
        return new ObservationConstellationOmObservationCreator(oc, fois, request, language, session).create();
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
    public static Collection<? extends OmObservation> createSosObservationFromSeries(Series series, AbstractObservationRequest request,
            Session session) throws OwsExceptionReport, ConverterException {
        return createSosObservationFromSeries(series, request,
                ServiceConfiguration.getInstance().getDefaultLanguage(), session);
    }

    public static Collection<? extends OmObservation> createSosObservationFromSeries(Series series, AbstractObservationRequest request,
            Locale language, Session session) throws OwsExceptionReport, ConverterException {
        if (series instanceof EReportingSeries) {
            return createSosObservationFromEReportingSeries((EReportingSeries) series, request, ServiceConfiguration
                    .getInstance().getDefaultLanguage(), session);
        } else {
            return new SeriesOmObservationCreator(series, request, language, session).create();
        }
    }

    public static Collection<? extends OmObservation> createSosObservationFromEReportingSeries(EReportingSeries series,
            AbstractObservationRequest r, Session session) throws OwsExceptionReport, ConverterException {
        return createSosObservationFromEReportingSeries(series, r,
                ServiceConfiguration.getInstance().getDefaultLanguage(), session);
    }

    public static Collection<? extends OmObservation> createSosObservationFromEReportingSeries(EReportingSeries series,
            AbstractObservationRequest r, Locale language, Session session) throws OwsExceptionReport, ConverterException {
        return new EReportingSeriesOmObservationCreator(series, r, language, session).create();
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
