package de.backcheck;

public class CompareResult {

	private final int fileCount;
	private final int diffCount;

	public CompareResult(int fileCount, int diffCount) {
		super();
		this.fileCount = fileCount;
		this.diffCount = diffCount;
	}

	public int getFileCount() {
		return fileCount;
	}

	public int getDiffCount() {
		return diffCount;
	}

}
