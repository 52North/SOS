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
package org.n52.sos.service;

import static java.lang.Boolean.TRUE;

import java.util.Collections;
import java.util.Set;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingDefinitionGroup;
import org.n52.sos.config.SettingDefinitionProvider;
import org.n52.sos.config.SettingsManager;
import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.config.annotation.Setting;
import org.n52.sos.config.settings.BooleanSettingDefinition;
import org.n52.sos.config.settings.StringSettingDefinition;
import org.n52.sos.util.Validation;

import com.google.common.collect.Sets;

/**
 * This class provides all settings to configure the sensor description
 * generation.
 * 
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 */
@Configurable
public class ProcedureDescriptionSettings implements SettingDefinitionProvider {

    public static final SettingDefinitionGroup GROUP = new SettingDefinitionGroup().setTitle("Procedure Description")
            .setDescription("Settings to configure the procedure description generation and enrichment feature.")
            .setOrder(4.2023f);

    public static final String IDENTIFIER_LONG_NAME_DEFINITION = "procedureDesc.IDENTIFIER_LONG_NAME_DEFINITION";

    public static final String IDENTIFIER_SHORT_NAME_DEFINITION = "procedureDesc.IDENTIFIER_SHORT_NAME_DEFINITION";

    public static final String DESCRIPTION_TEMPLATE = "procedureDesc.DESCRIPTION_TEMPLATE";

    public static final String GENERATE_CLASSIFICATION = "procedureDesc.GENERATE_CLASSIFICATION";

    public static final String CLASSIFIER_INTENDED_APPLICATION_DEFINITION =
            "procedureDesc.CLASSIFIER_INTENDED_APPLICATION_DEFINITION";

    public static final String CLASSIFIER_INTENDED_APPLICATION_VALUE =
            "procedureDesc.CLASSIFIER_INTENDED_APPLICATION_VALUE";

    public static final String CLASSIFIER_PROCEDURE_TYPE_DEFINITION =
            "procedureDesc.CLASSIFIER_PROCEDURE_TYPE_DEFINITION";

    public static final String CLASSIFIER_PROCEDURE_TYPE_VALUE = "procedureDesc.CLASSIFIER_PROCEDURE_TYPE_VALUE";

    public static final String LAT_LONG_UOM = "procedureDesc.LAT_LONG_UOM";

    public static final String ALTITUDE_UOM = "procedureDesc.ALTITUDE_UOM";

    public static final String USE_SERVICE_CONTACT_AS_PROCEDURE_CONTACT =
            "procedureDesc.USE_SERVICE_CONTACT_AS_SENSOR_CONTACT";

    public static final String PROCESS_METHOD_RULES_DEFINITION_DESCRIPTION_TEMPLATE =
            "procedureDesc.PROCESS_METHOD_RULES_DEFINITION_DESCRIPTION_TEMPLATE";

    public static final String ENRICH_WITH_OFFERINGS = "procedureDesc.ENRICH_WITH_OFFERINGS";

    public static final String ENRICH_WITH_FEATURES = "procedureDesc.ENRICH_WITH_FEATURES";

    public static final String ENRICH_WITH_DISCOVERY_INFORMATION = "procedureDesc.ENRICH_WITH_DISCOVERY_INFORMATION";

    public static final BooleanSettingDefinition ENRICH_WITH_OFFERINGS_DEFINITION =
            new BooleanSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_0)
                    .setKey(ENRICH_WITH_OFFERINGS)
                    .setDefaultValue(TRUE)
                    .setTitle("Enrich with offering information?")
                    .setDescription(
                            "If selected, the service enriches each procedure description with available offering information: "
                                    + "listing all procedure related offering ids, for example. If disabled, the returned description of "
                                    + "an DescribeSensor response might differ from the document used during the related InsertSensor call.");

    public static final BooleanSettingDefinition ENRICH_WITH_FEATURES_DEFINITION =
            new BooleanSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_1)
                    .setKey(ENRICH_WITH_FEATURES)
                    .setDefaultValue(TRUE)
                    .setTitle("Enrich with feature information?")
                    .setDescription(
                            "If selected, the service enriches each procedure description with available observed feature "
                                    + "information: listing all features observed by this procedure, for example. If disabled, "
                                    + "the returned description of an DescribeSensor response might differ from the document "
                                    + " used during the related InsertSensor call.");

    public static final BooleanSettingDefinition ENRICH_WITH_DISCOVERY_INFORMATION_DEFINITION =
            new BooleanSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_2)
                    .setKey(ENRICH_WITH_DISCOVERY_INFORMATION)
                    .setDefaultValue(TRUE)
                    .setTitle("Enrich with discovery information?")
                    .setDescription(
                            "If selected, the service enriches each procedure description with discovery relevant information"
                                    + " according to <a target=\"_blank\" href=\"https://portal.opengeospatial.org/files/?artifact_id=33284\">OGC#09-033 "
                                    + "'SensorML Profile for Discovery'</a>.");

    public static final StringSettingDefinition IDENTIFIER_LONG_NAME_DEFINITION_DEFINITION =
            new StringSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_3)
                    .setKey(IDENTIFIER_LONG_NAME_DEFINITION)
                    .setDefaultValue("urn:ogc:def:identifier:OGC:1.0:longname")
                    .setTitle("Identifier 'longname' definition")
                    .setDescription(
                            "The definition for the sml:identification holding the 'longname'. Used only if the procedure description is enriched according to OGC#09-033.");

    public static final StringSettingDefinition IDENTIFIER_SHORT_NAME_DEFINITION_DEFINITION =
            new StringSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_4)
                    .setKey(IDENTIFIER_SHORT_NAME_DEFINITION)
                    .setDefaultValue("urn:ogc:def:identifier:OGC:1.0:shortname")
                    .setTitle("Identifier 'shortname' definition")
                    .setDescription(
                            "The definition for the sml:identification holding the 'shortname'. Used only if the sensor description is enriched according to OGC#09-033.");

    public static final StringSettingDefinition DESCRIPTION_TEMPLATE_DEFINITION =
            new StringSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_5)
                    .setKey(DESCRIPTION_TEMPLATE)
                    .setDefaultValue("The '%s' with the id '%s' observes the following properties: '%s'.")
                    .setTitle("Description template")
                    .setDescription(
                            "The template used to generate a description using the sensor identifier and the observed properties "
                                    + "related. The template MUST contain '%s' three times. The first one will be replaced with 'sensor system' or "
                                    + "'procedure' depending if it's spatial or non-spatial. The second one will be replaced with the sensor id and"
                                    + " the third with a comma separated list of properties: e.g. <i>The %s with the id '%s' observes the following "
                                    + "properties: '%s'.</i>.");

    public static final BooleanSettingDefinition GENERATE_CLASSIFICATION_DEFINITION =
            new BooleanSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_6)
                    .setKey(GENERATE_CLASSIFICATION)
                    .setDefaultValue(TRUE)
                    .setTitle("Generate classification")
                    .setDescription(
                            "Should the classifiers for 'intendedApplication' and/or 'sensorType' be generated using the values from the next two settings?");

    public static final StringSettingDefinition CLASSIFIER_INTENDED_APPLICATION_DEFINITION_DEFINITION =
            new StringSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_7)
                    .setKey(CLASSIFIER_INTENDED_APPLICATION_DEFINITION)
                    .setDefaultValue("urn:ogc:def:classifier:OGC:1.0:application")
                    .setTitle("IntendedApplication definition")
                    .setDescription(
                            "The definition that will be used for all procedures/sensors of this SOS instance as definition for the classifier 'intendedApllication' if the classification generation is activated.");

    public static final StringSettingDefinition CLASSIFIER_INTENDED_APPLICATION_VALUE_DEFINITION =
            new StringSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_8)
                    .setKey(CLASSIFIER_INTENDED_APPLICATION_VALUE)
                    .setDefaultValue("")
                    .setOptional(true)
                    .setTitle("IntendedApplication Value")
                    .setDescription(
                            "The value that will be used for all procedures/sensors of this SOS instance as term for the classifier 'intendedApllication' if the classification generation is activated. In addition, if this field is <b>empty</b>, the classifier 'intendedApplication' will <b>not</b> be added.");

    public static final StringSettingDefinition CLASSIFIER_PROCEDURE_TYPE_DEFINITION_DEFINITION =
            new StringSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_9)
                    .setKey(CLASSIFIER_PROCEDURE_TYPE_DEFINITION)
                    .setDefaultValue("urn:ogc:def:classifier:OGC:1.0:procedureType")
                    .setTitle("ProcedureType definition")
                    .setDescription(
                            "The definition that will be used for all procedures/sensors of this SOS instance as definition for the classifier 'procedureType' if the classification generation is activated.");

    public static final StringSettingDefinition CLASSIFIER_PROCEDURE_TYPE_VALUE_DEFINITION =
            new StringSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_10)
                    .setKey(CLASSIFIER_PROCEDURE_TYPE_VALUE)
                    .setDefaultValue("")
                    .setOptional(true)
                    .setTitle("ProcedureType Value")
                    .setDescription(
                            "The value that will be used for all procedures of this SOS instance as term for the classifier 'procedureType' if the classification generation is activated. In addition, if this field is <b>empty</b>, the classifier 'procedureType' will <b>not</b> be added.");

    public static final BooleanSettingDefinition USE_SERVICE_CONTACT_AS_SENSOR_CONTACT_DEFINITION =
            new BooleanSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_11)
                    .setKey(USE_SERVICE_CONTACT_AS_PROCEDURE_CONTACT)
                    .setDefaultValue(TRUE)
                    .setTitle("Use service contact as procedure contact")
                    .setDescription(
                            "Should the service contact be encoded as procedure contact if procedure description enrichment is activated.");

    public static final StringSettingDefinition LAT_LONG_UOM_DEFINITION =
            new StringSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_12)
                    .setOptional(false)
                    .setKey(LAT_LONG_UOM)
                    .setDefaultValue("degree")
                    .setTitle("Latitude &amp; Longitude UOM")
                    .setDescription(
                            "The UOM for the latitude  &amp; longitude values of spatial procedures (e.g. sml:System). Something like 'degree', 'm'.");

    public static final StringSettingDefinition ALTITUDE_UOM_DEFINITION = new StringSettingDefinition()
            .setGroup(GROUP)
            .setOrder(ORDER_13)
            .setOptional(false)
            .setKey(ALTITUDE_UOM)
            .setDefaultValue("m")
            .setTitle("Altitude UOM")
            .setDescription(
                    "The UOM for the altitude value of spatial procedures (e.g. sml:System). Something like 'm'.");

    public static final StringSettingDefinition PROCESS_METHOD_RULES_DEFINITION_DESCRIPTION_TEMPLATE_DEFINITION =
            new StringSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_14)
                    .setKey(PROCESS_METHOD_RULES_DEFINITION_DESCRIPTION_TEMPLATE)
                    .setDefaultValue(
                            "The procedure '%s' generates the following output(s): '%s'. The input(s) is/are unknown (this description is generated).")
                    .setTitle("Description Template for the rules definition")
                    .setDescription(
                            "The template used to generate a description using the procedure identifier and the observed properties. "
                                    + "The template MUST contain '%s' two times. The first one will be replaced with the sensor id and"
                                    + " the second with a comma separated list of properties: e.g. <i>The procedure '%s' generates the following output(s): '%s'. The "
                                    + "input(s) is/are unknown (this description is generated).</i>");

    private static final Set<? extends SettingDefinition<?, ?>> DEFINITIONS = Sets
            .<SettingDefinition<?, ?>> newHashSet(ENRICH_WITH_OFFERINGS_DEFINITION, ENRICH_WITH_FEATURES_DEFINITION,
                    ENRICH_WITH_DISCOVERY_INFORMATION_DEFINITION, IDENTIFIER_LONG_NAME_DEFINITION_DEFINITION,
                    IDENTIFIER_SHORT_NAME_DEFINITION_DEFINITION, DESCRIPTION_TEMPLATE_DEFINITION,
                    GENERATE_CLASSIFICATION_DEFINITION, CLASSIFIER_INTENDED_APPLICATION_DEFINITION_DEFINITION,
                    CLASSIFIER_INTENDED_APPLICATION_VALUE_DEFINITION, CLASSIFIER_PROCEDURE_TYPE_DEFINITION_DEFINITION,
                    CLASSIFIER_PROCEDURE_TYPE_VALUE_DEFINITION, USE_SERVICE_CONTACT_AS_SENSOR_CONTACT_DEFINITION,
                    LAT_LONG_UOM_DEFINITION, ALTITUDE_UOM_DEFINITION,
                    PROCESS_METHOD_RULES_DEFINITION_DESCRIPTION_TEMPLATE_DEFINITION);

    private String descriptionTemplate;
    private boolean generateClassification;
    private String classifierIntendedApplicationValue;
    private String classifierIntendedApplicationDefinition;
    private String classifierProcedureTypeValue;
    private String classifierProcedureTypeDefinition;
    private boolean useServiceContactAsProcedureContact;
    private String identifierShortNameDefinition;
    private String identifierLongNameDefinition;
    private String latLongUom;
    private String altitudeUom;
    private String processMethodRulesDefinitionDescriptionTemplate;
    private boolean enrichWithOfferings;
    private boolean enrichWithFeatures;
    private boolean enrichWithDiscoveryInformation;

    private static ProcedureDescriptionSettings instance = null;

    public static synchronized ProcedureDescriptionSettings getInstance() {
        if (instance == null) {
            instance = new ProcedureDescriptionSettings();
            SettingsManager.getInstance().configure(instance);
        }
        return instance;
    }

    @Override
    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
        return Collections.unmodifiableSet(DEFINITIONS);
    }

    @Setting(DESCRIPTION_TEMPLATE)
    public void setDescriptionTemplate(final String descriptionTemplate) {
        Validation.notNullOrEmpty(DESCRIPTION_TEMPLATE, descriptionTemplate);
        this.descriptionTemplate = descriptionTemplate;
    }

    /**
     * @return Depends on configuration. Something like:<br>
     *         "<i>The '%s' with the id '%s' observes the following properties:
     *         '%s'.</i>"
     */
    public String getDescriptionTemplate() {
        return descriptionTemplate;
    }

    public boolean isGenerateClassification() {
        return generateClassification;
    }

    @Setting(GENERATE_CLASSIFICATION)
    public void setGenerateClassification(final boolean generateClassification) {
        this.generateClassification = generateClassification;
    }

    public String getClassifierIntendedApplicationValue() {
        return classifierIntendedApplicationValue;
    }

    @Setting(CLASSIFIER_INTENDED_APPLICATION_VALUE)
    public void setClassifierIntendedApplicationValue(final String classifierIntendedApplicationValue) {
        this.classifierIntendedApplicationValue =
                (classifierIntendedApplicationValue == null) ? "" : classifierIntendedApplicationValue;
    }

    public String getClassifierIntendedApplicationDefinition() {
        return classifierIntendedApplicationDefinition;
    }

    @Setting(CLASSIFIER_INTENDED_APPLICATION_DEFINITION)
    public void setClassifierIntendedApplicationDefinition(final String classifierIntendedApplicationDefinition) {
        Validation.notNull(CLASSIFIER_INTENDED_APPLICATION_DEFINITION, classifierIntendedApplicationDefinition);
        this.classifierIntendedApplicationDefinition = classifierIntendedApplicationDefinition;
    }

    public String getClassifierProcedureTypeDefinition() {
        return classifierProcedureTypeDefinition;
    }

    @Setting(CLASSIFIER_PROCEDURE_TYPE_DEFINITION)
    public void setClassifierProcedureTypeDefinition(final String classifierProcedureTypeDefinition) {
        Validation.notNull(CLASSIFIER_PROCEDURE_TYPE_DEFINITION, classifierProcedureTypeDefinition);
        this.classifierProcedureTypeDefinition = classifierProcedureTypeDefinition;
    }

    public String getClassifierProcedureTypeValue() {
        return classifierProcedureTypeValue;
    }

    @Setting(CLASSIFIER_PROCEDURE_TYPE_VALUE)
    public void setClassifierProcedureTypeValue(final String classifierProcedureTypeValue) {
        this.classifierProcedureTypeValue = (classifierProcedureTypeValue == null) ? "" : classifierProcedureTypeValue;
    }

    public boolean isUseServiceContactAsProcedureContact() {
        return useServiceContactAsProcedureContact;
    }

    @Setting(USE_SERVICE_CONTACT_AS_PROCEDURE_CONTACT)
    public void setUseServiceContactAsProcedureContact(final boolean useServiceContactAsProcedureContact) {
        Validation.notNull(USE_SERVICE_CONTACT_AS_PROCEDURE_CONTACT, useServiceContactAsProcedureContact);
        this.useServiceContactAsProcedureContact = useServiceContactAsProcedureContact;
    }

    @Setting(IDENTIFIER_SHORT_NAME_DEFINITION)
    public void setIdentifierShortNameDefinition(final String identifierShortNameDefinition) {
        Validation.notNullOrEmpty(IDENTIFIER_SHORT_NAME_DEFINITION, identifierShortNameDefinition);
        this.identifierShortNameDefinition = identifierShortNameDefinition;
    }

    public String getIdentifierShortNameDefinition() {
        return identifierShortNameDefinition;
    }

    @Setting(IDENTIFIER_LONG_NAME_DEFINITION)
    public void setIdentifierLongNameDefinition(final String identifierLongNameDefinition) {
        Validation.notNullOrEmpty(IDENTIFIER_LONG_NAME_DEFINITION, identifierLongNameDefinition);
        this.identifierLongNameDefinition = identifierLongNameDefinition;
    }

    public String getIdentifierLongNameDefinition() {
        return identifierLongNameDefinition;
    }

    @Setting(LAT_LONG_UOM)
    public void setLatitudeUom(final String latLongUom) {
        this.latLongUom = latLongUom;
    }

    public String getLatLongUom() {
        return latLongUom;
    }

    @Setting(ALTITUDE_UOM)
    public void setAltitudeUom(final String altitudeUom) {
        this.altitudeUom = altitudeUom;
    }

    public String getAltitudeUom() {
        return altitudeUom;
    }

    @Setting(PROCESS_METHOD_RULES_DEFINITION_DESCRIPTION_TEMPLATE)
    public void setProcessMethodRulesDefinitionDescriptionTemplate(
            final String processMethodRulesDefinitionDescriptionTemplate) {
        Validation.notNullOrEmpty(PROCESS_METHOD_RULES_DEFINITION_DESCRIPTION_TEMPLATE,
                processMethodRulesDefinitionDescriptionTemplate);
        this.processMethodRulesDefinitionDescriptionTemplate = processMethodRulesDefinitionDescriptionTemplate;
    }

    /**
     * @return Depends on configuration. Something like:<br>
     *         "<i>The procedure '%s' generates the following outputs: '%s'. The inputs are unknown (this description is generated).</i>"
     */
    public String getProcessMethodRulesDefinitionDescriptionTemplate() {
        return processMethodRulesDefinitionDescriptionTemplate;
    }

    @Setting(ENRICH_WITH_OFFERINGS)
    public void setEnrichWithOfferings(final boolean enrichWithOfferings) {
        Validation.notNull(ENRICH_WITH_OFFERINGS, enrichWithOfferings);
        this.enrichWithOfferings = enrichWithOfferings;
    }

    public boolean isEnrichWithOfferings() {
        return enrichWithOfferings;
    }

    @Setting(ENRICH_WITH_FEATURES)
    public void setEnrichWithFeatures(final boolean enrichWithFeatures) {
        Validation.notNull(ENRICH_WITH_FEATURES, enrichWithFeatures);
        this.enrichWithFeatures = enrichWithFeatures;
    }

    public boolean isEnrichWithFeatures() {
        return enrichWithFeatures;
    }

    @Setting(ENRICH_WITH_DISCOVERY_INFORMATION)
    public void setEnrichWithDiscoveryInformation(final boolean enrichWithDiscoveryInformation) {
        Validation.notNull(ENRICH_WITH_DISCOVERY_INFORMATION, enrichWithDiscoveryInformation);
        this.enrichWithDiscoveryInformation = enrichWithDiscoveryInformation;
    }

    public boolean isEnrichWithDiscoveryInformation() {
        return enrichWithDiscoveryInformation;
    }

}
