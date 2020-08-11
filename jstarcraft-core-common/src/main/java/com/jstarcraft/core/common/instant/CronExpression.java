package com.jstarcraft.core.common.instant;

import java.time.ZonedDateTime;

import com.cronutils.model.definition.CronConstraintsFactory;
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
public class CronExpression extends DateTimeExpression {

    private static final CronDefinition cronDefinition;

    private static final CronParser parser;

    static {
        cronDefinition = CronDefinitionBuilder.defineCron()

                .withSeconds().withValidRange(0, 59).and()

                .withMinutes().withValidRange(0, 59).and()

                .withHours().withValidRange(0, 23).and()

                .withDayOfMonth().withValidRange(1, 31).supportsL().supportsW().supportsLW().supportsQuestionMark().and()

                .withMonth().withValidRange(1, 12).and()

                .withDayOfWeek().withValidRange(1, 7).withMondayDoWValue(2).supportsHash().supportsL().supportsQuestionMark().and()

                // 无年份限制
                .withYear().optional().and()

                .withCronValidation(CronConstraintsFactory.ensureEitherDayOfWeekOrDayOfMonth())

                .instance();

        parser = new CronParser(cronDefinition);
    }

    private final ExecutionTime execution;

    public CronExpression(String expression) {
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

    @Override
    public boolean isMatchDateTime(ZonedDateTime dateTime) {
        return execution.isMatch(dateTime);
    }

}
