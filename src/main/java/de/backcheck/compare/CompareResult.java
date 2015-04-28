package de.backcheck.compare;

public class CompareResult {

	private int pathCount = 0;
	private int diffCount = 0;

	
	public void incPathCount() {
		pathCount++;
	}

	public void incDiffCount() {
		diffCount++;
	}
	
	public int getPathCount() {
		return pathCount;
	}

	public int getDiffCount() {
		return diffCount;
	}

}
