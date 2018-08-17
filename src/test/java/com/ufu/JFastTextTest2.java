package com.ufu;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.github.jfasttext.JFastText;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JFastTextTest2 {
	//na pasta ~/Dropbox/Doutorado/Projects/bot/src/test/resources/data rodar comando: 
	//cat queries.txt | java -jar ~/Projects/JFastText/target/jfasttext-0.4-SNAPSHOT-jar-with-dependencies.jar print-word-vectors ../models/cbow.model.bin > queries2.txt
	//ou
	//java -jar ~/Projects/JFastText/target/jfasttext-0.4-SNAPSHOT-jar-with-dependencies.jar print-word-vectors ../models/cbow.model.bin < queries.txt > queries3.txt
   
    //@Test
    public void test03TrainCbowCmd() {
        System.out.printf("\nTraining cbow word-embedding ...\n");
        JFastText jft = new JFastText();
        jft.runCmd(new String[] {
                "cbow",
                "-input", "src/test/resources/data/unlabeled_data.txt",
                "-output", "src/test/resources/models/cbow.model",
                "-bucket", "100",
                "-minCount", "1",
                "-wordNgrams", "3",
                "-epoch", "0.5"
        });
    }

    @Test
    public void testPrintWordVectors() {
        System.out.printf("\nPrintWordVectors ...\n");
        JFastText jft = new JFastText();
        jft.runCmd(new String[] {
                "print-word-vectors",
                "src/test/resources/models/cbow.model.bin",
                "<",
                "src/test/resources/data/queries.txt"
        });
    }

}
