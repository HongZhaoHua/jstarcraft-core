package com.jstarcraft.core.utility;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Stack;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 包工具
 * 
 * @author Birdy
 *
 */
public class PackageUtility {

    private static final Logger LOGGER = LoggerFactory.getLogger(PackageUtility.class);

    public static class PackageScanner {
        private final Collection<Class<?>> clazzCollection = new HashSet<Class<?>>();

        public PackageScanner(final String... packageNames) {
            for (String packageName : packageNames) {
                try {
                    final String packageDirectory = packageName.replace('.', '/');
                    final Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(packageDirectory);
                    while (urls.hasMoreElements()) {
                        final URL url = urls.nextElement();
                        if ("file".equals(url.getProtocol())) {
                            final File directory = new File(url.getPath());
                            if (!directory.isDirectory()) {
                                throw new RuntimeException("package:[" + packageName + "] is not directory");
                            }
                            clazzCollection.addAll(PackageUtility.scanClassByDirectory(packageName, directory));
                        } else if ("jar".equals(url.getProtocol())) {
                            final JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
                            clazzCollection.addAll(PackageUtility.scanClassByJar(packageName, jar));
                        }
                    }
                } catch (IOException exception) {
                    throw new RuntimeException(exception);
                }
            }
        }

        public PackageScanner(final Collection<String> packageNames) {
            this(packageNames.toArray(new String[packageNames.size()]));
        }

        public Collection<Class<?>> getClazzCollection() {
            return this.clazzCollection;
        }
    }

    /**
     * 按照Jar扫描类
     * 
     * @param packageName
     * @param jar
     * @return
     */
    public static Collection<Class<?>> scanClassByJar(final String packageName, final JarFile jar) {
        final Enumeration<JarEntry> jarEntries = jar.entries();
        final Pattern pattern = Pattern.compile("(" + packageName.replace('.', '/') + ".*)\\.class");
        final Collection<Class<?>> clazzCollection = new HashSet<Class<?>>();

        while (jarEntries.hasMoreElements()) {
            final JarEntry entry = jarEntries.nextElement();
            final String name = entry.getName();
            final Matcher matcher = pattern.matcher(name.replace(File.separatorChar, '/'));
            if (matcher.find()) {
                final String className = matcher.group(1).replace('/', '.');
                // 处理匿名类
                if (className.contains("$")) {
                    continue;
                }
                try {
                    // Class.forName会触发static代码块
                    final Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
                    clazzCollection.add(clazz);
                } catch (ClassNotFoundException exception) {
                    LOGGER.error("无法加载类[{}]", className, exception);
                }
            }
        }
        return clazzCollection;
    }

    /**
     * 按照目录扫描类
     * 
     * @param packageName
     * @param directory
     * @return
     */
    public static Collection<Class<?>> scanClassByDirectory(final String packageName, final File directory) {
        final Stack<File> scanDirectories = new Stack<File>();
        final Collection<File> classFiles = new ArrayList<File>();
        final FileFilter fileFilter = new FileFilter() {

            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    scanDirectories.push(file);
                    return false;
                }
                return file.getName().matches(".*\\.class$");
            }

        };

        scanDirectories.push(directory);

        while (!scanDirectories.isEmpty()) {
            final File scanDirectory = scanDirectories.pop();
            Collections.addAll(classFiles, scanDirectory.listFiles(fileFilter));
        }
        final Pattern pattern = Pattern.compile("(" + packageName.replace('.', '/') + ".*)\\.class");
        final Collection<Class<?>> clazzCollection = new HashSet<Class<?>>();
        for (File file : classFiles) {
            final Matcher matcher = pattern.matcher(file.getAbsolutePath().replace(File.separatorChar, '/'));
            if (matcher.find()) {
                final String className = matcher.group(1).replace('/', '.');
                // 处理匿名类
                if (className.contains("$")) {
                    continue;
                }
                try {
                    // Class.forName会触发static代码块
                    final Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
                    clazzCollection.add(clazz);
                } catch (ClassNotFoundException exception) {
                    LOGGER.error("无法加载类[{}]", className, exception);
                }
            }
        }

        return clazzCollection;
    }

}
