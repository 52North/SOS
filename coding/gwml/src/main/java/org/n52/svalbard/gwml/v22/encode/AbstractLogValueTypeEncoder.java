/**
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
package org.n52.svalbard.gwml.v22.encode;

import org.n52.sos.ogc.om.values.LogValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;

import net.opengis.gwmlWell.x21.LogValueType;

public abstract class AbstractLogValueTypeEncoder<T> extends AbstractGroundWaterMLEncoder<T, LogValue> {

    protected LogValueType encodeLogValue(LogValue logValue) throws OwsExceptionReport {
        LogValueType lvt = LogValueType.Factory.newInstance();
        setFromDepth(lvt, logValue);
        setToDepth(lvt, logValue);
        setValue(lvt, logValue);
        return lvt;
    }
    
    private void setFromDepth(LogValueType lvt, LogValue logValue) throws OwsExceptionReport {
        if (logValue.isSetFromDepth()) {
            lvt.addNewFromDepth().addNewQuantity().set(encodeSweCommon(logValue.getFromDepth()));
        }
    }

    private void setToDepth(LogValueType lvt, LogValue logValue) throws OwsExceptionReport {
        if (logValue.isSetToDepth()) {
            lvt.addNewToDepth().addNewQuantity().set(encodeSweCommon(logValue.getToDepth()));
        }
    }

    private void setValue(LogValueType lvt, LogValue logValue) throws OwsExceptionReport {
        if (logValue.isSetValue()) {
            lvt.addNewValue().addNewDataRecord().set(encodeSweCommon(logValue.getValue()));
        }
    }
}
