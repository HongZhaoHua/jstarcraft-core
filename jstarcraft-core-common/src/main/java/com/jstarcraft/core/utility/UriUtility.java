package com.jstarcraft.core.utility;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UriUtility {

    // 链接正则表达式
    private static final Pattern urlPattern = Pattern.compile("(?:([A-Za-z]+):)(\\/\\/)([0-9.\\-A-Za-z]+)(?::(\\d+))?(?:\\/([^?#]*))?(?:\\?([^#\\s]*))?(?:#([^\\s]*))?");

    public static List<String> extractUrls(String content) {
        List<String> urls = new ArrayList<>();
        Matcher matcher = urlPattern.matcher(content);
        while (matcher.find()) {
            String url = matcher.group(0);
            urls.add(url);
        }
        return urls;
    }

    public static String extractUrl(String content) {
        List<String> urls = extractUrls(content);
        return urls.isEmpty() ? null : urls.get(0);
    }

    /**
     * Data URI Scheme封装。data URI scheme 允许我们使用内联（inline-code）的方式在网页中包含数据，<br>
     * 目的是将一些小的数据，直接嵌入到网页中，从而不用再从外部文件载入。常用于将图片嵌入网页。
     *
     * <p>
     * Data URI的格式规范：
     * 
     * <pre>
     *     data:[&lt;mime type&gt;][;charset=&lt;charset&gt;][;&lt;encoding&gt;],&lt;encoded data&gt;
     * </pre>
     *
     * @param mime    可选项（null表示无），数据类型（image/png、text/plain等）
     * @param charset 可选项（null表示无），源文本的字符集编码方式
     * @param codec   数据编码方式（US-ASCII，BASE64等）
     * @param data    编码后的数据
     * @return Data URI字符串
     * @since 5.3.6
     */
    public static String getDataUri(String mime, Charset charset, String codec, String data) {
        final StringBuilder builder = new StringBuilder("data:");
        if (StringUtility.isNotBlank(mime)) {
            builder.append(mime);
        }
        if (null != charset) {
            builder.append(";charset=").append(charset.name());
        }
        if (StringUtility.isNotBlank(codec)) {
            builder.append(';').append(codec);
        }
        builder.append(',').append(data);

        return builder.toString();
    }

}
