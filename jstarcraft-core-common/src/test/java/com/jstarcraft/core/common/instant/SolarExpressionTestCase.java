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

public class SolarExpressionTestCase {

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
        SolarExpression expression = new SolarExpression("0 0 12 1,30 * ?");

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
        SolarExpression expression = new SolarExpression("0 0 12 1,30 * ?");

        LocalDateTime dateTime = LocalDateTime.of(2020, 1, 1, 0, 0, 0);
        Assert.assertFalse(expression.isMatchDateTime(dateTime));
        for (int index = 0, size = dateTimes.size(); index < size; index++) {
            dateTime = expression.getNextDateTime(dateTime);
            Assert.assertEquals(dateTimes.get(index), dateTime);
            Assert.assertTrue(expression.isMatchDateTime(dateTime));
        }
    }

    @Test
    public void testDate() {
        SolarExpression expression = new SolarExpression("0 0 12 1,30 * ? 2020");
        {
            LocalDateTime dateTime = expression.getPreviousDateTime(dateTimes.get(0));
            Assert.assertNull(dateTime);
        }
        {
            LocalDateTime dateTime = expression.getNextDateTime(dateTimes.get(dateTimes.size() - 1));
            Assert.assertNull(dateTime);
        }

        expression = new SolarExpression("0 0 12 15 6 ? 2000/10");
        LocalTime time = LocalTime.of(12, 0, 0);
        LocalDateTime dateTime = LocalDateTime.of(new SolarDate(2020, 6, 15).getDate(), time);
        Assert.assertEquals(LocalDateTime.of(new SolarDate(2010, 6, 15).getDate(), time), expression.getPreviousDateTime(dateTime));
        Assert.assertEquals(LocalDateTime.of(new SolarDate(2030, 6, 15).getDate(), time), expression.getNextDateTime(dateTime));

        expression = new SolarExpression("0 0 12 15 6 ? 2020");
        dateTime = LocalDateTime.of(2021, 12, 31, 12, 0, 0);
        Assert.assertEquals(LocalDateTime.of(2020, 6, 15, 12, 0, 0), expression.getPreviousDateTime(dateTime));
        dateTime = LocalDateTime.of(2019, 1, 1, 12, 0, 0);
        Assert.assertEquals(LocalDateTime.of(2020, 6, 15, 12, 0, 0), expression.getNextDateTime(dateTime));

        dateTime = LocalDateTime.of(2020, 12, 31, 12, 0, 0);
        Assert.assertEquals(LocalDateTime.of(2020, 6, 15, 12, 0, 0), expression.getPreviousDateTime(dateTime));
        dateTime = LocalDateTime.of(2020, 1, 1, 12, 0, 0);
        Assert.assertEquals(LocalDateTime.of(2020, 6, 15, 12, 0, 0), expression.getNextDateTime(dateTime));

        dateTime = LocalDateTime.of(2020, 6, 30, 12, 0, 0);
        Assert.assertEquals(LocalDateTime.of(2020, 6, 15, 12, 0, 0), expression.getPreviousDateTime(dateTime));
        dateTime = LocalDateTime.of(2020, 6, 1, 12, 0, 0);
        Assert.assertEquals(LocalDateTime.of(2020, 6, 15, 12, 0, 0), expression.getNextDateTime(dateTime));
    }

    @Test
    public void testLast() {
        // 每个月倒数第2天
        SolarExpression expression = new SolarExpression("0 0 12 L * ? 2020");
        {
            LocalDateTime dateTime = LocalDateTime.of(2021, 1, 1, 0, 0, 0);
            for (int index = 0, size = 12; index < size; index++) {
                dateTime = expression.getPreviousDateTime(dateTime);
                Assert.assertEquals(YearMonth.of(dateTime.getYear(), dateTime.getMonth()).lengthOfMonth(), dateTime.getDayOfMonth());
                Assert.assertEquals(2020, dateTime.getYear());
            }
        }
        {
            LocalDateTime dateTime = LocalDateTime.of(2020, 1, 1, 0, 0, 0);
            for (int index = 0, size = 12; index < size; index++) {
                dateTime = expression.getNextDateTime(dateTime);
                Assert.assertEquals(YearMonth.of(dateTime.getYear(), dateTime.getMonth()).lengthOfMonth(), dateTime.getDayOfMonth());
                Assert.assertEquals(2020, dateTime.getYear());
            }
        }
    }

    @Test
    public void testWeek() {
        {
            SolarExpression expression = new SolarExpression("0 0 12 ? * 1 2020");
            for (int index = 0, size = 12; index < size; index++) {
                LocalDateTime dateTime = LocalDateTime.of(2020, 12 - index, 15, 23, 59, 59);
                dateTime = dateTime.with(TemporalAdjusters.lastDayOfMonth());
                dateTime = expression.getPreviousDateTime(dateTime);
                Assert.assertEquals(LocalDateTime.of(2020, 12 - index, 1, 12, 0, 0).with(TemporalAdjusters.dayOfWeekInMonth(-1, DayOfWeek.SUNDAY)), dateTime);
            }
        }
        {
            SolarExpression expression = new SolarExpression("0 0 12 ? * 1 2020");
            for (int index = 0, size = 12; index < size; index++) {
                LocalDateTime dateTime = LocalDateTime.of(2020, 1 + index, 15, 0, 0, 0);
                dateTime = dateTime.with(TemporalAdjusters.firstDayOfMonth());
                dateTime = expression.getNextDateTime(dateTime);
                Assert.assertEquals(LocalDateTime.of(2020, 1 + index, 1, 12, 0, 0).with(TemporalAdjusters.dayOfWeekInMonth(1, DayOfWeek.SUNDAY)), dateTime);
            }
        }
        {
            SolarExpression expression = new SolarExpression("0 0 12 ? * SUN#4 2020");
            LocalDateTime dateTime = LocalDateTime.of(2021, 1, 1, 0, 0, 0);
            for (int index = 0, size = 12; index < size; index++) {
                dateTime = expression.getPreviousDateTime(dateTime);
                Assert.assertEquals(LocalDateTime.of(2020, 12 - index, 1, 12, 0, 0).with(TemporalAdjusters.dayOfWeekInMonth(4, DayOfWeek.SUNDAY)), dateTime);
            }
        }
        {
            SolarExpression expression = new SolarExpression("0 0 12 ? * SUN#4 2020");
            LocalDateTime dateTime = LocalDateTime.of(2020, 1, 1, 0, 0, 0);
            for (int index = 0, size = 12; index < size; index++) {
                dateTime = expression.getNextDateTime(dateTime);
                Assert.assertEquals(LocalDateTime.of(2020, 1 + index, 1, 12, 0, 0).with(TemporalAdjusters.dayOfWeekInMonth(4, DayOfWeek.SUNDAY)), dateTime);
            }
        }
        {
            SolarExpression expression = new SolarExpression("0 0 12 ? * SUNL 2020");
            LocalDateTime dateTime = LocalDateTime.of(2021, 1, 1, 0, 0, 0);
            for (int index = 0, size = 12; index < size; index++) {
                dateTime = expression.getPreviousDateTime(dateTime);
                Assert.assertEquals(LocalDateTime.of(2020, 12 - index, 1, 12, 0, 0).with(TemporalAdjusters.dayOfWeekInMonth(-1, DayOfWeek.SUNDAY)), dateTime);
            }
        }
        {
            SolarExpression expression = new SolarExpression("0 0 12 ? * SUNL 2020");
            LocalDateTime dateTime = LocalDateTime.of(2020, 1, 1, 0, 0, 0);
            for (int index = 0, size = 12; index < size; index++) {
                dateTime = expression.getNextDateTime(dateTime);
                Assert.assertEquals(LocalDateTime.of(2020, 1 + index, 1, 12, 0, 0).with(TemporalAdjusters.dayOfWeekInMonth(-1, DayOfWeek.SUNDAY)), dateTime);
            }
        }
    }

    @Test
    public void testBoundary() {
        // 表达式无年份限制
        SolarExpression expression = new SolarExpression("0 0 12 * * ?");
        {
            LocalDateTime dateTime = LocalDateTime.of(1850, 1, 1, 0, 0, 0);
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
        {
            SolarExpression leftExpression = new SolarExpression("0 0 12 1,6,11,16,21,26 * ?");
            SolarExpression rightExpression = new SolarExpression("0 0 12 1/5 * ?");
            LocalDateTime dateTime = LocalDateTime.of(2020, 6, 15, 0, 0, 0);
            Assert.assertEquals(leftExpression.getNextDateTime(dateTime), rightExpression.getNextDateTime(dateTime));
            Assert.assertEquals(leftExpression.getPreviousDateTime(dateTime), rightExpression.getPreviousDateTime(dateTime));
        }
        {
            SolarExpression leftExpression = new SolarExpression("0 0 12 10,11,12,13,14,15,16,17,18,19 * ?");
            SolarExpression rightExpression = new SolarExpression("0 0 12 10-19 * ?");
            LocalDateTime dateTime = LocalDateTime.of(2020, 6, 15, 0, 0, 0);
            Assert.assertEquals(leftExpression.getNextDateTime(dateTime), rightExpression.getNextDateTime(dateTime));
            Assert.assertEquals(leftExpression.getPreviousDateTime(dateTime), rightExpression.getPreviousDateTime(dateTime));
        }
    }

    @Test
    public void test1() {
        SolarExpression solarExpression = new SolarExpression("0 0 0 2,5,8,11 4 ? 2020");
        CronExpression cronExpression = new CronExpression("0 0 0 2,5,8,11 4 ? 2020");

        LocalDateTime startDateTime = LocalDateTime.of(LocalDate.of(2020, 3, 30), LocalTime.of(0, 0));
        LocalDateTime solarNextTime = solarExpression.getNextDateTime(startDateTime);
        LocalDateTime cronNextTime = cronExpression.getNextDateTime(startDateTime);

        System.out.println("solarNextTime:" + solarNextTime);
        System.out.println("cronNextTime:" + cronNextTime);
        Assert.assertEquals(solarNextTime, cronNextTime);
    }

    @Test
    public void test2() {
        SolarExpression solarExpression = new SolarExpression("0 0 0 1 8 ? 2020");
        CronExpression cronExpression = new CronExpression("0 0 0 1 8 ? 2020");

        LocalDateTime startDateTime = LocalDateTime.of(LocalDate.of(1990, 1, 1), LocalTime.of(0, 0));
        LocalDateTime solarNextTime = solarExpression.getNextDateTime(startDateTime);
        LocalDateTime cronNextTime = cronExpression.getNextDateTime(startDateTime);

        System.out.println("solarNextTime:" + solarNextTime);
        System.out.println("cronNextTime:" + cronNextTime);
        Assert.assertEquals(solarNextTime, cronNextTime);
    }

    @Test
    public void test3() {
        SolarExpression solarExpression = new SolarExpression("0 0 0 1,3,5,7,9,11,13,15,17,19,21,23,25,27,29,31 3 ? 2020");
        CronExpression cronExpression = new CronExpression("0 0 0 1,3,5,7,9,11,13,15,17,19,21,23,25,27,29,31 3 ? 2020");

        LocalDateTime startDateTime = LocalDateTime.of(LocalDate.of(2020, 2, 28), LocalTime.of(0, 0));
        LocalDateTime solarNextTime = solarExpression.getNextDateTime(startDateTime);
        LocalDateTime cronNextTime = cronExpression.getNextDateTime(startDateTime);

        System.out.println("solarNextTime:" + solarNextTime);
        System.out.println("cronNextTime:" + cronNextTime);
        Assert.assertEquals(solarNextTime, cronNextTime);
    }

    @Test
    public void test4() {
        SolarExpression solarExpression = new SolarExpression("0 0 10 ? * 1");
        CronExpression cronExpression = new CronExpression("0 0 10 ? * 1");

        LocalDateTime startDateTime = LocalDateTime.of(LocalDate.of(2020, 8, 1), LocalTime.of(20, 20));
        LocalDateTime solarNextTime = solarExpression.getNextDateTime(startDateTime);
        LocalDateTime cronNextTime = cronExpression.getNextDateTime(startDateTime);

        System.out.println("solarNextTime:" + solarNextTime);
        System.out.println("cronNextTime:" + cronNextTime);
        Assert.assertEquals(solarNextTime, cronNextTime);
    }

}
