package com.jstarcraft.core.utility;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;

/**
 * 内存文件管理器
 * 
 * @author Birdy
 *
 */
class MemoryJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {

	private final Map<String, byte[]> bytes = new HashMap<String, byte[]>();

	MemoryJavaFileManager(JavaFileManager manager) {
		super(manager);
	}

	public Map<String, byte[]> getClassBytes() {
		return new HashMap<String, byte[]>(bytes);
	}

	@Override
	public void flush() throws IOException {
	}

	@Override
	public void close() throws IOException {
		bytes.clear();
	}

	@Override
	public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className, Kind kind, FileObject sibling) throws IOException {
		if (kind == Kind.CLASS) {
			return new MemoryOutputJavaFileObject(className);
		} else {
			return super.getJavaFileForOutput(location, className, kind, sibling);
		}
	}

	JavaFileObject makeStringSource(String name, String code) {
		return new MemoryInputJavaFileObject(name, code);
	}

	static class MemoryInputJavaFileObject extends SimpleJavaFileObject {

		private final String code;

		MemoryInputJavaFileObject(String name, String code) {
			super(URI.create("string:///" + name), Kind.SOURCE);
			this.code = code;
		}

		@Override
		public CharBuffer getCharContent(boolean ignoreEncodingErrors) {
			return CharBuffer.wrap(code);
		}

	}

	class MemoryOutputJavaFileObject extends SimpleJavaFileObject {

		private final String name;

		MemoryOutputJavaFileObject(String name) {
			super(URI.create("string:///" + name), Kind.CLASS);
			this.name = name;
		}

		@Override
		public OutputStream openOutputStream() {
			return new FilterOutputStream(new ByteArrayOutputStream()) {
				@Override
				public void close() throws IOException {
					out.close();
					ByteArrayOutputStream stream = (ByteArrayOutputStream) out;
					bytes.put(name, stream.toByteArray());
				}
			};
		}

	}

}
