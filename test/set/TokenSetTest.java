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
        set.addSeqDone("");
        set.addSeqDone("aa");
        set.addSeqDone("ab");
        set.addSeqDone("ac");
        assertEquals("{eps aa ab ac}", set.toString());
    }

    @Test
    void addTier1() {
        Grammar grammar = TestGrammars.grammar2();
        TokenSet set1 = new TokenSet(grammar, 1);
        set1.addSeqDone("a");
        set1.addSeqDone("b");
        TokenSet set2 = new TokenSet(grammar, 1);
        set2.addSeqDone("b");
        set2.addSeqDone("c");
        boolean changed = set1.unionWithoutEps(set2);
        assertTrue(changed);
        changed = set1.unionWithoutEps(set2);
        assertFalse(changed);
        assertEquals("{a b c}", set1.toString());
    }

    @Test
    void concat1() {
        Grammar grammar = TestGrammars.LL1();
        TokenSet set1 = new TokenSet(grammar, 2);
        set1.addSeqBuild("");
        TokenSet set2 = new TokenSet(grammar, 2);
        set2.addSeqDone("(");
        set2.addSeqDone("i");
        assertEquals("{( i}",set2.toString());
        TokenSet set3 = set1.concat(set2);
        assertEquals("[( i]", set3.toString());
    }

    @Test
    void concat2() {
        Grammar grammar = TestGrammars.LL1();
        TokenSet set1 = new TokenSet(grammar, 2);
        set1.addSeqBuild("");
        TokenSet set2 = new TokenSet(grammar, 2);
        set2.addSeqDone("i");
        set2.addSeqDone("()");
        assertEquals("{i ()}",set2.toString());
        TokenSet set3 = set1.concat(set2);
        assertEquals("[i]{()}", set3.toString());
    }

    @Test
    void concatWithEps() {
        Grammar grammar = TestGrammars.LL1();
        TokenSet set1 = new TokenSet(grammar, 2);
        set1.addSeqBuild("i");
        set1.addSeqDone("()");
        TokenSet set2 = new TokenSet(grammar, 2);
        set2.addSeqDone("");
        TokenSet set3 = set1.concat(set2);
        assertEquals("[i]{()}", set1.toString());
        assertEquals("{eps}", set2.toString());
        assertEquals("[i]{()}", set3.toString());
    }

    @Test
    void concatWithEps_1() {
        Grammar grammar = TestGrammars.LL1();
        TokenSet set1 = new TokenSet(grammar, 2);
        set1.addSeqBuild("i");
        TokenSet set2 = new TokenSet(grammar, 2);
        set2.addSeqDone("");
        set2.addSeqDone("*i");
        TokenSet set3 = set1.concat(set2);
        assertEquals("[i]", set1.toString());
        assertEquals("{eps *i}", set2.toString());
        assertEquals("[i]{i*}", set3.toString());
    }

    @Test
    void concatFirstK3() {
        Grammar grammar = TestGrammars.LL1();
        TokenSet set1 = new TokenSet(grammar, 3);
        set1.addSeqBuild("+i");
        set1.addSeqDone("+(i");
        set1.addSeqDone("+i*");
        assertEquals("[+i]{+(i +i*}", set1.toString());
        TokenSet set2 = new TokenSet(grammar, 3);
        set2.addSeqDone("");
        set2.addSeqDone("+i");
        set2.addSeqDone("+i*");
        assertEquals("{eps +i +i*}", set2.toString());
        TokenSet set3 = set1.concat(set2);
        assertEquals("[+i]{+(i +i+ +i*}", set3.toString());
    }

    @Test
    void containsTest() {
        Grammar grammar = TestGrammars.LL1();
        TokenSet set = new TokenSet(grammar, 3);
        set.addSeqBuild("i+");
        set.addSeqDone("i+(");
        assertTrue(set.contains("i+"));
        assertFalse(set.contains("i+)"));
    }

    @Test
    void parseTest1() {
        Grammar grammar = TestGrammars.grammar2();
        TokenSet set = new TokenSet(grammar, 3);
        set.parse("[eps abc a ab]{c$ ab cab cc$ eps a}");
        assertEquals("[eps a ab abc]{eps a ab cab c$ cc$}",set.toString());
    }

    @Test
    void parseTest2() {
        Grammar grammar = TestGrammars.grammar2();
        TokenSet set = new TokenSet(grammar, 3);
        set.parse("[eps abc a ab]");
        assertEquals("[eps a ab abc]",set.toString());
    }

    @Test
    void parseTest3() {
        Grammar grammar = TestGrammars.grammar2();
        TokenSet set = new TokenSet(grammar, 3);
        set.parse("{c$ ab cab cc$ eps a}");
        assertEquals("{eps a ab cab c$ cc$}",set.toString());
    }
}
