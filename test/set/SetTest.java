package set;

import grammar.TestGrammars;
import grammar.Grammar;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SetTest {
    @Test
    void addSeq() {
        Grammar grammar = TestGrammars.grammar2();
        TokenSet set = new TokenSet(grammar, 3);
        Sequence eps = new Sequence(grammar, "");
        set.addSeq(eps);
        set.addSeq(new Sequence(grammar, "aa"));
        set.addSeq(new Sequence(grammar, "ab"));
        set.addSeq(new Sequence(grammar, "ac"));
        assertEquals("{eps aa ab ac}", set.toString());
    }

    @Test
    void addTier1() {
        Grammar grammar = TestGrammars.grammar2();
        TokenSet set1 = new TokenSet(grammar, 1);
        set1.addSeq(new Sequence(grammar, "a"));
        set1.addSeq(new Sequence(grammar, "b"));
        TokenSet set2 = new TokenSet(grammar, 1);
        set2.addSeq(new Sequence(grammar, "b"));
        set2.addSeq(new Sequence(grammar, "c"));
        boolean changed = set1.addTier(set2.tiers.get(1));
        assertTrue(changed);
        changed = set1.addTier(set2.tiers.get(1));
        assertFalse(changed);
        assertEquals("{a b c}", set1.toString());
    }
}
