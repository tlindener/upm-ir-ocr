package es.upm.ir.ocr;

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.io.filefilter.WildcardFileFilter;

public class FileUtils {

	public static File[] getAllFilesTifFiles(String path) {

		File dir = new File(path);
		FileFilter fileFilter = new WildcardFileFilter("*.tif");
		File[] files = dir.listFiles(fileFilter);
		return files;

	}

}
