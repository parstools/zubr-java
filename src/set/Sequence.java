package set;

import grammar.Grammar;
import grammar.Symbol;
import grammar.Terminal;

import java.util.ArrayList;

public class Sequence extends ArrayList<Integer> {
    Grammar grammar;
    public Sequence(Grammar grammar) {
        this.grammar = grammar;
    }

    public Sequence(Grammar grammar, Symbol symbol) {
        this.grammar = grammar;
        assert (symbol.terminal);
        add(grammar.terminals.indexOf(symbol));
    }

    public Sequence(Grammar grammar, String string) {
        this.grammar = grammar;
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            Terminal t = grammar.findT(Character.toString(c));
            add(grammar.terminals.indexOf(t));
        }
    }

    @Override
    public String toString() {
        if (isEmpty())
            return "eps";
        else {
            StringBuilder sb = new StringBuilder();
            for (int k : this)
                if (k == -1)
                    sb.append("$");
                else
                    sb.append(grammar.terminals.get(k).name);
            return sb.toString();
        }
    }

    public boolean isEof() {
        return size() > 0 && get(size() - 1) == -1;
    }

    public Sequence clone() {
        Sequence newSeq = new Sequence(grammar);
        newSeq.addAll(this);
        return newSeq;
    }
}
