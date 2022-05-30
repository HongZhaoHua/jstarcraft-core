package com.jstarcraft.core.storage.lucene.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jstarcraft.core.storage.lucene.converter.StoreConverter;

/**
 * Lucene存储
 * 
 * @author Birdy
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface LuceneStore {

    /** 存储转换器 */
    Class<? extends StoreConverter> clazz() default StoreConverter.class;

}
