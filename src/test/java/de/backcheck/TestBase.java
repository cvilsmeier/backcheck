package de.backcheck;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import org.junit.Assert;

public class TestBase extends Assert {

	public File tempFile(File parent, String name, String data) throws IOException {
		File f = new File(parent, name);
		try (FileOutputStream out = new FileOutputStream(f)) {
			out.write(data.getBytes("UTF-8"));
		}
		f.deleteOnExit();
		return f;
	}

	public File tempDir() throws IOException {
		File tmpdir = new File(System.getProperty("java.io.tmpdir"));
		return tempDir(tmpdir, "backcheck-"+UUID.randomUUID().toString());
	}

	public File tempDir(File parent, String name) throws IOException {
		File f = new File(parent, name);
		f.mkdirs();
		f.deleteOnExit();
		return f;
	}

}
