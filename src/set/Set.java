package set;

import java.util.ArrayList;
import java.util.List;

public class Set {
    int maxLen;
    List<Tier> tiers = new ArrayList<>();
    public Set(int maxLen) {
        this.maxLen = maxLen;
        for (int i=0; i<=maxLen; i++)
            tiers.add(new Tier(i));
    }

    void addSeq(Sequence seq) {
        /*treat sequence ending with -1 ($) as alwaays ful sized
         * because will never expand to full size*/
        if (!seq.isEmpty() && seq.get(seq.size() - 1) == -1)
            tiers.get(maxLen).addSeq(seq);
        else
            tiers.get(seq.size()).addSeq(seq);
    }

    public void fromSSeq(SequenceSet sseq) {
        for (Sequence seq: sseq)
            addSeq(seq);
    }
}
