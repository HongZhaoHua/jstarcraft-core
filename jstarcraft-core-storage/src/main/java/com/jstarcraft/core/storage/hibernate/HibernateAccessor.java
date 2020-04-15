package com.jstarcraft.core.storage.hibernate;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.query.Query;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import com.jstarcraft.core.common.identification.IdentityObject;
import com.jstarcraft.core.storage.StorageAccessor;
import com.jstarcraft.core.storage.StorageCondition;
import com.jstarcraft.core.storage.StorageIterator;
import com.jstarcraft.core.storage.StorageMetadata;
import com.jstarcraft.core.storage.StoragePagination;
import com.jstarcraft.core.storage.exception.StorageException;
import com.jstarcraft.core.storage.exception.StorageQueryException;
import com.jstarcraft.core.utility.StringUtility;

/**
 * Hibernate访问器
 * 
 * @author Birdy
 */
@Transactional
public class HibernateAccessor extends HibernateDaoSupport implements StorageAccessor {

    private static final int BATCH_SIZE = 1000;

    private enum Operation {
        AND, OR;
    }

    // 内置查询
    /** DELETE Class clazz WHERE clazz.field = ? */
    private final static String DELETE_HQL = "DELETE {} clazz WHERE clazz.{} = ?0";

    /** 查询指定范围的最大主键标识 */
    private final static String MAXIMUM_ID = "SELECT MAX(clazz.{}) FROM {} clazz WHERE clazz.{} BETWEEN ?0 AND ?1";

    /** 查询指定范围的最小主键标识 */
    private final static String MINIMUM_ID = "SELECT MIN(clazz.{}) FROM {} clazz WHERE clazz.{} BETWEEN ?0 AND ?1";

    /** 查询指定索引范围的主键映射 */
    private final static String INDEX_2_ID_MAP = "SELECT clazz.{}, clazz.{} FROM {} clazz";

    /** 查询指定索引范围的对象列表 */
    private final static String INDEX_2_OBJECT_SET = "FROM {} clazz";

    private final static String BETWEEN_CONDITION = " WHERE clazz.{} BETWEEN ?0 AND ?1";

    private final static String EQUAL_CONDITION = " WHERE clazz.{} = ?0";

    private final static String HIGHER_CONDITION = " WHERE clazz.{} > ?0";

    private final static String IN_CONDITION = " WHERE clazz.{} IN (?0{})";

    private final static String LOWER_CONDITION = " WHERE clazz.{} < ?0";

    private final static String UNEQUAL_CONDITION = " WHERE clazz.{} <> ?0";

    /** HQL删除语句 */
    private Map<Class, String> deleteHqls = new ConcurrentHashMap<>();

    /** HQL查询语句(查询指定范围的最大主键标识),用于IdentityManager */
    private Map<Class, String> maximumIdHqls = new ConcurrentHashMap<>();

    /** HQL查询语句(查询指定范围的最小主键标识),用于IdentityManager */
    private Map<Class, String> minimumIdHqls = new ConcurrentHashMap<>();

    /** Hibernate元信息 */
    protected Map<String, HibernateMetadata> metadatas = new ConcurrentHashMap<>();

    public HibernateAccessor(EntityManagerFactory sessionFactory) {
        MetamodelImplementor metamodelImplementor = (MetamodelImplementor) sessionFactory.getMetamodel();
        try {
            for (EntityPersister ormPersister : metamodelImplementor.entityPersisters().values()) {
                String ormName = ormPersister.getClassMetadata().getEntityName();
                try {
                    Class<?> ormClass = Class.forName(ormName);
                    HibernateMetadata metadata = new HibernateMetadata(ormClass);
                    metadatas.put(ormName, metadata);
                    String deleteHql = StringUtility.format(DELETE_HQL, ormClass.getSimpleName(), metadata.getPrimaryName());
                    deleteHqls.put(ormClass, deleteHql);

                    String maximumIdHql = StringUtility.format(MAXIMUM_ID, metadata.getPrimaryName(), ormClass.getSimpleName(), metadata.getPrimaryName());
                    maximumIdHqls.put(ormClass, maximumIdHql);

                    String minimumIdHql = StringUtility.format(MINIMUM_ID, metadata.getPrimaryName(), ormClass.getSimpleName(), metadata.getPrimaryName());
                    minimumIdHqls.put(ormClass, minimumIdHql);
                } catch (ClassNotFoundException exception) {
                    throw new StorageException(exception);
                }
            }
        } catch (Exception exception) {
            throw new StorageException(exception);
        }
        setSessionFactory((SessionFactory) sessionFactory);
    }

    @Override
    public Collection<? extends StorageMetadata> getAllMetadata() {
        return metadatas.values();
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> T getInstance(Class<T> clazz, K id) {
        T value = getHibernateTemplate().executeWithNativeSession(new HibernateCallback<T>() {

            @Override
            public T doInHibernate(Session session) throws HibernateException {
                return (T) session.get(clazz, (Serializable) id);
            }

        });
        return value;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> boolean createInstance(Class<T> clazz, T object) {
        return getHibernateTemplate().executeWithNativeSession(new HibernateCallback<Boolean>() {

            @Override
            public Boolean doInHibernate(Session session) throws HibernateException {
                try {
                    session.save(object);
                    return true;
                } catch (Exception exception) {
                    return false;
                }
            }

        });
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> boolean deleteInstance(Class<T> clazz, K id) {
        return getHibernateTemplate().executeWithNativeSession(new HibernateCallback<Boolean>() {

            @Override
            public Boolean doInHibernate(Session session) throws HibernateException {
                String hql = deleteHqls.get(clazz);
                Query<?> query = session.createQuery(hql);
                query.setParameter(0, id);
                return query.executeUpdate() > 0;
            }

        });
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> boolean deleteInstance(Class<T> clazz, T object) {
        return getHibernateTemplate().executeWithNativeSession(new HibernateCallback<Boolean>() {

            @Override
            public Boolean doInHibernate(Session session) throws HibernateException {
                try {
                    session.delete(object);
                    return true;
                } catch (Exception exception) {
                    return false;
                }
            }

        });
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> boolean updateInstance(Class<T> clazz, T object) {
        return getHibernateTemplate().executeWithNativeSession(new HibernateCallback<Boolean>() {

            @Override
            public Boolean doInHibernate(Session session) throws HibernateException {
                try {
                    session.update(object);
                    return true;
                } catch (Exception exception) {
                    return false;
                }
            }

        });
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> K maximumIdentity(Class<T> clazz, K from, K to) {
        return getHibernateTemplate().executeWithNativeSession(new HibernateCallback<K>() {

            @Override
            public K doInHibernate(Session session) throws HibernateException {
                String hql = maximumIdHqls.get(clazz);
                Query<?> query = session.createQuery(hql);
                query.setParameter(0, from);
                query.setParameter(1, to);
                return (K) query.getSingleResult();
            }

        });
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> K minimumIdentity(Class<T> clazz, K from, K to) {
        return getHibernateTemplate().executeWithNativeSession(new HibernateCallback<K>() {

            @Override
            public K doInHibernate(Session session) throws HibernateException {
                String hql = minimumIdHqls.get(clazz);
                Query<?> query = session.createQuery(hql);
                query.setParameter(0, from);
                query.setParameter(1, to);
                return (K) query.getSingleResult();
            }

        });
    }

    @Override
    public <K extends Comparable, I, T extends IdentityObject<K>> Map<K, I> queryIdentities(Class<T> clazz, StorageCondition condition, String name, I... values) {
        if (!condition.checkValues(values)) {
            throw new StorageQueryException();
        }
        return getHibernateTemplate().executeWithNativeSession(new HibernateCallback<Map<K, I>>() {

            @Override
            public Map<K, I> doInHibernate(Session session) throws HibernateException {
                StringBuilder buffer = new StringBuilder(INDEX_2_ID_MAP);
                switch (condition) {
                case All:
                    break;
                case Between:
                    buffer.append(BETWEEN_CONDITION);
                    break;
                case Equal:
                    buffer.append(EQUAL_CONDITION);
                    break;
                case Higher:
                    buffer.append(HIGHER_CONDITION);
                    break;
                case In:
                    StringBuilder string = new StringBuilder();
                    for (int index = 1, size = values.length - 1; index <= size; index++) {
                        string.append(", ?");
                        string.append(index);
                    }
                    buffer.append(StringUtility.format(IN_CONDITION, name, string.toString()));
                    break;
                case Lower:
                    buffer.append(LOWER_CONDITION);
                    break;
                case Unequal:
                    buffer.append(UNEQUAL_CONDITION);
                    break;
                }
                String hql = buffer.toString();
                HibernateMetadata metadata = metadatas.get(clazz.getName());
                hql = StringUtility.format(hql, metadata.getPrimaryName(), name, clazz.getSimpleName(), name);
                Query<Object[]> query = session.createQuery(hql);
                for (int index = 0; index < values.length; index++) {
                    query.setParameter(index, values[index]);
                }
                List<Object[]> list = query.getResultList();
                Map<K, I> map = new HashMap<>();
                for (Object[] element : list) {
                    map.put((K) element[0], (I) element[1]);
                }
                return map;
            }

        });
    }

    @Override
    public <K extends Comparable, I, T extends IdentityObject<K>> List<T> queryInstances(Class<T> clazz, StorageCondition condition, String name, I... values) {
        if (!condition.checkValues(values)) {
            throw new StorageQueryException();
        }
        return getHibernateTemplate().executeWithNativeSession(new HibernateCallback<List<T>>() {

            @Override
            public List<T> doInHibernate(Session session) throws HibernateException {
                StringBuilder buffer = new StringBuilder(INDEX_2_OBJECT_SET);
                switch (condition) {
                case All:
                    break;
                case Between:
                    buffer.append(BETWEEN_CONDITION);
                    break;
                case Equal:
                    buffer.append(EQUAL_CONDITION);
                    break;
                case Higher:
                    buffer.append(HIGHER_CONDITION);
                    break;
                case In:
                    StringBuilder string = new StringBuilder();
                    for (int index = 1, size = values.length - 1; index <= size; index++) {
                        string.append(", ?");
                        string.append(index);
                    }
                    buffer.append(StringUtility.format(IN_CONDITION, name, string.toString()));
                    break;
                case Lower:
                    buffer.append(LOWER_CONDITION);
                    break;
                case Unequal:
                    buffer.append(UNEQUAL_CONDITION);
                    break;
                }
                String hql = buffer.toString();
                hql = StringUtility.format(hql, clazz.getSimpleName(), name);
                Query<T> query = session.createQuery(hql);
                for (int index = 0; index < values.length; index++) {
                    query.setParameter(index, values[index]);
                }
                List<T> list = query.getResultList();
                return list;
            }

        });
    }

    private <K extends Comparable, T extends IdentityObject<K>> List<T> query(Class<T> clazz, Operation operation, Map<String, Object> condition, StoragePagination pagination) {
        return getHibernateTemplate().executeWithNativeSession(new HibernateCallback<List<T>>() {

            @Override
            public List<T> doInHibernate(Session session) throws HibernateException {
                CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(clazz);
                Root<T> root = criteriaQuery.from(clazz);
                if (condition != null) {
                    Predicate left = null, right = null;
                    final Iterator<Entry<String, Object>> iterator = condition.entrySet().iterator();
                    if (iterator.hasNext()) {
                        Entry<String, Object> entry = iterator.next();
                        left = criteriaBuilder.equal(root.get(entry.getKey()), entry.getValue());
                    }
                    while (iterator.hasNext()) {
                        Entry<String, Object> entry = iterator.next();
                        right = criteriaBuilder.equal(root.get(entry.getKey()), entry.getValue());
                        switch (operation) {
                        case AND:
                            left = criteriaBuilder.and(left, right);
                            break;
                        case OR:
                            left = criteriaBuilder.or(left, right);
                            break;
                        default:
                            throw new UnsupportedOperationException();
                        }
                    }
                    if (left != null) {
                        criteriaQuery.where(left);
                    }
                }
                TypedQuery<T> typedQuery = session.createQuery(criteriaQuery);
                if (pagination != null) {
                    typedQuery.setFirstResult(pagination.getFirst());
                    typedQuery.setMaxResults(pagination.getSize());
                }
                List<T> value = typedQuery.getResultList();
                return value;
            }

        });
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> List<T> queryInstances(Class<T> clazz, StoragePagination pagination) {
        return query(clazz, null, null, pagination);
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> List<T> queryIntersection(Class<T> clazz, Map<String, Object> condition, StoragePagination pagination) {
        return query(clazz, Operation.AND, condition, pagination);
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> List<T> queryUnion(Class<T> clazz, Map<String, Object> condition, StoragePagination pagination) {
        return query(clazz, Operation.OR, condition, pagination);
    }

    private <K extends Comparable, T extends IdentityObject<K>> long count(Class<T> clazz, Operation operation, Map<String, Object> condition) {
        return getHibernateTemplate().executeWithNativeSession(new HibernateCallback<Long>() {

            @Override
            public Long doInHibernate(Session session) throws HibernateException {
                CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
                Root<T> root = criteriaQuery.from(clazz);
                if (condition != null) {
                    Predicate left = null, right = null;
                    final Iterator<Entry<String, Object>> iterator = condition.entrySet().iterator();
                    if (iterator.hasNext()) {
                        Entry<String, Object> entry = iterator.next();
                        left = criteriaBuilder.equal(root.get(entry.getKey()), entry.getValue());
                    }
                    while (iterator.hasNext()) {
                        Entry<String, Object> entry = iterator.next();
                        right = criteriaBuilder.equal(root.get(entry.getKey()), entry.getValue());
                        switch (operation) {
                        case AND:
                            left = criteriaBuilder.and(left, right);
                            break;
                        case OR:
                            left = criteriaBuilder.or(left, right);
                            break;
                        default:
                            throw new UnsupportedOperationException();
                        }
                    }
                    if (left != null) {
                        criteriaQuery.where(left);
                    }
                }
                criteriaQuery.select(criteriaBuilder.countDistinct(root));
                TypedQuery<Long> typedQuery = session.createQuery(criteriaQuery);
                long count = typedQuery.getSingleResult().longValue();
                return count;
            }

        });
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> long countInstances(Class<T> clazz) {
        return count(clazz, null, null);
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> long countIntersection(Class<T> clazz, Map<String, Object> condition) {
        return count(clazz, Operation.AND, condition);
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> long countUnion(Class<T> clazz, Map<String, Object> condition) {
        return count(clazz, Operation.OR, condition);
    }

    private <K extends Comparable, T extends IdentityObject<K>> void iterate(StorageIterator<T> iterator, final Class<T> clazz, Operation operation, Map<String, Object> condition, StoragePagination pagination) {
        getHibernateTemplate().executeWithNativeSession(new HibernateCallback<T>() {

            @Override
            public T doInHibernate(Session session) throws HibernateException {
                CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(clazz);
                Root<T> root = criteriaQuery.from(clazz);
                if (condition != null) {
                    Predicate left = null, right = null;
                    final Iterator<Entry<String, Object>> cursor = condition.entrySet().iterator();
                    if (cursor.hasNext()) {
                        Entry<String, Object> entry = cursor.next();
                        left = criteriaBuilder.equal(root.get(entry.getKey()), entry.getValue());
                    }
                    while (cursor.hasNext()) {
                        Entry<String, Object> entry = cursor.next();
                        right = criteriaBuilder.equal(root.get(entry.getKey()), entry.getValue());
                        switch (operation) {
                        case AND:
                            left = criteriaBuilder.and(left, right);
                            break;
                        case OR:
                            left = criteriaBuilder.or(left, right);
                            break;
                        default:
                            throw new UnsupportedOperationException();
                        }
                    }
                    if (left != null) {
                        criteriaQuery.where(left);
                    }
                }
                TypedQuery<T> typedQuery = session.createQuery(criteriaQuery);
                if (pagination != null) {
                    typedQuery.setFirstResult(pagination.getFirst());
                    typedQuery.setMaxResults(pagination.getSize());
                }
                // 设置遍历过程的参数
                Query<T> query = (Query<T>) typedQuery;
                query.setFetchSize(BATCH_SIZE);
                query.setLockMode(LockModeType.NONE);
                query.setReadOnly(true);
                try (ScrollableResults scrollableResults = query.scroll()) {
                    while (scrollableResults.next()) {
                        try {
                            // TODO 需要考虑中断
                            final T object = clazz.cast(scrollableResults.get(0));
                            iterator.iterate(object);
                        } catch (Throwable throwable) {
                            throw new StorageQueryException(throwable);
                        }
                    }
                }
                return null;
            }

        });
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void iterate(StorageIterator<T> iterator, final Class<T> clazz, StoragePagination pagination) {
        iterate(iterator, clazz, null, null, pagination);
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void iterateIntersection(StorageIterator<T> iterator, final Class<T> clazz, Map<String, Object> condition, StoragePagination pagination) {
        iterate(iterator, clazz, Operation.AND, condition, pagination);
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void iterateUnion(StorageIterator<T> iterator, final Class<T> clazz, Map<String, Object> condition, StoragePagination pagination) {
        iterate(iterator, clazz, Operation.OR, condition, pagination);
    }

    public <R> List<R> queryDatas(String name, Class<R> queryType, StoragePagination pagination, Map<String, Object> parameters) {
        return getHibernateTemplate().executeWithNativeSession(new HibernateCallback<List<R>>() {
            @Override
            public List<R> doInHibernate(Session session) throws HibernateException {
                javax.persistence.Query query;
                if (queryType == null) {
                    // 执行更新与删除不可以指定类型
                    query = session.createNamedQuery(name);
                } else {
                    query = session.createNamedQuery(name, queryType);
                }
                for (Entry<String, Object> keyValue : parameters.entrySet()) {
                    String name = keyValue.getKey();
                    Object value = keyValue.getValue();
                    query.setParameter(name, value);
                }
                if (pagination != null) {
                    query.setFirstResult(pagination.getFirst());
                    query.setMaxResults(pagination.getSize());
                }
                if (queryType == null) {
                    // 执行更新与删除不可以指定类型
                    Integer count = query.executeUpdate();
                    return (List<R>) Arrays.asList(count);
                } else {
                    List<R> value = query.getResultList();
                    return value;
                }
            }
        });
    }

    public <R> List<R> queryDatas(String name, Class<R> queryType, StoragePagination pagination, Object... parameters) {
        return getHibernateTemplate().executeWithNativeSession(new HibernateCallback<List<R>>() {

            @Override
            public List<R> doInHibernate(Session session) throws HibernateException {
                javax.persistence.Query query;
                if (queryType == null) {
                    // 执行更新与删除不可以指定类型
                    query = session.createNamedQuery(name);
                } else {
                    query = session.createNamedQuery(name, queryType);
                }
                for (int index = 0; index < parameters.length; index++) {
                    query.setParameter(index, parameters[index]);
                }
                if (pagination != null) {
                    query.setFirstResult(pagination.getFirst());
                    query.setMaxResults(pagination.getSize());
                }
                if (queryType == null) {
                    // 执行更新与删除不可以指定类型
                    Integer count = query.executeUpdate();
                    return (List<R>) Arrays.asList(count);
                } else {
                    List<R> value = query.getResultList();
                    return value;
                }
            }
        });

    }

    public <R> R uniqueData(String name, Class<R> queryType, Map<String, Object> parameters) {
        return getHibernateTemplate().executeWithNativeSession(new HibernateCallback<R>() {

            @Override
            public R doInHibernate(Session session) throws HibernateException {
                Query<?> query = session.createNamedQuery(name, queryType);
                for (Entry<String, Object> keyValue : parameters.entrySet()) {
                    String name = keyValue.getKey();
                    Object value = keyValue.getValue();
                    query.setParameter(name, value);
                }
                R value = (R) query.getSingleResult();
                return value;
            }

        });
    }

    public <R> R uniqueData(String name, Class<R> queryType, Object... parameters) {
        return getHibernateTemplate().executeWithNativeSession(new HibernateCallback<R>() {

            @Override
            public R doInHibernate(Session session) throws HibernateException {
                Query<?> query = session.createNamedQuery(name, queryType);
                for (int index = 0; index < parameters.length; index++) {
                    query.setParameter(index, parameters[index]);
                }
                R value = (R) query.getSingleResult();
                return value;
            }

        });
    }

    public int modifyDatas(String name, Map<String, Object> parameters) {
        return getHibernateTemplate().executeWithNativeSession(new HibernateCallback<Integer>() {

            @Override
            public Integer doInHibernate(Session session) throws HibernateException {
                Query<?> query = session.getNamedQuery(name);
                for (Entry<String, Object> keyValue : parameters.entrySet()) {
                    String name = keyValue.getKey();
                    Object value = keyValue.getValue();
                    query.setParameter(name, value);
                }
                return query.executeUpdate();
            }

        });
    }

    public int modifyDatas(String name, Object... parameters) {
        return getHibernateTemplate().executeWithNativeSession(new HibernateCallback<Integer>() {

            @Override
            public Integer doInHibernate(Session session) throws HibernateException {
                Query<?> query = session.getNamedQuery(name);
                for (int index = 0; index < parameters.length; index++) {
                    query.setParameter(index, parameters[index]);
                }
                return query.executeUpdate();
            }

        });
    }

}
