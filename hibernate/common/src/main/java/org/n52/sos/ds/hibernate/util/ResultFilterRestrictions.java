/**
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
package org.n52.sos.ds.hibernate.util;

import java.util.LinkedList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.HibernateCriterionHelper;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.n52.sos.ds.hibernate.entities.observation.ValuedObservation;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.filter.ComparisonFilter;

public class ResultFilterRestrictions {
    
    public static void addResultFilterExpression(Criteria c, ComparisonFilter resultFilter, ResultFilterClasses resultFilterClasses, String column) throws CodedException {
        addResultFilterExpression(c, resultFilter, resultFilterClasses, column, column);
    }

    public static void addResultFilterExpression(Criteria c, ComparisonFilter resultFilter, ResultFilterClasses resultFilterClasses, String subqueryColumn, String column) throws CodedException {
        List<Criterion> list = new LinkedList<>();
        switch (resultFilter.getOperator()) {
            case PropertyIsEqualTo:
                if (isNumeric(resultFilter.getValue())) {
                    list.add(getSubquery(createEqDC(createDC(resultFilterClasses.getNumeric()), Double.parseDouble(resultFilter.getValue()), column), subqueryColumn));
                } 
                if (isCount(resultFilter.getValue())) {
                    list.add(getSubquery(createEqDC(createDC(resultFilterClasses.getCount()), Integer.parseInt(resultFilter.getValue()), column), subqueryColumn));
//                    c.add(Restrictions.disjunction()
//                            .add(getSubquery(count, column))
//                            .add(getSubquery(numeric, column)));
//                            .add(getSubquery(getComplexSubquery(createDC(resultFilterClasses.getComplex()), count, numeric)))
//                            .add(getSubquery(getProfileSubquery(createDC(resultFilterClasses.getProfile()), count, numeric))));
                } 
                if (!isNumeric(resultFilter.getValue()) && !isCount(resultFilter.getValue())){
                    list.add(getSubquery(createEqDC(createDC(resultFilterClasses.getCategory()), resultFilter.getValue(), column), subqueryColumn));
                    list.add(getSubquery(createEqDC(createDC(resultFilterClasses.getText()), resultFilter.getValue(), column), subqueryColumn));
//                    c.add(Restrictions.disjunction()
//                            .add(getSubquery(category, column))
//                            .add(getSubquery(text, column)));
//                            .add(getSubquery(getComplexSubquery(createDC(resultFilterClasses.getComplex()), category, text)))
//                            .add(getSubquery(getProfileSubquery(createDC(resultFilterClasses.getProfile()), category, text)))); 
                }
                break;
            case PropertyIsBetween:
                if (isCount(resultFilter.getValue()) && isCount(resultFilter.getValueUpper())) {
                   list.add(getSubquery(createBetweenDC(createDC(resultFilterClasses.getCount()), Integer.parseInt(resultFilter.getValue()),
                                    Integer.parseInt(resultFilter.getValueUpper()), column), subqueryColumn));
                }
                if (isNumeric(resultFilter.getValue()) && isNumeric(resultFilter.getValueUpper())) {
                            list.add(getSubquery(createBetweenDC(createDC(resultFilterClasses.getNumeric()), Double.parseDouble(resultFilter.getValue()),
                                    Double.parseDouble(resultFilter.getValueUpper()), column), subqueryColumn));
                }
                if (!isNumeric(resultFilter.getValue()) && !isCount(resultFilter.getValue())) {
                    throw new NoApplicableCodeException();
                }
                break;
            case PropertyIsGreaterThan:
                if (isCount(resultFilter.getValue())) {
                    list.add(getSubquery(createGtDC(createDC(resultFilterClasses.getCount()), Integer.parseInt(resultFilter.getValue()), column), subqueryColumn));
                }
                if (isNumeric(resultFilter.getValue())) {
                    list.add(getSubquery(createGtDC(createDC(resultFilterClasses.getNumeric()), Double.parseDouble(resultFilter.getValue()), column), subqueryColumn));
                }
                if (!isNumeric(resultFilter.getValue()) && !isCount(resultFilter.getValue())) {
                    throw new NoApplicableCodeException();
                }
                break;
            case PropertyIsGreaterThanOrEqualTo:
                if (isCount(resultFilter.getValue())) {
                    list.add(getSubquery(createGeDC(createDC(resultFilterClasses.getCount()),
                            Integer.parseInt(resultFilter.getValue()), column), subqueryColumn));
                }
                if (isNumeric(resultFilter.getValue())) {
                    list.add(getSubquery(createGeDC(createDC(resultFilterClasses.getNumeric()),
                                    Double.parseDouble(resultFilter.getValue()), column), subqueryColumn));
                }
                if (!isNumeric(resultFilter.getValue()) && !isCount(resultFilter.getValue())) {
                    throw new NoApplicableCodeException();
                }
                break;
            case PropertyIsLessThan:
                if (isCount(resultFilter.getValue())) {
                    list.add(getSubquery(createLtDC(createDC(resultFilterClasses.getCount()), Integer.parseInt(resultFilter.getValue()), column), subqueryColumn));
                }
                if (isNumeric(resultFilter.getValue())) {
                    list.add(getSubquery(createLtDC(createDC(resultFilterClasses.getNumeric()), Double.parseDouble(resultFilter.getValue()), column), subqueryColumn));
                }
                if (!isNumeric(resultFilter.getValue()) && !isCount(resultFilter.getValue())) {
                    throw new NoApplicableCodeException();
                }
                break;
            case PropertyIsLessThanOrEqualTo:
                if (isCount(resultFilter.getValue())) {
                    list.add(getSubquery(createLeDC(createDC(resultFilterClasses.getCount()),
                            Integer.parseInt(resultFilter.getValue()), column), subqueryColumn));
                }
                if (isNumeric(resultFilter.getValue())) {
                    list.add(getSubquery(createLeDC(createDC(resultFilterClasses.getNumeric()),
                            Double.parseDouble(resultFilter.getValue()), column), subqueryColumn));
                }
                if (!isNumeric(resultFilter.getValue()) && !isCount(resultFilter.getValue())) {
                    throw new NoApplicableCodeException();
                }
                break;
            case PropertyIsLike:
                list.add(getSubquery(createLikeDC(createDC(resultFilterClasses.getCategory()), resultFilter, column), subqueryColumn));
                list.add(getSubquery(createLikeDC(createDC(resultFilterClasses.getText()), resultFilter, column), subqueryColumn));
                // TODO
                break;
            default:
                break;
        }
        if (list.size() > 0) {
            if (list.size() > 1) {
                Disjunction d = Restrictions.disjunction();
                for (Criterion criterion : list) {
                    d.add(criterion);
                }
                c.add(d);
            } else {
                c.add(list.iterator().next());
            }
        }
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

    private static DetachedCriteria createEqDC(DetachedCriteria dc, Object value, String column) {
        return dc.add(Restrictions.eq(ValuedObservation.VALUE, value)).setProjection(Projections.property(column));
    }

    private static DetachedCriteria createGtDC(DetachedCriteria dc, Object value, String column) {
        return dc.add(Restrictions.gt(ValuedObservation.VALUE, value)).setProjection(Projections.property(column));
    }

    private static DetachedCriteria createGeDC(DetachedCriteria dc, Object value, String column) {
        return dc.add(Restrictions.ge(ValuedObservation.VALUE, value)).setProjection(Projections.property(column));
    }

    private static DetachedCriteria createLtDC(DetachedCriteria dc, Object value, String column) {
        return dc.add(Restrictions.lt(ValuedObservation.VALUE, value)).setProjection(Projections.property(column));
    }

    private static DetachedCriteria createLeDC(DetachedCriteria dc, Object value, String column) {
        return dc.add(Restrictions.le(ValuedObservation.VALUE, value)).setProjection(Projections.property(column));
    }

    private static DetachedCriteria createBetweenDC(DetachedCriteria dc, Object lower, Object upper, String column) {
        return dc.add(Restrictions.between(ValuedObservation.VALUE, lower, upper))
                .setProjection(Projections.property(column));
    }

    private static DetachedCriteria createLikeDC(DetachedCriteria dc, ComparisonFilter resultFilter, String column) {
        String value = resultFilter.getValue().replaceAll(resultFilter.getSingleChar(), "_")
                .replaceAll(resultFilter.getWildCard(), "%");
        return dc
                .add(HibernateCriterionHelper.getLikeExpression(ValuedObservation.VALUE, value,
                        resultFilter.getEscapeString(), resultFilter.isMatchCase()))
                .setProjection(Projections.property(column));
    }

    private static DetachedCriteria getProfileSubquery(DetachedCriteria dc, DetachedCriteria dcDub1,
            DetachedCriteria dcDub2, String column) {
        dc.setProjection(Projections.property(column));
        dc.createCriteria("value", "v")
                // .add(Restrictions.disjunction().add(Restrictions.and(Restrictions.eq("v.class",
                // NumericValuedSeriesObservation.class),
                // Restrictions.eq("v.value",
                // 8.5))).add(Restrictions.eq("v.class",
                // CountValuedSeriesObservation.class)));
                .add(Subqueries.in("v.childobservationId", dcDub1));
        return dc;
        // return dc.createCriteria(ValuedObservation.VALUE,
        // "v").add(Restrictions.disjunction().add(Subqueries.in("v." + column,
        // dcDub1))
        // .add(Subqueries.in("v." + column, dcDub2)))
        // .setProjection(Projections.property(column));

        // return dc
        // .add(Restrictions.disjunction().add(Subqueries.in(column, dcDub1))
        // .add(Subqueries.in(column, dcDub2)))
        // .setProjection(Projections.property(column));

        // HibernateHelper.getSqlString(DetachedCriteria.forClass(ProfileValuedSeriesObservation.class)
        // .createCriteria("value", "v").add(Restrictions.eq("class",
        // NumericValuedSeriesObservation.class))
        // .getExecutableCriteria(session));
    }

    private static DetachedCriteria getComplexSubquery(DetachedCriteria dc, DetachedCriteria dcDub1,
            DetachedCriteria dcDub2, String column) {
        return dc.add(Restrictions.disjunction().add(Subqueries.in("v." + column, dcDub1))
                .add(Subqueries.in("v." + column, dcDub2))).setProjection(Projections.property(column));
    }

}
