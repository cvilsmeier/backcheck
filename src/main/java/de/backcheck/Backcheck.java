package de.backcheck;

import java.io.File;

public class Backcheck {

	public static final String VERSION_STRING = "1.0.0";


	private static void printUsage() {
		System.out.println("");
		System.out.println("Backcheck - a recursive file comparison tool");
		System.out.println("");
		System.out.println("USAGE");
		System.out.println("  java de.backcheck.Backcheck [OPTIONS] SRC DEST");
		System.out.println("");
		System.out.println("  Backcheck compares a SRC file with a DEST file.");
		System.out.println("  If SRC and DEST are both directories, Backcheck compares");
		System.out.println("  the files in these directories, recursively.");
		System.out.println("  Backcheck compares files based on file length and file contents.");
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
		System.out.println("");
		System.out.println("  --exec <command>");
		System.out.println("    Execute <command> if SRC and DEST differ");
		System.out.println("");
		System.out.println("  --help");
		System.out.println("    Print this help page and exit");
		System.out.println("");
		System.out.println("  --version");
		System.out.println("    Print version and exit");
		System.out.println("");
	}

	
	public static void main(String[] args) throws Exception {
		int verbosity = 1;
		File logFile = null;
		int maxdepth = -1;
		String commandOrNull = null;
		File srcFile = null;
		File destFile = null;
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-v") || args[i].equals("--verbose")) {
				verbosity++;
			} else if (args[i].equals("--logfile")) {
				logFile = new File(args[i+1]);
			} else if (args[i].equals("--maxdepth")) {
				maxdepth = Integer.parseInt(args[i+1]);
			} else if (args[i].equals("--exec")) {
				commandOrNull = args[i+1];
			} else if (args[i].equals("--help")) {
				printUsage();
				System.exit(0);
			} else if (args[i].equals("--version")) {
				System.out.println(VERSION_STRING);
				System.exit(0);
			} else if (i == args.length - 2) {
				srcFile = new File(args[i]);
			} else if (i == args.length - 1) {
				destFile = new File(args[i]);
			}
		}
		if (srcFile == null || destFile == null) {
			printUsage();
			System.exit(2);
		}

		ChecksummerImpl checksummer = new ChecksummerImpl();
		LoggerImpl logger = new LoggerImpl(verbosity, logFile);
		CommandExecutor commandExecutor = new CommandExecutor(commandOrNull);
		Backcheck backcheck = new Backcheck();
		int exitCode = backcheck.execute(checksummer, logger, maxdepth, commandExecutor, srcFile, destFile);
		logger.close();
		System.exit(exitCode);
	}


	public int execute(Checksummer checksummer, Logger logger, int maxdepth, CommandExecutor commandExecutor, File srcFile, File destFile) {
		Comparer comparer = new Comparer(checksummer, logger, maxdepth);
		CompareResult compareResult = comparer.compare(srcFile, destFile);
		logger.info("finished "+srcFile+" | "+destFile);
		logger.info("  "+compareResult.getFileCount()+" files compared");
		logger.info("  "+compareResult.getDiffCount()+" differences found");
		int exitCode = 0;
		if( compareResult.getDiffCount() > 0 ) {
			exitCode = 1;
			commandExecutor.execute();
		}
		return exitCode;
	}


}
