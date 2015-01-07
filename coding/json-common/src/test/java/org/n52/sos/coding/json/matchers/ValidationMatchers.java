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
package org.n52.sos.coding.json.matchers;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.coding.json.JSONValidator;
import org.n52.sos.coding.json.SchemaConstants;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <autermann@uni-muenster.de>
 * 
 * @since 4.0.0
 */
public class ValidationMatchers {
    @Factory
    public static Matcher<JsonNode> instanceOf(String schemaURI) {
        return new IsValidInstance(schemaURI);
    }

    @Factory
    public static Matcher<JsonNode> validObservation() {
        return new IsValidInstance(SchemaConstants.Observation.OBSERVATION);
    }

    @Factory
    public static Matcher<JsonNode> validSchema() {
        return new IsValidInstance(SchemaConstants.SCHEMA_URI);
    }

    public static class IsValidInstance extends TypeSafeDiagnosingMatcher<JsonNode> {
        private final String schemaURI;

        public IsValidInstance(String schemaURI) {
            this.schemaURI = schemaURI;
        }

        @Override
        protected boolean matchesSafely(JsonNode item, Description mismatchDescription) {
            try {
                JsonSchema jsonSchema = JSONValidator.getInstance().getJsonSchemaFactory().getJsonSchema(schemaURI);
                ProcessingReport report = jsonSchema.validate(item);
                return describeProcessingReport(report, item, mismatchDescription);
            } catch (ProcessingException ex) {
                mismatchDescription.appendText(ex.getMessage());
            } catch (JsonProcessingException ex) {
                mismatchDescription.appendText(ex.getMessage());
            }
            return false;
        }

        protected boolean describeProcessingReport(ProcessingReport report, JsonNode item,
                Description mismatchDescription) throws JsonProcessingException {
            if (!report.isSuccess()) {
                ObjectNode objectNode = JacksonUtils.nodeFactory().objectNode();
                objectNode.put(JSONConstants.INSTANCE, item);
                ArrayNode errors = objectNode.putArray(JSONConstants.ERRORS);
                for (ProcessingMessage m : report) {
                    errors.add(m.asJson());
                }
                mismatchDescription.appendText(JacksonUtils.prettyPrint(objectNode));
            }
            return report.isSuccess();
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("valid instance of ").appendText(schemaURI);
        }
    }
}
