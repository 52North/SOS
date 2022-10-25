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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.cache.ctrl.StaticCapabilitiesProvider;
import org.n52.iceland.coding.encode.ResponseWriter;
import org.n52.iceland.coding.encode.ResponseWriterRepository;
import org.n52.janmayen.ConfigLocationProvider;
import org.n52.janmayen.http.MediaType;
import org.n52.janmayen.http.MediaTypes;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.service.GetCapabilitiesRequest;
import org.n52.shetland.ogc.ows.service.GetCapabilitiesResponse;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.svalbard.encode.exception.EncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configurable
public class SosStaticCapabilitiesProvider implements StaticCapabilitiesProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(SosStaticCapabilitiesProvider.class);
    private static final String SOS_100_XML = "SOS_100_XML";
    private static final String SOS_200_XML = "SOS_200_XML";
    private static final String CONFIG_PATH = "config";
    private static final String WEB_INF_PATH = "WEB-INF";
    private static final String FILE_ENDING = ".txt";
    private Boolean provideStaticCapabilities = Boolean.FALSE;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Inject
    private ConfigLocationProvider configLocationProvider;
    @Inject
    private AbstractSosGetCapabilitiesHandler handler;
    @Inject
    private ResponseWriterRepository responseWriterRepository;

    @Setting(PROVIDE_STATIC_CAPABILITIES)
    public void setCronExpression(Boolean provideStaticCapabilities) {
        this.provideStaticCapabilities = provideStaticCapabilities;
    }

    public boolean isProvideStaticCapabilities() {
        return provideStaticCapabilities;
    }

    private Path buildPath() {
        if (configLocationProvider != null && configLocationProvider.get() != null) {
            return Paths.get(configLocationProvider.get(), WEB_INF_PATH, CONFIG_PATH);
        }
        return Paths.get(WEB_INF_PATH, CONFIG_PATH);
    }

    private Path buildPath(String fileName) {
        return buildPath().resolve(fileName);
    }

    public void create() {
        if (isProvideStaticCapabilities()) {
            executor.execute(new CapabilitiesCreator());
        }
    }

    public Lock readLock() {
        return this.lock.readLock();
    }

    public Lock writeLock() {
        return this.lock.writeLock();
    }

    public String get(GetCapabilitiesResponse response) {
        if (isProvideStaticCapabilities()) {
            if (isV2(response)) {
                return get(SOS_200_XML);
            } else if (isV1(response)) {
                return get(SOS_100_XML);
            }
        }
        return "";
    }

    public String get(String identifier) {
        if (isProvideStaticCapabilities()) {
            try {
                readLock().lock();
                Path path = buildPath(identifier + FILE_ENDING);
                File file = path.toFile();
                if (file.exists()) {
                    try (FileInputStream fis = new FileInputStream(file);
                            InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
                            BufferedReader reader = new BufferedReader(isr)) {
                        return reader.lines().collect(Collectors.joining("\n"));
                    } catch (IOException e) {
                        LOGGER.error("Error while getting static capabilities!", e);
                    }
                }
            } finally {
                readLock().unlock();
            }
        }
        return "";
    }

    private static boolean isV2(GetCapabilitiesResponse response) {
        return response.getVersion().equals(Sos2Constants.SERVICEVERSION);
    }

    private static boolean isV1(GetCapabilitiesResponse response) {
        return response.getVersion().equals(Sos1Constants.SERVICEVERSION);
    }

    private class CapabilitiesCreator implements Runnable {

        @Override
        public void run() {
            createSos200();
            createSos100();
        }

        private GetCapabilitiesRequest getRequest(String version) {
            GetCapabilitiesRequest request = new GetCapabilitiesRequest(SosConstants.SOS);
            request.setVersion(version);
            return request;
        }

        private GetCapabilitiesResponse getResponse(String serviceversion) {
            return new GetCapabilitiesResponse(SosConstants.SOS, Sos2Constants.SERVICEVERSION);
        }

        private void createSos100() {
            try {
                GetCapabilitiesResponse response = getResponse(Sos1Constants.SERVICEVERSION);
                handler.createDynamicCapabilities(getRequest(Sos1Constants.SERVICEVERSION), response, false);
                write(response, SOS_100_XML, MediaTypes.APPLICATION_XML);
                // write(response, "SOS_100_JSON", MediaTypes.APPLICATION_JSON);
            } catch (OwsExceptionReport e) {
                LOGGER.error("Error while creating static capabilities for SOS 1.0.0!", e);
            }
        }

        private void createSos200() {
            try {
                GetCapabilitiesResponse response = getResponse(Sos2Constants.SERVICEVERSION);
                handler.createDynamicCapabilities(getRequest(Sos2Constants.SERVICEVERSION), response, false);
                write(response, SOS_200_XML, MediaTypes.APPLICATION_XML);
                // write(response, "SOS_200_JSON", MediaTypes.APPLICATION_JSON);
            } catch (OwsExceptionReport e) {
                LOGGER.error("Error while creating static capabilities for SOS 2.0.0!", e);
            }
        }

        private void write(GetCapabilitiesResponse response, String identifier, MediaType contentType) {
            writeLock().lock();
            try {
                Path path = buildPath(identifier + FILE_ENDING);
                Path parent = path.getParent();
                if (parent != null) {
                    if (!Files.isSymbolicLink(parent)) {
                        Files.createDirectories(parent);
                    }
                } else {
                    throw new RuntimeException("Error while creating config file path.");
                }
                File file = path.toFile();
                ResponseWriter<Object> writer = responseWriterRepository.getWriter(response.getClass());
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    response.setContentType(contentType);
                    writer.setContentType(contentType);
                    writer.write(response, fos);
                    fos.flush();
                }
            } catch (IOException | EncodingException e) {
                LOGGER.error("Error while persisting static capabilities!", e);
            } finally {
                writeLock().unlock();
            }
        }
    }

}
