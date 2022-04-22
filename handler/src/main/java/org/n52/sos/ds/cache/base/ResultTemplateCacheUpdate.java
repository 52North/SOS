/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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
package org.n52.sos.ds.cache.base;

import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.n52.iceland.util.action.Action;
import org.n52.series.db.beans.ResultTemplateEntity;
import org.n52.sos.ds.cache.AbstractThreadableDatasourceCacheUpdate;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * When executing this &auml;ction (see {@link Action}), the following relations
 * are added, settings are updated in cache:
 * <ul>
 * <li>Result template identifier</li>
 * <li>Procedure &rarr; 'Result template identifier' relation</li>
 * <li>'Result template identifier' &rarr; 'observable property' relation</li>
 * <li>'Result template identifier' &rarr; 'feature of interest' relation</li>
 * </ul>
 *
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * @since 4.0.0
 */
public class ResultTemplateCacheUpdate extends AbstractThreadableDatasourceCacheUpdate {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultTemplateCacheUpdate.class);

    @Override
    public void execute() {
        LOGGER.debug("Executing ResultTemplateCacheUpdate");
        startStopwatch();
        if (HibernateHelper.isEntitySupported(ResultTemplateEntity.class)) {
            List<ResultTemplateEntity> resultTemplates = getResultTemplateObjects(getSession());
            for (ResultTemplateEntity resultTemplate : resultTemplates) {
                String id = resultTemplate.getIdentifier();
                getCache().addResultTemplate(id);
                getCache().addResultTemplateForOffering(resultTemplate.getOffering().getIdentifier(), id);
                getCache().addObservablePropertyForResultTemplate(id, resultTemplate.getPhenomenon().getIdentifier());
                if (resultTemplate.getFeature() != null) {
                    getCache().addFeatureOfInterestForResultTemplate(id, resultTemplate.getFeature().getIdentifier());
                }
            }
        }
        LOGGER.debug("Finished executing ResultTemplateCacheUpdate ({})", getStopwatchResult());
    }

    private List<ResultTemplateEntity> getResultTemplateObjects(Session session) {
        return session.createCriteria(ResultTemplateEntity.class)
                .setFetchMode(ResultTemplateEntity.PROPERTY_OFFERING, FetchMode.JOIN)
                .setFetchMode(ResultTemplateEntity.PROPERTY_PHENOMENON, FetchMode.JOIN)
                .setFetchMode(ResultTemplateEntity.PROPERTY_FEATURE, FetchMode.JOIN).list();
    }

}
