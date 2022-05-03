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
package org.n52.sos.ds;

import static java.util.stream.Collectors.toSet;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.n52.shetland.ogc.ows.OwsAllowedValues;
import org.n52.shetland.ogc.ows.OwsDomain;
import org.n52.shetland.ogc.ows.OwsNoValues;
import org.n52.shetland.ogc.ows.OwsValue;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.DescribeSensorRequest;
import org.n52.shetland.ogc.sos.response.DescribeSensorResponse;
import org.n52.sos.coding.encode.ProcedureDescriptionFormatRepository;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * interface for getting procedure description for a passed DescribeSensor
 * request from the data source
 *
 * Renamed, in version 4.x called AbstractDescribeSensorDAO
 *
 * @since 5.0.0
 */
@SuppressFBWarnings({"EI_EXPOSE_REP2"})
public abstract class AbstractDescribeSensorHandler extends AbstractSosOperationHandler {

    private ProcedureDescriptionFormatRepository descriptionFormatRepository;

    public AbstractDescribeSensorHandler(String service) {
        super(service, SosConstants.Operations.DescribeSensor.name());
    }

    @Inject
    public void setDescriptionFormatRepository(ProcedureDescriptionFormatRepository descriptionFormatRepository) {
        this.descriptionFormatRepository = descriptionFormatRepository;
    }

    /**
     * Get the procedure description for a procedure
     *
     * @param request
     *            the request
     *
     * @return Returns the DescribeSensor response
     *
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    public abstract DescribeSensorResponse getSensorDescription(DescribeSensorRequest request)
            throws OwsExceptionReport;

    @Override
    protected Set<OwsDomain> getOperationParameters(String service, String version) {
        return Stream.of(getProcedureDescriptionFormatParameter(service, version),
                         getProcedureParameter(service, version))
                .filter(Objects::nonNull)
                .collect(toSet());
    }

    protected OwsDomain getProcedureDescriptionFormatParameter(String service, String version) {
        Enum<?> name = getProcedureDescriptionFormatName(version);
        if (name == null) {
            return null;
        }
        Set<String> pdfs = new HashSet<>(getCache().getRequestableProcedureDescriptionFormat());
        pdfs.addAll(descriptionFormatRepository.getSupportedProcedureDescriptionFormats(service, version));
        if (pdfs.isEmpty()) {
            return new OwsDomain(name, OwsNoValues.instance());
        }
        return new OwsDomain(name, new OwsAllowedValues(pdfs.stream().map(OwsValue::new)));
    }


    private Enum<?> getProcedureDescriptionFormatName(String version) {
        switch (version) {
            case Sos1Constants.SERVICEVERSION:
                return Sos1Constants.DescribeSensorParams.outputFormat;
            case Sos2Constants.SERVICEVERSION:
                return Sos2Constants.DescribeSensorParams.procedureDescriptionFormat;
            default:
                return null;
        }
    }

}
