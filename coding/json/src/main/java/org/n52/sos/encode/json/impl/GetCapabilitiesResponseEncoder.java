/*
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static org.n52.shetland.ogc.filter.FilterConstants.SpatialOperator.Beyond;
import static org.n52.shetland.ogc.filter.FilterConstants.SpatialOperator.Contains;
import static org.n52.shetland.ogc.filter.FilterConstants.SpatialOperator.Crosses;
import static org.n52.shetland.ogc.filter.FilterConstants.SpatialOperator.DWithin;
import static org.n52.shetland.ogc.filter.FilterConstants.SpatialOperator.Disjoint;
import static org.n52.shetland.ogc.filter.FilterConstants.SpatialOperator.Equals;
import static org.n52.shetland.ogc.filter.FilterConstants.SpatialOperator.Intersects;
import static org.n52.shetland.ogc.filter.FilterConstants.SpatialOperator.Overlaps;
import static org.n52.shetland.ogc.filter.FilterConstants.SpatialOperator.Touches;
import static org.n52.shetland.ogc.filter.FilterConstants.SpatialOperator.Within;
import static org.n52.shetland.ogc.filter.FilterConstants.TimeOperator.TM_After;
import static org.n52.shetland.ogc.filter.FilterConstants.TimeOperator.TM_Before;
import static org.n52.shetland.ogc.filter.FilterConstants.TimeOperator.TM_Begins;
import static org.n52.shetland.ogc.filter.FilterConstants.TimeOperator.TM_BegunBy;
import static org.n52.shetland.ogc.filter.FilterConstants.TimeOperator.TM_Contains;
import static org.n52.shetland.ogc.filter.FilterConstants.TimeOperator.TM_During;
import static org.n52.shetland.ogc.filter.FilterConstants.TimeOperator.TM_EndedBy;
import static org.n52.shetland.ogc.filter.FilterConstants.TimeOperator.TM_Ends;
import static org.n52.shetland.ogc.filter.FilterConstants.TimeOperator.TM_Equals;
import static org.n52.shetland.ogc.filter.FilterConstants.TimeOperator.TM_Meets;
import static org.n52.shetland.ogc.filter.FilterConstants.TimeOperator.TM_MetBy;
import static org.n52.shetland.ogc.filter.FilterConstants.TimeOperator.TM_OverlappedBy;
import static org.n52.shetland.ogc.filter.FilterConstants.TimeOperator.TM_Overlaps;
import static org.n52.sos.encode.json.impl.GeoJSONEncoder.SRID_LINK_PREFIX;

import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.SortedSet;

import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.janmayen.i18n.LocaleHelper;
import org.n52.janmayen.i18n.MultilingualString;
import org.n52.shetland.ogc.filter.FilterCapabilities;
import org.n52.shetland.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.shetland.ogc.filter.FilterConstants.TimeOperator;
import org.n52.shetland.ogc.gml.GmlConstants;
import org.n52.shetland.ogc.ows.OwsAddress;
import org.n52.shetland.ogc.ows.OwsAllowedValues;
import org.n52.shetland.ogc.ows.OwsContact;
import org.n52.shetland.ogc.ows.OwsDCP;
import org.n52.shetland.ogc.ows.OwsDomain;
import org.n52.shetland.ogc.ows.OwsDomainMetadata;
import org.n52.shetland.ogc.ows.OwsHttp;
import org.n52.shetland.ogc.ows.OwsKeyword;
import org.n52.shetland.ogc.ows.OwsLanguageString;
import org.n52.shetland.ogc.ows.OwsMetadata;
import org.n52.shetland.ogc.ows.OwsOnlineResource;
import org.n52.shetland.ogc.ows.OwsOperation;
import org.n52.shetland.ogc.ows.OwsOperationMetadataExtension;
import org.n52.shetland.ogc.ows.OwsOperationsMetadata;
import org.n52.shetland.ogc.ows.OwsPhone;
import org.n52.shetland.ogc.ows.OwsPossibleValues;
import org.n52.shetland.ogc.ows.OwsRange;
import org.n52.shetland.ogc.ows.OwsRequestMethod;
import org.n52.shetland.ogc.ows.OwsResponsibleParty;
import org.n52.shetland.ogc.ows.OwsServiceIdentification;
import org.n52.shetland.ogc.ows.OwsServiceProvider;
import org.n52.shetland.ogc.ows.OwsValueRestriction;
import org.n52.shetland.ogc.ows.OwsValuesReference;
import org.n52.shetland.ogc.ows.OwsValuesUnit;
import org.n52.shetland.ogc.ows.service.GetCapabilitiesResponse;
import org.n52.shetland.ogc.sos.SosCapabilities;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.SosObservationOffering;
import org.n52.shetland.ogc.sos.SosOffering;
import org.n52.shetland.w3c.xlink.Link;
import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.coding.json.SchemaConstants;
import org.n52.sos.encode.json.AbstractSosResponseEncoder;
import org.n52.svalbard.encode.exception.EncodingException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vividsolutions.jts.geom.Envelope;

/**
 * TODO JavaDoc
 *
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 *
 * @since 4.0.0
 */
public class GetCapabilitiesResponseEncoder extends AbstractSosResponseEncoder<GetCapabilitiesResponse> {
    private static final Logger LOG = LoggerFactory.getLogger(GetCapabilitiesResponseEncoder.class);

    public GetCapabilitiesResponseEncoder() {
        super(GetCapabilitiesResponse.class, SosConstants.Operations.GetCapabilities);
    }

    @Override
    protected void encodeResponse(ObjectNode json, GetCapabilitiesResponse t) throws EncodingException {
        SosCapabilities caps = (SosCapabilities) t.getCapabilities();

        encodeOptional(json, JSONConstants.UPDATE_SEQUENCE, caps.getUpdateSequence(), nodeFactory()::textNode);
        encodeOptional(json, JSONConstants.SERVICE_IDENTIFICATION, caps.getServiceIdentification(), this::encodeServiceIdentification);
        encodeOptional(json, JSONConstants.SERVICE_PROVIDER, caps.getServiceProvider(), this::encodeServiceProvider);
        encodeOptional(json, JSONConstants.OPERATION_METADATA, caps.getOperationsMetadata(), this::encodeOperationMetadata);
        encodeOptionalChecked(json, JSONConstants.CONTENTS, caps.getContents(), this::encodeContents);
        encodeExtensions(json, caps);
        encodeOptional(json, JSONConstants.FILTER_CAPABILITIES, caps.getFilterCapabilities(), this::encodeFilterCapabilities);
    }

    private void encodeMultilingualString(ObjectNode json, String name, Optional<MultilingualString> string) {
        string.ifPresent(t -> {
            ObjectNode title = json.putObject(name);
            t.forEach(ls -> json.put(LocaleHelper.encode(ls.getLang()), ls.getText()));
        });
    }

    public void encodeOwsLanguageString(ObjectNode json, OwsLanguageString ls) {
        json.put(JSONConstants.VALUE, ls.getValue());
        ls.getLang().ifPresent(lang -> json.put(JSONConstants.LANG, lang));
    }

    public ObjectNode encodeOwsKeyword(OwsKeyword keyword) {
        ObjectNode json = nodeFactory().objectNode();
        encodeOptional(json, JSONConstants.TYPE, keyword.getType(), this::encodeOwsCode);
        encodeOwsLanguageString(json, keyword.getKeyword());
        return json;
    }

    private ObjectNode encodeServiceProvider(OwsServiceProvider serviceProvider) {
        ObjectNode json = nodeFactory().objectNode();
        json.put(JSONConstants.NAME, serviceProvider.getProviderName());
        encodeOptional(json, JSONConstants.SITE, serviceProvider.getProviderSite(), this::encodeOnlineResource);
        encode(json, JSONConstants.SERVICE_CONTACT, serviceProvider.getServiceContact(), this::encodeResponsibleParty);
        return json;
    }

    private JsonNode encodeResponsibleParty(OwsResponsibleParty responsibleParty) {
        ObjectNode json = nodeFactory().objectNode();
        encodeOptional(json, JSONConstants.INDIVIDUAL_NAME, responsibleParty.getIndividualName(), nodeFactory()::textNode);
        encodeOptional(json, JSONConstants.ORGANISATION_NAME, responsibleParty.getOrganisationName(), nodeFactory()::textNode);
        encodeOptional(json, JSONConstants.POSITION_NAME, responsibleParty.getPositionName(), nodeFactory()::textNode);
        encodeOptional(json, JSONConstants.ROLE, responsibleParty.getRole(), this::encodeOwsCode);
        encodeOptional(json, JSONConstants.CONTACT_INFO, responsibleParty.getContactInfo(), this::encodeContact);
        return json;
    }

    private JsonNode encodeOnlineResource(OwsOnlineResource resource) {
        return encodeLink(resource);
    }

    private ObjectNode encodeAddress(OwsAddress address) {
        ObjectNode json = nodeFactory().objectNode();
        encodeList(json, JSONConstants.DELIVERY_POINT, address.getDeliveryPoint(), nodeFactory()::textNode);
        encodeOptional(json, JSONConstants.CITY, address.getCity(), nodeFactory()::textNode);
        encodeOptional(json, JSONConstants.POSTAL_CODE, address.getPostalCode(), nodeFactory()::textNode);
        encodeOptional(json, JSONConstants.ADMINISTRATIVE_AREA, address.getAdministrativeArea(), nodeFactory()::textNode);
        encodeOptional(json, JSONConstants.COUNTRY, address.getCountry(), nodeFactory()::textNode);
        encodeList(json, JSONConstants.EMAIL, address.getElectronicMailAddress(), nodeFactory()::textNode);
        return json;
    }

    private JsonNode encodePhone(OwsPhone phone) {
        ObjectNode node = nodeFactory().objectNode();
        encodeList(node, JSONConstants.VOICE, phone.getVoice(), nodeFactory()::textNode);
        encodeList(node, JSONConstants.FACSIMILE, phone.getFacsimile(), nodeFactory()::textNode);
        return node;
    }

    private ObjectNode encodeContact(OwsContact sp) {
        ObjectNode json = nodeFactory().objectNode();
        encodeOptional(json, JSONConstants.CONTACT_INSTRUCTIONS, sp.getContactInstructions(), nodeFactory()::textNode);
        encodeOptional(json, JSONConstants.HOURS_OF_SERVICE, sp.getHoursOfService(), nodeFactory()::textNode);
        encodeOptional(json, JSONConstants.ADDRESS, sp.getAddress(), this::encodeAddress);
        encodeOptional(json, JSONConstants.ONLINE_RESOURCE, sp.getOnlineResource(), this::encodeOnlineResource);
        encodeOptional(json, JSONConstants.PHONE, sp.getPhone(), this::encodePhone);
        return json;
    }

    private ObjectNode encodeFilterCapabilities(FilterCapabilities fc) {
        ObjectNode jfc = nodeFactory().objectNode();
        encodeFilterConformances(jfc, fc);
        encodeScalarCapabilities(jfc, fc);
        encode(jfc, JSONConstants.SPATIAL, fc, this::encodeSpatialCapabilities);
        encode(jfc, JSONConstants.TEMPORAL, fc, this::encodeTemporalCapabilities);
        return jfc;
    }

    private void encodeFilterConformances(ObjectNode jfc, FilterCapabilities fc) {
        /*
         * TODO implement
         * org.n52.sos.encode.json.impl.GetCapabilitiesResponseEncoder
         * .encodeFilterConformances()
         */
    }

    private ArrayNode encodeOperands(SortedSet<QName> so) {
        return so.stream().map(this::qnameToSchema).filter(Objects::nonNull)
                .map(schema -> nodeFactory().objectNode().put(JSONConstants.$REF, schema))
                .collect(nodeFactory()::arrayNode, ArrayNode::add, ArrayNode::addAll);
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

    private ObjectNode encodeSpatialCapabilities(FilterCapabilities fc) {
        ObjectNode json = nodeFactory().objectNode();
        encode(json, JSONConstants.OPERANDS, fc.getSpatialOperands(), this::encodeOperands);
        encode(json, JSONConstants.OPERATORS, fc.getSpatialOperators(), this::encodeSpatialOperators);
        return json;
    }

    private ObjectNode encodeSpatialOperators(SortedMap<SpatialOperator, SortedSet<QName>> operators) {
        ObjectNode json = nodeFactory().objectNode();
        operators.entrySet().forEach(so -> json.set(stringify(so.getKey()), encodeOperands(so.getValue())));
        return json;
    }

    private String stringify(SpatialOperator so) {
        switch (so) {
            case Equals:
                return JSONConstants.EQUALS;
            case Disjoint:
                return JSONConstants.DISJOINT;
            case Touches:
                return JSONConstants.TOUCHES;
            case Within:
                return JSONConstants.WITHIN;
            case Overlaps:
                return JSONConstants.OVERLAPS;
            case Crosses:
                return JSONConstants.CROSSES;
            case Intersects:
                return JSONConstants.INTERSECTS;
            case Contains:
                return JSONConstants.CONTAINS;
            case DWithin:
                return JSONConstants.D_WITHIN;
            case Beyond:
                return JSONConstants.BEYOND;
            case BBOX:
                return JSONConstants.BBOX;
            default:
                return so.name();
        }
    }

    private ObjectNode encodeTemporalCapabilities(FilterCapabilities fc) {
        ObjectNode json = nodeFactory().objectNode();
        encode(json, JSONConstants.OPERANDS, fc.getTemporalOperands(), this::encodeOperands);
        encode(json, JSONConstants.OPERATORS, fc.getTemporalOperators(), this::encodeTemporalOperators);
        return json;
    }

    private ObjectNode encodeTemporalOperators(SortedMap<TimeOperator, SortedSet<QName>> operators) {
        ObjectNode json = nodeFactory().objectNode();
        operators.entrySet().forEach(so -> json.set(stringify(so.getKey()), encodeOperands(so.getValue())));
        return json;
    }

    private String stringify(TimeOperator to) {
        switch (to) {
            case TM_Before:
                return JSONConstants.BEFORE;
            case TM_After:
                return JSONConstants.AFTER;
            case TM_Begins:
                return JSONConstants.BEGINS;
            case TM_Ends:
                return JSONConstants.ENDS;
            case TM_EndedBy:
                return JSONConstants.ENDED_BY;
            case TM_BegunBy:
                return JSONConstants.BEGUN_BY;
            case TM_During:
                return JSONConstants.DURING;
            case TM_Equals:
                return JSONConstants.EQUALS;
            case TM_Contains:
                return JSONConstants.CONTAINS;
            case TM_Overlaps:
                return JSONConstants.OVERLAPS;
            case TM_Meets:
                return JSONConstants.MEETS;
            case TM_MetBy:
                return JSONConstants.MET_BY;
            case TM_OverlappedBy:
                return JSONConstants.OVERLAPPEDBY;
            default:
                return to.name();
        }

    }

    private void encodeScalarCapabilities(ObjectNode jfc, FilterCapabilities fc) {
        ObjectNode sfc = jfc.objectNode();
        // FIXME scalar filter capabilities

        if (sfc.size() > 0) {
            jfc.set(JSONConstants.SCALAR, sfc);
        }
    }


    private void encodeExtensions(ObjectNode json, SosCapabilities caps) {
        // FIXME extensions
    }

    private JsonNode encodeOffering(SosObservationOffering soo) throws EncodingException {
        ObjectNode jsoo = nodeFactory().objectNode();
        SosOffering offering = soo.getOffering();
        jsoo.put(JSONConstants.IDENTIFIER, offering.getIdentifier());
        if (offering.isSetName()) {
            jsoo.put(JSONConstants.NAME, offering.getFirstName().getValue());
        }
        if (soo.isSetProcedures()) {
            jsoo.set(JSONConstants.PROCEDURE, soo.getProcedures().stream().map(nodeFactory()::textNode).collect(toJsonArray()));
        }
        if (soo.isSetObservableProperties()) {
            jsoo.set(JSONConstants.OBSERVABLE_PROPERTY, soo.getObservableProperties().stream().map(nodeFactory()::textNode).collect(toJsonArray()));
        }
        if (soo.isSetRelatedFeature()) {
            ArrayNode jrf = jsoo.putArray(JSONConstants.RELATED_FEATURE);
            soo.getRelatedFeatures().forEach((feature, roles) -> {
                jrf.addObject()
                        .put(JSONConstants.FEATURE_OF_INTEREST, feature)
                        .set(JSONConstants.ROLE, roles.stream().map(nodeFactory()::textNode).collect(toJsonArray()));
            });
        }
        if (soo.isSetObservedArea() && soo.getObservedArea().isSetEnvelope() && soo.getObservedArea().isSetSrid()) {
            Envelope e = soo.getObservedArea().getEnvelope();
            ObjectNode oa = jsoo.putObject(JSONConstants.OBSERVED_AREA);
            oa.putArray(JSONConstants.LOWER_LEFT).add(e.getMinX()).add(e.getMinY());
            oa.putArray(JSONConstants.UPPER_RIGHT).add(e.getMaxX()).add(e.getMaxY());
            oa.putObject(JSONConstants.CRS)
                    .put(JSONConstants.TYPE, JSONConstants.LINK)
                    .putObject(JSONConstants.PROPERTIES)
                            .put(JSONConstants.HREF, SRID_LINK_PREFIX + soo.getObservedArea().getSrid());
        }
        if (soo.isSetPhenomenonTime()) {
            jsoo.set(JSONConstants.PHENOMENON_TIME, encodeObjectToJson(soo.getPhenomenonTime()));
        }
        if (soo.isSetResultTime()) {
            jsoo.set(JSONConstants.RESULT_TIME, encodeObjectToJson(soo.getResultTime()));
        }
        if (soo.isSetResponseFormats()) {
            jsoo.set(JSONConstants.RESPONSE_FORMAT, soo.getResponseFormats().stream().map(nodeFactory()::textNode).collect(toJsonArray()));
        }
        if (soo.isSetObservationTypes()) {
            jsoo.set(JSONConstants.OBSERVATION_TYPE, soo.getObservationTypes().stream().map(nodeFactory()::textNode).collect(toJsonArray()));
        }
        if (soo.isSetFeatureOfInterestTypes()) {
            jsoo.set(JSONConstants.FEATURE_OF_INTEREST_TYPE, soo.getFeatureOfInterestTypes().stream().map(nodeFactory()::textNode).collect(toJsonArray()));
        }
        if (soo.isSetProcedureDescriptionFormats()) {
            jsoo.set(JSONConstants.PROCEDURE_DESCRIPTION_FORMAT, soo.getProcedureDescriptionFormats().stream().map(nodeFactory()::textNode).collect(toJsonArray()));
        }
        // TODO soo.getCompositePhenomena();
        // TODO soo.getFeatureOfInterest();
        // TODO soo.getObservationResultTypes();
        // TODO soo.getPhens4CompPhens();
        // TODO soo.getResponseModes();
        return jsoo;
    }


    private JsonNode encodeOperationMetadata(OwsOperationsMetadata operationsMetadata) {
        ObjectNode node = nodeFactory().objectNode();
        encodeList(node, JSONConstants.OPERATIONS, operationsMetadata.getOperations(), this::encodeOperation);
        encodeList(node, JSONConstants.CONSTRAINTS, operationsMetadata.getConstraints(), this::encodeOwsDomain);
        encodeList(node, JSONConstants.PARAMETERS, operationsMetadata.getParameters(), this::encodeOwsDomain);
        encodeOptional(node, JSONConstants.EXTENSIONS, operationsMetadata.getExtension(), this::encodeOwsOperationMetadataExtension);
        return node;
    }

    private JsonNode encodeOwsOperationMetadataExtension(OwsOperationMetadataExtension operationMetadataExtension) {
        //TODO extension
        return nodeFactory().nullNode();
    }

    private JsonNode encodeOperation(OwsOperation o) {
        ObjectNode jo = nodeFactory().objectNode();
        jo.put(JSONConstants.NAME, o.getName());
        encodeList(jo, JSONConstants.PARAMETERS, o.getParameters(), this::encodeOwsDomain);
        encodeList(jo, JSONConstants.CONSTRAINTS, o.getConstraints(), this::encodeOwsDomain);
        encodeList(jo, JSONConstants.METADATA, o.getMetadata(), this::encodeOwsMetadata);
        encodeList(jo, JSONConstants.DCP, o.getDCP(), this::encodeOwsDCP);
        return jo;
    }

    private JsonNode encodeOwsDCP(OwsDCP dcp) {
        if (dcp.isHTTP()) {
            return encodeOwsHTTP(dcp.asHTTP());
        }
        return nodeFactory().nullNode();
    }

    private JsonNode encodeOwsMetadata(OwsMetadata metadata) {
        ObjectNode node = encodeLink(metadata);
        encodeOptional(node, JSONConstants.ABOUT, metadata.getAbout(), this::encodeURI);
        return node;
    }

    private JsonNode encodeOwsDomainMetadata(OwsDomainMetadata metadata) {
        ObjectNode node = nodeFactory().objectNode();
        encodeOptional(node, JSONConstants.REFERENCE, metadata.getReference(), this::encodeURI);
        encodeOptional(node, JSONConstants.VALUE, metadata.getValue(), this::encodeAsString);
        return node;
    }

    private ObjectNode encodeLink(Link link) {
        ObjectNode node = nodeFactory().objectNode();
        encodeOptional(node, JSONConstants.$REF, link.getHref(), this::encodeURI);
        encodeOptional(node, JSONConstants.TITLE, link.getTitle(), nodeFactory()::textNode);
        encodeOptional(node, JSONConstants.ACTUATE, link.getActuate(), this::encodeAsString);
        encodeOptional(node, JSONConstants.ARCROLE, link.getArcrole(), this::encodeURI);
        encodeOptional(node, JSONConstants.ROLE, link.getRole(), this::encodeURI);
        encodeOptional(node, JSONConstants.SHOW, link.getShow(), this::encodeAsString);
        return node;
    }

    private JsonNode encodeOwsDomain(OwsDomain domain) {
        ObjectNode node = nodeFactory().objectNode();
        encode(node, JSONConstants.NAME, domain.getName(), this::encodeAsString);
        encodeOptional(node, JSONConstants.DATA_TYPE, domain.getDataType(), this::encodeOwsDomainMetadata);
        encodeOptional(node, JSONConstants.DEFAULT, domain.getDefaultValue(), this::encodeOwsValue);
        encodeOptional(node, JSONConstants.MEANING, domain.getMeaning(), this::encodeOwsDomainMetadata);
        encodeOptional(node, JSONConstants.METADATA, domain.getMeaning(), this::encodeOwsDomainMetadata);
        encode(node, JSONConstants.POSSIBLE_VALUES, domain.getPossibleValues(), this::encodeParameterPossibleValues);

        domain.getValuesUnit().ifPresent(unit -> {
            JsonNode json = encodeOwsValuesUnit(unit);
            if (unit.isReferenceSystem()) {
                node.set(JSONConstants.REFERENCE_SYSTEM, json);
            } else if (unit.isUOM()) {
                node.set(JSONConstants.UOM, json);
            }
        });

        encodeOptional(node, JSONConstants.VALUES_UNIT, domain.getValuesUnit(), this::encodeOwsValuesUnit);
        return node;
    }

    private JsonNode encodeOwsValuesUnit(OwsValuesUnit unit)  {
        ObjectNode node = nodeFactory().objectNode();
        encodeOptional(node, JSONConstants.REFERENCE, unit.getReference(), this::encodeURI);
        encodeOptional(node, JSONConstants.VALUE, unit.getValue(), this::encodeAsString);
        return node;
    }

    private JsonNode encodeParameterPossibleValues(OwsPossibleValues parameterValue) {
        if (parameterValue.isAnyValue()) {
            return nodeFactory().textNode(JSONConstants.ANY);
        } else if (parameterValue.isNoValues()) {
            return nodeFactory().textNode(JSONConstants.NONE);
        } else if (parameterValue.isAllowedValues()) {
            return encodeOwsAllowedValues(parameterValue.asAllowedValues());
        } else if (parameterValue.isValuesReference()) {
            return encodeOwsValueReference(parameterValue.asValuesReference());
        } else {
            LOG.warn("Unsupported OwsParameterValue type: {}", parameterValue.getClass());
            return nodeFactory().nullNode();
        }
    }


    private ObjectNode encodeServiceIdentification(OwsServiceIdentification serviceIdentification) {
        ObjectNode json = nodeFactory().objectNode();
        encodeMultilingualString(json, JSONConstants.TITLE, serviceIdentification.getTitle());
        encodeMultilingualString(json, JSONConstants.ABSTRACT, serviceIdentification.getAbstract());
        encodeList(json, JSONConstants.ACCESS_CONSTRAINTS, serviceIdentification.getAccessConstraints(), nodeFactory()::textNode);
        encodeList(json, JSONConstants.FEES, serviceIdentification.getFees(), nodeFactory()::textNode);
        encode(json, JSONConstants.SERVICE_TYPE, serviceIdentification.getServiceType(), this::encodeOwsCode);
        encodeList(json, JSONConstants.KEYWORDS, serviceIdentification.getKeywords(), this::encodeOwsKeyword);
        encodeList(json, JSONConstants.PROFILES, serviceIdentification.getProfiles(), this::encodeURI);
        encodeList(json, JSONConstants.VERSIONS, serviceIdentification.getServiceTypeVersion(), nodeFactory()::textNode);
        return json;
    }

    private JsonNode encodeOwsValue(OwsValueRestriction restriction) {
        return nodeFactory().textNode(restriction.asValue().getValue());
    }

    private JsonNode encodeOwsRange(OwsValueRestriction restriction) {
        OwsRange range = restriction.asRange();
        ObjectNode json = nodeFactory().objectNode();
        encodeOptional(json, JSONConstants.MIN, range.getLowerBound(), this::encodeOwsValue);
        encodeOptional(json, JSONConstants.MAX, range.getUpperBound(), this::encodeOwsValue);
        json.set(JSONConstants.TYPE, nodeFactory().textNode(range.getType()));
        encodeOptional(json, JSONConstants.SPACING, range.getSpacing(), this::encodeOwsValue);
        return json;
    }

    public JsonNode encodeOwsAllowedValues(OwsAllowedValues allowedValues) {
        return allowedValues.getRestrictions().stream().map(this::encodeOwsValueRestriction)
                .collect(nodeFactory()::arrayNode, ArrayNode::add, ArrayNode::addAll);
    }

    public JsonNode encodeOwsValueReference(OwsValuesReference valuesReference) {
        ObjectNode node = nodeFactory().objectNode();
        node.put(JSONConstants.REFERENCE, valuesReference.getReference().toString());
        node.put(JSONConstants.VALUE, valuesReference.getValue());
        return node;
    }

    private JsonNode encodeOwsHTTP(OwsHttp http) {
        ObjectNode node = nodeFactory().objectNode();
        node.put(JSONConstants.TYPE, JSONConstants.HTTP_TYPE);

        node.set(JSONConstants.METHODS, http.getRequestMethods().stream()
                 .collect(toJsonObject(groupingBy(OwsRequestMethod::getHttpMethod,
                                                  mapping(this::encodeOwsRequestMethod, toJsonArray())))));
        return node;
    }

    public ObjectNode encodeOwsRequestMethod(OwsRequestMethod m) {
        ObjectNode jm = encodeLink(m);
        encodeList(jm, JSONConstants.CONSTRAINTS, m.getConstraints(), this::encodeOwsDomain);
        return jm;
    }

    public JsonNode encodeOwsValueRestriction(OwsValueRestriction restriction) {
        JsonNode n;
        if (restriction.isValue()) {
            n = encodeOwsValue(restriction);
        } else if (restriction.isRange()) {
            n = encodeOwsRange(restriction.asRange());
        } else {
            n = nodeFactory().nullNode();
        }
        return n;
    }

    private JsonNode encodeContents(SortedSet<SosObservationOffering> contents) throws EncodingException {
        ArrayNode node = nodeFactory().arrayNode();
        for (SosObservationOffering offering : contents) {
            node.add(encodeOffering(offering));
        }
        return node;
    }

}
