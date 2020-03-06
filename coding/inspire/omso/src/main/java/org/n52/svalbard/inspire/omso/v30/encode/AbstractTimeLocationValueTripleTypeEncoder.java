/**
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package org.n52.svalbard.inspire.omso.v30.encode;

import java.util.Map;

import org.apache.xmlbeans.XmlObject;
import org.joda.time.DateTime;
import org.n52.sos.encode.AbstractSpecificXmlEncoder;
import org.n52.sos.encode.Encoder;
import org.n52.sos.exception.ows.concrete.DateTimeFormatException;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.TimeLocationValueTriple;
import org.n52.sos.ogc.om.values.CategoryValue;
import org.n52.sos.ogc.om.values.CountValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.ogc.swe.SweConstants;
import org.n52.sos.ogc.swe.simpleType.SweCategory;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.DateTimeHelper;
import org.n52.svalbard.inspire.base.InspireBaseConstants;
import org.n52.svalbard.inspire.omor.InspireOMORConstants;
import org.n52.svalbard.inspire.omso.InspireOMSOConstants;

import eu.europa.ec.inspire.schemas.omso.x30.CategoricalTimeLocationValueTripleType;
import eu.europa.ec.inspire.schemas.omso.x30.MeasurementTimeLocationValueTripleType;
import eu.europa.ec.inspire.schemas.omso.x30.TimeLocationValueTripleType;
import net.opengis.waterml.x20.TimeValuePairType;

/**
 * Abstract {@link Encoder} for {@link TimeLocationValueTriple}
 * 
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 * @param <T>
 */
public abstract class AbstractTimeLocationValueTripleTypeEncoder<T>
        extends AbstractSpecificXmlEncoder<T, TimeLocationValueTriple> {

    @Override
    public void addNamespacePrefixToMap(Map<String, String> nameSpacePrefixMap) {
        super.addNamespacePrefixToMap(nameSpacePrefixMap);
        nameSpacePrefixMap.put(InspireBaseConstants.NS_BASE, InspireBaseConstants.NS_BASE_PREFIX);
        nameSpacePrefixMap.put(InspireOMORConstants.NS_OMOR_30, InspireOMORConstants.NS_OMOR_PREFIX);
        nameSpacePrefixMap.put(InspireOMSOConstants.NS_OMSO_30, InspireOMSOConstants.NS_OMSO_PREFIX);
    }

    /**
     * Encode {@link TimeLocationValueTriple} to
     * {@link TimeLocationValueTripleType}
     * 
     * @param timeLocationValueTriple
     *            The {@link TimeLocationValueTriple} to encode
     * @return The encoded {@link TimeLocationValueTriple}
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    protected TimeValuePairType encodeTimeLocationValueTriple(TimeLocationValueTriple timeLocationValueTriple)
            throws OwsExceptionReport {
        if (timeLocationValueTriple.getValue() instanceof QuantityValue
                || timeLocationValueTriple.getValue() instanceof CountValue) {
            return createMeasurementTimeLocationValueTripleType(timeLocationValueTriple);
        } else if (timeLocationValueTriple.getValue() instanceof CategoryValue) {
            return createCategoricalTimeLocationValueTripleType(timeLocationValueTriple);
        } else {
            // TODO throw exception
        }
        return null;
    }

    /**
     * Create a {@link MeasurementTimeLocationValueTripleType} from
     * {@link TimeLocationValueTriple}
     * 
     * @param timeLocationValueTriple
     *            The {@link TimeLocationValueTriple} to encode
     * @return The encoded {@link TimeLocationValueTriple}
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private TimeValuePairType createMeasurementTimeLocationValueTripleType(
            TimeLocationValueTriple timeLocationValueTriple) throws OwsExceptionReport {
        MeasurementTimeLocationValueTripleType mtlvtt = MeasurementTimeLocationValueTripleType.Factory.newInstance();
        mtlvtt.addNewTime().setStringValue(getTimeString(timeLocationValueTriple.getTime()));
        mtlvtt.addNewLocation().addNewPoint().set(encodeGML(timeLocationValueTriple.getLocation()));
        String value = null;
        if (timeLocationValueTriple.getValue() instanceof QuantityValue) {
            QuantityValue quantityValue = (QuantityValue) timeLocationValueTriple.getValue();
            if (!quantityValue.getValue().equals(Double.NaN)) {
                value = Double.toString(quantityValue.getValue().doubleValue());
            }
        } else if (timeLocationValueTriple.getValue() instanceof CountValue) {
            CountValue countValue = (CountValue) timeLocationValueTriple.getValue();
            if (countValue.getValue() != null) {
                value = Integer.toString(countValue.getValue().intValue());
            }
        }
        if (value != null && !value.isEmpty()) {
            mtlvtt.addNewValue().setStringValue(value);
        } else {
            mtlvtt.addNewValue().setNil();
            mtlvtt.addNewMetadata().addNewTVPMeasurementMetadata().addNewNilReason().setNilReason("missing");
        }
        return mtlvtt;
    }

    /**
     * Create a {@link CategoricalTimeLocationValueTripleType} from
     * {@link TimeLocationValueTriple}
     * 
     * @param timeLocationValueTriple
     *            The {@link TimeLocationValueTriple} to encode
     * @return The encoded {@link TimeLocationValueTriple}
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private TimeValuePairType createCategoricalTimeLocationValueTripleType(
            TimeLocationValueTriple timeLocationValueTriple) throws OwsExceptionReport {
        CategoricalTimeLocationValueTripleType ctlvtt = CategoricalTimeLocationValueTripleType.Factory.newInstance();
        ctlvtt.addNewTime().setStringValue(getTimeString(timeLocationValueTriple.getTime()));
        ctlvtt.addNewLocation().addNewPoint().set(encodeGML(timeLocationValueTriple.getLocation()));
        if (timeLocationValueTriple.getValue() instanceof CategoryValue) {
            CategoryValue categoryValue = (CategoryValue) timeLocationValueTriple.getValue();
            if (categoryValue.isSetValue()) {
                ctlvtt.addNewValue().addNewCategory().set(encodeSweCommon(convertToSweCategory(categoryValue)));
            } else {
                ctlvtt.addNewValue().setNil();
                ctlvtt.addNewMetadata().addNewTVPMetadata().addNewNilReason().setNilReason("missing");
            }
        }

        return ctlvtt;
    }

    /**
     * Convert {@link CategoryValue} to {@link SweCategory}
     * 
     * @param categoryValue
     *            The {@link CategoryValue} to convert
     * @return Converted {@link CategoryValue}
     */
    private SweCategory convertToSweCategory(CategoryValue categoryValue) {
        SweCategory sweCategory = new SweCategory();
        sweCategory.setValue(categoryValue.getValue());
        sweCategory.setCodeSpace(categoryValue.getUnit());
        return sweCategory;
    }

    /**
     * Parses the ITime object to a time representation as String
     *
     * @param time
     *            SOS ITime object
     * @return Time as String
     * @throws DateTimeFormatException
     *             If a formatting error occurs
     */
    protected String getTimeString(Time time) throws DateTimeFormatException {
        DateTime dateTime = getTime(time);
        return DateTimeHelper.formatDateTime2String(dateTime, time.getTimeFormat());
    }

    /**
     * Get the time representation from ITime object
     *
     * @param time
     *            ITime object
     * @return Time as DateTime
     */
    private DateTime getTime(Time time) {
        if (time instanceof TimeInstant) {
            return ((TimeInstant) time).getValue();
        } else if (time instanceof TimePeriod) {
            TimePeriod timePeriod = (TimePeriod) time;
            if (timePeriod.getEnd() != null) {
                return timePeriod.getEnd();
            } else {
                return timePeriod.getStart();
            }
        }
        return new DateTime().minusYears(1000);
    }

    protected static XmlObject encodeGML(Object o) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, o);
    }

    protected static XmlObject encodeGML(Object o, Map<HelperValues, String> helperValues) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, o, helperValues);
    }

    protected static XmlObject encodeSweCommon(Object o) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(SweConstants.NS_SWE_20, o);
    }

    protected static XmlObject encodeSweCommon(Object o, Map<HelperValues, String> helperValues)
            throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(SweConstants.NS_SWE_20, o, helperValues);
    }
}
