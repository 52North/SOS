/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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

import static java.util.stream.Collectors.toSet;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.hibernate.Session;
import org.n52.iceland.convert.ConverterException;
import org.n52.janmayen.i18n.LocaleHelper;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.shetland.ogc.om.ObservationStream;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.request.AbstractObservationRequest;

/**
 * @since 4.0.0
 *
 */
public final class HibernateObservationUtilities {
    private HibernateObservationUtilities() {
    }

    public static ObservationStream createSosObservationsFromObservations(Collection<DataEntity<?>> o,
            AbstractObservationRequest r, String pdf, OmObservationCreatorContext ctx, Session s)
            throws OwsExceptionReport, ConverterException {
        return new ObservationOmObservationCreator(o, r,
                LocaleHelper.decode(r.getRequestedLanguage(), ctx.getDefaultLanguage()), pdf, ctx, s).create();
    }

    public static ObservationStream createSosObservationsFromObservations(Collection<DataEntity<?>> o,
            AbstractObservationRequest r, Locale l, String pdf, OmObservationCreatorContext ctx, Session s)
            throws OwsExceptionReport, ConverterException {
        return new ObservationOmObservationCreator(o, r, l, pdf, ctx, s).create();
    }

    public static OmObservation createSosObservationFromObservation(DataEntity<?> o, AbstractObservationRequest r,
            Locale l, String pdf, OmObservationCreatorContext ctx, Session s)
            throws OwsExceptionReport, ConverterException {
        ObservationStream c = new ObservationOmObservationCreator(Arrays.asList(o), r, l, pdf, ctx, s).create();
        if (c.hasNext()) {
            return c.next();
        }
        return null;
    }

    /**
     * Create SOS internal observation from ObservationConstellation
     *
     * @param dataset
     *            dataset object
     * @param fois
     *            List of featureOfInterest identifiers
     * @param r
     *            request
     * @param pdf
     *            procedure description format
     * @param ctx
     *            observation context
     * @param session
     *            Hibernate session
     * @return SOS internal observation
     * @throws OwsExceptionReport
     *             If an error occurs
     * @throws ConverterException
     *             If observation creation fails
     */
    public static ObservationStream createSosObservationFromObservationConstellation(DatasetEntity dataset,
            List<String> fois, AbstractObservationRequest r, String pdf, OmObservationCreatorContext ctx,
            Session session) throws OwsExceptionReport, ConverterException {
        return createSosObservationFromObservationConstellation(dataset, fois, r,
                LocaleHelper.decode(r.getRequestedLanguage(), ctx.getDefaultLanguage()), pdf, ctx, session);
    }

    public static ObservationStream createSosObservationFromObservationConstellation(DatasetEntity dataset,
            List<String> fois, AbstractObservationRequest request, Locale l, String pdf,
            OmObservationCreatorContext ctx, Session session) throws OwsExceptionReport, ConverterException {
        return new ObservationConstellationOmObservationCreator(dataset, fois, request, l, pdf, ctx, session).create();
    }

    /**
     * Create SOS internal observation from Series
     *
     * @param dataset
     *            dataset object
     * @param r
     *            The request
     * @param pdf
     *            procedure description format
     * @param ctx
     *            observation context
     * @param session
     *            Hibernate session
     * @return SOS internal observation
     * @throws OwsExceptionReport
     *             If an error octxurs
     * @throws ConverterException
     *             If procedure creation fails
     */
    public static ObservationStream createSosObservationFromSeries(DatasetEntity dataset, AbstractObservationRequest r,
            String pdf, OmObservationCreatorContext ctx, Session session)
            throws OwsExceptionReport, ConverterException {
        if (dataset.hasEreportingProfile()) {
            return createSosObservationFromEReportingSeries(dataset, r,
                    LocaleHelper.decode(r.getRequestedLanguage(), ctx.getDefaultLanguage()), pdf, ctx, session);
        } else {
            return createSosObservationFromSeries(dataset, r,
                    LocaleHelper.decode(r.getRequestedLanguage(), ctx.getDefaultLanguage()), pdf, ctx, session);
        }
    }

    public static ObservationStream createSosObservationFromSeries(DatasetEntity dataset, AbstractObservationRequest r,
            Locale l, String pdf, OmObservationCreatorContext ctx, Session session)
            throws OwsExceptionReport, ConverterException {
        if (dataset.hasEreportingProfile()) {
            return createSosObservationFromEReportingSeries(dataset, r, l, pdf, ctx, session);
        }
        return new SeriesOmObservationCreator(dataset, r, l, pdf, ctx, session).create();
    }

    public static ObservationStream createSosObservationFromEReportingSeries(DatasetEntity dataset,
            AbstractObservationRequest r, String pdf, OmObservationCreatorContext ctx, Session session)
            throws OwsExceptionReport, ConverterException {
        return createSosObservationFromEReportingSeries(dataset, r,
                LocaleHelper.decode(r.getRequestedLanguage(), ctx.getDefaultLanguage()), pdf, ctx, session);
    }

    public static ObservationStream createSosObservationFromEReportingSeries(DatasetEntity dataset,
            AbstractObservationRequest r, Locale l, String pdf, OmObservationCreatorContext ctx, Session session)
            throws OwsExceptionReport, ConverterException {
        return new EReportingSeriesOmObservationCreator(dataset, r, l, pdf, ctx, session).create();
    }

    /**
     * Get observation ids from observation objects
     *
     * @param observations
     *            Collection of observation objects
     * @return Observation ids as Set
     */
    public static Set<Long> getObservationIds(Collection<DataEntity<?>> observations) {
        return observations.stream().map(DataEntity::getId).collect(toSet());
    }

}
