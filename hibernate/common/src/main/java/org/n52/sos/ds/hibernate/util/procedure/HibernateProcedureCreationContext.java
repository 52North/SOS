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
package org.n52.sos.ds.hibernate.util.procedure;

import javax.inject.Inject;

import org.n52.faroe.annotation.Configurable;
import org.n52.iceland.binding.BindingRepository;
import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.convert.ConverterRepository;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.iceland.ogc.ows.OwsServiceMetadataRepository;
import org.n52.iceland.service.operator.ServiceOperatorRepository;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.util.procedure.generator.HibernateProcedureDescriptionGeneratorFactoryRepository;
import org.n52.sos.ds.procedure.AbstractProcedureCreationContext;
import org.n52.sos.service.ProcedureDescriptionSettings;
import org.n52.sos.util.GeometryHandler;
import org.n52.svalbard.decode.DecoderRepository;

@Configurable
public class HibernateProcedureCreationContext
        extends
        AbstractProcedureCreationContext {

    private DaoFactory daoFactory;

    @Inject
    public HibernateProcedureCreationContext(
            OwsServiceMetadataRepository serviceMetadataRepository,
            DecoderRepository decoderRepository,
            HibernateProcedureDescriptionGeneratorFactoryRepository factoryRepository,
            I18NDAORepository i18nr,
            DaoFactory daoFactory,
            ConverterRepository converterRepository,
            GeometryHandler geometryHandler,
            BindingRepository bindingRepository,
            ServiceOperatorRepository serviceOperatorRepository,
            ContentCacheController contentCacheController, ProcedureDescriptionSettings procedureSettings) {
        super(serviceMetadataRepository, decoderRepository, factoryRepository, i18nr, converterRepository,
                geometryHandler, bindingRepository, serviceOperatorRepository, contentCacheController,
                procedureSettings);
        this.daoFactory = daoFactory;
    }

    /**
     * @return the daoFactory
     */
    public DaoFactory getDaoFactory() {
        return daoFactory;
    }

}
