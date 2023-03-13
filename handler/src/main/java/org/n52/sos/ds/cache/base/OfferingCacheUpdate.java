/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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
package org.n52.sos.ds.cache.base;

import java.util.Collection;
import java.util.Locale;

import org.n52.iceland.exception.ows.concrete.GenericThrowableWrapperException;
import org.n52.io.request.IoParameters;
import org.n52.series.db.HibernateSessionStore;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.series.db.dao.DbQuery;
import org.n52.series.db.dao.OfferingDao;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.ds.ApiQueryHelper;
import org.n52.sos.ds.cache.AbstractQueueingDatasourceCacheUpdate;
import org.n52.sos.ds.cache.DatasourceCacheUpdateHelper;
import org.n52.sos.util.GeometryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 *
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 *
 * @since 4.0.0
 */
@SuppressFBWarnings({"EI_EXPOSE_REP2"})
public class OfferingCacheUpdate extends AbstractQueueingDatasourceCacheUpdate<OfferingCacheUpdateTask>
        implements ApiQueryHelper, DatasourceCacheUpdateHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(OfferingCacheUpdate.class);

    private static final String THREAD_GROUP_NAME = "offering-cache-update";

    private OfferingDao offeringDAO;

    private Collection<String> offeringsIdToUpdate = Lists.newArrayList();

    private Collection<OfferingEntity> offeringsToUpdate;

    private final Locale defaultLanguage;

    private GeometryHandler geometryHandler;

    public OfferingCacheUpdate(int threads, Locale defaultLanguage, GeometryHandler geometryHandler,
            HibernateSessionStore sessionStore) {
        this(threads, defaultLanguage, geometryHandler, sessionStore, null);
    }

    public OfferingCacheUpdate(int threads, Locale defaultLanguage, GeometryHandler geometryHandler,
            HibernateSessionStore sessionStore, Collection<String> offeringIdsToUpdate) {
        super(threads, THREAD_GROUP_NAME, sessionStore);
        if (offeringIdsToUpdate != null) {
            this.offeringsIdToUpdate.addAll(offeringIdsToUpdate);
        }
        this.defaultLanguage = defaultLanguage;
        this.geometryHandler = geometryHandler;
    }

    private Collection<OfferingEntity> getOfferingsToUpdate() {
        try {
            if (offeringDAO == null) {
                offeringDAO = new OfferingDao(getSession());
            }
            if (offeringsToUpdate == null) {
                if (offeringsIdToUpdate == null || offeringsIdToUpdate.isEmpty()) {
                    return offeringDAO.get(new DbQuery(IoParameters.createDefaults()));
                }
            }
        } catch (Exception e) {
            getErrors().add(new GenericThrowableWrapperException(e)
                    .withMessage("Error while processing procedure cache update task!"));
        }
        return offeringsToUpdate;
    }

    @Override
    public void execute() {
        LOGGER.debug("Executing OfferingCacheUpdate (Single Threaded Tasks)");
        startStopwatch();
        this.offeringsToUpdate = getOfferingsToUpdate();
        LOGGER.debug("Finished executing OfferingCacheUpdate (Single Threaded Tasks) ({})", getStopwatchResult());

        // execute multi-threaded updates
        LOGGER.debug("Executing OfferingCacheUpdate (Multi-Threaded Tasks)");
        startStopwatch();
        super.execute();
        LOGGER.debug("Finished executing OfferingCacheUpdate (Multi-Threaded Tasks) ({})", getStopwatchResult());
    }

    @Override
    protected OfferingCacheUpdateTask[] getUpdatesToExecute() throws OwsExceptionReport {
        Collection<OfferingCacheUpdateTask> offeringUpdateTasks = Lists.newArrayList();
        for (OfferingEntity offering : getOfferingsToUpdate()) {
            offeringUpdateTasks
                    .add(new OfferingCacheUpdateTask(offering.getId(), this.defaultLanguage, geometryHandler));
        }
        return offeringUpdateTasks.toArray(new OfferingCacheUpdateTask[offeringUpdateTasks.size()]);
    }

}
