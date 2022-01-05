/*
 * Copyright (C) 2012-2022 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.config.json;

import javax.inject.Inject;

import org.n52.faroe.json.AbstractJsonDao;
import org.n52.shetland.aqd.ReportObligation;
import org.n52.shetland.aqd.ReportObligationType;
import org.n52.shetland.inspire.base2.RelatedParty;
import org.n52.svalbard.decode.Decoder;
import org.n52.svalbard.decode.DecoderRepository;
import org.n52.svalbard.decode.JsonDecoderKey;
import org.n52.svalbard.decode.exception.DecodingException;
import org.n52.svalbard.encode.Encoder;
import org.n52.svalbard.encode.EncoderRepository;
import org.n52.svalbard.encode.exception.EncodingException;
import org.n52.svalbard.encode.json.JSONEncoderKey;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonReportingHeaderDao extends AbstractJsonDao implements ReportingHeaderDao {

    @Inject
    private DecoderRepository decoderRepository;

    @Inject
    private EncoderRepository encoderRepository;

    @Override
    public void save(RelatedParty relatedParty) {
        configuration().writeLock().lock();
        try {
            save(REPORTING_AUTHORITY_KEY, relatedParty);
        } finally {
            configuration().writeLock().unlock();
        }
        configuration().scheduleWrite();
    }

    @Override
    public void save(ReportObligationType type, ReportObligation reportObligation) {
        configuration().writeLock().lock();
        try {
            save(REPORT_OBLIGATION_KEY_PREFIX + type, reportObligation);
        } finally {
            configuration().writeLock().unlock();
        }
        configuration().scheduleWrite();
    }

    private void save(String key, Object o) {
        Encoder<JsonNode, Object> encoder = encoderRepository.getEncoder(new JSONEncoderKey(o.getClass()));
        JsonNode node;
        try {
            node = encoder.encode(o);
        } catch (EncodingException ex) {
            throw new RuntimeException(ex);

        }
        getConfiguration().set(key, node);
        configuration().writeNow();
    }

    @Override
    public RelatedParty loadRelatedParty() {
        configuration().readLock().lock();
        try {
            Decoder<RelatedParty, JsonNode> decoder =
                    decoderRepository.getDecoder(new JsonDecoderKey(RelatedParty.class));
            JsonNode node = load(REPORTING_AUTHORITY_KEY);
            return node == null || node.isMissingNode()  ? null :  decoder.decode(node);
        } catch (DecodingException ex) {
            throw new RuntimeException(ex);
        } finally {
            configuration().readLock().unlock();
        }
    }

    @Override
    public ReportObligation loadReportObligation(ReportObligationType type) {
        configuration().readLock().lock();
        try {
            Decoder<ReportObligation, JsonNode> decoder =
                    decoderRepository.getDecoder(new JsonDecoderKey(ReportObligation.class));
            JsonNode node = load(REPORT_OBLIGATION_KEY_PREFIX + type);
            return node == null || node.isMissingNode() ? null : decoder.decode(node);
        } catch (DecodingException ex) {
            throw new RuntimeException(ex);
        } finally {
            configuration().readLock().unlock();
        }
    }

    private JsonNode load(String key) {
        return getConfiguration().path(key);
    }

    @Override
    protected ObjectNode getConfiguration() {
        return super.getConfiguration().with(REPORTING_HEADERS);
    }
}
