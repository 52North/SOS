/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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
package org.n52.sos.aqd.web.admin;

import java.util.Iterator;

import javax.inject.Inject;

import org.n52.janmayen.Json;
import org.n52.shetland.aqd.EReportObligationRepository;
import org.n52.shetland.aqd.ReportObligation;
import org.n52.shetland.aqd.ReportObligationType;
import org.n52.shetland.inspire.base2.RelatedParty;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.web.common.AbstractController;
import org.n52.svalbard.coding.json.AQDJSONConstants;
import org.n52.svalbard.decode.Decoder;
import org.n52.svalbard.decode.DecoderRepository;
import org.n52.svalbard.decode.JsonDecoderKey;
import org.n52.svalbard.decode.exception.DecodingException;
import org.n52.svalbard.encode.Encoder;
import org.n52.svalbard.encode.EncoderRepository;
import org.n52.svalbard.encode.exception.EncodingException;
import org.n52.svalbard.encode.json.JSONEncoderKey;
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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
@Controller
@RequestMapping(value = "/admin/ereporting")
@SuppressFBWarnings({"EI_EXPOSE_REP2"})
public class AdminEReportingHeaderController extends AbstractController {

    private static final Logger LOG = LoggerFactory.getLogger(AdminEReportingHeaderController.class);

    private EReportObligationRepository reportObligationRepository;

    private EncoderRepository encoderRepository;

    private DecoderRepository decoderRepository;

    @Inject
    public void setReportObligationRepository(EReportObligationRepository reportObligationRepository) {
        this.reportObligationRepository = reportObligationRepository;
    }

    @Inject
    public void setEncoderRepository(EncoderRepository encoderRepository) {
        this.encoderRepository = encoderRepository;
    }

    @Inject
    public void setDecoderRepository(DecoderRepository decoderRepository) {
        this.decoderRepository = decoderRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String get() {
        return "admin/ereporting";
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public String getJSON() throws OwsExceptionReport, EncodingException {
        ObjectNode node = Json.nodeFactory().objectNode();
        Encoder<JsonNode, ReportObligation> reportObligationEncoder =
                encoderRepository.getEncoder(new JSONEncoderKey(ReportObligation.class));
        Encoder<JsonNode, RelatedParty> relatedPartyEncoder =
                encoderRepository.getEncoder(new JSONEncoderKey(RelatedParty.class));
        node.set(AQDJSONConstants.REPORTING_AUTHORITY,
                relatedPartyEncoder.encode(reportObligationRepository.getReportingAuthority()));
        ArrayNode ros = node.putArray(AQDJSONConstants.REPORT_OBLIGATIONS);
        for (ReportObligationType reportObligationType : ReportObligationType.values()) {
            ReportObligation reportObligation = reportObligationRepository.getReportObligation(reportObligationType);
            ros.addObject().put(AQDJSONConstants.ID, reportObligationType.name())
                    .put(AQDJSONConstants.NAME, reportObligationType.getTitle())
                    .put(AQDJSONConstants.DESCRIPTION, reportObligationType.getDescription())
                    .set(AQDJSONConstants.VALUE, reportObligationEncoder.encode(reportObligation));
        }
        return Json.print(node);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void save(@RequestBody String json) throws OwsExceptionReport, DecodingException {
        LOG.info("Saving {}", json);
        Decoder<ReportObligation, JsonNode> reportObligationDecoder =
                decoderRepository.getDecoder(new JsonDecoderKey(ReportObligation.class));
        Decoder<RelatedParty, JsonNode> relatedPartyDecoder =
                decoderRepository.getDecoder(new JsonDecoderKey(RelatedParty.class));

        JsonNode node = Json.loadString(json);

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
