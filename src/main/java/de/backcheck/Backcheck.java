package de.backcheck;

import java.io.File;

public class Backcheck {

	public static final String VERSION_STRING = "1.0.0";

	public static void main(String[] args) throws Exception {
		int verbosity = 1;
		File logFile = null;
		int maxdepth = -1;
		File leftFile = null;
		File rightFile = null;
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-v") || args[i].equals("--verbose")) {
				verbosity++;
			} else if (args[i].equals("--logfile")) {
				logFile = new File(args[i+1]);
			} else if (args[i].equals("--maxdepth")) {
				maxdepth = Integer.parseInt(args[i+1]);
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
		if (leftFile == null || rightFile == null) {
			printUsage();
			System.exit(2);
		}

		Checksummer checksummer = new ChecksummerImpl();
		Logger logger = new Logger(verbosity, logFile);
		Comparer comparer = new Comparer(checksummer, logger, maxdepth);
		CompareResult compareResult = comparer.compare(leftFile, rightFile);
		logger.info(""+compareResult.getFileCount()+" files compared");
		logger.info(""+compareResult.getDiffCount()+" differences found");
		logger.close();
		System.exit(compareResult.getDiffCount()==0 ? 0 : 1);
	}


	private static void printUsage() {
		System.out.println("");
		System.out.println("Backcheck - a recursive file comparison tool");
		System.out.println("");
		System.out.println("USAGE");
		System.out.println("  java de.backcheck.Backcheck [OPTIONS] LEFT RIGHT");
		System.out.println("");
		System.out.println("  Backcheck compares a LEFT file with a RIGHT file.");
		System.out.println("  If LEFT and RIGHT are both directories, Backcheck compares");
		System.out.println("  the files in these directories, recursively.");
		System.out.println("  Backcheck compares files based on file length and file contents.");
		System.out.println("");
		System.out.println("OPTIONS");
		System.out.println("  -v --verbose");
		System.out.println("    Verbose output");
		System.out.println("");
		System.out.println("  --logfile <filename>");
		System.out.println("    Log to file (in addition to stdout)");
		System.out.println("");
		System.out.println("  --maxdepth <maxdepth>");
		System.out.println("    Descend only <maxdepth> levels deep into directory structure");
		System.out.println("");
		System.out.println("  --help");
		System.out.println("    Print this help page and exit");
		System.out.println("");
		System.out.println("  --version");
		System.out.println("    Print version and exit");
		System.out.println("");
	}

}
