package com.jstarcraft.core.utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 压缩工具
 * 
 * @author Birdy
 */
@Deprecated
// TODO 待重构
public class PressUtility {

	private static final Logger LOGGER = LoggerFactory.getLogger(PressUtility.class);

	/** 缓冲区大小 */
	private static final int BUFFER_SIZE = 1024;

	/** 任务线程池 */
	private static final ExecutorService executorService = Executors.newCachedThreadPool(new NameThreadFactory("PressUtility"));

	/**
	 * 按照指定的级别压缩指定的数据
	 * 
	 * @param datas
	 * @param level
	 * @return
	 */
	public static byte[] zip(byte[] datas, int level) {
		if (level < Deflater.NO_COMPRESSION || level > Deflater.BEST_COMPRESSION) {
			String message = StringUtility.format("非法的压缩等级[{}]", level);
			LOGGER.error(message);
			throw new IllegalArgumentException(message);
		}
		Deflater deflater = new Deflater(level);
		deflater.setInput(datas);
		deflater.finish();
		try (ByteArrayOutputStream stream = new ByteArrayOutputStream(BUFFER_SIZE)) {
			byte[] bytes = new byte[BUFFER_SIZE];
			while (!deflater.finished()) {
				int count = deflater.deflate(bytes);
				stream.write(bytes, 0, count);
			}
			deflater.end();
			return stream.toByteArray();
		} catch (IOException exception) {
			throw new IllegalStateException("压缩异常", exception);
		}
	}

	/**
	 * 按照指定的时间解压指定的数据
	 * 
	 * @param datas
	 * @param time
	 * @param unit
	 * @return
	 */
	public static byte[] unzip(final byte[] datas, long time, TimeUnit unit) {
		Future<byte[]> future = executorService.submit(new Callable<byte[]>() {
			@Override
			public byte[] call() throws Exception {
				Inflater inflater = new Inflater();
				inflater.setInput(datas);
				byte[] bytes = new byte[BUFFER_SIZE];
				try (ByteArrayOutputStream stream = new ByteArrayOutputStream(BUFFER_SIZE)) {
					while (!inflater.finished()) {
						int count = inflater.inflate(bytes);
						stream.write(bytes, 0, count);
					}
					inflater.end();
					return stream.toByteArray();
				}
			}
		});
		try {
			return future.get(time, unit);
		} catch (InterruptedException exception) {
			throw new IllegalStateException("解压中断:", exception);
		} catch (ExecutionException exception) {
			throw new IllegalStateException("解压异常:", exception);
		} catch (TimeoutException exception) {
			throw new IllegalStateException("解压超时", exception);
		}
	}

	private static final CompressorStreamFactory compressorStreamFactory = new CompressorStreamFactory();

	/**
	 * 将输入流根据指定类型压缩到输出流
	 * 
	 * @param type
	 * @param input
	 * @param output
	 */
	public static void compress(String type, InputStream input, OutputStream output) {
		try (CompressorOutputStream compressor = compressorStreamFactory.createCompressorOutputStream(type, output)) {
			byte[] buffer = new byte[BUFFER_SIZE];
			int length = -1;
			while ((length = input.read(buffer)) != -1) {
				compressor.write(buffer, 0, length);
			}
		} catch (Exception exception) {
			String message = StringUtility.format("压缩{}异常", type);
			throw new IllegalStateException(message, exception);
		}
	}

	/**
	 * 将输入流根据指定类型解压到输出流
	 * 
	 * @param type
	 * @param input
	 * @param output
	 */
	public static void decompress(String type, InputStream input, OutputStream output) {
		try (CompressorInputStream compressor = compressorStreamFactory.createCompressorInputStream(type, input)) {
			byte[] buffer = new byte[BUFFER_SIZE];
			int length = -1;
			while ((length = compressor.read(buffer)) != -1) {
				output.write(buffer, 0, length);
			}
		} catch (Exception exception) {
			String message = StringUtility.format("解压{}异常", type);
			throw new IllegalStateException(message, exception);
		}
	}

	public static void compressZip(File fromDirectory, File toFile) {
		try (FileOutputStream fileOutputStream = new FileOutputStream(toFile); ZipArchiveOutputStream archiveOutputStream = new ZipArchiveOutputStream(fileOutputStream)) {
			byte[] buffer = new byte[BUFFER_SIZE];
			for (File file : fromDirectory.listFiles()) {
				try (FileInputStream fileInputStream = new FileInputStream(file)) {
					ZipArchiveEntry archiveEntry = new ZipArchiveEntry(file.getName());
					archiveOutputStream.putArchiveEntry(archiveEntry);
					int length = -1;
					while ((length = fileInputStream.read(buffer)) != -1) {
						archiveOutputStream.write(buffer, 0, length);
					}
					archiveOutputStream.closeArchiveEntry();
				}
			}
			archiveOutputStream.finish();
		} catch (IOException exception) {
			throw new IllegalStateException("压缩ZIP异常", exception);
		}
	}

	public static void decompressZip(File fromFile, File toDirectory) {
		try (FileInputStream fileInputStream = new FileInputStream(fromFile); ZipArchiveInputStream archiveInputStream = new ZipArchiveInputStream(fileInputStream)) {
			byte[] buffer = new byte[BUFFER_SIZE];
			ArchiveEntry archiveEntry;
			while (null != (archiveEntry = archiveInputStream.getNextEntry())) {
				File file = new File(toDirectory, archiveEntry.getName());
				try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
					int length = -1;
					while ((length = archiveInputStream.read(buffer)) != -1) {
						fileOutputStream.write(buffer, 0, length);
					}
					fileOutputStream.flush();
				}
			}
		} catch (IOException exception) {
			throw new IllegalStateException("解压ZIP异常:", exception);
		}
	}

	public static void compress7z(File fromDirectory, File toFile) {
		try (SevenZOutputFile archiveOutputStream = new SevenZOutputFile(toFile)) {
			byte[] buffer = new byte[BUFFER_SIZE];
			for (File file : fromDirectory.listFiles()) {
				try (FileInputStream fileInputStream = new FileInputStream(file)) {
					SevenZArchiveEntry archiveEntry = archiveOutputStream.createArchiveEntry(fromDirectory, file.getName());
					archiveOutputStream.putArchiveEntry(archiveEntry);
					int length = -1;
					while ((length = fileInputStream.read(buffer)) != -1) {
						archiveOutputStream.write(buffer, 0, length);
					}
					archiveOutputStream.closeArchiveEntry();
				}
			}
		} catch (IOException exception) {
			throw new IllegalStateException("压缩7Z异常", exception);
		}
	}

	public static void decompress7z(File fromFile, File toDirectory) {
		try (SevenZFile archiveInputStream = new SevenZFile(fromFile)) {
			byte[] buffer = new byte[BUFFER_SIZE];
			ArchiveEntry archiveEntry;
			while (null != (archiveEntry = archiveInputStream.getNextEntry())) {
				File file = new File(toDirectory, archiveEntry.getName());
				try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
					int length = -1;
					while ((length = archiveInputStream.read(buffer)) != -1) {
						fileOutputStream.write(buffer, 0, length);
					}
				}
			}
		} catch (IOException exception) {
			throw new IllegalStateException("解压7Z异常:", exception);
		}
	}

}
