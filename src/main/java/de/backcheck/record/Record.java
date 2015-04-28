package de.backcheck.record;

import java.io.IOException;

public class Record {

	public static Record fromString(String line) throws IOException {
		String toks[] = line.split("\\|");
		String relPath = toks[0];
		if( relPath.startsWith("r;") ) {
			relPath = relPath.substring(2);
		} else {
			throw new IOException("invalid record line '"+line+"'");
		}
		long length = Integer.parseInt(toks[1]);
		String checksum = toks[2];
		return new Record(relPath, length, checksum);
	}
	
	// ----------------------
	
	private final String relPath;
	private final long length;
	private final String checksum;

	public Record(String relPath, long length, String checksum) {
		super();
		this.relPath = relPath;
		this.length = length;
		this.checksum = checksum;
	}

	public String getRelPath() {
		return relPath;
	}

	public long getLength() {
		return length;
	}

	public String getChecksum() {
		return checksum;
	}

	@Override
	public String toString() {
		return "r;"+relPath+"|"+length+"|"+checksum;
	}
	
}
