/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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

import java.math.BigDecimal;

import org.n52.series.db.beans.parameter.ParameterEntity;
import org.n52.series.db.beans.parameter.observation.ObservationBooleanParameterEntity;
import org.n52.series.db.beans.parameter.observation.ObservationCategoryParameterEntity;
import org.n52.series.db.beans.parameter.observation.ObservationCountParameterEntity;
import org.n52.series.db.beans.parameter.observation.ObservationParameterEntity;
import org.n52.series.db.beans.parameter.observation.ObservationQuantityParameterEntity;
import org.n52.series.db.beans.parameter.observation.ObservationTextParameterEntity;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.ds.hibernate.util.AbstractParameterFactory;

public class ObservationParameterFactory extends AbstractParameterFactory<ObservationParameterEntity<?>> {

    protected ObservationParameterFactory() {
    }

    @Override
    public ParameterEntity<Boolean> truth() throws OwsExceptionReport {
        return new ObservationBooleanParameterEntity();
    }

    @Override
    public ParameterEntity<String> category() throws OwsExceptionReport {
        return new ObservationCategoryParameterEntity();
    }

    @Override
    public ParameterEntity<Integer> count() throws OwsExceptionReport {
        return new ObservationCountParameterEntity();
    }

    @Override
    public ParameterEntity<BigDecimal> quantity() throws OwsExceptionReport {
        return new ObservationQuantityParameterEntity();
    }

    @Override
    public ParameterEntity<String> text() throws OwsExceptionReport {
        return new ObservationTextParameterEntity();
    }

    public static ObservationParameterFactory getInstance() {
        return Holder.INSTANCE;
    }

    private static final class Holder {
        private static final ObservationParameterFactory INSTANCE = new ObservationParameterFactory();

        private Holder() {
        }
    }

}
