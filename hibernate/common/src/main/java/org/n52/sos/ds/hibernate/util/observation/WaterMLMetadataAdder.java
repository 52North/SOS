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
package org.n52.sos.ds.hibernate.util.observation;

import java.util.List;

import org.hibernate.Session;
import org.n52.sos.ds.hibernate.dao.metadata.SeriesMetadataDAO;
import org.n52.sos.ds.hibernate.entities.metadata.SeriesMetadata;
import org.n52.sos.ds.hibernate.entities.observation.series.Series;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.series.wml.DefaultPointMetadata;
import org.n52.sos.ogc.series.wml.DefaultTVPMeasurementMetadata;
import org.n52.sos.ogc.series.wml.MeasurementTimeseriesMetadata;
import org.n52.sos.ogc.series.wml.Metadata;
import org.n52.sos.ogc.series.wml.WaterMLConstants;
import org.n52.sos.ogc.series.wml.WaterMLConstants.InterpolationType;

import com.google.common.base.Optional;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public class WaterMLMetadataAdder {

    private OmObservation omObservation;
    private Series series;
    private Session session;
    private static SeriesMetadataDAO seriesMetadataDAO = new SeriesMetadataDAO();

    public WaterMLMetadataAdder(OmObservation omObservation, Series series, Session session) {
        this.omObservation = omObservation;
        this.series = series;
        this.session = session;
    }

    public WaterMLMetadataAdder add() throws CodedException {
        if (HibernateHelper.isEntitySupported(SeriesMetadata.class)) {
            List<SeriesMetadata> seriesMetadata = seriesMetadataDAO.getDomainMetadata(series.getSeriesId(), 
                    WaterMLConstants.NS_WML_20, session);
            OmObservationConstellation observationConstellation = omObservation.getObservationConstellation();
            /*
             * Add interpolation type
             */
            if (!observationConstellation.isSetDefaultPointMetadata()) {
                observationConstellation.setDefaultPointMetadata(new DefaultPointMetadata());
            }
            if (!observationConstellation.getDefaultPointMetadata().isSetDefaultTVPMeasurementMetadata()) {
                observationConstellation.getDefaultPointMetadata().setDefaultTVPMeasurementMetadata(
                        new DefaultTVPMeasurementMetadata());
            }
            /*
             * Get interpolation type from database
             */
            Optional<String> interpolationTypeTitle = seriesMetadataDAO.getMetadataElement(seriesMetadata,
                    WaterMLConstants.NS_WML_20,
                    WaterMLConstants.INTERPOLATION_TYPE);
            /*
             * Default Value
             */
            InterpolationType interpolationType = WaterMLConstants.InterpolationType.Continuous;
            if (interpolationTypeTitle.isPresent()) {
                try {
                    interpolationType = InterpolationType.from(interpolationTypeTitle.get());
                } catch (IllegalArgumentException iae) {
                    throw createMetadataInvalidException(WaterMLConstants.INTERPOLATION_TYPE,
                            interpolationType.getTitle(), iae);
                }
            }
            observationConstellation.getDefaultPointMetadata().getDefaultTVPMeasurementMetadata()
            .setInterpolationtype(interpolationType);
            /*
             * Add cumulative
             */
            if (!observationConstellation.isSetMetadata()) {
                observationConstellation.setMetadata(new Metadata());
            }
            if (!observationConstellation.getMetadata().isSetTimeseriesMetadata()) {
                observationConstellation.getMetadata().setTimeseriesmetadata(new MeasurementTimeseriesMetadata());
            }
            Optional<String> cumulativeMetadata = seriesMetadataDAO.getMetadataElement(seriesMetadata,
                    WaterMLConstants.NS_WML_20,
                    WaterMLConstants.SERIES_METADATA_CUMULATIVE);
            /*
             * Default Value
             */
            boolean cumulative = false;
            if (cumulativeMetadata.isPresent()) {
                if (!cumulativeMetadata.get().isEmpty() && (
                            cumulativeMetadata.get().equalsIgnoreCase("true") || 
                            cumulativeMetadata.get().equalsIgnoreCase("false") ||
                            cumulativeMetadata.get().equalsIgnoreCase("1") ||
                            cumulativeMetadata.get().equalsIgnoreCase("0"))) {
                    if (cumulativeMetadata.get().equals("1")) {
                        cumulative = true;
                    } else {
                        cumulative = Boolean.parseBoolean(cumulativeMetadata.get());
                    }
                } else {
                    throw createMetadataInvalidException(WaterMLConstants.SERIES_METADATA_CUMULATIVE,
                            cumulativeMetadata.get(), null);
                }
            }
            ((MeasurementTimeseriesMetadata)observationConstellation.getMetadata().getTimeseriesmetadata())
                .setCumulative(cumulative);
        }
        return this;
    }

    private CodedException createMetadataInvalidException(String metadataKey, String metadataContent,
            IllegalArgumentException iae) {
        CodedException e = new NoApplicableCodeException().withMessage("Series Metadata '%s' for Series '%s' "
                + "could not be parsed '%s'. Please contact the administrator of this service.", 
                metadataKey,
                series.getSeriesId(),
                metadataContent);
        if (iae != null) {
            return e.causedBy(iae);
        } else {
            return e;
        }
    }

    public OmObservation result() {
        return omObservation;
    }

}
