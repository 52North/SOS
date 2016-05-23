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
package org.n52.sos.decode.xml.stream.inspire.base2;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.n52.sos.aqd.AqdConstants;
import org.n52.sos.decode.xml.stream.XmlReader;
import org.n52.sos.decode.xml.stream.inspire.gco.NillableFreeTextReader;
import org.n52.sos.decode.xml.stream.w3c.xlink.NillableReferenceReader;
import org.n52.sos.inspire.aqd.RelatedParty;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class RelatedPartyReader extends XmlReader<RelatedParty> {

    private RelatedParty relatedParty;

    @Override
    protected void begin() {
        this.relatedParty = new RelatedParty();
    }

    @Override
    protected void read(QName name)
            throws XMLStreamException, OwsExceptionReport {
        if (name.equals(AqdConstants.QN_BASE2_INDIVIDUAL_NAME)) {
            this.relatedParty
                    .setIndividualName(delegate(new NillableFreeTextReader()));
        } else if (name.equals(AqdConstants.QN_BASE2_ORGANISATION_NAME)) {
            this.relatedParty
                    .setOrganisationName(delegate(new NillableFreeTextReader()));
        } else if (name.equals(AqdConstants.QN_BASE2_POSITION_NAME)) {
            this.relatedParty
                    .setPositionName(delegate(new NillableFreeTextReader()));
        } else if (name.equals(AqdConstants.QN_BASE2_CONTACT)) {
            this.relatedParty.setContact(delegate(new NillableContactReader()));
        } else if (name.equals(AqdConstants.QN_BASE2_ROLE)) {
            this.relatedParty.addRole(delegate(new NillableReferenceReader()));
        } else {
            ignore();
        }
    }

    @Override
    protected RelatedParty finish()
            throws OwsExceptionReport {
        return this.relatedParty;
    }

}
