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
package org.n52.sos.request.operator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.n52.sos.cache.ContentCache;
import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.config.annotation.Setting;
import org.n52.sos.ds.AbstractInsertSensorDAO;
import org.n52.sos.event.SosEventBus;
import org.n52.sos.event.events.SensorInsertion;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.MissingParameterValueException;
import org.n52.sos.exception.ows.concrete.InvalidFeatureOfInterestTypeException;
import org.n52.sos.exception.ows.concrete.InvalidOfferingParameterException;
import org.n52.sos.exception.ows.concrete.MissingFeatureOfInterestTypeException;
import org.n52.sos.exception.ows.concrete.MissingObservedPropertyParameterException;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.ConformanceClasses;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosOffering;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.request.InsertSensorRequest;
import org.n52.sos.response.InsertSensorResponse;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.MiscSettings;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.Constants;
import org.n52.sos.util.JavaHelper;
import org.n52.sos.util.SosHelper;
import org.n52.sos.wsdl.WSDLConstants;
import org.n52.sos.wsdl.WSDLOperation;

import com.google.common.collect.Sets;

/**
 * @since 4.0.0
 *
 */
@Configurable
public class SosInsertSensorOperatorV20 extends
        AbstractV2TransactionalRequestOperator<AbstractInsertSensorDAO, InsertSensorRequest, InsertSensorResponse> {


    private static final Set<String> CONFORMANCE_CLASSES = Sets.newHashSet(
            ConformanceClasses.SOS_V2_INSERTION_CAPABILITIES,
            ConformanceClasses.SOS_V2_SENSOR_INSERTION);

    private String defaultOfferingPrefix;

    private String defaultProcedurePrefix;

    public SosInsertSensorOperatorV20() {
        super( Sos2Constants.Operations.InsertSensor.name(), InsertSensorRequest.class);
    }

    public String getDefaultOfferingPrefix() {
        return this.defaultOfferingPrefix;
    }

    @Setting(MiscSettings.DEFAULT_OFFERING_PREFIX)
    public void setDefaultOfferingPrefix(String prefix) {
        this.defaultOfferingPrefix = prefix;
    }

    public String getDefaultProcedurePrefix() {
        return this.defaultProcedurePrefix;
    }

    @Setting(MiscSettings.DEFAULT_PROCEDURE_PREFIX)
    public void setDefaultProcedurePrefix(String prefix) {
        this.defaultProcedurePrefix = prefix;
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.unmodifiableSet(CONFORMANCE_CLASSES);
    }

    @Override
    public WSDLOperation getSosOperationDefinition() {
        return WSDLConstants.Operations.INSERT_SENSOR;
    }

    @Override
    public InsertSensorResponse receive(InsertSensorRequest request) throws OwsExceptionReport {
        InsertSensorResponse response = getDao().insertSensor(request);
        SosEventBus.fire(new SensorInsertion(request, response));
        return response;
    }

    @Override
    protected void checkParameters(InsertSensorRequest request) throws OwsExceptionReport {
        CompositeOwsException exceptions = new CompositeOwsException();
        // check parameters with variable content
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
            checkObservableProperty(request.getObservableProperty());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            SosHelper.checkProcedureDescriptionFormat(request.getProcedureDescriptionFormat(),
                    request.getService(), request.getVersion());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        checkAndSetAssignedProcedureID(request);
        checkAndSetAssignedOfferings(request);
        try {
            checkProcedureAndOfferingCombination(request);
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkParentChildProcedures(request.getProcedureDescription(), request.getAssignedProcedureIdentifier());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        if (request.getMetadata() != null) {
            try {
                checkObservationTypes(request.getMetadata().getObservationTypes());
            } catch (OwsExceptionReport owse) {
                exceptions.add(owse);
            }
            try {
                checkFeatureOfInterestTypes(request.getMetadata().getFeatureOfInterestTypes());
            } catch (OwsExceptionReport owse) {
                exceptions.add(owse);
            }
        } else {
            exceptions.add(new MissingParameterValueException(Sos2Constants.InsertSensorParams.observationType));
            exceptions.add(new MissingParameterValueException(Sos2Constants.InsertSensorParams.featureOfInterestType));
        }
        exceptions.throwIfNotEmpty();
    }

    private void checkObservableProperty(List<String> observableProperty) throws OwsExceptionReport {
        if (observableProperty == null || observableProperty.isEmpty()) {
            throw new MissingObservedPropertyParameterException();
//        } else {
            // TODO: check with existing and/or defined in outputs
        }
    }

    private void checkFeatureOfInterestTypes(Set<String> featureOfInterestTypes) throws OwsExceptionReport {
        if (featureOfInterestTypes != null) {
            CompositeOwsException exceptions = new CompositeOwsException();
            Collection<String> validFeatureOfInterestTypes =
                    Configurator.getInstance().getCache().getFeatureOfInterestTypes();
            for (String featureOfInterestType : featureOfInterestTypes) {
                if (featureOfInterestType.isEmpty()) {
                    exceptions.add(new MissingFeatureOfInterestTypeException());
                } else {
                    if (!validFeatureOfInterestTypes.contains(featureOfInterestType)) {
                        exceptions.add(new InvalidFeatureOfInterestTypeException(featureOfInterestType));
                    }
                }
            }
            exceptions.throwIfNotEmpty();
        }
    }

    private void checkObservationTypes(Set<String> observationTypes) throws OwsExceptionReport {
        if (observationTypes != null) {
            CompositeOwsException exceptions = new CompositeOwsException();
            for (String observationType : observationTypes) {
                try {
                    checkObservationType(observationType, Sos2Constants.InsertSensorParams.observationType.name());
                } catch (OwsExceptionReport e) {
                    exceptions.add(e);
                }
            }
            exceptions.throwIfNotEmpty();
        }
    }

    private void checkAndSetAssignedProcedureID(InsertSensorRequest request) {
        if (request.getProcedureDescription().isSetIdentifier()) {
            request.setAssignedProcedureIdentifier(request.getProcedureDescription().getIdentifier());
        } else {
            request.setAssignedProcedureIdentifier(getDefaultProcedurePrefix()
                    + JavaHelper.generateID(request.getProcedureDescription().toString()));
        }
    }

    private void checkAndSetAssignedOfferings(InsertSensorRequest request) throws InvalidOfferingParameterException {
        Set<SosOffering> sosOfferings = request.getProcedureDescription().getOfferings();        
        ContentCache cache = Configurator.getInstance().getCache();
        
        // add parent procedure offerings
        if (request.getProcedureDescription().isSetParentProcedures()) {            
            Set<String> allParentProcedures = cache.getParentProcedures(
                    request.getProcedureDescription().getParentProcedures(), true, true);
            for (String parentProcedure : allParentProcedures) {
                for (String offering : cache.getOfferingsForProcedure(parentProcedure)) {
                    // TODO I18N
                    SosOffering sosOffering = new SosOffering(offering, Constants.EMPTY_STRING);
                    sosOffering.setParentOfferingFlag(true);
                    sosOfferings.add(sosOffering);
                }
            }
        }

        // if no offerings are assigned, generate one
        if (CollectionHelper.isEmpty(sosOfferings)) {
            sosOfferings = new HashSet<SosOffering>(0);
            sosOfferings.add(new SosOffering(getDefaultOfferingPrefix() + request.getAssignedProcedureIdentifier()));
        }
        request.setAssignedOfferings(new ArrayList<SosOffering>(sosOfferings));
    }

    private void checkProcedureAndOfferingCombination(InsertSensorRequest request) throws OwsExceptionReport {
        for (SosOffering offering : request.getAssignedOfferings()) {
            if (!offering.isParentOffering() && getCache().getOfferings().contains(offering.getIdentifier())) {
                throw new InvalidParameterValueException()
                        .at(Sos2Constants.InsertSensorParams.offeringIdentifier)
                        .withMessage(
                                "The offering with the identifier '%s' still exists in this service and it is not allowed to insert more than one procedure to an offering!",
                                offering.getIdentifier());
            }
        }
    }
    
    private void checkParentChildProcedures(SosProcedureDescription procedureDescription, String assignedIdentifier) throws CodedException {
        if (procedureDescription.isSetChildProcedures()) {
            for (SosProcedureDescription child : procedureDescription.getChildProcedures()) {
                if (child.getIdentifier().equalsIgnoreCase(assignedIdentifier)) {
                    throw new InvalidParameterValueException()
                    .at("childProcdureIdentifier")
                    .withMessage(
                            "The procedure with the identifier '%s' is linked to itself as child procedure !",
                            procedureDescription.getIdentifier());
                }
            }
        }
        if (procedureDescription.isSetParentProcedures()) {
            if (procedureDescription.getParentProcedures().contains(assignedIdentifier)) {
                throw new InvalidParameterValueException()
                .at("parentProcdureIdentifier")
                .withMessage(
                        "The procedure with the identifier '%s' is linked to itself as parent procedure !",
                        procedureDescription.getIdentifier());
            }
        }
        
    }

    private void getChildProcedures() {
        // TODO implement
        // add parent offerings
        // insert if not exist and proc is encoded, else Exception
        // insert as hidden child
        // set relation in sensor_system
    }
}
