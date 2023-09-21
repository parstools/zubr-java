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
        if (trie != null)
            newTier.trie = trie.clone();
        return newTier;
    }

    public int calculateSize() {
        if (trie == null)
            return 0;
        else if (len == 0)
            return 1; //epsilon
        else
            return trie.calculateSize();
    }

    boolean addSeq(String str) {
        return addSeq(new Sequence(grammar, str));
    }

    boolean addSeq(Sequence seq) {
        boolean modified = false;
        if (trie == null) {
            trie = new Trie(grammar);
            modified = true;
        }
        assert (seq.size() == len);
        Trie previous = trie;
        for (int i : seq) {
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

    public boolean contains(Sequence seq) {
        if (trie == null)
            return false;
        if (len == 0)
            return true;
        assert (seq.size() == len);
        Trie tr = trie;
        for (int i : seq) {
            tr = tr.get(i);
            if (tr == null)
                return false;
        }
        return true;
    }

    public boolean contains(String str) {
        return contains(new Sequence(grammar, str));
    }

    public Tier concat(Tier tier, int newLen) {
        int prefixLen = newLen - len;
        Tier result = new Tier(grammar, len);
        if (trie == null || tier.trie == null)
            return result;
        result.trie = trie.clone();
        result.trie.concatPrefixes(prefixLen, tier.trie);
        return result;
    }

    public void clear() {
        trie = null;
    }

    public boolean isEmpty() {
        return trie == null;
    }

    public void getPrefixes(int prefixLen, SequenceSet ss) {
        if (trie != null)
            trie.getPrefixes(new Sequence(grammar), prefixLen, ss);
    }
}
