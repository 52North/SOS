/*
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.encode.sos.v1;

import java.util.Collection;
import java.util.Set;

import net.opengis.ogc.ComparisonOperatorType;
import net.opengis.ogc.GeometryOperandsType;
import net.opengis.ogc.IdCapabilitiesType;
import net.opengis.ogc.ScalarCapabilitiesType;
import net.opengis.ogc.SpatialCapabilitiesType;
import net.opengis.ogc.SpatialOperatorNameType;
import net.opengis.ogc.SpatialOperatorType;
import net.opengis.ogc.SpatialOperatorsType;
import net.opengis.ogc.TemporalCapabilitiesType;
import net.opengis.ogc.TemporalOperandsType;
import net.opengis.ogc.TemporalOperatorNameType;
import net.opengis.ogc.TemporalOperatorType;
import net.opengis.ogc.TemporalOperatorsType;
import net.opengis.sos.x10.CapabilitiesDocument;
import net.opengis.sos.x10.CapabilitiesDocument.Capabilities;
import net.opengis.sos.x10.ContentsDocument.Contents;
import net.opengis.sos.x10.ContentsDocument.Contents.ObservationOfferingList;
import net.opengis.sos.x10.FilterCapabilitiesDocument.FilterCapabilities;
import net.opengis.sos.x10.ObservationOfferingType;

import org.apache.xmlbeans.XmlObject;

import org.n52.oxf.xml.NcNameResolver;
import org.n52.shetland.ogc.filter.FilterConstants.ComparisonOperator;
import org.n52.shetland.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.shetland.ogc.filter.FilterConstants.TimeOperator;
import org.n52.shetland.ogc.gml.CodeType;
import org.n52.shetland.ogc.gml.GmlConstants;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.ows.OWSConstants;
import org.n52.shetland.ogc.ows.service.GetCapabilitiesResponse;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.swe.SweConstants;
import org.n52.shetland.w3c.SchemaLocation;
import org.n52.shetland.ogc.sos.SosCapabilities;
import org.n52.shetland.ogc.sos.SosObservationOffering;
import org.n52.sos.util.N52XmlHelper;
import org.n52.svalbard.encode.exception.EncodingException;

import com.google.common.collect.Sets;

/**
 * @since 4.0.0
 *
 */
public class GetCapabilitiesResponseEncoder extends AbstractSosResponseEncoder<GetCapabilitiesResponse> {

    public GetCapabilitiesResponseEncoder() {
        super(SosConstants.Operations.GetCapabilities.name(), GetCapabilitiesResponse.class);
    }

    @Override
    protected Set<SchemaLocation> getConcreteSchemaLocations() {
        return Sets.newHashSet(Sos1Constants.GET_CAPABILITIES_SOS1_SCHEMA_LOCATION);
    }

    @Override
    protected XmlObject create(GetCapabilitiesResponse response) throws EncodingException {
        CapabilitiesDocument xbCapsDoc = CapabilitiesDocument.Factory.newInstance(getXmlOptions());
        // cursor for getting prefixes
        Capabilities xbCaps = xbCapsDoc.addNewCapabilities();

        // set version.
        xbCaps.setVersion(response.getVersion());

        SosCapabilities sosCapabilities = (SosCapabilities)response.getCapabilities();

        if (sosCapabilities.getServiceIdentification().isPresent()) {
            xbCaps.addNewServiceIdentification().set(encodeObjectToXml(OWSConstants.NS_OWS, sosCapabilities.getServiceIdentification().get()));
        }
        if (sosCapabilities.getServiceProvider().isPresent()) {
            xbCaps.addNewServiceProvider().set(encodeObjectToXml(OWSConstants.NS_OWS, sosCapabilities.getServiceProvider().get()));

        }
        if (sosCapabilities.getOperationsMetadata().isPresent()) {
            xbCaps.addNewOperationsMetadata().set(encodeObjectToXml(OWSConstants.NS_OWS, sosCapabilities.getOperationsMetadata().get()));
        }
        if (sosCapabilities.getFilterCapabilities().isPresent()) {
            setFilterCapabilities(xbCaps.addNewFilterCapabilities(), sosCapabilities.getFilterCapabilities().get());
        }
        if (sosCapabilities.getContents().isPresent()) {
            setContents(xbCaps.addNewContents(), sosCapabilities.getContents().get(), response.getVersion());
        }

        N52XmlHelper.setSchemaLocationsToDocument(xbCapsDoc, Sets.newHashSet(N52XmlHelper.getSchemaLocationForSOS100()));

        return xbCapsDoc;
    }

    /**
     * Sets the FilterCapabilities section to the capabilities document.
     *
     * @param filterCapabilities
     *
     * @param sosFilterCaps
     *            FilterCapabilities.
     */
    protected void setFilterCapabilities(FilterCapabilities filterCapabilities,
                                         org.n52.shetland.ogc.filter.FilterCapabilities sosFilterCaps) {
        setScalarFilterCapabilities(filterCapabilities.addNewScalarCapabilities(), sosFilterCaps);
        setSpatialFilterCapabilities(filterCapabilities.addNewSpatialCapabilities(), sosFilterCaps);
        setTemporalFilterCapabilities(filterCapabilities.addNewTemporalCapabilities(), sosFilterCaps);
        setIdFilterCapabilities(filterCapabilities.addNewIdCapabilities());

    }

    /**
     * Sets the content section to the Capabilities document.
     *
     * @param xbContents
     *            SOS 2.0 contents section
     * @param offerings
     *            SOS offerings for contents
     * @param version
     *            SOS response version
     *
     *
     * @throws EncodingException
     *             * if an error occurs.
     */
    protected void setContents(Contents xbContents, Collection<SosObservationOffering> offerings, String version)
            throws EncodingException{
        // Contents xbContType = xbContents.addNewContents();
        ObservationOfferingList xbObservationOfferings = xbContents.addNewObservationOfferingList();

        for (SosObservationOffering offering : offerings) {

            ObservationOfferingType xbObservationOffering = xbObservationOfferings.addNewObservationOffering();
            // TODO check NAme or ID
            xbObservationOffering.setId(NcNameResolver.fixNcName(offering.getOffering().getIdentifier()));

            // only if fois are contained for the offering set the values of the
            // envelope
            if (offering.isSetObservedArea()) {
                xbObservationOffering.addNewBoundedBy().addNewEnvelope().set(encodeObjectToXml(GmlConstants.NS_GML, offering.getObservedArea()));
            }

            // TODO: add intended application
            // xbObservationOffering.addIntendedApplication("");

            // set gml:name to offering's id (not ncname resolved)
            for (CodeType name : offering.getOffering().getName()) {
                xbObservationOffering.addNewName().set(encodeObjectToXml(GmlConstants.NS_GML, name));
            }

            /*
             * // set up phenomena Collection<String> phenomenons =
             * offering.getObservableProperties(); Collection<String>
             * compositePhenomena = offering.getCompositePhenomena();
             * Collection<String> componentsOfCompPhens = new
             * ArrayList<String>();
             *
             * // set up composite phenomena if (compositePhenomena != null) {
             * first add a new compositePhenomenon for every compositePhenomenon
             * for (String compositePhenomenon : compositePhenomena) {
             * Collection<String> components =
             * offering.getPhens4CompPhens().get(compositePhenomenon);
             * componentsOfCompPhens.addAll(components); if (components != null)
             * { PhenomenonPropertyType xb_opType =
             * xb_oo.addNewObservedProperty();
             * xb_opType.set(SosConfigurator.getInstance().getOmEncoder()
             * .createCompositePhenomenon(compositePhenomenon, components)); } }
             * }
             */


            // set up time
            if (offering.getPhenomenonTime() instanceof TimePeriod) {
                xbObservationOffering.addNewTime().set(encodeObjectToXml(SweConstants.NS_SWE_101, offering.getPhenomenonTime()));
            }

            offering.getObservableProperties().forEach(phenomenon -> xbObservationOffering.addNewObservedProperty().setHref(phenomenon));
            offering.getFeatureOfInterestTypes().forEach(featureOfInterestType -> xbObservationOffering.addNewFeatureOfInterest().setHref(featureOfInterestType));
            offering.getProcedureDescriptionFormats().forEach(procedureDescriptionFormat -> xbObservationOffering.addNewProcedure().setHref(procedureDescriptionFormat));
            offering.getProcedures().forEach(procedure -> xbObservationOffering.addNewProcedure().setHref(procedure));
            offering.getFeatureOfInterest().forEach(featureOfInterest -> xbObservationOffering.addNewFeatureOfInterest().setHref(featureOfInterest));
            offering.getResultModels().forEach(xbObservationOffering::addResultModel);
            offering.getResponseFormats().forEach(responseFormat -> xbObservationOffering.addNewResponseFormat().setStringValue(responseFormat));
            offering.getResponseModes().forEach(responseMode -> xbObservationOffering.addNewResponseMode().setStringValue(responseMode));
        }
    }

    /**
     * Set the IdFilterCapabilities.
     *
     * !!! Modify method addicted to your implementation !!!
     *
     * @param idCapabilities
     *            IdCapabilities.
     */
    protected void setIdFilterCapabilities(IdCapabilitiesType idCapabilities) {

        idCapabilities.addNewFID();
        idCapabilities.addNewEID();
    }

    /**
     * Sets the SpatialFilterCapabilities.
     *
     * !!! Modify method addicted to your implementation !!!
     *
     * @param spatialCapabilities
     *            SpatialCapabilities.
     * @param sosFilterCaps
     */
    protected void setSpatialFilterCapabilities(SpatialCapabilitiesType spatialCapabilities,
                                                org.n52.shetland.ogc.filter.FilterCapabilities sosFilterCaps) {

        // set GeometryOperands
        if (!sosFilterCaps.getSpatialOperands().isEmpty()) {
            sosFilterCaps.getSpatialOperands().forEach(spatialCapabilities.addNewGeometryOperands()::addGeometryOperand);
        }

        // set SpatialOperators
        if (!sosFilterCaps.getSpatialOperators().isEmpty()) {
            SpatialOperatorsType spatialOps = spatialCapabilities.addNewSpatialOperators();
            Set<SpatialOperator> keys = sosFilterCaps.getSpatialOperators().keySet();
            keys.forEach(spatialOperator -> {
                SpatialOperatorType operator = spatialOps.addNewSpatialOperator();
                operator.setName(getEnum4SpatialOperator(spatialOperator));
                GeometryOperandsType bboxGeomOps = operator.addNewGeometryOperands();
                sosFilterCaps.getSpatialOperators().get(spatialOperator)
                        .forEach(bboxGeomOps::addGeometryOperand);
            });
        }
    }

    /**
     * Sets the TemporalFilterCapabilities.
     *
     * !!! Modify method addicted to your implementation !!!
     *
     * @param temporalCapabilities
     *            TemporalCapabilities.
     * @param sosFilterCaps
     */
    protected void setTemporalFilterCapabilities(TemporalCapabilitiesType temporalCapabilities,
                                                 org.n52.shetland.ogc.filter.FilterCapabilities sosFilterCaps) {

        // set TemporalOperands
        if (!sosFilterCaps.getTemporalOperands().isEmpty()) {
            TemporalOperandsType tempOperands = temporalCapabilities.addNewTemporalOperands();
            sosFilterCaps.getTemporalOperands().forEach(tempOperands::addTemporalOperand);
        }

        // set TemporalOperators
        if (!sosFilterCaps.getTemporalOperators().isEmpty()) {
            TemporalOperatorsType temporalOps = temporalCapabilities.addNewTemporalOperators();

            sosFilterCaps.getTemporalOperators().forEach((operator, operands) -> {
                TemporalOperatorType xbOperator = temporalOps.addNewTemporalOperator();
                xbOperator.setName(getEnum4TemporalOperator(operator));
                TemporalOperandsType bboxGeomOps = xbOperator.addNewTemporalOperands();
                operands.forEach(bboxGeomOps::addTemporalOperand);
            });
        }
    }

    /**
     * Sets the ScalarFilterCapabilities.
     *
     * !!! Modify method addicted to your implementation !!!
     *
     * @param scalarCapabilities
     *            ScalarCapabilities.
     * @param sosFilterCaps
     */
    protected void setScalarFilterCapabilities(ScalarCapabilitiesType scalarCapabilities,
                                               org.n52.shetland.ogc.filter.FilterCapabilities sosFilterCaps) {

        if (!sosFilterCaps.getComparisonOperators().isEmpty()) {
            sosFilterCaps.getComparisonOperators().stream()
                    .map(this::getEnum4ComparisonOperator)
                    .forEachOrdered(scalarCapabilities.addNewComparisonOperators()::addComparisonOperator);
        }
    }

    /**
     * Get the Enum for the spatial operator.
     *
     * @param spatialOperator
     *            Supported spatial operator
     * @return Enum
     */
    protected net.opengis.ogc.SpatialOperatorNameType.Enum getEnum4SpatialOperator(SpatialOperator spatialOperator) {
        switch (spatialOperator) {
        case BBOX:
            return SpatialOperatorNameType.BBOX;
        case Beyond:
            return SpatialOperatorNameType.BEYOND;
        case Contains:
            return SpatialOperatorNameType.CONTAINS;
        case Crosses:
            return SpatialOperatorNameType.CROSSES;
        case Disjoint:
            return SpatialOperatorNameType.DISJOINT;
        case DWithin:
            return SpatialOperatorNameType.D_WITHIN;
        case Equals:
            return SpatialOperatorNameType.EQUALS;
        case Intersects:
            return SpatialOperatorNameType.INTERSECTS;
        case Overlaps:
            return SpatialOperatorNameType.OVERLAPS;
        case Touches:
            return SpatialOperatorNameType.TOUCHES;
        case Within:
            return SpatialOperatorNameType.WITHIN;
        default:
            return null;
        }
    }

    /**
     * Get the Enum for the temporal operator.
     *
     * @param temporalOperator
     *            Supported temporal operator
     * @return Enum
     */
    protected net.opengis.ogc.TemporalOperatorNameType.Enum getEnum4TemporalOperator(TimeOperator temporalOperator) {
        switch (temporalOperator) {
        case TM_After:
            return TemporalOperatorNameType.TM_AFTER;
        case TM_Before:
            return TemporalOperatorNameType.TM_BEFORE;
        case TM_Begins:
            return TemporalOperatorNameType.TM_BEGINS;
        case TM_BegunBy:
            return TemporalOperatorNameType.TM_BEGUN_BY;
        case TM_Contains:
            return TemporalOperatorNameType.TM_CONTAINS;
        case TM_During:
            return TemporalOperatorNameType.TM_DURING;
        case TM_EndedBy:
            return TemporalOperatorNameType.TM_ENDED_BY;
        case TM_Ends:
            return TemporalOperatorNameType.TM_ENDS;
        case TM_Equals:
            return TemporalOperatorNameType.TM_EQUALS;
        case TM_Meets:
            return TemporalOperatorNameType.TM_MEETS;
        case TM_MetBy:
            return TemporalOperatorNameType.TM_MET_BY;
        case TM_OverlappedBy:
            return TemporalOperatorNameType.TM_OVERLAPPED_BY;
        case TM_Overlaps:
            return TemporalOperatorNameType.TM_OVERLAPS;
        default:
            return null;
        }
    }

    /**
     * Get the Enum for the comparison operator.
     *
     * @param comparisonOperator
     *            Supported comparison operator
     * @return Enum
     */
    protected net.opengis.ogc.ComparisonOperatorType.Enum getEnum4ComparisonOperator(
            ComparisonOperator comparisonOperator) {
        switch (comparisonOperator) {
        case PropertyIsBetween:
            return ComparisonOperatorType.BETWEEN;
        case PropertyIsEqualTo:
            return ComparisonOperatorType.EQUAL_TO;
        case PropertyIsGreaterThan:
            return ComparisonOperatorType.GREATER_THAN;
        case PropertyIsGreaterThanOrEqualTo:
            return ComparisonOperatorType.GREATER_THAN_EQUAL_TO;
        case PropertyIsLessThan:
            return ComparisonOperatorType.LESS_THAN;
        case PropertyIsLessThanOrEqualTo:
            return ComparisonOperatorType.LESS_THAN_EQUAL_TO;
        case PropertyIsLike:
            return ComparisonOperatorType.LIKE;
        case PropertyIsNotEqualTo:
            return ComparisonOperatorType.NOT_EQUAL_TO;
        case PropertyIsNull:
            return ComparisonOperatorType.NULL_CHECK;
        default:
            return null;
        }

    }
}
