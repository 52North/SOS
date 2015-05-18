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

import java.text.DecimalFormat;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 * 
 */
public abstract class RunnableAction implements Action, Runnable {
    private Long startTimeMillis;
    private final DecimalFormat stopwatchFormat = new DecimalFormat("#.###");

    public void startStopwatch() {
        startTimeMillis = System.currentTimeMillis();
    }

    public String getStopwatchResult() {
        if (startTimeMillis == null) {
            return "stopwatch not initialized";
        }
        return stopwatchFormat.format((System.currentTimeMillis() - startTimeMillis) / 1000.0) + " s";
    }
    
    @Override
    public void run() {
        execute();
    }
}
