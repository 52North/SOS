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

import static java.util.Collections.singletonMap;
import static org.n52.sos.util.CodingHelper.encoderKeysForElements;
import static org.n52.sos.util.CollectionHelper.union;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.opengis.sensorml.x20.AbstractPhysicalProcessType;
import net.opengis.sensorml.x20.AbstractProcessType;
import net.opengis.sensorml.x20.AbstractProcessType.FeaturesOfInterest;
import net.opengis.sensorml.x20.AbstractProcessType.Inputs;
import net.opengis.sensorml.x20.AbstractProcessType.Outputs;
import net.opengis.sensorml.x20.AggregateProcessDocument;
import net.opengis.sensorml.x20.AggregateProcessPropertyType;
import net.opengis.sensorml.x20.AggregateProcessType;
import net.opengis.sensorml.x20.CapabilityListType;
import net.opengis.sensorml.x20.CapabilityListType.Capability;
import net.opengis.sensorml.x20.CharacteristicListType;
import net.opengis.sensorml.x20.CharacteristicListType.Characteristic;
import net.opengis.sensorml.x20.ClassifierListPropertyType;
import net.opengis.sensorml.x20.ClassifierListType;
import net.opengis.sensorml.x20.ClassifierListType.Classifier;
import net.opengis.sensorml.x20.ComponentListPropertyType;
import net.opengis.sensorml.x20.ComponentListType;
import net.opengis.sensorml.x20.ComponentListType.Component;
import net.opengis.sensorml.x20.ContactListType;
import net.opengis.sensorml.x20.DescribedObjectType;
import net.opengis.sensorml.x20.DescribedObjectType.Capabilities;
import net.opengis.sensorml.x20.DescribedObjectType.Characteristics;
import net.opengis.sensorml.x20.DocumentListPropertyType;
import net.opengis.sensorml.x20.DocumentListType;
import net.opengis.sensorml.x20.FeatureListType;
import net.opengis.sensorml.x20.IdentifierListPropertyType;
import net.opengis.sensorml.x20.IdentifierListType;
import net.opengis.sensorml.x20.IdentifierListType.Identifier;
import net.opengis.sensorml.x20.InputListType;
import net.opengis.sensorml.x20.InputListType.Input;
import net.opengis.sensorml.x20.ObservablePropertyType;
import net.opengis.sensorml.x20.OutputListType;
import net.opengis.sensorml.x20.OutputListType.Output;
import net.opengis.sensorml.x20.PhysicalComponentDocument;
import net.opengis.sensorml.x20.PhysicalComponentPropertyType;
import net.opengis.sensorml.x20.PhysicalComponentType;
import net.opengis.sensorml.x20.PhysicalSystemDocument;
import net.opengis.sensorml.x20.PhysicalSystemPropertyType;
import net.opengis.sensorml.x20.PhysicalSystemType;
import net.opengis.sensorml.x20.PositionUnionPropertyType;
import net.opengis.sensorml.x20.ProcessMethodType;
import net.opengis.sensorml.x20.SimpleProcessDocument;
import net.opengis.sensorml.x20.SimpleProcessPropertyType;
import net.opengis.sensorml.x20.SimpleProcessType;
import net.opengis.sensorml.x20.TermType;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oxf.xml.NcNameResolver;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.iso.gmd.GmdConstants;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.AbstractProcess;
import org.n52.sos.ogc.sensorML.AbstractSensorML;
import org.n52.sos.ogc.sensorML.HasComponents;
import org.n52.sos.ogc.sensorML.HasProcessMethod;
import org.n52.sos.ogc.sensorML.ProcessMethod;
import org.n52.sos.ogc.sensorML.SensorML20Constants;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sensorML.SmlContact;
import org.n52.sos.ogc.sensorML.SmlResponsibleParty;
import org.n52.sos.ogc.sensorML.elements.AbstractSmlDocumentation;
import org.n52.sos.ogc.sensorML.elements.SmlCapabilities;
import org.n52.sos.ogc.sensorML.elements.SmlCapability;
import org.n52.sos.ogc.sensorML.elements.SmlCharacteristic;
import org.n52.sos.ogc.sensorML.elements.SmlCharacteristics;
import org.n52.sos.ogc.sensorML.elements.SmlClassifier;
import org.n52.sos.ogc.sensorML.elements.SmlComponent;
import org.n52.sos.ogc.sensorML.elements.SmlDocumentation;
import org.n52.sos.ogc.sensorML.elements.SmlDocumentationList;
import org.n52.sos.ogc.sensorML.elements.SmlDocumentationListMember;
import org.n52.sos.ogc.sensorML.elements.SmlIdentifier;
import org.n52.sos.ogc.sensorML.elements.SmlIo;
import org.n52.sos.ogc.sensorML.elements.SmlLocation;
import org.n52.sos.ogc.sensorML.elements.SmlPosition;
import org.n52.sos.ogc.sensorML.v20.AbstractPhysicalProcess;
import org.n52.sos.ogc.sensorML.v20.AbstractProcessV20;
import org.n52.sos.ogc.sensorML.v20.AggregateProcess;
import org.n52.sos.ogc.sensorML.v20.DescribedObject;
import org.n52.sos.ogc.sensorML.v20.PhysicalComponent;
import org.n52.sos.ogc.sensorML.v20.PhysicalSystem;
import org.n52.sos.ogc.sensorML.v20.SimpleProcess;
import org.n52.sos.ogc.sensorML.v20.SmlFeatureOfInterest;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweConstants;
import org.n52.sos.ogc.swe.SweDataArray;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.simpleType.SweObservableProperty;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.JavaHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.http.MediaType;
import org.n52.sos.w3c.SchemaLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;


/**
 * {@link AbstractSensorMLEncoder} class to encode OGC SensorML 2.0
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.2.0
 *
 */
public class SensorMLEncoderv20 extends AbstractSensorMLEncoder {
    private static final Logger LOGGER = LoggerFactory.getLogger(SensorMLEncoderv20.class);

    private static final Map<SupportedTypeKey, Set<String>> SUPPORTED_TYPES = singletonMap(
            SupportedTypeKey.ProcedureDescriptionFormat, (Set<String>) ImmutableSet.of(
                    SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL,
                    SensorML20Constants.SENSORML_20_CONTENT_TYPE.toString()));

    private static final Map<String, ImmutableMap<String, Set<String>>> SUPPORTED_PROCEDURE_DESCRIPTION_FORMATS =
            ImmutableMap.of(
                    SosConstants.SOS,
                    ImmutableMap
                            .<String, Set<String>> builder()
                            .put(Sos2Constants.SERVICEVERSION,
                                    ImmutableSet.of(SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL))
                            .put(Sos1Constants.SERVICEVERSION,
                                    ImmutableSet.of(SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_MIME_TYPE)).build());

    @SuppressWarnings("unchecked")
    private static final Set<EncoderKey> ENCODER_KEYS = union(
            encoderKeysForElements(SensorML20Constants.NS_SML_20, SosProcedureDescription.class,
                    AbstractSensorML.class, DescribedObject.class),
            encoderKeysForElements(SensorML20Constants.SENSORML_20_CONTENT_TYPE.toString(),
                    SosProcedureDescription.class, AbstractSensorML.class, DescribedObject.class));

    public SensorMLEncoderv20() {
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!", Joiner.on(", ")
                .join(ENCODER_KEYS));
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
    public void addNamespacePrefixToMap(final Map<String, String> nameSpacePrefixMap) {
        nameSpacePrefixMap.put(SensorML20Constants.NS_SML_20, SensorML20Constants.NS_SML_PREFIX);
    }

    @Override
    public MediaType getContentType() {
        return SensorML20Constants.SENSORML_20_CONTENT_TYPE;
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Sets.newHashSet(SensorML20Constants.SML_20_SCHEMA_LOCATION);
    }

    @Override
    public Set<String> getSupportedProcedureDescriptionFormats(final String service, final String version) {
        if (SUPPORTED_PROCEDURE_DESCRIPTION_FORMATS.containsKey(service)
                && SUPPORTED_PROCEDURE_DESCRIPTION_FORMATS.get(service).containsKey(version)) {
            return SUPPORTED_PROCEDURE_DESCRIPTION_FORMATS.get(service).get(version);
        }
        return Collections.emptySet();
    }

    @Override
    public XmlObject encode(Object objectToEncode, Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport, UnsupportedEncoderInputException {
        XmlObject encodedObject = null;
        try {
            if (objectToEncode instanceof SosProcedureDescription) {
                SosProcedureDescription description = (SosProcedureDescription) objectToEncode;
                if (description.isSetSensorDescriptionXmlString()) {
                    encodedObject =
                            XmlObject.Factory.parse(((SosProcedureDescription) objectToEncode)
                                    .getSensorDescriptionXmlString());
                    addValuesToXmlObject(encodedObject, (SosProcedureDescription) objectToEncode);
                    encodedObject = checkForAdditionalValues(encodedObject, additionalValues);
                } else {
                    encodedObject = encodeDescription(description, additionalValues);
                }
            } else {
                throw new UnsupportedEncoderInputException(this, objectToEncode);
            }
        } catch (final XmlException xmle) {
            throw new NoApplicableCodeException().causedBy(xmle);
        }
        LOGGER.debug("Encoded object {} is valid: {}", encodedObject.schemaType().toString(),
                XmlHelper.validateDocument(encodedObject));
        return encodedObject;
    }

    private XmlObject checkForAdditionalValues(XmlObject element, Map<HelperValues, String> additionalValues) {
        boolean doc = additionalValues.containsKey(HelperValues.DOCUMENT);
        boolean propertyType = additionalValues.containsKey(HelperValues.PROPERTY_TYPE);
        boolean type = additionalValues.containsKey(HelperValues.TYPE);
        if (element instanceof PhysicalSystemDocument) {
            if (propertyType) {
                PhysicalSystemPropertyType pspt = PhysicalSystemPropertyType.Factory.newInstance(getOptions());
                pspt.setPhysicalSystem(((PhysicalSystemDocument) element).getPhysicalSystem());
                return pspt;
            } else if (type) {
                return ((PhysicalSystemDocument) element).getPhysicalSystem();
            }
        } else if (element instanceof PhysicalSystemPropertyType) {
            if (doc) {
                PhysicalSystemDocument psd = PhysicalSystemDocument.Factory.newInstance(getOptions());
                psd.setPhysicalSystem(((PhysicalSystemPropertyType) element).getPhysicalSystem());
                return psd;
            } else if (type) {
                return ((PhysicalSystemPropertyType) element).getPhysicalSystem();
            }
        } else if (element instanceof PhysicalSystemType) {
            if (doc) {
                PhysicalSystemDocument psd = PhysicalSystemDocument.Factory.newInstance(getOptions());
                psd.setPhysicalSystem((PhysicalSystemType) element);
                return psd;
            } else if (propertyType) {
                PhysicalSystemPropertyType pspt = PhysicalSystemPropertyType.Factory.newInstance(getOptions());
                pspt.setPhysicalSystem((PhysicalSystemType) element);
                return pspt;
            }
        } else if (element instanceof PhysicalComponentDocument) {
            if (propertyType) {
                PhysicalComponentPropertyType pcpt = PhysicalComponentPropertyType.Factory.newInstance(getOptions());
                pcpt.setPhysicalComponent(((PhysicalComponentDocument) element).getPhysicalComponent());
            } else if (type) {
                return ((PhysicalComponentDocument) element).getPhysicalComponent();
            }
        } else if (element instanceof PhysicalComponentPropertyType) {
            if (doc) {
                PhysicalComponentDocument pcd = PhysicalComponentDocument.Factory.newInstance(getOptions());
                pcd.setPhysicalComponent(((PhysicalComponentPropertyType) element).getPhysicalComponent());
                return pcd;
            } else if (type) {
                return ((PhysicalComponentPropertyType) element).getPhysicalComponent();
            }
        } else if (element instanceof PhysicalComponentType) {
            if (doc) {
                PhysicalComponentDocument pcd = PhysicalComponentDocument.Factory.newInstance(getOptions());
                pcd.setPhysicalComponent((PhysicalComponentType) element);
                return pcd;
            } else if (propertyType) {
                PhysicalComponentPropertyType pcpt = PhysicalComponentPropertyType.Factory.newInstance(getOptions());
                pcpt.setPhysicalComponent((PhysicalComponentType) element);
                return pcpt;
            }
        } else if (element instanceof SimpleProcessDocument) {
            if (propertyType) {
                SimpleProcessPropertyType sppt = SimpleProcessPropertyType.Factory.newInstance(getOptions());
                sppt.setSimpleProcess(((SimpleProcessDocument) element).getSimpleProcess());
            } else if (type) {
                return ((SimpleProcessDocument) element).getSimpleProcess();
            }
        } else if (element instanceof SimpleProcessPropertyType) {
            if (doc) {
                SimpleProcessDocument spd = SimpleProcessDocument.Factory.newInstance(getOptions());
                spd.setSimpleProcess(((SimpleProcessPropertyType) element).getSimpleProcess());
                return spd;
            } else if (type) {
                return ((SimpleProcessPropertyType) element).getSimpleProcess();
            }
        } else if (element instanceof SimpleProcessType) {
            if (doc) {
                SimpleProcessDocument spd = SimpleProcessDocument.Factory.newInstance(getOptions());
                spd.setSimpleProcess((SimpleProcessType)element);
                return spd;
            } else if (propertyType) {
                SimpleProcessPropertyType sppt = SimpleProcessPropertyType.Factory.newInstance(getOptions());
                sppt.setSimpleProcess((SimpleProcessType)element);
                return sppt;
            }
        } else if (element instanceof AggregateProcessDocument) {
            if (propertyType) {
                AggregateProcessPropertyType appt = AggregateProcessPropertyType.Factory.newInstance(getOptions());
                appt.setAggregateProcess(((AggregateProcessDocument) element).getAggregateProcess());
            } else if (type) {
                return ((AggregateProcessDocument) element).getAggregateProcess();
            }
        } else if (element instanceof AggregateProcessPropertyType) {
            if (doc) {
                AggregateProcessDocument apd = AggregateProcessDocument.Factory.newInstance(getOptions());
                apd.setAggregateProcess(((AggregateProcessPropertyType) element).getAggregateProcess());
                return apd;
            } else if (type) {
                return ((AggregateProcessPropertyType) element).getAggregateProcess();
            }
        } else if (element instanceof AggregateProcessType) {
            if (doc) {
                AggregateProcessDocument apd = AggregateProcessDocument.Factory.newInstance(getOptions());
                apd.setAggregateProcess((AggregateProcessType)element);
                return apd;
            } else if (propertyType) {
                AggregateProcessPropertyType appt = AggregateProcessPropertyType.Factory.newInstance(getOptions());
                appt.setAggregateProcess((AggregateProcessType)element);
                return appt;
            }
        }
        return element;
    }

    private void addValuesToXmlObject(XmlObject element, SosProcedureDescription description)
            throws OwsExceptionReport {
        if (element instanceof PhysicalSystemDocument) {
            addPhysicalSystemValues(((PhysicalSystemDocument) element).getPhysicalSystem(),
                    (PhysicalSystem) description);
        } else if (element instanceof PhysicalSystemPropertyType) {
            addPhysicalSystemValues(((PhysicalSystemPropertyType) element).getPhysicalSystem(),
                    (PhysicalSystem) description);
        } else if (element instanceof PhysicalComponentDocument && description instanceof PhysicalComponent) {
            addPhysicalComponentValues(((PhysicalComponentDocument) element).getPhysicalComponent(),
                    (PhysicalComponent) description);
        } else if (element instanceof PhysicalComponentPropertyType && description instanceof PhysicalComponent) {
            addPhysicalComponentValues(((PhysicalComponentPropertyType) element).getPhysicalComponent(),
                    (PhysicalComponent) description);
        } else if (element instanceof SimpleProcessDocument && description instanceof SimpleProcess) {
            addSimpleProcessValues(((SimpleProcessDocument) element).getSimpleProcess(), (SimpleProcess) description);
        } else if (element instanceof SimpleProcessPropertyType && description instanceof SimpleProcess) {
            addSimpleProcessValues(((SimpleProcessPropertyType) element).getSimpleProcess(),
                    (SimpleProcess) description);
        } else if (element instanceof AggregateProcessDocument && description instanceof AggregateProcess) {
            addAggregateProcessValues(((AggregateProcessDocument) element).getAggregateProcess(),
                    (AggregateProcess) description);
        } else if (element instanceof AggregateProcessPropertyType && description instanceof AggregateProcess) {
            addAggregateProcessValues(((AggregateProcessPropertyType) element).getAggregateProcess(),
                    (AggregateProcess) description);
        }
    }

    private XmlObject encodeDescription(SosProcedureDescription description, Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport {
        XmlObject absProc = null;
        if (description instanceof AbstractPhysicalProcess) {
            absProc = encodeAbstractPhysicalProcess((AbstractPhysicalProcess) description, additionalValues);
        } else if (description instanceof SimpleProcess) {
            absProc = encodeSimpleProcess((SimpleProcess) description, additionalValues);
        } else if (description instanceof AggregateProcess) {
            absProc = encodeAggregateProcess((AggregateProcess) description, additionalValues);
        } else {
            throw new UnsupportedEncoderInputException(this, description);
        }
        return absProc;
    }

    private XmlObject encodeSimpleProcess(SimpleProcess abstractProcess, Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport {
        SimpleProcessPropertyType sppt = SimpleProcessPropertyType.Factory.newInstance(getOptions());
        addSimpleProcessValues(sppt.addNewSimpleProcess(), abstractProcess);
        if (additionalValues.containsKey(HelperValues.DOCUMENT)) {
            SimpleProcessDocument spd = SimpleProcessDocument.Factory.newInstance(getOptions());
            spd.setSimpleProcess(sppt.getSimpleProcess());
        } else if (additionalValues.containsKey(HelperValues.TYPE)) {
            return sppt.getSimpleProcess();
        }
        return sppt;
    }

    private void addSimpleProcessValues(SimpleProcessType spt, SimpleProcess abstractProcess)
            throws OwsExceptionReport {
        addAbstractProcessValues(spt, abstractProcess);
        addDescribedObjectValues(spt, abstractProcess);
        // set method
        if (abstractProcess.isSetMethod()) {
            spt.addNewMethod().setProcessMethod(createProcessMethod(abstractProcess));
        }
    }

    private XmlObject encodeAggregateProcess(AggregateProcess abstractProcess,
            Map<HelperValues, String> additionalValues) throws OwsExceptionReport {
        AggregateProcessPropertyType appt = AggregateProcessPropertyType.Factory.newInstance(getOptions());
        addAggregateProcessValues(appt.addNewAggregateProcess(), abstractProcess);
        if (additionalValues.containsKey(HelperValues.DOCUMENT)) {
            AggregateProcessDocument apd = AggregateProcessDocument.Factory.newInstance(getOptions());
            apd.setAbstractProcess(appt.getAggregateProcess());
        } else if (additionalValues.containsKey(HelperValues.TYPE)) {
            return appt.getAggregateProcess();
        }
        return appt;
    }

    private void addAggregateProcessValues(AggregateProcessType apt, AggregateProcess abstractProcess)
            throws OwsExceptionReport {
        addAbstractProcessValues(apt, abstractProcess);
        addDescribedObjectValues(apt, abstractProcess);
        // set components
        if (abstractProcess.isSetComponents() || abstractProcess.isSetChildProcedures()) {
            List<SmlComponent> smlComponents = checkForComponents(abstractProcess);
            if (!smlComponents.isEmpty()) {
                ComponentListPropertyType clpt = createComponents(smlComponents);
                if (clpt != null && clpt.getComponentList() != null
                        && clpt.getComponentList().sizeOfComponentArray() > 0) {
                    apt.setComponents(clpt);
                }
            }
        }
        // set connections
    }

    private XmlObject encodeAbstractPhysicalProcess(AbstractPhysicalProcess abstractPhysicalProcess,
            Map<HelperValues, String> additionalValues) throws OwsExceptionReport {
        XmlObject absPhysObj = null;
        if (abstractPhysicalProcess instanceof PhysicalSystem) {
            absPhysObj = encodePhysicalSystem((PhysicalSystem) abstractPhysicalProcess, additionalValues);
        } else if (abstractPhysicalProcess instanceof PhysicalComponent) {
            absPhysObj = encodePhysicalComponent((PhysicalComponent) abstractPhysicalProcess, additionalValues);
        } else {
            throw new UnsupportedEncoderInputException(this, abstractPhysicalProcess);
        }
        if (absPhysObj != null) {

        }
        return absPhysObj;
    }

    private XmlObject encodePhysicalComponent(PhysicalComponent abstractPhysicalProcess,
            Map<HelperValues, String> additionalValues) throws OwsExceptionReport {
        PhysicalComponentPropertyType pcpt = PhysicalComponentPropertyType.Factory.newInstance(getOptions());
        addPhysicalComponentValues(pcpt.addNewPhysicalComponent(), abstractPhysicalProcess);
        if (additionalValues.containsKey(HelperValues.DOCUMENT)) {
            PhysicalComponentDocument pcd = PhysicalComponentDocument.Factory.newInstance(getOptions());
            pcd.setPhysicalComponent(pcpt.getPhysicalComponent());
        } else if (additionalValues.containsKey(HelperValues.TYPE)) {
            return pcpt.getPhysicalComponent();
        }
        return pcpt;
    }

    private void addPhysicalComponentValues(PhysicalComponentType pct, PhysicalComponent abstractPhysicalProcess)
            throws OwsExceptionReport {
        addAbstractProcessValues(pct, abstractPhysicalProcess);
        addDescribedObjectValues(pct, abstractPhysicalProcess);
        addAbstractPhysicalProcessValues(pct, abstractPhysicalProcess);
        // set method
        if (abstractPhysicalProcess.isSetMethod()) {
            pct.addNewMethod().setProcessMethod(createProcessMethod(abstractPhysicalProcess));
        }
    }

    private XmlObject encodePhysicalSystem(PhysicalSystem abstractPhysicalProcess,
            Map<HelperValues, String> additionalValues) throws OwsExceptionReport {
        PhysicalSystemPropertyType pspt = PhysicalSystemPropertyType.Factory.newInstance(getOptions());
        addPhysicalSystemValues(pspt.addNewPhysicalSystem(), abstractPhysicalProcess);
        if (additionalValues.containsKey(HelperValues.DOCUMENT)) {
            PhysicalSystemDocument psd = PhysicalSystemDocument.Factory.newInstance(getOptions());
            psd.setPhysicalSystem(pspt.getPhysicalSystem());
        } else if (additionalValues.containsKey(HelperValues.TYPE)) {
            return pspt.getPhysicalSystem();
        }
        return pspt;
    }

    private void addPhysicalSystemValues(PhysicalSystemType pst, PhysicalSystem abstractPhysicalProcess)
            throws OwsExceptionReport {
        addAbstractProcessValues(pst, abstractPhysicalProcess);
        addDescribedObjectValues(pst, abstractPhysicalProcess);
        addAbstractPhysicalProcessValues(pst, abstractPhysicalProcess);
        // set components
        if (abstractPhysicalProcess.isSetComponents() || abstractPhysicalProcess.isSetChildProcedures()) {
            List<SmlComponent> smlComponents = checkForComponents(abstractPhysicalProcess);
            if (!smlComponents.isEmpty()) {
                ComponentListPropertyType clpt = createComponents(smlComponents);
                if (clpt != null && clpt.getComponentList() != null
                        && clpt.getComponentList().sizeOfComponentArray() > 0) {
                    pst.setComponents(clpt);
                }
            }
        }
        // set connections
    }

    private void addDescribedObjectValues(DescribedObjectType dot, DescribedObject describedObject)
            throws OwsExceptionReport {
        if (!describedObject.isSetGmlId()) {
            describedObject.setGmlId("do_" + JavaHelper.generateID(describedObject.toString()));
        }
        dot.setId(describedObject.getGmlId());
        
        // update/set gml:identifier
        if (describedObject.isSetIdentifier()) {
            describedObject.getIdentifierCodeWithAuthority().setCodeSpace(OGCConstants.UNIQUE_ID);
            XmlObject encodeObjectToXml = CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, describedObject.getIdentifierCodeWithAuthority());
            if (encodeObjectToXml != null) {
                if (dot.isSetIdentifier()) {
                    dot.getIdentifier().set(encodeObjectToXml);
                } else {
                    dot.addNewIdentifier().set(encodeObjectToXml);
                }
            }
        }
        

        // merge offerings if set
        if (describedObject.isSetOfferings()) {
            final Set<SweText> offeringsSet = convertOfferingsToSet(describedObject.getOfferings());
            mergeCapabilities(describedObject, SensorMLConstants.ELEMENT_NAME_OFFERINGS,
                    SensorMLConstants.OFFERING_FIELD_DEFINITION, null, offeringsSet);
        }
        // set capabilities
        // TODO remove parentProcedure from capabilities
        if (describedObject.isSetCapabilities()) {
            final Capabilities[] existing = dot.getCapabilitiesArray();
            final Set<String> names = Sets.newHashSetWithExpectedSize(existing.length);
            for (final Capabilities element : existing) {
                if (element.getName() != null) {
                    names.add(element.getName());
                }
            }
            for (final SmlCapabilities sosCapability : describedObject.getCapabilities()) {
                // check for observedBBOX, currently not supported, how to model
                // in SML 2.0?
                // Update Discovery Profile
                if (!SensorMLConstants.ELEMENT_NAME_OBSERVED_BBOX.equals(sosCapability.getName())) {
                    final Capabilities c = createCapability(sosCapability);
                    // replace existing capability with the same name
                    if (c != null) {
                        if (names.contains(c.getName())) {
                            removeCapability(dot, c);
                        }
                        dot.addNewCapabilities().set(c);
                    }
                }
            }
        }
        // set description
        if (describedObject.isSetDescription() && !dot.isSetDescription()) {
            dot.addNewDescription().setStringValue(describedObject.getDescription());
        }
        // set names
        if (describedObject.isSetName() && CollectionHelper.isNullOrEmpty(dot.getNameArray())) {
            // TODO check if override existing names
            addNamesToAbstractProcess(dot, describedObject.getNames());
        }
        // set location
        // set extension
        // set keywords
        if (describedObject.isSetKeywords()) {
            if (CollectionHelper.isNullOrEmpty(dot.getKeywordsArray())) {
                final List<String> keywords = describedObject.getKeywords();
                // final int length = dot.getKeywordsArray().length;
                // for (int i = 0; i < length; ++i) {
                // dot.removeKeywords(i);
                // }
                dot.addNewKeywords().addNewKeywordList()
                        .setKeywordArray(keywords.toArray(new String[keywords.size()]));
            } else {
                // TODO
            }
        }
        // set identification
        if (describedObject.isSetIdentifications()) {
            if (!CollectionHelper.isNullOrEmpty(dot.getIdentificationArray())) {
                // TODO check for merging identifications if exists
            }
            dot.setIdentificationArray(createIdentification(describedObject.getIdentifications()));
        }
        // set classification
        if (describedObject.isSetClassifications()) {
            dot.setClassificationArray(createClassification(describedObject.getClassifications()));
        }
        // set validTime
        if (describedObject.isSetValidTime() && CollectionHelper.isNullOrEmpty(dot.getValidTimeArray())) {
            final Time time = describedObject.getValidTime();
            final XmlObject xbtime = CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, time);
            if (time instanceof TimeInstant) {
                dot.addNewValidTime().addNewTimeInstant().set(xbtime);
            } else if (time instanceof TimePeriod) {
                dot.addNewValidTime().addNewTimePeriod().set(xbtime);
            }
            // } else {
            // TODO remove or
            // remove existing validTime element
            // final XmlCursor newCursor = dot.getValidTime().newCursor();
            // newCursor.removeXml();
            // newCursor.dispose();

        }
        // set securityConstraints
        // set legalConstraints
        // set characteristics
        if (describedObject.isSetCharacteristics()) {
            dot.setCharacteristicsArray(createCharacteristics(describedObject.getCharacteristics()));
        }
        // set contacts if contacts aren't already present in the abstract
        // process
        // if (describedObject.isSetContact() &&
        // CollectionHelper.isNotNullOrEmpty(dot.getContactsArray())) {
        if (describedObject.isSetContact()) {
            ContactListType cl = ContactListType.Factory.newInstance();
            for (SmlContact contact : describedObject.getContact()) {
                if (contact instanceof SmlResponsibleParty) {
                    XmlObject encodeObjectToXml =
                            CodingHelper.encodeObjectToXml(GmdConstants.NS_GMD, (SmlResponsibleParty) contact);
                    if (encodeObjectToXml != null) {
                        cl.addNewContact().addNewCIResponsibleParty().set(encodeObjectToXml);
                    }
                }
            }
            if (CollectionHelper.isNotNullOrEmpty(cl.getContactArray())) {
                dot.addNewContacts().setContactList(cl);
            }
        }
        // set documentation
        if (describedObject.isSetDocumentation()) {
            dot.setDocumentationArray(createDocumentationArray(describedObject.getDocumentation()));
        }
        // set history

    }

    private void addAbstractProcessValues(final AbstractProcessType apt, final AbstractProcessV20 sosAbstractProcess)
            throws OwsExceptionReport {
        // TODO
        // set typeOf
        // set configuration
        // set featureOfInterest
        if (sosAbstractProcess.isSetSmlFeatureOfInterest() && sosAbstractProcess.getSmlFeatureOfInterest().isSetFeatures()) {
            if (!apt.isSetFeaturesOfInterest()) {
                apt.setFeaturesOfInterest(createFeatureOfInterest(sosAbstractProcess.getSmlFeatureOfInterest()));
            }
            addFeatures(apt.getFeaturesOfInterest().getFeatureList(), sosAbstractProcess.getSmlFeatureOfInterest());
        }
        // set inputs
        if (sosAbstractProcess.isSetInputs()) {
            apt.setInputs(createInputs(sosAbstractProcess.getInputs()));
        }
        // set outputs
        if (sosAbstractProcess.isSetOutputs()) {
            extendOutputs(sosAbstractProcess);
            apt.setOutputs(createOutputs(sosAbstractProcess.getOutputs()));
        }
        // set parameters
        // set modes
    }

    private void addAbstractPhysicalProcessValues(final AbstractPhysicalProcessType appt,
            final AbstractPhysicalProcess absPhysicalProcess) throws OwsExceptionReport {
        // set attachedTo
        if (!appt.isSetAttachedTo() && absPhysicalProcess.isSetAttachedTo()) {
            XmlObject encodeObjectToXml =
                    CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, absPhysicalProcess.getAttachedTo());
            if (encodeObjectToXml != null) {
                appt.addNewAttachedTo().set(encodeObjectToXml);
            }
        }
        // set localReferenceFrame
        // set localTimeFrame
        // set position
        if (CollectionHelper.isNullOrEmpty(appt.getPositionArray()) && absPhysicalProcess.isSetPosition()) {
            createPosition(appt.addNewPosition(), absPhysicalProcess.getPosition());
        }
        // set timePosition
        // // set location
        // if (absPhysicalProcess.isSetLocation()) {
        // appt.setSmlLocation(createLocation(absPhysicalProcess.getLocation()));
        // }

    }

    private List<SmlComponent> checkForComponents(AbstractProcess abstractProcess) throws CodedException {
        List<SmlComponent> smlComponents = Lists.newArrayList();
        if (abstractProcess instanceof HasComponents<?> && ((HasComponents<?>) abstractProcess).isSetComponents()) {
            smlComponents.addAll(((HasComponents<?>) abstractProcess).getComponents());
        }
        if (abstractProcess.isSetChildProcedures()) {
            smlComponents.addAll(createComponentsForChildProcedures(abstractProcess.getChildProcedures()));
        }
        if (!smlComponents.isEmpty()) {
            // TODO check for duplicated outputs
            abstractProcess.getOutputs().addAll(getOutputsFromChilds(smlComponents));
            // TODO check if necessary
            // system.addFeatureOfInterest(getFeaturesFromChild(smlComponents));
        }
        return smlComponents;
    }

    private void addNamesToAbstractProcess(DescribedObjectType dot, List<CodeType> names) throws OwsExceptionReport {
        for (CodeType codeType : names) {
            dot.addNewName().set(CodingHelper.encodeObjectToXml(GmlConstants.NS_GML, codeType));
        }
    }

    // private ContactList createContactList(final List<SmlContact> contacts) {
    // final ContactList xbContacts = ContactList.Factory.newInstance();
    // for (final SmlContact smlContact : contacts) {
    // if (smlContact instanceof SmlPerson) {
    // ContactList.Member member = xbContacts.addNewMember();
    // member.addNewPerson().set(createPerson((SmlPerson) smlContact));
    // if (!Strings.isNullOrEmpty(smlContact.getRole())) {
    // member.setRole(smlContact.getRole());
    // }
    // } else if (smlContact instanceof SmlResponsibleParty) {
    // ContactList.Member member = xbContacts.addNewMember();
    // member.addNewResponsibleParty().set(createResponsibleParty((SmlResponsibleParty)
    // smlContact));
    // if (!Strings.isNullOrEmpty(smlContact.getRole())) {
    // member.setRole(smlContact.getRole());
    // }
    // } else if (smlContact instanceof SmlContactList) {
    // SmlContactList contactList = (SmlContactList) smlContact;
    // ContactList innerContactList =
    // createContactList(contactList.getMembers());
    // int innerContactLength = innerContactList.getMemberArray().length;
    // for (int i = 0; i < innerContactLength; i++) {
    // xbContacts.addNewMember().set(innerContactList.getMemberArray(i));
    // }
    // }
    // }
    // return xbContacts;
    // }
    //
    // private XmlObject createResponsibleParty(final SmlResponsibleParty
    // smlRespParty) {
    // final ResponsibleParty xbRespParty =
    // ResponsibleParty.Factory.newInstance();
    // if (smlRespParty.isSetIndividualName()) {
    // xbRespParty.setIndividualName(smlRespParty.getIndividualName());
    // }
    // if (smlRespParty.isSetOrganizationName()) {
    // xbRespParty.setOrganizationName(smlRespParty.getOrganizationName());
    // }
    // if (smlRespParty.isSetPositionName()) {
    // xbRespParty.setPositionName(smlRespParty.getPositionName());
    // }
    // if (smlRespParty.isSetContactInfo()) {
    // xbRespParty.setContactInfo(createContactInfo(smlRespParty));
    // }
    // return xbRespParty;
    // }

    // private ContactInfo createContactInfo(final SmlResponsibleParty
    // smlRespParty) {
    // final ContactInfo xbContactInfo = ContactInfo.Factory.newInstance();
    // if (smlRespParty.isSetHoursOfService()) {
    // xbContactInfo.setHoursOfService(smlRespParty.getHoursOfService());
    // }
    // if (smlRespParty.isSetContactInstructions()) {
    // xbContactInfo.setContactInstructions(smlRespParty.getContactInstructions());
    // }
    // if (smlRespParty.isSetOnlineResources()) {
    // for (final String onlineResouce : smlRespParty.getOnlineResources()) {
    // xbContactInfo.addNewOnlineResource().setHref(onlineResouce);
    // }
    // }
    // if (smlRespParty.isSetPhone()) {
    // final Phone xbPhone = xbContactInfo.addNewPhone();
    // if (smlRespParty.isSetPhoneFax()) {
    // for (final String fax : smlRespParty.getPhoneFax()) {
    // xbPhone.addFacsimile(fax);
    // }
    // }
    // if (smlRespParty.isSetPhoneVoice()) {
    // for (final String voice : smlRespParty.getPhoneVoice()) {
    // xbPhone.addVoice(voice);
    // }
    // }
    // }
    // if (smlRespParty.isSetAddress()) {
    // final Address xbAddress = xbContactInfo.addNewAddress();
    // if (smlRespParty.isSetDeliveryPoint()) {
    // for (final String deliveryPoint : smlRespParty.getDeliveryPoint()) {
    // xbAddress.addDeliveryPoint(deliveryPoint);
    // }
    // }
    // if (smlRespParty.isSetCity()) {
    // xbAddress.setCity(smlRespParty.getCity());
    // }
    // if (smlRespParty.isSetAdministrativeArea()) {
    // xbAddress.setAdministrativeArea(smlRespParty.getAdministrativeArea());
    // }
    // if (smlRespParty.isSetPostalCode()) {
    // xbAddress.setPostalCode(smlRespParty.getPostalCode());
    // }
    // if (smlRespParty.isSetCountry()) {
    // xbAddress.setCountry(smlRespParty.getCountry());
    // }
    // if (smlRespParty.isSetEmail()) {
    // xbAddress.setElectronicMailAddress(smlRespParty.getEmail());
    // }
    // }
    // return xbContactInfo;
    // }
    //
    // private Person createPerson(final SmlPerson smlPerson) {
    // final Person xbPerson = Person.Factory.newInstance();
    // if (smlPerson.isSetAffiliation()) {
    // xbPerson.setAffiliation(smlPerson.getAffiliation());
    // }
    // if (smlPerson.isSetEmail()) {
    // xbPerson.setEmail(smlPerson.getEmail());
    // }
    // if (smlPerson.isSetName()) {
    // xbPerson.setName(smlPerson.getName());
    // }
    // if (smlPerson.isSetPhoneNumber()) {
    // xbPerson.setPhoneNumber(smlPerson.getPhoneNumber());
    // }
    // if (smlPerson.isSetSurname()) {
    // xbPerson.setSurname(smlPerson.getSurname());
    // }
    // if (smlPerson.isSetUserID()) {
    // xbPerson.setUserID(smlPerson.getUserID());
    // }
    // return xbPerson;
    // }
    //

    // private ContactList createContactList(final List<SmlContact> contacts) {
    // final ContactList xbContacts = ContactList.Factory.newInstance();
    // for (final SmlContact smlContact : contacts) {
    // if (smlContact instanceof SmlPerson) {
    // ContactList.Member member = xbContacts.addNewMember();
    // member.addNewPerson().set(createPerson((SmlPerson) smlContact));
    // if (!Strings.isNullOrEmpty(smlContact.getRole())) {
    // member.setRole(smlContact.getRole());
    // }
    // } else if (smlContact instanceof SmlResponsibleParty) {
    // ContactList.Member member = xbContacts.addNewMember();
    // member.addNewResponsibleParty().set(createResponsibleParty((SmlResponsibleParty)
    // smlContact));
    // if (!Strings.isNullOrEmpty(smlContact.getRole())) {
    // member.setRole(smlContact.getRole());
    // }
    // } else if (smlContact instanceof SmlContactList) {
    // SmlContactList contactList = (SmlContactList) smlContact;
    // ContactList innerContactList =
    // createContactList(contactList.getMembers());
    // int innerContactLength = innerContactList.getMemberArray().length;
    // for (int i = 0; i < innerContactLength; i++) {
    // xbContacts.addNewMember().set(innerContactList.getMemberArray(i));
    // }
    // }
    // }
    // return xbContacts;
    // }
    //
    // private XmlObject createResponsibleParty(final SmlResponsibleParty
    // smlRespParty) {
    // final ResponsibleParty xbRespParty =
    // ResponsibleParty.Factory.newInstance();
    // if (smlRespParty.isSetIndividualName()) {
    // xbRespParty.setIndividualName(smlRespParty.getIndividualName());
    // }
    // if (smlRespParty.isSetOrganizationName()) {
    // xbRespParty.setOrganizationName(smlRespParty.getOrganizationName());
    // }
    // if (smlRespParty.isSetPositionName()) {
    // xbRespParty.setPositionName(smlRespParty.getPositionName());
    // }
    // if (smlRespParty.isSetContactInfo()) {
    // xbRespParty.setContactInfo(createContactInfo(smlRespParty));
    // }
    // return xbRespParty;
    // }
    // private boolean isContactListSetAndContainingElements(final Contact
    // contact) {
    // return contact.getContactList() != null &&
    // contact.getContactList().getMemberArray() != null
    // && contact.getContactList().getMemberArray().length > 0;
    // }

    private void removeCapability(final DescribedObjectType dot, final Capabilities c) {
        // get current index of element with this name
        for (int i = 0; i < dot.getCapabilitiesArray().length; i++) {
            if (dot.getCapabilitiesArray(i).getName().equals(c.getName())) {
                dot.removeCapabilities(i);
                return;
            }
        }
    }

    private Capabilities createCapability(final SmlCapabilities capabilities) throws OwsExceptionReport {
        Capabilities xbCapabilities = null;
        if (capabilities.isSetAbstractDataComponents()) {
            xbCapabilities = Capabilities.Factory.newInstance(getOptions());
            if (capabilities.isSetName()) {
                xbCapabilities.setName(capabilities.getName());
            }

            CapabilityListType capabilityList = xbCapabilities.addNewCapabilityList();
            if (capabilities.isSetCapabilities()) {
                for (SmlCapability capability : capabilities.getCapabilities()) {
                    XmlObject encodeObjectToXml = CodingHelper.encodeObjectToXml(SweConstants.NS_SWE_20, capability.getAbstractDataComponent());
                    Capability c = capabilityList.addNewCapability();
                    c.setName(NcNameResolver.fixNcName(capability.getName()));
                    XmlObject substituteElement =
                            XmlHelper.substituteElement(c.addNewAbstractDataComponent(), encodeObjectToXml);
                    substituteElement.set(encodeObjectToXml);
                    
                }
            } else if (capabilities.isSetAbstractDataComponents()) {
                for (SweAbstractDataComponent component : capabilities.getAbstractDataComponents()) {
                    XmlObject encodeObjectToXml = CodingHelper.encodeObjectToXml(SweConstants.NS_SWE_20, component);
                    Capability capability = capabilityList.addNewCapability();
                    capability.setName(NcNameResolver.fixNcName(component.getName().getValue()));
                    XmlObject substituteElement =
                            XmlHelper.substituteElement(capability.addNewAbstractDataComponent(), encodeObjectToXml);
                    substituteElement.set(encodeObjectToXml);
                }
            }
        }
        return xbCapabilities;
    }

    private void extendOutputs(AbstractProcess abstractProcess) {
        if (abstractProcess.isSetPhenomenon()) {
            for (SmlIo<?> output : abstractProcess.getOutputs()) {
                if (abstractProcess.hasPhenomenonFor(output.getIoValue().getDefinition())) {
                    output.getIoValue().setName(
                            abstractProcess.getPhenomenonFor(output.getIoValue().getDefinition()).getName());
                }
            }
        }
    }

    private ProcessMethodType createProcessMethod(HasProcessMethod processMethod) {
        // TODO how to?
        ProcessMethod method = processMethod.getMethod();
        ProcessMethodType pmt = ProcessMethodType.Factory.newInstance(getOptions());
        // if (false) {
        // pmt.setDescription("");
        // pmt.setIdentifier("");
        // pmt.setLabel("");
        // }

        // if (method.isSetHref()) {
        // xbMethod.setHref(method.getHref());
        // if (method.isSetTitle()) {
        // xbMethod.setTitle(method.getTitle());
        // }
        // if (method.isSetRole()) {
        // xbMethod.setRole(method.getRole());
        // }
        // } else if (method.isSetRulesDefinition()) {
        // final ProcessMethodType xbProcessMethod =
        // xbMethod.addNewProcessMethod();
        // final RulesDefinition xbRulesDefinition =
        // xbProcessMethod.addNewRules().addNewRulesDefinition();
        // if (method.getRulesDefinition().isSetDescription()) {
        // xbRulesDefinition.addNewDescription().setStringValue(method.getRulesDefinition().getDescription());
        // }
        // } else {
        // throw new NoApplicableCodeException().at("method").withMessage(
        // "The ProcessMethod should contain a href string or a RulesDefinition!");
        // }
        // return pmt;
        return null;
    }

    /**
     * Creates the valueentification section of the SensorML description.
     * 
     * @param identifications
     *            SOS valueentifications
     * @return XML Identification array
     */
    protected IdentifierListPropertyType[] createIdentification(final List<SmlIdentifier> identifications) {
        final IdentifierListPropertyType xbIdentification =
                IdentifierListPropertyType.Factory.newInstance(getOptions());
        final IdentifierListType xbIdentifierList = xbIdentification.addNewIdentifierList();
        for (final SmlIdentifier sosSMLIdentifier : identifications) {
            final Identifier xbIdentifier = xbIdentifierList.addNewIdentifier2();
            final TermType xbTerm = xbIdentifier.addNewTerm();
            if (sosSMLIdentifier.isSetDefinition()) {
                xbTerm.setDefinition(sosSMLIdentifier.getDefinition());
            }
            xbTerm.setLabel(sosSMLIdentifier.getLabel());
            xbTerm.setValue(sosSMLIdentifier.getValue());
        }
        return new IdentifierListPropertyType[] { xbIdentification };
    }

    /**
     * Creates the classification section of the SensorML description.
     * 
     * @param classifications
     *            SOS classifications
     * @return XML Classification array
     */
    private ClassifierListPropertyType[] createClassification(final List<SmlClassifier> classifications) {
        final ClassifierListPropertyType xbClassification =
                ClassifierListPropertyType.Factory.newInstance(getOptions());
        final ClassifierListType xbClassifierList = xbClassification.addNewClassifierList();
        for (final SmlClassifier sosSMLClassifier : classifications) {
            final Classifier xbClassifier = xbClassifierList.addNewClassifier();
            final TermType xbTerm = xbClassifier.addNewTerm();
            xbTerm.setValue(sosSMLClassifier.getValue());
            if (sosSMLClassifier.isSetDefinition()) {
                xbTerm.setDefinition(sosSMLClassifier.getDefinition());
            }
            if (sosSMLClassifier.isSetCodeSpace()) {
                xbTerm.addNewCodeSpace().setHref(sosSMLClassifier.getCodeSpace());
            }

        }
        return new ClassifierListPropertyType[] { xbClassification };
    }

    /**
     * Creates the characteristics section of the SensorML description.
     * 
     * @param smlCharacteristics
     *            SOS characteristics list
     * @return XML Characteristics array
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private Characteristics[] createCharacteristics(final List<SmlCharacteristics> smlCharacteristics)
            throws OwsExceptionReport {
        final List<Characteristics> characteristicsList =
                Lists.newArrayListWithExpectedSize(smlCharacteristics.size());
        for (final SmlCharacteristics sosSMLCharacteristics : smlCharacteristics) {
            if (sosSMLCharacteristics.isSetAbstractDataComponents()) {
                Characteristics xbCharacteristics = Characteristics.Factory.newInstance(getOptions());
                CharacteristicListType characteristicList = xbCharacteristics.addNewCharacteristicList();
                if (sosSMLCharacteristics.isSetCharacteristics()) {
                    for (SmlCharacteristic characteristic : sosSMLCharacteristics.getCharacteristic()) {
                        if (characteristic.isSetAbstractDataComponent()) {
                            XmlObject encodeObjectToXml = CodingHelper.encodeObjectToXml(SweConstants.NS_SWE_20, characteristic.getAbstractDataComponent());
                            Characteristic c = characteristicList.addNewCharacteristic();
                            c.setName(NcNameResolver.fixNcName(characteristic.getName()));
                            XmlObject substituteElement =
                                    XmlHelper.substituteElement(c.addNewAbstractDataComponent(), encodeObjectToXml);
                            substituteElement.set(encodeObjectToXml);
                        }
                    }
                } else if (sosSMLCharacteristics.isSetAbstractDataComponents()) {
                    for (SweAbstractDataComponent component : sosSMLCharacteristics.getAbstractDataComponents()) {
                        XmlObject encodeObjectToXml = CodingHelper.encodeObjectToXml(SweConstants.NS_SWE_20, component);
                        Characteristic c = characteristicList.addNewCharacteristic();
                        c.setName(NcNameResolver.fixNcName(component.getName().getValue()));
                        XmlObject substituteElement =
                                XmlHelper.substituteElement(c.addNewAbstractDataComponent(), encodeObjectToXml);
                        substituteElement.set(encodeObjectToXml);
                    }
                }
                characteristicsList.add(xbCharacteristics);
            }
        }
        return characteristicsList.toArray(new Characteristics[characteristicsList.size()]);
    }

    /**
     * Create XML Documentation array from SOS documentations
     * 
     * @param sosDocumentation
     *            SOS documentation list
     * @return XML Documentation array
     */
    protected DocumentListPropertyType[] createDocumentationArray(final List<AbstractSmlDocumentation> sosDocumentation) {
        final List<DocumentListPropertyType> documentationList =
                Lists.newArrayListWithExpectedSize(sosDocumentation.size());
        for (final AbstractSmlDocumentation abstractSosSMLDocumentation : sosDocumentation) {
            final DocumentListPropertyType documentation = DocumentListPropertyType.Factory.newInstance();
            if (abstractSosSMLDocumentation instanceof SmlDocumentation) {
                documentation.setDocumentList(createDocument((SmlDocumentation) abstractSosSMLDocumentation));
            } else if (abstractSosSMLDocumentation instanceof SmlDocumentationList) {
                documentation
                        .setDocumentList(createDocumentationList((SmlDocumentationList) abstractSosSMLDocumentation));
            }
            documentationList.add(documentation);
        }
        return documentationList.toArray(new DocumentListPropertyType[documentationList.size()]);
    }

    /**
     * Create a XML Documentation element from SOS documentation
     * 
     * @param sosDocumentation
     *            SOS documentation
     * @return XML Documentation element
     */
    private DocumentListType createDocument(final SmlDocumentation sosDocumentation) {
        final DocumentListType documentList = DocumentListType.Factory.newInstance();
        if (sosDocumentation.isSetDescription()) {
            documentList.setDescription(sosDocumentation.getDescription());
        }
        // TODO encode
        // documentList.addNewDocument().setCIOnlineResource(ciOnlineResource);
        return documentList;
    }

    /**
     * Create a XML DocuemntList from SOS documentList
     * 
     * @param sosDocumentationList
     *            SOS documentList
     * @return XML DocumentList element
     */
    private DocumentListType createDocumentationList(final SmlDocumentationList sosDocumentationList) {
        final DocumentListType documentList = DocumentListType.Factory.newInstance();
        if (sosDocumentationList.isSetDescription()) {
            documentList.setDescription(sosDocumentationList.getDescription());
        }
        if (sosDocumentationList.isSetMembers()) {
            for (final SmlDocumentationListMember sosMmember : sosDocumentationList.getMember()) {
                // TODO encode
                // documentList.addNewDocument().setCIOnlineResource(ciOnlineResource);
            }
        }
        return documentList;
    }

    /**
     * Creates the position section of the SensorML description.
     * 
     * @param pupt
     * 
     * @param position
     *            SOS position
     * @return XML Position element
     * @throws OwsExceptionReport
     *             if an error occurs
     */
    private void createPosition(PositionUnionPropertyType pupt, final SmlPosition position) throws OwsExceptionReport {
        if (position.isSetVector()) {
        pupt.addNewVector().set(CodingHelper.encodeObjectToXml(SweConstants.NS_SWE_20, position.getVector()));
        } else if (position.isSetAbstractDataComponent()) {
            SweAbstractDataComponent abstractDataComponent = position.getAbstractDataComponent();
            if (abstractDataComponent instanceof SweDataRecord) {
                pupt.addNewDataRecord().set(CodingHelper.encodeObjectToXml(SweConstants.NS_SWE_20, abstractDataComponent));
            } else if (abstractDataComponent instanceof SweDataArray) {
                pupt.addNewDataArray1().set(CodingHelper.encodeObjectToXml(SweConstants.NS_SWE_20, abstractDataComponent));
            }
        }
    }

    /**
     * Creates the location section of the SensorML description.
     * 
     * @param location
     *            SOS location representation.
     * @return XML SmlLocation2 element
     * @throws OwsExceptionReport
     *             if an error occurs
     */
    private void createLocation(DescribedObjectType dot, SmlLocation location) throws OwsExceptionReport {
        dot.addNewLocation().addNewAbstractGeometry()
                .set(CodingHelper.encodeObjectToXml(GmlConstants.NS_GML, location.getPoint()));
    }

    /**
     * Creates the inputs section of the SensorML description.
     * 
     * @param inputs
     *            SOS SWE representation.
     * @return XML Inputs element
     * 
     * @throws OwsExceptionReport
     *             if an error occurs
     */
    private Inputs createInputs(final List<SmlIo<?>> inputs) throws OwsExceptionReport {
        final Inputs xbInputs = Inputs.Factory.newInstance(getOptions());
        final InputListType xbInputList = xbInputs.addNewInputList();
        int counter = 1;
        for (final SmlIo<?> sosSMLIo : inputs) {
            if (!sosSMLIo.isSetName()) {
                sosSMLIo.setIoName("input_" + counter++);
            } else {
                sosSMLIo.setIoName(NcNameResolver.fixNcName(sosSMLIo.getIoName()));
            }
            addInput(xbInputList.addNewInput(), sosSMLIo);
        }
        return xbInputs;
    }

    /**
     * Creates the outputs section of the SensorML description.
     * 
     * @param sosOutputs
     *            SOS SWE representation.
     * @return XML Outputs element
     * 
     * @throws OwsExceptionReport
     */
    private Outputs createOutputs(final List<SmlIo<?>> sosOutputs) throws OwsExceptionReport {
        final Outputs outputs = Outputs.Factory.newInstance(getOptions());
        final OutputListType outputList = outputs.addNewOutputList();
        final Set<String> definitions = Sets.newHashSet();
        int counter = 1;
        final Set<String> outputNames = Sets.newHashSet();
        for (final SmlIo<?> sosSMLIo : sosOutputs) {
            if (sosSMLIo.isSetValue() && !definitions.contains(sosSMLIo.getIoValue().getDefinition())) {
                if (!sosSMLIo.isSetName() || outputNames.contains(sosSMLIo.getIoName())) {
                    sosSMLIo.setIoName(getValidOutputName(counter++, outputNames));
                } else {
                    sosSMLIo.setIoName(NcNameResolver.fixNcName(sosSMLIo.getIoName()));
                }
                outputNames.add(sosSMLIo.getIoName());
                addOutput(outputList.addNewOutput(), sosSMLIo);
                definitions.add(sosSMLIo.getIoValue().getDefinition());
            }
        }
        return outputs;
    }

    private FeaturesOfInterest createFeatureOfInterest(SmlFeatureOfInterest feature) {
        if (feature.isSetFeaturesOfInterest()) {
            FeaturesOfInterest foi = FeaturesOfInterest.Factory.newInstance(getOptions());
            FeatureListType featureList = foi.addNewFeatureList();
            if (feature.isSetDefinition()) {
                featureList.setDefinition(feature.getDefinition());
            }
            if (feature.isSetDescription()) {
                featureList.setDescription(feature.getDescription());
            }
            if (feature.isSetIdentifier()) {
                featureList.setIdentifier(feature.getIdentifier());
            }
            if (feature.isSetLabel()) {
                featureList.setLabel(feature.getLabel());
            }
            return foi;
        }
        return null;
    }
    
    private void addFeatures(FeatureListType featureList, SmlFeatureOfInterest feature) {
        Set<String> featuresToAdd = Sets.newHashSet();
        if (feature.isSetFeaturesOfInterest()) {
            featuresToAdd.addAll(feature.getFeaturesOfInterest());
        }
        if (feature.isSetFeaturesOfInterestMap()) {
            featuresToAdd.addAll(feature.getFeaturesOfInterestMap().keySet());
        }
        for (int i = 0; i < featureList.sizeOfFeatureArray(); i++) {
            featureList.removeFeature(i);
        }
        for (String featureIdentifier : featuresToAdd) {
            // TODO encode in GML 3.2.1 encoder
            featureList.addNewFeature().setHref(featureIdentifier);
        }
    }

    /**
     * Creates the components section of the SensorML description.
     * 
     * @param sosComponents
     *            SOS SWE representation.
     * @return encoded sml:components
     * @throws OwsExceptionReport
     */
    private ComponentListPropertyType createComponents(final List<SmlComponent> sosComponents)
            throws OwsExceptionReport {
        ComponentListPropertyType clpt = ComponentListPropertyType.Factory.newInstance(getOptions());
        final ComponentListType clt = clpt.addNewComponentList();
        for (final SmlComponent sosSMLComponent : sosComponents) {
            final Component component = clt.addNewComponent();
            if (sosSMLComponent.getName() != null) {
                component.setName(sosSMLComponent.getName());
            }
            if (sosSMLComponent.getTitle() != null) {
                component.setTitle(sosSMLComponent.getTitle());
            }
            if (sosSMLComponent.getHref() != null) {
                component.setHref(sosSMLComponent.getHref());
            }
            if (sosSMLComponent.getProcess() != null) {
                Map<HelperValues, String> additionalValues = Maps.newHashMap();
                additionalValues.put(HelperValues.TYPE, null);
                XmlObject xmlObject = encode(sosSMLComponent.getProcess(), additionalValues);
                // if
                // (sosSMLComponent.getProcess().getSensorDescriptionXmlString()
                // != null
                // &&
                // !sosSMLComponent.getProcess().getSensorDescriptionXmlString().isEmpty())
                // {
                // try {
                // xmlObject =
                // XmlObject.Factory.parse(sosSMLComponent.getProcess().getSensorDescriptionXmlString());
                //
                // } catch (final XmlException xmle) {
                // throw new
                // NoApplicableCodeException().causedBy(xmle).withMessage(
                // "Error while encoding SensorML child procedure description "
                // +
                // "from stored SensorML encoded sensor description with XMLBeans");
                // }
                // } else {
                // xmlObject = encode(sosSMLComponent.getProcess());
                // }
                if (xmlObject != null) {
                    // AbstractProcessType xbProcess = null;
                    // if (xmlObject instanceof AbstractProcessType) {
                    // xbProcess = (AbstractProcessType) xmlObject;
                    // } else {
                    // throw new NoApplicableCodeException()
                    // .withMessage("The sensor type is not supported by this SOS");
                    // }
                    // TODO add feature/parentProcs/childProcs to component - is
                    // this already done?
                    XmlObject substituteElement =
                            XmlHelper.substituteElement(component.addNewAbstractProcess(), xmlObject);
                    substituteElement.set(xmlObject);
                }
            }
        }
        return clpt;
    }

    /**
     * Adds a SOS SWE simple type to a XML SML Input.
     * 
     * @param input
     *            SML Input
     * @param sosSMLIO
     *            SOS SWE simple type.
     * @throws OwsExceptionReport
     */
    private void addInput(final Input input, final SmlIo<?> sosSMLIO) throws OwsExceptionReport {
        input.setName(sosSMLIO.getIoName());
        if (sosSMLIO.getIoValue() instanceof SweObservableProperty) {
            addValueToObservableProperty(input.addNewObservableProperty(), sosSMLIO.getIoValue());
        } else {
            final XmlObject encodeObjectToXml =
                    CodingHelper.encodeObjectToXml(SweConstants.NS_SWE_20, sosSMLIO.getIoValue());
            XmlObject substituteElement =
                    XmlHelper.substituteElement(input.addNewAbstractDataComponent(), encodeObjectToXml);
            substituteElement.set(encodeObjectToXml);
        }
    }

    private void addValueToObservableProperty(ObservablePropertyType opt, SweAbstractDataComponent observableProperty) {
        if (observableProperty.isSetDefinition()) {
            opt.setDefinition(observableProperty.getDefinition());
        }
        if (observableProperty.isSetDescription()) {
            opt.setDescription(observableProperty.getDescription());
        }
        if (observableProperty.isSetIdentifier()) {
            opt.setIdentifier(observableProperty.getIdentifier());
        }
        if (observableProperty.isSetLabel()) {
            opt.setLabel(observableProperty.getLabel());
        }
    }

    /**
     * Adds a SOS SWE simple type to a XML SML Output.
     * 
     * @param output
     *            SML Output
     * @param sosSMLIO
     *            SOS SWE simple type.
     * 
     * @throws OwsExceptionReport
     */
    private void addOutput(final Output output, final SmlIo<?> sosSMLIO) throws OwsExceptionReport {
        output.setName(sosSMLIO.getIoName());
        final XmlObject encodeObjectToXml =
                CodingHelper.encodeObjectToXml(SweConstants.NS_SWE_20, sosSMLIO.getIoValue());
        XmlObject substituteElement =
                XmlHelper.substituteElement(output.addNewAbstractDataComponent(), encodeObjectToXml);
        substituteElement.set(encodeObjectToXml);
    }

    // /**
    // * Get the QName for the SchemaType
    // *
    // * @param type
    // * Schema type
    // * @return Related QName
    // */
    // private QName getQnameForType(final SchemaType type) {
    // if (type == SystemType.type) {
    // return SensorMLConstants.SYSTEM_QNAME;
    // } else if (type == ProcessModelType.type) {
    // return SensorMLConstants.PROCESS_MODEL_QNAME;
    // }
    // return SensorMLConstants.ABSTRACT_PROCESS_QNAME;
    // }

}
