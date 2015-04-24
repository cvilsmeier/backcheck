package de.backcheck;

import java.io.File;
import java.util.Arrays;

public class Backcheck {

	public static void main(String[] args) {
		try {
			new Backcheck(args);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	public static final String VERSION_STRING = "1.0.0";
	public final Checksummer checksummer;
	public final Logger logger;

	public Backcheck(String[] args) throws Exception {
		this.checksummer = new Checksummer();
		int verbosity = 1;
		File leftFile = null;
		File rightFile = null;
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-v") || args[i].equals("--verbose")) {
				verbosity++;
			} else if (args[i].equals("--help")) {
				printUsage();
				System.exit(0);
			} else if (args[i].equals("--version")) {
				System.out.println(VERSION_STRING);
				System.exit(0);
			} else if (i == args.length - 2) {
				leftFile = new File(args[i]);
			} else if (i == args.length - 1) {
				rightFile = new File(args[i]);
			}
		}
		this.logger = new Logger(verbosity);
		if (leftFile == null || rightFile == null) {
			printUsage();
			System.exit(2);
		}

		boolean allEqual = compare(0, leftFile, rightFile, "");
		if( allEqual ) {
			logger.info("no differences found");
		}
		System.exit(allEqual ? 0 : 1);
	}

	private boolean compare(int depth, File leftRoot, File rightRoot, String relPath) {
		boolean allEqual = true;
		File leftFile = new File(leftRoot, relPath);
		File rightFile = new File(rightRoot, relPath);
		logger.debug("" + leftFile);
		if (leftFile.exists()) {
			if (rightFile.exists()) {
				if (leftFile.isDirectory() && rightFile.isDirectory()) {
					allEqual &= compareDirs(depth, leftRoot, rightRoot, relPath);
				} else if (leftFile.isFile() && rightFile.isFile()) {
					allEqual &= compareFiles(leftRoot, rightRoot, relPath);
				} else {
					logger.info("DIFF TYPE      " + leftFile + " | " + rightFile);
				}
			} else {
				logger.info("NOT FOUND      " + rightFile);
			}
		} else {
			logger.info("NOT FOUND      " + leftFile);
		}
		return allEqual;
	}

	private boolean compareDirs(int depth, File leftRoot, File rightRoot, String relPath) {
		boolean allEqual = true;
		File leftDir = new File(leftRoot, relPath);
		for (String name : leftDir.list()) {
			String childPath = relPath + "/" + name;
			allEqual &= compare(depth + 1, leftRoot, rightRoot, childPath);
		}
		return allEqual;
	}

	private boolean compareFiles(File leftRoot, File rightRoot, String relPath) {
		boolean filesEqual = false;
		File leftFile = new File(leftRoot, relPath);
		File rightFile = new File(rightRoot, relPath);
		if (!leftFile.exists()) {
			logger.info("NOT FOUND      " + leftFile);
		} else if (!rightFile.exists()) {
			logger.info("NOT FOUND      " + rightFile);
		} else if (leftFile.length() != rightFile.length()) {
			logger.info("DIFF LENGTH    " + leftFile + " " + leftFile.length() + " | " + rightFile + " " + rightFile.length());
		} else {
			try {
				byte[] leftCs = checksummer.checksum(leftFile);
				try {
					byte[] rightCs = checksummer.checksum(rightFile);
					if (Arrays.equals(leftCs, rightCs)) {
						filesEqual = true;
					} else {
						logger.info("DIFF CHECKSUM  " + leftFile + " | " + rightFile);
					}
				} catch (Exception e) {
					logger.info("IO ERROR       " + rightFile + " " + e.getMessage());
				}
			} catch (Exception e) {
				logger.info("IO ERROR       " + leftFile + " " + e.getMessage());
			}
		}
		return filesEqual;
	}

	private void printUsage() {
		System.out.println("NAME");
		System.out.println("  Backcheck - a recursive file comparison tool");
		System.out.println("");
		System.out.println("USAGE");
		System.out.println("  java de.backcheck.Backcheck [OPTIONS] LEFT RIGHT");
		System.out.println("");
		System.out.println("  Backcheck compares a LEFT file with a RIGHT file.");
		System.out.println("  If LEFT and RIGHT are both directories, Backcheck compares");
		System.out.println("  the files in these directories.");
		System.out.println("  Backcheck compares files based on file length and file contents.");
		System.out.println("");
		System.out.println("OPTIONS");
		System.out.println("  -v            Verbose output");
		System.out.println("  --verbose");
		System.out.println("");
		System.out.println("  --help        Print this help page and exit");
		System.out.println("");
		System.out.println("  --version     Print version and exit");
		System.out.println("");
	}

}
