package com.jstarcraft.core.common.instant;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class TermExpressionTestCase {

    private List<LocalDateTime> dateTimes = new ArrayList<>();
    {
        // 2020年与2021的立春,立夏,立秋,立冬
        dateTimes.add(LocalDateTime.of(TermType.LiChun.getDate(2020), LocalTime.of(0, 0, 0)));
        dateTimes.add(LocalDateTime.of(TermType.LiXia.getDate(2020), LocalTime.of(0, 0, 0)));
        dateTimes.add(LocalDateTime.of(TermType.LiQiu.getDate(2020), LocalTime.of(0, 0, 0)));
        dateTimes.add(LocalDateTime.of(TermType.LiDong.getDate(2020), LocalTime.of(0, 0, 0)));
        dateTimes.add(LocalDateTime.of(TermType.LiChun.getDate(2021), LocalTime.of(0, 0, 0)));
        dateTimes.add(LocalDateTime.of(TermType.LiXia.getDate(2021), LocalTime.of(0, 0, 0)));
        dateTimes.add(LocalDateTime.of(TermType.LiQiu.getDate(2021), LocalTime.of(0, 0, 0)));
        dateTimes.add(LocalDateTime.of(TermType.LiDong.getDate(2021), LocalTime.of(0, 0, 0)));
    }

    @Test
    public void testGetPreviousDateTime() {
        TermExpression expression = new TermExpression("0 0 0 LiChun,LiXia,LiQiu,LiDong *");

        LocalDateTime dateTime = LocalDateTime.of(2022, 1, 1, 0, 0, 0);
        Assert.assertFalse(expression.isMatchDateTime(dateTime));
        for (int index = dateTimes.size() - 1; index > 0; index--) {
            dateTime = expression.getPreviousDateTime(dateTime);
            Assert.assertEquals(dateTimes.get(index), dateTime);
            Assert.assertTrue(expression.isMatchDateTime(dateTime));
        }
    }

    @Test
    public void testGetNextDateTime() {
        TermExpression expression = new TermExpression("0 0 0 LiChun,LiXia,LiQiu,LiDong *");

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
        TermExpression expression = new TermExpression("0 0 0 LiChun,LiXia,LiQiu,LiDong 2020");
        {
            LocalDateTime dateTime = expression.getPreviousDateTime(dateTimes.get(0));
            Assert.assertNull(dateTime);
        }
        {
            LocalDateTime dateTime = expression.getNextDateTime(dateTimes.get(dateTimes.size() - 1));
            Assert.assertNull(dateTime);
        }

        expression = new TermExpression("0 0 0 XiaoHan *");
        {
            // 测试跨年
            LocalDateTime dateTime = LocalDateTime.of(2020, 12, 31, 23, 59, 59);
            dateTime = expression.getNextDateTime(dateTime);
            Assert.assertEquals(LocalDateTime.of(2021, 1, 5, 0, 0, 0), dateTime);
        }

        {
            // 测试不跨年
            LocalDateTime dateTime = LocalDateTime.of(2021, 1, 4, 23, 59, 59);
            dateTime = expression.getNextDateTime(dateTime);
            Assert.assertEquals(LocalDateTime.of(2021, 1, 5, 0, 0, 0), dateTime);
        }

        expression = new TermExpression("59 59 23 DongZhi *");
        {
            // 测试跨年
            LocalDateTime dateTime = LocalDateTime.of(2022, 1, 1, 0, 0, 0);
            dateTime = expression.getPreviousDateTime(dateTime);
            Assert.assertEquals(LocalDateTime.of(2021, 12, 21, 23, 59, 59), dateTime);
        }

        {
            // 测试不跨年
            LocalDateTime dateTime = LocalDateTime.of(2021, 12, 22, 0, 0, 0);
            dateTime = expression.getPreviousDateTime(dateTime);
            Assert.assertEquals(LocalDateTime.of(2021, 12, 21, 23, 59, 59), dateTime);
        }
    }

}
