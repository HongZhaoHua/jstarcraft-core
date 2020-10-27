package com.jstarcraft.core.transaction.zookeeper;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.PathUtils;
import org.apache.zookeeper.CreateMode;

import com.jstarcraft.core.transaction.TransactionDefinition;
import com.jstarcraft.core.transaction.TransactionManager;
import com.jstarcraft.core.transaction.exception.TransactionLockException;
import com.jstarcraft.core.transaction.exception.TransactionUnlockException;
import com.jstarcraft.core.utility.DelayElement;
import com.jstarcraft.core.utility.SensitivityQueue;

/**
 * ZooKeeper事务管理器
 * 
 * <pre>
 * 由于Curator Recipes已经提供许多锁方案,所以不再提供封装.
 * </pre>
 * 
 * @author Birdy
 *
 */
public class ZooKeeperTransactionManager extends TransactionManager {

    /** 修复时间间隔 */
    private static final long FIX_TIME = 1000;

    public static final String DEFAULT_PATH = "/jstarcraft";

    /** 定时队列 */
    private static final SensitivityQueue<DelayElement<ExpireTask>> QUEUE = new SensitivityQueue<>(FIX_TIME);
    /** 清理线程 */
    private static final Thread CLEANER = new Thread(new Runnable() {
        public void run() {
            try {
                while (true) {
                    // 保证锁会自动过期
                    DelayElement<ExpireTask> element = QUEUE.take();
                    ExpireTask task = element.getContent();
                    task.execute();
                }
            } catch (InterruptedException exception) {
                // 中断不处理
                // 由于创建节点设置为EPHEMERAL,所以连接中断会自动删除节点.
            }
        }
    });

    static {
        CLEANER.setDaemon(true);
        CLEANER.start();
    }

    private static class ExpireTask {

        private AtomicBoolean state;

        private CuratorFramework curator;

        private String path;

        public ExpireTask(AtomicBoolean state, CuratorFramework curator, String path) {
            this.state = state;
            this.curator = curator;
            this.path = path;
        }

        public void execute() {
            try {
                if (state.compareAndSet(true, false)) {
                    curator.delete().forPath(path);
                }
            } catch (Exception exception) {
                // TODO 记录日志
            }
        }

    }

    private final ThreadLocal<AtomicBoolean> states = new ThreadLocal<>();

    private final CuratorFramework curator;

    private final String path;

    public ZooKeeperTransactionManager(CuratorFramework curator) {
        this(curator, DEFAULT_PATH);
    }

    public ZooKeeperTransactionManager(CuratorFramework curator, String path) {
        this.curator = requireNonNull(curator);
        this.path = PathUtils.validatePath(path);
    }

    String getNodePath(TransactionDefinition definition) {
        return path + "/" + definition.getName();
    }

    @Override
    public void lock(TransactionDefinition definition) {
        try {
            String path = getNodePath(definition);
            curator.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
            AtomicBoolean state = new AtomicBoolean(true);
            states.set(state);
            ExpireTask task = new ExpireTask(state, curator, path);
            DelayElement<ExpireTask> element = new DelayElement<>(task, definition.getMost());
            QUEUE.put(element);
        } catch (Exception exception) {
            throw new TransactionLockException(exception);
        }
    }

    @Override
    public void unlock(TransactionDefinition definition) {
        try {
            AtomicBoolean state = states.get();
            state.compareAndSet(true, false);
            String path = getNodePath(definition);
            curator.delete().forPath(path);
        } catch (Exception exception) {
            throw new TransactionUnlockException(exception);
        }
    }

}
