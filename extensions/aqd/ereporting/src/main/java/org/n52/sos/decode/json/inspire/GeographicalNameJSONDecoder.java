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

import org.n52.sos.inspire.aqd.GeographicalName;
import org.n52.sos.inspire.aqd.Pronunciation;
import org.n52.sos.inspire.aqd.Spelling;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.AQDJSONConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;


public class GeographicalNameJSONDecoder  extends AbstractJSONDecoder<GeographicalName>{

    private static final Logger log = LoggerFactory.getLogger(GeographicalNameJSONDecoder.class);

    public GeographicalNameJSONDecoder() {
        super(GeographicalName.class);
    }

    @Override
    public GeographicalName decodeJSON(JsonNode node, boolean validate)
            throws OwsExceptionReport {
        GeographicalName geographicalName = new GeographicalName();
        geographicalName.setGrammaticalGender(parseNillableCodeType(node
                .path(AQDJSONConstants.GRAMMATICAL_GENDER)));
        geographicalName.setGrammaticalNumber(parseNillableCodeType(node
                .path(AQDJSONConstants.GRAMMATICAL_NUMBER)));
        geographicalName.setLanguage(parseNillableString(node
                .path(AQDJSONConstants.LANGUAGE)));
        geographicalName.setNameStatus(parseNillableCodeType(node
                .path(AQDJSONConstants.NAME_STATUS)));
        geographicalName.setNativeness(parseNillableCodeType(node
                .path(AQDJSONConstants.NATIVENESS)));
        geographicalName.setSourceOfName(parseNillableString(node
                .path(AQDJSONConstants.SOURCE_OF_NAME)));
        geographicalName.setPronunciation(decodeJsonToNillable(node
                .path(AQDJSONConstants.PRONUNCIATION), Pronunciation.class));
        for (JsonNode n : node.path(AQDJSONConstants.SPELLING)) {
            geographicalName.addSpelling(decodeJsonToObject(n, Spelling.class));
        }
        return geographicalName;
    }
}
