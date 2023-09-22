package ll;

import grammar.Grammar;
import grammar.TestGrammars;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParsingTableTest {
    @Test
    void testStdLL1() {
        Grammar g = TestGrammars.stdLL1();
        ParsingTable table = new ParsingTable(g);
        boolean res1 = table.createLL(1);
        assertTrue(res1);
    }

    @Test
    void testLL_1() {
        Grammar g = TestGrammars.LL1();
        ParsingTable table = new ParsingTable(g);
        boolean res1 = table.createLL(1);
        assertTrue(res1);
    }

    @Test
    void testLL_2() {
        Grammar g = TestGrammars.LL2();
        ParsingTable table = new ParsingTable(g);
        boolean res1 = table.createLL(1);
        boolean res2 = table.createLL(2);
        assertFalse(res1);
        assertTrue(res2);
    }

    @Test
    void testLL_3() {
        Grammar g = TestGrammars.LL3();
        ParsingTable table = new ParsingTable(g);
        boolean res1 = table.createLL(1);
        boolean res2 = table.createLL(2);
        boolean res3 = table.createLL(3);
        assertFalse(res1);
        assertFalse(res2);
        assertTrue(res3);
    }
}
