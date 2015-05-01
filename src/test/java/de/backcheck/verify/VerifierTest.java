package de.backcheck.verify;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import de.backcheck.Checksummer;
import de.backcheck.Logger;
import de.backcheck.TestBase;
import de.backcheck.record.Record;
import de.backcheck.record.RecorderResult;

public class VerifierTest extends TestBase {

	static class Csummer implements Checksummer {
		@Override
		public String checksum(File file) throws IOException {
			byte[] buf = new byte[1024];
			FileInputStream in = new FileInputStream(file);
			int c = in.read(buf);
			in.close();
			assertEquals(file.length(), c);
			return new String(buf, 0, c);
		}
	}

	static class Lger implements Logger {
		ArrayList<String> logs = new ArrayList<String>();

		@Override
		public void error(String msg, Throwable t) {
			logs.add(msg + " " + t.getMessage());
		}

		@Override
		public void info(String msg) {
			logs.add(msg);
		}

		@Override
		public void debug(String msg) {
		}
	}

	Csummer checksummer;
	Lger logger;
	File tmpdir;
	RecorderResult rr;
	File dir1, f1, dir2, f2, dir3, f3, f0;

	@Before
	public void setup() throws Exception {
		checksummer = new Csummer();
		logger = new Lger();
		tmpdir = tempDir();
		dir1 = tempDir(tmpdir, "dir1");
		f1 = tempFile(dir1, "f1", "f1");
		dir2 = tempDir(tmpdir, "dir2");
		f2 = tempFile(dir2, "f2", "f2");
		dir3 = tempDir(dir2, "dir3");
		f3 = tempFile(dir3, "f3", "f3");
		f0 = tempFile(tmpdir, "f0", "f0");
		// tmpdir/
		//   dir1/
		//     f1
		//   dir2/
		//     dir3/
		//       f3
		//     f2
		//   f0
		rr = new RecorderResult();
		rr.addRecord(new Record("dir1/f1", 2, "f1"));
		rr.addRecord(new Record("dir2/f2", 2, "f2"));
		rr.addRecord(new Record("dir2/dir3/f3", 2, "f3"));
		rr.addRecord(new Record("f0", 2, "f0"));
	}

	@Test
	public void testNoErrors() throws Exception {
		Verifier v = new Verifier(checksummer, logger);
		v.verify(tmpdir, rr.getRecords());
		assertEquals(0, logger.logs.size());
	}

	@Test
	public void testFileLengthChanged() throws Exception {
		writeFile(f0, "f0__");
		Verifier v = new Verifier(checksummer, logger);
		v.verify(tmpdir, rr.getRecords());
		assertEquals(1, logger.logs.size());
		assertLog("DIFF LENGTH", File.separator + "f0", logger.logs.remove(0));
	}

	@Test
	public void testFileContentChanged() throws Exception {
		writeFile(f0, "XX");
		Verifier v = new Verifier(checksummer, logger);
		v.verify(tmpdir, rr.getRecords());
		assertEquals(1, logger.logs.size());
		assertLog("DIFF CHECKSUM", File.separator + "f0", logger.logs.remove(0));
	}

	@Test
	public void testFileNotFound() throws Exception {
		f1.delete();
		Verifier v = new Verifier(checksummer, logger);
		v.verify(tmpdir, rr.getRecords());
		assertEquals(1, logger.logs.size());
		assertLog("NOT FOUND", "dir1" + File.separator + "f1", logger.logs.remove(0));
	}

	@Test
	public void testFileBecameDirectory() throws Exception {
		f1.delete();
		tempDir(dir1, "f1");
		Verifier v = new Verifier(checksummer, logger);
		v.verify(tmpdir, rr.getRecords());
		assertEquals(1, logger.logs.size());
		assertLog("NOT FOUND", "dir1" + File.separator + "f1", logger.logs.remove(0));
	}

	private void assertLog(String start, String end, String log) {
		assertEquals("'" + log + "' must start with '" + start + "'", start, log.substring(0, start.length()));
		assertEquals("'" + log + "' must end with '" + end + "'", end, log.substring(log.length() - end.length()));
	}

	private void writeFile(File file, String data) throws IOException {
		try (FileOutputStream out = new FileOutputStream(file)) {
			out.write(data.getBytes());
		}
	}

}
