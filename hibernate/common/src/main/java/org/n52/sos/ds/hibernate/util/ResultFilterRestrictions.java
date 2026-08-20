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
package org.n52.sos.ds.hibernate.util;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

import org.n52.series.db.beans.DataEntity;
import org.n52.shetland.ogc.filter.BinaryLogicFilter;
import org.n52.shetland.ogc.filter.ComparisonFilter;
import org.n52.shetland.ogc.filter.Filter;
import org.n52.shetland.ogc.filter.FilterConstants.BinaryLogicOperator;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.InvalidParameterValueException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.sos.ResultFilterConstants;

public class ResultFilterRestrictions {

    public static Predicate getResultFilterExpression(CriteriaBuilder cb, CriteriaQuery<?> query, Root<?> root,
            Filter<?> resultFilter, ResultFilterClasses resultFilterClasses, String column,
            SubQueryIdentifier identifier) throws CodedException {
        return getResultFilterExpression(cb, query, root, resultFilter, resultFilterClasses, column, column,
                identifier);
    }

    public static Predicate getResultFilterExpression(CriteriaBuilder cb, CriteriaQuery<?> query, Root<?> root,
            Filter<?> resultFilter, ResultFilterClasses resultFilterClasses, String subqueryColumn, String column,
            SubQueryIdentifier identifier) throws CodedException {
        if (resultFilter instanceof ComparisonFilter filter) {
            return getResultFilterExpression(cb, query, root, filter, resultFilterClasses, subqueryColumn, column,
                    identifier);
        }
        if (resultFilter instanceof BinaryLogicFilter logicFilter) {
            switch (logicFilter.getOperator()) {
                case And:
                case Or:
                    break;
                default:
                    throw new NoApplicableCodeException().withMessage("BinaryLogicalOpserator '%s' is not supported!",
                            logicFilter.getOperator().name());
            }
            List<Predicate> predicates = new LinkedList<>();
            for (Filter<?> filter : logicFilter.getFilterPredicates()) {
                predicates.add(
                        getResultFilterExpression(cb, query, root, filter, resultFilterClasses, subqueryColumn,
                                column, identifier));
            }
            return logicFilter.getOperator() == BinaryLogicOperator.And ? cb.and(predicates.toArray(new Predicate[0]))
                    : cb.or(predicates.toArray(new Predicate[0]));
        }
        return null;
    }

    private static Predicate getResultFilterExpression(CriteriaBuilder cb, CriteriaQuery<?> query, Root<?> root,
            ComparisonFilter resultFilter, ResultFilterClasses resultFilterClasses, String subqueryColumn,
            String column, SubQueryIdentifier identifier)
            throws NoApplicableCodeException, InvalidParameterValueException {
        List<Subquery<Object>> list = new LinkedList<>();
        List<Subquery<Object>> complexList = new LinkedList<>();
        switch (resultFilter.getOperator()) {
            case PropertyIsEqualTo:
                if (isNumeric(resultFilter.getValue())) {
                    list.add(createEqSQ(cb, query, resultFilterClasses.getNumeric(),
                            getBigDecimal(resultFilter.getValue()), column));
                    complexList.add(createEqSQ(cb, query, resultFilterClasses.getNumeric(),
                            getBigDecimal(resultFilter.getValue()), DataEntity.PROPERTY_ID));
                }
                if (isCount(resultFilter.getValue())) {
                    list.add(createEqSQ(cb, query, resultFilterClasses.getCount(),
                            Integer.parseInt(resultFilter.getValue()), column));
                    complexList.add(createEqSQ(cb, query, resultFilterClasses.getCount(),
                            Integer.parseInt(resultFilter.getValue()), DataEntity.PROPERTY_ID));
                }
                if (!isNumeric(resultFilter.getValue()) && !isCount(resultFilter.getValue())) {
                    list.add(createEqSQ(cb, query, resultFilterClasses.getCategory(), resultFilter.getValue(),
                            column));
                    list.add(createEqSQ(cb, query, resultFilterClasses.getText(), resultFilter.getValue(), column));
                    complexList.add(createEqSQ(cb, query, resultFilterClasses.getCategory(), resultFilter.getValue(),
                            DataEntity.PROPERTY_ID));
                    complexList.add(createEqSQ(cb, query, resultFilterClasses.getText(), resultFilter.getValue(),
                            DataEntity.PROPERTY_ID));
                }
                break;
            case PropertyIsBetween:
                if (isCount(resultFilter.getValue()) && isCount(resultFilter.getValueUpper())) {
                    list.add(createBetweenSQ(cb, query, resultFilterClasses.getCount(),
                            Integer.parseInt(resultFilter.getValue()), Integer.parseInt(resultFilter.getValueUpper()),
                            column));
                    complexList.add(createBetweenSQ(cb, query, resultFilterClasses.getCount(),
                            Integer.parseInt(resultFilter.getValue()), Integer.parseInt(resultFilter.getValueUpper()),
                            DataEntity.PROPERTY_ID));
                }
                if (isNumeric(resultFilter.getValue()) && isNumeric(resultFilter.getValueUpper())) {
                    list.add(createBetweenSQ(cb, query, resultFilterClasses.getNumeric(),
                            getBigDecimal(resultFilter.getValue()), getBigDecimal(resultFilter.getValueUpper()),
                            column));
                    complexList.add(createBetweenSQ(cb, query, resultFilterClasses.getNumeric(),
                            getBigDecimal(resultFilter.getValue()), getBigDecimal(resultFilter.getValueUpper()),
                            DataEntity.PROPERTY_ID));
                }
                if (!isNumeric(resultFilter.getValue()) && !isCount(resultFilter.getValue())) {
                    throw new NoApplicableCodeException();
                }
                break;
            case PropertyIsGreaterThan:
                if (isCount(resultFilter.getValue())) {
                    list.add(createGtSQ(cb, query, resultFilterClasses.getCount(),
                            Integer.parseInt(resultFilter.getValue()), column));
                    complexList.add(createGtSQ(cb, query, resultFilterClasses.getCount(),
                            Integer.parseInt(resultFilter.getValue()), DataEntity.PROPERTY_ID));
                }
                if (isNumeric(resultFilter.getValue())) {
                    list.add(createGtSQ(cb, query, resultFilterClasses.getNumeric(),
                            getBigDecimal(resultFilter.getValue()), column));
                    complexList.add(createGtSQ(cb, query, resultFilterClasses.getNumeric(),
                            getBigDecimal(resultFilter.getValue()), DataEntity.PROPERTY_ID));
                }
                if (!isNumeric(resultFilter.getValue()) && !isCount(resultFilter.getValue())) {
                    throw new NoApplicableCodeException();
                }
                break;
            case PropertyIsGreaterThanOrEqualTo:
                if (isCount(resultFilter.getValue())) {
                    list.add(createGeSQ(cb, query, resultFilterClasses.getCount(),
                            Integer.parseInt(resultFilter.getValue()), column));
                    complexList.add(createGeSQ(cb, query, resultFilterClasses.getCount(),
                            Integer.parseInt(resultFilter.getValue()), DataEntity.PROPERTY_ID));
                }
                if (isNumeric(resultFilter.getValue())) {
                    list.add(createGeSQ(cb, query, resultFilterClasses.getNumeric(),
                            getBigDecimal(resultFilter.getValue()), column));
                    complexList.add(createGeSQ(cb, query, resultFilterClasses.getNumeric(),
                            getBigDecimal(resultFilter.getValue()), DataEntity.PROPERTY_ID));
                }
                if (!isNumeric(resultFilter.getValue()) && !isCount(resultFilter.getValue())) {
                    throw new NoApplicableCodeException();
                }
                break;
            case PropertyIsLessThan:
                if (isCount(resultFilter.getValue())) {
                    list.add(createLtSQ(cb, query, resultFilterClasses.getCount(),
                            Integer.parseInt(resultFilter.getValue()), column));
                    complexList.add(createLtSQ(cb, query, resultFilterClasses.getCount(),
                            Integer.parseInt(resultFilter.getValue()), DataEntity.PROPERTY_ID));
                }
                if (isNumeric(resultFilter.getValue())) {
                    list.add(createLtSQ(cb, query, resultFilterClasses.getNumeric(),
                            getBigDecimal(resultFilter.getValue()), column));
                    complexList.add(createLtSQ(cb, query, resultFilterClasses.getNumeric(),
                            getBigDecimal(resultFilter.getValue()), DataEntity.PROPERTY_ID));
                }
                if (!isNumeric(resultFilter.getValue()) && !isCount(resultFilter.getValue())) {
                    throw new NoApplicableCodeException();
                }
                break;
            case PropertyIsLessThanOrEqualTo:
                if (isCount(resultFilter.getValue())) {
                    list.add(createLeSQ(cb, query, resultFilterClasses.getCount(),
                            Integer.parseInt(resultFilter.getValue()), column));
                    complexList.add(createLeSQ(cb, query, resultFilterClasses.getCount(),
                            Integer.parseInt(resultFilter.getValue()), DataEntity.PROPERTY_ID));
                }
                if (isNumeric(resultFilter.getValue())) {
                    list.add(createLeSQ(cb, query, resultFilterClasses.getNumeric(),
                            getBigDecimal(resultFilter.getValue()), column));
                    complexList.add(createLeSQ(cb, query, resultFilterClasses.getNumeric(),
                            getBigDecimal(resultFilter.getValue()), DataEntity.PROPERTY_ID));
                }
                if (!isNumeric(resultFilter.getValue()) && !isCount(resultFilter.getValue())) {
                    throw new NoApplicableCodeException();
                }
                break;
            case PropertyIsLike:
                list.add(createLikeSQ(cb, query, resultFilterClasses.getCategory(), resultFilter, column));
                list.add(createLikeSQ(cb, query, resultFilterClasses.getText(), resultFilter, column));
                complexList.add(
                        createLikeSQ(cb, query, resultFilterClasses.getCategory(), resultFilter,
                                DataEntity.PROPERTY_ID));
                complexList.add(
                        createLikeSQ(cb, query, resultFilterClasses.getText(), resultFilter,
                                DataEntity.PROPERTY_ID));
                break;
            default:
                throw new InvalidParameterValueException(ResultFilterConstants.RESULT_FILTER + ".operator",
                        resultFilter.getOperator().toString());
        }
        if (!complexList.isEmpty()) {
            if (identifier == null) {
                if (HibernateHelper.isEntitySupported(resultFilterClasses.getProfile())) {
                    list.add(createComplexSQ(cb, query, resultFilterClasses.getProfile(), complexList, column));
                }
                if (HibernateHelper.isEntitySupported(resultFilterClasses.getComplex())) {
                    list.add(createComplexSQ(cb, query, resultFilterClasses.getComplex(), complexList, column));
                }
            } else if (identifier == SubQueryIdentifier.Profile
                    && HibernateHelper.isEntitySupported(resultFilterClasses.getProfile())) {
                list.clear();
                list.add(createComplexSQ(cb, query, resultFilterClasses.getProfile(), complexList, column));
            } else if (identifier == SubQueryIdentifier.Complex
                    && HibernateHelper.isEntitySupported(resultFilterClasses.getComplex())) {
                list.clear();
                list.add(createComplexSQ(cb, query, resultFilterClasses.getComplex(), complexList, column));
            }
        }
        if (!list.isEmpty()) {
            if (list.size() > 1) {
                Predicate[] predicates = list.stream()
                        .map(subquery -> cb.in(root.get(subqueryColumn)).value(subquery))
                        .toArray(Predicate[]::new);
                return cb.or(predicates);
            } else {
                return cb.in(root.get(subqueryColumn)).value(list.get(0));
            }
        }
        return null;
    }

    private static boolean isNumeric(String value) {
        try {
            Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private static boolean isCount(String value) {
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private static Subquery<Object> createEqSQ(CriteriaBuilder cb, CriteriaQuery<?> query, Class<?> clazz,
            Object value, String column) {
        Subquery<Object> sq = query.subquery(Object.class);
        Root<?> sqRoot = sq.from(clazz);
        sq.select(sqRoot.<Object>get(column));
        sq.where(cb.equal(sqRoot.get(DataEntity.PROPERTY_VALUE), value));
        return sq;
    }

    private static Subquery<Object> createGtSQ(CriteriaBuilder cb, CriteriaQuery<?> query, Class<?> clazz,
            BigDecimal value, String column) {
        Subquery<Object> sq = query.subquery(Object.class);
        Root<?> sqRoot = sq.from(clazz);
        sq.select(sqRoot.<Object>get(column));
        sq.where(cb.greaterThan(sqRoot.<BigDecimal>get(DataEntity.PROPERTY_VALUE), value));
        return sq;
    }

    private static Subquery<Object> createGtSQ(CriteriaBuilder cb, CriteriaQuery<?> query, Class<?> clazz,
            Integer value, String column) {
        Subquery<Object> sq = query.subquery(Object.class);
        Root<?> sqRoot = sq.from(clazz);
        sq.select(sqRoot.<Object>get(column));
        sq.where(cb.greaterThan(sqRoot.<Integer>get(DataEntity.PROPERTY_VALUE), value));
        return sq;
    }

    private static Subquery<Object> createGeSQ(CriteriaBuilder cb, CriteriaQuery<?> query, Class<?> clazz,
            BigDecimal value, String column) {
        Subquery<Object> sq = query.subquery(Object.class);
        Root<?> sqRoot = sq.from(clazz);
        sq.select(sqRoot.<Object>get(column));
        sq.where(cb.greaterThanOrEqualTo(sqRoot.<BigDecimal>get(DataEntity.PROPERTY_VALUE), value));
        return sq;
    }

    private static Subquery<Object> createGeSQ(CriteriaBuilder cb, CriteriaQuery<?> query, Class<?> clazz,
            Integer value, String column) {
        Subquery<Object> sq = query.subquery(Object.class);
        Root<?> sqRoot = sq.from(clazz);
        sq.select(sqRoot.<Object>get(column));
        sq.where(cb.greaterThanOrEqualTo(sqRoot.<Integer>get(DataEntity.PROPERTY_VALUE), value));
        return sq;
    }

    private static Subquery<Object> createLtSQ(CriteriaBuilder cb, CriteriaQuery<?> query, Class<?> clazz,
            BigDecimal value, String column) {
        Subquery<Object> sq = query.subquery(Object.class);
        Root<?> sqRoot = sq.from(clazz);
        sq.select(sqRoot.<Object>get(column));
        sq.where(cb.lessThan(sqRoot.<BigDecimal>get(DataEntity.PROPERTY_VALUE), value));
        return sq;
    }

    private static Subquery<Object> createLtSQ(CriteriaBuilder cb, CriteriaQuery<?> query, Class<?> clazz,
            Integer value, String column) {
        Subquery<Object> sq = query.subquery(Object.class);
        Root<?> sqRoot = sq.from(clazz);
        sq.select(sqRoot.<Object>get(column));
        sq.where(cb.lessThan(sqRoot.<Integer>get(DataEntity.PROPERTY_VALUE), value));
        return sq;
    }

    private static Subquery<Object> createLeSQ(CriteriaBuilder cb, CriteriaQuery<?> query, Class<?> clazz,
            BigDecimal value, String column) {
        Subquery<Object> sq = query.subquery(Object.class);
        Root<?> sqRoot = sq.from(clazz);
        sq.select(sqRoot.<Object>get(column));
        sq.where(cb.lessThanOrEqualTo(sqRoot.<BigDecimal>get(DataEntity.PROPERTY_VALUE), value));
        return sq;
    }

    private static Subquery<Object> createLeSQ(CriteriaBuilder cb, CriteriaQuery<?> query, Class<?> clazz,
            Integer value, String column) {
        Subquery<Object> sq = query.subquery(Object.class);
        Root<?> sqRoot = sq.from(clazz);
        sq.select(sqRoot.<Object>get(column));
        sq.where(cb.lessThanOrEqualTo(sqRoot.<Integer>get(DataEntity.PROPERTY_VALUE), value));
        return sq;
    }

    private static Subquery<Object> createBetweenSQ(CriteriaBuilder cb, CriteriaQuery<?> query, Class<?> clazz,
            BigDecimal lower, BigDecimal upper, String column) {
        Subquery<Object> sq = query.subquery(Object.class);
        Root<?> sqRoot = sq.from(clazz);
        sq.select(sqRoot.<Object>get(column));
        sq.where(cb.between(sqRoot.<BigDecimal>get(DataEntity.PROPERTY_VALUE), lower, upper));
        return sq;
    }

    private static Subquery<Object> createBetweenSQ(CriteriaBuilder cb, CriteriaQuery<?> query, Class<?> clazz,
            Integer lower, Integer upper, String column) {
        Subquery<Object> sq = query.subquery(Object.class);
        Root<?> sqRoot = sq.from(clazz);
        sq.select(sqRoot.<Object>get(column));
        sq.where(cb.between(sqRoot.<Integer>get(DataEntity.PROPERTY_VALUE), lower, upper));
        return sq;
    }

    private static Subquery<Object> createLikeSQ(CriteriaBuilder cb, CriteriaQuery<?> query, Class<?> clazz,
            ComparisonFilter resultFilter, String column) {
        String value = resultFilter.getValue();
        if (resultFilter.getSingleChar() != null) {
            value = resultFilter.getValue().replaceAll(resultFilter.getSingleChar(), "_");
        }
        if (resultFilter.getWildCard() != null) {
            value = resultFilter.getValue().replaceAll(resultFilter.getWildCard(), "%");
        }
        Subquery<Object> sq = query.subquery(Object.class);
        Root<?> sqRoot = sq.from(clazz);
        sq.select(sqRoot.<Object>get(column));
        // Note: passes isMatchCase() straight through as the "ignoreCase" flag, same as the
        // pre-migration code did -- looks backwards, preserved as-is rather than silently fixed.
        sq.where(getLikePredicate(cb, sqRoot.<String>get(DataEntity.PROPERTY_VALUE), value,
                resultFilter.getEscapeString(), resultFilter.isMatchCase()));
        return sq;
    }

    private static Predicate getLikePredicate(CriteriaBuilder cb, Expression<String> path, String value,
            String escapeString, boolean ignoreCase) {
        String pattern = value;
        Character escapeChar = null;
        if (escapeString != null) {
            if (escapeString.length() > 1) {
                pattern = pattern.replace(escapeString, "\\");
                escapeChar = '\\';
            } else {
                escapeChar = escapeString.charAt(0);
            }
        }
        Expression<String> expression = ignoreCase ? cb.lower(path) : path;
        String comparisonValue = ignoreCase ? pattern.toLowerCase() : pattern;
        return escapeChar != null ? cb.like(expression, comparisonValue, escapeChar)
                : cb.like(expression, comparisonValue);
    }

    private static Subquery<Object> createComplexSQ(CriteriaBuilder cb, CriteriaQuery<?> query, Class<?> clazz,
            List<Subquery<Object>> childSubqueries, String column) {
        Subquery<Object> sq = query.subquery(Object.class);
        Root<?> sqRoot = sq.from(clazz);
        sq.select(sqRoot.<Object>get(column));
        Join<?, ?> valueJoin = sqRoot.join(DataEntity.PROPERTY_VALUE);
        Expression<Object> valueId = valueJoin.get(DataEntity.PROPERTY_ID);
        if (childSubqueries.size() > 1) {
            Predicate[] predicates = childSubqueries.stream()
                    .map(childSq -> cb.in(valueId).value(childSq))
                    .toArray(Predicate[]::new);
            sq.where(cb.or(predicates));
        } else {
            sq.where(cb.in(valueId).value(childSubqueries.get(0)));
        }
        return sq;
    }

    private static BigDecimal getBigDecimal(String value) {
        return new BigDecimal(value);
    }

    public static Set<SubQueryIdentifier> getSubQueryIdentifier(ResultFilterClasses resultFilterClasses) {
        Set<SubQueryIdentifier> set = new HashSet<>();
        set.add(SubQueryIdentifier.Simple);
        if (HibernateHelper.isEntitySupported(resultFilterClasses.getComplex())) {
            set.add(SubQueryIdentifier.Complex);
        }
        if (HibernateHelper.isEntitySupported(resultFilterClasses.getProfile())) {
            set.add(SubQueryIdentifier.Profile);
        }
        return set;
    }

    public enum SubQueryIdentifier {
        Simple, Complex, Profile;
    }

}