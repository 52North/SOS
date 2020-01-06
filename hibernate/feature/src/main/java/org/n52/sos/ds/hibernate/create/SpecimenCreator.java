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
package org.n52.sos.ds.hibernate.create;

import java.net.URI;
import java.util.Locale;

import org.hibernate.Session;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.sos.ds.hibernate.entities.feature.Specimen;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.UoM;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.features.samplingFeatures.SfProcess;
import org.n52.sos.ogc.om.features.samplingFeatures.SfSpecimen;
import org.n52.sos.ogc.om.features.samplingFeatures.SpecLocation;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.w3c.xlink.Reference;
import org.n52.sos.w3c.xlink.Referenceable;

import com.vividsolutions.jts.geom.Geometry;

public class SpecimenCreator extends AbstractFeatureOfInerestCreator<Specimen> {

    public SpecimenCreator(int storageEPSG, int storage3depsg) {
        super(storageEPSG, storage3depsg);
    }

    @Override
    public AbstractFeature create(Specimen f, Locale i18n, String version, Session s) throws OwsExceptionReport {
         AbstractFeature absFeat = createFeature(f, i18n, version, s);
         if (absFeat instanceof SfSpecimen) {
             SfSpecimen specimen = (SfSpecimen)absFeat;
             specimen.setMaterialClass(new ReferenceType(f.getMaterialClass()));
             specimen.setSamplingTime(getSamplingTime(f));
             if (f.isSetSamplingMethod()) {
                 Reference ref = new Reference().setHref(URI.create(f.getSamplingMethod()));
                 Referenceable<SfProcess> of = Referenceable.of(ref);;
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
                 Referenceable<SpecLocation> of = Referenceable.of(ref);;
                 specimen.setCurrentLocation(of);
             }
             if (f.isSetSpecimenType()) {
                 specimen.setSpecimenType(new ReferenceType(f.getSpecimenType()));
             }
         }
         return absFeat;
    }

    private Time getSamplingTime(Specimen s) {
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
    public Geometry createGeometry(Specimen feature, Session session) throws OwsExceptionReport {
        return createGeometryFrom(feature, session);
    }

    @Override
    protected AbstractFeature getFeatureType(CodeWithAuthority identifier) {
        return new SfSpecimen(identifier);
    }

}
