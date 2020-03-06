/**
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
package org.n52.sos.cache.ctrl.persistence;

import org.n52.sos.cache.ContentCachePersistenceStrategy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.sos.cache.ContentCache;
import org.n52.sos.cache.WritableContentCache;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.service.SosContextListener;

import com.google.common.base.Optional;

public abstract class AbstractPersistingCachePersistenceStrategy
        implements ContentCachePersistenceStrategy {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(AbstractPersistingCachePersistenceStrategy.class);
    private static final String CACHE_FILE = "cache.tmp";
    private final String cacheFile;

    public AbstractPersistingCachePersistenceStrategy() {
        this(null);
    }

    public AbstractPersistingCachePersistenceStrategy(File cacheFile) {
        if (cacheFile == null) {
            String basePath = getBasePath();
            Path path = Paths.get(basePath, CACHE_FILE);
            try {
                Files.createDirectories(path.getParent());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.cacheFile = path.toAbsolutePath().toString();
        } else {
            this.cacheFile = cacheFile.getAbsolutePath();
        }
        LOGGER.debug("Cache file: {}", this.cacheFile);
    }

    protected File getCacheFile() {
        return new File(this.cacheFile);
    }

    @Override
    public Optional<WritableContentCache> load() {
        File f = getCacheFile();
        if (f.exists() && f.canRead()) {
            LOGGER.debug("Reading cache from temp file '{}'",
                         f.getAbsolutePath());
            ObjectInputStream in = null;
            long start = System.currentTimeMillis();
            try {
                in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(f)));
                return Optional.of((WritableContentCache) in.readObject());
            } catch (IOException t) {
                LOGGER.error(String.format("Error reading cache file '%s'",
                                           f.getAbsolutePath()), t);
            } catch (ClassNotFoundException t) {
                LOGGER.error(String.format("Error reading cache file '%s'",
                                           f.getAbsolutePath()), t);
            } finally {
                IOUtils.closeQuietly(in);
                LOGGER.debug("Loading cache from file with size {} took {} ms!", f.length(), (System.currentTimeMillis() - start));
            }
            f.delete();
        } else {
            LOGGER.debug("No cache temp file found at '{}'",
                         f.getAbsolutePath());
        }
        return Optional.absent();
    }

    protected void persistCache(ContentCache cache) {
        File f = getCacheFile();
        if (!f.exists() || f.delete()) {
            if (cache != null) {
                ObjectOutputStream out = null;
                LOGGER.debug("Serializing cache to {}", f.getAbsolutePath());
                long start = System.currentTimeMillis();
                try {
                    if (f.createNewFile() && f.canWrite()) {
                        out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
                        out.writeObject(cache);
                    } else {
                        LOGGER.error("Can not create writable file {}",
                                     f.getAbsolutePath());
                    }
                } catch (IOException t) {
                    LOGGER.error(String
                            .format("Error serializing cache to '%s'",
                                    f.getAbsolutePath()), t);
                } finally {
                    IOUtils.closeQuietly(out);
                    LOGGER.debug("Writing cache to file with size {} took {} ms!", f.length(), (System.currentTimeMillis() - start));
                }
            }
        }
    }
    
    protected String getBasePath() {
        File cacheFileFolder = ServiceConfiguration.getInstance().getCacheFileFolder();
        if (cacheFileFolder != null && cacheFileFolder.exists()) {
            return cacheFileFolder.getAbsolutePath();
        }
        return Paths.get(SosContextListener.getPath(), "WEB-INF" , "tmp").toString();
    }

    @Override
    public void cleanup() {
        File f = getCacheFile();
        if (f != null && f.exists()) {
            f.delete();
        }
    }
}