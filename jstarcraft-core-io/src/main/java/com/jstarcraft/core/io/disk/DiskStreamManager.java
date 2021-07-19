package com.jstarcraft.core.io.disk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;

import com.jstarcraft.core.io.StreamManager;
import com.jstarcraft.core.io.exception.StreamException;
import com.jstarcraft.core.utility.KeyValue;

public class DiskStreamManager implements StreamManager {

    /** 缓冲区大小 */
    private static final int BUFFER_SIZE = 1024;

    private File directory;

    public DiskStreamManager(String directory) {
        this(new File(directory));
    }

    public DiskStreamManager(Path directory) {
        this(directory.toFile());
    }

    public DiskStreamManager(File directory) {
        this.directory = directory;
    }

    @Override
    public void saveResource(String path, InputStream stream) {
        File file = new File(directory, path);
        File directory = file.getParentFile();
        if (!directory.exists()) {
            directory.mkdirs();
        }
        try (FileOutputStream resource = new FileOutputStream(file)) {
            byte[] bytes = new byte[BUFFER_SIZE];
            int size;
            while ((size = stream.read(bytes)) > 0) {
                resource.write(bytes, 0, size);
            }
        } catch (Exception exception) {
            throw new StreamException(exception);
        }
    }

    @Override
    public void waiveResource(String path) {
        File file = new File(directory, path);
        file.delete();
    }

    @Override
    public boolean haveResource(String path) {
        File file = new File(directory, path);
        return file.exists();
    }

    private class DiskStreamIterator implements Iterator<KeyValue<String, InputStream>> {

        private Iterator<File> iterator;

        private DiskStreamIterator(Iterator<File> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public KeyValue<String, InputStream> next() {
            File file = iterator.next();
            try {
                InputStream stream = new FileInputStream(file);
                return new KeyValue<>(file.getName(), stream);
            } catch (Exception exception) {
                throw new StreamException(exception);
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

    @Override
    public Iterator<KeyValue<String, InputStream>> iterateResources(String path) {
        File file = new File(directory, path);
        Iterator<File> iterator = FileUtils.iterateFiles(file, null, null);
        return new DiskStreamIterator(iterator);
    }

    @Override
    public InputStream retrieveResource(String path) {
        try {
            File file = new File(directory, path);
            if (!file.exists() || !file.isFile()) {
                return null;
            }
            InputStream stream = new FileInputStream(file);
            return stream;
        } catch (Exception exception) {
            throw new StreamException(exception);
        }
    }

}
