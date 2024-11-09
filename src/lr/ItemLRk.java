package lr;

import grammar.Rule;
import grammar.Terminal;
import set.Sequence;

public class ItemLRk extends ItemLR0{
    Sequence sequence;

    ItemLRk(Rule rule, int dotPosition, Sequence sequence) {
        super(rule, dotPosition);
        this.sequence = sequence;
    }
}
