package grammar;

public class Symbol {
    public boolean terminal;
    public int index;
    private Grammar grammar;
    public Symbol(Grammar grammar, boolean terminal, int index) {
        this.grammar = grammar;
        this.terminal = terminal;
        this.index = index;
    }

    @Override
    public String toString() {
        return grammar.getSymbolName(this);
    }
}
