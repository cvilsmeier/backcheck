package de.backcheck;

import java.io.File;
import java.io.IOException;

public interface Checksummer {

	public String checksum(File file) throws IOException;
}
