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
package org.n52.sos.ds.procedure.enrich;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.n52.io.request.IoParameters;
import org.n52.series.db.beans.PhenomenonEntity;
import org.n52.sensorweb.server.db.old.dao.DbQuery;
import org.n52.series.db.old.dao.PhenomenonDao;
import org.n52.shetland.ogc.om.AbstractPhenomenon;
import org.n52.shetland.ogc.om.OmObservableProperty;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.ds.ApiQueryHelper;
import org.n52.sos.ds.I18nNameDescriptionAdder;
import org.n52.sos.ds.procedure.AbstractProcedureCreationContext;

import com.google.common.collect.Maps;

public class ObservablePropertyEnrichment extends ProcedureDescriptionEnrichment
        implements ApiQueryHelper, I18nNameDescriptionAdder {

    public ObservablePropertyEnrichment(AbstractProcedureCreationContext ctx) {
        super(ctx);
    }

    @Override
    public void enrich() throws OwsExceptionReport {
        List<PhenomenonEntity> phens = new PhenomenonDao(getSession()).getAllInstances(
                createDbQuery(checkForPublished(getCache().getObservablePropertiesForProcedure(getIdentifier()))));
        for (PhenomenonEntity phen : phens) {
            getDescription().addPhenomenon(createObservableProperty(phen));
        }
    }

    private AbstractPhenomenon createObservableProperty(PhenomenonEntity phen) throws OwsExceptionReport {
        OmObservableProperty observableProperty = new OmObservableProperty(phen.getIdentifier());
        addNameAndDescription(phen, observableProperty, getLocale(), null, false);
        return observableProperty;
    }

    private DbQuery createDbQuery(Collection<String> ids) {
        Map<String, String> map = Maps.newHashMap();
        if (ids != null && !ids.isEmpty()) {
            map.put(IoParameters.PHENOMENA, listToString(ids));
        }
        map.put(IoParameters.MATCH_DOMAIN_IDS, Boolean.toString(true));
        return new DbQuery(IoParameters.createFromSingleValueMap(map));
    }

    private Set<String> checkForPublished(Set<String> ids) {
        Set<String> obsProps = new HashSet<>();
        for (String id : ids) {
            if (getCache().getPublishedObservableProperties().contains(id)) {
                obsProps.add(id);
            }
        }
        return obsProps;
    }
}
