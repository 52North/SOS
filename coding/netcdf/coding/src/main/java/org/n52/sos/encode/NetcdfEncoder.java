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

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.janmayen.http.MediaType;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.response.BinaryAttachmentResponse;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.netcdf.NetcdfConstants;
import org.n52.sos.netcdf.data.dataset.AbstractSensorDataset;
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
 * Implementation of {@link AbstractBasicNetcdfEncoder} for netCDF encoding.
 *
 * @author <a href="mailto:shane@axiomdatascience.com">Shane StClair</a>
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public class NetcdfEncoder extends AbstractBasicNetcdfEncoder {
    private static final Logger LOGGER = LoggerFactory.getLogger(NetcdfEncoder.class);

    private static final Set<EncoderKey> ENCODER_KEYS = Sets.newHashSet(
          (EncoderKey) new OperationResponseEncoderKey(SosConstants.SOS, Sos1Constants.SERVICEVERSION,
                  SosConstants.Operations.GetObservation, NetcdfConstants.CONTENT_TYPE_NETCDF),
          (EncoderKey) new OperationResponseEncoderKey(SosConstants.SOS, Sos1Constants.SERVICEVERSION,
                  SosConstants.Operations.GetObservation, NetcdfConstants.CONTENT_TYPE_NETCDF_3),
          (EncoderKey) new OperationResponseEncoderKey(SosConstants.SOS, Sos1Constants.SERVICEVERSION,
                  SosConstants.Operations.GetObservation, NetcdfConstants.CONTENT_TYPE_NETCDF_4),
          (EncoderKey) new OperationResponseEncoderKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                  SosConstants.Operations.GetObservation, NetcdfConstants.CONTENT_TYPE_NETCDF),
          (EncoderKey) new OperationResponseEncoderKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                  SosConstants.Operations.GetObservation, NetcdfConstants.CONTENT_TYPE_NETCDF_3),
          (EncoderKey) new OperationResponseEncoderKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                  SosConstants.Operations.GetObservation, NetcdfConstants.CONTENT_TYPE_NETCDF_4));

    private final Set<String> MEDIA_TYPES = Sets.newHashSet(NetcdfConstants.CONTENT_TYPE_NETCDF.toString(),
            NetcdfConstants.CONTENT_TYPE_NETCDF_3.toString(), NetcdfConstants.CONTENT_TYPE_NETCDF_4.toString());

    private final Map<String, Map<String, Set<String>>> SUPPORTED_RESPONSE_FORMATS = Collections.singletonMap(
            SosConstants.SOS, (Map<String, Set<String>>) new ImmutableMap.Builder<String, Set<String>>()
            .put(Sos1Constants.SERVICEVERSION, MEDIA_TYPES)
            .put(Sos2Constants.SERVICEVERSION, MEDIA_TYPES)
            .build());

    public NetcdfEncoder() {
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!",
                Joiner.on(", ").join(ENCODER_KEYS));
    }

    @Override
    public MediaType getContentType() {
        return NetcdfConstants.CONTENT_TYPE_NETCDF;
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

    protected BinaryAttachmentResponse encodeNetCDFObsToNetcdf(List<NetCDFObservation> netCDFObsList, Version version)
            throws EncodingException {
        if (CollectionHelper.isEmptyOrNull(netCDFObsList)) {
            throw new EncodingException("No feature types to encode");
        } else if (netCDFObsList.size() > 1) {
            throwTooManyFeatureTypesOrSensorsException(netCDFObsList, netCDFObsList.size(), null);
        }

        NetCDFObservation netCDFObservation = netCDFObsList.get(0);

        if (CollectionHelper.isEmpty(netCDFObservation.getSensorDatasets())) {
            throw new EncodingException("No sensors to encode");
        } else if (netCDFObservation.getSensorDatasets().size() > 1) {
            throwTooManyFeatureTypesOrSensorsException(netCDFObsList, null,
                    netCDFObservation.getSensorDatasets().size());
        }

        AbstractSensorDataset sensorDataset = netCDFObservation.getSensorDatasets().get(0);
        File tempDir = Files.createTempDir();
        String filename = getFilename(sensorDataset);
        File netcdfFile = new File(tempDir, filename);
        try {
            encodeSensorDataToNetcdf(netcdfFile, sensorDataset, version);
            return new BinaryAttachmentResponse(Files.toByteArray(netcdfFile), getContentType(),
                    String.format(filename, makeDateSafe(new DateTime(DateTimeZone.UTC))));
        } catch (IOException e) {
            throw new EncodingException("Couldn't create netCDF file", e);
        } finally {
            LOGGER.debug("Temporary file deleted: {}", tempDir.delete());
        }
    }

    private void throwTooManyFeatureTypesOrSensorsException(List<NetCDFObservation> netCDFObsList,
            Integer numFeatureTypes, Integer numSensors) throws EncodingException {
        StringBuilder sb = new StringBuilder();
        sb.append("This encoder (").append(NetcdfConstants.CONTENT_TYPE_NETCDF.toString())
                .append(") can only encode a single feature type");
        if (numFeatureTypes != null) {
            sb.append(" (found ").append(numFeatureTypes).append(")");
        }
        sb.append(" and a single sensor");
        if (numSensors != null) {
            sb.append(" (").append("found ").append(numSensors).append(")");
        }
        sb.append(". Change your request to only return a single feature type or use the zipped netCDF encoder (")
                .append(NetcdfConstants.CONTENT_TYPE_NETCDF_ZIP.toString()).append(").");
        throw new EncodingException(sb.toString());
    }

}
