package com.jstarcraft.core.io;

import java.time.Instant;
import java.util.Map;

/**
 * 仓库元信息
 * 
 * @author Birdy
 *
 */
public class StreamMetadata {

    /** 修改 */
    private Instant updatedAt;

    private Long contentLength;

    private String contentType;

    private String contentEncoding;

    private String contentMd5;

    private String entityTag;

    private String storgeClass;

    private String objectType;

    /** 键值对 */
    private Map<String, String> keyValues;

}
