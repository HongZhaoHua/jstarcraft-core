package com.jstarcraft.core.storage.lucene;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.LeafCollector;
import org.apache.lucene.store.Directory;

/**
 * Lucene管理器
 * 
 * <pre>
 * 用于管理{@link LeafCollector},{@link IndexReader},{@link IndexWriter}的变更与获取
 * </pre>
 * 
 * @author Birdy
 *
 */
public interface LuceneManager {

    /**
     * 是否变更
     * 
     * @return
     */
    boolean isChanged();

    /**
     * 获取采集器
     * 
     * @param context
     * @param collector
     * @return
     * @throws IOException
     */
    LeafCollector getCollector(LeafReaderContext context, LeafCollector collector) throws IOException;

    /**
     * 获取目录
     * 
     * @return
     */
    Directory getDirectory();

    /**
     * 获取读取器
     * 
     * @return
     */
    IndexReader getReader();

    /**
     * 获取写入器
     * 
     * @return
     */
    IndexWriter getWriter();

}
