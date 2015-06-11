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
package org.n52.sos.web.admin;

import java.util.Iterator;

import org.n52.sos.aqd.ReportObligationType;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.decode.Decoder;
import org.n52.sos.decode.JsonDecoderKey;
import org.n52.sos.encode.Encoder;
import org.n52.sos.encode.json.JSONEncoderKey;
import org.n52.sos.inspire.aqd.RelatedParty;
import org.n52.sos.inspire.aqd.ReportObligation;
import org.n52.sos.inspire.aqd.ReportObligationRepository;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.AQDJSONConstants;
import org.n52.sos.util.JSONUtils;
import org.n52.sos.web.AbstractController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
@Controller
@RequestMapping(value = "/admin/ereporting")
public class AdminEReportingHeaderController extends AbstractController {

    private static final Logger LOG = LoggerFactory
            .getLogger(AdminEReportingHeaderController.class);

    @RequestMapping(method = RequestMethod.GET)
    public String get() {
        return "admin/ereporting";
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET,
                    produces = "application/json")
    public String getJSON() throws OwsExceptionReport {
        ObjectNode node = JSONUtils.nodeFactory().objectNode();
        ReportObligationRepository reportObligationRepository = ReportObligationRepository.getInstance();
        CodingRepository codingRepository = CodingRepository.getInstance();
        Encoder<JsonNode, ReportObligation> reportObligationEncoder = codingRepository.getEncoder(new JSONEncoderKey(ReportObligation.class));
        Encoder<JsonNode, RelatedParty> relatedPartyEncoder = codingRepository.getEncoder(new JSONEncoderKey(RelatedParty.class));
        node.put(AQDJSONConstants.REPORTING_AUTHORITY, relatedPartyEncoder.encode(reportObligationRepository.getReportingAuthority()));
        ArrayNode ros = node.putArray(AQDJSONConstants.REPORT_OBLIGATIONS);
        for (ReportObligationType reportObligationType : ReportObligationType.values()) {
            ReportObligation reportObligation = reportObligationRepository.getReportObligation(reportObligationType);
            ros.addObject().put(AQDJSONConstants.ID, reportObligationType.name())
                    .put(AQDJSONConstants.NAME, reportObligationType.getTitle())
                    .put(AQDJSONConstants.DESCRIPTION, reportObligationType.getDescription())
                    .put(AQDJSONConstants.VALUE, reportObligationEncoder.encode(reportObligation));
        }
        return JSONUtils.print(node);
    }

    @RequestMapping(method = RequestMethod.POST,
                    consumes = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void save(@RequestBody String json) throws OwsExceptionReport {
        LOG.info("Saving {}", json);
        ReportObligationRepository reportObligationRepository = ReportObligationRepository.getInstance();
        CodingRepository codingRepository = CodingRepository.getInstance();
        Decoder<ReportObligation, JsonNode> reportObligationDecoder = codingRepository.getDecoder(new JsonDecoderKey(ReportObligation.class));
        Decoder<RelatedParty, JsonNode> relatedPartyDecoder = codingRepository.getDecoder(new JsonDecoderKey(RelatedParty.class));

        JsonNode node = JSONUtils.loadString(json);

        RelatedParty relatedParty = relatedPartyDecoder.decode(node.path(AQDJSONConstants.REPORTING_AUTHORITY));
        reportObligationRepository.saveReportingAuthority(relatedParty);


        JsonNode obligations = node.path(AQDJSONConstants.REPORT_OBLIGATIONS);
        Iterator<String> it = obligations.fieldNames();
        while (it.hasNext()) {
            String id = it.next();
            ReportObligation reportObligation = reportObligationDecoder.decode(obligations.path(id));
            reportObligationRepository.saveReportObligation(ReportObligationType.valueOf(id), reportObligation);
        }
    }

}
