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
package org.n52.sos.ds.hibernate.cache.base;

import java.util.List;

import org.n52.sos.ds.hibernate.cache.AbstractThreadableDatasourceCacheUpdate;
import org.n52.sos.ds.hibernate.dao.ResultTemplateDAO;
import org.n52.sos.ds.hibernate.entities.ResultTemplate;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.util.Action;
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
 * @author Christian Autermann <c.autermann@52north.org>
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
        if (HibernateHelper.isEntitySupported(ResultTemplate.class)) {
            List<ResultTemplate> resultTemplates = new ResultTemplateDAO().getResultTemplateObjects(getSession());
            for (ResultTemplate resultTemplate : resultTemplates) {
                String id = resultTemplate.getIdentifier();
                getCache().addResultTemplate(id);
                getCache().addResultTemplateForOffering(resultTemplate.getOffering().getIdentifier(), id);
                getCache().addObservablePropertyForResultTemplate(id,
                        resultTemplate.getObservableProperty().getIdentifier());
                getCache().addFeatureOfInterestForResultTemplate(id,
                        resultTemplate.getFeatureOfInterest().getIdentifier());
            }
        }
        LOGGER.debug("Finished executing ResultTemplateCacheUpdate ({})", getStopwatchResult());
    }
}
