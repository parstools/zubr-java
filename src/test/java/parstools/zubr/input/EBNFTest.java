package parstools.zubr.input;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EBNFTest {

    String[] fromSecondElement(String[] originalArray) {
        String[] newArray = new String[originalArray.length - 1];
        for (int i = 1; i < originalArray.length; i++) {
            newArray[i - 1] = originalArray[i];
        }
        return newArray;
    }

    @Test
    void convertToLR() throws RuntimeException {
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
            String[] expected = fromSecondElement(testCase);
            String[] actual = ebnf.toLines();
            assertEquals(expected.length, actual.length);
            for (int i= 0; i<expected.length; i++)
                assertEquals(expected[i], actual[i]);
        }
    }

    @Test
    void convertToLL() throws RuntimeException {
        //loops changes to right recursion, suitable to LL parsers
        String[][] testCases = {
                {"A->a?", "A->a", "A->"},
                {"A->a+", "A->aA", "A->a"},
                {"A->a*", "A->aA", "A->"},
                {"A->ab+c", "A->aBc","B->bB","B->b"},
                {"A->a(Bc)*d?", "A->aCD","C->BcC","C->","D->d","D->"}
        };
        for (String[] testCase : testCases) {
            String input = testCase[0];
            EBNF ebnf=new EBNF(true);
            ebnf.add(input);
            ebnf.convert();
            String[] expected = fromSecondElement(testCase);
            String[] actual = ebnf.toLines();
            assertEquals(expected.length, actual.length);
            for (int i= 0; i<expected.length; i++)
                assertEquals(expected[i], actual[i]);
        }
    }

    @Test
    void twoLines() throws RuntimeException {
        String[] input = {
                "A->Ba*",
                "A->b+",
        };
        String[] expected = {
                "A->BC",
                "B->bB",
                "B->b",
                "C->aC",
                "C->",
        };
        EBNF ebnf=new EBNF(true);
        for (String line: input)
            ebnf.add(line);
        ebnf.convert();
        String[] actual = ebnf.toLines();
        assertEquals(expected.length, actual.length);
        for (int i= 0; i<expected.length; i++)
            assertEquals(expected[i], actual[i]);
    }
}
