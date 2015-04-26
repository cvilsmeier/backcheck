package de.backcheck.record;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import de.backcheck.Checksummer;
import de.backcheck.Logger;
import de.backcheck.util.FileUtils;
import de.backcheck.util.HexUtils;

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

	public Record record(File root) {
		logger.info("recording " + root);
		return record(0, root, "");
	}

	private Record record(int depth, File root, String relPath) {
		Record record = null;
		File file = new File(root, relPath);
		logger.debug(file.toString());
		if (file.isDirectory()) {
			ArrayList<Record> records = new ArrayList<Record>();
			if (maxdepth < 0 || depth <= maxdepth) {
				for (String name : FileUtils.listAndSortFilenames(file)) {
					String subPath = relPath.isEmpty() ? name : relPath + "/" + name;
					Record subRecord = record(depth + 1, root, subPath);
					if( subRecord != null ) {
						records.add(subRecord);
					}
				}
			}
			record = new Record(file.getName(), true, 0, "", records);
		} else if (file.isFile()) {
			try {
				byte[] cs = checksummer.checksum(file);
				String checksum = HexUtils.bytesToHex(cs);
				record = new Record(file.getName(), false, file.length(), checksum, Collections.emptyList());
			} catch (Exception e) {
				logger.info("IO ERROR       " + file + ": " + e.getMessage());
			}
		}
		return record;
	}
}
