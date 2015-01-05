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
package org.n52.sos.decode.json.inspire;

import org.n52.sos.inspire.aqd.EReportingChange;
import org.n52.sos.inspire.aqd.EReportingHeader;
import org.n52.sos.inspire.aqd.InspireID;
import org.n52.sos.inspire.aqd.RelatedParty;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.AQDJSONConstants;

import com.fasterxml.jackson.databind.JsonNode;

public class EReportingHeaderJSONDecoder extends AbstractJSONDecoder<EReportingHeader> {

    public EReportingHeaderJSONDecoder() {
        super(EReportingHeader.class);
    }

    @Override
    public EReportingHeader decodeJSON(JsonNode node, boolean validate)
            throws OwsExceptionReport {
        EReportingHeader header = new EReportingHeader();
        header.setChange(decodeJsonToObject(node.path(AQDJSONConstants.CHANGE), EReportingChange.class));
        header.setInspireID(decodeJsonToObject(node.path(AQDJSONConstants.INSPIRE_ID), InspireID.class));
        header.setReportingAuthority(decodeJsonToObject(node.path(AQDJSONConstants.REPORTING_AUTHORITY), RelatedParty.class));
        header.setReportingPeriod(parseReferenceableTime(node.path(AQDJSONConstants.REPORTING_PERIOD)));
        for (JsonNode child : node.path(AQDJSONConstants.CONTENT)) {
            header.addContent(decodeJsonToReferencable(child, AbstractFeature.class));
        }
        for (JsonNode child : node.path(AQDJSONConstants.DELETE)) {
            header.addDelete(decodeJsonToReferencable(child, AbstractFeature.class));
        }
        return header;
    }

}
