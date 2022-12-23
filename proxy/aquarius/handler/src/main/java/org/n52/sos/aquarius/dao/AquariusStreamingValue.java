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
package org.n52.sos.aquarius.dao;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.n52.series.db.beans.DataEntity;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.StreamingValue;
import org.n52.shetland.ogc.om.TimeValuePair;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.ds.observation.ObservationHelper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings({ "EI_EXPOSE_REP", "EI_EXPOSE_REP2" })
public class AquariusStreamingValue extends StreamingValue<DataEntity<?>> {

    private Iterator<DataEntity<?>> seriesValuesResult;
    private ObservationHelper observationHelper;

    public AquariusStreamingValue(ObservationHelper observationHelper) {
        this.observationHelper = observationHelper;
    }

    public void setResultValues(Iterator<DataEntity<?>> values) {
        this.seriesValuesResult = values;
    }

    public void setResultValues(Collection<DataEntity<?>> values) {
        this.seriesValuesResult = values.iterator();
    }

    @Override
    public DataEntity<?> nextEntity() throws OwsExceptionReport {
        return (DataEntity<?>) seriesValuesResult.next();
    }

    @Override
    public TimeValuePair nextValue() throws OwsExceptionReport {
        if (hasNext()) {
            DataEntity<?> resultObject = seriesValuesResult.next();
            TimeValuePair value = observationHelper.createTimeValuePairFrom(resultObject);
            return value;
        }
        return null;
    }

    @Override
    public OmObservation next() throws NoSuchElementException, OwsExceptionReport {
        OmObservation observation = getObservationTemplate().cloneTemplate();
        DataEntity<?> resultObject = seriesValuesResult.next();
        observationHelper.addValuesToObservation(resultObject, observation, getResponseFormat());
        checkForModifications(observation);
        return observation;
    }

    @Override
    public boolean hasNext() throws OwsExceptionReport {
        return seriesValuesResult != null && seriesValuesResult.hasNext();
    }

    @Override
    protected void queryTimes() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void queryUnit() {
        // TODO Auto-generated method stub

    }
}