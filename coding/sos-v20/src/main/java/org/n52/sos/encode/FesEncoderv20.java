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
package org.n52.sos.encode;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import net.opengis.fes.x20.BBOXDocument;
import net.opengis.fes.x20.BBOXType;
import net.opengis.fes.x20.BinaryTemporalOpType;
import net.opengis.fes.x20.ComparisonOperatorsType;
import net.opengis.fes.x20.ConformanceType;
import net.opengis.fes.x20.DuringDocument;
import net.opengis.fes.x20.FilterCapabilitiesDocument.FilterCapabilities;
import net.opengis.fes.x20.GeometryOperandsType;
import net.opengis.fes.x20.IdCapabilitiesType;
import net.opengis.fes.x20.ScalarCapabilitiesType;
import net.opengis.fes.x20.SpatialCapabilitiesType;
import net.opengis.fes.x20.SpatialOperatorType;
import net.opengis.fes.x20.SpatialOperatorsType;
import net.opengis.fes.x20.TEqualsDocument;
import net.opengis.fes.x20.TemporalCapabilitiesType;
import net.opengis.fes.x20.TemporalOperandsType;
import net.opengis.fes.x20.TemporalOperatorType;
import net.opengis.fes.x20.TemporalOperatorsType;
import net.opengis.fes.x20.ValueReferenceDocument;
import net.opengis.fes.x20.impl.ComparisonOperatorNameTypeImpl;
import net.opengis.fes.x20.impl.SpatialOperatorNameTypeImpl;
import net.opengis.fes.x20.impl.TemporalOperatorNameTypeImpl;

import org.apache.xmlbeans.XmlObject;
import org.n52.iceland.encode.EncoderKey;
import org.n52.iceland.exception.ows.NoApplicableCodeException;
import org.n52.iceland.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.iceland.ogc.filter.FilterConstants;
import org.n52.iceland.ogc.filter.FilterConstants.ComparisonOperator;
import org.n52.iceland.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.iceland.ogc.filter.FilterConstants.TimeOperator;
import org.n52.iceland.ogc.filter.TemporalFilter;
import org.n52.iceland.ogc.gml.GmlConstants;
import org.n52.iceland.ogc.gml.time.TimeInstant;
import org.n52.iceland.ogc.gml.time.TimePeriod;
import org.n52.iceland.ogc.ows.OWSConstants;
import org.n52.iceland.ogc.ows.OWSConstants.HelperValues;
import org.n52.iceland.ogc.ows.OwsDomainType;
import org.n52.iceland.ogc.ows.OwsExceptionReport;
import org.n52.iceland.util.XmlOptionsHelper;
import org.n52.iceland.w3c.SchemaLocation;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.util.CodingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

/**
 * @since 4.0.0
 * 
 */
public class FesEncoderv20 extends AbstractXmlEncoder<Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FesEncoderv20.class);

    private static final String FALSE = Boolean.FALSE.toString();

    private static final String TRUE = Boolean.TRUE.toString();

    private static final Set<EncoderKey> ENCODER_KEYS = CodingHelper.encoderKeysForElements(FilterConstants.NS_FES_2,
            TemporalFilter.class, org.n52.iceland.ogc.filter.FilterCapabilities.class, SpatialFilter.class);

    public FesEncoderv20() {
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!", Joiner.on(", ")
                .join(ENCODER_KEYS));
    }

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return Collections.unmodifiableSet(ENCODER_KEYS);
    }

    @Override
    public void addNamespacePrefixToMap(final Map<String, String> nameSpacePrefixMap) {
        if (nameSpacePrefixMap != null) {
            nameSpacePrefixMap.put(FilterConstants.NS_FES_2, FilterConstants.NS_FES_2_PREFIX);
        }
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Sets.newHashSet(FilterConstants.FES_20_SCHEMA_LOCATION);
    }

    @Override
    public XmlObject encode(final Object element, final Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport {
        XmlObject encodedObject = null;
        if (element instanceof org.n52.iceland.ogc.filter.FilterCapabilities) {
            encodedObject = encodeFilterCapabilities((org.n52.iceland.ogc.filter.FilterCapabilities) element);
            // LOGGER.debug("Encoded object {} is valid: {}",
            // encodedObject.schemaType().toString(),
            // XmlHelper.validateDocument(encodedObject));
        } else if (element instanceof TemporalFilter) {
            encodedObject = encodeTemporalFilter((TemporalFilter) element);
        } else if (element instanceof SpatialFilter) {
            encodedObject = encodeSpatialFilter((SpatialFilter) element);
        } else {
            throw new UnsupportedEncoderInputException(this, element);
        }
        return encodedObject;

    }

    private XmlObject encodeTemporalFilter(final TemporalFilter temporalFilter) throws OwsExceptionReport {
        if (temporalFilter.getOperator().equals(TimeOperator.TM_During)) {
            return encodeTemporalFilterDuring(temporalFilter);
        } else if (temporalFilter.getOperator().equals(TimeOperator.TM_Equals)) {
            return encodeTemporalFilterEquals(temporalFilter);
        } else {
            throw new UnsupportedEncoderInputException(this, temporalFilter);
        }
    }

    private XmlObject encodeTemporalFilterDuring(final TemporalFilter temporalFilter) throws OwsExceptionReport {
        final DuringDocument duringDoc =
                DuringDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        final BinaryTemporalOpType during = duringDoc.addNewDuring();
        if (temporalFilter.getTime() instanceof TimePeriod) {
            final Map<HelperValues, String> additionalValues = new EnumMap<HelperValues, String>(HelperValues.class);
            additionalValues.put(HelperValues.DOCUMENT, "");
            during.set(CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, temporalFilter.getTime(),
                    additionalValues));
        } else {
            throw new NoApplicableCodeException().withMessage("The temporal filter value is not a TimePeriod!");
        }
        checkAndAddValueReference(during, temporalFilter);
        return duringDoc;
    }

    private XmlObject encodeTemporalFilterEquals(final TemporalFilter temporalFilter) throws OwsExceptionReport {
        final TEqualsDocument equalsDoc =
                TEqualsDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        final BinaryTemporalOpType equals = equalsDoc.addNewTEquals();
        if (temporalFilter.getTime() instanceof TimeInstant) {
            final Map<HelperValues, String> additionalValues = new EnumMap<HelperValues, String>(HelperValues.class);
            additionalValues.put(HelperValues.DOCUMENT, "");
            equals.set(CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, temporalFilter.getTime(),
                    additionalValues));
        } else {
            throw new NoApplicableCodeException().withMessage("The temporal filter value is not a TimeInstant!");
        }
        checkAndAddValueReference(equals, temporalFilter);
        return equalsDoc;
    }

    private XmlObject encodeExpression(final Object object) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, object);
    }

    private void checkAndAddValueReference(final BinaryTemporalOpType binaryTemporalOp,
            final TemporalFilter temporalFilter) {
        if (temporalFilter.hasValueReference()) {
            binaryTemporalOp.setValueReference(temporalFilter.getValueReference());
        }
    }

    private XmlObject encodeSpatialFilter(final SpatialFilter spatialFilter) throws OwsExceptionReport {
        final BBOXDocument bboxDoc = BBOXDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        final BBOXType bbox = bboxDoc.addNewBBOX();
        if (spatialFilter.hasValueReference()) {
            bbox.set(encodeReferenceValue(spatialFilter.getValueReference()));
        }
        // TODO check if srid is needed, then add as HelperValue
        bbox.setExpression(encodeExpression(spatialFilter.getGeometry()));
        return bbox;
    }

    private XmlObject encodeReferenceValue(final String sosValueReference) {
        final ValueReferenceDocument valueReferenceDoc =
                ValueReferenceDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        valueReferenceDoc.setValueReference(sosValueReference);
        return valueReferenceDoc;
    }

    private XmlObject encodeFilterCapabilities(final org.n52.iceland.ogc.filter.FilterCapabilities sosFilterCaps)
            throws OwsExceptionReport {
        final FilterCapabilities filterCapabilities =
                FilterCapabilities.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        if (sosFilterCaps.isSetCoinformance()) {
            setConformance(filterCapabilities.addNewConformance(), sosFilterCaps.getConformance());
//        } else {
//            setConformance(filterCapabilities.addNewConformance());
        }
        if (sosFilterCaps.getComparisonOperators() != null && !sosFilterCaps.getComparisonOperators().isEmpty()) {
            setScalarFilterCapabilities(filterCapabilities.addNewScalarCapabilities(), sosFilterCaps);
        }
        if (sosFilterCaps.getSpatialOperands() != null && !sosFilterCaps.getSpatialOperands().isEmpty()) {
            setSpatialFilterCapabilities(filterCapabilities.addNewSpatialCapabilities(), sosFilterCaps);
        }
        if (sosFilterCaps.getTemporalOperands() != null && !sosFilterCaps.getTemporalOperands().isEmpty()) {
            setTemporalFilterCapabilities(filterCapabilities.addNewTemporalCapabilities(), sosFilterCaps);
        }
        // setIdFilterCapabilities(filterCapabilities.addNewIdCapabilities());
        return filterCapabilities;

    }

    /**
     * Sets the FES conformance classes in the filter capabilities section.
     * 
     * @param conformance
     *            XML FES conformence
     * @param sosConformance
     *            Service conformance
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private void setConformance(final ConformanceType conformance, Collection<OwsDomainType> sosConformance)
            throws OwsExceptionReport {

        for (OwsDomainType owsDomainType : sosConformance) {
            XmlObject encodeObjectToXml = CodingHelper.encodeObjectToXml(OWSConstants.NS_OWS, owsDomainType);
            conformance.addNewConstraint().set(encodeObjectToXml);
        }
    }

    /**
     * Sets the SpatialFilterCapabilities.
     * 
     * !!! Modify method addicted to your implementation !!!
     * 
     * @param spatialCapabilitiesType
     *            FES SpatialCapabilities.
     * @param sosFilterCaps
     *            SOS spatial filter information
     */
    private void setSpatialFilterCapabilities(final SpatialCapabilitiesType spatialCapabilitiesType,
            final org.n52.iceland.ogc.filter.FilterCapabilities sosFilterCaps) throws UnsupportedEncoderInputException {

        // set GeometryOperands
        if (sosFilterCaps.getSpatialOperands() != null && !sosFilterCaps.getSpatialOperands().isEmpty()) {
            final GeometryOperandsType spatialOperands = spatialCapabilitiesType.addNewGeometryOperands();
            for (final QName operand : sosFilterCaps.getSpatialOperands()) {
                spatialOperands.addNewGeometryOperand().setName(operand);
            }
        }

        // set SpatialOperators
        if (sosFilterCaps.getSpatialOperators() != null && !sosFilterCaps.getSpatialOperators().isEmpty()) {
            final SpatialOperatorsType spatialOps = spatialCapabilitiesType.addNewSpatialOperators();
            final Set<SpatialOperator> keys = sosFilterCaps.getSpatialOperators().keySet();
            for (final SpatialOperator spatialOperator : keys) {
                final SpatialOperatorType operator = spatialOps.addNewSpatialOperator();
                operator.setName(getEnum4SpatialOperator(spatialOperator));
                final GeometryOperandsType geomOps = operator.addNewGeometryOperands();
                for (final QName operand : sosFilterCaps.getSpatialOperators().get(spatialOperator)) {
                    geomOps.addNewGeometryOperand().setName(operand);
                }
            }
        }
    }

    /**
     * Sets the TemporalFilterCapabilities.
     * 
     * !!! Modify method addicted to your implementation !!!
     * 
     * @param temporalCapabilitiesType
     *            FES TemporalCapabilities.
     * @param sosFilterCaps
     *            SOS temporal filter information
     */
    private void setTemporalFilterCapabilities(final TemporalCapabilitiesType temporalCapabilitiesType,
            final org.n52.iceland.ogc.filter.FilterCapabilities sosFilterCaps) throws UnsupportedEncoderInputException {

        // set TemporalOperands
        if (sosFilterCaps.getTemporalOperands() != null && !sosFilterCaps.getTemporalOperands().isEmpty()) {
            final TemporalOperandsType tempOperands = temporalCapabilitiesType.addNewTemporalOperands();
            for (final QName operand : sosFilterCaps.getTemporalOperands()) {
                tempOperands.addNewTemporalOperand().setName(operand);
            }
        }

        // set TemporalOperators
        if (sosFilterCaps.getTempporalOperators() != null && !sosFilterCaps.getTempporalOperators().isEmpty()) {
            final TemporalOperatorsType temporalOps = temporalCapabilitiesType.addNewTemporalOperators();
            final Set<TimeOperator> keys = sosFilterCaps.getTempporalOperators().keySet();
            for (final TimeOperator temporalOperator : keys) {
                final TemporalOperatorType operator = temporalOps.addNewTemporalOperator();
                operator.setName(getEnum4TemporalOperator(temporalOperator));
                final TemporalOperandsType bboxGeomOps = operator.addNewTemporalOperands();
                for (final QName operand : sosFilterCaps.getTempporalOperators().get(temporalOperator)) {
                    bboxGeomOps.addNewTemporalOperand().setName(operand);
                }
            }
        }
    }

    /**
     * Sets the ScalarFilterCapabilities.
     * 
     * !!! Modify method addicted to your implementation !!!
     * 
     * @param scalarCapabilitiesType
     *            FES ScalarCapabilities.
     * @param sosFilterCaps
     *            SOS scalar filter information
     */
    private void setScalarFilterCapabilities(final ScalarCapabilitiesType scalarCapabilitiesType,
            final org.n52.iceland.ogc.filter.FilterCapabilities sosFilterCaps) throws UnsupportedEncoderInputException {

        if (sosFilterCaps.getComparisonOperators() != null && !sosFilterCaps.getComparisonOperators().isEmpty()) {
            final ComparisonOperatorsType scalarOps = scalarCapabilitiesType.addNewComparisonOperators();
            for (final ComparisonOperator operator : sosFilterCaps.getComparisonOperators()) {
                scalarOps.addNewComparisonOperator().setName(getEnum4ComparisonOperator(operator));
            }
        }
    }

    /**
     * Set the IdFilterCapabilities.
     * 
     * !!! Modify method addicted to your implementation !!!
     * 
     * @param idCapabilitiesType
     *            FES IdCapabilities.
     */
    private void setIdFilterCapabilities(final IdCapabilitiesType idCapabilitiesType) {
        idCapabilitiesType.addNewResourceIdentifier();
    }

    private String getEnum4SpatialOperator(final SpatialOperator spatialOperator)
            throws UnsupportedEncoderInputException {
        switch (spatialOperator) {
        case BBOX:
            return SpatialOperatorNameTypeImpl.BBOX.toString();
        case Beyond:
            return SpatialOperatorNameTypeImpl.BEYOND.toString();
        case Contains:
            return SpatialOperatorNameTypeImpl.CONTAINS.toString();
        case Crosses:
            return SpatialOperatorNameTypeImpl.CROSSES.toString();
        case Disjoint:
            return SpatialOperatorNameTypeImpl.DISJOINT.toString();
        case DWithin:
            return SpatialOperatorNameTypeImpl.D_WITHIN.toString();
        case Equals:
            return SpatialOperatorNameTypeImpl.EQUALS.toString();
        case Intersects:
            return SpatialOperatorNameTypeImpl.INTERSECTS.toString();
        case Overlaps:
            return SpatialOperatorNameTypeImpl.OVERLAPS.toString();
        case Touches:
            return SpatialOperatorNameTypeImpl.TOUCHES.toString();
        case Within:
            return SpatialOperatorNameTypeImpl.WITHIN.toString();
        default:
            throw new UnsupportedEncoderInputException(this, spatialOperator);
        }
    }

    private String getEnum4TemporalOperator(final TimeOperator temporalOperator)
            throws UnsupportedEncoderInputException {
        switch (temporalOperator) {
        case TM_After:
            return TemporalOperatorNameTypeImpl.AFTER.toString();
        case TM_Before:
            return TemporalOperatorNameTypeImpl.BEFORE.toString();
        case TM_Begins:
            return TemporalOperatorNameTypeImpl.BEGINS.toString();
        case TM_BegunBy:
            return TemporalOperatorNameTypeImpl.BEGUN_BY.toString();
        case TM_Contains:
            return TemporalOperatorNameTypeImpl.T_CONTAINS.toString();
        case TM_During:
            return TemporalOperatorNameTypeImpl.DURING.toString();
        case TM_EndedBy:
            return TemporalOperatorNameTypeImpl.ENDED_BY.toString();
        case TM_Ends:
            return TemporalOperatorNameTypeImpl.ENDS.toString();
        case TM_Equals:
            return TemporalOperatorNameTypeImpl.T_EQUALS.toString();
        case TM_Meets:
            return TemporalOperatorNameTypeImpl.MEETS.toString();
        case TM_MetBy:
            return TemporalOperatorNameTypeImpl.MET_BY.toString();
        case TM_OverlappedBy:
            return TemporalOperatorNameTypeImpl.OVERLAPPED_BY.toString();
        case TM_Overlaps:
            return TemporalOperatorNameTypeImpl.T_OVERLAPS.toString();
        default:
            throw new UnsupportedEncoderInputException(this, temporalOperator);
        }
    }

    private String getEnum4ComparisonOperator(final ComparisonOperator comparisonOperator)
            throws UnsupportedEncoderInputException {
        switch (comparisonOperator) {
        case PropertyIsBetween:
            return ComparisonOperatorNameTypeImpl.PROPERTY_IS_BETWEEN.toString();
        case PropertyIsEqualTo:
            return ComparisonOperatorNameTypeImpl.PROPERTY_IS_EQUAL_TO.toString();
        case PropertyIsGreaterThan:
            return ComparisonOperatorNameTypeImpl.PROPERTY_IS_GREATER_THAN.toString();
        case PropertyIsGreaterThanOrEqualTo:
            return ComparisonOperatorNameTypeImpl.PROPERTY_IS_GREATER_THAN_OR_EQUAL_TO.toString();
        case PropertyIsLessThan:
            return ComparisonOperatorNameTypeImpl.PROPERTY_IS_LESS_THAN.toString();
        case PropertyIsLessThanOrEqualTo:
            return ComparisonOperatorNameTypeImpl.PROPERTY_IS_LESS_THAN_OR_EQUAL_TO.toString();
        case PropertyIsLike:
            return ComparisonOperatorNameTypeImpl.PROPERTY_IS_LIKE.toString();
        case PropertyIsNil:
            return ComparisonOperatorNameTypeImpl.PROPERTY_IS_NIL.toString();
        case PropertyIsNotEqualTo:
            return ComparisonOperatorNameTypeImpl.PROPERTY_IS_NOT_EQUAL_TO.toString();
        case PropertyIsNull:
            return ComparisonOperatorNameTypeImpl.PROPERTY_IS_NULL.toString();
        default:
            throw new UnsupportedEncoderInputException(this, comparisonOperator);
        }
    }
}
