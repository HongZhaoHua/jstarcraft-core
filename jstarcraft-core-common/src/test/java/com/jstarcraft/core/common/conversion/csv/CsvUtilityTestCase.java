package com.jstarcraft.core.common.conversion.csv;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.common.conversion.csv.CsvUtility;
import com.jstarcraft.core.common.conversion.json.JsonUtility;
import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.utility.KeyValue;
import com.jstarcraft.core.utility.StringUtility;

public class CsvUtilityTestCase {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void testConvert() {
        CsvObject object = CsvObject.instanceOf(0, "birdy", "hong", 1, Instant.now(), CsvEnumeration.TERRAN);
        String csv = CsvUtility.object2String(object, CsvObject.class);
        Object intance = CsvUtility.string2Object(csv, CsvObject.class);
        Assert.assertThat(intance, CoreMatchers.equalTo(object));

        Object[] array = new CsvObject[] { object };
        csv = CsvUtility.object2String(array, CsvObject[].class);
        intance = CsvUtility.string2Object(csv, CsvObject[].class);
        Assert.assertThat(intance, CoreMatchers.equalTo(array));

        Type collectionType = TypeUtility.parameterize(HashSet.class, CsvObject.class);
        HashSet<CsvObject> collection = new HashSet<>();
        collection.add(object);
        csv = CsvUtility.object2String(collection, collectionType);
        intance = CsvUtility.string2Object(csv, collectionType);
        Assert.assertThat(intance, CoreMatchers.equalTo(collection));

        Type mapType = TypeUtility.parameterize(HashMap.class, CsvObject.class, CsvObject.class);
        HashMap<CsvObject, CsvObject> map = new HashMap<>();
        map.put(object, object);
        csv = CsvUtility.object2String(map, mapType);
        intance = CsvUtility.string2Object(csv, mapType);
        Assert.assertThat(intance, CoreMatchers.equalTo(map));

        Type keyValueType = TypeUtility.parameterize(KeyValue.class, Integer.class, CsvObject.class);
        KeyValue<Integer, CsvObject> keyValue = new KeyValue<>(0, object);
        csv = CsvUtility.object2String(keyValue, keyValueType);
        intance = CsvUtility.string2Object(csv, keyValueType);
        Assert.assertThat(intance, CoreMatchers.equalTo(keyValue));

        // 测试区分对象为null与属性为null的情况.
        Type arrayType = TypeUtility.genericArrayType(keyValueType);
        array = new KeyValue[] { null, new KeyValue<>(null, null), keyValue };
        csv = CsvUtility.object2String(array, arrayType);
        intance = CsvUtility.string2Object(csv, arrayType);
        Assert.assertThat(intance, CoreMatchers.equalTo(array));
    }

    @Test
    public void testPerformance() {
        Instant now = null;
        int times = 100000;
        CsvObject object = CsvObject.instanceOf(0, "birdy", "hong", 10, Instant.now(), CsvEnumeration.TERRAN);
        // CSV与JSON各自都装载一次
        String csv = CsvUtility.object2String(object, CsvObject.class);
        String json = JsonUtility.object2String(object);
        String message = StringUtility.format("CSV格式化与JSON格式化对比:\nCSV:{}\nJSON:{}", csv, json);
        logger.debug(message);

        now = Instant.now();
        for (int index = 0; index < times; index++) {
            CsvUtility.object2String(CsvObject.instanceOf(index, "birdy" + index, "hong" + index, index % 10, Instant.now(), CsvEnumeration.TERRAN), CsvObject.class);
        }
        logger.debug(StringUtility.format("CSV编码{}次一共消耗{}毫秒.", times, System.currentTimeMillis() - now.toEpochMilli()));

        now = Instant.now();
        for (int index = 0; index < times; index++) {
            JsonUtility.object2String(CsvObject.instanceOf(index, "birdy" + index, "hong" + index, index % 10, Instant.now(), CsvEnumeration.TERRAN));
        }
        logger.debug(StringUtility.format("JSON编码{}次一共消耗{}毫秒.", times, System.currentTimeMillis() - now.toEpochMilli()));

        now = Instant.now();
        for (int index = 0; index < times; index++) {
            csv = CsvUtility.object2String(CsvObject.instanceOf(index, "birdy" + index, "hong" + index, index % 10, Instant.now(), CsvEnumeration.TERRAN), CsvObject.class);
            CsvUtility.string2Object(csv, CsvObject.class);
        }
        logger.debug(StringUtility.format("CSV解码{}次一共消耗{}毫秒.", times, System.currentTimeMillis() - now.toEpochMilli()));

        now = Instant.now();
        for (int index = 0; index < times; index++) {
            json = JsonUtility.object2String(CsvObject.instanceOf(index, "birdy" + index, "hong" + index, index % 10, Instant.now(), CsvEnumeration.TERRAN));
            JsonUtility.string2Object(json, CsvObject.class);
        }
        logger.debug(StringUtility.format("JSON解码{}次一共消耗{}毫秒.", times, System.currentTimeMillis() - now.toEpochMilli()));
    }

}
