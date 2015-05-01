package de.backcheck;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class LoggerImpl implements Logger, Closeable {

	private final int verbosity;
	private final PrintStream outOrNull;

	public LoggerImpl(int verbosity, File logfileOrNull) {
		super();
		this.verbosity = verbosity;
		PrintStream out = null;
		if (logfileOrNull != null) {
			try {
				out = new PrintStream(new FileOutputStream(logfileOrNull, true), true, "UTF-8");
			} catch (IOException ignore) {}
		}
		this.outOrNull = out;
	}

	@Override
	public void error(String msg, Throwable t) {
		if (verbosity >= 0) {
			System.err.println(msg);
			t.printStackTrace(System.err);
			if (outOrNull != null) {
				outOrNull.println(msg);
				t.printStackTrace(outOrNull);
			}
		}
	}

	@Override
	public void info(String msg) {
		if (verbosity >= 1) {
			System.out.println(msg);
			if (outOrNull != null) {
				outOrNull.println(msg);
			}
		}
	}

	@Override
	public void debug(String msg) {
		if (verbosity >= 2) {
			System.out.println(msg);
		}
	}

	@Override
	public void close() {
		if( outOrNull != null ) {
			outOrNull.close();
		}
	}

}
