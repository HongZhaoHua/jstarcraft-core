package com.jstarcraft.core.transaction.elasticsearch;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;

import com.jstarcraft.core.transaction.TransactionDefinition;
import com.jstarcraft.core.transaction.TransactionManager;
import com.jstarcraft.core.transaction.exception.TransactionLockException;
import com.jstarcraft.core.transaction.exception.TransactionUnlockException;
import com.jstarcraft.core.utility.StringUtility;

/**
 * Elasticsearch事务管理器
 * 
 * <pre>
 * 需要设置script.max_compilations_rate参数
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/circuit-breaker.html#script-compilation-circuit-breaker
 * </pre>
 * 
 * @author Birdy
 *
 */
public class ElasticsearchTransactionManager extends TransactionManager {

    public static final String DEFAULT_INDEX = "jstarcraft";

    public static final String DEFAULT_TYPE = "ElasticsearchTransactionDefinition";

    public static final String NAME = "name";

    public static final String MOST = "most";

    public static final String NOW = "now";

    private static final String SCRIPT = "painless";

    private static final String LOCK_SCRIPT = new StringBuilder()

            .append(StringUtility.format("if (ctx._source.{} <= params.{}) {", MOST, NOW))

            .append(StringUtility.format("    ctx._source.{} = params.{};", MOST, MOST))

            .append(StringUtility.format("} else {"))

            .append(StringUtility.format("    ctx.op = 'none';"))

            .append(StringUtility.format("}"))

            .toString();

    private static final String UNLOCK_SCRIPT = new StringBuilder()

            .append(StringUtility.format("if (ctx._source.{} > params.{}) {", MOST, NOW))

            .append(StringUtility.format("    ctx._source.{} = params.{};", MOST, NOW))

            .append(StringUtility.format("} else {"))

            .append(StringUtility.format("    ctx.op = 'none';"))

            .append(StringUtility.format("}"))

            .toString();

    private final RestHighLevelClient elastic;

    private final String index;

    private final String type;

    public ElasticsearchTransactionManager(RestHighLevelClient elastic) {
        this(elastic, DEFAULT_INDEX, DEFAULT_TYPE);
    }

    public ElasticsearchTransactionManager(RestHighLevelClient elastic, String index, String type) {
        this.elastic = elastic;
        this.index = index;
        this.type = type;
    }

    @Override
    public void lock(TransactionDefinition definition) {
        // 尝试加锁
        String key = definition.getName();
        Long value = definition.getMost().toEpochMilli();
        Map<String, Object> document = new HashMap<>();
        document.put(NAME, key);
        document.put(MOST, value);
        document.put(NOW, Instant.now().toEpochMilli());
        UpdateRequest request = new UpdateRequest().index(index).type(type)

                .id(key)

                .script(new Script(ScriptType.INLINE, SCRIPT, LOCK_SCRIPT, document))

                .upsert(document)

                .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        try {
            UpdateResponse response = elastic.update(request, RequestOptions.DEFAULT);
            if (response.getResult() == DocWriteResponse.Result.NOOP) {
                throw new TransactionLockException();
            }
        } catch (Exception exception) {
            throw new TransactionLockException(exception);
        }

    }

    @Override
    public void unlock(TransactionDefinition definition) {
        // 尝试解锁
        String key = definition.getName();
        Long value = definition.getMost().toEpochMilli();
        Map<String, Object> document = new HashMap<>();
        document.put(NAME, key);
        document.put(MOST, value);
        document.put(NOW, Instant.now().toEpochMilli());
        UpdateRequest request = new UpdateRequest().index(index).type(type)

                .id(key)

                .script(new Script(ScriptType.INLINE, SCRIPT, UNLOCK_SCRIPT, document));
        try {
            UpdateResponse response = elastic.update(request, RequestOptions.DEFAULT);
            if (response.getResult() == DocWriteResponse.Result.NOOP) {
                throw new TransactionUnlockException();
            }
        } catch (Exception exception) {
            throw new TransactionUnlockException(exception);
        }
    }

}
