/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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
package org.hibernate.criterion;

/**
 * Helper class to create Hibernate LikeExpression
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 *
 * @since 4.0.0
 *
 */
public class HibernateCriterionHelper {

    /**
     * Create Hibernate LikeExpression from values
     *
     * @param propertyName
     *            Property name
     * @param value
     *            Requested query value
     * @param matchMode
     *            Match mode
     * @param escapeChar
     *            Escape char
     * @param ignoreCase
     *            Ignore case sensitivity
     * @return LikeExpression
     */
    public static LikeExpression getLikeExpression(String propertyName, String value, MatchMode matchMode,
            Character escapeChar, boolean ignoreCase) {
        return new LikeExpression(propertyName, value, matchMode, escapeChar, ignoreCase);
    }

    /**
     * Create Hibernate LikeExpression from values
     *
     * @param propertyName
     *            Property name
     * @param value
     *            Requested query value
     * @return LikeExpression
     */
    public static LikeExpression getLikeExpression(String propertyName, String value) {
        return new LikeExpression(propertyName, value);
    }

    /**
     * Create Hibernate LikeExpression from values
     *
     * @param propertyName
     *            Property name
     * @param value
     *            Requested query value
     * @param matchMode
     *            Match mode
     * @return LikeExpression
     */
    public static LikeExpression getLikeExpression(String propertyName, String value, MatchMode matchMode) {
        return new LikeExpression(propertyName, value, matchMode);
    }

    /**
     * Create Hibernate LikeExpression from values
     *
     * @param propertyName
     *            Property name
     * @param value
     *            Requested query value
     * @param escapeChar
     *            Escape char
     * @param ignoreCase
     *            Ignore case sensitivity
     * @return LikeExpression
     */
    public static LikeExpression getLikeExpression(String propertyName, String value,
            Character escapeChar, boolean ignoreCase) {
        return new LikeExpression(propertyName, value, escapeChar, ignoreCase);
    }

    /**
     * Create Hibernate LikeExpression from values
     *
     * @param propertyName
     *            Property name
     * @param value
     *            Requested query value
     * @param escapeChar
     *            Escape char
     * @param ignoreCase
     *            Ignore case sensitivity
     * @return LikeExpression
     */
    public static LikeExpression getLikeExpression(String propertyName, String value, String escapeChar,
            boolean ignoreCase) {
        if (escapeChar != null) {
            if (escapeChar.length() > 1) {
                return new LikeExpression(propertyName, value.replace(escapeChar, "\\"), escapeChar.charAt(0),
                        ignoreCase);
            }
            return new LikeExpression(propertyName, value, escapeChar.charAt(0), ignoreCase);
        }
        return new LikeExpression(propertyName, value, null, ignoreCase);
    }

}
