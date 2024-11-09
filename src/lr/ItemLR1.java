package lr;

import grammar.Rule;
import grammar.Terminal;

public class ItemLR1 extends ItemLR0 {
    Terminal t;

    ItemLR1(Rule rule, int dotPosition, Terminal t) {
        super(rule, dotPosition);
        this.t = t;
    }
}
