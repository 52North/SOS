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

import static org.n52.sos.coding.json.JSONConstants.$REF;
import static org.n52.sos.coding.json.JSONConstants.ABSTRACT;
import static org.n52.sos.coding.json.JSONConstants.ACCESS_CONSTRAINTS;
import static org.n52.sos.coding.json.JSONConstants.ADDRESS;
import static org.n52.sos.coding.json.JSONConstants.ADMINISTRATIVE_AREA;
import static org.n52.sos.coding.json.JSONConstants.AFTER;
import static org.n52.sos.coding.json.JSONConstants.ALLOWED_VALUES;
import static org.n52.sos.coding.json.JSONConstants.ANY;
import static org.n52.sos.coding.json.JSONConstants.BBOX;
import static org.n52.sos.coding.json.JSONConstants.BEFORE;
import static org.n52.sos.coding.json.JSONConstants.BEGINS;
import static org.n52.sos.coding.json.JSONConstants.BEGUN_BY;
import static org.n52.sos.coding.json.JSONConstants.BEYOND;
import static org.n52.sos.coding.json.JSONConstants.CITY;
import static org.n52.sos.coding.json.JSONConstants.COMMON_PARAMETERS;
import static org.n52.sos.coding.json.JSONConstants.CONSTRAINTS;
import static org.n52.sos.coding.json.JSONConstants.CONTACT;
import static org.n52.sos.coding.json.JSONConstants.CONTAINS;
import static org.n52.sos.coding.json.JSONConstants.CONTENTS;
import static org.n52.sos.coding.json.JSONConstants.COUNTRY;
import static org.n52.sos.coding.json.JSONConstants.CROSSES;
import static org.n52.sos.coding.json.JSONConstants.CRS;
import static org.n52.sos.coding.json.JSONConstants.DATA_TYPE;
import static org.n52.sos.coding.json.JSONConstants.DCP;
import static org.n52.sos.coding.json.JSONConstants.DELIVERY_POINT;
import static org.n52.sos.coding.json.JSONConstants.DISJOINT;
import static org.n52.sos.coding.json.JSONConstants.DURING;
import static org.n52.sos.coding.json.JSONConstants.D_WITHIN;
import static org.n52.sos.coding.json.JSONConstants.EMAIL;
import static org.n52.sos.coding.json.JSONConstants.ENDED_BY;
import static org.n52.sos.coding.json.JSONConstants.ENDS;
import static org.n52.sos.coding.json.JSONConstants.EQUALS;
import static org.n52.sos.coding.json.JSONConstants.FEATURE_OF_INTEREST;
import static org.n52.sos.coding.json.JSONConstants.FEATURE_OF_INTEREST_TYPE;
import static org.n52.sos.coding.json.JSONConstants.FEES;
import static org.n52.sos.coding.json.JSONConstants.FILTER_CAPABILITIES;
import static org.n52.sos.coding.json.JSONConstants.HREF;
import static org.n52.sos.coding.json.JSONConstants.IDENTIFIER;
import static org.n52.sos.coding.json.JSONConstants.INTERSECTS;
import static org.n52.sos.coding.json.JSONConstants.KEYWORDS;
import static org.n52.sos.coding.json.JSONConstants.LINK;
import static org.n52.sos.coding.json.JSONConstants.LOWER_LEFT;
import static org.n52.sos.coding.json.JSONConstants.MAX;
import static org.n52.sos.coding.json.JSONConstants.MEETS;
import static org.n52.sos.coding.json.JSONConstants.METHOD;
import static org.n52.sos.coding.json.JSONConstants.MET_BY;
import static org.n52.sos.coding.json.JSONConstants.MIN;
import static org.n52.sos.coding.json.JSONConstants.NAME;
import static org.n52.sos.coding.json.JSONConstants.NONE;
import static org.n52.sos.coding.json.JSONConstants.OBSERVABLE_PROPERTY;
import static org.n52.sos.coding.json.JSONConstants.OBSERVATION_TYPE;
import static org.n52.sos.coding.json.JSONConstants.OBSERVED_AREA;
import static org.n52.sos.coding.json.JSONConstants.OPERANDS;
import static org.n52.sos.coding.json.JSONConstants.OPERATIONS;
import static org.n52.sos.coding.json.JSONConstants.OPERATION_METADATA;
import static org.n52.sos.coding.json.JSONConstants.OPERATORS;
import static org.n52.sos.coding.json.JSONConstants.OVERLAPPEDBY;
import static org.n52.sos.coding.json.JSONConstants.OVERLAPS;
import static org.n52.sos.coding.json.JSONConstants.PARAMETERS;
import static org.n52.sos.coding.json.JSONConstants.PHENOMENON_TIME;
import static org.n52.sos.coding.json.JSONConstants.PHONE;
import static org.n52.sos.coding.json.JSONConstants.POSITION;
import static org.n52.sos.coding.json.JSONConstants.POSTAL_CODE;
import static org.n52.sos.coding.json.JSONConstants.PROCEDURE;
import static org.n52.sos.coding.json.JSONConstants.PROCEDURE_DESCRIPTION_FORMAT;
import static org.n52.sos.coding.json.JSONConstants.PROFILES;
import static org.n52.sos.coding.json.JSONConstants.PROPERTIES;
import static org.n52.sos.coding.json.JSONConstants.RELATED_FEATURE;
import static org.n52.sos.coding.json.JSONConstants.RESPONSE_FORMAT;
import static org.n52.sos.coding.json.JSONConstants.RESULT_TIME;
import static org.n52.sos.coding.json.JSONConstants.ROLE;
import static org.n52.sos.coding.json.JSONConstants.SCALAR;
import static org.n52.sos.coding.json.JSONConstants.SERVICE_IDENTIFICATION;
import static org.n52.sos.coding.json.JSONConstants.SERVICE_PROVIDER;
import static org.n52.sos.coding.json.JSONConstants.SERVICE_TYPE;
import static org.n52.sos.coding.json.JSONConstants.SITE;
import static org.n52.sos.coding.json.JSONConstants.SPATIAL;
import static org.n52.sos.coding.json.JSONConstants.TEMPORAL;
import static org.n52.sos.coding.json.JSONConstants.TITLE;
import static org.n52.sos.coding.json.JSONConstants.TOUCHES;
import static org.n52.sos.coding.json.JSONConstants.TYPE;
import static org.n52.sos.coding.json.JSONConstants.UPDATE_SEQUENCE;
import static org.n52.sos.coding.json.JSONConstants.UPPER_RIGHT;
import static org.n52.sos.coding.json.JSONConstants.VERSIONS;
import static org.n52.sos.coding.json.JSONConstants.WITHIN;
import static org.n52.sos.encode.json.impl.GeoJSONEncoder.SRID_LINK_PREFIX;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

import javax.xml.namespace.QName;

import org.n52.sos.coding.json.SchemaConstants;
import org.n52.sos.encode.json.AbstractSosResponseEncoder;
import org.n52.sos.i18n.LocaleHelper;
import org.n52.sos.i18n.LocalizedString;
import org.n52.sos.ogc.filter.FilterCapabilities;
import org.n52.sos.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.sos.ogc.filter.FilterConstants.TimeOperator;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.ows.Constraint;
import org.n52.sos.ogc.ows.DCP;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsOperation;
import org.n52.sos.ogc.ows.OwsOperationsMetadata;
import org.n52.sos.ogc.ows.OwsParameterDataType;
import org.n52.sos.ogc.ows.OwsParameterValue;
import org.n52.sos.ogc.ows.OwsParameterValuePossibleValues;
import org.n52.sos.ogc.ows.OwsParameterValueRange;
import org.n52.sos.ogc.ows.SosServiceIdentification;
import org.n52.sos.ogc.ows.SosServiceProvider;
import org.n52.sos.ogc.sos.SosCapabilities;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosObservationOffering;
import org.n52.sos.ogc.sos.SosOffering;
import org.n52.sos.response.GetCapabilitiesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vividsolutions.jts.geom.Envelope;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 *
 * @since 4.0.0
 */
public class GetCapabilitiesResponseEncoder extends AbstractSosResponseEncoder<GetCapabilitiesResponse> {
    private static final Logger LOG = LoggerFactory.getLogger(GetCapabilitiesResponseEncoder.class);

    public GetCapabilitiesResponseEncoder() {
        super(GetCapabilitiesResponse.class, SosConstants.Operations.GetCapabilities);
    }

    @Override
    protected void encodeResponse(ObjectNode json, GetCapabilitiesResponse t) throws OwsExceptionReport {
        SosCapabilities caps = t.getCapabilities();
        encodeServiceIdentification(json, caps);
        encodeServiceProvider(json, caps);
        encodeUpdateSequence(json, caps);
        encodeOperationMetadata(json, caps);
        encodeContents(json, caps);
        encodeExtensions(json, caps);
        encodeFilterCapabilities(json, caps);
    }

    private void encodeServiceIdentification(ObjectNode json, SosCapabilities caps) {

        if (caps.isSetServiceIdentification()) {
            SosServiceIdentification si = caps.getServiceIdentification();
            ObjectNode jsi = json.putObject(SERVICE_IDENTIFICATION);

            if (si.hasTitle()) {
                ObjectNode title = jsi.putObject(TITLE);
                for (LocalizedString ls : si.getTitle()) {
                    title.put(LocaleHelper.toString(ls.getLang()), ls.getText());
                }
            }
            if (si.hasAbstract()) {
                ObjectNode abstrakt = jsi.putObject(ABSTRACT);
                for (LocalizedString ls : si.getAbstract()) {
                    abstrakt.put(LocaleHelper.toString(ls.getLang()), ls.getText());
                }
            }
            if (si.hasAccessConstraints()) {
                ArrayNode constraints = jsi.putArray(ACCESS_CONSTRAINTS);
                for (String constraint : si.getAccessConstraints()) {
                    constraints.add(constraint);
                }
            }
            if (si.hasFees()) {
                jsi.put(FEES, si.getFees());
            }
            if (si.hasServiceType()) {
                jsi.put(SERVICE_TYPE, si.getServiceType());
                // TODO si.getServiceTypeCodeSpace();
            }
            if (si.hasKeywords()) {
                ArrayNode keywords = jsi.putArray(KEYWORDS);
                for (String keyword : si.getKeywords()) {
                    keywords.add(keyword);
                }
            }
            if (si.hasProfiles()) {
                ArrayNode profiles = jsi.putArray(PROFILES);
                for (String profile : si.getProfiles()) {
                    profiles.add(profile);
                }
            }
            if (si.hasVersions()) {
                ArrayNode versions = jsi.putArray(VERSIONS);
                for (String version : si.getVersions()) {
                    versions.add(version);
                }
            }
        }
    }

    private void encodeServiceProvider(ObjectNode json, SosCapabilities caps) {

        if (caps.isSetServiceProvider()) {
            SosServiceProvider sp = caps.getServiceProvider();
            ObjectNode jsp = json.putObject(SERVICE_PROVIDER);
            if (sp.hasName()) {
                jsp.put(NAME, sp.getName());
            }
            if (sp.hasSite()) {
                jsp.put(SITE, sp.getSite());
            }
            encodeContact(jsp, sp);
        }
    }

    private void encodeAdress(SosServiceProvider sp, ObjectNode contact) {
        ObjectNode address = contact.objectNode();
        if (sp.hasDeliveryPoint()) {
            address.put(DELIVERY_POINT, sp.getDeliveryPoint());
        }
        if (sp.hasCity()) {
            address.put(CITY, sp.getCity());
        }
        if (sp.hasAdministrativeArea()) {
            address.put(ADMINISTRATIVE_AREA, sp.getAdministrativeArea());
        }
        if (sp.hasPostalCode()) {
            address.put(POSTAL_CODE, sp.getPostalCode());
        }
        if (sp.hasCountry()) {
            address.put(COUNTRY, sp.getCountry());
        }
        if (sp.hasMailAddress()) {
            address.put(EMAIL, sp.getMailAddress());
        }
        if (address.size() > 0) {
            contact.put(ADDRESS, address);
        }
    }

    private void encodeContact(ObjectNode jsp, SosServiceProvider sp) {
        ObjectNode contact = jsp.objectNode();
        if (sp.hasIndividualName()) {
            contact.put(NAME, sp.getIndividualName());
        }
        if (sp.hasPositionName()) {
            contact.put(POSITION, sp.getPositionName());
        }
        if (sp.hasPhone()) {
            contact.put(PHONE, sp.getPhone());
        }
        encodeAdress(sp, contact);
        if (contact.size() > 0) {
            jsp.put(CONTACT, contact);
        }
    }

    private void encodeUpdateSequence(ObjectNode json, SosCapabilities caps) {
        if (caps.isSetUpdateSequence()) {
            json.put(UPDATE_SEQUENCE, caps.getUpdateSequence());
        }
    }

    private void encodeFilterCapabilities(ObjectNode json, SosCapabilities caps) {
        if (caps.isSetFilterCapabilities()) {
            FilterCapabilities fc = caps.getFilterCapabilities();
            ObjectNode jfc = json.putObject(FILTER_CAPABILITIES);
            encodeFilterConformances(jfc, fc);
            encodeScalarCapabilities(jfc, fc);
            encodeSpatialCapabilities(jfc, fc);
            encodeTemporalCapabilities(jfc, fc);
        }
    }

    private void encodeFilterConformances(ObjectNode jfc, FilterCapabilities fc) {
        /*
         * TODO implement
         * org.n52.sos.encode.json.impl.GetCapabilitiesResponseEncoder
         * .encodeFilterConformances()
         */
    }

    private ArrayNode encodeOperands(SortedSet<QName> so) {
        ArrayNode operands = nodeFactory().arrayNode();
        for (QName qn : so) {
            String schema = qnameToSchema(qn);
            if (schema != null) {
                operands.addObject().put($REF, schema);
            } else {
                LOG.warn("Can not transform QName {} to JSON Schema URI", qn);
            }
        }
        return operands;
    }

    private String qnameToSchema(QName qn) {
        if (qn.equals(GmlConstants.QN_TIME_INSTANT_32)) {
            return SchemaConstants.Common.TIME_INSTANT;
        } else if (qn.equals(GmlConstants.QN_TIME_PERIOD_32)) {
            return SchemaConstants.Common.TIME_PERIOD;
        } else if (qn.equals(GmlConstants.QN_ENVELOPE_32)) {
            return SchemaConstants.Common.ENVELOPE;
        } else {
            return null;
        }
    }

    private void encodeSpatialCapabilities(ObjectNode jfc, FilterCapabilities fc) {
        ObjectNode sfc = jfc.objectNode();
        encodeSpatialOperands(fc, sfc);
        encodeSpatialOperators(fc, sfc);
        if (sfc.size() > 0) {
            jfc.put(SPATIAL, sfc);
        }
    }

    private void encodeSpatialOperators(FilterCapabilities fc, ObjectNode sfc) {
        SortedMap<SpatialOperator, SortedSet<QName>> sos = fc.getSpatialOperators();
        if (sos != null && !sos.isEmpty()) {
            ObjectNode operators = sfc.putObject(OPERATORS);
            for (Entry<SpatialOperator, SortedSet<QName>> so : sos.entrySet()) {
                operators.put(stringify(so.getKey()), encodeOperands(so.getValue()));
            }
        }
    }

    private void encodeSpatialOperands(FilterCapabilities fc, ObjectNode sfc) {
        SortedSet<QName> so = fc.getSpatialOperands();
        if (so != null && !so.isEmpty()) {
            sfc.put(OPERANDS, encodeOperands(so));
        }
    }

    private String stringify(SpatialOperator so) {
        switch (so) {
        case Equals:
            return EQUALS;
        case Disjoint:
            return DISJOINT;
        case Touches:
            return TOUCHES;
        case Within:
            return WITHIN;
        case Overlaps:
            return OVERLAPS;
        case Crosses:
            return CROSSES;
        case Intersects:
            return INTERSECTS;
        case Contains:
            return CONTAINS;
        case DWithin:
            return D_WITHIN;
        case Beyond:
            return BEYOND;
        case BBOX:
            return BBOX;
        default:
            return so.name();
        }
    }

    private void encodeTemporalCapabilities(ObjectNode jfc, FilterCapabilities fc) {
        ObjectNode tfc = jfc.objectNode();
        encodeTemporalOperands(fc, tfc);
        encodeTemporalOperators(fc, tfc);
        if (tfc.size() > 0) {
            jfc.put(TEMPORAL, tfc);
        }
    }

    private void encodeTemporalOperators(FilterCapabilities fc, ObjectNode tfc) {
        SortedMap<TimeOperator, SortedSet<QName>> tos = fc.getTempporalOperators();
        if (tos != null && !tos.isEmpty()) {
            ObjectNode operators = tfc.putObject(OPERATORS);
            for (Entry<TimeOperator, SortedSet<QName>> to : tos.entrySet()) {
                operators.put(stringify(to.getKey()), encodeOperands(to.getValue()));
            }
        }
    }

    private void encodeTemporalOperands(FilterCapabilities fc, ObjectNode tfc) {
        SortedSet<QName> so = fc.getTemporalOperands();
        if (so != null && !so.isEmpty()) {
            tfc.put(OPERANDS, encodeOperands(so));
        }
    }

    private String stringify(TimeOperator to) {
        switch (to) {
        case TM_Before:
            return BEFORE;
        case TM_After:
            return AFTER;
        case TM_Begins:
            return BEGINS;
        case TM_Ends:
            return ENDS;
        case TM_EndedBy:
            return ENDED_BY;
        case TM_BegunBy:
            return BEGUN_BY;
        case TM_During:
            return DURING;
        case TM_Equals:
            return EQUALS;
        case TM_Contains:
            return CONTAINS;
        case TM_Overlaps:
            return OVERLAPS;
        case TM_Meets:
            return MEETS;
        case TM_MetBy:
            return MET_BY;
        case TM_OverlappedBy:
            return OVERLAPPEDBY;
        default:
            return to.name();
        }

    }

    private void encodeScalarCapabilities(ObjectNode jfc, FilterCapabilities fc) {
        ObjectNode sfc = jfc.objectNode();
        // FIXME scalar filter capabilities
        if (sfc.size() > 0) {
            jfc.put(SCALAR, sfc);
        }
    }

    private void encodeContents(ObjectNode json, SosCapabilities caps) throws OwsExceptionReport {
        if (caps.isSetContents()) {
            ArrayNode jc = json.putArray(CONTENTS);
            for (SosObservationOffering soo : caps.getContents()) {
                if (!soo.isEmpty()) {
                    jc.add(encodeOffering(soo));
                }
            }
        }
    }

    private void encodeExtensions(ObjectNode json, SosCapabilities caps) {
        // FIXME extensions
    }

    private JsonNode encodeOffering(SosObservationOffering soo) throws OwsExceptionReport {
        ObjectNode jsoo = nodeFactory().objectNode();
        SosOffering offering = soo.getOffering();
        jsoo.put(IDENTIFIER, offering.getIdentifier());
        if (offering.isSetName()) {
                jsoo.put(NAME, offering.getFirstName().getValue());
        }
        encodeProcedures(soo, jsoo);
        encodeObservableProperties(soo, jsoo);
        encodeRelatedFeatures(soo, jsoo);
        encodeObservedArea(soo, jsoo);
        encodePhenomenonTime(soo, jsoo);
        encodeResultTime(soo, jsoo);
        encodeResponseFormat(soo, jsoo);
        encodeObservationTypes(soo, jsoo);
        encodeFeatureOfInterestTypes(soo, jsoo);
        encodeProcedureDescriptionFormats(soo, jsoo);
        // TODO soo.getCompositePhenomena();
        // TODO soo.getFeatureOfInterest();
        // TODO soo.getObservationResultTypes();
        // TODO soo.getPhens4CompPhens();
        // TODO soo.getResponseModes();
        return jsoo;
    }

    private void encodeObservableProperties(SosObservationOffering soo, ObjectNode jsoo) {
        if (soo.isSetObservableProperties()) {
            ArrayNode jops = jsoo.putArray(OBSERVABLE_PROPERTY);
            for (String op : soo.getObservableProperties()) {
                jops.add(op);
            }
        }
    }

    private void encodeProcedures(SosObservationOffering soo, ObjectNode jsoo) {
        if (soo.isSetProcedures()) {
            ArrayNode jps = jsoo.putArray(PROCEDURE);
            for (String p : soo.getProcedures()) {
                jps.add(p);
            }
        }
    }

    private void encodeRelatedFeatures(SosObservationOffering soo, ObjectNode jsoo) {
        if (soo.isSetRelatedFeature()) {
            ArrayNode jrf = jsoo.putArray(RELATED_FEATURE);
            for (Entry<String, SortedSet<String>> rf : soo.getRelatedFeatures().entrySet()) {
                ArrayNode roles = jrf.addObject().put(FEATURE_OF_INTEREST, rf.getKey()).putArray(ROLE);
                for (String role : rf.getValue()) {
                    roles.add(role);
                }
            }
        }
    }

    private void encodeObservedArea(SosObservationOffering soo, ObjectNode jsoo) throws OwsExceptionReport {
        if (soo.isSetObservedArea() && soo.getObservedArea().isSetEnvelope() && soo.getObservedArea().isSetSrid()) {
            Envelope e = soo.getObservedArea().getEnvelope();
            ObjectNode oa = jsoo.putObject(OBSERVED_AREA);
            oa.putArray(LOWER_LEFT).add(e.getMinX()).add(e.getMinY());
            oa.putArray(UPPER_RIGHT).add(e.getMaxX()).add(e.getMaxY());
            oa.putObject(CRS).put(TYPE, LINK).putObject(PROPERTIES)
                    .put(HREF, SRID_LINK_PREFIX + soo.getObservedArea().getSrid());
        }
    }

    private void encodePhenomenonTime(SosObservationOffering soo, ObjectNode jsoo) throws OwsExceptionReport {
        if (soo.isSetPhenomenonTime()) {
            jsoo.put(PHENOMENON_TIME, encodeObjectToJson(soo.getPhenomenonTime()));
        }
    }

    private void encodeResultTime(SosObservationOffering soo, ObjectNode jsoo) throws OwsExceptionReport {
        if (soo.isSetResultTime()) {
            jsoo.put(RESULT_TIME, encodeObjectToJson(soo.getResultTime()));
        }
    }

    private void encodeResponseFormat(SosObservationOffering soo, ObjectNode jsoo) {
        if (soo.isSetResponseFormats()) {
            ArrayNode jrf = jsoo.putArray(RESPONSE_FORMAT);
            for (String rf : soo.getResponseFormats()) {
                jrf.add(rf);
            }
        }
    }

    private void encodeFeatureOfInterestTypes(SosObservationOffering soo, ObjectNode jsoo) {
        if (soo.isSetFeatureOfInterestTypes()) {
            ArrayNode jft = jsoo.putArray(FEATURE_OF_INTEREST_TYPE);
            for (String ft : soo.getFeatureOfInterestTypes()) {
                jft.add(ft);
            }
        }
    }

    private void encodeObservationTypes(SosObservationOffering soo, ObjectNode jsoo) {
        if (soo.isSetObservationTypes()) {
            ArrayNode jot = jsoo.putArray(OBSERVATION_TYPE);
            for (String ot : soo.getObservationTypes()) {
                jot.add(ot);
            }
        }
    }

    private void encodeOperationMetadata(ObjectNode json, SosCapabilities caps) {
        if (caps.isSetOperationsMetadata()) {
            ObjectNode jom = json.putObject(OPERATION_METADATA);
            OwsOperationsMetadata om = caps.getOperationsMetadata();
            encodeCommonValues(om, jom);
            encodeOperations(om, jom);
            // TODO om.isSetExtendedCapabilities();
        }
    }

    private JsonNode encodeOperation(OwsOperation o) {
        ObjectNode jo = nodeFactory().objectNode();
        SortedMap<String, Set<DCP>> dcp = o.getDcp();
        Map<String, List<OwsParameterValue>> param = o.getParameterValues();
        if (param != null && !param.isEmpty()) {
            ObjectNode jcv = jo.putObject(PARAMETERS);
            for (Entry<String, List<OwsParameterValue>> e : param.entrySet()) {
                jcv.put(e.getKey(), encodeParameterValues(e.getValue()));
            }
        }
        if (dcp != null && !dcp.isEmpty()) {
            jo.put(DCP, encodeDcp(dcp));
        }
        return jo;
    }

    private JsonNode encodeParameterValues(List<OwsParameterValue> parameterValues) {
        if (parameterValues.isEmpty()) {
            return nodeFactory().textNode(NONE);
        }
        if (parameterValues.size() == 1) {
            return encodeParamterValue(parameterValues.get(0));
        }
        ArrayNode parameters = nodeFactory().arrayNode();
        for (OwsParameterValue parameterValue : parameterValues) {
            JsonNode node = encodeParamterValue(parameterValue);
            if (node != null) {
                parameters.add(node);
            }
        }
        return parameters;
    }

    private JsonNode encodeDcp(SortedMap<String, Set<DCP>> dcpByMethod) {
        ArrayNode jdcps = nodeFactory().arrayNode();
        for (Entry<String, Set<DCP>> e : dcpByMethod.entrySet()) {
            String method = e.getKey();
            for (DCP dcp : e.getValue()) {
                ObjectNode jdcp = jdcps.addObject().put(METHOD, method).put(HREF, dcp.getUrl());
                Set<Constraint> constraints = dcp.getConstraints();
                if (constraints != null && !constraints.isEmpty()) {
                    ObjectNode jc = jdcp.putObject(CONSTRAINTS);
                    for (Constraint c : constraints) {
                        jc.put(c.getName(), encodeParameterValues(c.getValues()));
                    }
                }
            }
        }
        return jdcps;
    }

    private JsonNode encodeParamterValue(OwsParameterValue parameterValue) {
        if (parameterValue instanceof OwsParameterValuePossibleValues) {
            return encodeParameterPossibleValues(parameterValue);
        } else if (parameterValue instanceof OwsParameterValueRange) {
            return encodeParameterValueRange(parameterValue);
        } else if (parameterValue instanceof OwsParameterDataType) {
            return encodeParameterDataType(parameterValue);
        } else {
            LOG.warn("Unsupported OwsParameterValue type: {}",
                    parameterValue == null ? null : parameterValue.getClass());
            return null;
        }
    }

    private JsonNode encodeParameterPossibleValues(OwsParameterValue parameterValue) {
        OwsParameterValuePossibleValues possibleValues = (OwsParameterValuePossibleValues) parameterValue;
        SortedSet<String> values = possibleValues.getValues();
        if (values == null) {
            return nodeFactory().textNode(NONE);
        }
        if (values.isEmpty()) {
            return nodeFactory().textNode(ANY);
        }
        ObjectNode json = nodeFactory().objectNode();
        ArrayNode av = json.putArray(ALLOWED_VALUES);
        for (String v : values) {
            av.add(v);
        }
        return json;
    }

    private JsonNode encodeParameterValueRange(OwsParameterValue parameterValue) {
        OwsParameterValueRange valueRange = (OwsParameterValueRange) parameterValue;
        String min = valueRange.getMinValue();
        String max = valueRange.getMaxValue();
        if (min == null || max == null) {
            return nodeFactory().textNode(NONE);
        }
        if (min.isEmpty() || max.isEmpty()) {
            return nodeFactory().textNode(ANY);
        }
        return nodeFactory().objectNode().put(MIN, min).put(MAX, max);
    }

    private JsonNode encodeParameterDataType(OwsParameterValue parameterValue) {
        OwsParameterDataType dataType = (OwsParameterDataType) parameterValue;
        String reference = dataType.getReference();
        if (reference == null || reference.isEmpty()) {
            return nodeFactory().textNode(NONE);
        }
        return nodeFactory().objectNode().put(DATA_TYPE, reference);
    }

    private void encodeCommonValues(OwsOperationsMetadata om, ObjectNode jom) {
        if (om.isSetCommonValues()) {
            ObjectNode jcv = jom.putObject(COMMON_PARAMETERS);
            for (Entry<String, List<OwsParameterValue>> e : om.getCommonValues().entrySet()) {
                jcv.put(e.getKey(), encodeParameterValues(e.getValue()));
            }
        }
    }

    private void encodeOperations(OwsOperationsMetadata om, ObjectNode jom) {
        if (om.isSetOperations()) {
            ObjectNode jo = jom.putObject(OPERATIONS);
            for (OwsOperation o : om.getOperations()) {
                jo.put(o.getOperationName(), encodeOperation(o));
            }
        }
    }

    private void encodeProcedureDescriptionFormats(SosObservationOffering soo, ObjectNode jsoo) {
        if (soo.isSetProcedureDescriptionFormats()) {
            ArrayNode jpdf = jsoo.putArray(PROCEDURE_DESCRIPTION_FORMAT);
            for (String pdf : soo.getProcedureDescriptionFormats()) {
                jpdf.add(pdf);
            }
        }
    }
}
