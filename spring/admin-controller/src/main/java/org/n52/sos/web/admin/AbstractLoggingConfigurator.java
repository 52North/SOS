/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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
package org.n52.sos.web.admin;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @since 4.0.0
 *
 */
public interface AbstractLoggingConfigurator {

    Set<Appender> getEnabledAppender();

    boolean isEnabled(Appender a);

    boolean enableAppender(Appender appender, boolean enabled);

    Level getRootLogLevel();

    boolean setRootLogLevel(Level level);

    Map<String, Level> getLoggerLevels();

    Level getLoggerLevel(String id);

    boolean setLoggerLevel(String id, Level level);

    boolean setLoggerLevel(Map<String, Level> levels);

    int getMaxHistory();

    boolean setMaxHistory(int days);

    List<String> getLastLogEntries(int maxSize);

    InputStream getLogFile();

    String getMaxFileSize();

    boolean setMaxFileSize(String maxFileSize);

    enum Level {
        TRACE, DEBUG, INFO, WARN, ERROR;

        static Level[] getValues() {
            return values();
        }
    }

    enum Appender {
        FILE("FILE"), CONSOLE("STDOUT");
        private final String name;

        Appender(String name) {
            this.name = name;
        }

        String getName() {
            return this.name;
        }

        static Appender byName(String name) {
            for (Appender a : values()) {
                if (a.getName().equals(name)) {
                    return a;
                }
            }
            return null;
        }
    }
}
