package de.backcheck;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Logger implements Closeable {

	private final int verbosity;
	private final OutputStreamWriter outOrNull;
	private final String lineSeparator;

	public Logger(int verbosity, File logfileOrNull) {
		super();
		this.verbosity = verbosity;
		OutputStreamWriter out = null;
		if (logfileOrNull != null) {
			try {
				out = new OutputStreamWriter(new FileOutputStream(logfileOrNull, true), "UTF-8");
			} catch (IOException ignore) {}
		}
		this.outOrNull = out;
		this.lineSeparator = System.getProperty("line.separator");
	}

	public void info(String msg) {
		if (verbosity >= 1) {
			System.out.println(msg);
			if (outOrNull != null) {
				try {
					outOrNull.write(msg);
					outOrNull.write(lineSeparator);
					outOrNull.flush();
				} catch (IOException ignore) {}
			}
		}
	}

	public void debug(String msg) {
		if (verbosity >= 2) {
			System.out.println(msg);
		}
	}

	@Override
	public void close() {
		if( outOrNull != null ) {
			try {
				outOrNull.close();
			} catch (IOException ignored) {}
		}
	}

}
