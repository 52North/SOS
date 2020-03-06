/**
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package org.n52.svalbard.encode;

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Set;
import net.opengis.drt.x10.DeleteResultTemplateResponseDocument;
import net.opengis.drt.x10.DeleteResultTemplateResponseType;
import org.apache.xmlbeans.XmlObject;
import org.n52.shetland.ogc.sos.drt.DeleteResultTemplateConstants;
import org.n52.sos.encode.AbstractResponseEncoder;
import org.n52.sos.exception.ows.concrete.MissingServiceParameterException;
import org.n52.sos.exception.ows.concrete.MissingVersionParameterException;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.response.DeleteResultTemplateResponse;
import org.n52.sos.w3c.SchemaLocation;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 * @since 4.4.0
 */
public class DeleteResultTemplateEncoder extends AbstractResponseEncoder<DeleteResultTemplateResponse> {
    public static final SchemaLocation SCHEMA_LOCATION = new SchemaLocation(DeleteResultTemplateConstants.NS,
            DeleteResultTemplateConstants.SCHEMA_LOCATION_URL);

    public DeleteResultTemplateEncoder() {
        super(SosConstants.SOS, Sos2Constants.SERVICEVERSION, DeleteResultTemplateConstants.OPERATION_NAME,
                DeleteResultTemplateConstants.NS, DeleteResultTemplateConstants.NS_PREFIX,
                DeleteResultTemplateResponse.class);
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.unmodifiableSet(Sets.newHashSet(
                DeleteResultTemplateConstants.CONFORMANCE_CLASS_INSERTION,
                DeleteResultTemplateConstants.CONFORMANCE_CLASS_RETRIEVAL));
    }

    @Override
    protected XmlObject create(DeleteResultTemplateResponse drtr) throws OwsExceptionReport {
        if (drtr == null) {
            throw new UnsupportedEncoderInputException(this, drtr);
        }
        final CompositeOwsException exceptions = new CompositeOwsException();
        if (drtr.getService() == null) {
            exceptions.add(new MissingServiceParameterException());
        }
        if (drtr.getVersion() == null) {
            exceptions.add(new MissingVersionParameterException());
        }
        exceptions.throwIfNotEmpty();
        
        DeleteResultTemplateResponseDocument drtrd =
                DeleteResultTemplateResponseDocument.Factory.newInstance(getXmlOptions());
        DeleteResultTemplateResponseType drtrt = drtrd.addNewDeleteResultTemplateResponse();
        if (drtr.isSetResultTemplates()) {
            for (String resultTemplate : drtr.getResultTemplates()) {
                drtrt.addDeletedTemplate(resultTemplate);
            }
        }
        return drtrd;
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Sets.newHashSet(SCHEMA_LOCATION);
    }

    @Override
    protected Set<SchemaLocation> getConcreteSchemaLocations() {
        return Sets.newHashSet();
    }
    
}
