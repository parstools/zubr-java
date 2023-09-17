package set;

import grammar.Grammar;
import grammar.TestGrammars;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SetContainerTest {
    @Test
    void First1() {
        Grammar g = TestGrammars.testFirstFollow();
        SetContainer sc = new SetContainer(g);
        sc.reset(1);
        sc.makeFirstSets1();
        assertEquals("{( i}", sc.firstSetForIndex(0).toString());
        assertEquals("{eps +}", sc.firstSetForIndex(1).toString());
        assertEquals("{( i}", sc.firstSetForIndex(2).toString());
        assertEquals("{eps *}", sc.firstSetForIndex(3).toString());
        assertEquals("{( i}", sc.firstSetForIndex(4).toString());
    }
}
