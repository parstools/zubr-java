package set;

import grammar.Grammar;
import grammar.Symbol;
import util.Hash;

import java.util.Iterator;
import java.util.Set;

public class TokenSet {
    int maxLen;
    private Tier[][] tiers;
    Grammar grammar;

    public TokenSet(Grammar grammar, int maxLen) {
        this.maxLen = maxLen;
        tiers = new Tier[2][];

        tiers[1] = new Tier[maxLen + 1];
        for (int n = 0; n < 2; n++) {
            tiers[n] = new Tier[maxLen + 1];
            for (int i = 0; i <= maxLen; i++)
                tiers[n][i] = new Tier(grammar, i);
        }
    }

    public boolean isEmpty() {
        for (int n = 0; n < 2; n++)
            for (int i = 0; i <= maxLen; i++) {
                Tier tier = tiers[n][i];
                if (tier.trie != null)
                    return false;
            }
        return true;
    }

    public boolean isEmptyBuild() {
        for (int i = 0; i <= maxLen; i++) {
            Tier tier = tiers[0][i];
            if (tier.trie != null)
                return false;
        }
        return true;
    }

    public boolean addSeqBuild(Sequence seq) {
        assert (seq.isEmpty() || seq.get(seq.size() - 1) >= 0);
        return tiers[0][seq.size()].addSeq(seq);
    }

    public boolean addSeqDone(Sequence seq) {
        return tiers[1][seq.size()].addSeq(seq);
    }

    public void addAllSSeqDone(SequenceSet sseq) {
        for (Sequence seq : sseq)
            addSeqDone(seq);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean needSpace = false;
        for (int n = 0; n < 2; n++)
            for (int i = 0; i <= maxLen; i++) {
                String tstr = tiers[n][i].toString();
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
        for (int n = 0; n < 2; n++)
            for (Tier t : tiers[n])
                h.add(t.hashCode());
        return h.hash();
    }

    public boolean addEpsilonBuild() {
        return tiers[0][0].addSeq(new Sequence(grammar));
    }

    public boolean addEpsilonDone() {
        return tiers[1][0].addSeq(new Sequence(grammar));
    }

    public boolean hasEpsilon() {
        return tiers[1][0].trie != null;
    }

    private boolean addTier(Tier tier) {
        return tiers[1][tier.len].unionWith(tier);
    }

    public boolean unionWith(TokenSet tokenSet) {
        boolean changed = false;
        for (Tier tier : tokenSet.tiers[1])
            if (addTier(tier))
                changed = true;
        return changed;
    }

    public boolean unionWithoutEps(TokenSet tokenSet) {
        boolean changed = false;
        for (Tier tier : tokenSet.tiers[1])
            if (tier.len > 0)
                if (addTier(tier))
                    changed = true;
        return changed;
    }

    public void appendStrings(Symbol symbol) {
        assert (maxLen > 0);
        for (int i = maxLen; i >= 1; i--) {
            Trie trie = tiers[0][i - 1].trie;
            if (trie != null) {
                tiers[0][i - 1].trie = null;
                trie.appendStrings(symbol);
                int target = i == maxLen ? 1 : 0;
                if (tiers[target][i].trie == null)
                    tiers[target][i].trie = trie;
                else
                    tiers[target][i].trie.unionWith(trie);
            }
        }
    }

    public boolean concatenable() {
        return isEmptyBuild();
    }

    void done() {
        for (int i = 0; i < maxLen; i++) {
            Tier t0 = tiers[0][i];
            Tier t1 = tiers[1][i];
            t1.unionWith(t0);
            t0.trie = null;
        }
    }


    public TokenSet clone() {
        TokenSet newSet = new TokenSet(grammar, maxLen);
        for (int n = 0; n < 2; n++)
            for (int i = 0; i <= maxLen; i++)
                newSet.tiers[n][i] = tiers[n][i].clone();
        return newSet;
    }

    public boolean concatPrefixes(TokenSet firstY) {
        assert (!firstY.isEmpty());
        assert (firstY.isEmptyBuild());
        boolean changed = false;
        /*TokenSet srcCloned = null;
        if (firstY.hasEpsilon()) {
            srcCloned = this.clone();
            srcCloned.done();
        }*/
        for (int i = maxLen - 1; i >= 0; i--) {
            Trie trie = tiers[0][i].trie;
            if (trie != null) {
                int maxPrefixLen = maxLen - i;
                for (int j = 0; j <= maxLen; j++) {
                    Trie ytrie = firstY.tiers[1][j].trie;
                    if (ytrie == null)
                        continue;
                    int prefixLen = Math.min(maxPrefixLen, j);
                    Trie cloned = trie.clone();
                    cloned.concatPrefixes(prefixLen, ytrie);
                    tiers[0][i].trie = null;
                    changed = true;
                    int targetIndex = i + prefixLen;
                    int target = targetIndex == maxLen ? 1 : 0;
                    Tier tt = tiers[1][targetIndex];
                    if (tt.trie == null)
                        tt.trie = cloned;
                    else
                        tt.trie.unionWith(cloned);
                    if (target==0) {
                        tt = tiers[0][targetIndex];
                        if (tt.trie == null)
                            tt.trie = cloned.clone();
                        else
                            tt.trie.unionWith(cloned);
                    }
                }
            }
        }
        /*if (srcCloned != null) {
            this.unionWith(srcCloned);
        }*/
        return changed;
    }
}
