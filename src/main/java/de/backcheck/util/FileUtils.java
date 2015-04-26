package de.backcheck.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class FileUtils {

	public static List<String> listAndSortFilenames(File dir) {
		ArrayList<String> names = new ArrayList<String>(Arrays.asList(dir.list()));
		Collections.sort(names);
		return names;
	}
	
}
