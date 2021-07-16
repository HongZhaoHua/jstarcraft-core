package com.jstarcraft.core.resource.path;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipPathAdapter implements PathAdapter {

    private ZipFile zip;

    private String directory;

    public ZipPathAdapter(String name, Charset charset, String directory) {
        try {
            this.zip = new ZipFile(name, charset);
            this.directory = directory;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public ZipPathAdapter(ZipFile zip, String directory) {
        this.zip = zip;
        this.directory = directory;
    }

    @Override
    public InputStream getStream(String path) throws Exception {
        ZipEntry term = zip.getEntry(directory + path);
        InputStream stream = zip.getInputStream(term);
        return stream;
    }

}
