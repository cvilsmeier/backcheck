package de.backcheck;

public interface Logger {

	public void error(String msg, Throwable t);
	public void info(String msg);
	public void debug(String msg);
}
