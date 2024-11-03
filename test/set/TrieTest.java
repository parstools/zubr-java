package set;

import grammar.Grammar;
import grammar.TestGrammars;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TrieTest {
    @Test
    void cloneTest() {
        Grammar grammar = TestGrammars.tokenGrammar();
        Trie trie1 = new Trie(grammar, 1);
        trie1.put(1, null);
        Trie trie2 = new Trie(grammar, 2);
        trie2.put(0, trie1);
        Trie trie3 = (Trie) trie2.clone();
        assertEquals("ab", trie2.toString());
        assertEquals("ab", trie3.toString());
        Trie trie4 = trie2.clonePrefix(1);
        assertEquals("a", trie4.toString());
        Trie trie5 = trie2.clonePrefix(2);
        assertEquals("ab", trie5.toString());
    }

    Trie trieDef() {
        Trie resultC = new Trie(TestGrammars.tokenGrammar(), 1);
        resultC.put(2, null);
        Trie resultD = new Trie(TestGrammars.tokenGrammar(), 1);
        resultD.put(3, null);
        Trie result = new Trie(TestGrammars.tokenGrammar(), 2);
        result.put(0, resultC);
        result.put(1, resultD);
        return result;
    }

    @Test
    void concatPrefixes() {
        Trie bottomTrie = trieDef();
        assertEquals("ac bd", bottomTrie.toString());
        Trie topTrie = bottomTrie.clone();
        assertEquals("ac bd", topTrie.toString());
        Trie trie3 = bottomTrie.concatPrefixes(1, topTrie);
        assertEquals("ac bd", bottomTrie.toString()); //not change operandds
        assertEquals("ac bd", topTrie.toString());
        assertEquals("aca acb bda bdb", trie3.toString());
    }

}
