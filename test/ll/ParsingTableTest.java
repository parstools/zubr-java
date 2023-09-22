package ll;

import grammar.Grammar;
import grammar.TestGrammars;
import org.junit.jupiter.api.Test;

public class ParsingTableTest {
    @Test
    void testStdLL1() {
        Grammar g = TestGrammars.stdLL1();
        ParsingTable table = new ParsingTable(g);
        table.createLL(1);
    }

    @Test
    void testLL_1() {
        Grammar g = TestGrammars.LL1();
        ParsingTable table = new ParsingTable(g);
        table.createLL(1);
    }

    @Test
    void testLL_2() {
        Grammar g = TestGrammars.LL2();
        ParsingTable table = new ParsingTable(g);
        table.createLL(2);
    }

    @Test
    void testLL_3() {
        Grammar g = TestGrammars.LL3();
        ParsingTable table = new ParsingTable(g);
        table.createLL(3);
    }
}
