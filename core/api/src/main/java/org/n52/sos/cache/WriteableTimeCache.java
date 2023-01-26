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
package org.n52.sos.cache;

import java.util.Objects;

import org.joda.time.DateTime;

import org.n52.shetland.ogc.gml.time.Time;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.gml.time.TimePeriod;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public interface WriteableTimeCache extends TemporalCache, CacheConstants {
    /**
     * Reset the offering to minimal phenomenon time relation.
     */
    void clearMinPhenomenonTimeForOfferings();

    /**
     * Reset the offering to maximal phenomenon time relation.
     */
    void clearMaxPhenomenonTimeForOfferings();

    /**
     * Reset the procedure to minimal phenomenon time relation.
     */
    void clearMinPhenomenonTimeForProcedures();

    /**
     * Reset the procedure to maximal phenomenon time relation.
     */
    void clearMaxPhenomenonTimeForProcedures();

    /**
     * Reset the offering to minimal result time relation.
     */
    void clearMinResultTimeForOfferings();

    /**
     * Reset the offering to maximal result time relation.
     */
    void clearMaxResultTimeForOfferings();

    /**
     * Sets the global maximal phenomenon time.
     *
     * @param maxEventTime the max phenomenon time
     */
    void setMaxPhenomenonTime(DateTime maxEventTime);

    /**
     * Sets the maximal phenomenon time for the specified offering to the specified time.
     *
     * @param offering the offering
     * @param maxTime  the max phenomenon time
     */
    void setMaxPhenomenonTimeForOffering(String offering, DateTime maxTime);

    /**
     * Sets the maximal phenomenon time for the specified procedure to the specified time.
     *
     * @param procedure the procedure
     * @param maxTime   the max phenomenon time
     */
    void setMaxPhenomenonTimeForProcedure(String procedure, DateTime maxTime);

    /**
     * Sets the global minimal phenomenon time.
     *
     * @param minEventTime the min phenomenon time
     */
    void setMinPhenomenonTime(DateTime minEventTime);

    /**
     * Sets the minimal phenomenon time for the specified offering to the specified time.
     *
     * @param offering the offering
     * @param minTime  the min phenomenon time
     */
    void setMinPhenomenonTimeForOffering(String offering, DateTime minTime);

    /**
     * Sets the minimal phenomenon time for the specified procedure to the specified time.
     *
     * @param procedure the procedure
     * @param minTime   the min phenomenon time
     */
    void setMinPhenomenonTimeForProcedure(String procedure, DateTime minTime);

    /**
     * Updates the phenomenon time envelope of the specified offering to include the specified event time.
     *
     * @param offering  the offering
     * @param eventTime the time to include
     */
    default void updatePhenomenonTimeForOffering(String offering, Time eventTime) {
        CacheValidation.notNullOrEmpty(OFFERING, offering);
        Objects.requireNonNull(eventTime, EVENT_TIME);
        final TimePeriod tp = toTimePeriod(eventTime);
        if (!hasMaxPhenomenonTimeForOffering(offering) ||
            getMaxPhenomenonTimeForOffering(offering).isBefore(tp.getEnd())) {
            setMaxPhenomenonTimeForOffering(offering, tp.getEnd());
        }
        if (!hasMinPhenomenonTimeForOffering(offering) ||
            getMinPhenomenonTimeForOffering(offering).isAfter(tp.getStart())) {
            setMinPhenomenonTimeForOffering(offering, tp.getStart());
        }
    }

    /**
     * Updates the phenomenon time envelope of the specified procedure to include the specified event time.
     *
     * @param procedure the procedure
     * @param eventTime the time to include
     */
    default void updatePhenomenonTimeForProcedure(String procedure, Time eventTime) {
        CacheValidation.notNullOrEmpty(PROCEDURE, procedure);
        Objects.requireNonNull(eventTime, EVENT_TIME);
        final TimePeriod tp = toTimePeriod(eventTime);
        if (!hasMaxPhenomenonTimeForProcedure(procedure) ||
            getMaxPhenomenonTimeForProcedure(procedure).isBefore(tp.getEnd())) {
            setMaxPhenomenonTimeForProcedure(procedure, tp.getEnd());
        }
        if (!hasMinPhenomenonTimeForProcedure(procedure) ||
            getMinPhenomenonTimeForProcedure(procedure).isAfter(tp.getStart())) {
            setMinPhenomenonTimeForProcedure(procedure, tp.getStart());
        }
    }

    /**
     * Sets the global maximal result time.
     *
     * @param maxResultTime the max result time
     */
    void setMaxResultTime(DateTime maxResultTime);

    /**
     * Sets the maximal result time for the specified offering to the specified time.
     *
     * @param offering the offering
     * @param maxTime  the max result time
     */
    void setMaxResultTimeForOffering(String offering, DateTime maxTime);

    /**
     * Sets the global minimal result time.
     *
     * @param minResultTime the min result time
     */
    void setMinResultTime(DateTime minResultTime);

    /**
     * Sets the minimal result time for the specified offering to the specified time.
     *
     * @param offering the offering
     * @param minTime  the min result time
     */
    void setMinResultTimeForOffering(String offering, DateTime minTime);

    /**
     * Updates the result time envelope of the specified offering to include the specified result time.
     *
     * @param offering   the offering
     * @param resultTime the time to include
     */
    default void updateResultTimeForOffering(String offering, Time resultTime) {
        CacheValidation.notNullOrEmpty(OFFERING, offering);
        if (resultTime == null) {
            return;
        }
        final TimePeriod tp = toTimePeriod(resultTime);
        if (!hasMaxResultTimeForOffering(offering) || getMaxResultTimeForOffering(offering).isBefore(tp.getEnd())) {
            setMaxResultTimeForOffering(offering, tp.getEnd());
        }
        if (!hasMinResultTimeForOffering(offering) || getMinResultTimeForOffering(offering).isAfter(tp.getStart())) {
            setMinResultTimeForOffering(offering, tp.getStart());
        }
    }

    /**
     * Sets the new global phenomenon envelope.
     *
     * @param min the minimal phenomenon time
     * @param max the maximal phenomenon time
     */
    default void setPhenomenonTime(DateTime min, DateTime max) {
        setMinPhenomenonTime(min);
        setMaxPhenomenonTime(max);
    }

    /**
     * Update the global phenomenon time by extending the global envelope to include the specified {@code ITime}.
     *
     * @param eventTime the time to include
     */
    default void updatePhenomenonTime(Time eventTime) {
        Objects.requireNonNull(eventTime, EVENT_TIME);
        final TimePeriod tp = toTimePeriod(eventTime);
        if (!hasMinPhenomenonTime() || getMinPhenomenonTime().isAfter(tp.getStart())) {
            setMinPhenomenonTime(tp.getStart());
        }
        if (!hasMaxPhenomenonTime() || getMaxPhenomenonTime().isBefore(tp.getEnd())) {
            setMaxPhenomenonTime(tp.getEnd());
        }
    }

    /**
     * Recalculates the global phenomenon time envelope based on the current offering phenomenon time envelopes.
     */
    void recalculatePhenomenonTime();

    /**
     * Sets the new global result envelope.
     *
     * @param min the minimal result time
     * @param max the maximal result time
     */
    default void setResultTime(DateTime min, DateTime max) {
        setMinResultTime(min);
        setMaxResultTime(max);
    }

    /**
     * Update the global result time by extending the global envelope to include the specified {@code ITime}.
     *
     * @param resultTime the time to include
     */
    default void updateResultTime(Time resultTime) {
        if (resultTime == null) {
            return;
        }
        final TimePeriod tp = toTimePeriod(resultTime);
        if (!hasMinResultTime() || getMinResultTime().isAfter(tp.getStart())) {
            setMinResultTime(tp.getStart());
        }
        if (!hasMaxResultTime() || getMaxResultTime().isBefore(tp.getEnd())) {
            setMaxResultTime(tp.getEnd());
        }
    }

    /**
     * Recalculates the global result time envelope based on the current offering result time envelopes.
     */
    void recalculateResultTime();

    /**
     * Remove the maximal phenomenon time for the specified offering.
     *
     * @param offering the offering
     */
    void removeMaxPhenomenonTimeForOffering(String offering);

    /**
     * Remove the minimal phenomenon time for the specified offering.
     *
     * @param offering the offering
     */
    void removeMinPhenomenonTimeForOffering(String offering);

    /**
     * Remove the maximal phenomenon time for the specified procedure.
     *
     * @param procedure the procedure
     */
    void removeMaxPhenomenonTimeForProcedure(String procedure);

    /**
     * Remove the minimal phenomenon time for the specified procedure.
     *
     * @param procedure the procedure
     */
    void removeMinPhenomenonTimeForProcedure(String procedure);

    /**
     * Remove the maximal result time for the specified offering.
     *
     * @param offering the offering
     */
    void removeMaxResultTimeForOffering(String offering);

    /**
     * Remove the minimal result time for the specified offering.
     *
     * @param offering the offering
     */
    void removeMinResultTimeForOffering(String offering);

    /**
     * Creates a {@code TimePeriod} for the specified {@code ITime}.
     *
     * @param time the abstract time
     *
     * @return the period describing the abstract time
     */
    static TimePeriod toTimePeriod(Time time) {
        if (time instanceof TimeInstant) {
            DateTime instant = ((TimeInstant) time).getValue();
            return new TimePeriod(instant, instant);
        } else {
            return (TimePeriod) time;
        }
    }
}
