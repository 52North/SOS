/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.n52.iceland.request.handler.OperationHandler;
import org.n52.shetland.ogc.om.AbstractPhenomenon;
import org.n52.shetland.ogc.om.OmCompositePhenomenon;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.OmObservableProperty;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.OmObservationConstellation;
import org.n52.shetland.ogc.om.values.ComplexValue;
import org.n52.shetland.ogc.ows.exception.InvalidParameterValueException;
import org.n52.shetland.ogc.ows.service.OwsServiceRequest;
import org.n52.shetland.ogc.ows.service.OwsServiceResponse;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.swe.SweAbstractDataComponent;
import org.n52.shetland.ogc.swe.SweAbstractDataRecord;
import org.n52.shetland.ogc.swe.SweField;

/**
 * @since 4.0.0
 *
 * @param <D>
 *            The OperationDAO implementation class
 * @param <Q>
 *            the request type
 * @param <A>
 *            the response type
 */
public abstract class AbstractV2TransactionalRequestOperator<D extends OperationHandler,
                                                                Q extends OwsServiceRequest,
                                                                A extends OwsServiceResponse>
        extends AbstractTransactionalRequestOperator<D, Q, A> implements WSDLAwareRequestOperator {

    public AbstractV2TransactionalRequestOperator(String operationName, Class<Q> requestType) {
        super(SosConstants.SOS, Sos2Constants.SERVICEVERSION, operationName, requestType);
    }

    @Override
    public Map<String, String> getAdditionalSchemaImports() {
        return null;
    }

    @Override
    public Map<String, String> getAdditionalPrefixes() {
        return null;
    }

    protected void checkForCompositeObservableProperty(AbstractPhenomenon observableProperty, Set<String> offerings,
            Enum<?> parameterName) throws InvalidParameterValueException {
        String observablePropertyIdentifier = observableProperty.getIdentifier();
        if (hasObservations(observablePropertyIdentifier, offerings)
                && observableProperty.isComposite() != getCache().isCompositePhenomenon(observablePropertyIdentifier)
                && checkComponentsIfInserted(((OmCompositePhenomenon) observableProperty).getPhenomenonComponents())) {
            throw new InvalidParameterValueException(parameterName, observablePropertyIdentifier);
        }
    }

    private boolean checkComponentsIfInserted(List<OmObservableProperty> phenomenonComponents) {
        for (OmObservableProperty omObservableProperty : phenomenonComponents) {
            if (getCache().hasObservableProperty(omObservableProperty.getIdentifier()) && !getCache()
                    .getProceduresForObservableProperty(omObservableProperty.getIdentifier()).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private boolean hasObservations(String observableProperty, Set<String> offerings) {
        // if (offerings != null) {
        // for (String offering :
        // getCache().getOfferingsForObservableProperty(observableProperty)) {
        // if (offerings.contains(offering) &&
        // getCache().hasMaxPhenomenonTimeForOffering(offering)) {
        // return true;
        // }
        // }
        // }
        return false;
    }

    protected void createCompositePhenomenon(OmObservation observation) {
        if (isComplexObservation(observation)) {
            OmObservationConstellation oc = observation.getObservationConstellation();
            AbstractPhenomenon observableProperty = oc.getObservableProperty();

            if (!(observableProperty instanceof OmCompositePhenomenon)) {
                final OmCompositePhenomenon parent;
                parent = new OmCompositePhenomenon(observableProperty.getIdentifier());
                parent.setDefaultElementEncoding(observableProperty.getDefaultElementEncoding());
                parent.setHumanReadableIdentifier(observableProperty.getHumanReadableIdentifierCodeWithAuthority());
                parent.setIdentifier(observableProperty.getIdentifierCodeWithAuthority());
                parent.setDescription(observableProperty.getDescription());
                parent.setName(observableProperty.getName());

                ComplexValue value = (ComplexValue) observation.getValue().getValue();
                SweAbstractDataRecord dataRecord = value.getValue();
                for (SweField field : dataRecord.getFields()) {
                    SweAbstractDataComponent element = field.getElement();
                    OmObservableProperty child = new OmObservableProperty(element.getDefinition());
                    child.setName(element.getNames());
                    child.setDescription(element.getDescription());
                    parent.addPhenomenonComponent(child);
                }

                oc.setObservableProperty(parent);
            }
        }
    }

    protected static boolean isComplexObservation(OmObservation observation) {
        return OmConstants.OBS_TYPE_COMPLEX_OBSERVATION
                .equalsIgnoreCase(observation.getObservationConstellation().getObservationType())
                && observation.getValue().getValue() instanceof ComplexValue;
    }
}
