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
package org.n52.sos.request;

import org.joda.time.DateTime;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosResultEncoding;
import org.n52.sos.ogc.sos.SosResultStructure;
import org.n52.sos.response.InsertResultTemplateResponse;
import org.n52.sos.util.JavaHelper;
import org.n52.sos.util.StringHelper;

import com.google.common.base.Strings;

/**
 * @since 4.0.0
 */
public class InsertResultTemplateRequest extends AbstractServiceRequest<InsertResultTemplateResponse> {

    private String identifier;

    private OmObservationConstellation observationTemplate;

    private SosResultStructure resultStructure;

    private SosResultEncoding resultEncoding;

    @Override
    public String getOperationName() {
        return Sos2Constants.Operations.InsertResultTemplate.name();
    }

    public String getIdentifier() {
        if (Strings.isNullOrEmpty(identifier)) {
            StringBuilder builder = new StringBuilder();
            builder.append(getObservationTemplate().toString());
            builder.append(new DateTime().getMillis());
            identifier = JavaHelper.generateID(builder.toString());
        }
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public boolean isSetIdentifier() {
        return StringHelper.isNotEmpty(getIdentifier());
    }

    public OmObservationConstellation getObservationTemplate() {
        return observationTemplate;
    }

    public void setObservationTemplate(OmObservationConstellation observationConstellation) {
        this.observationTemplate = observationConstellation;
    }

    public boolean isSetObservatioTenmplate() {
        return getObservationTemplate() != null && !getObservationTemplate().isEmpty();
    }

    public SosResultStructure getResultStructure() {
        return resultStructure;
    }

    public void setResultStructure(SosResultStructure resultStructure) {
        this.resultStructure = resultStructure;
    }

    public boolean isSetResultStructure() {
        return getResultStructure() != null && !getResultStructure().isEmpty();
    }

    public SosResultEncoding getResultEncoding() {
        return resultEncoding;
    }

    public void setResultEncoding(SosResultEncoding resultEncoding) {
        this.resultEncoding = resultEncoding;
    }

    public boolean isSetResultEncoding() {
        return getResultEncoding() != null && !getResultEncoding().isEmpty();
    }

    @Override
    public InsertResultTemplateResponse getResponse() throws OwsExceptionReport {
        return (InsertResultTemplateResponse) new InsertResultTemplateResponse().set(this);
    }

}
