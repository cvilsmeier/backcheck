package de.backcheck.record;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class RecorderResult {
	
	public static RecorderResult readFromFile(File file) throws IOException {
		BufferedReader in= null;
		try {
			RecorderResult rr = new RecorderResult();
			in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			String line;
			while( (line=in.readLine()) != null ) {
				Record record = Record.fromString(line);
				rr.addRecord(record);
			}
			return rr;
		} finally {
			if( in != null ) {
				try {
					in.close();
				} catch (Exception ignored) {}
			}
		}
	}

	
	private List<Record> records = new ArrayList<Record>();

	public void addRecord(Record record) {
		records.add(record);
	}

	public List<Record> getRecords() {
		return records;
	}
	

	public void writeToFile(File file) throws IOException {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
			for( Record r : records ) {
				out.write(r.toString());
				out.write("\r\n");
			}
		} finally {
			if( out != null ) {
				try {
					out.close();
				} catch (Exception ignored) {}
			}
		}
	}


}
