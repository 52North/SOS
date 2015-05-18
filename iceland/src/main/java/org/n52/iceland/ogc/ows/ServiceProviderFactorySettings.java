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

import java.net.URI;
import java.util.Collections;
import java.util.Set;

import org.n52.iceland.config.SettingDefinition;
import org.n52.iceland.config.SettingDefinitionGroup;
import org.n52.iceland.config.SettingDefinitionProvider;
import org.n52.iceland.config.settings.FileSettingDefinition;
import org.n52.iceland.config.settings.StringSettingDefinition;
import org.n52.iceland.config.settings.UriSettingDefinition;

import com.google.common.collect.Sets;

/**
 * Setting definitions for the OWS Service Provider.
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class ServiceProviderFactorySettings implements SettingDefinitionProvider {

    public static final String FILE = "serviceProvider.file";

    public static final String STATE = "serviceProvider.state";

    public static final String PHONE = "serviceProvider.phone";

    public static final String ADDRESS = "serviceProvider.address";

    public static final String SITE = "serviceProvider.site";

    public static final String CITY = "serviceProvider.city";

    public static final String POSITION_NAME = "serviceProvider.positionName";

    public static final String NAME = "serviceProvider.name";

    public static final String INDIVIDUAL_NAME = "serviceProvider.individualName";

    public static final String POSTAL_CODE = "serviceProvider.postalCode";

    public static final String EMAIL = "serviceProvider.email";

    public static final String COUNTRY = "serviceProvider.country";

    public static final SettingDefinitionGroup GROUP = new SettingDefinitionGroup().setTitle("Service Provider")
            .setOrder(0);

    public static final StringSettingDefinition NAME_DEFINITION = new StringSettingDefinition().setGroup(GROUP)
            .setOrder(ORDER_0).setKey(NAME).setTitle("Name").setDescription("Your or your company's name.")
            .setDefaultValue("52North");

    public static final UriSettingDefinition SITE_DEFINITION = new UriSettingDefinition().setGroup(GROUP)
            .setOrder(ORDER_1).setKey(SITE).setTitle("Website").setDescription("Your website.")
            .setDefaultValue(URI.create("http://52north.org/swe"));

    public static final StringSettingDefinition PHONE_DEFINITION = new StringSettingDefinition().setGroup(GROUP)
            .setOrder(ORDER_2).setKey(PHONE).setTitle("Phone")
            .setDescription("The phone number of the responsible person.").setDefaultValue("+49(0)251/396 371-0");

    public static final StringSettingDefinition INDIVIDUAL_NAME_DEFINITION = new StringSettingDefinition()
            .setGroup(GROUP).setOrder(ORDER_3).setKey(INDIVIDUAL_NAME).setTitle("Responsible Person")
            .setDescription("The name of the responsible person of this service.").setDefaultValue("TBA");

    public static final StringSettingDefinition POSITION_NAME_DEFINITION = new StringSettingDefinition()
            .setGroup(GROUP).setOrder(ORDER_4).setKey(POSITION_NAME).setTitle("Position")
            .setDescription("The position of the responsible person.").setDefaultValue("TBA");

    public static final StringSettingDefinition MAIL_ADDRESS_DEFINITION = new StringSettingDefinition()
            .setGroup(GROUP).setOrder(ORDER_5).setKey(EMAIL).setTitle("Mail-Address")
            .setDescription("The electronic mail address of the responsible person.")
            .setDefaultValue("info@52north.org");

    public static final StringSettingDefinition DELIVERY_POINT_DEFINITION = new StringSettingDefinition()
            .setGroup(GROUP).setOrder(ORDER_6).setKey(ADDRESS).setTitle("Address")
            .setDescription("The street address of the responsible person.")
            .setDefaultValue("Martin-Luther-King-Weg 24");

    public static final StringSettingDefinition POSTAL_CODE_DEFINITION = new StringSettingDefinition().setGroup(GROUP)
            .setOrder(ORDER_7).setKey(POSTAL_CODE).setTitle("Postal Code")
            .setDescription("The postal code of the responsible person.").setDefaultValue("48155");

    public static final StringSettingDefinition CITY_DEFINITION = new StringSettingDefinition().setGroup(GROUP)
            .setOrder(ORDER_8).setKey(CITY).setTitle("City").setDescription("The city of the responsible person.")
            .setDefaultValue("M\u00fcnster");

    public static final StringSettingDefinition ADMINISTRATIVE_AREA_DEFINITION = new StringSettingDefinition()
            .setGroup(GROUP).setOrder(ORDER_9).setKey(STATE).setTitle("State")
            .setDescription("The state of the responsible person.").setDefaultValue("North Rhine-Westphalia");

    public static final StringSettingDefinition COUNTRY_DEFINITION = new StringSettingDefinition().setGroup(GROUP)
            .setOrder(ORDER_10).setKey(COUNTRY).setTitle("Country")
            .setDescription("The country of the responsible person.").setDefaultValue("Germany");

    public static final FileSettingDefinition FILE_DEFINITION = new FileSettingDefinition()
            .setGroup(GROUP)
            .setOrder(ORDER_11)
            .setKey(FILE)
            .setTitle("Service Provider File")
            .setDescription(
                    "The path to a file containing an ows:ServiceProvider "
                            + "overriding the above settings. It can be either an "
                            + "absolute path (like <code>/home/user/sosconfig/provider.xml</code>) "
                            + "or a path relative to the web application directory (e.g. "
                            + "<code>WEB-INF/provider.xml</code>).").setOptional(true);

    private static final Set<SettingDefinition<?, ?>> DEFINITIONS = Sets.<SettingDefinition<?, ?>> newHashSet(
            NAME_DEFINITION, SITE_DEFINITION, INDIVIDUAL_NAME_DEFINITION, POSITION_NAME_DEFINITION, PHONE_DEFINITION,
            DELIVERY_POINT_DEFINITION, CITY_DEFINITION, POSTAL_CODE_DEFINITION, ADMINISTRATIVE_AREA_DEFINITION,
            COUNTRY_DEFINITION, MAIL_ADDRESS_DEFINITION, FILE_DEFINITION);

    @Override
    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
        return Collections.unmodifiableSet(DEFINITIONS);
    }
}
