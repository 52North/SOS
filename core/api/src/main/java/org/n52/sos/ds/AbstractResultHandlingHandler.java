/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds;

import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.swe.SweAbstractDataComponent;
import org.n52.shetland.ogc.swe.SweDataArray;
import org.n52.shetland.ogc.swe.SweDataRecord;

/**
 * Abstract class for Result Handling operation Handlers to provide common methods
 *
 * Renamed, in version 4.x called AbstractResultHandlingDAO
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 5.0.0
 *
 */
public abstract class AbstractResultHandlingHandler extends AbstractOperationHandler {

    public AbstractResultHandlingHandler(String service, String operationName) {
        super(service, operationName);
    }

    /**
     * Get internal SweDataRecord from internal ResultStructure
     *
     * @param resultStructure
     *            Internal ResultStructure
     * @return internal SweDataRecord
     * @throws OwsExceptionReport
     *             If the ResultStructure is not supported
     */
    protected SweDataRecord setRecordFrom(final SweAbstractDataComponent resultStructure) throws OwsExceptionReport {
        SweDataRecord record = null;
        if (resultStructure instanceof SweDataArray
                && ((SweDataArray) resultStructure).getElementType() instanceof SweDataRecord) {
            final SweDataArray array = (SweDataArray) resultStructure;
            record = (SweDataRecord) array.getElementType();
        } else if (resultStructure instanceof SweDataRecord) {
            record = (SweDataRecord) resultStructure;
        } else {
            throw new NoApplicableCodeException().withMessage("Unsupported ResultStructure!");
        }
        return record;
    }

}
