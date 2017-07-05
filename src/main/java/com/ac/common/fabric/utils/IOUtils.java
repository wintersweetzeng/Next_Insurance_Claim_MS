package com.ac.common.fabric.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

public class IOUtils {

	public static File getFileFromClasspath(String filePath) {
		if (StringUtils.isBlank(filePath)) {
			return null;
		}

		InputStream stream = IOUtils.class.getResourceAsStream(filePath);

		File dest = new File(FilenameUtils.getName(filePath));
		try {
			FileUtils.copyInputStreamToFile(stream, dest);
			return dest;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

}
