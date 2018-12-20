package com.jstarcraft.core.storage.schema;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
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

import com.jstarcraft.core.storage.annotation.StorageConfiguration;
import com.jstarcraft.core.storage.definition.FormatDefinition;
import com.jstarcraft.core.storage.exception.StorageException;
import com.jstarcraft.core.utility.StringUtility;
import com.jstarcraft.core.utility.XmlUtility;

/**
 * 仓储XML解析器
 * 
 * @author Birdy
 */
public class StorageXmlParser extends AbstractBeanDefinitionParser {

	private static final Logger logger = LoggerFactory.getLogger(StorageXmlParser.class);
	/** 资源匹配符 */
	private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

	/** 资源搜索分析器(负责查找StorageConfiguration) */
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
			String name = StorageConfiguration.class.getName();
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
			logger.error(message, exception);
			throw new StorageException(message, exception);
		}
	}
	
	private void assembleProcessor(ParserContext parserContext) {
		BeanDefinitionRegistry registry = parserContext.getRegistry();
		String name = StringUtility.uncapitalize(StorageAccessorProcessor.class.getSimpleName());
		BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(StorageAccessorProcessor.class);
		registry.registerBeanDefinition(name, factory.getBeanDefinition());
	}

	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
		assembleProcessor(parserContext);
		
		// 仓储管理器工厂
		BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(StorageManagerFactory.class);

		String formatName = element.getAttribute(AttributeDefinition.FORMAT.getName());

		// 设置仓储格式映射
		Map<String, BeanDefinition> formats = new ManagedMap<>();
		Collection<Element> formatElements = XmlUtility.getChildElementsByTagName(element, ElementDefinition.FORMAT.getName());
		for (Element formatElement : formatElements) {
			String adapter = formatElement.getAttribute(AttributeDefinition.ADAPTER.getName());
			String name = formatElement.getAttribute(AttributeDefinition.NAME.getName());
			String path = formatElement.getAttribute(AttributeDefinition.PATH.getName());
			String suffix = formatElement.getAttribute(AttributeDefinition.SUFFIX.getName());
			BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(FormatDefinition.class);
			builder.addConstructorArgReference(adapter);
			builder.addConstructorArgValue(name);
			builder.addConstructorArgValue(path);
			builder.addConstructorArgValue(suffix);
			BeanDefinition format = builder.getBeanDefinition();
			formats.put(name, format);
		}

		// 设置仓储定义映射
		Map<Class<?>, BeanDefinition> definitions = new ManagedMap<>();
		NodeList scanNodes = XmlUtility.getChildElementByTagName(element, ElementDefinition.SCAN.getName()).getChildNodes();
		for (int index = 0; index < scanNodes.getLength(); index++) {
			Node node = scanNodes.item(index);
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
						BeanDefinition format = null;
						StorageConfiguration configuration = clazz.getAnnotation(StorageConfiguration.class);
						if (StringUtility.isNoneBlank(configuration.format())) {
							format = formats.get(configuration.format());
						} else {
							format = formats.get(formatName);
						}
						if (format == null) {
							String message = StringUtility.format("无法获取格式[{}]", format);
							logger.error(message);
							throw new StorageException(message);
						}
						definitions.put(clazz, format);
					} catch (ClassNotFoundException exception) {
						String message = StringUtility.format("无法获取类型[{}]", className);
						logger.error(message);
						throw new StorageException(message, exception);
					}
				}
			}

			if (name.equals(ElementDefinition.CLASS.getName())) {
				// 自动类加载处理
				String className = ((Element) node).getAttribute(AttributeDefinition.NAME.getName());
				Class<?> clazz = null;
				try {
					clazz = (Class<?>) Class.forName(className);
					BeanDefinition format = null;
					StorageConfiguration configuration = clazz.getAnnotation(StorageConfiguration.class);
					if (StringUtility.isNoneBlank(configuration.format())) {
						format = formats.get(configuration.format());
					} else {
						format = formats.get(formatName);
					}
					if (format == null) {
						String message = StringUtility.format("无法获取格式[{}]", format);
						logger.error(message);
						throw new StorageException(message);
					}
					definitions.put(clazz, format);
				} catch (ClassNotFoundException exception) {
					String message = StringUtility.format("无法获取类型[{}]", className);
					logger.error(message);
					throw new StorageException(message, exception);
				}
			}
		}
		factory.addPropertyValue(StorageManagerFactory.DEFINITIONS, definitions);

		return factory.getBeanDefinition();
	}

	/**
	 * 仓储Schema定义的元素
	 * 
	 * @author Birdy
	 */
	enum ElementDefinition {

		/** 根配置元素(属性id,format) */
		CONFIGURATION("configuration"),

		/** 格式定义元素(属性name,adapter,path,suffix) */
		FORMAT("format"),

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
	 * 仓储Schema定义的属性
	 * 
	 * @author Birdy
	 */
	enum AttributeDefinition {

		/** 标识 */
		ID("id"),

		/** 格式 */
		FORMAT("format"),

		/** 适配器 */
		ADAPTER("adapter"),

		/** 名称 */
		NAME("name"),

		/** 路径 */
		PATH("path"),

		/** 后缀 */
		SUFFIX("suffix");

		private String name;

		private AttributeDefinition(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

	}

}
