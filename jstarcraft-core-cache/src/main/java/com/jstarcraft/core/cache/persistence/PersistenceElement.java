package com.jstarcraft.core.cache.persistence;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.cache.exception.CacheOperationException;
import com.jstarcraft.core.cache.persistence.PersistenceStrategy.PersistenceOperation;
import com.jstarcraft.core.common.identification.IdentityObject;

/**
 * 持久元素
 * 
 * <pre>
 * 配合{@link PrompPersistenceStrategy},{@link QueuePersistenceStrategy},{@link SchedulePersistenceStrategy}实现具体持久化策略.
 * </pre>
 * 
 * @author Birdy
 */
public class PersistenceElement {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceElement.class);

    /** 操作类型 */
    private PersistenceOperation operation;
    /** 缓存主键 */
    private final Comparable cacheId;
    /** 缓存对象({@link PersistenceOperation.DELETE}时为null) */
    private IdentityObject<?> cacheObject;
    /** 是否忽略 */
    private boolean ignore;

    PersistenceElement(PersistenceOperation operation, Comparable cacheId, IdentityObject<?> cacheObject) {
        this.operation = operation;
        this.cacheId = cacheId;
        this.cacheObject = cacheObject;
    }

    /**
     * 是否忽略
     * 
     * <pre>
     * 为了防止歧义,例如:
     * create->delete = ignore
     * create->delete->create = create;
     * delete->create = update;
     * delete->create->delete = delete;
     * 当ignore为true时,应该立刻放弃该元素.
     * </pre>
     * 
     * @return
     */
    boolean isIgnore() {
        return ignore;
    }

    /**
     * 修改元素
     * 
     * <pre>
     * 根据新的元素修改旧的元素,返回值为true表示此元素需要忽略,false表示此元素不需要忽略
     * </pre>
     * 
     * @param element
     * @return
     */
    boolean modify(PersistenceElement element) {
        if (ignore) {
            throw new CacheOperationException("元素处于忽略状态,不能再执行修改");
        }
        cacheObject = element.getCacheObject();
        switch (operation) {
        case CREATE:
            // 旧的状态为CREATE
            switch (element.getOperation()) {
            case CREATE:
                LOGGER.error("元素操作异常,缓存对象[{}]旧操作[{}]新操作[{}]", new Object[] { cacheId, operation, element.getOperation() });
                throw new CacheOperationException();
            case UPDATE:
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("元素操作修改,缓存对象[{}]旧操作[{}]新操作[{}]现在操作[{}]是否保留元素[{}]", new Object[] { cacheId, PersistenceOperation.CREATE, element.getOperation(), operation, true });
                }
                break;
            case DELETE:
                operation = null;
                ignore = true;
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("元素操作修改,缓存对象[{}]旧操作[{}]新操作[{}]现在操作[{}]是否保留元素[{}]", new Object[] { cacheId, PersistenceOperation.CREATE, element.getOperation(), operation, false });
                }
                break;
            }
            break;
        case UPDATE:
            // 旧的状态为UPDATE
            switch (element.getOperation()) {
            case CREATE:
                LOGGER.error("元素操作异常,缓存对象[{}]旧操作[{}]新操作[{}]", new Object[] { cacheId, operation, element.getOperation() });
                throw new CacheOperationException();
            case UPDATE:
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("元素操作修改,缓存对象[{}]旧操作[{}]新操作[{}]现在操作[{}]是否保留元素[{}]", new Object[] { cacheId, PersistenceOperation.UPDATE, element.getOperation(), operation, true });
                }
                break;
            case DELETE:
                operation = PersistenceOperation.DELETE;
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("元素操作修改,缓存对象[{}]旧操作[{}]新操作[{}]现在操作[{}]是否保留元素[{}]", new Object[] { cacheId, PersistenceOperation.UPDATE, element.getOperation(), operation, true });
                }
                break;
            }
            break;
        case DELETE:
            // 旧的状态为DELETE
            switch (element.getOperation()) {
            case CREATE:
                operation = PersistenceOperation.UPDATE;
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("元素操作修改,缓存对象[{}]旧操作[{}]新操作[{}]现在操作[{}]是否保留元素[{}]", new Object[] { cacheId, PersistenceOperation.DELETE, PersistenceOperation.CREATE, operation, true });
                }
                break;
            case UPDATE:
                LOGGER.error("元素操作异常,缓存对象[{}]旧操作[{}]新操作[{}]", new Object[] { cacheId, operation, element.getOperation() });
                throw new CacheOperationException();
            case DELETE:
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("元素操作修改,缓存对象[{}]旧操作[{}]新操作[{}]现在操作[{}]是否保留元素[{}]", new Object[] { cacheId, PersistenceOperation.DELETE, PersistenceOperation.DELETE, operation, true });
                }
                break;
            }
            break;
        }
        return ignore;
    }

    /**
     * 获取操作类型
     * 
     * @return
     */
    public PersistenceOperation getOperation() {
        return operation;
    }

    /**
     * 获取缓存标识
     * 
     * @return
     */
    public Comparable getCacheId() {
        return cacheId;
    }

    /**
     * 获取缓存对象
     * 
     * @return
     */
    public IdentityObject<?> getCacheObject() {
        return cacheObject;
    }

    @Override
    public String toString() {
        ToStringBuilder string = new ToStringBuilder(this);
        string.append(operation);
        string.append(cacheId);
        return string.toString();
    }

}
