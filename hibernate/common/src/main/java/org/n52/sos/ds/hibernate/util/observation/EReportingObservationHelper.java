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
package org.n52.sos.ds.hibernate.util.observation;

import java.util.Collection;

import org.n52.sos.aqd.AqdConstants.AssessmentType;
import org.n52.sos.aqd.AqdConstants.ProcessParameter;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingAssessmentType;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingNetwork;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingSamplingPoint;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingSeries;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingStation;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.values.HrefAttributeValue;
import org.n52.sos.ogc.om.values.ReferenceValue;
import org.n52.sos.w3c.xlink.W3CHrefAttribute;

import com.google.common.collect.Lists;

public class EReportingObservationHelper {
    
    public Collection<NamedValue<?>> createOmParameterForEReporting(EReportingSeries series) {
        Collection<NamedValue<?>> namedValues = Lists.newArrayList(createSamplingPointParameter(series));
        EReportingSamplingPoint samplingPoint = series.getSamplingPoint();
        if (samplingPoint.isSetStation()) {
            namedValues.add(getStation(samplingPoint.getStation()));
        }
        if (samplingPoint.isSetNetwork()) {
            namedValues.add(getNetwork(samplingPoint.getNetwork()));
        }
        return namedValues;
    }
    
    public Collection<NamedValue<?>> createSamplingPointParameter(EReportingSeries series) {
        Collection<NamedValue<?>> namedValues = Lists.newArrayListWithCapacity(2);
        namedValues.add(getAssessmentType(series.getSamplingPoint()));
        namedValues.add(getAssesmentMethod(series.getSamplingPoint()));
        return namedValues;
    }

    private NamedValue<?> getStation(EReportingStation station) {
        NamedValue<W3CHrefAttribute> namedValue = new NamedValue<W3CHrefAttribute>();
        namedValue.setName(new ReferenceType(ProcessParameter.MonitoringStation.getConceptURI()));
        namedValue.setValue(createHrefAttributeValue(station.getIdentifier()));
        return namedValue;
    }

    private NamedValue<?> getNetwork(EReportingNetwork network) {
        NamedValue<W3CHrefAttribute> namedValue = new NamedValue<W3CHrefAttribute>();
        namedValue.setName(new ReferenceType(ProcessParameter.Network.getConceptURI()));
        namedValue.setValue(createHrefAttributeValue(network.getIdentifier()));
        return namedValue;
    }

    private NamedValue<?> getAssessmentType(EReportingSamplingPoint samplingPoint) {
        NamedValue<W3CHrefAttribute> namedValue = new NamedValue<W3CHrefAttribute>();
        namedValue.setName(new ReferenceType(ProcessParameter.AssessmentType.getConceptURI()));
        namedValue.setValue(createHrefAttributeValueFromAssessmentType(samplingPoint.getAssessmentType()));
        return namedValue;
    }

    private NamedValue<?> getAssesmentMethod(EReportingSamplingPoint samplingPoint) {
        if (samplingPoint.isSetName()) {
            NamedValue<ReferenceType> namedValue = new NamedValue<ReferenceType>();
            namedValue.setName(new ReferenceType(ProcessParameter.SamplingPoint.getConceptURI()));
            ReferenceValue value = createReferenceValue(samplingPoint.getIdentifier());
//            value.getValue().setTitle(samplingPoint.getName());
            namedValue.setValue(value);
            return namedValue;
        }
        NamedValue<W3CHrefAttribute> namedValue = new NamedValue<W3CHrefAttribute>();
        namedValue.setName(new ReferenceType(ProcessParameter.SamplingPoint.getConceptURI()));
        namedValue.setValue(createHrefAttributeValue(samplingPoint.getIdentifier()));
        return namedValue;
    }
    
    private ReferenceValue createReferenceValue(String value) {
        ReferenceValue referenceValue = new ReferenceValue();
        referenceValue.setValue(new ReferenceType(value));
        return referenceValue;
    }

    private HrefAttributeValue createHrefAttributeValue(String value) {
        HrefAttributeValue hrefAttributeValue = new HrefAttributeValue();
        hrefAttributeValue.setValue(new W3CHrefAttribute(value));
        return hrefAttributeValue;
    }

    private HrefAttributeValue createHrefAttributeValueFromAssessmentType(EReportingAssessmentType assessmentType) {
        if (assessmentType.isSetUri()) {
            return createHrefAttributeValue(assessmentType.getUri());
        } else {
            return createHrefAttributeValue(AssessmentType.fromId(assessmentType.getAssessmentType()).getConceptURI());
        }
    }
}
