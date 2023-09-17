package set;

import grammar.Grammar;
import util.Hash;

import java.util.ArrayList;
import java.util.List;

public class TokenSet {
    int maxLen;
    List<Tier> tiers = new ArrayList<>();
    Grammar grammar;

    public TokenSet(Grammar grammar, int maxLen) {
        this.maxLen = maxLen;
        for (int i = 0; i <= maxLen; i++)
            tiers.add(new Tier(grammar, i));
    }

    public boolean addSeq(Sequence seq) {
        /*treat sequence ending with -1 ($) as alwaays ful sized
         * because will never expand to full size*/
        if (!seq.isEmpty() && seq.get(seq.size() - 1) == -1)
            return tiers.get(maxLen).addSeq(seq);
        else
            return tiers.get(seq.size()).addSeq(seq);
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
            String tstr = tiers.get(i).toString();
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
        for (Tier t: tiers)
            h.add(t.hashCode());
        return h.hash();
    }

    public boolean addEpsilon() {
        return tiers.get(0).addSeq(new Sequence(grammar));
    }

    public boolean hasEpsilon() {
        return tiers.get(0).trie != null;
    }

    public boolean addTier(Tier tier) {
        return tiers.get(tier.len).unionWith(tier);
    }
}
