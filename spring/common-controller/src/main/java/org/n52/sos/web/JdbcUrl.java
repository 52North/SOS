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
package org.n52.sos.web;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.n52.sos.ds.hibernate.util.DefaultHibernateConstants;
import org.n52.sos.util.Constants;

/**
 * @since 4.0.0
 * 
 */
public class JdbcUrl implements Constants {

    private static final String QUERY_PARAMETER_USER = "user";

    private static final String QUERY_PARAMETER_PASSWORD = "password";

    private static final String DEFAULT_USERNAME = "user";

    private static final String DEFAULT_PASSWORD = "password";

    private static final String DEFAULT_HOST = "localhost";

    private static final String DEFAULT_DATABASE = "sos";

    private static final String SCHEME = "jdbc";

    private static final String TYPE = "postgresql";

    private static final int DEFAULT_PORT = 5432;

    private String scheme;

    private String type;

    private String host;

    private int port;

    private String database;

    private String user;

    private String password;

    public JdbcUrl(String scheme, String type, String host, int port, String database, String user, String password) {
        this.scheme = scheme;
        this.type = type;
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    public JdbcUrl(String uri) throws URISyntaxException {
        this.parse(uri);
    }

    public JdbcUrl(Properties p) throws URISyntaxException {
        this(toURI(p));
    }

    public Properties toProperties() {
        Properties properties = new Properties();
        properties.put(DefaultHibernateConstants.CONNECTION_STRING_PROPERTY, getConnectionString());
        properties.put(DefaultHibernateConstants.USER_PROPERTY, getUser());
        properties.put(DefaultHibernateConstants.PASS_PROPERTY, getPassword());
        return properties;
    }

    private static String toURI(Properties p) {
        StringBuilder sb = new StringBuilder();
        sb.append(p.getProperty(DefaultHibernateConstants.CONNECTION_STRING_PROPERTY));
        sb.append(QUERSTIONMARK_CHAR).append(QUERY_PARAMETER_USER).append(EQUAL_SIGN_CHAR)
                .append(p.getProperty(DefaultHibernateConstants.USER_PROPERTY));
        sb.append(AMPERSAND_CHAR).append(QUERY_PARAMETER_PASSWORD).append(EQUAL_SIGN_CHAR)
                .append(p.getProperty(DefaultHibernateConstants.PASS_PROPERTY));
        return sb.toString();
    }

    protected final void parse(String string) throws URISyntaxException {
        URI uri = new URI(string);
        scheme = uri.getScheme();
        uri = new URI(uri.getSchemeSpecificPart());
        type = uri.getScheme();
        host = uri.getHost();
        port = uri.getPort();
        String[] path = uri.getPath().split(SLASH_STRING);
        if (path.length == 1 && !path[0].isEmpty()) {
            database = path[0];
        } else if (path.length == 2 && path[0].isEmpty() && !path[1].isEmpty()) {
            database = path[1];
        }
        for (NameValuePair nvp : URLEncodedUtils.parse(uri, "UTF-8")) {
            if (nvp.getName().equals(QUERY_PARAMETER_USER)) {
                user = nvp.getValue();
            } else if (nvp.getName().equals(QUERY_PARAMETER_PASSWORD)) {
                password = nvp.getValue();
            }
        }
    }

    public String isValid() {
        if (user == null || user.isEmpty()) {
            return "Invalid user.";
        }
        if (password == null) {
            return "Invalid password.";
        }
        if (port < 0) {
            return "Invalid port";
        }
        if (scheme == null || !scheme.equals(SCHEME)) {
            return "Invalid scheeme";
        }
        if (type == null || !type.equals(TYPE)) {
            return "Invalid database type.";
        }
        if (database == null || database.isEmpty()) {
            return "Invalid database.";
        }

        return null;
    }

    public void correct() {
        if (!isSchemeValid()) {
            setScheme(SCHEME);
        }
        if (!isTypeValid()) {
            setType(TYPE);
        }
        if (!isHostValid()) {
            setHost(DEFAULT_HOST);
        }
        if (!isPortValid()) {
            setPort(DEFAULT_PORT);
        }
        if (!isDatabaseValid()) {
            setDatabase(DEFAULT_DATABASE);
        }
        if (!isUserValid()) {
            setUser(DEFAULT_USERNAME);
        }
        if (!isPasswordValid()) {
            setPassword(DEFAULT_PASSWORD);
        }
    }

    public String getScheme() {
        return scheme;
    }

    public boolean isSchemeValid() {
        return getScheme() != null && getScheme().equals(SCHEME);
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getType() {
        return type;
    }

    public boolean isTypeValid() {
        return getType() != null && getType().equals(TYPE);
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHost() {
        return host;
    }

    public boolean isHostValid() {
        return getHost() != null && !getHost().isEmpty();
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public boolean isPortValid() {
        return getPort() >= 0;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDatabase() {
        return database;
    }

    public boolean isDatabaseValid() {
        return getDatabase() != null && !getDatabase().isEmpty();
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUser() {
        return user;
    }

    public boolean isUserValid() {
        return getUser() != null && !getUser().isEmpty();
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public boolean isPasswordValid() {
        return getPassword() != null && !getPassword().isEmpty();
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return new StringBuilder(getConnectionString()).append(QUERSTIONMARK_CHAR).append(QUERY_PARAMETER_USER)
                .append(EQUAL_SIGN_CHAR).append(getUser()).append(AMPERSAND_CHAR).append(QUERY_PARAMETER_PASSWORD)
                .append(EQUAL_SIGN_CHAR).append(getPassword()).toString();
    }

    public String getConnectionString() {
        return new StringBuilder().append(getScheme()).append(COLON_CHAR).append(getType()).append(COLON_CHAR)
                .append(SLASH_CHAR).append(SLASH_CHAR).append(getHost()).append(COLON_CHAR).append(getPort())
                .append(SLASH_CHAR).append(getDatabase()).toString();
    }
}
