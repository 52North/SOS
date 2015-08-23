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


import org.n52.iceland.config.SettingDefinitionGroup;
import org.n52.iceland.config.annotation.Configurable;
import org.n52.iceland.config.annotation.Setting;
import org.n52.iceland.lifecycle.Constructable;
import org.n52.iceland.util.Validation;


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
public class ProcedureDescriptionSettings implements Constructable {

    public static final SettingDefinitionGroup GROUP = new SettingDefinitionGroup().setTitle("Procedure Description Enrichment")
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

    @Deprecated
    private static ProcedureDescriptionSettings instance = null;

    @Override
    public void init() {
        ProcedureDescriptionSettings.instance = this;
    }
    
    @Deprecated
    public static ProcedureDescriptionSettings getInstance() {
        return instance;
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
