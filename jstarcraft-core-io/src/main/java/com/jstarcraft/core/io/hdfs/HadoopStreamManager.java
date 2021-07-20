package com.jstarcraft.core.io.hdfs;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.jstarcraft.core.io.StreamManager;
import com.jstarcraft.core.io.exception.StreamException;

public class HadoopStreamManager implements StreamManager {

    /** 缓冲区大小 */
    private static final int BUFFER_SIZE = 1024;

    private FileSystem system;

    private Path directory;

    public HadoopStreamManager(FileSystem system, String directory) {
        this.system = system;
        this.directory = new Path(directory);
    }

    @Override
    public void saveResource(String path, InputStream stream) {
        Path uri = new Path(directory, path);
        try (OutputStream resource = system.create(uri)) {
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
        try {
            Path uri = new Path(directory, path);
            system.delete(uri, false);
        } catch (Exception exception) {
            throw new StreamException(exception);
        }
    }

    @Override
    public boolean haveResource(String path) {
        try {
            Path uri = new Path(directory, path);
            FileStatus status = system.getFileStatus(uri);
            return status.isFile();
        } catch (Exception exception) {
            throw new StreamException(exception);
        }
    }

    @Override
    public InputStream retrieveResource(String path) {
        try {
            Path uri = new Path(directory, path);
            FileStatus status = system.getFileStatus(uri);
            if (!status.isFile()) {
                return null;
            }
            InputStream stream = system.open(new Path(directory, uri));
            return stream;
        } catch (Exception exception) {
            throw new StreamException(exception);
        }
    }

    private class HadoopStreamIterator implements Iterator<String> {

        private Iterator<FileStatus> iterator;

        private int index;

        private HadoopStreamIterator(Path directory, Iterator<FileStatus> iterator) {
            this.index = directory.toUri().getPath().length();
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public String next() {
            FileStatus file = iterator.next();
            return file.getPath().toUri().getPath().substring(index);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

    @Override
    public Iterator<String> iterateResources(String path) {
        try {
            Path uri = new Path(directory, path);
            FileStatus[] status = system.listStatus(uri);
            Iterator<FileStatus> iterator = Arrays.asList(status).iterator();
            return new HadoopStreamIterator(directory, iterator);
        } catch (Exception exception) {
            throw new StreamException(exception);
        }
    }

}
