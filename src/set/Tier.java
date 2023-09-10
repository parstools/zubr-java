package set;

import java.util.HashMap;
import java.util.HashSet;

public class Tier {
    static public class Trie extends HashMap<Integer, Trie> {
    }

    int len;
    Trie trie = new Trie();

    Tier(int len) {
        this.len = len;
    }

    void addSeq(Sequence seq) {
        assert (seq.size() == len || seq.size() < len && !seq.isEmpty() && seq.get(seq.size() - 1) == -1);
        Trie prior = trie;
        for (Integer i : seq) {
            Trie tr = prior.get(i);
            if (tr == null) {
                tr = new Trie();
                prior.put(i, tr);
            }
            prior = tr;
        }
    }
}
