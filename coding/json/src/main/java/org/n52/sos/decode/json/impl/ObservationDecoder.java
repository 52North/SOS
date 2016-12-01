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
package org.n52.sos.decode.json.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.n52.shetland.ogc.SupportedType;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.CodeWithAuthority;
import org.n52.shetland.ogc.gml.ReferenceType;
import org.n52.shetland.ogc.gml.time.Time;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.om.AbstractPhenomenon;
import org.n52.shetland.ogc.om.NamedValue;
import org.n52.shetland.ogc.om.ObservationValue;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.OmObservableProperty;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.OmObservationConstellation;
import org.n52.shetland.ogc.om.SingleObservationValue;
import org.n52.shetland.ogc.om.values.BooleanValue;
import org.n52.shetland.ogc.om.values.CategoryValue;
import org.n52.shetland.ogc.om.values.CountValue;
import org.n52.shetland.ogc.om.values.GeometryValue;
import org.n52.shetland.ogc.om.values.HrefAttributeValue;
import org.n52.shetland.ogc.om.values.QuantityValue;
import org.n52.shetland.ogc.om.values.TextValue;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.shetland.w3c.xlink.W3CHrefAttribute;
import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.coding.json.JSONValidator;
import org.n52.sos.coding.json.SchemaConstants;
import org.n52.sos.decode.json.JSONDecoder;
import org.n52.sos.decode.json.JSONDecodingException;
import org.n52.svalbard.decode.exception.DecodingException;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Geometry;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 *
 * @since 4.0.0
 */
public class ObservationDecoder extends JSONDecoder<OmObservation> {

    private static final Set<SupportedType> SUPPORTED_TYPES = ImmutableSet
            .<SupportedType>builder()
//            .add(OBS_TYPE_SWE_ARRAY_OBSERVATION)
//            .add(OBS_TYPE_COMPLEX_OBSERVATION)
            .add(OmConstants.OBS_TYPE_GEOMETRY_OBSERVATION_TYPE)
            .add(OmConstants.OBS_TYPE_CATEGORY_OBSERVATION_TYPE)
            .add(OmConstants.OBS_TYPE_COUNT_OBSERVATION_TYPE)
            .add(OmConstants.OBS_TYPE_MEASUREMENT_TYPE)
            .add(OmConstants.OBS_TYPE_TEXT_OBSERVATION_TYPE)
            .add(OmConstants.OBS_TYPE_TRUTH_OBSERVATION_TYPE)
            .build();

    private final JSONDecoder<AbstractFeature> featureDecoder = new FeatureDecoder();

    private final JSONDecoder<Geometry> geometryDecoder = new GeoJSONDecoder();

    public ObservationDecoder() {
        super(OmObservation.class);
    }

    @Override
    public Set<SupportedType> getSupportedTypes() {
        return Collections.unmodifiableSet(SUPPORTED_TYPES);
    }

    @Override
    public OmObservation decodeJSON(JsonNode node, boolean validate) throws DecodingException {
        if (node == null) {
            return null;
        }
        if (validate) {
            JSONValidator.getInstance().validateAndThrow(node, SchemaConstants.Observation.OBSERVATION);
        }
        return decodeJSON(node);
    }

    protected OmObservation decodeJSON(JsonNode node) throws DecodingException {
        if (node.isObject()) {
            OmObservation o = new OmObservation();
            o.setIdentifier(parseIdentifier(node));
            o.setValidTime(parseValidTime(node));
            o.setResultTime(parseResultTime(node));
            o.setValue(parseValue(node));
            o.setParameter(parseParameter(node));
            o.setObservationConstellation(parseObservationConstellation(node));
            return o;
        } else {
            return null;
        }
    }

    public OmObservationConstellation parseObservationConstellation(JsonNode node) throws DecodingException {
        OmObservationConstellation oc = new OmObservationConstellation();
        oc.setProcedure(parseProcedure(node));
        oc.setObservableProperty(parseObservableProperty(node));
        oc.setObservationType(parseObservationType(node));
        oc.setFeatureOfInterest(parseFeatureOfInterest(node));
        return oc;
    }

    protected SosProcedureDescription<?> parseProcedure(JsonNode node) {
        return new SensorML().setIdentifier(node.path(JSONConstants.PROCEDURE).textValue());
    }

    private AbstractPhenomenon parseObservableProperty(JsonNode node) {
        return new OmObservableProperty(node.path(JSONConstants.OBSERVED_PROPERTY).textValue());
    }

    private CodeWithAuthority parseIdentifier(JsonNode node) {
        return parseCodeWithAuthority(node.path(JSONConstants.IDENTIFIER));
    }

    protected String parseObservationType(JsonNode node) {
        return node.path(JSONConstants.TYPE).textValue();
    }

    protected TimePeriod parseValidTime(JsonNode node) throws DecodingException {
        return parseTimePeriod(node.path(JSONConstants.VALID_TIME));
    }

    protected TimeInstant parseResultTime(JsonNode node) throws DecodingException {
        return parseTimeInstant(node.path(JSONConstants.RESULT_TIME));
    }

    private Time parsePhenomenonTime(JsonNode node) throws DecodingException {
        return parseTime(node.path(JSONConstants.PHENOMENON_TIME));
    }

    protected Collection<NamedValue<?>> parseParameter(JsonNode node) throws DecodingException {
        Set<NamedValue<?>> parameters = Sets.newTreeSet();
        JsonNode parameter = node.path(JSONConstants.PARAMETER);
        if (parameter.isArray()) {
            for (JsonNode jsonNode : parameter) {
                parameters.add(parseNamedValue(jsonNode));
            }
        } else if (parameter.isObject()) {
            parameters.add(parseNamedValue(parameter));
        }
        return parameters;
    }

    private NamedValue<?> parseNamedValue(JsonNode parameter) throws DecodingException {
        JsonNode namedValue = parameter.path(JSONConstants.NAMED_VALUE);
        NamedValue<?> nv = parseNamedValueValue(namedValue.path(JSONConstants.VALUE));
        ReferenceType referenceType = new ReferenceType(namedValue.path(JSONConstants.NAME).asText());
        nv.setName(referenceType);
        return nv;
    }

    private NamedValue<?> parseNamedValueValue(JsonNode value) throws DecodingException {
        if (value.isTextual()) {
            NamedValue<W3CHrefAttribute> nv = new NamedValue<>();
            nv.setValue(new HrefAttributeValue(new W3CHrefAttribute(value.asText())));
            return nv;
        } else if (value.isBoolean()) {
            NamedValue<Boolean> nv = new NamedValue<>();
            nv.setValue(new BooleanValue(value.asBoolean()));
            return nv;
        } else if (value.isInt()) {
            NamedValue<Integer> nv = new NamedValue<>();
            nv.setValue(new CountValue(value.asInt()));
            return nv;
        } else if (value.isObject()) {
            if (value.has(JSONConstants.CODESPACE)) {
                NamedValue<String> nv = new NamedValue<>();
                nv.setValue(parseCategroyValue(value));
                return nv;
            } else if (value.has(JSONConstants.UOM)) {
                NamedValue<Double> nv = new NamedValue<>();
                nv.setValue(parseQuantityValue(value));
                return nv;
            } else if (value.has(JSONConstants.COORDINATES)) {
                NamedValue<Geometry> nv = new NamedValue<>();
                nv.setValue(new GeometryValue(geometryDecoder.decodeJSON(value, false)));
                return nv;
            }
        }
        throw new DecodingException("%s is not yet supported", value.toString());
    }

    protected AbstractFeature parseFeatureOfInterest(JsonNode node) throws DecodingException {
        return featureDecoder.decodeJSON(node.path(JSONConstants.FEATURE_OF_INTEREST), false);
    }

    private ObservationValue<?> parseValue(JsonNode node) throws DecodingException {
        String type = parseObservationType(node);
        if (type.equals(OmConstants.OBS_TYPE_MEASUREMENT)) {
            return parseMeasurementValue(node);
        } else if (type.equals(OmConstants.OBS_TYPE_TEXT_OBSERVATION)) {
            return parseTextObservationValue(node);
        } else if (type.equals(OmConstants.OBS_TYPE_COUNT_OBSERVATION)) {
            return parseCountObservationValue(node);
        } else if (type.equals(OmConstants.OBS_TYPE_TRUTH_OBSERVATION)) {
            return parseTruthObservationValue(node);
        } else if (type.equals(OmConstants.OBS_TYPE_CATEGORY_OBSERVATION)) {
            return parseCategoryObservationValue(node);
        } else if (type.equals(OmConstants.OBS_TYPE_GEOMETRY_OBSERVATION)) {
            return parseGeometryObservation(node);
        } else {
            throw new JSONDecodingException("Unsupported observationType: " + type);
        }
    }

    protected ObservationValue<?> parseMeasurementValue(JsonNode node) throws DecodingException {
        final QuantityValue qv = parseQuantityValue(node.path(JSONConstants.RESULT));
//                new QuantityValue(node.path(JSONConstants.RESULT).path(JSONConstants.VALUE).doubleValue(), node
//                        .path(JSONConstants.RESULT).path(JSONConstants.UOM).textValue());
        return new SingleObservationValue<>(parsePhenomenonTime(node), qv);
    }

    private QuantityValue parseQuantityValue(JsonNode node) throws DecodingException {
       return new QuantityValue(node.path(JSONConstants.VALUE).doubleValue(), node.path(JSONConstants.UOM).textValue());
    }

    private ObservationValue<?> parseTextObservationValue(JsonNode node) throws DecodingException {
        final TextValue v = new TextValue(node.path(JSONConstants.RESULT).textValue());
        return new SingleObservationValue<>(parsePhenomenonTime(node), v);
    }

    private ObservationValue<?> parseCountObservationValue(JsonNode node) throws DecodingException {
        final CountValue v = new CountValue(node.path(JSONConstants.RESULT).intValue());
        return new SingleObservationValue<>(parsePhenomenonTime(node), v);
    }

    private ObservationValue<?> parseTruthObservationValue(JsonNode node) throws DecodingException {
        final BooleanValue v = new BooleanValue(node.path(JSONConstants.RESULT).booleanValue());
        return new SingleObservationValue<>(parsePhenomenonTime(node), v);
    }

    private ObservationValue<?> parseCategoryObservationValue(JsonNode node) throws DecodingException {
        final CategoryValue v = parseCategroyValue(node.path(JSONConstants.RESULT));
//                new CategoryValue(node.path(JSONConstants.RESULT).path(JSONConstants.VALUE).textValue(), node
//                        .path(JSONConstants.RESULT).path(JSONConstants.CODESPACE).textValue());
        return new SingleObservationValue<>(parsePhenomenonTime(node), v);
    }

    private CategoryValue parseCategroyValue(JsonNode node) throws DecodingException {
        return new CategoryValue(node.path(JSONConstants.VALUE).textValue(), node.path(JSONConstants.CODESPACE).textValue());
    }

    private ObservationValue<?> parseGeometryObservation(JsonNode node) throws DecodingException {
        GeometryValue v = new GeometryValue(geometryDecoder.decodeJSON(node.path(JSONConstants.RESULT), false));
        return new SingleObservationValue<>(parsePhenomenonTime(node), v);
    }

}
