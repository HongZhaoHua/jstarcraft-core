package com.jstarcraft.core.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.junit.Assert;
import org.junit.Test;

public class PressUtilityTestCase {

	@Test
	public void testFactory() throws Exception {
		String left = "message";
		ByteArrayInputStream bufferInput = new ByteArrayInputStream(left.getBytes(StringUtility.CHARSET));
		PipedOutputStream pipeOutput = new PipedOutputStream();
		PipedInputStream pipeInput = new PipedInputStream(pipeOutput);
		ByteArrayOutputStream bufferOutput = new ByteArrayOutputStream();

		PressUtility.compress("gz", bufferInput, pipeOutput);
		PressUtility.decompress("gz", pipeInput, bufferOutput);

		String right = bufferOutput.toString(StringUtility.CHARSET.name());
		Assert.assertEquals(left, right);
	}

}
