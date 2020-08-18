package com.jstarcraft.core.common.instant;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class CronExpressionTestCase {

    private List<LocalDateTime> dateTimes = new ArrayList<>();
    {
        dateTimes.add(LocalDateTime.of(2020, 1, 1, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 1, 30, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 2, 1, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 3, 1, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 3, 30, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 4, 1, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 4, 30, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 5, 1, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 5, 30, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 6, 1, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 6, 30, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 7, 1, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 7, 30, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 8, 1, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 8, 30, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 9, 1, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 9, 30, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 10, 1, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 10, 30, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 11, 1, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 11, 30, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 12, 1, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 12, 30, 12, 0, 0));
    }

    @Test
    public void testGetPreviousDateTime() {
        CronExpression expression = new CronExpression("0 0 12 1,30 * ?");

        LocalDateTime dateTime = LocalDateTime.of(2020, 12, 30, 23, 59, 59);
        Assert.assertFalse(expression.isMatchDateTime(dateTime));
        for (int index = dateTimes.size() - 1; index > 0; index--) {
            dateTime = expression.getPreviousDateTime(dateTime);
            Assert.assertEquals(dateTimes.get(index), dateTime);
            Assert.assertTrue(expression.isMatchDateTime(dateTime));
        }
    }

    @Test
    public void testGetNextDateTime() {
        CronExpression expression = new CronExpression("0 0 12 1,30 * ?");

        LocalDateTime dateTime = LocalDateTime.of(2020, 1, 1, 0, 0, 0);
        Assert.assertFalse(expression.isMatchDateTime(dateTime));
        for (int index = 0, size = dateTimes.size(); index < size; index++) {
            dateTime = expression.getNextDateTime(dateTime);
            Assert.assertEquals(dateTimes.get(index), dateTime);
            Assert.assertTrue(expression.isMatchDateTime(dateTime));
        }
    }

    @Test
    public void testYear() {
        CronExpression expression = new CronExpression("0 0 12 1,30 * ? 2020");
        {
            LocalDateTime dateTime = expression.getPreviousDateTime(dateTimes.get(0));
            Assert.assertNull(dateTime);
        }
        {
            LocalDateTime dateTime = expression.getNextDateTime(dateTimes.get(dateTimes.size() - 1));
            Assert.assertNull(dateTime);
        }

        // TODO 此处cron-utils存在Bug,导致测试无法通过,等待修复.
//        expression = new SolarExpression("0 0 12 29 6 ? 2000/10");
//        LocalTime time = LocalTime.of(12, 0, 0);
//        LocalDateTime dateTime = LocalDateTime.of(new SolarDate(2020, 6, 29).getDate(), time);
//        {
//            Assert.assertEquals(LocalDateTime.of(new SolarDate(2010, 6, 29).getDate(), time), expression.getPreviousDateTime(dateTime));
//        }
//        {
//            Assert.assertEquals(LocalDateTime.of(new SolarDate(2030, 6, 29).getDate(), time), expression.getNextDateTime(dateTime));
//        }

        expression = new CronExpression("0 0 12 1,31 * ? 2010");
        {
            LocalDateTime dateTime = LocalDateTime.of(2008, 10, 31, 23, 59, 59);
            dateTime = expression.getNextDateTime(dateTime);
            Assert.assertEquals(LocalDateTime.of(2010, 1, 1, 12, 0, 0), dateTime);
        }
        // TODO 此处cron-utils存在Bug,导致测试无法通过,等待修复.
//        {
//            LocalDateTime dateTime = LocalDateTime.of(2012, 2, 1, 0, 0, 0);
//            dateTime = expression.getPreviousDateTime(dateTime);
//            Assert.assertEquals(LocalDateTime.of(2010, 12, 31, 12, 0, 0), dateTime);
//        }
    }

    @Test
    public void testLast() {
        // 每个月倒数第2天
        CronExpression expression = new CronExpression("0 0 12 L-1 * ? 2020");
        {
            LocalDateTime dateTime = LocalDateTime.of(2021, 1, 1, 0, 0, 0);
            for (int index = 0, size = 12; index < size; index++) {
                dateTime = expression.getPreviousDateTime(dateTime);
                Assert.assertEquals(YearMonth.of(dateTime.getYear(), dateTime.getMonth()).lengthOfMonth() - 1, dateTime.getDayOfMonth());
                Assert.assertEquals(2020, dateTime.getYear());
            }
        }
        {
            LocalDateTime dateTime = LocalDateTime.of(2020, 1, 1, 0, 0, 0);
            for (int index = 0, size = 12; index < size; index++) {
                dateTime = expression.getNextDateTime(dateTime);
                Assert.assertEquals(YearMonth.of(dateTime.getYear(), dateTime.getMonth()).lengthOfMonth() - 1, dateTime.getDayOfMonth());
                Assert.assertEquals(2020, dateTime.getYear());
            }
        }
    }

    @Test
    public void testWeek() {
        {
            CronExpression expression = new CronExpression("0 0 12 ? * SUNL 2020");
            LocalDateTime dateTime = LocalDateTime.of(2021, 1, 1, 0, 0, 0);
            for (int index = 0, size = 12; index < size; index++) {
                dateTime = expression.getPreviousDateTime(dateTime);
                Assert.assertEquals(LocalDateTime.of(2020, 12 - index, 1, 12, 0, 0).with(TemporalAdjusters.dayOfWeekInMonth(-1, DayOfWeek.SUNDAY)), dateTime);
            }
        }
        {
            CronExpression expression = new CronExpression("0 0 12 ? * SUNL 2020");
            LocalDateTime dateTime = LocalDateTime.of(2020, 1, 1, 0, 0, 0);
            for (int index = 0, size = 12; index < size; index++) {
                dateTime = expression.getNextDateTime(dateTime);
                Assert.assertEquals(LocalDateTime.of(2020, 1 + index, 1, 12, 0, 0).with(TemporalAdjusters.dayOfWeekInMonth(-1, DayOfWeek.SUNDAY)), dateTime);
            }
        }
        {
            // TODO 此处cron-utils存在Bug,导致测试无法通过,等待修复.
//            SolarExpression expression = new SolarExpression("0 0 12 ? * SUN#4 2020");
//            LocalDateTime dateTime = LocalDateTime.of(2021, 1, 1, 0, 0, 0);
//            for (int index = 0, size = 12; index < size; index++) {
//                dateTime = expression.getPreviousDateTime(dateTime);
//                Assert.assertEquals(LocalDateTime.of(2020, 12 - index, 1, 12, 0, 0).with(TemporalAdjusters.dayOfWeekInMonth(4, DayOfWeek.SUNDAY)), dateTime);
//            }
        }
        {
            CronExpression expression = new CronExpression("0 0 12 ? * SUN#4 2020");
            LocalDateTime dateTime = LocalDateTime.of(2020, 1, 1, 0, 0, 0);
            for (int index = 0, size = 12; index < size; index++) {
                dateTime = expression.getNextDateTime(dateTime);
                Assert.assertEquals(LocalDateTime.of(2020, 1 + index, 1, 12, 0, 0).with(TemporalAdjusters.dayOfWeekInMonth(4, DayOfWeek.SUNDAY)), dateTime);
            }
        }
    }

    @Test
    public void testBoundary() {
        // TODO 此处cron-utils存在Bug,导致测试无法通过,等待修复.
        // QUARTZ有年份限制(1970-2099)
//        CronDefinition definition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
//        CronParser parser = new CronParser(definition);
//        ExecutionTime execution = ExecutionTime.forCron(parser.parse("0 0 12 * * ?"));
//        {
//            ZonedDateTime dateTime = ZonedDateTime.of(1900, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
//            dateTime = execution.nextExecution(dateTime).orElse(null);
//            Assert.assertEquals(ZonedDateTime.of(1970, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC), dateTime);
//        }
//        {
//            ZonedDateTime dateTime = ZonedDateTime.of(2150, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
//            dateTime = execution.lastExecution(dateTime).orElse(null);
//            Assert.assertEquals(ZonedDateTime.of(2099, 12, 31, 12, 0, 0, 0, ZoneOffset.UTC), dateTime);
//        }

        // 表达式无年份限制
        CronExpression expression = new CronExpression("0 0 12 * * ?");
        {
            LocalDateTime dateTime = LocalDateTime.of(1849, 12, 31, 23, 59, 59);
            dateTime = expression.getNextDateTime(dateTime);
            Assert.assertEquals(LocalDateTime.of(1850, 1, 1, 12, 0, 0), dateTime);
        }
        {
            LocalDateTime dateTime = LocalDateTime.of(2150, 1, 1, 0, 0, 0);
            dateTime = expression.getPreviousDateTime(dateTime);
            Assert.assertEquals(LocalDateTime.of(2149, 12, 31, 12, 0, 0), dateTime);
        }
    }

    @Test
    public void testSlash() {
        CronExpression leftExpression = new CronExpression("0 0 12 1,6,11,16,21,26 * ?");
        CronExpression rightExpression = new CronExpression("0 0 12 1/5 * ?");

        LocalDateTime dateTime = LocalDateTime.of(2020, 6, 30, 0, 0, 0);

        Assert.assertEquals(leftExpression.getNextDateTime(dateTime), rightExpression.getNextDateTime(dateTime));
        Assert.assertEquals(leftExpression.getPreviousDateTime(dateTime), rightExpression.getPreviousDateTime(dateTime));
    }

}
