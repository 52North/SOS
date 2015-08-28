/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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

import org.apache.xmlbeans.XmlObject;
import org.n52.iceland.exception.ows.OwsExceptionReport;
import org.n52.iceland.ogc.sos.Sos2Constants;
import org.n52.iceland.ogc.sos.SosConstants;
import org.n52.iceland.w3c.SchemaLocation;
import org.n52.sos.encode.swes.ExtensibleRequestEncoder;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.request.GetObservationRequest;

import com.google.common.collect.Sets;

import net.opengis.sos.x20.GetObservationDocument;
import net.opengis.sos.x20.GetObservationType;

/**
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 5.0.0
 *
 */
public class GetObservationRequestEncoder extends AbstractSosRequestEncoder<GetObservationRequest> implements ExtensibleRequestEncoder {

    public GetObservationRequestEncoder() {
        super(SosConstants.Operations.GetObservation.name(), GetObservationRequest.class);
    }

    @Override
    protected Set<SchemaLocation> getConcreteSchemaLocations() {
        return Sets.newHashSet(Sos2Constants.SOS_GET_OBSERVATION_SCHEMA_LOCATION);
    }

    @Override
    protected XmlObject create(GetObservationRequest request) throws OwsExceptionReport {
        GetObservationDocument doc = GetObservationDocument.Factory.newInstance(getXmlOptions());
        GetObservationType got = doc.addNewGetObservation();
        addService(got, request);
        addVersion(got, request);
        addExtension(got, request);
        addProcedure(got, request);
        addOffering(got, request);
        addObservedProperty(got, request);
        addTemporalFilter(got, request);
        addFeatureOfInterest(got, request);
        addSpatialFilter(got, request);
        return doc;
    }

    private void addProcedure(GetObservationType got, GetObservationRequest request) {
        if (request.isSetProcedure()) {
            for (String procedure : request.getProcedures()) {
                got.addProcedure(procedure);
            }
        }
    }

    private void addOffering(GetObservationType got, GetObservationRequest request) {
        if (request.isSetOffering()) {
            for (String offering : request.getOfferings()) {
                got.addOffering(offering);
            }
        }
        
    }

    private void addObservedProperty(GetObservationType got, GetObservationRequest request) {
        if (request.isSetObservableProperty()) {
            for (String observedProperty : request.getObservedProperties()) {
                got.addObservedProperty(observedProperty);
            }
        }
    }

    private void addTemporalFilter(GetObservationType got, GetObservationRequest request) throws OwsExceptionReport {
        if (request.isSetTemporalFilter()) {
            for (TemporalFilter temporalFilter : request.getTemporalFilters()) {
                got.addNewTemporalFilter().addNewTemporalOps().set(encodeFes(temporalFilter));
            }
        }
    }

    private void addFeatureOfInterest(GetObservationType got, GetObservationRequest request) {
        if (request.isSetFeatureOfInterest()) {
            for (String featureOfInterest : request.getFeatureIdentifiers()) {
                got.addFeatureOfInterest(featureOfInterest);
            }
        }
    }

    private void addSpatialFilter(GetObservationType got, GetObservationRequest request) throws OwsExceptionReport {
        if (request.isSetSpatialFilter()) {
            got.addNewSpatialFilter().addNewSpatialOps().set(encodeFes(request.getSpatialFilter()));
        }
    }

}
