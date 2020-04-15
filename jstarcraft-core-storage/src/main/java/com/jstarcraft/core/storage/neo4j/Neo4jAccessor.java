package com.jstarcraft.core.storage.neo4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.metadata.ClassInfo;
import org.neo4j.ogm.metadata.MetaData;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

import com.jstarcraft.core.common.conversion.ConversionUtility;
import com.jstarcraft.core.common.identification.IdentityObject;
import com.jstarcraft.core.storage.StorageAccessor;
import com.jstarcraft.core.storage.StorageCondition;
import com.jstarcraft.core.storage.StorageIterator;
import com.jstarcraft.core.storage.StorageMetadata;
import com.jstarcraft.core.storage.StoragePagination;
import com.jstarcraft.core.storage.exception.StorageQueryException;
import com.jstarcraft.core.utility.StringUtility;

/**
 * Neo4j访问器
 * 
 * @author Birdy
 *
 */
public class Neo4jAccessor implements StorageAccessor {

    private static final int BATCH_SIZE = 1000;

    private enum Operation {
        AND, OR;
    }

    // 内置查询
    /** MATCH (clazz:Class) WHERE clazz.id = ? DELETE clazz */
    private final static String DELETE_CQL = "MATCH (clazz:{}) WHERE clazz.{} = {id} DELETE clazz";

    /** 查询指定范围的最大主键标识 */
    private final static String MAXIMUM_ID = "MATCH (clazz:{}) WHERE clazz.{} >= {from} AND clazz.{} < {to} RETURN max(clazz.{})";

    /** 查询指定范围的最小主键标识 */
    private final static String MINIMUM_ID = "MATCH (clazz:{}) WHERE clazz.{} >= {from} AND clazz.{} < {to} RETURN min(clazz.{})";

    /** 查询指定索引范围的主键映射 */
    private final static String INDEX_2_ID_MAP_BEGIN = "MATCH (clazz:{})";

    private final static String INDEX_2_ID_MAP_END = " RETURN clazz.{} as key, clazz.{} as value";

    /** 查询指定索引范围的对象列表 */
    private final static String INDEX_2_OBJECT_SET_BEGIN = "MATCH (clazz:{})";

    private final static String INDEX_2_OBJECT_SET_END = " RETURN clazz";

    private final static String BETWEEN_CONDITION = " WHERE clazz.{} >= {0} AND clazz.{} <= {1}";

    private final static String EQUAL_CONDITION = " WHERE clazz.{} = {0}";

    private final static String HIGHER_CONDITION = " WHERE clazz.{} > {0}";

    private final static String IN_CONDITION = " WHERE clazz.{} IN [{0}{}]";

    private final static String LOWER_CONDITION = " WHERE clazz.{} < {0}";

    private final static String UNEQUAL_CONDITION = " WHERE clazz.{} <> {0}";

    private final static String AND_CONDITION = " AND clazz.{} = {{}}";

    private final static String OR_CONDITION = " OR clazz.{} = {{}}";

    private final static String PAGINATION_CONDITION = " SKIP {} LIMIT {}";

    private final static String ITERATE_BEGIN = "MATCH (clazz:{})";

    private final static String ITERATE_END = " RETURN clazz";

    private final static String COUNT_BEGIN = "MATCH (clazz:{})";

    private final static String COUNT_END = " RETURN count(clazz)";

    /** CQL删除语句 */
    private Map<Class, String> deleteCqls = new ConcurrentHashMap<>();

    /** CQL查询语句(查询指定范围的最大主键标识),用于IdentityManager */
    private Map<Class, String> maximumIdCqls = new ConcurrentHashMap<>();

    /** CQL查询语句(查询指定范围的最小主键标识),用于IdentityManager */
    private Map<Class, String> minimumIdCqls = new ConcurrentHashMap<>();

    /** 元数据集合 */
    private HashMap<Class<?>, Neo4jMetadata> metadatas = new HashMap<>();

    private Session template;

    public Neo4jAccessor(SessionFactory factory) {
        this.template = factory.openSession();
        MetaData metaData = factory.metaData();
        for (ClassInfo information : metaData.persistentEntities()) {
            Class<?> ormClass = information.getUnderlyingClass();
            NodeEntity node = ormClass.getAnnotation(NodeEntity.class);
            RelationshipEntity relation = ormClass.getAnnotation(RelationshipEntity.class);
            if (node == null && relation == null) {
                continue;
            }
            Neo4jMetadata metadata = new Neo4jMetadata(ormClass);
            metadatas.put(ormClass, metadata);
            String ormName = metadata.getOrmName();
            String idName = metadata.getPrimaryName();

            String deletecCql = StringUtility.format(DELETE_CQL, ormName, idName);
            deleteCqls.put(ormClass, deletecCql);

            String maximumIdCql = StringUtility.format(MAXIMUM_ID, ormName, idName, idName, idName);
            maximumIdCqls.put(ormClass, maximumIdCql);

            String minimumIdCql = StringUtility.format(MINIMUM_ID, ormName, idName, idName, idName);
            minimumIdCqls.put(ormClass, minimumIdCql);
        }
    }

    @Override
    public Collection<? extends StorageMetadata> getAllMetadata() {
        return metadatas.values();
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> T getInstance(Class<T> clazz, K id) {
        try {
            // TODO 注意:Neo4j-OGM对原始类型支持似乎有问题,必须使用包装类型,否则Session.load无法装载到指定对象.
            return template.load(clazz, (Serializable) id);
        } finally {
            template.clear();
        }
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> boolean createInstance(Class<T> clazz, T object) {
        try {
            template.save(object);
            return true;
        } catch (Exception exception) {
            return false;
        } finally {
            template.clear();
        }
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> boolean deleteInstance(Class<T> clazz, K id) {
        try {
            Neo4jMetadata metadata = metadatas.get(clazz);
            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put(metadata.getPrimaryName(), id);
            String cql = deleteCqls.get(clazz);
            template.query(cql, parameters);
            return true;
        } catch (Exception exception) {
            return false;
        } finally {
            template.clear();
        }
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> boolean deleteInstance(Class<T> clazz, T object) {
        try {
            template.delete(object);
            return true;
        } catch (Exception exception) {
            return false;
        } finally {
            template.clear();
        }
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> boolean updateInstance(Class<T> clazz, T object) {
        try {
            template.save(object);
            return true;
        } catch (Exception exception) {
            return false;
        } finally {
            template.clear();
        }
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> K maximumIdentity(Class<T> clazz, K from, K to) {
        try {
            Neo4jMetadata metadata = metadatas.get(clazz);
            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put("from", from);
            parameters.put("to", to);
            String cql = maximumIdCqls.get(clazz);
            return template.queryForObject(metadata.getPrimaryClass(), cql, parameters);
        } finally {
            template.clear();
        }
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> K minimumIdentity(Class<T> clazz, K from, K to) {
        try {
            Neo4jMetadata metadata = metadatas.get(clazz);
            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put("from", from);
            parameters.put("to", to);
            String cql = minimumIdCqls.get(clazz);
            return template.queryForObject(metadata.getPrimaryClass(), cql, parameters);
        } finally {
            template.clear();
        }
    }

    @Override
    public <K extends Comparable, I, T extends IdentityObject<K>> Map<K, I> queryIdentities(Class<T> clazz, StorageCondition condition, String name, I... values) {
        if (!condition.checkValues(values)) {
            throw new StorageQueryException();
        }
        try {
            Neo4jMetadata metadata = metadatas.get(clazz);
            HashMap<String, Object> parameters = new HashMap<>();
            StringBuilder buffer = new StringBuilder(INDEX_2_ID_MAP_BEGIN);
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
                    string.append(", {");
                    string.append(index);
                    string.append("}");
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
            buffer.append(StringUtility.format(INDEX_2_ID_MAP_END, metadata.getPrimaryName(), name));
            String cql = buffer.toString();
            cql = StringUtility.format(cql, metadata.getOrmName(), name, name);
            for (int index = 0; index < values.length; index++) {
                parameters.put(String.valueOf(index), values[index]);
            }
            Iterable<Map<String, Object>> keyValues = template.query(cql, parameters);
            Map<K, I> map = new HashMap<>();
            for (Map<String, Object> keyValue : keyValues) {
                Object key = keyValue.get("key");
                Object value = keyValue.get("value");
                key = ConversionUtility.convert(key, metadata.getFields().get(metadata.getPrimaryName()));
                value = ConversionUtility.convert(value, metadata.getFields().get(name));
                map.put((K) key, (I) value);
            }
            return map;
        } finally {
            template.clear();
        }
    }

    @Override
    public <K extends Comparable, I, T extends IdentityObject<K>> List<T> queryInstances(Class<T> clazz, StorageCondition condition, String name, I... values) {
        if (!condition.checkValues(values)) {
            throw new StorageQueryException();
        }
        try {
            Neo4jMetadata metadata = metadatas.get(clazz);
            HashMap<String, Object> parameters = new HashMap<>();
            StringBuilder buffer = new StringBuilder(INDEX_2_OBJECT_SET_BEGIN);
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
                    string.append(", {");
                    string.append(index);
                    string.append("}");
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
            buffer.append(INDEX_2_OBJECT_SET_END);
            String cql = buffer.toString();
            cql = StringUtility.format(cql, metadata.getOrmName(), name, name);
            for (int index = 0; index < values.length; index++) {
                parameters.put(String.valueOf(index), values[index]);
            }
            Iterable<T> keyValues = template.query(clazz, cql, parameters);
            List<T> list = new ArrayList<>(BATCH_SIZE);
            for (T keyValue : keyValues) {
                list.add(keyValue);
            }
            return list;
        } finally {
            template.clear();
        }
    }

    private <K extends Comparable, T extends IdentityObject<K>> Iterable<T> iterate(Class<T> clazz, Operation operation, Map<String, Object> condition, StoragePagination pagination) {
        try {
            Neo4jMetadata metadata = metadatas.get(clazz);
            HashMap<String, Object> parameters = new HashMap<>();
            StringBuilder buffer = new StringBuilder(StringUtility.format(ITERATE_BEGIN, metadata.getOrmName()));
            if (condition != null) {
                int index = 0;
                final Iterator<Entry<String, Object>> iterator = condition.entrySet().iterator();
                if (iterator.hasNext()) {
                    Entry<String, Object> entry = iterator.next();
                    buffer.append(StringUtility.format(EQUAL_CONDITION, entry.getKey()));
                    parameters.put(String.valueOf(index), entry.getValue());
                    index++;
                }
                while (iterator.hasNext()) {
                    Entry<String, Object> entry = iterator.next();
                    switch (operation) {
                    case AND:
                        buffer.append(StringUtility.format(AND_CONDITION, entry.getKey(), index));
                        break;
                    case OR:
                        buffer.append(StringUtility.format(OR_CONDITION, entry.getKey(), index));
                        break;
                    default:
                        throw new UnsupportedOperationException();
                    }
                    parameters.put(String.valueOf(index), entry.getValue());
                    index++;
                }
            }
            buffer.append(ITERATE_END);
            if (pagination != null) {
                buffer.append(StringUtility.format(PAGINATION_CONDITION, pagination.getFirst(), pagination.getSize()));
            }
            String cql = buffer.toString();
            Iterable<T> iterable = template.query(clazz, cql, parameters);
            return iterable;
        } finally {
            template.clear();
        }
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> List<T> queryInstances(Class<T> clazz, StoragePagination pagination) {
        Iterable<T> iterable = iterate(clazz, null, null, pagination);
        List<T> query = new ArrayList<>(BATCH_SIZE);
        for (T keyValue : iterable) {
            query.add(keyValue);
        }
        return query;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> List<T> queryIntersection(Class<T> clazz, Map<String, Object> condition, StoragePagination pagination) {
        Iterable<T> iterable = iterate(clazz, Operation.AND, condition, pagination);
        List<T> query = new ArrayList<>(BATCH_SIZE);
        for (T keyValue : iterable) {
            query.add(keyValue);
        }
        return query;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> List<T> queryUnion(Class<T> clazz, Map<String, Object> condition, StoragePagination pagination) {
        Iterable<T> iterable = iterate(clazz, Operation.OR, condition, pagination);
        List<T> query = new ArrayList<>(BATCH_SIZE);
        for (T keyValue : iterable) {
            query.add(keyValue);
        }
        return query;
    }

    private <K extends Comparable, T extends IdentityObject<K>> long count(Class<T> clazz, Operation operation, Map<String, Object> condition) {
        try {
            Neo4jMetadata metadata = metadatas.get(clazz);
            HashMap<String, Object> parameters = new HashMap<>();
            StringBuilder buffer = new StringBuilder(StringUtility.format(COUNT_BEGIN, metadata.getOrmName()));
            if (condition != null) {
                int index = 0;
                final Iterator<Entry<String, Object>> iterator = condition.entrySet().iterator();
                if (iterator.hasNext()) {
                    Entry<String, Object> entry = iterator.next();
                    buffer.append(StringUtility.format(EQUAL_CONDITION, entry.getKey()));
                    parameters.put(String.valueOf(index), entry.getValue());
                    index++;
                }
                while (iterator.hasNext()) {
                    Entry<String, Object> entry = iterator.next();
                    switch (operation) {
                    case AND:
                        buffer.append(StringUtility.format(AND_CONDITION, entry.getKey(), index));
                        break;
                    case OR:
                        buffer.append(StringUtility.format(OR_CONDITION, entry.getKey(), index));
                        break;
                    default:
                        throw new UnsupportedOperationException();
                    }
                    parameters.put(String.valueOf(index), entry.getValue());
                    index++;
                }
            }
            buffer.append(COUNT_END);
            String cql = buffer.toString();
            Long count = template.queryForObject(Long.class, cql, parameters);
            return count;
        } finally {
            template.clear();
        }
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

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void iterate(StorageIterator<T> iterator, final Class<T> clazz, StoragePagination pagination) {
        Iterable<T> iterable = iterate(clazz, null, null, pagination);
        for (T keyValue : iterable) {
            iterator.iterate(keyValue);
        }
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void iterateIntersection(StorageIterator<T> iterator, final Class<T> clazz, Map<String, Object> condition, StoragePagination pagination) {
        Iterable<T> iterable = iterate(clazz, Operation.AND, condition, pagination);
        for (T keyValue : iterable) {
            iterator.iterate(keyValue);
        }
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void iterateUnion(StorageIterator<T> iterator, final Class<T> clazz, Map<String, Object> condition, StoragePagination pagination) {
        Iterable<T> iterable = iterate(clazz, Operation.OR, condition, pagination);
        for (T keyValue : iterable) {
            iterator.iterate(keyValue);
        }
    }

    void clear() {
        template.clear();
    }

}
