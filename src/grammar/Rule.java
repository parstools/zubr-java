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
    public int maxLen = -1;
    int countNonNullableSymbols = 0;

    public Rule clone(Nonterminal newOwner) {
        Rule cloned = new Rule(grammar, newOwner);
        for (Symbol symbol: this)
            cloned.add(symbol);
        return cloned;
    }

    boolean directLeftRecursive(Nonterminal nt) {
        if (isEmpty())
            return false;
        Symbol symbol = get(0);
        return !symbol.terminal && symbol == nt;
    }

    boolean startWithNonterminal() {
        if (isEmpty())
            return false;
        Symbol symbol = get(0);
        return !symbol.terminal;
    }

    boolean startWithNonterminal(Nonterminal nt) {
        if (!startWithNonterminal())
            return false;
        Symbol symbol = get(0);
        return symbol == nt;
    }

    void computeNonNullableCount() {
        countNonNullableSymbols = 0;
        for (Symbol symbol : this)
            if (symbol.terminal)
                countNonNullableSymbols++;
            else if (symbol.minLen > 0)
                countNonNullableSymbols++;
    }

    boolean computeMinLen() {
        int old = minLen;
        minLen = 0;
        for (Symbol symbol : this)
            if (!symbol.terminal && symbol.minLen < 0) {
                minLen = -1;
                return minLen != old;
            }
        for (Symbol symbol : this)
            if (symbol.terminal)
                minLen++;
            else
                minLen += symbol.minLen;
        return minLen != old;
    }

    boolean computeMaxLen() {
        final int infinity = Integer.MAX_VALUE;
        if (maxLen == infinity)
            return false;
        int old = maxLen;
        maxLen = 0;
        for (Symbol symbol : this)
            if (!symbol.terminal && symbol.maxLen < 0) {
                maxLen = -1;
                return maxLen != old;
            }
        for (Symbol symbol : this)
            if (symbol.terminal)
                maxLen++;
            else if (symbol.maxLen == infinity) {
                maxLen = infinity;
                return true;
            } else
                maxLen += symbol.maxLen;
        return maxLen != old;
    }

    public Rule(Grammar grammar, Nonterminal owner) {
        this.grammar = grammar;
        this.owner = owner;
    }

    public void parse(String input) {
        Scanner scanner = new Scanner(input);
        while (scanner.hasNext()) {
            String symbolName = scanner.next();
            Symbol symbol = grammar.findNt(symbolName);
            if (symbol!=null) {
                hasNt = true;
            } else {
                symbol = grammar.findT(symbolName);
                if (symbol == null) {
                    symbol = new Terminal(grammar, symbolName);
                    grammar.terminals.add((Terminal) symbol);
                }
                hasT = true;
            }
            add(symbol);
        }
    }

    @Override
    public int hashCode() {
        Hash h = new Hash();
        h.add(owner.getIndex());
        for (Symbol symbol : this) {
            h.add(symbol.hashCodeShallow());
            if (symbol.terminal)
                h.add(symbol.hashCode());
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

    public boolean conflict(Rule rule1, int k) {
        int minLength = Math.min(this.size(), rule1.size());
        int prefixLen = 0;
        for (int i = 0; i < minLength; i++)
            if (this.get(i) == rule1.get(i))
                prefixLen++;
            else
                break;
        int sum = 0;
        for (int i=0; i<prefixLen; i++) {
            Symbol symbol = get(i);
            sum += symbol.maxLen;
        }
        return true;
    }
}