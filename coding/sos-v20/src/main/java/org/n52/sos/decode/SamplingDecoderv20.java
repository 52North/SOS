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
package org.n52.sos.decode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.opengis.gml.x32.FeaturePropertyType;
import net.opengis.gml.x32.ReferenceType;
import net.opengis.samplingSpatial.x20.SFSpatialSamplingFeatureDocument;
import net.opengis.samplingSpatial.x20.SFSpatialSamplingFeatureType;
import net.opengis.samplingSpatial.x20.ShapeType;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.om.features.SfConstants;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.ConformanceClasses;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * @since 4.0.0
 * 
 */
public class SamplingDecoderv20 implements Decoder<AbstractFeature, XmlObject> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SamplingDecoderv20.class);

    private static final Map<SupportedTypeKey, Set<String>> SUPPORTED_TYPES = Collections.singletonMap(
            SupportedTypeKey.FeatureType, (Set<String>) Sets.newHashSet(OGCConstants.UNKNOWN,
                    SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_POINT,
                    SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_CURVE,
                    SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_SURFACE));

    private static final Set<String> CONFORMANCE_CLASSES = Sets.newHashSet(ConformanceClasses.OM_V2_SPATIAL_SAMPLING,
            ConformanceClasses.OM_V2_SAMPLING_POINT, ConformanceClasses.OM_V2_SAMPLING_CURVE,
            ConformanceClasses.OM_V2_SAMPLING_SURFACE);

    @SuppressWarnings("unchecked")
    private static final Set<DecoderKey> DECODER_KEYS = CollectionHelper.union(CodingHelper.decoderKeysForElements(
            SfConstants.NS_SF, SFSpatialSamplingFeatureDocument.class, SFSpatialSamplingFeatureType.class),
            CodingHelper.decoderKeysForElements(SfConstants.NS_SAMS, SFSpatialSamplingFeatureDocument.class,
                    SFSpatialSamplingFeatureType.class));

    public SamplingDecoderv20() {
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
    public AbstractFeature decode(final XmlObject element) throws OwsExceptionReport {
        // validate XmlObject
        XmlHelper.validateDocument(element);
        if (element instanceof SFSpatialSamplingFeatureDocument) {
            return parseSpatialSamplingFeature(((SFSpatialSamplingFeatureDocument) element)
                    .getSFSpatialSamplingFeature());
        } else if (element instanceof SFSpatialSamplingFeatureType) {
            return parseSpatialSamplingFeature(((SFSpatialSamplingFeatureType) element));
        }
        return null;
    }

    private AbstractFeature parseSpatialSamplingFeature(final SFSpatialSamplingFeatureType spatialSamplingFeature)
            throws OwsExceptionReport {
        final SamplingFeature sosFeat = new SamplingFeature(null, spatialSamplingFeature.getId());
        if (spatialSamplingFeature.getIdentifier() != null
                && spatialSamplingFeature.getIdentifier().getStringValue() != null
                && !spatialSamplingFeature.getIdentifier().getStringValue().isEmpty()) {
            final CodeWithAuthority identifier =
                    (CodeWithAuthority) CodingHelper.decodeXmlElement(spatialSamplingFeature.getIdentifier());
            sosFeat.setIdentifier(identifier);
        }
        if (spatialSamplingFeature.getNameArray() != null) {
            sosFeat.setName(getNames(spatialSamplingFeature));
        }
        if (spatialSamplingFeature.isSetDescription()) {
            sosFeat.setDescription(spatialSamplingFeature.getDescription().getStringValue());
        }
        sosFeat.setFeatureType(getFeatureType(spatialSamplingFeature.getType()));
        sosFeat.setSampledFeatures(getSampledFeatures(spatialSamplingFeature.getSampledFeatureArray()));
        sosFeat.setXmlDescription(getXmlDescription(spatialSamplingFeature));
        sosFeat.setGeometry(getGeometry(spatialSamplingFeature.getShape()));
        checkTypeAndGeometry(sosFeat);
        sosFeat.setGmlId(spatialSamplingFeature.getId());
        return sosFeat;
    }

    private String getXmlDescription(final SFSpatialSamplingFeatureType spatialSamplingFeature) {
        final SFSpatialSamplingFeatureDocument featureDoc =
                SFSpatialSamplingFeatureDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        featureDoc.setSFSpatialSamplingFeature(spatialSamplingFeature);
        return featureDoc.xmlText(XmlOptionsHelper.getInstance().getXmlOptions());
    }

    private List<CodeType> getNames(final SFSpatialSamplingFeatureType spatialSamplingFeature)
            throws OwsExceptionReport {
        final int length = spatialSamplingFeature.getNameArray().length;
        final List<CodeType> names = new ArrayList<CodeType>(length);
        for (int i = 0; i < length; i++) {
            final Object decodedElement = CodingHelper.decodeXmlObject(spatialSamplingFeature.getNameArray(i));
            if (decodedElement instanceof CodeType) {
                names.add((CodeType) decodedElement);
            }
        }
        return names;
    }

    private String getFeatureType(final ReferenceType type) {
        if (type != null && type.getHref() != null && !type.getHref().isEmpty()) {
            return type.getHref();
        }
        return null;
    }

    /**
     *  Parse {@link FeaturePropertyType} sampledFeatures to {@link AbstractFeature} list. 
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
                    final SamplingFeature sampFeat =
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

    private Geometry getGeometry(final ShapeType shape) throws OwsExceptionReport {
        final Object decodedObject = CodingHelper.decodeXmlElement(shape.getAbstractGeometry());
        if (decodedObject instanceof Geometry) {
            return (Geometry) decodedObject;
        }
        throw new InvalidParameterValueException().at(Sos2Constants.InsertObservationParams.observation).withMessage(
                "The requested geometry type of featureOfInterest is not supported by this service!");
    }

    private void checkTypeAndGeometry(final SamplingFeature sosFeat) throws OwsExceptionReport {
        final String featTypeForGeometry = getFeatTypeForGeometry(sosFeat.getGeometry());
        if (sosFeat.getFeatureType() == null) {
            sosFeat.setFeatureType(featTypeForGeometry);
        } else {
            if (!featTypeForGeometry.equals(sosFeat.getFeatureType())) {

                throw new NoApplicableCodeException().withMessage(
                        "The requested observation is invalid! The featureOfInterest type "
                                + "does not comply with the defined type (%s)!", sosFeat.getFeatureType());
            }
        }

    }

    private String getFeatTypeForGeometry(final Geometry geometry) {
        if (geometry instanceof Point) {
            return SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_POINT;
        } else if (geometry instanceof LineString) {
            return SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_CURVE;
        } else if (geometry instanceof Polygon) {
            return SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_SURFACE;
        }
        return OGCConstants.UNKNOWN;
    }

}
