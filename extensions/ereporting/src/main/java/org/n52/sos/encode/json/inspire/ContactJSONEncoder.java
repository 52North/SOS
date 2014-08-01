/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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

import org.n52.sos.inspire.aqd.Contact;
import org.n52.sos.encode.json.JSONEncoder;
import org.n52.sos.ogc.ows.OwsExceptionReport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class ContactJSONEncoder extends JSONEncoder<Contact> {

    protected static final String WEBSITE = "website";
    protected static final String TELEPHONE_VOICE = "telephoneVoice";
    protected static final String HOURS_OF_SERVICE = "hoursOfService";
    protected static final String TELEPHONE_FACSIMILE = "telephoneFacsimile";
    protected static final String CONTACT_INSTRUCTIONS = "contactInstructions";
    protected static final String ADDRESS = "address";
    protected static final String ELECTRONIC_MAIL_ADDRESS = "electronicMailAddress";

    public ContactJSONEncoder() {
        super(Contact.class);
    }

    @Override
    public JsonNode encodeJSON(Contact t)
            throws OwsExceptionReport {
        ObjectNode j = nodeFactory().objectNode();
        j.put(ADDRESS, encodeObjectToJson(t.getAddress()));
        j.put(CONTACT_INSTRUCTIONS, encodeObjectToJson(t.getContactInstructions()));
        j.put(ELECTRONIC_MAIL_ADDRESS, encodeObjectToJson(t.getElectronicMailAddress()));
        j.put(HOURS_OF_SERVICE, encodeObjectToJson(t.getHoursOfService()));
        j.put(TELEPHONE_FACSIMILE, encodeObjectToJson(t.getTelephoneFacsimile()));
        j.put(TELEPHONE_VOICE, encodeObjectToJson(t.getTelephoneVoice()));
        j.put(WEBSITE, encodeObjectToJson(t.getWebsite()));
        return j;
    }

}
