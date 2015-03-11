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
package org.n52.sos.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @param <A>
 * @author Christian Autermann <c.autermann@52north.org>
 * @author Shane StClair <shane@axiomalaska.com>
 * @since 4.0.0
 * 
 */
public abstract class CompositeSerialAction<A extends Action> extends CompositeAction<A> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompositeSerialAction.class);

    public CompositeSerialAction(A... actions) {
        super(actions);
    }

    @Override
    public void execute() {
        if (getActions() != null) {
            for (A action : getActions()) {
                pre(action);
                LOGGER.debug("Running {}.", action);                
                action.execute();
                post(action);
            }
        }
    }
}
