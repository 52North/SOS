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
package org.n52.sos.ogc.sos;

import static java.util.Collections.unmodifiableSet;

import java.util.Set;

import org.n52.sos.util.http.MediaTypes;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * SosConstants holds all important and often used constants of this SOS (e.g.
 * name of the getCapabilities operation) that are global between all supported
 * versions
 * 
 * @since 4.0.0
 */
public interface SosConstants {

    String NS_SOS_PREFIX = "sos";

    /**
     * Constant for the content types of the accept formats
     */
    Set<String> ACCEPT_FORMATS = unmodifiableSet(Sets.newHashSet(MediaTypes.APPLICATION_XML.toString()));

    String PROCEDURE_STANDARD_DESC_URL = "standardURL";

    /**
     * Constant for the service name of the SOS
     */
    String SOS = "SOS";

    /**
     * String representing parameter value, if parameter is not set in an
     * operation request
     */
    @Deprecated
    String PARAMETER_NOT_SET = "NOT_SET";

    /**
     * String representing parameter value, if parameter is any in an operation
     * request
     */
    String PARAMETER_ANY = "ANY";

    /**
     * String representing parameter value, if parameter is no values in an
     * operation request
     */
    String PARAMETER_NO_VALUES = "NoValues";

    String NOT_DEFINED = "NOT_DEFINED";

    /**
     * request timeout in ms for split requests to SOS instances
     */
    long UPDATE_TIMEOUT = 10000;

    /**
     * Constant for actual implementing version Measurement
     */
    String OBS_ID_PREFIX = "o_";

    /**
     * Constant for actual implementing version OvservationCollection
     */
    String OBS_GENERIC_ID_PREFIX = "go_";

    /**
     * Constant for actual implementing version OvservationCollection
     */
    String OBS_COL_ID_PREFIX = "oc_";

    /**
     * Constant for actual implementing version ObservationTemplate
     */
    String OBS_TEMP_ID_PREFIX = "ot_";

    /**
     * Constant 'out-of-bands' for response mode, which means that the results
     * in an observation response appear external to the observation element
     */
    String RESPONSE_MODE_OUT_OF_BANDS = "out-of-bands";

    /**
     * Constant 'resultTemplate' for response mode, which means that the result
     * is an ObservationTemplate for subsequent calls to GetResult operation
     */
    String RESPONSE_RESULT_TEMPLATE = "resultTemplate";

    /**
     * Constant 'inline' for response mode, which means that results are
     * contained inline the Observation elements of an observation response
     * document
     */
    String RESPONSE_MODE_INLINE = "inline";

    /**
     * Constant 'attached' for response mode, which means that result values of
     * an observation response are attached as MIME attachments
     */
    String RESPONSE_MODE_ATTACHED = "attached";

    /**
     * Array of constants for response mode.
     */
    Set<String> RESPONSE_MODES = ImmutableSet.of(RESPONSE_MODE_INLINE, RESPONSE_RESULT_TEMPLATE);

    String MIN_VALUE = "MinValue";

    String MAX_VALUE = "MaxValue";

    String ALL_RELATED_FEATURES = "allFeatures";

    String SEPARATOR_4_REL_FEAT = "_._";

    String SEPARATOR_4_OFFERINGS = "_._";

    String SOAP_REASON_RESPONSE_EXCEEDS_SIZE_LIMIT =
            "The requested result set exceeds the response size limit of this service and thus cannot be delivered.";

    String SOAP_REASON_INVALID_PROPERTY_OFFERING_COMBINATION =
            "Observations for the requested combination of observedProperty and offering do not use SWE Common encoded results.";

    String GENERATED_IDENTIFIER_PREFIX = "generated_";

    /**
     * the names of the operations supported by all versions of the SOS
     * specification
     */
    enum Operations {
        GetCapabilities, GetObservation, GetObservationById, DescribeSensor, InsertObservation, GetResult, GetFeatureOfInterest;
    }

    enum Filter {
        ValueReference, TimePeriod, TimeInstant;
    }

    /**
     * enum with names of Capabilities sections supported by all versions
     */
    enum CapabilitiesSections {
        ServiceIdentification, ServiceProvider, OperationsMetadata, Contents, All;
    }

    /**
     * enum with parameter names for getCapabilities request
     */
    enum GetCapabilitiesParams {
        Sections,
        AcceptVersions,
        updateSequence,
        AcceptFormats,
        service,
        request,
        Section,
        CapabilitiesId;

        public static final String DYNAMIC_CAPABILITIES_IDENTIFIER = "dynamic";
    }

    /**
     * enum with parameter names for getObservation request supported by all
     * versions
     */
    enum GetObservationParams {
        srsName, resultType, startPosition, maxRecords, offering, procedure, observedProperty, featureOfInterest, result, responseFormat, resultModel, responseMode, SortBy, BBOX;
    }

    /**
     * enum with parameter names for getObservation request supported by all
     * versions
     */
    enum DescribeSensorParams {
        procedure;
    }
    
    enum SosIndeterminateTime {
        first, latest;
        
        private static final String GET_FIRST = "getFirst";

        public static boolean contains(final String timeString) {
            return timeString.equalsIgnoreCase(first.name()) || timeString.equalsIgnoreCase(latest.name()) || timeString.equalsIgnoreCase(GET_FIRST);
        }

        public static SosIndeterminateTime getEnumForString(final String value) {
            for (SosIndeterminateTime sit : values()) {
                if (sit.name().equalsIgnoreCase(value) || (GET_FIRST.equalsIgnoreCase(value) && sit.equals(first))) {
                    return sit;
                }
            }
            return null;
        }
    }

    // TODO add javadoc for each value
    enum HelperValues {
        GMLID, EXIST_FOI_IN_DOC, VERSION, TYPE,
        /**
         * Encode the given 'object to encode' in a <tt>*Document</tt> object
         * and not <tt>*Type</tt>.
         */
        DOCUMENT, PROPERTY_TYPE, FOR_OBSERVATION, ENCODE, ENCODE_NAMESPACE, REFERENCED,
        /**
         * Encode the given <tt>OwsExceptionReport</tt> not into an
         * <tt>ows:ExceptionReport</tt> but one <tt>ows:Exception</tt>.
         */
        ENCODE_OWS_EXCEPTION_ONLY
    }
}
