/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.util.procedure.enrich;

import java.util.Collection;
import java.util.Set;

import org.n52.sos.ds.I18NDAO;
import org.n52.sos.i18n.I18NDAORepository;
import org.n52.sos.i18n.LocalizedString;
import org.n52.sos.i18n.metadata.I18NObservablePropertyMetadata;
import org.n52.sos.ogc.om.AbstractPhenomenon;
import org.n52.sos.ogc.ows.OwsExceptionReport;

import com.google.common.base.Optional;

public class ObservablePropertyEnrichment extends ProcedureDescriptionEnrichment {

    @Override
    public void enrich()
            throws OwsExceptionReport {
        if (isSetLocale()) {
            I18NDAO<I18NObservablePropertyMetadata> dao = I18NDAORepository.
                    getInstance().getDAO(I18NObservablePropertyMetadata.class);
            if (dao != null) {
                Set<String> ids = getCache().getObservablePropertiesForProcedure(getIdentifier());
                Collection<I18NObservablePropertyMetadata> metadata = dao.getMetadata(ids);
                for (I18NObservablePropertyMetadata i18n : metadata) {
                    AbstractPhenomenon abstractPhenomenon = new AbstractPhenomenon(i18n.getIdentifier());
                    Optional<LocalizedString> name = i18n.getName().getLocalizationOrDefault(getLocale());
                    if (name.isPresent()) {
                        abstractPhenomenon.addName(name.get().asCodeType());
                    }
                    getDescription().addPhenomenon(abstractPhenomenon);
                }
            }
        }
    }
}
