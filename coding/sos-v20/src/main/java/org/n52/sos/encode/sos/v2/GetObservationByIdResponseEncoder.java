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

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.opengis.sos.x20.GetObservationByIdResponseDocument;
import net.opengis.sos.x20.GetObservationByIdResponseType;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.encode.ObservationEncoder;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.response.GetObservationByIdResponse;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.w3c.SchemaLocation;

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
            GetObservationByIdResponse response) throws OwsExceptionReport {
        GetObservationByIdResponseDocument doc =
                GetObservationByIdResponseDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        GetObservationByIdResponseType xbResponse = doc.addNewGetObservationByIdResponse();
        List<OmObservation> oc = response.getObservationCollection();
        HashMap<CodeWithAuthority, String> gmlID4sfIdentifier = new HashMap<CodeWithAuthority, String>(oc.size());
        for (OmObservation observation : oc) {
            Map<HelperValues, String> foiHelper = new EnumMap<HelperValues, String>(HelperValues.class);
            final String gmlId;
            CodeWithAuthority foiId = observation.getObservationConstellation().getFeatureOfInterest().getIdentifierCodeWithAuthority();
            if (gmlID4sfIdentifier.containsKey(foiId)) {
                gmlId = gmlID4sfIdentifier.get(foiId);
                foiHelper.put(HelperValues.EXIST_FOI_IN_DOC, Boolean.toString(true));
            } else {
                gmlId = GML_ID;
                gmlID4sfIdentifier.put(foiId, gmlId);
                foiHelper.put(HelperValues.EXIST_FOI_IN_DOC, Boolean.toString(false));
            }
            foiHelper.put(HelperValues.GMLID, gmlId);
            xbResponse.addNewObservation().addNewOMObservation().set(encoder.encode(observation, foiHelper));
        }
        XmlHelper.makeGmlIdsUnique(xbResponse.getDomNode());
        return doc;
    }

    @Override
    public Set<SchemaLocation> getConcreteSchemaLocations() {
        return Sets.newHashSet(Sos2Constants.SOS_GET_OBSERVATION_BY_ID_SCHEMA_LOCATION);
    }
}
