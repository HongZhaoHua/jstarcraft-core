package com.jstarcraft.core.io.hdfs;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;

import com.jstarcraft.core.io.StreamManager;
import com.jstarcraft.core.io.exception.StreamException;

public class HdfsStreamManager implements StreamManager {

    /** 缓冲区大小 */
    private static final int BUFFER_SIZE = 1024;

    private FileSystem system;

    private Path directory;

    public HdfsStreamManager(FileSystem system, String directory) {
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
            if (system.exists(uri)) {
                FileStatus status = system.getFileStatus(uri);
                if (status.isFile()) {
                    system.delete(uri, false);
                }
            }
        } catch (Exception exception) {
            throw new StreamException(exception);
        }
    }

    @Override
    public boolean haveResource(String path) {
        try {
            Path uri = new Path(directory, path);
            if (system.exists(uri)) {
                FileStatus status = system.getFileStatus(uri);
                return status.isFile();
            }
            return false;
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
            InputStream stream = system.open(uri);
            return stream;
        } catch (Exception exception) {
            throw new StreamException(exception);
        }
    }

    private class HadoopStreamIterator implements Iterator<String> {

        private RemoteIterator<LocatedFileStatus> iterator;

        private int index;

        private HadoopStreamIterator(FileStatus directory, RemoteIterator<LocatedFileStatus> iterator) {
            this.index = directory.getPath().toUri().getPath().length() + 1;
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            try {
                return iterator.hasNext();
            } catch (Exception exception) {
                throw new StreamException(exception);
            }
        }

        @Override
        public String next() {
            try {
                LocatedFileStatus status = iterator.next();
                return status.getPath().toUri().getPath().substring(index);
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
    public Iterator<String> iterateResources(String path) {
        try {
            Path uri = new Path(directory, path);
            RemoteIterator<LocatedFileStatus> iterator = system.listFiles(uri, true);
            return new HadoopStreamIterator(system.getFileStatus(directory), iterator);
        } catch (Exception exception) {
            throw new StreamException(exception);
        }
    }

    @Override
    public long getUpdatedAt(String path) {
        try {
            Path uri = new Path(directory, path);
            if (system.exists(uri)) {
                FileStatus status = system.getFileStatus(uri);
                return status.getModificationTime();
            }
            return 0;
        } catch (Exception exception) {
            throw new StreamException(exception);
        }
    }

}
