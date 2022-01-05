/*
 * Copyright (C) 2012-2022 52°North Initiative for Geospatial Open Source
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

import org.n52.shetland.ogc.sos.SosResultEncoding;
import org.n52.shetland.ogc.sos.SosResultStructure;
import org.n52.shetland.ogc.sos.SosResultTemplate;
import org.n52.shetland.ogc.sos.request.InsertResultTemplateRequest;

public class InternalInsertResultTemplateRequest extends InsertResultTemplateRequest {

    private final SosResultTemplate observationResultTemplate = new SosResultTemplate();

    public SosResultStructure getObservationStructure() {
        return this.observationResultTemplate.getResultStructure();
    }

    public void setObservationStructure(SosResultStructure resultStructure) {
        this.observationResultTemplate.setResultStructure(resultStructure);
    }

    public boolean isSetObservationStructure() {
        return getObservationStructure() != null;
    }

    public SosResultEncoding getObservationEncoding() {
        return this.observationResultTemplate.getResultEncoding();
    }

    public void setObservationEncoding(SosResultEncoding resultEncoding) {
        this.observationResultTemplate.setResultEncoding(resultEncoding);
    }

    public boolean isSetObservationEncoding() {
        return getObservationEncoding() != null;
    }
}
