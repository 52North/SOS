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
import org.n52.sos.inspire.aqd.Contact;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.AQDJSONConstants;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class ContactJSONEncoder extends JSONEncoder<Contact> {

    public ContactJSONEncoder() {
        super(Contact.class);
    }

    @Override
    public JsonNode encodeJSON(Contact t)
            throws OwsExceptionReport {
        ObjectNode j = nodeFactory().objectNode();
        j.put(AQDJSONConstants.ADDRESS, encodeObjectToJson(t.getAddress()));
        j.put(AQDJSONConstants.CONTACT_INSTRUCTIONS, encodeObjectToJson(t.getContactInstructions()));
        j.put(AQDJSONConstants.ELECTRONIC_MAIL_ADDRESS, encodeObjectToJson(t.getElectronicMailAddress()));
        j.put(AQDJSONConstants.HOURS_OF_SERVICE, encodeObjectToJson(t.getHoursOfService()));
        j.put(AQDJSONConstants.TELEPHONE_FACSIMILE, encodeObjectToJson(t.getTelephoneFacsimile()));
        j.put(AQDJSONConstants.TELEPHONE_VOICE, encodeObjectToJson(t.getTelephoneVoice()));
        j.put(AQDJSONConstants.WEBSITE, encodeObjectToJson(t.getWebsite()));
        return j;
    }

}
