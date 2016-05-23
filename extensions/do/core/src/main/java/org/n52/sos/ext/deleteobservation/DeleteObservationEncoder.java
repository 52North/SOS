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
package org.n52.sos.ext.deleteobservation;

import java.util.Set;

import net.opengis.sosdo.x10.DeleteObservationResponseDocument;
import net.opengis.sosdo.x10.DeleteObservationResponseType;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.encode.AbstractResponseEncoder;
import org.n52.sos.exception.ows.MissingParameterValueException;
import org.n52.sos.exception.ows.concrete.MissingServiceParameterException;
import org.n52.sos.exception.ows.concrete.MissingVersionParameterException;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.w3c.SchemaLocation;

import com.google.common.collect.Sets;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * @since 1.0.0
 */
public class DeleteObservationEncoder extends AbstractResponseEncoder<DeleteObservationResponse> {
    public static final SchemaLocation SCHEMA_LOCATION = new SchemaLocation(DeleteObservationConstants.NS_SOSDO_1_0,
            DeleteObservationConstants.NS_SOSDO_1_0_SCHEMA_LOCATION);

    public DeleteObservationEncoder() {
        super(SosConstants.SOS, Sos2Constants.SERVICEVERSION, DeleteObservationConstants.Operations.DeleteObservation
                .name(), DeleteObservationConstants.NS_SOSDO_1_0, DeleteObservationConstants.NS_SOSDO_1_0_PREFIX,
                DeleteObservationResponse.class);
    }

    @Override
    public Set<String> getConformanceClasses() {
        return DeleteObservationConstants.CONFORMANCE_CLASSES;
    }

    @Override
    protected XmlObject create(DeleteObservationResponse dor) throws OwsExceptionReport {
        if (dor == null) {
            throw new UnsupportedEncoderInputException(this, dor);
        }
        final CompositeOwsException exceptions = new CompositeOwsException();
        if (dor.getService() == null) {
            exceptions.add(new MissingServiceParameterException());
        }
        if (dor.getVersion() == null) {
            exceptions.add(new MissingVersionParameterException());
        }
        if (dor.getObservationId() == null || dor.getObservationId().isEmpty()) {
            exceptions.add(new MissingParameterValueException(DeleteObservationConstants.PARAMETER_NAME));
        }
        exceptions.throwIfNotEmpty();

        String observationId = dor.getObservationId();
        DeleteObservationResponseDocument xbDeleteObsDoc =
                DeleteObservationResponseDocument.Factory.newInstance(getXmlOptions());
        DeleteObservationResponseType xbDeleteObservationResponse = xbDeleteObsDoc.addNewDeleteObservationResponse();
        xbDeleteObservationResponse.setDeletedObservation(observationId);
        return xbDeleteObsDoc;
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
