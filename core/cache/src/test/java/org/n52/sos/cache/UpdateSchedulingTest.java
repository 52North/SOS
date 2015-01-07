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
package org.n52.sos.cache;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.n52.sos.exception.ows.concrete.GenericThrowableWrapperException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.GroupedAndNamedThreadFactory;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class UpdateSchedulingTest extends AbstractCacheControllerTest {

    private static final long TIMEOUT = 100 * 5;

    private static final long PAUSE = 50;

    @Test
    public void test() throws InterruptedException {
        final TestableInMemoryCacheController ue = new TestableInMemoryCacheController();
        ue.setCache(new WritableCache());
        ExecutorService e = Executors.newFixedThreadPool(10, new GroupedAndNamedThreadFactory("test"));

        e.execute(new BlockingCacheUpdate(ue, "complete0", TIMEOUT));
        Thread.sleep(PAUSE);
        e.execute(new NonBlockingCacheUpdate(ue, "partial1"));
        e.execute(new BlockingCacheUpdate(ue, "complete2", TIMEOUT));
        e.execute(new NonBlockingCacheUpdate(ue, "partial2"));
        e.execute(new BlockingCacheUpdate(ue, "complete3", TIMEOUT));
        e.execute(new NonBlockingCacheUpdate(ue, "partial3"));
        Thread.sleep(TIMEOUT);
        e.execute(new BlockingCacheUpdate(ue, "complete4", TIMEOUT));
        Thread.sleep(TIMEOUT);
        e.execute(new NonBlockingCacheUpdate(ue, "partial4"));
        /*
         * expected: 1, TIMEOUT, 2,3, TIMEOUT 4
         */
        e.shutdown();
        e.awaitTermination(TIMEOUT * 10, TimeUnit.MILLISECONDS);
    }

    private class BlockingCacheUpdate extends NonBlockingCacheUpdate {
        private final long timeout;

        BlockingCacheUpdate(TestableInMemoryCacheController e, String offering, long timeout) {
            super(e, offering);
            this.timeout = timeout;
        }

        @Override
        public void execute() {
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException ex) {
                fail(new GenericThrowableWrapperException(ex));
            }
            super.execute();
        }

        @Override
        public boolean isCompleteUpdate() {
            return true;
        }
    }

    private class NonBlockingCacheUpdate extends ContentCacheUpdate implements Runnable {
        private final TestableInMemoryCacheController executor;

        private final String offering;

        NonBlockingCacheUpdate(TestableInMemoryCacheController executor, String offering) {
            this.offering = offering;
            this.executor = executor;
        }

        @Override
        public void execute() {
            getCache().addOffering(offering);
        }

        @Override
        public boolean isCompleteUpdate() {
            return false;
        }

        @Override
        public void run() {
            try {
                executor.update(this);
            } catch (OwsExceptionReport ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public String toString() {
            return String.format("%s[offering=%s]", getClass().getSimpleName(), offering);
        }
    }
}
