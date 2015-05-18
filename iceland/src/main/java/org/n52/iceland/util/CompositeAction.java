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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Joiner;

/**
 * @param <A>
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 * 
 */
public abstract class CompositeAction<A extends Action> extends RunnableAction {

    private List<A> actions;

    public CompositeAction(A... actions) {
        this.actions = Arrays.asList(actions);
    }

    public List<A> getActions() {
        return Collections.unmodifiableList(actions);
    }

    protected abstract void pre(A action);

    protected abstract void post(A action);

    @Override
    public String toString() {
        return String.format("%s[actions=[%s]]", getClass().getSimpleName(), Joiner.on(", ").join(getActions()));
    }
}
