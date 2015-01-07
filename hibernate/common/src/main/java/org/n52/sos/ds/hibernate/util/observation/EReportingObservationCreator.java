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

import java.util.Set;

import org.n52.sos.aqd.AqdConstants;
import org.n52.sos.ds.hibernate.entities.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingBlobObservation;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingBooleanObservation;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingCategoryObservation;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingCountObservation;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingGeometryObservation;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingNumericObservation;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingObservation;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingSeries;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingSweDataArrayObservation;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingTextObservation;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.util.CollectionHelper;

public class EReportingObservationCreator implements AdditionalObservationCreator<EReportingSeries> {

    @SuppressWarnings("unchecked")
    private static final Set<AdditionalObservationCreatorKey> KEYS = CollectionHelper.union(
            AdditionalObservationCreatorRepository.encoderKeysForElements(AqdConstants.NS_AQD,
                    EReportingObservation.class, EReportingBlobObservation.class, EReportingBooleanObservation.class,
                    EReportingCategoryObservation.class, EReportingCountObservation.class,
                    EReportingGeometryObservation.class, EReportingNumericObservation.class,
                    EReportingSweDataArrayObservation.class, EReportingTextObservation.class, EReportingSeries.class),
            AdditionalObservationCreatorRepository.encoderKeysForElements(null, EReportingObservation.class,
                    EReportingBlobObservation.class, EReportingBooleanObservation.class,
                    EReportingCategoryObservation.class, EReportingCountObservation.class,
                    EReportingGeometryObservation.class, EReportingNumericObservation.class,
                    EReportingSweDataArrayObservation.class, EReportingTextObservation.class));

    private final EReportingObservationHelper helper = new EReportingObservationHelper();

    @Override
    public Set<AdditionalObservationCreatorKey> getKeys() {
        return KEYS;
    }

    @Override
    public OmObservation create(OmObservation omObservation, AbstractObservation observation) {
        if (observation instanceof EReportingObservation) {
            EReportingObservation eReportingObservation = (EReportingObservation) observation;
            create(omObservation, eReportingObservation.getEReportingSeries());
            add(omObservation, observation);
            omObservation.setValue(EReportingHelper.createSweDataArrayValue(omObservation, eReportingObservation));
            omObservation.getObservationConstellation().setObservationType(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION);
        }
        return omObservation;
    }

    @Override
    public OmObservation create(OmObservation omObservation, EReportingSeries series) {
        for (NamedValue<?> namedValue : helper.createOmParameterForEReporting(series)) {
            omObservation.addParameter(namedValue);
        }
        return omObservation;
    }

    @Override
    public OmObservation add(OmObservation omObservation, AbstractObservation observation) {
        if (observation instanceof EReportingObservation) {
            EReportingObservation eReportingObservation = (EReportingObservation) observation;
            omObservation.setAdditionalMergeIndicator(eReportingObservation.getPrimaryObservation());
        }
        return omObservation;
    }

}
