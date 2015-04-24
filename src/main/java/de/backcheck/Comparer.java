package de.backcheck;

import java.io.File;
import java.util.Arrays;

public class Comparer {

	private final Checksummer checksummer;
	private final Logger logger;
	private final int maxdepth;
	
	public Comparer(Checksummer checksummer, Logger logger, int maxdepth) {
		super();
		this.checksummer = checksummer;
		this.logger = logger;
		this.maxdepth = maxdepth;
	}

	public CompareResult compare(File leftFile, File rightFile) {
		logger.info("comparing "+leftFile+" | "+rightFile);
		CompareResultBuilder resultBuilder = new CompareResultBuilder();
		compare(0,leftFile, rightFile,"", resultBuilder);
		return resultBuilder.build();
	}
		
	private void compare(int depth, File leftRoot, File rightRoot, String relPath, CompareResultBuilder resultBuilder) {
		if( maxdepth>=0 && depth>maxdepth+1) return;
		File leftFile = new File(leftRoot, relPath);
		File rightFile = new File(rightRoot, relPath);
		logger.debug("" + leftFile);
		if (leftFile.exists()) {
			if (rightFile.exists()) {
				if (leftFile.isDirectory() && rightFile.isDirectory()) {
					compareDirs(depth, leftRoot, rightRoot, relPath, resultBuilder);
				} else if (leftFile.isFile() && rightFile.isFile()) {
					compareFiles(leftRoot, rightRoot, relPath, resultBuilder);
				} else {
					logger.info("DIFF TYPE      " + leftFile + " | " + rightFile);
					resultBuilder.incDiffCount();
				}
			} else {
				logger.info("NOT FOUND      " + rightFile);
				resultBuilder.incDiffCount();
			}
		} else {
			logger.info("NOT FOUND      " + leftFile);
		}
	}

	private void compareDirs(int depth, File leftRoot, File rightRoot, String relPath, CompareResultBuilder resultBuilder) {
		File leftDir = new File(leftRoot, relPath);
		for (String name : leftDir.list()) {
			String childPath = relPath + "/" + name;
			compare(depth+1, leftRoot, rightRoot, childPath, resultBuilder);
		}
	}

	private void compareFiles(File leftRoot, File rightRoot, String relPath, CompareResultBuilder resultBuilder) {
		File leftFile = new File(leftRoot, relPath);
		File rightFile = new File(rightRoot, relPath);
		resultBuilder.incFileCount();
		if (!leftFile.exists()) {
			logger.info("NOT FOUND      " + leftFile);
			resultBuilder.incDiffCount();
		} else if (!rightFile.exists()) {
			logger.info("NOT FOUND      " + rightFile);
			resultBuilder.incDiffCount();
		} else if (leftFile.length() != rightFile.length()) {
			logger.info("DIFF LENGTH    " + leftFile + " " + leftFile.length() + " | " + rightFile + " " + rightFile.length());
			resultBuilder.incDiffCount();
		} else {
			try {
				byte[] leftCs = checksummer.checksum(leftFile);
				try {
					byte[] rightCs = checksummer.checksum(rightFile);
					if (! Arrays.equals(leftCs, rightCs)) {
						logger.info("DIFF CHECKSUM  " + leftFile + " | " + rightFile);
						resultBuilder.incDiffCount();
					}
				} catch (Exception e) {
					logger.info("IO ERROR       " + rightFile + " " + e.getMessage());
					resultBuilder.incDiffCount();
				}
			} catch (Exception e) {
				logger.info("IO ERROR       " + leftFile + " " + e.getMessage());
			}
		}
	}
}
