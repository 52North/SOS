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
package org.n52.sos.request.operator;

import java.util.Collections;
import java.util.Set;

import org.n52.sos.ds.AbstractDescribeSensorDAO;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.DescribeSensorRequest;
import org.n52.sos.response.DescribeSensorResponse;
import org.n52.sos.util.SosHelper;

public class AqdDescribeSensorOperatorV10
		extends
		AbstractAqdRequestOperator<AbstractDescribeSensorDAO, DescribeSensorRequest, DescribeSensorResponse> {
	private static final String OPERATION_NAME = SosConstants.Operations.DescribeSensor
			.name();

	public AqdDescribeSensorOperatorV10() {
		super(OPERATION_NAME, DescribeSensorRequest.class);
	}

	@Override
	public Set<String> getConformanceClasses() {
		return Collections.emptySet();
	}

	@Override
	public DescribeSensorResponse receive(DescribeSensorRequest request)
			throws OwsExceptionReport {
		return (DescribeSensorResponse) changeResponseServiceVersion(getDao()
				.getSensorDescription(
						(DescribeSensorRequest) changeRequestServiceVersion(request)));
	}

	@Override
	protected void checkParameters(DescribeSensorRequest request)
			throws OwsExceptionReport {
		CompositeOwsException exceptions = new CompositeOwsException();
		try {
			checkServiceParameter(request.getService());
		} catch (OwsExceptionReport owse) {
			exceptions.add(owse);
		}
		try {
			checkSingleVersionParameter(request);
		} catch (OwsExceptionReport owse) {
			exceptions.add(owse);
		}
		try {
			checkProcedureID(request.getProcedure(),
					SosConstants.DescribeSensorParams.procedure.name());
		} catch (OwsExceptionReport owse) {
			exceptions.add(owse);
		}
		try {
			SosHelper.checkProcedureDescriptionFormat(
					request.getProcedureDescriptionFormat(),
					SosConstants.SOS, Sos2Constants.SERVICEVERSION);
		} catch (OwsExceptionReport owse) {
			exceptions.add(owse);
		}
		// if (sosRequest.isSetValidTime()) {
		// exceptions.add(new
		// OptionNotSupportedException().at(Sos2Constants.DescribeSensorParams.validTime)
		// .withMessage("The requested parameter is not supported by this server!"));
		// }
		checkExtensions(request, exceptions);
		exceptions.throwIfNotEmpty();
	}

}
