/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.observation;

import javax.inject.Inject;

import org.n52.faroe.annotation.Configurable;
import org.n52.iceland.binding.BindingRepository;
import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.convert.ConverterRepository;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.iceland.ogc.ows.OwsServiceMetadataRepository;
import org.n52.sos.ds.FeatureQueryHandler;
import org.n52.sos.service.profile.ProfileHandler;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.SosHelper;
import org.n52.svalbard.decode.DecoderRepository;
import org.n52.sos.ds.procedure.ProcedureConverter;
import org.n52.sos.ds.procedure.generator.ProcedureDescriptionGeneratorFactoryRepository;

@Configurable
public class OmObservationCreatorContext extends AbstractOmObservationCreatorContext {

    private ProcedureDescriptionGeneratorFactoryRepository procedureDescriptionGeneratorFactoryRepository;
    private ProcedureConverter procedureConverter;

    @Inject
    public OmObservationCreatorContext(OwsServiceMetadataRepository serviceMetadataRepository,
            I18NDAORepository i18nr,
            ProfileHandler profileHandler,
            SosHelper sosHelper,
            AdditionalObservationCreatorRepository additionalObservationCreatorRepository,
            ContentCacheController contentCacheController,
            FeatureQueryHandler featureQueryHandler,
            ConverterRepository converterRepository,
            ProcedureDescriptionGeneratorFactoryRepository procedureDescriptionGeneratorFactoryRepository,
            GeometryHandler geometryHandler,
            DecoderRepository decoderRepository,
            ProcedureConverter procedureConverter,
            BindingRepository bindingRepository) {
        super(serviceMetadataRepository, i18nr, profileHandler, sosHelper, additionalObservationCreatorRepository,
                contentCacheController, featureQueryHandler, converterRepository, geometryHandler, decoderRepository,
                bindingRepository);
        this.procedureConverter = procedureConverter;
        this.procedureDescriptionGeneratorFactoryRepository = procedureDescriptionGeneratorFactoryRepository;
    }


    /**
     * @return the procedureDescriptionGeneratorFactoryRepository
     */
    public ProcedureDescriptionGeneratorFactoryRepository getProcedureDescriptionGeneratorFactoryRepository() {
        return procedureDescriptionGeneratorFactoryRepository;
    }

    public ProcedureConverter getProcedureConverter() {
        return procedureConverter;
    }
}
