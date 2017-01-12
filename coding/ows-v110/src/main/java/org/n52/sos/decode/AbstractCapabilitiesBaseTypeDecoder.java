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
package org.n52.sos.decode;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import net.opengis.ows.x11.AddressType;
import net.opengis.ows.x11.AllowedValuesDocument.AllowedValues;
import net.opengis.ows.x11.AnyValueDocument.AnyValue;
import net.opengis.ows.x11.CapabilitiesBaseType;
import net.opengis.ows.x11.CodeType;
import net.opengis.ows.x11.ContactType;
import net.opengis.ows.x11.DCPDocument.DCP;
import net.opengis.ows.x11.DomainMetadataType;
import net.opengis.ows.x11.DomainType;
import net.opengis.ows.x11.HTTPDocument.HTTP;
import net.opengis.ows.x11.KeywordsType;
import net.opengis.ows.x11.LanguageStringType;
import net.opengis.ows.x11.MetadataType;
import net.opengis.ows.x11.NoValuesDocument.NoValues;
import net.opengis.ows.x11.OnlineResourceType;
import net.opengis.ows.x11.OperationDocument.Operation;
import net.opengis.ows.x11.OperationsMetadataDocument.OperationsMetadata;
import net.opengis.ows.x11.RangeType;
import net.opengis.ows.x11.RequestMethodType;
import net.opengis.ows.x11.ResponsiblePartySubsetType;
import net.opengis.ows.x11.ServiceIdentificationDocument.ServiceIdentification;
import net.opengis.ows.x11.ServiceProviderDocument.ServiceProvider;
import net.opengis.ows.x11.TelephoneType;
import net.opengis.ows.x11.ValueType;
import net.opengis.ows.x11.ValuesReferenceDocument.ValuesReference;

import org.apache.xmlbeans.XmlObject;

import org.n52.janmayen.i18n.LocaleHelper;
import org.n52.janmayen.http.HTTPMethods;
import org.n52.janmayen.i18n.LocalizedString;
import org.n52.janmayen.i18n.MultilingualString;
import org.n52.shetland.ogc.ows.OwsAddress;
import org.n52.shetland.ogc.ows.OwsAllowedValues;
import org.n52.shetland.ogc.ows.OwsAnyValue;
import org.n52.shetland.ogc.ows.OwsCapabilities;
import org.n52.shetland.ogc.ows.OwsCapabilitiesExtension;
import org.n52.shetland.ogc.ows.OwsCode;
import org.n52.shetland.ogc.ows.OwsContact;
import org.n52.shetland.ogc.ows.OwsDCP;
import org.n52.shetland.ogc.ows.OwsDomain;
import org.n52.shetland.ogc.ows.OwsDomainMetadata;
import org.n52.shetland.ogc.ows.OwsHttp;
import org.n52.shetland.ogc.ows.OwsKeyword;
import org.n52.shetland.ogc.ows.OwsLanguageString;
import org.n52.shetland.ogc.ows.OwsMetadata;
import org.n52.shetland.ogc.ows.OwsNoValues;
import org.n52.shetland.ogc.ows.OwsOnlineResource;
import org.n52.shetland.ogc.ows.OwsOperation;
import org.n52.shetland.ogc.ows.OwsOperationMetadataExtension;
import org.n52.shetland.ogc.ows.OwsOperationsMetadata;
import org.n52.shetland.ogc.ows.OwsPhone;
import org.n52.shetland.ogc.ows.OwsPossibleValues;
import org.n52.shetland.ogc.ows.OwsRange;
import org.n52.shetland.ogc.ows.OwsReferenceSystem;
import org.n52.shetland.ogc.ows.OwsRequestMethod;
import org.n52.shetland.ogc.ows.OwsResponsibleParty;
import org.n52.shetland.ogc.ows.OwsServiceIdentification;
import org.n52.shetland.ogc.ows.OwsServiceProvider;
import org.n52.shetland.ogc.ows.OwsUOM;
import org.n52.shetland.ogc.ows.OwsValue;
import org.n52.shetland.ogc.ows.OwsValuesReference;
import org.n52.shetland.ogc.ows.OwsValuesUnit;
import org.n52.shetland.w3c.xlink.Actuate;
import org.n52.shetland.w3c.xlink.Show;
import org.n52.svalbard.decode.exception.DecodingException;

import com.google.common.base.Strings;

public abstract class AbstractCapabilitiesBaseTypeDecoder {

    protected OwsCapabilities parseCapabilitiesBaseType(String service, CapabilitiesBaseType cbt)
            throws DecodingException {
        if (cbt == null) {
            return null;
        }
        OwsServiceIdentification serviceIdentification = parseServiceIdentification(cbt.getServiceIdentification());
        OwsServiceProvider serviceProvider = parseServiceProvider(cbt.getServiceProvider());
        OwsOperationsMetadata operationsMetadata = parseOperationMetadata(cbt.getOperationsMetadata());
        Collection<String> languages = null;
        Collection<OwsCapabilitiesExtension> extensions = null;
        return new OwsCapabilities(service, cbt.getVersion(), cbt.getUpdateSequence(), serviceIdentification,
                                   serviceProvider, operationsMetadata, languages, extensions);
    }

    private List<OwsOperation> parseOperations(Operation[] operations) {
        return Optional.ofNullable(operations).map(Arrays::stream)
                .orElseGet(Stream::empty).map(this::parseOperation).collect(toList());
    }

    private OwsOperation parseOperation(Operation operation) {
        if (operation == null) {
            return null;
        }
        Collection<OwsDomain> parameters = parseDomains(operation.getParameterArray());
        Collection<OwsDomain> constraints = parseDomains(operation.getConstraintArray());
        Collection<OwsMetadata> metadata = parseMetadata(operation.getMetadataArray());
        Collection<OwsDCP> dcps = parseDCPs(operation.getDCPArray());
        return new OwsOperation(operation.getName(), parameters, constraints, metadata, dcps);

    }

    private OwsValue parseValue(ValueType value) {
        if (value == null) {
            return null;
        }
        return new OwsValue(value.getStringValue());
    }

    private OwsRange parseRange(RangeType range) {
        if (range == null) {
            return null;
        }
        OwsValue upperBound = parseValue(range.getMaximumValue());
        OwsValue lowerBound = parseValue(range.getMinimumValue());
        OwsValue spacing = parseValue(range.getSpacing());
        String type = null;
        if (range.isSetRangeClosure() && !range.getRangeClosure().isEmpty()) {
            type = (String) range.getRangeClosure().get(0);
        }
        return new OwsRange(lowerBound, upperBound, type, spacing);
    }

    private <T extends OwsDomainMetadata> T parse(BiFunction<URI, String, T> fun, DomainMetadataType metadata) {
        if (metadata == null) {
            return null;
        }
        URI reference = Optional.ofNullable(metadata.getReference()).map(Strings::emptyToNull).map(URI::create)
                .orElse(null);
        String value = metadata.getStringValue();
        return fun.apply(reference, value);
    }

    private OwsDomainMetadata parseDomainMetadata(DomainMetadataType metadata) {
        return parse(OwsDomainMetadata::new, metadata);
    }

    private Set<OwsDomainMetadata> parseDomainMetadata(DomainMetadataType[] metadata) {
        return Optional.ofNullable(metadata).map(Arrays::stream).orElseGet(Stream::empty).map(this::parseDomainMetadata)
                .filter(Objects::nonNull).collect(toSet());
    }

    private OwsMetadata parseMetadata(MetadataType metadata) {
        if (metadata == null) {
            return null;
        }
        URI href = Optional.ofNullable(metadata.getHref()).map(Strings::emptyToNull).map(URI::create).orElseGet(null);
        URI role = Optional.ofNullable(metadata.getRole()).map(Strings::emptyToNull).map(URI::create).orElseGet(null);
        URI arcrole = Optional.ofNullable(metadata.getArcrole()).map(Strings::emptyToNull).map(URI::create)
                .orElseGet(null);
        Show show = Optional.ofNullable(metadata.getShow()).map(Object::toString).map(Show::valueOf).orElse(null);
        Actuate actuate = Optional.ofNullable(metadata.getActuate()).map(Object::toString).map(Actuate::valueOf)
                .orElse(null);
        URI about = Optional.ofNullable(metadata.getAbout()).map(Strings::emptyToNull).map(URI::create).orElseGet(null);
        String title = metadata.getTitle();
        return new OwsMetadata(href, role, arcrole, title, show, actuate, about);
    }

    private Set<OwsMetadata> parseMetadata(MetadataType[] metadata) {
        return Optional.ofNullable(metadata).map(Arrays::stream).orElseGet(Stream::empty).map(this::parseMetadata)
                .filter(Objects::nonNull).collect(toSet());
    }

    private OwsUOM parseUom(DomainMetadataType uom) {
        return parse(OwsUOM::new, uom);
    }

    private OwsReferenceSystem parseReferenceSystem(DomainMetadataType uom) {
        return parse(OwsReferenceSystem::new, uom);
    }

    private OwsNoValues parseNoValues(NoValues noValues) {
        if (noValues == null) {
            return null;
        }
        return OwsNoValues.instance();
    }

    private OwsAnyValue parseAnyValue(AnyValue anyValue) {
        if (anyValue == null) {
            return null;
        }
        return OwsAnyValue.instance();
    }

    private OwsValuesReference parseValuesReference(ValuesReference valuesReference) {
        if (valuesReference == null) {
            return null;
        }
        URI reference = Optional.ofNullable(valuesReference.getReference()).map(Strings::emptyToNull).map(URI::create)
                .orElse(null);
        String value = valuesReference.getStringValue();
        return new OwsValuesReference(reference, value);
    }

    private OwsAllowedValues parseAllowedValues(AllowedValues allowedValues) {
        if (allowedValues == null) {
            return null;
        }
        return new OwsAllowedValues(Stream.concat(
                Optional.ofNullable(allowedValues.getValueArray()).map(Arrays::stream).orElseGet(Stream::empty)
                        .map(this::parseValue),
                Optional.ofNullable(allowedValues.getRangeArray()).map(Arrays::stream).orElseGet(Stream::empty)
                        .map(this::parseRange))
                .filter(Objects::nonNull));
    }

    private OwsDomain parseDomain(DomainType domain) {
        if (domain == null) {
            return null;
        }
        OwsValue defaultValue = parseValue(domain.getDefaultValue());
        Collection<OwsMetadata> metadata = parseMetadata(domain.getMetadataArray());
        OwsDomainMetadata meaning = parseDomainMetadata(domain.getMeaning());
        OwsDomainMetadata dataType = parseDomainMetadata(domain.getDataType());
        OwsValuesUnit valuesUnit = Stream.of(parseUom(domain.getUOM()),
                                             parseReferenceSystem(domain.getReferenceSystem()))
                .filter(Objects::nonNull).findFirst().orElse(null);
        OwsPossibleValues possibleValues = Stream.of(parseAnyValue(domain.getAnyValue()),
                                                     parseAllowedValues(domain.getAllowedValues()),
                                                     parseValuesReference(domain.getValuesReference()),
                                                     parseNoValues(domain.getNoValues()))
                .filter(Objects::nonNull).findFirst().orElse(null);

        return new OwsDomain(domain.getName(), possibleValues, defaultValue, meaning, dataType, valuesUnit, metadata);
    }

    private List<OwsDomain> parseDomains(DomainType[] domains) {
        return Optional.ofNullable(domains).map(Arrays::stream).orElseGet(Stream::empty).map(this::parseDomain)
                .filter(Objects::nonNull).collect(toList());
    }

    private List<OwsDCP> parseDCPs(DCP[] dcps) {
        return Optional.ofNullable(dcps).map(Arrays::stream).orElseGet(Stream::empty).map(this::parseDCP)
                .filter(Objects::nonNull).collect(toList());
    }

    private OwsDCP parseDCP(DCP dcp) {
        if (dcp == null) {
            return null;
        }
        return parseHTTP(dcp.getHTTP());
    }

    private OwsDCP parseHTTP(HTTP http) {
        if (http == null) {
            return null;
        }
        return new OwsHttp(Stream.concat(
                Optional.ofNullable(http.getGetArray()).map(Arrays::stream).orElseGet(Stream::empty)
                        .map(this::parseGetRequestMethod),
                Optional.ofNullable(http.getPostArray()).map(Arrays::stream).orElseGet(Stream::empty)
                        .map(this::parsePostRequestMethod))
                .filter(Objects::nonNull)
                .collect(toList()));
    }

    private OwsRequestMethod parseGetRequestMethod(RequestMethodType method) {
        return parseRequestMethod(HTTPMethods.GET, method);
    }

    private OwsRequestMethod parsePostRequestMethod(RequestMethodType method) {
        return parseRequestMethod(HTTPMethods.POST, method);
    }

    private OwsRequestMethod parseRequestMethod(String httpMethod, RequestMethodType method) {
        if (method == null) {
            return null;
        }
        URI href = Optional.ofNullable(method.getHref()).map(Strings::emptyToNull).map(URI::create).orElseGet(null);
        URI role = Optional.ofNullable(method.getRole()).map(Strings::emptyToNull).map(URI::create).orElseGet(null);
        URI arcrole = Optional.ofNullable(method.getArcrole()).map(Strings::emptyToNull).map(URI::create)
                .orElseGet(null);
        Show show = Optional.ofNullable(method.getShow()).map(Object::toString).map(Show::valueOf).orElse(null);
        Actuate actuate = Optional.ofNullable(method.getActuate()).map(Object::toString).map(Actuate::valueOf)
                .orElse(null);
        String title = method.getTitle();
        List<OwsDomain> constraints = parseDomains(method.getConstraintArray());
        return new OwsRequestMethod(href, constraints, httpMethod, role, arcrole, title, show, actuate);
    }

    private OwsOperationsMetadata parseOperationMetadata(OperationsMetadata operationsMetadata) {
        if (operationsMetadata == null) {
            return null;
        }
        List<OwsOperation> operations = parseOperations(operationsMetadata.getOperationArray());
        List<OwsDomain> parameters = parseDomains(operationsMetadata.getParameterArray());
        List<OwsDomain> constraints = parseDomains(operationsMetadata.getConstraintArray());
        OwsOperationMetadataExtension extension = parseOperationsMetadataExtension(operationsMetadata
                .getExtendedCapabilities());
        return new OwsOperationsMetadata(operations, parameters, constraints, extension);
    }

    private OwsServiceIdentification parseServiceIdentification(ServiceIdentification serviceIdentification) {
        OwsCode serviceType = parseCode(serviceIdentification.getServiceType());
        Set<String> serviceTypeVersion = Optional.ofNullable(serviceIdentification.getServiceTypeVersionArray())
                .map(Arrays::stream).orElseGet(Stream::empty).collect(toSet());
        Set<String> fees = Optional.ofNullable(serviceIdentification.getFees()).map(Collections::singleton)
                .orElseGet(Collections::emptySet);
        Set<URI> profiles = Optional.ofNullable(serviceIdentification.getProfileArray()).map(Arrays::stream)
                .orElseGet(Stream::empty).map(URI::create).collect(toSet());
        Set<String> accessConstraints = Optional.ofNullable(serviceIdentification.getAccessConstraintsArray())
                .map(Arrays::stream).orElseGet(Stream::empty).collect(toSet());
        MultilingualString title = new MultilingualString();
        MultilingualString abstrakt = new MultilingualString();
        Optional.ofNullable(serviceIdentification.getTitleArray()).map(Arrays::stream).orElseGet(Stream::empty)
                .map(this::parseLanguageString).forEach(title::addLocalization);
        Optional.ofNullable(serviceIdentification.getAbstractArray()).map(Arrays::stream).orElseGet(Stream::empty)
                .map(this::parseLanguageString).forEach(abstrakt::addLocalization);
        Set<OwsKeyword> keywords = Optional.ofNullable(serviceIdentification.getKeywordsArray()).map(Arrays::stream)
                .orElseGet(Stream::empty).flatMap(this::parseKeyword).filter(Objects::nonNull).collect(toSet());
        return new OwsServiceIdentification(serviceType, serviceTypeVersion, profiles, fees, accessConstraints, title, abstrakt, keywords);
    }

    private Stream<OwsKeyword> parseKeyword(KeywordsType keyword) {
        if (keyword == null) {
            return Stream.empty();
        }
        OwsCode type = parseCode(keyword.getType());
        return Arrays.stream(keyword.getKeywordArray())
                .map(ls -> new OwsLanguageString(ls.getLang(), ls.getStringValue()))
                .map(ls -> new OwsKeyword(ls, type));

    }

    private OwsServiceProvider parseServiceProvider(ServiceProvider serviceProvider) {
        if (serviceProvider == null) {
            return null;
        }
        OwsOnlineResource providerSite = parseOnlineResource(serviceProvider.getProviderSite());
        OwsResponsibleParty serviceContact = parseResponsibleParty(serviceProvider.getServiceContact());
        String providerName = serviceProvider.getProviderName();
        return new OwsServiceProvider(providerName, providerSite, serviceContact);
    }

    private OwsOnlineResource parseOnlineResource(OnlineResourceType onlineResource) {
        if (onlineResource == null) {
            return null;
        }
        URI href = Optional.ofNullable(onlineResource.getHref()).map(Strings::emptyToNull).map(URI::create)
                .orElseGet(null);
        URI role = Optional.ofNullable(onlineResource.getRole()).map(Strings::emptyToNull).map(URI::create)
                .orElseGet(null);
        URI arcrole = Optional.ofNullable(onlineResource.getArcrole()).map(Strings::emptyToNull).map(URI::create)
                .orElseGet(null);
        Show show = Optional.ofNullable(onlineResource.getShow()).map(Object::toString).map(Show::valueOf).orElse(null);
        Actuate actuate = Optional.ofNullable(onlineResource.getActuate()).map(Object::toString).map(Actuate::valueOf)
                .orElse(null);
        String title = onlineResource.getTitle();
        return new OwsOnlineResource(href, role, arcrole, title, show, actuate);
    }

    private OwsResponsibleParty parseResponsibleParty(ResponsiblePartySubsetType responsibleParty) {
        if (responsibleParty == null) {
            return null;
        }
        String positionName = responsibleParty.getPositionName();
        String individualName = responsibleParty.getIndividualName();
        String organisationName = null;
        OwsContact contactInfo = parseContact(responsibleParty.getContactInfo());
        OwsCode role = parseCode(responsibleParty.getRole());
        return new OwsResponsibleParty(individualName, organisationName, positionName, contactInfo, role);
    }

    private OwsContact parseContact(ContactType contactInfo) {
        if (contactInfo == null) {
            return null;
        }
        OwsOnlineResource onlineResource = parseOnlineResource(contactInfo.getOnlineResource());
        String hoursOfService = contactInfo.getHoursOfService();
        String contactInstructions = contactInfo.getContactInstructions();
        OwsAddress address = parseAddress(contactInfo.getAddress());
        OwsPhone phone = parsePhone(contactInfo.getPhone());
        return new OwsContact(phone, address, onlineResource, hoursOfService, contactInstructions);

    }

    private OwsCode parseCode(CodeType code) {
        if (code == null) {
            return null;
        }
        return new OwsCode(code.getStringValue(), Optional.ofNullable(code.getCodeSpace()).map(Strings::emptyToNull)
                           .map(URI::create).orElse(null));
    }

    private OwsPhone parsePhone(TelephoneType phone) {
        if (phone == null) {
            return null;
        }
        Set<String> voice = Optional.ofNullable(phone.getVoiceArray()).map(Arrays::stream).orElseGet(Stream::empty)
                .map(Strings::emptyToNull).filter(Objects::nonNull).collect(toSet());
        Set<String> facsimile = Optional.ofNullable(phone.getFacsimileArray()).map(Arrays::stream)
                .orElseGet(Stream::empty).map(Strings::emptyToNull).filter(Objects::nonNull).collect(toSet());
        return new OwsPhone(voice, facsimile);
    }

    private OwsAddress parseAddress(AddressType address) {
        if (address == null) {
            return null;
        }
        List<String> deliveryPoint = Optional.ofNullable(address.getDeliveryPointArray()).map(Arrays::stream)
                .orElseGet(Stream::empty).map(Strings::emptyToNull).filter(Objects::nonNull).collect(toList());
        List<String> electronicMailAddress = Optional.ofNullable(address.getElectronicMailAddressArray())
                .map(Arrays::stream).orElseGet(Stream::empty).map(Strings::emptyToNull).filter(Objects::nonNull)
                .collect(toList());
        String city = address.getCity();
        String administrativeArea = address.getAdministrativeArea();
        String postalCode = address.getPostalCode();
        String country = address.getCountry();
        return new OwsAddress(deliveryPoint, city, administrativeArea, postalCode, country, electronicMailAddress);
    }

    private OwsOperationMetadataExtension parseOperationsMetadataExtension(XmlObject extendedCapabilities) {
        /* TODO implement org.n52.sos.decode.AbstractCapabilitiesBaseTypeDecoder.parseOperationsMetadataExtension() */
        return null;
    }

    private LocalizedString parseLanguageString(LanguageStringType lst) {
        return new LocalizedString(LocaleHelper.decode(lst.getLang()), lst.getStringValue());
    }

}
