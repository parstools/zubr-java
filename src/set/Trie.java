package set;

import grammar.Grammar;
import grammar.Symbol;
import util.Hash;

import java.util.*;

public class Trie {
    TreeMap<Integer, Trie> map = new TreeMap<>();

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
            newTrie.put(t, (Trie) get(t).clone());
        }
        return newTrie;
    }

    Trie clone(int prefixLen) {
        Trie newTrie = new Trie(grammar);
        if (prefixLen > 0) {
            Set<Integer> intSet = map.keySet();
            Iterator<Integer> iter = intSet.iterator();
            while (iter.hasNext()) {
                int t = iter.next();
                newTrie.put(t, get(t).clone(prefixLen - 1));
            }
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
        if (prefixLen == 0) return;
        if (trie == null) return;
        if (map.isEmpty()) {
            Set<Integer> intSet = trie.map.keySet();
            for (int t : intSet) {
                map.put(t, (Trie) trie.get(t).clone());
            }
        } else {
            appendPrefixes(prefixLen, trie);
        }
    }

    private void appendPrefixes(int prefixLen, Trie trie) {
        Set<Integer> intSet1 = map.keySet();
        assert (!intSet1.isEmpty());
        for (int t1 : intSet1) {
            Trie sub = get(t1);
            if (sub.map.isEmpty())
                put(t1, trie.clone(prefixLen));
            else
                sub.appendPrefixes(prefixLen - 1, trie);
        }
    }
}
