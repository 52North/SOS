/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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

import java.util.List;

import org.hibernate.Session;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.i18n.AbstractFeatureI18NDAO;
import org.n52.sos.ds.hibernate.entities.AbstractIdentifierNameDescriptionEntity;
import org.n52.sos.ds.hibernate.entities.i18n.AbstractFeatureI18N;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.util.CollectionHelper;

/**
 * Hibernate helper class for I18N support
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.0.0
 * 
 */
public class I18NHibernateHelper {

    /**
     * Query and add language specific values to the abstract feature object
     * 
     * @param abstractEntity
     *            Queried abstract entity with language specific values
     * @param abstractFeature
     *            Abstract feature to add language specific values
     * @param i18n
     *            Requested language to add values for
     * @param showAllLanguageValues
     *            Indicator if all language values should be added
     * @param session
     *            Hibernate session
     * @return <code>true</code>, if language specific values are added
     */
    public static boolean getLanguageSpecificData(AbstractIdentifierNameDescriptionEntity abstractEntity,
            AbstractFeature abstractFeature, String i18n, boolean showAllLanguageValues, Session session) {
        AbstractFeatureI18NDAO i18ndao = DaoFactory.getInstance().getI18NDAO(abstractEntity, session);
        if (i18ndao != null) {
            if (showAllLanguageValues) {
                List<AbstractFeatureI18N> abstractI18Ns = i18ndao.getObjects(abstractEntity, session);
                if (CollectionHelper.isNotEmpty(abstractI18Ns)) {
                    for (AbstractFeatureI18N abstractI18N : abstractI18Ns) {
                        if (abstractI18N.getCodespace().getCodespace().equals(i18n)) {
                            addLanguageSpecificDescriptionToFeature(abstractFeature, abstractI18N);
                        }
                        addLanguageSpecificNameToFeature(abstractFeature, abstractI18N);
                    }
                }
            } else {
                AbstractFeatureI18N abstractI18N = i18ndao.getObject(abstractEntity, i18n, session);
                addLanguageSpecificNameToFeature(abstractFeature, abstractI18N);
                addLanguageSpecificDescriptionToFeature(abstractFeature, abstractI18N);
            }
            return true;
        }
        return false;
    }

    /**
     * Add name to abstract feature
     * 
     * @param abstractFeature
     *            Abstract feature to add name
     * @param abstractI18N
     *            I18N with language specific values
     */
    public static void addLanguageSpecificNameToFeature(AbstractFeature abstractFeature, AbstractFeatureI18N abstractI18N) {
        if (abstractI18N != null && abstractI18N.isSetName()) {
            CodeType codeType = new CodeType(abstractI18N.getName());
            codeType.setCodeSpace(abstractI18N.getCodespace().getCodespace());
            abstractFeature.addName(codeType);
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
            AbstractFeatureI18N abstractI18N) {
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
