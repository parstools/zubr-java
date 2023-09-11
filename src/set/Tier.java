package set;

import grammar.Grammar;
import util.Hash;

import java.util.*;

public class Tier {
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
    }

    int len;
    Trie trie = null;
    Grammar grammar;

    Tier(Grammar grammar, int len) {
        this.len = len;
        this.grammar = grammar;
    }

    void addSeq(Sequence seq) {
        if (trie == null)
            trie = new Trie(grammar); //for first addSeq, for example epsilon empty sequence;
        assert (seq.size() == len || seq.size() < len && !seq.isEmpty() && seq.get(seq.size() - 1) == -1);
        Trie prior = trie;
        for (Integer i : seq) {
            Trie tr = prior.get(i);
            if (tr == null) {
                tr = new Trie(grammar);
                prior.put(i, tr);
            }
            prior = tr;
        }
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
