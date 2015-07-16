package org.n52.sos.service.it.statistics;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

public class ExampleReaderUtil {
	
	public static String readExample(String requestFile) throws URISyntaxException, IOException {
		String path = "/examples/sos/"+requestFile;
		URL resource = ExampleReaderUtil.class.getResource(path);
		return FileUtils.readFileToString(new File(resource.toURI()));
	}

}
