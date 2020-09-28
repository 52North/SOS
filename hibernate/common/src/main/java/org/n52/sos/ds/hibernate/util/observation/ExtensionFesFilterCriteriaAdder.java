/*
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.util.observation;

import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.parameter.ParameterEntity;
import org.n52.series.db.beans.parameter.TextParameterEntity;
import org.n52.shetland.ogc.filter.BinaryLogicFilter;
import org.n52.shetland.ogc.filter.ComparisonFilter;
import org.n52.shetland.ogc.filter.Filter;
import org.n52.shetland.ogc.filter.FilterConstants.BinaryLogicOperator;
import org.n52.shetland.ogc.filter.FilterConstants.ComparisonOperator;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.extension.Extension;
import org.n52.shetland.util.CollectionHelper;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Class that creates and adds a {@link Criteria} for om:parameter to the query.
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public class ExtensionFesFilterCriteriaAdder {

    public static final String OM_PARAMETER = "om:parameter";

    public static final String OM_NAME = "om:name";

    public static final String OM_VALUE = "om:value";

    public static final String XPATH_OM_PARAMETER_NAMED_VALUE = OM_PARAMETER + "/om:NamedValue";

    public static final String XPATH_OM_PARAMETER_NAMED_VALUE_NAME = XPATH_OM_PARAMETER_NAMED_VALUE + "/" + OM_NAME;

    public static final String XPATH_OM_PARAMETER_NAMED_VALUE_VALUE = XPATH_OM_PARAMETER_NAMED_VALUE + "/" + OM_VALUE;

    public static final String FILTER_NOT_SUPPORTED_LOG = "The requested filter '{}' is not yet supported!";

    public static final String SUPPORTED_BINARY_LOGICAL_OPERATOR =
            "Currently only the binary logical operator 'AND' is supported!";

    public static final String FILERT_NOT_CONTAIN_VALUES = "The filter does not contain values for '{}' or '{}'!";

    private Criteria c;

    private Set<Extension<?>> fesFilterExtensions;

    public ExtensionFesFilterCriteriaAdder(Criteria c, Set<Extension<?>> fesFilterExtensions) {
        this.c = c;
        this.fesFilterExtensions = fesFilterExtensions;
    }

    /**
     * Creates and adds the {@link Criteria} to the {@link Criteria} from the
     * constructor
     *
     * @return Hibernate {@link Criteria}
     * @throws CodedException
     *             If an error occurs or an unsupported filter is queried
     */
    public Criteria add() throws CodedException {
        if (fesFilterExtensions.size() > 1) {
            Conjunction conj = new Conjunction();
            for (Extension<?> swesExtension : fesFilterExtensions) {
                Criterion filter = getFilter((Filter<?>) swesExtension.getValue());
                if (filter != null) {
                    conj.add(getFilter((Filter<?>) swesExtension.getValue()));
                }
            }
            if (conj.conditions().iterator().hasNext()) {
                c.add(conj);
            }
        } else {
            Criterion filter = getFilter((Filter<?>) fesFilterExtensions.iterator().next().getValue());
            if (filter != null) {
                c.add(filter);
            }
        }
        return c;
    }

    private Criterion getFilter(Filter<?> filter) throws CodedException {
        if (filter instanceof BinaryLogicFilter) {
            Map<NameValue, Set<String>> map =
                    mergeNamesValues((BinaryLogicFilter) filter, Maps.<NameValue, Set<String>> newHashMap(), 0);
            checkMap(map);
            return Subqueries.propertyIn(DataEntity.PROPERTY_ID, getDetachedCriteria(getClassFor(null, null), map));
            // current implementation, maybe change in the future
            // return getBinaryLogicFilterCriterion((BinaryLogicFilter) filter);
        } else if (filter instanceof ComparisonFilter) {
            if (isParameterName((ComparisonFilter) filter) || isParameterValue((ComparisonFilter) filter)) {
                Map<NameValue, Set<String>> map = Maps.<NameValue, Set<String>> newHashMap();
                if (isParameterName((ComparisonFilter) filter)) {
                    addValue(NameValue.NAME, (ComparisonFilter) filter, map);
                } else if (isParameterValue((ComparisonFilter) filter)) {
                    addValue(NameValue.VALUE, (ComparisonFilter) filter, map);
                }
                checkMap(map);
                return Subqueries.propertyIn(DataEntity.PROPERTY_ID,
                        getDetachedCriteria(getClassFor(null, null), map));
            }
            throw new NoApplicableCodeException().withMessage(
                    "Currently only the valueReference values '{}' and '{}' "
                            + "are supported! The requested valueReference is '{}'",
                    XPATH_OM_PARAMETER_NAMED_VALUE_NAME, XPATH_OM_PARAMETER_NAMED_VALUE_VALUE,
                    filter.getValueReference());
        }
        throw new NoApplicableCodeException().withMessage(FILTER_NOT_SUPPORTED_LOG,
                filter.getClass().getSimpleName());
    }

    private DetachedCriteria getDetachedCriteria(Class<?> clazz, Map<NameValue, Set<String>> map) {
        DetachedCriteria detachedCriteria = DetachedCriteria.forClass(clazz);
        if (map.containsKey(NameValue.NAME)) {
            detachedCriteria.add(getRestrictionIn(ParameterEntity.NAME, map.get(NameValue.NAME)));
        }
        if (map.containsKey(NameValue.VALUE)) {
            detachedCriteria.add(getRestrictionIn(ParameterEntity.VALUE, map.get(NameValue.VALUE)));
        }
        detachedCriteria.setProjection(Projections.distinct(Projections.property(ParameterEntity.PROPERTY_ID)));
        return detachedCriteria;
    }

    private Class<?> getClassFor(String value, ComparisonOperator operator) {
        // TODO check for other types
        return TextParameterEntity.class;
    }

    private Criterion getRestrictionIn(String name, Set<String> values) {
        return Restrictions.in(name, values);
    }

    private boolean isParameterName(ComparisonFilter filter) {
        return XPATH_OM_PARAMETER_NAMED_VALUE_NAME.equals(filter.getValueReference());
    }

    private boolean isParameterValue(ComparisonFilter filter) {
        return XPATH_OM_PARAMETER_NAMED_VALUE_VALUE.equals(filter.getValueReference());
    }

    private Map<NameValue, Set<String>> mergeNamesValues(BinaryLogicFilter filter, Map<NameValue, Set<String>> map,
            int level) throws CodedException {
        if (level == 0) {
            if (BinaryLogicOperator.And.equals(filter.getOperator())) {
                for (Filter<?> filterPredicate : filter.getFilterPredicates()) {
                    if (filterPredicate instanceof BinaryLogicFilter) {
                        mergeNamesValues((BinaryLogicFilter) filterPredicate, map, level + 1);
                    } else if (filterPredicate instanceof ComparisonFilter) {
                        checkForNameValue((ComparisonFilter) filterPredicate, map);
                    } else {
                        throw new NoApplicableCodeException().withMessage(
                                FILTER_NOT_SUPPORTED_LOG,
                                filterPredicate.getClass().getSimpleName());
                    }
                }
            } else {
                throw new NoApplicableCodeException()
                        .withMessage(SUPPORTED_BINARY_LOGICAL_OPERATOR);
            }
        } else if (level == 1) {
            if (BinaryLogicOperator.And.equals(filter.getOperator())) {
                for (Filter<?> filterPredicate : filter.getFilterPredicates()) {
                    if (filterPredicate instanceof ComparisonFilter) {
                        checkForNameValue((ComparisonFilter) filterPredicate, map);
                    } else {
                        throw new NoApplicableCodeException().withMessage(
                                "Currently only comparison filters are supported at binary logical filter level 1!");
                    }
                }
            } else {
                throw new NoApplicableCodeException()
                        .withMessage(SUPPORTED_BINARY_LOGICAL_OPERATOR);
            }
        } else {
            throw new NoApplicableCodeException()
                    .withMessage("Currently only a filter level for binary logical operators (AND, OR) of 1 is "
                            + "supported (<And><And>...</And><And>...</And></And>!");
        }
        return map;
    }

    private Map<NameValue, Set<String>> checkForNameValue(ComparisonFilter filter, Map<NameValue, Set<String>> map)
            throws CodedException {
        switch (filter.getOperator()) {
            case PropertyIsEqualTo:
                if (isParameterName(filter)) {
                    addValue(NameValue.NAME, filter, map);
                } else if (isParameterValue(filter)) {
                    addValue(NameValue.VALUE, filter, map);
                } else {
                    throw new NoApplicableCodeException().withMessage(
                            "Currently only the valueReference values '{}' and '{}' are supported! "
                                    + "The requested valueReference is '{}'",
                            XPATH_OM_PARAMETER_NAMED_VALUE_NAME, XPATH_OM_PARAMETER_NAMED_VALUE_VALUE,
                            filter.getValueReference());
                }
                return map;
            default:
                throw new NoApplicableCodeException().withMessage("Currently only the operator '{}' is supported!",
                        ComparisonOperator.PropertyIsEqualTo.toString());
        }
    }

    private void addValue(NameValue nameValue, ComparisonFilter filter, Map<NameValue, Set<String>> map) {
        switch (nameValue) {
            case NAME:
                if (!map.containsKey(NameValue.NAME)) {
                    map.put(NameValue.NAME, Sets.<String> newHashSet());
                }
                map.get(NameValue.NAME).add(filter.getValue());
                break;
            case VALUE:
                if (!map.containsKey(NameValue.VALUE)) {
                    map.put(NameValue.VALUE, Sets.<String> newHashSet());
                }
                map.get(NameValue.VALUE).add(filter.getValue());
                break;
            default:
                return;
        }
    }

    private void checkMap(Map<NameValue, Set<String>> map) throws CodedException {
        if (!map.containsKey(NameValue.NAME) && !map.containsKey(NameValue.VALUE)) {
            throw new NoApplicableCodeException().withMessage(FILERT_NOT_CONTAIN_VALUES,
                    XPATH_OM_PARAMETER_NAMED_VALUE_NAME, XPATH_OM_PARAMETER_NAMED_VALUE_VALUE);
        } else {
            if (!CollectionHelper.isNotEmpty(map.get(NameValue.NAME))
                    && !CollectionHelper.isNotEmpty(map.get(NameValue.VALUE))) {
                throw new NoApplicableCodeException().withMessage(
                        FILERT_NOT_CONTAIN_VALUES, XPATH_OM_PARAMETER_NAMED_VALUE_NAME,
                        XPATH_OM_PARAMETER_NAMED_VALUE_VALUE);
            }
        }
    }

    public enum NameValue {
        NAME, VALUE;
    }

}
