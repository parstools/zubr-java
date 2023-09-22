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
            sb.append(grammar.getTerminalName(k));
        sb.append("}");
        return sb.toString();
    }
}
