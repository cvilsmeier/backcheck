package de.backcheck;

import java.io.File;

public interface Checksummer {

	public byte[] checksum(File file) throws Exception;
}
