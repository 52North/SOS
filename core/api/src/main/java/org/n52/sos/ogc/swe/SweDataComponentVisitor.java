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
package org.n52.sos.ogc.swe;

import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.elements.SmlPosition;
import org.n52.sos.ogc.sensorML.v20.SmlDataInterface;
import org.n52.sos.ogc.sensorML.v20.SmlFeatureOfInterest;
import org.n52.sos.ogc.swe.simpleType.SweBoolean;
import org.n52.sos.ogc.swe.simpleType.SweCategory;
import org.n52.sos.ogc.swe.simpleType.SweCategoryRange;
import org.n52.sos.ogc.swe.simpleType.SweCount;
import org.n52.sos.ogc.swe.simpleType.SweCountRange;
import org.n52.sos.ogc.swe.simpleType.SweObservableProperty;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.ogc.swe.simpleType.SweQuantityRange;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.ogc.swe.simpleType.SweTime;
import org.n52.sos.ogc.swe.simpleType.SweTimeRange;
import org.n52.sos.ogc.swe.stream.StreamingSweDataArray;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public interface SweDataComponentVisitor<T> {
    T visit(SweField component)
            throws OwsExceptionReport;

    T visit(SweDataRecord component)
            throws OwsExceptionReport;

    T visit(SweDataArray component)
            throws OwsExceptionReport;

    T visit(SweCount component)
            throws OwsExceptionReport;

    T visit(SweCountRange component)
            throws OwsExceptionReport;

    T visit(SweBoolean component)
            throws OwsExceptionReport;

    T visit(SweCategory component)
            throws OwsExceptionReport;
    
    T visit(SweCategoryRange component)
            throws OwsExceptionReport;

    T visit(SweObservableProperty component)
            throws OwsExceptionReport;

    T visit(SweQuantity component)
            throws OwsExceptionReport;

    T visit(SweQuantityRange component)
            throws OwsExceptionReport;

    T visit(SweText component)
            throws OwsExceptionReport;

    T visit(SweTime component)
            throws OwsExceptionReport;

    T visit(SweTimeRange component)
            throws OwsExceptionReport;

    T visit(SweEnvelope component)
            throws OwsExceptionReport;

    T visit(SweVector component)
            throws OwsExceptionReport;

    T visit(StreamingSweDataArray component)
            throws OwsExceptionReport;

    T visit(SweSimpleDataRecord component)
            throws OwsExceptionReport;

    T visit(SmlPosition component)
            throws OwsExceptionReport;
    
    T visit(SmlDataInterface component)
            throws OwsExceptionReport;

    T visit(SmlFeatureOfInterest component)
            throws OwsExceptionReport;

}
