package de.backcheck;

public class Logger {

	private final int verbosity;

	public Logger(int verbosity) {
		super();
		this.verbosity = verbosity;
	}

	public void info(String msg) {
		if (verbosity >= 1) {
			System.out.println(msg);
		}
	}

	public void debug(String msg) {
		if (verbosity >= 2) {
			System.out.println(msg);
		}
	}
}
