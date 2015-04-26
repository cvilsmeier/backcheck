package de.backcheck.compare;

public class CompareResultBuilder {

	private int fileCount = 0;
	private int diffCount = 0;

	public void incFileCount() {
		fileCount++;
	}

	public void incDiffCount() {
		diffCount++;
	}

	public CompareResult build() {
		return new CompareResult(fileCount, diffCount);
	}

}
