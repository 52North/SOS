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
package org.n52.sos.ogc.sensorML.elements;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class SmlClassifierTest {
	
	@Test public void 
	shouldReturnTrueIfCodeSpaceIsSetAndNotEmpty()
	{
		final String codeSpace = "test-codespace";
		final SmlClassifier smlClassifier = new SmlClassifier("name", "definition", codeSpace, "value");
		assertThat(smlClassifier.isSetCodeSpace(), is(true));
		assertThat(smlClassifier.getCodeSpace(), is(codeSpace));
	}
	
	@Test public void
	shouldReturnFalseIfCodeSpaceIsEmptyOrNotSet()
	{
		final String codeSpace = null;
		final SmlClassifier smlClassifier = new SmlClassifier("name", "definition", codeSpace, "value");
		assertThat(smlClassifier.isSetCodeSpace(), is(false));
		
		smlClassifier.setCodeSpace("");
		assertThat(smlClassifier.isSetCodeSpace(), is(false));
	}

	@Test public void 
	shouldReturnTrueIfDefinitionIsSetAndNotEmpty()
	{
		final String definition = "test-definition";
		final SmlClassifier smlClassifier = new SmlClassifier(null, definition, null, null);
		assertThat(smlClassifier.isSetDefinition(), is(true));
		assertThat(smlClassifier.getDefinition(), is(definition));
	}
	
	@Test public void
	shouldReturnFalseIfDefinitionIsEmptyOrNotSet()
	{
		final String definition = null;
		final SmlClassifier smlClassifier = new SmlClassifier("name", definition, "codeSpace", "value");
		assertThat(smlClassifier.isSetDefinition(), is(false));
		
		smlClassifier.setDefinition("");
		assertThat(smlClassifier.isSetDefinition(), is(false));
	}
}
