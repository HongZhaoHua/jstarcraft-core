package com.jstarcraft.core.utility;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

/**
 * 内存编译器
 * 
 * @author Birdy
 *
 */
public class MemoryCompiler {

	private JavaCompiler compiler;
	private StandardJavaFileManager standardManager;

	public MemoryCompiler() {
		this.compiler = ToolProvider.getSystemJavaCompiler();
		this.standardManager = compiler.getStandardFileManager(null, null, null);
	}

	public Map<String, byte[]> compile(String file, String code) throws IOException {
		try (MemoryJavaFileManager manager = new MemoryJavaFileManager(standardManager)) {
			JavaFileObject java = manager.makeStringSource(file, code);
			CompilationTask task = compiler.getTask(null, manager, null, null, null, Arrays.asList(java));
			if (!task.call()) {
				throw new RuntimeException("编译代码异常");
			}
			return manager.getClassBytes();
		}
	}

}
