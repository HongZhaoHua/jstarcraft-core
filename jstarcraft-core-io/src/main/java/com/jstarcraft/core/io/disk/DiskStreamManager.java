package com.jstarcraft.core.io.disk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import com.jstarcraft.core.io.StreamManager;
import com.jstarcraft.core.io.exception.StreamException;

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
        try (OutputStream resource = new FileOutputStream(file)) {
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
        return file.isFile() && file.exists();
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

    private class DiskStreamIterator implements Iterator<String> {

        private Iterator<File> iterator;

        private int index;

        private DiskStreamIterator(File directory, Iterator<File> iterator) {
            this.index = directory.toURI().getPath().length();
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public String next() {
            File file = iterator.next();
            return file.toURI().getPath().substring(index);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

    @Override
    public Iterator<String> iterateResources(String path) {
        File files = new File(directory, path);
        Iterator<File> iterator = FileUtils.iterateFiles(files, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        return new DiskStreamIterator(directory, iterator);
    }

    @Override
    public long getUpdatedAt(String path) {
        File file = new File(directory, path);
        if (!file.exists() || !file.isFile()) {
            return file.lastModified();
        }
        return 0;
    }

}
