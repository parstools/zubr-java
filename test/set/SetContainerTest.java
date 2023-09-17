package set;

import grammar.Grammar;
import grammar.TestGrammars;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SetContainerTest {
    @Test
    void FirstFollow1() {
        Grammar g = TestGrammars.testFirstFollow();
        SetContainer sc = new SetContainer(g);
        sc.reset(1);
        sc.makeFirstSets1();
        assertEquals("{( i}", sc.firstSetForIndex(0).toString());
        assertEquals("{eps +}", sc.firstSetForIndex(1).toString());
        assertEquals("{( i}", sc.firstSetForIndex(2).toString());
        assertEquals("{eps *}", sc.firstSetForIndex(3).toString());
        assertEquals("{( i}", sc.firstSetForIndex(4).toString());

        sc.makeFollowSets1();
        assertEquals("{$ )}", sc.followSetForIndex(0).toString());
        assertEquals("{$ )}", sc.followSetForIndex(1).toString());
        assertEquals("{$ + )}", sc.followSetForIndex(2).toString());
        assertEquals("{$ + )}", sc.followSetForIndex(3).toString());
        assertEquals("{$ + * )}", sc.followSetForIndex(4).toString());
    }
}
