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
package org.n52.sos.statistics.impl.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.ImmutableSettings.Builder;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.n52.sos.statistics.api.utils.FileDownloader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.util.IOUtils;

public class EmbeddedElasticsearch {

    private static final Logger logger = LoggerFactory.getLogger(EmbeddedElasticsearch.class);

    private String homePath;
    private Node embeddedNode;
    private Client client;

    public void destroy() {
        if (client != null) {
            client.close();
        }
        logger.info("Closing embedded elasticsearch node");
        if (embeddedNode != null) {
            embeddedNode.close();
        }
    }

    public void init() {
        Objects.requireNonNull(homePath);

        logger.info("Home path for Embedded Elasticsearch: {}", homePath);

        logger.info("Starting embedded elasticsearch node");
        Builder setting = ImmutableSettings.settingsBuilder().loadFromClasspath("elasticsearch_embedded.yml");
        setting.put("path.home", homePath);

        try {
            if (!new File(homePath).exists()) {
                FileUtils.forceMkdir(new File(homePath));
                copyScriptFiles();
                downlaodGroovyLibrary();
            } else {
                logger.info("Path " + homePath + "for embedded elasticsearch is exsits. Continue.");
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        embeddedNode = NodeBuilder.nodeBuilder().settings(setting.build()).build();
        embeddedNode.start();
        try {
            logger.info("Waiting 8 seconds to startup the Elasticsearch");
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        logger.info("Started embedded elasticsearch node");
    }

    private void downlaodGroovyLibrary() throws IOException {
        String groovyDir = homePath + "/plugins/groovy";
        FileUtils.forceMkdir(new File(groovyDir));
        FileDownloader.downloadFile("http://central.maven.org/maven2/org/codehaus/groovy/groovy-all/2.4.4/groovy-all-2.4.4.jar",
                groovyDir + "/groovy-all-2.4.4.jar");
    }

    /**
     * I don't now if this as a hack or no. The directories read as files give
     * back the content Every line represents the name of the file.
     * 
     * @throws IOException
     */
    private void copyScriptFiles() throws IOException {
        File scripts = new File(homePath + "/config/scripts");
        FileUtils.forceMkdir(scripts);

        InputStream folder = EmbeddedElasticsearch.class.getResourceAsStream("/scripts");
        // write file content
        File contents = File.createTempFile(UUID.randomUUID().toString(), "tmp");
        FileOutputStream out = new FileOutputStream(contents);
        IOUtils.copy(folder, out);
        out.close();

        // read the files list at least on windows works
        for (String line : Files.readAllLines(Paths.get(contents.getAbsolutePath()))) {
            InputStream scriptFile = EmbeddedElasticsearch.class.getResourceAsStream("/scripts/" + line);
            FileOutputStream scriptFileOut = new FileOutputStream(scripts.getAbsolutePath() + "/" + line);
            IOUtils.copy(scriptFile, scriptFileOut);
            scriptFileOut.close();
        }
    }

    public String getHomePath() {
        return homePath;
    }

    public void setHomePath(String homePath) {
        this.homePath = homePath;
    }

    public Client getClient() {
        return embeddedNode.client();
    }
}
