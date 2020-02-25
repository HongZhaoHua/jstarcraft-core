package com.jstarcraft.core.storage.lucene;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.jstarcraft.core.storage.StorageIterator;
import com.jstarcraft.core.storage.exception.StorageException;
import com.jstarcraft.core.utility.KeyValue;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;

/**
 * Lucene引擎
 * 
 * @author Birdy
 *
 */
public class LuceneEngine implements AutoCloseable {

    /** 配置 */
    private final IndexWriterConfig config;

    /** 瞬时化管理器 */
    private volatile TransienceManager transienceManager;

    /** 持久化管理器 */
    private volatile PersistenceManager persistenceManager;

    /** Lucene搜索器 */
    private volatile LuceneSearcher searcher;

    /** 信号量(读写隔离) */
    private final AtomicInteger semaphore;

    /** 读写锁(合并隔离) */
    private final Lock readLock;

    private final Lock writeLock;

    public LuceneEngine(IndexWriterConfig config, Path path) {
        try {
            this.config = config;
            Directory transienceDirectory = new ByteBuffersDirectory();
            this.transienceManager = new TransienceManager((IndexWriterConfig) BeanUtils.cloneBean(config), transienceDirectory);
            Directory persistenceDirectory = FSDirectory.open(path);
            this.persistenceManager = new PersistenceManager((IndexWriterConfig) BeanUtils.cloneBean(config), persistenceDirectory);
            this.searcher = new LuceneSearcher(this.transienceManager, this.persistenceManager);

            this.semaphore = new AtomicInteger();
            ReadWriteLock lock = new ReentrantReadWriteLock();
            this.readLock = lock.readLock();
            this.writeLock = lock.writeLock();
        } catch (Exception exception) {
            throw new StorageException(exception);
        }
    }

    /**
     * 加锁读操作
     */
    private void lockRead() {
        while (true) {
            int semaphore = this.semaphore.get();
            if (semaphore >= 0) {
                if (this.semaphore.compareAndSet(semaphore, semaphore + 1)) {
                    break;
                }
            }
        }
    }

    /**
     * 解锁读操作
     */
    private void unlockRead() {
        this.semaphore.decrementAndGet();
    }

    /**
     * 加锁写操作
     */
    private void lockWrite() {
        while (true) {
            int semaphore = this.semaphore.get();
            if (semaphore <= 0) {
                if (this.semaphore.compareAndSet(semaphore, semaphore - 1)) {
                    break;
                }
            }
        }
    }

    /**
     * 解锁写操作
     */
    private void unlockWrite() {
        this.semaphore.incrementAndGet();
    }

    /**
     * 合并管理器
     * 
     * @throws Exception
     */
    void mergeManager() throws Exception {
        writeLock.lock();
        TransienceManager newTransienceManager = new TransienceManager((IndexWriterConfig) BeanUtils.cloneBean(config), new ByteBuffersDirectory());
        TransienceManager oldTransienceManager = this.transienceManager;
        try {
            lockWrite();
            this.transienceManager = newTransienceManager;
            // 触发变更
            this.persistenceManager.setManager(oldTransienceManager);
        } finally {
            unlockWrite();
        }

        // 此处需要防止有线程在使用时关闭.
        try {
            lockRead();
            // 只关闭writer,不关闭reader.
            oldTransienceManager.close();
        } finally {
            unlockRead();
        }

        this.persistenceManager.mergeManager();

        try {
            lockWrite();
            // 触发变更
            this.persistenceManager.setManager(null);
        } finally {
            unlockWrite();
        }
        writeLock.unlock();
    }

    /**
     * 创建文档
     * 
     * @param documents
     * @throws Exception
     */
    public void createDocument(String id, Document document) {
        try {
            lockWrite();
            this.transienceManager.createDocument(id, document);
        } catch (Exception exception) {
            throw new StorageException(exception);
        } finally {
            unlockWrite();
        }
    }

    /**
     * 变更文档
     * 
     * @param documents
     * @throws Exception
     */
    public void updateDocument(String id, Document document) {
        try {
            lockWrite();
            this.transienceManager.updateDocument(id, document);
        } catch (Exception exception) {
            throw new StorageException(exception);
        } finally {
            unlockWrite();
        }
    }

    /**
     * 删除文档
     * 
     * @param ids
     * @throws Exception
     */
    public void deleteDocument(String id) {
        try {
            lockWrite();
            this.transienceManager.deleteDocument(id);
        } catch (Exception exception) {
            throw new StorageException(exception);
        } finally {
            unlockWrite();
        }
    }

    /**
     * 检索文档
     * 
     * @param query
     * @param sort
     * @param offset
     * @param size
     * @return
     */
    public KeyValue<List<Document>, FloatList> retrieveDocuments(Query query, Sort sort, int offset, int size) {
        try {
            readLock.lock();
            lockRead();
            synchronized (this.semaphore) {
                if (this.transienceManager.isChanged() || this.persistenceManager.isChanged()) {
                    this.searcher = new LuceneSearcher(this.transienceManager, this.persistenceManager);
                }
            }
            ScoreDoc[] search = null;
            int begin = offset;
            int end = offset + size;
            if (sort == null) {
                search = this.searcher.search(query, end).scoreDocs;
            } else {
                search = this.searcher.search(query, end, sort).scoreDocs;
            }
            end = search.length;
            size = end - begin;
            size = size < 0 ? 0 : size;
            ArrayList<Document> documents = new ArrayList<>(size);
            FloatList scores = new FloatArrayList(size);
            for (int index = begin; index < end; index++) {
                ScoreDoc score = search[index];
                Document document = this.searcher.doc(score.doc);
                documents.add(document);
                scores.add(score.score);
            }
            return new KeyValue<>(documents, scores);
        } catch (Exception exception) {
            throw new StorageException(exception);
        } finally {
            unlockRead();
            readLock.unlock();
        }
    }

    /**
     * 遍历文档
     * 
     * @param iterator
     * @param query
     * @param sort
     * @param offset
     * @param size
     */
    public void iterateDocuments(StorageIterator<Document> iterator, Query query, Sort sort, int offset, int size) {
        try {
            readLock.lock();
            lockRead();
            synchronized (this.semaphore) {
                if (this.transienceManager.isChanged() || this.persistenceManager.isChanged()) {
                    this.searcher = new LuceneSearcher(this.transienceManager, this.persistenceManager);
                }
            }
            ScoreDoc[] search = null;
            int begin = offset;
            int end = offset + size;
            if (sort == null) {
                search = this.searcher.search(query, end).scoreDocs;
            } else {
                search = this.searcher.search(query, end, sort).scoreDocs;
            }
            end = search.length;
            for (int index = begin; index < end; index++) {
                ScoreDoc score = search[index];
                Document document = this.searcher.doc(score.doc);
                iterator.iterate(document);
            }
        } catch (Exception exception) {
            throw new StorageException(exception);
        } finally {
            unlockRead();
            readLock.unlock();
        }
    }

    /**
     * 统计文档
     * 
     * @param query
     * @return
     * @throws Exception
     */
    public int countDocuments(Query query) {
        try {
            readLock.lock();
            lockRead();
            synchronized (this.semaphore) {
                if (this.transienceManager.isChanged() || this.persistenceManager.isChanged()) {
                    this.searcher = new LuceneSearcher(this.transienceManager, this.persistenceManager);
                }
            }
            int count = this.searcher.count(query);
            return count;
        } catch (Exception exception) {
            throw new StorageException(exception);
        } finally {
            unlockRead();
            readLock.unlock();
        }
    }

    @Override
    public void close() {
        try {
            mergeManager();
            this.transienceManager.close();
            this.persistenceManager.close();
        } catch (Exception exception) {
            throw new StorageException(exception);
        }
    }

}
