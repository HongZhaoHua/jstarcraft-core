package com.jstarcraft.core.resource.path;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;

public class FilePathAdapter implements PathAdapter {

    private File directory;

    public FilePathAdapter(String directory) {
        this(new File(directory));
    }

    public FilePathAdapter(Path directory) {
        this(directory.toFile());
    }

    public FilePathAdapter(File directory) {
        this.directory = directory;
    }

    @Override
    public InputStream getStream(String path) throws Exception {
        File file = new File(directory, path);
        InputStream stream = new FileInputStream(file);
        return stream;
    }

}
