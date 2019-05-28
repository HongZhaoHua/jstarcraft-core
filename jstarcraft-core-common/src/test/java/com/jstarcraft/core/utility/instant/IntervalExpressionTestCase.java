package com.jstarcraft.core.utility.instant;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.common.instant.IntervalExpression;

public class IntervalExpressionTestCase {

	private List<LocalDateTime> dateTimes = new ArrayList<>();
	{
		dateTimes.add(LocalDateTime.of(2020, 1, 1, 0, 0, 15));
		dateTimes.add(LocalDateTime.of(2020, 1, 1, 0, 0, 30));
		dateTimes.add(LocalDateTime.of(2020, 1, 1, 0, 0, 45));
		dateTimes.add(LocalDateTime.of(2020, 1, 1, 0, 1, 0));
		dateTimes.add(LocalDateTime.of(2020, 1, 1, 0, 1, 15));
		dateTimes.add(LocalDateTime.of(2020, 1, 1, 0, 1, 30));
		dateTimes.add(LocalDateTime.of(2020, 1, 1, 0, 1, 45));
	}

	@Test
	public void testGetPreviousDateTime() {
		{
			IntervalExpression expression = new IntervalExpression("0 1 0 1 1 2020 15");

			LocalDateTime dateTime = LocalDateTime.of(2020, 1, 1, 0, 2, 0);
			for (int index = dateTimes.size() - 1; index > 0; index--) {
				dateTime = expression.getPreviousDateTime(dateTime);
				Assert.assertEquals(dateTimes.get(index), dateTime);
			}
			
			dateTime = LocalDateTime.of(2020, 1, 1, 0, 1, 59);
			for (int index = dateTimes.size() - 1; index > 0; index--) {
				dateTime = expression.getPreviousDateTime(dateTime);
				Assert.assertEquals(dateTimes.get(index), dateTime);
			}
			
			dateTime = LocalDateTime.of(2020, 1, 1, 0, 1, 46);
			for (int index = dateTimes.size() - 1; index > 0; index--) {
				dateTime = expression.getPreviousDateTime(dateTime);
				Assert.assertEquals(dateTimes.get(index), dateTime);
			}
		}
	}

	@Test
	public void testGetNextDateTime() {
		{
			IntervalExpression expression = new IntervalExpression("0 1 0 1 1 2020 15");

			LocalDateTime dateTime = LocalDateTime.of(2020, 1, 1, 0, 0, 0);
			for (int index = 0, size = dateTimes.size(); index < size; index++) {
				dateTime = expression.getNextDateTime(dateTime);
				Assert.assertEquals(dateTimes.get(index), dateTime);
			}
			
			dateTime = LocalDateTime.of(2020, 1, 1, 0, 0, 1);
			for (int index = 0, size = dateTimes.size(); index < size; index++) {
				dateTime = expression.getNextDateTime(dateTime);
				Assert.assertEquals(dateTimes.get(index), dateTime);
			}
			
			dateTime = LocalDateTime.of(2020, 1, 1, 0, 0, 14);
			for (int index = 0, size = dateTimes.size(); index < size; index++) {
				dateTime = expression.getNextDateTime(dateTime);
				Assert.assertEquals(dateTimes.get(index), dateTime);
			}
		}
	}

}
