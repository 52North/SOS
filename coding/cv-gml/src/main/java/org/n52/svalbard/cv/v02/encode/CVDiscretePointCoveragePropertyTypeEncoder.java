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
package org.n52.svalbard.cv.v02.encode;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import org.n52.sos.encode.ClassToClassEncoderKey;
import org.n52.sos.encode.Encoder;
import org.n52.sos.encode.EncoderKey;
import org.n52.sos.encode.XmlPropertyTypeEncoderKey;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.cv.CvConstants;
import org.n52.sos.ogc.om.values.CvDiscretePointCoverage;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;

import com.google.common.collect.Sets;

import net.opengis.cv.x02.gml32.CVDiscretePointCoveragePropertyType;

/**
 * {@link Encoder} for {@link CvDiscretePointCoverage} to encode to {@link CVDiscretePointCoveragePropertyType}
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public class CVDiscretePointCoveragePropertyTypeEncoder extends AbstractCVDiscretePointCoverageTypeEncoder<CVDiscretePointCoveragePropertyType> {
    
    protected static final Set<EncoderKey> ENCODER_KEYS = Sets.newHashSet(
            new ClassToClassEncoderKey(CvDiscretePointCoverage.class, CVDiscretePointCoveragePropertyType.class),
            new XmlPropertyTypeEncoderKey(CvConstants.NS_CV, CvDiscretePointCoverage.class));

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return Collections.unmodifiableSet(ENCODER_KEYS);
    }
    
    @Override
    public CVDiscretePointCoveragePropertyType encode(CvDiscretePointCoverage cvDiscretePointCoverage)
            throws OwsExceptionReport, UnsupportedEncoderInputException {
        return encode(cvDiscretePointCoverage, new EnumMap<HelperValues, String>(HelperValues.class));
    }

    @Override
    public CVDiscretePointCoveragePropertyType encode(CvDiscretePointCoverage cvDiscretePointCoverage, Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport, UnsupportedEncoderInputException {
        // spatialObservation + measured value 
        CVDiscretePointCoveragePropertyType cvdpcpt = CVDiscretePointCoveragePropertyType.Factory.newInstance(getXmlOptions());
        cvdpcpt.setCVDiscretePointCoverage(encodeCVDiscretePointCoverage(cvDiscretePointCoverage));
        return cvdpcpt;
    }

}
