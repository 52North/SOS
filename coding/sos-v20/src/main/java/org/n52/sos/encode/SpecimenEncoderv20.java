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
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.om.features.SfConstants;
import org.n52.sos.ogc.om.features.samplingFeatures.PreparationStep;
import org.n52.sos.ogc.om.features.samplingFeatures.SfSpecimen;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.ConformanceClasses;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.GmlHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.w3c.SchemaLocation;
import org.n52.sos.w3c.xlink.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3.x1999.xlink.ActuateType;
import org.w3.x1999.xlink.ShowType;
import org.w3.x1999.xlink.TypeType;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.opengis.sampling.x20.SFProcessPropertyType;
import net.opengis.samplingSpatial.x20.SFSpatialSamplingFeatureDocument;
import net.opengis.samplingSpecimen.x20.LocationPropertyType;
import net.opengis.samplingSpecimen.x20.SFSpecimenDocument;
import net.opengis.samplingSpecimen.x20.SFSpecimenType;
import net.opengis.samplingSpecimen.x20.SFSpecimenType.Size;

public class SpecimenEncoderv20 extends SamplingEncoderv20 {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpecimenEncoderv20.class);

    @SuppressWarnings("unchecked")
    private static final Set<EncoderKey> ENCODER_KEYS =
            CollectionHelper.union(CodingHelper.encoderKeysForElements(SfConstants.NS_SPEC, AbstractFeature.class, SfSpecimen.class),
                    CodingHelper.encoderKeysForElements(SfConstants.NS_SF, AbstractFeature.class, SfSpecimen.class));

    private static final Set<String> CONFORMANCE_CLASSES =
            Sets.newHashSet(ConformanceClasses.OM_V2_SPATIAL_SAMPLING);

    private static final Map<SupportedTypeKey, Set<String>> SUPPORTED_TYPES = Collections.singletonMap(
            SupportedTypeKey.FeatureType,
            (Set<String>) Sets.newHashSet(OGCConstants.UNKNOWN, SfConstants.SAMPLING_FEAT_TYPE_SF_SPECIMEN));

    public SpecimenEncoderv20() {
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
        nameSpacePrefixMap.put(SfConstants.NS_SPEC, SfConstants.NS_SPEC_PREFIX);
        nameSpacePrefixMap.put(SfConstants.NS_SF, SfConstants.NS_SF_PREFIX);
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Sets.newHashSet(SfConstants.SF_SCHEMA_LOCATION, SfConstants.SPEC_SCHEMA_LOCATION);
    }

    @Override
    public XmlObject encode(final AbstractFeature abstractFeature, final Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport {
        XmlObject encodedObject;
        if (abstractFeature instanceof SfSpecimen) {
            encodedObject = createSpecimen((SfSpecimen)abstractFeature);
        } else {
            encodedObject = createFeature(abstractFeature);
        }
        // LOGGER.debug("Encoded object {} is valid: {}",
        // encodedObject.schemaType().toString(),
        // XmlHelper.validateDocument(encodedObject));
        return encodedObject;
    }

    private XmlObject createSpecimen(SfSpecimen specimen) throws OwsExceptionReport {
        SFSpecimenDocument sfsd = SFSpecimenDocument.Factory.newInstance(getXmlOptions());
        if (specimen.getXmlDescription() != null) {
            try {
                final XmlObject feature = XmlObject.Factory.parse(specimen.getXmlDescription(),
                        XmlOptionsHelper.getInstance().getXmlOptions());
                XmlHelper.updateGmlIDs(feature.getDomNode().getFirstChild(), specimen.getGmlId(), null);
                if (XmlHelper.getNamespace(feature).equals(SfConstants.NS_SPEC)
                        && feature instanceof SFSpecimenType) {
                    sfsd.setSFSpecimen((SFSpecimenType) feature);
                    addName(sfsd.getSFSpecimen(), specimen);
                    addDescription(sfsd.getSFSpecimen(), specimen);
                    return sfsd;
                }
                addName(((SFSpecimenDocument) feature).getSFSpecimen(),
                        specimen);
                addDescription(((SFSpecimenDocument) feature).getSFSpecimen(),
                        specimen);
                return feature;
            } catch (final XmlException xmle) {
                throw new NoApplicableCodeException().causedBy(xmle).withMessage(
                        "Error while encoding GetFeatureOfInterest response, invalid specimen description!");
            }
        }
        final SFSpecimenType sfst = sfsd.addNewSFSpecimen();
        // TODO: CHECK for all fields set gml:id
        addId(sfst, specimen);
        addIdentifier(sfst, specimen);
        // set type
        addFeatureType(sfst, specimen);
        addName(sfst, specimen);
        addDescription(sfst, specimen);
        // set sampledFeatures
        addSampledFeatures(sfst, specimen);
        addParameter(sfst, specimen);
        // set specimen specific data
        addMaterialClass(sfst, specimen);
        addSamplingTime(sfst, specimen);
        addSamplingMethod(sfst, specimen);
        addSamplingLocation(sfst, specimen);
        addProcessingDetails(sfst, specimen);
        addSize(sfst, specimen);
        addCurrentLocation(sfst, specimen);
        addSpecimenType(sfst, specimen);
        specimen.wasEncoded();
        return sfsd;
    }
    
    private void addMaterialClass(SFSpecimenType sfst, SfSpecimen specimen) throws OwsExceptionReport {
        sfst.addNewMaterialClass().set(encodeGML32(specimen.getMaterialClass()));
    }

    private void addSamplingTime(SFSpecimenType sfst, SfSpecimen specimen) throws OwsExceptionReport {
        XmlObject xmlObject = encodeGML32(specimen.getSamplingTime());
        XmlObject substitution =
                sfst.addNewSamplingTime().addNewAbstractTimeObject().substitute(
                        GmlHelper.getGml321QnameForITime(specimen.getSamplingTime()), xmlObject.schemaType());
        substitution.set(xmlObject);
    }

    private void addSamplingMethod(SFSpecimenType sfst, SfSpecimen specimen) {
        if (specimen.isSetSamplingMethod()) {
            if (specimen.getSamplingMethod().getInstance().isPresent()) {
                // TODO
            } else {
                sfst.addNewSamplingMethod().setHref(specimen.getSamplingMethod().getReference().getHref().get().toString());
                Reference ref = specimen.getCurrentLocation().getReference();
                SFProcessPropertyType sfppt = sfst.addNewSamplingMethod();
                if (ref.getHref().isPresent()) {
                    sfppt.setHref(ref.getHref().get().toString());
                }
                if (ref.getTitle().isPresent()) {
                    sfppt.setTitle(ref.getTitle().get());
                }
                if (ref.getActuate().isPresent()) {
                    sfppt.setActuate(ActuateType.Enum.forString(ref.getActuate().get()));
                }
                if (ref.getArcrole().isPresent()) {
                    sfppt.setArcrole(ref.getArcrole().get());
                }
                if (ref.getRole().isPresent()) {
                    sfppt.setRole(ref.getRole().get());
                }
                if (ref.getShow().isPresent()) {
                    sfppt.setShow(ShowType.Enum.forString(ref.getShow().get()));
                }
                if (ref.getType().isPresent()) {
                    sfppt.setType(TypeType.Enum.forString(ref.getType().get()));
                }
            }
        }
    }

    private void addSamplingLocation(SFSpecimenType sfst, SfSpecimen specimen) throws OwsExceptionReport {
        if (specimen.isSetSamplingLocation()) {
            Map<HelperValues, String> helperValues = Maps.newHashMap();
            helperValues.put(HelperValues.PROPERTY_TYPE, "true");
            sfst.addNewSamplingLocation().set(encodeGML32(specimen.getSamplingLocation(), helperValues));
        }
    }

    private void addProcessingDetails(SFSpecimenType sfst, SfSpecimen specimen) {
        if (specimen.isSetProcessingDetails()) {
            for (PreparationStep preparationStep : specimen.getProcessingDetails()) {
                // TODO
            }
        }
    }

    private void addSize(SFSpecimenType sfst, SfSpecimen specimen) {
        if (specimen.isSetSize()) {
            Size size = sfst.addNewSize();
            size.setDoubleValue(specimen.getSize().getValue());
            if (specimen.getSize().isSetUnit()) {
                size.setUom(specimen.getSize().getUnit());
            }
        }
    }

    private void addCurrentLocation(SFSpecimenType sfst, SfSpecimen specimen) {
        if (specimen.isSetCurrentLocation()) {
            if (specimen.getCurrentLocation().getInstance().isPresent()) {
                // TODO
            } else {
                Reference ref = specimen.getCurrentLocation().getReference();
                LocationPropertyType lpt = sfst.addNewCurrentLocation();
                if (ref.getHref().isPresent()) {
                    lpt.setHref(ref.getHref().get().toString());
                }
                if (ref.getTitle().isPresent()) {
                    lpt.setTitle(ref.getTitle().get());
                }
                if (ref.getActuate().isPresent()) {
                    lpt.setActuate(ActuateType.Enum.forString(ref.getActuate().get()));
                }
                if (ref.getArcrole().isPresent()) {
                    lpt.setArcrole(ref.getArcrole().get());
                }
                if (ref.getRole().isPresent()) {
                    lpt.setRole(ref.getRole().get());
                }
                if (ref.getShow().isPresent()) {
                    lpt.setShow(ShowType.Enum.forString(ref.getShow().get()));
                }
                if (ref.getType().isPresent()) {
                    lpt.setType(TypeType.Enum.forString(ref.getType().get()));
                }
            }
        }
    }

    private void addSpecimenType(SFSpecimenType sfst, SfSpecimen specimen) throws OwsExceptionReport {
        if (specimen.isSetSpecimenType()) {
            sfst.addNewSpecimenType().set(encodeGML32(specimen.getSpecimenType()));
        }
    }


}
