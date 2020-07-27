package com.jstarcraft.core.storage.elasticsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.elasticsearch.action.admin.cluster.settings.ClusterUpdateSettingsRequest;
import org.elasticsearch.action.admin.cluster.settings.ClusterUpdateSettingsResponse;
import org.elasticsearch.action.admin.cluster.storedscripts.DeleteStoredScriptRequest;
import org.elasticsearch.action.admin.cluster.storedscripts.PutStoredScriptRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
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
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.repository.support.ElasticsearchEntityInformation;
import org.springframework.data.elasticsearch.repository.support.ElasticsearchRepositoryFactory;
import org.springframework.data.elasticsearch.repository.support.SimpleElasticsearchRepository;

import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic;

public class ElasticsearchApiTestCase {

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
    public void testRoute() {
        ElasticsearchOperations template = new ElasticsearchRestTemplate(elasticClient);
        for (int index = 0; index < 5; index++) {
            // 构建索引
            String name = "test" + index;
            IndexCoordinates coordinate = IndexCoordinates.of(name);
            IndexOperations operation = template.indexOps(coordinate);
            operation.delete();
            operation.create();
            // 构建映射
            Document mapping = operation.createMapping(Mock.class);
            operation.putMapping(mapping);

            int size = 1000;
            List<Mock> mocks = new ArrayList<>(size);
            for (int id = 0; id < size; id++) {
                Mock mock = new Mock(index * 1000L + id, name, new String[] { "left", "middle", "right" }, index * 1000D + id);
                mocks.add(mock);
            }

            template.save(mocks, coordinate);
            operation.refresh();
        }

        for (int index = 0; index < 5; index++) {
            // 构建索引
            String name = "test" + index;
            IndexCoordinates coordinate = IndexCoordinates.of(name);

            // 聚合查询
            NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
            builder.addAggregation(AggregationBuilders.max("maximum").field("price"));
            builder.addAggregation(AggregationBuilders.min("minimum").field("price"));
            SearchHits<Mock> searchHits = template.search(builder.build(), Mock.class, coordinate);

            ParsedSingleValueNumericMetricsAggregation maximum = (ParsedSingleValueNumericMetricsAggregation) searchHits.getAggregations().get("maximum");
            ParsedSingleValueNumericMetricsAggregation minimum = (ParsedSingleValueNumericMetricsAggregation) searchHits.getAggregations().get("minimum");
            Assert.assertEquals(index * 1000D + 999, maximum.value(), 0D);
            Assert.assertEquals(index * 1000D + 0, minimum.value(), 0D);
        }
    }

    @Test
    // TODO Spring Data Elasticsearch 3.2.7目前只兼容到Elasticsearch 6.8.8和Lucene 7.7.2
    // 需要等Spring Data Elasticsearch兼容到Lucene 8再支持ElasticsearchAccessor
    public void testSpringData() {
        ElasticsearchOperations template = new ElasticsearchRestTemplate(elasticClient);
        ElasticsearchRepositoryFactory factory = new ElasticsearchRepositoryFactory(template);

        // 构建索引
        IndexOperations operation = template.indexOps(Mock.class);
        operation.delete();
        operation.create();
        // 构建映射
        Document mapping = operation.createMapping();
        operation.putMapping(mapping);

        Map<String, Map<String, Object>> properties = mapping.get("properties", Map.class);
        // TODO 注意:在Spring Data Elasticsearch中
        // 如果属性使用注解@Id或者名称为id,则无论@FiealdType如何声明,映射的type永远为keyword
        Assert.assertEquals("keyword", properties.get("id").get("type"));
        Assert.assertEquals("text", properties.get("title").get("type"));
        Assert.assertEquals("keyword", properties.get("categories").get("type"));
        Assert.assertEquals("double", properties.get("price").get("type"));
        Assert.assertEquals("keyword", properties.get("race").get("type"));

        ElasticsearchEntityInformation<Mock, Long> information = factory.getEntityInformation(Mock.class);
        SimpleElasticsearchRepository<Mock, Long> repository = new SimpleElasticsearchRepository<>(information, template);
        Assert.assertEquals(0, repository.count());

        int size = 1000;
        List<Mock> mocks = new ArrayList<>(size);
        for (int index = 0; index < size; index++) {
            long id = index;
            Mock mock = new Mock(id, ": title", new String[] { "left", "middle", "right" }, index * 1000D);
            mocks.add(mock);
        }
        repository.saveAll(mocks);
        Assert.assertEquals(size, repository.count());

        {
            Assert.assertEquals(mocks.get(0), repository.findById(0L).get());
            Assert.assertEquals(mocks.get(999), repository.findById(999L).get());
        }

        {
            NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
            builder.withQuery(QueryBuilders.matchQuery("title", "title"));
            Assert.assertEquals(1000, repository.search(builder.build()).getSize());
        }
        
        {
            NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
            MatchQueryBuilder query = QueryBuilders.matchQuery("title", ":");
            query.analyzer("whitespace");
            builder.withQuery(query);
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
                // 创建脚本
                PutStoredScriptRequest request = new PutStoredScriptRequest();
                request.id("update");
                XContentBuilder builder = XContentFactory.jsonBuilder();
                builder.startObject();
                {
                    builder.startObject("script");
                    {
                        builder.field("lang", "painless");
                        builder.field("source", "ctx._source.categories.remove(ctx._source.categories.indexOf('middle'));");
                    }
                    builder.endObject();
                }
                builder.endObject();
                request.content(BytesReference.bytes(builder), XContentType.JSON);
                AcknowledgedResponse response = elasticClient.putScript(request, RequestOptions.DEFAULT);
                Assert.assertTrue(response.isAcknowledged());
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }

            try {
                // 批量更新
                UpdateByQueryRequest request = new UpdateByQueryRequest("mock");
                request.setQuery(QueryBuilders.termQuery("categories", "middle"));
                request.setScript(new Script(ScriptType.STORED, null, "update", Collections.EMPTY_MAP));
                request.setRefresh(true);
                BulkByScrollResponse response = elasticClient.updateByQuery(request, RequestOptions.DEFAULT);
                long updated = response.getUpdated();
                Assert.assertEquals(1000, updated);
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }

            try {
                // 删除脚本
                DeleteStoredScriptRequest request = new DeleteStoredScriptRequest("update");
                AcknowledgedResponse response = elasticClient.deleteScript(request, RequestOptions.DEFAULT);
                Assert.assertTrue(response.isAcknowledged());
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }

            Assert.assertEquals(1000, repository.search(left.build()).getSize());
            Assert.assertEquals(1000, repository.search(right.build()).getSize());
            Assert.assertEquals(0, repository.search(middle.build()).getSize());
        }

        {
            // 聚合查询
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
            // 分页与排序
            NativeSearchQueryBuilder first = new NativeSearchQueryBuilder();
            first.withPageable(PageRequest.of(0, 1));
            first.withSort(SortBuilders.fieldSort("id").order(SortOrder.ASC));
            Assert.assertEquals(Long.valueOf(0L), repository.search(first.build()).getContent().get(0).getId());

            NativeSearchQueryBuilder last = new NativeSearchQueryBuilder();
            last.withPageable(PageRequest.of(0, 1));
            last.withSort(SortBuilders.fieldSort("id").order(SortOrder.DESC));
            Assert.assertEquals(Long.valueOf(999L), repository.search(last.build()).getContent().get(0).getId());
        }

        {
            // 组合查询
            BoolQueryBuilder and = QueryBuilders.boolQuery();
            and.must(QueryBuilders.termQuery("id", 500L));
            and.must(QueryBuilders.termQuery("race", MockEnumeration.RANDOM));
            NativeSearchQueryBuilder andBuilder = new NativeSearchQueryBuilder();
            andBuilder.withQuery(and);

            BoolQueryBuilder or = QueryBuilders.boolQuery();
            or.should(QueryBuilders.termQuery("id", 500L));
            or.should(QueryBuilders.termQuery("race", MockEnumeration.RANDOM));
            NativeSearchQueryBuilder orBuilder = new NativeSearchQueryBuilder();
            orBuilder.withQuery(or);

            Assert.assertEquals(1, template.count(andBuilder.build(), Mock.class, template.getIndexCoordinatesFor(Mock.class)));
            Assert.assertEquals(1000, template.count(orBuilder.build(), Mock.class, template.getIndexCoordinatesFor(Mock.class)));
            Assert.assertEquals(1, repository.search(andBuilder.build()).getSize());
            Assert.assertEquals(1000, repository.search(orBuilder.build()).getSize());
        }

        repository.deleteAll(mocks);
        Assert.assertEquals(0, repository.count());
    }

    @Test
    public void test1024Index() throws Exception {
        // Elasticsearch 7版本及以上的,默认只允许1000个分片,需要修改配置
        ClusterUpdateSettingsRequest request = new ClusterUpdateSettingsRequest();
        Settings settings = Settings.builder().put("cluster.max_shards_per_node", 5000).build();
        request.persistentSettings(settings);
        ClusterUpdateSettingsResponse response = elasticClient.cluster().putSettings(request, RequestOptions.DEFAULT);
        Assert.assertTrue(response.isAcknowledged());

        ElasticsearchOperations template = new ElasticsearchRestTemplate(elasticClient);
        for (int index = 0; index < 1024; index++) {
            // 构建索引
            String name = "test" + index;
            IndexCoordinates coordinate = IndexCoordinates.of(name);
            Mock mock = new Mock(index + 0L, name, new String[] { "left", "middle", "right" }, index * 1000D);
            IndexQuery indexQuery = new IndexQueryBuilder().withId(String.valueOf(mock.getId())).withObject(mock).build();
            template.index(indexQuery, coordinate);
        }

        for (int index = 0; index < 1024; index++) {
            // 构建索引
            String name = "test" + index;
            IndexCoordinates coordinate = IndexCoordinates.of(name);
            template.indexOps(coordinate).delete();
        }
    }

}
