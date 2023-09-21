package set;

import grammar.Grammar;
import grammar.TestGrammars;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TierTest {
    @Test
    void concat() {
        Grammar grammar = TestGrammars.stdLL1();
        Tier tier0 = new Tier(grammar,2);
        tier0.addSeq("+i");
        assertEquals("+i", tier0.toString());
        Tier tier1 = new Tier(grammar,3);
        tier1.addSeq("+i*");
        assertEquals("+i*", tier1.toString());
        Tier tier3 = tier0.concat(tier1, 3);
        assertEquals("+i+", tier3.toString());
    }
}
