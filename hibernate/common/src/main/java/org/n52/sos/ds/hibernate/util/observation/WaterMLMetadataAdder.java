/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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

import java.util.Optional;

import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.parameter.ParameterEntity;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.OmObservationConstellation;
import org.n52.shetland.ogc.om.series.DefaultPointMetadata;
import org.n52.shetland.ogc.om.series.MeasurementTimeseriesMetadata;
import org.n52.shetland.ogc.om.series.Metadata;
import org.n52.shetland.ogc.om.series.wml.DefaultTVPMeasurementMetadata;
import org.n52.shetland.ogc.om.series.wml.WaterMLConstants;
import org.n52.shetland.ogc.om.series.wml.WaterMLConstants.InterpolationType;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 *
 */
public class WaterMLMetadataAdder {

    private OmObservation omObservation;

    private DatasetEntity series;

    public WaterMLMetadataAdder(OmObservation omObservation, DatasetEntity series) {
        this.omObservation = omObservation;
        this.series = series;
    }

    public WaterMLMetadataAdder add() throws CodedException {
        if (series.hasParameters()) {
            OmObservationConstellation observationConstellation = omObservation.getObservationConstellation();
            /*
             * Add interpolation type
             */
            if (!observationConstellation.isSetDefaultPointMetadata()) {
                observationConstellation.setDefaultPointMetadata(new DefaultPointMetadata());
            }
            if (!observationConstellation.getDefaultPointMetadata().isSetDefaultTVPMeasurementMetadata()) {
                observationConstellation.getDefaultPointMetadata()
                        .setDefaultTVPMeasurementMetadata(new DefaultTVPMeasurementMetadata());
            }
            /*
             * Get interpolation type from database
             */
            Optional<Object> interpolationTypeTitle =
                    getMetadataElement(series, WaterMLConstants.NS_WML_20, WaterMLConstants.INTERPOLATION_TYPE);
            /*
             * Default Value
             */
            InterpolationType interpolationType = WaterMLConstants.InterpolationType.Continuous;
            if (interpolationTypeTitle.isPresent()) {
                try {
                    interpolationType = InterpolationType.from(interpolationTypeTitle.get().toString());
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
            Optional<Object> cumulativeMetadata = getMetadataElement(series, WaterMLConstants.NS_WML_20,
                    WaterMLConstants.SERIES_METADATA_CUMULATIVE);
            /*
             * Default Value
             */
            boolean cumulative = false;
            if (cumulativeMetadata.isPresent()) {
                String cumulativeMetadataValue = cumulativeMetadata.get().toString();
                if (!cumulativeMetadataValue.isEmpty() && (cumulativeMetadataValue.equalsIgnoreCase("true")
                        || cumulativeMetadataValue.equalsIgnoreCase("false")
                        || cumulativeMetadataValue.equalsIgnoreCase("1")
                        || cumulativeMetadataValue.equalsIgnoreCase("0"))) {
                    if (cumulativeMetadataValue.equals("1")) {
                        cumulative = true;
                    } else {
                        cumulative = Boolean.parseBoolean(cumulativeMetadataValue);
                    }
                } else {
                    throw createMetadataInvalidException(WaterMLConstants.SERIES_METADATA_CUMULATIVE,
                            cumulativeMetadataValue, null);
                }
            }
            ((MeasurementTimeseriesMetadata) observationConstellation.getMetadata().getTimeseriesmetadata())
                    .setCumulative(cumulative);
        }
        return this;
    }

    private Optional<Object> getMetadataElement(DatasetEntity dataset, String domain, String name) {
        if (dataset.hasParameters()) {
            for (ParameterEntity<?> parameter : dataset.getParameters()) {
                if (domain.equals(parameter.getDomain()) && name.equals(parameter.getName())) {
                    return Optional.ofNullable(parameter.getValue());
                }
            }
        }
        return Optional.empty();
    }

    private CodedException createMetadataInvalidException(String metadataKey, String metadataContent,
            IllegalArgumentException iae) {
        CodedException e = new NoApplicableCodeException().withMessage(
                "Series Metadata '%s' for Series '%s' "
                        + "could not be parsed '%s'. Please contact the administrator of this service.",
                metadataKey, series.getId(), metadataContent);
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
