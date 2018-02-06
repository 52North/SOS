/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.dao.observation;

import org.n52.series.db.beans.data.Data;
import org.n52.series.db.beans.data.Data.BlobData;
import org.n52.series.db.beans.data.Data.BooleanData;
import org.n52.series.db.beans.data.Data.CategoryData;
import org.n52.series.db.beans.data.Data.ComplexData;
import org.n52.series.db.beans.data.Data.CountData;
import org.n52.series.db.beans.data.Data.DataArrayData;
import org.n52.series.db.beans.data.Data.GeometryData;
import org.n52.series.db.beans.data.Data.ProfileData;
import org.n52.series.db.beans.data.Data.QuantityData;
import org.n52.series.db.beans.data.Data.ReferencedData;
import org.n52.series.db.beans.data.Data.TextData;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.sos.ds.hibernate.util.observation.ObservationVisitor;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class ObservationTypeObservationVisitor implements ObservationVisitor<String> {

    private ObservationTypeObservationVisitor() {
    }

    public String visit(Data o) {
       if (o instanceof QuantityData) {
           return visit((QuantityData)o);
       } else if (o instanceof BlobData) {
           return visit((BlobData)o);
       } else if (o instanceof BooleanData) {
           return visit((BooleanData)o);
       } else if (o instanceof CategoryData) {
           return visit((CategoryData)o);
       } else if (o instanceof ComplexData) {
           return visit((ComplexData)o);
       } else if (o instanceof CountData) {
           return visit((CountData)o);
       } else if (o instanceof GeometryData) {
           return visit((GeometryData)o);
       } else if (o instanceof TextData) {
           return visit((TextData)o);
       } else if (o instanceof ProfileData) {
           return visit((ProfileData)o);
       } else if (o instanceof ReferencedData) {
           return visit((ReferencedData)o);
       }
       return null;
    }

    @Override
    public String visit(QuantityData o) {
        return OmConstants.OBS_TYPE_MEASUREMENT;
    }

    @Override
    public String visit(BlobData o) {
        return OmConstants.OBS_TYPE_UNKNOWN;
    }

    @Override
    public String visit(BooleanData o) {
        return OmConstants.OBS_TYPE_TRUTH_OBSERVATION;
    }

    @Override
    public String visit(CategoryData o) {
        return OmConstants.OBS_TYPE_CATEGORY_OBSERVATION;
    }

    @Override
    public String visit(ComplexData o) {
        return OmConstants.OBS_TYPE_COMPLEX_OBSERVATION;
    }

    @Override
    public String visit(CountData o) {
        return OmConstants.OBS_TYPE_COUNT_OBSERVATION;
    }

    @Override
    public String visit(GeometryData o) {
        return OmConstants.OBS_TYPE_GEOMETRY_OBSERVATION;
    }

    @Override
    public String visit(TextData o) {
        return OmConstants.OBS_TYPE_TEXT_OBSERVATION;
    }

    @Override
    public String visit(DataArrayData o) {
        return OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION;
    }

    @Override
    public String visit(ProfileData o) {
        return OmConstants.OBS_TYPE_PROFILE_OBSERVATION;
    }

    @Override
    public String visit(ReferencedData o)  {
        return OmConstants.OBS_TYPE_REFERENCE_OBSERVATION;
    }

    public static ObservationTypeObservationVisitor getInstance() {
        return Holder.INSTANCE;
    }

    private static final class Holder {
        private static final ObservationTypeObservationVisitor INSTANCE
                = new ObservationTypeObservationVisitor();

        private Holder() {
        }
    }

}
