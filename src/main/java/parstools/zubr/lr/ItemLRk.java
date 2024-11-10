package parstools.zubr.lr;

import parstools.zubr.grammar.Rule;
import parstools.zubr.set.Sequence;

public class ItemLRk extends ItemLR0{
    Sequence sequence;

    ItemLRk(Rule rule, int dotPosition, Sequence sequence) {
        super(rule, dotPosition);
        this.sequence = sequence;
    }
}
