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

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.ImmutableSettings.Builder;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.search.SearchHit;
import org.n52.sos.statistics.api.utils.dto.KibanaConfigEntryDto;
import org.n52.sos.statistics.api.utils.dto.KibanaConfigHolderDto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class KibanaExporter {

    private static String statisticsIndex;
    private static TransportClient client;

    public static void main(String args[]) throws Exception {
        if (args.length != 2) {
            System.out.println(String.format("Usage: java KibanaExporter.jar %s %s", "localhost:9300", "my-cluster-name"));
            System.exit(0);
        }
        if (!args[0].contains(":")) {
            throw new IllegalArgumentException(String.format("%s not a valid format. Expected <hostname>:<port>.", args[0]));
        }

        // set ES address
        String split[] = args[0].split(":");
        InetSocketTransportAddress address = new InetSocketTransportAddress(split[0], Integer.valueOf(split[1]));

        // set cluster name
        Builder tcSettings = ImmutableSettings.settingsBuilder();
        tcSettings.put("cluster.name", args[1]);
        System.out.println("Connection to " + args[1]);

        client = new TransportClient(tcSettings);
        client.addTransportAddress(address);

        // search index pattern for needle
        searchIndexPattern();

        KibanaConfigHolderDto holder = new KibanaConfigHolderDto();
        System.out.println("Reading .kibana index");

        SearchResponse resp = client.prepareSearch(".kibana").setSize(1000).get();
        Arrays.asList(resp.getHits().getHits()).stream().forEach(l -> {
            holder.add(parseSearchHit(l));
        });
        System.out.println("Reading finished");

        ObjectMapper mapper = new ObjectMapper();
        // we love pretty things
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        File f = new File("kibana_config.json");
        if (f.exists()) {
            System.out.println(f.getAbsolutePath() + " exists it will be deleted.");
            f.delete();
        }
        mapper.writeValue(new FileOutputStream(f), holder);
        System.out.println("File outputted to: " + f.getAbsolutePath());

        client.close();

    }

    private static void searchIndexPattern() throws Exception {
        // find statistics index
        System.out.println("Searching index pattern name for index-needle");
        SearchResponse indexPatternResp = client.prepareSearch(".kibana").setTypes("index-pattern").get();
        if (indexPatternResp.getHits().getHits().length != 1) {
            throw new Exception("The .kibana/index-pattern type has multiple elements or none. Only one element is legal. "
                    + "Set your kibana settings with only one index-pattern");
        }

        statisticsIndex = indexPatternResp.getHits().getHits()[0].getId();
        System.out.println("Found index " + statisticsIndex);
    }

    private static KibanaConfigEntryDto parseSearchHit(SearchHit hit) {
        System.out.println(String.format("Reading %s/%s/%s", hit.getIndex(), hit.getType(), hit.getId()));

        String id = hit.getId();
        if (hit.getId().equals(statisticsIndex)) {
            id = KibanaImporter.INDEX_NEEDLE;
        }

        String source = hit.getSourceAsString().replace(statisticsIndex, KibanaImporter.INDEX_NEEDLE);

        return new KibanaConfigEntryDto(hit.getIndex(), hit.getType(), id, source);

    }
}