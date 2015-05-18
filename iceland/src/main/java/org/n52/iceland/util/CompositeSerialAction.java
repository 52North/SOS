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
package org.n52.iceland.util;

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
