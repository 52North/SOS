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
package org.n52.sos.encode.json.inspire;

import org.n52.sos.encode.json.JSONEncoder;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.AQDJSONConstants;
import org.n52.sos.util.Reference;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class ReferenceJSONEncoder extends JSONEncoder<Reference> {

    public ReferenceJSONEncoder() {
        super(Reference.class);
    }

    @Override
    public JsonNode encodeJSON(Reference t)
            throws OwsExceptionReport {
        ObjectNode ref = nodeFactory().objectNode();
        ref.put(AQDJSONConstants.HREF, t.getHref().toString());
        if (t.getActuate().isPresent()) {
            ref.put(AQDJSONConstants.ACTUATE, t.getActuate().get());
        }
        if (t.getArcrole().isPresent()) {
            ref.put(AQDJSONConstants.ARCROLE, t.getArcrole().get());
        }
        if (t.getRemoteSchema().isPresent()) {
            ref.put(AQDJSONConstants.REMOTE_SCHEMA, t.getRemoteSchema().get());
        }
        if (t.getRole().isPresent()) {
            ref.put(AQDJSONConstants.ROLE, t.getRole().get());
        }
        if (t.getShow().isPresent()) {
            ref.put(AQDJSONConstants.SHOW, t.getShow().get());
        }
        if (t.getTitle().isPresent()) {
            ref.put(AQDJSONConstants.TITLE, t.getTitle().get());
        }
        if (t.getType().isPresent()) {
            ref.put(AQDJSONConstants.TYPE, t.getType().get());
        }
        return ref;
    }

}
