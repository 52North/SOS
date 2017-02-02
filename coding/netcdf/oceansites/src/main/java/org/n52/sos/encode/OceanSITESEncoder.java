/**
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
package org.n52.sos.encode;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.sos.encode.EncoderKey;
import org.n52.sos.encode.OperationEncoderKey;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.netcdf.data.dataset.AbstractSensorDataset;
import org.n52.sos.netcdf.oceansites.OceanSITESConstants;
import org.n52.sos.netcdf.om.NetCDFObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.response.BinaryAttachmentResponse;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.common.io.Files;

import ucar.nc2.NetcdfFileWriter.Version;

/**
 * Implementation of {@link AbstractOceanSITESEncoder} for OceanSITE netCDF
 * encoding.
 * 
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public class OceanSITESEncoder extends AbstractOceanSITESEncoder {

private static final Logger LOGGER = LoggerFactory.getLogger(OceanSITESEncoder.class);
    
    private final Set<String> MEDIA_TYPES = Sets.newHashSet(OceanSITESConstants.CONTENT_TYPE_NETCDF_OCEANSITES.toString(),
            OceanSITESConstants.CONTENT_TYPE_NETCDF_3_OCEANSITES.toString(), OceanSITESConstants.CONTENT_TYPE_NETCDF_4_OCEANSITES.toString());

    private final Map<String, Map<String, Set<String>>> SUPPORTED_RESPONSE_FORMATS = Collections.singletonMap(
            SosConstants.SOS, (Map<String, Set<String>>) new ImmutableMap.Builder<String, Set<String>>()
            .put(Sos1Constants.SERVICEVERSION, MEDIA_TYPES)
            .put(Sos2Constants.SERVICEVERSION, MEDIA_TYPES)
            .build());

    private final Set<EncoderKey> ENCODER_KEYS = Sets.newHashSet(
          (EncoderKey) new OperationEncoderKey(SosConstants.SOS, Sos1Constants.SERVICEVERSION,
                  SosConstants.Operations.GetObservation, OceanSITESConstants.CONTENT_TYPE_NETCDF_OCEANSITES),
          (EncoderKey) new OperationEncoderKey(SosConstants.SOS, Sos1Constants.SERVICEVERSION,
                  SosConstants.Operations.GetObservation, OceanSITESConstants.CONTENT_TYPE_NETCDF_3_OCEANSITES),
          (EncoderKey) new OperationEncoderKey(SosConstants.SOS, Sos1Constants.SERVICEVERSION,
                  SosConstants.Operations.GetObservation, OceanSITESConstants.CONTENT_TYPE_NETCDF_4_OCEANSITES),
          (EncoderKey) new OperationEncoderKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                  SosConstants.Operations.GetObservation, OceanSITESConstants.CONTENT_TYPE_NETCDF_OCEANSITES),
          (EncoderKey) new OperationEncoderKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                  SosConstants.Operations.GetObservation, OceanSITESConstants.CONTENT_TYPE_NETCDF_3_OCEANSITES),
          (EncoderKey) new OperationEncoderKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                  SosConstants.Operations.GetObservation, OceanSITESConstants.CONTENT_TYPE_NETCDF_4_OCEANSITES));
    
    public OceanSITESEncoder() {
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!",
                Joiner.on(", ").join(ENCODER_KEYS));
    }

    @Override
    public MediaType getContentType() {
        return OceanSITESConstants.CONTENT_TYPE_NETCDF_OCEANSITES;
    }

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
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
    
    protected BinaryAttachmentResponse encodeNetCDFObsToNetcdf(List<NetCDFObservation> netCDFObsList, Version version) throws OwsExceptionReport {
        if (CollectionHelper.isEmpty(netCDFObsList)) {
            throw new NoApplicableCodeException().withMessage("No feature types to encode");
        } else if (netCDFObsList.size() > 1) {
            throwTooManyFeatureTypesOrSensorsException(netCDFObsList, netCDFObsList.size(), null);
        }

        NetCDFObservation netCDFObservation = netCDFObsList.get(0);

        if (CollectionHelper.isEmpty(netCDFObservation.getSensorDatasets())) {
            throw new NoApplicableCodeException().withMessage("No sensors to encode");
        } else if (netCDFObservation.getSensorDatasets().size() > 1) {
            throwTooManyFeatureTypesOrSensorsException(netCDFObsList, null, netCDFObservation.getSensorDatasets().size());
        }

        AbstractSensorDataset sensorDataset = netCDFObservation.getSensorDatasets().get(0);
        File tempDir = Files.createTempDir();
        String filename = getFilename(sensorDataset);
        File netcdfFile = new File(tempDir, filename);
        encodeSensorDataToNetcdf(netcdfFile, sensorDataset, version);                

        BinaryAttachmentResponse response = null;
        try {
            response = new BinaryAttachmentResponse(Files.toByteArray(netcdfFile), getContentType(),
                    String.format(filename, makeDateSafe(new DateTime(DateTimeZone.UTC))));
        } catch (IOException e) {
            throw new NoApplicableCodeException().causedBy(e).withMessage("Couldn't create netCDF file");
        } finally {
            tempDir.delete();
        }

        return response;
    }

    private void throwTooManyFeatureTypesOrSensorsException(List<NetCDFObservation> netCDFObsList,
            Integer numFeatureTypes, Integer numSensors) throws CodedException {
        StringBuilder sb = new StringBuilder();
        sb.append("This encoder (" + OceanSITESConstants.CONTENT_TYPE_NETCDF_OCEANSITES.toString() + ") can only encode a single feature type");
        if (numFeatureTypes != null) {
            sb.append(" (found " + numFeatureTypes + ")");
        }
        sb.append(" and a single sensor");
        if (numSensors != null) {
            sb.append(" (found " + numSensors + ")");
        }
        sb.append(". Change your request to only return a single feature type or use the zipped netCDF encoder ("
                + OceanSITESConstants.CONTENT_TYPE_NETCDF_ZIP_OCEANSITES.toString() + ").");
        throw new UnsupportedEncoderInputException(this, netCDFObsList).withMessage(sb.toString());
    }

}
