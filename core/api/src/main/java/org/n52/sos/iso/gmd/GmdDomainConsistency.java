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
package org.n52.sos.iso.gmd;

import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.om.quality.OmResultQuality;

public abstract class GmdDomainConsistency implements OmResultQuality {

    public static GmdConformanceResult dataCapture(boolean pass) {
        return new GmdConformanceResult(pass, GmdSpecification.dataCapture());
    }
    
    public static OmResultQuality dataCapture(GmlConstants.NilReason nilReason) {
        return new GmdConformanceResult(nilReason, GmdSpecification.dataCapture());
    }

    public static GmdConformanceResult timeCoverage(boolean pass) {
        return new GmdConformanceResult(pass, GmdSpecification.timeCoverage());
    }
    
    public static GmdConformanceResult timeCoverage(GmlConstants.NilReason nilReason) {
        return new GmdConformanceResult(nilReason, GmdSpecification.timeCoverage());
    }

    public static GmdQuantitativeResult uncertaintyEstimation(double value) {
        return new GmdQuantitativeResult( GmlBaseUnit.uncertaintyEstimation().unifyId(value),
                                         Double.toString(value));
    }

    public static GmdQuantitativeResult uncertaintyEstimation(GmlConstants.NilReason nilReason) {
        return new GmdQuantitativeResult(GmlBaseUnit.uncertaintyEstimation().unifyId(nilReason), nilReason);
    }

}
