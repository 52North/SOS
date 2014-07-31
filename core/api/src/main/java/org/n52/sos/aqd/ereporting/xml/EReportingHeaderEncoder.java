/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.aqd.ereporting.xml;

import java.io.OutputStream;
import java.util.Collections;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.joda.time.DateTime;

import org.n52.oxf.xml.NcNameResolver;
import org.n52.sos.aqd.ereporting.Address;
import org.n52.sos.aqd.ereporting.Contact;
import org.n52.sos.aqd.ereporting.EReportingChange;
import org.n52.sos.aqd.ereporting.EReportingHeader;
import org.n52.sos.aqd.ereporting.GeographicalName;
import org.n52.sos.aqd.ereporting.Pronunciation;
import org.n52.sos.aqd.ereporting.RelatedParty;
import org.n52.sos.aqd.ereporting.Spelling;
import org.n52.sos.encode.EncodingValues;
import org.n52.sos.encode.XmlStreamWriter;
import org.n52.sos.exception.ows.concrete.DateTimeFormatException;
import org.n52.sos.aqd.ereporting.InspireID;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.Time.TimeFormat;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.util.JavaHelper;
import org.n52.sos.util.Nillable;
import org.n52.sos.util.Reference;
import org.n52.sos.util.Referenceable;
import org.n52.sos.w3c.W3CConstants;

import com.google.common.base.Optional;

public class EReportingHeaderEncoder extends XmlStreamWriter<EReportingHeader> {
    private final EReportingHeader header;

    public EReportingHeaderEncoder(EReportingHeader header) {
        this.header = header;
    }

    @Override
    public void write(OutputStream out, EncodingValues encodingValues)
            throws XMLStreamException, OwsExceptionReport {
        write(this.header, out, encodingValues);
    }

    @Override
    public void write(EReportingHeader elementToStream, OutputStream out)
            throws XMLStreamException, OwsExceptionReport {
        write(elementToStream, out, new EncodingValues());
    }

    @Override
    public void write(OutputStream out)
            throws XMLStreamException, OwsExceptionReport {
        write(this.header, out, new EncodingValues());
    }

    @Override
    public void write(EReportingHeader elementToStream, OutputStream out,
                      EncodingValues encodingValues)
            throws XMLStreamException, OwsExceptionReport {
        this.init(out, encodingValues);
        this.encodeReportingHeader(elementToStream);
    }

    private void encodeReportingHeader(EReportingHeader h)
            throws XMLStreamException, DateTimeFormatException {

        start(AQDConstants.QN_AQD_REPORTING_HEADER);
        namespace(AQDConstants.NS_AD_PREFIX, AQDConstants.NS_AD);
        namespace(AQDConstants.NS_AQD_PREFIX, AQDConstants.NS_AQD);
        namespace(AQDConstants.NS_BASE_PREFIX, AQDConstants.NS_BASE);
        namespace(AQDConstants.NS_BASE2_PREFIX, AQDConstants.NS_BASE2);
        namespace(AQDConstants.NS_GCO_PREFIX, AQDConstants.NS_GCO);
        namespace(AQDConstants.NS_GN_PREFIX, AQDConstants.NS_GN);
        namespace(GmlConstants.NS_GML_PREFIX, GmlConstants.NS_GML_32);
        namespace(W3CConstants.NS_XLINK_PREFIX, W3CConstants.NS_XLINK);
        namespace(W3CConstants.NS_XSI_PREFIX, W3CConstants.NS_XSI);
        schemaLocation(Collections.singleton(AQDConstants.NS_AQD_SCHEMA_LOCATION));

        attr(GmlConstants.QN_ID_32, getGMLId(h));

        encodeChange(h.getChange());
        encodeInpireID(h.getInspireID());
        encodeReportingAuthority(h.getReportingAuthority());
        encodeReportingPeriod(h.getReportingPeriod());

        end(AQDConstants.QN_AQD_REPORTING_HEADER);
    }

    protected String getGMLId(Object h) {
        String gmlId = JavaHelper.generateID(h.toString() + System.currentTimeMillis());
        return NcNameResolver.fixNcName(gmlId);
    }

    private void encodeReportingPeriod(Referenceable<? extends Time> v)
            throws XMLStreamException, DateTimeFormatException {
        if (!v.isAbsent()) {
            if (v.isReference()) {
                empty(AQDConstants.QN_AQD_REPORTING_PERIOD);
                encodeReferenceAttr(v.getReference());
            } else {
                if (v.getInstance().isNil()) {
                    empty(AQDConstants.QN_AQD_REPORTING_PERIOD);
                    encodeNilAttr(v.getInstance().getNilReason());
                } else {
                    start(AQDConstants.QN_AQD_REPORTING_PERIOD);
                    encodeTime(v.getInstance().get());
                    end(AQDConstants.QN_AQD_REPORTING_PERIOD);
                }
            }
        }
    }

    private void encodeReportingAuthority(RelatedParty v)
            throws XMLStreamException {
        start(AQDConstants.QN_AQD_REPORTING_AUTHORITY);
        encodeRelatedParty(v);
        end(AQDConstants.QN_AQD_REPORTING_AUTHORITY);
    }

    private void encodeInpireID(InspireID v)
            throws XMLStreamException {
        start(AQDConstants.QN_AQD_INSPIRE_ID);
        start(AQDConstants.QN_BASE_IDENTIFIER);
        encodeString(AQDConstants.QN_BASE_LOCAL_ID, v.getLocalId());
        encodeString(AQDConstants.QN_BASE_NAMESPACE, v.getNamespace());
        encodeNillableString(AQDConstants.QN_BASE_VERSION_ID, v.getVersionId());
        end(AQDConstants.QN_BASE_IDENTIFIER);
        end(AQDConstants.QN_AQD_INSPIRE_ID);
    }

    private void encodeChange(EReportingChange v)
            throws XMLStreamException {
        start(AQDConstants.QN_AQD_CHANGE);
        chars(Boolean.toString(v.isChange()));
        endInline(AQDConstants.QN_AQD_CHANGE);
        if (v.getDescription().isPresent()) {
            start(AQDConstants.QN_AQD_CHANGE_DESCRIPTION);
            chars(v.getDescription().get());
            endInline(AQDConstants.QN_AQD_CHANGE_DESCRIPTION);
        }
    }

    private void encodeRelatedParty(RelatedParty v)
            throws XMLStreamException {
        start(AQDConstants.QN_BASE2_RELATED_PARTY);

        encodeNillableFreeText(AQDConstants.QN_BASE2_INDIVIDUAL_NAME, v.getIndividualName());
        encodeNillableFreeText(AQDConstants.QN_BASE2_ORGANISATION_NAME, v.getOrganisationName());
        encodeNillableFreeText(AQDConstants.QN_BASE2_POSITION_NAME, v.getPositionName());
        encodeContact(v.getContact());
        for (Nillable<Reference> role : v.getRoles()) {
            encodeNillableReference(AQDConstants.QN_BASE2_ROLE, role);
        }
        end(AQDConstants.QN_BASE2_RELATED_PARTY);
    }

    private void encodeReferenceAttr(Reference v)
            throws XMLStreamException {
        attr(W3CConstants.QN_XLINK_HREF, v.getHref().toString());
        attr(W3CConstants.QN_XLINK_ACTUATE, v.getActuate());
        attr(W3CConstants.QN_XLINK_ARCROLE, v.getArcrole());
        attr(W3CConstants.QN_XLINK_ROLE, v.getRole());
        attr(W3CConstants.QN_XLINK_SHOW, v.getShow());
        attr(W3CConstants.QN_XLINK_TITLE, v.getTitle());
        attr(W3CConstants.QN_XLINK_TYPE, v.getType());
        attr(GmlConstants.QN_REMOTE_SCHEMA, v.getRemoteSchema());
    }

    protected void attr(QName qn, Optional<?> v)
            throws XMLStreamException {
        if (v.isPresent()) {
            attr(qn, v.get().toString());
        }
    }

    protected void encodeNillableString(QName qn, Nillable<?> v)
            throws XMLStreamException {
        if (!v.isAbsent()) {
            if (v.isNil()) {
                empty(qn);
                encodeNilAttr(v.getNilReason());
            } else {
                start(qn);
                chars(v.get().toString());
                endInline(qn);
            }
        }
    }

    protected void encodeString(QName qn, String v)
            throws XMLStreamException {
        start(qn);
        chars(v);
        endInline(qn);
    }

    private void encodeNilAttr(Optional<String> reason)
            throws XMLStreamException {
        attr(W3CConstants.QN_XSI_NIL, Boolean.toString(true));
        if (reason.isPresent()) {
            attr(AQDConstants.AN_NIL_REASON, reason.get());
        }
    }

    private void encodeGCONilAttr(Nillable<?> v)
            throws XMLStreamException {
        if (v.isNil()) {
            attr(W3CConstants.QN_XSI_NIL, Boolean.toString(true));
            if (v.getNilReason().isPresent()) {
                attr(AQDConstants.QN_GCO_NIL_REASON, v.getNilReason().get());
            }
        }
    }

    protected void encodeNillableFreeText(QName qn, Nillable<String> v)
            throws XMLStreamException {
        if (!v.isAbsent()) {
            if (v.isNil()) {
                empty(qn);
                encodeGCONilAttr(v);
            } else {
                start(qn);
                start(AQDConstants.QN_GCO_CHARACTER_STRING);
                chars(v.get());
                endInline(AQDConstants.QN_GCO_CHARACTER_STRING);
                end(qn);
            }
        }
    }

    private void encodeContact(Nillable<Contact> v)
            throws XMLStreamException {
        if (!v.isAbsent()) {
            if (v.isNil()) {
                empty(AQDConstants.QN_BASE2_CONTACT);
                encodeNilAttr(v.getNilReason());
            } else {
                start(AQDConstants.QN_BASE2_CONTACT);
                encodeContact(v.get());
                end(AQDConstants.QN_BASE2_CONTACT);
            }
        }
    }

    protected void encodeContact(Contact c)
            throws XMLStreamException {
        start(AQDConstants.QN_BASE2_C_ONTACT);
        encodeAddress(c.getAddress());
        encodeNillableFreeText(AQDConstants.QN_BASE2_CONTACT_INSTRUCTIONS,
                               c.getContactInstructions());
        encodeNillableString(AQDConstants.QN_BASE2_ELECTRONIC_MAIL_ADDRESS,
                             c.getElectronicMailAddress());
        encodeNillableFreeText(AQDConstants.QN_BASE2_HOURS_OF_SERVICE,
                               c.getHoursOfService());
        for (Nillable<String> value : c.getTelephoneFacsimile()) {
            encodeNillableString(AQDConstants.QN_BASE2_TELEPHONE_FACSIMILE, value);
        }
        for (Nillable<String> value : c.getTelephoneVoice()) {
            encodeNillableString(AQDConstants.QN_BASE2_TELEPHONE_VOICE, value);
        }
        encodeNillableString(AQDConstants.QN_BASE2_WEBSITE, c.getWebsite());
        end(AQDConstants.QN_BASE2_C_ONTACT);
    }

    private void encodeAddress(Nillable<Address> v)
            throws XMLStreamException {
        if (!v.isAbsent()) {
            if (v.isNil()) {
                empty(AQDConstants.QN_BASE2_ADDRESS);
                encodeNilAttr(v.getNilReason());
            } else {
                start(AQDConstants.QN_BASE2_ADDRESS);
                encodeAddress(v.get());
                end(AQDConstants.QN_BASE2_ADDRESS);
            }
        }
    }

    private void encodeAddress(Address v)
            throws XMLStreamException {
        start(AQDConstants.QN_AD_ADDRESS_REPRESENTATION);

        for (GeographicalName value : v.getAdminUnits()) {
            start(AQDConstants.QN_AD_ADMIN_UNIT);
            encodeGeographicalName(value);
            end(AQDConstants.QN_AD_ADMIN_UNIT);
        }
        for (String value : v.getLocatorDesignators()) {
            start(AQDConstants.QN_AD_LOCATOR_DESIGNATOR);
            chars(value);
            endInline(AQDConstants.QN_AD_LOCATOR_DESIGNATOR);
        }
        for (GeographicalName value : v.getLocatorNames()) {
            start(AQDConstants.QN_AD_LOCATOR_NAME);
            encodeGeographicalName(value);
            end(AQDConstants.QN_AD_LOCATOR_NAME);
        }
        for (Nillable<GeographicalName> value : v.getAddressAreas()) {
            encodeNillableGeographicalName(AQDConstants.QN_AD_ADDRESS_AREA, value);
        }
        for (Nillable<GeographicalName> value : v.getPostNames()) {
            encodeNillableGeographicalName(AQDConstants.QN_AD_POST_NAME, value);
        }
        encodeNillableString(AQDConstants.QN_AD_POST_CODE, v.getPostCode());
        for (Nillable<GeographicalName> value : v.getThoroughfares()) {
            encodeNillableGeographicalName(AQDConstants.QN_AD_THOROUGHFARE, value);
        }
        encodeNillableReference(AQDConstants.QN_AD_ADDRESS_FEATURE, v.getAddressFeature());

        end(AQDConstants.QN_AD_ADDRESS_REPRESENTATION);
    }

    protected void encodeNillableGeographicalName(QName qn,
                                                  Nillable<GeographicalName> v)
            throws XMLStreamException {
        if (!v.isAbsent()) {
            if (v.isNil()){
                empty(qn);
                encodeNilAttr(v.getNilReason());
            } else {
                start(qn);
                encodeGeographicalName(v.get());
                end(qn);
            }
        }
    }

    protected void encodeNillableReference(QName qn, Nillable<Reference> v)
            throws XMLStreamException {
        if (!v.isAbsent()) {
            empty(qn);
            if (v.isNil()) {
                encodeNilAttr(v.getNilReason());
            } else {
                encodeReferenceAttr(v.get());
            }
        }
    }

    private void encodeGeographicalName(GeographicalName v)
            throws XMLStreamException {
        start(AQDConstants.QN_GN_GEOGRAPHICAL_NAME);
        encodeNillableString(AQDConstants.QN_GN_LANGUAGE, v.getLanguage());
        encodeNillableCodeType(AQDConstants.QN_GN_NATIVENESS, v.getNativeness());
        encodeNillableCodeType(AQDConstants.QN_GN_NAME_STATUS, v.getNameStatus());
        encodeNillableString(AQDConstants.QN_GN_SOURCE_OF_NAME, v.getSourceOfName());
        encodeNillablePronunciation(AQDConstants.QN_GN_PRONUNCIATION, v.getPronunciation());
        encodeNillableSpelling(AQDConstants.QN_GN_SPELLING, v.getSpelling());
        encodeNillableCodeType(AQDConstants.QN_GN_GRAMMATICAL_GENDER, v.getGrammaticalGender());
        encodeNillableCodeType(AQDConstants.QN_GN_GRAMMATICAL_NUMBER, v.getGrammaticalNumber());
        end(AQDConstants.QN_GN_GEOGRAPHICAL_NAME);
    }

    protected void encodeNillableCodeType(QName qn, Nillable<CodeType> v)
            throws XMLStreamException {
        if (!v.isAbsent()) {
            if (v.isNil()) {
                empty(qn);
                encodeNilAttr(v.getNilReason());
            } else {
                start(qn);
                if (v.get().isSetCodeSpace()) {
                    attr(AQDConstants.AN_CODE_SPACE, v.get().getCodeSpace());
                }
                chars(v.get().getValue());
                endInline(qn);
            }
        }
    }

    protected void encodeNillablePronunciation(QName qn,
                                               Nillable<Pronunciation> v)
            throws XMLStreamException {
        if (!v.isAbsent()) {
            if (v.isNil()) {
                empty(qn);
                encodeNilAttr(v.getNilReason());
            } else {
                start(qn);
                encodePronunciationOfName(v.get());
                end(qn);
            }
        }
    }

    protected void encodePronunciationOfName(Pronunciation pronunciation)
            throws XMLStreamException {
        start(AQDConstants.QN_GN_PRONUNCIATION_OF_NAME);
        encodeNillableString(AQDConstants.QN_GN_PRONUNCIATION_SOUND_LINK, pronunciation
                             .getSoundLink());
        encodeNillableString(AQDConstants.QN_GN_PRONUNCIATION_IPA, pronunciation.getIPA());
        end(AQDConstants.QN_GN_PRONUNCIATION_OF_NAME);
    }

    protected void encodeNillableSpelling(QName qn, Nillable<Spelling> v)
            throws XMLStreamException {
        if (!v.isAbsent()) {
            if (v.isNil()) {
                empty(qn);
                encodeNilAttr(v.getNilReason());
            } else {
                start(qn);
                encodeSpellingOfName(v.get());
                end(qn);
            }
        }
    }

    protected void encodeSpellingOfName(Spelling spelling)
            throws XMLStreamException {
        start(AQDConstants.QN_GN_SPELLING_OF_NAME);
        encodeString(AQDConstants.QN_GN_TEXT, spelling.getText());
        encodeNillableString(AQDConstants.QN_GN_SCRIPT, spelling.getScript());
        encodeNillableString(AQDConstants.QN_GN_TRANSLITERATION_SCHEME, spelling
                             .getTransliterationScheme());
        end(AQDConstants.QN_GN_SPELLING_OF_NAME);
    }

    private void encodeTime(Time v) throws XMLStreamException, DateTimeFormatException {
        if (v instanceof TimePeriod) {
            encodeTimePeriod((TimePeriod) v);
        } else if (v instanceof TimeInstant) {
            encodeTimeInstant((TimeInstant) v);
        }
    }

    private void encodeTimeInstant(TimeInstant ti)
            throws XMLStreamException, DateTimeFormatException {
        start(GmlConstants.QN_TIME_INSTANT_32);
        attr(GmlConstants.QN_ID_32, getGMLId(ti));
        start(GmlConstants.QN_TIME_POSITION_32);
        encodeTimeString(ti.getValue(), ti.getTimeFormat());
        endInline(GmlConstants.QN_TIME_POSITION_32);
        end(GmlConstants.QN_TIME_INSTANT_32);
    }

    private void encodeTimePeriod(TimePeriod tp)
            throws XMLStreamException, DateTimeFormatException {
        start(GmlConstants.QN_TIME_PERIOD_32);
        attr(GmlConstants.QN_ID_32, getGMLId(tp));
        start(GmlConstants.QN_BEGIN_POSITION_32);
        encodeTimeString(tp.getStart(), tp.getTimeFormat());
        endInline(GmlConstants.QN_BEGIN_POSITION_32);
        start(GmlConstants.QN_END_POSITION_32);
        encodeTimeString(tp.getEnd(), tp.getTimeFormat());
        endInline(GmlConstants.QN_END_POSITION_32);
        end(GmlConstants.QN_TIME_PERIOD_32);
    }

    protected void encodeTimeString(DateTime time, TimeFormat format)
            throws XMLStreamException,
                   DateTimeFormatException {
        chars(DateTimeHelper.formatDateTime2String(time, format));
    }
}