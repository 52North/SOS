/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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

import org.joda.time.DateTime;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public interface TemporalCache {
    /**
     * @return the maximal phenomenon time for all observations
     */
    DateTime getMaxPhenomenonTime();

    /**
     * @return if the maximal phenomenon time is set
     */
    boolean hasMaxPhenomenonTime();

    /**
     * Returns the maximal phenomenon time for the specified offering.
     *
     * @param offering
     *                 the offering identifier
     *
     * @return the maximal phenomenon time for or null if it is not set
     */
    DateTime getMaxPhenomenonTimeForOffering(String offering);

    /**
     * Returns the whether or not the maximal phenomenon time for the specified
     * offering is set.
     *
     * @param offering
     *                 the offering identifier
     *
     * @return if the maximal phenomenon time is set
     */
    boolean hasMaxPhenomenonTimeForOffering(String offering);

    /**
     * Returns the maximal phenomenon time period for the specified procedure.
     *
     * @param procedure
     *                  the procedure identifier
     *
     * @return the maximal phenomenon time for the specified procedure or null
     *         if it is not set
     */
    DateTime getMaxPhenomenonTimeForProcedure(String procedure);

    /**
     * Returns the whether or not the maximal phenomenon time for the specified
     * procedure is set.
     *
     * @param procedure
     *                  the procedure identifier
     *
     * @return if the maximal phenomenon time is set
     */
    boolean hasMaxPhenomenonTimeForProcedure(String procedure);

    /**
     * @return the minimal phenomenon time for all observations
     */
    DateTime getMinPhenomenonTime();

    /**
     * @return if the minimal phenomenon time is set
     */
    boolean hasMinPhenomenonTime();

    /**
     * Returns the minimal phenomenon time for the specified offering.
     *
     * @param offering
     *                 the offering identifier
     *
     * @return the minimal phenomenon time for or null if it is not set
     */
    DateTime getMinPhenomenonTimeForOffering(String offering);

    /**
     * Returns the whether or not the minimal phenomenon time for the specified
     * offering is set.
     *
     * @param offering
     *                 the offering identifier
     *
     * @return if the minimal phenomenon time is set
     */
    boolean hasMinPhenomenonTimeForOffering(String offering);

    /**
     * Returns the minimal phenomenon time period for the specified procedure.
     *
     * @param procedure
     *                  the procedure identifier
     *
     * @return the minimal phenomenon time for the specified procedure or null
     *         if it is not set
     */
    DateTime getMinPhenomenonTimeForProcedure(String procedure);

    /**
     * Returns the whether or not the minimal phenomenon time for the specified
     * procedure is set.
     *
     * @param procedure
     *                  the procedure identifier
     *
     * @return if the minimal phenomenon time is set
     */
    boolean hasMinPhenomenonTimeForProcedure(String procedure);

    /**
     * @return the maximal result time for all observations
     */
    DateTime getMaxResultTime();

    /**
     * @return if the maximal result time is set
     */
    boolean hasMaxResultTime();

    /**
     * Returns the maximal result time for the specified offering.
     *
     * @param offering
     *                 the offering identifier
     *
     * @return the maximal result time for or null if it is not set
     */
    DateTime getMaxResultTimeForOffering(String offering);

    /**
     * Returns the whether or not the maximal result time for the specified
     * offering is set.
     *
     * @param offering
     *                 the offering identifier
     *
     * @return if the maximal result time is set
     */
    boolean hasMaxResultTimeForOffering(String offering);

    /**
     * @return the minimal result time for all observations
     */
    DateTime getMinResultTime();

    /**
     * @return if the minimal result time is set
     */
    boolean hasMinResultTime();

    /**
     * Returns the minimal result time for the specified offering.
     *
     * @param offering
     *                 the offering identifier
     *
     * @return the minimal result time for or null if it is not set
     */
    DateTime getMinResultTimeForOffering(String offering);

    /**
     * Returns the whether or not the minimal result time for the specified
     * offering is set.
     *
     * @param offering
     *                 the offering identifier
     *
     * @return if the minimal result time is set
     */
    boolean hasMinResultTimeForOffering(String offering);

}
