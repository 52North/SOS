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
package org.n52.sos.encode;


import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.Supplier;
import java.util.stream.Stream;

import net.opengis.ows.x11.AcceptVersionsType;
import net.opengis.ows.x11.AddressType;
import net.opengis.ows.x11.AllowedValuesDocument.AllowedValues;
import net.opengis.ows.x11.CodeType;
import net.opengis.ows.x11.ContactType;
import net.opengis.ows.x11.DCPDocument.DCP;
import net.opengis.ows.x11.DomainMetadataType;
import net.opengis.ows.x11.DomainType;
import net.opengis.ows.x11.ExceptionDocument;
import net.opengis.ows.x11.ExceptionReportDocument;
import net.opengis.ows.x11.ExceptionReportDocument.ExceptionReport;
import net.opengis.ows.x11.ExceptionType;
import net.opengis.ows.x11.HTTPDocument.HTTP;
import net.opengis.ows.x11.KeywordsType;
import net.opengis.ows.x11.LanguageStringType;
import net.opengis.ows.x11.MetadataType;
import net.opengis.ows.x11.OnlineResourceType;
import net.opengis.ows.x11.OperationDocument.Operation;
import net.opengis.ows.x11.OperationsMetadataDocument.OperationsMetadata;
import net.opengis.ows.x11.RangeType;
import net.opengis.ows.x11.RequestMethodType;
import net.opengis.ows.x11.ResponsiblePartySubsetType;
import net.opengis.ows.x11.SectionsType;
import net.opengis.ows.x11.ServiceIdentificationDocument.ServiceIdentification;
import net.opengis.ows.x11.ServiceProviderDocument.ServiceProvider;
import net.opengis.ows.x11.TelephoneType;
import net.opengis.ows.x11.ValuesReferenceDocument.ValuesReference;

import org.apache.xmlbeans.XmlObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3.x1999.xlink.ActuateType;
import org.w3.x1999.xlink.ShowType;

import org.n52.iceland.config.annotation.Configurable;
import org.n52.iceland.config.annotation.Setting;
import org.n52.iceland.i18n.LocaleHelper;
import org.n52.janmayen.http.HTTPMethods;
import org.n52.janmayen.http.MediaTypes;
import org.n52.shetland.i18n.LocalizedString;
import org.n52.shetland.i18n.MultilingualString;
import org.n52.shetland.ogc.ows.OWSConstants;
import org.n52.shetland.ogc.ows.OwsAcceptVersions;
import org.n52.shetland.ogc.ows.OwsAddress;
import org.n52.shetland.ogc.ows.OwsAllowedValues;
import org.n52.shetland.ogc.ows.OwsCode;
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
import org.n52.shetland.ogc.ows.OwsSections;
import org.n52.shetland.ogc.ows.OwsServiceIdentification;
import org.n52.shetland.ogc.ows.OwsServiceProvider;
import org.n52.shetland.ogc.ows.OwsValue;
import org.n52.shetland.ogc.ows.OwsValuesReference;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionCode;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.util.CollectionHelper;
import org.n52.shetland.w3c.SchemaLocation;
import org.n52.shetland.w3c.xlink.Actuate;
import org.n52.shetland.w3c.xlink.Show;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.N52XmlHelper;
import org.n52.svalbard.EncodingContext;
import org.n52.svalbard.SosHelperValues;
import org.n52.svalbard.encode.EncoderKey;
import org.n52.svalbard.encode.ExceptionEncoderKey;
import org.n52.svalbard.encode.exception.EncodingException;
import org.n52.svalbard.encode.exception.UnsupportedEncoderInputException;
import org.n52.svalbard.xml.AbstractXmlEncoder;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;



/**
 * @since 4.0.0
 *
 */
@Configurable
public class OwsEncoderv110 extends AbstractXmlEncoder<XmlObject, Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OwsEncoderv110.class);

    private static final Set<EncoderKey> ENCODER_KEYS = CollectionHelper.union(Sets.<EncoderKey>newHashSet(
            new ExceptionEncoderKey(MediaTypes.TEXT_XML), new ExceptionEncoderKey(MediaTypes.APPLICATION_XML)),
                                                                               CodingHelper
                                                                               .encoderKeysForElements(OWSConstants.NS_OWS,
                                                                                                       OwsServiceIdentification.class,
                                                                                                       OwsServiceProvider.class,
                                                                                                       OwsOperationsMetadata.class,
                                                                                                       OwsExceptionReport.class,
                                                                                                       OwsMetadata.class,
                                                                                                       OwsDomain.class));

    private boolean includeStackTraceInExceptionReport = false;

    public OwsEncoderv110() {
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!", Joiner.on(", ").join(ENCODER_KEYS));
    }

    @Setting(OwsEncoderSettings.INCLUDE_STACK_TRACE_IN_EXCEPTION_REPORT)
    public void setIncludeStackTrace(final boolean includeStackTraceInExceptionReport) {
        this.includeStackTraceInExceptionReport = includeStackTraceInExceptionReport;
    }

    @Override
    public Set<EncoderKey> getKeys() {
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
    public XmlObject encode(Object element, EncodingContext additionalValues)
            throws EncodingException {
        if (element instanceof OwsServiceIdentification) {
            return encodeServiceIdentification((OwsServiceIdentification) element);
        } else if (element instanceof OwsServiceProvider) {
            return encodeServiceProvider((OwsServiceProvider) element);
        } else if (element instanceof OwsOperationsMetadata) {
            return encodeOperationsMetadata((OwsOperationsMetadata) element);
        } else if (element instanceof OwsExceptionReport) {
            if (isEncodeExceptionsOnly(additionalValues) && !((OwsExceptionReport) element).getExceptions().isEmpty()) {
                return encodeOwsException(((OwsExceptionReport) element).getExceptions().get(0));
            }
            return encodeOwsExceptionReport((OwsExceptionReport) element);
        } else if (element instanceof OwsMetadata) {
            MetadataType metadataType = MetadataType.Factory.newInstance(getXmlOptions());
            encodeOwsMetadata((OwsMetadata) element, metadataType);
            return metadataType;
        } else if (element instanceof OwsDomain) {
            DomainType domainType = DomainType.Factory.newInstance(getXmlOptions());
            encodeOwsDomain((OwsDomain) element, domainType);
            return domainType;
        } else if (element instanceof OwsAcceptVersions) {
            return encodeAcceptVersions((OwsAcceptVersions) element);
        } else if (element instanceof OwsSections) {
            return encodeSections((OwsSections) element);
        }
        throw new UnsupportedEncoderInputException(this, element);
    }

    protected boolean isEncodeExceptionsOnly(EncodingContext additionalValues) {
        return additionalValues != null && additionalValues.has(SosHelperValues.ENCODE_OWS_EXCEPTION_ONLY);
    }

    private XmlObject encodeServiceIdentification(OwsServiceIdentification serviceIdentification)
            throws EncodingException {
        ServiceIdentification serviceIdent;
        /* TODO check for required fields and fail on missing ones */
        serviceIdent = ServiceIdentification.Factory.newInstance();
        serviceIdentification.getAccessConstraints().forEach(serviceIdent::addAccessConstraints);
        if (!serviceIdentification.getFees().isEmpty()) {
            serviceIdent.setFees(serviceIdentification.getFees().iterator().next());
        }
        CodeType xbServiceType = serviceIdent.addNewServiceType();
        xbServiceType.setStringValue(serviceIdentification.getServiceType().getValue());
        if (serviceIdentification.getServiceType().getCodeSpace().isPresent()) {
            xbServiceType.setCodeSpace(serviceIdentification.getServiceType().getCodeSpace().get().toString());
        }

        encodeMultilingualString(serviceIdentification.getTitle(), serviceIdent::addNewTitle);
        encodeMultilingualString(serviceIdentification.getAbstract(), serviceIdent::addNewAbstract);
        serviceIdentification.getServiceTypeVersion().stream().forEach(serviceIdent::addServiceTypeVersion);
        serviceIdentification.getProfiles().stream().map(URI::toString).forEach(serviceIdent::addProfile);

        serviceIdentification.getKeywords().stream()
                .collect(groupingBy(OwsKeyword::getType, mapping(OwsKeyword::getKeyword, toList())))
                .forEach((type, keywords) -> encodeOwsKeywords(type, keywords, serviceIdent.addNewKeywords()));

        return serviceIdent;
    }

    private void encodeMultilingualString(Optional<MultilingualString> string,
                                          Supplier<? extends LanguageStringType> supplier) {
        string.map(MultilingualString::stream)
                .orElseGet(Stream::empty)
                .map(this::encodeOwsLanguageString)
                .forEach(t ->  supplier.get().set(t));
    }

    private ServiceProvider encodeServiceProvider(OwsServiceProvider osp) {
        /* TODO check for required fields and fail on missing ones */
        ServiceProvider serviceProvider = ServiceProvider.Factory.newInstance();
        serviceProvider.setProviderName(osp.getProviderName());
        osp.getProviderSite().ifPresent(x -> encodeOnlineResource(x, serviceProvider.addNewProviderSite()));
        encodeOwsResponsibleParty(osp.getServiceContact(), serviceProvider.addNewServiceContact());
        return serviceProvider;
    }

    private OperationsMetadata encodeOperationsMetadata(OwsOperationsMetadata om)
            throws EncodingException {
        OperationsMetadata xom = OperationsMetadata.Factory.newInstance(getXmlOptions());

        om.getOperations().forEach(x -> encodeOwsOperation(x, xom.addNewOperation()));
        om.getConstraints().forEach(x -> encodeOwsDomain(x, xom.addNewConstraint()));
        om.getParameters().forEach(x -> encodeOwsDomain(x, xom.addNewParameter()));

        if (om.getExtension().isPresent()) {
            xom.setExtendedCapabilities(encodeOwsOperationsMetadataExtension(om.getExtension().get()));
        }
        return xom;
    }

    private ExceptionDocument encodeOwsException(CodedException owsException) {
        ExceptionDocument exceptionDoc =
                ExceptionDocument.Factory.newInstance(getXmlOptions());
        ExceptionType exceptionType = exceptionDoc.addNewException();
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
                    exceptionText.append(message).append('\n');
                }
                exceptionText.append(localizedMessage).append('\n');
            } else if (localizedMessage != null) {
                exceptionText.append(localizedMessage).append('\n');
            } else if (message != null) {
                exceptionText.append(message).append('\n');
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
                    exceptionText.append(message).append('\n');
                }
                exceptionText.append(localizedMessage).append('\n');
            } else if (localizedMessage != null) {
                exceptionText.append(localizedMessage).append('\n');
            } else if (message != null) {
                exceptionText.append(message).append('\n');
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
        ExceptionReportDocument erd = ExceptionReportDocument.Factory.newInstance(getXmlOptions());
        ExceptionReport er = erd.addNewExceptionReport();
        // er.setLanguage("en");
        er.setVersion(owsExceptionReport.getVersion());
        List<ExceptionType> exceptionTypes = new ArrayList<>(owsExceptionReport.getExceptions().size());
        owsExceptionReport.getExceptions().stream()
                .map(this::encodeOwsException)
                .map(ExceptionDocument::getException)
                .forEach(exceptionTypes::add);
        er.setExceptionArray(exceptionTypes.toArray(new ExceptionType[exceptionTypes.size()]));
        N52XmlHelper.setSchemaLocationsToDocument(erd,
                Collections.singletonList(N52XmlHelper.getSchemaLocationForOWS110()));
        return erd;
    }

    private LanguageStringType encodeOwsLanguageString(LocalizedString ls) {
        LanguageStringType lst = LanguageStringType.Factory.newInstance(getXmlOptions());
        lst.setStringValue(ls.getText());
        lst.setLang(LocaleHelper.toString(ls.getLang()));
        return lst;
    }

    private AcceptVersionsType encodeAcceptVersions(OwsAcceptVersions acceptVersions) {
        AcceptVersionsType avt = AcceptVersionsType.Factory.newInstance(getXmlOptions());
        acceptVersions.getAcceptVersions().forEach(avt::addVersion);
        return avt;
    }

    private SectionsType encodeSections(OwsSections sections) {
        SectionsType st = SectionsType.Factory.newInstance(getXmlOptions());
        sections.getSections().forEach(st::addSection);
        return st;
    }

    private void encodeOnlineResource(OwsOnlineResource site, OnlineResourceType xsite) {
        site.getHref().map(URI::toString).ifPresent(xsite::setHref);
        site.getTitle().ifPresent(xsite::setTitle);
        site.getActuate().map(Actuate::toString).map(ActuateType.Enum::forString).ifPresent(xsite::setActuate);
        site.getArcrole().map(URI::toString).ifPresent(xsite::setArcrole);
        site.getRole().map(URI::toString).ifPresent(xsite::setRole);
        site.getShow().map(Show::toString).map(ShowType.Enum::forString).ifPresent(xsite::setShow);
    }

    private void encodeOwsDomain(OwsDomain domain, DomainType xdomain) {
        xdomain.setName(domain.getName());
        domain.getDefaultValue().ifPresent(value -> xdomain.addNewDefaultValue().setStringValue(value.getValue()));
        domain.getDataType().ifPresent(domainMetadata -> encodeOwsDomainMetadata(domainMetadata, xdomain.addNewDataType()));
        domain.getMeaning().ifPresent(domainMetadata -> encodeOwsDomainMetadata(domainMetadata, xdomain.addNewMeaning()));
        domain.getMetadata().forEach(metadata -> encodeOwsMetadata(metadata, xdomain.addNewMetadata()));
        domain.getMetadata().forEach(metadata -> encodeOwsMetadata(metadata, xdomain.addNewMetadata()));
        domain.getValuesUnit().ifPresent(x -> {
            if (x.isReferenceSystem()) {
                encodeOwsDomainMetadata(x.asReferenceSystem(), xdomain.addNewReferenceSystem());
            } else if (x.isUOM()) {
                encodeOwsDomainMetadata(x.asUOM(), xdomain.addNewUOM());
            }
        });
        encodeOwsPossibleValues(domain.getPossibleValues(), xdomain);
     }

    private void encodeOwsMetadata(OwsMetadata metadata, MetadataType xmetadata) {
        metadata.getHref().map(URI::toString).ifPresent(xmetadata::setHref);
        metadata.getTitle().ifPresent(xmetadata::setTitle);
        metadata.getActuate().map(Actuate::toString).map(ActuateType.Enum::forString).ifPresent(xmetadata::setActuate);
        metadata.getArcrole().map(URI::toString).ifPresent(xmetadata::setArcrole);
        metadata.getRole().map(URI::toString).ifPresent(xmetadata::setRole);
        metadata.getShow().map(Show::toString).map(ShowType.Enum::forString).ifPresent(xmetadata::setShow);
        metadata.getAbout().map(URI::toString).ifPresent(xmetadata::setAbout);
    }

    private void encodeOwsDomainMetadata(OwsDomainMetadata domainMetadata, DomainMetadataType xdomainMetadata) {
        domainMetadata.getReference().map(URI::toString).ifPresent(xdomainMetadata::setReference);
        domainMetadata.getValue().ifPresent(xdomainMetadata::setStringValue);
    }

    private void encodeOwsAddress(OwsAddress address, AddressType xaddress) {
        address.getAdministrativeArea().ifPresent(xaddress::setAdministrativeArea);
        address.getCity().ifPresent(xaddress::setCity);
        address.getCountry().ifPresent(xaddress::setCountry);
        address.getPostalCode().ifPresent(xaddress::setPostalCode);
        address.getDeliveryPoint().forEach(xaddress::addDeliveryPoint);
        address.getElectronicMailAddress().forEach(xaddress::addElectronicMailAddress);
    }

    private void encodeOwsPhone(OwsPhone phone, TelephoneType xphone) {
        phone.getVoice().forEach(xphone::addVoice);
        phone.getFacsimile().forEach(xphone::addFacsimile);
    }

    private void encodeOwsCode(OwsCode role, CodeType xrole) {
        role.getCodeSpace().map(URI::toString).ifPresent(xrole::setCodeSpace);
        xrole.setStringValue(role.getValue());
    }

    private void encodeOwsOperation(OwsOperation operation, Operation xoperation) {
        xoperation.setName(operation.getName());
        operation.getConstraints().forEach(x -> encodeOwsDomain(x, xoperation.addNewConstraint()));
        operation.getMetadata().forEach(x -> encodeOwsMetadata(x, xoperation.addNewMetadata()));
        operation.getParameters().forEach(x -> encodeOwsDomain(x, xoperation.addNewParameter()));
        operation.getDCP().forEach(x -> encodeOwsDCP(x, xoperation.addNewDCP()));
    }

    private void encodeOwsContact(OwsContact contact, ContactType xcontact) {
        contact.getOnlineResource().ifPresent(site -> encodeOnlineResource(site, xcontact.addNewOnlineResource()));
        contact.getContactInstructions().ifPresent(xcontact::setContactInstructions);
        contact.getHoursOfService().ifPresent(xcontact::setHoursOfService);
        contact.getPhone().ifPresent(x -> encodeOwsPhone(x, xcontact.addNewPhone()));
        contact.getAddress().ifPresent(x -> encodeOwsAddress(x, xcontact.addNewAddress()));
    }

    private void encodeOwsResponsibleParty(OwsResponsibleParty responsibleParty, ResponsiblePartySubsetType xresponsibleParty) {
        responsibleParty.getIndividualName().ifPresent(xresponsibleParty::setIndividualName);
        responsibleParty.getPositionName().ifPresent(xresponsibleParty::setPositionName);
        responsibleParty.getContactInfo().ifPresent(x -> encodeOwsContact(x, xresponsibleParty.addNewContactInfo()));
        responsibleParty.getRole().ifPresent(x -> encodeOwsCode(x, xresponsibleParty.addNewRole()));
    }

    private void encodeOwsDCP(OwsDCP dcp, DCP xdcp) {
        if (dcp.isHTTP()) {
            HTTP xhttp = xdcp.addNewHTTP();
            OwsHttp http = dcp.asHTTP();

            SortedSet<OwsRequestMethod> requestMethods = http.getRequestMethods();
            requestMethods.forEach(method -> {
                RequestMethodType xmethod;
                switch (method.getHttpMethod()) {
                    case HTTPMethods.GET:
                        xmethod = xhttp.addNewGet();
                        break;
                    case HTTPMethods.POST:
                        xmethod = xhttp.addNewPost();
                        break;
                    default:
                        return;
                }
                encodeOnlineResource(method, xmethod);
                method.getConstraints().forEach(x -> encodeOwsDomain(x, xmethod.addNewConstraint()));
            });
        }
    }

    private void encodeOwsLanguageString(OwsLanguageString languageString, LanguageStringType xlanguageString) {
        xlanguageString.setStringValue(languageString.getValue());
        languageString.getLang().ifPresent(xlanguageString::setLang);
    }

    private void encodeOwsKeywords(Optional<OwsCode> type, List<OwsLanguageString> keywords, KeywordsType xkeywords) {
        type.ifPresent(x -> encodeOwsCode(x, xkeywords.addNewType()));
        keywords.forEach(x ->  encodeOwsLanguageString(x, xkeywords.addNewKeyword()));
    }

    private void encodeOwsPossibleValues(OwsPossibleValues possibleValues, DomainType xdomain) {
        if (possibleValues.isAnyValue()) {
            xdomain.addNewAnyValue();
        } else if (possibleValues.isNoValues()) {
            xdomain.addNewNoValues();
        } else if (possibleValues.isValuesReference()) {
            OwsValuesReference vr = possibleValues.asValuesReference();
            ValuesReference xvr = xdomain.addNewValuesReference();
            xvr.setReference(vr.getReference().toString());
            xvr.setStringValue(vr.getValue());
        } else if (possibleValues.isAllowedValues()) {
            OwsAllowedValues av = possibleValues.asAllowedValues();
            AllowedValues xav = xdomain.addNewAllowedValues();
            av.getRestrictions().forEach(restriction -> {
                if (restriction.isRange()) {
                    OwsRange range = restriction.asRange();
                    RangeType xrange = xav.addNewRange();
                    range.getLowerBound().map(OwsValue::getValue).ifPresent(v -> xrange.addNewMinimumValue().setStringValue(v));
                    range.getUpperBound().map(OwsValue::getValue).ifPresent(v -> xrange.addNewMaximumValue().setStringValue(v));
                    range.getSpacing().map(OwsValue::getValue).ifPresent(v -> xrange.addNewSpacing().setStringValue(v));
                    xrange.setRangeClosure(Collections.singletonList(range.getType()));
                } else if (restriction.isValue()) {
                    xav.addNewValue().setStringValue(restriction.asValue().getValue());
                }
            });
        }
    }

    private XmlObject encodeOwsOperationsMetadataExtension(OwsOperationMetadataExtension extension) throws EncodingException {
        return encodeObjectToXml(extension.getNamespace(), extension);
    }
}
