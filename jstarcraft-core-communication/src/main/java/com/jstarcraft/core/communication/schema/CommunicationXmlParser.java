package com.jstarcraft.core.communication.schema;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedMap;
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
import com.jstarcraft.core.communication.annotation.CommunicationModule;
import com.jstarcraft.core.communication.annotation.CommunicationModule.ModuleSide;
import com.jstarcraft.core.communication.command.CommandDefinition;
import com.jstarcraft.core.communication.exception.CommunicationConfigurationException;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 通讯XML解析器
 * 
 * @author Birdy
 */
public class CommunicationXmlParser extends AbstractBeanDefinitionParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommunicationXmlParser.class);
    /** 资源匹配符 */
    private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    /** 资源搜索分析器(负责查找CommunicationModule) */
    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    /** 元数据分析器(负责获取注解) */
    private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

    /** 获取指令策略集合 */
    private static ManagedMap<String, Object> getStrategies(Element configurationElement, ParserContext context) {
        // 设置每个执行策略配置
        ManagedMap<String, Object> strategies = new ManagedMap<>();
        String strategyName = configurationElement.getAttribute(AttributeDefinition.REFERENCE.getName());
        strategies.put(StringUtility.EMPTY, new RuntimeBeanReference(strategyName));
        List<Element> elements = XmlUtility.getChildElementsByTagName(configurationElement, ElementDefinition.STRATEGY.getName());
        for (Element element : elements) {
            String name = element.getAttribute(AttributeDefinition.NAME.getName());
            String reference = element.getAttribute(AttributeDefinition.REFERENCE.getName());
            strategies.put(name, new RuntimeBeanReference(reference));
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
            String name = CommunicationModule.class.getName();
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
            throw new CommunicationConfigurationException(message, exception);
        }
    }

    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext context) {
        // 通信调度器工厂
        BeanDefinitionBuilder factory = BeanDefinitionBuilder.genericBeanDefinition(CommunicationDispatcherFactory.class);

        // 设置通讯端
        String sideName = element.getAttribute(AttributeDefinition.SIDE.getName());
        factory.addPropertyValue(AttributeDefinition.SIDE.getName(), ModuleSide.valueOf(sideName));

        // 设置接口定义集合
        NodeList nodes = XmlUtility.getChildElementByTagName(element, ElementDefinition.SCAN.getName()).getChildNodes();
        List<CommandDefinition> definitions = new LinkedList<>();
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
                        CommunicationModule socketModule = clazz.getAnnotation(CommunicationModule.class);
                        if (clazz.isInterface() && socketModule != null) {
                            for (Method method : clazz.getMethods()) {
                                CommandDefinition definition = CommandDefinition.instanceOf(method);
                                definitions.add(definition);
                            }
                        }
                    } catch (ClassNotFoundException exception) {
                        String message = StringUtility.format("无法获取类型[{}]", className);
                        LOGGER.error(message);
                        throw new CommunicationConfigurationException(message, exception);
                    }
                }
            }

            if (name.equals(ElementDefinition.CLASS.getName())) {
                // 自动类加载处理
                String className = ((Element) node).getAttribute(AttributeDefinition.NAME.getName());
                Class<?> clazz = null;
                try {
                    clazz = (Class<?>) Class.forName(className);
                    CommunicationModule socketModule = clazz.getAnnotation(CommunicationModule.class);
                    if (clazz.isInterface() && socketModule != null) {
                        for (Method method : clazz.getMethods()) {
                            CommandDefinition definition = CommandDefinition.instanceOf(method);
                            definitions.add(definition);
                        }
                    }
                } catch (ClassNotFoundException exception) {
                    String message = StringUtility.format("无法获取类型[{}]", className);
                    LOGGER.error(message);
                    throw new CommunicationConfigurationException(message, exception);
                }
            }
        }
        factory.addPropertyValue(CommunicationDispatcherFactory.DEFINITIONS, definitions);

        // 设置接收者
        Element receiverElement = XmlUtility.getUniqueElement(element, ElementDefinition.RECEIVER.getName());
        if (receiverElement == null) {
            throw new CommunicationConfigurationException("接收者配置缺失");
        }
        String receiverBeanName = receiverElement.getAttribute(AttributeDefinition.REFERENCE.getName());
        factory.addPropertyReference(ElementDefinition.RECEIVER.getName(), receiverBeanName);

        // 设置发送者
        Element senderElement = XmlUtility.getUniqueElement(element, ElementDefinition.SENDER.getName());
        if (senderElement == null) {
            throw new CommunicationConfigurationException("发送者配置缺失");
        }
        String senderBeanName = senderElement.getAttribute(AttributeDefinition.REFERENCE.getName());
        factory.addPropertyReference(ElementDefinition.SENDER.getName(), senderBeanName);

        // 设置执行策略
        ManagedMap<String, Object> strategies = getStrategies(element, context);
        factory.addPropertyValue(CommunicationDispatcherFactory.STRATEGIES, strategies);

        // 设置等待
        long wait = Long.valueOf(element.getAttribute(AttributeDefinition.WAIT.getName()));
        factory.addPropertyValue(AttributeDefinition.WAIT.getName(), wait);

        return factory.getBeanDefinition();
    }

    /**
     * 通讯Schema定义的元素
     * 
     * @author Birdy
     */
    enum ElementDefinition {

        /** 根配置元素(属性id,reference,side) */
        CONFIGURATION("configuration"),

        /** 扫描定义元素 */
        SCAN("scan"),
        /** 包定义元素(属性name) */
        PACKAGE("package"),
        /** 类定义元素(属性name) */
        CLASS("class"),

        /** 接收者定义元素(属性reference) */
        RECEIVER("receiver"),
        /** 发送者定义元素(属性reference) */
        SENDER("sender"),

        /** 执行配置定义元素(属性name,reference) */
        STRATEGY("strategy");

        private String name;

        private ElementDefinition(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

    /**
     * 通讯Schema定义的属性
     * 
     * @author Birdy
     */
    enum AttributeDefinition {

        /** 标识 */
        ID("id"),

        /** 名称 */
        NAME("name"),

        /** 引用 */
        REFERENCE("reference"),

        /** 端 */
        SIDE("side"),

        /** 等待 */
        WAIT("wait");

        private String name;

        private AttributeDefinition(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

}
