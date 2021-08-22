package com.jstarcraft.core.io.git;

import com.jstarcraft.core.io.StreamManager;
import com.jstarcraft.core.io.exception.StreamException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.eclipse.jgit.api.Git;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class GitLocalStreamManager implements StreamManager {

    /**
     * 缓冲区大小
     */
    private static final int BUFFER_SIZE = 1024;

    private Git git;

    private String parent;

    public GitLocalStreamManager(Git git) {
        this.git = git;
        this.parent = git.getRepository().getDirectory().getParent();

    }

    public Git getGit() {
        return git;
    }

    @Override
    public void saveResource(String path, InputStream stream) {
        File file = new File(parent, path);
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
            String fileName = getFileName(path);
            git.add().addFilepattern(fileName).call();
            git.commit().setMessage("创建文件:" + fileName).call();
        } catch (Exception exception) {
            throw new StreamException(exception);
        }

    }

    @Override
    public void waiveResource(String path) {
        try {
            Path filePath = Paths.get(parent + File.separator + path);
            boolean del = Files.deleteIfExists(filePath);
            if (del) {
                git.add().addFilepattern(".").call();
                git.commit().setMessage("删除文件:" + getFileName(path)).call();
            } else {
                throw new FileNotFoundException();
            }
        } catch (Exception e) {
            throw new StreamException(e);
        }
    }

    @Override
    public boolean haveResource(String path) {
        Path resPath = Paths.get(parent + File.separator + path);
        return Files.exists(resPath) && !Files.isDirectory(resPath);
    }

    @Override
    public InputStream retrieveResource(String path) {
        try {
            Path filePath = Paths.get(parent + File.separator + path);
            if (Files.isDirectory(filePath)) {
                return null;
            }
            return Files.newInputStream(filePath);
        } catch (Exception exception) {
            throw new StreamException(exception);
        }
    }

    private class GitLocalStreamIterator implements Iterator<String> {

        private Iterator<File> iterator;

        private int index;

        private GitLocalStreamIterator(File directory, Iterator<File> iterator) {
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
        File directory = new File(parent);
        File files = new File(directory, path);
        Iterator<File> iterator = FileUtils.iterateFiles(files, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        return new GitLocalStreamIterator(directory, iterator);
    }

    @Override
    public long getUpdatedAt(String path) {
        try {
            Path filePath = Paths.get(parent + File.separator + path);
            if (!Files.isDirectory(filePath)) {
                FileTime time = Files.getLastModifiedTime(filePath);
                return time.to(TimeUnit.MILLISECONDS);
            }
            return 0;
        } catch (Exception e) {
            throw new StreamException(e);
        }
    }

    private String getFileName(String path) {
        return path.substring(path.lastIndexOf(File.separator) + 1);
    }
}
