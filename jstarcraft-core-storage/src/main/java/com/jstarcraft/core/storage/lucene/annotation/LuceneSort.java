package com.jstarcraft.core.storage.lucene.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jstarcraft.core.storage.lucene.converter.SortConverter;

/**
 * Lucene排序
 * 
 * @author Birdy
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface LuceneSort {

    /** 排序转换器 */
    Class<? extends SortConverter> clazz() default SortConverter.class;

}
