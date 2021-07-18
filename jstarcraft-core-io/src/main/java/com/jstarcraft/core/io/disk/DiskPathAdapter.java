package com.jstarcraft.core.io.disk;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;

import com.jstarcraft.core.io.PathAdapter;

public class DiskPathAdapter implements PathAdapter {

    private File directory;

    public DiskPathAdapter(String directory) {
        this(new File(directory));
    }

    public DiskPathAdapter(Path directory) {
        this(directory.toFile());
    }

    public DiskPathAdapter(File directory) {
        this.directory = directory;
    }

    @Override
    public InputStream getStream(String path) throws Exception {
        File file = new File(directory, path);
        InputStream stream = new FileInputStream(file);
        return stream;
    }

}
