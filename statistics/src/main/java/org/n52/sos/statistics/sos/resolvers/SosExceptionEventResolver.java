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
package org.n52.sos.statistics.sos.resolvers;

import java.util.Map;

import javax.inject.Inject;

import org.n52.iceland.exception.CodedException;
import org.n52.iceland.exception.ows.OwsExceptionReport;
import org.n52.sos.statistics.api.interfaces.IStatisticsServiceEventResolver;
import org.n52.sos.statistics.sos.handlers.exceptions.SosCodedExceptionEventResolver;
import org.n52.sos.statistics.sos.handlers.exceptions.SosOwsExceptionEventResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SosExceptionEventResolver implements IStatisticsServiceEventResolver {

    private static final Logger logger = LoggerFactory.getLogger(SosExceptionEventResolver.class);

    private Exception exception;

    @Inject
    private SosCodedExceptionEventResolver codedExceptionEventResolver;

    @Inject
    private SosOwsExceptionEventResolver owsExceptionEventResolver;

    @Override
    public Map<String, Object> resolve() {
        //Objects.requireNonNull(exception);
    	if(exception == null) {
    		return null;
    	}

        Map<String, Object> dataMap = null;
        if (exception instanceof CodedException) {
            dataMap = codedExceptionEventResolver.resolveAsMap((CodedException) exception);
        } else if (exception instanceof OwsExceptionReport) {
            dataMap = owsExceptionEventResolver.resolveAsMap((OwsExceptionReport) exception);
        } else {
            logger.warn("No appropriate ExceptionEventResolver for type {}", exception.getClass());
            return null;
        }
        return dataMap;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

}
