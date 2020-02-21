package com.jstarcraft.core.orm.lucene;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.index.IndexWriterConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jstarcraft.core.orm.lucene.converter.IdConverter;
import com.jstarcraft.core.orm.lucene.converter.id.JsonIdConverter;

@Configuration
public class LuceneAccessorConfigurer {

    @Bean(name = "converter")
    public IdConverter getConverter() throws Exception {
        IdConverter converter = new JsonIdConverter();
        return converter;
    }

    @Bean(name = "engine")
    public LuceneEngine getEngine() throws Exception {
        IndexWriterConfig config = new IndexWriterConfig();

        Path path = Paths.get("./lucene");
        File file = path.toFile();
        FileUtils.deleteDirectory(file);
        LuceneEngine engine = new LuceneEngine(config, path);
        return engine;
    }

}
