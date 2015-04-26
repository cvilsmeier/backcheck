package de.backcheck;

import java.io.File;
import java.io.IOException;

public interface Checksummer {

	public byte[] checksum(File file) throws IOException;
}
