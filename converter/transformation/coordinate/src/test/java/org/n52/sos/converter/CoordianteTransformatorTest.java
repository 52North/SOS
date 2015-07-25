package org.n52.sos.converter;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.n52.sos.ogc.ows.OwsExceptionReport;

import com.google.common.annotations.VisibleForTesting;

public class CoordianteTransformatorTest {
	
	private final CoordianteTransformator transformer = new CoordianteTransformator();
	
	private final int EPSG = 4326;
	
	private final int TARGET_EPSG = 31466;
	
	private final String HTTP_PREFIX = "http://test.org/i/am/an/epsg/";
	
	private final String URN_PREFIX = "urn:ogc:epsg:";
	
	private final String EPSG_PREFIX = "EPSG::";
	
	@Test
	public void testGetCrsFromStringHttp() throws OwsExceptionReport {
		assertThat(transformer.getCrsFromString(HTTP_PREFIX + EPSG), is(EPSG));
	}
	
	@Test
	public void testGetCrsFromStringUrn() throws OwsExceptionReport {
		assertThat(transformer.getCrsFromString(URN_PREFIX + EPSG), is(EPSG));
	}
	
	@Test
	public void testGetCrsFromStringEpsg() throws OwsExceptionReport {
		assertThat(transformer.getCrsFromString(EPSG_PREFIX + EPSG), is(EPSG));
	}
	
	@Test(expected = OwsExceptionReport.class)
	public void testGetCrsFromStringException() throws OwsExceptionReport {
		transformer.getCrsFromString(HTTP_PREFIX);
	}
	
	
	@Test
	public void testTransformReferenceFrameHttp() {
		assertThat(transformer.transformReferenceFrame(HTTP_PREFIX + EPSG, EPSG, TARGET_EPSG), is(HTTP_PREFIX + TARGET_EPSG));
	}
	
	@Test
	public void testTransformReferenceFrameUrn() {
		assertThat(transformer.transformReferenceFrame(URN_PREFIX + EPSG, EPSG, TARGET_EPSG), is(URN_PREFIX + TARGET_EPSG));
	}
	
	@Test
	public void testTransformReferenceFrameEpsg() {
		assertThat(transformer.transformReferenceFrame(EPSG_PREFIX + EPSG, EPSG, TARGET_EPSG), is(EPSG_PREFIX + TARGET_EPSG));
	}
	
	@Test
	public void testTransformReferenceFrameNotTransformed() {
		assertThat(transformer.transformReferenceFrame(HTTP_PREFIX + EPSG, -1, -1), is(HTTP_PREFIX + EPSG));
	}

}

