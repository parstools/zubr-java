package grammar;

import util.Hash;

public class Symbol {
    public final boolean terminal;
    public int index;
    private Grammar grammar;
    public Symbol(Grammar grammar, boolean terminal, int index) {
        this.grammar = grammar;
        this.terminal = terminal;
        this.index = index;
    }

    public Symbol clone() {
        Symbol cloned = new Symbol(grammar, terminal, index);
        return cloned;
    }

    @Override
    public String toString() {
        return grammar.getSymbolName(this);
    }

    @Override
    public int hashCode() {
        Hash h = new Hash();
        h.add(terminal?1:0);
        h.add(index);
        return h.hash();
    }

    public void updateNtFrom(int updateIndex) {
        if (!terminal && index>=updateIndex)
            index++;
    }
}
