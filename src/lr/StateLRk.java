package lr;

import grammar.Grammar;
import grammar.Rule;

import java.util.HashSet;

public class StateLRk extends State {
    StateLRk(Grammar grammar) {
        super(grammar);
    }

    @Override
    void add(HashSet<ItemLR0> newItems, Rule rule, ItemLR0 itemFrom) {

    }
}
