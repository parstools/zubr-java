package parstools.zubr.input;

import org.junit.jupiter.api.Test;

public class EBNFTest {
    @Test
    void convert() {
        String[][] testCases = {
                {"inpu0", "output0", "output1"},
                {"inpu1"},
                {"inpu2", "output2"}
        };
        for (String[] testCase : testCases) {
            String input = testCase[0];
            for (int j = 1; j < testCase.length; j++) {
                System.out.println("Element " + j + ": " + testCase[j]);
            }
        }
    }
}
