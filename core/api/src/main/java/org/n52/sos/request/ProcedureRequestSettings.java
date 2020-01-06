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
package org.n52.sos.request;

import static java.lang.Boolean.FALSE;

import java.util.Collections;
import java.util.Set;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingDefinitionGroup;
import org.n52.sos.config.SettingDefinitionProvider;
import org.n52.sos.config.SettingsManager;
import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.config.annotation.Setting;
import org.n52.sos.config.settings.BooleanSettingDefinition;

import com.google.common.collect.ImmutableSet;

/**
 * {@link SettingDefinitionProvider} for procedure request/response handling
 * 
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
@Configurable
public class ProcedureRequestSettings implements SettingDefinitionProvider {
    
    public static final String ALLOW_QUERYING_FOR_INSTANCES_ONLY = "request.procedure.instancesOnly";
    
    public static final String SHOW_ONLY_AGGREGATED_PROCEDURES = "request.procedure.aggregationOnly";
    
    public static final String ENCODE_FULL_CHILDREN_IN_DESCRIBE_SENSOR = "service.encodeFullChildrenInDescribeSensor";
    
    public static final String ADD_OUTPUTS_TO_SENSOR_ML = "service.addOutputsToSensorML";
    
    private boolean allowQueryingForInstancesOnly;
    
    private boolean showOnlyAggregatedProcedures;
    
    private boolean encodeFullChildrenInDescribeSensor;
    
    private boolean addOutputsToSensorML;
    
    public static final SettingDefinitionGroup GROUP = new SettingDefinitionGroup().setTitle("Procedure request/response handling")
            .setDescription("Settings to configure the procedure request/response handling, e.g. which procedures should be queryable.")
            .setOrder(ORDER_11);

    private static final Set<SettingDefinition<?, ?>> DEFINITIONS = ImmutableSet
            .<SettingDefinition<?, ?>> of(new BooleanSettingDefinition()
                            .setGroup(GROUP)
                            .setOrder(ORDER_0)
                            .setKey(ALLOW_QUERYING_FOR_INSTANCES_ONLY)
                            .setDefaultValue(false)
                            .setTitle("Allow requesting only for procedure instances")
                            .setDescription(
                                    "Allow requesting only for procedure instances, procedure types (without observations) are not queryable"),
            
                    new BooleanSettingDefinition()
                            .setGroup(GROUP)
                            .setOrder(ORDER_1)
                            .setKey(SHOW_ONLY_AGGREGATED_PROCEDURES)
                            .setDefaultValue(FALSE)
                            .setTitle("Show and query only aggregated procedures")
                            .setDescription(
                                    "Show and query only aggregated procedures or also components in capabilities."),
                                    
                    new BooleanSettingDefinition()
                            .setGroup(GROUP)
                            .setOrder(ORDER_2)
                            .setKey(ENCODE_FULL_CHILDREN_IN_DESCRIBE_SENSOR)
                            .setDefaultValue(true)
                            .setTitle("Encode full for child procedure SensorML in parent DescribeSensor responses")
                            .setDescription(
                                    "Whether to encode full SensorML for each child procedures in a DescribeSensor response for a parent procedure."),
                    
                  new BooleanSettingDefinition()
                            .setGroup(GROUP)
                            .setOrder(ORDER_3)
                            .setKey(ADD_OUTPUTS_TO_SENSOR_ML)
                            .setDefaultValue(true)
                            .setTitle("Add outputs to DescribeSensor SensorML responses")
                            .setDescription(
                                    "Whether to query example observations and dynamically add outputs to DescribeSensor SensorML responses.")
                    );
    
    private static ProcedureRequestSettings instance = null;

    public static synchronized ProcedureRequestSettings getInstance() {
        if (instance == null) {
            instance = new ProcedureRequestSettings();
            SettingsManager.getInstance().configure(instance);
        }
        return instance;
    }
    
    @Override
    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
        return Collections.unmodifiableSet(DEFINITIONS);
    }
    
    /**
     * @return the allowQueryingForInstancesOnly
     */
    public boolean isAllowQueryingForInstancesOnly() {
        return allowQueryingForInstancesOnly;
    }

    /**
     * @param allowQueryingForInstancesOnly the allowQueryingForInstancesOnly to set
     */
    @Setting(ALLOW_QUERYING_FOR_INSTANCES_ONLY)
    public void setAllowQueryingForInstancesOnly(boolean allowQueryingForInstancesOnly) {
        this.allowQueryingForInstancesOnly = allowQueryingForInstancesOnly;
    }

    /**
     * @return the showOnlyAggregatedProcedures
     */
    public boolean isShowOnlyAggregatedProcedures() {
        return showOnlyAggregatedProcedures;
    }

    /**
     * @param showOnlyAggregatedProcedures the showOnlyAggregatedProcedures to set
     */
    /**
     * @param showOnlyAggregatedProcedures
     */
    @Setting(SHOW_ONLY_AGGREGATED_PROCEDURES)
    public void setShowOnlyAggregatedProcedures(boolean showOnlyAggregatedProcedures) {
        this.showOnlyAggregatedProcedures = showOnlyAggregatedProcedures;
    }
    
    public boolean isEncodeFullChildrenInDescribeSensor() {
        return encodeFullChildrenInDescribeSensor;
    }

    @Setting(ENCODE_FULL_CHILDREN_IN_DESCRIBE_SENSOR)
    public void setEncodeFullChildrenInDescribeSensor(final boolean encodeFullChildrenInDescribeSensor) {
        this.encodeFullChildrenInDescribeSensor = encodeFullChildrenInDescribeSensor;
    }
    
    public boolean isAddOutputsToSensorML() {
        return addOutputsToSensorML;
    }

    @Setting(ADD_OUTPUTS_TO_SENSOR_ML)
    public void setAddOutputsToSensorML(final boolean addOutputsToSensorML) {
        this.addOutputsToSensorML = addOutputsToSensorML;
    }

}
