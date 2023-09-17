package set;

import grammar.Grammar;
import grammar.Symbol;
import util.Hash;

import java.util.*;

public class Tier {
    public boolean unionWith(Tier tier) {
        boolean modified = false;
        if (tier.trie == null)
            return false;
        if (trie == null) {
            trie = new Trie(grammar);
            modified = true;
        }
        if (trie.unionWith(tier.trie))
            modified = true;
        return modified;
    }

    int len;
    Trie trie = null;
    Grammar grammar;

    Tier(Grammar grammar, int len) {
        this.len = len;
        this.grammar = grammar;
    }

    public Tier clone() {
        Tier newTier = new Tier(grammar, len);
        if (trie!=null)
            newTier.trie = trie.clone();
        return newTier;
    }

    boolean addSeq(Sequence seq) {
        boolean modified = false;
        if (trie == null) {
            trie = new Trie(grammar);
            modified = true;
        }
        assert (seq.size() == len || seq.size() < len && !seq.isEmpty() && seq.get(seq.size() - 1) == -1);
        Trie previous = trie;
        for (Integer i : seq) {
            Trie tr = previous.get(i);
            if (tr == null) {
                tr = new Trie(grammar);
                previous.put(i, tr);
                modified = true;
            }
            previous = tr;
        }
        return modified;
    }

    @Override
    public String toString() {
        if (trie == null)
            return "";
        if (len == 0)
            return "eps";
        return trie.toString();
    }

    @Override
    public int hashCode() {
        if (trie == null)
            return 0;
        else
            return trie.hashCode();
    }
}
