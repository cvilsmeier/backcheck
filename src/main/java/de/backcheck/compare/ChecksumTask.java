package de.backcheck.compare;

import java.io.File;
import java.util.concurrent.Callable;

import de.backcheck.Checksummer;

public class ChecksumTask implements Callable<String> {

	private final Checksummer checksummer;
	private final File file;
	
	public ChecksumTask(Checksummer checksummer, File file) {
		super();
		this.checksummer = checksummer;
		this.file = file;
	}

	@Override
	public String call() throws Exception {
		String checksum = checksummer.checksum(file);
		return checksum;
	}

}
