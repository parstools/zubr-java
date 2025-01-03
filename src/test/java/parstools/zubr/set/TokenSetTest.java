package parstools.zubr.set;

import org.junit.jupiter.api.Test;
import parstools.zubr.grammar.Grammar;
import parstools.zubr.grammar.TestGrammars;

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
    void addTier() {
        Grammar grammar = TestGrammars.grammar2();
        TokenSet set1 = new TokenSet(grammar, 1);
        set1.addSeqDone("a");
        set1.addSeqDone("b");
        TokenSet set2 = new TokenSet(grammar, 1);
        set2.addSeqDone("b");
        set2.addSeqDone("c");
        boolean changed = set1.unionWith(set2);
        assertTrue(changed);
        changed = set1.unionWith(set2);
        assertFalse(changed);
        assertEquals("{a b c}", set1.toString());
    }

    @Test
    void concat1toEps() {
        Grammar grammar = TestGrammars.stdLL1();
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
    void concat1noEps() {
        Grammar grammar = TestGrammars.stdLL1();
        TokenSet set1 = new TokenSet(grammar, 2);
        TokenSet set2 = new TokenSet(grammar, 2);
        set2.addSeqDone("(");
        set2.addSeqDone("i");
        assertEquals("{( i}",set2.toString());
        TokenSet set3 = set1.concat(set2);
        assertEquals("{}", set3.toString());
    }

    @Test
    void concat2() {
        Grammar grammar = TestGrammars.stdLL1();
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
        Grammar grammar = TestGrammars.stdLL1();
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
        Grammar grammar = TestGrammars.stdLL1();
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
        Grammar grammar = TestGrammars.stdLL1();
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
        Grammar grammar = TestGrammars.stdLL1();
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

    @Test
    void getPrefixesTest() {
        Grammar grammar = TestGrammars.grammar2();
        TokenSet set = new TokenSet(grammar, 3);
        set.parse("{a ab bca bb bac $ c$ aa$}");
        SequenceSet ss = set.getPrefixes(1);
        TokenSet sorted1 = new TokenSet(grammar, 1);
        sorted1.addAllSeqDoneOrEof(ss);
        assertEquals("{a b c $}", sorted1.toString());
        ss = set.getPrefixes(2);
        TokenSet sorted2 = new TokenSet(grammar, 2);
        sorted2.addAllSeqDoneOrEof(ss);
        assertEquals("{aa ab ba bb bc $ c$}", sorted2.toString());
        ss = set.getPrefixes(3);
        TokenSet sorted3 = new TokenSet(grammar, 3);
        sorted3.addAllSeqDoneOrEof(ss);
        assertEquals("{bac bca $ c$ aa$}", sorted3.toString());
    }

    @Test
    void firstTokensTest() {
        Grammar grammar = TestGrammars.grammar2();
        TokenSet set = new TokenSet(grammar, 3);
        set.parse("{a ab bca bb bac $ c$ aa$}");
        SingleTokenSet sts = set.firstTokens();
        assertEquals("{$abc}", sts.toString());
    }

    @Test
    void nthTokensTest() {
        Grammar grammar = TestGrammars.grammar2();
        TokenSet set = new TokenSet(grammar, 3);
        set.parse("{a ab bca bb bac $ c$ aa$}");
        SingleTokenSet sts = set.nthTokens("");
        assertEquals("{$abc}", sts.toString());
        sts = set.nthTokens("a");
        assertEquals("{ab}", sts.toString());
        sts = set.nthTokens("b");
        assertEquals("{abc}", sts.toString());
        sts = set.nthTokens("c");
        assertEquals("{$}", sts.toString());
        sts = set.nthTokens("ba");
        assertEquals("{c}", sts.toString());
    }
}
