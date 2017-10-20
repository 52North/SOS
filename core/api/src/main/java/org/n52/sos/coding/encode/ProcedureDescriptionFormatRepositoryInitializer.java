/*
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.coding.encode;

import javax.inject.Inject;

import org.n52.iceland.service.operator.ServiceOperatorRepository;
import org.n52.janmayen.lifecycle.Constructable;
import org.n52.svalbard.encode.EncoderRepository;

/**
 * Separate initializer for {@link ProcedureDescriptionFormatRepository}
 *
 * @see ProcedureDescriptionFormatRepository#init(org.n52.iceland.service.operator.ServiceOperatorRepository,
 * org.n52.svalbard.encode.EncoderRepository)
 * @author Martin Kiesow
 */
public class ProcedureDescriptionFormatRepositoryInitializer implements Constructable {

    private final EncoderRepository encoderRepository;
    private final ServiceOperatorRepository serviceOperatorRepository;
    private final ProcedureDescriptionFormatRepository procedureDescriptionFormatRepository;

    @Inject
    public ProcedureDescriptionFormatRepositoryInitializer(
            EncoderRepository encoderRepository,
            ServiceOperatorRepository serviceOperatorRepository,
            ProcedureDescriptionFormatRepository procedureDescriptionFormatRepository) {
        this.encoderRepository = encoderRepository;
        this.serviceOperatorRepository = serviceOperatorRepository;
        this.procedureDescriptionFormatRepository = procedureDescriptionFormatRepository;
    }

    @Override
    public void init() {
        this.procedureDescriptionFormatRepository.init(this.serviceOperatorRepository, this.encoderRepository);
    }

}
