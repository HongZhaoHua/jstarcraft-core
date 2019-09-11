package com.jstarcraft.core.common.conversion.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.common.conversion.csv.annotation.CsvConfiguration;
import com.jstarcraft.core.common.conversion.csv.query.AwkCommand;
import com.jstarcraft.core.common.conversion.csv.query.AwkCondition;
import com.jstarcraft.core.utility.StringUtility;

public class AwkQueryTestCase {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String file = "csv";

    private static final String type = "awk";

    private static final List<String> paths = new LinkedList<>();

    private static final Logger storage = Logger.getLogger("Storage");

    @BeforeClass
    public static void beforeTest() {
        paths.add("logs/" + type + "/2017-01-01/" + file + ".00-00-00.log");
        paths.add("logs/" + type + "/2017-01-01/" + file + ".00-05-00.log");

        for (String path : paths) {
            File file = new File(path);
            FileUtils.deleteQuietly(file);
        }

        ZonedDateTime dateTime = ZonedDateTime.of(2017, 1, 1, 0, 4, 0, 0, ZoneId.of("UTC"));
        CsvObject log = null;
        for (int index = 0; index < 100; index++) {
            // 创建以逗号分隔的CSV文件.
            log = CsvObject.instanceOf(index, "birdy", "hong", index % 10, Instant.now(), CsvEnumeration.TERRAN);
            storage.info(new Object[] { Instant.from(dateTime), log, type, file });
        }

        dateTime = dateTime.plusMinutes(2);
        // 创建包含双引号,单引号,逗号,中文字符串的CSV文件.
        log = CsvObject.instanceOf(1, "\"mickey's,mouse\"", "洪", 1, Instant.now(), CsvEnumeration.TERRAN);
        storage.info(new Object[] { Instant.from(dateTime), log, type, file });
        log = CsvObject.instanceOf(2, "\"mickey's,mouse\"", "\"洪", 1, Instant.now(), CsvEnumeration.TERRAN);
        storage.info(new Object[] { Instant.from(dateTime), log, type, file });
        log = CsvObject.instanceOf(3, "\"mickey's,mouse\"", "洪\"", 1, Instant.now(), CsvEnumeration.TERRAN);
        storage.info(new Object[] { Instant.from(dateTime), log, type, file });
        log = CsvObject.instanceOf(4, "\"mickey's,mouse\"", "\"洪\"", 1, Instant.now(), CsvEnumeration.TERRAN);
        storage.info(new Object[] { Instant.from(dateTime), log, type, file });

        log = CsvObject.instanceOf(1, "\"mickey's,mouse\"", ";洪", 1, Instant.now(), CsvEnumeration.TERRAN);
        storage.info(new Object[] { Instant.from(dateTime), log, type, file });
        log = CsvObject.instanceOf(2, "\"mickey's,mouse\"", ";\"洪", 1, Instant.now(), CsvEnumeration.TERRAN);
        storage.info(new Object[] { Instant.from(dateTime), log, type, file });
        log = CsvObject.instanceOf(3, "\"mickey's,mouse\"", ";洪\"", 1, Instant.now(), CsvEnumeration.TERRAN);
        storage.info(new Object[] { Instant.from(dateTime), log, type, file });
        log = CsvObject.instanceOf(4, "\"mickey's,mouse\"", ";\"洪\"", 1, Instant.now(), CsvEnumeration.TERRAN);
        storage.info(new Object[] { Instant.from(dateTime), log, type, file });

        log = CsvObject.instanceOf(1, "\"mickey's,mouse\"", "洪;", 1, Instant.now(), CsvEnumeration.TERRAN);
        storage.info(new Object[] { Instant.from(dateTime), log, type, file });
        log = CsvObject.instanceOf(2, "\"mickey's,mouse\"", "\"洪;", 1, Instant.now(), CsvEnumeration.TERRAN);
        storage.info(new Object[] { Instant.from(dateTime), log, type, file });
        log = CsvObject.instanceOf(3, "\"mickey's,mouse\"", "洪\";", 1, Instant.now(), CsvEnumeration.TERRAN);
        storage.info(new Object[] { Instant.from(dateTime), log, type, file });
        log = CsvObject.instanceOf(4, "\"mickey's,mouse\"", "\"洪\";", 1, Instant.now(), CsvEnumeration.TERRAN);
        storage.info(new Object[] { Instant.from(dateTime), log, type, file });
    }

    @Test
    public void test() {
        CsvConfiguration configuration = CsvObject.class.getAnnotation(CsvConfiguration.class);
        AwkCommand command = new AwkCommand(",", "\"", configuration.value());
        AwkCondition inCondition = command.in("id", 0, 1, 2, 3, 4);
        AwkCondition equalCondition = command.lessEqual("money", 4);
        AwkCondition andCondition = command.and(inCondition, equalCondition);

        String[] files = new String[paths.size()];
        for (int index = 0; index < paths.size(); index++) {
            String path = paths.get(index);
            File file = new File(path);
            files[index] = file.getAbsolutePath();
        }

        try {
            String query = command.query(andCondition, files);
            logger.debug(query);
            int count = 0;
            // 由于涉及到与AWK的交互,需要设置用户环境变量.
            Process process = Runtime.getRuntime().exec(query);
            try (InputStream stream = process.getInputStream(); InputStreamReader reader = new InputStreamReader(stream, StringUtility.CHARSET); BufferedReader buffer = new BufferedReader(reader)) {
                for (String line = buffer.readLine(); line != null; line = buffer.readLine()) {
                    logger.debug(line);
                    Assert.assertTrue(StringUtility.isNotBlank(line));
                    count++;
                }
            }
            Assert.assertThat(count, CoreMatchers.equalTo(17));
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        try {
            String count = command.count(andCondition, files);
            logger.debug(count);
            // 由于涉及到与AWK的交互,需要设置用户环境变量.
            Process process = Runtime.getRuntime().exec(count);
            try (InputStream stream = process.getInputStream(); InputStreamReader reader = new InputStreamReader(stream, StringUtility.CHARSET); BufferedReader buffer = new BufferedReader(reader)) {
                for (String line = buffer.readLine(); line != null; line = buffer.readLine()) {
                    logger.debug(line);
                    Assert.assertTrue(StringUtility.isNotBlank(line));
                    Assert.assertThat(Integer.valueOf(line), CoreMatchers.equalTo(17));
                }
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

}
