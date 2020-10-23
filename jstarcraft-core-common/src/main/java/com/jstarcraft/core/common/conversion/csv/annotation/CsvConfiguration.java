package com.jstarcraft.core.common.conversion.csv.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * CSV(Comma Separated Values)索引
 * 
 * @author Birdy
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Deprecated
public @interface CsvConfiguration {

    String[] value();

}
