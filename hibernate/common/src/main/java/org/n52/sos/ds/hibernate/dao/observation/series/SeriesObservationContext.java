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
package org.n52.sos.ds.hibernate.dao.observation.series;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.n52.sos.ds.hibernate.dao.observation.ObservationContext;
import org.n52.sos.ds.hibernate.entities.Category;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasSeriesType;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasWriteableObservationContext;
import org.n52.sos.ds.hibernate.entities.observation.series.Series;
import org.n52.sos.ds.hibernate.util.HibernateHelper;

public class SeriesObservationContext extends ObservationContext {

    private Category category;
    
    public void setCategory(Category category) {
        this.category = category;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public boolean isSetCategory() {
        return getCategory() != null;
    }

    @Override
    public void addIdentifierRestrictionsToCritera(Criteria c) {
        super.addIdentifierRestrictionsToCritera(c);
        if (HibernateHelper.isColumnSupported(Series.class, Series.CATEGORY) && isSetCategory()) {
            c.add(Restrictions.eq(Series.CATEGORY, getCategory()));
        }
    }

    @Override
    public void addValuesToSeries(HasWriteableObservationContext contextual) {
        super.addValuesToSeries(contextual);
        if (isSetCategory() && contextual instanceof Series) {
            ((Series) contextual).setCategory(getCategory());
        }
        if (contextual instanceof HasSeriesType && isSetSeriesType()) {
            ((HasSeriesType)contextual).setSeriesType(getSeriesType());
        }
    }
}
