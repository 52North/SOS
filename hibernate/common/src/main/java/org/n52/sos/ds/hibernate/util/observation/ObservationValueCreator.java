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
package org.n52.sos.ds.hibernate.util.observation;

import org.n52.series.db.beans.BlobDataEntity;
import org.n52.series.db.beans.BooleanDataEntity;
import org.n52.series.db.beans.CategoryDataEntity;
import org.n52.series.db.beans.ComplexDataEntity;
import org.n52.series.db.beans.CountDataEntity;
import org.n52.series.db.beans.DataArrayDataEntity;
import org.n52.series.db.beans.GeometryDataEntity;
import org.n52.series.db.beans.ProfileDataEntity;
import org.n52.series.db.beans.QuantityDataEntity;
import org.n52.series.db.beans.ReferencedDataEntity;
import org.n52.series.db.beans.TextDataEntity;
import org.n52.shetland.ogc.gml.ReferenceType;
import org.n52.shetland.ogc.om.values.BooleanValue;
import org.n52.shetland.ogc.om.values.CategoryValue;
import org.n52.shetland.ogc.om.values.ComplexValue;
import org.n52.shetland.ogc.om.values.CountValue;
import org.n52.shetland.ogc.om.values.GeometryValue;
import org.n52.shetland.ogc.om.values.ProfileValue;
import org.n52.shetland.ogc.om.values.QuantityValue;
import org.n52.shetland.ogc.om.values.ReferenceValue;
import org.n52.shetland.ogc.om.values.SweDataArrayValue;
import org.n52.shetland.ogc.om.values.TextValue;
import org.n52.shetland.ogc.om.values.UnknownValue;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.swe.SweDataRecord;
import org.n52.svalbard.decode.DecoderRepository;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class ObservationValueCreator extends AbstractObservationValueCreator {

    public ObservationValueCreator(DecoderRepository decoderRepository) {
        super(decoderRepository);
    }

    @Override
    public QuantityValue visit(QuantityDataEntity o) {
        return visit(o, new QuantityValue(o.hasValue() ? o.getValue().doubleValue() : null));
    }

    @Override
    public UnknownValue visit(BlobDataEntity o) {
        return visit(o, new UnknownValue(o.getValue()));
    }

    @Override
    public BooleanValue visit(BooleanDataEntity o) {
        return visit(o, new BooleanValue(o.getValue()));
    }

    @Override
    public CategoryValue visit(CategoryDataEntity o) {
        return visit(o, new CategoryValue(o.getValue()));
    }

    @Override
    public ComplexValue visit(ComplexDataEntity o) throws OwsExceptionReport {
        SweAbstractDataComponentCreator visitor = new SweAbstractDataComponentCreator(getDecoderRepository());
        SweDataRecord record = visitor.visit(o);
        return new ComplexValue(record);
    }

    @Override
    public CountValue visit(CountDataEntity o) {
        return new CountValue(o.getValue());
    }

    @Override
    public GeometryValue visit(GeometryDataEntity o) throws OwsExceptionReport {
        return visit(o, new GeometryValue(o.hasValue() ? o.getValue().getGeometry() : null));
    }

    @Override
    public TextValue visit(TextDataEntity o) {
        return visit(o, new TextValue(o.getValue()));
    }

    @Override
    public SweDataArrayValue visit(DataArrayDataEntity o) throws OwsExceptionReport {
        return new SweDataArrayValue(createSweDataArray(o));
    }

    @Override
    public ProfileValue visit(ProfileDataEntity o) throws OwsExceptionReport {
        return new ProfileGeneratorSplitter(this).create(o);
    }

    @Override
    public ReferenceValue visit(ReferencedDataEntity o) {
        return visit(o, new ReferenceValue(new ReferenceType(o.getValue())));
    }

}
