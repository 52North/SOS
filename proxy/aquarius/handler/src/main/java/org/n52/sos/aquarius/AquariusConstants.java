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
package org.n52.sos.aquarius;

public interface AquariusConstants {

    String AQUARIUS_PATH = "/AQUARIUS/Publish/v2/";

    String HEADER_AQ_AUTH_TOKEN = "X-Authentication-Token";

    String FILTER = "filter";

    String LAST_UPDATE_TIME = "LAST_UPDATE_TIME";

    String GROUP = "Aquarius";

    public interface Paths {
        String SESSION = "session";

        String PUBLIC_KEY = SESSION + "/publickey";

        String KEEP_ALIVE = SESSION + "/keepalive";

        String GET_PARAMETER_LIST = "GetParameterList";

        String GET_UNIT_LIST = "GetUnitList";

        String GET_LOCATION_DESCRIPTION_LIST = "GetLocationDescriptionList";

        String GET_LOCATION_DATA = "GetLocationData";

        String GET_TIME_SERIES_DESCRIPTION_LIST = "GetTimeSeriesDescriptionList";

        String GET_TIME_SERIES_CORRECTED_DATA = "GetTimeSeriesCorrectedData";

        String GET_TIME_SERIES_RAW_DATA = "GetTimeSeriesRawData";

        String GET_TIME_SERIES_UNIQUE_ID_LIST = "GetTimeSeriesUniqueIdList";
    }

    public interface Parameters {
        String GROUP_IDENTIFIER = "GroupIdentifier";

        String LOCATION_NAME = "LocationName";

        String LOCATION_IDENTIFIER = "LocationIdentifier";

        String EXTENDED_FILTERS = "ExtendedFilters";

        String INCLUDE_LOCATION_ATTACHMENTS = "IncludeLocationAttachments";

        String PARAMETER = "Parameter";

        String TIME_SERIES_UNIQUE_ID = "TimeSeriesUniqueId";

        String TIME_SERIES_UNIQUE_IDS = "TimeSeriesUniqueIds";

        String QUERY_FROM = "QueryFrom";

        String QUERY_TO = "QueryTo";

        String GET_PARTS = "GetParts";

        String APPLY_ROUNDING = "ApplyRounding";

        String RETURN_FULL_COVERAGE = "ReturnFullCoverage";

        String INCLUDE_GAP_MARKERS = "IncludeGapMarkers";

        String PUBLISHED = "Publish";

        String CHANGES_SINCE_TOKEN = "ChangesSinceToken";

        String FIRST_POINT_CHANGED = "firstPointChanged";

        String CHANGE_EVENT_TYPE = "ChangeEventType";
    }

    interface ChangeEventTypes {
        String ATTRIBUTE = "Attribute";

        String DATA = "Data";
    }

    enum GetParts {
        All, PointsOnly, MetadataOnly;
    }

    enum InterpolationTypes {
        PrecedingConstant,
        SucceedingConstant,
        InstantaneousValues,
        DiscreteValues,
        InstantaneousTotals,
        PrecedingTotals, Default;

        public static InterpolationTypes getFrom(String value) {
            for (InterpolationTypes identifier : values()) {
                if (identifier.name()
                        .equalsIgnoreCase(value)) {
                    return identifier;
                }
            }
            return InterpolationTypes.Default;
        }
    }

    enum ComputationIdentifiers {
        Mean, Max, Min, Default;

        public static ComputationIdentifiers getFrom(String value) {
            for (ComputationIdentifiers identifier : values()) {
                if (identifier.name()
                        .equalsIgnoreCase(value)) {
                    return identifier;
                }
            }
            return ComputationIdentifiers.Default;
        }
    }

}
