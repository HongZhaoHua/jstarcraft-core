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

/**
 * ElasticSearch事务管理器
 * 
 * @author Birdy
 *
 */
public class ElasticSearchTransactionManager extends TransactionManager {

    public static final String DEFAULT_INDEX = "jstarcraft";

    public static final String DEFAULT_TYPE = "ElasticSearchTransactionDefinition";

    public static final String NAME = "name";

    public static final String MOST = "most";

    public static final String NOW = "now";

    private static final String SCRIPT = "painless";

    private static final String LOCK_SCRIPT =

            "if (ctx._source." + MOST + " <= " + "params." + NOW + ") { " +

                    "ctx._source." + MOST + " =  params." + MOST + "; " +

                    "} else { " +

                    "ctx.op = 'none' " +

                    "}";

    private static final String UNLOCK_SCRIPT =

            "if (ctx._source." + MOST + " > " + "params." + NOW + ") { " +

                    "ctx._source." + MOST + " =  params." + NOW + "; " +

                    "} else { " +

                    "ctx.op = 'none' " +

                    "}";

    private final RestHighLevelClient highLevelClient;
    private final String index;
    private final String type;

    public ElasticSearchTransactionManager(RestHighLevelClient highLevelClient) {
        this(highLevelClient, DEFAULT_INDEX, DEFAULT_TYPE);
    }

    public ElasticSearchTransactionManager(RestHighLevelClient highLevelClient, String index, String type) {
        this.highLevelClient = highLevelClient;
        this.index = index;
        this.type = type;
    }

    @Override
    protected void lock(TransactionDefinition definition) {
        // 尝试加锁
        String key = definition.getName();
        Long value = definition.getMost().toEpochMilli();
        Map<String, Object> document = new HashMap<>();
        document.put(NAME, key);
        document.put(MOST, value);
        document.put(NOW, Instant.now().toEpochMilli());
        UpdateRequest ur = new UpdateRequest().index(index).type(type)

                .id(key)

                .script(new Script(ScriptType.INLINE, SCRIPT, LOCK_SCRIPT, document))

                .upsert(document)

                .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        try {
            UpdateResponse res = highLevelClient.update(ur, RequestOptions.DEFAULT);
            if (res.getResult() == DocWriteResponse.Result.NOOP) {
                throw new TransactionLockException();
            }
        } catch (Exception exception) {
            throw new TransactionLockException(exception);
        }

    }

    @Override
    protected void unlock(TransactionDefinition definition) {
        // 尝试解锁
        String key = definition.getName();
        Long value = definition.getMost().toEpochMilli();
        Map<String, Object> document = new HashMap<>();
        document.put(NAME, key);
        document.put(MOST, value);
        document.put(NOW, Instant.now().toEpochMilli());
        UpdateRequest ur = new UpdateRequest().index(index).type(type)

                .id(key)

                .script(new Script(ScriptType.INLINE, SCRIPT, UNLOCK_SCRIPT, document));
        try {
            UpdateResponse res = highLevelClient.update(ur, RequestOptions.DEFAULT);
            if (res.getResult() == DocWriteResponse.Result.NOOP) {
                throw new TransactionUnlockException();
            }
        } catch (Exception exception) {
            throw new TransactionUnlockException(exception);
        }
    }

}
