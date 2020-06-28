package com.jstarcraft.core.storage.elasticsearch;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.ParsedSingleValueNumericMetricsAggregation;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.repository.support.ElasticsearchEntityInformation;
import org.springframework.data.elasticsearch.repository.support.ElasticsearchRepositoryFactory;
import org.springframework.data.elasticsearch.repository.support.SimpleElasticsearchRepository;

import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic;

public class ElasticsearchTestCase {

    private static final String EMBEDDED_ELASTIC_HOST = "localhost";

    private static final int EMBEDDED_ELASTIC_PORT = 9200;

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

//    @BeforeClass
//    public static void startEmbeddedElastic() throws IOException, InterruptedException {
//        elasticServer = EmbeddedElastic.builder().withElasticVersion("6.8.8")
//
//                .withSetting(PopularProperties.HTTP_PORT, EMBEDDED_ELASTIC_PORT)
//
//                .withStartTimeout(2, TimeUnit.MINUTES)
//
//                .build()
//
//                .start();
//    }
//
//    @AfterClass
//    public static void stopEmbeddedElastic() {
//        if (elasticServer != null) {
//            elasticServer.stop();
//        }
//    }

    @Test
    // TODO Spring Data Elasticsearch 3.2.7目前只兼容到Elasticsearch 6.8.8和Lucene 7.7.2
    // 需要等Spring Data Elasticsearch兼容到Lucene 8再支持ElasticsearchAccessor
    public void testSpringData() {
        ElasticsearchOperations template = new ElasticsearchRestTemplate(elasticClient);
        ElasticsearchRepositoryFactory factory = new ElasticsearchRepositoryFactory(template);

        IndexOperations index = template.indexOps(Mock.class);
        index.delete();
        index.create();

        Document mapping = index.createMapping(Mock.class);
        index.putMapping(mapping);

        ElasticsearchEntityInformation<Mock, Long> information = factory.getEntityInformation(Mock.class);
        SimpleElasticsearchRepository<Mock, Long> repository = new SimpleElasticsearchRepository<>(information, template);
        Assert.assertEquals(0, repository.count());

        Mock mock = new Mock(0L, "title", "category", 1000D);

        repository.save(mock);
        Assert.assertEquals(mock, repository.findById(0L).get());
        Assert.assertEquals(1, repository.count());

        {
            NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
            builder.withQuery(QueryBuilders.matchQuery("title", "title"));
            Assert.assertEquals(1, repository.search(builder.build()).getSize());
        }

        {
            NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
            builder.addAggregation(AggregationBuilders.max("maximum").field("price"));
            builder.addAggregation(AggregationBuilders.min("minimum").field("price"));
            AggregatedPage<Mock> page = (AggregatedPage<Mock>) repository.search(builder.build());
            ParsedSingleValueNumericMetricsAggregation maximum = (ParsedSingleValueNumericMetricsAggregation) page.getAggregation("maximum");
            ParsedSingleValueNumericMetricsAggregation minimum = (ParsedSingleValueNumericMetricsAggregation) page.getAggregation("minimum");
            Assert.assertEquals(1000D, maximum.value(), 0D);
            Assert.assertEquals(1000D, minimum.value(), 0D);
        }

        repository.delete(mock);
        Assert.assertFalse(repository.findById(0L).isPresent());
        Assert.assertEquals(0, repository.count());
    }

}
