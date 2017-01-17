/*
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.opengis.sos.x20.GetObservationByIdResponseDocument;
import net.opengis.sos.x20.GetObservationByIdResponseType;

import org.apache.xmlbeans.XmlObject;

import org.n52.shetland.ogc.gml.CodeWithAuthority;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.response.GetObservationByIdResponse;
import org.n52.shetland.w3c.SchemaLocation;
import org.n52.sos.coding.encode.ObservationEncoder;
import org.n52.sos.util.XmlHelper;
import org.n52.svalbard.HelperValues;
import org.n52.svalbard.SosHelperValues;
import org.n52.svalbard.encode.EncodingContext;
import org.n52.svalbard.encode.exception.EncodingException;

import com.google.common.collect.Sets;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 *
 * @since 4.0.0
 */
public class GetObservationByIdResponseEncoder extends AbstractObservationResponseEncoder<GetObservationByIdResponse> {
    public static final String GML_ID = "sf_1";

    public GetObservationByIdResponseEncoder() {
        super(SosConstants.Operations.GetObservationById.name(), GetObservationByIdResponse.class);
    }

    @Override
    protected XmlObject createResponse(ObservationEncoder<XmlObject, OmObservation> encoder,
                                       GetObservationByIdResponse response) throws EncodingException {
        GetObservationByIdResponseDocument doc =
                GetObservationByIdResponseDocument.Factory.newInstance(getXmlOptions());
        GetObservationByIdResponseType xbResponse = doc.addNewGetObservationByIdResponse();
        List<OmObservation> oc = response.getObservationCollection();
        HashMap<CodeWithAuthority, String> gmlID4sfIdentifier = new HashMap<>(oc.size());
        for (OmObservation observation : oc) {
            EncodingContext codingContext = EncodingContext.empty();
            Map<HelperValues, String> foiHelper = new HashMap<>(2);
            final String gmlId;
            CodeWithAuthority foiId = observation.getObservationConstellation().getFeatureOfInterest().getIdentifierCodeWithAuthority();
            if (gmlID4sfIdentifier.containsKey(foiId)) {
                gmlId = gmlID4sfIdentifier.get(foiId);
                codingContext = codingContext.with(SosHelperValues.EXIST_FOI_IN_DOC, true);
            } else {
                gmlId = GML_ID;
                gmlID4sfIdentifier.put(foiId, gmlId);
                codingContext = codingContext.with(SosHelperValues.EXIST_FOI_IN_DOC, false);
            }
            codingContext = codingContext.with(SosHelperValues.GMLID, gmlId);
            xbResponse.addNewObservation().addNewOMObservation().set(encoder.encode(observation, codingContext));
        }
        XmlHelper.makeGmlIdsUnique(xbResponse.getDomNode());
        return doc;
    }

    @Override
    public Set<SchemaLocation> getConcreteSchemaLocations() {
        return Sets.newHashSet(Sos2Constants.SOS_GET_OBSERVATION_BY_ID_SCHEMA_LOCATION);
    }
}
