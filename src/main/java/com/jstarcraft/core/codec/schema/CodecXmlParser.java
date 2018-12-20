package com.jstarcraft.core.codec.schema;

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

import com.jstarcraft.core.codec.protocolbufferx.annotation.ProtocolConfiguration;

/**
 * 编解码XML解析器
 * 
 * @author Birdy
 */
public class CodecXmlParser extends AbstractBeanDefinitionParser {

	private static final Logger LOGGER = LoggerFactory.getLogger(CodecXmlParser.class);

	/** 默认资源匹配符 */
	protected static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

	/** 资源搜索分析器(负责查找ProtocolConfiguration) */
	private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
	/** 元数据分析器(负责获取注解) */
	private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {

		BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(CodecDefinitionFactory.class);

		// 要创建的对象信息
		HashSet<Class<?>> protocolClasses = new HashSet<>();

		// 检查XML内容
		NodeList nodes = element.getChildNodes();
		for (int nodeIndex = 0; nodeIndex < nodes.getLength(); nodeIndex++) {
			Node node = nodes.item(nodeIndex);
			if (node.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			String name = node.getLocalName();

			if (name.equals(ElementDefinition.PACKAGE.getName())) {
				// 自动包扫描处理
				String packageName = ((Element) node).getAttribute(AttributeDefinition.NAME.getName());
				String[] names = getResources(packageName);
				for (String resource : names) {
					try {
						Class<?> clazz = Class.forName(resource);
						if (!protocolClasses.add(clazz)) {
							throw new IllegalStateException();
						}
					} catch (ClassNotFoundException exception) {
						throw new IllegalStateException(exception);
					}
				}
			}
			if (name.equals(ElementDefinition.CLASS.getName())) {
				try {
					String className = ((Element) node).getAttribute(AttributeDefinition.NAME.getName());
					Class<?> clazz = Class.forName(className);
					if (!protocolClasses.add(clazz)) {
						throw new IllegalStateException();
					}
				} catch (Exception exception) {
					throw new IllegalStateException(exception);
				}
			}
		}
		factory.addPropertyValue("protocolClasses", protocolClasses);
		AbstractBeanDefinition definition = factory.getBeanDefinition();
		return definition;
	}

	/**
	 * 获取指定包下的静态资源对象
	 * 
	 * @param packageName
	 *            包名
	 * @return
	 * @throws IOException
	 */
	private String[] getResources(String packageName) {
		try {
			// 搜索资源
			String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(packageName)) + "/" + DEFAULT_RESOURCE_PATTERN;
			Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);
			// 提取资源
			Set<String> names = new HashSet<String>();
			String name = ProtocolConfiguration.class.getName();
			for (Resource resource : resources) {
				if (!resource.isReadable()) {
					continue;
				}
				// 判断是否静态资源
				MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
				ClassMetadata classMetadata = metadataReader.getClassMetadata();
				AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
				if (annotationMetadata.hasAnnotation(name)) {
					names.add(classMetadata.getClassName());
				} else {
					String[] interfaceNames = classMetadata.getInterfaceNames();
					for (String interfaceName : interfaceNames) {
						metadataReader = this.metadataReaderFactory.getMetadataReader(interfaceName);
						annotationMetadata = metadataReader.getAnnotationMetadata();
						if (annotationMetadata.hasAnnotation(name)) {
							names.add(classMetadata.getClassName());
							break;
						}
					}
				}
			}
			return names.toArray(new String[0]);
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	/**
	 * 协议Schema定义的元素
	 * 
	 * @author Birdy
	 */
	enum ElementDefinition {

		/** 根配置元素(属性id) */
		CONFIGURATION("configuration"),

		/** 包定义元素(属性name,index) */
		PACKAGE("package"),
		/** 类定义元素(属性name,index) */
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
	 * 协议Schema定义的属性
	 * 
	 * @author Birdy
	 */
	enum AttributeDefinition {

		// /** 标识 */
		// INDEX("index"),

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
