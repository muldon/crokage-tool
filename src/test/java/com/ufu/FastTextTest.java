package com.ufu;

import java.util.List;
import java.util.Map;

import com.ufu.crokage.util.CrokageUtils;

public class FastTextTest {

	public static void main(String[] args) throws Exception {
		String words = "java filewriter file";
		
		/*String modelPath = CrokageStaticData.FAST_TEXT_MODEL_PATH;
		String fastTextIntallationDir = CrokageStaticData.FAST_TEXT_INSTALLATION_DIR;
		
		
		// echo "java filewriter file" | ./fasttext print-word-vectors
		String[] cmd = { "bash", "-c", "echo \""+words+"\" | " + fastTextIntallationDir+ "/fasttext print-word-vectors "+modelPath };

		ProcessBuilder pb = new ProcessBuilder(cmd);
		Process p = pb.start();
		p.waitFor();
		String output = CrokageUtils.loadStream(p.getInputStream());
		String error = CrokageUtils.loadStream(p.getErrorStream());
		int rc = p.waitFor();
		System.out.println(output);
*/
		CrokageUtils crokageUtils = new CrokageUtils();
		Map<String, List<Double>> vectors = crokageUtils.readVectorsForQuery(words);
		System.out.println(vectors);
	}
	
	
	
}
