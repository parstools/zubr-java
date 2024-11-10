package parstools.zubr.lr;

import parstools.zubr.grammar.Grammar;
import parstools.zubr.grammar.Rule;

import java.util.HashSet;

public class StateLR1 extends State {
    StateLR1(Grammar grammar) {
        super(grammar);
    }

    @Override
    void add(HashSet<ItemLR0> newItems, Rule rule, ItemLR0 itemFrom) {

    }
}
