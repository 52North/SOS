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
package org.n52.sos.decode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import net.opengis.sensorml.x20.DataInterfaceType;
import net.opengis.swe.x20.DataRecordPropertyType;
import net.opengis.swe.x20.DataRecordType.Field;

import org.hamcrest.CoreMatchers;
import org.junit.Ignore;
import org.junit.Test;
import org.n52.sos.AbstractBeforeAfterClassSettingsManagerTest;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.v20.SmlDataInterface;
import org.n52.sos.ogc.swe.SweDataRecord;


/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 * @since 4.3.0
 */
public class SensorMLDecoderV20Test extends AbstractBeforeAfterClassSettingsManagerTest {
	
	@Test
	public void shouldDecodeDataInterface() throws OwsExceptionReport{
		DataInterfaceType xbDataInterface = DataInterfaceType.Factory.newInstance();
		SmlDataInterface parsedDataInterface = new SensorMLDecoderV20().parseDataInterfaceType(xbDataInterface);
		assertThat(parsedDataInterface, is(notNullValue()));
	}
	
	@Test @Ignore("Activat again and extend while implementing the DataInterface decoding.")
	public void shouldDecodeDataInterfaceData() throws OwsExceptionReport {
		DataInterfaceType xbDataInterface = DataInterfaceType.Factory.newInstance();
		xbDataInterface.addNewData();
		SmlDataInterface parsedDataInterface = new SensorMLDecoderV20().parseDataInterfaceType(xbDataInterface);
		assertThat(parsedDataInterface.getData(), is(notNullValue()));
	}
	
	@Test @Ignore("Activate again and continue implementation here")
	public void shouldDecodeDataInterfaceInterfaceParameters() throws OwsExceptionReport {
		DataInterfaceType xbDataInterface = DataInterfaceType.Factory.newInstance();
		DataRecordPropertyType xbInterfaceParameters = xbDataInterface.addNewInterfaceParameters();
		Field field = xbInterfaceParameters.addNewDataRecord().addNewField();
		field.setName("test-field-name");
		SmlDataInterface parsedDataInterface = new SensorMLDecoderV20().parseDataInterfaceType(xbDataInterface);
		assertThat(parsedDataInterface.isSetInterfaceParameters(),is(true));
		assertThat(parsedDataInterface.getInterfaceParameters(), CoreMatchers.isA(SweDataRecord.class));
	}

}
