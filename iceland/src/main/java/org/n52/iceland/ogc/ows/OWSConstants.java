/**
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.ogc.ows;

import javax.xml.namespace.QName;

import org.n52.iceland.exception.ows.OwsExceptionCode;
import org.n52.iceland.w3c.SchemaLocation;

/**
 * Constants for OWS.
 * 
 * @since 4.0.0
 */
public interface OWSConstants {

    // namespace and schema locations
    String NS_OWS = "http://www.opengis.net/ows/1.1";

    String NS_OWS_PREFIX = "ows";

    String SCHEMA_LOCATION_URL_OWS = "http://schemas.opengis.net/ows/1.1.0/owsAll.xsd";

    String SCHEMA_LOCATION_URL_OWS_EXCEPTIONREPORT = "http://schemas.opengis.net/ows/1.1.0/owsExceptionReport.xsd";

    SchemaLocation OWS_110_SCHEMA_LOCATION = new SchemaLocation(NS_OWS, SCHEMA_LOCATION_URL_OWS);

    SchemaLocation OWS_110_EXCEPTION_REPORT_SCHEMA_LOCATION = new SchemaLocation(NS_OWS_PREFIX,
            SCHEMA_LOCATION_URL_OWS_EXCEPTIONREPORT);

    // exception messages
    String SOAP_REASON_INVALID_PARAMETER_VALUE = "The request contained an invalid parameter value.";

    String SOAP_REASON_INVALID_UPDATE_SEQUENCES =
            "The value of the updateSequence parameter in the GetCapabilities operation request was greater than the current value of the service metadata updateSequence number.";

    String SOAP_REASON_MISSING_PARAMETER_VALUE =
            "The request did not include a value for a required parameter and this server does not declare a default value for it.";

    String SOAP_REASON_NO_APPLICABLE_CODE = "A server exception was encountered.";

    String SOAP_REASON_NO_DATA_AVAILABLE = "There are no data available.";

    String SOAP_REASON_OPERATION_NOT_SUPPORTED = "The requested operation is not supported by this server.";

    String SOAP_REASON_OPTION_NOT_SUPPORTED =
            "The request included/targeted an option that is not supported by this server.";

    String SOAP_REASON_REQUEST_EXTENSION_NOT_SUPPORTED =
            "The request included an extension that is not supported by this server.";

    String SOAP_REASON_VERSION_NEGOTIATION_FAILED =
            "The list of versions in the ‘AcceptVersions’ parameter value of the GetCapabilities operation request did not include any version supported by this server.";

    String SOAP_REASON_RESPONSE_EXCEEDS_SIZE_LIMIT =
            "The requested result set exceeds the response size limit of this service and thus cannot be delivered.";

    String SOAP_REASON_INVALID_PROPERTY_OFFERING_COMBINATION =
            "Observations for the requested combination of observedProperty and offering do not use SWE Common encoded results.";

    String SOAP_REASON_UNKNOWN = "A server exception was encountered.";

    String EN_EXCEPTION = "Exception";

    String EN_EXCEPTION_CODE = "exceptionCode";

    String EN_LOCATOR = "locator";

    String EN_EXCEPTION_TEXT = "ExceptionText";

    QName QN_EXCEPTION = new QName(NS_OWS, EN_EXCEPTION, NS_OWS_PREFIX);

    QName QN_EXCEPTION_TEXT = new QName(NS_OWS, EN_EXCEPTION_TEXT, NS_OWS_PREFIX);

    QName QN_NO_APPLICABLE_CODE = new QName(NS_OWS, OwsExceptionCode.NoApplicableCode.name(), NS_OWS_PREFIX);

    String VERSION = "1.1.0";
    
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
     * the names of the operations supported by all versions of the service
     * specification
     */
    enum Operations {
        GetCapabilities;
    }

    /**
     * Enumeration for related feature role
     * 
     */
    enum RelatedFeatureRole {
        featureOfInterestID, relatedFeatureID
    }

    /** enum with names of get request parameters for all requests */
    enum RequestParams {
        request, service, version;

        /**
         * method checks whether the string parameter is contained in this
         * enumeration
         * 
         * @param s
         *            the name which should be checked
         * @return true if the name is contained in the enumeration
         */
        public static boolean contains(String s) {
            for (Enum<?> p : values()) {
                if (p.name().equals(s)) {
                    return true;
                }
            }
            return false;
        }
    }
    
    /** enum with names of get request parameters for all requests */
    enum AdditionalRequestParams {
        language, crs, returnHumanReadableIdentifier;

        /**
         * method checks whether the string parameter is contained in this
         * enumeration
         * 
         * @param s
         *            the name which should be checked
         * @return true if the name is contained in the enumeration
         */
        public static boolean contains(String s) {
            for (Enum<?> p : values()) {
                if (p.name().equals(s)) {
                    return true;
                }
            }
            return false;
        }
    }
    
    enum ExtendedIndeterminateTime {
        first, latest;
        
        private static final String GET_FIRST = "getFirst";

        public static boolean contains(final String timeString) {
            return timeString.equalsIgnoreCase(first.name()) || timeString.equalsIgnoreCase(latest.name()) || timeString.equalsIgnoreCase(GET_FIRST);
        }

        public static ExtendedIndeterminateTime getEnumForString(final String value) {
            for (ExtendedIndeterminateTime sit : values()) {
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
