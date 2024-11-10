package parstools.zubr.lr;

import parstools.zubr.grammar.Rule;
import parstools.zubr.grammar.Terminal;

public class ItemLR1 extends ItemLR0 {
    Terminal t;

    ItemLR1(Rule rule, int dotPosition, Terminal t) {
        super(rule, dotPosition);
        this.t = t;
    }
}
