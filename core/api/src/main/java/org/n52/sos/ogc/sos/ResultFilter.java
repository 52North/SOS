/**
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
package org.n52.sos.ogc.sos;

import org.n52.sos.ogc.filter.ComparisonFilter;
import org.n52.sos.ogc.swes.SwesExtension;

/**
 * 
 * @author Carsten Hollmann
 * @since 4.4.1
 *
 */
public class ResultFilter implements SwesExtension<ComparisonFilter> {
    
    private ComparisonFilter filter;

    public ResultFilter(ComparisonFilter filter) {
        setValue(filter);
    }
    
    @Override
    public String getNamespace() {
        return ResultFilterConstants.NS_RF;
    }

    @Override
    public ResultFilter setNamespace(String namespace) {
        return this;
    }

    @Override
    public boolean isSetNamespace() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return ResultFilterConstants.RESULT_FILTER;
    }

    @Override
    public ResultFilter setIdentifier(String identifier) {
        return null;
    }

    @Override
    public boolean isSetIdentifier() {
        return true;
    }

    @Override
    public String getDefinition() {
        return ResultFilterConstants.RESULT_FILTER;
    }

    @Override
    public ResultFilter setDefinition(String definition) {
        return this;
    }

    @Override
    public boolean isSetDefinition() {
        return true;
    }

    @Override
    public ComparisonFilter getValue() {
        return filter;
    }

    @Override
    public ResultFilter setValue(ComparisonFilter value) {
        this.filter = value;
        return this;
    }
    
}
