package com.jstarcraft.core.common.instant;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.common.instant.LunarExpression;

public class LunarExpressionTestCase {

    private List<LocalDateTime> dateTimes = new ArrayList<>();
    {
        dateTimes.add(LocalDateTime.of(2020, 1, 24, 0, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 1, 25, 0, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 2, 23, 0, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 3, 23, 0, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 3, 24, 0, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 4, 22, 0, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 4, 23, 0, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 5, 22, 0, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 5, 23, 0, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 6, 21, 0, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 7, 20, 0, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 7, 21, 0, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 8, 19, 0, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 9, 17, 0, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 10, 16, 0, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 10, 17, 0, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 11, 15, 0, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 12, 14, 0, 0, 0));
        dateTimes.add(LocalDateTime.of(2020, 12, 15, 0, 0, 0));
        dateTimes.add(LocalDateTime.of(2021, 1, 13, 0, 0, 0));
        dateTimes.add(LocalDateTime.of(2021, 2, 11, 0, 0, 0));
    }

    @Test
    public void testGetPreviousDateTime() {
        LunarExpression expression = new LunarExpression("0 0 0 1,30 *");

        LocalDateTime dateTime = LocalDateTime.of(2021, 2, 12, 0, 0, 0);
        for (int index = dateTimes.size() - 1; index > 0; index--) {
            dateTime = expression.getPreviousDateTime(dateTime);
            Assert.assertEquals(dateTimes.get(index), dateTime);
        }
    }

    @Test
    public void testGetNextDateTime() {
        LunarExpression expression = new LunarExpression("0 0 0 1,30 *");

        LocalDateTime dateTime = LocalDateTime.of(2020, 1, 23, 0, 0, 0);
        for (int index = 0, size = dateTimes.size(); index < size; index++) {
            dateTime = expression.getNextDateTime(dateTime);
            Assert.assertEquals(dateTimes.get(index), dateTime);
        }
    }

    @Test
    public void testYear() {
        LunarExpression expression = new LunarExpression("0 0 0 1,30 * 2020");
        {
            LocalDateTime dateTime = expression.getPreviousDateTime(dateTimes.get(0));
            Assert.assertNull(dateTime);
        }
        {
            LocalDateTime dateTime = expression.getNextDateTime(dateTimes.get(dateTimes.size() - 1));
            Assert.assertNull(dateTime);
        }
    }

}
