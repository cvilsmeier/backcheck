package de.backcheck.verify;

public class VerifyResult {

	private int recordCount = 0;
	private int diffCount = 0;
	
	public void incRecordCount() {
		recordCount++;
	}
	
	public void incDiffCount() {
		diffCount++;
	}

	public int getRecordCount() {
		return recordCount;
	}

	public int getDiffCount() {
		return diffCount;
	}
	
}
