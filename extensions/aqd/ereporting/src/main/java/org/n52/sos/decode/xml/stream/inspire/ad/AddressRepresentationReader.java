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
package org.n52.sos.decode.xml.stream.inspire.ad;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.n52.sos.aqd.AqdConstants;
import org.n52.sos.decode.xml.stream.NillableStringReader;
import org.n52.sos.decode.xml.stream.XmlReader;
import org.n52.sos.decode.xml.stream.w3c.xlink.NillableReferenceReader;
import org.n52.sos.inspire.aqd.Address;
import org.n52.sos.ogc.ows.OwsExceptionReport;

public class AddressRepresentationReader extends XmlReader<Address> {
    private Address address;

    @Override
    protected void begin()
            throws XMLStreamException, OwsExceptionReport {
        this.address = new Address();
    }

    @Override
    protected void read(QName name)
            throws XMLStreamException, OwsExceptionReport {
        if (name.equals(AqdConstants.QN_AD_ADMIN_UNIT)) {
            address.addAdminUnit(delegate(new AdminUnitReader()));
        } else if (name.equals(AqdConstants.QN_AD_LOCATOR_DESIGNATOR)) {
            address.addLocatorDesignator(chars());
        } else if (name.equals(AqdConstants.QN_AD_LOCATOR_NAME)) {
            address.addLocatorName(delegate(new LocatorNameReader()));
        } else if (name.equals(AqdConstants.QN_AD_ADDRESS_AREA)) {
            address.addAddressArea(delegate(new AddressAreaReader()));
        } else if (name.equals(AqdConstants.QN_AD_POST_NAME)) {
            address.addPostName(delegate(new PostNameReader()));
        } else if (name.equals(AqdConstants.QN_AD_POST_CODE)) {
            address.setPostCode(delegate(new NillableStringReader()));
        } else if (name.equals(AqdConstants.QN_AD_THOROUGHFARE)) {
            address.addThoroughfare(delegate(new ThoroughfareReader()));
        } else if (name.equals(AqdConstants.QN_AD_ADDRESS_FEATURE)) {
            address.setAddressFeature(delegate(new NillableReferenceReader()));
        } else {
            ignore();
        }
    }

    @Override
    protected Address finish()
            throws OwsExceptionReport {
        return this.address;
    }

}
