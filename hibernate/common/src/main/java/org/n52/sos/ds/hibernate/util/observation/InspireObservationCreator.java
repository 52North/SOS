/*
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

import java.net.URI;
import java.util.Collections;
import java.util.Set;

import javax.inject.Inject;

import org.hibernate.Session;
import org.n52.faroe.ConfigurationError;
import org.n52.faroe.Validation;
import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.binding.BindingRepository;
import org.n52.iceland.service.ServiceSettings;
import org.n52.janmayen.http.MediaTypes;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.RelatedDatasetEntity;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.ows.exception.CodedException;

@Configurable
public class InspireObservationCreator implements AdditionalObservationCreator {

    private static final String NS_OMSO_30 = "http://inspire.ec.europa.eu/schemas/omso/3.0";

    private static final Set<AdditionalObservationCreatorKey> KEYS = AdditionalObservationCreatorRepository
            .encoderKeysForElements(NS_OMSO_30, DataEntity.class, DatasetEntity.class);

    @Inject
    private BindingRepository bindingRepository;
    private String serviceURL;

    @Setting(ServiceSettings.SERVICE_URL)
    public void setServiceURL(URI serviceURL) throws ConfigurationError {
        Validation.notNull("Service URL", serviceURL);
        String url = serviceURL.toString();
        if (url.contains("?")) {
            url = url.split("[?]")[0];
        }
        this.serviceURL = url;
    }

    @Override
    public Set<AdditionalObservationCreatorKey> getKeys() {
        return Collections.unmodifiableSet(KEYS);
    }

    @Override
    public OmObservation create(OmObservation omObservation, DatasetEntity series, Session session)
            throws CodedException {
        create(omObservation, series);
        // TODO remove from PointObservation, profile, multipoint
        addRelatedSeries(omObservation, series.getRelatedDatasets());
        return omObservation;
    }

    @Override
    public OmObservation create(OmObservation omObservation, DataEntity<?> observation, Session session)
            throws CodedException {
        create(omObservation, observation);
        addRelatedSeries(omObservation, observation.getDataset().getRelatedDatasets());
        return omObservation;
    }

    @Override
    public OmObservation add(OmObservation omObservation, DataEntity<?> observation, Session session)
            throws CodedException {
        add(omObservation, observation);
        addRelatedSeries(omObservation, observation.getDataset().getRelatedDatasets());
        return omObservation;
    }

    private void addRelatedSeries(OmObservation omObservation, Set<RelatedDatasetEntity> relatedSeries)
            throws CodedException {
        new RelatedSeriesAdder(omObservation, relatedSeries, serviceURL.toString(),
                bindingRepository.isActive(MediaTypes.APPLICATION_KVP)).add();
    }
}
