/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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
package custom;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.env.Environment;
import org.elasticsearch.node.Node;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class KibanaImpExp {

    @Test
    public void exportSettings() throws IOException {
        Builder settingsBuilder = Settings.builder();
        // Builder settingsBuilder = Settings.builder();
        settingsBuilder.put("discovery.zen.ping.unicast.hosts", "localhost").put("cluster.name", "ogc-statistics-cluster");

        Node node = new Node(new Environment(settingsBuilder.build(), null));
        // Node node = new TestNode(settingsBuilder.build());
        Client c = node.client();

        KibanaConfigHolder holder = new KibanaConfigHolder();
        SearchResponse resp = c.prepareSearch(".kibana").setSize(1000).get();
        Arrays.asList(resp.getHits().getHits()).stream().forEach(l -> {
            holder.add(new KibanaConfigEntry(l.getIndex(), l.getType(), l.getId(), l.getSourceAsString()));
        });

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        System.err.println(mapper.writeValueAsString(holder));

        // write back
        for (KibanaConfigEntry e : holder.getEntries()) {
            c.prepareIndex(e.getIndex() + "2", e.getType(), e.getId()).setSource(e.getSource()).get();
        }

        c.close();
        node.close();
    }

    public static class KibanaConfigHolder {
        private List<KibanaConfigEntry> entries = new LinkedList<>();

        public void add(KibanaConfigEntry entry) {
            entries.add(entry);
        }

        public List<KibanaConfigEntry> getEntries() {
            return entries;
        }

        public void setEntries(List<KibanaConfigEntry> entries) {
            this.entries = entries;
        }

    }

    public static class KibanaConfigEntry {
        private String index;

        private String type;

        private String id;

        private String source;

        public KibanaConfigEntry(String index, String type, String id, String source) {
            super();
            this.index = index;
            this.type = type;
            this.id = id;
            this.source = source;
        }

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }
    }

}
