package com.jstarcraft.core.io.git;

import com.jstarcraft.core.io.exception.StreamException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.util.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class GitLocalStreamManagerTest {

    private GitLocalStreamManager gitLocalStreamManager;

    private String parent;

    @Before
    public void setUp() throws Exception {
        String tempDir = System.getProperty("java.io.tmpdir");
        Git git = Git.init().setDirectory(new File(tempDir + File.separator + "git-test")).call();
        gitLocalStreamManager = new GitLocalStreamManager(git);
        parent = git.getRepository().getDirectory().getParent();
    }

    @After
    public void tearDown() throws Exception {
        Git git = gitLocalStreamManager.getGit();
        String parent = git.getRepository().getDirectory().getParent();
        FileUtils.delete(new File(parent), 1);
    }

    @Test
    public void saveResource() {
        String[] types = {"protoss", "terran", "zerg","/local-git/mysql"};
        for (String type : types) {
            gitLocalStreamManager.saveResource(File.separator + type + ".txt",
                    new ByteArrayInputStream(type.getBytes(StandardCharsets.UTF_8)));
        }
        Git git = gitLocalStreamManager.getGit();
        assertNotNull(git);
        int count = 0;
        List<String> messages = new ArrayList<>();
        try {
            Iterable<RevCommit> call = git.log().all().call();
            Iterator<RevCommit> iterator = call.iterator();
            while (iterator.hasNext()) {
                RevCommit next = iterator.next();
                count++;
                messages.add(0, next.getFullMessage());
            }
        } catch (GitAPIException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(types.length, count);
        assertNotNull(messages);
        assertEquals(types.length, messages.size());
        for (int i = 0; i < types.length; i++) {
            assertEquals("创建文件:"+types[i]+".txt", messages.get(i));
        }
    }

    @Test
    public void waiveResource_exception() {
        String[] types = initResource();
        for (String type : types) {
            assertThrows(StreamException.class,()->{
                gitLocalStreamManager.waiveResource(type + "XXX.txt");
            });
        }
    }

    @Test
    public void waiveResource() {
        String[] types = initResource();
        for (String type : types) {
            gitLocalStreamManager.waiveResource(type + ".txt");
        }
    }

    @Test
    public void haveResource() {
        String[] types = initResource();
        int count = 0;
        for (String type : types) {
            boolean result = gitLocalStreamManager.haveResource(type + ".txt");
            assertTrue(result);
            result = gitLocalStreamManager.haveResource(type + "XXX.txt");
            assertFalse(result);
            result = gitLocalStreamManager.haveResource(type );
            assertFalse(result);
            count++;
        }
        assertEquals(types.length, count);

    }


    @Test
    public void retrieveResource() {
        String[] types = initResource();
        for (String type : types) {
            InputStream stream = gitLocalStreamManager.retrieveResource(type + ".txt");
            assertNotNull(stream);
            try {
                FileUtils.mkdir(new File(parent + File.separator + type));
            } catch (IOException e) {
                e.printStackTrace();
            }
            stream = gitLocalStreamManager.retrieveResource(type );
            assertNull(stream);
        }
    }


    @Test
    public void retrieveResource_null() {
        String[] types = initResource();
        for (String type : types) {
            try {
                FileUtils.mkdir(new File(parent + File.separator + type));
            } catch (IOException e) {
                e.printStackTrace();
            }
            InputStream stream = gitLocalStreamManager.retrieveResource(type );
            assertNull(stream);
        }
    }

    @Test
    public void retrieveResource_exception() {
        String[] types = initResource();
        for (String type : types) {
            assertThrows(StreamException.class,()->{
                InputStream stream = gitLocalStreamManager.retrieveResource(type + "xxx.txt");
            });
        }
    }

    @Test
    public void iterateResources() {
        String[] types = initResource();
        Iterator<String> iterator = gitLocalStreamManager.iterateResources("/");
        int count = 0;
        while (iterator.hasNext()) {
            String next = iterator.next();
            if (!next.startsWith(".git")) {
                assertNotNull(next);
                long typeCount = Arrays.stream(types)
                        .filter(type -> type.equals(next.substring(0, next.lastIndexOf('.'))))
                        .count();
                assertEquals(1, typeCount);
                count++;
            }
        }
        assertEquals(types.length, count);
    }

    @Test
    public void getUpdatedAt() {
        String[] types = initResource();
        for (String type : types) {
            String path = type + ".txt";
            long updatedAt = gitLocalStreamManager.getUpdatedAt(path);
            assertNotNull(updatedAt);
            String filePath = parent + File.separator + path;
            File file = new File(filePath);
            assertNotNull(file);
            assertTrue(file.isFile());
            assertTrue(file.exists());
            assertTrue(file.canWrite());
            assertTrue(file.canRead());
            try {
                Files.write(Paths.get(filePath), "update".getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
            long updated = gitLocalStreamManager.getUpdatedAt(path);
            assertNotNull(updated);
            assertTrue(updated - updatedAt > 0);
        }
    }



    @Test
    public void getUpdatedAt_return_0() {
        String[] types = initResource();
        String path = types[0];
        try {
            FileUtils.mkdir(new File(parent + File.separator + path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        long updatedAt = gitLocalStreamManager.getUpdatedAt(path);
        assertEquals(0,updatedAt);
    }

    @Test
    public void getUpdatedAt_exception() {
        String[] types = initResource();
        String path = types[0] + "XXX.txt";
        assertThrows(StreamException.class,() ->{
            long updatedAt = gitLocalStreamManager.getUpdatedAt(path);
        });
    }

    private String[] initResource() {
        String[] types = {"protoss", "terran", "zerg"};
        Git git = gitLocalStreamManager.getGit();
        for (String type : types) {
            try {
                FileUtils.createNewFile(new File(parent + File.separator + type + ".txt"));
                git.add().addFilepattern(type + ".txt").call();
                git.commit().setMessage(type + ".txt").call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return types;
    }
}