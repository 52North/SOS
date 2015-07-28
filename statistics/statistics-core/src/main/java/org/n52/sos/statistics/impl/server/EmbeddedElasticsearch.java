package org.n52.sos.statistics.impl.server;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.ImmutableSettings.Builder;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmbeddedElasticsearch {

    @Inject
    private ServletContext context;

    private static final Logger logger = LoggerFactory.getLogger(EmbeddedElasticsearch.class);

    private static Node embeddedNode;

    public void destroy() {
        logger.info("Closing embedded elasticsearch node");
        embeddedNode.close();
    }

    public void init() {
        logger.info("Starting embedded elasticsearch node");
        Builder setting = ImmutableSettings.settingsBuilder().loadFromClasspath("elasticsearch_embedded.yml");
        String realPath = context.getRealPath("WEB-INF");
        setting.put("path.home", realPath + "\\" + "elasticsearch");

        embeddedNode = NodeBuilder.nodeBuilder().settings(setting.build()).build();
        embeddedNode.start();

        logger.info("Started embedded elasticsearch node");
    }
}
