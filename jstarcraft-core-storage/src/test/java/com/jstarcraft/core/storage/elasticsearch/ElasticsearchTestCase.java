package com.jstarcraft.core.storage.elasticsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.ParsedSingleValueNumericMetricsAggregation;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
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

        IndexOperations operation = template.indexOps(Mock.class);
        operation.delete();
        operation.create();

        Document mapping = operation.createMapping(Mock.class);
        operation.putMapping(mapping);

        ElasticsearchEntityInformation<Mock, Long> information = factory.getEntityInformation(Mock.class);
        SimpleElasticsearchRepository<Mock, Long> repository = new SimpleElasticsearchRepository<>(information, template);
        Assert.assertEquals(0, repository.count());

        int size = 1000;
        List<Mock> mocks = new ArrayList<>(size);
        for (int index = 0; index < size; index++) {
            long id = index;
            Mock mock = new Mock(id, "title", new String[] { "left", "middle", "right" }, index * 1000D);
            mocks.add(mock);
        }
        repository.saveAll(mocks);
        Assert.assertEquals(size, repository.count());

        {
            NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
            builder.withQuery(QueryBuilders.matchQuery("title", "title"));
            Assert.assertEquals(1000, repository.search(builder.build()).getSize());
        }

        {
            NativeSearchQueryBuilder left = new NativeSearchQueryBuilder();
            left.withQuery(QueryBuilders.termQuery("categories", "left"));
            Assert.assertEquals(1000, repository.search(left.build()).getSize());
            NativeSearchQueryBuilder right = new NativeSearchQueryBuilder();
            right.withQuery(QueryBuilders.termQuery("categories", "right"));
            Assert.assertEquals(1000, repository.search(right.build()).getSize());
            NativeSearchQueryBuilder middle = new NativeSearchQueryBuilder();
            middle.withQuery(QueryBuilders.termQuery("categories", "middle"));
            Assert.assertEquals(1000, repository.search(middle.build()).getSize());

            try {
                // 批量更新
                UpdateByQueryRequest request = new UpdateByQueryRequest("mock");
                request.setQuery(QueryBuilders.termQuery("categories", "middle"));
                request.setScript(new Script("ctx._source.categories.remove(ctx._source.categories.indexOf('middle'));"));
                request.setRefresh(true);
                BulkByScrollResponse response = elasticClient.updateByQuery(request, RequestOptions.DEFAULT);
                long updated = response.getUpdated();
                Assert.assertEquals(1000, updated);
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }

            Assert.assertEquals(1000, repository.search(left.build()).getSize());
            Assert.assertEquals(1000, repository.search(right.build()).getSize());
            Assert.assertEquals(0, repository.search(middle.build()).getSize());
        }

        {
            NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
            builder.addAggregation(AggregationBuilders.max("maximum").field("price"));
            builder.addAggregation(AggregationBuilders.min("minimum").field("price"));
            AggregatedPage<Mock> page = (AggregatedPage<Mock>) repository.search(builder.build());
            ParsedSingleValueNumericMetricsAggregation maximum = (ParsedSingleValueNumericMetricsAggregation) page.getAggregation("maximum");
            ParsedSingleValueNumericMetricsAggregation minimum = (ParsedSingleValueNumericMetricsAggregation) page.getAggregation("minimum");
            Assert.assertEquals(999000D, maximum.value(), 0D);
            Assert.assertEquals(0D, minimum.value(), 0D);
        }

        {
            NativeSearchQueryBuilder first = new NativeSearchQueryBuilder();
            first.withPageable(PageRequest.of(0, 1));
            first.withSort(SortBuilders.fieldSort("id").order(SortOrder.ASC));
            Assert.assertEquals(Long.valueOf(0L), repository.search(first.build()).getContent().get(0).getId());

            NativeSearchQueryBuilder last = new NativeSearchQueryBuilder();
            last.withPageable(PageRequest.of(0, 1));
            last.withSort(SortBuilders.fieldSort("id").order(SortOrder.DESC));
            Assert.assertEquals(Long.valueOf(999L), repository.search(last.build()).getContent().get(0).getId());
        }

        repository.deleteAll(mocks);
        Assert.assertEquals(0, repository.count());
    }

}
