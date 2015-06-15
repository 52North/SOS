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
package org.n52.sos.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.n52.sos.exception.ows.concrete.GenericThrowableWrapperException;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * Utility class for file handling
 * 
 * @since 4.0.0
 * 
 */
public final class FileIOHelper {

    private static final byte LINE_FEED = (byte) '\n';

    private static final byte CARRIAGE_RETURN = (byte) '\r';

    private static final String READ_MODE = "r";

    /**
     * Loads a file and returns an InputStream
     * 
     * 
     * 
     * @param file
     *            File to load
     * 
     * @return InputStream of the file
     * 
     * @throws OwsExceptionReport
     *             If and error occurs;
     */
    public static InputStream loadInputStreamFromFile(File file) throws OwsExceptionReport {
        InputStream is;
        try {
            is = new FileInputStream(file);
            return is;
        } catch (FileNotFoundException fnfe) {
            throw new GenericThrowableWrapperException(fnfe);
        }
    }

    /* TODO refactor this */
    public static List<String> tail(File file, int lines) throws IOException {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, READ_MODE);
            final long length = file.length() - 1;
            ArrayList<String> out = new ArrayList<String>(lines);
            StringBuilder sb = new StringBuilder();
            byte prev = -1;
            for (long pos = length; pos != -1; pos--) {
                raf.seek(pos);
                byte b = raf.readByte();
                try {
                    if (b == CARRIAGE_RETURN) {
                        continue;
                    } else if (b == LINE_FEED) {
                        if (pos == length || pos == length - 1 || prev == CARRIAGE_RETURN) {
                            continue;
                        }
                        out.add(sb.reverse().toString());
                        sb = null;
                        if (out.size() == lines) {
                            break;
                        } else {
                            sb = new StringBuilder();
                        }
                    } else {
                        sb.append((char) b);
                    }
                } finally {
                    prev = b;
                }
            }
            if (sb != null) {
                out.add(sb.reverse().toString());
            }
            Collections.reverse(out);
            return out;
        } finally {
            if (raf != null) {
                raf.close();
            }
        }
    }

    private FileIOHelper() {
    }
}
