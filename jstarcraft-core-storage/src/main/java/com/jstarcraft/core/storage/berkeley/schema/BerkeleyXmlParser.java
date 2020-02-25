package com.jstarcraft.core.storage.berkeley.schema;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jstarcraft.core.common.conversion.xml.XmlUtility;
import com.jstarcraft.core.storage.exception.StorageConfigurationException;
import com.jstarcraft.core.utility.StringUtility;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.Persistent;

/**
 * BerkeleyXML解析器
 * 
 * @author Birdy
 *
 */
public class BerkeleyXmlParser extends AbstractBeanDefinitionParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(BerkeleyXmlParser.class);

    /** 资源匹配符 */
    private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    /** 资源搜索分析器(负责查找Entity) */
    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    /** 元数据分析器(负责获取注解) */
    private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

    private String[] getResources(String packageName) {
        try {
            // 搜索资源
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(packageName)) + "/" + DEFAULT_RESOURCE_PATTERN;
            Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
            // 提取资源
            Set<String> names = new HashSet<String>();
            String entity = Entity.class.getName();
            String persistent = Persistent.class.getName();
            for (Resource resource : resources) {
                if (!resource.isReadable()) {
                    continue;
                }
                // 判断是否静态资源
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
                if (!annotationMetadata.hasAnnotation(entity) && !annotationMetadata.hasAnnotation(persistent)) {
                    continue;
                }
                ClassMetadata classMetadata = metadataReader.getClassMetadata();
                names.add(classMetadata.getClassName());
            }
            return names.toArray(new String[0]);
        } catch (IOException exception) {
            String message = "无法获取资源";
            LOGGER.error(message, exception);
            throw new StorageConfigurationException(message, exception);
        }
    }

    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder factory = BeanDefinitionBuilder.genericBeanDefinition(BerkeleyAccessorFactory.class);
        // 设置目录
        String directory = element.getAttribute(AttributeDefinition.DIRECTORY.getName());
        factory.addPropertyValue(AttributeDefinition.DIRECTORY.getName(), directory);

        // 设置配置
        String properties = element.getAttribute(AttributeDefinition.PROPERTIES.getName());
        factory.addPropertyValue(AttributeDefinition.PROPERTIES.getName(), properties);

        // 设置是否只读
        String readOnly = element.getAttribute(AttributeDefinition.READ_ONLY.getName());
        factory.addPropertyValue(AttributeDefinition.READ_ONLY.getName(), Boolean.valueOf(readOnly));

        // 设置是否写延迟
        String writeDelay = element.getAttribute(AttributeDefinition.WRITE_DELAY.getName());
        factory.addPropertyValue(AttributeDefinition.WRITE_DELAY.getName(), Boolean.valueOf(writeDelay));

        // 设置是否临时
        String temporary = element.getAttribute(AttributeDefinition.TEMPORARY.getName());
        factory.addPropertyValue(AttributeDefinition.TEMPORARY.getName(), Boolean.valueOf(temporary));

        // 设置版本缓存时间
        String versionCache = element.getAttribute(AttributeDefinition.VERSION_KEEP.getName());
        factory.addPropertyValue(AttributeDefinition.VERSION_KEEP.getName(), Long.valueOf(versionCache));

        // 设置接口定义集合
        NodeList nodes = XmlUtility.getChildElementByTagName(element, ElementDefinition.SCAN.getName()).getChildNodes();
        HashSet<Class<?>> classes = new HashSet<>();
        for (int index = 0; index < nodes.getLength(); index++) {
            Node node = nodes.item(index);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            String name = node.getLocalName();
            if (name.equals(ElementDefinition.PACKAGE.getName())) {
                // 自动包扫描处理
                String packageName = ((Element) node).getAttribute(AttributeDefinition.NAME.getName());
                String[] classNames = getResources(packageName);
                for (String className : classNames) {
                    Class<?> clazz = null;
                    try {
                        clazz = (Class<?>) Class.forName(className);
                        if (clazz.getAnnotation(Entity.class) != null || clazz.getAnnotation(Persistent.class) != null) {
                            classes.add(clazz);
                        }
                    } catch (ClassNotFoundException exception) {
                        String message = StringUtility.format("无法获取类型[{}]", className);
                        LOGGER.error(message);
                        throw new StorageConfigurationException(message, exception);
                    }
                }
            }

            if (name.equals(ElementDefinition.CLASS.getName())) {
                // 自动类加载处理
                String className = ((Element) node).getAttribute(AttributeDefinition.NAME.getName());
                Class<?> clazz = null;
                try {
                    clazz = (Class<?>) Class.forName(className);
                    if (clazz.getAnnotation(Entity.class) != null || clazz.getAnnotation(Persistent.class) != null) {
                        classes.add(clazz);
                    }
                } catch (ClassNotFoundException exception) {
                    String message = StringUtility.format("无法获取类型[{}]", className);
                    LOGGER.error(message);
                    throw new StorageConfigurationException(message, exception);
                }
            }
        }
        factory.addPropertyValue(BerkeleyAccessorFactory.CLASSES, classes);

        return factory.getBeanDefinition();
    }

    /**
     * 数据库Schema定义的元素
     * 
     * @author Birdy
     */
    enum ElementDefinition {

        /**
         * 根配置元素(属性directory,id,properties,readOnly,temporary,versionKeep,writeDelay)
         */
        CONFIGURATION("configuration"),

        /** 扫描定义元素 */
        SCAN("scan"),
        /** 包定义元素(属性name) */
        PACKAGE("package"),
        /** 类定义元素(属性name) */
        CLASS("class");

        private String name;

        private ElementDefinition(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

    /**
     * 数据库Schema定义的属性
     * 
     * @author Birdy
     */
    enum AttributeDefinition {

        /** 目录 */
        DIRECTORY("directory"),

        /** 标识 */
        ID("id"),

        /** 名称 */
        NAME("name"),

        /** 配置 */
        PROPERTIES("properties"),

        /** 只读 */
        READ_ONLY("readOnly"),

        /** 临时 */
        TEMPORARY("temporary"),

        /** 版本缓存时间 */
        VERSION_KEEP("versionKeep"),

        /** 延迟写 */
        WRITE_DELAY("writeDelay");

        private String name;

        private AttributeDefinition(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

}
