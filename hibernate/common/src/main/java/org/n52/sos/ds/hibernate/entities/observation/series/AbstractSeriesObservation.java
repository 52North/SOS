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
package org.n52.sos.ds.hibernate.entities.observation.series;

import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.feature.AbstractFeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.observation.AbstractObservation;

/**
 * Abstract implementation of {@link SeriesObservation}.
 *
 * @author Christian Autermann
 * @param <T> the value type
 */
public abstract class AbstractSeriesObservation<T>
        extends AbstractObservation<T>
        implements SeriesObservation<T> {

    private static final long serialVersionUID = -1173799550126124321L;
    private Series series;
    private long seriesid;
    
    @Override
    public long getSeriesId() {
        return seriesid;
    }
    
    @Override
    public void setSeriesId(long seriesid) {
        this.seriesid =  seriesid;
    }

    @Override
    public Series getSeries() {
        return series;
    }

    @Override
    public void setSeries(Series series) {
        this.series = series;
    }

    @Override
    public boolean isSetSeries() {
        return getSeries() != null;
    }

    @Override
    public AbstractFeatureOfInterest getFeatureOfInterest() {
        return isSetSeries() ? getSeries().getFeatureOfInterest() : null;
    }

    @Override
    public ObservableProperty getObservableProperty() {
        return isSetSeries() ? getSeries().getObservableProperty() : null;
    }

    @Override
    public Procedure getProcedure() {
        return isSetSeries() ? getSeries().getProcedure() : null;
    }

    @Override
    public Offering getOffering() {
        return isSetSeries() ? getSeries().getOffering() : null;
    }
    
}
