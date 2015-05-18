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

import java.util.concurrent.CountDownLatch;

/**
 * Makes a RunnableAction optionally threadable (if CountDownLatch is set, it is counted down after execution)
 * 
 * @author Shane StClair <shane@axiomalaska.com>
 * @since 4.0.0
 * 
 */
public abstract class ThreadableAction extends RunnableAction {
    private CountDownLatch parentCountDownLatch;

    public void setParentCountDownLatch(CountDownLatch parentCountDownLatch) {
        this.parentCountDownLatch = parentCountDownLatch;
    }

    @Override
    public void run() {
        try {
            super.run();
        } finally {
            if (parentCountDownLatch != null) {
                parentCountDownLatch.countDown();
            }
        }
    }
}
