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
package org.n52.sos.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.n52.oxf.xml.NcNameResolver;
import org.n52.sos.binding.Binding;
import org.n52.sos.binding.BindingConstants;
import org.n52.sos.binding.BindingRepository;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.encode.Encoder;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.MissingParameterValueException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.InvalidResponseFormatParameterException;
import org.n52.sos.exception.ows.concrete.MissingResponseFormatParameterException;
import org.n52.sos.exception.sos.ResponseExceedsSizeLimitException;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.ows.OWSConstants;
import org.n52.sos.ogc.ows.OWSConstants.RequestParams;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.elements.SmlIo;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweConstants;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.ogc.swe.simpleType.SweTime;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.service.operator.ServiceOperatorKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Utility class for SOS
 *
 * @since 4.0.0
 *
 */
public class SosHelper implements Constants {

    private static Configuration config = new Configuration();

    private static final Logger LOGGER = LoggerFactory.getLogger(SosHelper.class);

    private static final int KILO_BYTE = 1024;

    private static final int KILO_BYTES_256 = 256 * KILO_BYTE;

    protected static Configuration getConfiguration() {
        return config;
    }

    protected static void setConfiguration(final Configuration config) {
        SosHelper.config = config;
    }

    private static String getBaseGetUrl(String serviceURL, String urlPattern) {
        final StringBuilder url = new StringBuilder();
        // service URL
        url.append(serviceURL);
        // URL pattern for KVP
        url.append(urlPattern);
        // ?
        url.append(Constants.QUERSTIONMARK_CHAR);
        return url.toString();
    }

    private static String getRequest(String requestName) {
        return new StringBuilder().append(RequestParams.request.name()).append(EQUAL_SIGN_CHAR).append(requestName)
                .toString();
    }

    private static String getServiceParam() {
        return new StringBuilder().append(AMPERSAND_CHAR).append(OWSConstants.RequestParams.service.name())
                .append(EQUAL_SIGN_CHAR).append(SosConstants.SOS).toString();
    }

    private static String getVersionParam(String version) {
        return new StringBuilder().append(AMPERSAND_CHAR).append(OWSConstants.RequestParams.version.name())
                .append(EQUAL_SIGN_CHAR).append(version).toString();
    }

    private static String getParameter(String name, String value) {
        return new StringBuilder().append(AMPERSAND_CHAR).append(name).append(EQUAL_SIGN_CHAR).append(value)
                .toString();
    }

    /**
     * Creates a HTTP-Get request for FeatureOfInterst without identifier
     *
     * @deprecated use {@link #createFoiGetUrl(String, String, String, String)}
     *
     * @param version
     *            SOS version
     * @param serviceURL
     *            Service URL
     * @return HTTP-Get request for FeatureOfInterest
     */
    @Deprecated
    public static String getFoiGetUrl(final String version, final String serviceURL, final String urlPattern) {
        final StringBuilder url = new StringBuilder();
        // service URL
        url.append(getBaseGetUrl(serviceURL, urlPattern));
        // request
        url.append(getRequest(SosConstants.Operations.GetFeatureOfInterest.name()));
        // service
        url.append(getServiceParam());
        // version
        url.append(getVersionParam(version));
        // FOI identifier
        if (version.equalsIgnoreCase(Sos1Constants.SERVICEVERSION)) {
            url.append(AMPERSAND_CHAR).append(Sos1Constants.GetFeatureOfInterestParams.featureOfInterestID.name())
                    .append(EQUAL_SIGN_CHAR);
        } else {
            url.append(AMPERSAND_CHAR).append(Sos2Constants.GetFeatureOfInterestParams.featureOfInterest.name())
                    .append(EQUAL_SIGN_CHAR);
        }

        return url.toString();
    }

    /**
     * Creates a HTTP-Get URL from FOI identifier and service URL for SOS
     * version
     *
     * @param foiId
     *            FeatureOfInterst identifier
     * @param version
     *            SOS version
     * @param serviceURL
     *            Service URL
     * @return HTTP-Get request for featureOfInterst identifier
     */
    public static String createFoiGetUrl(final String foiId, final String version, final String serviceURL,
            final String urlPattern) {
        final StringBuilder url = new StringBuilder();
        // service URL
        url.append(getBaseGetUrl(serviceURL, urlPattern));
        // request
        url.append(getRequest(SosConstants.Operations.GetFeatureOfInterest.name()));
        // service
        url.append(getServiceParam());
        // version
        url.append(getVersionParam(version));
        // FOI identifier
        if (version.equalsIgnoreCase(Sos1Constants.SERVICEVERSION)) {
            url.append(getParameter(Sos1Constants.GetFeatureOfInterestParams.featureOfInterestID.name(), foiId));
        } else {
            url.append(getParameter(Sos2Constants.GetFeatureOfInterestParams.featureOfInterest.name(), foiId));
        }
        return url.toString();
    }

    /**
     * creates a HTTP-GET string for DescribeSensor.
     *
     * @param version
     *            the version of the request
     * @param serviceURL
     *            the service url
     * @param procedureId
     *            The procedureId for the DescribeSensor request
     * @param procedureDescriptionFormat
     *            The procedureDescriptionFormat for the DescribeSensor request
     *
     * @param urlPattern
     *            the url pattern (e.g. /kvp)
     * @return Get-URL as String
     * @throws UnsupportedEncodingException
     */
    public static String getDescribeSensorUrl(final String version, final String serviceURL, final String procedureId,
            final String urlPattern, String procedureDescriptionFormat) throws UnsupportedEncodingException {
        final StringBuilder url = new StringBuilder();
        // service URL
        url.append(getBaseGetUrl(serviceURL, urlPattern));
        // request
        url.append(getRequest(SosConstants.Operations.DescribeSensor.name()));
        // service
        url.append(getServiceParam());
        // version
        url.append(getVersionParam(version));
        // procedure
        url.append(getParameter(SosConstants.DescribeSensorParams.procedure.name(), procedureId));
        // outputFormat
        if (version.equalsIgnoreCase(Sos1Constants.SERVICEVERSION)) {
            url.append(getParameter(Sos1Constants.DescribeSensorParams.outputFormat.name(),
                    URLEncoder.encode(procedureDescriptionFormat, "UTF-8")));
        } else {

            url.append(getParameter(Sos2Constants.DescribeSensorParams.procedureDescriptionFormat.name(),
                    URLEncoder.encode(procedureDescriptionFormat, "UTF-8")));
        }

        return url.toString();
    }

    public static String getGetObservationKVPRequest(String version) {
        final StringBuilder url = new StringBuilder();
        // service URL
        url.append(getBaseGetUrl(ServiceConfiguration.getInstance().getServiceURL(),
                BindingConstants.KVP_BINDING_ENDPOINT));
        // request
        url.append(getRequest(SosConstants.Operations.GetObservation.name()));
        // service
        url.append(getServiceParam());
        // version
        url.append(getVersionParam(version));
        return url.toString();
    }

    public static String addKVPOfferingParameterToRequest(String request, String offering) {
        if (StringHelper.isNotEmpty(offering)) {
            final StringBuilder url = new StringBuilder(request);
            url.append(getParameter(SosConstants.GetObservationParams.offering.name(), offering));
            return url.toString();
        }
        return request;
    }

    public static String addKVPLanguageParameterToRequest(String request, String language) {
        if (StringHelper.isNotEmpty(language)) {
            final StringBuilder url = new StringBuilder(request);
            url.append(getParameter(OWSConstants.AdditionalRequestParams.language.name(), language));
            return url.toString();
        }
        return request;
    }

    public static String addKVPCrsParameterToRequest(String request, String crs) {
        if (StringHelper.isNotEmpty(crs)) {
            final StringBuilder url = new StringBuilder(request);
            url.append(getParameter(OWSConstants.AdditionalRequestParams.crs.name(), crs));
            return url.toString();
        }
        return request;
    }

    public static String getGetCapabilitiesKVPRequest() {
        final StringBuilder url = new StringBuilder();
        // service URL
        url.append(getBaseGetUrl(ServiceConfiguration.getInstance().getServiceURL(),
                BindingConstants.KVP_BINDING_ENDPOINT));
        // request
        url.append(getRequest(SosConstants.Operations.GetCapabilities.name()));
        // service
        url.append(getServiceParam());
        return url.toString();
    }

    /**
     *
     * Parse the srsName to integer value
     *
     * @param srsName
     *            the srsName to parse
     * @return srsName integer value
     * @throws OwsExceptionReport
     *             If the srsName can not be parsed
     *
     */
    public static int parseSrsName(final String srsName) throws OwsExceptionReport {
        int srid = -1;
        if (StringHelper.isNotEmpty(srsName) && !"NOT_SET".equalsIgnoreCase(srsName)) {
            final String urnSrsPrefix = getConfiguration().getSrsNamePrefix();
            final String urlSrsPrefix = getConfiguration().getSrsNamePrefixSosV2();
            try {
                srid =
                        Integer.valueOf(srsName.replace(urnSrsPrefix, Constants.EMPTY_STRING).replace(urlSrsPrefix,
                                Constants.EMPTY_STRING));
            } catch (final NumberFormatException nfe) {
                throw new InvalidParameterValueException()
                        .causedBy(nfe)
                        .at(SosConstants.GetObservationParams.srsName)
                        .withMessage(
                                "Error while parsing srsName parameter! Parameter has to match "
                                        + "pattern '%s' or '%s' with appended EPSGcode number", urnSrsPrefix,
                                urlSrsPrefix);
            }
        }
        return srid;
    }

    /**
     * Checks the free memory size.
     *
     * @throws OwsExceptionReport
     *             If no free memory size.
     */
    public static void checkFreeMemory() throws OwsExceptionReport {
        Runtime runtime = Runtime.getRuntime();
        // check remaining free memory on heap if too small, throw exception to
        // avoid an OutOfMemoryError
        long freeMem = runtime.freeMemory();
        LOGGER.debug("Remaining Heap Size: " + (freeMem / KILO_BYTE) + "KB");
        if ((runtime.totalMemory() == runtime.maxMemory()) && (freeMem < KILO_BYTES_256)) {
            // accords to 256 kB create service exception
            throw new ResponseExceedsSizeLimitException().withMessage(
                    "The observation response is to big for the maximal heap size of %d Byte of the "
                            + "virtual machine! Please either refine your getObservation request to reduce the "
                            + "number of observations in the response or ask the administrator of this SOS to "
                            + "increase the maximum heap size of the virtual machine!", runtime.maxMemory());
        }
    }

    /**
     * Returns an Envelope that contains the Geometry
     *
     * @param envelope
     *            Current envelope
     * @param geometry
     *            Geometry to include
     * @return Envelope that includes the Geometry
     */
    public static Envelope checkEnvelope(final Envelope envelope, final Geometry geometry) {
        Envelope checkedEnvelope = envelope;
        if (checkedEnvelope == null) {
            checkedEnvelope = geometry.getEnvelopeInternal();
        } else if (!checkedEnvelope.contains(geometry.getEnvelopeInternal())) {
            checkedEnvelope.expandToInclude(geometry.getEnvelopeInternal());
        }
        return checkedEnvelope;
    }

    /**
     * Parses the HTTP-Post body with a parameter
     *
     * @param paramNames
     *            Parameter names
     * @param parameterMap
     *            Parameter map
     * @return Value of the parameter
     *
     * @throws OwsExceptionReport
     *             * If the parameter is not supported by this SOS.
     */
    public static String parseHttpPostBodyWithParameter(final Enumeration<?> paramNames, final Map<?, ?> parameterMap)
            throws OwsExceptionReport {
        while (paramNames.hasMoreElements()) {
            final String paramName = (String) paramNames.nextElement();
            if (RequestParams.request.name().equalsIgnoreCase(paramName)) {
                final String[] paramValues = (String[]) parameterMap.get(paramName);
                if (paramValues.length == 1) {
                    return paramValues[0];
                } else {
                    throw new NoApplicableCodeException()
                            .withMessage(
                                    "The parameter '%s' has more than one value or is empty for HTTP-Post requests by this SOS!",
                                    paramName);
                }
            } else {
                throw new NoApplicableCodeException().withMessage(
                        "The parameter '%s' is not supported for HTTP-Post requests by this SOS!", paramName);
            }
        }
        // FIXME: valid exception
        throw new NoApplicableCodeException();
    }

    /**
     * Checks if the FOI identifier was generated by SOS
     *
     * @param featureOfInterestIdentifier
     *            FOI identifier from database
     * @param version
     *            SOS version
     * @return True if the FOI identifier was generated
     */
    public static boolean checkFeatureOfInterestIdentifierForSosV2(final String featureOfInterestIdentifier,
            final String version) {
        return !(Sos2Constants.SERVICEVERSION.equals(version) && featureOfInterestIdentifier
                .startsWith(SosConstants.GENERATED_IDENTIFIER_PREFIX));
    }

    /**
     * get collection of hierarchy values for a key
     *
     * @param hierarchy
     *            map to example
     * @param key
     *            start key
     * @param fullHierarchy
     *            whether to traverse down the full hierarchy
     * @param includeStartKey
     *            whether to include the passed key in the result collection
     * @return collection of the full hierarchy
     */
    // FIXME move to ReadableCache
    public static Set<String> getHierarchy(final Map<String, Set<String>> hierarchy, final String key,
            final boolean fullHierarchy, final boolean includeStartKey) {
        final Set<String> hierarchyValues = Sets.newHashSet();
        if (includeStartKey) {
            hierarchyValues.add(key);
        }

        final Stack<String> keysToCheck = new Stack<String>();
        keysToCheck.push(key);

        while (!keysToCheck.isEmpty()) {
            final Collection<String> keyValues = hierarchy.get(keysToCheck.pop());
            if (keyValues != null) {
                for (final String value : keyValues) {
                    if (hierarchyValues.add(value) && fullHierarchy) {
                        keysToCheck.push(value);
                    }
                }
            }
        }

        return hierarchyValues;
    }

    /**
     * get collection of hierarchy values for a set of keys
     *
     * @param hierarchy
     *            map to example
     * @param keys
     *            start key
     * @param fullHierarchy
     *            whether to traverse down the full hierarchy
     * @param includeStartKeys
     *            whether to include the passed keys in the result collection
     *
     * @return collection of the full hierarchy
     */
    // FIXME move to ReadableCache
    public static Set<String> getHierarchy(final Map<String, Set<String>> hierarchy, final Set<String> keys,
            final boolean fullHierarchy, final boolean includeStartKeys) {
        final Set<String> parents = new HashSet<String>();
        for (final String key : keys) {
            parents.addAll(getHierarchy(hierarchy, key, fullHierarchy, includeStartKeys));
        }
        return parents;
    }

    /**
     * help method to check the result format parameter. If the application/zip
     * result format is set, true is returned. If not and the value is text/xml;
     * subtype="OM" false is returned. If neither zip nor OM is set, a
     * ServiceException with InvalidParameterValue as its code is thrown.
     *
     * @param responseFormat
     *            String containing the value of the result format parameter
     * @param service
     * @param version
     *
     * @throws OwsExceptionReport
     *             * if the parameter value is incorrect
     */
    public static void checkResponseFormat(final String responseFormat, final String service, final String version)
            throws OwsExceptionReport {
        if (Strings.isNullOrEmpty(responseFormat)) {
            throw new MissingResponseFormatParameterException();
        } else {
            final Collection<String> supportedResponseFormats =
                    CodingRepository.getInstance().getSupportedResponseFormats(service, version);
            if (!supportedResponseFormats.contains(responseFormat)) {
                throw new InvalidResponseFormatParameterException(responseFormat);
            }
        }
    }

    /**
     * checks whether the value of procedureDescriptionFormat parameter is valid
     *
     * @param procedureDescriptionFormat
     *            the procedureDecriptionFormat parameter which should be
     *            checked
     * @param service
     *            Service
     * @param version
     *            Service version
     * @throws OwsExceptionReport
     *             if the value of the procedureDecriptionFormat is incorrect
     */
    public static void checkProcedureDescriptionFormat(final String procedureDescriptionFormat, final String service,
            final String version) throws OwsExceptionReport {
        checkFormat(procedureDescriptionFormat, new ServiceOperatorKey(service, version),
                Sos2Constants.DescribeSensorParams.procedureDescriptionFormat);
    }

    /**
     * checks whether the value of outputFormat parameter is valid
     *
     * @param checkOutputFormat
     *            the outputFormat parameter which should be checked
     * @param service
     *            Service
     * @param version
     *            Service version
     * @throws OwsExceptionReport
     *             if the value of the outputFormat is incorrect
     */
    public static void checkOutputFormat(final String checkOutputFormat, final String service, final String version)
            throws OwsExceptionReport {
        checkFormat(checkOutputFormat, new ServiceOperatorKey(service, version),
                Sos1Constants.DescribeSensorParams.outputFormat);
    }

    /**
     * checks whether the value of procedure format parameter is valid
     *
     * @param format
     *            the procedure format parameter which should be checked
     * @param serviceOperatorKey
     *            Service and version
     * @param parameter
     *            name of the checked parameter
     * @throws OwsExceptionReport
     *             if the value of the procedure format is incorrect
     */
    private static void checkFormat(final String format, ServiceOperatorKey serviceOperatorKey, Enum<?> parameter)
            throws OwsExceptionReport {
        if (Strings.isNullOrEmpty(format)) {
            throw new MissingParameterValueException(parameter);
        } else {
            final Collection<String> supportedFormats =
                    CodingRepository.getInstance().getSupportedProcedureDescriptionFormats(serviceOperatorKey);
            if (!supportedFormats.contains(format)) {
                throw new InvalidParameterValueException(parameter, format);
            }
        }
    }

    /**
     * Get valid FOI identifiers for SOS 2.0
     *
     * @param featureIDs
     *            FOI identifiers to test
     * @param version
     *            SOS version
     * @return valid FOI identifiers
     */
    public static Collection<String> getFeatureIDs(final Collection<String> featureIDs, final String version) {
        if (Sos2Constants.SERVICEVERSION.equals(version)) {
            final Collection<String> validFeatureIDs = new ArrayList<String>(featureIDs.size());
            for (final String featureID : featureIDs) {
                if (checkFeatureOfInterestIdentifierForSosV2(featureID, version)) {
                    validFeatureIDs.add(featureID);
                }
            }
            return validFeatureIDs;
        }
        return featureIDs;
    }

    /**
     * Creates the minimum and maximum values of this envelope in the default
     * EPSG.
     * <p/>
     *
     * @param envelope
     *            the envelope
     *            <p/>
     * @return the {@code MinMax} describing the envelope
     *         <p/>
     */
    public static MinMax<String> getMinMaxFromEnvelope(final Envelope envelope) {
        // TODO for full 3D support add minz to parameter in setStringValue
        return new MinMax<String>().setMaximum(Joiner.on(' ').join(envelope.getMaxX(), envelope.getMaxY()))
                .setMinimum(Joiner.on(' ').join(envelope.getMinX(), envelope.getMinY()));
    }

    /**
     * Creates the minimum and maximum values of this envelope in the default
     * EPSG as list.
     * <p/>
     *
     * @param envelope
     *            the envelope
     *            <p/>
     * @return the {@code MinMax} describing the envelope
     *         <p/>
     */
    public static MinMax<List<String>> getMinMaxFromEnvelopeAsList(final Envelope envelope) {
        // TODO for full 3D support add minz to parameter in setStringValue
        return new MinMax<List<String>>().setMaximum(
                Lists.newArrayList(Double.toString(envelope.getMaxX()), Double.toString(envelope.getMaxY())))
                .setMinimum(
                        Lists.newArrayList(Double.toString(envelope.getMinX()), Double.toString(envelope.getMinY())));
    }

    public static OmObservableProperty createSosOberavablePropertyFromSosSMLIo(final SmlIo<?> output) {
        final SweAbstractDataComponent ioValue = output.getIoValue();
        final String identifier = ioValue.getDefinition();
        final String description = ioValue.getDescription();
        String unit = null;
        String valueType = SosConstants.NOT_DEFINED;
        switch (ioValue.getDataComponentType()) {
        case Boolean:
            valueType = SweConstants.VT_BOOLEAN;
            break;
        case Category:
            valueType = SweConstants.VT_CATEGORY;
            break;
        case Count:
            valueType = SweConstants.VT_COUNT;
            break;
        case CountRange:
            valueType = SweConstants.VT_COUNT_RANGE;
            break;
        case ObservableProperty:
            valueType = SweConstants.VT_OBSERVABLE_PROPERTY;
            break;
        case Quantity:
            unit = ((SweQuantity) ioValue).getUom();
            valueType = SweConstants.VT_QUANTITY;
            break;
        case QuantityRange:
            valueType = SweConstants.VT_QUANTITY_RANGE;
            break;
        case Text:
            valueType = SweConstants.VT_TEXT;
            break;
        case Time:
            unit = ((SweTime) ioValue).getUom();
            valueType = SweConstants.VT_TIME;
            break;
        case TimeRange:
            valueType = SweConstants.VT_TIME_RANGE;
            break;
        case DataArray:
            valueType = SweConstants.VT_DATA_ARRAY;
            break;
        case DataRecord:
            valueType = SweConstants.VT_DATA_RECORD;
            break;
        default:
            break;
        }
        if ((unit == null) || unit.isEmpty()) {
            unit = SosConstants.NOT_DEFINED;
        }
        return new OmObservableProperty(identifier, description, unit, valueType);
    }

    public static void checkHref(final String href, final String parameterName) throws OwsExceptionReport {
        if (!href.startsWith("http") && !href.startsWith("urn")) {
            throw new InvalidParameterValueException().at(parameterName).withMessage(
                    "The reference (href) has an invalid style!");
        }
    }

    public static String createCSVFromCodeTypeList(final List<CodeType> values) {
        final StringBuilder builder = new StringBuilder();
        if (CollectionHelper.isNotEmpty(values)) {
            for (final CodeType value : values) {
                builder.append(value.getValue());
                if (value.isSetCodeSpace()) {
                    builder.append(CSV_TOKEN_SEPARATOR);
                    builder.append(value.getCodeSpace());
                }
                builder.append(CSV_BLOCK_SEPARATOR);
            }
            builder.delete(builder.lastIndexOf(CSV_BLOCK_SEPARATOR), builder.length());
        }
        return builder.toString();
    }

    public static List<CodeType> createCodeTypeListFromCSV(final String csv) {
        final List<CodeType> names = new ArrayList<CodeType>(0);
        if (StringHelper.isNotEmpty(csv)) {
            for (final String nameCodespaces : csv.split(CSV_BLOCK_SEPARATOR)) {
                String[] nameCodespace = nameCodespaces.split(CSV_TOKEN_SEPARATOR);
                CodeType codeType = new CodeType(nameCodespace[0]);
                if (nameCodespace.length == 2) {
                    codeType.setCodeSpace(nameCodespace[1]);
                }
                names.add(codeType);
            }
        }
        return names;
    }

    /**
     * Hide utility constructor
     */
    protected SosHelper() {
    }

    /**
     * Class to encapsulate all calls to the {@link Configurator}. Can be
     * overwritten by tests.
     *
     * @see SosHelper#setConfiguration(org.n52.sos.util.SosHelper.Configuration)
     */
    protected static class Configuration {
        protected Collection<String> getObservationTypes() {
            return Configurator.getInstance().getCache().getObservationTypes();
        }

        protected String getSrsNamePrefix() {
            return ServiceConfiguration.getInstance().getSrsNamePrefix();
        }

        protected String getSrsNamePrefixSosV2() {
            return ServiceConfiguration.getInstance().getSrsNamePrefixSosV2();
        }

        protected Set<Encoder<?, ?>> getEncoders() {
            return CodingRepository.getInstance().getEncoders();
        }

        protected Collection<Binding> getBindings() {
            return BindingRepository.getInstance().getBindings().values();
        }
    }

    @Deprecated
    public static void checkSection(List<String> sections) throws CodedException {
        if (CollectionHelper.isEmpty(sections)) {
            throw new MissingParameterValueException(SosConstants.GetCapabilitiesParams.Section.name())
                    .withMessage("The section element is empty!");
        }
    }

    public static Map<String, String> getNcNameResolvedOfferings(Collection<String> offerings) {
        Map<String, String> resolvedOfferings = new HashMap<String, String>();
        for (String offering : offerings) {
            if (!NcNameResolver.isNCName(offering)) {
                resolvedOfferings.put(NcNameResolver.fixNcName(offering), offering);
            } else {
                resolvedOfferings.put(offering, offering);
            }
        }
        return resolvedOfferings;
    }
}
