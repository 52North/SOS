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
import java.util.Locale;

import org.hibernate.Session;

import org.n52.sos.convert.ConverterException;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.EReportingSeries;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.values.NilTemplateValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosProcedureDescription;

import com.google.common.collect.Lists;

public class EReportingSeriesOmObservationCreator extends SeriesOmObservationCreator {
    
    
    public EReportingSeriesOmObservationCreator(EReportingSeries series, String version, Session session) {
        super(series, version, session);
    }

    public EReportingSeriesOmObservationCreator(EReportingSeries series, String version, Locale language, Session session) {
        super(series, version, language, session);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<OmObservation> create() throws OwsExceptionReport, ConverterException {
        final List<OmObservation> observations = Lists.newLinkedList();
        if (series != null) {
            SosProcedureDescription procedure = createProcedure(series.getProcedure().getIdentifier());
            OmObservableProperty obsProp = createObservableProperty(series.getObservableProperty());
            AbstractFeature feature = createFeatureOfInterest(series.getFeatureOfInterest().getIdentifier());

            final OmObservationConstellation obsConst = getObservationConstellation(procedure, obsProp, feature);

            final OmObservation sosObservation = new OmObservation();
            sosObservation.setNoDataValue(getNoDataValue());
            sosObservation.setTokenSeparator(getTokenSeparator());
            sosObservation.setTupleSeparator(getTupleSeparator());
            sosObservation.setObservationConstellation(obsConst);
            // set or add???
            sosObservation.setParameter(new EReportingObservationHelper().createSamplingPointParameter((EReportingSeries)series));
            final NilTemplateValue value = new NilTemplateValue();
            value.setUnit(obsProp.getUnit());
            sosObservation
                    .setValue(new SingleObservationValue(new TimeInstant(), value));
            observations.add(sosObservation);
        }
        return observations;
    }
}
