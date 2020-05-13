package com.jstarcraft.core.transaction.elasticsearch;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.jstarcraft.core.transaction.TransactionManager;
import com.jstarcraft.core.transaction.TransactionManagerTestCase;

import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic;
import pl.allegro.tech.embeddedelasticsearch.IndexSettings;
import pl.allegro.tech.embeddedelasticsearch.PopularProperties;

public class ElasticsearchTransactionManagerTestCase extends TransactionManagerTestCase {

    private static final String EMBEDDED_ELASTIC_HOST = "localhost";

    private static final int EMBEDDED_ELASTIC_PORT = 9350;

    private static EmbeddedElastic elasticServer;

    private RestHighLevelClient elasticClient;

    @Before
    public void testBefore() {
        elasticClient = new RestHighLevelClient(RestClient.builder(new HttpHost(EMBEDDED_ELASTIC_HOST, EMBEDDED_ELASTIC_PORT, "http")));
    }

    @After
    public void testAfter() throws IOException {
        elasticClient.close();
    }

    @BeforeClass
    public static void startEmbeddedElastic() throws IOException, InterruptedException {
        elasticServer = EmbeddedElastic.builder().withElasticVersion("6.8.8")

                .withSetting(PopularProperties.HTTP_PORT, EMBEDDED_ELASTIC_PORT)

                .withStartTimeout(2, TimeUnit.MINUTES)

                .withIndex(ElasticsearchTransactionManager.DEFAULT_INDEX, IndexSettings.builder()

                        .withType(ElasticsearchTransactionManager.DEFAULT_TYPE, ClassLoader.getSystemResourceAsStream("ElasticsearchTransactionDefinition.mapping.json"))

                        .build())

                .build()

                .start();
    }

    @AfterClass
    public static void stopEmbeddedElastic() {
        if (elasticServer != null) {
            elasticServer.stop();
        }
    }

    @Override
    protected TransactionManager getDistributionManager() {
        return new ElasticsearchTransactionManager(elasticClient);
    }

}
