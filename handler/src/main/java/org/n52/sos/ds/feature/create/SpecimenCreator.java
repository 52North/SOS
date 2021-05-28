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
package org.n52.sos.ds.feature.create;

import java.net.URI;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.locationtech.jts.geom.Geometry;
import org.n52.series.db.beans.feature.SpecimenEntity;
import org.n52.shetland.ogc.OGCConstants;
import org.n52.shetland.ogc.UoM;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.CodeWithAuthority;
import org.n52.shetland.ogc.gml.ReferenceType;
import org.n52.shetland.ogc.gml.time.Time;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.om.features.samplingFeatures.SfProcess;
import org.n52.shetland.ogc.om.features.samplingFeatures.SfSpecimen;
import org.n52.shetland.ogc.om.features.samplingFeatures.SpecLocation;
import org.n52.shetland.ogc.om.values.QuantityValue;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.w3c.xlink.Reference;
import org.n52.shetland.w3c.xlink.Referenceable;

public class SpecimenCreator extends AbstractFeatureOfInerestCreator<SpecimenEntity> {

    public SpecimenCreator(FeatureVisitorContext context) {
        super(context);
    }

    @Override
    public AbstractFeature create(SpecimenEntity f) throws OwsExceptionReport {
        AbstractFeature absFeat = createFeature(f);
        if (absFeat instanceof SfSpecimen) {
            SfSpecimen specimen = (SfSpecimen) absFeat;
            specimen.setMaterialClass(new ReferenceType(f.getMaterialClass()));
            specimen.setSamplingTime(getSamplingTime(f));
            if (f.isSetSamplingMethod()) {
                Reference ref = new Reference().setHref(URI.create(f.getSamplingMethod()));
                Referenceable<SfProcess> of = Referenceable.of(ref);
                specimen.setSamplingMethod(of);
            }
            if (f.isSetSize()) {
                QuantityValue size = new QuantityValue(f.getSize());
                if (f.isSetSizeUnit()) {
                    UoM uom = new UoM(f.getSizeUnit().getUnit());
                    if (f.getSizeUnit().isSetName()) {
                        uom.setName(f.getSizeUnit().getName());
                    }
                    if (f.getSizeUnit().isSetLink()) {
                        uom.setLink(f.getSizeUnit().getLink());
                    }
                    size.setUnit(uom);
                } else {
                    size.setUnit(OGCConstants.UNKNOWN);
                }
                specimen.setSize(size);
            }
            if (f.isSetCurrentLocation()) {
                Reference ref = new Reference().setHref(URI.create(f.getCurrentLocation()));
                Referenceable<SpecLocation> of = Referenceable.of(ref);
                specimen.setCurrentLocation(of);
            }
            if (f.isSetSpecimenType()) {
                specimen.setSpecimenType(new ReferenceType(f.getSpecimenType()));
            }
        }
        return absFeat;
    }

    private Time getSamplingTime(SpecimenEntity s) {
        final DateTime phenStartTime = new DateTime(s.getSamplingTimeStart(), DateTimeZone.UTC);
        DateTime phenEndTime;
        if (s.getSamplingTimeEnd() != null) {
            phenEndTime = new DateTime(s.getSamplingTimeEnd(), DateTimeZone.UTC);
        } else {
            phenEndTime = phenStartTime;
        }
        return createTime(phenStartTime, phenEndTime);
    }

    private Time createTime(DateTime start, DateTime end) {
        if (start.equals(end)) {
            return new TimeInstant(start);
        } else {
            return new TimePeriod(start, end);
        }
    }

    @Override
    public Geometry createGeometry(SpecimenEntity feature) throws OwsExceptionReport {
        return createGeometryFrom(feature);
    }

    @Override
    protected AbstractFeature createFeature(CodeWithAuthority identifier) {
        return new SfSpecimen(identifier);
    }

}
