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
package org.n52.sos.aquarius.ds;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import org.n52.sos.aquarius.pojo.data.Grade;
import org.n52.sos.aquarius.pojo.data.Point;

public class GradeChecker implements Checker, Serializable {

    private static final long serialVersionUID = 1L;

    private Set<Grade> grades = new LinkedHashSet<>();

    public GradeChecker addGrade(Grade grade) {
        if (grade != null) {
            this.grades.add(grade);
        }
        return this;
    }

    public Point check(Point point) {
        if (point != null) {
            for (Grade grade : grades) {
                grade.applyGrade(point);
            }
        }
        return point;
    }
}
