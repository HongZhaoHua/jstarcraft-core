package com.jstarcraft.core.common.instant;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class LunisolarExpressionTestCase {

    private List<LocalDateTime> dateTimes = new ArrayList<>();
    {
        dateTimes.add(LocalDateTime.of(2020, 1, 24, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 1, 25, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 2, 22, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 2, 23, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 3, 23, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 3, 24, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 4, 22, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 4, 23, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 5, 22, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 5, 23, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 6, 20, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 6, 21, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 7, 20, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 7, 21, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 8, 18, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 8, 19, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 9, 16, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 9, 17, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 10, 16, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 10, 17, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 11, 14, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 11, 15, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 12, 14, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 12, 15, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2021, 1, 12, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2021, 1, 13, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2021, 2, 11, 12, 0, 0));
    }

    @Test
    public void testGetPreviousDateTime() {
        LunisolarExpression expression = new LunisolarExpression("0 0 12 1,L1 *");

        LocalDateTime dateTime = LocalDateTime.of(2021, 2, 11, 23, 59, 59);
        Assert.assertFalse(expression.isMatchDateTime(dateTime));
        for (int index = dateTimes.size() - 1; index > 0; index--) {
            dateTime = expression.getPreviousDateTime(dateTime);
            Assert.assertEquals(dateTimes.get(index), dateTime);
            Assert.assertTrue(expression.isMatchDateTime(dateTime));
        }
    }

    @Test
    public void testGetNextDateTime() {
        LunisolarExpression expression = new LunisolarExpression("0 0 12 1,L1 *");

        LocalDateTime dateTime = LocalDateTime.of(2020, 1, 24, 0, 0, 0);
        Assert.assertFalse(expression.isMatchDateTime(dateTime));
        for (int index = 0, size = dateTimes.size(); index < size; index++) {
            dateTime = expression.getNextDateTime(dateTime);
            Assert.assertEquals(dateTimes.get(index), dateTime);
            Assert.assertTrue(expression.isMatchDateTime(dateTime));
        }
    }

    @Test
    public void testDate() {
        LunisolarExpression expression = new LunisolarExpression("0 0 12 1,L1 * 2020");
        {
            LocalDateTime dateTime = expression.getPreviousDateTime(dateTimes.get(0));
            Assert.assertNull(dateTime);
        }
        {
            LocalDateTime dateTime = expression.getNextDateTime(dateTimes.get(dateTimes.size() - 1));
            Assert.assertNull(dateTime);
        }

        expression = new LunisolarExpression("0 0 12 15 6 2000/10");
        LocalTime time = LocalTime.of(12, 0, 0);
        LocalDateTime dateTime = LocalDateTime.of(new LunisolarDate(2020, false, 6, 15).getDate(), time);
        Assert.assertEquals(LocalDateTime.of(new LunisolarDate(2010, false, 6, 15).getDate(), time), expression.getPreviousDateTime(dateTime));
        Assert.assertEquals(LocalDateTime.of(new LunisolarDate(2030, false, 6, 15).getDate(), time), expression.getNextDateTime(dateTime));

        expression = new LunisolarExpression("0 0 12 15 6 2020");
        dateTime = LocalDateTime.of(new LunisolarDate(2021, false, 12, 29).getDate(), time);
        Assert.assertEquals(LocalDateTime.of(new LunisolarDate(2020, false, 6, 15).getDate(), time), expression.getPreviousDateTime(dateTime));
        dateTime = LocalDateTime.of(new LunisolarDate(2019, false, 1, 1).getDate(), time);
        Assert.assertEquals(LocalDateTime.of(new LunisolarDate(2020, false, 6, 15).getDate(), time), expression.getNextDateTime(dateTime));

        dateTime = LocalDateTime.of(new LunisolarDate(2020, false, 12, 29).getDate(), time);
        Assert.assertEquals(LocalDateTime.of(new LunisolarDate(2020, false, 6, 15).getDate(), time), expression.getPreviousDateTime(dateTime));
        dateTime = LocalDateTime.of(new LunisolarDate(2020, false, 1, 1).getDate(), time);
        Assert.assertEquals(LocalDateTime.of(new LunisolarDate(2020, false, 6, 15).getDate(), time), expression.getNextDateTime(dateTime));

        dateTime = LocalDateTime.of(new LunisolarDate(2020, false, 6, 29).getDate(), time);
        Assert.assertEquals(LocalDateTime.of(new LunisolarDate(2020, false, 6, 15).getDate(), time), expression.getPreviousDateTime(dateTime));
        dateTime = LocalDateTime.of(new LunisolarDate(2020, false, 6, 1).getDate(), time);
        Assert.assertEquals(LocalDateTime.of(new LunisolarDate(2020, false, 6, 15).getDate(), time), expression.getNextDateTime(dateTime));
    }

    @Test
    public void testDash() {
        LunisolarExpression leftExpression = new LunisolarExpression("0 0 12 15,16 *");
        LunisolarExpression rightExpression = new LunisolarExpression("0 0 12 15-16 *");

        Assert.assertEquals(leftExpression.getBigDays(), rightExpression.getBigDays());
        Assert.assertEquals(leftExpression.getSmallDays(), rightExpression.getSmallDays());
    }

    @Test
    public void testSlash() {
        {
            LunisolarExpression leftExpression = new LunisolarExpression("0 0 12 1,6,11,16,21,26 * ?");
            LunisolarExpression rightExpression = new LunisolarExpression("0 0 12 1/5 * ?");
            LocalDateTime dateTime = LocalDateTime.of(new LunisolarDate(2020, false, 6, 15).getDate(), LocalTime.of(0, 0, 0));
            Assert.assertEquals(leftExpression.getNextDateTime(dateTime), rightExpression.getNextDateTime(dateTime));
            Assert.assertEquals(leftExpression.getPreviousDateTime(dateTime), rightExpression.getPreviousDateTime(dateTime));
        }
        {
            LunisolarExpression leftExpression = new LunisolarExpression("0 0 12 10,11,12,13,14,15,16,17,18,19 * ?");
            LunisolarExpression rightExpression = new LunisolarExpression("0 0 12 10-19 * ?");
            LocalDateTime dateTime = LocalDateTime.of(new LunisolarDate(2020, false, 6, 15).getDate(), LocalTime.of(0, 0, 0));
            Assert.assertEquals(leftExpression.getNextDateTime(dateTime), rightExpression.getNextDateTime(dateTime));
            Assert.assertEquals(leftExpression.getPreviousDateTime(dateTime), rightExpression.getPreviousDateTime(dateTime));
        }
    }

}
