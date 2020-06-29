/*
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
package org.n52.sos.request.operator;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.shetland.ogc.gml.CodeWithAuthority;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.OmObservationConstellation;
import org.n52.shetland.ogc.om.SingleObservationValue;
import org.n52.shetland.ogc.om.values.ComplexValue;
import org.n52.shetland.ogc.ows.exception.CompositeOwsException;
import org.n52.shetland.ogc.ows.exception.InvalidParameterValueException;
import org.n52.shetland.ogc.ows.exception.MissingParameterValueException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.InsertResultTemplateRequest;
import org.n52.shetland.ogc.sos.response.InsertResultTemplateResponse;
import org.n52.shetland.ogc.swe.SweDataRecord;
import org.n52.sos.ds.AbstractInsertResultTemplateHandler;
import org.n52.sos.event.events.ResultTemplateInsertion;
import org.n52.sos.exception.ows.concrete.DuplicateIdentifierException;
import org.n52.sos.service.SosSettings;
import org.n52.sos.wsdl.Metadata;
import org.n52.sos.wsdl.Metadatas;
import org.n52.svalbard.ConformanceClasses;

/**
 * @since 4.0.0
 *
 */
@Configurable
public class SosInsertResultTemplateOperatorV20 extends
        AbstractV2TransactionalRequestOperator<AbstractInsertResultTemplateHandler,
        InsertResultTemplateRequest,
        InsertResultTemplateResponse> {

    private static final String OPERATION_NAME = Sos2Constants.Operations.InsertResultTemplate.name();
    private static final String TEMPLATE = "template";
    private static final String PROCEDURE_NIL_REASON = ".procedure.nilReason";
    private static final String FEATURE_NIL_REASON = ".featureOfInterest.nilReason";

    private static final Set<String> CONFORMANCE_CLASSES =
            Collections.singleton(ConformanceClasses.SOS_V2_RESULT_INSERTION);

    private boolean allowTemplateWithoutProcedureAndFeature;

    public SosInsertResultTemplateOperatorV20() {
        super(OPERATION_NAME, InsertResultTemplateRequest.class);
    }

    @Setting(SosSettings.ALLOW_TEMPLATE_WITHOUT_PROCEDURE_FEATURE)
    public void setAllowTemplateWithoutProcedureAndFeature(boolean allowTemplateWithoutProcedureAndFeature) {
        this.allowTemplateWithoutProcedureAndFeature = allowTemplateWithoutProcedureAndFeature;
    }

    @Override
    public Set<String> getConformanceClasses(String service, String version) {
        if (SosConstants.SOS.equals(service) && Sos2Constants.SERVICEVERSION.equals(version)) {
            return Collections.unmodifiableSet(CONFORMANCE_CLASSES);
        }
        return Collections.emptySet();
    }

    @Override
    public InsertResultTemplateResponse receive(InsertResultTemplateRequest request) throws OwsExceptionReport {
        InsertResultTemplateResponse response = getOperationHandler().insertResultTemplate(request);
        getServiceEventBus().submit(new ResultTemplateInsertion(request, response));
        return response;
    }

    @Override
    protected void checkParameters(InsertResultTemplateRequest request) throws OwsExceptionReport {
        createCompositePhenomenons(request);
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
        // check offering
        try {
            checkOfferings(request.getObservationTemplate().getOfferings(),
                    Sos2Constants.InsertResultTemplateParams.proposedTemplate, true);
            try {
                checkObservationType(request);
            } catch (OwsExceptionReport owse) {
                exceptions.add(owse);
            }
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        // check procedure
        try {
            if (allowTemplateWithoutProcedureAndFeature
                    && request.getObservationTemplate().getNillableProcedure().isNil()) {
                if (!request.getObservationTemplate().getNillableProcedure().hasReason()) {
                    throw new MissingParameterValueException(
                            Sos2Constants.InsertResultTemplateParams.proposedTemplate.name() + PROCEDURE_NIL_REASON);
                } else if (!request.getObservationTemplate().getNillableProcedure().getNilReason().get()
                        .equals(TEMPLATE)) {
                    throw new InvalidParameterValueException(
                            Sos2Constants.InsertResultTemplateParams.proposedTemplate.name() + PROCEDURE_NIL_REASON,
                            request.getObservationTemplate().getNillableProcedure().getNilReason().get());
                }
            } else {
                if (request.getObservationTemplate().getProcedureIdentifier() == null
                        || request.getObservationTemplate().getProcedureIdentifier() == null
                        || request.getObservationTemplate().getProcedureIdentifier().isEmpty()) {
                    exceptions.add(new MissingParameterValueException(
                            Sos2Constants.InsertResultTemplateParams.proposedTemplate + ".procedure"));
                }
                checkTransactionalProcedure(request.getObservationTemplate().getProcedureIdentifier(),
                        Sos2Constants.InsertResultTemplateParams.proposedTemplate.name());
            }
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        // check observedProperty
        try {
            checkObservedProperty(request.getObservationTemplate().getObservablePropertyIdentifier(),
                    Sos2Constants.InsertResultTemplateParams.proposedTemplate + ".observableProperty", true);
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        // check for observed character of featureOfInterest
        try {
            if (allowTemplateWithoutProcedureAndFeature
                    && request.getObservationTemplate().getNillableFeatureOfInterest().isNil()) {
                if (!request.getObservationTemplate().getNillableFeatureOfInterest().hasReason()) {
                    throw new MissingParameterValueException(
                            Sos2Constants.InsertResultTemplateParams.proposedTemplate.name()
                                    + FEATURE_NIL_REASON);
                } else if (!request.getObservationTemplate().getNillableFeatureOfInterest().getNilReason().get()
                        .equals(TEMPLATE)) {
                    throw new InvalidParameterValueException(
                            Sos2Constants.InsertResultTemplateParams.proposedTemplate.name()
                                    + FEATURE_NIL_REASON,
                            request.getObservationTemplate().getNillableFeatureOfInterest().getNilReason().get());
                }
            } else {
                if (request.getObservationTemplate().getFeatureOfInterest() == null
                        || request.getObservationTemplate().getFeatureOfInterestIdentifier() == null
                        || request.getObservationTemplate().getFeatureOfInterestIdentifier().isEmpty()) {
                    exceptions.add(new MissingParameterValueException(
                            Sos2Constants.InsertResultTemplateParams.proposedTemplate + ".featureOfInterest"));
                }
                checkReservedCharacter(request.getObservationTemplate().getFeatureOfInterestIdentifier(),
                        Sos2Constants.InsertResultTemplateParams.featureOfInterest);
            }
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }

        // check identifier
        try {
            checkResultTemplateIdentifier(request.getIdentifier());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        exceptions.throwIfNotEmpty();
        // TODO check parameter as defined in SOS 2.0 spec

        /*
         * check observation template
         *
         * same resultSTructure for procedure, obsProp and Offering
         *
         * empty phenTime, resultTime and result
         *
         * phenTime and resultTime nilReason = 'template'
         *
         * proc, foi, obsProp not empty
         *
         * resultStructure: swe:Time or swe:TimeRange with value
         * "http://www.opengis.net/def/property/OGC/0/PhenomenonTime"
         *
         * If the resultStructure in the SosResultTemplate has a swe:Time
         * component with definition property set to the value
         * "http://www.opengis.net/def/property/OGC/0/ResultTime" then the value
         * of this component shall be used by the service to populate the
         * om:resultTime property of the observation template for each new
         * result block the client is going to insert via the InsertResult
         * operation. If no such component is contained in the resultStructure
         * then the service shall use the om:phenomenonTime as value of the
         * om:resultTime (at least the phenomenon time has to be provided in
         * each SosResultTemplate). In case the om:phenomenonTime is not a
         * TimeInstant, an InvalidParameterValue exception shall be returned,
         * with locator ‘resultTime’.
         *
         * A client shall encode the om:phenomenonTime as a swe:Time or
         * swe:TimeRange component with definition
         * "http://www.opengis.net/def/property/OGC/0/PhenomenonTime". in the
         * resultStructure that it proposes to the service in the
         * InsertResultTemplate operation request. If any of the observation
         * results that the client intends to send to the service via the
         * InsertResult operation is going to have a resultTime that is
         * different to the phenomenonTime then the resultStructure of the
         * SosResultTemplate shall also have a swe:Time component with
         * definition "http://www.opengis.net/def/property/OGC/0/ResultTime".
         *
         * If a result template with differing observationType or (SWE Common
         * encoded) result structure is inserted for the same constellation of
         * procedure, observedProperty and ObservationOffering (for which
         * observations already exist) an exception shall be returned with the
         * ExceptionCode "InvalidParameterValue" and locator value
         * "proposedTemplate".
         */
    }

    private void createCompositePhenomenons(InsertResultTemplateRequest request) throws OwsExceptionReport {
        if (request.getResultStructure().isDecoded()
                && request.getResultStructure().get().get() instanceof SweDataRecord) {
            SweDataRecord record = (SweDataRecord) request.getResultStructure().get().get();
            String observablePropertyIdentifier = request.getObservationTemplate().getObservablePropertyIdentifier();
            if (record.existsFieldForIdentifier(observablePropertyIdentifier)) {
                if (record.getFieldByIdentifier(observablePropertyIdentifier).getElement() instanceof SweDataRecord) {
                    ComplexValue cv = new ComplexValue(
                            (SweDataRecord) record.getFieldByIdentifier(observablePropertyIdentifier).getElement());
                    OmObservation observation = new OmObservation();
                    observation.setObservationConstellation(request.getObservationTemplate());
                    observation.setValue(new SingleObservationValue<>(cv));
                    createCompositePhenomenon(observation);
                }
            }
        }
    }

    private void checkResultTemplateIdentifier(CodeWithAuthority identifier) throws OwsExceptionReport {
        checkResultTemplateIdentifier(identifier.getValue());
    }

    private void checkResultTemplateIdentifier(String identifier) throws OwsExceptionReport {
        if (getCache().hasResultTemplate(identifier)) {
            throw new DuplicateIdentifierException("resultTemplate", identifier);
        }
        // check for reserved character
        checkReservedCharacter(identifier, "resultTemplateIdentifier");
    }

    private void checkObservationType(InsertResultTemplateRequest request) throws OwsExceptionReport {
        OmObservationConstellation observationConstellation = request.getObservationTemplate();
        if (observationConstellation.isSetObservationType()) {
            // TODO check why setting SweArray_Observation as type
            // observationConstellation.setObservationType(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION);
            // check if observation type is supported
            checkObservationType(observationConstellation.getObservationType(),
                    Sos2Constants.InsertResultTemplateParams.observationType.name());
        }

        Set<String> validObservationTypesForOffering = new HashSet<>(0);
        for (String offering : observationConstellation.getOfferings()) {
            validObservationTypesForOffering.addAll(getCache().getAllowedObservationTypesForOffering(offering));
        }
        // check if observation type is valid for offering
        if (!validObservationTypesForOffering.contains(observationConstellation.getObservationType())) {
            throw new InvalidParameterValueException().at(Sos2Constants.InsertResultTemplateParams.observationType)
                    .withMessage("The requested observation type is not valid for the offering!");
        }
    }

    @Override
    public Metadata getSosOperationDefinition() {
        return Metadatas.INSERT_RESULT_TEMPLATE;
    }
}
