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
package org.n52.sos.decode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.AbstractGeometry;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.om.features.SfConstants;
import org.n52.sos.ogc.om.features.samplingFeatures.AbstractSamplingFeature;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.om.features.samplingFeatures.SfSpecimen;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.ConformanceClasses;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Geometry;

import net.opengis.gml.x32.FeaturePropertyType;
import net.opengis.samplingSpecimen.x20.SFSpecimenDocument;
import net.opengis.samplingSpecimen.x20.SFSpecimenType;
import net.opengis.samplingSpecimen.x20.SFSpecimenType.Size;

public class SpecimenDecoderv20 extends SamplingDecoderv20 {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpecimenDecoderv20.class);

    private static final Map<SupportedTypeKey, Set<String>> SUPPORTED_TYPES = Collections.singletonMap(
            SupportedTypeKey.FeatureType, (Set<String>) Sets.newHashSet(OGCConstants.UNKNOWN,
                    SfConstants.SAMPLING_FEAT_TYPE_SF_SPECIMEN));

    private static final Set<String> CONFORMANCE_CLASSES = Sets.newHashSet(ConformanceClasses.OM_V2_SPATIAL_SAMPLING);

    private static final Set<DecoderKey> DECODER_KEYS = CodingHelper.decoderKeysForElements(SfConstants.NS_SPEC, SFSpecimenDocument.class,
                    SFSpecimenType.class);

    public SpecimenDecoderv20() {
        LOGGER.debug("Decoder for the following keys initialized successfully: {}!", Joiner.on(", ")
                .join(DECODER_KEYS));
    }

    @Override
    public Set<DecoderKey> getDecoderKeyTypes() {
        return Collections.unmodifiableSet(DECODER_KEYS);
    }

    @Override
    public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
        return Collections.unmodifiableMap(SUPPORTED_TYPES);
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.unmodifiableSet(CONFORMANCE_CLASSES);

    }

    @Override
    public AbstractFeature decode(final Object element) throws OwsExceptionReport {
        // validate XmlObject
        if (element instanceof XmlObject) {
            XmlHelper.validateDocument((XmlObject)element);
        }
        if (element instanceof SFSpecimenDocument) {
            return parseSpatialSamplingFeature(((SFSpecimenDocument) element)
                    .getSFSpecimen());
        } else if (element instanceof SFSpecimenType) {
            return parseSpatialSamplingFeature(((SFSpecimenType) element));
        }
        return null;
    }
    private AbstractFeature parseSpatialSamplingFeature(final SFSpecimenType sfst)
            throws OwsExceptionReport {
        final SfSpecimen specimen = new SfSpecimen(null, sfst.getId());
        // parse identifier, names, description
        parseAbstractFeatureType(sfst, specimen);
        specimen.setSampledFeatures(getSampledFeatures(sfst.getSampledFeatureArray()));
        specimen.setXmlDescription(getXmlDescription(sfst));
        if (sfst.getParameterArray() != null) {
            specimen.setParameters(parseNamedValueTypeArray(sfst.getParameterArray()));
        }
        // TODO
        sfst.getMaterialClass();
        specimen.setMaterialClass((ReferenceType)CodingHelper.decodeXmlElement(sfst.getMaterialClass()));
        specimen.setSamplingTime(getSamplingTime(sfst));
        if (sfst.isSetSamplingMethod()) {
//        specimen.setSamplingMethod(sfst.getSamplingMethod());
        }
        // samplingLocation
        if (sfst.isSetSamplingLocation()) {
            specimen.setSamplingLocation(getGeometry(sfst));
        }
//        sfst.getProcessingDetailsArray();
        if (sfst.isSetSize()) {
            specimen.setSize(getSize(sfst.getSize()));
        }
        sfst.getCurrentLocation();
        if (sfst.isSetCurrentLocation()) {
//            specimen.setCurrentLocation(currentLocation);
        }
        if (sfst.isSetSpecimenType()) {
            specimen.setSpecimenType((ReferenceType)CodingHelper.decodeXmlElement(sfst.getSpecimenType()));
        }
        return specimen;
    }

    private Time getSamplingTime(SFSpecimenType sfst) throws OwsExceptionReport {
        if (sfst.getSamplingTime().isSetAbstractTimeObject()) {
            Object decodedObject = CodingHelper.decodeXmlObject(sfst.getSamplingTime().getAbstractTimeObject());
            if (decodedObject instanceof Time) {
                return (Time) decodedObject;
            }
        }
        return null;
    }

    private QuantityValue getSize(Size size) {
        QuantityValue quantityValue = new QuantityValue(size.getDoubleValue());
        quantityValue.setUnit(size.getUom());
        return quantityValue;
    }

    private String getXmlDescription(final SFSpecimenType sfst) {
        final SFSpecimenDocument sfsd =
                SFSpecimenDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        sfsd.setSFSpecimen(sfst);
        return sfsd.xmlText(XmlOptionsHelper.getInstance().getXmlOptions());
    }

    /**
     * Parse {@link FeaturePropertyType} sampledFeatures to {@link AbstractFeature} list. 
     * @param sampledFeatureArray SampledFeatures to parse
     * @return List with the parsed sampledFeatures
     * @throws OwsExceptionReport If an error occurs
     */
    private List<AbstractFeature> getSampledFeatures(FeaturePropertyType[] sampledFeatureArray)
            throws OwsExceptionReport {
        final List<AbstractFeature> sampledFeatures = Lists.newArrayList();
        for (FeaturePropertyType featurePropertyType : sampledFeatureArray) {
            sampledFeatures.addAll(getSampledFeatures(featurePropertyType));
        }
        return sampledFeatures;
    }

    /**
     * Parse {@link FeaturePropertyType} sampledFeature to {@link AbstractFeature} list. 
     * 
     * @param sampledFeature SampledFeature to parse
     * @return List with the parsed sampledFeature
     * @throws OwsExceptionReport If an error occurs
     */
    private List<AbstractFeature> getSampledFeatures(final FeaturePropertyType sampledFeature)
            throws OwsExceptionReport {
        final List<AbstractFeature> sampledFeatures = new ArrayList<AbstractFeature>(1);
        if (sampledFeature != null && !sampledFeature.isNil()) {
            // if xlink:href is set
            if (sampledFeature.getHref() != null && !sampledFeature.getHref().isEmpty()) {
                if (sampledFeature.getHref().startsWith("#")) {
                    sampledFeatures.add(new SamplingFeature(null, sampledFeature.getHref().replace("#", "")));
                } else {
                    final AbstractSamplingFeature sampFeat =
                            new SamplingFeature(new CodeWithAuthority(sampledFeature.getHref()));
                    if (sampledFeature.getTitle() != null && !sampledFeature.getTitle().isEmpty()) {
                        sampFeat.addName(new CodeType(sampledFeature.getTitle()));
                    }
                    sampledFeatures.add(sampFeat);
                }
            } else {
                XmlObject abstractFeature = null;
                if (sampledFeature.getAbstractFeature() != null) {
                    abstractFeature = sampledFeature.getAbstractFeature();
                } else if (sampledFeature.getDomNode().hasChildNodes()) {
                    try {
                        abstractFeature =
                                XmlObject.Factory.parse(XmlHelper.getNodeFromNodeList(sampledFeature.getDomNode()
                                        .getChildNodes()));
                    } catch (final XmlException xmle) {
                        throw new NoApplicableCodeException().causedBy(xmle).withMessage(
                                "Error while parsing feature request!");
                    }
                }
                if (abstractFeature != null) {
                    final Object decodedObject = CodingHelper.decodeXmlObject(abstractFeature);
                    if (decodedObject instanceof AbstractFeature) {
                        sampledFeatures.add((AbstractFeature) decodedObject);
                    }
                }
                throw new InvalidParameterValueException().at(Sos2Constants.InsertObservationParams.observation)
                        .withMessage("The requested sampledFeature type is not supported by this service!");
            }
        }
        return sampledFeatures;
    }

    private Geometry getGeometry(final SFSpecimenType sfst) throws OwsExceptionReport {
        final Object decodedObject = CodingHelper.decodeXmlElement(sfst.getSamplingLocation());
        if (decodedObject instanceof Geometry) {
            return (Geometry) decodedObject;
        } else if (decodedObject instanceof AbstractGeometry) {
            return ((AbstractGeometry) decodedObject).getGeometry();
        }
        throw new InvalidParameterValueException().at(Sos2Constants.InsertObservationParams.observation).withMessage(
                "The requested geometry type of featureOfInterest is not supported by this service!");
    }

}
