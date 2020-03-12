package com.jstarcraft.core.cache.schema;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.ManagedSet;
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

import com.jstarcraft.core.cache.annotation.CacheConfiguration;
import com.jstarcraft.core.cache.exception.CacheConfigurationException;
import com.jstarcraft.core.common.conversion.xml.XmlUtility;
import com.jstarcraft.core.common.identification.IdentityObject;
import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 缓存XML解析器
 * 
 * @author Birdy
 */
public class CacheXmlParser extends AbstractBeanDefinitionParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheXmlParser.class);
    /** 资源匹配符 */
    private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    /** 资源搜索分析器(负责查找CacheObject) */
    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    /** 元数据分析器(负责获取注解) */
    private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

    private void assembleProcessor(ParserContext context) {
        BeanDefinitionRegistry registry = context.getRegistry();
        String name = StringUtility.uncapitalize(CacheAccessorProcessor.class.getSimpleName());
        BeanDefinitionBuilder factory = BeanDefinitionBuilder.genericBeanDefinition(CacheAccessorProcessor.class);
        registry.registerBeanDefinition(name, factory.getBeanDefinition());
    }

    /** 获取瞬时策略集合 */
    private static ManagedSet<Object> getTransienceStrategies(Element configurationElement, ParserContext context) {
        // 设置每个执行策略配置
        ManagedSet<Object> strategies = new ManagedSet<>();
        List<Element> elements = XmlUtility.getChildElementsByTagName(configurationElement, ElementDefinition.TRANSIENCE_STRATEGY.getName());
        for (Element element : elements) {
            String reference = element.getAttribute(AttributeDefinition.REFERENCE.getName());
            strategies.add(new RuntimeBeanReference(reference));
        }
        return strategies;
    }

    /** 获取持久策略集合 */
    private static ManagedSet<Object> getPersistenceStrategies(Element configurationElement, ParserContext context) {
        // 设置每个执行策略配置
        ManagedSet<Object> strategies = new ManagedSet<>();
        List<Element> elements = XmlUtility.getChildElementsByTagName(configurationElement, ElementDefinition.PERSISTENCE_STRATEGY.getName());
        for (Element element : elements) {
            String reference = element.getAttribute(AttributeDefinition.REFERENCE.getName());
            strategies.add(new RuntimeBeanReference(reference));
        }
        return strategies;
    }

    private String[] getResources(String packageName) {
        try {
            // 搜索资源
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(packageName)) + "/" + DEFAULT_RESOURCE_PATTERN;
            Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
            // 提取资源
            Set<String> names = new HashSet<String>();
            String name = CacheConfiguration.class.getName();
            for (Resource resource : resources) {
                if (!resource.isReadable()) {
                    continue;
                }
                // 判断是否静态资源
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
                if (!annotationMetadata.hasAnnotation(name)) {
                    continue;
                }
                ClassMetadata classMetadata = metadataReader.getClassMetadata();
                names.add(classMetadata.getClassName());
            }
            return names.toArray(new String[0]);
        } catch (IOException exception) {
            String message = "无法获取资源";
            LOGGER.error(message, exception);
            throw new CacheConfigurationException(message, exception);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext context) {
        // 装配缓存处理器
        assembleProcessor(context);

        // 缓存服务工厂
        BeanDefinitionBuilder factory = BeanDefinitionBuilder.genericBeanDefinition(CacheServiceFactory.class);

        // 设置访问器
        Element accessorElement = XmlUtility.getUniqueElement(element, ElementDefinition.ACCESSOR.getName());
        if (accessorElement == null) {
            throw new CacheConfigurationException("访问器配置缺失");
        }
        String accessorBeanName = accessorElement.getAttribute(AttributeDefinition.REFERENCE.getName());
        factory.addPropertyReference(ElementDefinition.ACCESSOR.getName(), accessorBeanName);

        Type mapType = TypeUtility.parameterize(HashMap.class, String.class, String.class);

        // 设置内存策略
        ManagedSet<Object> transienceStrategies = getTransienceStrategies(element, context);
        factory.addPropertyValue(CacheServiceFactory.TRANSIENCE_STRATEGIES_NAME, transienceStrategies);

        // 设置持久策略
        ManagedSet<Object> persistenceStrategies = getPersistenceStrategies(element, context);
        factory.addPropertyValue(CacheServiceFactory.PERSISTENCE_STRATEGIES_NAME, persistenceStrategies);

        // 设置实体集合
        NodeList nodes = XmlUtility.getChildElementByTagName(element, ElementDefinition.SCAN.getName()).getChildNodes();
        Set<Class<? extends IdentityObject>> classes = new HashSet<Class<? extends IdentityObject>>();

        for (int index = 0; index < nodes.getLength(); index++) {
            Node node = nodes.item(index);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            String name = node.getLocalName();
            if (name.equals(ElementDefinition.PACKAGE.getName())) {
                // 自动包扫描处理
                String packageName = ((Element) node).getAttribute(AttributeDefinition.NAME.getName());
                String[] names = getResources(packageName);
                for (String resource : names) {
                    Class<? extends IdentityObject> clazz = null;
                    try {
                        clazz = (Class<? extends IdentityObject>) Class.forName(resource);
                    } catch (ClassNotFoundException exception) {
                        String message = StringUtility.format("无法获取类型[{}]", resource);
                        LOGGER.error(message);
                        throw new CacheConfigurationException(message, exception);
                    }
                    classes.add(clazz);
                }
            }

            if (name.equals(ElementDefinition.CLASS.getName())) {
                // 自动类加载处理
                String className = ((Element) node).getAttribute(AttributeDefinition.NAME.getName());
                Class<? extends IdentityObject> clazz = null;
                try {
                    clazz = (Class<? extends IdentityObject>) Class.forName(className);
                } catch (ClassNotFoundException exception) {
                    String message = StringUtility.format("无法获取类型[{}]", className);
                    LOGGER.error(message);
                    throw new CacheConfigurationException(message, exception);
                }
                classes.add(clazz);
            }
        }
        factory.addPropertyValue(CacheServiceFactory.CACHE_CLASSES_NAME, classes);

        return factory.getBeanDefinition();
    }

    /**
     * 缓存Schema定义的元素
     * 
     * @author Birdy
     */
    enum ElementDefinition {

        /** 根配置元素(属性id) */
        CONFIGURATION("configuration"),

        /** 扫描定义元素 */
        SCAN("scan"),
        /** 包定义元素(属性name) */
        PACKAGE("package"),
        /** 类定义元素(属性name) */
        CLASS("class"),

        /** 访问器定义元素(属性accessor) */
        ACCESSOR("accessor"),

        /** 内存配置定义元素(属性name,type,parameters) */
        TRANSIENCE_STRATEGY("transienceStrategy"),
        /** 持久配置定义元素(属性name,type,parameters) */
        PERSISTENCE_STRATEGY("persistenceStrategy");

        private String name;

        private ElementDefinition(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

    /**
     * 缓存Schema定义的属性
     * 
     * @author Birdy
     */
    enum AttributeDefinition {

        /** 标识 */
        ID("id"),

        /** 引用 */
        REFERENCE("reference"),

        /** 名称 */
        NAME("name");

        private String name;

        private AttributeDefinition(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

}
