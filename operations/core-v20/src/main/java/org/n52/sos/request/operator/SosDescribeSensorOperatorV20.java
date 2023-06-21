/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.binding.BindingRepository;
import org.n52.janmayen.http.MediaTypes;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.CodeType;
import org.n52.shetland.ogc.ows.exception.CompositeOwsException;
import org.n52.shetland.ogc.ows.exception.MissingParameterValueException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.service.OwsServiceResponse;
import org.n52.shetland.ogc.sensorML.AbstractProcess;
import org.n52.shetland.ogc.sensorML.AbstractSensorML;
import org.n52.shetland.ogc.sensorML.HasComponents;
import org.n52.shetland.ogc.sensorML.SensorML;
import org.n52.shetland.ogc.sensorML.SensorML20Constants;
import org.n52.shetland.ogc.sensorML.SensorMLConstants;
import org.n52.shetland.ogc.sensorML.elements.SmlCapabilities;
import org.n52.shetland.ogc.sensorML.elements.SmlCapabilitiesPredicates;
import org.n52.shetland.ogc.sensorML.elements.SmlComponent;
import org.n52.shetland.ogc.sensorML.elements.SmlIo;
import org.n52.shetland.ogc.sensorML.v20.AbstractProcessV20;
import org.n52.shetland.ogc.sensorML.v20.SmlFeatureOfInterest;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.SosOffering;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.shetland.ogc.sos.request.DescribeSensorRequest;
import org.n52.shetland.ogc.sos.response.DescribeSensorResponse;
import org.n52.shetland.ogc.swe.DataRecord;
import org.n52.shetland.ogc.swe.SweField;
import org.n52.shetland.ogc.swe.SweSimpleDataRecord;
import org.n52.shetland.ogc.swe.simpleType.SweText;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.ds.AbstractDescribeSensorHandler;
import org.n52.sos.util.SosHelper;
import org.n52.sos.wsdl.Metadata;
import org.n52.sos.wsdl.Metadatas;
import org.n52.svalbard.ConformanceClasses;
import org.n52.svalbard.encode.exception.EncodingException;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * class handles the DescribeSensor request
 *
 * @since 4.0.0
 *
 */
@Configurable
@SuppressFBWarnings({"EI_EXPOSE_REP2"})
public class SosDescribeSensorOperatorV20 extends
        AbstractV2RequestOperator<AbstractDescribeSensorHandler, DescribeSensorRequest, DescribeSensorResponse> {

    public static final String ENCODE_FULL_CHILDREN_IN_DESCRIBE_SENSOR = "service.encodeFullChildrenInDescribeSensor";

    private static final String OPERATION_NAME = SosConstants.Operations.DescribeSensor.name();

    private static final Set<String> CONFORMANCE_CLASSES =
            Collections.singleton(ConformanceClasses.SOS_V2_CORE_PROFILE);

    private PostProcessor postProcessor;

    private BindingRepository bindingRepository;

    private SosHelper sosHelper;

    private boolean encodeFullChildrenInDescribeSensor;

    public SosDescribeSensorOperatorV20() {
        super(OPERATION_NAME, DescribeSensorRequest.class);
        postProcessor = new PostProcessor();
    }

    private BindingRepository getBindingRepository() {
        return bindingRepository;
    }

    @Inject
    public void setBindingRepository(BindingRepository bindingRepository) {
        this.bindingRepository = bindingRepository;
    }

    @Inject
    public void setSosHelperL(SosHelper sosHelper) {
        this.sosHelper = sosHelper;
    }

    private String getServiceURL() {
        return sosHelper.getServiceURL();
    }

    @Setting(ENCODE_FULL_CHILDREN_IN_DESCRIBE_SENSOR)
    public void setEncodeFullChildrenInDescribeSensor(final boolean encodeFullChildrenInDescribeSensor) {
        this.encodeFullChildrenInDescribeSensor = encodeFullChildrenInDescribeSensor;
    }

    private boolean isEncodeFullChildrenInDescribeSensor() {
        return encodeFullChildrenInDescribeSensor;
    }

    @Override
    public Set<String> getConformanceClasses(String service, String version) {
        if (SosConstants.SOS.equals(service) && Sos2Constants.SERVICEVERSION.equals(version)) {
            return Collections.unmodifiableSet(CONFORMANCE_CLASSES);
        }
        return Collections.emptySet();
    }

    @Override
    public DescribeSensorResponse receiveSensorDescription(DescribeSensorRequest request) throws OwsExceptionReport {
        return getOperationHandler().getSensorDescription(request);
        // TODO check if sensor description position/location/observedArea
        // should be transformed (CRS support)
    }

    @Override
    protected void checkParameters(DescribeSensorRequest sosRequest) throws OwsExceptionReport {
        CompositeOwsException exceptions = new CompositeOwsException();
        try {
            checkServiceParameter(sosRequest.getService());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkSingleVersionParameter(sosRequest);
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkProcedure(sosRequest.getProcedure(), SosConstants.DescribeSensorParams.procedure.name());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkProcedureDescriptionFromat(sosRequest.getProcedureDescriptionFormat(), sosRequest);
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        // if (sosRequest.isSetValidTime()) {
        // exceptions.add(new
        // OptionNotSupportedException().at(Sos2Constants.DescribeSensorParams.validTime)
        // .withMessage("The requested parameter is not supported by this
        // server!"));
        // }
        checkExtensions(sosRequest, exceptions);
        exceptions.throwIfNotEmpty();
    }

    @Override
    public Metadata getSosOperationDefinition() {
        return Metadatas.DESCRIBE_SENSOR;
    }

    @Override
    protected OwsServiceResponse postProcessResponse(DescribeSensorResponse response) {
        return super.postProcessResponse(postProcessor.process(response));
    }

    private void checkProcedureDescriptionFromat(String procedureDescriptionFormat, DescribeSensorRequest sosRequest)
            throws MissingParameterValueException, OwsExceptionReport {
        if (!checkOnlyRequestableProcedureDescriptionFromats(sosRequest.getProcedureDescriptionFormat(),
                Sos2Constants.DescribeSensorParams.procedureDescriptionFormat, false)) {
            checkProcedureDescriptionFormat(sosRequest.getProcedureDescriptionFormat(), sosRequest.getService(),
                    sosRequest.getVersion());
        }
    }

    private class PostProcessor {

        public DescribeSensorResponse process(DescribeSensorResponse response) {
            if (response.isSetProcedureDescriptions()) {
                for (SosProcedureDescription<?> procedureDescription : response.getProcedureDescriptions()) {
                    if (procedureDescription.getProcedureDescription() instanceof AbstractProcess) {
                        AbstractProcess abstractProcess =
                                (AbstractProcess) procedureDescription.getProcedureDescription();
                        if (abstractProcess.isSetOutputs()) {
                            extendOutputs(procedureDescription, abstractProcess);
                        }
                        if (SensorMLConstants.NS_SML.equalsIgnoreCase(response.getOutputFormat())) {
                            addSpecialCapabilities(procedureDescription, abstractProcess);
                        }
                        if (SensorML20Constants.NS_SML_20.equalsIgnoreCase(response.getOutputFormat())) {
                            if (procedureDescription.isSetOfferings()) {
                                final Set<SweText> offeringsSet =
                                        convertOfferingsToSet(procedureDescription.getOfferings());
                                mergeCapabilities(abstractProcess, SensorMLConstants.ELEMENT_NAME_OFFERINGS,
                                        SensorMLConstants.OFFERING_FIELD_DEFINITION, null, offeringsSet);
                            }
                            if (procedureDescription.isSetFeaturesOfInterest()
                                    || procedureDescription.isSetFeaturesOfInterestMap()) {
                                if (abstractProcess instanceof AbstractProcessV20) {
                                    AbstractProcessV20 abstractProcessV20 = (AbstractProcessV20) abstractProcess;
                                    SmlFeatureOfInterest smlFeatureOfInterest;
                                    if (!abstractProcessV20.isSetSmlFeatureOfInterest()) {
                                        smlFeatureOfInterest = new SmlFeatureOfInterest();
                                        smlFeatureOfInterest
                                                .setDefinition(SensorMLConstants.FEATURE_OF_INTEREST_FIELD_DEFINITION)
                                                .setLabel(SensorMLConstants.ELEMENT_NAME_FEATURES_OF_INTEREST);
                                        abstractProcessV20.setSmlFeatureOfInterest(smlFeatureOfInterest);
                                    } else {
                                        smlFeatureOfInterest = abstractProcessV20.getSmlFeatureOfInterest();
                                    }
                                    if (procedureDescription.isSetFeaturesOfInterestMap()) {
                                        smlFeatureOfInterest.addFeaturesOfInterest(
                                                procedureDescription.getFeaturesOfInterestMap());
                                    } else if (procedureDescription.isSetFeaturesOfInterest()) {
                                        smlFeatureOfInterest
                                                .addFeaturesOfInterest(procedureDescription.getFeaturesOfInterest());
                                    }
                                }
                            }
                        }
                        if (procedureDescription.isSetChildProcedures() && abstractProcess instanceof HasComponents) {
                            List<SmlComponent> smlComponents = Lists.newArrayList();
                            smlComponents.addAll(
                                    createComponentsForChildProcedures(procedureDescription.getChildProcedures()));
                            abstractProcess.getOutputs().addAll(getOutputsFromChilds(smlComponents));
                            ((HasComponents) abstractProcess).addComponents(smlComponents);
                        }
                    } else if (procedureDescription.getProcedureDescription() instanceof SensorML) {
                        for (AbstractProcess abstractProcess : ((SensorML) procedureDescription
                                .getProcedureDescription()).getMembers()) {
                            addSpecialCapabilities(procedureDescription, abstractProcess);
                        }
                    }
                }
            }
            return response;
        }

        /**
         * Add special capabilities to abstract process:
         * <ul>
         * <li>featureOfInterest,</li>
         * <li>sosOfferings,</li>
         * <li>parentProcedures</li>
         * </ul>
         * but only, if available.
         *
         * @param procedureDescription
         *
         * @param abstractProcess
         *            SOS abstract process.
         */
        private void addSpecialCapabilities(SosProcedureDescription<?> procedureDescription,
                AbstractProcess abstractProcess) {
            if (procedureDescription.isSetFeaturesOfInterestMap()) {
                final Set<SweText> featureSet = convertFeaturesToSet(procedureDescription.getFeaturesOfInterestMap());
                mergeCapabilities(abstractProcess, SensorMLConstants.ELEMENT_NAME_FEATURES_OF_INTEREST,
                        SensorMLConstants.FEATURE_OF_INTEREST_FIELD_DEFINITION,
                        SensorMLConstants.FEATURE_OF_INTEREST_FIELD_NAME, featureSet);
            } else {
                if (procedureDescription.isSetFeaturesOfInterest()) {
                    final Map<String, String> valueNamePairs =
                            createValueNamePairs(SensorMLConstants.FEATURE_OF_INTEREST_FIELD_NAME,
                                    procedureDescription.getFeaturesOfInterest());
                    mergeCapabilities(abstractProcess, SensorMLConstants.ELEMENT_NAME_FEATURES_OF_INTEREST,
                            SensorMLConstants.FEATURE_OF_INTEREST_FIELD_DEFINITION, valueNamePairs);
                }
            }
            if (procedureDescription.isSetOfferings()) {
                final Set<SweText> offeringsSet = convertOfferingsToSet(procedureDescription.getOfferings());
                mergeCapabilities(abstractProcess, SensorMLConstants.ELEMENT_NAME_OFFERINGS,
                        SensorMLConstants.OFFERING_FIELD_DEFINITION, null, offeringsSet);
            }
            if (procedureDescription.isSetParentProcedure()) {
                final Map<String, String> valueNamePairs =
                        createValueNamePairs(SensorMLConstants.PARENT_PROCEDURE_FIELD_NAME,
                                Sets.newHashSet(procedureDescription.getParentProcedure().getTitleOrFromHref()));
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
                additionalFields.forEach(dataRecord::addField);
            } else {
                if (capabilities.isPresent()) {
                    process.removeCapabilities(capabilities.get());
                }
                // create new capabilities
                process.addCapabilities(createCapabilitiesFrom(capabilitiesName, definition, valueNamePairs));
            }
        }

        private void mergeCapabilities(final AbstractProcess process, final String capabilitiesName,
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
                        // update the definition if not present
                        if (!sweText.isSetDefinition()) {
                            sweText.setDefinition(definition);
                        }
                        Set<SweText> fieldsToRemove = Sets.newHashSet();
                        for (SweText sweTextField : sweTextFieldSet) {
                            if (sweText.getValue().equals(sweTextField.getValue())) {
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
                createCapabilitiesFieldsFrom(definition, fieldName, sweTextFieldSet).forEach(dataRecord::addField);
            } else {
                if (capabilities.isPresent()) {
                    process.removeCapabilities(capabilities.get());
                }
                // create new capabilities
                process.addCapabilities(
                        createCapabilitiesFrom(capabilitiesName, definition, fieldName, sweTextFieldSet));
            }
        }

        /**
         * Convert SOS sosOfferings to map with key == identifier and value =
         * name
         *
         * @param offerings
         *            SOS sosOfferings
         * @return Map with identifier, name.
         */
        private Map<String, String> convertOfferingsToMap(final Set<SosOffering> offerings) {
            final Map<String, String> valueNamePairs = Maps.newHashMapWithExpectedSize(offerings.size());
            offerings.forEach(offering -> valueNamePairs.put(offering.getIdentifier(), offering.getOfferingName()));
            return valueNamePairs;
        }

        /**
         * Convert SOS sosOfferings to map with key == identifier and value =
         * name
         *
         * @param map
         *            .values() SOS sosOfferings
         * @return Set with identifier, name.
         */
        private Set<SweText> convertFeaturesToSet(final Map<String, AbstractFeature> map) {
            final Set<SweText> featureSet = Sets.newHashSetWithExpectedSize(map.values().size());
            for (final AbstractFeature abstractFeature : map.values()) {
                SweText sweText = new SweText();
                sweText.setValue(abstractFeature.getIdentifier());
                abstractFeature.getName().forEach(sweText::addName);
                if (abstractFeature.isSetDescription()) {
                    sweText.setDescription(abstractFeature.getDescription());
                }
                featureSet.add(sweText);
            }
            return featureSet;
        }

        /**
         * Convert SOS sosOfferings to map with key == identifier and value =
         * name
         *
         * @param offerings
         *            SOS sosOfferings
         * @return Set with identifier, name.
         */
        private Set<SweText> convertOfferingsToSet(final Set<SosOffering> offerings) {
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

        private Map<String, String> createValueNamePairs(final String fieldName, final Collection<String> values) {
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
        private SmlCapabilities createCapabilitiesFrom(final String capabilitiesName, final String fieldDefinition,
                final Map<String, String> valueNamePairs) {
            final SmlCapabilities capabilities = new SmlCapabilities();
            capabilities.setName(capabilitiesName);
            final SweSimpleDataRecord simpleDataRecord = new SweSimpleDataRecord();
            final List<SweField> fields = createCapabilitiesFieldsFrom(fieldDefinition, valueNamePairs);
            capabilities.setDataRecord(simpleDataRecord.setFields(fields));
            return capabilities;
        }

        private SmlCapabilities createCapabilitiesFrom(final String capabilitiesName, final String fieldDefinition,
                final String fieldName, final Set<SweText> sweTextSet) {
            final SmlCapabilities capabilities = new SmlCapabilities();
            capabilities.setName(capabilitiesName);
            final SweSimpleDataRecord simpleDataRecord = new SweSimpleDataRecord();
            simpleDataRecord.setName(capabilitiesName);
            final List<SweField> fields = createCapabilitiesFieldsFrom(fieldDefinition, fieldName, sweTextSet);
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

        private List<SweField> createCapabilitiesFieldsFrom(final String fieldElementDefinition,
                final String fieldName, final Set<SweText> sweTextSet) {
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
                        name = SensorMLConstants.DEFAULT_FIELD_NAME + Integer.toString(values.indexOf(text));
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
         * @param set
         *            Chile procedure descriptions
         * @return SOS component list
         * @throws EncodingException
         *             If an error occurs
         */
        private List<SmlComponent> createComponentsForChildProcedures(final Set<AbstractSensorML> set) {
            final List<SmlComponent> smlComponents = Lists.newLinkedList();
            int childCount = 0;
            for (final AbstractSensorML childProcedure : set) {
                childCount++;
                final SmlComponent component = new SmlComponent("component" + childCount);
                component.setTitle(childProcedure.getIdentifier());

                if (isEncodeFullChildrenInDescribeSensor()) {
                    component.setProcess(childProcedure);
                } else {
                    if (getBindingRepository().isBindingSupported(MediaTypes.APPLICATION_KVP)) {
                        try {
                            String version = getServiceOperatorRepository().getSupportedVersions(SosConstants.SOS)
                                    .contains(Sos2Constants.SERVICEVERSION) ? Sos2Constants.SERVICEVERSION
                                            : Sos1Constants.SERVICEVERSION;
                            String pdf = childProcedure.getDefaultElementEncoding();
                            component.setHref(SosHelper.getDescribeSensorUrl(version, getServiceURL(),
                                    childProcedure.getIdentifier(), pdf).toString());
                        } catch (MalformedURLException uee) {
                            component.setHref(childProcedure.getIdentifier());
                        }
                    } else {
                        component.setHref(childProcedure.getIdentifier());
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
        private Collection<SmlIo> getOutputsFromChilds(final List<SmlComponent> smlComponents) {
            final Set<SmlIo> outputs = Sets.newHashSet();
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

        private void extendOutputs(SosProcedureDescription<?> procedureDescription, AbstractProcess abstractProcess) {
            if (procedureDescription.isSetPhenomenon()) {
                abstractProcess.getOutputs().stream()
                        .filter(output -> procedureDescription.hasPhenomenonFor(output.getIoValue().getDefinition()))
                        .forEach(output -> output.getIoValue().setName(
                                procedureDescription.getPhenomenonFor(output.getIoValue().getDefinition()).getName()));
            }
        }
    }
}
