/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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

import java.util.Arrays;
import java.util.Optional;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Junction;
import static org.n52.sos.ds.hibernate.util.HibernateCollectors.toConjunction;
import static org.n52.sos.ds.hibernate.util.HibernateCollectors.toDisjunction;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class MoreRestrictions {

    private MoreRestrictions() {
    }

    @SafeVarargs
    @SuppressWarnings(value = "varargs")
    public static Optional<? extends Criterion> and(Optional<? extends Criterion>... criteria) {
        Conjunction conjunction = Arrays.stream(criteria).filter(Optional::isPresent).map((optional) -> optional.get())
                .collect(toConjunction());
        return Optional.of(conjunction).filter(MoreRestrictions::hasConditions);
    }

    @SafeVarargs
    @SuppressWarnings(value = "varargs")
    public static Optional<? extends Criterion> or(Optional<? extends Criterion>... criteria) {
        Disjunction disjunction = Arrays.stream(criteria).filter(Optional::isPresent).map((optional) -> optional.get())
                .collect(toDisjunction());
        return Optional.of(disjunction).filter(MoreRestrictions::hasConditions);
    }

    public static boolean hasConditions(Junction j) {
        return j.conditions().iterator().hasNext();
    }

}
