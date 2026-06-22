/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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

import org.n52.series.db.beans.BlobDataEntity;
import org.n52.series.db.beans.BooleanDataEntity;
import org.n52.series.db.beans.CategoryDataEntity;
import org.n52.series.db.beans.ComplexDataEntity;
import org.n52.series.db.beans.CountDataEntity;
import org.n52.series.db.beans.DataArrayDataEntity;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.GeometryDataEntity;
import org.n52.series.db.beans.ProfileDataEntity;
import org.n52.series.db.beans.QuantityDataEntity;
import org.n52.series.db.beans.ReferencedDataEntity;
import org.n52.series.db.beans.TextDataEntity;
import org.n52.series.db.beans.TrajectoryDataEntity;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.sos.ds.hibernate.util.observation.ObservationVisitor;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public final class ObservationTypeObservationVisitor implements ObservationVisitor<String> {

    private ObservationTypeObservationVisitor() {
    }

    public String visit(DataEntity o) {
        if (o instanceof QuantityDataEntity entity11) {
            return visit(entity11);
        } else if (o instanceof BlobDataEntity entity10) {
            return visit(entity10);
        } else if (o instanceof BooleanDataEntity entity9) {
            return visit(entity9);
        } else if (o instanceof CategoryDataEntity entity8) {
            return visit(entity8);
        } else if (o instanceof ComplexDataEntity entity7) {
            return visit(entity7);
        } else if (o instanceof CountDataEntity entity6) {
            return visit(entity6);
        } else if (o instanceof DataArrayDataEntity entity5) {
            return visit(entity5);
        } else if (o instanceof GeometryDataEntity entity4) {
            return visit(entity4);
        } else if (o instanceof TextDataEntity entity3) {
            return visit(entity3);
        } else if (o instanceof ProfileDataEntity entity2) {
            return visit(entity2);
        } else if (o instanceof TrajectoryDataEntity entity1) {
            return visit(entity1);
        } else if (o instanceof ReferencedDataEntity entity) {
            return visit(entity);
        }
        return null;
    }

    @Override
    public String visit(QuantityDataEntity o) {
        return OmConstants.OBS_TYPE_MEASUREMENT;
    }

    @Override
    public String visit(BlobDataEntity o) {
        return OmConstants.OBS_TYPE_UNKNOWN;
    }

    @Override
    public String visit(BooleanDataEntity o) {
        return OmConstants.OBS_TYPE_TRUTH_OBSERVATION;
    }

    @Override
    public String visit(CategoryDataEntity o) {
        return OmConstants.OBS_TYPE_CATEGORY_OBSERVATION;
    }

    @Override
    public String visit(ComplexDataEntity o) {
        return OmConstants.OBS_TYPE_COMPLEX_OBSERVATION;
    }

    @Override
    public String visit(CountDataEntity o) {
        return OmConstants.OBS_TYPE_COUNT_OBSERVATION;
    }

    @Override
    public String visit(GeometryDataEntity o) {
        return OmConstants.OBS_TYPE_GEOMETRY_OBSERVATION;
    }

    @Override
    public String visit(TextDataEntity o) {
        return OmConstants.OBS_TYPE_TEXT_OBSERVATION;
    }

    @Override
    public String visit(DataArrayDataEntity o) {
        return OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION;
    }

    @Override
    public String visit(ProfileDataEntity o) {
        return OmConstants.OBS_TYPE_PROFILE_OBSERVATION;
    }

    @Override
    public String visit(TrajectoryDataEntity o) {
        return OmConstants.OBS_TYPE_TRAJECTORY_OBSERVATION;
    }

    @Override
    public String visit(ReferencedDataEntity o) {
        return OmConstants.OBS_TYPE_REFERENCE_OBSERVATION;
    }

    public static ObservationTypeObservationVisitor getInstance() {
        return Holder.INSTANCE;
    }

    private static final class Holder {
        private static final ObservationTypeObservationVisitor INSTANCE = new ObservationTypeObservationVisitor();

        private Holder() {
        }
    }

}
