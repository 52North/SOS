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
package org.n52.sos.decode;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.n52.iceland.coding.decode.ProcedureDecoder;
import org.n52.shetland.ogc.SupportedType;
import org.n52.shetland.ogc.gml.ReferenceType;
import org.n52.shetland.ogc.sos.ProcedureDescriptionFormat;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.wml.WaterMLConstants;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.ogc.wml.ObservationProcess;
import org.n52.sos.util.CodingHelper;
import org.n52.svalbard.decode.DecoderKey;
import org.n52.svalbard.decode.exception.DecodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import net.opengis.waterml.x20.ObservationProcessDocument;
import net.opengis.waterml.x20.ObservationProcessPropertyType;
import net.opengis.waterml.x20.ObservationProcessType;

public class WmlObservationProcessDecoderv20 extends AbstractWmlDecoderv20 implements ProcedureDecoder<Object, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WmlObservationProcessDecoderv20.class);

    private static final Set<DecoderKey> DECODER_KEYS = CollectionHelper.union(
            CodingHelper.decoderKeysForElements(WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING,
                                                ObservationProcessDocument.class,
                                                ObservationProcessPropertyType.class,
                                                ObservationProcessType.class),
            CodingHelper.decoderKeysForElements(WaterMLConstants.NS_WML_20,
                                                ObservationProcessDocument.class,
                                                ObservationProcessPropertyType.class,
                                                ObservationProcessType.class));

    private static final Map<String, Map<String, Set<String>>> SUPPORTED_TRANSACTIONAL_PROCEDURE_DESCRIPTION_FORMATS =
            ImmutableMap.of(SosConstants.SOS, ImmutableMap.of(Sos2Constants.SERVICEVERSION, ImmutableSet.of(WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING)));

    private static final Set<SupportedType> SUPPORTED_TYPES = ImmutableSet.of(new ProcedureDescriptionFormat(WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING));

    public WmlObservationProcessDecoderv20() {
        LOGGER.debug("Decoder for the following keys initialized successfully: {}!",
                Joiner.on(", ").join(DECODER_KEYS));
    }

    @Override
    public Set<DecoderKey> getKeys() {
        return Collections.unmodifiableSet(DECODER_KEYS);
    }

    @Override
    public Set<SupportedType> getSupportedTypes() {
        return Collections.unmodifiableSet(SUPPORTED_TYPES);
    }

    @Override
    public Set<String> getSupportedProcedureDescriptionFormats(String service, String version) {
        return SUPPORTED_TRANSACTIONAL_PROCEDURE_DESCRIPTION_FORMATS
                .getOrDefault(service, Collections.emptyMap())
                .getOrDefault(version, Collections.emptySet());
    }

    @Override
    public Object decode(Object object) throws DecodingException {
        if (object instanceof ObservationProcessDocument) {
            return parseObservationProcess(((ObservationProcessDocument) object).getObservationProcess());
        } else if (object instanceof ObservationProcessPropertyType) {
            return parseObservationProcess(((ObservationProcessPropertyType) object).getObservationProcess());
        } else if (object instanceof ObservationProcessType) {
            return parseObservationProcess((ObservationProcessType) object);
        }
        return super.decode(object);
    }

    private Object parseObservationProcess(ObservationProcessType opt) throws DecodingException {
        ObservationProcess observationProcess = new ObservationProcess();
        observationProcess.setGmlId(opt.getId());
        // parse identifier, names, description, locations
        parseAbstractFeatureType(opt, observationProcess);
        parseProcessType(opt, observationProcess);
        parseOriginatingProcess(opt, observationProcess);
        parseAggregatingDuration(opt, observationProcess);
        parseVerticalDatum(opt, observationProcess);
        parseComment(opt, observationProcess);
        parseProcessReference(opt, observationProcess);
        parseInput(opt, observationProcess);
        parseParameter(opt, observationProcess);
        setDescriptionXml(opt, observationProcess);
        return observationProcess;
    }

    private void setDescriptionXml(ObservationProcessType opt, ObservationProcess observationProcess) {
        ObservationProcessDocument doc =
                ObservationProcessDocument.Factory.newInstance(getXmlOptions());
        doc.setObservationProcess(opt);
        observationProcess.setXml(doc.xmlText(getXmlOptions()));
    }

    private void parseProcessType(ObservationProcessType opt, ObservationProcess observationProcess) {
        observationProcess.setProcessType(parseReferenceType(opt.getProcessType()));
    }

    private void parseOriginatingProcess(ObservationProcessType opt, ObservationProcess observationProcess)
            throws DecodingException {
        if (opt.isSetOriginatingProcess()) {
            observationProcess.setOriginatingProcess(parseReferenceType(opt.getOriginatingProcess()));
        }
    }

    private void parseAggregatingDuration(ObservationProcessType opt, ObservationProcess observationProcess) {
        if (opt.isSetAggregationDuration()) {
            observationProcess.setAggregationDuration(opt.getAggregationDuration().toString());
        }
    }

    private void parseVerticalDatum(ObservationProcessType opt, ObservationProcess observationProcess)
            throws DecodingException {
        if (opt.isSetVerticalDatum()) {
            Object decodeXmlElement = decodeXmlElement(opt.getVerticalDatum());
            if (decodeXmlElement instanceof ReferenceType) {
                observationProcess.setVerticalDatum((ReferenceType) decodeXmlElement);
            }
        }
    }

    private void parseComment(ObservationProcessType opt, ObservationProcess observationProcess) {
        if (CollectionHelper.isNotNullOrEmpty(opt.getCommentArray())) {
            observationProcess.setComments(Lists.newArrayList(opt.getCommentArray()));
        }
    }

    private void parseProcessReference(ObservationProcessType opt, ObservationProcess observationProcess) {
        if (opt.isSetProcessReference()) {
            observationProcess.setProcessReference(parseReferenceType(opt.getProcessReference()));
        }
    }

    private void parseInput(ObservationProcessType opt, ObservationProcess observationProcess) {
        if (CollectionHelper.isNotNullOrEmpty(opt.getInputArray())) {
            parseReferenceType(opt.getInputArray());
        }
    }

    private void parseParameter(ObservationProcessType opt, ObservationProcess observationProcess) throws DecodingException  {
        if (CollectionHelper.isNotNullOrEmpty(opt.getParameterArray())) {
            observationProcess.setParameters(parseNamedValueTypeArray(opt.getParameterArray()));
        }
    }
}
