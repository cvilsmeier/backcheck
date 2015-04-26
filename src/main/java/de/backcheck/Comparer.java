package de.backcheck;

import java.io.File;
import java.util.Arrays;

import de.backcheck.util.FileUtils;

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
					compareFiles(srcPath, destPath, resultBuilder);
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
		for (String name : FileUtils.listAndSortFilenames(srcDir)) {
			String relChildPath = relPath + "/" + name;
			compare(depth+1, srcRoot, destRoot, relChildPath, resultBuilder);
		}
	}

	private void compareFiles(File srcFile, File destFile, CompareResultBuilder resultBuilder) {
		resultBuilder.incFileCount();
		if (!srcFile.exists()) {
			logger.info("NOT FOUND      " + srcFile);
			resultBuilder.incDiffCount();
		} else if (!destFile.exists()) {
			logger.info("NOT FOUND      " + destFile);
			resultBuilder.incDiffCount();
		} else if (srcFile.length() != destFile.length()) {
			logger.info("DIFF LENGTH    " + srcFile + " " + srcFile.length() + " | " + destFile + " " + destFile.length());
			resultBuilder.incDiffCount();
		} else {
			try {
				byte[] srcCs = checksummer.checksum(srcFile);
				try {
					byte[] destCs = checksummer.checksum(destFile);
					if (! Arrays.equals(srcCs, destCs)) {
						logger.info("DIFF CHECKSUM  " + srcFile + " | " + destFile);
						resultBuilder.incDiffCount();
					}
				} catch (Exception e) {
					logger.info("IO ERROR       " + destFile + " " + e.getMessage());
					resultBuilder.incDiffCount();
				}
			} catch (Exception e) {
				logger.info("IO ERROR       " + srcFile + " " + e.getMessage());
			}
		}
	}
}
