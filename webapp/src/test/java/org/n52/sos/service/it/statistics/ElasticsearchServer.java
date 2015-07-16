package org.n52.sos.service.it.statistics;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.junit.rules.ExternalResource;

public class ElasticsearchServer extends ExternalResource {
	
	private Node embeddedNode;
	private Client client;

	@Override
	protected void before() throws Throwable {
        Settings settings = ImmutableSettings.settingsBuilder().loadFromClasspath("elasticsearch_embedded.yml").build();
        embeddedNode = NodeBuilder.nodeBuilder().settings(settings).build();
        embeddedNode.start();
        client = embeddedNode.client();
	}

	@Override
	protected void after() {
		embeddedNode.close();
	}
	
	public Client getClient() {
		return client;
	}

	
}
