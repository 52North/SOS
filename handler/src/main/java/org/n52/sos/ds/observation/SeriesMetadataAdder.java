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
package org.n52.sos.ds.observation;

import java.util.Optional;

import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.parameter.ParameterEntity;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings({"EI_EXPOSE_REP2"})
public abstract class SeriesMetadataAdder {
    private OmObservation omObservation;

    private DatasetEntity dataset;

    public SeriesMetadataAdder(OmObservation omObservation, DatasetEntity dataset) {
        this.omObservation = omObservation;
        this.dataset = dataset;
    }

    protected Optional<Object> getMetadataElement(DatasetEntity dataset, String domain, String name) {
        if (dataset.hasParameters()) {
            for (ParameterEntity<?> parameter : dataset.getParameters()) {
                if (domain.equals(parameter.getDomain()) && name.equals(parameter.getName())) {
                    return Optional.ofNullable(parameter.getValue());
                }
            }
        }
        return Optional.empty();
    }

    protected CodedException createMetadataInvalidException(String metadataKey, String metadataContent,
            IllegalArgumentException iae) {
        CodedException e = new NoApplicableCodeException().withMessage(
                "Series Metadata '%s' for Series '%s' "
                        + "could not be parsed '%s'. Please contact the administrator of this service.",
                metadataKey, getDataset().getId(), metadataContent);
        if (iae != null) {
            return e.causedBy(iae);
        } else {
            return e;
        }
    }

    public OmObservation result() {
        return getObservation();
    }

    protected OmObservation getObservation() {
        return omObservation;
    }

    protected DatasetEntity getDataset() {
        return dataset;
    }
}
