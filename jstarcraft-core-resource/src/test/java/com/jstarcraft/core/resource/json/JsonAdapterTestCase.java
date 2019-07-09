package com.jstarcraft.core.resource.json;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jstarcraft.core.resource.Storage;
import com.jstarcraft.core.resource.StorageManager;
import com.jstarcraft.core.resource.annotation.StorageAccessor;
import com.jstarcraft.core.resource.exception.StorageException;
import com.jstarcraft.core.utility.DelayElement;
import com.jstarcraft.core.utility.SensitivityQueue;

/**
 * 仓储注解测试
 * 
 * @author Birdy
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Component
public class JsonAdapterTestCase {

	/** 修复时间间隔 */
	private static final long FIX_TIME = 1000;

	@Autowired
	private MockSpringObject springObject;
	@Autowired
	private StorageManager storageManager;
	@StorageAccessor
	private Storage<Integer, Person> storage;
	@StorageAccessor("2")
	private Person person;
	@StorageAccessor(value = "2", clazz = Person.class, property = "sex")
	private boolean sex;
	@StorageAccessor(value = "2", clazz = Person.class, property = "description")
	private String description;

	/**
	 * 测试仓储访问器
	 */
	@Test
	public void testAssemblage() {
		// 保证@StorageAccessor注解的接口与类型能被自动装配
		Assert.assertThat(springObject, CoreMatchers.notNullValue());
		Assert.assertThat(storage, CoreMatchers.notNullValue());
		Assert.assertThat(person, CoreMatchers.notNullValue());

		// 检查仓储访问
		Assert.assertThat(storage.getAll().size(), CoreMatchers.equalTo(3));
		Assert.assertThat(storage.getInstance(2, false), CoreMatchers.sameInstance(person));

		// 检查实例访问
		Assert.assertThat(person.isSex(), CoreMatchers.equalTo(sex));

		// 检查引用访问
		Assert.assertThat(person.getChild(), CoreMatchers.sameInstance(storage.getInstance(2, false)));
		Assert.assertThat(person.getReference(), CoreMatchers.sameInstance(springObject));
		Assert.assertThat(person.getStorage(), CoreMatchers.sameInstance(storage));

		// 检查属性访问
		Assert.assertTrue(sex);
		Assert.assertThat(description, CoreMatchers.notNullValue());
	}

	/**
	 * 测试仓储索引
	 */
	@Test
	public void testIndex() {
		List<Person> ageIndex = storage.getMultiple(Person.INDEX_AGE, 32);
		Assert.assertThat(ageIndex.size(), CoreMatchers.equalTo(2));

		Person birdy = storage.getSingle(Person.INDEX_NAME, "Birdy");
		Assert.assertThat(birdy, CoreMatchers.equalTo(storage.getInstance(1, false)));
	}

	private static final String oldFileName = "Person-old.js";
	private static final String personFileName = "Person.js";
	private static final String newFileName = "Person-new.js";

	private void setMonitor() {
		try {
			SensitivityQueue<DelayElement<Class<?>>> tasks = new SensitivityQueue<>(FIX_TIME);
			// 监控文件修改
			HashSet<File> directories = new HashSet<>();
			HashMap<String, Class<?>> classes = new HashMap<>();
			HashMap<WatchKey, Path> paths = new HashMap<>();
			for (Class<?> clazz : storageManager.getDefinitions().keySet()) {
				Resource resource = storageManager.getResource(clazz);
				File file = resource.getFile();
				classes.put(file.getAbsolutePath(), clazz);
				directories.add(file.getParentFile());
			}

			WatchService watchService = FileSystems.getDefault().newWatchService();
			for (File directory : directories) {
				Path path = Paths.get(directory.getAbsolutePath());
				WatchKey key = path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
				paths.put(key, path);
			}

			Thread monitor = new Thread(() -> {
				while (true) {
					try {
						WatchKey key = watchService.take();
						for (WatchEvent<?> event : key.pollEvents()) {
							if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
								Path directory = paths.get(key);
								Path file = (Path) event.context();
								File path = new File(directory.toString(), file.toString());
								Class<?> clazz = classes.get(path.getAbsolutePath());
								Instant now = Instant.now();
								Instant expire = now.plusMillis(100);
								DelayElement<Class<?>> element = new DelayElement<>(clazz, expire);
								tasks.offer(element);
							}
						}
					} catch (InterruptedException exception) {
						break;
					}
				}
			});
			monitor.setDaemon(true);
			monitor.start();

			Thread tasker = new Thread(() -> {
				while (true) {
					try {
						DelayElement<Class<?>> element = tasks.take();
						Class<?> clazz = element.getContent();
						storageManager.loadStorage(clazz);
					} catch (InterruptedException exception) {
						break;
					}
				}
			});
			tasker.setDaemon(true);
			tasker.start();
		} catch (Exception exception) {
			throw new StorageException(exception);
		}
	}

	/**
	 * 测试仓储装载
	 */
	@Test
	public void testLoad() throws Exception {
		File newFile = new File(this.getClass().getResource(newFileName).toURI());
		File oldFile = new File(this.getClass().getResource(oldFileName).toURI());
		File personFile = new File(this.getClass().getResource(personFileName).toURI());

		setMonitor();

		try {
			// 新的文件会将Person.js所有的Person年龄修改为10.
			FileUtils.copyFile(newFile, personFile);
			Thread.sleep(1000);
			// 验证索引是否修改
			Collection<Person> persons = storage.getMultiple(Person.INDEX_AGE, 10);
			Assert.assertThat(persons.size(), CoreMatchers.equalTo(3));
			// 验证访问器是否修改
			Assert.assertThat(person.getAge(), CoreMatchers.equalTo(10));
		} finally {
			FileUtils.copyFile(oldFile, personFile);
		}
	}

}
