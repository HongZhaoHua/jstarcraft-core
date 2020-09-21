package com.jstarcraft.core.common.instant;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class LunarExpressionTestCase {

    private List<LocalDateTime> dateTimes = new ArrayList<>();
    {
        dateTimes.add(LocalDateTime.of(2010, 12, 07, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2011, 1, 4, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2011, 1, 5, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2011, 2, 3, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2011, 2, 4, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2011, 3, 5, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2011, 3, 6, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2011, 4, 4, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2011, 4, 5, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2011, 5, 3, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2011, 5, 4, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2011, 6, 2, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2011, 6, 3, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2011, 7, 1, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2011, 7, 2, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2011, 7, 31, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2011, 8, 1, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2011, 8, 29, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2011, 8, 30, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2011, 9, 28, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2011, 9, 29, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2011, 10, 27, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2011, 10, 28, 12, 0, 0));
        dateTimes.add(LocalDateTime.of(2011, 11, 25, 12, 0, 0));
    }

    @Test
    public void testGetPreviousDateTime() {
        LunarExpression expression = new LunarExpression("0 0 12 1,L1 *");

        LocalDateTime dateTime = LocalDateTime.of(2011, 11, 25, 23, 59, 59);
        Assert.assertFalse(expression.isMatchDateTime(dateTime));
        for (int index = dateTimes.size() - 1; index > 0; index--) {
            dateTime = expression.getPreviousDateTime(dateTime);
            Assert.assertEquals(dateTimes.get(index), dateTime);
            Assert.assertTrue(expression.isMatchDateTime(dateTime));
        }
    }

    @Test
    public void testGetNextDateTime() {
        LunarExpression expression = new LunarExpression("0 0 12 1,L1 *");

        LocalDateTime dateTime = LocalDateTime.of(2010, 12, 7, 0, 0, 0);
        Assert.assertFalse(expression.isMatchDateTime(dateTime));
        for (int index = 0, size = dateTimes.size(); index < size; index++) {
            dateTime = expression.getNextDateTime(dateTime);
            Assert.assertEquals(dateTimes.get(index), dateTime);
            Assert.assertTrue(expression.isMatchDateTime(dateTime));
        }
    }

    @Test
    public void testDate() {
        LunarExpression expression = new LunarExpression("0 0 12 1,L1 * 1432");
        {
            LocalDateTime dateTime = expression.getPreviousDateTime(dateTimes.get(0));
            Assert.assertNull(dateTime);
        }
        {
            LocalDateTime dateTime = expression.getNextDateTime(dateTimes.get(dateTimes.size() - 1));
            Assert.assertNull(dateTime);
        }

        expression = new LunarExpression("0 0 12 15 6 1421/10");
        LocalTime time = LocalTime.of(12, 0, 0);
        LocalDateTime dateTime = LocalDateTime.of(new LunarDate(1441, 6, 15).getDate(), time);
        Assert.assertEquals(LocalDateTime.of(new LunarDate(1431, 6, 15).getDate(), time), expression.getPreviousDateTime(dateTime));
        Assert.assertEquals(LocalDateTime.of(new LunarDate(1451, 6, 15).getDate(), time), expression.getNextDateTime(dateTime));

        expression = new LunarExpression("0 0 12 15 6 1441");
        dateTime = LocalDateTime.of(new LunarDate(1442, 12, 29).getDate(), time);
        Assert.assertEquals(LocalDateTime.of(new LunarDate(1441, 6, 15).getDate(), time), expression.getPreviousDateTime(dateTime));
        dateTime = LocalDateTime.of(new LunarDate(1440, 1, 1).getDate(), time);
        Assert.assertEquals(LocalDateTime.of(new LunarDate(1441, 6, 15).getDate(), time), expression.getNextDateTime(dateTime));

        dateTime = LocalDateTime.of(new LunarDate(1441, 12, 29).getDate(), time);
        Assert.assertEquals(LocalDateTime.of(new LunarDate(1441, 6, 15).getDate(), time), expression.getPreviousDateTime(dateTime));
        dateTime = LocalDateTime.of(new LunarDate(1441, 1, 1).getDate(), time);
        Assert.assertEquals(LocalDateTime.of(new LunarDate(1441, 6, 15).getDate(), time), expression.getNextDateTime(dateTime));

        dateTime = LocalDateTime.of(new LunarDate(1441, 6, 29).getDate(), time);
        Assert.assertEquals(LocalDateTime.of(new LunarDate(1441, 6, 15).getDate(), time), expression.getPreviousDateTime(dateTime));
        dateTime = LocalDateTime.of(new LunarDate(1441, 6, 1).getDate(), time);
        Assert.assertEquals(LocalDateTime.of(new LunarDate(1441, 6, 15).getDate(), time), expression.getNextDateTime(dateTime));
    }

    @Test
    public void testDash() {
        LunarExpression leftExpression = new LunarExpression("0 0 12 15,16 *");
        LunarExpression rightExpression = new LunarExpression("0 0 12 15-16 *");

        Assert.assertEquals(leftExpression.getBigDays(), rightExpression.getBigDays());
        Assert.assertEquals(leftExpression.getSmallDays(), rightExpression.getSmallDays());
    }

    @Test
    public void testSlash() {
        {
            LunarExpression leftExpression = new LunarExpression("0 0 12 1,6,11,16,21,26 * ?");
            LunarExpression rightExpression = new LunarExpression("0 0 12 1/5 * ?");
            LocalDateTime dateTime = LocalDateTime.of(new LunarDate(1441, 6, 15).getDate(), LocalTime.of(0, 0, 0));
            Assert.assertEquals(leftExpression.getNextDateTime(dateTime), rightExpression.getNextDateTime(dateTime));
            Assert.assertEquals(leftExpression.getPreviousDateTime(dateTime), rightExpression.getPreviousDateTime(dateTime));
        }
        {
            LunarExpression leftExpression = new LunarExpression("0 0 12 10,11,12,13,14,15,16,17,18,19 * ?");
            LunarExpression rightExpression = new LunarExpression("0 0 12 10-19 * ?");
            LocalDateTime dateTime = LocalDateTime.of(new LunarDate(1441, 6, 15).getDate(), LocalTime.of(0, 0, 0));
            Assert.assertEquals(leftExpression.getNextDateTime(dateTime), rightExpression.getNextDateTime(dateTime));
            Assert.assertEquals(leftExpression.getPreviousDateTime(dateTime), rightExpression.getPreviousDateTime(dateTime));
        }
    }

}
