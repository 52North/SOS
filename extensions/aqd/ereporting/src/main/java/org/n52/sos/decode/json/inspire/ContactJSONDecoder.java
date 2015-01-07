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
package org.n52.sos.decode.json.inspire;

import org.n52.sos.inspire.aqd.Address;
import org.n52.sos.inspire.aqd.Contact;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.AQDJSONConstants;

import com.fasterxml.jackson.databind.JsonNode;

public class ContactJSONDecoder extends AbstractJSONDecoder<Contact> {

    public ContactJSONDecoder() {
        super(Contact.class);
    }

    @Override
    public Contact decodeJSON(JsonNode node, boolean validate)
            throws OwsExceptionReport {
        Contact contact = new Contact();
        contact.setAddress(decodeJsonToNillable(node
                                            .path(AQDJSONConstants.ADDRESS), Address.class));
        contact.setContactInstructions(parseNillableString(node
                .path(AQDJSONConstants.CONTACT_INSTRUCTIONS)));
        contact.setElectronicMailAddress(parseNillableString(node
                .path(AQDJSONConstants.ELECTRONIC_MAIL_ADDRESS)));
        contact.setHoursOfService(parseNillableString(node
                .path(AQDJSONConstants.HOURS_OF_SERVICE)));
        contact.setWebsite(parseNillableString(node
                .path(AQDJSONConstants.WEBSITE)));
        for (JsonNode n : node.path(AQDJSONConstants.TELEPHONE_FACSIMILE)) {
            contact.addTelephoneFacsimile(parseNillableString(n));
        }
        for (JsonNode n : node.path(AQDJSONConstants.TELEPHONE_VOICE)) {
            contact.addTelephoneVoice(parseNillableString(n));
        }
        return contact;
    }

}
