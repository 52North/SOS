/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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

import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;

public abstract class AbstractObservationInsertDAOTest extends AbstractInsertDAOTest {

    @Override
    protected void assertInsertionAftermath(boolean afterCacheUpdate) throws OwsExceptionReport {
        // check observation types
        assertThat(getCache().getObservationTypesForOffering(OFFERING3),
                contains(OmConstants.OBS_TYPE_MEASUREMENT));

        // check offerings for procedure
        assertThat(getCache().getOfferingsForProcedure(PROCEDURE1), contains(OFFERING1));
        assertThat(getCache().getOfferingsForProcedure(PROCEDURE2), containsInAnyOrder(OFFERING2));
        assertThat(getCache().getOfferingsForProcedure(PROCEDURE3), containsInAnyOrder(OFFERING3));

        // check procedures and hidden child procedures for offering
        assertThat(getCache().getProceduresForOffering(OFFERING1), containsInAnyOrder(PROCEDURE1));
        // assertThat(getCache().getHiddenChildProceduresForOffering(OFFERING1),
        // containsInAnyOrder(PROCEDURE2, PROCEDURE3));

        assertThat(getCache().getProceduresForOffering(OFFERING2), contains(PROCEDURE2));
        // assertThat(getCache().getHiddenChildProceduresForOffering(OFFERING2),
        // contains(PROCEDURE3));

        assertThat(getCache().getProceduresForOffering(OFFERING3), contains(PROCEDURE3));
        assertThat(getCache().getHiddenChildProceduresForOffering(OFFERING3), empty());

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

        // check features of interest for offering
        // TODO add geometries to features, check bounds, etc
        // TODO investigate these, getting guid back instead of assigned
        // identifier
        // assertThat(getCache().getFeaturesOfInterestForOffering(OFFERING1),
        // contains(FEATURE3));
        // assertThat(getCache().getFeaturesOfInterestForOffering(OFFERING2),
        // contains(FEATURE3));
        // assertThat(getCache().getFeaturesOfInterestForOffering(OFFERING3),
        // contains(FEATURE3));

        // check obsprops for offering
        assertThat(getCache().getObservablePropertiesForOffering(OFFERING1), containsInAnyOrder(OBSPROP1));
        assertThat(getCache().getObservablePropertiesForOffering(OFFERING2), containsInAnyOrder(OBSPROP2));
        assertThat(getCache().getObservablePropertiesForOffering(OFFERING3), contains(OBSPROP3));

        // check offering for obsprops
        assertThat(getCache().getOfferingsForObservableProperty(OBSPROP1), contains(OFFERING1));
        assertThat(getCache().getOfferingsForObservableProperty(OBSPROP2), containsInAnyOrder(OFFERING2));
        assertThat(getCache().getOfferingsForObservableProperty(OBSPROP3), containsInAnyOrder(OFFERING3));

        // assertThat(getCache().getParentOfferings(OBSPROP1, true, false),
        // empty());
        // assertThat(getCache().getParentOfferings(OBSPROP2, true, false),
        // containsInAnyOrder(OFFERING1));
        // assertThat(getCache().getParentOfferings(OBSPROP3, true, false),
        // containsInAnyOrder(OFFERING1, OFFERING2));
        // assertThat(getCache().getChildOfferings(OBSPROP1, true, false),
        // containsInAnyOrder(OFFERING2, OFFERING3));
        // assertThat(getCache().getChildOfferings(OBSPROP2, true, false),
        // containsInAnyOrder(OFFERING3));
        // assertThat(getCache().getChildOfferings(OBSPROP3, true, false),
        // empty());

        // check obsprops for procedure
        // TODO child procedure obsprops are not currently set for parents.
        // should they be?
        // assertThat(getCache().getObservablePropertiesForProcedure(PROCEDURE1),
        // containsInAnyOrder(OBSPROP1, OBSPROP2, OBSPROP3));
        // assertThat(getCache().getObservablePropertiesForProcedure(PROCEDURE2),
        // containsInAnyOrder(OBSPROP2, OBSPROP3));
        assertThat(getCache().getObservablePropertiesForProcedure(PROCEDURE3), contains(OBSPROP3));

        // check procedures for obsprop
        // TODO child procedure obsprops are not currently set for parents.
        // should they be?
        assertThat(getCache().getProceduresForObservableProperty(OBSPROP1), contains(PROCEDURE1));
        // assertThat(getCache().getProceduresForObservableProperty(OBSPROP2),
        // containsInAnyOrder(PROCEDURE1, PROCEDURE2));
        // assertThat(getCache().getProceduresForObservableProperty(OBSPROP3),
        // containsInAnyOrder(PROCEDURE1, PROCEDURE2, PROCEDURE3));

        // check procedures for feature
        // TODO child procedure features are not currently set for parents.
        // should they be?
        // assertThat(getCache().getProceduresForFeatureOfInterest(FEATURE3),
        // containsInAnyOrder(PROCEDURE1, PROCEDURE2, PROCEDURE3));
    }

}
