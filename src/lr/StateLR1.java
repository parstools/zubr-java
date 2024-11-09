package lr;

import grammar.Grammar;
import grammar.Rule;

import java.util.HashSet;

public class StateLR1 extends State {
    StateLR1(Grammar grammar) {
        super(grammar);
    }

    @Override
    void add(HashSet<ItemLR0> newItems, Rule rule, ItemLR0 itemFrom) {

    }
}
