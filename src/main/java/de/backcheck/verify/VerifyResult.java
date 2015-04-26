package de.backcheck.verify;

public class VerifyResult {

	private int diffCount = 0;
	
	public void incDiffCount() {
		diffCount++;
	}

	public int getDiffCount() {
		return diffCount;
	}
	
}
