package de.backcheck.record;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Record {

	public static Record readFromFile(File file) throws IOException {
		DataInputStream in = null;
		try {
			in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
			Record record = Record.read(in);
			return record;
		} finally {
			if( in != null ) {
				try {
					in.close();
				} catch (Exception ignored) {}
			}
		}
	}

	private static Record read(DataInput in) throws IOException {
		String name = in.readUTF();
		boolean directory = in.readBoolean();
		long length = in.readLong();
		String checksum = in.readUTF();
		int c = in.readInt();
		ArrayList<Record> records = new ArrayList<Record>();
		for( int i=0 ; i<c ; i++ ) {
			records.add(Record.read(in));
		}
		return new Record(name, directory, length, checksum, records);
	}
	
	// ----------------------
	
	private final String name;
	private final boolean directory;
	private final long length;
	private final String checksum;
	private final List<Record> records;

	public Record(String name, boolean directory, long length, String checksum, List<Record> records) {
		super();
		this.name = name;
		this.directory = directory;
		this.length = length;
		this.checksum = checksum;
		this.records = Collections.unmodifiableList(new ArrayList<Record>(records));
	}

	public String getName() {
		return name;
	}

	public boolean isDirectory() {
		return directory;
	}

	public long getLength() {
		return length;
	}

	public String getChecksum() {
		return checksum;
	}

	public List<Record> getRecords() {
		return records;
	}

	public void writeToFile(File file) throws IOException {
		DataOutputStream out = null;
		try {
			out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
			write(out);
		} finally {
			if( out != null ) {
				try {
					out.close();
				} catch (Exception ignored) {}
			}
		}
	}
	
	private void write(DataOutput out) throws IOException {
		out.writeUTF(name);
		out.writeBoolean(directory);
		out.writeLong(length);
		out.writeUTF(checksum);
		out.writeInt(records.size());
		for( Record r : records ) r.write(out);
	}
	
}
