package set;

import grammar.Grammar;
import grammar.Symbol;

import java.util.ArrayList;

public class Sequence extends ArrayList<Integer> {
    Grammar grammar;
    public Sequence(Grammar grammar) {
        this.grammar = grammar;
    }

    public Sequence(Grammar grammar, Symbol symbol) {
        this.grammar = grammar;
        assert (symbol.terminal);
        add(symbol.index);
    }

    public Sequence(Grammar grammar, String string) {
        this.grammar = grammar;
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            int t = grammar.findTerminal(Character.toString(c));
            add(t);
        }
    }

    @Override
    public String toString() {
        if (isEmpty())
            return "eps";
        else {
            StringBuilder sb = new StringBuilder();
            for (int k : this)
                sb.append(grammar.getTerminalName(k));
            return sb.toString();
        }
    }

    public boolean isEof() {
        return size() > 0 && get(size() - 1) == -1;
    }
}
