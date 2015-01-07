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
package org.n52.sos.ds.hibernate.entities.ereporting;

import java.io.Serializable;

import org.n52.sos.util.StringHelper;

public class EReportingAssessmentType implements Serializable {
    
    private static final long serialVersionUID = 1721670905098408954L;
    
    public static final String ID = "assessmentType";
    
    public static final String ASSESSMENT_TYPE = ID;

    private String assessmentType;
    
    private String uri;

    /**
     * @return the assessmentType
     */
    public String getAssessmentType() {
        return assessmentType;
    }

    /**
     * @param assessmentType the assessmentType to set
     */
    public EReportingAssessmentType setAssessmentType(String assessmentType) {
        this.assessmentType = assessmentType;
        return this;
    }

    /**
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * @param uri the uri to set
     */
    public EReportingAssessmentType setUri(String uri) {
        this.uri = uri;
        return this;
    }
    
    public boolean isSetAssessmentType() {
        return StringHelper.isNotEmpty(getAssessmentType());
    }
    
    public boolean isSetUri() {
        return StringHelper.isNotEmpty(getUri());
    }
}
