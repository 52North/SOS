/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds;

import java.util.Collections;
import java.util.Set;

import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.OwsAnyValue;
import org.n52.shetland.ogc.ows.OwsDomain;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.request.InsertResultTemplateRequest;
import org.n52.shetland.ogc.sos.response.InsertResultTemplateResponse;

/**
 * Renamed, in version 4.x called AbstractInsertResultTemplateDAO
 *
 * @since 5.0.0
 *
 */
public abstract class AbstractInsertResultTemplateHandler extends AbstractResultHandlingHandler {
    public AbstractInsertResultTemplateHandler(String service) {
        super(service, Sos2Constants.Operations.InsertResultTemplate.name());
    }

    public abstract InsertResultTemplateResponse insertResultTemplate(InsertResultTemplateRequest request)
            throws OwsExceptionReport;

    @Override
    protected Set<OwsDomain> getOperationParameters(String service, String version) throws OwsExceptionReport {
        return Collections.singleton(new OwsDomain(Sos2Constants.InsertResultTemplateParams.proposedTemplate, OwsAnyValue.instance()));
    }
}
