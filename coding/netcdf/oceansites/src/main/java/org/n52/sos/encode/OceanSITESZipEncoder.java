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
package org.n52.sos.encode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.janmayen.http.MediaType;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.response.BinaryAttachmentResponse;
import org.n52.sos.netcdf.data.dataset.AbstractSensorDataset;
import org.n52.sos.netcdf.oceansites.OceanSITESConstants;
import org.n52.sos.netcdf.om.NetCDFObservation;
import org.n52.svalbard.encode.EncoderKey;
import org.n52.svalbard.encode.OperationResponseEncoderKey;
import org.n52.svalbard.encode.exception.EncodingException;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.common.io.Files;

import ucar.nc2.NetcdfFileWriter.Version;

/**
 * Implementation of {@link AbstractOceanSITESEncoder} for OceanSITE netCDF
 * encoding with multiple files as ZIP.
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public class OceanSITESZipEncoder extends AbstractOceanSITESEncoder {
    private static final Logger LOGGER = LoggerFactory.getLogger(OceanSITESZipEncoder.class);

    private static final String DOWNLOAD_FILENAME_FORMAT = "i52n-sos_netcdf_%s.zip";

    private final Set<String> MEDIA_TYPES = Sets.newHashSet(
            OceanSITESConstants.CONTENT_TYPE_NETCDF_ZIP_OCEANSITES.toString(),
            OceanSITESConstants.CONTENT_TYPE_NETCDF_3_ZIP_OCEANSITES.toString(),
            OceanSITESConstants.CONTENT_TYPE_NETCDF_4_ZIP_OCEANSITES.toString());

    private final Map<String, Map<String, Set<String>>> SUPPORTED_RESPONSE_FORMATS = Collections.singletonMap(
            SosConstants.SOS,
            (Map<String, Set<String>>) new ImmutableMap.Builder<String, Set<String>>()
                    .put(Sos1Constants.SERVICEVERSION, MEDIA_TYPES).put(Sos2Constants.SERVICEVERSION, MEDIA_TYPES)
                    .build());

    private final Set<EncoderKey> ENCODER_KEYS = Sets.newHashSet((EncoderKey) new OperationResponseEncoderKey(
            SosConstants.SOS, Sos1Constants.SERVICEVERSION, SosConstants.Operations.GetObservation,
            OceanSITESConstants.CONTENT_TYPE_NETCDF_ZIP_OCEANSITES), (EncoderKey) new OperationResponseEncoderKey(
            SosConstants.SOS, Sos1Constants.SERVICEVERSION, SosConstants.Operations.GetObservation,
            OceanSITESConstants.CONTENT_TYPE_NETCDF_3_ZIP_OCEANSITES), (EncoderKey) new OperationResponseEncoderKey(
            SosConstants.SOS, Sos1Constants.SERVICEVERSION, SosConstants.Operations.GetObservation,
            OceanSITESConstants.CONTENT_TYPE_NETCDF_4_ZIP_OCEANSITES), (EncoderKey) new OperationResponseEncoderKey(
            SosConstants.SOS, Sos2Constants.SERVICEVERSION, SosConstants.Operations.GetObservation,
            OceanSITESConstants.CONTENT_TYPE_NETCDF_ZIP_OCEANSITES), (EncoderKey) new OperationResponseEncoderKey(
            SosConstants.SOS, Sos2Constants.SERVICEVERSION, SosConstants.Operations.GetObservation,
            OceanSITESConstants.CONTENT_TYPE_NETCDF_3_ZIP_OCEANSITES), (EncoderKey) new OperationResponseEncoderKey(
            SosConstants.SOS, Sos2Constants.SERVICEVERSION, SosConstants.Operations.GetObservation,
            OceanSITESConstants.CONTENT_TYPE_NETCDF_4_ZIP_OCEANSITES));

    public OceanSITESZipEncoder() {
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!", Joiner.on(", ")
                .join(ENCODER_KEYS));
    }

    @Override
    public MediaType getContentType() {
        return OceanSITESConstants.CONTENT_TYPE_NETCDF_ZIP_OCEANSITES;
    }

    @Override
    public Set<EncoderKey> getKeys() {
        return Collections.unmodifiableSet(ENCODER_KEYS);
    }

    @Override
    public Set<String> getSupportedResponseFormats(String service, String version) {
        if (SUPPORTED_RESPONSE_FORMATS.get(service) != null) {
            if (SUPPORTED_RESPONSE_FORMATS.get(service).get(version) != null) {
                return SUPPORTED_RESPONSE_FORMATS.get(service).get(version);
            }
        }
        return Collections.emptySet();
    }

    @Override
    protected BinaryAttachmentResponse encodeNetCDFObsToNetcdf(List<NetCDFObservation> netCDFObsList, Version version)
            throws EncodingException, IOException {
        File tempDir = Files.createTempDir();

        for (NetCDFObservation netCDFObs : netCDFObsList) {
            for (AbstractSensorDataset sensorDataset : netCDFObs.getSensorDatasets()) {
                File netcdfFile = new File(tempDir, getFilename(sensorDataset));
                encodeSensorDataToNetcdf(netcdfFile, sensorDataset, version);
            }
        }

        BinaryAttachmentResponse response = null;
        try (ByteArrayOutputStream zipBoas = createZip(tempDir)) {
            response =
                    new BinaryAttachmentResponse(zipBoas.toByteArray(), getContentType(), String.format(
                            DOWNLOAD_FILENAME_FORMAT, makeDateSafe(new DateTime(DateTimeZone.UTC))));
        } finally {
            LOGGER.debug("Temporary file deleted: {}", tempDir.delete());
        }

        return response;
    }

    private static ByteArrayOutputStream createZip(File dirToZip) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        if (dirToZip != null) {
            try (ZipOutputStream zipfile = new ZipOutputStream(bos)) {
                File[] listFiles = dirToZip.listFiles();
                if (listFiles != null) {
                    for (File file : listFiles) {
                        ZipEntry zipentry = new ZipEntry(file.getName());
                        zipfile.putNextEntry(zipentry);
                        zipfile.write(Files.toByteArray(file));
                    }
                }
            }
        }
        return bos;
    }

}
