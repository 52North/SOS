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
package org.n52.sos.statistics.api.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileDownloader {

    private static final Logger logger = LoggerFactory.getLogger(FileDownloader.class);

    /**
     * Download the url to the specified location
     * 
     * @param url
     *            url to download
     * @param outfilePath
     *            outputfile location
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void downloadFile(String url,
            String outfilePath) throws FileNotFoundException, IOException {
        Objects.requireNonNull(url);
        Objects.requireNonNull(outfilePath);

        URL fileUrl = new URL(url);
        File out = new File(outfilePath);
        FileUtils.copyURLToFile(fileUrl, out);
    }

    public static void gunzipFile(String filePath) throws IOException {
        File file = new File(filePath);
        String outPath = null;
        final byte[] buff = new byte[1024];

        if (!file.getName().endsWith("gz")) {
            throw new IOException("File is not ends with .gz extension");
        } else {
            outPath = file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 3);
        }

        FileOutputStream out = null;
        GzipCompressorInputStream gzFile = null;
        try {
            out = new FileOutputStream(outPath);
            gzFile = new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(file)));
            int n = 0;
            while (-1 != (n = gzFile.read(buff))) {
                out.write(buff, 0, n);
            }
            logger.debug("Extracted file path {}", outPath);
        } catch (IOException e) {
            throw e;
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(gzFile);
        }
    }

    public static boolean isPathExists(String fullPath) {
        if (fullPath == null) {
            return false;
        }
        return new File(fullPath).exists();
    }
}
