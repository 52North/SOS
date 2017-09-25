/*
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.entities.observation.series;

import java.net.URI;
import java.net.URISyntaxException;

import org.n52.io.response.dataset.AbstractValue;
import org.n52.shetland.ogc.gml.CodeType;
import org.n52.shetland.ogc.gml.CodeWithAuthority;
import org.n52.shetland.ogc.om.OmObservableProperty;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.values.Value;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.util.OMHelper;
import org.n52.sos.ds.hibernate.entities.observation.AbstractValuedObservation;
import org.n52.sos.ds.hibernate.util.HibernateGeometryCreator;
import org.n52.sos.ds.hibernate.util.observation.ObservationValueCreator;

/**
 * Abstract implementation of {@link ValuedSeriesObservation}.
 *
 * @author Christian Autermann
 * @param <T> the value type
 */
public abstract class AbstractValuedSeriesObservation<T>
        extends AbstractValuedObservation<T>
        implements ValuedSeriesObservation<T> {

    private static final long serialVersionUID = -2757686338936995366L;
    private Series series;

    @Override
    public Series getSeries() {
        return series;
    }

    @Override
    public void setSeries(Series series) {
        this.series = series;
    }

    /**
     * Add {@link AbstractValue} data to {@link OmObservation}
     *
     * @param observation
     *            {@link OmObservation} to add data
     * @param responseFormat
     * @throws OwsExceptionReport
     *             If an error occurs when getting the value
     */
    public OmObservation addValuesToObservation(OmObservation observation, String responseFormat)
            throws OwsExceptionReport {
        observation.setObservationID(Long.toString(getObservationId()));
        if (!observation.isSetIdentifier() && isSetIdentifier()) {
            CodeWithAuthority identifier = new CodeWithAuthority(getIdentifier());
            if (isSetCodespace()) {
                identifier.setCodeSpace(getCodespace().getCodespace());
            }
            observation.setIdentifier(identifier);
        }
        if (!observation.isSetName() && isSetDescription()) {
            CodeType name = new CodeType(getName());
            if (isSetCodespace()) {
                try {
                    name.setCodeSpace(new URI(getCodespace().getCodespace()));
                } catch (URISyntaxException e) {
                    throw new NoApplicableCodeException().causedBy(e).withMessage("Invalid codeSpace value: {}", getCodespace().getCodespace());
                }
            }
            observation.setName(name);
        }
        if (!observation.isSetDescription() && isSetDescription()) {
            observation.setDescription(getDescription());
        }
        Value<?> value = accept(new ObservationValueCreator());
        if (!value.isSetUnit()
                && observation.getObservationConstellation().getObservableProperty() instanceof OmObservableProperty
                && ((OmObservableProperty) observation.getObservationConstellation().getObservableProperty())
                        .isSetUnit()) {
            value.setUnit( ((OmObservableProperty) observation.getObservationConstellation().getObservableProperty())
                        .getUnit());
        }
        if (!observation.getObservationConstellation().isSetObservationType()) {
            observation.getObservationConstellation().setObservationType(OMHelper.getObservationTypeFor(value));
        }
        observation.setResultTime(createResutlTime(getResultTime()));
        observation.setValidTime(createValidTime(getValidTimeStart(), getValidTimeEnd()));
        if (hasSamplingGeometry()) {
            observation.addParameter(createSpatialFilteringProfileParameter(getSamplingGeometry()));
        } else if (isSetLongLat()) {
            observation.addParameter(createSpatialFilteringProfileParameter(new HibernateGeometryCreator().createGeometry(this)));
        }
        addRelatedObservation(observation);
        addParameter(observation);
        addValueSpecificDataToObservation(observation, responseFormat);
        addObservationValueToObservation(observation, value, responseFormat);
        return observation;
    }

}
