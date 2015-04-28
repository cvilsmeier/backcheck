package de.backcheck.recorder;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import de.backcheck.TestBase;
import de.backcheck.record.Record;
import de.backcheck.record.RecorderResult;

public class RecorderResultTest extends TestBase {

	File tmpdir;
	File datfile;

	@Before
	public void setup() throws Exception {
		tmpdir = tempDir();
		datfile = tempFile(tmpdir,"dat","");
	}
	
	@Test
	public void testWriteAndReadEmpty() throws Exception {
		RecorderResult rr = new RecorderResult();
		rr.writeToFile(datfile);
		rr = RecorderResult.readFromFile(datfile);
		assertEquals(0, rr.getRecords().size());
	}
	
	@Test
	public void testWriteAndReadNonEmpty() throws Exception {
		RecorderResult rr = new RecorderResult();
		rr.addRecord(new Record("relPath", 13, "131313"));
		rr.writeToFile(datfile);
		rr = RecorderResult.readFromFile(datfile);
		assertEquals(1, rr.getRecords().size());
		Record r = rr.getRecords().get(0);
		assertEquals("relPath", r.getRelPath());
		assertEquals(13, r.getLength());
		assertEquals("131313", r.getChecksum());
	}

}
