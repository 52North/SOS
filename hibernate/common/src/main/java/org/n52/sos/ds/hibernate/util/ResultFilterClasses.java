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

import org.n52.series.db.beans.CategoryDataEntity;
import org.n52.series.db.beans.ComplexDataEntity;
import org.n52.series.db.beans.CountDataEntity;
import org.n52.series.db.beans.ProfileDataEntity;
import org.n52.series.db.beans.QuantityDataEntity;
import org.n52.series.db.beans.TextDataEntity;

public class ResultFilterClasses {

    public Class<? extends QuantityDataEntity> numeric;
    public Class<? extends CountDataEntity> count;
    public Class<? extends TextDataEntity> text;
    public Class<? extends CategoryDataEntity> category;
    public Class<? extends ComplexDataEntity> complex;
    public Class<? extends ProfileDataEntity> profile;


    /**
     * @param numeric
     * @param count
     * @param text
     * @param category
     * @param complex
     * @param profile
     */
    public ResultFilterClasses(
            Class<? extends QuantityDataEntity> numeric, Class<? extends CountDataEntity> count,
            Class<? extends TextDataEntity> text, Class<? extends CategoryDataEntity> category,
            Class<? extends ComplexDataEntity> complex, Class<? extends ProfileDataEntity> profile) {
        this.numeric = numeric;
        this.count = count;
        this.text = text;
        this.category = category;
        this.complex = complex;
        this.profile = profile;
    }

    /**
     * @return the numeric
     */
    public Class<? extends QuantityDataEntity> getNumeric() {
        return numeric;
    }

    /**
     * @return the count
     */
    public Class<? extends CountDataEntity> getCount() {
        return count;
    }

    /**
     * @return the text
     */
    public Class<? extends TextDataEntity> getText() {
        return text;
    }

    /**
     * @return the category
     */
    public Class<? extends CategoryDataEntity> getCategory() {
        return category;
    }

    /**
     * @return the complex
     */
    protected Class<? extends ComplexDataEntity> getComplex() {
        return complex;
    }

    /**
     * @return the profile
     */
    protected Class<? extends ProfileDataEntity> getProfile() {
        return profile;
    }
}
