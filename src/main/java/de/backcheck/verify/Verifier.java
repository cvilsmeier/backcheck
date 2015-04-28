package de.backcheck.verify;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.backcheck.Checksummer;
import de.backcheck.Logger;
import de.backcheck.record.Record;

public class Verifier {

	private final Checksummer checksummer;
	private final Logger logger;
	
	public Verifier(Checksummer checksummer, Logger logger) {
		super();
		this.checksummer = checksummer;
		this.logger = logger;
	}
	
	public VerifyResult verify(File destRoot, List<Record> records) {
		VerifyResult verifyResult = new VerifyResult();
		for( Record record : records ) {
			File destFile = record.getRelPath().isEmpty() ? destRoot : new File(destRoot, record.getRelPath());
			logger.debug(destFile.toString());
			verifyResult.incFileCount();
			if( destFile.isFile() ) {
				if( record.getLength() == destFile.length() ) {
					try {
						String destChecksum = checksummer.checksum(destFile);
						if( ! record.getChecksum().equals(destChecksum) ) {
							logger.info("DIFF CHECKSUM  "+destFile);
							verifyResult.incDiffCount();
						}
					} catch (IOException e) {
						logger.info("IO ERROR       "+destFile);
						verifyResult.incDiffCount();
					}
				} else {
					logger.info("DIFF LENGTH    "+destFile);
					verifyResult.incDiffCount();
				}
			} else {
				logger.info("NOT FOUND      "+destFile);
				verifyResult.incDiffCount();
			}
		}
		return verifyResult;
	}
	
}
