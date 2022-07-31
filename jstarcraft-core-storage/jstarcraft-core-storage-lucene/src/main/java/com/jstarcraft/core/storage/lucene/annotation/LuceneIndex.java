package com.jstarcraft.core.storage.lucene.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jstarcraft.core.storage.lucene.converter.IndexConverter;

/**
 * Lucene索引
 * 
 * @author Birdy
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface LuceneIndex {

    /** 是否分词 */
    boolean analyze() default false;

    /** 反向词向量 */
    LuceneTerm negative() default @LuceneTerm;

    /** 正向词向量 */
    LuceneTerm positive() default @LuceneTerm;

    /** 索引转换器 */
    Class<? extends IndexConverter> clazz() default IndexConverter.class;

}
