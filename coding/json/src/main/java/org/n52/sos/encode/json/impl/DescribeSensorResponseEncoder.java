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
package org.n52.sos.encode.json.impl;

import java.util.List;

import org.apache.xmlbeans.XmlOptions;
import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.encode.json.AbstractSosResponseEncoder;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.ogc.sos.SosProcedureDescriptionUnknowType;
import org.n52.sos.response.DescribeSensorResponse;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.XmlOptionsHelper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class DescribeSensorResponseEncoder extends AbstractSosResponseEncoder<DescribeSensorResponse> {
    public DescribeSensorResponseEncoder() {
        super(DescribeSensorResponse.class, SosConstants.Operations.DescribeSensor);
    }

    @Override
    protected void encodeResponse(ObjectNode json, DescribeSensorResponse t) throws OwsExceptionReport {
        json.put(JSONConstants.PROCEDURE_DESCRIPTION_FORMAT, t.getOutputFormat());
        json.put(JSONConstants.PROCEDURE_DESCRIPTION,
                encodeDescriptions(t.getProcedureDescriptions(), t.getOutputFormat()));

    }

    private String toString(SosProcedureDescription desc, String format) throws OwsExceptionReport {
        if (desc instanceof SosProcedureDescriptionUnknowType && desc.isSetSensorDescriptionXmlString()) {
           return desc.getSensorDescriptionXmlString();
        }
        XmlOptions options = XmlOptionsHelper.getInstance().getXmlOptions();
        return CodingHelper.encodeObjectToXml(format, desc).xmlText(options);
    }

    private JsonNode encodeDescription(SosProcedureDescription desc, String format) throws OwsExceptionReport {
        String xml = toString(desc, format);
        if (desc.isSetValidTime()) {
            ObjectNode j = nodeFactory().objectNode();
            j.put(JSONConstants.VALID_TIME, encodeObjectToJson(desc.getValidTime()));
            j.put(JSONConstants.DESCRIPTION, xml);
            return j;
        } else {
            return nodeFactory().textNode(xml);
        }
    }

    private JsonNode encodeDescriptions(List<SosProcedureDescription> descs, String format) throws OwsExceptionReport {
        if (descs.size() == 1) {
            return encodeDescription(descs.get(0), format);
        } else {
            ArrayNode a = nodeFactory().arrayNode();
            for (SosProcedureDescription desc : descs) {
                a.add(encodeDescription(desc, format));
            }
            return a;
        }
    }
}
