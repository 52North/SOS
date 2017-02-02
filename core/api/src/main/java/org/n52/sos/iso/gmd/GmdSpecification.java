/**
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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

import org.n52.sos.iso.gco.AbtractGmd;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class GmdSpecification extends AbtractGmd {

    private static final GmdSpecification DATA_CAPTURE_SPECIFICATION
            = new GmdSpecification("Data Capture", GmdCitation
                                   .airQualityDirectiveEC502008());
    private static final GmdSpecification TIME_COVERAGE_SPECIFICATION
            = new GmdSpecification("Time Coverage", GmdCitation
                                   .airQualityDirectiveEC502008());
    private final String explanation;
    private final GmdCitation citation;

    public GmdSpecification(String explanation, GmdCitation citation) {
        this.explanation = explanation;
        this.citation = citation;
    }

    public String getExplanation() {
        return explanation;
    }

    public GmdCitation getCitation() {
        return citation;
    }

    public static GmdSpecification dataCapture() {
        return DATA_CAPTURE_SPECIFICATION;
    }

    public static GmdSpecification timeCoverage() {
        return TIME_COVERAGE_SPECIFICATION;
    }

}
