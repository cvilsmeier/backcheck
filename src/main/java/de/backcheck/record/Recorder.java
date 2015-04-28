package de.backcheck.record;

import java.io.File;

import de.backcheck.Checksummer;
import de.backcheck.Logger;
import de.backcheck.util.FileUtils;

public class Recorder {

	private final Checksummer checksummer;
	private final Logger logger;
	private final int maxdepth;

	public Recorder(Checksummer checksummer, Logger logger, int maxdepth) {
		super();
		this.checksummer = checksummer;
		this.logger = logger;
		this.maxdepth = maxdepth;
	}

	public RecorderResult record(File root) {
		RecorderResult recorderResult = new RecorderResult();
		record(0, root, "", recorderResult);
		return recorderResult;
	}

	private void record(int depth, File root, String relPath, RecorderResult recorderResult) {
		File file = new File(root, relPath);
		logger.debug(file.toString());
		if (file.isDirectory()) {
			if (maxdepth < 0 || depth <= maxdepth) {
				for (String name : FileUtils.listAndSortFilenames(file)) {
					String subPath = relPath.isEmpty() ? name : relPath + "/" + name;
					record(depth + 1, root, subPath, recorderResult);
				}
			}
		} else if (file.isFile()) {
			try {
				String checksum = checksummer.checksum(file);
				Record record = new Record(relPath, file.length(), checksum);
				recorderResult.addRecord(record);
			} catch (Exception e) {
				logger.info("IO ERROR       " + file + " (" + e.getMessage() + ")");
			}
		}
	}
}
