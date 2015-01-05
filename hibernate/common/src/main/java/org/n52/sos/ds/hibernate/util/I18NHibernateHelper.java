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
package org.n52.sos.ds.hibernate.util;

import org.n52.sos.ds.hibernate.entities.i18n.AbstractHibernateI18NMetadata;
import org.n52.sos.i18n.LocaleHelper;
import org.n52.sos.i18n.LocalizedString;
import org.n52.sos.i18n.metadata.AbstractI18NMetadata;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeType;

/**
 * Hibernate helper class for I18N support
 *
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.0.0
 * @deprecated nowhere used
 */
@Deprecated
public class I18NHibernateHelper {

    /**
     * Add name to abstract feature
     *
     * @param abstractFeature
     *            Abstract feature to add name
     * @param abstractI18N
     *            I18N with language specific values
     */
    public static void addLanguageSpecificNameToFeature(
            AbstractFeature abstractFeature, AbstractHibernateI18NMetadata abstractI18N) {
        if (abstractI18N != null && abstractI18N.isSetName()) {
            //FIXME autermann: create a setting to control in which format the locale is outputted
            String locale = abstractI18N.getLocale().getISO3Language();
            abstractFeature.addName(new CodeType(abstractI18N.getName(), locale));
        }
    }
    /**
     * Add names to abstract feature
     *
     * @param abstractFeature
     *            Abstract feature to add name
     * @param i18n
     *            I18N with language specific values
     */
    public static void addLanguageSpecificNameToFeature(
            AbstractFeature abstractFeature, AbstractI18NMetadata i18n) {
        if (i18n != null) {
            for (LocalizedString name : i18n.getName()) {
                abstractFeature.addName(name.asCodeType());
            }
        }
    }

    /**
     * Add description to abstract feature
     *
     * @param abstractFeature
     *            Abstract feature to add description
     * @param abstractI18N
     *            I18N with language specific values
     */
    public static void addLanguageSpecificDescriptionToFeature(AbstractFeature abstractFeature,
            AbstractHibernateI18NMetadata abstractI18N) {
        if (abstractI18N != null && abstractI18N.isSetDescription()) {
            abstractFeature.setDescription(abstractI18N.getDescription());
        }
    }

    /**
     * private constructor
     */
    private I18NHibernateHelper() {

    }

}
