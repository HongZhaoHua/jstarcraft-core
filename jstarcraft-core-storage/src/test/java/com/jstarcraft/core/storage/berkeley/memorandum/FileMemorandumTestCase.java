package com.jstarcraft.core.storage.berkeley.memorandum;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jstarcraft.core.storage.berkeley.BerkeleyAccessor;
import com.jstarcraft.core.storage.berkeley.entity.Pack;
import com.jstarcraft.core.storage.berkeley.entity.Person;
import com.jstarcraft.core.storage.berkeley.memorandum.FileMemorandum;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class FileMemorandumTestCase {

    @Autowired
    private BerkeleyAccessor accessor;

    private File environmentDirectory = new File("target" + File.separator + "environment");

    private File memorandumDirectory = new File("target" + File.separator + "memorandum");

    private LocalDateTime dateTime = LocalDateTime.of(2017, 1, 1, 0, 0, 0);

    @Before
    public void beforeTest() throws Exception {
        FileUtils.forceMkdir(environmentDirectory);
        FileUtils.forceMkdir(memorandumDirectory);
    }

    @After
    public void afterTest() throws Exception {
        FileUtils.forceDelete(environmentDirectory);
        FileUtils.forceDelete(memorandumDirectory);
    }

    @Test
    public void testMemorandum() throws Exception {
        FileMemorandum fileMemorandum = new FileMemorandum(10485760, environmentDirectory, memorandumDirectory);

        int size = 100000;
        for (long index = 0; index < size; index++) {
            Person person = new Person(index, String.valueOf(index));
            accessor.createInstance(Person.class, person);
        }
        Instant from = dateTime.toInstant(ZoneOffset.UTC);
        // 执行备份
        fileMemorandum.checkIn(accessor.getEnvironment(), from);
        int memorandum = 0;
        Iterator<File> iterator = FileUtils.iterateFiles(memorandumDirectory, null, true);
        while (iterator.hasNext()) {
            File file = iterator.next();
            if (file.getName().equals(FileMemorandum.MEMORANDUM_FILE)) {
                memorandum++;
            }
        }
        Assert.assertTrue(memorandum == 1);

        for (long index = 0; index < size; index++) {
            Pack pack = new Pack(index, size, index);
            accessor.createInstance(Pack.class, pack);
        }
        Instant to = from.plus(1, ChronoUnit.HOURS);
        // 执行备份
        fileMemorandum.checkIn(accessor.getEnvironment(), to);
        memorandum = 0;
        iterator = FileUtils.iterateFiles(memorandumDirectory, null, true);
        while (iterator.hasNext()) {
            File file = iterator.next();
            if (file.getName().equals(FileMemorandum.MEMORANDUM_FILE)) {
                memorandum++;
            }
        }
        Assert.assertTrue(memorandum == 2);

        // 执行清理
        fileMemorandum.clean(to);
        memorandum = 0;
        iterator = FileUtils.iterateFiles(memorandumDirectory, null, true);
        while (iterator.hasNext()) {
            File file = iterator.next();
            if (file.getName().equals(FileMemorandum.MEMORANDUM_FILE)) {
                memorandum++;
            }
        }
        Assert.assertTrue(memorandum == 1);

        accessor.stop();
        FileUtils.forceDelete(environmentDirectory);

        // 执行还原
        fileMemorandum.checkOut(from, to);
        accessor.start();
        Assert.assertTrue(accessor.countInstances(Person.class) == size);
        Assert.assertTrue(accessor.countInstances(Pack.class) == size);
        accessor.stop();
    }

}
