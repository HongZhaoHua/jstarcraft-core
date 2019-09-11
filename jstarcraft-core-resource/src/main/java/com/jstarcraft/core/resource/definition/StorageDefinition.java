package com.jstarcraft.core.resource.definition;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.jstarcraft.core.resource.annotation.ResourceConfiguration;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 仓储定义
 * 
 * @author Birdy
 */
public class StorageDefinition {

    public static final String FILE_PATH = File.separator;

    public static final String FILE_DOT = ".";

    /** 仓储类型 */
    private final Class<?> clazz;
    /** 仓储路径 */
    private final String path;
    /** 仓储格式信息 */
    private final FormatDefinition format;
    /** 仓储引用信息 */
    private final Collection<ReferenceDefinition> references;

    /**
     * 
     * @param clazz
     * @param format
     * @param factory
     */
    public StorageDefinition(Class<?> clazz, FormatDefinition format, Collection<ReferenceDefinition> references, String path) {
        this.clazz = clazz;
        this.format = format;
        this.references = references;
        ResourceConfiguration annotation = clazz.getAnnotation(ResourceConfiguration.class);
        StringBuilder buffer = new StringBuilder();
        buffer.append(path);
        buffer.append(FILE_PATH);
        if (StringUtility.isNotBlank(annotation.path())) {
            path = annotation.path();
            int begin = 0;
            int end = path.length();
            if (StringUtility.startsWith(path, FILE_PATH)) {
                begin++;
            }
            if (StringUtility.endsWith(path, FILE_PATH)) {
                end--;
            }
            buffer.append(path.substring(begin, end)).append(FILE_PATH);
        }
        buffer.append(clazz.getSimpleName()).append(FILE_DOT).append(format.getSuffix());
        this.path = buffer.toString();
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public FormatDefinition getFormat() {
        return format;
    }

    public Collection<ReferenceDefinition> getReferences() {
        return Collections.unmodifiableCollection(references);
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}
