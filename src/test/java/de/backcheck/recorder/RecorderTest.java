package de.backcheck.recorder;

import java.io.File;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import de.backcheck.ChecksummerImpl;
import de.backcheck.Logger;
import de.backcheck.TestBase;
import de.backcheck.record.Record;
import de.backcheck.record.Recorder;

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

		void reset() {
			logs.clear();
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
		File d1 = tempDir(tmpdir, "dir1");
		File d2 = tempDir(tmpdir, "dir2");
		tempFile(tmpdir, "f0", "f0");
		tempFile(d1, "f1", "f1");
		tempFile(d1, "f2", "f2");
		tempFile(d2, "f3", "f3");
		tempFile(d2, "f4", "f4");
	}
	
	@Test
	public void testRecurseIntoDirectories() throws Exception {
		Recorder recorder = new Recorder(checksummer, logger, -1);
		Record r = recorder.record(tmpdir);
		assertEquals(3, r.getRecords().size());
		assertEquals("dir1, true, 0, , 2", digest(r.getRecords().get(0)));
		{
			assertEquals("f1, false, 2, bd19836ddb62c11c55ab251ccaca5645, 0", digest(r.getRecords().get(0).getRecords().get(0)));
			assertEquals("f2, false, 2, 3667f6a0c97490758d7dc9659d01ea34, 0", digest(r.getRecords().get(0).getRecords().get(1)));
		}
		assertEquals("dir2, true, 0, , 2", digest(r.getRecords().get(1)));
		{
			assertEquals("f3, false, 2, 1779cf3aa50c413afc7e05adb7e1b0de, 0", digest(r.getRecords().get(1).getRecords().get(0)));
			assertEquals("f4, false, 2, 6e1fcd704528ad8bf6d6bbedb9210096, 0", digest(r.getRecords().get(1).getRecords().get(1)));
		}
		assertEquals("f0, false, 2, cae8a623cc417d219936676028e26d4f, 0", digest(r.getRecords().get(2)));
	}

	
	@Test
	public void testMaxDepth() throws Exception {
		Recorder recorder = new Recorder(checksummer, logger, 0);
		Record r = recorder.record(tmpdir);
		assertEquals(3, r.getRecords().size());
		assertEquals("dir1, true, 0, , 0", digest(r.getRecords().get(0)));
		assertEquals("dir2, true, 0, , 0", digest(r.getRecords().get(1)));
		assertEquals("f0, false, 2, cae8a623cc417d219936676028e26d4f, 0", digest(r.getRecords().get(2)));
	}

	
	private String digest(Record r ) {
		return ""+r.getName()+", "+r.isDirectory()+", "+r.getLength()+", "+r.getChecksum()+", "+r.getRecords().size();
	}
}
