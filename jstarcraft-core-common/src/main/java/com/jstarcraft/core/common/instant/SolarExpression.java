package com.jstarcraft.core.common.instant;

import java.time.ZonedDateTime;

import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

/**
 * 阳历表达式
 * 
 * @author Birdy
 *
 */
public class SolarExpression extends DateTimeExpression {

	private static final CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);

	private static final CronParser parser = new CronParser(cronDefinition);

	private final ExecutionTime execution;

	public SolarExpression(String expression) {
		super(expression);
		this.execution = ExecutionTime.forCron(parser.parse(expression));
	}

	@Override
	public ZonedDateTime getPreviousDateTime(ZonedDateTime dateTime) {
		dateTime = execution.lastExecution(dateTime).orElse(null);
		return dateTime;
	}

	@Override
	public ZonedDateTime getNextDateTime(ZonedDateTime dateTime) {
		dateTime = execution.nextExecution(dateTime).orElse(null);
		return dateTime;
	}

}
