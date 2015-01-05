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
package org.n52.sos.ds.hibernate;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.n52.sos.cache.WritableCache;
import org.n52.sos.cache.WritableContentCache;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 */
public class SosCacheFeederDAOTest extends HibernateTestCase {
    /* FIXTURES */
    private SosCacheFeederDAO instance;

    @Before
    public void initCacheFeeder() {
        instance = new SosCacheFeederDAO();
    }

    @Test
    public void updateCacheFillsCapabilitiesCache() throws OwsExceptionReport {
        WritableContentCache cache = new WritableCache();
        instance.updateCache(cache);
        testCacheResult(cache);
    }

    @Test(expected = NullPointerException.class)
    public void updateNullThrowsNullPointerException() throws OwsExceptionReport {
        instance.updateCache(null);
    }

    /* HELPER */
    private void testCacheResult(WritableContentCache cache) {
        assertNotNull("cache is null", cache);
        assertNotNull("envelope of features is null", cache.getGlobalEnvelope());
        assertNotNull("feature types is null", cache.getFeatureOfInterestTypes());
        assertNotNull("offerings is null", cache.getOfferings());
        // assertNotNull("max phenomenon time is null",
        // cache.getMaxPhenomenonTime());
        // assertNotNull("min phenomenon time is null",
        // cache.getMinPhenomenonTime());
        // assertNotNull("max result time is null", cache.getMaxResultTime());
        // assertNotNull("min result time is null", cache.getMinResultTime());
        assertNotNull("observation types is null", cache.getObservationTypes());
        assertNotNull("result templates is null", cache.getResultTemplates());
    }
}
