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
package org.n52.sos.inspire.settings;

import java.util.Collections;
import java.util.Set;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingDefinitionGroup;
import org.n52.sos.config.SettingDefinitionProvider;
import org.n52.sos.config.settings.BooleanSettingDefinition;
import org.n52.sos.config.settings.StringSettingDefinition;
import org.n52.sos.config.settings.UriSettingDefinition;

import com.google.common.collect.ImmutableSet;

/**
 * SettingDefinitionProvider for INSPIRE
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 * 
 */
public class InspireSettings implements SettingDefinitionProvider {
    
    public static final String INSPIRE_ENABLED_KEY = "inspire.enabled";
    
    public static final String INSPIRE_ID_KEY = "inspire.id";
    
    public static final String INSPIRE_FULL_EXTENDED_CAPABILITIES_KEY = "inspire.fullExtendedCapabilities";
    
    public static final String INSPIRE_METADATA_URL_URL_KEY = "inspire.metadataUrl.url";
    
    public static final String INSPIRE_METADATA_URL_MEDIA_TYPE_KEY = "inspire.metadataUrl.mediaType";
    
    public static final String INSPIRE_CONFORMITY_TITLE_KEY = "inspire.conformity.title";
    
    public static final String INSPIRE_CONFORMITY_DATE_OF_CREATION_KEY = "inspire.conformity.dateOfCreation";
    
    public static final String INSPIRE_METADATA_DATE_KEY = "inspire.metadataDate";
    
    public static final String INSPIRE_USE_AUTHORITY_KEY = "inspire.useAuthority";
    
    public static final String INSPIRE_NAMESPACE_KEY = "inspire.namespace";

    public static final SettingDefinitionGroup GROUP = new SettingDefinitionGroup().setTitle("INSPIRE").setOrder(
            ORDER_10);
    
    public static final BooleanSettingDefinition INSPIRE_ENABLED_DEFINITION = new BooleanSettingDefinition()
                        .setGroup(GROUP)
                        .setOrder(ORDER_0)
                        .setKey(INSPIRE_ENABLED_KEY)
                        .setDefaultValue(false)
                        .setTitle("Enable INSPIRE extension")
                        .setDescription("Indicator to enable/disable the INSPIRE extension");
    
    public static final BooleanSettingDefinition INSPIRE_FULL_EXTENDED_CAPABILITIES_DEFINITION = new BooleanSettingDefinition()
                        .setGroup(GROUP)
                        .setOrder(ORDER_1)
                        .setKey(INSPIRE_FULL_EXTENDED_CAPABILITIES_KEY)
                        .setDefaultValue(true)
                        .setTitle("Show full INSPIRE ExtendedCapabilities")
                        .setDescription("Should the SOS show the full or the minimal INSPIRE ExtendedCapabilities");
    
    public static final UriSettingDefinition INSPIRE_METADATA_URL_URL_DEFINITION = new UriSettingDefinition()
                        .setGroup(GROUP)
                        .setOrder(ORDER_2)
                        .setKey(INSPIRE_METADATA_URL_URL_KEY)
                        .setDefaultStringValue("http://myserver.org/")
                        .setTitle("INSPIRE MetadataUrl URLs")
                        .setDescription("Set the INSPIRE MetadataUrl URL, required if full INSPIRE ExtendedCapabilities is disabled");
    
    public static final StringSettingDefinition INSPIRE_METADATA_URL_MEDIA_TYPE_DEFINITION = new StringSettingDefinition()
                        .setGroup(GROUP)
                        .setOrder(ORDER_3)
                        .setKey(INSPIRE_METADATA_URL_MEDIA_TYPE_KEY)
                        .setDefaultValue("application/xml")
                        .setTitle("INSPIRE MetadataUrl MediaType")
                        .setDescription("Set the INSPIRE MetadataUrl MediaType");
    
    public static final StringSettingDefinition INSPIRE_CONFORMITY_TITLE_DEFINITION = new StringSettingDefinition()
                        .setGroup(GROUP)
                        .setOrder(ORDER_4)
                        .setKey(INSPIRE_CONFORMITY_TITLE_KEY)
                        .setDefaultValue("OGC SOS 2.0 for Inspire")
                        .setTitle("INSPIRE Conformity title")
                        .setDescription("Set the INSPIRE extended capabilities Conformity title");
    
    public static final StringSettingDefinition INSPIRE_CONFORMITY_DATE_OF_CREATION_DEFINITION = new StringSettingDefinition()
                        .setGroup(GROUP)
                        .setOrder(ORDER_5)
                        .setKey(INSPIRE_CONFORMITY_DATE_OF_CREATION_KEY)
                        .setDefaultValue("2008-06-01")
                        .setTitle("INSPIRE Conformity date of creation")
                        .setDescription("Set the INSPIRE extended capabilities Conformity date of creation");
    
    public static final StringSettingDefinition INSPIRE_METADATA_DATE_DEFINITION = new StringSettingDefinition()
                        .setGroup(GROUP)
                        .setOrder(ORDER_6)
                        .setKey(INSPIRE_METADATA_DATE_KEY)
                        .setDefaultValue("2008-06-01")
                        .setTitle("INSPIRE Metadata date")
                        .setDescription("Set the INSPIRE extended capabilities metadata date");

    public static final StringSettingDefinition INSPIRE_ID_DEFINITION = new StringSettingDefinition()
                        .setGroup(GROUP)
                        .setOrder(ORDER_10)
                        .setKey(INSPIRE_ID_KEY)
                        .setDefaultValue("123")
                        .setTitle("INSPIRE id")
                        .setDescription("Set the INSPIRE id for this service");
    
    public static final BooleanSettingDefinition INSPIRE_USE_AUTHORITY_DEFINITION = new BooleanSettingDefinition()
                        .setGroup(GROUP)
                        .setOrder(ORDER_11)
                        .setKey(INSPIRE_USE_AUTHORITY_KEY)
                        .setDefaultValue(false)
                        .setTitle("Use authority as CRS prefix ")
                        .setDescription("Should the SOS use the authority prefix (EPSG::) or the OGC CRS prefix (http://www.opengis.net/def/crs/EPSG/0/)?");
    
    public static final StringSettingDefinition INSPIRE_NAMESPACE_DEFINITION = new StringSettingDefinition()
                        .setGroup(GROUP)
                        .setOrder(ORDER_12)
                        .setKey(INSPIRE_NAMESPACE_KEY)
                        .setDefaultValue("http://www.52north.org/")
                        .setTitle("INSPIRE namespace")
                        .setDescription("Set the INSPIRE namespace for this service.");
    
    
    private static final Set<SettingDefinition<?, ?>> DEFINITIONS = ImmutableSet.<SettingDefinition<?, ?>> of(
            INSPIRE_ENABLED_DEFINITION, INSPIRE_FULL_EXTENDED_CAPABILITIES_DEFINITION, INSPIRE_METADATA_URL_URL_DEFINITION, 
            INSPIRE_METADATA_URL_MEDIA_TYPE_DEFINITION, INSPIRE_CONFORMITY_TITLE_DEFINITION,
            INSPIRE_CONFORMITY_DATE_OF_CREATION_DEFINITION, INSPIRE_CONFORMITY_TITLE_DEFINITION, 
            INSPIRE_METADATA_DATE_DEFINITION, INSPIRE_USE_AUTHORITY_DEFINITION, INSPIRE_NAMESPACE_DEFINITION
//            , INSPIRE_ID_DEFINITION
            );

    @Override
    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
        return Collections.unmodifiableSet(DEFINITIONS);
    }
}
