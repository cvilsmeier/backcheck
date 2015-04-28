package de.backcheck.verify;

import java.io.File;
import java.io.IOException;

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
	
	public VerifyResult verify(Record rootRecord, File destRoot) {
		VerifyResult verifyResult = new VerifyResult();
		verify(rootRecord, destRoot, "", verifyResult);
		return verifyResult;
	}
	
	private void verify(Record record, File destRoot, String relPath, VerifyResult verifyResult) {
		File destPath = relPath.isEmpty() ? destRoot : new File(destRoot, relPath);
		logger.debug(destPath.toString());
		verifyResult.incRecordCount();
		if( destPath.exists() ) {
			if( record.isDirectory() ) {
				if( destPath.isDirectory() ) {
					for( Record r : record.getRecords() ) {
						verify(r, destRoot, relPath.isEmpty() ? r.getName() : relPath+"/"+r.getName(), verifyResult);
					}
				} else {
					logger.info("DIFF TYPE      "+destPath+" (expected directory but was file)");
					verifyResult.incDiffCount();
				}
			} else {
				if( destPath.isFile() ) {
					if( record.getLength() == destPath.length() ) {
						try {
							String destChecksum = checksummer.checksum(destPath);
							if( ! record.getChecksum().equals(destChecksum) ) {
								logger.info("DIFF CHECKSUM  "+destPath);
								verifyResult.incDiffCount();
							}
						} catch (IOException e) {
							logger.info("IO ERROR       "+destPath);
							verifyResult.incDiffCount();
						}
					} else {
						logger.info("DIFF LENGTH    "+destPath);
						verifyResult.incDiffCount();
					}
				} else {
					logger.info("DIFF TYPE      "+destPath+" (expected file but found a directory)");
					verifyResult.incDiffCount();
				}
			}
		} else {
			logger.info("NOT FOUND      "+destPath);
			verifyResult.incDiffCount();
		}
	}

}
