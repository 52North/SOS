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

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 * 
 */
public class GroupedAndNamedThreadFactory implements ThreadFactory {
    private final AtomicInteger i = new AtomicInteger(0);

    private final ThreadGroup tg;

    public GroupedAndNamedThreadFactory(String name) {
        tg = new ThreadGroup(name);
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(tg, r, String.format("%s-%d", tg.getName(), i.getAndIncrement()));
    }

}
