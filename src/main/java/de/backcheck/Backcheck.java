package de.backcheck;

import java.io.File;

import de.backcheck.compare.CompareResult;
import de.backcheck.compare.Comparer;
import de.backcheck.record.Recorder;
import de.backcheck.record.RecorderResult;
import de.backcheck.verify.Verifier;
import de.backcheck.verify.VerifyResult;

public class Backcheck {

	public static final String VERSION_STRING = "1.0.0";

	public static void main(String[] args) {
		try {
			if (args.length == 0) {
				printUsage();
				System.exit(2);
			}
			String operation = "";
			int verbosity = 1;
			File logFile = null;
			int maxdepth = -1;
			File srcFile = null;
			File destFile = null;
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("-v") || args[i].equals("--verbose")) {
					verbosity++;
				} else if (args[i].equals("--logfile")) {
					logFile = new File(args[i + 1]);
				} else if (args[i].equals("--maxdepth")) {
					maxdepth = Integer.parseInt(args[i + 1]);
				} else if (args[i].equals("--help")) {
					printUsage();
					System.exit(0);
				} else if (args[i].equals("--version")) {
					System.out.println(VERSION_STRING);
					System.exit(0);
				} else if (i == 0) {
					operation = args[i];
				} else if (i == args.length - 2) {
					srcFile = new File(args[i]);
				} else if (i == args.length - 1) {
					destFile = new File(args[i]);
				}
			}
			if (srcFile == null) {
				System.err.println("SRC not specified");
				printUsage();
				System.exit(2);
			}
			if (destFile == null) {
				System.err.println("DEST not specified");
				printUsage();
				System.exit(2);
			}

			ChecksummerImpl checksummer = new ChecksummerImpl();
			LoggerImpl logger = new LoggerImpl(verbosity, logFile);
			int exitCode = 0;
			if (operation.equals("compare")) {
				exitCode = executeCompare(checksummer, logger, maxdepth, srcFile, destFile);
			} else if (operation.equals("record")) {
				exitCode = executeRecord(checksummer, logger, maxdepth, srcFile, destFile);
			} else if (operation.equals("verify")) {
				exitCode = executeVerify(checksummer, logger, srcFile, destFile);
			} else {
				System.err.println("unknown operation '" + operation + "', must be 'compare' or 'record' or 'verify'");
				printUsage();
				exitCode = 2;
			}
			logger.close();
			System.exit(exitCode);
		} catch (Throwable t) {
			t.printStackTrace(System.err);
			System.exit(2);
		}
	}

	public static int executeCompare(Checksummer checksummer, Logger logger, int maxdepth, File srcPath, File destPath) {
		logger.info("comparing " + srcPath + " -> " + destPath);
		Comparer comparer = new Comparer(checksummer, logger, maxdepth);
		CompareResult compareResult = comparer.compare(srcPath, destPath);
		logger.info("  " + compareResult.getPathCount() + " files compared");
		logger.info("  " + compareResult.getDiffCount() + " differences found");
		int exitCode = 0;
		if (compareResult.getDiffCount() > 0) {
			exitCode = 1;
		}
		return exitCode;
	}

	public static int executeRecord(Checksummer checksummer, Logger logger, int maxdepth, File srcPath, File destFile) {
		logger.info("recording " + srcPath + " -> " + destFile);
		Recorder recorder = new Recorder(checksummer, logger, maxdepth);
		RecorderResult recorderResult = recorder.record(srcPath);
		logger.info("  " + recorderResult.getRecords().size() + " files recorded");
		int exitCode = 0;
		try {
			recorderResult.writeToFile(destFile);
		} catch (Exception e) {
			System.err.println("cannot write " + destFile + " (" + e.getMessage() + ")");
			exitCode = 2;
		}
		return exitCode;
	}

	public static int executeVerify(Checksummer checksummer, Logger logger, File srcFile, File destPath) {
		logger.info("verifying " + srcFile + " -> " + destPath);
		int exitCode = 0;
		try {
			RecorderResult recorderResult = RecorderResult.readFromFile(srcFile);
			Verifier verifier = new Verifier(checksummer, logger);
			VerifyResult verifyResult = verifier.verify(destPath, recorderResult.getRecords());
			logger.info("  " + verifyResult.getFileCount() + " files verified");
			logger.info("  " + verifyResult.getDiffCount() + " differences found");
			if (verifyResult.getDiffCount() > 0) {
				exitCode = 1;
			}
		} catch (Exception e) {
			System.err.println("verify error (" + e.getMessage()+")");
			exitCode = 2;
		}
		return exitCode;
	}

	private static void printUsage() {
		System.out.println("");
		System.out.println("Backcheck - a recursive file comparison and verification tool");
		System.out.println("");
		System.out.println("SYNOPSIS");
		System.out.println("");
		System.out.println("  java de.backcheck.Backcheck compare [OPTIONS] SRC DEST");
		System.out.println("  java de.backcheck.Backcheck record  [OPTIONS] SRC DEST");
		System.out.println("  java de.backcheck.Backcheck verify  [OPTIONS] SRC DEST");
		System.out.println("");
		System.out.println("DESCRIPTION");
		System.out.println("");
		System.out.println("  If started with 'compare', Backcheck compares a SRC file with a DEST file.");
		System.out.println("  If SRC and DEST are both directories, Backcheck compares");
		System.out.println("  the files in these directories, recursively.");
		System.out.println("");
		System.out.println("  If started with 'record', Backcheck traverses SRC, calculates");
		System.out.println("  each file's length and checksum, and writes them to DEST file.");
		System.out.println("");
		System.out.println("  If started with 'verify', Backcheck loads lengths and checksums");
		System.out.println("  from SRC (a file created with 'record') and compares them");
		System.out.println("  with DEST, recursively.");
		System.out.println("");
		System.out.println("  The record/verify mode can be used for taking a snapshot of an");
		System.out.println("  existing directory structure and later verify that the files in");
		System.out.println("  that directory structure did not change.");
		System.out.println("  You may find it useful for checking read-only archives.");
		System.out.println("");
		System.out.println("  Backcheck returns the following exit codes:");
		System.out.println("    0  Success/No differences found");
		System.out.println("    1  One or more differences found");
		System.out.println("    2  Other error");
		System.out.println("");
		System.out.println("OPTIONS");
		System.out.println("  -v --verbose");
		System.out.println("    Verbose output");
		System.out.println("");
		System.out.println("  --logfile <filename>");
		System.out.println("    Append log output to <filename>, in addition to stdout");
		System.out.println("");
		System.out.println("  --maxdepth <maxdepth>");
		System.out.println("    Descend only <maxdepth> levels deep into directory structure");
		System.out.println("    (for 'compare' and 'record')");
		System.out.println("");
		System.out.println("  --help");
		System.out.println("    Print this help page and exit");
		System.out.println("");
		System.out.println("  --version");
		System.out.println("    Print version and exit");
		System.out.println("");
		System.out.println("EXAMPLES");
		System.out.println("");
		System.out.println("  java de.backcheck.Backcheck compare /foobar /backups/foobar");
		System.out.println("    Compares /foobar with /backups/foobar, recursively, and reports");
		System.out.println("    each difference");
		System.out.println("");
		System.out.println("  java de.backcheck.Backcheck record /backups/archive /home/joe/digest");
		System.out.println("    Records the checksum of each file in /backups/archive and writes the");
		System.out.println("    results to /home/joe/digest");
		System.out.println("");
		System.out.println("  java de.backcheck.Backcheck verify /home/joe/digest /backups/archive");
		System.out.println("    Verifies for each entry in /home/joe/digest that the same file");
		System.out.println("    exists in /backups/archive and has the same checksum");
		System.out.println("");
	}

}
