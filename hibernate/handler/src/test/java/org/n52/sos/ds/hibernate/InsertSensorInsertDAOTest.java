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
package org.n52.sos.ds.hibernate;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.n52.iceland.convert.ConverterException;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.ds.hibernate.util.HibernateMetadataCache;
import org.n52.svalbard.encode.exception.EncodingException;

public class InsertSensorInsertDAOTest extends AbstractInsertDAOTest {

    @Before
    public void setUp() throws OwsExceptionReport, ConverterException, EncodingException {
        super.setUp();
        Session session = null;
        try {
            session = getSession();
            HibernateMetadataCache.init(session);
            insertSensor(PROCEDURE1, OFFERING1, OBSPROP1, null, OmConstants.OBS_TYPE_MEASUREMENT);
            insertSensor(PROCEDURE2, OFFERING2, OBSPROP2, PROCEDURE1, OmConstants.OBS_TYPE_MEASUREMENT);
            insertSensor(PROCEDURE3, OFFERING3, OBSPROP3, PROCEDURE2, OmConstants.OBS_TYPE_MEASUREMENT);
        } finally {
            returnSession(session);
        }
    }

    @Test
    public void testCacheContents() throws OwsExceptionReport, InterruptedException {
        assertThat(getCache().getProcedures(), containsInAnyOrder(PROCEDURE1, PROCEDURE2, PROCEDURE3));
        assertThat(getCache().getOfferings(), containsInAnyOrder(OFFERING1, OFFERING2, OFFERING3));
        assertThat(getCache().getObservableProperties(), containsInAnyOrder(OBSPROP1, OBSPROP2, OBSPROP3));

        assertInsertionAftermathBeforeAndAfterCacheReload();
    }

    @Override
    protected void assertInsertionAftermath(boolean afterCacheUpdate) throws OwsExceptionReport {
        // check offerings for procedure
        assertThat(getCache().getOfferingsForProcedure(PROCEDURE1), contains(OFFERING1));
        assertThat(getCache().getOfferingsForProcedure(PROCEDURE2), contains(OFFERING2));
        assertThat(getCache().getOfferingsForProcedure(PROCEDURE3), contains(OFFERING3));

        // check procedures and hidden child procedures for offering
        assertThat(getCache().getProceduresForOffering(OFFERING1), contains(PROCEDURE1));
        assertThat(getCache().getProceduresForOffering(OFFERING2), contains(PROCEDURE2));
        assertThat(getCache().getProceduresForOffering(OFFERING3), contains(PROCEDURE3));

        // check allowed observation types for offering
        assertThat(getCache().getAllowedObservationTypesForOffering(OFFERING1),
                contains(OmConstants.OBS_TYPE_MEASUREMENT));
        assertThat(getCache().getAllowedObservationTypesForOffering(OFFERING2),
                contains(OmConstants.OBS_TYPE_MEASUREMENT));
        assertThat(getCache().getAllowedObservationTypesForOffering(OFFERING3),
                contains(OmConstants.OBS_TYPE_MEASUREMENT));

        // check parent procedures
        assertThat(getCache().getParentProcedures(PROCEDURE1, true, false), empty());
        assertThat(getCache().getParentProcedures(PROCEDURE2, true, false), contains(PROCEDURE1));
        assertThat(getCache().getParentProcedures(PROCEDURE3, true, false),
                containsInAnyOrder(PROCEDURE1, PROCEDURE2));

        // check child procedures
        assertThat(getCache().getChildProcedures(PROCEDURE1, true, false), containsInAnyOrder(PROCEDURE2, PROCEDURE3));
        assertThat(getCache().getChildProcedures(PROCEDURE2, true, false), contains(PROCEDURE3));
        assertThat(getCache().getChildProcedures(PROCEDURE3, true, false), empty());

        // check obsprops for offering
        assertThat(getCache().getObservablePropertiesForOffering(OFFERING1), contains(OBSPROP1));
        assertThat(getCache().getObservablePropertiesForOffering(OFFERING2), contains(OBSPROP2));
        assertThat(getCache().getObservablePropertiesForOffering(OFFERING3), contains(OBSPROP3));

        // check offering for obsprops
        assertThat(getCache().getOfferingsForObservableProperty(OBSPROP1), contains(OFFERING1));
        assertThat(getCache().getOfferingsForObservableProperty(OBSPROP2), contains(OFFERING2));
        assertThat(getCache().getOfferingsForObservableProperty(OBSPROP3), contains(OFFERING3));

//        // check parent offering
//        assertThat(getCache().getParentOfferings(OBSPROP1, true, false), empty());
//        assertThat(getCache().getParentOfferings(OBSPROP2, true, false), contains(OFFERING1));
//        assertThat(getCache().getParentOfferings(OBSPROP3, true, false), containsInAnyOrder(OFFERING1, OFFERING2));
//
//        // check child offerings
//        assertThat(getCache().getChildOfferings(OBSPROP1, true, false), containsInAnyOrder(OFFERING2, OFFERING3));
//        assertThat(getCache().getChildOfferings(OBSPROP2, true, false), contains(OFFERING3));
//        assertThat(getCache().getChildOfferings(OBSPROP3, true, false), empty());

        // check obsprops for procedure
        // TODO child procedure obsprops are not currently set for parents.
        // should they be?
        assertThat(getCache().getObservablePropertiesForProcedure(PROCEDURE1), contains(OBSPROP1));
        assertThat(getCache().getObservablePropertiesForProcedure(PROCEDURE2), contains(OBSPROP2));
        assertThat(getCache().getObservablePropertiesForProcedure(PROCEDURE3), contains(OBSPROP3));

        // check procedures for obsprop
        // TODO child procedure obsprops are not currently set for parents.
        // should they be?
        assertThat(getCache().getProceduresForObservableProperty(OBSPROP1), contains(PROCEDURE1));
        assertThat(getCache().getProceduresForObservableProperty(OBSPROP2), contains(PROCEDURE2));
        assertThat(getCache().getProceduresForObservableProperty(OBSPROP3), contains(PROCEDURE3));
    }

}
