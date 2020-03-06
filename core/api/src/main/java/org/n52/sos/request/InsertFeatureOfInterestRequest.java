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
package org.n52.sos.request;

import java.util.ArrayList;
import java.util.List;

import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.response.InsertFeatureOfInterestResponse;

public class InsertFeatureOfInterestRequest extends AbstractServiceRequest<InsertFeatureOfInterestResponse> {

    private List<AbstractFeature> abstractFeatures = new ArrayList<>();

    public List<AbstractFeature> getFeatureMembers() {
        return abstractFeatures;
    }

    public InsertFeatureOfInterestRequest setFeatureMembers(List<AbstractFeature> abstractFeatures) {
        abstractFeatures.clear();
        if (abstractFeatures != null) {
            this.abstractFeatures.addAll(abstractFeatures); 
        }
        return this;
    }
    
    public InsertFeatureOfInterestRequest addFeatureMember(AbstractFeature abstractFeature) {
        if (abstractFeature != null) {
            this.abstractFeatures.add(abstractFeature); 
        }
        return this;
    }
    
    public boolean hasFeatureMembers() {
        return !getFeatureMembers().isEmpty();
    }
    
    @Override
    public InsertFeatureOfInterestResponse getResponse() throws OwsExceptionReport {
        return (InsertFeatureOfInterestResponse) new InsertFeatureOfInterestResponse().set(this);
    }

    @Override
    public String getOperationName() {
        return "InsertFeatureOfInterest";
    }

}
