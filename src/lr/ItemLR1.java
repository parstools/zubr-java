package lr;

import grammar.Rule;
import grammar.Terminal;

public class ItemLR1 {
    Rule rule;
    int dotPosition;
    Terminal t;

    ItemLR1(Rule rule, int dotPosition, Terminal t) {
        this.rule = rule;
        this.dotPosition = dotPosition;
        this.t = t;
    }
}
