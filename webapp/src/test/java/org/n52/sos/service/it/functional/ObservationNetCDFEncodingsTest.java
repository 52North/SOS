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
package org.n52.sos.service.it.functional;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.sos.netcdf.NetcdfConstants;

import com.sun.jna.Native;

import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.dataset.NetcdfDatasets;
import ucar.nc2.jni.netcdf.Nc4prototypes;

public class ObservationNetCDFEncodingsTest extends AbstractObservationEncodingsTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void testSos2GetObsNetcdf() throws IOException {
        testGetObsNetcdf(Sos2Constants.SERVICEVERSION, NetcdfConstants.CONTENT_TYPE_NETCDF.toString(), false);
    }

    @Test
    public void testSos1GetObsNetcdf() throws IOException {
        testGetObsNetcdf(Sos1Constants.SERVICEVERSION, NetcdfConstants.CONTENT_TYPE_NETCDF.toString(), false);
    }

    @Test
    public void testSos2GetObsNetcdfZip() throws IOException {
        testGetObsNetcdf(Sos2Constants.SERVICEVERSION, NetcdfConstants.CONTENT_TYPE_NETCDF_ZIP.toString(), true);
    }

    @Test
    public void testSos1GetObsNetcdfZip() throws IOException {
        testGetObsNetcdf(Sos1Constants.SERVICEVERSION, NetcdfConstants.CONTENT_TYPE_NETCDF_ZIP.toString(), true);
    }

    @Test
    public void testSos2GetObsNetcdf3() throws IOException {
        testGetObsNetcdf(Sos2Constants.SERVICEVERSION, NetcdfConstants.CONTENT_TYPE_NETCDF_3.toString(), false);
    }

    @Test
    public void testSos1GetObsNetcdf3() throws IOException {
        testGetObsNetcdf(Sos1Constants.SERVICEVERSION, NetcdfConstants.CONTENT_TYPE_NETCDF_3.toString(), false);
    }

    @Test
    public void testSos2GetObsNetcdf3Zip() throws IOException {
        testGetObsNetcdf(Sos2Constants.SERVICEVERSION, NetcdfConstants.CONTENT_TYPE_NETCDF_3_ZIP.toString(), true);
    }

    @Test
    public void testSos1GetObsNetcdf3Zip() throws IOException {
        testGetObsNetcdf(Sos1Constants.SERVICEVERSION, NetcdfConstants.CONTENT_TYPE_NETCDF_3_ZIP.toString(), true);
    }

    @Test
    public void testSos2GetObsNetcdf4() throws IOException {
        testGetObsNetcdf(Sos2Constants.SERVICEVERSION, NetcdfConstants.CONTENT_TYPE_NETCDF_4.toString(), false);
    }

    @Test
    public void testSos1GetObsNetcdf4() throws IOException {
        testGetObsNetcdf(Sos1Constants.SERVICEVERSION, NetcdfConstants.CONTENT_TYPE_NETCDF_4.toString(), false);
    }

    @Test
    public void testSos2GetObsNetcdf4Zip() throws IOException {
        testGetObsNetcdf(Sos2Constants.SERVICEVERSION, NetcdfConstants.CONTENT_TYPE_NETCDF_4_ZIP.toString(), true);
    }

    @Test
    public void testSos1GetObsNetcdf4Zip() throws IOException {
        testGetObsNetcdf(Sos1Constants.SERVICEVERSION, NetcdfConstants.CONTENT_TYPE_NETCDF_4_ZIP.toString(), true);
    }

    private void testGetObsNetcdf(String serviceVersion, String responseFormat, boolean isZip) throws IOException {
        // check for netcdf lib before test is run. on debian/ubuntu the package
        // is libnetcdf-dev
        // TODO shouldn't netcdf3 response formats work without this library?
        try {
            Native.load("netcdf", Nc4prototypes.class);
        } catch (UnsatisfiedLinkError e) {
            Assume.assumeNoException("netcdf library not detected, skipping test", e);
        }

        File netcdfFile = testFolder.newFile("52n-sos-netcdf-test.nc");
        try (InputStream inputStream = sendGetObsKvp(serviceVersion, responseFormat).asInputStream();
                FileOutputStream fileOutputStream = new FileOutputStream(netcdfFile)) {
            if (isZip) {
                try (ZipInputStream zis = new ZipInputStream(inputStream)) {
                    zis.getNextEntry();
                    IOUtils.copy(zis, fileOutputStream);
                    zis.closeEntry();
                }
            } else {
                IOUtils.copy(inputStream, fileOutputStream);
            }
        }
        assertThat(netcdfFile, notNullValue());

        try (NetcdfDataset netcdfDataset = NetcdfDatasets.openDataset(netcdfFile.getAbsolutePath())) {
            assertThat(netcdfDataset, notNullValue());
            netcdfDataset.close();
        }
        netcdfFile.delete();
        testFolder.delete();
    }

}
