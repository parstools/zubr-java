package grammar;

import util.Hash;

import java.util.ArrayList;
import java.util.Scanner;

public class Rule extends ArrayList<Symbol> {
    Grammar grammar;
    public Nonterminal owner;
    //public int globalIndex;
    public int index;

    public boolean hasNt = false;
    public boolean hasT = false;
    public int minLen = -1;
    int countNonNullableSymbols = 0;

    boolean directLeftRecursive(int ntIndex) {
        if (isEmpty())
            return false;
        Symbol symbol = get(0);
        return !symbol.terminal && symbol.index == ntIndex;
    }

    boolean startWithNonterminal() {
        if (isEmpty())
            return false;
        Symbol symbol = get(0);
        return !symbol.terminal;
    }

    void computeNonNullableCount() {
        countNonNullableSymbols = 0;
        for (Symbol symbol : this)
            if (symbol.terminal)
                countNonNullableSymbols++;
            else if (grammar.getNT(symbol.index).minLen > 0)
                countNonNullableSymbols++;
    }

    boolean computeMinLen() {
        int old = minLen;
        minLen = 0;
        for (Symbol symbol : this)
            if (!symbol.terminal && grammar.getNT(symbol.index).minLen < 0) {
                minLen = -1;
                return minLen != old;
            }
        for (Symbol symbol : this)
            if (symbol.terminal)
                minLen++;
            else
                minLen += grammar.getNT(symbol.index).minLen;
        return minLen != old;
    }

    public Rule(Grammar grammar, Nonterminal owner) {
        this.grammar = grammar;
        this.owner = owner;
    }

    public void parse(String input) {
        Scanner scanner = new Scanner(input);
        while (scanner.hasNext()) {
            String symbolName = scanner.next();
            Symbol symbol = grammar.findSymbolAndAddTerminal(symbolName);
            if (symbol.terminal)
                hasT = true;
            else
                hasNt = true;
            add(symbol);
        }
    }

    @Override
    public int hashCode() {
        Hash h = new Hash();
        h.add(owner.index);
        for (Symbol symbol : this) {
            h.add(symbol.hashCode());
            if (symbol.terminal)
                h.add(grammar.getTerminalName(symbol.index).hashCode());
        }
        return h.hash();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(owner.toString());
        sb.append(" -> ");
        for (int i = 0; i < size(); i++) {
            Symbol symbol = get(i);
            if (i > 0)
                sb.append(" ");
            sb.append(symbol.toString());
        }
        return sb.toString();
    }

    boolean cycleSuspected() {
        assert (minLen >= owner.minLen);
        return size() > 0 && !hasT && countNonNullableSymbols <= 1 && minLen == owner.minLen;
    }
}