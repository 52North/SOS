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

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.response.DeleteResultTemplateResponse;
import org.n52.sos.util.CollectionHelper;

public class DeleteResultTemplateRequest extends AbstractServiceRequest<DeleteResultTemplateResponse> {

    private List<String> resultTemplates;
    private List<AbstractMap.SimpleEntry<String,String>> observedPropertyOfferingPairs;

    @Override
    public DeleteResultTemplateResponse getResponse() throws OwsExceptionReport {
        return (DeleteResultTemplateResponse) new DeleteResultTemplateResponse().set(this);
    }

    @Override
    public String getOperationName() {
        return "DeleteResultTemplate";
    }

    public DeleteResultTemplateRequest addResultTemplate(String resultTemplateId) {
        if (!isSetResultTemplates()) {
            resultTemplates = Lists.newArrayList();
        }
        if (!Strings.isNullOrEmpty(resultTemplateId)) {
            resultTemplates.add(resultTemplateId);
        }
        return this;
    }

    public boolean isSetResultTemplates() {
        return CollectionHelper.isNotEmpty(resultTemplates);
    }

    public List<String> getResultTemplates() {
        if (isSetResultTemplates()) {
            return resultTemplates;
        } else {
            return Collections.emptyList();
        }
    }

    public DeleteResultTemplateRequest addObservedPropertyOfferingPair(String observedProperty, String offering) {
        if (!isSetObservedPropertyOfferingPairs()) {
            observedPropertyOfferingPairs = Lists.newArrayList();
        }
        observedPropertyOfferingPairs.add(new AbstractMap.SimpleEntry<>(observedProperty, offering));
        return this;
    }

    public boolean isSetObservedPropertyOfferingPairs() {
        return observedPropertyOfferingPairs != null && !observedPropertyOfferingPairs.isEmpty();
    }
    
    public List<AbstractMap.SimpleEntry<String, String>> getObservedPropertyOfferingPairs() {
        if (isSetObservedPropertyOfferingPairs()) {
            return observedPropertyOfferingPairs;
        } else {
            return Collections.emptyList();
        }
    }

}
