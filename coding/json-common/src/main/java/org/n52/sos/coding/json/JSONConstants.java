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
package org.n52.sos.coding.json;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public interface JSONConstants {
    String $REF = "$ref";

    String ABSTRACT = "abstract";

    String ACCEPT_FORMATS = "acceptFormats";

    String ACCEPT_VERSIONS = "acceptVersions";

    String ACCEPTED_TEMPLATE = "acceptedTemplate";

    String ACCESS_CONSTRAINTS = "accessConstraints";

    String ADDRESS = "address";

    String ADMINISTRATIVE_AREA = "administrativeArea";

    String AFTER = "after";

    String ALLOWED_VALUES = "allowedValues";

    String ANY = "any";

    String ASSIGNED_OFFERING = "assignedOffering";

    String ASSIGNED_PROCEDURE = "assignedProcedure";

    String BBOX = "bbox";

    String BEFORE = "before";

    String BEGINS = "begins";

    String BEGUN_BY = "begunBy";

    String BEYOND = "beyond";

    String BLOCK_SEPARATOR = "blockSeparator";

    String BOOLEAN = "boolean";

    String BOOLEAN_TYPE = "boolean";

    String CATEGORY = "category";

    String CATEGORY_TYPE = "category";

    String CITY = "city";

    String CODE = "code";

    String CODESPACE = "codespace";

    String COMMON_PARAMETERS = "commonParameters";

    String CONSTRAINTS = "constraints";

    String CONTACT = "contact";

    String CONTAINS = "contains";

    String CONTENTS = "contents";

    String COORDINATES = "coordinates";

    String COUNT = "count";

    String COUNT_RANGE = "countRange";

    String COUNT_RANGE_TYPE = "countRange";

    String COUNT_TYPE = "count";

    String COUNTRY = "country";

    String CROSSES = "crosses";

    String CRS = "crs";

    String D_WITHIN = "dWithin";

    String DATA_TYPE = "dataType";

    String DCP = "dcp";

    String DECIMAL_SEPARATOR = "decimalSeparator";

    String DEFAULT = "default";

    String DEFINITION = "definition";

    String DELETED_OBSERVATION = "deletedObservation";

    String DELETED_PROCEDURE = "deletedProcedure";

    String DELIVERY_POINT = "deliveryPoint";

    String DESCRIPTION = "description";

    String DISJOINT = "disjoint";

    String DURING = "during";

    String EMAIL = "email";

    String ENDED_BY = "endedBy";

    String ENDS = "ends";

    String EQUALS = "equals";

    String ERRORS = "errors";

    String EXCEPTIONS = "exceptions";

    String EXTENSIONS = "extensions";
    
    String FEATURE_OF_INTEREST = "featureOfInterest";

    String FEATURE_OF_INTEREST_TYPE = "featureOfInterestType";

    String FEES = "fees";

    String FIELDS = "fields";

    String FILTER_CAPABILITIES = "filterCapabilities";

    String GEOMETRIES = "geometries";

    String GEOMETRY = "geometry";

    String GEOMETRY_COLLECTION = "GeometryCollection";

    String HREF = "href";

    String IDENTIFIER = "identifier";

    String INSTANCE = "instance";

    String INTERSECTS = "intersects";

    String KEYWORDS = "keywords";

    String LABEL = "label";

    String LINE_STRING = "LineString";

    String LINK = "link";

    String LOCATOR = "locator";

    String LOWER_LEFT = "lowerLeft";

    String MAX = "max";

    String MEETS = "meets";

    String MET_BY = "metBy";

    String METHOD = "method";

    String MIN = "min";

    String MULTI_LINE_STRING = "MultiLineString";

    String MULTI_POINT = "MultiPoint";

    String MULTI_POLYGON = "MultiPolygon";

    String NAME = "name";

    String NONE = "none";

    String OBSERVABLE_PROPERTY = "observableProperty";

    String OBSERVABLE_PROPERTY_TYPE = "observableProperty";

    String OBSERVATION = "observation";

    String OBSERVATION_TEMPLATE = "observationTemplate";

    String OBSERVATION_TYPE = "observationType";

    String OBSERVATIONS = "observations";

    String OBSERVED_AREA = "observedArea";

    String OBSERVED_PROPERTY = "observedProperty";

    String OFFERING = "offering";

    String OPERANDS = "operands";

    String OPERATION_METADATA = "operationMetadata";

    String OPERATIONS = "operations";

    String OPERATORS = "operators";

    String OVERLAPPEDBY = "overlappedby";

    String OVERLAPS = "overlaps";

    String PARAMETERS = "parameters";

    String PHENOMENON_TIME = "phenomenonTime";

    String PHONE = "phone";

    String POINT = "Point";

    String POLYGON = "Polygon";

    String POSITION = "position";

    String POSTAL_CODE = "postalCode";

    String PROCEDURE = "procedure";

    String PROCEDURE_DESCRIPTION = "procedureDescription";

    String PROCEDURE_DESCRIPTION_FORMAT = "procedureDescriptionFormat";

    String PROFILES = "profiles";

    String PROPERTIES = "properties";

    String QUALITY = "quality";

    String QUALITY_TYPE = "quality";

    String QUANTITY = "quantity";

    String QUANTITY_RANGE = "quantityRange";

    String QUANTITY_RANGE_TYPE = "quantityRange";

    String QUANTITY_TYPE = "quantity";

    String REF = "ref";

    String RELATED_FEATURE = "relatedFeature";

    String REQUEST = "request";

    String REQUESTS = "requests";

    String RESPONSE_FORMAT = "responseFormat";

    String RESPONSE_MODE = "responseMode";

    String RESPONSES = "responses";

    String RESULT = "result";

    String RESULT_ENCODING = "resultEncoding";

    String RESULT_FILTER = "resultFilter";

    String RESULT_MODEL = "resultModel";

    String RESULT_STRUCTURE = "resultStructure";

    String RESULT_TIME = "resultTime";

    String RESULT_VALUES = "resultValues";

    String ROLE = "role";

    String SAMPLED_FEATURE = "sampledFeature";

    String SCALAR = "scalar";

    String SECTIONS = "sections";

    String SERVICE = "service";

    String SERVICE_IDENTIFICATION = "serviceIdentification";

    String SERVICE_PROVIDER = "serviceProvider";

    String SERVICE_TYPE = "serviceType";

    String SITE = "site";

    String SPATIAL = "spatial";

    String SPATIAL_FILTER = "spatialFilter";

    String STOP_AT_FAILURE = "stopAtFailure";

    String TARGET = "target";

    String TEMPLATE_IDENTIFIER = "templateIdentifier";

    String TEMPORAL = "temporal";

    String TEMPORAL_FILTER = "temporalFilter";

    String TEXT = "text";

    String TEXT_FIELD = "textField";

    String TEXT_TYPE = "text";

    String TIME = "time";

    String TIME_RANGE = "timeRange";

    String TIME_RANGE_TYPE = "timeRange";

    String TIME_TYPE = "time";

    String TITLE = "title";

    String TOKEN_SEPARATOR = "tokenSeparator";

    String TOUCHES = "touches";

    String TYPE = "type";

    String UOM = "uom";

    String UPDATE_SEQUENCE = "updateSequence";

    String UPDATED_PROCEDURE = "updatedProcedure";

    String UPPER_RIGHT = "upperRight";

    String VALID_TIME = "validTime";

    String VALUE = "value";

    String VALUES = "values";

    String VERSION = "version";

    String VERSIONS = "versions";

    String WITHIN = "within";
}
