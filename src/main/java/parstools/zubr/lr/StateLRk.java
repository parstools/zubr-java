package parstools.zubr.lr;

import parstools.zubr.grammar.Grammar;
import parstools.zubr.grammar.Rule;

import java.util.HashSet;

public class StateLRk extends State {
    StateLRk(Grammar grammar) {
        super(grammar);
    }

    @Override
    void add(HashSet<ItemLR0> newItems, Rule rule, ItemLR0 itemFrom) {

    }
}
