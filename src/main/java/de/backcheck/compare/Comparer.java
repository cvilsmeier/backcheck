package de.backcheck.compare;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.backcheck.Checksummer;
import de.backcheck.Logger;
import de.backcheck.util.FileUtils;

public class Comparer {

	private final Checksummer checksummer;
	private final Logger logger;
	private final int maxdepth;
	private final ExecutorService executorService;
	
	public Comparer(Checksummer checksummer, Logger logger, int maxdepth) {
		super();
		this.checksummer = checksummer;
		this.logger = logger;
		this.maxdepth = maxdepth;
		this.executorService = Executors.newFixedThreadPool(2);
	}

	public CompareResult compare(File srcPath, File destPath) {
		CompareResult compareResult = new CompareResult();
		compare(0,srcPath, destPath,"", compareResult);
		return compareResult;
	}
		
	private void compare(int depth, File srcRoot, File destRoot, String relPath, CompareResult compareResult) {
		if( maxdepth>=0 && depth>maxdepth+1) return;
		File srcPath = new File(srcRoot, relPath);
		File destPath = new File(destRoot, relPath);
		logger.debug("" + srcPath);
		compareResult.incPathCount();
		if (srcPath.exists()) {
			if (destPath.exists()) {
				if (srcPath.isDirectory() && destPath.isDirectory()) {
					compareDirs(depth, srcRoot, destRoot, relPath, compareResult);
				} else if (srcPath.isFile() && destPath.isFile()) {
					compareFiles(srcPath, destPath, compareResult);
				} else {
					logger.info("DIFF TYPE      " + srcPath);
					compareResult.incDiffCount();
				}
			} else {
				logger.info("NOT FOUND      " + destPath);
				compareResult.incDiffCount();
			}
		} else {
			logger.info("NOT FOUND      " + srcPath);
		}
	}

	private void compareDirs(int depth, File srcRoot, File destRoot, String relPath, CompareResult compareResult) {
		File srcDir = new File(srcRoot, relPath);
		for (String name : FileUtils.listAndSortFilenames(srcDir)) {
			String relChildPath = relPath + "/" + name;
			compare(depth+1, srcRoot, destRoot, relChildPath, compareResult);
		}
	}

	private void compareFiles(File srcFile, File destFile, CompareResult compareResult) {
		if (!srcFile.exists()) {
			logger.info("NOT FOUND      " + srcFile);
			compareResult.incDiffCount();
		} else if (!destFile.exists()) {
			logger.info("NOT FOUND      " + destFile);
			compareResult.incDiffCount();
		} else if (srcFile.length() != destFile.length()) {
			logger.info("DIFF LENGTH    " + srcFile);
			compareResult.incDiffCount();
		} else {
			Future<String> srcFuture = executorService.submit(new ChecksumTask(checksummer, srcFile)); 
			Future<String> destFuture = executorService.submit(new ChecksumTask(checksummer, destFile)); 
			try {				
				String srcCs = srcFuture.get();
				String destCs = destFuture.get();
				if (! srcCs.equals(destCs)) {
					logger.info("DIFF CHECKSUM  " + srcFile);
					compareResult.incDiffCount();
				}
			} catch (Exception e) {
				logger.info("IO ERROR       (" + e.getMessage()+")");
			}
			
//			try {				
//				String srcCs = checksummer.checksum(srcFile);
//				try {
//					String destCs = checksummer.checksum(destFile);
//					if (! srcCs.equals(destCs)) {
//						logger.info("DIFF CHECKSUM  " + srcFile);
//						compareResult.incDiffCount();
//					}
//				} catch (Exception e) {
//					logger.info("IO ERROR       " + destFile + " (" + e.getMessage()+")");
//					compareResult.incDiffCount();
//				}
//			} catch (Exception e) {
//				logger.info("IO ERROR       " + srcFile + " (" + e.getMessage()+")");
//			}
		}
	}
}
