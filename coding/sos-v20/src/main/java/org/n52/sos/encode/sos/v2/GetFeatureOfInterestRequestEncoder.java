/*
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
package org.n52.sos.encode.sos.v2;

import java.util.Set;

import net.opengis.fes.x20.SpatialOpsDocument;
import net.opengis.sos.x20.GetFeatureOfInterestDocument;
import net.opengis.sos.x20.GetFeatureOfInterestType;

import org.apache.xmlbeans.XmlObject;

import org.n52.svalbard.decode.Decoder;
import org.n52.svalbard.encode.exception.EncodingException;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.filter.SpatialFilter;
import org.n52.shetland.w3c.SchemaLocation;
import org.n52.sos.encode.swes.ExtensibleRequestEncoder;
import org.n52.sos.request.GetFeatureOfInterestRequest;

import com.google.common.collect.Sets;

/**
 * XML {@link Decoder} for {@link GetFeatureOfInterestRequest}
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 5.0.0
 *
 */
public class GetFeatureOfInterestRequestEncoder extends AbstractSosRequestEncoder<GetFeatureOfInterestRequest> implements ExtensibleRequestEncoder {

    public GetFeatureOfInterestRequestEncoder() {
        super(SosConstants.Operations.GetFeatureOfInterest.name(), GetFeatureOfInterestRequest.class);
    }

    @Override
    protected Set<SchemaLocation> getConcreteSchemaLocations() {
        return Sets.newHashSet(Sos2Constants.SOS_GET_OBSERVATION_SCHEMA_LOCATION);
    }

    @Override
    protected XmlObject create(GetFeatureOfInterestRequest request) throws EncodingException {
        GetFeatureOfInterestDocument doc = GetFeatureOfInterestDocument.Factory.newInstance(getXmlOptions());
        GetFeatureOfInterestType gfoit = doc.addNewGetFeatureOfInterest();
        addService(gfoit, request);
        addVersion(gfoit, request);
        addExtension(gfoit, request);
        addProcedure(gfoit, request);
        addObservedProperty(gfoit, request);
        addFeatureOfInterest(gfoit, request);
        addSpatialFilters(gfoit, request);
        return doc;
    }

    private void addProcedure(GetFeatureOfInterestType gfoit, GetFeatureOfInterestRequest request) {
        if (request.isSetProcedures()) {
            request.getProcedures().forEach(gfoit::addProcedure);
        }
    }
    private void addObservedProperty(GetFeatureOfInterestType gfoit, GetFeatureOfInterestRequest request) {
        if (request.isSetObservableProperties()) {
            request.getObservedProperties().forEach(gfoit::addObservedProperty);
        }
    }

    private void addFeatureOfInterest(GetFeatureOfInterestType gfoit, GetFeatureOfInterestRequest request) {
        if (request.isSetFeatureOfInterestIdentifiers()) {
            request.getFeatureIdentifiers().forEach(gfoit::addFeatureOfInterest);
        }
    }

    private void addSpatialFilters(GetFeatureOfInterestType got, GetFeatureOfInterestRequest request) throws EncodingException {
        if (request.isSetSpatialFilters()) {
            for (SpatialFilter spatialFilter : request.getSpatialFilters()) {
                // TODO fixme
                XmlObject encodeFes = encodeFes(spatialFilter);
                if (encodeFes instanceof SpatialOpsDocument) {
                    substitute(got.addNewSpatialFilter().getSpatialOps(), ((SpatialOpsDocument) encodeFes).getSpatialOps());
                }
            }
        }
    }

}
