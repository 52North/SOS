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
package org.n52.sos.ogc.sos;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.vividsolutions.jts.geom.Envelope;

/**
 * @since 4.0.0
 */
public class SosEnvelopeTest {

	final double y12 = 0;
	final double y11 = 1;
	final double x12 = 0;
	final double x11 = 1;
	
	final double x21 = 2;
	final double x22 = 3;
	final double y21 = 2;
	final double y22 = 3;
	final int srid = 4326;
	
	Envelope extensionEnvelope;
	SosEnvelope originEnvelope;
	SosEnvelope emptySosEnvelope;
	Envelope emptyEnvelope;
	
	@Before
	public void setUpEnvelopes() {
		extensionEnvelope = new Envelope(x21, x22, y21, y22);
		originEnvelope = new SosEnvelope(new Envelope(x11, x12, y11, y12),srid);
		emptySosEnvelope = new SosEnvelope();
		emptyEnvelope = new Envelope();
	}
	
	@Test public void
	testExpandToIncludeEmptyEnvelope()
			throws Exception {
		
		originEnvelope.expandToInclude(emptyEnvelope);
		
		assertThat(originEnvelope.getSrid(), is(4326));
		final Envelope envelope = originEnvelope.getEnvelope();
		assertThat(envelope.getMinX(),is(0.0));
		assertThat(envelope.getMaxX(),is(1.0));
		assertThat(envelope.getMinY(),is(0.0));
		assertThat(envelope.getMaxY(),is(1.0));
		assertThat(envelope.getArea(),is(1.0));
	}
	
	@Test public void 
	testExpandToIncludeEnvelope()
			throws Exception {
		originEnvelope.expandToInclude(extensionEnvelope);
		
		assertThat(originEnvelope.getSrid(), is(srid));
		final Envelope envelope = originEnvelope.getEnvelope();
		assertThat(envelope.getMinX(),is(0.0));
		assertThat(envelope.getMaxX(),is(3.0));
		assertThat(envelope.getMinY(),is(0.0));
		assertThat(envelope.getMaxY(),is(3.0));
		assertThat(envelope.getArea(),is(9.0));
	}
	
	@Test public void
	testExpandToIncludeEnvelopeWithNull()
			throws Exception {
		final Envelope e = null;
		originEnvelope.expandToInclude(e);
		assertThat(originEnvelope.isSetSrid(), is(true));
		assertThat(originEnvelope.getSrid(), is(srid));
		assertThat(originEnvelope.isSetEnvelope(), is(true));
		final Envelope envelope = originEnvelope.getEnvelope();
		assertThat(envelope.getArea(), is(1.0));
		assertThat(envelope.getMinX(), is(0.0));
		assertThat(envelope.getMaxX(), is(1.0));
		assertThat(envelope.getMinY(), is(0.0));
		assertThat(envelope.getMaxY(), is(1.0));
	}
	
	@Test public void
	testExpandToIncludeSosEnvelopeWithNull() throws Exception {
		final SosEnvelope e = null;
		originEnvelope.expandToInclude(e);
		assertThat(originEnvelope.isSetSrid(), is(true));
		assertThat(originEnvelope.getSrid(), is(srid));
		assertThat(originEnvelope.isSetEnvelope(), is(true));
		final Envelope envelope = originEnvelope.getEnvelope();
		assertThat(envelope.getArea(), is(1.0));
		assertThat(envelope.getMinX(), is(0.0));
		assertThat(envelope.getMaxX(), is(1.0));
		assertThat(envelope.getMinY(), is(0.0));
		assertThat(envelope.getMaxY(), is(1.0));
	}
	
	@Test public void
	testExpandToIncludeEmptySosEnvelope()
			throws Exception {
		originEnvelope.expandToInclude(emptySosEnvelope);
		
		assertThat(originEnvelope.getSrid(), is(4326));
		final Envelope envelope = originEnvelope.getEnvelope();
		assertThat(envelope.getMinX(),is(0.0));
		assertThat(envelope.getMaxX(),is(1.0));
		assertThat(envelope.getMinY(),is(0.0));
		assertThat(envelope.getMaxY(),is(1.0));
		assertThat(envelope.getArea(),is(1.0));
	}
	
	@Test public void
	testExpandToIncludeSosEnvelope()
			throws Exception {
		originEnvelope.expandToInclude(new SosEnvelope(extensionEnvelope, srid));
		
		assertThat(originEnvelope.getSrid(), is(srid));
		final Envelope envelope = originEnvelope.getEnvelope();
		assertThat(envelope.getMinX(),is(0.0));
		assertThat(envelope.getMaxX(),is(3.0));
		assertThat(envelope.getMinY(),is(0.0));
		assertThat(envelope.getMaxY(),is(3.0));
		assertThat(envelope.getArea(),is(9.0));
}
	
	@Test public void
	testExpandToIncludeEnvelopeToNullEnvelope()
			throws Exception {
		final SosEnvelope nullEnvelope = new SosEnvelope(null, srid);
		
		nullEnvelope.expandToInclude(extensionEnvelope);
		
		assertThat(nullEnvelope.getSrid(), is(srid));
		final Envelope envelope = nullEnvelope.getEnvelope();
		assertThat(envelope.getMinX(),is(2.0));
		assertThat(envelope.getMaxX(),is(3.0));
		assertThat(envelope.getMinY(),is(2.0));
		assertThat(envelope.getMaxY(),is(3.0));
		assertThat(envelope.getArea(),is(1.0));
	}

	@Test public void
	testIsSetSrid()
			throws Exception {
		final SosEnvelope sosEnvelope = new SosEnvelope();
		sosEnvelope.setSrid(52);
		assertThat(new SosEnvelope().isSetSrid(), is(false));
		assertThat(sosEnvelope.isSetSrid(), is(true));
	}

	@Test public void
	testIsSetEnvelope() 
			throws Exception {
		final SosEnvelope sosEnvelope = new SosEnvelope();
		sosEnvelope.setEnvelope(extensionEnvelope);
		assertThat(new SosEnvelope().isSetEnvelope(), is(false));
		assertThat(sosEnvelope.isSetEnvelope(), is(true));
		sosEnvelope.setEnvelope(emptyEnvelope);
		assertThat(sosEnvelope.isSetEnvelope(), is(false));
	}

	@Test public void 
	testIsNotNullOrEmpty() 
			throws Exception {
		assertThat(SosEnvelope.isNotNullOrEmpty(null), is(false));
		assertThat(SosEnvelope.isNotNullOrEmpty(emptySosEnvelope), is(false));
		assertThat(SosEnvelope.isNotNullOrEmpty(originEnvelope), is(true));
		
	}

	@Test public void
	testEquals() {
		assertThat(new SosEnvelope().equals(null), is(false));
		assertThat(new SosEnvelope().equals(new Object()), is(false));
		assertThat(new SosEnvelope().equals(emptySosEnvelope), is(true));
		assertThat(originEnvelope.equals(emptySosEnvelope), is(false));
		final SosEnvelope otherEnvelope = new SosEnvelope(extensionEnvelope, 52);
		final SosEnvelope myEnvelope = new SosEnvelope(extensionEnvelope, 1024);
		assertThat(otherEnvelope.equals(myEnvelope), is(false));
		final SosEnvelope anEnvelope = new SosEnvelope(new Envelope(1.0, 2.0, 3.0, 4.0), 52);
		final SosEnvelope anotherEnvelope = new SosEnvelope(null, 52);
		assertThat(anEnvelope.equals(anotherEnvelope), is(false));
		assertThat(anEnvelope.equals(anEnvelope), is(true));
		assertThat(anotherEnvelope.equals(anEnvelope), is(false));
	}
	
	@Test public void
	testHashCode() {
		final SosEnvelope anEnvelope = new SosEnvelope(new Envelope(1.0, 2.0, 3.0, 4.0), 52);
		final SosEnvelope anotherEnvelope = new SosEnvelope(null, 52);
		assertThat(anEnvelope.hashCode(), is(anEnvelope.hashCode()));
		assertThat(anEnvelope.hashCode(), is(not(anotherEnvelope.hashCode())));
	}

}
