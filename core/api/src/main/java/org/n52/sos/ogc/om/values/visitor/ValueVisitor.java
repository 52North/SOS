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
package org.n52.sos.ogc.om.values.visitor;

import org.n52.sos.ogc.om.values.BooleanValue;
import org.n52.sos.ogc.om.values.CategoryValue;
import org.n52.sos.ogc.om.values.ComplexValue;
import org.n52.sos.ogc.om.values.CountValue;
import org.n52.sos.ogc.om.values.CvDiscretePointCoverage;
import org.n52.sos.ogc.om.values.ProfileValue;
import org.n52.sos.ogc.om.values.QuantityRangeValue;
import org.n52.sos.ogc.om.values.GeometryValue;
import org.n52.sos.ogc.om.values.HrefAttributeValue;
import org.n52.sos.ogc.om.values.MultiPointCoverage;
import org.n52.sos.ogc.om.values.NilTemplateValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.RectifiedGridCoverage;
import org.n52.sos.ogc.om.values.ReferenceValue;
import org.n52.sos.ogc.om.values.SweDataArrayValue;
import org.n52.sos.ogc.om.values.TLVTValue;
import org.n52.sos.ogc.om.values.TVPValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.om.values.UnknownValue;
import org.n52.sos.ogc.om.values.XmlValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public interface ValueVisitor<T> {
    T visit(BooleanValue value)
            throws OwsExceptionReport;

    T visit(CategoryValue value)
            throws OwsExceptionReport;

    T visit(ComplexValue value)
            throws OwsExceptionReport;

    T visit(CountValue value)
            throws OwsExceptionReport;

    T visit(GeometryValue value)
            throws OwsExceptionReport;

    T visit(HrefAttributeValue value)
            throws OwsExceptionReport;

    T visit(NilTemplateValue value)
            throws OwsExceptionReport;

    T visit(QuantityValue value)
            throws OwsExceptionReport;

    T visit(ReferenceValue value)
            throws OwsExceptionReport;

    T visit(SweDataArrayValue value)
            throws OwsExceptionReport;

    T visit(TVPValue value)
            throws OwsExceptionReport;
    
    T visit(TLVTValue value)
            throws OwsExceptionReport;

    T visit(TextValue value)
            throws OwsExceptionReport;
    
    T visit(CvDiscretePointCoverage value)
            throws OwsExceptionReport;

    T visit(MultiPointCoverage value)
            throws OwsExceptionReport;

    T visit(RectifiedGridCoverage value)
            throws OwsExceptionReport;
    
    T visit(ProfileValue value)
            throws OwsExceptionReport;
    
    T visit(UnknownValue value)
            throws OwsExceptionReport;

    T visit(XmlValue value)
            throws OwsExceptionReport;

    T visit(QuantityRangeValue value)
            throws OwsExceptionReport;
    
}
