package de.backcheck.recorder;

import java.io.File;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import de.backcheck.ChecksummerImpl;
import de.backcheck.Logger;
import de.backcheck.TestBase;
import de.backcheck.record.Recorder;
import de.backcheck.record.RecorderResult;

public class RecorderTest extends TestBase {
	
	static class Lger implements Logger {
		ArrayList<String> logs = new ArrayList<String>();
		@Override
		public void info(String msg) {
			logs.add(msg);
		}

		@Override
		public void debug(String msg) {
			logs.add(msg);
		}
	}

	ChecksummerImpl checksummer;
	Lger logger;
	File tmpdir;
	
	@Before
	public void setup() throws Exception {
		checksummer = new ChecksummerImpl();
		logger = new Lger();
		tmpdir = tempDir();
		File dir1 = tempDir(tmpdir, "dir1");
		File dir2 = tempDir(tmpdir, "dir2");
		File dir3 = tempDir(dir2, "dir3");
		tempFile(tmpdir, "f0", "f0");
		tempFile(dir1, "f1", "f1");
		tempFile(dir1, "f2", "f2");
		tempFile(dir2, "f3", "f3");
		tempFile(dir2, "f4", "f4");
		tempFile(dir3, "fx", "fx");
	}
	
	@Test
	public void testRecurseIntoDirectories() throws Exception {
		Recorder recorder = new Recorder(checksummer, logger, -1);
		RecorderResult rr = recorder.record(tmpdir);
		assertEquals(6, rr.getRecords().size());
		assertEquals("r;dir1/f1|2|bd19836ddb62c11c55ab251ccaca5645", rr.getRecords().get(0).toString());
		assertEquals("r;dir1/f2|2|3667f6a0c97490758d7dc9659d01ea34", rr.getRecords().get(1).toString());
		assertEquals("r;dir2/dir3/fx|2|c3f9558d681bac963339b7c69894c4f7", rr.getRecords().get(2).toString());
		assertEquals("r;dir2/f3|2|1779cf3aa50c413afc7e05adb7e1b0de", rr.getRecords().get(3).toString());
		assertEquals("r;dir2/f4|2|6e1fcd704528ad8bf6d6bbedb9210096", rr.getRecords().get(4).toString());
		assertEquals("r;f0|2|cae8a623cc417d219936676028e26d4f", rr.getRecords().get(5).toString());
	}

	
	@Test
	public void testMaxDepthZero() throws Exception {
		Recorder recorder = new Recorder(checksummer, logger, 0);
		RecorderResult rr = recorder.record(tmpdir);
		assertEquals(1, rr.getRecords().size());
		assertEquals("r;f0|2|cae8a623cc417d219936676028e26d4f", rr.getRecords().get(0).toString());
	}
	
	@Test
	public void testMaxDepthOne() throws Exception {
		Recorder recorder = new Recorder(checksummer, logger, 1);
		RecorderResult rr = recorder.record(tmpdir);
		assertEquals(5, rr.getRecords().size());
		assertEquals("r;dir1/f1|2|bd19836ddb62c11c55ab251ccaca5645", rr.getRecords().get(0).toString());
		assertEquals("r;dir1/f2|2|3667f6a0c97490758d7dc9659d01ea34", rr.getRecords().get(1).toString());
		assertEquals("r;dir2/f3|2|1779cf3aa50c413afc7e05adb7e1b0de", rr.getRecords().get(2).toString());
		assertEquals("r;dir2/f4|2|6e1fcd704528ad8bf6d6bbedb9210096", rr.getRecords().get(3).toString());
		assertEquals("r;f0|2|cae8a623cc417d219936676028e26d4f", rr.getRecords().get(4).toString());
	}

}
