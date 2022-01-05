/*
 * Copyright (C) 2012-2022 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.request.operator;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.ows.exception.CompositeOwsException;
import org.n52.shetland.ogc.ows.exception.InvalidParameterValueException;
import org.n52.shetland.ogc.ows.exception.MissingParameterValueException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.ifoi.InsertFeatureOfInterestConstants;
import org.n52.shetland.ogc.sos.ifoi.InsertFeatureOfInterestRequest;
import org.n52.shetland.ogc.sos.ifoi.InsertFeatureOfInterestResponse;
import org.n52.shetland.util.IdGenerator;
import org.n52.shetland.w3c.wsdl.Fault;
import org.n52.sos.ds.AbstractInsertFeatureOfInterestHandler;
import org.n52.sos.event.events.FeatureInsertion;
import org.n52.sos.wsdl.Metadata;

/**
 * {@code IRequestOperator} to handle {@link InsertFeatureOfInterestRequest}s.
 *
 * @author Christian Autermann
 *
 * @since 4.0.0
 */
public class InsertFeatureOfInterestOperator extends
        AbstractTransactionalRequestOperator<AbstractInsertFeatureOfInterestHandler,
        InsertFeatureOfInterestRequest,
        InsertFeatureOfInterestResponse>
        implements WSDLAwareRequestOperator {

    private static final String OPERATION_PATH = InsertFeatureOfInterestConstants.NS_IFOI + "/";

    private static final String RESPONSE = InsertFeatureOfInterestConstants.OPERATION_NAME + "Response";

    private static final QName QN_INSERT_FEATURE_OF_INTEREST_REQUEST =
            new QName(InsertFeatureOfInterestConstants.NS_IFOI, InsertFeatureOfInterestConstants.OPERATION_NAME,
                    InsertFeatureOfInterestConstants.NS_IFOI_PREFIX);

    private static final URI INSERT_FEATURE_OF_INTEREST_REQUEST =
            URI.create(OPERATION_PATH + InsertFeatureOfInterestConstants.OPERATION_NAME);

    private static final QName QN_INSERT_FEATURE_OF_INTEREST_RESPONSE = new QName(
            InsertFeatureOfInterestConstants.NS_IFOI, RESPONSE, InsertFeatureOfInterestConstants.NS_IFOI_PREFIX);

    private static final URI INSERT_FEATURE_OF_INTEREST_RESPONSE = URI.create(OPERATION_PATH + RESPONSE);

    /**
     * Constructs a new {@code InsertFeatureOfInterestOperator}.
     */
    public InsertFeatureOfInterestOperator() {
        super(SosConstants.SOS, Sos2Constants.SERVICEVERSION, InsertFeatureOfInterestConstants.OPERATION_NAME,
                InsertFeatureOfInterestRequest.class);
    }

    @Override
    public InsertFeatureOfInterestResponse receive(InsertFeatureOfInterestRequest request) throws OwsExceptionReport {
        InsertFeatureOfInterestResponse response = getOperationHandler().insertFeatureOfInterest(request);
        getServiceEventBus().submit(new FeatureInsertion(request, response));
        return response;
    }

    @Override
    protected void checkParameters(InsertFeatureOfInterestRequest request) throws OwsExceptionReport {
        CompositeOwsException exceptions = new CompositeOwsException();

        try {
            checkServiceParameter(request.getService());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkSingleVersionParameter(request);
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }

        try {
            if (!request.hasFeatureMembers()) {
                throw new MissingParameterValueException("featureMember");
            }
            checkFeatureMembers(request.getFeatureMembers());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        exceptions.throwIfNotEmpty();
    }

    private void checkFeatureMembers(List<AbstractFeature> featureMembers) throws OwsExceptionReport {
        for (AbstractFeature abstractFeature : featureMembers) {
            if (!abstractFeature.isSetIdentifier()) {
                abstractFeature.setIdentifier(IdGenerator.generate(abstractFeature.toString()));
            }
            if (getCache().hasFeatureOfInterest(abstractFeature.getIdentifier())) {
                throw new InvalidParameterValueException().at("featureMember.identifier").withMessage(
                        "The featureOfInterest with identifier '%s' still exists!", abstractFeature.getIdentifier());
            }
        }
    }

    @Override
    public Metadata getSosOperationDefinition() {
        return Metadata.newMetadata()
                .setName(InsertFeatureOfInterestConstants.OPERATION_NAME)
                .setVersion(Sos2Constants.SERVICEVERSION).setRequest(QN_INSERT_FEATURE_OF_INTEREST_REQUEST)
                .setRequestAction(INSERT_FEATURE_OF_INTEREST_REQUEST)
                .setResponse(QN_INSERT_FEATURE_OF_INTEREST_RESPONSE)
                .setResponseAction(INSERT_FEATURE_OF_INTEREST_RESPONSE)
                .setFaults(Fault.DEFAULT_FAULTS).build();
    }

    @Override
    public Map<String, String> getAdditionalSchemaImports() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, String> getAdditionalPrefixes() {
        return Collections.emptyMap();
    }
}
