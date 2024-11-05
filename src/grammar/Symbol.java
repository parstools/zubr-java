package grammar;

import util.Hash;

public abstract class Symbol {
    public final boolean terminal;
    protected Grammar grammar;
    public String name;
    public int minLen;
    public int maxLen;

    public static int compare(Symbol symbol0, Symbol symbol1) {
        if (symbol0.terminal!=symbol1.terminal) {
          if (symbol0.terminal)
              return 1;
          else
              return -1;
        }
        return Integer.compare(symbol0.getIndex(), symbol1.getIndex());
    }

    public abstract int getIndex();

    public Symbol(Grammar grammar, boolean terminal, String name) {
        this.grammar = grammar;
        this.terminal = terminal;
        this.name = name;
    }

    public int hashCodeShallow() {
        Hash h = new Hash();
        h.add(terminal?1:0);
        h.addString(name);
        return h.hash();
    }

    @Override
    public int hashCode() {
        return hashCodeShallow();
    }

    @Override
    public String toString() {
        return name;
    }}
