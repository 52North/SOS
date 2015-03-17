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
package org.n52.sos.ogc.ows;

import org.n52.sos.i18n.MultilingualString;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingDefinitionGroup;
import org.n52.sos.config.SettingDefinitionProvider;
import org.n52.sos.config.settings.FileSettingDefinition;
import org.n52.sos.config.settings.MultilingualStringSettingDefinition;
import org.n52.sos.config.settings.StringSettingDefinition;

import com.google.common.collect.Sets;

/**
 * Setting definitions for the OWS Service Identification.
 *
 * @author Christian Autermann <c.autermann@52north.org>
 *
 * @since 4.0.0
 */
public class SosServiceIdentificationFactorySettings implements SettingDefinitionProvider {
    public static final String SERVICE_TYPE = "serviceIdentification.serviceType";

    public static final String SERVICE_TYPE_CODE_SPACE = "serviceIdentification.serviceTypeCodeSpace";

    public static final String ACCESS_CONSTRAINTS = "serviceIdentification.accessConstraints";

    public static final String FILE = "serviceIdentification.file";

    public static final String TITLE = "serviceIdentification.title";

    public static final String KEYWORDS = "serviceIdentification.keywords";

    public static final String ABSTRACT = "serviceIdentification.abstract";

    public static final String FEES = "serviceIdentification.fees";

    public static final SettingDefinitionGroup GROUP = new SettingDefinitionGroup().setTitle("Service Identification")
            .setOrder(1);

    public static final MultilingualStringSettingDefinition TITLE_DEFINITION = new MultilingualStringSettingDefinition().setGroup(GROUP)
            .setOrder(ORDER_1).setKey(TITLE).setTitle("Title").setDescription("SOS Service Title.")
            .setDefaultValue(new MultilingualString().addLocalization(Locale.ENGLISH, "52N SOS"));

    public static final StringSettingDefinition KEYWORDS_DEFINITION = new StringSettingDefinition().setGroup(GROUP)
            .setOrder(ORDER_2).setKey(KEYWORDS).setTitle("Keywords")
            .setDescription("Comma separated SOS service keywords.").setOptional(true);

    public static final MultilingualStringSettingDefinition ABSTRACT_DEFINITION = new MultilingualStringSettingDefinition().setGroup(GROUP)
            .setOrder(ORDER_3).setKey(ABSTRACT).setTitle("SOS Abstract").setDescription("SOS service abstract.")
            .setDefaultValue(new MultilingualString().addLocalization(Locale.ENGLISH, "52North Sensor Observation Service - Data Access for the Sensor Web"));

    public static final StringSettingDefinition ACCESS_CONSTRAINTS_DEFINITION = new StringSettingDefinition()
            .setGroup(GROUP).setOrder(ORDER_4).setKey(ACCESS_CONSTRAINTS).setTitle("Access Constraints")
            .setDescription("Service access constraints.").setDefaultValue("NONE").setOptional(true);

    public static final StringSettingDefinition FEES_DEFINITION = new StringSettingDefinition().setGroup(GROUP)
            .setOrder(ORDER_5).setKey(FEES).setTitle("Fees").setDescription("SOS Service Fees.")
            .setDefaultValue("NONE").setOptional(true);

    public static final StringSettingDefinition SERVICE_TYPE_DEFINITION = new StringSettingDefinition()
            .setGroup(GROUP).setOrder(ORDER_6).setKey(SERVICE_TYPE).setTitle("Service Type")
            .setDescription("SOS Service Type.").setDefaultValue("OGC:SOS");

    public static final StringSettingDefinition SERVICE_TYPE_CODE_SPACE_DEFINITION = new StringSettingDefinition()
            .setGroup(GROUP).setOrder(ORDER_7).setKey(SERVICE_TYPE_CODE_SPACE).setTitle("Service Type Code Space")
            .setDescription("SOS Service Type Code Space.").setOptional(true);

    public static final FileSettingDefinition FILE_DEFINITION = new FileSettingDefinition()
            .setGroup(GROUP)
            .setOrder(ORDER_8)
            .setKey(FILE)
            .setTitle("Service Identification File")
            .setOptional(true)
            .setDescription(
                    "The path to a file containing an ows:ServiceIdentification"
                            + " overriding the above settings. It can be either an absolute path "
                            + "(like <code>/home/user/sosconfig/identification.xml</code>) or a path "
                            + "relative to the web application directory "
                            + "(e.g. <code>WEB-INF/identification.xml</code>).");

    private static final Set<SettingDefinition<?, ?>> DEFINITIONS = Sets.<SettingDefinition<?, ?>> newHashSet(
             TITLE_DEFINITION, ABSTRACT_DEFINITION, SERVICE_TYPE_DEFINITION,
            SERVICE_TYPE_CODE_SPACE_DEFINITION, KEYWORDS_DEFINITION,
            FEES_DEFINITION, ACCESS_CONSTRAINTS_DEFINITION,
            FILE_DEFINITION);

    @Override
    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
        return Collections.unmodifiableSet(DEFINITIONS);
    }
}
