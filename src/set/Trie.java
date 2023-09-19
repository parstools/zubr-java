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
        List<String> list = new ArrayList<>();
        for (Map.Entry<Integer,Trie> entry : map.entrySet()) {
            int t = entry.getKey();
            Trie value = entry.getValue();
            List<String> subList = value.getSuffixes();
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
        Hash h = new Hash();
        for (Map.Entry<Integer,Trie> entry : map.entrySet()) {
            int t = entry.getKey();
            h.add(t);
            h.add(entry.getValue().hashCode());
        }
        return h.hash();
    }

    public Trie clone() {
        Trie newTrie = new Trie(grammar);
        for (Map.Entry<Integer,Trie> entry : map.entrySet()) {
            int t = entry.getKey();
            newTrie.put(t, entry.getValue().clone());
        }
        return newTrie;
    }

    Trie clone(int prefixLen) {
        Trie newTrie = new Trie(grammar);
        if (prefixLen > 0) {
            for (Map.Entry<Integer,Trie> entry : map.entrySet()) {
                int t = entry.getKey();
                newTrie.put(t, entry.getValue().clone(prefixLen - 1));
            }
        }
        return newTrie;
    }

    public void appendStrings(int index) {
        if (!map.isEmpty())
            for (Map.Entry<Integer,Trie> entry : map.entrySet()) {
                entry.getValue().appendStrings(index);
            }
        else {
            Trie tr = new Trie(grammar);
            put(index, tr);
        }
    }

    public int calculateSize() {
        if (map.isEmpty())
            return 1;
        else {
            int sum = 0;
            for (Map.Entry<Integer,Trie> entry : map.entrySet()) {
                sum += entry.getValue().calculateSize();
            }
            return sum;
        }
    }

    public void appendStrings(Symbol symbol) {
        assert (symbol.terminal);
        appendStrings(symbol.index);
    }

    public boolean unionWith(Trie trie) {
        boolean modified = false;
        for (Map.Entry<Integer,Trie> entry : trie.map.entrySet()) {
            int t = entry.getKey();
            if (map.containsKey(t)) {
                if (get(t).unionWith(entry.getValue()))
                    modified = true;
            } else {
                modified = true;
                put(t, entry.getValue().clone());
            }
        }
        return modified;
    }

    public void concatPrefixes(int prefixLen, Trie trie) {
        assert (prefixLen >= 0);
        if (prefixLen == 0) return;
        if (trie == null) return;
        if (map.isEmpty()) {
            for (Map.Entry<Integer,Trie> entry : trie.map.entrySet()) {
                map.put(entry.getKey(), entry.getValue().clone());
            }
        } else {
            appendPrefixes(prefixLen, trie);
        }
    }

    private void appendPrefixes(int prefixLen, Trie trie) {
        assert (!map.isEmpty());
        for (Map.Entry<Integer,Trie> entry : map.entrySet()) {
            int t1 = entry.getKey();
            Trie sub = entry.getValue();
            if (sub.map.isEmpty())
                put(t1, trie.clone(prefixLen));
            else
                sub.appendPrefixes(prefixLen, trie);
        }
    }
}
