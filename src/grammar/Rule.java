package grammar;

import util.Hash;

import java.util.ArrayList;
import java.util.Scanner;

public class Rule extends ArrayList<Symbol> {
    Grammar grammar;
    Nonterminal owner;
    public int globalIndex;

    public boolean hasNt = false;
    public int minLen = -1;

    boolean computeMinLen() {
        int old = minLen;
        minLen = 0;
        for (Symbol symbol : this)
            if (!symbol.terminal && grammar.getNT(symbol.index).minLen<0) {
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
        globalIndex = grammar.globalRuleCounter;
        grammar.globalRuleCounter++;
    }

    public void parse(String input) {
        Scanner scanner = new Scanner(input);
        while (scanner.hasNext()) {
            String symbolName = scanner.next();
            add(grammar.findSymbolAndAddTerminal(symbolName));
        }
    }

    @Override
    public int hashCode() {
        Hash h = new Hash();
        for (Symbol symbol: this) {
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
        for (int i=0; i<size() ; i++) {
            Symbol symbol = get(i);
            if (i>0)
                sb.append(" ");
            sb.append(symbol.toString());
        }
        return sb.toString();
    }
}