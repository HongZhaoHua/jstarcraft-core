package com.jstarcraft.core.storage.lucene.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Lucene词向量
 * 
 * @author Birdy
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface LuceneTerm {

    /** 词频 */
    boolean frequency() default false;

    /** 位置 */
    boolean position() default false;

    /** 偏移 */
    boolean offset() default false;

}
