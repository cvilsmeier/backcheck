package de.backcheck.recorder;

import java.io.File;
import java.util.Arrays;

import org.junit.Test;

import de.backcheck.TestBase;
import de.backcheck.record.Record;

public class RecordTest extends TestBase {
	
	@Test
	public void testWriteAndReadEmpty() throws Exception {
		File tmpdir = tempDir();
		File dat = tempFile(tmpdir,"dat",""); 
		Record root = new Record("root", true, 0, "");
		root.writeToFile(dat);
		Record r = Record.readFromFile(dat);
		assertEquals("root", r.getName());
		assertEquals(true, r.isDirectory());
		assertEquals(0, r.getLength());
		assertEquals(0, r.getRecords().size());
	}
	
	@Test
	public void testWriteAndReadNonEmpty() throws Exception {
		File tmpdir = tempDir();
		File dat = tempFile(tmpdir,"dat",""); 
		Record r1 = new Record("r1", false, 1, "1111");
		Record r2 = new Record("r2", false, 2, "2222");
		Record root = new Record("root", true, 0, "", Arrays.asList(r1,r2));
		root.writeToFile(dat);
		Record r = Record.readFromFile(dat);
		assertEquals("root, true, 0, , 2", digest(r));
		assertEquals("r1, false, 1, 1111, 0", digest(r.getRecords().get(0)));
		assertEquals("r2, false, 2, 2222, 0", digest(r.getRecords().get(1)));
	}

	private String digest(Record r ) {
		return ""+r.getName()+", "+r.isDirectory()+", "+r.getLength()+", "+r.getChecksum()+", "+r.getRecords().size();
	}
}
