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
package org.n52.sos.ds.hibernate.util;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.HibernateCriterionHelper;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.n52.series.db.beans.DataEntity;
import org.n52.shetland.ogc.filter.BinaryLogicFilter;
import org.n52.shetland.ogc.filter.ComparisonFilter;
import org.n52.shetland.ogc.filter.Filter;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.InvalidParameterValueException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.sos.ResultFilterConstants;

public class ResultFilterRestrictions {

    private static final String PO_PREFIX = "po";

    private static final String CO_PREFIX = "co";

    public static Criterion getResultFilterExpression(ComparisonFilter resultFilter,
            ResultFilterClasses resultFilterClasses, String column) throws CodedException {
        return getResultFilterExpression(resultFilter, resultFilterClasses, column, column);
    }

    public static Criterion getResultFilterExpression(ComparisonFilter resultFilter,
            ResultFilterClasses resultFilterClasses, String subqueryColumn, String column) throws CodedException {
        return getResultFilterExpression(resultFilter, resultFilterClasses, subqueryColumn, column, null);
    }

    public static Criterion getResultFilterExpression(ComparisonFilter resultFilter,
            ResultFilterClasses resultFilterClasses, String column, SubQueryIdentifier identifier)
            throws CodedException {
        return getResultFilterExpression(resultFilter, resultFilterClasses, column, column, identifier);
    }

    public static Criterion getResultFilterExpression(ComparisonFilter resultFilter,
            ResultFilterClasses resultFilterClasses, String subqueryColumn, String column,
            SubQueryIdentifier identifier) throws NoApplicableCodeException, InvalidParameterValueException {
        List<DetachedCriteria> list = new LinkedList<>();
        List<DetachedCriteria> complexList = new LinkedList<>();
        switch (resultFilter.getOperator()) {
            case PropertyIsEqualTo:
                if (isNumeric(resultFilter.getValue())) {
                    list.add(createEqDC(createDC(resultFilterClasses.getNumeric()),
                            getBigDecimal(resultFilter.getValue()),
                            column));
                    complexList.add(createEqDC(createDC(resultFilterClasses.getNumeric()),
                            getBigDecimal(resultFilter.getValue()), DataEntity.PROPERTY_ID));
                }
                if (isCount(resultFilter.getValue())) {
                    list.add(createEqDC(createDC(resultFilterClasses.getCount()),
                            Integer.parseInt(resultFilter.getValue()), column));
                    complexList.add(createEqDC(createDC(resultFilterClasses.getCount()),
                            Integer.parseInt(resultFilter.getValue()), DataEntity.PROPERTY_ID));
                }
                if (!isNumeric(resultFilter.getValue()) && !isCount(resultFilter.getValue())) {
                    list.add(createEqDC(createDC(resultFilterClasses.getCategory()), resultFilter.getValue(), column));
                    list.add(createEqDC(createDC(resultFilterClasses.getText()), resultFilter.getValue(), column));
                    complexList.add(createEqDC(createDC(resultFilterClasses.getCategory()), resultFilter.getValue(),
                            DataEntity.PROPERTY_ID));
                    complexList.add(createEqDC(createDC(resultFilterClasses.getText()), resultFilter.getValue(),
                            DataEntity.PROPERTY_ID));
                }
                break;
            case PropertyIsBetween:
                if (isCount(resultFilter.getValue()) && isCount(resultFilter.getValueUpper())) {
                    list.add(createBetweenDC(createDC(resultFilterClasses.getCount()),
                            Integer.parseInt(resultFilter.getValue()), Integer.parseInt(resultFilter.getValueUpper()),
                            column));
                    complexList.add(createBetweenDC(createDC(resultFilterClasses.getCount()),
                            Integer.parseInt(resultFilter.getValue()), Integer.parseInt(resultFilter.getValueUpper()),
                            DataEntity.PROPERTY_ID));
                }
                if (isNumeric(resultFilter.getValue()) && isNumeric(resultFilter.getValueUpper())) {
                    list.add(createBetweenDC(createDC(resultFilterClasses.getNumeric()),
                            getBigDecimal(resultFilter.getValue()),
                            getBigDecimal(resultFilter.getValueUpper()), column));
                    complexList.add(createBetweenDC(createDC(resultFilterClasses.getNumeric()),
                            getBigDecimal(resultFilter.getValue()), getBigDecimal(resultFilter.getValueUpper()),
                            DataEntity.PROPERTY_ID));
                }
                if (!isNumeric(resultFilter.getValue()) && !isCount(resultFilter.getValue())) {
                    throw new NoApplicableCodeException();
                }
                break;
            case PropertyIsGreaterThan:
                if (isCount(resultFilter.getValue())) {
                    list.add(createGtDC(createDC(resultFilterClasses.getCount()),
                            Integer.parseInt(resultFilter.getValue()), column));
                    complexList.add(createGtDC(createDC(resultFilterClasses.getCount()),
                            Integer.parseInt(resultFilter.getValue()), DataEntity.PROPERTY_ID));
                }
                if (isNumeric(resultFilter.getValue())) {
                    list.add(createGtDC(createDC(resultFilterClasses.getNumeric()),
                            getBigDecimal(resultFilter.getValue()),
                            column));
                    complexList.add(createGtDC(createDC(resultFilterClasses.getNumeric()),
                            getBigDecimal(resultFilter.getValue()), DataEntity.PROPERTY_ID));
                }
                if (!isNumeric(resultFilter.getValue()) && !isCount(resultFilter.getValue())) {
                    throw new NoApplicableCodeException();
                }
                break;
            case PropertyIsGreaterThanOrEqualTo:
                if (isCount(resultFilter.getValue())) {
                    list.add(createGeDC(createDC(resultFilterClasses.getCount()),
                            Integer.parseInt(resultFilter.getValue()), column));
                    complexList.add(createGeDC(createDC(resultFilterClasses.getCount()),
                            Integer.parseInt(resultFilter.getValue()), DataEntity.PROPERTY_ID));
                }
                if (isNumeric(resultFilter.getValue())) {
                    list.add(createGeDC(createDC(resultFilterClasses.getNumeric()),
                            getBigDecimal(resultFilter.getValue()),
                            column));
                    complexList.add(createGeDC(createDC(resultFilterClasses.getNumeric()),
                            getBigDecimal(resultFilter.getValue()), DataEntity.PROPERTY_ID));
                }
                if (!isNumeric(resultFilter.getValue()) && !isCount(resultFilter.getValue())) {
                    throw new NoApplicableCodeException();
                }
                break;
            case PropertyIsLessThan:
                if (isCount(resultFilter.getValue())) {
                    list.add(createLtDC(createDC(resultFilterClasses.getCount()),
                            Integer.parseInt(resultFilter.getValue()), column));
                    complexList.add(createLtDC(createDC(resultFilterClasses.getCount()),
                            Integer.parseInt(resultFilter.getValue()), DataEntity.PROPERTY_ID));
                }
                if (isNumeric(resultFilter.getValue())) {
                    list.add(createLtDC(createDC(resultFilterClasses.getNumeric()),
                            getBigDecimal(resultFilter.getValue()),
                            column));
                    complexList.add(createLtDC(createDC(resultFilterClasses.getNumeric()),
                            getBigDecimal(resultFilter.getValue()), DataEntity.PROPERTY_ID));
                }
                if (!isNumeric(resultFilter.getValue()) && !isCount(resultFilter.getValue())) {
                    throw new NoApplicableCodeException();
                }
                break;
            case PropertyIsLessThanOrEqualTo:
                if (isCount(resultFilter.getValue())) {
                    list.add(createLeDC(createDC(resultFilterClasses.getCount()),
                            Integer.parseInt(resultFilter.getValue()), column));
                    complexList.add(createLeDC(createDC(resultFilterClasses.getCount()),
                            Integer.parseInt(resultFilter.getValue()), DataEntity.PROPERTY_ID));
                }
                if (isNumeric(resultFilter.getValue())) {
                    list.add(createLeDC(createDC(resultFilterClasses.getNumeric()),
                            getBigDecimal(resultFilter.getValue()),
                            column));
                    complexList.add(createLeDC(createDC(resultFilterClasses.getNumeric()),
                            getBigDecimal(resultFilter.getValue()), DataEntity.PROPERTY_ID));
                }
                if (!isNumeric(resultFilter.getValue()) && !isCount(resultFilter.getValue())) {
                    throw new NoApplicableCodeException();
                }
                break;
            case PropertyIsLike:
                list.add(createLikeDC(createDC(resultFilterClasses.getCategory()), resultFilter, column));
                list.add(createLikeDC(createDC(resultFilterClasses.getText()), resultFilter, column));
                complexList.add(
                    createLikeDC(createDC(resultFilterClasses.getCategory()),
                            resultFilter, DataEntity.PROPERTY_ID));
                complexList
                    .add(createLikeDC(createDC(resultFilterClasses.getText()),
                            resultFilter, DataEntity.PROPERTY_ID));
                break;
            default:
                throw new InvalidParameterValueException(ResultFilterConstants.RESULT_FILTER + ".operator",
                        resultFilter.getOperator().toString());
        }
        if (!complexList.isEmpty()) {
            if (identifier == null) {
                if (HibernateHelper.isEntitySupported(resultFilterClasses.getProfile())) {
                    list.add(createProfileDC(createDC(resultFilterClasses.getProfile(), PO_PREFIX),
                            complexList, column));
                }
                if (HibernateHelper.isEntitySupported(resultFilterClasses.getComplex())) {
                    list.add(createComplexDC(createDC(resultFilterClasses.getComplex(), CO_PREFIX),
                            complexList, column));
                }
            } else if (identifier.equals(SubQueryIdentifier.Profile)
                    && HibernateHelper.isEntitySupported(resultFilterClasses.getProfile())) {
                list.clear();
                list.add(createProfileDC(createDC(resultFilterClasses.getProfile(), PO_PREFIX), complexList, column));
            } else if (identifier.equals(SubQueryIdentifier.Complex)
                    && HibernateHelper.isEntitySupported(resultFilterClasses.getComplex())) {
                list.clear();
                list.add(createComplexDC(createDC(resultFilterClasses.getComplex(), CO_PREFIX), complexList, column));
            }
        }
        if (!list.isEmpty()) {
            if (list.size() > 1) {
                Disjunction d = Restrictions.disjunction();
                for (DetachedCriteria dc : list) {
                    d.add(getSubquery(dc, subqueryColumn));
                }
                return d;
            } else {
                return getSubquery(list.iterator().next(), subqueryColumn);
            }
        }
        return null;
    }

    public static Criterion getResultFilterExpression(Filter<?> resultFilter, ResultFilterClasses resultFilterClasses,
            String column, SubQueryIdentifier identifier) throws CodedException {
        return getResultFilterExpression(resultFilter, resultFilterClasses, column, column, identifier);
    }

    public static Criterion getResultFilterExpression(Filter<?> resultFilter, ResultFilterClasses resultFilterClasses,
            String subqueryColumn, String column, SubQueryIdentifier identifier) throws CodedException {
        if (resultFilter instanceof ComparisonFilter) {
            return getResultFilterExpression((ComparisonFilter) resultFilter,
                    resultFilterClasses, subqueryColumn, column, identifier);
        }
        if (resultFilter instanceof BinaryLogicFilter) {
            Junction junction = null;
            switch (((BinaryLogicFilter) resultFilter).getOperator()) {
                case And:
                    junction = Restrictions.conjunction();
                    break;
                case Or:
                    junction = Restrictions.disjunction();
                    break;
                default:
                    throw new NoApplicableCodeException().withMessage("BinaryLogicalOpserator '%s' is not supported!",
                        ((BinaryLogicFilter) resultFilter).getOperator().name());
            }
            for (Filter<?> filter : ((BinaryLogicFilter) resultFilter).getFilterPredicates()) {
                junction.add(
                        getResultFilterExpression(filter, resultFilterClasses, subqueryColumn, column, identifier));
            }
            return junction;
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

    private static Criterion getSubquery(DetachedCriteria dc, String column) {
        return Subqueries.propertyIn(column, dc);
    }

    private static DetachedCriteria createDC(Class<?> clazz) {
        return DetachedCriteria.forClass(clazz);
    }

    private static DetachedCriteria createDC(Class<?> clazz, String alias) {
        return DetachedCriteria.forClass(clazz, alias);
    }

    private static DetachedCriteria createDC(DetachedCriteria dc, List<DetachedCriteria> list, String column,
            String alias) {
        DetachedCriteria complex =
                dc.setProjection(Projections.property(column)).createAlias(DataEntity.PROPERTY_VALUE, alias);
        if (list.size() > 1) {
            Disjunction d = Restrictions.disjunction();
            for (DetachedCriteria ldc : list) {
                d.add(Subqueries.propertyIn(alias + "." + DataEntity.PROPERTY_ID, ldc));
            }
            complex.add(d);
        } else {
            complex.add(Subqueries.propertyIn(alias + "." + DataEntity.PROPERTY_ID, list.iterator().next()));
        }
        return complex;
    }

    private static DetachedCriteria createEqDC(DetachedCriteria dc, Object value, String column) {
        return dc.add(Restrictions.eq(DataEntity.PROPERTY_VALUE, value)).setProjection(Projections.property(column));
    }

    private static DetachedCriteria createGtDC(DetachedCriteria dc, Object value, String column) {
        return dc.add(Restrictions.gt(DataEntity.PROPERTY_VALUE, value)).setProjection(Projections.property(column));
    }

    private static DetachedCriteria createGeDC(DetachedCriteria dc, Object value, String column) {
        return dc.add(Restrictions.ge(DataEntity.PROPERTY_VALUE, value)).setProjection(Projections.property(column));
    }

    private static DetachedCriteria createLtDC(DetachedCriteria dc, Object value, String column) {
        return dc.add(Restrictions.lt(DataEntity.PROPERTY_VALUE, value)).setProjection(Projections.property(column));
    }

    private static DetachedCriteria createLeDC(DetachedCriteria dc, Object value, String column) {
        return dc.add(Restrictions.le(DataEntity.PROPERTY_VALUE, value)).setProjection(Projections.property(column));
    }

    private static DetachedCriteria createBetweenDC(DetachedCriteria dc, Object lower, Object upper, String column) {
        return dc.add(Restrictions.between(DataEntity.PROPERTY_VALUE, lower, upper))
                .setProjection(Projections.property(column));
    }

    private static DetachedCriteria createLikeDC(DetachedCriteria dc, ComparisonFilter resultFilter, String column) {
        String value = resultFilter.getValue();
        if (resultFilter.getSingleChar() != null) {
            value = resultFilter.getValue().replaceAll(resultFilter.getSingleChar(), "_");
        }
        if (resultFilter.getWildCard() != null) {
            value = resultFilter.getValue().replaceAll(resultFilter.getWildCard(), "%");
        }
        return dc
                .add(HibernateCriterionHelper.getLikeExpression(DataEntity.PROPERTY_VALUE, value,
                        resultFilter.getEscapeString(), resultFilter.isMatchCase()))
                .setProjection(Projections.property(column));
    }

    private static DetachedCriteria createProfileDC(DetachedCriteria dc, List<DetachedCriteria> list, String column) {
        return createDC(dc, list, column, "pv");
    }

    private static DetachedCriteria createComplexDC(DetachedCriteria dc, List<DetachedCriteria> list, String column) {
        return createDC(dc, list, column, "cv");
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
