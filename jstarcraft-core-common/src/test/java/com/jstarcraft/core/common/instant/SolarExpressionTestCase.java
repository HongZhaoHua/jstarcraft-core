package com.jstarcraft.core.common.instant;

import java.time.LocalDateTime;
import java.time.YearMonth;
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

        LocalDateTime dateTime = LocalDateTime.of(2021, 1, 1, 0, 0, 0);
        for (int index = dateTimes.size() - 1; index > 0; index--) {
            dateTime = expression.getPreviousDateTime(dateTime);
            Assert.assertEquals(dateTimes.get(index), dateTime);
        }
    }

    @Test
    public void testGetNextDateTime() {
        SolarExpression expression = new SolarExpression("0 0 12 1,30 * ?");

        LocalDateTime dateTime = LocalDateTime.of(2020, 1, 1, 0, 0, 0);
        for (int index = 0, size = dateTimes.size(); index < size; index++) {
            dateTime = expression.getNextDateTime(dateTime);
            Assert.assertEquals(dateTimes.get(index), dateTime);
        }
    }

    @Test
    public void testYear() {
        SolarExpression expression = new SolarExpression("0 0 12 1,30 * ? 2020");
        {
            LocalDateTime dateTime = expression.getPreviousDateTime(dateTimes.get(0));
            Assert.assertNull(dateTime);
        }
        {
            LocalDateTime dateTime = expression.getNextDateTime(dateTimes.get(dateTimes.size() - 1));
            Assert.assertNull(dateTime);
        }
    }

    @Test
    public void testLast() {
        // 每个月倒数第2天
        SolarExpression expression = new SolarExpression("0 0 12 L-1 * ? 2020");
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

}
