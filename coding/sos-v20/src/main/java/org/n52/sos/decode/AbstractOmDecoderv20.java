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
package org.n52.sos.decode;

import java.util.Set;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.XmlAnyTypeImpl;
import org.n52.sos.exception.ows.concrete.UnsupportedDecoderInputException;
import org.n52.sos.ogc.gml.AbstractGeometry;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.values.BooleanValue;
import org.n52.sos.ogc.om.values.CategoryValue;
import org.n52.sos.ogc.om.values.CountValue;
import org.n52.sos.ogc.om.values.GeometryValue;
import org.n52.sos.ogc.om.values.HrefAttributeValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.ReferenceValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.w3c.xlink.W3CHrefAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Geometry;

import net.opengis.om.x20.NamedValuePropertyType;
import net.opengis.om.x20.NamedValueType;

public abstract class AbstractOmDecoderv20 extends AbstractGmlDecoderv321<Object, Object> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractOmDecoderv20.class);
	
	@Override
	public Object decode(Object object) throws OwsExceptionReport, UnsupportedDecoderInputException {
		if (object instanceof NamedValuePropertyType) {
            return parseNamedValueType((NamedValuePropertyType) object);
        } else if (object instanceof NamedValuePropertyType[]) {
            return parseNamedValueTypeArray((NamedValuePropertyType[]) object);
        }
        throw new UnsupportedDecoderInputException(this, object);
	}
	
	protected Set<NamedValue<?>> parseNamedValueTypeArray(NamedValuePropertyType[] namedValuePropertyArray)
            throws OwsExceptionReport {
        Set<NamedValue<?>> parameters = Sets.newHashSet();
        for (NamedValuePropertyType namedValueProperty : namedValuePropertyArray) {
            parameters.add(parseNamedValueType(namedValueProperty));
        }
        return parameters;
    }

	protected NamedValue<?> parseNamedValueType(NamedValuePropertyType namedValueProperty) throws OwsExceptionReport {
        if (namedValueProperty.isSetNamedValue()) {
            NamedValueType namedValue = namedValueProperty.getNamedValue();
            NamedValue<?> sosNamedValue = parseNamedValueValue(namedValue.getValue());
            ReferenceType referenceType = (ReferenceType) CodingHelper.decodeXmlObject(namedValue.getName());
            sosNamedValue.setName(referenceType);
            return sosNamedValue;
        } else if (namedValueProperty.isSetHref()) {
            NamedValue<?> sosNamedValue = new NamedValue<ReferenceType>();
            ReferenceType referenceType = new ReferenceType(namedValueProperty.getHref());
            if (namedValueProperty.isSetTitle()) {
                referenceType.setTitle(namedValueProperty.getTitle());
            }
            sosNamedValue.setName(referenceType);
            return sosNamedValue;
        } else {
            throw new UnsupportedDecoderInputException(this, namedValueProperty);
        }
    }

	protected NamedValue<?> parseNamedValueValue(XmlObject xmlObject) throws OwsExceptionReport {
        if (xmlObject.schemaType() == XmlAnyTypeImpl.type) {
            try {
                xmlObject = XmlObject.Factory.parse(xmlObject.xmlText().trim());
            } catch (XmlException e) {
                LOGGER.error("Error while parsing NamedValueValue", e);
            }
        }
        Object value = CodingHelper.decodeXmlObject(xmlObject);
        if (value instanceof BooleanValue) {
            NamedValue<Boolean> namedValue = new NamedValue<Boolean>();
            namedValue.setValue((BooleanValue) value);
            return namedValue;
        } else if (value instanceof CategoryValue) {
            NamedValue<String> namedValue = new NamedValue<String>();
            namedValue.setValue((CategoryValue) value);
            return namedValue;
        } else if (value instanceof CountValue) {
            NamedValue<Integer> namedValue = new NamedValue<Integer>();
            namedValue.setValue((CountValue) value);
            return namedValue;
        } else if (value instanceof GeometryValue) {
            NamedValue<Geometry> namedValue = new NamedValue<Geometry>();
            namedValue.setValue((GeometryValue) value);
            return namedValue;
        } else if (value instanceof QuantityValue) {
            NamedValue<Double> namedValue = new NamedValue<Double>();
            namedValue.setValue((QuantityValue) value);
            return namedValue;
        } else if (value instanceof TextValue) {
            NamedValue<String> namedValue = new NamedValue<String>();
            namedValue.setValue((TextValue) value);
            return namedValue;
        } else if (value instanceof AbstractGeometry) {
            NamedValue<Geometry> namedValue = new NamedValue<Geometry>();
            namedValue.setValue(new GeometryValue((AbstractGeometry)value));
            return namedValue;
        } else if (value instanceof ReferenceType) {
            NamedValue<ReferenceType> namedValue = new NamedValue<ReferenceType>();
            namedValue.setValue(new ReferenceValue((ReferenceType)value));
            return namedValue;
        } else if (value instanceof W3CHrefAttribute) {
            NamedValue<W3CHrefAttribute> namedValue = new NamedValue<W3CHrefAttribute>();
            namedValue.setValue(new HrefAttributeValue((W3CHrefAttribute)value));
            return namedValue;
        } else {
            throw new UnsupportedDecoderInputException(this, xmlObject);
        }
    }

}
