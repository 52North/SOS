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
package org.n52.sos.encode;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.opengis.ows.x11.AddressType;
import net.opengis.ows.x11.AllowedValuesDocument.AllowedValues;
import net.opengis.ows.x11.CodeType;
import net.opengis.ows.x11.ContactType;
import net.opengis.ows.x11.DCPDocument;
import net.opengis.ows.x11.DomainType;
import net.opengis.ows.x11.ExceptionDocument;
import net.opengis.ows.x11.ExceptionReportDocument;
import net.opengis.ows.x11.ExceptionReportDocument.ExceptionReport;
import net.opengis.ows.x11.ExceptionType;
import net.opengis.ows.x11.HTTPDocument.HTTP;
import net.opengis.ows.x11.KeywordsType;
import net.opengis.ows.x11.LanguageStringType;
import net.opengis.ows.x11.MetadataType;
import net.opengis.ows.x11.OperationDocument.Operation;
import net.opengis.ows.x11.OperationsMetadataDocument.OperationsMetadata;
import net.opengis.ows.x11.RangeType;
import net.opengis.ows.x11.RequestMethodType;
import net.opengis.ows.x11.ResponsiblePartySubsetType;
import net.opengis.ows.x11.ServiceIdentificationDocument;
import net.opengis.ows.x11.ServiceIdentificationDocument.ServiceIdentification;
import net.opengis.ows.x11.ServiceProviderDocument;
import net.opengis.ows.x11.ServiceProviderDocument.ServiceProvider;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.xmlbeans.XmlObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3.x1999.xlink.ActuateType;
import org.w3.x1999.xlink.ShowType;
import org.w3.x1999.xlink.TypeType;
import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.config.annotation.Setting;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.OwsExceptionCode;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.i18n.LocaleHelper;
import org.n52.sos.i18n.LocalizedString;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.Constraint;
import org.n52.sos.ogc.ows.DCP;
import org.n52.sos.ogc.ows.OWSConstants;
import org.n52.sos.ogc.ows.OwsAllowedValues;
import org.n52.sos.ogc.ows.OwsAllowedValuesRange;
import org.n52.sos.ogc.ows.OwsAllowedValuesValue;
import org.n52.sos.ogc.ows.OwsAnyValue;
import org.n52.sos.ogc.ows.OwsDomainType;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsMetadata;
import org.n52.sos.ogc.ows.OwsNoValues;
import org.n52.sos.ogc.ows.OwsOperation;
import org.n52.sos.ogc.ows.OwsOperationsMetadata;
import org.n52.sos.ogc.ows.OwsParameterDataType;
import org.n52.sos.ogc.ows.OwsParameterValue;
import org.n52.sos.ogc.ows.OwsParameterValuePossibleValues;
import org.n52.sos.ogc.ows.OwsParameterValueRange;
import org.n52.sos.ogc.ows.OwsPossibleValues;
import org.n52.sos.ogc.ows.OwsRange;
import org.n52.sos.ogc.ows.OwsValuesRererence;
import org.n52.sos.ogc.ows.SosServiceIdentification;
import org.n52.sos.ogc.ows.SosServiceProvider;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.JavaHelper;
import org.n52.sos.util.N52XmlHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.util.http.HTTPMethods;
import org.n52.sos.util.http.MediaTypes;
import org.n52.sos.w3c.SchemaLocation;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


/**
 * @since 4.0.0
 *
 */
@Configurable
public class OwsEncoderv110 extends AbstractXmlEncoder<Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OwsEncoderv110.class);

    @SuppressWarnings("unchecked")
    private static final Set<EncoderKey> ENCODER_KEYS = CollectionHelper.union(Sets.<EncoderKey> newHashSet(
            new ExceptionEncoderKey(MediaTypes.TEXT_XML), new ExceptionEncoderKey(MediaTypes.APPLICATION_XML)),
            CodingHelper.encoderKeysForElements(OWSConstants.NS_OWS, SosServiceIdentification.class,
                    SosServiceProvider.class, OwsOperationsMetadata.class, OwsExceptionReport.class,
                    OwsMetadata.class, OwsDomainType.class));

    private boolean includeStackTraceInExceptionReport = false;

    public OwsEncoderv110() {
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!", Joiner.on(", ")
                .join(ENCODER_KEYS));
    }

    @Setting(OwsEncoderSettings.INCLUDE_STACK_TRACE_IN_EXCEPTION_REPORT)
    public void setIncludeStackTrace(final boolean includeStackTraceInExceptionReport) {
        this.includeStackTraceInExceptionReport = includeStackTraceInExceptionReport;
    }

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return Collections.unmodifiableSet(ENCODER_KEYS);
    }

    @Override
    public void addNamespacePrefixToMap(final Map<String, String> nameSpacePrefixMap) {
        nameSpacePrefixMap.put(OWSConstants.NS_OWS, OWSConstants.NS_OWS_PREFIX);
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Sets.newHashSet(OWSConstants.OWS_110_SCHEMA_LOCATION);
    }

    @Override
    public XmlObject encode(final Object element, final Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport {
        if (element instanceof SosServiceIdentification) {
            return encodeServiceIdentification((SosServiceIdentification) element);
        } else if (element instanceof SosServiceProvider) {
            return encodeServiceProvider((SosServiceProvider) element);
        } else if (element instanceof OwsOperationsMetadata) {
            return encodeOperationsMetadata((OwsOperationsMetadata) element);
        } else if (element instanceof OwsExceptionReport) {
            if (isEncodeExceptionsOnly(additionalValues) && !((OwsExceptionReport) element).getExceptions().isEmpty()) {
                return encodeOwsException(((OwsExceptionReport) element).getExceptions().get(0));
            }
            return encodeOwsExceptionReport((OwsExceptionReport) element);
        } else if (element instanceof OwsMetadata) {
            return encodeOwsMetadata((OwsMetadata) element);
        } else if (element instanceof OwsDomainType) {
            return encodeDomainType((OwsDomainType) element);
        }
        throw new UnsupportedEncoderInputException(this, element);
    }

    protected boolean isEncodeExceptionsOnly(final Map<HelperValues, String> additionalValues) {
        return additionalValues != null && !additionalValues.isEmpty()
                && additionalValues.containsKey(SosConstants.HelperValues.ENCODE_OWS_EXCEPTION_ONLY);
    }

    /**
     * Set the service identification information
     *
     * @param sosServiceIdentification
     *            SOS representation of ServiceIdentification.
     *
     * @throws OwsExceptionReport
     *             * if the file is invalid.
     */
    private XmlObject encodeServiceIdentification(final SosServiceIdentification sosServiceIdentification)
            throws OwsExceptionReport {
        ServiceIdentification serviceIdent;
        if (sosServiceIdentification.getServiceIdentification() != null) {

            if (sosServiceIdentification.getServiceIdentification() instanceof ServiceIdentificationDocument) {
                serviceIdent =
                        ((ServiceIdentificationDocument) sosServiceIdentification.getServiceIdentification())
                                .getServiceIdentification();
            } else if (sosServiceIdentification.getServiceIdentification() instanceof ServiceIdentification) {
                serviceIdent = (ServiceIdentification) sosServiceIdentification.getServiceIdentification();
            } else {
                throw new NoApplicableCodeException()
                        .withMessage("The service identification file is not a ServiceIdentificationDocument, ServiceIdentification or invalid! Check the file in the Tomcat webapps: /SOS_webapp/WEB-INF/conf/capabilities/.");
            }
            if (sosServiceIdentification.hasAbstract()) {
                clearAbstracts(serviceIdent);
                for (LocalizedString ls : sosServiceIdentification.getAbstract()) {
                    serviceIdent.addNewAbstract().set(encodeOwsLanguageString(ls));
                }
            }
            if (sosServiceIdentification.hasTitle()) {
                clearTitles(serviceIdent);
                for (LocalizedString ls : sosServiceIdentification.getTitle()) {
                    serviceIdent.addNewTitle().set(encodeOwsLanguageString(ls));
                }
            }
        } else {
            /* TODO check for required fields and fail on missing ones */
            serviceIdent = ServiceIdentification.Factory.newInstance();
            for (String accessConstraint : sosServiceIdentification.getAccessConstraints()) {
                serviceIdent.addAccessConstraints(accessConstraint);
            }
            if (sosServiceIdentification.hasFees()) {
            	serviceIdent.setFees(sosServiceIdentification.getFees());
            }
            CodeType xbServiceType = serviceIdent.addNewServiceType();
            xbServiceType.setStringValue(sosServiceIdentification.getServiceType());
            if (sosServiceIdentification.getServiceTypeCodeSpace() != null) {
                xbServiceType.setCodeSpace(sosServiceIdentification.getServiceTypeCodeSpace());
            }
            if (sosServiceIdentification.hasAbstract()) {
                for (LocalizedString ls : sosServiceIdentification.getAbstract()) {
                    serviceIdent.addNewAbstract().set(encodeOwsLanguageString(ls));
                }
            }
            if (sosServiceIdentification.hasTitle()) {
                for (LocalizedString ls : sosServiceIdentification.getTitle()) {
                    serviceIdent.addNewTitle().set(encodeOwsLanguageString(ls));
                }
            }
        }
        // set service type versions
        if (sosServiceIdentification.hasVersions()) {
            serviceIdent.setServiceTypeVersionArray(sosServiceIdentification.getVersions().toArray(
                    new String[sosServiceIdentification.getVersions().size()]));
        }

        // set Profiles
        if (sosServiceIdentification.hasProfiles()) {
            serviceIdent.setProfileArray(sosServiceIdentification.getProfiles().toArray(
                    new String[sosServiceIdentification.getProfiles().size()]));
        }
        // set keywords if they're not already in the service identification
        // doc
        if (sosServiceIdentification.hasKeywords() && serviceIdent.getKeywordsArray().length == 0) {
            final KeywordsType keywordsType = serviceIdent.addNewKeywords();
            for (final String keyword : sosServiceIdentification.getKeywords()) {
                keywordsType.addNewKeyword().setStringValue(keyword.trim());
            }
        }
        return serviceIdent;
    }

    private void clearTitles(ServiceIdentification serviceIdent) {
        if (CollectionHelper.isNotNullOrEmpty(serviceIdent.getTitleArray())) {
            for (int i = 0; i < serviceIdent.getTitleArray().length; i++) {
                serviceIdent.removeTitle(i);
            }
        }
    }

    private void clearAbstracts(ServiceIdentification serviceIdent) {
        if (CollectionHelper.isNotNullOrEmpty(serviceIdent.getAbstractArray())) {
            for (int i = 0; i < serviceIdent.getAbstractArray().length; i++) {
                serviceIdent.removeAbstract(i);
            }
        }
    }

    /**
     * Set the service provider information
     *
     * @param sosServiceProvider
     *            SOS representation of ServiceProvider.
     *
     * @throws OwsExceptionReport
     *             * if the file is invalid.
     */
    private XmlObject encodeServiceProvider(final SosServiceProvider sosServiceProvider) throws OwsExceptionReport {
        if (sosServiceProvider.getServiceProvider() != null) {
            if (sosServiceProvider.getServiceProvider() instanceof ServiceProviderDocument) {
                return ((ServiceProviderDocument) sosServiceProvider.getServiceProvider()).getServiceProvider();
            } else if (sosServiceProvider.getServiceProvider() instanceof ServiceProvider) {
                return sosServiceProvider.getServiceProvider();
            } else {
                throw new NoApplicableCodeException()
                        .withMessage("The service identification file is not a ServiceProviderDocument, "
                                + "ServiceProvider or invalid! Check the file in the Tomcat webapps: "
                                + "/SOS_webapp/WEB-INF/conf/capabilities/.");
            }
        } else {
            /* TODO check for required fields and fail on missing ones */
            final ServiceProvider serviceProvider = ServiceProvider.Factory.newInstance();
            if (sosServiceProvider.getName() != null) {
                serviceProvider.setProviderName(sosServiceProvider.getName());
            }
            if (sosServiceProvider.getSite() != null) {
                serviceProvider.addNewProviderSite().setHref(sosServiceProvider.getSite());
            }
            final ResponsiblePartySubsetType responsibleParty = serviceProvider.addNewServiceContact();
            if (sosServiceProvider.getIndividualName() != null) {
                responsibleParty.setIndividualName(sosServiceProvider.getIndividualName());
            }
            if (sosServiceProvider.getPositionName() != null) {
                responsibleParty.setPositionName(sosServiceProvider.getPositionName());
            }

            final ContactType contact = responsibleParty.addNewContactInfo();
            if (sosServiceProvider.getPhone() != null) {
                contact.addNewPhone().addVoice(sosServiceProvider.getPhone());
            }

            final AddressType address = contact.addNewAddress();
            if (sosServiceProvider.getDeliveryPoint() != null) {
                address.addDeliveryPoint(sosServiceProvider.getDeliveryPoint());
            }
            if (sosServiceProvider.getMailAddress() != null) {
                address.addElectronicMailAddress(sosServiceProvider.getMailAddress());
            }
            if (sosServiceProvider.getAdministrativeArea() != null) {
                address.setAdministrativeArea(sosServiceProvider.getAdministrativeArea());
            }
            if (sosServiceProvider.getCity() != null) {
                address.setCity(sosServiceProvider.getCity());
            }
            if (sosServiceProvider.getCountry() != null) {
                address.setCountry(sosServiceProvider.getCountry());
            }
            if (sosServiceProvider.getPostalCode() != null) {
                address.setPostalCode(sosServiceProvider.getPostalCode());
            }
            return serviceProvider;
        }

    }

    /**
     * Sets the OperationsMetadata section to the capabilities document.
     *
     * @param operationsMetadata
     *            SOS metadatas for the operations
     *
     *
     * @throws CompositeOwsException
     *             * if an error occurs
     */
    protected OperationsMetadata encodeOperationsMetadata(final OwsOperationsMetadata operationsMetadata)
            throws OwsExceptionReport {
        final OperationsMetadata xbOperationMetadata =
                OperationsMetadata.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        for (final OwsOperation operationMetadata : operationsMetadata.getOperations()) {
            final Operation operation = xbOperationMetadata.addNewOperation();
            // name
            operation.setName(operationMetadata.getOperationName());
            // dcp
            encodeDCP(operation.addNewDCP(), operationMetadata.getDcp());
            // parameter
            if (operationMetadata.getParameterValues() != null) {
                for (final String parameterName : operationMetadata.getParameterValues().keySet()) {
                    setParameterValue(operation.addNewParameter(), parameterName, operationMetadata
                            .getParameterValues().get(parameterName));
                }
            }
        }
        // set SERVICE and VERSION for all operations.
        for (final String name : operationsMetadata.getCommonValues().keySet()) {
            setParameterValue(xbOperationMetadata.addNewParameter(), name,
                    operationsMetadata.getCommonValues().get(name));
        }

        if (operationsMetadata.isSetExtendedCapabilities()) {
            xbOperationMetadata.setExtendedCapabilities(CodingHelper.encodeObjectToXml(operationsMetadata
                    .getExtendedCapabilities().getNamespace(), operationsMetadata.getExtendedCapabilities()));
        }
        return xbOperationMetadata;
    }

    private ExceptionDocument encodeOwsException(final CodedException owsException) {
        final ExceptionDocument exceptionDoc =
                ExceptionDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        final ExceptionType exceptionType = exceptionDoc.addNewException();
        String exceptionCode;
        if (owsException.getCode() == null) {
            exceptionCode = OwsExceptionCode.NoApplicableCode.toString();
        } else {
            exceptionCode = owsException.getCode().toString();
        }
        exceptionType.setExceptionCode(exceptionCode);
        if (owsException.getLocator() != null) {
            exceptionType.setLocator(owsException.getLocator());
        }
        final StringBuilder exceptionText = new StringBuilder();
        if (owsException.getMessage() != null) {
            exceptionText.append(owsException.getMessage());
            exceptionText.append("\n");
        }
        if (owsException.getCause() != null) {
            exceptionText.append("[EXEPTION]: \n");
            final String localizedMessage = owsException.getCause().getLocalizedMessage();
            final String message = owsException.getCause().getMessage();
            if (localizedMessage != null && message != null) {
                if (!message.equals(localizedMessage)) {
                    JavaHelper.appendTextToStringBuilderWithLineBreak(exceptionText, message);
                }
                JavaHelper.appendTextToStringBuilderWithLineBreak(exceptionText, localizedMessage);
            } else {
                JavaHelper.appendTextToStringBuilderWithLineBreak(exceptionText, localizedMessage);
                JavaHelper.appendTextToStringBuilderWithLineBreak(exceptionText, message);
            }
            if (includeStackTraceInExceptionReport) {
                final ByteArrayOutputStream os = new ByteArrayOutputStream();
                owsException.getCause().printStackTrace(new PrintStream(os));
                exceptionText.append(os.toString());
            }
        }
        exceptionType.addExceptionText(exceptionText.toString());
        return exceptionDoc;
    }

    private void addExceptionMessages(StringBuilder exceptionText, Throwable exception) {
        exceptionText.append("[EXCEPTION]: \n");
        final String localizedMessage = exception.getLocalizedMessage();
        final String message = exception.getMessage();
        if (localizedMessage != null && message != null) {
            if (!message.equals(localizedMessage)) {
                JavaHelper.appendTextToStringBuilderWithLineBreak(exceptionText, message);
            }
            JavaHelper.appendTextToStringBuilderWithLineBreak(exceptionText, localizedMessage);
        } else {
            JavaHelper.appendTextToStringBuilderWithLineBreak(exceptionText, localizedMessage);
            JavaHelper.appendTextToStringBuilderWithLineBreak(exceptionText, message);
        }

        //recurse cause if necessary
        if (exception.getCause() != null) {
            exceptionText.append("[CAUSED BY]\n");
            addExceptionMessages(exceptionText, exception.getCause());
        }
        
        //recurse SQLException if necessary
        if (exception instanceof SQLException) {
            SQLException sqlException = (SQLException) exception;
            if (sqlException.getNextException() != null) {
                exceptionText.append("[NEXT SQL EXCEPTION]\n");
                addExceptionMessages(exceptionText, sqlException.getNextException());
            }
        }
    }
    
    private ExceptionReportDocument encodeOwsExceptionReport(final OwsExceptionReport owsExceptionReport) {
        final ExceptionReportDocument erd =
                ExceptionReportDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        final ExceptionReport er = erd.addNewExceptionReport();
        // er.setLanguage("en");
        er.setVersion(owsExceptionReport.getVersion());
        final List<ExceptionType> exceptionTypes =
                new ArrayList<ExceptionType>(owsExceptionReport.getExceptions().size());
        for (final CodedException e : owsExceptionReport.getExceptions()) {
            exceptionTypes.add(encodeOwsException(e).getException());
        }
        er.setExceptionArray(exceptionTypes.toArray(new ExceptionType[exceptionTypes.size()]));
        N52XmlHelper.setSchemaLocationsToDocument(erd,
                Collections.singletonList(N52XmlHelper.getSchemaLocationForOWS110()));
        return erd;
    }

    private void encodeDCP(final DCPDocument.DCP xbDcp, final Map<String, ? extends Collection<DCP>> supportedDcp)
            throws OwsExceptionReport {
        final HTTP http = xbDcp.addNewHTTP();
        if (supportedDcp.containsKey(HTTPMethods.GET)) {
            List<DCP> getDcps = Lists.newArrayList(supportedDcp.get(HTTPMethods.GET));
            Collections.sort(getDcps);
            for (DCP dcp : getDcps) {
                RequestMethodType get = http.addNewGet();
                get.setHref(dcp.getUrl());
                addConstraints(get, dcp);

            }
        }
        if (supportedDcp.containsKey(HTTPMethods.POST)) {
            List<DCP> postDcps = Lists.newArrayList(supportedDcp.get(HTTPMethods.POST));
            Collections.sort(postDcps);
            for (DCP dcp : postDcps) {
                RequestMethodType post = http.addNewPost();
                post.setHref(dcp.getUrl());
                addConstraints(post, dcp);
            }
        }
        // TODO add if ows supports more than get and post
    }

    /**
     * Encode OWS DomainType
     *
     * @param owsDomainType
     *            Service OWS DomainType
     * @return XML OWS DomainType
     */
    private DomainType encodeDomainType(OwsDomainType owsDomainType) {
        DomainType domainType = DomainType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        domainType.setName(owsDomainType.getName());
        addPossibleValues(domainType, owsDomainType.getValue());
        if (owsDomainType.isSetDefaultValue()) {
            domainType.addNewDefaultValue().setStringValue(owsDomainType.getDefaultValue());
        }
        return domainType;
    }

    /**
     * Add XML OWS PossibleValues to XML OWS DomainType
     *
     * @param domainType
     *            XML OWS DomainType
     * @param value
     *            Service OWS PossibleValues to add
     */
    private void addPossibleValues(DomainType domainType, OwsPossibleValues value) {
        if (value instanceof OwsAnyValue) {
            domainType.addNewAnyValue();
        } else if (value instanceof OwsNoValues) {
            domainType.addNewNoValues();
        } else if (value instanceof OwsAllowedValues) {
            addAllowedValues(domainType, (OwsAllowedValues) value);
        } else if (value instanceof OwsValuesRererence) {
            OwsValuesRererence owsValueReference = (OwsValuesRererence) value;
            domainType.addNewValuesReference().setReference(owsValueReference.getReference());
        }
    }

    /**
     * Add XML OWS AllowedValues to XML OWS DomainType
     *
     * @param domainType
     *            XML OWS DomainType
     * @param value
     *            Service OWS AllowedValues to add
     */
    private void addAllowedValues(DomainType domainType, OwsAllowedValues value) {
        AllowedValues allowedValues = domainType.addNewAllowedValues();
        if (value instanceof OwsAllowedValuesValue) {
            for (String stringValue : ((OwsAllowedValuesValue) value).getValues()) {
                allowedValues.addNewValue().setStringValue(stringValue);
            }
        } else if (value instanceof OwsAllowedValuesRange) {
            for (OwsRange owsRange : ((OwsAllowedValuesRange) value).getValues()) {
                RangeType range = allowedValues.addNewRange();
                if (owsRange.isSetMinValue()) {
                    range.addNewMinimumValue().setStringValue(owsRange.getMinValue());
                }
                if (owsRange.isSetMaxValue()) {
                    range.addNewMaximumValue().setStringValue(owsRange.getMaxValue());
                }
                if (owsRange.isSetSpacing()) {
                    range.addNewSpacing().setStringValue(owsRange.getSpacing());
                }
            }
        }
    }

    private void setParameterValue(DomainType domainType, String name, Collection<OwsParameterValue> values)
            throws OwsExceptionReport {
        domainType.setName(name);
        if (CollectionHelper.isEmpty(values)) {
            domainType.addNewNoValues();
        } else {
            for (OwsParameterValue value : values) {
                if (value instanceof OwsParameterValuePossibleValues) {
                    setParamList(domainType, (OwsParameterValuePossibleValues) value);
                } else if (value instanceof OwsParameterValueRange) {
                    setParamRange(domainType, (OwsParameterValueRange) value);
                } else if (value instanceof OwsParameterDataType) {
                    setParamDataType(domainType, (OwsParameterDataType) value);
                }
            }
        }
    }

    /**
     * Sets operation parameters to AnyValue, NoValues or AllowedValues.
     *
     * @param domainType
     *            Paramter.
     * @param parameterValue
     *            .getValues() List of values.
     */
    private void setParamList(final DomainType domainType, final OwsParameterValuePossibleValues parameterValue) {
        if (parameterValue.getValues() != null) {
            if (!parameterValue.getValues().isEmpty()) {
                AllowedValues allowedValues = null;
                for (final String value : parameterValue.getValues()) {
                    if (value == null) {
                        domainType.addNewNoValues();
                        break;
                    } else {
                        if (allowedValues == null) {
                            allowedValues = domainType.addNewAllowedValues();
                        }
                        allowedValues.addNewValue().setStringValue(value);
                    }
                }
            } else {
                domainType.addNewAnyValue();
            }
        } else {
            domainType.addNewNoValues();
        }
    }

    private void setParamDataType(final DomainType domainType, final OwsParameterDataType parameterValue) {
        if (parameterValue.getReference() != null && !parameterValue.getReference().isEmpty()) {
            domainType.addNewDataType().setReference(parameterValue.getReference());
        } else {
            domainType.addNewNoValues();
        }

    }

    /**
     * Sets the EventTime parameter.
     *
     * @param domainType
     *            Parameter.
     * @param parameterValue
     *
     *
     * @throws CompositeOwsException
     */
    private void setParamRange(final DomainType domainType, final OwsParameterValueRange parameterValue)
            throws OwsExceptionReport {
        if (parameterValue.getMinValue() != null && parameterValue.getMaxValue() != null) {
            if (!parameterValue.getMinValue().isEmpty() && !parameterValue.getMaxValue().isEmpty()) {
                final RangeType range = domainType.addNewAllowedValues().addNewRange();
                range.addNewMinimumValue().setStringValue(parameterValue.getMinValue());
                range.addNewMaximumValue().setStringValue(parameterValue.getMaxValue());
            } else {
                domainType.addNewAnyValue();
            }
        } else {
            domainType.addNewNoValues();
        }
    }

    private void addConstraints(RequestMethodType method, DCP dcp) throws OwsExceptionReport {
        for (Constraint c : dcp.getConstraints()) {
            setParameterValue(method.addNewConstraint(), c.getName(), c.getValues());
        }
    }

    /**
     * Encode OwsMetadata element
     *
     * @param owsMeatadata
     *            SOS OwsMetadata object
     * @return XML OwsMetadata object
     */
    private XmlObject encodeOwsMetadata(OwsMetadata owsMeatadata) {
        MetadataType xbMetadata = MetadataType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        if (owsMeatadata.isSetActuate()) {
            xbMetadata.setActuate(ActuateType.Enum.forString(owsMeatadata.getActuate().name()));
        } else if (owsMeatadata.isSetArcrole()) {
            xbMetadata.setArcrole(owsMeatadata.getArcrole());
        } else if (owsMeatadata.isSetHref()) {
            xbMetadata.setHref(owsMeatadata.getHref());
        } else if (owsMeatadata.isSetRole()) {
            xbMetadata.setRole(owsMeatadata.getRole());
        } else if (owsMeatadata.isSetShow()) {
            xbMetadata.setShow(ShowType.Enum.forString(owsMeatadata.getShow().toString()));
        } else if (owsMeatadata.isSetTitle()) {
            xbMetadata.setTitle(owsMeatadata.getTitle());
        }
        xbMetadata.setType(TypeType.Enum.forString(owsMeatadata.getType().name()));
        return xbMetadata;
    }

    private LanguageStringType encodeOwsLanguageString(LocalizedString ls) {
        LanguageStringType lst = LanguageStringType.Factory.newInstance();
        lst.setStringValue(ls.getText());
        lst.setLang(LocaleHelper.toString(ls.getLang()));
        return lst;
    }
}
