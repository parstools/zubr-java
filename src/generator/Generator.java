package generator;

import grammar.Grammar;
import grammar.Nonterminal;
import grammar.Rule;
import grammar.Symbol;

import java.util.ArrayList;
import java.util.List;

public class Generator {
    Grammar grammar;
    List<NTInfo> ntInfos = new ArrayList<>();
    int maxLen;
    Node root;
    Generator(Grammar grammar, int maxLen) {
        this.grammar = grammar;
        this.maxLen = maxLen;
        for (Nonterminal nt: grammar.nonterminals) {
            ntInfos.add(new NTInfo(this, nt));
        }
        boolean changed = true;
        while (changed) {
            changed = false;
            for (NTInfo ntInfo: ntInfos) {
                if (ntInfo.computeMinLen())
                    changed = true;
            }
        }
        for (NTInfo ntInfo: ntInfos)
            ntInfo.sortRules();
        root = new Node(this, getNT(0), maxLen);
    }

    Symbol getNT(int ntIndex) {
        return new Symbol(grammar, false, ntIndex);
    }

    int ruleMinLen(Symbol symbol, int sortedIndex) {
        if (symbol.terminal)
            return 1;
        else {
            int ntIndex = symbol.index;
            return ntInfos.get(ntIndex).ruleInfos.get(sortedIndex).minLen;
        }
    }

    Rule getRule(int ntIndex, int sortedIndex) {
       return ntInfos.get(ntIndex).ruleInfos.get(sortedIndex).rule;
    }

    void collectFirstSet(int k) {

    }

    void collectFollowSet(int k) {

    }

    void collectFFSet(int k) {

    }

    int ruleCount(Symbol symbol) {
        if (symbol.terminal)
            return 0;
        else {
            return ntInfos.get(symbol.index).ruleInfos.size();
        }
    }

    public String string() {
        return root.string();
    }

    public boolean next() {
        return root.next(maxLen);
    }
}
