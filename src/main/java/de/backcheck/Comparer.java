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

	public CompareResult compare(File src, File dest) {
		logger.info("comparing "+src+" | "+dest);
		CompareResultBuilder resultBuilder = new CompareResultBuilder();
		compare(0,src, dest,"", resultBuilder);
		return resultBuilder.build();
	}
		
	private void compare(int depth, File srcRoot, File destRoot, String relPath, CompareResultBuilder resultBuilder) {
		if( maxdepth>=0 && depth>maxdepth+1) return;
		File srcPath = new File(srcRoot, relPath);
		File destPath = new File(destRoot, relPath);
		logger.debug("" + srcPath);
		if (srcPath.exists()) {
			if (destPath.exists()) {
				if (srcPath.isDirectory() && destPath.isDirectory()) {
					compareDirs(depth, srcRoot, destRoot, relPath, resultBuilder);
				} else if (srcPath.isFile() && destPath.isFile()) {
					compareFiles(srcRoot, destRoot, relPath, resultBuilder);
				} else {
					logger.info("DIFF TYPE      " + srcPath + " | " + destPath);
					resultBuilder.incDiffCount();
				}
			} else {
				logger.info("NOT FOUND      " + destPath);
				resultBuilder.incDiffCount();
			}
		} else {
			logger.info("NOT FOUND      " + srcPath);
		}
	}

	private void compareDirs(int depth, File srcRoot, File destRoot, String relPath, CompareResultBuilder resultBuilder) {
		File srcDir = new File(srcRoot, relPath);
		for (String name : srcDir.list()) {
			String relChildPath = relPath + "/" + name;
			compare(depth+1, srcRoot, destRoot, relChildPath, resultBuilder);
		}
	}

	private void compareFiles(File srcRoot, File destRoot, String relPath, CompareResultBuilder resultBuilder) {
		File srcPath = new File(srcRoot, relPath);
		File destPath = new File(destRoot, relPath);
		resultBuilder.incFileCount();
		if (!srcPath.exists()) {
			logger.info("NOT FOUND      " + srcPath);
			resultBuilder.incDiffCount();
		} else if (!destPath.exists()) {
			logger.info("NOT FOUND      " + destPath);
			resultBuilder.incDiffCount();
		} else if (srcPath.length() != destPath.length()) {
			logger.info("DIFF LENGTH    " + srcPath + " " + srcPath.length() + " | " + destPath + " " + destPath.length());
			resultBuilder.incDiffCount();
		} else {
			try {
				byte[] srcCs = checksummer.checksum(srcPath);
				try {
					byte[] destCs = checksummer.checksum(destPath);
					if (! Arrays.equals(srcCs, destCs)) {
						logger.info("DIFF CHECKSUM  " + srcPath + " | " + destPath);
						resultBuilder.incDiffCount();
					}
				} catch (Exception e) {
					logger.info("IO ERROR       " + destPath + " " + e.getMessage());
					resultBuilder.incDiffCount();
				}
			} catch (Exception e) {
				logger.info("IO ERROR       " + srcPath + " " + e.getMessage());
			}
		}
	}
}
