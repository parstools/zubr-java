package parstools.zubr.grammar;

import parstools.zubr.util.Hash;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Rule extends ArrayList<Symbol> {
    private final int infinity = Integer.MAX_VALUE;
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

    int expandedLen(int prefixLen) {
        int result = 0;
        for (int i=0; i<prefixLen; i++) {
            Symbol symbol = get(i);
            if (symbol.maxLen == infinity) {
                result = infinity;
                break;
            } else
                result += symbol.maxLen;
        }
        return result;
    }

    public boolean conflict(Rule rule1, int k) {
        int minLength = Math.min(this.size(), rule1.size());
        int prefixLen = 0;
        for (int i = 0; i < minLength; i++)
            if (this.get(i) == rule1.get(i))
                prefixLen++;
            else
                break;
        return expandedLen(prefixLen)  >= k;
    }

    public Rule getCommonPrefix(Rule other) {
        Rule result = new Rule(grammar, owner);
        for (int i = 0; i < Math.min(this.size(), other.size()); i++) {
            if (get(i) != other.get(i))
                break;
            result.add(get(i));
        }
        return result;
    }

    public boolean startWith(Rule bestPrefix) {
        if (this.size() < bestPrefix.size())
            return false;
        for (int i = 0; i < bestPrefix.size(); i++) {
            if (get(i) != bestPrefix.get(i))
                return false;
        }
        return true;
    }

    public void fillRandom(Random random) {
        int count = random.nextInt(4);
        for (int i = 0; i < count; i++) {
            int ifTerminal = random.nextInt(2);
            if (count == 1)
                ifTerminal = 1;
            if (ifTerminal == 1) {
                Terminal t = grammar.terminals.get(random.nextInt(grammar.terminals.size()));
                hasT = true;
                add(t);
            } else {
                Nonterminal nt = grammar.nonterminals.get(random.nextInt(grammar.nonterminals.size()));
                hasNt = true;
                add(nt);
            }
        }
    }
}