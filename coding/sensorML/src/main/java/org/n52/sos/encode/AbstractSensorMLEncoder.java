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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.custommonkey.xmlunit.Diff;
import org.n52.oxf.xml.NcNameResolver;
import org.n52.sos.binding.BindingConstants;
import org.n52.sos.binding.BindingRepository;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.sensorML.AbstractProcess;
import org.n52.sos.ogc.sensorML.AbstractSensorML;
import org.n52.sos.ogc.sensorML.SensorML;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sensorML.elements.SmlCapabilities;
import org.n52.sos.ogc.sensorML.elements.SmlCapabilitiesPredicates;
import org.n52.sos.ogc.sensorML.elements.SmlComponent;
import org.n52.sos.ogc.sensorML.elements.SmlIo;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosOffering;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.ogc.swe.DataRecord;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.SweSimpleDataRecord;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.service.operator.ServiceOperatorRepository;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.SosHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;


/**
 * Abstract {@link AbstractXmlEncoder} class to encode OGC SensorML
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.2.0
 *
 */
public abstract class AbstractSensorMLEncoder extends AbstractXmlEncoder<Object> implements ProcedureEncoder<XmlObject, Object> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSensorMLEncoder.class);
    
    private static final String OUTPUT_PREFIX = "output#";

    /**
     * Add special capabilities to abstract process:
     * <ul>
     * <li>featureOfInterest,</li>
     * <li>sosOfferings,</li>
     * <li>parentProcedures</li>
     * </ul>
     * but only, if available.
     * 
     * @param abstractProcess
     *            SOS abstract process.
     */
    protected void addSpecialCapabilities(final AbstractProcess abstractProcess) {
        if (abstractProcess.isSetFeaturesOfInterestMap()) {
            final Set<SweText> featureSet = convertFeaturesToSet(abstractProcess.getFeaturesOfInterestMap());
            mergeCapabilities(abstractProcess, SensorMLConstants.ELEMENT_NAME_FEATURES_OF_INTEREST,
                    SensorMLConstants.FEATURE_OF_INTEREST_FIELD_DEFINITION, SensorMLConstants.FEATURE_OF_INTEREST_FIELD_NAME, featureSet);

        } else {
            if (abstractProcess.isSetFeaturesOfInterest()) {
                final Map<String, String> valueNamePairs =
                        createValueNamePairs(SensorMLConstants.FEATURE_OF_INTEREST_FIELD_NAME,
                                abstractProcess.getFeaturesOfInterest());
                mergeCapabilities(abstractProcess, SensorMLConstants.ELEMENT_NAME_FEATURES_OF_INTEREST,
                        SensorMLConstants.FEATURE_OF_INTEREST_FIELD_DEFINITION, valueNamePairs);
            }
        }

        if (abstractProcess.isSetOfferings()) {
            final Set<SweText> offeringsSet = convertOfferingsToSet(abstractProcess.getOfferings());
            mergeCapabilities(abstractProcess, SensorMLConstants.ELEMENT_NAME_OFFERINGS,
                    SensorMLConstants.OFFERING_FIELD_DEFINITION, null, offeringsSet);
        }

        if (abstractProcess.isSetParentProcedures()) {
            final Map<String, String> valueNamePairs =
                    createValueNamePairs(SensorMLConstants.PARENT_PROCEDURE_FIELD_NAME,
                            abstractProcess.getParentProcedures());
            mergeCapabilities(abstractProcess, SensorMLConstants.ELEMENT_NAME_PARENT_PROCEDURES,
                    SensorMLConstants.PARENT_PROCEDURE_FIELD_DEFINITION, valueNamePairs);
        }
    }
    
    
    private void mergeCapabilities(final AbstractProcess process, final String capabilitiesName,
            final String definition, final Map<String, String> valueNamePairs) {
        final Optional<SmlCapabilities> capabilities =
                process.findCapabilities(SmlCapabilitiesPredicates.name(capabilitiesName));

        if (capabilities.isPresent() && capabilities.get().isSetAbstractDataRecord()) {
            final DataRecord dataRecord = capabilities.get().getDataRecord();
            // update present capabilities
            for (final SweField field : dataRecord.getFields()) {

                // update the definition if not present
                if (field.getDefinition() == null) {
                    field.setDefinition(definition);
                }

                // update the name of present field
                if (field.getElement() instanceof SweText) {
                    final SweText sweText = (SweText) field.getElement();
                    final String value = sweText.getValue();
                    if (valueNamePairs.containsKey(value)) {
                        field.setName(valueNamePairs.get(value));
                        // we don't need to add it any more
                        valueNamePairs.remove(value);
                    }
                }
            }
            // add capabilities not yet present
            final List<SweField> additionalFields = createCapabilitiesFieldsFrom(definition, valueNamePairs);
            for (final SweField field : additionalFields) {
                dataRecord.addField(field);
            }
        } else {
            if (capabilities.isPresent()) {
                process.removeCapabilities(capabilities.get());
            }
            // create new capabilities
            process.addCapabilities(createCapabilitiesFrom(capabilitiesName, definition, valueNamePairs));
        }
    }

    protected void mergeCapabilities(final AbstractProcess process, final String capabilitiesName,
            final String definition, String fieldName, final Set<SweText> sweTextFieldSet) {
        final Optional<SmlCapabilities> capabilities =
                process.findCapabilities(SmlCapabilitiesPredicates.name(capabilitiesName));
        if (capabilities.isPresent() && capabilities.get().isSetAbstractDataRecord()) {
            final DataRecord dataRecord = capabilities.get().getDataRecord();
            // update present capabilities
            for (final SweField field : dataRecord.getFields()) {

                // update the definition if not present
                if (field.getDefinition() == null) {
                    field.setDefinition(definition);
                }

                // update the name of present field
                if (field.getElement() instanceof SweText) {
                    final SweText sweText = (SweText) field.getElement();
                    Set<SweText> fieldsToRemove = Sets.newHashSet();
                    for (SweText sweTextField : sweTextFieldSet) {
                        if (sweText.getValue().equals(sweTextField.getValue())) {
                            if (sweTextField.isSetName()) {
                                field.setName(sweTextField.getName().getValue());
                            } else {
                                field.setName(fieldName);
                            }
                            // we don't need to add it any more
                            fieldsToRemove.add(sweTextField);
                        }
                    }
                    if (CollectionHelper.isNotEmpty(fieldsToRemove)) {
                        for (SweText st : fieldsToRemove) {
                            sweTextFieldSet.remove(st);
                        }
                    }
                }
            }
            // add capabilities not yet present
            final List<SweField> additionalFields = createCapabilitiesFieldsFrom(definition, fieldName, sweTextFieldSet);
            for (final SweField field : additionalFields) {
                dataRecord.addField(field);
            }
        } else {
            if (capabilities.isPresent()) {
                process.removeCapabilities(capabilities.get());
            }
            // create new capabilities
            process.addCapabilities(createCapabilitiesFrom(capabilitiesName, definition, fieldName, sweTextFieldSet));
        }
    }

    /**
     * Convert SOS sosOfferings to map with key == identifier and value = name
     * 
     * @param offerings
     *            SOS sosOfferings
     * @return Map with identifier, name.
     */
    protected Map<String, String> convertOfferingsToMap(final Set<SosOffering> offerings) {
        final Map<String, String> valueNamePairs = Maps.newHashMapWithExpectedSize(offerings.size());
        for (final SosOffering offering : offerings) {
            valueNamePairs.put(offering.getIdentifier(), offering.getOfferingName());
        }
        return valueNamePairs;
    }

    /**
     * Convert SOS sosOfferings to map with key == identifier and value = name
     * @param featureOfInterestFieldName 
     * 
     * @param map
     *            .values() SOS sosOfferings
     * @return Set with identifier, name.
     */
    protected Set<SweText> convertFeaturesToSet(final Map<String, AbstractFeature> map) {
        final Set<SweText> featureSet = Sets.newHashSetWithExpectedSize(map.values().size());
        for (final AbstractFeature abstractFeature : map.values()) {
            SweText sweText = new SweText();
            sweText.setValue(abstractFeature.getIdentifier());
            for (CodeType name : abstractFeature.getName()) {
                sweText.addName(name);
            }
            if (abstractFeature.isSetDescription()) {
                sweText.setDescription(abstractFeature.getDescription());
            }
            featureSet.add(sweText);
        }
        return featureSet;
    }

    /**
     * Convert SOS sosOfferings to map with key == identifier and value = name
     * 
     * @param offerings
     *            SOS sosOfferings
     * @return Set with identifier, name.
     */
    protected Set<SweText> convertOfferingsToSet(final Set<SosOffering> offerings) {
        final Set<SweText> offeringSet = Sets.newHashSetWithExpectedSize(offerings.size());
        for (final SosOffering offering : offerings) {
            SweText sweText = new SweText();
            sweText.setValue(offering.getIdentifier());
            for (CodeType name : offering.getName()) {
                sweText.addName(name);
            }
            if (offering.isSetDescription()) {
                sweText.setDescription(offering.getDescription());
            }
            offeringSet.add(sweText);
        }
        return offeringSet;
    }

    protected Map<String, String> createValueNamePairs(final String fieldName, final Set<String> values) {
        final Map<String, String> valueNamePairs = Maps.newHashMapWithExpectedSize(values.size());
        if (values.size() == 1) {
            valueNamePairs.put(values.iterator().next(), fieldName);
        } else {
            int counter = 1;
            for (final String value : values) {
                valueNamePairs.put(value, fieldName + (counter++));
            }
        }
        return valueNamePairs;
    }

    /**
     * Creates a SOS capability object form data
     * 
     * @param capabilitiesName
     *            Element name
     * @param fieldDefinition
     *            Field definition
     * @param valueNamePairs
     *            Value map
     * @return SOS capability
     */
    protected SmlCapabilities createCapabilitiesFrom(final String capabilitiesName, final String fieldDefinition,
            final Map<String, String> valueNamePairs) {
        final SmlCapabilities capabilities = new SmlCapabilities();
        capabilities.setName(capabilitiesName);
        final SweSimpleDataRecord simpleDataRecord = new SweSimpleDataRecord();
        final List<SweField> fields = createCapabilitiesFieldsFrom(fieldDefinition, valueNamePairs);
        capabilities.setDataRecord(simpleDataRecord.setFields(fields));
        return capabilities;
    }

    private List<SweField> createCapabilitiesFieldsFrom(final String fieldDefinition,
            final Map<String, String> valueNamePairs) {
        final List<SweField> fields = Lists.newArrayListWithExpectedSize(valueNamePairs.size());
        final List<String> values = Lists.newArrayList(valueNamePairs.keySet());
        Collections.sort(values);
        for (final String value : values) {
            final SweText text = new SweText();
            text.setDefinition(fieldDefinition);
            text.setValue(value);
            final String fieldName = valueNamePairs.get(value);
            final SweField field = new SweField(fieldName, text);
            fields.add(field);
        }
        return fields;
    }

    protected SmlCapabilities createCapabilitiesFrom(final String capabilitiesName, final String fieldDefinition, final String fieldName,
            final Set<SweText> sweTextSet) {
        final SmlCapabilities capabilities = new SmlCapabilities();
        capabilities.setName(capabilitiesName);
        final SweSimpleDataRecord simpleDataRecord = new SweSimpleDataRecord();
        final List<SweField> fields = createCapabilitiesFieldsFrom(fieldDefinition, fieldName, sweTextSet);
        capabilities.setDataRecord(simpleDataRecord.setFields(fields));
        return capabilities;
    }

    private List<SweField> createCapabilitiesFieldsFrom(final String fieldElementDefinition, final String fieldName, final Set<SweText> sweTextSet) {
        final List<SweField> fields = Lists.newArrayListWithExpectedSize(sweTextSet.size());
        final List<SweText> values = Lists.newArrayList(sweTextSet);
        Collections.sort(values);
        for (final SweText text : values) {
            text.setDefinition(fieldElementDefinition);
            String name = fieldName;
            if (Strings.isNullOrEmpty(fieldName)) {
                if (text.isSetName() && text.getName().isSetValue()) {
                    name = text.getName().getValue();
                } else {
                    name =  SensorMLConstants.DEFAULT_FIELD_NAME  + Integer.toString(values.indexOf(text));
                }
            }
            final SweField field = new SweField(name, text);
            fields.add(field);
        }
        return fields;
    }

    /**
     * Create SOS component list from child SOS procedure descriptions
     * 
     * @param childProcedures
     *            Chile procedure descriptions
     * @return SOS component list
     * @throws CodedException
     *             If an error occurs
     */
    protected List<SmlComponent> createComponentsForChildProcedures(final Set<SosProcedureDescription> childProcedures)
            throws CodedException {
        final List<SmlComponent> smlComponents = Lists.newLinkedList();
        int childCount = 0;
        for (final SosProcedureDescription childProcedure : childProcedures) {
            childCount++;
            final SmlComponent component = new SmlComponent("component" + childCount);
            component.setTitle(childProcedure.getIdentifier());

            if (ServiceConfiguration.getInstance().isEncodeFullChildrenInDescribeSensor()
                    && childProcedure instanceof AbstractSensorML) {
                component.setProcess((AbstractSensorML) childProcedure);
            } else {
                try {
                    if (BindingRepository.getInstance().isBindingSupported(BindingConstants.KVP_BINDING_ENDPOINT)) {
                        final String version =
                                ServiceOperatorRepository.getInstance().getSupportedVersions(SosConstants.SOS)
                                        .contains(Sos2Constants.SERVICEVERSION) ? Sos2Constants.SERVICEVERSION
                                        : Sos1Constants.SERVICEVERSION;

                        component.setHref(SosHelper.getDescribeSensorUrl(version, ServiceConfiguration.getInstance()
                                .getServiceURL(), childProcedure.getIdentifier(),
                                BindingConstants.KVP_BINDING_ENDPOINT, childProcedure.getDescriptionFormat()));
                    } else {
                        component.setHref(childProcedure.getIdentifier());
                    }
                } catch (final UnsupportedEncodingException uee) {
                    throw new NoApplicableCodeException().withMessage("Error while encoding DescribeSensor URL")
                            .causedBy(uee);
                }
            }
            smlComponents.add(component);
        }
        return smlComponents;
    }

    /**
     * Get the output values from childs
     * 
     * @param smlComponents
     *            SOS component list
     * @return Child outputs
     */
    protected Collection<? extends SmlIo<?>> getOutputsFromChilds(final List<SmlComponent> smlComponents) {
        final Set<SmlIo<?>> outputs = Sets.newHashSet();
        for (final SmlComponent sosSMLComponent : smlComponents) {
            if (sosSMLComponent.isSetProcess()) {
                if (sosSMLComponent.getProcess() instanceof SensorML) {
                    final SensorML sensorML = (SensorML) sosSMLComponent.getProcess();
                    if (sensorML.isSetMembers()) {
                        for (final AbstractProcess abstractProcess : sensorML.getMembers()) {
                            if (abstractProcess.isSetOutputs()) {
                                outputs.addAll(abstractProcess.getOutputs());
                            }
                        }
                    }
                } else if (sosSMLComponent.getProcess() instanceof AbstractProcess) {
                    final AbstractProcess abstractProcess = (AbstractProcess) sosSMLComponent.getProcess();
                    if (abstractProcess.isSetOutputs()) {
                        outputs.addAll(abstractProcess.getOutputs());
                    }
                }
            }
        }
        return outputs;
    }

    /**
     * Get featureOfInterests from components
     * 
     * @param smlComponents
     *            SOS component list
     * @return Child featureOfInterests
     */
    protected Collection<String> getFeaturesFromChild(final List<SmlComponent> smlComponents) {
        final Set<String> features = Sets.newHashSet();
        for (final SmlComponent sosSMLComponent : smlComponents) {
            if (sosSMLComponent.isSetProcess() && sosSMLComponent.getProcess().isSetFeaturesOfInterest()) {
                features.addAll(sosSMLComponent.getProcess().getFeaturesOfInterest());
            }
        }
        return features;
    }
    
    protected boolean isIdentical(final XmlObject xmlObject, final XmlOptions xmlOptions,
            final XmlObject anotherXmlObject) {
        try {
            final Diff diff = new Diff(xmlObject.xmlText(xmlOptions), anotherXmlObject.xmlText(xmlOptions));
            if (diff.similar()) {
                return true;
            }
        } catch (final SAXException e) {
            // TODO Auto-generated catch block generated on 05.12.2013 around
            // 09:56:52
            LOGGER.error("Exception thrown: {}", e.getMessage(), e);
        } catch (final IOException e) {
            // TODO Auto-generated catch block generated on 05.12.2013 around
            // 09:56:52
            LOGGER.error("Exception thrown: {}", e.getMessage(), e);
        }
        return false;
    }
    
    /**
     * Create a valvalue output element name
     * 
     * @param counter
     *            Element counter
     * @param outputNames
     *            Set with otput names
     * @return Valvalue output element name
     */
    protected String getValidOutputName(final int counter, final Set<String> outputNames) {
        String outputName = OUTPUT_PREFIX + counter;
        while (outputNames.contains(outputName)) {
            outputName = OUTPUT_PREFIX + (counter + 1);
        }
        return NcNameResolver.fixNcName(outputName);
    }
    
    protected XmlOptions getOptions() {
        return XmlOptionsHelper.getInstance().getXmlOptions();
    }
    
    
}
