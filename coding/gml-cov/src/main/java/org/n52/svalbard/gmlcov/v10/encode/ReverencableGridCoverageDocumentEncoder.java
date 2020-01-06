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
package org.n52.svalbard.gmlcov.v10.encode;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.encode.Encoder;
import org.n52.sos.encode.EncoderKey;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.om.values.ReferencableGridCoverage;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;

import net.opengis.gmlcov.x10.AbstractDiscreteCoverageType;
import net.opengis.gmlcov.x10.ReferenceableGridCoverageDocument;

/**
 * {@link Encoder} implementation to encode {@link ReferencableGridCoverage} to
 * {@link ReferenceableGridCoverageDocument}
 * 
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public class ReverencableGridCoverageDocumentEncoder
        extends AbstractReverencableGridCoverageType<ReferenceableGridCoverageDocument> {

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        // TODO Auto-generated method stub
        return Collections.emptySet();
    }

    @Override
    public ReferenceableGridCoverageDocument encode(ReferencableGridCoverage objectToEncode)
            throws OwsExceptionReport, UnsupportedEncoderInputException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ReferenceableGridCoverageDocument encode(ReferencableGridCoverage objectToEncode,
            Map<HelperValues, String> additionalValues) throws OwsExceptionReport, UnsupportedEncoderInputException {
        // TODO Auto-generated method stub
        return null;
    }

    private XmlObject create() {
        ReferenceableGridCoverageDocument rgcd = ReferenceableGridCoverageDocument.Factory.newInstance();
        AbstractDiscreteCoverageType rgct = rgcd.addNewReferenceableGridCoverage();
        return rgcd;
    }

}
