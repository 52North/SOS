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
package org.n52.sos.ds.hibernate.cache;

import org.n52.sos.util.CompositeAction;
import org.n52.sos.util.CompositeSerialAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

/**
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public abstract class CompositeCacheUpdate extends AbstractDatasourceCacheUpdate {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(CompositeCacheUpdate.class);

    private CompositeAction<AbstractDatasourceCacheUpdate> delegatedAction;

    public CompositeCacheUpdate(AbstractDatasourceCacheUpdate... actions) {
        this.delegatedAction = new CompositeSerialAction<AbstractDatasourceCacheUpdate>(actions) {
            @Override
            protected void pre(AbstractDatasourceCacheUpdate action) {
                action.setCache(getCache());
                action.setErrors(getErrors());
                action.setSession(getSession());
            }

            @Override
            protected void post(AbstractDatasourceCacheUpdate action) {
                getSession().clear();
            }
        };
    }

    @Override
    public void execute() {
        delegatedAction.execute();
    }

    @Override
    public String toString() {
        return String.format("%s [actions=[%s]]", getClass().getSimpleName(),
                Joiner.on(", ").join(delegatedAction.getActions()));
    }
}
