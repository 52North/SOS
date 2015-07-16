package org.n52.sos.service.it.statistics;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.sort.SortBuilders;
import org.junit.ClassRule;
import org.n52.sos.statistics.api.ServiceEventDataMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractStatisticsBase {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());

	public static final String ES_INDEX = "ogc-statistics-index";
	public static final String ES_TYPE = "ogc-type";
	public static final int JETTY_PORT = 10100;
	//public static final String SOS_ENDPOINT = "http://localhost:"+JETTY_PORT+"/service";
	public static final String SOS_ENDPOINT = "http://localhost:8080/sos1/service";
	
	private static final CloseableHttpClient httpClient = HttpClients.createDefault();

	@ClassRule
	public static ElasticsearchServer elasticsearch;

	public SearchResponse getLatestElasticsearchEntry() {
		return elasticsearch.getClient().prepareSearch(ES_INDEX).setTypes(ES_TYPE).addSort(SortBuilders.fieldSort(ServiceEventDataMapping.TIMESTAMP_FIELD)).get();
	}
	
	public void postJsonAsString(String json){

		HttpPost post = new HttpPost(SOS_ENDPOINT);
		CloseableHttpResponse response = null;
		try {
			post.setEntity(new StringEntity(json,ContentType.APPLICATION_JSON));
			response = httpClient.execute(post);
			EntityUtils.consume(response.getEntity());
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		} finally {
			IOUtils.closeQuietly(response);
		}
	}

}
