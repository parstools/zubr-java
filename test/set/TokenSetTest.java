package set;

import grammar.TestGrammars;
import grammar.Grammar;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TokenSetTest {
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
        boolean changed = set1.unionWithoutEps(set2);
        assertTrue(changed);
        changed = set1.unionWithoutEps(set2);
        assertFalse(changed);
        assertEquals("{a b c}", set1.toString());
    }

    @Test
    void concatPrefixes1() {
        Grammar grammar = TestGrammars.testFirstFollow();
        TokenSet set1 = new TokenSet(grammar, 2);
        set1.addSeq(new Sequence(grammar, ""));
        TokenSet set2 = new TokenSet(grammar, 2);
        set2.addSeq(new Sequence(grammar, "("));
        set2.addSeq(new Sequence(grammar, "i"));
        assertEquals("{( i}",set2.toString());
        set1.concatPrefixes(set2);
        assertEquals("{( i}",set1.toString());
    }

    @Test
    void concatPrefixes2() {
        Grammar grammar = TestGrammars.testFirstFollow();
        TokenSet set1 = new TokenSet(grammar, 2);
        set1.addSeq(new Sequence(grammar, ""));
        TokenSet set2 = new TokenSet(grammar, 2);
        set2.addSeq(new Sequence(grammar, "i"));
        set2.addSeq(new Sequence(grammar, "()"));
        assertEquals("{i ()}",set2.toString());
        set1.concatPrefixes(set2);
        assertEquals("{i ()}",set1.toString());
    }

    @Test
    void concateWithEps() {
        Grammar grammar = TestGrammars.testFirstFollow();
        TokenSet set1 = new TokenSet(grammar, 2);
        set1.addSeq(new Sequence(grammar, "i"));
        set1.addSeq(new Sequence(grammar, "()"));
        TokenSet set2 = new TokenSet(grammar, 2);
        set2.addSeq(new Sequence(grammar, ""));
        set1.concatPrefixes(set2);
        assertEquals("{i ()}",set1.toString());
    }
}
