/**
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
package org.n52.sos.ds.hibernate.util;

import org.n52.sos.ds.hibernate.entities.observation.valued.CategoryValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.ComplexValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.CountValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.NumericValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.ProfileValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.TextValuedObservation;

public class ResultFilterClasses {
    
    public Class<? extends NumericValuedObservation> numeric;
    public Class<? extends CountValuedObservation> count;
    public Class<? extends TextValuedObservation> text;
    public Class<? extends CategoryValuedObservation> category;
    public Class<? extends ComplexValuedObservation> complex;
    public Class<? extends ProfileValuedObservation> profile;
    

    /**
     * @param numeric
     * @param count
     * @param text
     * @param category
     * @param complex
     * @param profile
     */
    public ResultFilterClasses(
            Class<? extends NumericValuedObservation> numeric, Class<? extends CountValuedObservation> count,
            Class<? extends TextValuedObservation> text, Class<? extends CategoryValuedObservation> category,
            Class<? extends ComplexValuedObservation> complex, Class<? extends ProfileValuedObservation> profile) {
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
    public Class<? extends NumericValuedObservation> getNumeric() {
        return numeric;
    }

    /**
     * @return the count
     */
    public Class<? extends CountValuedObservation> getCount() {
        return count;
    }

    /**
     * @return the text
     */
    public Class<? extends TextValuedObservation> getText() {
        return text;
    }

    /**
     * @return the category
     */
    public Class<? extends CategoryValuedObservation> getCategory() {
        return category;
    }

    /**
     * @return the complex
     */
    protected Class<? extends ComplexValuedObservation> getComplex() {
        return complex;
    }

    /**
     * @return the profile
     */
    protected Class<? extends ProfileValuedObservation> getProfile() {
        return profile;
    }
}