package set;

import grammar.Grammar;

public class Tier {
    public boolean unionWith(Tier tier) {
        boolean modified = false;
        if (tier.trie == null)
            return false;
        if (trie == null) {
            trie = new Trie(grammar, len);
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
            return 1; // epsilon
        else
            return trie.calculateSize();
    }

    boolean addSeq(String str) {
        return addSeq(new Sequence(grammar, str));
    }

    private Trie searchTrieToInsert(Sequence seq) {
        Trie result = trie;
        for (int t : seq) {
            Trie value = result.get(t);
            if (value == null) {
                if (result.containsKey(t))
                    return null;
                else
                    return result;
            } else
                result = value;
        }
        return result;
    }

    public boolean addSeq(Sequence seq) {
        assert(len == seq.size());
        boolean modified = false;
        if (trie == null) {
            trie = new Trie(grammar, len);
            modified = true;
        }
        Trie trieToInsert = searchTrieToInsert(seq);
        if (trieToInsert == null) return false;
        if (trieToInsert.insertSuffix(seq))
            modified = true;
        return modified;
    }

    /*boolean addSeq(Sequence seq) {
        boolean modified = false;
        if (trie == null) {
            trie = new Trie(grammar, len);
            modified = true;
        }
        assert (seq.size() == len);
        Trie previous = trie;
        int h = 0;
        for (int i : seq) {
            h++;
            Trie tr = previous.get(i);
            if (tr == null) {
                if (h < len)
                    tr = new Trie(grammar, len);
                previous.put(i, tr);
                modified = true;
            }
            previous = tr;
        }
        return modified;
    }*/

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
        assert(len == seq.size());
        if (trie == null)
            return false;
        if (len == 0)
            return true;// epsilon
        return trie.contains(seq);
    }

    public boolean contains(String str) {
        return contains(new Sequence(grammar, str));
    }

    public Tier concat(Tier tier, int newLen) {
        int prefixLen = newLen - len;
        Tier result = new Tier(grammar, newLen);
        if (trie == null || tier.trie == null)
            return result;
        if (len == 0) {
            assert (trie.isEmpty()); // epsilon
            result.trie = tier.trie.clonePrefix(prefixLen);
        } else
            result.trie = trie.concatPrefixes(prefixLen, tier.trie);
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

    public void firstTokens(SingleTokenSet sts) {
        if (trie != null)
            trie.firstTokens(sts);
    }

    public void nthTokens(Sequence seq, SingleTokenSet sts) {
        if (trie != null)
            trie.nthTokens(seq, sts);
    }

    public Tier intersection(Tier tier1) {
        assert(len == tier1.len);
        if (trie == null || tier1.trie == null)
            return null;
        Trie resTrie = trie.intersection(tier1.trie);
        if (resTrie == null)
            return null;
        else {
            Tier resTier = new Tier(grammar, len);
            resTier.trie = resTrie;
            return resTier;
        }
    }

    public boolean isIntersection(Tier tier1) {
        assert(len == tier1.len);
        if (trie == null || tier1.trie == null)
            return false;
        return trie.isIntersection(tier1.trie, len);
    }
}
