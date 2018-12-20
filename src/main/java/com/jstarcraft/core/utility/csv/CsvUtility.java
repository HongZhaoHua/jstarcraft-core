package com.jstarcraft.core.utility.csv;

import java.io.StringReader;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringEscapeUtils;

import com.jstarcraft.core.utility.ClassUtility;
import com.jstarcraft.core.utility.ConversionUtility;
import com.jstarcraft.core.utility.StringUtility;
import com.jstarcraft.core.utility.TypeUtility;
import com.jstarcraft.core.utility.csv.annotation.CsvConfiguration;

/**
 * CSV工具
 * 
 * @author Birdy
 *
 */
public class CsvUtility {

	/** 分号(为了兼容null与AWK搜索,所有字符串以分号结束.) */
	public static final String SEMICOLON = ";";

	private static final CSVFormat FORMAT = CSVFormat.DEFAULT;

	private static final HashMap<Class<?>, CsvInformation> INFORMATIONS = new HashMap<>();

	private static final CsvInformation getInformation(Class<?> clazz) {
		synchronized (clazz) {
			CsvInformation information = INFORMATIONS.get(clazz);
			try {
				if (information == null) {
					CsvConfiguration configuration = clazz.getAnnotation(CsvConfiguration.class);
					if (configuration == null || configuration.value().length == 0) {
						return null;
					}
					Constructor<?> constructor = clazz.getDeclaredConstructor();
					constructor.setAccessible(true);
					String[] names = configuration.value();
					LinkedList<Field> fields = new LinkedList<>();
					for (String name : names) {
						Field field = clazz.getDeclaredField(name);
						field.setAccessible(true);
						fields.add(field);
					}
					information = new CsvInformation(constructor, fields.toArray(new Field[fields.size()]));
					INFORMATIONS.put(clazz, information);
				}
			} catch (Exception exception) {
				throw new RuntimeException(exception);
			}
			return information;
		}
	}

	private static void writeValue(Object object, Type type, CSVPrinter output) {
		try {
			// TODO 处理null
			if (object == null) {
				output.print(StringUtility.EMPTY);
				return;
			}
			Class<?> clazz = TypeUtility.getRawType(type, null);
			// 处理枚举/字符串/原始类型
			if (clazz.isEnum() || String.class == clazz || ClassUtility.isPrimitiveOrWrapper(clazz)) {
				Object value = object.toString();
				if (String.class == clazz) {
					value = value + SEMICOLON;
				}
				output.print(value);
				return;
			}
			// 处理日期类型
			if (Date.class.isAssignableFrom(clazz)) {
				Object value = String.valueOf(Date.class.cast(object).getTime());
				output.print(value);
				return;
			}
			if (Instant.class.isAssignableFrom(clazz)) {
				Object value = String.valueOf(Instant.class.cast(object).toEpochMilli());
				output.print(value);
				return;
			}
			// 处理数组类型
			if (clazz.isArray()) {
				Class<?> componentClass = null;
				Type componentType = null;
				if (type instanceof GenericArrayType) {
					GenericArrayType genericArrayType = GenericArrayType.class.cast(type);
					componentType = genericArrayType.getGenericComponentType();
					componentClass = TypeUtility.getRawType(componentType, null);
				} else {
					componentType = clazz.getComponentType();
					componentClass = clazz.getComponentType();
				}
				CsvInformation information = CsvUtility.getInformation(componentClass);
				int length = Array.getLength(object);
				output.print(length);
				for (int index = 0; index < length; index++) {
					Object element = Array.get(object, index);
					if (information == null) {
						writeValue(element, componentType, output);
					} else {
						for (Field field : information.getFields()) {
							Object value = field.get(element);
							writeValue(value, field.getGenericType(), output);
						}
					}
				}
				return;
			}
			// 处理集合类型
			if (Collection.class.isAssignableFrom(clazz)) {
				ParameterizedType parameterizedType = ParameterizedType.class.cast(type);
				Type[] types = parameterizedType.getActualTypeArguments();
				CsvInformation information = CsvUtility.getInformation(Class.class.cast(types[0]));
				Collection<?> collection = Collection.class.cast(object);
				output.print(collection.size());
				for (Object element : collection) {
					if (information == null) {
						writeValue(element, types[0], output);
					} else {
						for (Field field : information.getFields()) {
							Object value = field.get(element);
							writeValue(value, field.getGenericType(), output);
						}
					}
				}
				return;
			}
			// 处理映射类型
			if (Map.class.isAssignableFrom(clazz)) {
				ParameterizedType parameterizedType = ParameterizedType.class.cast(type);
				Type[] types = parameterizedType.getActualTypeArguments();
				CsvInformation keyInformation = CsvUtility.getInformation(Class.class.cast(types[0]));
				CsvInformation valueInformation = CsvUtility.getInformation(Class.class.cast(types[1]));
				Map<Object, Object> map = Map.class.cast(object);
				output.print(map.size());
				for (Entry<Object, Object> keyValue : map.entrySet()) {
					Object key = keyValue.getKey();
					if (keyInformation == null) {
						writeValue(key, types[0], output);
					} else {
						for (Field field : keyInformation.getFields()) {
							Object element = field.get(key);
							writeValue(element, field.getGenericType(), output);
						}
					}
					Object value = keyValue.getValue();
					if (valueInformation == null) {
						writeValue(value, types[1], output);
					} else {
						for (Field field : valueInformation.getFields()) {
							Object element = field.get(value);
							writeValue(element, field.getGenericType(), output);
						}
					}
				}
				return;
			}
			// 处理对象类型
			CsvInformation information = CsvUtility.getInformation(clazz);
			for (Field field : information.getFields()) {
				Object value = field.get(object);
				writeValue(value, field.getGenericType(), output);
			}
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private static Object readValue(Type type, Iterator<String> input) {
		try {
			// TODO 处理null
			Class<?> clazz = TypeUtility.getRawType(type, null);
			// 处理枚举/字符串/原始类型
			if (clazz.isEnum() || String.class == clazz || ClassUtility.isPrimitiveOrWrapper(clazz)) {
				String element = input.next();
				if (StringUtility.isEmpty(element)) {
					return null;
				}
				if (String.class == clazz) {
					element = element.substring(0, element.length() - 1);
				}
				Object value = ConversionUtility.convert(element, clazz);
				return value;
			}
			// 处理日期类型
			if (Date.class.isAssignableFrom(clazz)) {
				String element = input.next();
				if (StringUtility.isEmpty(element)) {
					return null;
				}
				Object value = new Date(Long.valueOf(element));
				return value;
			}
			if (Instant.class.isAssignableFrom(clazz)) {
				String element = input.next();
				if (StringUtility.isEmpty(element)) {
					return null;
				}
				Object value = Instant.ofEpochMilli(Long.valueOf(element));
				return value;
			}
			// 处理数组类型
			if (clazz.isArray()) {
				String check = input.next();
				if (StringUtility.isEmpty(check)) {
					return null;
				}
				int length = Integer.valueOf(check);
				Class<?> componentClass = null;
				Type componentType = null;
				if (type instanceof GenericArrayType) {
					GenericArrayType genericArrayType = GenericArrayType.class.cast(type);
					componentType = genericArrayType.getGenericComponentType();
					componentClass = TypeUtility.getRawType(componentType, null);
				} else {
					componentType = clazz.getComponentType();
					componentClass = clazz.getComponentType();
				}
				CsvInformation information = getInformation(componentClass);
				Object array = Array.newInstance(componentClass, length);
				for (int index = 0; index < length; index++) {
					Object element = null;
					if (information == null) {
						element = readValue(componentType, input);
					} else {
						Constructor<?> constructor = information.getConstructor();
						element = constructor.newInstance();
						for (Field field : information.getFields()) {
							Object value = readValue(field.getGenericType(), input);
							field.set(element, value);
						}
					}
					Array.set(array, index, element);
				}
				return array;
			}
			// 处理集合类型
			if (Collection.class.isAssignableFrom(clazz)) {
				String check = input.next();
				if (StringUtility.isEmpty(check)) {
					return null;
				}
				int length = Integer.valueOf(check);
				ParameterizedType parameterizedType = ParameterizedType.class.cast(type);
				Type[] types = parameterizedType.getActualTypeArguments();
				CsvInformation information = getInformation(Class.class.cast(types[0]));
				Collection<Object> collection = Collection.class.cast(clazz.newInstance());
				for (int index = 0; index < length; index++) {
					Object element = null;
					if (information == null) {
						element = readValue(types[0], input);
					} else {
						Constructor<?> constructor = information.getConstructor();
						element = constructor.newInstance();
						for (Field field : information.getFields()) {
							Object value = readValue(field.getGenericType(), input);
							field.set(element, value);
						}
					}
					collection.add(element);
				}
				return collection;
			}
			// 处理映射类型
			if (Map.class.isAssignableFrom(clazz)) {
				String check = input.next();
				if (StringUtility.isEmpty(check)) {
					return null;
				}
				int length = Integer.valueOf(check);
				ParameterizedType parameterizedType = ParameterizedType.class.cast(type);
				Type[] types = parameterizedType.getActualTypeArguments();
				CsvInformation keyInformation = getInformation(Class.class.cast(types[0]));
				CsvInformation valueInformation = getInformation(Class.class.cast(types[1]));
				Map<Object, Object> map = Map.class.cast(clazz.newInstance());
				for (int index = 0; index < length; index++) {
					Object key = null;
					if (keyInformation == null) {
						key = readValue(types[0], input);
					} else {
						Constructor<?> constructor = keyInformation.getConstructor();
						key = constructor.newInstance();
						for (Field field : keyInformation.getFields()) {
							Object value = readValue(field.getGenericType(), input);
							field.set(key, value);
						}
					}
					Object element = null;
					if (valueInformation == null) {
						element = readValue(types[1], input);
					} else {
						Constructor<?> constructor = valueInformation.getConstructor();
						element = constructor.newInstance();
						for (Field field : valueInformation.getFields()) {
							Object value = readValue(field.getGenericType(), input);
							field.set(element, value);
						}
					}
					map.put(key, element);
				}
				return map;
			}
			// 处理对象类型
			CsvInformation information = CsvUtility.getInformation(clazz);
			Constructor<?> constructor = information.getConstructor();
			Object object = constructor.newInstance();
			for (Field field : information.getFields()) {
				Object value = readValue(field.getGenericType(), input);
				field.set(object, value);
			}
			return object;
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	/**
	 * 将对象转换为CSV(TODO 测试发现csv转换为json转换2倍时间)
	 * 
	 * @param instance
	 * @return
	 */
	public static String object2String(Object instance, Type type) {
		StringBuilder buffer = new StringBuilder();
		try (CSVPrinter output = new CSVPrinter(buffer, FORMAT)) {
			writeValue(instance, type, output);
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
		return buffer.toString();
	}

	/**
	 * 将CSV转换为对象(TODO 测试发现csv转换为json转换2倍时间)
	 * 
	 * @param csv
	 * @param clazz
	 * @return
	 */
	public static <T> T string2Object(String csv, Type type) {
		StringReader buffer = new StringReader(csv);
		try (CSVParser input = new CSVParser(buffer, FORMAT)) {
			Iterator<CSVRecord> iterator = input.iterator();
			if (iterator.hasNext()) {
				CSVRecord values = iterator.next();
				return (T) readValue(type, values.iterator());
			}
			return null;
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	/**
	 * 对字符串执行CSV转义
	 * 
	 * @param string
	 * @return
	 */
	public static final String escapeCsv(String string) {
		return StringEscapeUtils.escapeCsv(string);
	}

	/**
	 * 对字符串执行CSV翻译
	 * 
	 * @param string
	 * @return
	 */
	public static final String unescapeCsv(String string) {
		return StringEscapeUtils.unescapeCsv(string);
	}

}
