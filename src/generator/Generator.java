package generator;

import grammar.Grammar;
import grammar.Nonterminal;
import grammar.Rule;
import grammar.Symbol;
import set.Set;

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
        root = new Node(this, getNT(0));
    }

    Symbol getNT(int ntIndex) {
        return new Symbol(grammar, false, ntIndex);
    }

    Rule getRule(int ntIndex, int sortedIndex) {
       return ntInfos.get(ntIndex).ruleInfos.get(sortedIndex).rule;
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

    public String parenString() {
        return root.parenString();
    }

    public void first() {
        root.first(maxLen);
    }

    public boolean next() {
        return root.next(maxLen);
    }

    public Set collectFirst(int ntNumber, int k) {
        return new Set();
    }

    public Set collectFollow(int ntNumber, int k) {
        return new Set();
    }

}
