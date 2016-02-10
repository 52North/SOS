/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.entities.observation;

import org.n52.sos.ds.hibernate.entities.observation.series.HibernateSeriesRelations.HasSeries;
import org.n52.sos.ds.hibernate.entities.observation.valued.GeologyLogCoverageValuedObservation;
import org.n52.sos.ogc.om.values.GWGeologyLogCoverage;
import org.n52.sos.ogc.om.values.LogValue;
import org.n52.sos.ogc.swe.DataRecord;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.util.SweHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeologyLogCoverageGeneratorSplitter {
    private static final Logger LOG = LoggerFactory.getLogger(GeologyLogCoverageGeneratorSplitter.class);

    public static GWGeologyLogCoverage create(GeologyLogCoverageValuedObservation entity) {
        GWGeologyLogCoverage coverage = new GWGeologyLogCoverage();
        coverage.setGmlId("glc_" + entity.getObservationId());
        coverage.addValue(createLogValue(entity));
        return coverage;
    }

    private static LogValue createLogValue(GeologyLogCoverageValuedObservation entity) {
        LogValue logValue = new LogValue();
        String uom = "";
        if (entity.isSetDephtUnit()) {
            uom = entity.getDepthunit().getUnit();
        }
        if (entity.isSetFromDepth()) {
            logValue.setFromDepth(SweHelper.createSweQuantity(entity.getFromDepth(), uom));
        }
        if (entity.isSetToDepth()) {
            logValue.setToDepth(SweHelper.createSweQuantity(entity.getToDepth(), uom));
        }
        if (entity.isSetLogValue()) {
            logValue.setValue(createValue(entity));
        }
        return logValue;
    }

    private static DataRecord createValue(GeologyLogCoverageValuedObservation entity) {
        SweDataRecord dataRecord = new SweDataRecord();
        String observedProperty = getObservedProperty(entity);
        dataRecord.setDefinition(observedProperty);
        dataRecord.addField(new SweField(getFieldNameFrom(observedProperty),
                SweHelper.createSweQuantity(entity.getLogValue(), entity.getUnit().getUnit())));
        return dataRecord;
    }

    private static String getObservedProperty(GeologyLogCoverageValuedObservation entity) {
        if (entity instanceof HasSeries) {
            return ((HasSeries) entity).getSeries().getObservableProperty().getIdentifier();
        }
        return "unknown";
    }

    private static String getFieldNameFrom(String observedProperty) {
        return observedProperty.substring(observedProperty.lastIndexOf("/") + 1);
    }

    public static void split(GWGeologyLogCoverage coverage, GeologyLogCoverageValuedObservation entity) {
        LOG.warn("Inserting of GW_GeologyLogCoverages is not yet supported!");
    }

}
