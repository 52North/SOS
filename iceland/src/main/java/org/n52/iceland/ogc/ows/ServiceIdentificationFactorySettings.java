/**
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.ogc.ows;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import org.n52.iceland.config.SettingDefinition;
import org.n52.iceland.config.SettingDefinitionGroup;
import org.n52.iceland.config.SettingDefinitionProvider;
import org.n52.iceland.config.settings.FileSettingDefinition;
import org.n52.iceland.config.settings.MultilingualStringSettingDefinition;
import org.n52.iceland.config.settings.StringSettingDefinition;
import org.n52.iceland.i18n.MultilingualString;

import com.google.common.collect.Sets;

/**
 * Setting definitions for the OWS Service Identification.
 *
 * @author Christian Autermann <c.autermann@52north.org>
 *
 * @since 4.0.0
 */
public class ServiceIdentificationFactorySettings implements SettingDefinitionProvider {
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
