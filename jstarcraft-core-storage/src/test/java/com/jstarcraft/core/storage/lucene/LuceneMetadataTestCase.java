package com.jstarcraft.core.storage.lucene;

import java.time.Instant;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.storage.lucene.LuceneMetadata;
import com.jstarcraft.core.storage.lucene.converter.LuceneContext;

public class LuceneMetadataTestCase {

    @Test
    public void testMetadata() {
        LuceneContext context = new LuceneContext(CodecDefinition.instanceOf(MockSimpleObject.class, MockComplexObject.class));
        try {
            new LuceneMetadata(MockSimpleObject.class, context);
            Assert.fail();
        } catch (IllegalArgumentException exception) {
        }
        LuceneMetadata metadata = new LuceneMetadata(MockComplexObject.class, context);
        Assert.assertEquals(MockComplexObject.class.getName(), metadata.getOrmName());
        Assert.assertEquals("id", metadata.getPrimaryName());
        Assert.assertEquals(11, metadata.getIndexNames().size());
        Assert.assertTrue(metadata.getIndexNames().contains("firstName"));
        Assert.assertTrue(metadata.getIndexNames().contains("lastName"));
        Assert.assertEquals(LuceneMetadata.LUCENE_VERSION, metadata.getVersionName());
    }
    
    @Test
    public void testCodec() throws Exception {
        Directory directory = new ByteBuffersDirectory();
        Analyzer analyzer = new WhitespaceAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, config);

        LuceneContext context = new LuceneContext(CodecDefinition.instanceOf(MockComplexObject.class));
        LuceneMetadata codec = new LuceneMetadata(MockComplexObject.class, context);
        Instant now = Instant.now();
        MockComplexObject protoss = MockComplexObject.instanceOf(-1, "protoss", "jstarcraft", -1, now, MockEnumeration.PROTOSS);
        MockComplexObject terran = MockComplexObject.instanceOf(0, "terran", "jstarcraft", 0, now, MockEnumeration.TERRAN);
        MockComplexObject zerg = MockComplexObject.instanceOf(1, "zerg", "jstarcraft", 1, now, MockEnumeration.ZERG);
        MockComplexObject[] objects = new MockComplexObject[] { protoss, terran, zerg };

        indexWriter.addDocument(codec.encodeDocument(protoss));
        indexWriter.addDocument(codec.encodeDocument(terran));
        indexWriter.addDocument(codec.encodeDocument(zerg));

        IndexReader indexReader = DirectoryReader.open(indexWriter);

        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        {
            TopDocs search = indexSearcher.search(IntPoint.newRangeQuery("id", -1, 1), 1000);
            Assert.assertEquals(3L, search.totalHits.value);
            int index = 0;
            for (ScoreDoc scoreDoc : search.scoreDocs) {
                Document document = indexReader.document(scoreDoc.doc);
                Assert.assertEquals(objects[index++], codec.decodeDocument(document));
            }
        }

        {
            TopDocs search = indexSearcher.search(IntPoint.newRangeQuery("currencies", new int[] { -1, -1 }, new int[] { 1, 1 }), 1000);
            Assert.assertEquals(3L, search.totalHits.value);
            int index = 0;
            for (ScoreDoc scoreDoc : search.scoreDocs) {
                Document document = indexReader.document(scoreDoc.doc);
                Assert.assertEquals(objects[index++], codec.decodeDocument(document));
            }
        }

        {
            TopDocs search = indexSearcher.search(new TermQuery(new Term("names", "jstarcraft")), 1000);
            Assert.assertEquals(3L, search.totalHits.value);
            int index = 0;
            for (ScoreDoc scoreDoc : search.scoreDocs) {
                Document document = indexReader.document(scoreDoc.doc);
                Assert.assertEquals(objects[index++], codec.decodeDocument(document));
            }

            search = indexSearcher.search(new TermQuery(new Term("names", "protoss")), 1000);
            Assert.assertEquals(1L, search.totalHits.value);
            for (ScoreDoc scoreDoc : search.scoreDocs) {
                Document document = indexReader.document(scoreDoc.doc);
                Assert.assertEquals(protoss, codec.decodeDocument(document));
            }
            search = indexSearcher.search(new TermQuery(new Term("names", "terran")), 1000);
            Assert.assertEquals(1L, search.totalHits.value);
            for (ScoreDoc scoreDoc : search.scoreDocs) {
                Document document = indexReader.document(scoreDoc.doc);
                Assert.assertEquals(terran, codec.decodeDocument(document));
            }
            search = indexSearcher.search(new TermQuery(new Term("names", "zerg")), 1000);
            Assert.assertEquals(1L, search.totalHits.value);
            for (ScoreDoc scoreDoc : search.scoreDocs) {
                Document document = indexReader.document(scoreDoc.doc);
                Assert.assertEquals(zerg, codec.decodeDocument(document));
            }

            search = indexSearcher.search(new TermQuery(new Term("object.name", "protoss")), 1000);
            Assert.assertEquals(1L, search.totalHits.value);
            for (ScoreDoc scoreDoc : search.scoreDocs) {
                Document document = indexReader.document(scoreDoc.doc);
                Assert.assertEquals(protoss, codec.decodeDocument(document));
            }
            search = indexSearcher.search(new TermQuery(new Term("object.name", "terran")), 1000);
            Assert.assertEquals(1L, search.totalHits.value);
            for (ScoreDoc scoreDoc : search.scoreDocs) {
                Document document = indexReader.document(scoreDoc.doc);
                Assert.assertEquals(terran, codec.decodeDocument(document));
            }
            search = indexSearcher.search(new TermQuery(new Term("object.name", "zerg")), 1000);
            Assert.assertEquals(1L, search.totalHits.value);
            for (ScoreDoc scoreDoc : search.scoreDocs) {
                Document document = indexReader.document(scoreDoc.doc);
                Assert.assertEquals(zerg, codec.decodeDocument(document));
            }

            search = indexSearcher.search(new TermQuery(new Term("list[0].name", "protoss")), 1000);
            Assert.assertEquals(1L, search.totalHits.value);
            for (ScoreDoc scoreDoc : search.scoreDocs) {
                Document document = indexReader.document(scoreDoc.doc);
                Assert.assertEquals(protoss, codec.decodeDocument(document));
            }
            search = indexSearcher.search(new TermQuery(new Term("list[0].name", "terran")), 1000);
            Assert.assertEquals(1L, search.totalHits.value);
            for (ScoreDoc scoreDoc : search.scoreDocs) {
                Document document = indexReader.document(scoreDoc.doc);
                Assert.assertEquals(terran, codec.decodeDocument(document));
            }
            search = indexSearcher.search(new TermQuery(new Term("list[0].name", "zerg")), 1000);
            Assert.assertEquals(1L, search.totalHits.value);
            for (ScoreDoc scoreDoc : search.scoreDocs) {
                Document document = indexReader.document(scoreDoc.doc);
                Assert.assertEquals(zerg, codec.decodeDocument(document));
            }

            search = indexSearcher.search(IntPoint.newExactQuery("map[0]", -1), 1000);
            Assert.assertEquals(1L, search.totalHits.value);
            for (ScoreDoc scoreDoc : search.scoreDocs) {
                Document document = indexReader.document(scoreDoc.doc);
                Assert.assertEquals(protoss, codec.decodeDocument(document));
            }
            search = indexSearcher.search(IntPoint.newExactQuery("map[0]", 0), 1000);
            Assert.assertEquals(1L, search.totalHits.value);
            for (ScoreDoc scoreDoc : search.scoreDocs) {
                Document document = indexReader.document(scoreDoc.doc);
                Assert.assertEquals(terran, codec.decodeDocument(document));
            }
            search = indexSearcher.search(IntPoint.newExactQuery("map[0]", 1), 1000);
            Assert.assertEquals(1L, search.totalHits.value);
            for (ScoreDoc scoreDoc : search.scoreDocs) {
                Document document = indexReader.document(scoreDoc.doc);
                Assert.assertEquals(zerg, codec.decodeDocument(document));
            }
        }

        indexReader.close();
        indexWriter.close();
    }

}
