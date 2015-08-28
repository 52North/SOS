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
package org.n52.sos.encode.sos.v2;

import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.n52.iceland.exception.ows.OwsExceptionReport;
import org.n52.iceland.ogc.ows.AcceptVersions;
import org.n52.iceland.ogc.ows.Sections;
import org.n52.iceland.ogc.sos.Sos2Constants;
import org.n52.iceland.ogc.sos.SosConstants;
import org.n52.iceland.request.GetCapabilitiesRequest;
import org.n52.iceland.w3c.SchemaLocation;

import com.google.common.collect.Sets;

import net.opengis.sos.x20.GetCapabilitiesDocument;
import net.opengis.sos.x20.GetCapabilitiesType;

/**
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 5.0.0
 *
 */
public class GetCapabilitiesRequestEncoder extends AbstractSosRequestEncoder<GetCapabilitiesRequest> {

    
    public GetCapabilitiesRequestEncoder() {
        super(SosConstants.Operations.GetCapabilities.name(), GetCapabilitiesRequest.class);
    }

    @Override
    protected Set<SchemaLocation> getConcreteSchemaLocations() {
        return Sets.newHashSet(Sos2Constants.SOS_GET_CAPABILITIES_SCHEMA_LOCATION);
    }

    @Override
    protected XmlObject create(GetCapabilitiesRequest request) throws OwsExceptionReport {
       GetCapabilitiesDocument doc = GetCapabilitiesDocument.Factory.newInstance(getXmlOptions());
       GetCapabilitiesType gct = doc.addNewGetCapabilities2();
       addService(gct, request);
       addAcceptVersion(gct, request);
       addSections(gct, request);
       return doc;
    }

    private void addService(GetCapabilitiesType gct, GetCapabilitiesRequest request) {
        if (request.isSetService()) {
            gct.setService(request.getService());
        } else {
            gct.setService(SosConstants.SOS); 
        }
    }

    private void addAcceptVersion(GetCapabilitiesType gct, GetCapabilitiesRequest request) throws OwsExceptionReport {
       if (request.isSetAcceptVersions()) {
           gct.addNewAcceptVersions().set(encodeOws(new AcceptVersions().setAcceptVersions(request.getAcceptVersions())));
       } else if (request.isSetVersion()) {
           gct.addNewAcceptVersions().addVersion(request.getVersion());
       } else {
           gct.addNewAcceptVersions().addVersion(Sos2Constants.SERVICEVERSION);
       }
    }

    private void addSections(GetCapabilitiesType gct, GetCapabilitiesRequest request) throws OwsExceptionReport {
       if (request.isSetSections()) {
           gct.addNewSections().set(encodeOws(new Sections().setSections(request.getSections())));
       }
    }

}
