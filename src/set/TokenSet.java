package set;

import grammar.Grammar;
import grammar.Symbol;
import util.Hash;

import java.util.ArrayList;
import java.util.List;

public class TokenSet {
    int maxLen;
    private Tier[] tiers;
    Grammar grammar;

    public TokenSet(Grammar grammar, int maxLen) {
        this.maxLen = maxLen;
        tiers = new Tier[maxLen + 1];
        for (int i = 0; i <= maxLen; i++)
            tiers[i] = new Tier(grammar, i);
    }

    public boolean isEmpty() {
        for (int i = 0; i <= maxLen; i++) {
            Tier tier = tiers[i];
            if (tier.trie != null)
                return false;
        }
        return true;
    }

    boolean hasLen(int requiredLen) {
        for (int i = maxLen; i >= requiredLen; i--) {
            Tier tier = tiers[i];
            if (tier.trie != null) {
                assert (i == 0 || !tier.trie.map.isEmpty());
                return true;
            }
        }
        return false;
    }

    int minLen() {
        for (int i = 0; i <= maxLen; i++) {
            Tier tier = tiers[i];
            if (tier.trie != null) {
                assert (i == 0 || !tier.trie.map.isEmpty());
                return i;
            }
        }
        return maxLen;
    }

    public boolean addSeq(Sequence seq) {
        /*treat sequence ending with -1 ($) as alwaays ful sized
         * because will never expand to full size*/
        if (!seq.isEmpty() && seq.get(seq.size() - 1) == -1)
            return tiers[maxLen].addSeq(seq);
        else
            return tiers[seq.size()].addSeq(seq);
    }

    public void addAllSSeq(SequenceSet sseq) {
        for (Sequence seq : sseq)
            addSeq(seq);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean needSpace = false;
        for (int i = 0; i <= maxLen; i++) {
            String tstr = tiers[i].toString();
            boolean empty = tstr.isEmpty();
            if (!empty) {
                if (needSpace) {
                    sb.append(" ");
                    needSpace = false;
                }
                sb.append(tstr);
                needSpace = true;
            }
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        Hash h = new Hash();
        for (Tier t : tiers)
            h.add(t.hashCode());
        return h.hash();
    }

    public boolean addEpsilon() {
        return tiers[0].addSeq(new Sequence(grammar));
    }

    public boolean hasEpsilon() {
        return tiers[0].trie != null;
    }

    private boolean addTier(Tier tier) {
        return tiers[tier.len].unionWith(tier);
    }

    public boolean unionWith(TokenSet tokenSet) {
        boolean changed = false;
        for (Tier tier : tokenSet.tiers)
            if (addTier(tier))
                changed = true;
        return changed;
    }

    public boolean unionWithoutEps(TokenSet tokenSet) {
        boolean changed = false;
        for (Tier tier : tokenSet.tiers)
            if (tier.len > 0)
                if (addTier(tier))
                    changed = true;
        return changed;
    }

    public void appendStrings(Symbol symbol) {
        assert (maxLen > 0);
        for (int i = maxLen; i >= 1; i--) {
            Trie trie = tiers[i - 1].trie;
            if (trie != null) {
                tiers[i - 1].trie = null;
                trie.appendStrings(symbol);
                if (tiers[i].trie == null)
                    tiers[i].trie = trie;
                else
                    tiers[i].trie.unionWith(trie);
            }
        }
    }

    public boolean concatenable() {
        for (int i = 0; i < maxLen; i++)
            if (tiers[i].trie != null)
                return true;
        return false;
    }

    public boolean concatPrefixes(TokenSet firstY) {
        assert (!firstY.isEmpty());
        boolean changed = false;
        for (int i = maxLen - 1; i >= 0; i--) {
            Trie trie = tiers[i].trie;
            if (trie != null) {
                int maxPrefixLen = maxLen - i;
                for (int j = 1; j <= maxLen; j++) {
                    Trie ytrie = firstY.tiers[j].trie;
                    if (ytrie == null)
                        continue;
                    int prefixLen = Math.min(maxPrefixLen, j);
                    Trie cloned = (Trie) trie.clone();
                    cloned.concatPrefixes(prefixLen, ytrie);
                    tiers[i].trie = null;
                    changed = true;
                    Tier tt = tiers[i + prefixLen];
                    if (tt.trie == null)
                        tt.trie = cloned;
                    else
                        tt.trie.unionWith(cloned);
                }
            }
        }
        return changed;
    }
}
