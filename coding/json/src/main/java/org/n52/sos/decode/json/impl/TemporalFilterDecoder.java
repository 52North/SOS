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
package org.n52.sos.decode.json.impl;

import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.coding.json.JSONValidator;
import org.n52.sos.coding.json.SchemaConstants;
import org.n52.sos.decode.json.JSONDecoder;
import org.n52.sos.exception.ows.concrete.DateTimeParseException;
import org.n52.sos.ogc.filter.FilterConstants.TimeOperator;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.ows.OwsExceptionReport;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class TemporalFilterDecoder extends JSONDecoder<TemporalFilter> {
    public TemporalFilterDecoder() {
        super(TemporalFilter.class);
    }

    @Override
    public TemporalFilter decodeJSON(JsonNode node, boolean validate) throws OwsExceptionReport {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return null;
        }
        if (validate) {
            JSONValidator.getInstance().validateAndThrow(node, SchemaConstants.Common.TEMPORAL_FILTER);
        }
        if (node.isObject()) {
            return parseTemporalFilter(node);
        } else {
            return null;
        }
    }

    protected TemporalFilter parseTemporalFilter(JsonNode node) throws DateTimeParseException {
        if (node.isObject()) {
            final String oName = node.fields().next().getKey();
            final TOp o = TOp.valueOf(oName);
            return new TemporalFilter(o.getOp(), parseTime(node.path(oName).path(JSONConstants.VALUE)), node
                    .path(oName).path(JSONConstants.REF).textValue());
        } else {
            return null;
        }
    }

    private enum TOp {
        before(TimeOperator.TM_Before), after(TimeOperator.TM_After), begins(TimeOperator.TM_Begins), ends(
                TimeOperator.TM_Ends), endedBy(TimeOperator.TM_EndedBy), begunBy(TimeOperator.TM_BegunBy), during(
                TimeOperator.TM_During), equals(TimeOperator.TM_Equals), contains(TimeOperator.TM_Contains), overlaps(
                TimeOperator.TM_Overlaps), meets(TimeOperator.TM_Meets), metBy(TimeOperator.TM_MetBy), overlappedBy(
                TimeOperator.TM_OverlappedBy);
        private TimeOperator op;

        private TOp(TimeOperator op) {
            this.op = op;
        }

        public TimeOperator getOp() {
            return op;
        }
    }
}
