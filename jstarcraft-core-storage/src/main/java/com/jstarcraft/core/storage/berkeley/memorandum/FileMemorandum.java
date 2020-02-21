package com.jstarcraft.core.storage.berkeley.memorandum;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.storage.berkeley.exception.BerkeleyMemorandumException;
import com.jstarcraft.core.utility.StringUtility;
import com.sleepycat.je.CheckpointConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.util.DbBackup;
import com.sleepycat.je.util.LogVerificationInputStream;

// 备份数据目录要做层多层，例如:[年-月]/[日]/[时间]
public class FileMemorandum implements Memorandum {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileMemorandum.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd" + File.separator + "HH-mm-ss").withZone(ZoneOffset.UTC);

    public static final String MARK_FILE = "mark.txt";

    public static final String MEMORANDUM_FILE = "memorandum.txt";

    private final int cacheSize;

    /** 环境目录 */
    private final File environmentDirectory;
    /** 备忘目录 */
    private final File memorandumDirectory;
    /** 标记文件 */
    private final File markFile;
    /** 标记数字 */
    private long markNumber;

    public FileMemorandum(int cacheSize, File environmentDirectory, File memorandumDirectory) {
        this.cacheSize = cacheSize;
        this.environmentDirectory = environmentDirectory;
        this.memorandumDirectory = memorandumDirectory;
        this.markFile = new File(memorandumDirectory, MARK_FILE);
        try {
            if (markFile.createNewFile()) {
                // 标记文件不存在
                markNumber = -1L;
            } else {
                try (FileInputStream input = new FileInputStream(markFile); InputStreamReader reader = new InputStreamReader(input); BufferedReader buffer = new BufferedReader(reader)) {
                    markNumber = Long.valueOf(buffer.readLine());
                }
            }
        } catch (Exception exception) {
            throw new BerkeleyMemorandumException(exception);
        }

    }

    private void copyFile(Environment environment, File from, File to) throws IOException {
        final byte[] cache = new byte[cacheSize];
        try (FileInputStream input = new FileInputStream(from); LogVerificationInputStream log = new LogVerificationInputStream(environment, input, from.getName()); FileOutputStream output = new FileOutputStream(to)) {
            while (true) {
                final int length = log.read(cache);
                if (length < 0) {
                    break;
                }
                output.write(cache, 0, length);
            }
        }
    }

    @Override
    public void checkIn(Environment environment, Instant now) {
        // 了解CheckpointConfig对checkpoint()的影响
        // 此处执行checkpoint()是为了减少恢复时间
        CheckpointConfig configuration = new CheckpointConfig();
        environment.checkpoint(configuration.setForce(true));
        DbBackup berkeley = new DbBackup(environment, markNumber);
        File directory = new File(memorandumDirectory, formatter.format(now));
        try {
            berkeley.startBackup();
            final String[] memorandumNames = berkeley.getLogFilesInBackupSet();
            Collection<File> memorandumFiles = new ArrayList<File>(memorandumNames.length);
            for (String name : memorandumNames) {
                File file = new File(environmentDirectory, name);
                memorandumFiles.add(file);
            }
            if (!directory.mkdirs()) {
                throw new BerkeleyMemorandumException();
            }
            for (File from : memorandumFiles) {
                final File to = new File(directory, from.getName());
                copyFile(environment, from, to);
            }

            File memorandumFile = new File(directory, MEMORANDUM_FILE);
            memorandumFile.createNewFile();
            try (FileOutputStream output = new FileOutputStream(memorandumFile); OutputStreamWriter writer = new OutputStreamWriter(output); BufferedWriter buffer = new BufferedWriter(writer);) {
                final String[] environmentNames = environmentDirectory.list();
                for (String name : environmentNames) {
                    buffer.write(name);
                    buffer.newLine();
                }
            }
            markNumber = berkeley.getLastFileInBackupSet();
        } catch (Exception exception) {
            String message = StringUtility.format("备份异常");
            LOGGER.error(message, exception);
            try {
                FileUtils.deleteDirectory(directory);
            } catch (IOException ioException) {
            }
            throw new BerkeleyMemorandumException(message, exception);
        } finally {
            berkeley.endBackup();
        }

    }

    @Override
    public void checkOut(Instant from, Instant to) {
        // 清空目标目录
        try {
            FileUtils.deleteDirectory(environmentDirectory);
        } catch (IOException ioException) {
        }

        environmentDirectory.mkdirs();

        // 按照日期排序的Map,保证新文件会覆盖旧文件
        final TreeMap<Instant, File> restoreDirectoryMap = new TreeMap<>();
        for (File dayDirectory : memorandumDirectory.listFiles()) {
            if (dayDirectory.isDirectory()) {
                for (File timeDirectory : dayDirectory.listFiles()) {
                    if (timeDirectory.isDirectory()) {
                        Instant date = Instant.from(formatter.parse(dayDirectory.getName() + File.separator + timeDirectory.getName()));
                        if (!date.isBefore(from) && !date.isAfter(to)) {
                            restoreDirectoryMap.put(date, timeDirectory);
                        }
                    }
                }
            }
        }

        if (restoreDirectoryMap.size() == 0) {
            throw new BerkeleyMemorandumException("不包含恢复目录");
        }

        final File lastBackUpDirectory = restoreDirectoryMap.lastEntry().getValue();
        final File listFile = new File(lastBackUpDirectory, MEMORANDUM_FILE);

        if (!listFile.exists()) {
            throw new BerkeleyMemorandumException("备忘文件不存在");
        }

        // 恢复的文件列表
        final Collection<String> names = new HashSet<String>();

        try (FileInputStream fileInputStream = new FileInputStream(listFile); InputStreamReader dataInputStream = new InputStreamReader(fileInputStream); BufferedReader bufferedReader = new BufferedReader(dataInputStream);) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                names.add(line);
            }

            // 恢复目标目录
            for (Entry<Instant, File> keyValue : restoreDirectoryMap.entrySet()) {
                final File restoreDirectory = keyValue.getValue();
                for (File fromFile : restoreDirectory.listFiles()) {
                    if (names.contains(fromFile.getName()) && fromFile.isFile()) {
                        final File toFile = new File(environmentDirectory, fromFile.getName());
                        FileUtils.copyFile(fromFile, toFile);
                    }
                }
            }
        } catch (Exception exception) {
            throw new BerkeleyMemorandumException("恢复文件失败", exception);
        }
    }

    @Override
    public void clean(Instant expire) {
        // 按照日期排序的Map,保证新文件会覆盖旧文件
        final TreeMap<Instant, File> cleanDirectoryMap = new TreeMap<>();

        for (File dayDirectory : memorandumDirectory.listFiles()) {
            if (dayDirectory.isDirectory()) {
                for (File timeDirectory : dayDirectory.listFiles()) {
                    if (timeDirectory.isDirectory()) {
                        Instant date = Instant.from(formatter.parse(dayDirectory.getName() + File.separator + timeDirectory.getName()));
                        if (!date.isAfter(expire)) {
                            cleanDirectoryMap.put(date, timeDirectory);
                        }
                    }
                }
            }
        }

        if (cleanDirectoryMap.size() == 0) {
            throw new BerkeleyMemorandumException("不包含清理目录");
        }

        final File lastBackUpDirectory = cleanDirectoryMap.lastEntry().getValue();
        final File listFile = new File(lastBackUpDirectory, MEMORANDUM_FILE);

        if (!listFile.exists()) {
            throw new BerkeleyMemorandumException("列表文件不存在");
        }

        // 保留的文件列表
        final Collection<String> saveList = new HashSet<String>();
        try (FileInputStream input = new FileInputStream(listFile); InputStreamReader reader = new InputStreamReader(input); BufferedReader buffer = new BufferedReader(reader);) {
            String line;
            while ((line = buffer.readLine()) != null) {
                saveList.add(line);
            }
        } catch (Exception exception) {
            throw new BerkeleyMemorandumException("清理文件失败", exception);
        }

        // 清理目标目录
        for (Entry<Instant, File> keyValue : cleanDirectoryMap.entrySet()) {
            final File cleanDirectory = keyValue.getValue();
            for (File cleanFile : cleanDirectory.listFiles()) {
                if (cleanFile.isFile()) {
                    if (!listFile.equals(cleanFile) && !saveList.contains(cleanFile.getName())) {
                        cleanFile.delete();
                    }
                }
            }
            if (cleanDirectory.list().length == 0) {
                cleanDirectory.delete();
            }
        }
    }

}
