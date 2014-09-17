/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
import java.util.Set;

import org.n52.sos.aqd.AqdConstants;
import org.n52.sos.aqd.AqdConstants.ElementType;
import org.n52.sos.aqd.AqdUomRepository;
import org.n52.sos.aqd.AqdUomRepository.Uom;
import org.n52.sos.ds.hibernate.entities.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.ereporting.HiberanteEReportingRelations.EReportingValues;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingBlobObservation;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingBooleanObservation;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingCategoryObservation;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingCountObservation;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingGeometryObservation;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingNumericObservation;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingObservation;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingSweDataArrayObservation;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingTextObservation;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.quality.SosQuality;
import org.n52.sos.ogc.om.quality.SosQuality.QualityType;
import org.n52.sos.ogc.om.values.SweDataArrayValue;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweDataArray;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.encoding.SweAbstractEncoding;
import org.n52.sos.ogc.swe.simpleType.SweCategory;
import org.n52.sos.ogc.swe.simpleType.SweCount;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.ogc.swe.simpleType.SweTime;
import org.n52.sos.util.Constants;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.util.JavaHelper;
import org.n52.sos.util.SweHelper;

import com.google.common.collect.Lists;

public class EReportingObservationCreator implements AdditionalObservationCreator {

    private EReportingObservationHelper helper = new EReportingObservationHelper();

    private static final Set<AdditionalObservationCreatorKey> KEYS = AdditionalObservationCreatorRepository
            .encoderKeysForElements(AqdConstants.NS_AQD, EReportingObservation.class, EReportingBlobObservation.class,
                    EReportingBooleanObservation.class, EReportingCategoryObservation.class,
                    EReportingCountObservation.class, EReportingGeometryObservation.class,
                    EReportingNumericObservation.class, EReportingSweDataArrayObservation.class,
                    EReportingTextObservation.class);

    @Override
    public Set<AdditionalObservationCreatorKey> getKeys() {
        return KEYS;
    }

    @Override
    public OmObservation create(OmObservation omObservation, AbstractObservation observation) {
        if (observation instanceof EReportingObservation) {
            for (NamedValue<?> namedValue : helper.createSamplingPointParameter(((EReportingObservation) observation)
                    .getEReportingSeries())) {
                omObservation.addParameter(namedValue);
            }
            // if (omObservation.getValue() instanceof
            // SingleObservationValue<?>) {
            // addQualityFlags((SingleObservationValue<?>)omObservation.getValue(),
            // (EReportingObservation)observation);
            // }
            omObservation.setValue(EReportingHelper.createSweDataArrayValue(omObservation, (EReportingObservation) observation));
            omObservation.getObservationConstellation().setObservationType(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION);
        }
        return omObservation;
    }

    private void addQualityFlags(SingleObservationValue<?> value, EReportingValues observation) {
        value.addQuality(new SosQuality(ElementType.Validation.name(), null, Integer.toString(observation
                .getValidation()), ElementType.Validation.getDefinition(), QualityType.category));
        value.addQuality(new SosQuality(ElementType.Verification.name(), null, Integer.toString(observation
                .getVerification()), ElementType.Verification.getDefinition(), QualityType.category));
    }

}
