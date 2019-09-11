package com.jstarcraft.core.monitor.route.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.AbstractDataSource;

/**
 * 路由数据源
 * 
 * @author Birdy
 *
 */
public class RouteDataSource extends AbstractDataSource {

    /** 路由策略 */
    private RouteStrategy strategy;

    /** 所有数据键 */
    private List<String> keys;

    /** 所有数据源 */
    private Map<String, DataSource> dataSources;

    /** 读写锁 */
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public RouteDataSource(RouteStrategy strategy, HashMap<String, DataSource> dataSources) {
        this.strategy = strategy;
        this.keys = Arrays.asList(dataSources.keySet().toArray(new String[dataSources.size()]));
        this.dataSources = new HashMap<>(dataSources);
    }

    /**
     * 设置策略
     * 
     * @param strategy
     */
    public void setStrategy(RouteStrategy strategy) {
        assert strategy != null;
        Lock write = lock.writeLock();
        try {
            write.lock();
            this.strategy = strategy;
        } finally {
            write.unlock();
        }
    }

    /**
     * 添加数据源
     * 
     * @param key
     * @param dataSource
     * @return
     */
    public boolean attachDataSource(String key, DataSource dataSource) {
        assert key != null;
        Lock write = lock.writeLock();
        try {
            write.lock();
            if (dataSources.containsKey(key)) {
                return false;
            } else {
                dataSources.put(key, dataSource);
                this.keys = Arrays.asList(dataSources.keySet().toArray(new String[dataSources.size()]));
                return true;
            }
        } finally {
            write.unlock();
        }
    }

    /**
     * 移除数据源
     * 
     * @param key
     * @return
     */
    public boolean detachDataSource(String key) {
        assert key != null;
        Lock write = lock.writeLock();
        try {
            write.lock();
            if (dataSources.containsKey(key)) {
                dataSources.remove(key);
                this.keys = Arrays.asList(dataSources.keySet().toArray(new String[dataSources.size()]));
                return true;
            } else {
                return false;
            }
        } finally {
            write.unlock();
        }
    }

    /**
     * 根据策略切换数据源
     * 
     * @return
     */
    protected DataSource switchDataSource() {
        Lock read = lock.readLock();
        try {
            read.lock();
            String key = strategy.chooseDataSource(keys);
            return dataSources.get(key);
        } finally {
            read.unlock();
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return switchDataSource().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return switchDataSource().getConnection(username, password);
    }

    @Override
    public <T> T unwrap(Class<T> clazz) throws SQLException {
        if (clazz.isInstance(this)) {
            return (T) this;
        }
        return switchDataSource().unwrap(clazz);
    }

    @Override
    public boolean isWrapperFor(Class<?> clazz) throws SQLException {
        return (clazz.isInstance(this) || switchDataSource().isWrapperFor(clazz));
    }
}