package set;

import grammar.Grammar;

import java.util.TreeMap;
import java.util.TreeSet;

public class SingleTokenSet extends TreeSet<Integer> {
    Grammar grammar;
    SingleTokenSet(Grammar grammar) {
        this.grammar = grammar;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int k : this)
            if (k == -1)
                sb.append("$");
            else
                sb.append(grammar.terminals.get(k).name);
        sb.append("}");
        return sb.toString();
    }
}
