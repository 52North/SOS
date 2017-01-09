/*
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.settings;

/**
 * Setting definition provider for AQD e-Reporting definitions
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.3.0
 *
 */
public interface EReportingSetting {

    String EREPORTING_NAMESPACE = "eReporting.namespace";

    String EREPORTING_OBSERVATION_PREFIX = "eReporting.observation.prefix";

    String EREPORTING_OFFERING_PREFIX_KEY = "eReporting.offering.prefix";

    String EREPORTING_PROCEDURE_PREFIX_KEY = "eReporting.procedure.prefix";

//     String EREPORTING_OBSERVABLE_PROPERTY_PREFIX_KEY = "eReporting.obervableProperty.prefix";
    String EREPORTING_FEATURE_OF_INTEREST_PREFIX_KEY
            = "eReporting.featureOfInterest.prefix";

    String EREPORTING_SAMPLING_POINT_PREFIX_KEY
            = "eReporting.samplingPoint.prefix";

    String EREPORTING_STATION_PREFIX_KEY = "eReporting.station.prefix";

    String EREPORTING_NETWORK_PREFIX_KEY = "eReporting.network.prefix";

    String EREPORTING_VALIDITY_FLAGS = "eReporting.flags.validity";

    String EREPORTING_VERIFICATION_FLAGS = "eReporting.flags.verification";

}
