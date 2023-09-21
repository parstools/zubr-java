package set;

import grammar.Grammar;
import grammar.TestGrammars;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrieTest {
    @Test
    void cloneTest() {
        Grammar grammar = TestGrammars.stdLL1();
        Trie trie1 = new Trie(grammar);
        trie1.put(0, new Trie(grammar));
        Trie trie2 = new Trie(grammar);
        trie2.put(1, trie1);
        Trie trie3 = (Trie) trie2.clone();
        assertEquals("*+", trie3.toString());
        Trie trie4 = trie2.clone(0);
        assertEquals("", trie4.toString());
        Trie trie5 = trie2.clone(1);
        assertEquals("*", trie5.toString());
    }

    @Test
    void concatPrefixes() {
        Grammar grammar = TestGrammars.stdLL1();
        Trie trie1 = new Trie(grammar);
        Trie trie2 = new Trie(grammar);
        trie2.put(0, new Trie(grammar));
        trie2.put(1, new Trie(grammar));
        trie1.concatPrefixes(1, trie2);
        trie2.put(2, new Trie(grammar));
        trie1.concatPrefixes(1, trie2);
        assertEquals("++ +* +( *+ ** *(", trie1.toString());
    }
}
