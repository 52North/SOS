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
package org.n52.sos.encode;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.AbstractMetaData;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.features.SfConstants;
import org.n52.sos.ogc.om.features.samplingFeatures.AbstractSamplingFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.ConformanceClasses;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.OMHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.w3c.SchemaLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import net.opengis.gml.x32.FeaturePropertyType;
import net.opengis.sampling.x20.SFSamplingFeatureType;
import net.opengis.samplingSpatial.x20.SFSpatialSamplingFeatureDocument;
import net.opengis.samplingSpatial.x20.SFSpatialSamplingFeatureType;
import net.opengis.samplingSpatial.x20.ShapeType;

/**
 * @since 4.0.0
 * 
 */
public class SamplingEncoderv20 extends AbstractGmlEncoderv321<AbstractFeature> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SamplingEncoderv20.class);

    @SuppressWarnings("unchecked")
    private static final Set<EncoderKey> ENCODER_KEYS =
            CollectionHelper.union(CodingHelper.encoderKeysForElements(SfConstants.NS_SAMS, AbstractFeature.class),
                    CodingHelper.encoderKeysForElements(SfConstants.NS_SF, AbstractFeature.class));

    private static final Set<String> CONFORMANCE_CLASSES =
            Sets.newHashSet(ConformanceClasses.OM_V2_SPATIAL_SAMPLING, ConformanceClasses.OM_V2_SAMPLING_POINT,
                    ConformanceClasses.OM_V2_SAMPLING_CURVE, ConformanceClasses.OM_V2_SAMPLING_SURFACE);

    private static final Map<SupportedTypeKey, Set<String>> SUPPORTED_TYPES = Collections.singletonMap(
            SupportedTypeKey.FeatureType,
            (Set<String>) Sets.newHashSet(OGCConstants.UNKNOWN, SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_POINT,
                    SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_CURVE,
                    SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_SURFACE));

    public SamplingEncoderv20() {
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!",
                Joiner.on(", ").join(ENCODER_KEYS));
    }

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return Collections.unmodifiableSet(ENCODER_KEYS);
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
    public void addNamespacePrefixToMap(final Map<String, String> nameSpacePrefixMap) {
        nameSpacePrefixMap.put(SfConstants.NS_SAMS, SfConstants.NS_SAMS_PREFIX);
        nameSpacePrefixMap.put(SfConstants.NS_SF, SfConstants.NS_SF_PREFIX);
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Sets.newHashSet(SfConstants.SF_SCHEMA_LOCATION, SfConstants.SAMS_SCHEMA_LOCATION);
    }

    @Override
    public XmlObject encode(final AbstractFeature abstractFeature, final Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport {
        final XmlObject encodedObject = createFeature(abstractFeature);
        // LOGGER.debug("Encoded object {} is valid: {}",
        // encodedObject.schemaType().toString(),
        // XmlHelper.validateDocument(encodedObject));
        return encodedObject;
    }

    protected XmlObject createFeature(final AbstractFeature absFeature) throws OwsExceptionReport {
        if (absFeature instanceof AbstractSamplingFeature) {
            final AbstractSamplingFeature sampFeat = (AbstractSamplingFeature) absFeature;
            final SFSpatialSamplingFeatureDocument xbSampFeatDoc = SFSpatialSamplingFeatureDocument.Factory
                    .newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            if (sampFeat.getXmlDescription() != null) {
                try {
                    final XmlObject feature = XmlObject.Factory.parse(sampFeat.getXmlDescription(),
                            XmlOptionsHelper.getInstance().getXmlOptions());
                    if (XmlHelper.getNamespace(feature).equals(SfConstants.NS_SAMS)) {
                        XmlHelper.updateGmlIDs(feature.getDomNode().getFirstChild(), absFeature.getGmlId(), null);
                        if (feature instanceof SFSpatialSamplingFeatureType) {
                            xbSampFeatDoc.setSFSpatialSamplingFeature((SFSpatialSamplingFeatureType) feature);
                            encodeShape(xbSampFeatDoc.getSFSpatialSamplingFeature().getShape(), sampFeat);
                            addNameDescription(xbSampFeatDoc.getSFSpatialSamplingFeature(), sampFeat);
                            return xbSampFeatDoc;
                        }
                        encodeShape(((SFSpatialSamplingFeatureDocument) feature).getSFSpatialSamplingFeature().getShape(),
                                sampFeat);
                        addNameDescription(((SFSpatialSamplingFeatureDocument) feature).getSFSpatialSamplingFeature(),
                                sampFeat);
                        sampFeat.wasEncoded();
                        return feature;
                    } else {
                        return CodingHelper.encodeObjectToXml(XmlHelper.getNamespace(feature), absFeature);
                    }
                } catch (final XmlException xmle) {
                    throw new NoApplicableCodeException().causedBy(xmle).withMessage(
                            "Error while encoding GetFeatureOfInterest response, invalid samplingFeature description!");
                }
            }
            final SFSpatialSamplingFeatureType xbSampFeature = xbSampFeatDoc.addNewSFSpatialSamplingFeature();
            // TODO: CHECK for all fields set gml:id
            addId(xbSampFeature, sampFeat);
            addIdentifier(xbSampFeature, sampFeat);
            // set type
            addFeatureType(xbSampFeature, sampFeat);
            // set type
            addNameDescription(xbSampFeature, sampFeat);
            setMetaDataProperty(xbSampFeature, sampFeat);
            // set sampledFeatures
            // TODO: CHECK
            addSampledFeatures(xbSampFeature, sampFeat);

            addParameter(xbSampFeature, sampFeat);

            // set position
            encodeShape(xbSampFeature.addNewShape(), sampFeat);
            sampFeat.wasEncoded();
            return xbSampFeatDoc;
        }
        throw new UnsupportedEncoderInputException(this, absFeature);
    }

    protected void addSampledFeatures(SFSamplingFeatureType sfsft, AbstractSamplingFeature sampFeat) throws OwsExceptionReport {
        if (sampFeat.isSetSampledFeatures()) {
            Map<HelperValues, String> additionalValues = Maps.newHashMap();
            additionalValues.put(HelperValues.REFERENCED, null);
            for (AbstractFeature sampledFeature : sampFeat.getSampledFeatures()) {
                XmlObject encodeObjectToXml =
                        CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, sampledFeature, additionalValues);
                sfsft.addNewSampledFeature().set(encodeObjectToXml);
            }
        } else {
            sfsft.addNewSampledFeature().setHref(OGCConstants.UNKNOWN);
        }
    }

    protected void addFeatureType(SFSamplingFeatureType sfsft, AbstractSamplingFeature sampFeat) {
        if (sampFeat.isSetFeatureType() && !OGCConstants.UNKNOWN.equals(sampFeat.getFeatureType())) {
            sfsft.addNewType().setHref(sampFeat.getFeatureType());
        } else {
            if (sampFeat.isSetGeometry()) {
                addFeatureTypeForGeometry(sfsft, sampFeat.getGeometry());
            }
        }
    }

    private void addFeatureTypeForGeometry(SFSamplingFeatureType sfsft, Geometry geometry) {
        if (geometry instanceof Point) {
            sfsft.addNewType().setHref(SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_POINT);
        } else if (geometry instanceof LineString) {
            sfsft.addNewType().setHref(SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_CURVE);
        } else if (geometry instanceof Polygon) {
            sfsft.addNewType().setHref(SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_SURFACE);
        }
    }

    private void encodeShape(final ShapeType xbShape, final AbstractSamplingFeature sampFeat) throws OwsExceptionReport {
        final Encoder<XmlObject, Geometry> encoder = CodingRepository.getInstance()
                .getEncoder(CodingHelper.getEncoderKey(GmlConstants.NS_GML_32, sampFeat.getGeometry()));
        if (encoder != null) {
            final Map<HelperValues, String> gmlAdditionalValues =
                    new EnumMap<HelperValues, String>(HelperValues.class);
            gmlAdditionalValues.put(HelperValues.GMLID, sampFeat.getGmlId());
            final XmlObject xmlObject = encoder.encode(sampFeat.getGeometry(), gmlAdditionalValues);
            if (xbShape.isSetAbstractGeometry()) {
                xbShape.getAbstractGeometry().set(xmlObject);
            } else {
                xbShape.addNewAbstractGeometry().set(xmlObject);
            }
            XmlHelper.substituteElement(xbShape.getAbstractGeometry(), xmlObject);
        } else {
            throw new NoApplicableCodeException()
                    .withMessage("Error while encoding geometry for feature, needed encoder is missing!");
        }
    }

    protected void addParameter(final SFSamplingFeatureType xbSampFeature, final AbstractSamplingFeature sampFeat)
            throws OwsExceptionReport {
        if (sampFeat.isSetParameter()) {
            for (final NamedValue<?> namedValuePair : sampFeat.getParameters()) {
                final XmlObject encodeObjectToXml = CodingHelper.encodeObjectToXml(OmConstants.NS_OM_2, namedValuePair);
                if (encodeObjectToXml != null) {
                    xbSampFeature.addNewParameter().addNewNamedValue().set(encodeObjectToXml);
                }
            }
        }
    }

    private void addNameDescription(SFSamplingFeatureType xbSamplingFeature, AbstractSamplingFeature samplingFeature)
            throws OwsExceptionReport {
        addName(xbSamplingFeature, samplingFeature);
        addDescription(xbSamplingFeature, samplingFeature);
    }

    @Override
    protected XmlObject createFeature(FeaturePropertyType featurePropertyType, AbstractFeature abstractFeature,
            Map<HelperValues, String> additionalValues) throws OwsExceptionReport {
        if (abstractFeature instanceof AbstractSamplingFeature) {
            final AbstractSamplingFeature samplingFeature = (AbstractSamplingFeature) abstractFeature;
            String namespace;
            if (additionalValues.containsKey(HelperValues.ENCODE_NAMESPACE)) {
                namespace = additionalValues.get(HelperValues.ENCODE_NAMESPACE);
            } else {
                namespace = OMHelper.getNamespaceForFeatureType(samplingFeature.getFeatureType());
            }
            final XmlObject encodedXmlObject = CodingHelper.encodeObjectToXml(namespace, samplingFeature);

            if (encodedXmlObject != null) {
                return encodedXmlObject;
            } else {
                if (samplingFeature.getXmlDescription() != null) {
                    try {
                        // TODO how set gml:id in already existing
                        // XmlDescription? <-- XmlCursor
                        return XmlObject.Factory.parse(samplingFeature.getXmlDescription());
                    } catch (final XmlException xmle) {
                        throw new NoApplicableCodeException().causedBy(xmle)
                                .withMessage("Error while encoding featurePropertyType!");
                    }
                } else {
                    featurePropertyType.setHref(samplingFeature.getIdentifierCodeWithAuthority().getValue());
                    if (samplingFeature.isSetName()) {
                        featurePropertyType.setTitle(samplingFeature.getFirstName().getValue());
                    }
                    return featurePropertyType;
                }
            }
        }
        return featurePropertyType;
    }

    private void setMetaDataProperty(SFSpatialSamplingFeatureType sfssft, AbstractSamplingFeature sampFeat) throws OwsExceptionReport {
        if (sampFeat.isSetMetaDataProperty()) {
            for (AbstractMetaData abstractMetaData : sampFeat.getMetaDataProperty()) {
                XmlObject encodeObject = CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, abstractMetaData);
                XmlObject substituteElement = XmlHelper.substituteElement(
                        sfssft.addNewMetaDataProperty().addNewAbstractMetaData(), encodeObject);
                substituteElement.set(encodeObject);
            }
        }
    }

    private void removeExitingNames(SFSpatialSamplingFeatureType xbSamplingFeature) {
        if (CollectionHelper.isNotNullOrEmpty(xbSamplingFeature.getNameArray())) {
            for (int i = 0; i < xbSamplingFeature.getNameArray().length; i++) {
                xbSamplingFeature.removeName(i);
            }
        }
    }
}
