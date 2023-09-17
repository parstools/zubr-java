package set;

import grammar.Grammar;
import util.Hash;

import java.util.*;

public class Tier {
    public boolean unionWith(Tier tier) {
        boolean modified = false;
        if (tier.trie==null)
            return false;
        if (trie == null) {
            trie = new Trie(grammar);
            modified= true;
        }
        if (trie.unionWith(tier.trie))
            modified= true;
        return modified;
    }

    static public class Trie extends TreeMap<Integer, Trie> {
        Grammar grammar;

        Trie(Grammar grammar) {
            this.grammar = grammar;
        }

        List<String> getSuffixes() {
            Set<Integer> intSet = this.keySet();
            Iterator<Integer> iter = intSet.iterator();
            List<String> list = new ArrayList<>();
            while (iter.hasNext()) {
                int t = iter.next();
                List<String> subList = get(t).getSuffixes();
                String tName = grammar.getTerminalName(t);
                if (subList.isEmpty())
                    list.add(tName);
                else
                    for (String s : subList) {
                        StringBuilder sb = new StringBuilder(tName);
                        sb.append(s);
                        list.add(sb.toString());
                    }
            }
            return list;
        }

        @Override
        public int hashCode() {
            Set<Integer> intSet = this.keySet();
            Iterator<Integer> iter = intSet.iterator();
            Hash h = new Hash();
            while (iter.hasNext()) {
                int t = iter.next();
                h.add(t);
                h.add(get(t).hashCode());
            }
            return h.hash();
        }

        public Object clone() {
            Trie newTrie = new Trie(grammar);
            Set<Integer> intSet = keySet();
            Iterator<Integer> iter = intSet.iterator();
            while (iter.hasNext()) {
                int t = iter.next();
                newTrie.put(t, (Trie)get(t).clone());
            }
            return newTrie;
        }

        public boolean unionWith(Trie trie) {
            Set<Integer> intSet = trie.keySet();
            Iterator<Integer> iter = intSet.iterator();
            boolean modified = false;
            while (iter.hasNext()) {
                int t = iter.next();
                if (containsKey(t)) {
                    if (get(t).unionWith(trie.get(t)))
                        modified = true;
                } else {
                    modified = true;
                    put(t, (Trie)trie.get(t).clone());
                }
            }
            return modified;
        }
    }

    int len;
    Trie trie = null;
    Grammar grammar;

    Tier(Grammar grammar, int len) {
        this.len = len;
        this.grammar = grammar;
    }

    boolean addSeq(Sequence seq) {
        boolean modified = false;
        if (trie == null) {
            trie = new Trie(grammar); //for first addSeq, for example epsilon empty sequence;
            modified= true;
        }
        assert (seq.size() == len || seq.size() < len && !seq.isEmpty() && seq.get(seq.size() - 1) == -1);
        Trie prior = trie;
        for (Integer i : seq) {
            Trie tr = prior.get(i);
            if (tr == null) {
                tr = new Trie(grammar);
                prior.put(i, tr);
                modified= true;
            }
            prior = tr;
        }
        return modified;
    }

    @Override
    public String toString() {
        if (trie == null)
            return "";
        if (len == 0)
            return "eps";
        StringBuilder sb = new StringBuilder();
        List<String> stringList = trie.getSuffixes();
        for (int i = 0; i < stringList.size(); i++) {
            if (i > 0)
                sb.append(" ");
            sb.append(stringList.get(i));
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        if (trie==null)
            return 0;
        else
            return trie.hashCode();
    }
}
