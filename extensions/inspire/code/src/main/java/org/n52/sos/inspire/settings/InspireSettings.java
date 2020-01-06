/*
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
package org.n52.sos.inspire.settings;

/**
 * SettingDefinitionProvider for INSPIRE
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.1.0
 *
 */
public interface InspireSettings {

    String INSPIRE_ENABLED_KEY = "inspire.enabled";

    String INSPIRE_ID_KEY = "inspire.id";

    String INSPIRE_NAMESPACE_KEY = "inspire.namespace";

    String INSPIRE_FULL_EXTENDED_CAPABILITIES_KEY = "inspire.fullExtendedCapabilities";

    String INSPIRE_METADATA_URL_URL_KEY = "inspire.metadataUrl.url";

    String INSPIRE_METADATA_URL_MEDIA_TYPE_KEY = "inspire.metadataUrl.mediaType";

    String INSPIRE_CONFORMITY_TITLE_KEY = "inspire.conformity.title";

    String INSPIRE_CONFORMITY_DATE_OF_CREATION_KEY = "inspire.conformity.dateOfCreation";

    String INSPIRE_METADATA_DATE_KEY = "inspire.metadataDate";

    String INSPIRE_USE_AUTHORITY_KEY = "inspire.useAuthority";

    // String INSPIRE_LANGUAGES_DEFAULT_KEY = "inspire.defaultLanguage";
    //
    // String INSPIRE_CRS_DEFAULT_KEY = "inspire.defaultCrs";
}
