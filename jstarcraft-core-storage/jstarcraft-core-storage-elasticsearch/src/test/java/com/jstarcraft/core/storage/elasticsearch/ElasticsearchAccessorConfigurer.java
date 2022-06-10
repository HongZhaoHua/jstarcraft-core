package com.jstarcraft.core.storage.elasticsearch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchCustomConversions;
import org.springframework.data.elasticsearch.core.convert.MappingElasticsearchConverter;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;

import com.jstarcraft.core.common.conversion.json.JsonUtility;
import com.jstarcraft.core.storage.elasticsearch.converter.AbstractConverter;
import com.jstarcraft.core.storage.elasticsearch.converter.DecodeJsonProxy;
import com.jstarcraft.core.storage.elasticsearch.converter.EncodeJsonProxy;

@Configuration
public class ElasticsearchAccessorConfigurer {

    private static final String EMBEDDED_ELASTIC_HOST = "localhost";

    private static final int EMBEDDED_ELASTIC_PORT = 9200;

    @Bean(name = "classes")
    public Collection<Class<?>> getClasses() {
        return Collections.singleton(MockObject.class);
    }

    @Bean(name = "factory", destroyMethod = "close")
    public RestHighLevelClient getFactory() throws Exception {
        RestHighLevelClient factory = new RestHighLevelClient(RestClient.builder(new HttpHost(EMBEDDED_ELASTIC_HOST, EMBEDDED_ELASTIC_PORT, "http")));
        return factory;
    }

    private EncodeJsonProxy encodeJsonProxy = new EncodeJsonProxy();

    private DecodeJsonProxy decodeJsonProxy = new DecodeJsonProxy();

    private <T> void buildConverters(List<Converter> converters, Class<T> clazz) {
        
        Converter<T, String> from = new AbstractConverter<T, String>() {

            @Override
            public String convert(T instance) {
                return JsonUtility.object2String(instance);
            }

        };

        Converter<String, T> to = new AbstractConverter<String, T>() {

            @Override
            public T convert(String json) {
                return JsonUtility.string2Object(json, clazz);
            }

        };
        converters.add(encodeJsonProxy.getToConverter(clazz));
        converters.add(decodeJsonProxy.getToConverter(clazz));
    }

    @Bean(name = "accessor")
    public ElasticsearchAccessor getAccessor(RestHighLevelClient factory, Collection<Class<?>> classes) {
        List<Converter> converters = new ArrayList<>();
//        converters.add(new LongToInstantConverter());
//        converters.add(new InstantToLongConverter());
        for (Class<?> clazz : classes) {
            buildConverters(converters, clazz);
        }

        ElasticsearchCustomConversions conversion = new ElasticsearchCustomConversions(converters);
        MappingElasticsearchConverter converter = new MappingElasticsearchConverter(new SimpleElasticsearchMappingContext());
        converter.setConversions(conversion);
        converter.afterPropertiesSet();
        ElasticsearchOperations template = new ElasticsearchRestTemplate(factory, converter);

        for (Class clazz : classes) {
            // 构建索引
            IndexOperations operation = template.indexOps(clazz);
            operation.delete();
            operation.create();
            // 构建映射
            Document mapping = operation.createMapping();
            operation.putMapping(mapping);
        }

        ElasticsearchAccessor accessor = new ElasticsearchAccessor(classes, template);
        return accessor;
    }

}
