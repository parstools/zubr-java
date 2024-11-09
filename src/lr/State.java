package lr;

import grammar.Grammar;
import grammar.Nonterminal;
import grammar.Rule;
import grammar.Symbol;

import java.util.HashSet;

public class State extends HashSet<ItemLR0> {
    Grammar grammar;
    long longHash = 0;

    State(Grammar grammar) {
        this.grammar = grammar;
    }

    public static long ror(long value, int shift) {
        return (value >>> shift) | (value << (Long.SIZE - shift));
    }

    void closure() {
        boolean isModified;
        do {
            isModified = false;
            HashSet<ItemLR0> newItems = new HashSet<>();
            for (ItemLR0 item: this) {
                Nonterminal nt = item.NtAfterDot();
                if (nt != null)
                    for (Rule rule: nt.rules) {
                        ItemLR0 newItem = new ItemLR0(rule, 0);
                        newItems.add(newItem);
                    }
            }
            isModified = this.addAll(newItems);
        } while (isModified);
        longHash = 0;
        for (ItemLR0 item: this)
            longHash = ror(longHash,10) ^  item.hashCode();
    }

    public State goto_(Symbol symbol) {
        State newState = new StateLR0(grammar);
        for (ItemLR0 item: this) {
            Symbol symbol1 = item.symbolAfterDot();
            if (symbol1 == symbol)
                newState.add(item.goto_());
        }
        if (newState.size() > 0) {
            newState.closure();
            return newState;
        } else
            return null;
    }
}