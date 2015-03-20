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
package org.n52.sos.decode.json.impl;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.coding.json.JSONValidator;
import org.n52.sos.coding.json.SchemaConstants;
import org.n52.sos.decode.json.JSONDecoder;
import org.n52.sos.decode.json.JSONDecodingException;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.AbstractPhenomenon;
import org.n52.sos.ogc.om.ObservationValue;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.values.BooleanValue;
import org.n52.sos.ogc.om.values.CategoryValue;
import org.n52.sos.ogc.om.values.CountValue;
import org.n52.sos.ogc.om.values.GeometryValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.SensorML;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.vividsolutions.jts.geom.Geometry;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class ObservationDecoder extends JSONDecoder<OmObservation> {
    private static final Map<SupportedTypeKey, Set<String>> SUPPORTED_TYPES = ImmutableMap.of(
            SupportedTypeKey.ObservationType, (Set<String>) ImmutableSet.of(
                    // OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION,
                    // OmConstants.OBS_TYPE_COMPLEX_OBSERVATION,
                    OmConstants.OBS_TYPE_GEOMETRY_OBSERVATION, OmConstants.OBS_TYPE_CATEGORY_OBSERVATION,
                    OmConstants.OBS_TYPE_COUNT_OBSERVATION, OmConstants.OBS_TYPE_MEASUREMENT,
                    OmConstants.OBS_TYPE_TEXT_OBSERVATION, OmConstants.OBS_TYPE_TRUTH_OBSERVATION));

    private final JSONDecoder<AbstractFeature> featureDecoder = new FeatureDecoder();

    private final JSONDecoder<Geometry> geometryDecoder = new GeoJSONDecoder();

    public ObservationDecoder() {
        super(OmObservation.class);
    }

    @Override
    public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
        return Collections.unmodifiableMap(SUPPORTED_TYPES);
    }

    @Override
    public OmObservation decodeJSON(JsonNode node, boolean validate) throws OwsExceptionReport {
        if (node == null) {
            return null;
        }
        if (validate) {
            JSONValidator.getInstance().validateAndThrow(node, SchemaConstants.Observation.OBSERVATION);
        }
        return decodeJSON(node);
    }

    protected OmObservation decodeJSON(JsonNode node) throws OwsExceptionReport {
        if (node.isObject()) {
            OmObservation o = new OmObservation();
            o.setIdentifier(parseIdentifier(node));
            o.setValidTime(parseValidTime(node));
            o.setResultTime(parseResultTime(node));
            o.setValue(parseValue(node));
            o.setObservationConstellation(parseObservationConstellation(node));
            return o;
        } else {
            return null;
        }
    }

    public OmObservationConstellation parseObservationConstellation(JsonNode node) throws OwsExceptionReport {
        OmObservationConstellation oc = new OmObservationConstellation();
        oc.setProcedure(parseProcedure(node));
        oc.setObservableProperty(parseObservableProperty(node));
        oc.setObservationType(parseObservationType(node));
        oc.setFeatureOfInterest(parseFeatureOfInterest(node));
        return oc;
    }

    protected SosProcedureDescription parseProcedure(JsonNode node) {
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

    protected TimePeriod parseValidTime(JsonNode node) throws OwsExceptionReport {
        return parseTimePeriod(node.path(JSONConstants.VALID_TIME));
    }

    protected TimeInstant parseResultTime(JsonNode node) throws OwsExceptionReport {
        return parseTimeInstant(node.path(JSONConstants.RESULT_TIME));
    }

    private Time parsePhenomenonTime(JsonNode node) throws OwsExceptionReport {
        return parseTime(node.path(JSONConstants.PHENOMENON_TIME));
    }

    protected AbstractFeature parseFeatureOfInterest(JsonNode node) throws OwsExceptionReport {
        return featureDecoder.decodeJSON(node.path(JSONConstants.FEATURE_OF_INTEREST), false);
    }

    private ObservationValue<?> parseValue(JsonNode node) throws OwsExceptionReport {
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

    protected ObservationValue<?> parseMeasurementValue(JsonNode node) throws OwsExceptionReport {
        final QuantityValue qv =
                new QuantityValue(node.path(JSONConstants.RESULT).path(JSONConstants.VALUE).doubleValue(), node
                        .path(JSONConstants.RESULT).path(JSONConstants.UOM).textValue());
        return new SingleObservationValue<Double>(parsePhenomenonTime(node), qv);
    }

    private ObservationValue<?> parseTextObservationValue(JsonNode node) throws OwsExceptionReport {
        final TextValue v = new TextValue(node.path(JSONConstants.RESULT).textValue());
        return new SingleObservationValue<String>(parsePhenomenonTime(node), v);
    }

    private ObservationValue<?> parseCountObservationValue(JsonNode node) throws OwsExceptionReport {
        final CountValue v = new CountValue(node.path(JSONConstants.RESULT).intValue());
        return new SingleObservationValue<Integer>(parsePhenomenonTime(node), v);
    }

    private ObservationValue<?> parseTruthObservationValue(JsonNode node) throws OwsExceptionReport {
        final BooleanValue v = new BooleanValue(node.path(JSONConstants.RESULT).booleanValue());
        return new SingleObservationValue<Boolean>(parsePhenomenonTime(node), v);
    }

    private ObservationValue<?> parseCategoryObservationValue(JsonNode node) throws OwsExceptionReport {
        final CategoryValue v =
                new CategoryValue(node.path(JSONConstants.RESULT).path(JSONConstants.VALUE).textValue(), node
                        .path(JSONConstants.RESULT).path(JSONConstants.CODESPACE).textValue());
        return new SingleObservationValue<String>(parsePhenomenonTime(node), v);
    }

    private ObservationValue<?> parseGeometryObservation(JsonNode node) throws OwsExceptionReport {
        GeometryValue v = new GeometryValue(geometryDecoder.decodeJSON(node.path(JSONConstants.RESULT), false));
        return new SingleObservationValue<Geometry>(parsePhenomenonTime(node), v);
    }
}
