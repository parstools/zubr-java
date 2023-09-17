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

    static public class Trie {
        private TreeMap<Integer, Trie> map = new TreeMap<>();

        Trie get(int key) {
            return map.get(key);
        }

        void put(int key, Trie value) {
            map.put(key, value);
        }

        Grammar grammar;

        Trie(Grammar grammar) {
            this.grammar = grammar;
        }

        List<String> getSuffixes() {
            Set<Integer> intSet = map.keySet();
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
                        list.add(tName + s);
                    }
            }
            return list;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            List<String> stringList = getSuffixes();
            for (int i = 0; i < stringList.size(); i++) {
                if (i > 0)
                    sb.append(" ");
                sb.append(stringList.get(i));
            }
            return sb.toString();
        }

        @Override
        public int hashCode() {
            Set<Integer> intSet = map.keySet();
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
            Set<Integer> intSet = map.keySet();
            Iterator<Integer> iter = intSet.iterator();
            while (iter.hasNext()) {
                int t = iter.next();
                newTrie.put(t, (Trie)get(t).clone());
            }
            return newTrie;
        }

        public void appendStrings(int index) {
            Set<Integer> intSet = map.keySet();
            Iterator<Integer> iter = intSet.iterator();
            if (iter.hasNext())
                while (iter.hasNext()) {
                    int t = iter.next();
                    get(t).appendStrings(index);
                }
            else {
                Trie tr = new Trie(grammar);
                put(index, tr);
            }
        }

        public void appendStrings(Symbol symbol) {
            assert (symbol.terminal);
            appendStrings(symbol.index);
        }

        public boolean unionWith(Trie trie) {
            Set<Integer> intSet = trie.map.keySet();
            Iterator<Integer> iter = intSet.iterator();
            boolean modified = false;
            while (iter.hasNext()) {
                int t = iter.next();
                if (map.containsKey(t)) {
                    if (get(t).unionWith(trie.get(t)))
                        modified = true;
                } else {
                    modified = true;
                    put(t, (Trie) trie.get(t).clone());
                }
            }
            return modified;
        }

        public void concatPrefixes(int prefixLen, Trie trie) {
            assert (prefixLen >= 0);
            if (prefixLen==0) return;
            if (trie == null) return;
            Set<Integer> intSet = trie.map.keySet();
            for (int t : intSet) {
                appendStrings(t);
                concatPrefixes(prefixLen-1, get(t));
            }
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
