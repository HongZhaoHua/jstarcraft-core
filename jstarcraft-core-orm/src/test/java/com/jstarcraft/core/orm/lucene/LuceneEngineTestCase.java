package com.jstarcraft.core.orm.lucene;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.TermQuery;
import org.junit.Assert;
import org.junit.Test;

public class LuceneEngineTestCase {

    @Test
    public void testCRUD() throws Exception {
        IndexWriterConfig config = new IndexWriterConfig();

        Path path = Paths.get("./lucene");
        File file = path.toFile();
        FileUtils.deleteDirectory(file);
        LuceneEngine searcher = new LuceneEngine(config, path);

        for (int index = 0; index < 1000; index++) {
            String data = String.valueOf(index);
            Document document = new Document();
            Field field = new StringField("title", data, Store.NO);
            document.add(field);
            searcher.createDocument(data, document);
        }
        Assert.assertEquals(1000, searcher.countDocuments(new MatchAllDocsQuery()));
        Assert.assertEquals(1, searcher.countDocuments(new TermQuery(new Term("title", "0"))));
        Assert.assertEquals(0, searcher.countDocuments(new TermQuery(new Term("title", "1000"))));
        searcher.mergeManager();
        Assert.assertEquals(1000, searcher.countDocuments(new MatchAllDocsQuery()));
        Assert.assertEquals(1, searcher.countDocuments(new TermQuery(new Term("title", "0"))));
        Assert.assertEquals(0, searcher.countDocuments(new TermQuery(new Term("title", "1000"))));

        for (int index = 0; index < 1000; index++) {
            String data = String.valueOf(index % 2);
            Document document = new Document();
            Field field = new StringField("title", data, Store.NO);
            document.add(field);
            searcher.updateDocument(String.valueOf(index), document);
        }
        Assert.assertEquals(1000, searcher.countDocuments(new MatchAllDocsQuery()));
        Assert.assertEquals(500, searcher.countDocuments(new TermQuery(new Term("title", "0"))));
        Assert.assertEquals(500, searcher.countDocuments(new TermQuery(new Term("title", "1"))));
        searcher.mergeManager();
        Assert.assertEquals(1000, searcher.countDocuments(new MatchAllDocsQuery()));
        Assert.assertEquals(500, searcher.countDocuments(new TermQuery(new Term("title", "0"))));
        Assert.assertEquals(500, searcher.countDocuments(new TermQuery(new Term("title", "1"))));

        for (int index = 0; index < 500; index++) {
            searcher.deleteDocument(String.valueOf(index));
        }

        Assert.assertEquals(500, searcher.countDocuments(new MatchAllDocsQuery()));
        Assert.assertEquals(250, searcher.countDocuments(new TermQuery(new Term("title", "0"))));
        Assert.assertEquals(250, searcher.countDocuments(new TermQuery(new Term("title", "1"))));
        searcher.mergeManager();
        Assert.assertEquals(500, searcher.countDocuments(new MatchAllDocsQuery()));
        Assert.assertEquals(250, searcher.countDocuments(new TermQuery(new Term("title", "0"))));
        Assert.assertEquals(250, searcher.countDocuments(new TermQuery(new Term("title", "1"))));

        searcher.close();
        FileUtils.deleteDirectory(file);
    }

    @Test
    public void testMerge() throws Exception {
        IndexWriterConfig config = new IndexWriterConfig();

        Path path = Paths.get("./lucene");
        File file = path.toFile();
        FileUtils.deleteDirectory(file);
        LuceneEngine searcher = new LuceneEngine(config, path);

        for (int index = 0; index < 1000; index++) {
            String data = String.valueOf(index);
            Document document = new Document();
            Field field = new StringField("title", data, Store.NO);
            document.add(field);
            searcher.createDocument(data, document);
        }
        searcher.mergeManager();
        Assert.assertEquals(1000, searcher.countDocuments(new MatchAllDocsQuery()));

        AtomicBoolean state = new AtomicBoolean(true);
        AtomicInteger readExceptions = new AtomicInteger();
        AtomicInteger writeExceptions = new AtomicInteger();

        for (int index = 0; index < Runtime.getRuntime().availableProcessors(); index++) {
            Thread readThead = new Thread() {

                @Override
                public void run() {
                    try {
                        while (state.get()) {
                            Assert.assertEquals(1000, searcher.countDocuments(new MatchAllDocsQuery()));
                            Thread.sleep(1L);
                        }
                    } catch (Exception exception) {
                        readExceptions.incrementAndGet();
                        Assert.fail();
                    }
                }

            };
            readThead.setDaemon(true);
            readThead.start();

            Thread writeThead = new Thread() {

                @Override
                public void run() {
                    try {
                        while (state.get()) {
                            String data = String.valueOf(0);
                            Document document = new Document();
                            Field field = new StringField("title", data, Store.NO);
                            document.add(field);
                            searcher.updateDocument(data, document);
                            Thread.sleep(1L);
                        }
                    } catch (Exception exception) {
                        writeExceptions.incrementAndGet();
                        Assert.fail();
                    }
                }

            };
            writeThead.setDaemon(true);
            writeThead.start();
        }

        for (int index = 0; index < 10; index++) {
            // 不断触发合并
            searcher.mergeManager();
            Thread.sleep(1000L);
        }

        state.set(false);
        Assert.assertEquals(0, readExceptions.get());
        Assert.assertEquals(0, writeExceptions.get());
        Assert.assertEquals(1000, searcher.countDocuments(new MatchAllDocsQuery()));

        searcher.close();
        FileUtils.deleteDirectory(file);
    }

}
