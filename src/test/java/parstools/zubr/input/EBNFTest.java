package parstools.zubr.input;

import org.junit.jupiter.api.Test;

public class EBNFTest {
    @Test
    void convertToLeft() {
        //loops changes to left recursion, suitable to LR parsers
        String[][] testCases = {
                {"A->a?", "A->a", "A->"},
                {"A->a+", "A->Aa", "A->a"},
                {"A->a*", "A->Aa", "A->"},
                {"A->ab+c", "A->aBc","B->Bb","B->b"},
                {"A->a(Bc)*d?", "A->aCD","C->CBc","C->","D->d","D->"}
        };
        for (String[] testCase : testCases) {
            String input = testCase[0];
            EBNF ebnf=new EBNF(false);
            ebnf.add(input);
            ebnf.convert();
            String[] actual = ebnf.toLines();
            for (int j = 1; j < testCase.length; j++) {
                System.out.println("Element " + j + ": " + testCase[j]);
            }
        }
    }

    @Test
    void convertToRight() {
        //loops changes to right recursion, suitable to LL parsers
        String[][] testCases = {
                {"A->a?", "A->a", "A->"},
                {"A->a+", "A->Aa", "A->a"},
                {"A->a*", "A->Aa", "A->"},
                {"A->ab+c", "A->aBc","B->Bb","B->b"},
                {"A->a(Bc)*d?", "A->aCD","C->CBc","C->","D->d","D->"}
        };
        for (String[] testCase : testCases) {
            String input = testCase[0];
            EBNF ebnf=new EBNF(true);
            ebnf.add(input);
            ebnf.convert();
            String[] actual = ebnf.toLines();
            for (int j = 1; j < testCase.length; j++) {
                System.out.println("Element " + j + ": " + testCase[j]);
            }
        }
    }
}
