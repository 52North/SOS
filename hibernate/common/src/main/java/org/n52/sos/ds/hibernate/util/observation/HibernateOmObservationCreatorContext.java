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
package org.n52.sos.ds.hibernate.util.observation;

import javax.inject.Inject;

import org.n52.faroe.annotation.Configurable;
import org.n52.iceland.binding.BindingRepository;
import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.convert.ConverterRepository;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.iceland.ogc.ows.OwsServiceMetadataRepository;
import org.n52.sos.ds.FeatureQueryHandler;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.util.procedure.HibernateProcedureConverter;
import org.n52.sos.ds.hibernate.util.procedure.generator.HibernateProcedureDescriptionGeneratorFactoryRepository;
import org.n52.sos.ds.observation.AbstractOmObservationCreatorContext;
import org.n52.sos.ds.observation.AdditionalObservationCreatorRepository;
import org.n52.sos.service.profile.ProfileHandler;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.SosHelper;
import org.n52.svalbard.decode.DecoderRepository;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Configurable
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class HibernateOmObservationCreatorContext extends AbstractOmObservationCreatorContext {

    private DaoFactory daoFactory;
    private HibernateProcedureDescriptionGeneratorFactoryRepository procedureDescriptionGeneratorFactoryRepository;
    private HibernateProcedureConverter procedureConverter;
    private FeatureQueryHandler featureQueryHandler;

    @Inject
    public HibernateOmObservationCreatorContext(
            OwsServiceMetadataRepository serviceMetadataRepository,
            I18NDAORepository i18nr,
            DaoFactory daoFactory,
            SosHelper sosHelper,
            ProfileHandler profileHandler,
            AdditionalObservationCreatorRepository additionalObservationCreatorRepository,
            ContentCacheController contentCacheController,
            FeatureQueryHandler featureQueryHandler,
            ConverterRepository converterRepository,
            HibernateProcedureDescriptionGeneratorFactoryRepository procedureDescriptionGeneratorFactoryRepository,
            GeometryHandler geometryHandler,
            DecoderRepository decoderRepository,
            HibernateProcedureConverter procedureConverter,
            BindingRepository bindingRepository) {
        super(serviceMetadataRepository, i18nr, profileHandler, sosHelper, additionalObservationCreatorRepository,
                contentCacheController, converterRepository, geometryHandler, decoderRepository,
                bindingRepository);
        this.daoFactory = daoFactory;
        this.procedureConverter = procedureConverter;
        this.procedureDescriptionGeneratorFactoryRepository = procedureDescriptionGeneratorFactoryRepository;
        this.featureQueryHandler = featureQueryHandler;

    }

    /**
     * @return the daoFactory
     */
    public DaoFactory getDaoFactory() {
        return daoFactory;
    }

    /**
     * @return the procedureDescriptionGeneratorFactoryRepository
     */
    public HibernateProcedureDescriptionGeneratorFactoryRepository getProcedureDescriptionGeneratorFactoryRepository() {
        return procedureDescriptionGeneratorFactoryRepository;
    }

    public HibernateProcedureConverter getProcedureConverter() {
        return procedureConverter;
    }

    /**
     * @return the featureQueryHandler
     */
    public FeatureQueryHandler getFeatureQueryHandler() {
        return featureQueryHandler;
    }
}
