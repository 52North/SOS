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
package org.n52.sos.tasking;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.Timer;

import org.n52.sos.util.Cleanupable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class Tasking implements Cleanupable {

    private static final Logger LOG = LoggerFactory.getLogger(Tasking.class);

    private final ServiceLoader<ASosTasking> serviceLoader = ServiceLoader.load(ASosTasking.class);

    private Timer taskingExecutor;

    public Tasking() {
        load();
    }

    @Override
    public void cleanup() {
        if (taskingExecutor != null) {
            taskingExecutor.cancel();
            taskingExecutor = null;
        }
    }

    private void load() {
        Iterator<ASosTasking> iterator = this.serviceLoader.iterator();
        if (iterator.hasNext()) {
            this.taskingExecutor = new Timer("TaskingTimer");
            long delayCounter = 0;
            while (iterator.hasNext()) {
                try {
                    ASosTasking aSosTasking = iterator.next();
                    this.taskingExecutor.scheduleAtFixedRate(aSosTasking, delayCounter,
                            (aSosTasking.getExecutionIntervall() * 60000));
                    delayCounter += 60000;
                    LOG.debug("The task '{}' is started!", aSosTasking.getName());
                } catch (Exception e) {
                    LOG.error("Error while starting task", e);
                }
            }
            LOG.info("\n******\n Task(s) loaded and started successfully!\n******\n");
        }
    }
}
