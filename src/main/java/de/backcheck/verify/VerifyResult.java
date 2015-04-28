package de.backcheck.verify;

public class VerifyResult {

	private int fileCount = 0;
	private int diffCount = 0;
	
	public void incFileCount() {
		fileCount++;
	}
	
	public void incDiffCount() {
		diffCount++;
	}

	public int getFileCount() {
		return fileCount;
	}

	public int getDiffCount() {
		return diffCount;
	}
	
}
