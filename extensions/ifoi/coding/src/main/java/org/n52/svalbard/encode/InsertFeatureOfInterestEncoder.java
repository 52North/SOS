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

import static java.util.Collections.unmodifiableSet;

import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.n52.shetland.ogc.sos.ifoi.InsertFeatureOfInterestConstants;
import org.n52.sos.encode.AbstractResponseEncoder;
import org.n52.sos.exception.ows.concrete.MissingServiceParameterException;
import org.n52.sos.exception.ows.concrete.MissingVersionParameterException;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.response.InsertFeatureOfInterestResponse;
import org.n52.sos.w3c.SchemaLocation;

import com.google.common.collect.Sets;

import net.opengis.ifoi.x10.InsertFeatureOfInterestResponseDocument;

/**
 * @since 1.0.0
 */
public class InsertFeatureOfInterestEncoder extends AbstractResponseEncoder<InsertFeatureOfInterestResponse> {
    public static final SchemaLocation SCHEMA_LOCATION = new SchemaLocation(InsertFeatureOfInterestConstants.NS_IFOI,
            InsertFeatureOfInterestConstants.SCHEMA_LOCATION_URL_INSERT_FEATURE_OF_INTEREST);

    public InsertFeatureOfInterestEncoder() {
        super(SosConstants.SOS, Sos2Constants.SERVICEVERSION, InsertFeatureOfInterestConstants.OPERATION_NAME,
                InsertFeatureOfInterestConstants.NS_IFOI, InsertFeatureOfInterestConstants.NS_IFOI_PREFIX,
                InsertFeatureOfInterestResponse.class);
    }

    @Override
    public Set<String> getConformanceClasses() {
        return unmodifiableSet(Sets.newHashSet(InsertFeatureOfInterestConstants.CONFORMANCE_CLASS));
    }

    @Override
    protected XmlObject create(InsertFeatureOfInterestResponse ifoir) throws OwsExceptionReport {
        if (ifoir == null) {
            throw new UnsupportedEncoderInputException(this, ifoir);
        }
        final CompositeOwsException exceptions = new CompositeOwsException();
        if (ifoir.getService() == null) {
            exceptions.add(new MissingServiceParameterException());
        }
        if (ifoir.getVersion() == null) {
            exceptions.add(new MissingVersionParameterException());
        }
        exceptions.throwIfNotEmpty();
        
        InsertFeatureOfInterestResponseDocument ifoird =
                InsertFeatureOfInterestResponseDocument.Factory.newInstance(getXmlOptions());
        ifoird.addNewInsertFeatureOfInterestResponse();
        return ifoird;
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
