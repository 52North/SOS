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

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.HibernateCriterionHelper;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.observation.BaseObservation;
import org.n52.sos.ds.hibernate.entities.observation.ValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.Series;
import org.n52.sos.ds.hibernate.entities.observation.series.valued.CountValuedSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.valued.NumericValuedSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.valued.ProfileValuedSeriesObservation;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.filter.ComparisonFilter;

public class ResultFilterRestrictions {
    
    public static void addResultFilterExpression(Criteria c, ComparisonFilter resultFilter, ResultFilterClasses resultFilterClasses) throws CodedException {
        switch (resultFilter.getOperator()) {
            case PropertyIsEqualTo:
                if (isNumeric(resultFilter.getValue())) {
                    DetachedCriteria count = createEqDC(createDC(resultFilterClasses.getCount()), Integer.parseInt(resultFilter.getValue()));
                    DetachedCriteria numeric = createEqDC(createDC(resultFilterClasses.getNumeric()), Double.parseDouble(resultFilter.getValue()));
                    c.add(Restrictions.disjunction()
                            .add(getSubquery(count))
                            .add(getSubquery(numeric))
//                            .add(getSubquery(getComplexSubquery(createDC(resultFilterClasses.getComplex()), count, numeric)))
                            .add(getSubquery(getProfileSubquery(createDC(resultFilterClasses.getProfile()), count, numeric))));
                } else {
                    DetachedCriteria category = createEqDC(createDC(resultFilterClasses.getCategory()), resultFilter.getValue());
                    DetachedCriteria text = createEqDC(createDC(resultFilterClasses.getText()), resultFilter.getValue());
                    c.add(Restrictions.disjunction()
                            .add(getSubquery(category))
                            .add(getSubquery(text))
                            .add(getSubquery(getComplexSubquery(createDC(resultFilterClasses.getComplex()), category, text)))
                            .add(getSubquery(getProfileSubquery(createDC(resultFilterClasses.getProfile()), category, text)))); 
                }
                break;
            case PropertyIsBetween:
                if (isNumeric(resultFilter.getValue()) && isNumeric(resultFilter.getValueUpper())) {
                    c.add(Restrictions.disjunction()
                            .add(getSubquery(createBetweenDC(createDC(resultFilterClasses.getCount()), Integer.parseInt(resultFilter.getValue()),
                                    Integer.parseInt(resultFilter.getValueUpper()))))
                            .add(getSubquery(createBetweenDC(createDC(resultFilterClasses.getNumeric()), Double.parseDouble(resultFilter.getValue()),
                                    Double.parseDouble(resultFilter.getValueUpper())))));
                } else {
                    throw new NoApplicableCodeException();
                }
                break;
            case PropertyIsGreaterThan:
                if (isNumeric(resultFilter.getValue())) {
                    c.add(Restrictions.disjunction()
                            .add(getSubquery(createGtDC(createDC(resultFilterClasses.getCount()), Integer.parseInt(resultFilter.getValue()))))
                            .add(getSubquery(createGtDC(createDC(resultFilterClasses.getNumeric()), Double.parseDouble(resultFilter.getValue())))));
                } else {
                    throw new NoApplicableCodeException();
                }
                break;
            case PropertyIsGreaterThanOrEqualTo:
                if (isNumeric(resultFilter.getValue())) {
                    c.add(Restrictions.disjunction()
                            .add(getSubquery(createGeDC(createDC(resultFilterClasses.getCount()), Integer.parseInt(resultFilter.getValue()))))
                            .add(getSubquery(createGeDC(createDC(resultFilterClasses.getNumeric()), Double.parseDouble(resultFilter.getValue())))));
                } else {
                    throw new NoApplicableCodeException();
                }
                break;
            case PropertyIsLessThan:
                if (isNumeric(resultFilter.getValue())) {
                    c.add(Restrictions.disjunction()
                            .add(getSubquery(createLtDC(createDC(resultFilterClasses.getCount()), Integer.parseInt(resultFilter.getValue()))))
                            .add(getSubquery(createLtDC(createDC(resultFilterClasses.getNumeric()), Double.parseDouble(resultFilter.getValue())))));
                } else {
                    throw new NoApplicableCodeException();
                }
                break;
            case PropertyIsLessThanOrEqualTo:
                if (isNumeric(resultFilter.getValue())) {
                    c.add(Restrictions.disjunction()
                            .add(getSubquery(createLeDC(createDC(resultFilterClasses.getCount()), Integer.parseInt(resultFilter.getValue()))))
                            .add(getSubquery(createLeDC(createDC(resultFilterClasses.getNumeric()), Double.parseDouble(resultFilter.getValue())))));
                } else {
                    throw new NoApplicableCodeException();
                }
                break;
            case PropertyIsLike:
                c.add(Restrictions.disjunction()
                        .add(getSubquery(createLikeDC(createDC(resultFilterClasses.getCategory()), resultFilter)))
                        .add(getSubquery(createLikeDC(createDC(resultFilterClasses.getText()), resultFilter))));
                // TODO
                break;
            default:
                break;
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

    private static Criterion getSubquery(DetachedCriteria dc) {
        return Subqueries.propertyIn(BaseObservation.OBS_ID, dc);
    }
    
    private static DetachedCriteria createDC(Class<?> clazz) {
        return DetachedCriteria.forClass(clazz);
    }

    private static DetachedCriteria createEqDC(DetachedCriteria dc, Object value) {
        return dc.add(Restrictions.eq(ValuedObservation.VALUE, value))
                .setProjection(Projections.property(BaseObservation.OBS_ID));
    }

    private static DetachedCriteria createGtDC(DetachedCriteria dc, Object value) {
        return dc.add(Restrictions.gt(ValuedObservation.VALUE, value))
                .setProjection(Projections.property(BaseObservation.OBS_ID));
    }

    private static DetachedCriteria createGeDC(DetachedCriteria dc, Object value) {
        return dc.add(Restrictions.ge(ValuedObservation.VALUE, value))
                .setProjection(Projections.property(BaseObservation.OBS_ID));
    }

    private static DetachedCriteria createLtDC(DetachedCriteria dc, Object value) {
        return dc.add(Restrictions.lt(ValuedObservation.VALUE, value))
                .setProjection(Projections.property(BaseObservation.OBS_ID));
    }

    private static DetachedCriteria createLeDC(DetachedCriteria dc, Object value) {
        return dc.add(Restrictions.le(ValuedObservation.VALUE, value))
                .setProjection(Projections.property(BaseObservation.OBS_ID));
    }

    private static DetachedCriteria createBetweenDC(DetachedCriteria dc, Object lower, Object upper) {
        return dc.add(Restrictions.between(ValuedObservation.VALUE, lower, upper))
                .setProjection(Projections.property(BaseObservation.OBS_ID));
    }

    private static DetachedCriteria createLikeDC(DetachedCriteria dc, ComparisonFilter resultFilter) {
        String value = resultFilter.getValue().replaceAll(resultFilter.getSingleChar(), "_").replaceAll(resultFilter.getWildCard(), "%");
        return dc
                .add(HibernateCriterionHelper.getLikeExpression(ValuedObservation.VALUE, value,
                        resultFilter.getEscapeString(), resultFilter.isMatchCase()))
                .setProjection(Projections.property(BaseObservation.OBS_ID));
    }
    
    private static DetachedCriteria getProfileSubquery(DetachedCriteria dc, DetachedCriteria dcDub1,
            DetachedCriteria dcDub2) {
        dc.setProjection(Projections.property(BaseObservation.OBS_ID));
        dc.createCriteria("value", "v")
//        .add(Restrictions.disjunction().add(Restrictions.and(Restrictions.eq("v.class", NumericValuedSeriesObservation.class), Restrictions.eq("v.value", 8.5))).add(Restrictions.eq("v.class", CountValuedSeriesObservation.class)));
        .add(Subqueries.in("v.childobservationId", dcDub1));
        return dc;
//        return dc.createCriteria(ValuedObservation.VALUE, "v").add(Restrictions.disjunction().add(Subqueries.in("v." + BaseObservation.OBS_ID, dcDub1))
//                .add(Subqueries.in("v." + BaseObservation.OBS_ID, dcDub2)))
//        .setProjection(Projections.property(BaseObservation.OBS_ID));
        

//        return dc
//                .add(Restrictions.disjunction().add(Subqueries.in(BaseObservation.OBS_ID, dcDub1))
//                        .add(Subqueries.in(BaseObservation.OBS_ID, dcDub2)))
//                .setProjection(Projections.property(BaseObservation.OBS_ID));
        
//        HibernateHelper.getSqlString(DetachedCriteria.forClass(ProfileValuedSeriesObservation.class)
//                .createCriteria("value", "v").add(Restrictions.eq("class", NumericValuedSeriesObservation.class))
//                .getExecutableCriteria(session));
    }

    private static DetachedCriteria getComplexSubquery(DetachedCriteria dc, DetachedCriteria dcDub1,
            DetachedCriteria dcDub2) {
        return dc
                .add(Restrictions.disjunction().add(Subqueries.in("v." + BaseObservation.OBS_ID, dcDub1))
                        .add(Subqueries.in("v." + BaseObservation.OBS_ID, dcDub2)))
                .setProjection(Projections.property(BaseObservation.OBS_ID));
    }
    

}
